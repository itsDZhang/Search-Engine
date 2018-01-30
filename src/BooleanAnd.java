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
		
		ArrayList<Queries> queries = readQueries(localPathQueries);
		
	}
	
	public static void sharkAndAttack() {
//		Key: doc id Value: value of count in postings 
		HashMap<Integer, Integer> docCount = new HashMap<>();
		
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
