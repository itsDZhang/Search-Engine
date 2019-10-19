package Evaluation;

import java.io.*;
import java.util.Scanner;

public class ResultsFile {
	public Results results = new Results();
	public String runID;

	public ResultsFile(String fullpath) throws Exception {

		Scanner sr = new Scanner(new FileReader(fullpath));
		boolean firstLine = true;
		String line = "";

		while(sr.hasNextLine()) {
			line = sr.nextLine();
			String[] fields = line.split("\\s+");
			if(fields.length != 6) {
				throw new Exception("input should have 6 columns");
			}
			String queryID = fields[0];
			String docID = fields[2];
			int rank = 0;
			try {
				rank = Integer.parseInt(fields[3]);
			} catch (Exception e) {
				System.out.println("An error occured");
			}

			double score = Double.parseDouble(fields[4]);
			results.AddResults(queryID, docID, score, rank);
			if( firstLine) {
				this.runID = fields[5];
				firstLine = false;
			}
			else if( !this.runID.equals(fields[5])) {

				throw new Exception("mismatching runID in file");
			}

		}
		sr.close();

	}
}
