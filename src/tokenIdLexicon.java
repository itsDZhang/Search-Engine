import java.util.ArrayList;
import java.util.HashMap;

public class tokenIdLexicon {
	private ArrayList<Integer> tokens;
	private HashMap <String,Integer> term2IdLexicon; 
	private HashMap <Integer, String> id2TermLexicon;
	
	tokenIdLexicon(ArrayList<Integer> tokens, HashMap <String,Integer> term2IdLexicon, HashMap <Integer, String> id2TermLexicon){
		this.tokens = tokens;
		this.term2IdLexicon = term2IdLexicon;
		this.id2TermLexicon = id2TermLexicon;
		
	}
	public ArrayList<Integer> getTokens(){
		return this.tokens;
	}
	public HashMap <String,Integer> getTerm2IdLexicon() {
		return this.term2IdLexicon;
	}
	public HashMap <Integer,String> getid2TermLexicon(){
		
		return this.id2TermLexicon;
	}
}
