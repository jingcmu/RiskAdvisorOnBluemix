package webdownload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;
import resourceparse.GetURL;

public class Get10K {
	/*
	private void DownLoad10K(String urlStr, String outPath) {
        // the length of input stream 
        int chByte = 0; 
        
        // the url of the doc to download 
        URL url = null; 
        
        // HTTP connection 
        HttpURLConnection httpConn = null;        
        InputStream in = null;        
        FileOutputStream out = null;
        
        try {
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            httpConn.setRequestMethod("GET");            
            in = httpConn.getInputStream();
            out = new FileOutputStream(new File(outPath));
            chByte = in.read();
            while (chByte != -1) {
                out.write(chByte);
                chByte = in.read();
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
                in.close();
                httpConn.disconnect();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    } */
	
	private StringBuffer Get10kContent(String urlStr)
    {
        int chByte = 0;
        
        URL url = null;
        
        HttpURLConnection httpConn = null;
        
        InputStream in = null;
        
        StringBuffer sb = new StringBuffer("");

        try
        {
        	//int len = 0;
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            httpConn.setRequestMethod("GET"); 
            
            in = httpConn.getInputStream();
            
            chByte = in.read();
            while (chByte != -1)
            {
            	//len++;
            	sb.append((char)chByte);
                chByte = in.read();
            }
            //System.out.println(len);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                in.close();
                httpConn.disconnect();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return sb;
    }	
	
	/** 
	 * Download 10-k files for a given company using CIK or symbol
	 * @param symbol
	 * @param isCurrent "true" means to download 10K for the day, we don't use this in the demo
	 */
	public void Download10KbyCIK(String symbol, boolean isCurrent) {
		GetURL gURL = new GetURL();
        ArrayList<String> URLs = gURL.Get10kURLwithCIK(symbol, isCurrent);
		Iterator<String> it = URLs.iterator();
        while(it.hasNext()) {
        	String str = it.next();
        	int index0 = str.indexOf("data");
        	int index1 = str.indexOf("/", index0+5);
        	String CIK = str.substring(index0+5, index1);
        	int index2 = str.lastIndexOf('.');
        	if(index2 <= 14) continue; 
        	String year, url; 
        	if(isCurrent == false) {
        		url = str.substring(0, str.length() - 2);    
	        	year = str.substring(str.length() - 2);
	        	if(Integer.parseInt(year) < 60) {
	        		year = "20" + year;
	        	}
	        	else {
	        		year = "19" + year;
	        	}
        	}
        	else {
        		// this part need to improve if used later
        		// we don't use this now
        		year = "2014"; //this year is 2014, will be false next year.
        		url = str;
        	}
        	String ext = url.substring(index2);
        	// 10K folder should exist
        	String fileName = "./10K/" + CIK + "_" + year + ".txt";// + "_" + index + ext;
        	//DownLoad10K(url, fileName);
        	
        	StringBuffer sb_10K = Get10kContent(url);
        	String s_10K = null;
        	if(ext.equals(".txt")) {
        		s_10K = sb_10K.toString();
        	}
        	else if(sb_10K != null) {
        		s_10K = extractAllText(sb_10K.toString());
        	}
        	try {				
				if(s_10K != null && s_10K.length() > 0) {
					FileWriter fw = new FileWriter(fileName);
					fw.write(s_10K);
					fw.close();
					System.out.println(fileName);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        	
        }
	}
	
	/** 
	 * Download 10-k files for a list of companies
	 * @param filename contains the stock symbols of the companies
	 */
	public void Download10KbyCIKList(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String str = br.readLine();
			while(str != null) {
				Download10KbyCIK(str, false); //download 10K doc for a company using the stock symbol
				str = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * extract item 1a section from the 10K document
	 * @param htmlText the downloaded string of 10K document 
	 * @return the string of item 1a section
	 */
	public String extractAllText(String htmlText){
		try {
		    Source source = new Source(htmlText);
		    // transform html to plain text
		    TextExtractor page_TE = source.getTextExtractor();
		    // transform all symbol to lowercase
		    String page = page_TE.toString().toLowerCase();		    
		    boolean flag = true;
		    int start = 0, start_risk = 0, end = 0, item2 = 0;
		    while(flag) {
		    	start = page.indexOf("item 1a", start + 1);
		    	start_risk = page.indexOf("risk factors", start + 1);
		    	if(start == -1 || start_risk == -1) {
		    		return null;
		    	}
		    	// to exclude some false start point of item 1a section
		    	if(start_risk - start > 200) continue;
		    	String s = page.substring(start, start_risk);
		    	s = s.replaceAll(" ", ""); // remove all space in the string to get better result
		    	
		    	// whether it is the real start point of item 1a section
		    	if( page.substring(start - 4, start - 1).toLowerCase().equals("see") ||
		    		page.substring(start - 3, start - 1).toLowerCase().equals("to") ||     //refer to
		    		page.substring(start - 3, start - 1).toLowerCase().equals("in") ||     //... in
		    		page.substring(start - 3, start - 1).toLowerCase().equals("ed") ||     //... ed
		    		page.substring(start - 5, start - 1).toLowerCase().equals("also") ||   //see also
		    		page.substring(start - 6, start - 1).toLowerCase().equals("under") ||   //under
		    	   (page.charAt(start-1) != ' ' && page.charAt(start-1) != '.') ||
		    	   (page.charAt(start+7) != ' ' && page.charAt(start+7) != '.') ||
		    	   (page.charAt(start_risk-1) != ' ' && page.charAt(start_risk - 1) != '.') ||
		    	   (page.charAt(start_risk+12) != ' ' && page.charAt(start_risk + 12) != '.') ||
		    	   !(s.equals("item1a") || s.equals("item1a."))
		    	   ) {
		    		continue; // if it is not a real start point of item 1a section, ignore it
		    	}
		    	page = page.substring(start);		    	
		    	end = page.indexOf("unresolved staff comments");
		    	item2 = page.indexOf("item 2");
		    	if(end > 100 && item2 > 100) {		    		
		    		return page.substring(0, end + 10);
		    	}
		    }
		}
		catch (Exception e) {
			System.out.println("Java heap space!");
		}
		return null;
	}

	
	//Main
	public static void main(String[] args) {
        System.out.println("Start crawling from www.sec.gov...");        
        String CIK = "HPQ"; //"ABIO"
        Get10K g10K = new Get10K();
        //g10K.Download10KbyCIK(CIK, false); // download 10K for CIK
        g10K.Download10KbyCIKList("stocksymbol");
        System.out.println("Finished crawling.");
    }
}
