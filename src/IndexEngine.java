import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
//import metaData.java;

public class IndexEngine {
	
	public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException {
		
//		String currentDir = System.getProperty("user.dir");
//		System.out.println(currentDir);
		if(args.length < 2) {
			System.out.println("You did not input sufficient arguements. This program must accept two arguements");
			System.out.println("First Arguement: a path to the latimes.gz file");
			System.out.println("Second Arguement: a path to a directory where the documents and metadata are being stored");
			System.exit(0);
		}
		String localPathGzip = args[0];
		String localPathProcess = args[1];
//		String localPathProcess = "C:/Users/Rui/eclipse-workspace/541-Hw1";
//		String localPathGzip = "C:/Users/Rui/eclipse-workspace/541-Hw1/latimes.gz";
//		File results = new File(localPathProcess + "/filesToBeStored");
//		if ( results.exists()) {
//			System.out.println("The directory "+ "filesToBeStored" + " already exists.");
//	    	System.exit(0);
//		}
		readAndProcess(localPathGzip, localPathProcess);
	}
	
	public static void readAndProcess(String localPathGzip, String localPathProcess) throws IOException, ParseException, ClassNotFoundException{
//		Reads the zip file
		File latimesFile = new File(localPathGzip);
		InputStream fileStream = new FileInputStream(latimesFile);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);
		Scanner data = new Scanner(buffered);
		
//		Scanner data = new Scanner(new FileReader(localPathGzip));
		//initializes the first two hashmaps
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		String line = "";
		int internalId = 0;
		ArrayList<String> storage4File = new ArrayList<String>();
//		Setting an temp storage
		String storage4Data = "";
		
		HashMap <String,Integer> term2IdLexicon = new HashMap<>();
		HashMap <Integer, String> id2TermLexicon = new HashMap<>();
		HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndex = new HashMap<>();
		
//		int toDelete = 0;
//		for(int i = 0; i <100000; i ++) {
		while(data.hasNextLine()) {
			line = data.nextLine();
			storage4Data += line;
			storage4File.add(line);
//			System.out.println(line);
//			Populating the temp stroage until the </doc> tag gets hit
			if(line.contains("</DOC>")) {
				ArrayList<String> tokens = extractTokens(storage4Data);
				tokenIdLexicon data4tokensLexicon = convertTokens2Ids(tokens, term2IdLexicon, id2TermLexicon);
				HashMap <String,Integer> term2IdTemp = data4tokensLexicon.getTerm2IdLexicon();
				
				for(String j: term2IdTemp.keySet()) {
					if(!term2IdLexicon.containsKey(j)) {
						term2IdLexicon.put(j, term2IdTemp.get(j));
					}
				}
				
				ArrayList<Integer> tokenIds = data4tokensLexicon.getTokens();
				HashMap<Integer, Integer> wordCounts = countWords(tokenIds);
				invertedIndex= add2Posting(wordCounts, internalId, invertedIndex);
				
//				System.out.println("Size " + tokens.size());
				
//				ArrayList<String> tokens = extractTokens(storage4Data);
//				grabs the current file, gets the id, docno, metadata
//				and puts them into its hashmaps and makes a file
				id2MetaData.put(internalId, getMetaData(storage4Data, internalId)); 
				doc2Id.put(getDocNo(storage4Data),internalId);
				makeFile(storage4File, internalId, storage4Data, localPathProcess);
//				Clears the temp story for the next file
				storage4Data = "";
				storage4File = new ArrayList<String>();
				internalId +=1;
			}
			
		}
		
