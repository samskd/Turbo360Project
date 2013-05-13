package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Spearman {
	
	private Map<Integer,Double> pageRankMap = new HashMap<Integer,Double>();
	private Map<Integer,Integer> numViewsMap = new HashMap<Integer,Integer>();
	private pageRankValueComparator prvc = new pageRankValueComparator(pageRankMap);
	private Map<Integer,Double> sortedPageRankMap = new TreeMap<Integer,Double>(prvc);
	private numViewsValueComparator nvvc = new numViewsValueComparator(numViewsMap);
	private Map<Integer,Integer> sortedNumViewsMap = new TreeMap<Integer,Integer>(nvvc);
	
	private void createStructure(File pageRank, File numViews){
		try{
			FileReader fileReaderPageRank = new FileReader(pageRank);
			BufferedReader bufferedReaderPageRank = new BufferedReader(fileReaderPageRank);
			FileReader fileReaderNumViews = new FileReader(numViews);
			BufferedReader bufferedReaderNumViews = new BufferedReader(fileReaderNumViews);
			@SuppressWarnings("unused")
			int totalPages = Integer.parseInt(bufferedReaderPageRank.readLine());
			String line = null;
			while((line = bufferedReaderPageRank.readLine()) != null){
				String[] entry = line.trim().split("\\s+");
				if(entry.length < 2) continue;
				pageRankMap.put(Integer.parseInt(entry[0]),Double.parseDouble(entry[1]));	
				}
			bufferedReaderPageRank.close();
			fileReaderPageRank.close();
			totalPages = Integer.parseInt(bufferedReaderNumViews.readLine());
			line = null;
			while((line = bufferedReaderNumViews.readLine()) != null){
				String[] entry = line.trim().split("\\s+");
				if(entry.length < 2) continue;
				numViewsMap.put(Integer.parseInt(entry[0]),Integer.parseInt(entry[1]));	
				}
			bufferedReaderNumViews.close();
			fileReaderNumViews.close();
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	class pageRankValueComparator implements Comparator<Integer> {

	    Map<Integer, Double> base;
	    public pageRankValueComparator(Map<Integer, Double> base) {
	        this.base = base;
	    }
    
	    //Descending Order
	    public int compare(Integer a, Integer b) {
	        if (base.get(a) <= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	class numViewsValueComparator implements Comparator<Integer> {

	    Map<Integer, Integer> base;
	    public numViewsValueComparator(Map<Integer, Integer> base) {
	        this.base = base;
	    }
	    //Descending Order
	    public int compare(Integer a, Integer b) {
	        if (base.get(a) <= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	private void createSortedMaps(File pageRank, File numViews){
		this.createStructure(pageRank, numViews);
		sortedPageRankMap.putAll(pageRankMap);
		sortedNumViewsMap.putAll(numViewsMap);
		
	}
	
	public double calculateSpearmanCoefficient(String pageRank1, String numViews1){
		File pageRank = new File(pageRank1);
		File numViews = new File(numViews1);
		createSortedMaps(pageRank, numViews);
		Integer[] numViewsArray = new Integer[sortedNumViewsMap.size()];
		Integer[] pageRankArray = new Integer[sortedPageRankMap.size()];
		
		int pgVar = -1;
		for(Integer i : sortedPageRankMap.keySet()){
			pageRankArray[i] = ++pgVar;
		}
		int nmVar = -1;
		for(Integer i : sortedNumViewsMap.keySet()){
			numViewsArray[i] = ++nmVar;
		}
		
		double coeff = 0d;
		double summation = 0d;
		for(int i=0; i<numViewsArray.length;i++){
			summation += Math.pow((pageRankArray[i] - numViewsArray[i]),2);
		}
		long n = numViewsArray.length;
		//System.out.println(summation);
		//System.out.println(Math.pow(n, 3)-n);
		double denominator = Math.pow(n, 3)-n;
		coeff = 1-((6*summation)/denominator);
		return coeff;
	}
	
	public static void main(String args[]){
		String pg = args[0];
		String nv = args[1];
		Spearman sp = new Spearman();
		System.out.println("Spearman's Coefficent : " + sp.calculateSpearmanCoefficient(pg, nv));
		
		
	}
	
}
