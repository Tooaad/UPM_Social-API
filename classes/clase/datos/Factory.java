package clase.datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Factory {
	
	private static Factory myInstance;
	public static Factory getInstance() {
		if (myInstance == null)
			myInstance = new Factory();
		return myInstance;
	}
	
	private Hashtable<String, User> users;
	private Hashtable<String, Userpage> userpages;
	private Hashtable<String, Set<String>> friendLists;
	
	private Factory() {
		this.users = new Hashtable<>();
		this.userpages = new Hashtable<>();
		this.friendLists = new Hashtable<>();
		loadData();
	}
	
	private void loadData() {
		String fileName = "./software2018/eclipse-workspace/WebApp/src/clase/datos/datos.txt";
	    String line = null;
	    User currentUser = null;
	    Map<String, List<String>> msgSentByOther = new HashMap<>();
	    try (Scanner sc = new Scanner(new File(fileName))) {
	        while ((sc.hasNextLine())) {
	        	String[] tokens = sc.nextLine().split(";");
	            if (tokens.length == 3) { // Es un usuario
	                String username = tokens[0];
	                String realname = tokens[1];
	                String email = tokens[2];
	                currentUser = new User(username, realname, email);
	                createUser(currentUser); // Se anade a memoria
	            } else if (tokens.length == 2) {
	            	String msgText = tokens[0];
	                String msgSender = tokens[1];
	                if (msgSender.equals(currentUser.getUsername())) // El mensaje es del usuario actual
	                    createMessage(new Message(msgText, msgSender));
	                else { // El mensaje es de un amigo
	                    sendMessageTo(new Message(msgText, msgSender), currentUser.getUsername());
	                    msgSentByOther.computeIfAbsent(currentUser.getUsername(), k -> new ArrayList<>()).add(msgSender);
	                }
	            } else// Línea inválida
	                System.out.println("Línea inválida: " + line);
	        }
	        for (Entry<String, List<String>> entry : msgSentByOther.entrySet()) {
	            String username = entry.getKey();
	            User user = getUserByUsername(username);
	            if (user != null)
	                for (String friend : entry.getValue())
	                    addFriend(username, friend);   
	        }
	    } catch (FileNotFoundException e) {
	        System.out.println("No se encontró el archivo: " + fileName);
	        e.printStackTrace();
	    } catch (IOException e) {
	        System.out.println("Error al leer el archivo: " + fileName);
	        e.printStackTrace();
	    }
		
		
	    /*
		createUser(new User("Kayle", "Kayle R.", "kaykayr@gmail.com"));
		createUser(new User("Tom", "Tom H.", "tommhyy@gmail.com"));
		createUser(new User("Alexa", "Alexa F.", "Alexxaaa@gmail.com"));
		createUser(new User("Sara", "Sara E.", "EsaraE@gmail.com"));
		createMessage(new Message("Buenos dias", "Kayle"));
		createMessage(new Message("hoy he comido macarrones", "Sara"));
		createMessage(new Message("Estoy skiando", "Tom"));
		createMessage(new Message("Hola Mundo", "Alexa"));
		sendMessageTo(new Message("Buenas tardes", "Alexa"), "Kayle");
		sendMessageTo(new Message("Yo tambien", "Alexa"), "Sara"); // Alexa esta intentando enviar un mensaje a sara y esta sobreescibiendo la PP
		sendMessageTo(new Message("Buenasss", "Tom"), "Kayle");
		addFriend("Kayle", "Alexa");
		addFriend("Kayle", "Tom");
		addFriend("Sara", "Alexa");
		addFriend("Alexa", "Kayle");
		addFriend("Tom", "Kayle");
		addFriend("Kayle", "Tom");
		addFriend("Alexa", "Tom");
		addFriend("Tom", "Alexa");
		*/
	}	

	public Hashtable<String, User> getUsers(){
		return users;
	}
	
	public User getUserByUsername(String username) {
		return users.get(username);
	}

	public Userpage getUserpageByUsername(String username) {
		return userpages.get(username);
	}
	
	public Set<String> getFriendListByUsername(String username) {
		return friendLists.get(username);
	}
	
	public void createUser(User user) {
		users.put(user.getUsername(), user);
		userpages.put(user.getUsername(), new Userpage());
		friendLists.put(user.getUsername(), new HashSet<String>());
    }
    
    public void deleteUser(User user) {
		users.remove(user.getUsername());
		// Borrar Posts del usuario
		userpages.remove(user.getUsername());
		friendLists.remove(user.getUsername());
    }
    
    public void editRealname(User user, String realname) {
    	user.setUsername(realname);
    	users.put(user.getUsername(), user);
    }
	
    public void createMessage(Message msg) {
    	Userpage pp = getUserpageByUsername(msg.getAuthor());
    	int id = pp.getMsgId();
    	
    	pp.getMessages().put(id, msg);
		msg.setId(id);
		msg.setDate(null);
		pp.setMsgId();
    	
    	userpages.put(msg.getAuthor(), pp);
    }
    
    public void eraseMessage(String owner, int id) {
    	Userpage pp = getUserpageByUsername(owner);
    	pp.getMessages().remove(id);
    	userpages.put(owner, pp);
    }
    
    public void sendMessageTo(Message msg, String to) {
    	Userpage pp = getUserpageByUsername(to);
    	int id = pp.getMsgId();
    	
    	pp.getMessages().put(id, msg);
		msg.setId(id);
		msg.setDate(null);
		pp.setMsgId();
    	
    	userpages.put(to, pp);
	}
    
    public void addFriend(String username, String friendname) {
    	Set<String> uList = getFriendListByUsername(username);
    	Set<String> fList = getFriendListByUsername(friendname);
    	uList.add(friendname);
    	fList.add(username);
    	friendLists.put(username, uList);
    	friendLists.put(friendname, fList);
    }
    
    public void removeFriend(String username, String friendname) {
    	Set<String> uList = getFriendListByUsername(username);
    	Set<String> fList = getFriendListByUsername(friendname);
    	uList.remove(friendname);
    	fList.remove(username);
    	friendLists.put(username, uList);
    	friendLists.put(friendname, fList);
    }
    
    public Message getLatestMsg(String owner) {
    	Userpage pp = getUserpageByUsername(owner);
    	
    	HashMap<Integer, Message> messages = pp.getMessages();
        Message lastMessage = messages.values().stream()
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .findFirst()
                .orElse(null);
    	
    	return lastMessage;
    }
    
    public Message getLatestMsgOfFriend(String friend) {
    	Userpage pp = getUserpageByUsername(friend);
    	
    	HashMap<Integer, Message> messages = pp.getMessages();
    	List<Message> friendMessages = messages.values().stream()
                .filter(msg -> msg.getAuthor().equals(friend))
                .collect(Collectors.toList());
    	
        Message lastMessage = friendMessages.stream()
                .sorted(Comparator.comparing(Message::getDate).reversed())
                .findFirst()
                .orElse(null);
    	
    	return lastMessage;
    }
    
    public List<Message> getLatestFriendMsgs(Set<String> friends) {
    	List<Message> latestMessages = new ArrayList<>();

        for (String friendName : friends) {
        	Userpage friendPage = getUserpageByUsername(friendName);
            if (friendPage.getMessages().size() > 0) {
                Message latestMsg = getLatestMsgOfFriend(friendName);
                if (latestMsg != null)
                    latestMessages.add(latestMsg);
            }
        }
        
        latestMessages.sort(Comparator.comparing(Message::getDate).reversed());

        if (latestMessages.size() > 10)
            latestMessages = latestMessages.subList(0, 10);

        return latestMessages;
    }
   
}