package edu.cmu.sv.webcrawler.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Risks {
	private HashMap<String, ArrayList<String>> keyPhrases;
	private ArrayList<String> risktype;
	private HashMap<String, ArrayList<String>> kw4risktype;
	private HashMap<String, ArrayList<String>> sen4kw;
	
	public Risks() {
		risktype = new ArrayList<String>();
		kw4risktype = new HashMap<String, ArrayList<String>>();
		sen4kw = new HashMap<String, ArrayList<String>>();
		keyPhrases = new HashMap<String, ArrayList<String>>();
		keyPhrases = getAllKeywords();
	}
	
	/**
	 * transform string to JSONObject
	 * @param keywords string from file
	 * @return JSONObject
	 */
	private JSONObject getJasonKeywords(String keywords) {
		JSONObject jsonObj = null;
		try {
			jsonObj = JSONObject.fromObject(keywords);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	// init key phrases set for each risk type
	public HashMap<String, ArrayList<String>> getAllKeywords() {
		try {
			String pathname = "keywords.json";
			File filename = new File(pathname);  			
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader);
			// read all content of file "keywords.json"
			String keyword = br.readLine();
			String keywords = "";
			while(keyword != null) {
				keywords += keyword;
				keyword = br.readLine();
			}
			
			//get the JSONObject
			JSONObject keywordJson = getJasonKeywords(keywords);
			//for each risk type, init its key phrases set
			for(Iterator<String> iter = keywordJson.keys(); iter.hasNext();) {
				String riskName = iter.next();
				ArrayList<String> phases = new ArrayList<String>();
				JSONArray jarray = keywordJson.getJSONArray(riskName);
				for(int j=0; j<jarray.size(); j++) {
					String phase = (String)jarray.getString(j);
					phases.add(phase);
				}
				keyPhrases.put(riskName, phases);
			}			
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		return keyPhrases;
	}
	
	public ArrayList<String> getKeyphasesByType(String riskType) {
		return keyPhrases.get(riskType);
	}

	public ArrayList<String> getRisktype() {
		return risktype;
	}

	public void setRisktype(String str) {
		this.risktype.add(str);
	}

	public HashMap<String, ArrayList<String>> getKw4risktype() {
		return kw4risktype;
	}

	public void setKw4risktype(String key, ArrayList<String> val) {
		this.kw4risktype.put(key, val);
	}

	public HashMap<String, ArrayList<String>> getSen4kw() {
		return sen4kw;
	}

	public void setSen4kw(String key, ArrayList<String> val) {
		this.sen4kw.put(key, val);
	}
	
}
