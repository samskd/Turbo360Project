package edu.nyu.cs.cs2580;

import java.util.Date;
import java.util.Vector;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
/**
 * @author samitpatel
 *
 */
public class DocumentIndexed extends Document {
	private static final long serialVersionUID = 9184892508124423115L;

	private Indexer _indexer = null;
	
	//Tweet Features
	public long _retweetCount = 0;
	public boolean _isFavorited = false;
	public boolean _isPossiblySensitive = false;
	public int _totalContributors = 0;
	public Date _createdAt = null;
	public int _userFavoriteCount = 0;
	public int _userFollowers = 0;
	public int _totalPublicLists = 0;
	public boolean _isVerified = false;
	
	private Vector<Integer> _documentTokens = new Vector<Integer>();

	public <T extends Indexer> DocumentIndexed(int docid, T indexer) {
		super(docid);
		_indexer = indexer;
	}

	public void setDocumentTokens(Vector<Integer> documentTokens) {
		_documentTokens = documentTokens;
	}

	public Vector<Integer> getDocumentTokens() {
		return _documentTokens;
	}
	
	
}
