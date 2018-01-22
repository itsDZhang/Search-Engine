
//the metadata class
public class metaData {
	private String docNo = "";
	private String headLine = "";
	private String date = "";
	private int internalId = 0;
    public metaData ( int internalId, String docNo, String headLine, String date) {
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
}
