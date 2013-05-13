package edu.nyu.cs.cs2580.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Util {

	public static void writeTempGraphToFile(
			Map<Integer, Set<Integer>> incomingLinks, String tempFile) 
					throws IOException{

		FileWriter fileWriter = new FileWriter(tempFile);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		try{

			List<Integer> pageIDs = new ArrayList<Integer>(incomingLinks.keySet());
			Collections.sort(pageIDs);

			StringBuilder temp = new StringBuilder();

			for(int pageID : pageIDs){
				temp.append(pageID+" ");
				Set<Integer> links = incomingLinks.get(pageID);
				for(int link : links){
					temp.append(link+" "); 
				}

				bufferWriter.write(temp+"\n");
				//empty the stringbuilder
				temp.delete(0, temp.length());
			}

		}finally{
			bufferWriter.close();
			fileWriter.close();
		}

	}


	public static void mergeGraphFiles(String folderName, String outputFile, 
			int totalPages) throws IOException {

		//		BufferedReader[] readers = null;

		File file = new File(outputFile);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

		//writes the total pages on the first line
		bufferWriter.write(totalPages+"\n");

		try{

			File directory = new File(folderName);
			File[] tempFiles = new File[directory.list().length];

			if(directory.exists() && directory.isDirectory()){
				tempFiles = directory.listFiles();
			}

			//combine all the files into one. Temp files doesn't have correlated data, 
			//so just copy and add into new file
			for(File tFile : tempFiles){
				BufferedReader reader = new BufferedReader(new FileReader(tFile));
				try{
					String line = null; 
					while((line = reader.readLine())  != null){
						line = line.trim();
						if(line.isEmpty()) continue;
						bufferWriter.write(line + "\n");
					}
					reader.close();
					reader = null;
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(reader != null) reader.close();
				}

			}

			bufferWriter.close();

			//Delete all temporary files
			for(File tFile : tempFiles){
				tFile.delete();
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			bufferWriter.close();
			fileWriter.close();
		}

	}


	public static void writePageRanks(double[] pageRanks, String outputFile) throws IOException{

		FileWriter fileWriter = new FileWriter(outputFile);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		bufferWriter.write(pageRanks.length + "\n");
		try{
			for(int i=0; i<pageRanks.length; i++){
				bufferWriter.write(i + " " + pageRanks[i] + "\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			bufferWriter.close();
			fileWriter.close();
		}
	}
	
	
	
	public static long getTermCount(Vector<Integer> documentTerms, int termId){
		long count = 0;
		
		for(int term : documentTerms){
			if(term == termId) count++;
		}
		
		return count;
	}

}
