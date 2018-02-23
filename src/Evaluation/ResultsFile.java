package Evaluation;

import java.io.*;
import java.util.Scanner;

public class ResultsFile {
	public Results results = new Results();
	public String runID;
	
	public ResultsFile(String fullpath) throws Exception {
//		char[] whitespace = {'\t',' '};
		Scanner sr = new Scanner(new FileReader(fullpath));
		
		boolean firstLine = true;
		String line = "";
		
		while((line = sr.nextLine()) != null) {
			String[] fields = line.split("\\s+");
			if(fields.length != 6) {
				throw new Exception("input should have 6 columns");
			}
			String queryID = fields[0];
			String docID = fields[2];
			int rank = Integer.parseInt(fields[3]);
			double score = Double.parseDouble(fields[4]);
			results.AddResults(queryID, docID, score, rank);
			if( firstLine) {
				this.runID = fields[5];
				firstLine = false;
			}
			else if( this.runID != fields[5]) {
				throw new Exception("mismatching runID in file");
				
			}
			
		}
		sr.close();
		
	}
}
