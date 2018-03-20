import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Scanner;

public class GetDoc {

	public static void main(String[] args) throws FileNotFoundException, ParseException {
		// TODO Auto-generated method stub
//		if(args.length < 3) {
//			System.out.println("You did not input sufficient arguements. This program must accept 3 arguements");
//			System.out.println("First Arguement: a path to the location of the documents and metadata created from the IndexEngine");
//			System.out.println("Second Arguement: either the strings \"id\" or \"docno\" ");
//			System.out.println("Third Arguement: either the internal integer id or the document's docno");
//			System.exit(0);
//		}
//		String currentDir = System.getProperty("user.dir");
//		System.out.println(currentDir);
		String localPath = "C:/Users/Rui/eclipse-workspace/541";
		String type = "docno";
		String DocOrId = "LA093090-0075";
//		String type = "id";
//		String DocOrId = "97515";
//		The two hashmaps to add
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		doc2Id = generateDoc2IdHash(localPath);
		id2MetaData = generateid2MetaDataHash(localPath);
		if (type.contains("id")) {
			int search = Integer.parseInt(DocOrId);
			runAsId(id2MetaData, search, localPath );
		} else if ( type.contains("docno")) {
			runAsDocNo(doc2Id, id2MetaData, DocOrId, localPath );
		}
	}
//	This method will activate if user is searching up by internal id
	public static void runAsId(HashMap<Integer, metaData> id2MetaData, int searchId, String localPath) throws FileNotFoundException, ParseException {
		metaData testB = id2MetaData.get(searchId);
		String date = getDateNum(testB.getDocNo());
		Scanner text = new Scanner( 
				new FileReader(localPath + "/filesToBeStored/" + date + "/" + 
						searchId + ".txt"));
		System.out.println("DocNo: " + testB.getDocNo());
		System.out.println("Internal Id: " + testB.getId());
		System.out.println("Headline: " + testB.getHeadline());
		System.out.println("Date: " + testB.getDate());
		System.out.println("Raw Document: ");
		while(text.hasNextLine()) {
			System.out.println(text.nextLine());
		}
	}
//	This method will activate if user is searching by docno 
	public static void runAsDocNo(HashMap<String, Integer> doc2Id, HashMap<Integer, metaData> id2MetaData, String searchString, String localPath) throws ParseException, FileNotFoundException {
		metaData testA = id2MetaData.get(doc2Id.get(searchString));
		String date = getDateNum(searchString);
		Scanner text = new Scanner( 
				new FileReader(localPath + "/filesToBeStored/" + date + "/" + 
						doc2Id.get(searchString) + ".txt"));
		
		System.out.println("DocNo: " + testA.getDocNo());
		System.out.println("Internal Id: " + testA.getId());
		System.out.println("Headline: " + testA.getHeadline());
		System.out.println("Date: " +testA.getDate());
		System.out.println("Raw Document: ");
		while(text.hasNextLine()) {
			System.out.println(text.nextLine());
		}
	}
	
//	This method grabs the docno to id txt file, reads the file and populates it
	public static HashMap<String, Integer> generateDoc2IdHash(String localPath) throws FileNotFoundException {
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		Scanner docno2idtxt = new Scanner(new FileReader(localPath + "/index/doc2Id.txt"));
		while(docno2idtxt.hasNextLine()) {
			String nextLine = docno2idtxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
//			System.out.println(nextLine);
			String key = nextLineArr[0];
			int value = Integer.parseInt(nextLineArr[1]);
			doc2Id.put(key, value);
//			System.out.println(docno2idtxt.next());
		}
		return doc2Id;
	}
//	This method grabs the id to metadata txt file, reads it, and populates the hashmap	
	public static HashMap<Integer, metaData> generateid2MetaDataHash(String localPath) throws FileNotFoundException {
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		Scanner id2MetaDatatxt = new Scanner(new FileReader(localPath +"/index/id2MetaData.txt"));
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
//	Converts the string the string to a number
	public static String getDateNum(String docNum) throws ParseException {
		String month = "";
		String day = "";
		String year = "";
		String date = "";
			docNum = docNum.replaceAll("\\D+","");
			month = docNum.substring(0,2);
			day = docNum.substring(2,4);
			year = docNum.substring(4,6);
		date = year  + month  + day;
		return date;
	}

}
