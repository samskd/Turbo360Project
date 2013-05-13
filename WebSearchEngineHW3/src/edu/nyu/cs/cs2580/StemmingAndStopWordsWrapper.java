package edu.nyu.cs.cs2580;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class StemmingAndStopWordsWrapper {

	PorterStemmer stemmer;
	Set<String> stopWords = new HashSet<String>();


	@SuppressWarnings("unused")
	private StemmingAndStopWordsWrapper(){}

	public StemmingAndStopWordsWrapper(PorterStemmer stemmer){
		this.stemmer = stemmer;
		generateStopWordsList();
	}


	public Vector<String> process(String[] tokens){

		Vector<String> stemmedTokens = new Vector<String>();

		for(String word : tokens){
			word = word.toLowerCase();
			if(!stopWords.contains(word)){
				char[] c = word.toCharArray();
				stemmer.add(c, c.length);
				stemmer.stem();
				stemmedTokens.add(stemmer.toString());
			}
		}

		return stemmedTokens;
	}



	/**
	 * Generates a list of StopWords
	 * */
	private void generateStopWordsList() {

		//Stopwords list from Rainbow


	}

}
