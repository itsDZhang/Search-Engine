//Doc ID Count Class
public class DocIDCountPair implements java.io.Serializable {
	private int docID = 0;
	private int count = 0;
	private static final long serialVersionUID = 1L;
	
	DocIDCountPair(int docID, int count){
		this.docID = docID;
		this.count = count;
	}
	public int getDocID() {
		return this.docID;
	}
	public int getCount() {
		return this.count;
	}
}
