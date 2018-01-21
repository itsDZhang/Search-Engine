import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Properties;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
//import metaData.java;

public class processFileProgram {
	
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
//		metaData yolo = new metaData();
		readAndProcess();
	}
	
//	@SuppressWarnings("unchecked")
	public static void readAndProcess() throws IOException, ParseException{
		File latimesFile = new File("./latimes.gz");
		InputStream fileStream = new FileInputStream(latimesFile);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);
		Scanner data = new Scanner(buffered);
		//initializes the first two hashmaps
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		
		String line = "";
		int internalId = 0;
		ArrayList<String> storage4File = new ArrayList<String>();
		String storage4Data = "";
		
		while(data.hasNextLine()) {
			
//		for( int i=0; i <20000; i++) {
			line = data.nextLine();
			storage4Data += line;
			storage4File.add(line);
			
			if(line.contains("</DOC>")) {
				
				id2MetaData.put(internalId, getMetaData(storage4Data, internalId)); 
				doc2Id.put(getDocNo(storage4Data),internalId);
				makeFile(storage4File, internalId, storage4Data);

				storage4Data = "";
				storage4File = new ArrayList<String>();
				internalId +=1;
			}
			
		}
		saveHashMap2File(doc2Id, id2MetaData);

		
		buffered.close();
	}
	
	public static void saveHashMap2File(HashMap<String, Integer> doc2Id,HashMap<Integer, metaData> id2MetaData ) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writerA = new PrintWriter("doc2Id.txt", "UTF-8");
		PrintWriter writerB = new PrintWriter("id2MetaData.txt", "UTF-8");
		
		for (Map.Entry<String, Integer> entry : doc2Id.entrySet()) {
//			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			String key = (String) entry.getKey();
			String value = entry.getValue().toString();
			writerA.println(key + "|" + value);
//			System.out.println(key + " = " + value);
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
	public static String getDataFromMetadata(metaData value) {
		String finalValue = "";
		
//		finalValue = "Internal Id: " + value.getId() + ",DocNo: " + value.getDocNo() + 
//				",Headline: " + value.getHeadline() + ",Date: " + value.getDate();
//		
		finalValue = value.getId() + "{}" + value.getDocNo() + 
				"{}" + value.getHeadline() + "{}" + value.getDate();
		
		return finalValue;
	}
	//Extracts each file from another 
	public static void makeFile(ArrayList<String> storage4File, int internalId, String storage4Data) throws FileNotFoundException, UnsupportedEncodingException, ParseException {
		
		String id = Integer.toString(internalId);
		String date = getDateAsNum(storage4Data);
		
		String currentDir = System.getProperty("user.dir");
		File results = new File(currentDir+ "/results");
		if(! results.exists()) {
			results.mkdir();
		}
		
		File directory = new File(currentDir + "/results/" + date);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	    
		PrintWriter writer = new PrintWriter("results/" + date + "/" + id + ".txt", "UTF-8");
		
		for (Iterator<String> i = storage4File.iterator(); i.hasNext();) {
		    String item = i.next();
		    writer.println(item);
		}
		
		writer.close();
	}
	
	public static String getDocNo(String storage) {
		
		int startPosition = storage.indexOf("<DOCNO>") + "<DOCNO>".length();
		int endPosition = storage.indexOf("</DOCNO>", startPosition);
		String docNo = storage.substring(startPosition, endPosition).trim();
		
		return docNo;
	}
	
	public static String getDateAsNum(String storage) throws ParseException {
		
		String month = "";
		String day = "";
		String year = "";
		String date = "";
		
		
		if(storage.contains("</DOCNO>")) {
			String docNum = getDocNo(storage);
			
			docNum = docNum.replaceAll("\\D+","");
//			System.out.println(docNum);
			month = docNum.substring(0,2);
			day = docNum.substring(2,4);
			year = docNum.substring(4,6);
			
		}
		
		date = year  + month  + day;
		
		return date;
	}
	
	public static metaData getMetaData(String storage, int internalId) throws ParseException {
		String date = getDateAsNum(storage);
//		System.out.println(date);
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
		
		//File 86 doesn't contain the beginning of the headline tag.
		if(storage.contains("<HEADLINE>") && storage.contains("</HEADLINE>")) {
			
			int startPosition = storage.indexOf("<HEADLINE>") + "<HEADLINE>".length();
			int endPosition = storage.indexOf("</HEADLINE>", startPosition);
//			System.out.println(storage);
			headLine = storage.substring(startPosition, endPosition).trim();
			
			
			headLine = headLine.replaceAll("<.*?>", "");
			
		}
//		System.out.println( month + " " + day + " " + year + " Internal id: " + internalId + " docNo: " + docNo + " Headline: " + headLine);
		
		metaData data = new metaData(internalId, docNo, headLine, date );
		return data;
					
	}
	
}
