package clase.web;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import clase.datos.Factory;
import clase.datos.Link;
import clase.datos.Message;
import clase.datos.Messages;
import clase.datos.User;
import clase.datos.Users;

@Path("/usuarios/")
public class GestorAmigos {

	@Context
	private UriInfo uriInfo;
	private Factory factory;


	public GestorAmigos() {
		this.factory = Factory.getInstance();
	}


	@POST
	@Path("{username}/amigos/{friend}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addFriend(@PathParam("username") String username, @PathParam("friend") String friend) {
		if (!(factory.getUsers().containsKey(username) && factory.getUsers().containsKey(friend)))
			return Response.status(Response.Status.NOT_FOUND).build();
	
	    factory.addFriend(username, friend);
	    factory.addFriend(friend, username);
	    
	    String loc = uriInfo.getAbsolutePath() + "/" + friend;
		return Response.status(Response.Status.CREATED).entity("Se ha anadido a " + friend + " como amigo.").header("Location", loc).header("Content-Location", loc).build();
	}
	
	@DELETE
	@Path("{username}/amigos/{friend}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeFriend(@PathParam("username") String username, @PathParam("friend") String friend) {
		if (!(factory.getUsers().containsKey(username) && factory.getUsers().containsKey(friend)))
			return Response.status(Response.Status.NOT_FOUND).build();				
		
		factory.removeFriend(username, friend);
		factory.removeFriend(friend, username);
		
		return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@GET
	@Path("{username}/amigos")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getFriendsURI(	@PathParam("username") String username,
									@QueryParam("namePattern") String namePattern,
									@QueryParam("offset") @DefaultValue("0") int offset,
									@QueryParam("limit") @DefaultValue("10") int limit) {
		
		if (factory.getUsers().contains(username))
			return Response.status(Response.Status.NOT_FOUND).build();
		Set<String> friendList = factory.getFriendListByUsername(username);
		
		if (namePattern != null && !namePattern.isEmpty())
	        friendList = friendList.stream().filter(u -> u.contains(namePattern)).collect(Collectors.toSet());
		
		if (offset < 0)
			offset = 0;
		if (limit <= 0)
			limit = 10;
		
		List<String> friendListSubset = new ArrayList<>(friendList);
		int end = Math.min(offset + limit, friendList.size());
		friendListSubset = friendListSubset.subList(offset, end);
		
		Users u = new Users();
		List<Link> usuarios = u.getUsuarios();
		for (String friend : friendList) {
			URI uri = UriBuilder.fromUri(uriInfo.getBaseUri() + "usuarios/" + friend).build();
			usuarios.add(new Link(uri.toString(), "self"));
		}

		if (!usuarios.isEmpty())
			return Response.status(Response.Status.OK).entity(usuarios).build();
		return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron amigos").build();
	}

	// Revisar nuevos mensajes de nuestros amigos dada una fecha
	@GET
	@Path("{username}/amigos/nuevos_mensajes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFriendsMessagesURI(	@PathParam("username") String username,
											@QueryParam("fromDate") String fromDateStr,
											@QueryParam("limit") @DefaultValue("10") int limit,
											@QueryParam("offset") @DefaultValue("0") int offset) {
	    User user = factory.getUserByUsername(username);
	    if (user == null)
	        return Response.status(Response.Status.NOT_FOUND).build();
	    
	    List<String> friendList = new ArrayList<>(factory.getFriendListByUsername(username));
	    List<Message> messages = new ArrayList<>();
	    for (String friendUsername : friendList) {
	        User friend = factory.getUserByUsername(friendUsername);
	    	List<Message> friendMessages = new ArrayList<>(factory.getUserpageByUsername(friend.getUsername()).getMessages().values());
	        Date toDate;
	    	if (fromDateStr != null && !fromDateStr.isEmpty()) {
	            try {
	            	toDate = new SimpleDateFormat("yyyy-MM-dd").parse(fromDateStr);
	                friendMessages = friendMessages.stream().filter(msg -> msg.getDate().after(toDate)).collect(Collectors.toList());
	            } catch (ParseException e) {
	                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format. Use yyyy-MM-dd").build();
	            }
	        } else {
	            Calendar cal = Calendar.getInstance();
	            cal.add(Calendar.DATE, -1);
	            toDate = cal.getTime();
	        }
	        messages.addAll(friendMessages.stream().filter(msg -> msg.getAuthor().equals(friend.getUsername())).collect(Collectors.toList()));
	    }

	    if (limit > 0) {
	        if (offset > 0)
	            messages = messages.subList(offset, Math.min(offset + limit, messages.size()));
	        else
	            messages = messages.subList(0, Math.min(limit, messages.size()));
	    }
	     

		Messages u = new Messages();
		List<Link> friendMessages = u.getMessages();
		for (Message fMsgs : messages) {
			URI uri = UriBuilder.fromPath(uriInfo.getBaseUri() + "usuarios/" + fMsgs.getAuthor() + "/pagina_personal/" + fMsgs.getId()).build();
			friendMessages.add(new Link(uri.toString(), "self"));
		}
	    
	    if (!friendMessages.isEmpty())
	        return Response.status(Response.Status.OK).entity(friendMessages).build();
	    return Response.status(Response.Status.NOT_FOUND).entity("No hay mensajes disponibles").build();
	}
	
	@GET
	@Path("{username}/amigos/mensajes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchFriendsMessagesByContent( @PathParam("username") String username,
	                                                @QueryParam("content") String content,
	                                                @QueryParam("offset") @DefaultValue("0") int offset,
	                                                @QueryParam("limit") @DefaultValue("10") int limit) {
		
		User user = factory.getUserByUsername(username);
	    if (user == null)
	        return Response.status(Response.Status.NOT_FOUND).build();
	
	    List<Message> messages = new ArrayList<>();
	    List<User> friends = new ArrayList<>();
	    for (String friendName : factory.getFriendListByUsername(username)) {
	        User friend = factory.getUserByUsername(friendName);
	        friends.add(friend);
	        if (content == null || content.isEmpty())
	            messages.addAll(factory.getUserpageByUsername(friend.getUsername()).getMessages().values());
	        else
	        	for (Message msg : factory.getUserpageByUsername(friend.getUsername()).getMessages().values())
	                if (msg.getContent().contains(content))
	                    messages.add(msg);
	    }
	    
	    Collections.sort(messages, Comparator.comparing(Message::getDate).reversed());

	    if (offset < 0)
	        offset = 0;
	    if (limit <= 0)
	        limit = 10;

	    int end = Math.min(offset + limit, messages.size());
	    messages = messages.subList(offset, end);
	
	    Users u = new Users();
	    List<Link> links = u.getUsuarios();
	    for (Message msg : messages) {
	        User author = null;
	        for (User friend : friends) {
	        	if (factory.getUserpageByUsername(friend.getUsername()).getMessages().containsValue(msg)) {
	                author = friend;
	                break;
	            }
	        }
	        if (author != null) {
	            URI uri = UriBuilder.fromPath(uriInfo.getBaseUri() + "usuarios/" + author.getUsername() + "/pagina_personal/" + msg.getId()).build(msg.getId());
	            links.add(new Link(uri.toString(), "self"));
	        }
	    }
	    if (!links.isEmpty())
	        return Response.status(Response.Status.OK).entity(links).build();
	    return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron mensajes con ese contenido").build();
	}
	
	
}
