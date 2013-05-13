package edu.nyu.cs.cs2580;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * This class represents a single entry in the {@link PostingsWithOccurences} 
 * posting list. It stores the document ID and the term offset within the document.
 * 
 * @author samitpatel
 * */
class PostingEntry<T> implements Serializable {

	private static final long serialVersionUID = 3167577397839073790L;
	private Integer _docid;
	private Vector<T> _values;

	@SuppressWarnings("unused")
	private PostingEntry(){}


	/**
	 * Constructs the object with document ID and term offset within document.
	 * 
	 * @param docid Document ID
	 * @param offset Term offset within document
	 * */
	PostingEntry (Integer docid, T value){
		_docid = docid;
		_values = new Vector<T>();
		_values.add(value);
	}

	/**
	 * Returns the Document ID.
	 * @return Document ID
	 * */
	public Integer getDocID(){
		return _docid;
	}

	/**
	 * Returns the offset of the term with the document.
	 * @return Term offset within the document
	 * */
	public Vector<T> getOffset(){
		return _values;
	}

	public void addOffset(T value) {
		_values.add(value);
	}

	@Override
	public String toString() {
		String str = _docid+" ";
		for(T value : _values){
			str = str + value.toString() + " ";
		}
		return str;
	}
}

/**
 * This class handles the Postings list for each term in the 
 * {@link IndexerInvertedOccurrence} indexer.
 * 
 * @author samitpatel
 * */
public class PostingsWithOccurences<T> extends Vector<PostingEntry<T>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7865772446822434246L;
	private Integer cachedIndex = -1;


	/**
	 * Adds an entry to Posting List
	 * */
	public void addEntry(Integer docid, T value){
		if(!this.isEmpty()){
			PostingEntry<T> lastDocument = this.lastElement();
			if(lastDocument.getDocID() == docid){
				lastDocument.addOffset(value);
				return;
			}
		}
		
		PostingEntry<T> entry = new PostingEntry<T>(docid, value);
		super.addElement(entry);

	}


	/**
	 * Searchs the documentID in the Vector
	 * */
	public PostingEntry<T> searchDocumentID(Integer docID) {

		int documentID = Collections.binarySearch(this, new PostingEntry<T>(docID, null), new Comparator<PostingEntry<T>>() {

			@Override
			public int compare(PostingEntry<T> o1, PostingEntry<T> o2) {
				return o1.getDocID().compareTo(o2.getDocID());
			}
		});

		return this.get(documentID);

	}


	/**
	 * Returns the Cached Index
	 * */
	public Integer getCachedIndex() {
		return cachedIndex;
	}

	/**
	 * Sets the cachedIndex
	 * */
	public void setCachedIndex(Integer cachedIndex) {
		this.cachedIndex = cachedIndex;
	}


	@Override
	public synchronized String toString() {
		String str = "";
		for(PostingEntry<T> entry : this){
			str = str + entry.toString() + ":";
		}
		return str;
	}
	

	public synchronized String formatString() {
		String str = this.get(0).getDocID() + ":";
		for(PostingEntry<T> entry : this){
			str = str + entry.toString() + ":";
		}
		return str;
	}

}
