package edu.nyu.cs.cs2580.FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class T3FileWriter {


	private FileWriter fileOutputStream ;
	private BufferedWriter writer ;
	@SuppressWarnings("unused")
	private String filepath;

	
	public T3FileWriter(String filepath){

		this.filepath = filepath;
		try 
		{
			fileOutputStream = new FileWriter(filepath);
			writer = new BufferedWriter(fileOutputStream);

		} catch (IOException e){
			System.out.println("Creating new file");
			File tmp = new File(filepath);
			try {
				tmp.getParentFile().mkdirs();
				tmp.createNewFile();

				fileOutputStream = new FileWriter(filepath);
				writer = new BufferedWriter(fileOutputStream);
			} catch (IOException e1) {

				e1.printStackTrace();
			}

		}

	}

	public void write(String content){
		try {
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close(){
		try {
			writer.close();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clearFile(String fileLocation){
	    try{
	        BufferedWriter bw = new BufferedWriter(new FileWriter(fileLocation));
	        bw.write("");
	        bw.flush();
	        bw.close();
	    }catch(IOException ioe){
	        // You should really do something more appropriate here
	        ioe.printStackTrace();
	    }
	}

}
