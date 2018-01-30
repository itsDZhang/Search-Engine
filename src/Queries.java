
public class Queries {
	private String topicNum = "";
	private String title = "";
	
	Queries(String topicNum, String title){
		this.topicNum = topicNum;
		this.title = title;
	}
	
	public String getNum() {
		return this.topicNum;
	}
	public String getTitle() {
		return this.title;
	}
}
