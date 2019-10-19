import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BM25Test {

public static void main(String[] argv) throws IOException, ClassNotFoundException{

		String localPath = "C:/Users/Rui/eclipse-workspace/541/hw4Files";
//		String localPathQueries = args[0]+"/" +args[1];
////		the path of where you would store your output file
		String localPathOutputFileToStore = localPath + "/results";
//		Reading inverted Index
		InputStream fileStream = new FileInputStream(new File(localPath + "/topics.401-450.txt"));
		Reader decoder = new InputStreamReader(fileStream);
		BufferedReader reader = new BufferedReader(decoder);
		Scanner queries = new Scanner(reader);
		HashMap<Integer, Integer> docId2Count = docID2docCount( localPath + "/id2MetaData.txt");

		try {
//			File file = new File( "r255zhan-hw4-cosine.txt");
			File file = new File( "r255zhan-hw4-bm25-baseline.txt");
//			File file = new File( "r255zhan-hw4-bm25-stemmed.txt");
	             boolean fvar = file.createNewFile();
		     if (fvar){
		          System.out.println("File has been created successfully");
		     }
		     else{
		          System.out.println("File already present at the specified location");
		     }
	    	} catch (IOException e) {
	    		System.out.println("Exception Occurred:");
		        e.printStackTrace();
		  }
//		Reading term2Id Lexicon
		FileInputStream fileRead = new FileInputStream(new File(localPath + "/term2IdLexicon.txt"));
//		FileInputStream fileRead = new FileInputStream(new File(localPath + "/term2IdLexiconStemmed.txt"));
		ObjectInputStream toRead = new ObjectInputStream(fileRead);
		@SuppressWarnings("unchecked")
		HashMap <String, Integer> term2IdLexicon =  (HashMap<String, Integer>) toRead.readObject();

		System.out.println("Starting to Read Inverted Index. Time: " + LocalDateTime.now());
		fileRead = new FileInputStream(new File(localPath + "/invertedIndex.txt"));
//		fileRead = new FileInputStream(new File(localPath + "/invertedIndexStemmed.txt"));
		toRead = new ObjectInputStream(fileRead);
		@SuppressWarnings("unchecked")
		HashMap<Integer, ArrayList<DocIDCountPair>>  invertedIndexRead  = (HashMap<Integer, ArrayList<DocIDCountPair>>) toRead.readObject();
		System.out.println("Read Inverted Index. Time: " + LocalDateTime.now());

		System.out.println("Read Everything");
//		Reading the metaData
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		id2MetaData = generateid2MetaDataHash(localPath +"/id2MetaData.txt");
//		System.out.println("Length of Inverted Index: " + invertedIndexRead.keySet().size() +
//				"Length of Lexicon: " + term2IdLexicon.keySet().size());
//

		while(queries.hasNextLine()) {
			String line = queries.nextLine();
			String topic = line.substring(0, 3);
			line = extractTopic(line);
			BM25(topic, line, term2IdLexicon, invertedIndexRead, docId2Count, id2MetaData);
//			cosineSimilarity(topic, line, term2IdLexicon, invertedIndexRead, docId2Count, id2MetaData);
		}
    }
	public static String extractTopic(String line) {
		return line.substring(3, line.length());
	}
	public static void BM25(String topic,
			String query,
			HashMap <String, Integer> term2Id,
			HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex,
			HashMap<Integer, Integer> docId2Count,
			HashMap<Integer, metaData> id2MetaData) {
		double k1 = 1.2;
		double k2 = 7;
		double k;
		double tf4Doc, tf4Query, logVal, numofRelDocs, id, qfi, fi = 0;
		double sumOfIterations =0;
		double numOfDocs = 131896;
		int docId = 0;
		ArrayList<String> queryTerms = tokenize(query);
		HashMap<String, Integer> queryFreq = new HashMap<>();
		for(String term: queryTerms) {
			if(queryFreq.containsKey(term)) {
				queryFreq.put(term, queryFreq.get(term) + 1);
			} else {
				queryFreq.put(term,1);
			}

		}
		Map<Integer, Double> accumulator = new HashMap<>();


		for( String term : queryTerms) {
			int termId = term2Id.get(term);
			ArrayList<DocIDCountPair> postings = invertedIndex.get(termId);
			System.out.println("Term: " + term + " query: " + queryTerms.toString());
			for(DocIDCountPair post: postings) {
				docId = post.getDocID();
				numofRelDocs = postings.size();
				logVal = Math.log((numOfDocs - numofRelDocs + 0.5)/(numofRelDocs+0.5));
				qfi = queryFreq.get(term);
				tf4Query = (((k2+1)*qfi)/(k2+qfi));

				k = calcK(k1,docId, invertedIndex, term2Id,term, docId2Count);
				fi = post.getCount();
				tf4Doc = ((k1 + 1)*fi)/(k+fi);
				sumOfIterations = tf4Doc * tf4Query * logVal;

				if(accumulator.containsKey(docId)) {
					accumulator.put(docId, accumulator.get(docId) + sumOfIterations);
				} else {
					accumulator.put(docId,sumOfIterations);
				}
			}
		}
		Map sortedMap = sortByValue(accumulator);
		int counter = 1;
		for( Object i: sortedMap.keySet()) {
			if(counter == 1001) break;

			String docno = id2MetaData.get(i).getDocNo();

			try
			{
				String filename= "r255zhan-hw4-bm25-baseline.txt";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write(topic + " Q0 "+ docno + " " + counter + " " + accumulator.get(i) + " "+ "r255zhan" + "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
			counter ++;
		}
	}
//	Credits: https://www.mkyong.com/java/how-to-sort-a-map-in-java/
	private static Map<Integer, Double> sortByValue(Map<Integer, Double> unsortMap) {
        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
	public static double calcK(double k1,
			int docId,
			HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex,
			HashMap <String, Integer> term2Id,
			String term,
			HashMap<Integer, Integer> docId2Count) {
		double k =0;
		double b = 0.75;
		int dl=0;
		double avgdl = 534.47;
		int termId = term2Id.get(term);
		dl = docId2Count.get(docId);
		ArrayList<DocIDCountPair> postings = new ArrayList<>();
		postings = invertedIndex.get(termId);
		k = k1*((1-b)+ b*(dl/avgdl));

		return k;
	}
	public static HashMap<Integer, Integer> docID2docCount(String metaDataPath) throws FileNotFoundException {
		HashMap<Integer, Integer> docID2Count = new HashMap<>();
		Scanner docno2Counttxt = new Scanner(new FileReader(metaDataPath ));
		while(docno2Counttxt.hasNextLine()) {
			String nextLine = docno2Counttxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
			String[] data = nextLineArr[1].split("\\{}");
			int docCount = Integer.parseInt(data[4]);
			int docId = Integer.parseInt(data[0]);
			docID2Count.put(docId, docCount);
		}
		docno2Counttxt.close();
		return docID2Count;
	}
	//To tokenize
	public static ArrayList<String> tokenize(String text) {
		text = text.toLowerCase();
		ArrayList<String> tokens = new ArrayList<String>();
		int start = 0;
		int i =0;

		for (i=0;i<text.length();++i) {
			String c = text.substring(i, i+1);
			if(  checkForCharAndDigits(c) ) {
				if( start != i ) {
//					========================= Porter Stemming ========================
//					String token = PorterStemmer.stem(text.substring(start, i));
//					========================================
					String token = text.substring(start, i);
					tokens.add(token);
				}
				start = i+1;
			}
		}
		if(start!=i) {
//			========================= Porter Stemming ========================
//			tokens.add(PorterStemmer.stem(text.substring(start, i)));
//			========================================
			tokens.add(text.substring(start, i));
		}
		return tokens;
	}
	public static boolean checkForCharAndDigits(String str) {
        Matcher m = Pattern.compile("[^a-zA-Z0-9]").matcher(str);
        if (m.find()) return true;
        else          return false;
    }
//	This method grabs the id to metadata txt file, reads it, and populates the hashmap
	public static HashMap<Integer, metaData> generateid2MetaDataHash(String localPath) throws FileNotFoundException {
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		Scanner id2MetaDatatxt = new Scanner(new FileReader(localPath));
		while(id2MetaDatatxt.hasNextLine()) {
			String nextLine = id2MetaDatatxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
			int key = Integer.parseInt(nextLineArr[0]);
			String[] data = nextLineArr[1].split("\\{}");
			metaData meta = new metaData(Integer.parseInt(data[0]), data[1], data[2],data[3], data[4]);
			id2MetaData.put(key, meta);
		}
		return id2MetaData;
	}
	public static void cosineSimilarity(String topic,
			String query,
			HashMap <String, Integer> term2Id,
			HashMap<Integer, ArrayList<DocIDCountPair>> invertedIndex,
			HashMap<Integer, Integer> docId2Count,
			HashMap<Integer, metaData> id2MetaData) {

		Map<Integer, Double> accumulator = new HashMap<>();
		ArrayList<String> queryTerms = tokenize(query);
		HashMap<String, Integer> queryFreq = new HashMap<>();
		int n = 247034;
		double dt = 0;
		double qt = 0.3333333333333;
		int docId = 0;
		int nk;
		double cosineVal = 0;
		for( String term : queryTerms) {
			int termId = term2Id.get(term);
			ArrayList<DocIDCountPair> postings = invertedIndex.get(termId);
			System.out.println("Term: " + term + " query: " + queryTerms.toString());
			for(DocIDCountPair post: postings) {
				double dtsquaredSum = 0;
				double qtsquaredSum = 0;
				double numerator = 0;
				docId = post.getDocID();
				nk = postings.size();
				dt = calcdt(term2Id, term, post, nk);

				for(int i = 1; i <=n; i++) {
					numerator += dt*qt;
					dtsquaredSum += dt*dt;
					qtsquaredSum += qt*qt;
				}
				cosineVal = numerator/(Math.sqrt(dtsquaredSum*qtsquaredSum));
				if(accumulator.containsKey(docId)) {
					accumulator.put(docId, accumulator.get(docId) + cosineVal);
				} else {
					accumulator.put(docId,cosineVal);
				}
			}


		}
		Map sortedMap = sortByValue(accumulator);
		int counter = 1;
		for( Object i: sortedMap.keySet()) {
			if(counter == 1001) break;
			String docno = id2MetaData.get(i).getDocNo();
			try
			{
//				String filename= "r255zhan-hw4-bm25-stemmed.txt";
				String filename= "r255zhan-hw4-cosine.txt";
			    FileWriter fw = new FileWriter(filename,true);
			    fw.write(topic + " Q0 "+ docno + " " + counter + " " + accumulator.get(i) + " "+ "r255zhan" + "\n");
			    fw.close();
			}
			catch(IOException ioe)
			{
			    System.err.println("IOException: " + ioe.getMessage());
			}
			counter ++;
		}
	}
	public static double calcdt(
			HashMap <String, Integer> term2Id,
			String term,
			DocIDCountPair post,
			double nk) {

		int n = 247034;
		double N = 131896;
		double fi = post.getCount();
		double Logfi = 0;
		double numerator;
		if (fi > 0) {
			Logfi = Math.log(fi) + 1;
		}
		numerator = (Logfi)*(Math.log(N/nk));
		double rawDenominator = 0;
		rawDenominator = Logfi*Math.log(N/nk);
		double rawDenominatorsquared = rawDenominator*rawDenominator;
		double realdenom = 0;
		for(int i = 1; i<=n; i ++) {
			realdenom += rawDenominatorsquared;
		}
		realdenom = Math.sqrt(realdenom);
		return numerator/realdenom;
	}

}