		id2TermLexicon = reverseHashMap(term2IdLexicon);
		saveHashMap2File(doc2Id, id2MetaData, localPathProcess);
		saveInvertedIndexLexicon(invertedIndex, id2TermLexicon, term2IdLexicon, localPathProcess);
		buffered.close();
		
	}
	public static HashMap<Integer,String> reverseHashMap(HashMap<String,Integer> map) {
	    HashMap<Integer, String> rev = new HashMap<>();
	    for(Entry<String, Integer> entry : map.entrySet())
	        rev.put(entry.getValue(), entry.getKey());
	    return rev;
	}
	public static void saveInvertedIndexLexicon(HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndex, 
		HashMap <Integer, String> id2TermLexicon, 
		HashMap <String,Integer> term2IdLexicon, String localPathProcess) throws IOException, ClassNotFoundException {
		File directory = new File("index");
	    if (! directory.exists()){
	        directory.mkdir();
	    }
//		Writing inverted index
//	    FileOutputStream file = new FileOutputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/testCollection/invertedIndex.txt"));
		FileOutputStream file = new FileOutputStream(new File(localPathProcess + "/index/invertedIndex.txt"));
		ObjectOutputStream toWrite = new ObjectOutputStream(file);
		toWrite.writeObject(invertedIndex);
//		Writing id 2 term lexicon
		file = new FileOutputStream(new File(localPathProcess + "/index/id2TermLexicon.txt"));
//		file = new FileOutputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/testCollection/id2TermLexicon.txt"));
		toWrite = new ObjectOutputStream(file);
		toWrite.writeObject(id2TermLexicon);
//		Writing term 2 id lexicon
//		file = new FileOutputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/testCollection/term2IdLexicon.txt"));
		file = new FileOutputStream(new File(localPathProcess + "/index/term2IdLexicon.txt"));
		toWrite = new ObjectOutputStream(file);
		toWrite.writeObject(term2IdLexicon);
		
		file.close();
		toWrite.close();
		
//		Reading inverted Index
//		FileInputStream fileRead = new FileInputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/index/invertedIndex.txt"));
//		FileInputStream fileRead = new FileInputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/testCollection/invertedIndex.txt"));
//		ObjectInputStream toRead = new ObjectInputStream(fileRead);
//		@SuppressWarnings("unchecked")
//		HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead  = (HashMap<Integer, ArrayList<DocIDCountPair>>) toRead.readObject();
////		Reading term 2 id Lexicon
////		fileRead = new FileInputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/index/term2IdLexicon.txt"));
//		fileRead = new FileInputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/testCollection/term2IdLexicon.txt"));
//		toRead = new ObjectInputStream(fileRead);
//		@SuppressWarnings("unchecked")
//		HashMap <String, Integer> term2IdLexiconRead =  (HashMap<String, Integer>) toRead.readObject();
////		Reading id 2 term lexicon
////		fileRead = new FileInputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/index/id2TermLexicon.txt"));
//		fileRead = new FileInputStream(new File("C:/Users/Rui/eclipse-workspace/541-Hw1/testCollection/id2TermLexicon.txt"));
//		toRead = new ObjectInputStream(fileRead);
//		@SuppressWarnings("unchecked")
//		HashMap <Integer, String> id2TermLexiconRead =  (HashMap<Integer, String>) toRead.readObject();
//		for(int i : invertedIndexRead.keySet()) {
//			ArrayList<DocIDCountPair> temp = invertedIndexRead.get(i);
//			System.out.println("Term Id: " + i);
//			System.out.println("Term Name: " + id2TermLexiconRead.get(i));
//			for(DocIDCountPair j: temp) {
//				System.out.println("DocId is: " + j.getDocID());
//				System.out.println("Doc Count is: " + j.getCount());
//				System.out.println("---");
//			}
//			System.out.println("---End of Temp---");
//		}
//		for(String i : term2IdLexiconRead.keySet()) {
//			System.out.println("key: " + i + " Value: " + term2IdLexiconRead.get(i));
//		}
	}
//	converting to tokenId
	public static tokenIdLexicon convertTokens2Ids(ArrayList<String> tokens, HashMap <String,Integer> term2IdLexicon, HashMap <Integer, String> id2TermLexicon) {
		ArrayList<Integer> tokenIds = new ArrayList<>();
		for (String i: tokens) {
			if(term2IdLexicon.containsKey(i)) {
				tokenIds.add(term2IdLexicon.get(i));
			} else {
				int id = term2IdLexicon.size();
				term2IdLexicon.put(i, id);
//				id2TermLexicon.put(id, i);
				tokenIds.add(id);
			}
		}
		tokenIdLexicon temp = new tokenIdLexicon(tokenIds, term2IdLexicon, id2TermLexicon);
		return temp;
	}
//	Count words
	public static HashMap<Integer, Integer> countWords(ArrayList<Integer> tokenIds) {
//		term id to count
		HashMap<Integer, Integer> wordCounts = new HashMap<>();
		for (int id: tokenIds) {
			if (wordCounts.containsKey(id)) {
				wordCounts.put(id, wordCounts.get(id)+1);
			} else {
				wordCounts.put(id, 1);
			}
		}
		return wordCounts;
	}
	public static HashMap<Integer, ArrayList<DocIDCountPair>> add2Posting(HashMap<Integer, Integer> wordCount, int docId, HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex) {
		for (int termId: wordCount.keySet()) {
//			System.out.println(termId);
			ArrayList<DocIDCountPair> postings = new ArrayList<>();
			int count = wordCount.get(termId);
			if (invertedIndex.containsKey(termId)) {
				postings = invertedIndex.get(termId);
			} else {
				postings = new ArrayList<DocIDCountPair>();
				invertedIndex.put(termId, postings);
			}
//			System.out.println("termId: " + termId + " docId: " + docId + " Count: " + count);
			DocIDCountPair temp = new DocIDCountPair(docId,count);
			postings.add(temp);
			invertedIndex.put(termId,postings);
		}
//		System.out.println("-----------------------------------------------");
		return invertedIndex;
	}
	
