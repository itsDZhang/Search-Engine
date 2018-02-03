import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class BooleanAnd {

	public static void main(String[] args) throws FileNotFoundException {

//		String localPathInvertedIndex = args[0];
//		String localPathQueries = args[1];
////		the path of where you would store your output file
//		String localPathOutputFileToStore = args[2];
		String localPathInvertedIndex = "C:/Users/Rui/eclipse-workspace/541-Hw1/invertedIndex.txt";
		String localPathQueries = "C:/Users/Rui/eclipse-workspace/541-Hw1/topics.txt";
//		the path of where you would store your output file
		String localPathOutputFileToStore = "C:/Users/Rui/eclipse-workspace/541-Hw1";
		HashMap <Integer, metaData> id2MetaData = new HashMap<>();
		HashMap <String,Integer> term2IdLexicon = new HashMap<>();
		HashMap <Integer, String> id2TermLexicon = new HashMap<>();
		HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead = new HashMap<Integer, ArrayList<DocIDCountPair>>();
		ArrayList<Queries> rawQueries = readQueries(localPathQueries);
		ArrayList<ArrayList<String>> queries = new ArrayList<>();
		
		for( Queries i : rawQueries) {
			String words = i.getTitle();
			queries.add(extractTokens(words));
		}
		
		
		
//		ResultList has all the docId's 
		ArrayList<Integer> resultList =  sharkAndAttack(queries, invertedIndexRead, term2IdLexicon);
		
	}
	
//	@SuppressWarnings("unlikely-arg-type")
	public static ArrayList<Integer> sharkAndAttack(ArrayList<ArrayList<String>> queries, HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead, HashMap <String,Integer> term2IdLexicon ) {
//		Key: doc id Value: value of count in postings 
		HashMap<Integer, Integer> docCount = new HashMap<>();
		ArrayList<DocIDCountPair> postings = new ArrayList<>();
		ArrayList<Integer> resultList = new ArrayList<>();
		for( ArrayList<String> perQuery : queries) {
//			Queries is incomplete
			for( String term: perQuery) {
				
				int termId = term2IdLexicon.get(term);
				postings = invertedIndexRead.get(termId);
				
				for( DocIDCountPair j : postings) {
					int docId = j.getDocID();
					
					if(docCount.containsKey(docId)) {
						docCount.put(docId, docCount.get(docId) + 1);
					} else {
						docCount.put(docId, 1);
					}
				}
			}
			for( int docId: docCount.keySet()) {
				if( docCount.get(docId) == perQuery.size()) {
					resultList.add(docId);
				}
			}
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
			tokens.add(text.substring(start, i-start));
		}
		return tokens;
	}
	public static boolean checkForCharAndDigits(String str) {
        Matcher m = Pattern.compile("[^a-zA-Z0-9]").matcher(str);
        if (m.find()) return true;
        else          return false;
    }
}
