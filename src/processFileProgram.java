import java.io.*;
import java.text.DateFormatSymbols;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

public class processFileProgram {
	
//	int internalId = 0;

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		readGzipFile();
		
	}
	
	public static void readGzipFile() throws IOException, ParseException {
		File latimesFile = new File("./latimes.gz");
		InputStream fileStream = new FileInputStream(latimesFile);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);
		
		String line = "";
		int internalId = 0;
		ArrayList<String> storage4File = new ArrayList<String>();
		String storage4Data = "";
		while(( line = buffered.readLine()) != null) {
			storage4Data += line;
			storage4File.add(line);
			
			//determining the ending of each file 
			if(line.contains("</DOC>")) {
				getMetaData(storage4Data, internalId); 
				makeFolder(storage4Data);
				makeFile(storage4File, internalId, storage4Data);
				storage4Data = "";
				storage4File = new ArrayList<String>();
				internalId +=1;
				
			}
		}
		buffered.close();
		
	}
	
	
	public static void makeFolder(String storage) throws ParseException {
		
		
		
		
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
	
	public static String getDateAsNum(String storage) throws ParseException {
		
		String month = "";
		String day = "";
		String year = "";
		String date = "";
		
		
		if(storage.contains("</DOCNO>")) {
			
			int startPosition = storage.indexOf("<DOCNO>") + "<DOCNO>".length();
			int endPosition = storage.indexOf("</DOCNO>", startPosition);
			String docNum = storage.substring(startPosition, endPosition).trim();
			
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
	
	public static void getMetaData(String storage, int internalId) throws ParseException {
		String date = getDateAsNum(storage);
//		System.out.println(date.substring(3,5));
		
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
			
			int startPosition = storage.indexOf("<DOCNO>") + "<DOCNO>".length();
			int endPosition = storage.indexOf("</DOCNO>", startPosition);
			docNo = storage.substring(startPosition, endPosition).trim();
			
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
		
					
	}
	
	
	
	
	
	
	
	
	
}
