
public class Snippet {
	private String text = "";
	private String headline = "";
	private String date = "";
	private String docno ="";
	public Snippet(String text, String headline, String date, String docno) {
		this.text = text;
		this.headline = headline;
		this.date = date;
	}
	public String getDocno() {
		return this.docno;
	}
	
	public String getDate() {
		return this.date;
	}
	
	public String getHeadline() {
		return this.headline;
	}
	
	public String getText() {
		return this.text;
	}
	public void setHeadline(String newHeadline) {
		this.headline = newHeadline;
	}
}
