package clase.web;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import clase.datos.Factory;
import clase.datos.Link;
import clase.datos.Message;
import clase.datos.Messages;
import clase.datos.Profile;
import clase.datos.User;
import clase.datos.Userpage;
import clase.datos.Users;

@Path("/usuarios/")
public class GestorUsuarios {
	
	@Context
	private UriInfo uriInfo;
	private Factory factory;


	public GestorUsuarios() {
		this.factory = Factory.getInstance();
	}
	
	// Ver datos de un Usuario en concreto	✓✓✓
	@GET
	@Path("{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserContent(@PathParam("username") String username) {
	    if (!factory.getUsers().containsKey(username))
	    	return Response.status(Response.Status.NOT_FOUND).entity("No existe un usuario con el username "+ username).build();
	    User user = factory.getUserByUsername(username);

	    return Response.status(Response.Status.OK).entity(user).build();
	}
	
	// Anadir un nuevo usuario 	✓✓✓
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(User newUser) {
		try {
			if (newUser == null || newUser.getUsername() == null || newUser.getUsername().isEmpty()) {
		        return Response.status(Response.Status.BAD_REQUEST).entity("El usuario es vacío o inválido").build();
		    }
			Hashtable<String, User> users = factory.getUsers();
			if (users.containsKey(newUser.getUsername()))
				return Response.status(Response.Status.CONFLICT).entity("El usuario ya existe").build();
			factory.createUser(newUser);

			String loc = uriInfo.getAbsolutePath() + "/" + (newUser.getUsername());
			return Response.status(Response.Status.CREATED).entity("El usuario ha sido creado").header("Location", loc).header("Content-Location", loc).build();
		} catch (Exception e) {
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Ha ocurrido un error interno en el servidor").build();
	    }
	}
	
	//Cambiar datos basicos de nuestro perfil ✓✓✓
	@PUT
	@Path("{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("username") String username, User updatedUser) {
		User user = factory.getUserByUsername(username);
		if (user == null)
			return Response.status(Response.Status.NOT_FOUND).build();		
		if (!updatedUser.getUsername().equals(user.getUsername()))
			return Response.status(Response.Status.FORBIDDEN).entity("El nombre de usuario no puede ser editado").build();
		if (updatedUser == null || updatedUser.getRealname() == null || updatedUser.getRealname().isEmpty())
	        return Response.status(Response.Status.BAD_REQUEST).entity("Los datos del usuario son inválidos o están vacíos").build();
		
		user.setRealname(updatedUser.getRealname());
		user.setEmail(updatedUser.getEmail());
		
		String loc = uriInfo.getAbsolutePath() + "/" + user.getUsername();
		return Response.status(Response.Status.OK).header("Content-Location", loc).build(); 
	}
	
	//Borrar nuestro perfil de la red social
	@DELETE
	@Path("{username}")
	public Response deleteUser(@PathParam("username") String username ) {
		User userDelete = factory.getUserByUsername(username);
		if (userDelete != null) {
			factory.getUsers().remove(username); 
			return Response.status(Response.Status.NO_CONTENT).build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	//Obtener lista de todos los usuarios por URI
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getUsersURI(
			@QueryParam("namePattern") String namePattern,
			@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("10") int limit) {
		
		List<User> filtUsers = new ArrayList<>(factory.getUsers().values());
		
		if (namePattern != null && !namePattern.isEmpty())
			filtUsers = filtUsers.stream().filter(user -> user.getUsername().contains(namePattern)).collect(Collectors.toList());
		
		if (offset < 0)
			offset = 0;
		if (limit <= 0)
			limit = 10;
		
		int end = Math.min(offset + limit, filtUsers.size());
		filtUsers = filtUsers.subList(offset, end);
		
		Users u = new Users();
		List<Link> usuarios = u.getUsuarios();
		for (User user : filtUsers) {
			URI uri = UriBuilder.fromPath(uriInfo.getAbsolutePath() + "/{username}").build(user.getUsername());
			usuarios.add(new Link(uri.toString(), "self"));
		}
		
		
		if (!usuarios.isEmpty())
			return Response.status(Response.Status.OK).entity(usuarios).build();
		return Response.status(Response.Status.NOT_FOUND).entity("No existe ningun usuario con el patron " + namePattern).build();
	}
	
	// Ver perfil de un Usuario en concreto ✓✓✓
	@GET
	@Path("{username}/perfil")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile(@PathParam("username") String username) {
	    if (!factory.getUsers().containsKey(username))
	    	return Response.status(Response.Status.NOT_FOUND).entity("No existe un usuario con el username "+ username).build();
	    User user = factory.getUserByUsername(username);
	    
	    Profile userProfile = new Profile();
	    userProfile.setUsername(user.getUsername());
	    userProfile.setRealname(user.getRealname());
	    userProfile.setEmail(user.getEmail());
	    userProfile.setLatestMsg(factory.getLatestMsg(username));
	    userProfile.setAmigos(factory.getFriendListByUsername(username).size());
	    Set<String> friends = factory.getFriendListByUsername(username);
	    userProfile.setLatestFriendMsgs(factory.getLatestFriendMsgs(friends));
	    
	    return Response.status(Response.Status.OK).entity(userProfile).build();
	}
}
