package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import edu.nyu.cs.cs2580.FileManager.T3FileWriter;


public class IndexWrapper extends HashMap<Integer, PostingsWithOccurences<String>>{

	private static final long serialVersionUID = 6127445645999471161L;

	private String _indexFolder;
	private String _indexTempFolder;
	private int _tempFileCount = 0;

	@SuppressWarnings("unused")
	private IndexWrapper(){};

	public IndexWrapper(String indexFolder)
	{
		_indexFolder = indexFolder;
		_indexTempFolder = indexFolder+"/temp";
		File tempdir = new File(_indexTempFolder);
		if(!tempdir.exists())
			tempdir.mkdir();
	}


	public void writeToDisk() 
	{
		T3FileWriter t3 = new T3FileWriter(_indexTempFolder + "/" + Integer.toString(_tempFileCount));

		Set<Integer> terms = this.keySet();
		Integer[] keys = terms.toArray(new Integer[terms.size()]);
		Arrays.sort(keys);

		System.out.println("have to write "+keys.length + " entries");
		for(int i=0; i<keys.length; i++)
		{
			t3.write(keys[i]+":");
			t3.write(this.get(keys[i])+"");
			t3.write("\n");
		}

		t3.close();
		this.clear();

		System.out.println("Created " + Integer.toString(_tempFileCount));
		_tempFileCount++;

	}

	public PostingsWithOccurences getPostingList(int termID){
		FileReader fileReader;
		String line="";
		try {
			if(this.containsKey(termID)){
				return this.get(termID);
			}
			
			fileReader = new FileReader(_indexFolder+"/"+"index.idx");

			BufferedReader bufferReader = new BufferedReader(fileReader);
			int tempCount = 0;
			
			while(tempCount != termID && (line = bufferReader.readLine()) != null){
				tempCount++;
			}
			bufferReader.close();
			fileReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PostingsWithOccurences postingList = T3Parser.parsePostingInvertedIndex(line);
		this.put(termID, postingList);
		return postingList;
	}


}
