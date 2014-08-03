package main;
import java.io.*;
import java.util.ArrayList;

import edu.cmu.sv.webcrawler.services.*;

public class Main {
	private static void printArrayList(String message, ArrayList<String> AL) {
		System.out.println(message);
			if(AL.size() > 0) {
			System.out.print("[");
			for(int i=0; i<AL.size()-1; i++) {
				System.out.print(AL.get(i) + ", ");
				if((i+1)%5 == 0) {
					System.out.println();
				}
			}
			System.out.println(AL.get(AL.size()-1) + "]");
		}
		System.out.println();
	}
	
	private static void printMessage(String message1, String message2) {
		System.out.println(message1);
		System.out.println(message2);
		System.out.println();
	}
	
	private static void printRiskAndKeywords(RiskExtractor RE) {
		ArrayList<String> riskTypes = RE.get_risktype();
		for(int i=0; i<riskTypes.size(); i++) {
			String riskType = riskTypes.get(i);
			ArrayList<String> phrases = RE.get_keyword(riskType);
			printArrayList("Key phrases for the risk type:" + riskType, phrases);
		}
	}
	
	private static void printKeywordAndSen(RiskExtractor RE) {
		ArrayList<String> riskTypes = RE.get_risktype();
		System.out.println("Key phrases and sentences: [");
		for(int i=0; i<riskTypes.size(); i++) {
			String riskType = riskTypes.get(i);
			ArrayList<String> phrases = RE.get_keyword(riskType);
			for(int j=0; j<phrases.size(); j++) {
				String phrase = phrases.get(j);
				ArrayList<String> sens = RE.get_sentences(phrase);
				System.out.println();
				System.out.println(phrase+":");
				for(int k=0; k<sens.size(); k++) {
					System.out.println("( " + sens.get(k) + " )");
				}
			}		
		}
		System.out.println("]");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		try {
			// Get the risk description
			String pathname = "10K/320193_2012.txt";
			File filename = new File(pathname);
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename));
			BufferedReader br = new BufferedReader(reader);
			String riskStr = br.readLine();
			RiskExtractor RE = new RiskExtractor(riskStr);		
			
			//print results
			//print risk types for the company
			ArrayList<String> riskTypes = RE.get_risktype();
			printArrayList("Contains following risk types:", riskTypes);
			
			//print risk level for the company
			Integer riskLevel = RE.get_risklevel();
			printMessage("The risk level for the company is:", ""+riskLevel);
			
			//only print key phrases
			printRiskAndKeywords(RE);
			
			//print the sentences contain the key phrases
			//printKeywordAndSen(RE);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
