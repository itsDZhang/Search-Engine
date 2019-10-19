package Evaluation;

import java.util.*;

public class RelevanceJudgements {

//	public
	public class Tuple {
		private String queryID ="";
		private String docID = "";
		private int relevant = 0;

		public Tuple(String queryID, String docID, int relevant) {
			this.queryID = queryID;
			this.docID = docID;
			this.relevant = relevant;
		}

		public String getQueryID() {
			return this.queryID;
		}
		public String getDocID() {
			return this.docID;
		}
		public int getRelevant() {
			return this.relevant;
		}
//		I removed "Static" from the original code
		public String generateKey(String queryID, String docID) {
			return queryID + "-" + docID;
		}
		public String getKey() {
			return this.queryID + "-" + this.docID;
		}
	}

	private HashMap<String, Tuple> tuples = new HashMap<>();
	private HashMap<String, ArrayList<String>> query2reldocnos = new HashMap<>();

	public RelevanceJudgements() {
		this.tuples = new HashMap<>();
		this.query2reldocnos = new HashMap<>();

	}
	 public void addJudgements(String queryID, String docID, int relevant) throws Exception {
		 Tuple tuple = new Tuple(queryID, docID, relevant);
		 if(this.tuples.containsKey(tuple.getKey())) {
			 throw new Exception( "Cannot have duplicate queryID and docID data points" ) ;
		 }
		 this.tuples.put(tuple.getKey(), tuple);

		 if(tuple.relevant !=0) {

			 ArrayList<String> tmpRelDocnos = null;
			 if(query2reldocnos.containsKey(queryID)) {
				 tmpRelDocnos = query2reldocnos.get(queryID);
			 }else {
				 tmpRelDocnos = new ArrayList<>();
				 query2reldocnos.put(queryID, tmpRelDocnos);
			 }
			 if(!tmpRelDocnos.contains(docID)) {
				 tmpRelDocnos.add(docID);
			 }
			 query2reldocnos.put(queryID, tmpRelDocnos);

		 }


	 }
	 public String generateKey(String queryID, String docID) {
			return queryID + "-" + docID;
	 }

	 public boolean isRelevant(String queryID, String docID) throws Exception {
		 return getJudgement(queryID, docID, true) != 0;
	 }
	 public int isNotRelevant(String queryID, String docID) throws Exception {
		 return getJudgement(queryID, docID, false);
	 }

	 public int getJudgement(String queryID, String docID, boolean assumeNonRelevant) throws Exception {
		 if( ! this.query2reldocnos.containsKey(queryID)) {
			 throw new Exception( "no relevance judgments for queryID = " + queryID ) ;
		 }

		 String key = generateKey(queryID, docID);

		 if(!tuples.containsKey(key)) {
			 if(assumeNonRelevant)
				 return 0;
			 else
				 throw new Exception( "no relevance judgement for queryID and docID" ) ;
		 } else {
			 Tuple tuple = (Tuple) tuples.get(key);
			 return tuple.relevant;
		 }
	 }

	 public int numRelevant(String queryID) throws Exception {
		 if (this.query2reldocnos.containsKey(queryID)) {
			 return this.query2reldocnos.get(queryID).size();
		 } else {
			 throw new Exception("No relevance judgements for queryID = " + queryID);
		 }
	 }
//	 Might not work
	 public ArrayList<String> getQueryIDs(){
		ArrayList<String> keySet = new ArrayList<>();
			for( String i : this.query2reldocnos.keySet()) {
				keySet.add(i);
			}

		return keySet;
	 }

	 public ArrayList<String> getRelDocnos(String queryID) throws Exception {
		 if( this.query2reldocnos.containsKey(queryID)) {
			 return this.query2reldocnos.get(queryID);
		 } else {
			 throw new Exception("no relevance judgement for queryID = " + queryID);
		 }
	 }

}
