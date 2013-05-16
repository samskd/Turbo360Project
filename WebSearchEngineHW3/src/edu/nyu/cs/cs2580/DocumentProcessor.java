package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;


public class DocumentProcessor {

	StemmingAndStopWordsWrapper stemmingAndStopWordsWrapper;
	Analyzer analyzer;
	
	public DocumentProcessor(){
		stemmingAndStopWordsWrapper = 
				new StemmingAndStopWordsWrapper(new PorterStemmer());
		analyzer = new StandardAnalyzer(Version.LUCENE_35);
	}

	/**
	 * Process the text to include the following operations (removes the html tags, 
	 * replace punctuations with whitespace, downcase the words, 
	 * removes the stopwords and stems the words)
	 * 
	 * @param fileReader FileReader object
	 * @throws FileNotFoundException 
	 * */
	public Vector<String> process(File file) throws FileNotFoundException {

		Scanner scan = new Scanner(file);  
		//reads all the text at once
		scan.useDelimiter("\\Z");  
		String content = scan.next();  
		scan.close();
		return process(content);
	}


	/**
	 * Process the text to include the following operations (removes the html tags, 
	 * replace punctuations with whitespace, downcase the words, 
	 * removes the stopwords and stems the words)
	 * 
	 * @param text text to be process
	 */
	public Vector<String> process(String htmlText)  {

		Vector<String> processedTokens = null;

		try{
			String plainText = ArticleExtractor.INSTANCE.getText(htmlText);
			processedTokens = new Vector<String>(parseKeywords(analyzer, plainText));

		}catch(Exception e){
			e.printStackTrace();
		}

		return processedTokens;
	}


	public static List<String> parseKeywords(Analyzer analyzer, String keywords) {

		List<String> result = new ArrayList<String>();
		TokenStream stream  = analyzer.tokenStream(null, new StringReader(keywords));

		try {
			CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);
			while(stream.incrementToken()) {
				String term = termAttribute.toString();
				result.add(term);
			}
		}
		catch(IOException e) {
			// not thrown b/c we're using a string reader...
		}

		return result;
	}  



	/**
	 * Converts HTML to plain text. Removes everything in &lt;script&gt; tag.
	 * @throws BoilerpipeProcessingException 
	 * */
	public static String htmlToText(String htmlText) {

		String content = "";
		try{
			content = ArticleExtractor.getInstance().getText(htmlText);
		}catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		}
		return content;
	}

}
