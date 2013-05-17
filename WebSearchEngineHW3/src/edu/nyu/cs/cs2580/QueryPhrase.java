package edu.nyu.cs.cs2580;

import java.util.Scanner;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 * ["new york city"], the presence of the phrase "new york city" must be
 * recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {

	public QueryPhrase(String query) {
		super(query);
	}

	@Override
	public void processQuery() {
		if (_query == null) {
			return;
		}

		int firstOccurenceIndex = _query.indexOf("\"");
		int secondOccurenceIndex = 0;
		if(firstOccurenceIndex!=-1) {
			secondOccurenceIndex = _query.indexOf("\"", firstOccurenceIndex+1);
			if(secondOccurenceIndex != -1){

				String leadingQuery = _query.substring(0, firstOccurenceIndex);
				Scanner s = new Scanner(leadingQuery);
				while (s.hasNext()) {
					_tokens.add(s.next());
				}
				s.close();

				String phrase = _query.substring(firstOccurenceIndex + 1, secondOccurenceIndex);
				_tokens.add(phrase);

				String followingQuery = _query.substring(secondOccurenceIndex + 1, _query.length());
				s = new Scanner(followingQuery);
				while (s.hasNext()) {
					_tokens.add(s.next());
				}
				s.close();
			}
		}else{
			Scanner s = new Scanner(_query);
		    while (s.hasNext()) {
		      _tokens.add(s.next());
		    }
		    s.close();
		}
	}
}

