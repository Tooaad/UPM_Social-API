package clase.datos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mensaje") // REVISAR
public class Message {
	private String content;
	private Date date;
	private String author;
	private int Id;
	
	public Message() {
	}
	
	public Message(String content, String author) {
		this.content = content;
		this.date = new Date(System.currentTimeMillis());
		this.author = author;
	}
	
	public Message(String content, String author, Date date, int id) {
		this.content = content;
		this.date = date;
		this.author = author;
		this.Id = id;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public void setDate(Date date) {
		if (date == null) {
			this.date = new Date(System.currentTimeMillis());
			return ;
		}
		this.date = date;
	}
	
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public int getId() {
		return this.Id;
	}
	
	public void setId(int Id) {
		this.Id = Id;
	}
	
}
