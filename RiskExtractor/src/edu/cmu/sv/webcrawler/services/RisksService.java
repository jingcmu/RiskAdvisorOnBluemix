package edu.cmu.sv.webcrawler.services;

import java.util.ArrayList;

public interface RisksService {
	//get the risk types for a company
	public ArrayList<String> get_risktype();
	//get the key words/phrases for a risk type
	public ArrayList<String> get_keyword(String risktype);
	//get the sentences that contain the key words/phrases
	public ArrayList<String> get_sentences(String keyword);
	//get the risk level of the company
	public int get_risklevel();
}
