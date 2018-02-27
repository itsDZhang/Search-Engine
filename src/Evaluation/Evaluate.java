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
		calcPrecisionAt10(resultFile, qrels);
		
		calcNdcg10(resultFile, qrels);
		calcNdcg1000(resultFile, qrels);
		
		

	}
	public static void calcNdcg1000(String resultFile, String qrels) throws Exception {
		ResultsFile resultsRaw = new ResultsFile(resultFile);
		qRels qrelsList = new qRels(qrels);
		
		Results results = resultsRaw.results;
		ArrayList<Result> queryResult = new ArrayList<>();
		ArrayList<String> topicIds = results.QueryIDs();
		HashMap<String, ArrayList<Result>> query2Result = new HashMap<>();
		HashMap<String, ArrayList<String>> topic2RelDocnos = new HashMap<>();
		RelevanceJudgements relDocs = qrelsList.judgements;
		ArrayList<String> reldocnos = new ArrayList<>();
		ArrayList<Double> ndcgList = new ArrayList<>();
		
		for(String topicNum : topicIds) {
			queryResult = results.QueryResults(topicNum);
			query2Result.put(topicNum, queryResult);
			topic2RelDocnos.put(topicNum, relDocs.getRelDocnos(topicNum));
		}
		Collections.sort(topicIds);
		double ndcg = 0;
		double idcg = 0;
		for( int i =1; i <=1000; i++) {
			idcg += (1/(Math.log(i+1)/Math.log(2)));
		}
		
		for(String topic : topicIds) {
			int breaker = 0;
			double dcg = 0;
			queryResult = query2Result.get(topic);
			reldocnos = topic2RelDocnos.get(topic);
			
			String docno = "";
			int stop = 1000;
			if( stop != queryResult.size()) {
				stop = queryResult.size();
			}
			for(int i=1; i<=stop; i++ ) {
				Result result = queryResult.get(i-1);
//				System.out.println(result.getRank());
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					System.out.println(result.getRank());
					dcg += (1/(Math.log(result.getRank() + 1)/Math.log(2)));
					
				}
			}
			

			
			ndcg = dcg/idcg;
			ndcgList.add(ndcg);
			System.out.println("Topic: "+ topic + " dcg: " + dcg +" idcg: " + idcg);
			System.out.println("NDCG: "+ ndcg);
			
		}
		
		System.out.println(ndcgList.toString());
//		for(Result result: queryResult) {
//		breaker +=1;
//		docno = result.getDocID();
//		if(reldocnos.contains(docno)) {
//			System.out.println(result.getRank());
//			dcg += (1/(Math.log(result.getRank() + 1)/Math.log(2)));
//			
//		}
//		if(breaker == 1000) {
//			break;
//		}
//		
//		
//	}
	}
	
	public static void calcNdcg10(String resultFile, String qrels) throws Exception {
		ResultsFile resultsRaw = new ResultsFile(resultFile);
		qRels qrelsList = new qRels(qrels);
		
		Results results = resultsRaw.results;
		ArrayList<Result> queryResult = new ArrayList<>();
		ArrayList<String> topicIds = results.QueryIDs();
		HashMap<String, ArrayList<Result>> query2Result = new HashMap<>();
		HashMap<String, ArrayList<String>> topic2RelDocnos = new HashMap<>();
		RelevanceJudgements relDocs = qrelsList.judgements;
		ArrayList<String> reldocnos = new ArrayList<>();
		ArrayList<Double> ndcgList = new ArrayList<>();
		for(String topicNum : topicIds) {
			queryResult = results.QueryResults(topicNum);
			query2Result.put(topicNum, queryResult);
			topic2RelDocnos.put(topicNum, relDocs.getRelDocnos(topicNum));
		}
		Collections.sort(topicIds);
		double ndcg = 0;
		double idcg = 0;
		for( int i =1; i <=10; i++) {
			idcg += (1/(Math.log(i+1)/Math.log(2)));
		}
		
		for(String topic : topicIds) {
			double dcg = 0;
			queryResult = query2Result.get(topic);
			reldocnos = topic2RelDocnos.get(topic);
			
			String docno = "";
			
			for(int breaker=1; breaker<=10; breaker++ ) {
				Result result = queryResult.get(breaker-1);
//				System.out.println(result.getRank());
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
//					System.out.println(result.getRank());
					dcg += (1/(Math.log(result.getRank() + 1)/Math.log(2)));
					
				}
			}
			ndcg = dcg/idcg;
			ndcgList.add(ndcg);
//			System.out.println("Topic: "+ topic + " dcg: " + dcg +" idcg: " + idcg);
//			System.out.println("NDCG: "+ ndcg);
			
			
			
		}
		
		System.out.println(ndcgList.toString());
		
	}
	
	public static void calcPrecisionAt10(String resultFile, String qrels) throws Exception {
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
		for(String topicNum : topicIds) {
			queryResult = results.QueryResults(topicNum);
			query2Result.put(topicNum, queryResult);
			topic2RelDocnos.put(topicNum, relDocs.getRelDocnos(topicNum));
		}
		Collections.sort(topicIds);
		
		for(String topic : topicIds) {
			queryResult = query2Result.get(topic);
			reldocnos = topic2RelDocnos.get(topic);
			double precision = 0.0;
			double counter = 0.0;
			double rank = 10.0;
			String docno = "";
			
			for(int breaker=0; breaker<10; breaker++ ) {
				Result result = queryResult.get(breaker);
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					counter +=1;
				}
			}
			precision = counter/rank;
			avgScores.add(precision);
		}
		System.out.println(avgScores.toString());
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
			double precision = 0.0;
			double counter = 0.0;
			double rank = 0.0;
			double sum = 0.0;
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
//			System.out.println( "Topic #: " + topic +
//					" |count size: " + counter 
//					+ " |reldocnos size: " + reldocnos.size() +
//					" |queryResult size: " + queryResult.size());
			avgScores.add(sum/reldocnos.size());
		}
		System.out.println(avgScores.toString());
	}

}
