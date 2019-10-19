package Evaluation;
import java.io.*;
import java.util.*;

import Evaluation.Results.Result;

public class Evaluate {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String qrels = "C:/Users/Rui/eclipse-workspace/541/hw3Files/LA-only.trec8-401.450.minus416-423-437-444-447.txt";
		String topics = "C:/Users/Rui/eclipse-workspace/541/hw3Files/topics.401-450.txt";
//		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/results-files/student2.results";
//		String resultFile = "C:\\Users\\Rui\\eclipse-workspace\\541\\hw4Files/r255zhan-hw4-bm25-baseline.txt";
		String resultFile = "C:\\Users\\Rui\\eclipse-workspace\\541\\hw4Files/r255zhan-hw4-bm25-stemmed.txt";
//		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/results-files/r255zhan-hw2-results.results";
		String metaDataPath = "C:/Users/Rui/eclipse-workspace/541/hw3Files/id2MetaData.txt";
		try {
			File file = new File("student2.results");
	             boolean fvar = file.createNewFile();
		     if (fvar){
//		          System.out.println("File has been created successfully");
		     }
		     else{
//		          System.out.println("File already present at the specified location");
		     }
	    	} catch (IOException e) {
	    		System.out.println("Exception Occurred:");
		        e.printStackTrace();
		  }
		ArrayList<Double> ap10 = calcMeanAveragePrecision(resultFile, qrels);
		ArrayList<Double> precision10 = calcPrecisionAt10(resultFile, qrels);
		ArrayList<Double> ndcgList10 = calcNdcg10(resultFile, qrels);
		ArrayList<Double> ndcgList1000 = calcNdcg1000(resultFile, qrels);
		ArrayList<Double> TBGList = calcTBG(resultFile, qrels, metaDataPath);
	}
	public static ArrayList<Double> calcTBG(String resultFile, String qrels, String metaDataPath) throws Exception {
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
			for(Result result : queryResult) {
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
			TBGList.add(TBG);
//
			try
			{
				String filename= "student2.results";
//			    String filename= "r255zhan-hw2-results.results";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write("TBG \t\t" + topic + "\t\t" + TBG+ "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		}
		double tmp = 0;
		for(int i =0; i<TBGList.size();i++) {
			tmp += TBGList.get(i);
		}
		System.out.println(tmp/TBGList.size());
		return TBGList;

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
	public static ArrayList<Double> calcNdcg1000(String resultFile, String qrels) throws Exception {
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
			try
			{
				String filename= "student2.results";
//			    String filename= "r255zhan-hw2-results.results";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write("ndcg@1000 \t\t" + topic + "\t\t" + ndcg+ "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		}
		double tmp = 0;
		for(int i =0; i<ndcgList.size();i++) {
			tmp += ndcgList.get(i);
		}
		System.out.println(tmp/ndcgList.size());
		return ndcgList;
	}

	public static ArrayList<Double> calcNdcg10(String resultFile, String qrels) throws Exception {
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


			try
			{
			    String filename= "student2.results";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write("ndcg@10 \t\t" + topic + "\t\t" + ndcg+ "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		}
		double tmp = 0;
		for(int i =0; i<ndcgList.size();i++) {
			tmp += ndcgList.get(i);
		}
		System.out.println(tmp/ndcgList.size());
		return ndcgList;
	}

	public static ArrayList<Double> calcPrecisionAt10(String resultFile, String qrels) throws Exception {
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
			int stop = 10;
			if(stop>queryResult.size()) {
				stop = queryResult.size();
				rank = queryResult.size();
			}
			for(int breaker=0; breaker<stop; breaker++ ) {
				Result result = queryResult.get(breaker);
				docno = result.getDocID();
				if(reldocnos.contains(docno)) {
					counter +=1;
				}
			}
			precision = counter/rank;
			avgScores.add(precision);
			try
			{
			    String filename= "student2.results";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write("Precision@10 \t\t" + topic + "\t\t" + precision + "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		}
		double tmp = 0;
		for(int i =0; i<avgScores.size();i++) {
			tmp += avgScores.get(i);
		}
		System.out.println(tmp/avgScores.size());
		return avgScores;

	}

	public static ArrayList<Double> calcMeanAveragePrecision(String resultFile, String qrels) throws Exception {
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
			try
			{
//				String filename= "r255zhan-hw2-results.results";
			    String filename= "student2.results";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write("AP \t\t" + topic + "\t\t" + (sum/reldocnos.size()) + "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
		}
		double tmp = 0;
		for(int i =0; i<avgScores.size();i++) {
			tmp += avgScores.get(i);
		}
		System.out.println(tmp/avgScores.size());
		return avgScores;
	}

}
