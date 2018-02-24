package Evaluation;
import java.io.*;
import java.util.*;
public class Evaluate {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String qrels = "C:/Users/Rui/eclipse-workspace/541/hw3Files/LA-only.trec8-401.450.minus416-423-437-444-447.txt";
		String topics = "C:/Users/Rui/eclipse-workspace/541/hw3Files/topics.401-450.txt";
		String resultFile = "C:/Users/Rui/eclipse-workspace/541/hw3Files/results-files/student6.results";
//		Student 6 is bad
		ResultsFile results = new ResultsFile(resultFile);
		
		qRels qrelsList = new qRels(qrels);

	}

}
