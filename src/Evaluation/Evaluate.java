package Evaluation;
import java.io.*; 
import java.util.*;

import Evaluation.Results.Result;

public class Evaluate {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String qrels = "C:/Users/Rui/eclipse-workspace/541/hw3Files/LA-only.trec8-401.450.minus416-423-437-444-447.txt";
		String topics = "C:/Users/Rui/eclipse-workspace/541/hw3Files/topics.401-450.txt";
		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/results-files/student1.results";
//		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/correct-scores-for-student1-file/student1-measures.txt";
//		Student 6 is bad maybe 10? may 12?
		
		calcMeanAveragePrecision(resultFile, qrels);
		
		

	}
	
	public static void calcMeanAveragePrecision(String resultFile, String qrels) throws Exception {
		ResultsFile resultsRaw = new ResultsFile(resultFile);
		Results results = resultsRaw.results;
		HashMap<String, ArrayList<Result>> query2Result = new HashMap<>();
		ArrayList<Result> queryResult = new ArrayList<>();
		ArrayList<String> topicIds = results.QueryIDs();
		HashMap<String, ArrayList<String>> topic2RelDocnos = new HashMap<>();
		ArrayList<Double> avgScores = new ArrayList<>();
		qRels qrelsList = new qRels(qrels);
		RelevanceJudgements relDocs = qrelsList.judgements;
		ArrayList<String> reldocnos = new ArrayList<>();
		double avg4topicNum = 0;
		double numerator = 0;
		double denominator = 0;
		
		for(String topicNum : topicIds) {
			queryResult = results.QueryResults(topicNum);
			query2Result.put(topicNum, queryResult);
			topic2RelDocnos.put(topicNum, relDocs.getRelDocnos(topicNum));
		}
		
		
		Collections.sort(topicIds);
//		https://people.cs.umass.edu/~jpjiang/cs646/03_eval_basics.pdf
		for(String topic : topicIds) {
//			Array of each topic # of id, topicnum -- Correct rel ones
			queryResult = query2Result.get(topic);
//			Array of rel doc nos -- Testing this
			reldocnos = topic2RelDocnos.get(topic);
			Double precision = 0.0;
			Double counter = 0.0;
			Double rank = 0.0;
			Double sum = 0.0;
			String docno = "";
			
			for( Result result : queryResult) {
				
				docno = result.getDocID();
//				System.out.println(docno);
				if(reldocnos.contains(docno)) {
					counter +=1;
					rank = (double) result.getRank();
					precision = counter/rank;
					sum += precision;
				}
			}
			System.out.println( "Topic #: " + topic +
					" |count size: " + counter 
					+ " |reldocnos size: " + reldocnos.size() +
					" |queryResult size: " + queryResult.size());
//			System.out.println("Sum: " + sum);
			avgScores.add(sum/reldocnos.size());
//			avgScores.add(sum/counter);
			
			
			
			
			
			
			
//			avg4topicNum = 0;
			
//			denominator = topic2RelDocnos.get(topic).size();
//			numerator = queryResult.size();
//			avg4topicNum = numerator/denominator;
//			
//			avg4topicNum = queryResult.size();
//			
//			avgScores.add(avg4topicNum);
		}
		
		System.out.println(avgScores.toString());
		
//		==============================================================================================
		
	}

}