//	Tokenize
	public static ArrayList<String> extractTokens(String storage) {
		String rawText = "";
		int startPosition = 0;
		int endPosition = 0;
		if(storage.contains("</TEXT>")) {
			startPosition = storage.indexOf("<TEXT>") + "<TEXT>".length();
			endPosition = storage.indexOf("</TEXT>", startPosition);
			rawText = storage.substring(startPosition, endPosition).trim();
		}
		
		if(storage.contains("</HEADLINE>")) {
			startPosition = storage.indexOf("<HEADLINE>") + "<HEADLINE>".length();
			endPosition = storage.indexOf("</HEADLINE>", startPosition);
			rawText += storage.substring(startPosition, endPosition).trim();
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
		//tokenize
		ArrayList<String> tokens = tokenize(rawText);
		return tokens;
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

	public static void saveHashMap2File(HashMap<String, Integer> doc2Id,HashMap<Integer, metaData> id2MetaData, String localPathProcess ) throws FileNotFoundException, UnsupportedEncodingException {
		File directory = new File("index");
	    if (! directory.exists()){
	        directory.mkdir();
	    }
//	    PrintWriter writerA = new PrintWriter(localPathProcess + "/testCollection/doc2Id.txt", "UTF-8");
		PrintWriter writerA = new PrintWriter(localPathProcess + "/index/doc2Id.txt", "UTF-8");
//		PrintWriter writerB = new PrintWriter(localPathProcess + "/testCollection/id2MetaData.txt", "UTF-8");
		PrintWriter writerB = new PrintWriter(localPathProcess + "/index/id2MetaData.txt", "UTF-8");
		
		for (Map.Entry<String, Integer> entry : doc2Id.entrySet()) {
			String key = (String) entry.getKey();
			String value = entry.getValue().toString();
			writerA.println(key + "|" + value);
		}
		for (Map.Entry<Integer, metaData> entry : id2MetaData.entrySet()) {
			metaData toCopy = (metaData) entry.getValue();
	    	String key = entry.getKey().toString();
	    	String value = getDataFromMetadata(toCopy);
	    	writerB.println(key + "|" + value);
//	        System.out.println(key + " = " + value);
		}
	    writerA.close();
	    writerB.close();
	}
//	Grabing the data
	public static String getDataFromMetadata(metaData value) {
		String finalValue = "";
		finalValue = value.getId() + "{}" + value.getDocNo() + 
				"{}" + value.getHeadline() + "{}" + value.getDate() + "{}" + value.getDocLength();
		return finalValue;
	}
	//Makes the file 
	public static void makeFile(ArrayList<String> storage4File, 
			int internalId, String storage4Data, String localPathProcess) 
					throws FileNotFoundException, UnsupportedEncodingException, ParseException {
		String id = Integer.toString(internalId);
		String date = getDateAsNum(storage4Data);
		File results = new File(localPathProcess + "/filesToBeStored");
//		File results = new File(localPathProcess + "/testCollection");
		if(! results.exists()) {
			results.mkdir();
		}
		File directory = new File(localPathProcess + "/filesToBeStored/" + date);
//		File directory = new File(localPathProcess + "/testCollection/" + date);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
		PrintWriter writer = new PrintWriter(localPathProcess +"/filesToBeStored/" + date + "/" + id + ".txt", "UTF-8");
		for (Iterator<String> i = storage4File.iterator(); i.hasNext();) {
		    String item = i.next();
		    writer.println(item);
		}
		writer.close();
	}
//	Gets the docno
	public static String getDocNo(String storage) {
		int startPosition = storage.indexOf("<DOCNO>") + "<DOCNO>".length();
		int endPosition = storage.indexOf("</DOCNO>", startPosition);
		String docNo = storage.substring(startPosition, endPosition).trim();
		return docNo;
	}
//	Gets the date number from the docno
	public static String getDateAsNum(String storage) throws ParseException {
		String month = "";
		String day = "";
		String year = "";
		String date = "";
		if(storage.contains("</DOCNO>")) {
			String docNum = getDocNo(storage);
			docNum = docNum.replaceAll("\\D+","");
			month = docNum.substring(0,2);
			day = docNum.substring(2,4);
			year = docNum.substring(4,6);
		}
		date = year  + month  + day;
		return date;
	}
	public static metaData getMetaData(String storage, int internalId) throws ParseException {
		String date = getDateAsNum(storage);
		int monthNum = Integer.parseInt(date.substring(2,4));
		String month = new DateFormatSymbols().getMonths()[monthNum-1];
		String year = "19" + date.substring(0,2);
		String day = date.substring(4,6);
		String docNo = "";
		String headLine = "";
		if (day.substring(0,1).contains("0")) {
			day = day.substring(1, 2);
		}
		date = month + " " + day + " ," + year;
		if(storage.contains("</DOCNO>")) {
			docNo = getDocNo(storage);
		}
		if(storage.contains("<HEADLINE>") && storage.contains("</HEADLINE>")) {
			int startPosition = storage.indexOf("<HEADLINE>") + "<HEADLINE>".length();
			int endPosition = storage.indexOf("</HEADLINE>", startPosition);
			headLine = storage.substring(startPosition, endPosition).trim();
			headLine = headLine.replaceAll("<.*?>", "");
		}
		ArrayList<String> tokens = extractTokens(storage);
		String docLength = String.valueOf(tokens.size());
		metaData data = new metaData(internalId, docNo, headLine, date, docLength);
		return data;
	}
}
