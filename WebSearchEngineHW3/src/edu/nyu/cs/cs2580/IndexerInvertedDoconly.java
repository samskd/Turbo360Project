package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import twitter4j.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import edu.nyu.cs.cs2580.SearchEngine.Options;
import edu.nyu.cs.cs2580.FileManager.T3FileReader;
import edu.nyu.cs.cs2580.FileManager.T3FileWriter;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedDoconly extends Indexer implements Serializable
{
	private static final long serialVersionUID = 1077111905740085030L;

	// Maps each term to their posting list
	private Map<Integer, Postings> _invertedIndex = new TreeMap<Integer, Postings>();

	//Stores all Document in memory.
	private Vector<Document> _documents = new Vector<Document>();

	private Map<String, Integer> _docIds = new HashMap<String, Integer>();

	// Term frequency, key is the integer representation of the term and value is
	// the number of times the term appears in the corpus.
	private Map<Integer, Integer> _termCorpusFrequency =
			new HashMap<Integer, Integer>();

	// Maps each term to their integer representation
	Map<String, Integer> _dictionary = new HashMap<String, Integer>();

	// All unique terms appeared in corpus. Offsets are integer representations.
	Vector<String> _terms = new Vector<String>();

	// Term document frequency, key is the integer representation of the term and
	// value is the number of documents the term appears in.
	Map<Integer, Integer> _termDocFrequency = new HashMap<Integer, Integer>();

	//used for merging indexes
	private static int fileId = 1;
	private static int docId = 1;
	private static int documentsCount = 0;
	private int mergeCount = 10000;
	private static Map<String,Scanner> scanners= new HashMap<String,Scanner>();
	private static Map<String,String> pointerToScanners= new HashMap<String,String>();
	private static int finalIndexCount = 1;

	private final double[] pageRanks;
	private final Map<Integer, Integer> numViews;

	Map<Integer, Postings> postingLists = new HashMap<Integer, Postings>();

	@SuppressWarnings("unchecked")
	public IndexerInvertedDoconly(Options options) {
		super(options);
		System.out.println("Using Indexer: " + this.getClass().getSimpleName());
		try{
			pageRanks = (double[]) new CorpusAnalyzerPagerank(options).load();
			numViews = (Map<Integer, Integer>) new LogMinerNumviews(options).load();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructs the index from the corpus file.
	 * 
	 * @throws IOException
	 */
	@Override
	public void constructIndex() throws IOException {

		createWikiIndex(new File(_options._corpusPrefix));
		createTwitterIndex(new File("data/tweets"));

		System.out.println(
				"Indexed " + Integer.toString(_numDocs) + " docs with " +
						Long.toString(_totalTermFrequency) + " terms.");

		String indexFile = _options._indexPrefix + "/corpus.idx";
		System.out.println("Store index to: " + indexFile);

		ObjectOutputStream writer =
				new ObjectOutputStream(new FileOutputStream(indexFile));
		writer.writeObject(this);
		writer.close();
	}

	private void createWikiIndex(File corpusDirectory) throws FileNotFoundException {
		int fileCount = 0;
		finalIndexCount = 1;
		System.out.println("Processing Documents");
		
		if(corpusDirectory.isDirectory()){
			for(File corpusFile :corpusDirectory.listFiles()){
				processDocument(corpusFile);	
				fileCount++;

				if(fileCount > 0 && fileCount % 100 == 0){
					saveIndexInFile("wiki");
				}
			}
		}
		//save the remaining data
		saveIndexInFile("wiki");
		mergeFile("wiki");
	}
	
	private void createTwitterIndex(File corpusDirectory) throws FileNotFoundException {
		int fileCount = 0;
		finalIndexCount = 1;
		System.out.println("Processing Tweets");
		
		if(corpusDirectory.isDirectory()){
			for(File tweetFile :corpusDirectory.listFiles()){
				
				processTweet(tweetFile);	
				fileCount++;
				
				if(fileCount > 0 && fileCount % 100 == 0){
					saveIndexInFile("twitter");
				}
			}
		}
		//save the remaining data
		saveIndexInFile("twitter");
		mergeFile("twitter");

	}

	private void mergeFile(String docType)
	{
		System.out.println("Merging...");
		
		//Final index file
		T3FileWriter indexWriter = new T3FileWriter(_options._indexPrefix+"/index/"+docType+"/"+(finalIndexCount++)+".idx");

		File indexDirectory = new File(_options._indexPrefix+"/temp/"+docType);
		Gson gson = new Gson();

		if(indexDirectory.isDirectory())
		{
			indexWriter.write("{");
			for(int  i = 0 ; i < _dictionary.size();i++){

//				System.out.println("Merging indexes "+i+" out of "+_dictionary.size()+" terms");

				//get posting list of term_id i from all the files and merge them
				List<Integer> mergedPostingList = new ArrayList<Integer>();

				File[] files = indexDirectory.listFiles();

				Comparator<File> comp = new Comparator<File>()
						{
					public int compare(File f1, File f2)
					{

						// Alphabetic order otherwise
						Integer fileIndex1 = Integer.parseInt(f1.getName().replaceFirst(".idx",""));
						Integer fileIndex2 = Integer.parseInt(f2.getName().replaceFirst(".idx",""));
						return fileIndex1.compareTo(fileIndex2);
					}
						};
						Arrays.sort(files, comp); 

						for(File indexTempFile : files) {
							if(scanners.get(indexTempFile.getName()) == null) {
								try {
									Scanner scanner = new Scanner(indexTempFile);
									scanner.useDelimiter("],");
									scanners.put(indexTempFile.getName(),scanner);

								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							}

							String postingList = getPostingList(indexTempFile , i );

							//sample posting list = [1,2,3,4,5]
							if(postingList != null){
								try{

									if(postingList.charAt(postingList.length()-2)== '}'){
										postingList = postingList.substring(0,postingList.length()-2);
									}

									int[] intList = gson.fromJson(postingList, int[].class); 
									mergedPostingList.addAll(asList(intList));
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}

						//Write the merger list to file i
						if(i%mergeCount == 0 && i > 0 ){
							indexWriter.write("}");
							indexWriter.close();
							indexWriter = new T3FileWriter(_options._indexPrefix+"/index/"+docType+"/"+(finalIndexCount++)+".idx");
							indexWriter.write("{");
						}
						String entry = "\""+i+"\""+":"+gson.toJson(mergedPostingList);
						indexWriter.write(entry);
						if((i+1)%mergeCount != 0){
							indexWriter.write(",");
						}

						if(i == _dictionary.size() -1){
							indexWriter.write("}");
							indexWriter.close();
						}
			}
		}

		indexWriter.close();
		//clean temp files 
		deleteTempFiles();
	}


	private void deleteTempFiles() 
	{
		File directory = new File(_options._indexPrefix+"/temp");
		try{
			delete(directory);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void delete(File file) throws IOException{

		if(file.isDirectory()){

			//directory is empty, then delete it
			if(file.list().length==0){
				file.delete();
//				System.out.println("Directory is deleted : " 
//						+ file.getAbsolutePath());

			}else{

				//list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);

					//recursive delete
					delete(fileDelete);
				}

				//check the directory again, if empty then delete it
				if(file.list().length==0){
					file.delete();
//					System.out.println("Directory is deleted : " 
//							+ file.getAbsolutePath());
				}
			}

		}else{
			//if file, then delete it
			file.delete();
//			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}


	public List<Integer> asList(int[] ints)
	{
		List<Integer> intList = new ArrayList<Integer>();
		for (int index = 0; index < ints.length; index++)
		{
			intList.add(ints[index]);
		}
		return intList;
	}

	private String getPostingList(File indexTempFile, int term_id)
	{
		Scanner scanner = scanners.get(indexTempFile.getName());

		while(scanner.hasNext()){

			String nextElement ;
			if(pointerToScanners.get(indexTempFile.getName()) == null){
				nextElement = scanner.next();
				nextElement += "]";

			}else{
				nextElement = pointerToScanners.get(indexTempFile.getName());
			}


			nextElement = nextElement.substring(nextElement.indexOf("\""));

			String currentTerm_id =nextElement.substring(nextElement.indexOf("\"")+1,nextElement.lastIndexOf("\""));

			if(term_id == Integer.parseInt(currentTerm_id)){
				pointerToScanners.remove(indexTempFile.getName());
				return nextElement.substring(nextElement.indexOf(":")+1);
			}

			if(Integer.parseInt(currentTerm_id) > term_id){
				pointerToScanners.put(indexTempFile.getName(), nextElement);
				break;
			}else{
				System.out.println("If this line is showing up then something went terribly wrong....");
			}
		}

		return null;
	}

	private void clearMem()
	{
		_invertedIndex.clear();
		_documents.clear();
	}

	private void saveIndexInFile(String docType) {
		System.out.println("Saving file "+fileId);

		T3FileWriter fileWriter= new T3FileWriter(_options._indexPrefix+"/temp/"+docType+"/"+(fileId++)+".idx");
		Gson gson = new Gson();
		String json = gson.toJson(_invertedIndex);
		fileWriter.write(json);
		fileWriter.close();

		fileWriter= new T3FileWriter(_options._indexPrefix+"/Documents/"+(docId++)+".idx");
		json = gson.toJson(_documents);
		fileWriter.write(json);
		fileWriter.close();

		clearMem();
	}

	/**
	 * Process the raw content (i.e., one line in corpus.tsv) corresponding to a
	 * document, and constructs the token vectors for both title and body.
	 * @param content
	 * @throws FileNotFoundException 
	 */
	private void processDocument(File file) throws FileNotFoundException
	{

		DocumentProcessor documentProcessor = new DocumentProcessor();

		Vector<String> titleTokens_Str = documentProcessor .process(file.getName());
		Vector<String> bodyTokens_Str = documentProcessor.process(file);

		Vector<Integer> titleTokens = new Vector<Integer>();
		readTermVector(titleTokens_Str, titleTokens);

		Vector<Integer> bodyTokens = new Vector<Integer>();
		readTermVector(bodyTokens_Str, bodyTokens);

		//Document tokens
		Vector<Integer> documentTokens = bodyTokens;
		documentTokens.addAll(titleTokens);

		String title = file.getName();

		//Integer documentID = _documents.size();
		Integer documentID = documentsCount++;
		int numView = numViews.get(documentID);
		double pageRank = pageRanks[documentID];
		DocumentIndexed doc = new DocumentIndexed(documentID, null);
		doc.setTitle(title);
		doc.setNumViews(numView);
		doc.setPageRank((float)pageRank);
		doc.setDocumentTokens(documentTokens);
		doc.setUrl(title);
		_documents.add(doc);
		_docIds.put(title, documentID);
		++_numDocs;

		Set<Integer> uniqueTerms = new HashSet<Integer>();
		updateStatistics(documentID, doc.getDocumentTokens(), uniqueTerms);

		for (Integer idx : uniqueTerms) {
			_termDocFrequency.put(idx, _termDocFrequency.get(idx) + 1);
		}
	}
	
	
	private void processTweet(File file) {

		DocumentProcessor documentProcessor = new DocumentProcessor();
		ObjectInputStream ois = null;

		try{
			ois = new ObjectInputStream(new FileInputStream(file));
			Status tweet = (Status)ois.readObject();

			Vector<String> bodyTokens_Str = documentProcessor.process(tweet.getText());

			Vector<Integer> bodyTokens = new Vector<Integer>();
			readTermVector(bodyTokens_Str, bodyTokens);

			String title = tweet.getText();

			Integer documentID = documentsCount++;
			DocumentIndexed doc = new DocumentIndexed(documentID, null);
			doc.setTitle(title);
			doc.setNumViews(0);
			doc.setPageRank(0);
			doc.setTweet(tweet);
			doc.setDocumentTokens(bodyTokens);
			_documents.add(doc);
			_docIds.put(title, documentID);
			++_numDocs;

			Set<Integer> uniqueTerms = new HashSet<Integer>();
			updateStatistics(documentID, doc.getDocumentTokens(), uniqueTerms);

			for (Integer idx : uniqueTerms) {
				_termDocFrequency.put(idx, _termDocFrequency.get(idx) + 1);
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(ois != null) ois.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}

	/**
	 * Tokenize {@code content} into terms, translate terms into their integer
	 * representation, store the integers in {@code tokens}.
	 * @param content
	 * @param tokens
	 */
	private void readTermVector(Vector<String> tokens_str, Vector<Integer> tokens) {
		try{
			for (String token : tokens_str) {
				int idx = -1;
				if (_dictionary.containsKey(token)) {
					idx = _dictionary.get(token);
				} else {
					idx = _terms.size();
					_terms.add(token);
					_dictionary.put(token, idx);
					_termCorpusFrequency.put(idx, 0);
					_termDocFrequency.put(idx, 0);
				}
				tokens.add(idx);
			}
		}catch (OutOfMemoryError oome){
			throw new OutOfMemoryError(oome.getLocalizedMessage());
		} 
		return;
	}


	/**
	 * Update the corpus statistics with {@code tokens}. Using {@code uniques} to
	 * bridge between different token vectors.
	 * @param tokens
	 * @param uniques
	 */
	private void updateStatistics(Integer documentID, Vector<Integer> tokens, Set<Integer> uniques) {

		try{
			for(int i=0; i<tokens.size(); i++){

				Integer idx = tokens.get(i);
				uniques.add(idx);

				//create and initialize the posting list
				if(!_invertedIndex.containsKey(idx)){
					_invertedIndex.put(idx,new Postings());
				}

				if(!_invertedIndex.get(idx).contains(documentID)){
					_invertedIndex.get(idx).add(documentID);
				}

				_termCorpusFrequency.put(idx, _termCorpusFrequency.get(idx) + 1);
				++_totalTermFrequency;
			}
		}catch (OutOfMemoryError oome){
			throw new OutOfMemoryError(oome.getLocalizedMessage());
		} 
	}

	///// Loading related functions.

	/**
	 * Loads the index from the index file.
	 * 
	 * @throws IOException, ClassNotFoundException
	 */
	@Override
	public void loadIndex() throws IOException, ClassNotFoundException {

		String indexFile = _options._indexPrefix + "/corpus.idx";
		System.out.println("Load index from: " + indexFile);

		ObjectInputStream reader =
				new ObjectInputStream(new FileInputStream(indexFile));
		IndexerInvertedDoconly loaded = (IndexerInvertedDoconly) reader.readObject();

		this._documents = loaded._documents;
		// Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
		this._numDocs = _documents.size();
		for (Integer freq : loaded._termCorpusFrequency.values()) {
			this._totalTermFrequency += freq;
		}
		this._dictionary = loaded._dictionary;
		this._terms = loaded._terms;
		this._termCorpusFrequency = loaded._termCorpusFrequency;
		this._termDocFrequency = loaded._termDocFrequency;
		this._invertedIndex = loaded._invertedIndex;
		this._docIds = loaded._docIds;
		reader.close();

		System.out.println(Integer.toString(_numDocs) + " documents loaded " +
				"with " + Long.toString(_totalTermFrequency) + " terms!");
	}

	@Override
	public Document getDoc(int docid) {

		//check if it's available
		for(int i = 0 ; i< _documents.size(); i++){
			if(_documents.get(i)._docid == docid){
				return _documents.get(i);
			}
		}

		//if not then retrieve from backend store
		_documents = getDocuments(docid);

		//again check if its available
		for(int i = 0 ; i< _documents.size(); i++){
			if(_documents.get(i)._docid == docid){
				return _documents.get(i);
			}
		}

		return null;
		//return (/*docid >= _documents.size() ||*/ docid < 0) ? null : _documents.get(docid);
	}

	private Vector<Document> getDocuments(int docid2) {

		int file_no = (int)docid2 /100 + 1;
		String filepath = _options._indexPrefix+"/Documents/"+file_no+".idx";
		T3FileReader fileReader = new T3FileReader(filepath);
		String fileContents = fileReader.readAllBytes();
		fileReader.close();

		Gson gson = new Gson();

		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(fileContents).getAsJsonArray();

		Vector<Document> retVal = new Vector<Document>();
		for(int i =0;i<array.size();i++){
			DocumentIndexed doc = gson.fromJson(array.get(i), DocumentIndexed.class);
			retVal.add(doc);
		}

		return retVal;
	}

	/**
	 * In HW2, you should be using {@link DocumentIndexed}
	 */
	@Override
	public Document nextDoc(Query query, int docid) {
		return nextDoc(query, docid, "wiki");
	}
	
	public Document nextDoc(Query query, int docid, String docType) {

		System.out.println("NextDoc -> "+docid);
		Vector<String> queryTerms = query._tokens;

		if(docid<0){
			docid = 0;
		}

		//case 1 
		Vector <Integer> docIds = new Vector<Integer>();
		for(String token : queryTerms){
			Integer nextDocID = next(token, docid, docType);
			if(nextDocID == Integer.MAX_VALUE){
				//value not found;
				return null;
			}
			docIds.add(nextDocID);
		}

		//case 2 
		boolean documentFound = true;
		for(int i = 0 ; i < docIds.size() ; i++){
			if(docIds.get(i) != docIds.get(0)){
				documentFound = false;
				break;
			}
		}

		if(documentFound){
			Document doc = getDoc(docIds.get(0));
			return doc;
		}

		//case 3 
		Integer maxDocID = Collections.max(docIds);

		return nextDoc(query, maxDocID-1);
	}

	/**
	 * Finds the next document containing the term.
	 * If not found then it returns Integer.Maxvalue
	 * @param term
	 * @param docid 
	 * @return
	 */
	private int next(String term , int currentDoc, String docType){

		Postings postingList = null;
		Integer termID = _dictionary.get(term);

		if(postingLists.containsKey(termID)){
			postingList = postingLists.get(termID);
		} else if(_invertedIndex != null && _dictionary != null && termID != null){
			postingList = _invertedIndex.get(termID);
			postingLists.put(termID, postingList);
		}

		if(postingList == null){
			_invertedIndex = getIndex(termID, docType);
			postingList = _invertedIndex.get(termID);
			postingLists.put(termID, postingList);
		}

		Integer lt = postingList.size();
		Integer ct = postingList.getCachedIndex();

		if(ct == null){
			ct = 0;
			postingList.setCachedIndex(ct);
		}

		boolean isExit = postingList.get(lt-1) <= currentDoc;
		if(lt == 0 || isExit){
			return Integer.MAX_VALUE;
		}

		if(postingList.get(0) > currentDoc){
			postingList.setCachedIndex(0);
			return postingList.get(0);
		}

		if(ct > 0 && postingList.get(ct-1) > currentDoc){
			ct=0;
			postingList.setCachedIndex(0);
		}

		while(postingList.get(ct) <= currentDoc){
			ct = ct + 1;
		}

		postingList.setCachedIndex(ct);
		return postingList.get(ct);
	}

	private Map<Integer, Postings> getIndex(Integer integer, String docType) {

		int file_no = (int)integer /mergeCount + 1;
		String filepath = _options._indexPrefix+"/index/"+docType+"/"+file_no+".idx";
		T3FileReader fileReader = new T3FileReader(filepath);
		String fileContents = fileReader.readAllBytes();
		fileReader.close();

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
		Type type = new TypeToken<TreeMap<Integer,Postings>>(){}.getType();
		TreeMap<Integer,Postings> treeMap = gson.fromJson(fileContents, type);

		return treeMap;
	}

	@Override
	public int corpusDocFrequencyByTerm(String term) {
		return corpusDocFrequencyByTerm(term, "wiki");
	}
	
	public int corpusDocFrequencyByTerm(String term, String docType) {
		Postings p = _invertedIndex.get(_dictionary.get(term));
		if(p == null){
			_invertedIndex = getIndex(_dictionary.get(term), docType);
		}
		return _invertedIndex.get(_dictionary.get(term)).size();
	}

	@Override
	public int corpusTermFrequency(String term) {
		return _termCorpusFrequency.get(_dictionary.get(term));
	}

	@Override
	public int documentTermFrequency(String term, String url) {
		return documentTermFrequency(term, url, "wiki");
	}
	
	public int documentTermFrequency(String term, String url, String docType) {
		int returnValue = 0;
		int docID =  _docIds.get(url);

		Postings p = _invertedIndex.get(_dictionary.get(term));
		if(p == null){
			_invertedIndex = getIndex(_dictionary.get(term), docType);
		}

		HashMap<Integer,Integer> count_terms = _invertedIndex.get(_dictionary.get(term)).get_countTerm();
		returnValue = count_terms.get(docID);
		return returnValue;
	}

	@Override
	public int nextPhrase(Query query, int docid, int position) {
		return 0;
	}


	public String getTerm(int termId){
		return _terms.get(termId);
	}
}
