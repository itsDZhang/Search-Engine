import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.text.BreakIterator;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SnippetEngine {

	public static void main(String[] args) throws ClassNotFoundException, IOException, ParseException {
		// TODO Auto-generated method stub
		String localPath = "C:/Users/Rui/eclipse-workspace/541/hw5Files";
		String filesLocalPath = "C:/Users/Rui/eclipse-workspace/541";
//		Reading term2Id Lexicon
		FileInputStream fileRead = new FileInputStream(new File(localPath + "/term2IdLexicon.txt"));
		ObjectInputStream toRead = new ObjectInputStream(fileRead);
		@SuppressWarnings("unchecked")
		HashMap <String, Integer> term2IdLexicon =  (HashMap<String, Integer>) toRead.readObject();
		
		System.out.println("Starting to Read Inverted Index. Time: " + LocalDateTime.now());
		fileRead = new FileInputStream(new File(localPath + "/invertedIndex.txt"));
		toRead = new ObjectInputStream(fileRead);
		@SuppressWarnings("unchecked")
		HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead  = (HashMap<Integer, ArrayList<DocIDCountPair>>) toRead.readObject();
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		id2MetaData = generateid2MetaDataHash(localPath +"/id2MetaData.txt");
		HashMap<Integer, Integer> docId2Count = docID2docCount( localPath + "/id2MetaData.txt");
		System.out.println("Finished Reading. Time: " + LocalDateTime.now());
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		doc2Id = generateDoc2IdHash(filesLocalPath);
		Scanner reader = new Scanner(System.in);
		
		boolean quit = false;
		
		while(!quit) {
			String query = reader.nextLine();
			
			if(query.equals("q")) {
				quit = true;
				break;
			}
			ArrayList<String> queryTerms = tokenize(query);
			
//			ArrayList<String> tokens = tokenize(query);
			
//			System.out.println(tokens.toString());
			ArrayList<String> docnoResult = BM25(query, term2IdLexicon, invertedIndexRead, docId2Count, id2MetaData);
//			System.out.println(docnoResult.toString());
			runSnippet(docnoResult, doc2Id, id2MetaData, filesLocalPath, queryTerms);
			
		}
		
		
		reader.close();
		
	}
	public static void runSnippet(ArrayList<String> docnoResult,
			HashMap<String, Integer> doc2Id, 
			HashMap<Integer, metaData> id2MetaData, 
			String filesLocalPath, 
			ArrayList<String> queryTerms) throws FileNotFoundException, ParseException {
		
		ArrayList<Snippet> snippets = new ArrayList<>();
		
		for(String docno : docnoResult ) {
			Snippet tmp = runAsDocNo(doc2Id, id2MetaData, docno, filesLocalPath, queryTerms);
			snippets.add(tmp);
		}
	
		
		
		int rank =1;
		for(Snippet snip: snippets) {
			
			if(snip.getHeadline().length() <2){
				String tmpHead = "";
				for(int i = 0; i<50; i++) {
					char counter = snip.getText().charAt(i);
					tmpHead +=counter;
				}
				snip.setHeadline(tmpHead);
			}
			
			
			System.out.println(rank + " " + snip.getHeadline() + " (" + snip.getDate() + ")");
			System.out.println(snip.getText() + " (" + snip.getDocno() + ")");
			
		}
		
		
		
	}


	public static Snippet runAsDocNo(HashMap<String, Integer> doc2Id, 
			HashMap<Integer, metaData> id2MetaData, 
			String docno, 
			String filesLocalPath,
			ArrayList<String> queryTerms) throws ParseException, FileNotFoundException {
		metaData testA = id2MetaData.get(doc2Id.get(docno));
		
		String date = getDateNum(docno);
		String storage = "";
		Scanner text = new Scanner( 
				new FileReader(filesLocalPath + "/filesToBeStored/" + date + "/" + 
						doc2Id.get(docno) + ".txt"));
		
//		System.out.println("DocNo: " + testA.getDocNo());
//		System.out.println("Internal Id: " + testA.getId());
//		System.out.println("Headline: " + testA.getHeadline());
//		System.out.println("Date: " +testA.getDate());
//		System.out.println("Raw Document: ");
		while(text.hasNextLine()) {
			storage += text.nextLine();
		}
		
		String snip = extractSnippet(storage, queryTerms);
		Snippet tmpSnip = new Snippet(snip, testA.getHeadline(), testA.getDate(), docno);
		
		
		
		text.close();
		return tmpSnip;
	}
//	Tokenize
	public static String extractSnippet(String storage, ArrayList<String> queryTerms) {
		String rawText = "";
		int startPosition = 0;
		int endPosition = 0;
		if(storage.contains("</TEXT>")) {
			startPosition = storage.indexOf("<TEXT>") + "<TEXT>".length();
			endPosition = storage.indexOf("</TEXT>", startPosition);
			rawText = storage.substring(startPosition, endPosition).trim();
		}
		
		if(storage.contains("</GRAPHIC>")) {
			startPosition = storage.indexOf("<GRAPHIC>") + "<GRAPHIC>".length();
			endPosition = storage.indexOf("</GRAPHIC>", startPosition);
			rawText += storage.substring(startPosition, endPosition).trim();
		}
		if(rawText.contains("</P>")) {
			rawText = rawText.replaceAll("<P>", "");
			rawText = rawText.replaceAll("</P>", "");
		}
		
		String snip = extractSnipFromSentence(rawText, queryTerms);
		
		
		return snip;
	}
	
	public static String extractSnipFromSentence(String Twosnip, ArrayList<String> queryTerms) {
		String result = "";
		ArrayList<String> sentences = new ArrayList<>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(Twosnip);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
			sentences.add(Twosnip.substring(start,end));
		}
		
		for(String sentence : sentences) {
			for(String term: queryTerms) {
				if(sentence.contains(term)) {
					result += sentence;
				}
			}
			
		}
		String finalResult = "";
		for(int i=0; i<345; i++) {
			char tmp = result.charAt(i);
			finalResult+=tmp;
		}
		finalResult += "...";
		return finalResult;
	}
	
