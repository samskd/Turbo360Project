package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import twitter4j.Status;
import twitter4j.User;
import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3 based on your {@code RankerFavorite}
 * from HW2. The new Ranker should now combine both term features and the
 * document-level features including the PageRank and the NumViews. 
 */
public class RankerComprehensive extends Ranker {

	private double realtimeWeighingFactor = 1.0;
	public RankerComprehensive(Options options,
			CgiArguments arguments, Indexer indexer) {
		super(options, arguments, indexer);
		System.out.println("Using Ranker: " + this.getClass().getSimpleName());
	}

	@Override
	public Vector<ScoredDocument> runQuery(Query query, int numResults) {

		Queue<ScoredDocument> rankQueue = new PriorityQueue<ScoredDocument>();
		DocumentIndexed doc = null;
		int docid = -1;

		while ((doc = (DocumentIndexed)_indexer.nextDoc(query, docid)) != null) {
			//Scoring the document
			double contentScore = this.getScore(query, doc) + 0.2*doc.getNumViews() + 0.8*doc.getPageRank();
			double realtimeScore = this.getRealTimeTwitterScore(doc);
			double totalScore = realtimeWeighingFactor*realtimeScore + (1-realtimeWeighingFactor)*contentScore;
			
			System.out.println(contentScore + ", "+realtimeScore + ", "+totalScore);
			rankQueue.add(new ScoredDocument(doc,totalScore));
			
			if (rankQueue.size() > numResults) {
				rankQueue.poll();
			}
			docid = doc._docid;
		}
		
		while ((doc = (DocumentIndexed) _indexer.nextDoc(query, docid, "twitter")) != null) {
			//Scoring the document
			double contentScore = this.getScore(query, doc) + 0.2*doc.getNumViews() + 0.8*doc.getPageRank();
			double realtimeScore = this.getRealTimeTwitterScore(doc);
			double totalScore = realtimeWeighingFactor*realtimeScore + (1-realtimeWeighingFactor)*contentScore;
			
			rankQueue.add(new ScoredDocument(doc,totalScore));
			
			if (rankQueue.size() > numResults) {
				rankQueue.poll();
			}
			docid = doc._docid;
		}

		Vector<ScoredDocument> results = new Vector<ScoredDocument>();
		ScoredDocument scoredDoc = null;
		while ((scoredDoc = rankQueue.poll()) != null) {
			results.add(scoredDoc);
		}
		Collections.sort(results, Collections.reverseOrder());
		return results;
	}


	private Double getScore(Query query, DocumentIndexed d) {

		Vector<String> qv = query._tokens;

		Vector <Integer> dv = d.getDocumentTokens();
		Vector<Double> QueryVector_Smoothening = new Vector<Double>();

		double smoothFactor = 0.5;

		double score = 1d;
		int docTermCount = dv.size();
		long collectionTermCount = _indexer.totalTermFrequency();

		for(int i = 0; i < qv.size(); i++){

			String queryTerm = qv.get(i);
			boolean isPhraseQuery = false;

			Query phraseToken = null;
			if(queryTerm.indexOf("\\s+") != -1){
				isPhraseQuery = true;
				phraseToken = new Query(queryTerm);
				phraseToken.processQuery();
			}

			int qtermFreqDoc = 0;
			if(isPhraseQuery){
				while(_indexer.nextPhrase(phraseToken, d._docid, -1) != Integer.MAX_VALUE){
					qtermFreqDoc++;
				}
			}else{
				qtermFreqDoc = _indexer.documentTermFrequency(queryTerm, d.getUrl());
			}

			double firstTerm = (double) qtermFreqDoc/docTermCount;
			firstTerm *= (1-smoothFactor);

			int qtermFreqCollection = 0;
			if(isPhraseQuery){
				for(String token : phraseToken._tokens){
					qtermFreqCollection += _indexer.corpusTermFrequency(token);
				}
			}else{
				qtermFreqCollection = _indexer.corpusTermFrequency(queryTerm);
			}

			double secondTerm = (double) qtermFreqCollection/collectionTermCount;
			secondTerm *= smoothFactor;
			QueryVector_Smoothening.add(firstTerm + secondTerm);

		}

		for(int i=0; i<QueryVector_Smoothening.size(); i++){
			score *= QueryVector_Smoothening.get(i);
		}

		return score;
	}
	
	
	private Double getRealTimeTwitterScore(DocumentIndexed d){
		
		double score = 0.0;
				
		Status tweet = d.getTweet();
		
		long retweetCount = tweet.getRetweetCount();
			score+=retweetCount*0.3;
		boolean isFavourited = tweet.isFavorited();
			if(isFavourited) score += 0.1;
		boolean isPossiblySensitive = tweet.isPossiblySensitive();
			if(isPossiblySensitive) score += 0.02;
		long[] contributors = tweet.getContributors();
		int totalContributors = 0;
		if(contributors != null) {
			totalContributors = contributors.length;
			score += totalContributors*0.05;
		}
		
		Date createdOn = tweet.getCreatedAt();
		long timeDiff = createdOn.getTime() - (new Date()).getTime();
			score+=timeDiff*0.15;
		
		//User Entities
		User user = tweet.getUser();
		int favouritesCount = user.getFavouritesCount();
			score += favouritesCount*0.03;
		int friendsCount = user.getFriendsCount();
			score += friendsCount*0.01;
		int followerCount = user.getFollowersCount();
			score += followerCount*0.12;
		int publicLists = user.getListedCount();
			score += publicLists*0.02;
			
		boolean isVerified = user.isVerified();
			if(isVerified) score *= 0.2;
		
		return score;
	}
}
