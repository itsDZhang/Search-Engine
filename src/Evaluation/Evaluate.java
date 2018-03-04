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
		String metaDataPath = "C:/Users/Rui/eclipse-workspace/541/hw3Files/id2MetaData.txt";
//		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/correct-scores-for-student1-file/student1-measures.txt";
//		Student 6 is bad maybe 10? may 12?
//		System.out.println(Math.log(2));
		System.out.println(Math.exp(-1*20*(Math.log(2)/224)));
		calcMeanAveragePrecision(resultFile, qrels);
		calcPrecisionAt10(resultFile, qrels);
		
		calcNdcg10(resultFile, qrels);
		calcNdcg1000(resultFile, qrels);
		
//		System.out.println(Math.log(499));
		calcTBG(resultFile, qrels, metaDataPath);
		

	}
	public static void calcTBG(String resultFile, String qrels, String metaDataPath) throws Exception {
		ResultsFile resultsRaw = new ResultsFile(resultFile);
		qRels qrelsList = new qRels(qrels);
		HashMap<String, Integer> docno2Count = docno2docCount(metaDataPath);
		Results results = resultsRaw.results;
		ArrayList<Result> queryResult = new ArrayList<>();
		ArrayList<String> topicIds = results.QueryIDs();
		HashMap<String, ArrayList<Result>> query2Result = new HashMap<>();
		HashMap<String, ArrayList<String>> topic2RelDocnos = new HashMap<>();
		RelevanceJudgements relDocs = qrelsList.judgements;
		ArrayList<Double> TBGList = new ArrayList<>();
		ArrayList<String> reldocnos = new ArrayList<>();
		for(String topicNum : topicIds) {
			queryResult = results.QueryResults(topicNum);
			query2Result.put(topicNum, queryResult);
			topic2RelDocnos.put(topicNum, relDocs.getRelDocnos(topicNum));
		}
		double gainK = 0.64*0.77;
		
		
		Collections.sort(topicIds);
		for(String topic : topicIds) {
			double TBG = 0;
			double TSum = 0;
			double DofTk =0;
			queryResult = query2Result.get(topic);
			reldocnos = topic2RelDocnos.get(topic);
//			int delete = 0;
			for(Result result : queryResult) {
//				System.out.println(delete++);
				String docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					int rank = result.getRank();
					TSum = calcTimeofK(docno2Count, rank,queryResult, reldocnos);
					if(TSum > 1800) {
						continue;
					}
					DofTk = calcDofK(TSum);
					TBG += gainK * DofTk;
				}
			}
			System.out.println("Topic ID: " + topic+ " TBG: " + TBG);
			TBGList.add(TBG);
//			System.out.println(TBG);
			
		}
//		System.out.println(TBGList.toString());
		
	}
	public static double calcTimeofK(
									HashMap<String, 
									Integer> docno2Count, 
									int currentRank,
									ArrayList<Result> queryResult,
									ArrayList<String> reldocnos) {
		
		double TofK =0;
		int tmpRank = 0;
		
//		int delete = 0;
		for(Result result: queryResult) {
			tmpRank = result.getRank();
			if(tmpRank >= currentRank) {
//				System.out.println(delete);
				break;
			}
			
//			System.out.println("tmp Rank: " + tmpRank + " currentRank " + currentRank);
//			delete++;
			
			String docno = result.getDocID();
//			System.out.println(docno2Count.get("LA121589-0087"));
//			System.out.println(docno2Count.get("LA062090-0079"));
//			System.out.println(docno2Count.get("LA070190-0033"));
//			System.out.println(docno2Count.get("LA030490-0051"));
//			
			
//			System.out.println(tmpRank);
			double docLength = docno2Count.get(docno);
//			TofK += (4.4 + ((0.018*(docLength) + 7.8) * 0.64));
			if(reldocnos.contains(docno)) {
//				tmpRank = result.getRank();
				TofK += (4.4 + ((0.018*(docLength) + 7.8) * 0.64));
//				if(tmpRank >= currentRank) {
//					break;
//				}
//				TofK += Ts + ((Aslope*docno2Count.get(docno) + bConst) * probClickGivenRel);
				
			} else {
				TofK += (4.4 + ((0.018*(docLength) + 7.8) * 0.39));
			}
		}
//		System.out.println("T of K: " + TofK);
		return TofK;
	}
	public static double calcDofK(double TSum) {
		double DofK = 0;
		
		DofK = Math.exp(-1*TSum*(Math.log(2)/224));
		
		return DofK;
	}
	public static HashMap<String, Integer> docno2docCount(String metaDataPath) throws FileNotFoundException {
		HashMap<String, Integer> docno2Count = new HashMap<>();
		Scanner docno2Counttxt = new Scanner(new FileReader(metaDataPath ));
		while(docno2Counttxt.hasNextLine()) {
			String nextLine = docno2Counttxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
			String[] data = nextLineArr[1].split("\\{}");
			String docno = data[1];
			int docCount = Integer.parseInt(data[4]);
			docno2Count.put(docno, docCount);
		}
		docno2Counttxt.close();
		
//		for (String i: docno2Count.keySet()) {
//			System.out.println(i + "  " + docno2Count.get(i));
//		}
		
		return docno2Count;
		
		
		
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
		
		for(String topic : topicIds) {
			int breaker = 0;
			double dcg = 0;
			double idcg = 0;
			queryResult = query2Result.get(topic);
			reldocnos = topic2RelDocnos.get(topic);
			for( int i =1; i <=reldocnos.size(); i++) {
				idcg += (1/(Math.log(i+1)/Math.log(2)));
			}
			String docno = "";
			for(int i=1; i<=queryResult.size(); i++ ) {
				Result result = queryResult.get(i-1);
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
		
		for(String topic : topicIds) {
			double dcg = 0;
			double idcg = 0;
			int checkSize = 10;
			queryResult = query2Result.get(topic);
			reldocnos = topic2RelDocnos.get(topic);
			if(reldocnos.size() <= checkSize) {
				checkSize = reldocnos.size();
			}
			
			for( int i =1; i <=checkSize; i++) {
				idcg += (1/(Math.log(i+1)/Math.log(2)));
			}
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
