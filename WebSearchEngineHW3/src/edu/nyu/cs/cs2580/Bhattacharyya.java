package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bhattacharyya {
	
	//private String directoryPath;
	//File folder = new File(directoryPath);
	//private File[] files = folder.listFiles();
	
	/**
	 * Create Array of all files with extension .prf
	 * @return
	 */
	/*private String[] createListOfFiles(){
		String[] fileNames = new String[files.length];
		int i=-1;
		for(File f : files){
			if(f.isFile() && f.getName().endsWith(".prf"))
				fileNames[++i] = f.getName();
		}
		return fileNames;
	}
	*/
	
	private Map<String,String> inputFileToMap(String filename){
		File file = new File(filename);
		Map<String,String> map = new HashMap<String,String>();
		BufferedReader br;
		String[] temp;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				temp = line.split(":");
				if(temp.length != 2)
					continue;
				map.put(temp[0].trim(),temp[1].trim());
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * Creates a map from a file where key=term and value=probability
	 * @param f is file name
	 * @return the map created
	 */
	private Map<String,Double> createMap(String f){
		File file = new File(f);
		Map<String,Double> map = new HashMap<String,Double>();
		BufferedReader br;
		String[] temp;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				temp = line.split("\\s+");
				if(temp.length != 2)
					continue;
				map.put(temp[0],Double.parseDouble(temp[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * Creates Intersection of 2 Maps
	 * @param m1 - Map 1
	 * @param m2 - Map 2
	 * @return - List if common terms
	 */
	private List<String> intersection(Map<String,Double> m1, Map<String,Double> m2){
		Set<String> intersectionSet = new HashSet<String>(m1.keySet());
		intersectionSet.retainAll(m2.keySet());
		return new ArrayList<String>(intersectionSet);
	}
	
	
	
	/**
	 * Calculates the Query Similarity
	 */
	public void querySimilarity(String inputFile, String outputFile){
		//String[] files = this.createListOfFiles();
		Map<String,String> queryAndPath = this.inputFileToMap(inputFile);
		String[] filesFromMap = queryAndPath.keySet().toArray(new String[0]);
		try{
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
			Map<String,Double> map1;
			Map<String,Double> map2;
			List<String> keys;
			for(int i=0; i<filesFromMap.length-1; i++){
				for(int j=i+1; j<filesFromMap.length; j++){
					 map1 = this.createMap(queryAndPath.get(filesFromMap[i]));
					 map2 = this.createMap(queryAndPath.get(filesFromMap[j]));
					 keys = this.intersection(map1, map2);
					 double summation = 0d;
					 for(String s : keys){
						 summation += Math.sqrt(map1.get(s)*map2.get(s));
					 }
					 bufferWriter.write(queryAndPath.get(filesFromMap[i]) + "\t" + queryAndPath.get(filesFromMap[j]) + "\t" + summation + "\n");
					 //System.out.println(files[i] + "\\t" + files[j] + "\\t" + summation);
				}
			}
			bufferWriter.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		Bhattacharyya b = new Bhattacharyya();
		if(args[0] != null && args[1] != null)
			b.querySimilarity(args[0], args[1]);
	}
}
