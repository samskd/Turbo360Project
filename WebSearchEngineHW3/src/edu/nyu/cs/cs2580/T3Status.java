package edu.nyu.cs.cs2580;
import java.util.HashMap;
import java.util.Map;

import twitter4j.Status;

/**
 * Wrapper for Status for serialization
 * @author kunal
 *
 */

public class T3Status implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	Status status;
	public Map<String,String> documents = new HashMap<String,String>();
	
	public T3Status(Status status){
		this.status = status;
	}
}
