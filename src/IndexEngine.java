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

public class IndexEngine {
	
	public static void main(String[] args) throws IOException, ParseException {
		if(args.length < 2) {
			System.out.println("You did not input sufficient arguements. This program must accept two arguements");
			System.out.println("First Arguement: a path to the latimes.gz file");
			System.out.println("Second Arguement: a path to a directory where the documents and metadata are being stored");
			System.exit(0);
		}
		String localPathGzip = args[0];
		String localPathProcess = args[1];
		File results = new File(localPathProcess + "/filesToBeStored");
		if ( results.exists()) {
			System.out.println("The directory "+ "filesToBeStored" + " already exists.");
	    	System.exit(0);
		}
		readAndProcess(localPathGzip, localPathProcess);
	}
	
	public static void readAndProcess(String localPathGzip, String localPathProcess) throws IOException, ParseException{
//		Reads the zip file
		File latimesFile = new File(localPathGzip);
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
//		Setting an temp storage
		String storage4Data = "";
		
		while(data.hasNextLine()) {
			line = data.nextLine();
			storage4Data += line;
			storage4File.add(line);
//			Populating the temp stroage until the </doc> tag gets hit
			if(line.contains("</DOC>")) {
//				grabs the current file, gets the id, docno, metadata
//				and puts them into its hashmaps and makes a file
				id2MetaData.put(internalId, getMetaData(storage4Data, internalId)); 
				doc2Id.put(getDocNo(storage4Data),internalId);
				makeFile(storage4File, internalId, storage4Data, localPathProcess);
//				Clears the temp story for the next file
				storage4Data = "";
				storage4File = new ArrayList<String>();
				internalId +=1;
				System.out.println("Creating file with internal id: " + internalId + " and adding it to two hashmaps ");
			}
			
		}
		saveHashMap2File(doc2Id, id2MetaData, localPathProcess);

		
		buffered.close();
	}
//	Saving the hashmaps to file
	public static void saveHashMap2File(HashMap<String, Integer> doc2Id,HashMap<Integer, metaData> id2MetaData, String localPathProcess ) throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter writerA = new PrintWriter(localPathProcess + "/doc2Id.txt", "UTF-8");
		PrintWriter writerB = new PrintWriter(localPathProcess + "/id2MetaData.txt", "UTF-8");
		
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
				"{}" + value.getHeadline() + "{}" + value.getDate();
		return finalValue;
	}
	//Makes the file 
	public static void makeFile(ArrayList<String> storage4File, 
			int internalId, String storage4Data, String localPathProcess) 
					throws FileNotFoundException, UnsupportedEncodingException, ParseException {
		String id = Integer.toString(internalId);
		String date = getDateAsNum(storage4Data);
		File results = new File(localPathProcess + "/filesToBeStored");
		if(! results.exists()) {
			results.mkdir();
		}
		File directory = new File(localPathProcess + "/filesToBeStored/" + date);
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
		metaData data = new metaData(internalId, docNo, headLine, date );
		return data;
	}
}
