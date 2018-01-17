import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class processFileProgram {

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		readFile();
		
	}
	
	public static void readFile() throws IOException, ParseException {
		File latimesFile = new File("./latimes.gz");
		InputStream fileStream = new FileInputStream(latimesFile);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream);
		BufferedReader buffered = new BufferedReader(decoder);

		
		String line;
//		String date = "";
		String storage = "";
		while(( line = buffered.readLine()) != null) {
			storage += line;
			
			// To find the date of the current File
			if(line.contains("</DATE>")) {
				
				int startPosition = storage.indexOf("<DATE><P>") + "<DATE><P>".length();
				int endPosition = storage.indexOf("</P></DATE>", startPosition);
				String subS = storage.substring(startPosition, endPosition).trim();
				
				//formatting the date properly
				String start_dt = "2011-01-01";
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD"); 
				Date date = (Date)formatter.parse(start_dt);
				SimpleDateFormat newFormat = new SimpleDateFormat("MM-dd-yyyy");
				String finalString = newFormat.format(date);
				System.out.println(finalString);
			}
			
			//determining the ending of each file 
			if(line.contains("</DOC>")) {
				System.out.println(storage);
				storage = "";
				break;
			}
		}
		
	}
	
	public static void makeFolder() {
		
	}
	//Extracts each file from another 
	public static void makeFile() {
		
	}
	
	
	
	
	
	
	
	
	
}
