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
		
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		doc2Id = generateDoc2IdHash();
		id2MetaData = generateid2MetaDataHash();
		
		
		String search = "8029";
		int searchA = 8029;
		Object obj = searchA;
//		Object obj = search;
		if( obj instanceof Integer ) {
			int searchId = (int) obj;
			metaData testB = id2MetaData.get(searchId);
			String date = getDateNum(testB.getDocNo());
			Scanner text = new Scanner( 
					new FileReader("results/" + date + "/" + 
							searchId + ".txt"));
			
			System.out.println("DocNo: " + testB.getDocNo());
			System.out.println("Internal Id: " + testB.getId());
			System.out.println("Headline: " + testB.getHeadline());
			System.out.println("Date: " + testB.getDate());
			System.out.println("Raw Document: ");
			while(text.hasNextLine()) {
				System.out.println(text.nextLine());
			}
//			=========================End==========================
		} else if ( obj instanceof String) {
			//=================Search if parameter is a string ===========
//			String search = "LA012089-0180";
			String searchString = (String) obj;
			metaData testA = id2MetaData.get(doc2Id.get(searchString));
			String date = getDateNum(searchString);
			Scanner text = new Scanner( 
					new FileReader("results/" + date + "/" + 
							doc2Id.get(searchString) + ".txt"));
			
			System.out.println("DocNo: " + testA.getDocNo());
			System.out.println("Internal Id: " + testA.getId());
			System.out.println("Headline: " + testA.getHeadline());
			System.out.println("Date: " +testA.getDate());
			System.out.println("Raw Document: ");
			while(text.hasNextLine()) {
				System.out.println(text.nextLine());
			}
			// ===================End ===================================
		}
		
		
		
	}
	
	
	public static HashMap<String, Integer> generateDoc2IdHash() throws FileNotFoundException {
		HashMap<String, Integer> doc2Id = new HashMap<String, Integer>();
		Scanner docno2idtxt = new Scanner(new FileReader("doc2Id.txt"));
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
	public static HashMap<Integer, metaData> generateid2MetaDataHash() throws FileNotFoundException {
		HashMap<Integer, metaData> id2MetaData = new HashMap<Integer, metaData>();
		Scanner id2MetaDatatxt = new Scanner(new FileReader("id2MetaData.txt"));
		while(id2MetaDatatxt.hasNextLine()) {
			String nextLine = id2MetaDatatxt.nextLine();
			String[] nextLineArr = nextLine.split("\\|");
			int key = Integer.parseInt(nextLineArr[0]);
			String[] data = nextLineArr[1].split("\\{}");
			metaData meta = new metaData(Integer.parseInt(data[0]), data[1], data[2],data[3]);
			id2MetaData.put(key, meta);
		}
		return id2MetaData;
	}
	public static String getDateNum(String docNum) throws ParseException {
		
		String month = "";
		String day = "";
		String year = "";
		String date = "";
		
			docNum = docNum.replaceAll("\\D+","");
//			System.out.println(docNum);
			month = docNum.substring(0,2);
			day = docNum.substring(2,4);
			year = docNum.substring(4,6);
			
		
		date = year  + month  + day;
		
		return date;
	}

}
