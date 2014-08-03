package edu.cmu.sv.webcrawler.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.cmu.sv.webcrawler.models.Risks;

public class RiskExtractor implements RisksService {
	private Risks riskExtracted;
	//risk type names are pre-determined
	private final String[] riskType = {"Poor financial condition risks", "Funding Risks",
			"Regulation changes", "Catastrophes", "Macroeconomic risks",
			"International risks", "Suppliers risks", "Input prices risks",
			"Concentration on few large customers", "Competition risks",
			"Industry is cyclical", "Volatile demand and financial results",
			"Volatile stock price risks", "New product introduction risks",
			"Downstream risks", "Potential defects in products", "Restructure Risks",
			"Shareholder’s interest risks", "Intellectual Property Risks",
			"Potential/Ongoing Lawsuits", "Infrastructure risks","Human resource risks"
			};
	
	public RiskExtractor(String str_10k) {
		riskExtracted = new Risks();
		//traverse all risktype and find keywords for each one 
 		for(int i=0; i<riskType.length; i++) {
			String riskName = riskType[i];
			KeywordExtractor ke = new KeywordExtractor(str_10k, 
							 riskExtracted.getKeyphasesByType(riskName));
			//if find the key words for the risk type
			if(ke.getKeywords()) {
				ArrayList<String> kp_temp = new ArrayList<String>();
				riskExtracted.setRisktype(riskName);
				Iterator<String> it = ke.keywordExtracted.keySet().iterator();
				//add the keywords to a list
				while(it.hasNext()) {
					String keyphase = it.next();
					kp_temp.add(keyphase);
					
					//get the sentences for the key phrase
					ArrayList<String> sen = new ArrayList<String>();
					KeywordInfo KI = ke.keywordExtracted.get(keyphase);
					Set<Integer> senIndex = KI.indexOfSentence;
					Iterator<Integer> iterator = senIndex.iterator();
					while(iterator.hasNext()) {
						sen.add(ke.sentences.get(iterator.next()));
					}
					riskExtracted.setSen4kw(keyphase, sen);
				}
				//add the key phrases list to the hash map
				riskExtracted.setKw4risktype(riskName, kp_temp);
			}
		}
	}
	
	public ArrayList<String> get_risktype() {
		return riskExtracted.getRisktype();
	}
	
	//get keywords for the risk type
	public ArrayList<String> get_keyword(String risktype) {
		HashMap<String, ArrayList<String>> kw4risktype = riskExtracted.getKw4risktype();
		return kw4risktype.get(risktype);
	}
	
	//get sentences that contain that keyword
	public ArrayList<String> get_sentences(String keyword) {
		HashMap<String, ArrayList<String>> sen4kw = riskExtracted.getSen4kw();
		return sen4kw.get(keyword);
	}
	
	//return the number of risks it has
	public int get_risklevel() {
		return riskExtracted.getRisktype().size();
	}
}
