package edu.cmu.sv.webcrawler.models;

import java.util.ArrayList;

public class Keywords {
	private ArrayList<String> keyPhases;
	
	public Keywords(ArrayList<String> keyPhases) {
		this.keyPhases = keyPhases;
	}
	
	public ArrayList<String> getKeyPhases() {
		return keyPhases;
	}
	
	public void setKeyPhases(ArrayList<String> keyPhases) {
		this.keyPhases = keyPhases;
	}
}