//	This method grabs the docno to id txt file, reads the file and populates it
	public static HashMap<String, Integer> generateDoc2IdHash(String localPath) throws FileNotFoundException {
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		Scanner docno2idtxt = new Scanner(new FileReader(localPath + "/hw5Files/doc2Id.txt"));
		while(docno2idtxt.hasNextLine()) {
			String nextLine = docno2idtxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
//			System.out.println(nextLine);
			String key = nextLineArr[0];
			int value = Integer.parseInt(nextLineArr[1]);
			doc2Id.put(key, value);
//			System.out.println(docno2idtxt.next());
		}
		return doc2Id;
	}
//	Converts the string the string to a number
	public static String getDateNum(String docNum) throws ParseException {
		String month = "";
		String day = "";
		String year = "";
		String date = "";
			docNum = docNum.replaceAll("\\D+","");
			month = docNum.substring(0,2);
			day = docNum.substring(2,4);
			year = docNum.substring(4,6);
		date = year  + month  + day;
		return date;
	}
	public static ArrayList<String> BM25(
			String query, 
			HashMap <String, Integer> term2Id, 
			HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex,
			HashMap<Integer, Integer> docId2Count,
			HashMap<Integer, metaData> id2MetaData) {
		double k1 = 1.2;
		double k2 = 7;
		double k;
		double tf4Doc, tf4Query, logVal, numofRelDocs, id, qfi, fi = 0; 
		double sumOfIterations =0;
		double numOfDocs = 131896;
		int docId = 0;
		ArrayList<String> queryTerms = tokenize(query);
		HashMap<String, Integer> queryFreq = new HashMap<>();
		for(String term: queryTerms) {
			if(queryFreq.containsKey(term)) {
				queryFreq.put(term, queryFreq.get(term) + 1);
			} else {
				queryFreq.put(term,1);
			}
			
		}
		Map<Integer, Double> accumulator = new HashMap<>();
		
		
		for( String term : queryTerms) {
			int termId = term2Id.get(term);
			ArrayList<DocIDCountPair> postings = invertedIndex.get(termId);
//			System.out.println(term);
//			System.out.println("Term: " + term + " query: " + queryTerms.toString());
			for(DocIDCountPair post: postings) {
				docId = post.getDocID();
//				id = term2Id.get(term);
				numofRelDocs = postings.size();
				logVal = Math.log((numOfDocs - numofRelDocs + 0.5)/(numofRelDocs+0.5));
				qfi = queryFreq.get(term);
				tf4Query = (((k2+1)*qfi)/(k2+qfi));
				
				k = calcK(k1,docId, invertedIndex, term2Id,term, docId2Count);
				fi = post.getCount();
				tf4Doc = ((k1 + 1)*fi)/(k+fi);
				sumOfIterations = tf4Doc * tf4Query * logVal;
				
				if(accumulator.containsKey(docId)) {
					accumulator.put(docId, accumulator.get(docId) + sumOfIterations);
				} else {
					accumulator.put(docId,sumOfIterations);
				}
			}
		}
		Map sortedMap = sortByValue(accumulator);
		ArrayList<String> docnoResult = new ArrayList<>();
		int counter = 1;
		for( Object i: sortedMap.keySet()) {
			if(counter == 11) break;
			
			String docno = id2MetaData.get(i).getDocNo();
			docnoResult.add(docno);
			counter ++;
		}
		return docnoResult;
	}
//	Credits: https://www.mkyong.com/java/how-to-sort-a-map-in-java/
	private static Map<Integer, Double> sortByValue(Map<Integer, Double> unsortMap) {
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
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
		double avgdl = 534.47;
//		double avgdl = 513.46;
		int termId = term2Id.get(term);
		dl = docId2Count.get(docId);
		ArrayList<DocIDCountPair> postings = new ArrayList<>();
		postings = invertedIndex.get(termId);
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
	
	
	
//	This method grabs the id to metadata txt file, reads it, and populates the hashmap	
	public static HashMap<Integer, metaData> generateid2MetaDataHash(String localPath) throws FileNotFoundException {
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		Scanner id2MetaDatatxt = new Scanner(new FileReader(localPath));
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
//						========================= Porter Stemming ========================
//						String token = PorterStemmer.stem(text.substring(start, i));
//						========================================
						String token = text.substring(start, i);
						tokens.add(token);
					}
					start = i+1;
				}
			}
			if(start!=i) {
//				========================= Porter Stemming ========================
//				tokens.add(PorterStemmer.stem(text.substring(start, i)));
//				========================================
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
