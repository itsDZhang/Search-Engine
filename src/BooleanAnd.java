import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class BooleanAnd {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		String localPathIndex = args[0];
		String localPathQueries = args[0]+"/" +args[1];
////		the path of where you would store your output file
		String localPathOutputFileToStore = args[0] + "/" + args[2];
//		Reading inverted Index
		FileInputStream fileRead = new FileInputStream(new File(localPathIndex + "/invertedIndex.txt"));
		ObjectInputStream toRead = new ObjectInputStream(fileRead);
		@SuppressWarnings("unchecked")
		HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead  = (HashMap<Integer, ArrayList<DocIDCountPair>>) toRead.readObject();
//		Reading term2Id Lexicon
		fileRead = new FileInputStream(new File(localPathIndex + "/term2IdLexicon.txt"));
		toRead = new ObjectInputStream(fileRead);
		@SuppressWarnings("unchecked")
		HashMap <String, Integer> term2IdLexicon =  (HashMap<String, Integer>) toRead.readObject();
		System.out.println("Read the index");
//		Reading the metaData
//		System.out.println(invertedIndexRead.size());
//		System.out.println(term2IdLexicon.size());
//		
		HashMap <Integer, metaData> id2MetaData =  generateid2MetaDataHash(localPathIndex);
		
//		String localPathQueries = "C:/Users/Rui/eclipse-workspace/541-Hw1/topics.txt";
//		the path of where you would store your output file
//		String localPathOutputFileToStore = "C:/Users/Rui/eclipse-workspace/541-Hw1";

		
		ArrayList<Queries> rawQueries = readQueries(localPathQueries);
		ArrayList<ArrayList<String>> queries = new ArrayList<>();
		
		for( Queries i : rawQueries) {
			String words = i.getTitle();
//			System.out.println(words);
			queries.add(extractTokens(words));
		}
		
		
		
//		topicid to docId
		LinkedHashMap<String, Integer> resultList = sharkAndAttack(queries, invertedIndexRead, term2IdLexicon, rawQueries);
		
//		for(String i: resultList.keySet()) {
//			System.out.println(i + " " + resultList.get(i));
//			
//		}
		
		ArrayList<String> result = formalizeResults(resultList, id2MetaData);
		writeResults(result, localPathOutputFileToStore);
		
	}
	public static void writeResults(ArrayList<String> result, String localPathOutputFileToStore) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writerA = new PrintWriter(localPathOutputFileToStore, "UTF-8");
		
		for(String line: result) {
			writerA.println(line);
		}
		writerA.close();
	}
	
	public static ArrayList<String> formalizeResults(LinkedHashMap<String, Integer> resultList , HashMap<Integer, metaData> id2MetaData) {
		ArrayList<String> resultArray = new ArrayList<>();
		String topicId = "    ";
		String q0 = "q0";
		String docNo = "";
		int rank = 1, score = 0, docId = 0, scoreCount=1;
		String runTag = "r255zhanAND";
		
		ArrayList<Integer> scoreKeeper = getScores(resultList);
		
		for( String key : resultList.keySet()) {
			if (!topicId.substring(0,3).equals(key.substring(0,3))) {
				rank = 1;
				score = scoreKeeper.get(scoreCount);
				scoreCount+=1;
			}
			topicId = key;
			docId = resultList.get(key);
			metaData currentTemp = id2MetaData.get(docId);
			docNo = currentTemp.getDocNo();
			
			String resultLine = topicId.substring(0,3) + " " + q0 + " " + docNo + " " + rank + " " + (score-rank) + " " + runTag;
			resultArray.add(resultLine);
			rank += 1;
		}
		
		return resultArray;
	}
