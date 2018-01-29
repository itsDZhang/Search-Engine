import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanAnd {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
