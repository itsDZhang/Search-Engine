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
import java.util.zip.GZIPInputStream;
//import metaData.java;

public class processFileProgram {
	
//	int internalId = 0;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
//		metaData yolo = new metaData();
		readAndProcess();
	}
	
	@SuppressWarnings("unchecked")
	public static void readAndProcess() throws IOException, ParseException{
		File latimesFile = new File("./latimes.gz");
		InputStream fileStream = new FileInputStream(latimesFile);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		
		String line = "";
		int internalId = 0;
		ArrayList<String> storage4File = new ArrayList<String>();
		String storage4Data = "";
		while(( line = buffered.readLine()) != null) {
			storage4Data += line;
			storage4File.add(line);
			
			//determining the ending of each file 
			if(line.contains("</DOC>")) {
				id2MetaData.put(internalId, getMetaData(storage4Data, internalId)); 
				doc2Id.put(getDocNo(storage4Data),internalId);
				makeFile(storage4File, internalId, storage4Data);
//				
//				
//				try {
//			         FileOutputStream fileOut =
//			         new FileOutputStream("data.txt");
//			         ObjectOutputStream out = new ObjectOutputStream(fileOut);
//			         out.writeObject(id2MetaData);
//			         out.close();
//			         fileOut.close();
//			         System.out.printf("test");
//			      } catch (IOException i) {
//			         i.printStackTrace();
//			      }
//				HashMap<Integer,metaData> id2MetaDataFinal = new HashMap<>();
//				try {
//			         FileInputStream fileIn = new FileInputStream("data.txt");
//			         ObjectInputStream in = new ObjectInputStream(fileIn);
//			         id2MetaDataFinal = (HashMap<Integer,metaData>) in.readObject();
//			         in.close();
//			         fileIn.close();
//			      } catch (IOException i) {
//			         i.printStackTrace();
//			         return;
//			      }
//			      catch (ClassNotFoundException c) {
//			         System.out.println("Employee class not found");
//			         c.printStackTrace();
//			         return;
//			      }
//			      
//			      System.out.println(id2MetaDataFinal.get(internalId));
				
				storage4Data = "";
				storage4File = new ArrayList<String>();
				internalId +=1;
			}
		}
//		Properties properties = new Properties();
//		for (Map.Entry<String,Integer> entry : doc2Id.entrySet()) {
//		    properties.put(entry.getKey(), entry.getValue());
//		}
//		properties.store(new FileOutputStream("data.properties"), null);
		
		
		buffered.close();
	}
	
	
	
	//Extracts each file from another 
	public static void makeFile(ArrayList<String> storage4File, int internalId, String storage4Data) throws FileNotFoundException, UnsupportedEncodingException, ParseException {
		
		String id = Integer.toString(internalId);
		String date = getDateAsNum(storage4Data);
		
		String currentDir = System.getProperty("user.dir");
//		System.out.println(currentDir);
		
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
//			System.out.println(year + "-" + month + "-" + day);
			
		}
		
//		System.out.println(year + "-" + month + "-" + day);
		date = year + "-" + month + "-" + day;
		
		return date;
	}
	
	public static metaData getMetaData(String storage, int internalId) throws ParseException {
		String date = getDateAsNum(storage);
//		System.out.println(date.substring(3,5));
		System.out.println(date);
		int monthNum = Integer.parseInt(date.substring(3,5));
		String month = new DateFormatSymbols().getMonths()[monthNum-1];
		
		String year = "19" + date.substring(0,2);
		
		String day = date.substring(6,8);
		String docNo = "";
		String headLine = "";
		
		if (day.substring(0,1).contains("0")) {
			day = day.substring(1, 2);
		}
		
		
		
		if(storage.contains("</DOCNO>")) {
			
			
			docNo = getDocNo(storage);
			
//			System.out.println(subS);
		}
		if(storage.contains("</HEADLINE>")) {
			
			int startPosition = storage.indexOf("<HEADLINE>") + "<HEADLINE>".length();
			int endPosition = storage.indexOf("</HEADLINE>", startPosition);
			headLine = storage.substring(startPosition, endPosition).trim();
			
			headLine = headLine.replaceAll("<.*?>", "");
			
//			System.out.println(subS);
		}
		System.out.println( month + " " + day + " " + year + " Internal id: " + internalId + " docNo: " + docNo + " Headline: " + headLine);
		
		metaData data = new metaData(internalId, docNo, headLine, date );
		return data;
					
	}
	
	
	
}
