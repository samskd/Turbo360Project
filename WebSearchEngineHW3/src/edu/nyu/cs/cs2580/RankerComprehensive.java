package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3 based on your {@code RankerFavorite}
 * from HW2. The new Ranker should now combine both term features and the
 * document-level features including the PageRank and the NumViews. 
 */
public class RankerComprehensive extends Ranker {

	private double realtimeWeighingFactor = 0.5;
	private double realtimeResultsPercentage = 0.3;
	private final String REALTIME = "twitter"; //realtime
	private final String CORPUS = "wiki";

	public RankerComprehensive(Options options,
			CgiArguments arguments, Indexer indexer) {
		super(options, arguments, indexer);
		realtimeWeighingFactor = arguments._realtimeWeight;
		System.out.println("Using Ranker: " + this.getClass().getSimpleName());
	}

	@Override
	public Vector<ScoredDocument> runQuery(Query query, int numResults) {

		Vector<ScoredDocument> realtimeResults = new Vector<ScoredDocument>();
		Vector<ScoredDocument> corpusResults = new Vector<ScoredDocument>();
		
		try{
			Queue<ScoredDocument> corpusRankQueue = new PriorityQueue<ScoredDocument>();
			DocumentIndexed doc = null;
			int docid = -1;

			
			System.out.println("Corpus Processing");
			while ((doc = (DocumentIndexed)_indexer.nextDoc(query, docid)) != null) {
				//Scoring the document
				double contentScore = this.getContentScore(query, doc, CORPUS);
				corpusRankQueue.add(new ScoredDocument(doc, contentScore));

				if (corpusRankQueue.size() > numResults) {
					corpusRankQueue.poll();
				}
				docid = doc._docid;
			}

			int totalRealtimeDocs = (int)(numResults*realtimeResultsPercentage);
			Queue<ScoredDocument> realtimeRankQueue = new PriorityQueue<ScoredDocument>();
			docid = -1;
			
			System.out.println("Tweets Processing");
			while ((doc = (DocumentIndexed) _indexer.nextDoc(query, docid, REALTIME)) != null) {
				//Scoring the document
				double contentScore = this.getContentScore(query, doc, REALTIME);
				double realtimeScore = this.getRealTimeTwitterScore(doc);
				double totalScore = realtimeWeighingFactor*realtimeScore + (1-realtimeWeighingFactor)*contentScore;
				
				realtimeRankQueue.add(new ScoredDocument(doc, totalScore));

				if (realtimeRankQueue.size() > totalRealtimeDocs) {
					realtimeRankQueue.poll();
				}
				docid = doc._docid;
			}

			ScoredDocument scoredDoc = null;
			
			while ((scoredDoc = realtimeRankQueue.poll()) != null) {
				realtimeResults.add(scoredDoc);
			}
			Collections.sort(realtimeResults, Collections.reverseOrder());
			
			while ((scoredDoc = corpusRankQueue.poll()) != null) {
				corpusResults.add(scoredDoc);
			}
			Collections.sort(corpusResults, Collections.reverseOrder());
			
			int totalCorpusDocs = numResults - realtimeResults.size();
			if(totalCorpusDocs > corpusResults.size()){
				totalCorpusDocs = corpusResults.size();
				realtimeResults.addAll(corpusResults);
			}else{
				realtimeResults.addAll(corpusResults.subList(0, totalCorpusDocs));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return realtimeResults;
	}



	private double getContentScore(Query query, DocumentIndexed doc, String docType) {
		return this.getScore(query, doc, docType);
	}


	private double getScore(Query query, DocumentIndexed d, String docType) {

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
//				phraseToken.processQuery();
			}

			int qtermFreqDoc = 0;
			if(isPhraseQuery){
				while(_indexer.nextPhrase(phraseToken, d._docid, -1) != Integer.MAX_VALUE){
					qtermFreqDoc++;
				}
			}else{
				qtermFreqDoc = _indexer.documentTermFrequency(queryTerm, d.getUrl(), docType);
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

	private double getRealTimeTwitterScore(DocumentIndexed d) {

		double score = 0.0;

		try{
			if(!d._isTweet) return 0.0;

			long retweetCount = d._retweetCount;
			score += retweetCount*0.3;
			boolean isFavourited = d._isFavorited;
			if(isFavourited) score += 0.1;
			boolean isPossiblySensitive = d._isPossiblySensitive;
			if(isPossiblySensitive) score += 0.02;
			int totalContributors = d._totalContributors;
			score += totalContributors*0.05;

			Date createdOn = d._createdAt;
			long timeDiff = (new Date()).getTime() - createdOn.getTime();
			score+=timeDiff/1000000;
			score*=0.15;

			//User Entities
			int followerCount = d._userFollowers;
			//			score += followerCount*0.13;
			switch(followerCount){
			case 5000 : score += 0.05; break;
			case 10000 : score += 0.09; break;
			default : score += 0.13;
			}
			int publicLists = d._totalPublicLists;
			score += publicLists*0.02;

			boolean isVerified = d._isVerified;
			if(isVerified) score *= 0.2;

		}catch(Exception e){
			e.printStackTrace();
		}

		return score;
	}
}
