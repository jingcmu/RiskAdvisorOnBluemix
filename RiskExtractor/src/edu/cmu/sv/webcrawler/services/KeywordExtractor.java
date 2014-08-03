package edu.cmu.sv.webcrawler.services;

import java.util.*;

public class KeywordExtractor implements KeywordsService {
	ArrayList<String> keyphases;	  //key phases	
	public ArrayList<String> sentences;  //store separated sentences
	public HashMap<String, KeywordInfo> keywordExtracted; //store extracted keyword
	private HashMap<String,Integer>result; //no use now

	public KeywordExtractor(String str_risk, ArrayList<String> phases) {
		keyphases = phases;
		result = new HashMap<String,Integer>();
		sentences = new ArrayList<String>();
		keywordExtracted = new HashMap<String, KeywordInfo>();
		//split the paragraph into sentences
		getSentences(str_risk);
		//search keywords in the paragraph
		this.getKeywords();
	}

	//split the paragraph into sentences
	private void getSentences(String str_risk) {
		int start = 0, end = 0, last = 0;
		last = start = str_risk.indexOf(".", end);
		while (start != -1 && end != -1) {
			end = str_risk.indexOf(".", last + 1);
			if (end - last > 10) {
				sentences.add(str_risk.substring(start + 1, end));
				start = end;
			}
			last = end;
		}
	}
	
	//get keywords in a sentence
	private boolean getKeywordsInSentence(String sen, Integer index) {
		boolean found = false;
		boolean found_temp = false;
		if(this.keyphases == null) {
			return found;
		}
		Iterator<String> it = this.keyphases.iterator();
		while (it.hasNext()) {
			String s = it.next();
			if(s.indexOf("/") != -1) {
				String[] keyword = s.split("/");
				found_temp = true;
				for(int i=0; i<keyword.length; i++) {
					if(sen.indexOf(keyword[i]) == -1) {
						found_temp = false;
					}
				}
			}
			else
			{
				if(sen.indexOf(s) != -1) {
					found_temp = true;						
				}
			}
			
			if(found_temp) {
				found = true;
				if (!keywordExtracted.containsKey(s)) {
					KeywordInfo ki = new KeywordInfo();
					keywordExtracted.put(s, ki);
				}				
				if(!keywordExtracted.get(s).indexOfSentence.contains(index)) {
					keywordExtracted.get(s).num++;
					keywordExtracted.get(s).indexOfSentence.add(index);
				}
			}
		}
		return found;
	}

	//get keywords/phrases in the paragraph
	public boolean getKeywords() {
		boolean found = false;
		int size = sentences.size();
		for (int i = 0; i < size; i++) {
			String sen = this.sentences.get(i);
			if(getKeywordsInSentence(sen, i)) {
				found = true;
			}
		}

		Iterator<String> iter = keywordExtracted.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
//			System.out.print(key + " ");
			KeywordInfo ki = keywordExtracted.get(key);
			result.put(key, ki.num);
//			System.out.print(ki.num);
//			System.out.print("[");
//			for (int i = 0; i < ki.indexOfSentence.size(); i++) {
//				System.out.print(ki.indexOfSentence.get(i) + " ");
//			}
//			System.out.println("]");
		}
		return found;
	}

	public HashMap<String, Integer> getKeywordsBySymbol() {
		return result;
	}
}

class KeywordInfo {
	public int num; // number of the word that appears
	//store the sentences that contain the keyword, use set to avoid duplication
	public Set<Integer> indexOfSentence;

	KeywordInfo() {
		num = 0;
		indexOfSentence = new HashSet<Integer>();
	}
};