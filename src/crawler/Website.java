package crawler;

import java.util.Objects;

public class Website implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String url;
	private String content;
	public Website(String url, String content){
		this.url = url;
		this.content = content;
	}
	public String getURL(){
		return url;
	}
	public String getContent(){
		return content;
	}
	public String toString(){
		return url + ":\n" + content;
	}
	public boolean equals(Website w){
		return url.equals(w.url) && content.equals(w.content);
	}
	public int hashCode(){
		return Objects.hash(url,content);
	}
}