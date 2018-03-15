package BM25;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BM25Main {
	
	
	public static void main(String[] argv) throws IOException, ClassNotFoundException{
		
		String localPath = "C:/Users/Rui/eclipse-workspace/541/h4Files";
		String localPathIndex = localPath + "/invertedIndex.txt";
//		String localPathQueries = args[0]+"/" +args[1];
////		the path of where you would store your output file
		String localPathOutputFileToStore = localPath + "/results";
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
		
//	    String word = "running" ;
//		String stem = PorterStemmer.stem(word);
//		System.out.println(word + " -> " + stem);
		InputStream fileStream = new FileInputStream(new File(localPath + "/hw4Files/topics.401-450.txt"));
		Reader decoder = new InputStreamReader(fileStream);
		BufferedReader reader = new BufferedReader(decoder);
		Scanner queries = new Scanner(reader);
		HashMap<Integer, Integer> docId2Count = docID2docCount( localPath + "/id2MetaData.txt");
		while(queries.hasNextLine()) {
			System.out.println(queries.nextLine());
		}
		
    }
	
	public static void BM25(int docId, 
			String query, 
			HashMap <String, Integer> term2Id, 
			HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex,
			HashMap<Integer, Integer> docId2Count) {
		double k1 = 1.2;
		double k2 = 7;
		double k;
		double tf4Doc, tf4Query, logVal, numofRelDocs, id, qfi, fi = 0; 
		double sumOfIterations =0;
		int numOfDocs = 131896;
		
		ArrayList<String> queryTerms = tokenize(query);
		HashMap<String, Integer> queryFreq = new HashMap<>();
		for(String term: queryTerms) {
			if(queryFreq.containsKey(term)) {
				queryFreq.put(term, queryFreq.get(term) + 1);
			} else {
				queryFreq.put(term,1);
			}
			
		}
		
		
		for( String term : queryTerms) {
			id = term2Id.get(term);
			numofRelDocs = invertedIndex.get(id).size();
			logVal = Math.log((numOfDocs - numofRelDocs + 0.5)/(numofRelDocs+0.5));
			qfi = queryFreq.get(term);
			tf4Query = (((k2+1)*qfi)/(k2+qfi));
			
			k = calcK(k1,docId, invertedIndex, term2Id,term, docId2Count);
			
			int termId = term2Id.get(term);
			ArrayList<DocIDCountPair> postings = invertedIndex.get(termId);
			for(DocIDCountPair post: postings) {
				int docTmpId = post.getDocID();
				if(docTmpId == docId) {
					fi = post.getCount();
					break;
				}
			}
			tf4Doc = ((k1 + 1)*fi)/(k+fi);
			
			
			sumOfIterations += tf4Doc + tf4Query + logVal;
			
		}
				
	}
	
	public static double calcK(double k1,
			int docId, 
			HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex,
			HashMap <String, Integer> term2Id, 
			String term,
			HashMap<Integer, Integer> docId2Count) {
		double k =0;
		double b = 0.75;
		int dl=0;
		double avgdl = 0;
		int termId = term2Id.get(term);
		dl = docId2Count.get(docId);
		ArrayList<DocIDCountPair> postings = invertedIndex.get(termId);
		
		for(DocIDCountPair post: postings) {
			int tmpDocId = post.getDocID();
			int docLenCount = docId2Count.get(tmpDocId);
			avgdl+=docLenCount;
		}
		avgdl = avgdl/postings.size();
		
		k = k1*((1-b)+ b*(dl/avgdl));
		
		return k;
	}
	public static HashMap<Integer, Integer> docID2docCount(String metaDataPath) throws FileNotFoundException {
		HashMap<Integer, Integer> docID2Count = new HashMap<>();
		Scanner docno2Counttxt = new Scanner(new FileReader(metaDataPath ));
		while(docno2Counttxt.hasNextLine()) {
			String nextLine = docno2Counttxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
			String[] data = nextLineArr[1].split("\\{}");
			int docCount = Integer.parseInt(data[4]);
			int docId = Integer.parseInt(data[0]);
			docID2Count.put(docId, docCount);
		}
		docno2Counttxt.close();
		return docID2Count;
	}

	//To tokenize
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
	
	
	
}

