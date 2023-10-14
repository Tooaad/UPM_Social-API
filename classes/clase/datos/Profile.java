package clase.datos;

import java.util.List;

public class Profile {
    private String username;
    private String realname;
    private String email;
    private int amigos;
    private Message latestMsg;
    private List<Message> latestFriendMsgs;
    
    public Profile() {
    	
    }
    
    public Profile(String username, String realname, String email, int amigos, Message latestMsg, List<Message> latestFriendMsgs) {
    	this.username = username;
    	this.realname = realname;
    	this.email = email;
    	this.amigos = amigos;
    	this.latestMsg = latestMsg;
    	this.latestFriendMsgs = latestFriendMsgs;
    }
    
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

	public int getAmigos() {
		return amigos;
	}

	public void setAmigos(int nAmigos) {
		this.amigos = nAmigos;
	}

	public Message getLatestMsg() {
		return latestMsg;
	}

	public void setLatestMsg(Message latestMsg) {
		this.latestMsg = latestMsg;
	}

	public List<Message> getLatestFriendMsgs() {
		return latestFriendMsgs;
	}

	public void setLatestFriendMsgs(List<Message> latestFriendMsgs) {
		this.latestFriendMsgs = latestFriendMsgs;
	}
}
