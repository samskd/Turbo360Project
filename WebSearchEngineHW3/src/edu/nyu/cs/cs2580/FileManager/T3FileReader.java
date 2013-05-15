package edu.nyu.cs.cs2580.FileManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class T3FileReader{


	private FileInputStream fileInputStream ;
	private DataInputStream in;
	private BufferedReader reader;
	private File file;

	public T3FileReader(String filepath){

		try {
			file = new File(filepath);
			fileInputStream = new FileInputStream(filepath);
			in = new DataInputStream(fileInputStream);
			reader = new BufferedReader(new InputStreamReader(in));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readLine(){

		String retVal = null;
		try {
			retVal =  reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return retVal;
	}
	
	public String readAllBytes(){

		String retVal = null;
		try {
			byte[] b = new byte[(int) file.length()]; 
			fileInputStream.read(b);
			retVal =  new String(b);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return retVal;
	}

	public void close(){
		try {
			reader.close();
			in.close();
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isStringPresent(String term){

		String strLine;
		try {
			//Read File Line By Line
			while ((strLine = reader.readLine()) != null)  {
				if(term.charAt(0) == strLine.charAt(0)){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
}
