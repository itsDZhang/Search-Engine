package Evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Results {
//	implements comparable
	public class Result implements Comparable<Result>  {
		
		private String docID = "";
		private double score = 0;
		private int rank = 0;
		
		public Result(String docID, double score, int rank) {
			this.docID = docID;
			this.score = score;
			this.rank = rank;
			
		}
		
		public String getDocID() {
			return this.docID;
		}
		public double getScore() {
			return this.score;
		}
		public int getRank() {
			return this.rank;
		}
//		@Override;
		public int compareTo(Result obj) {
//			Result rhs = obj;
//			Result lhs = this;
//			if(lhs.score > rhs.getScore()) {
//				return 1;
//			} else if (lhs.score < rhs.getScore()) {
//				return -1;
//			} else {
//				return -1 * lhs.docID.compareTo(rhs.docID);
//			}
//			=========================================================
			Result rhs = (Result) obj;
            Result lhs = this;
            int scoreCompare = -1 * Double.compare(lhs.getScore(), rhs.getScore());
            if (scoreCompare == 0) {
                return -1 * lhs.getDocID().compareTo(rhs.getDocID());
            } else {
                return scoreCompare;
            }
//			if( lhs.score == rhs.getScore()) {
//				return -1*lhs.docID.compareTo(rhs.getDocID());
//			} else if ( lhs.score > rhs.getScore()) {
//				return -1;
//			} else {
//				return 1;
//			}
//			int scoreCompare = -1 * Double.compare(lhs.score, rhs.score);
//			int scoreCompare = -1 * lhs.getScore().compareTo(rhs.getScore());
//			
////					lhs.score.CompareTo(rhs.score);
//			
//			if( scoreCompare == 0) {
//				return -1 * lhs.docID.compareTo(rhs.docID);
//			} else {
//				return scoreCompare;
//			}
			
		}
		
	}
	
	private HashMap<String, String> tupleKeys = new HashMap<>();
	private HashMap<String, ArrayList<Result>> query2results = new HashMap<>();
	private HashMap<String, Boolean> query2isSorted = new HashMap<>();
	
	public Results() {
		tupleKeys = new HashMap<>();
		query2results = new HashMap<>();
		query2isSorted = new HashMap<>();
	}
	public void AddResults(String queryID, String docID, double score, int rank) throws Exception {
//		System.out.println("===");
		String key = this.GenerateTupleKey(queryID, docID);
		if( this.tupleKeys.containsKey(key)) {
			throw new Exception("Cannot have duplicate queryID and docID data points");
		}
		this.tupleKeys.put(key, null);
		
		ArrayList<Result> results = null;
		if( this.query2results.containsKey(queryID)) {
			results = (ArrayList) this.query2results.get(queryID);
		} else {
			results = new ArrayList<>();
			this.query2results.put(queryID, results);
			this.query2isSorted.put(queryID, false);
			
		}
//		System.out.println("score: " + score);
		Result result = new Result(docID, score, rank);
		results.add(result);
		
//		added since these hashmaps don't get updated
		this.query2results.put(queryID, results);
		this.query2isSorted.put(queryID, false);
		
	}
	public String GenerateTupleKey(String queryID, String docID) {
		return queryID + "-" + docID;
	}
	
	public ArrayList<Result> QueryResults(String queryID) throws Exception{
		if(!this.query2results.containsKey(queryID)) {
			throw new Exception("No such queryID in results");
		}
		ArrayList<Result> results = (ArrayList)this.query2results.get(queryID);
		if (!  this.query2isSorted.get(queryID)) {
			Collections.sort(results);
			
//			for( Result i: results) {
//				System.out.println(i.getRank());
//			}
//			
			
			
//			results.sort(null);
			this.query2isSorted.put(queryID, true);
//			this.query2isSorted.get(queryID) = true;
		}
		return results;
		
		
	}
//	Might not work
	public ArrayList<String> QueryIDs() {
		ArrayList<String> keySet = new ArrayList<>();
		for( String i : this.query2results.keySet()) {
			keySet.add(i);
		}
		
		return keySet;
	}
	
	public Boolean queryIDExists(String queryID) {
		return this.query2results.containsKey(queryID);
	}
	
	
	
	

}
