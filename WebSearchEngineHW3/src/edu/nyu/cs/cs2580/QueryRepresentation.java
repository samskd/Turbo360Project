package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import edu.nyu.cs.cs2580.util.StopWords;
import edu.nyu.cs.cs2580.util.Util;

public class QueryRepresentation {

	private Ranker _ranker;
	private IndexerInvertedDoconly _indexer;

	private final String prfFolder = "data/prf";

	public QueryRepresentation(Ranker ranker, Indexer indexer){
		this._ranker = ranker;
		this._indexer = (IndexerInvertedDoconly) indexer;
		File file = new File(prfFolder);
		if(!file.exists()){
			file.mkdir();
		}
	}

	public String represent(Query query, int numberOfDocumentsToBeConsidered, int numberOfTerms) throws IOException{

		FileWriter fileWrite = null;
		BufferedWriter bufferedWriter = null;

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;

		StringBuilder output = new StringBuilder();

		try{

			String resultFile = prfFolder+"/qr_"+query._query+"_" +
					numberOfDocumentsToBeConsidered+"_"+numberOfTerms + ".tsv";

			Vector<ScoredDocument> documents = _ranker.runQuery(query, numberOfDocumentsToBeConsidered);
			long totalWords = 0;

			Set<Integer> allTerms = new HashSet<Integer>();

			for(ScoredDocument doc : documents){
				DocumentIndexed d = (DocumentIndexed)doc.getDocument();
				Vector<Integer> documentVector = d.getDocumentTokens();
				for(int term : documentVector){
					allTerms.add(term);
				}

				totalWords += documentVector.size();
			}


			class TermProb implements Comparable<TermProb>{

				public String _term;
				public double _prob;
				public TermProb(String term, double prob) {
					this._term = term;
					this._prob = prob;
				}
				@Override
				public int compareTo(TermProb o) {
					if (this._prob == o._prob) {
						return 0;
					}
					return (this._prob > o._prob) ? 1 : -1;
				}

			}
			
			Queue<TermProb> topMProb = new PriorityQueue<TermProb>();
			
			Iterator<Integer> terms = allTerms.iterator();
			while(terms.hasNext()){
				int term = terms.next();
				long termTotal = 0;

				for(ScoredDocument doc : documents){
					DocumentIndexed d = (DocumentIndexed)doc.getDocument();
					termTotal += Util.getTermCount(d.getDocumentTokens(), term);
				}

				double prob = (double)termTotal/totalWords;
				String termStr = _indexer._terms.get(term);
				if(!StopWords.isStopWord(termStr)){
					topMProb.add(new TermProb(termStr, prob));
					if (topMProb.size() > numberOfTerms) {
						topMProb.poll();
					}
				}
			}

			
			//total probability of top M terms
			double probabilitySum = 0.0;
			for(TermProb termP : topMProb){
				probabilitySum += termP._prob;
			}

			fileWrite = new FileWriter(resultFile);
			bufferedWriter = new BufferedWriter(fileWrite);
			
			TermProb termP = null;
			while ((termP = topMProb.poll()) != null) {
				output.insert(0, termP._term +"\t"+ (termP._prob/probabilitySum) + "\n");
			}
			
			bufferedWriter.write(output.toString());
			bufferedWriter.close();
			fileWrite.close();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fileWrite != null) fileWrite.close();
			if(fileReader != null) fileReader.close();
			if(bufferedReader != null) bufferedReader.close();
			if(bufferedWriter != null) bufferedWriter.close();
		}

		return output.toString();
	}

}
