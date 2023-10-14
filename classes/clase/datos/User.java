package clase.datos;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "user")
public class User {
	//private int id;
	private String username;
	private String realname;
	private String email;
	//private ArrayList<String> friendList; // Quitar + Cambiar ArrayList por Set por claves unicas
	
	public User() {
	}
	
	public User(String username, String realname, String email) {
		this.username = username;
		this.realname = realname;
		this.email = email;
		//this.page = new Userpage();
		//this.friendList = new ArrayList<String>(); 
	}
	
	@XmlAttribute(required = true)
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getRealname() {
		return realname;
	}
	
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
/*
	@XmlElement(name = "userpage")
	public Userpage getUserpage() {
		return page;
	}
	
	public void setUserpage(Userpage page) {
		this.page = page;
	}
	public ArrayList<String> getFriendList() {
		return this.friendList;
	}
	
	public void setFriendList(ArrayList<String> friendList) {
		this.friendList = friendList;
	}
 */
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Username: ").append(username).append("\n");
	    sb.append("Realname: ").append(realname).append("\n");
	    sb.append("Email: ").append(email).append("\n");
	    return sb.toString();
	}
}
