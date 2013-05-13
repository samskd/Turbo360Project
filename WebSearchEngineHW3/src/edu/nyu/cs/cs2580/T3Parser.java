package edu.nyu.cs.cs2580;


//Utility class for parsing
public class T3Parser {

	public static Integer parseTermInvertedIndex(String line){
		return Integer.parseInt(line.trim().charAt(0)+"");
	}

	public static PostingsWithOccurences<Integer> parsePostingInvertedIndex(String line){

		PostingsWithOccurences<Integer> posting = new PostingsWithOccurences<Integer>();

		String[] postings = line.split(":");
		for(int i = 1 ; i < postings.length ; i++)
		{
			String[] postingentry = postings[i].split("\\s+");
			if(postingentry.length > 1){
				
				for(int j =1 ; j < postingentry.length;j++){
					posting.addEntry(Integer.parseInt(postingentry[0]), Integer.parseInt(postingentry[j]));
				}
				
			}
		}
		return posting;
	}
}
