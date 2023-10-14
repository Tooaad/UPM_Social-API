package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

public class Messages {
	private ArrayList<Link> message;
	
	public Messages() {
		this.message = new ArrayList<Link>();
	}
	
	@XmlElement(name="mensajes")
	public ArrayList<Link> getMessages() {
		return message;
	}
	
	public void setMessages(ArrayList<Link> message) {
		this.message = message;
	}
}
