package Evaluation;

import java.io.*;
import java.util.*;

public class qRels {

	public RelevanceJudgements judgements = new RelevanceJudgements();
	public qRels(String fullpath) throws Exception {
		Scanner sr = new Scanner(new FileReader(fullpath));
		String line="";
		while(sr.hasNextLine()) {
			line = sr.nextLine();
//			System.out.println(line);
			String[] fields = line.split("\\s+");
			
			if(fields.length !=4) {
				throw new Exception(" Input should have 4 columns");
			}
			String queryID = fields[0];
			String docID = fields[2];
			int relevant = Integer.parseInt(fields[3]);
//			System.out.println("relevant Number:" + relevant);
			judgements.addJudgements(queryID, docID, relevant);
			
			
			
		}
		sr.close();
		
		
		
		
		
	}

}
