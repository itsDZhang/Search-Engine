
//the metadata class
public class metaData {
	private String docNo = "";
	private String headLine = "";
	private String date = "";
	private int internalId = 0;
	private String docLength = "";
    public metaData ( int internalId, String docNo, String headLine, String date, String documentLength) {
    	this.docLength = documentLength;
    	this.docNo = docNo;
    	this.headLine = headLine;
    	this.date = date;
    	this.internalId = internalId;
    }
    public void setId(int id) {
    	this.internalId = id;
    }
    public void setDocNo(String docNo) {
    	this.docNo = docNo;
    }
    public void setHeadLine(String headLine) {
    	this.headLine = headLine;
    }
    public void setDate(String date) {
    	this.date = date;
    }
    public void setDocLength(String length) {
    	this.docLength = length;
    }
    public int getId() {
    	
    	return this.internalId;
    }
    public String getDocNo() {
    	return this.docNo;
    }
    public String getHeadline() {
    	return this.headLine;
    }
    public String getDate() {
    	return this.date;
    }
    public String docLength() {
    	return this.docLength;
    }
}
