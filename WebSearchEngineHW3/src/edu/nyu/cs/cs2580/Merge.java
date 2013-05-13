package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Merge {

	/**
	 * @param tempFiles file names
	 * @param output output filename
	 * @param delimiter 
	 * */
	public static void merge(String base, int blockSize) throws IOException{
		
		File directory = new File(base+"/temp");
		String[] tempFiles = new String[directory.list().length];
		
		if(directory.exists() && directory.isDirectory()){
			tempFiles = directory.list();
		}
		BufferedReader[] readers = new BufferedReader[tempFiles.length];
		String[] currentLines = new String[tempFiles.length];
		
		File file = new File(base+"/0.idx");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fileWritter = new FileWriter(file);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		
		for(int i = 0; i<tempFiles.length; i++){
			readers[i] = new BufferedReader(new FileReader(base + "/temp/" + tempFiles[i]));
			currentLines[i] = readers[i].readLine();
		}
		
		int endOfFiles = 0;
		int currentTermID = 0;
		String currentTermPostingList = "";
		
		while(endOfFiles != tempFiles.length){
			currentTermPostingList = currentTermID+" ";
			for(int i = 0; i < tempFiles.length; i++){
				
				if(currentLines[i] != null && 
						currentLines[i].startsWith(Integer.toString(currentTermID))){
					currentTermPostingList += " "+currentLines[i].substring(currentLines[i].indexOf(":"), currentLines[i].length());
					currentLines[i] = readers[i].readLine();
					if(currentLines[i] == null){
						endOfFiles++;
						readers[i].close();
					}
				}
			}
			bufferWritter.write(currentTermPostingList + "\n");
			currentTermID++;
			
//			if(currentTermID > 0 &&  currentTermID % blockSize == 0){
//				bufferWritter.close();
//				fileWritter = new FileWriter(new File(base+"/"+currentTermID+".idx"));
//				bufferWritter = new BufferedWriter(fileWritter);
//				blockSize += blockSize; 
//			}
			
		}
		
		bufferWritter.close();

		//Delete all temporary files
		for(String tFile : tempFiles){
			File currentFile = new File(base + "/temp/" + tFile);
			currentFile.delete();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		merge("data/index/invertedOccurenceIndex",10);

	}

}
