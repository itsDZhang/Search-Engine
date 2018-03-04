package Evaluation;
import java.io.*; 
import java.util.*;

import Evaluation.Results.Result;

public class Evaluate {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String qrels = "C:/Users/Rui/eclipse-workspace/541/hw3Files/LA-only.trec8-401.450.minus416-423-437-444-447.txt";
		String topics = "C:/Users/Rui/eclipse-workspace/541/hw3Files/topics.401-450.txt";
		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/results-files/student2.results";
		String metaDataPath = "C:/Users/Rui/eclipse-workspace/541/hw3Files/id2MetaData.txt";
		
		calcMeanAveragePrecision(resultFile, qrels);
		calcPrecisionAt10(resultFile, qrels);
		
		calcNdcg10(resultFile, qrels);
		calcNdcg1000(resultFile, qrels);
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
//			System.out.println( TBG);
			TBGList.add(TBG);
//			System.out.println(TBG);
			
		}
		
		double tmp = 0;
		for(int i =0; i<TBGList.size();i++) {
			tmp += TBGList.get(i);
		}
		System.out.println(tmp/TBGList.size());

	}
	public static double calcTimeofK(
									HashMap<String, 
									Integer> docno2Count, 
									int currentRank,
									ArrayList<Result> queryResult,
									ArrayList<String> reldocnos) {
		
		double TofK =0;
		int tmpRank = 0;
		
		for(Result result: queryResult) {
			tmpRank = result.getRank();
			if(tmpRank >= currentRank) {
				break;
			}
			String docno = result.getDocID();
//			System.out.println(docno);
			double docLength = docno2Count.get(docno);
			if(reldocnos.contains(docno)) {
				TofK += (4.4 + ((0.018*(docLength) + 7.8) * 0.64));
			} else {
				TofK += (4.4 + ((0.018*(docLength) + 7.8) * 0.39));
			}
		}
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
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					dcg += (1/(Math.log(result.getRank() + 1)/Math.log(2)));
					
				}
			}
			ndcg = dcg/idcg;
			ndcgList.add(ndcg);
//			System.out.println(ndcg);
		}
		double tmp = 0;
		for(int i =0; i<ndcgList.size();i++) {
			tmp += ndcgList.get(i);
		}
		System.out.println(tmp/ndcgList.size());
		
//		System.out.println(ndcgList.toString());

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
			int stop = 10;
			if(stop>queryResult.size()) {
				stop = queryResult.size();
			}
			for(int breaker=1; breaker<=stop; breaker++ ) {
				Result result = queryResult.get(breaker-1);
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					dcg += (1/(Math.log(result.getRank() + 1)/Math.log(2)));
				}
			}
			ndcg = dcg/idcg;
			ndcgList.add(ndcg);
//			System.out.println(ndcg);
		}
		double tmp = 0;
		for(int i =0; i<ndcgList.size();i++) {
			tmp += ndcgList.get(i);
		}
		System.out.println(tmp/ndcgList.size());
//		System.out.println(ndcgList.toString());
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
//			System.out.println(topic);
			int stop = 10;
			if(stop>queryResult.size()) {
				stop = queryResult.size();
			}
			for(int breaker=0; breaker<stop; breaker++ ) {
//				System.out.println("QueryResult: " + );
				Result result = queryResult.get(breaker);
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					counter +=1;
				}
			}
			precision = counter/rank;
			avgScores.add(precision);
//			System.out.println(precision);
		}
		double tmp = 0;
		for(int i =0; i<avgScores.size();i++) {
			tmp += avgScores.get(i);
		}
		System.out.println(tmp/avgScores.size());
		
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
				if(reldocnos.contains(docno)) {
					counter +=1;
					rank = (double) result.getRank();
					precision = counter/rank;
					sum += precision;
				}
			}
			avgScores.add(sum/reldocnos.size());
//			System.out.println(sum/reldocnos.size());
		}
		double tmp = 0;
		for(int i =0; i<avgScores.size();i++) {
			tmp += avgScores.get(i);
		}
		System.out.println(tmp/avgScores.size());
//		System.out.println(avgScores.toString());
	}

}
