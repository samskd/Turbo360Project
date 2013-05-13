package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3.
 */
public class LogMinerNumviews extends LogMiner {

  public LogMinerNumviews(Options options) {
    super(options);
  }

  
  final int linksBlockSize = 2000;
  final String logFile = "data/log/20130301-160000.log";
  final String outputFile = "numViews";
  
  //Maps page link to ID
  Map<String, Integer> pages = new HashMap<String, Integer>();
  //Maps ID to page link
  Map<Integer, String> IDPages = new HashMap<Integer, String>();
  // Maps page ID to numviews
  Map<Integer, Integer> numViews = new HashMap<Integer, Integer>();
  
  /**
   * This function processes the logs within the log directory as specified by
   * the {@link _options}. The logs are obtained from Wikipedia dumps and have
   * the following format per line: [language]<space>[article]<space>[#views].
   * Those view information are to be extracted for documents in our corpus and
   * stored somewhere to be used during indexing.
   *
   * Note that the log contains view information for all articles in Wikipedia
   * and it is necessary to locate the information about articles within our
   * corpus.
   *
   * @throws IOException
   */
  @Override
  public void compute() throws IOException {
    System.out.println("Computing using " + this.getClass().getName());
    try{

		File corpusDirectory = new File(_options._corpusPrefix);

		//returns if the corpus prefix is not a directory
		if(!corpusDirectory.isDirectory()){
			return;
		}

		
		
		int pageCount = -1;
		int numViewsCount = -1;
		int IDpagesCount = -1;
		for(File page : corpusDirectory.listFiles()){
			pages.put(page.getName(), ++pageCount);
			IDPages.put(++IDpagesCount, page.getName());
			numViews.put(++numViewsCount, 0);
		}
		
		BufferedReader br = new BufferedReader(new FileReader(logFile));
		String line;
		int id=0;
		int temp = 0;
		while ((line = br.readLine()) != null) {
		   String[] lineSplit = line.split("\\s+");
		   if(lineSplit.length == 3){
			 //  id = pages.get(filter(lineSplit[1]));
			   try{
			   String in = URLDecoder.decode(lineSplit[1], "UTF-8");
			  
			   if(pages.containsKey(in)){
				   //System.out.println(in);
				   id = pages.get(in);
				   temp = numViews.get(id);
				   //Modify lineSplit[1] to remove special characters
				   numViews.put(id, temp +Integer.parseInt(lineSplit[2]));
			   }
			   
			   } catch(IllegalArgumentException e){
				   
			   }
		   }
		}
		br.close();
		
		FileWriter fileWriter = new FileWriter(outputFile);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		bufferWriter.write(numViews.size() + "\n");
		Set<Entry<Integer,Integer>> numViewsSet = numViews.entrySet();
		Iterator<Entry<Integer, Integer>> i = numViewsSet.iterator();
		while(i.hasNext()){
			Entry<Integer,Integer> entry = (Map.Entry<Integer, Integer>)i.next();
			//Wrote ID -> numViews
			bufferWriter.write(entry.getKey() + " " + entry.getValue() + "\n");
		}
		bufferWriter.close();
		fileWriter.close();
    }catch(Exception e){
		e.printStackTrace();
	} finally{
		
	}
    return;
  }
  
  @SuppressWarnings("unused")
private String filter(String in){
	  in = in.trim();
	  StringBuilder modified = new StringBuilder();
	  String tempStr = "";
	  for(int i=0; i<in.length();){
		  if(in.charAt(i)=='%' && i<in.length()-2){
			  tempStr = Character.toString(in.charAt(i+1))+Character.toString(in.charAt(i+2));
			  modified.append((char)Integer.parseInt(tempStr,16));
		  }
		  else
			  modified.append(in.charAt(i));
	  }
	  return modified.toString();
  }
  
  

  /**
   * During indexing mode, this function loads the NumViews values computed
   * during mining mode to be used by the indexer.
   * 
   * @throws IOException
   */
  @Override
  public Object load() throws IOException {
    System.out.println("Loading using " + this.getClass().getName());
    
    try{

		File corpusDirectory = new File(_options._corpusPrefix);

		//returns if the corpus prefix is not a directory
		if(!corpusDirectory.isDirectory()){
			return null;
		}
    	
  		int pageCount = -1;
  		for(File page : corpusDirectory.listFiles()){
  			pages.put(page.getName(), ++pageCount);
  		}
    
    BufferedReader br = new BufferedReader(new FileReader(outputFile));
	String line;
	while ((line = br.readLine()) != null) {
	   String[] lineSplit = line.split("\\s+");
	   if(lineSplit.length == 2){
		   numViews.put(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]));
	   }
	}
	br.close();
    }catch(Exception e){
		e.printStackTrace();
	}
    return numViews;
  }
}
