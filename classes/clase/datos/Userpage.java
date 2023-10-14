package clase.datos;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userpage")
public class Userpage {
	private HashMap<Integer, Message> messages;
	private int msgId;
	
	public Userpage() {
		this.messages = new HashMap<Integer, Message>();
		this.msgId = 1;
	}
	
	@XmlElement(name = "post")
	public HashMap<Integer, Message> getMessages() {
		return this.messages;
	}
	
	public void setMessages(HashMap<Integer, Message> messages) {
		this.messages = messages;
	}
	
	public int getMsgId() {
		return this.msgId;
	}
	
	public void setMsgId() {
		msgId++;
	}
}