//	resultList = Topic--> docId
	public static ArrayList<Integer> getScores(LinkedHashMap<String, Integer> resultList){
		ArrayList<Integer> resultScores = new ArrayList<>();
		String tempTopicId = "    ";
		int numItemRetrieved = 1;
		
		for(String topicId : resultList.keySet()) {
			if( !tempTopicId.substring(0,3).equals(topicId.substring(0,3))) {
				resultScores.add(numItemRetrieved);
				numItemRetrieved = 1;
			}
			numItemRetrieved +=1;
			tempTopicId = topicId;
			
		
		}
		resultScores.add(numItemRetrieved);
		
//		for(int i : resultScores) {
//			System.out.println(i);
//		}
//		
		return resultScores;
	}
	
	public static LinkedHashMap<String, Integer> sharkAndAttack(
			ArrayList<ArrayList<String>> queries, 
			HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead, 
			HashMap <String,Integer> term2IdLexicon,
			ArrayList<Queries> rawQueries ) {

		ArrayList<DocIDCountPair> postings = new ArrayList<>();
		LinkedHashMap<String, Integer> resultList = new LinkedHashMap<>();
		int i = 0; // Used for getting the topic number
		for( ArrayList<String> perQuery : queries) {
//			Key: doc id Value: value of count in postings 
			HashMap<Integer, Integer> docCount = new HashMap<>();
			for( String term: perQuery) {
				
				if(!term2IdLexicon.containsKey(term)) continue;
				
				int termId = term2IdLexicon.get(term);
				postings = invertedIndexRead.get(termId);

				for( DocIDCountPair j : postings) {
					int docId = j.getDocID();
					
					if(docCount.containsKey(docId)) {
						docCount.put(docId, docCount.get(docId) + 1);
//						System.out.println( "docId: " +  docId);
					} else {
						docCount.put(docId, 1);
					}
				}
			}
			for( int docId: docCount.keySet()) {
				if( docCount.get(docId) == perQuery.size()) {
					Queries temp = rawQueries.get(i);
//					System.out.println(docCount.get(docId) + " " + perQuery.size());
					String topicID = temp.getNum();
					resultList.put(topicID + docId , docId);
//					resultList.put(topicID , docId);
//					System.out.println("topicId: " + topicID + " Docid: " + docId);
				}
			}
			i+=1;
		}
		return resultList;
	}
	public static ArrayList<Queries> readQueries(String localPathQueries) throws FileNotFoundException{
		ArrayList<Queries> queryArr = new ArrayList<Queries>();
		File latimesFile = new File(localPathQueries);
		InputStream fileStream = new FileInputStream(latimesFile);
		Reader decoder = new InputStreamReader(fileStream);
		BufferedReader buffered = new BufferedReader(decoder);
		Scanner data = new Scanner(buffered);
		
		ArrayList<String> tempQueries = new ArrayList<String>();
		while(data.hasNextLine()) {
			tempQueries.add(data.nextLine());
		}
		for (int i = 0; i<tempQueries.size(); i+=2) {
			String topicNum = tempQueries.get(i);
			String topicTitle = tempQueries.get(i+1);
			Queries tempData = new Queries(topicNum, topicTitle);
			queryArr.add(tempData);
		}
		return queryArr; 
		
	}
//	Tokenize
	public static ArrayList<String> extractTokens(String storage) {
		//tokenize
		ArrayList<String> tokens = tokenize(storage);
		return tokens;
		
	}
	public static ArrayList<String> tokenize(String text) {
		text = text.toLowerCase();
		ArrayList<String> tokens = new ArrayList<String>();
		int start = 0;
		int i =0;
		
		for (i=0;i<text.length();++i) {
			String c = text.substring(i, i+1);
			if(  checkForCharAndDigits(c) ) {
				if( start != i ) {
					String token = text.substring(start, i);
					tokens.add(token);
				}
				start = i+1;
			}
		}
		if(start!=i) {
			tokens.add(text.substring(start, i));
		}
		return tokens;
	}
	public static boolean checkForCharAndDigits(String str) {
        Matcher m = Pattern.compile("[^a-zA-Z0-9]").matcher(str);
        if (m.find()) return true;
        else          return false;
    }
	public static HashMap<Integer, metaData> generateid2MetaDataHash(String localPathIndex) throws FileNotFoundException {
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		Scanner id2MetaDatatxt = new Scanner(new FileReader(localPathIndex + "/id2MetaData.txt"));
		while(id2MetaDatatxt.hasNextLine()) {
			String nextLine = id2MetaDatatxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
			int key = Integer.parseInt(nextLineArr[0]);
			String[] data = nextLineArr[1].split("\\{}");
			metaData meta = new metaData(Integer.parseInt(data[0]), data[1], data[2],data[3], data[4]);
			id2MetaData.put(key, meta);
		}
		return id2MetaData;
	}
}
