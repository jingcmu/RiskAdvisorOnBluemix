package resourceparse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
public class GetURL {
	private ArrayList<String> URLs;
	private StringBuffer sBuffer;
	
	public GetURL() {
		sBuffer = new StringBuffer();
		URLs = new ArrayList<String>();
	}
	
	/**
	 * get the URLs for downloading 10K docs
	 * @param isCurrent "true" means to download 10K for the day, we don't use this in the demo
	 */
	private void ParseURLs(boolean isCurrent) {
		int startIndex = 0, endIndex = 0;
		while(true) {	
			startIndex = sBuffer.indexOf("/Archives/edgar/data", endIndex);
			if(startIndex == -1) {
				return;
			}
			endIndex = sBuffer.indexOf("\"", startIndex);
			String str = sBuffer.substring(startIndex, endIndex);
			String year = "";
			if(isCurrent == false) {
				int index = str.indexOf("-");
				year = str.substring(index+1, index+3);
			}
			str = "http://www.sec.gov" + str;
			if(Integer.parseInt(year) < 11) {
				break;
			}
			//append the year str to the URLs and remove it when use the URLs later
			//just a way to save year info
			URLs.add("http://www.sec.gov" + this.ParseIntoURLs(str) + year);
		}
	}
	
	/**
	 * get the downloading URL for a 10K doc
	 * @param urlStr 10K page URL
	 * @return the downloading url for 10K
	 */
	private String ParseIntoURLs(String urlStr) {
		StringBuffer sb = Get10kSearchPage(urlStr);
		String str = "";
		int startIndex = 0, endIndex = 0;
		while(true) {
			startIndex = sb.indexOf("/Archives/edgar/data", endIndex);
			if(startIndex == -1) {
				break;
			}
			endIndex = sb.indexOf("\"", startIndex);
			str = sb.substring(startIndex, endIndex);
			int index = str.lastIndexOf('.');
			if(index != -1) {
				break;
			}
		}
		return str;
	}
	
	/**
	 * 
	 * @param CIK
	 * @param isCurrent "true" means to download 10K for the day, we don't use this in the demo
	 * @return URL list for downloading the 10K forms
	 */
	public ArrayList<String> Get10kURLwithCIK(String CIK, boolean isCurrent) {
		String queryURL;
		if(isCurrent == false) {
			//this is the URL used to search 10K docs for a CIK
			queryURL = "http://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&type=10-K&CIK=";
			this.sBuffer = Get10kSearchPage(queryURL + CIK);
		}
		else {
			//this is the URL used to get the 10K docs submitted today
			queryURL = "http://www.sec.gov/cgi-bin/current.pl?q1=1&q2=0";
			this.sBuffer = Get10kSearchPage(queryURL);
		}				
		ParseURLs(isCurrent);
		return URLs;
	}
	
	/**
	 * download a web page using the url
	 * @param urlStr
	 * @return a StringBuffers
	 */
	private StringBuffer Get10kSearchPage(String urlStr)
    {
        /** the length of input stream */
        int chByte = 0;
        
        URL url = null;
        
        /** HTTP connection */
        HttpURLConnection httpConn = null;
        
        InputStream in = null;
        
        StringBuffer sb = new StringBuffer("");

        try
        {
            url = new URL(urlStr);
            httpConn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            httpConn.setRequestMethod("GET"); 
            
            in = httpConn.getInputStream();
            
            chByte = in.read();
            while (chByte != -1)
            {
            	sb.append((char)chByte);
                chByte = in.read();
            }
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
}