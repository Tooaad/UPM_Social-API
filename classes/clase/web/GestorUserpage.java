package clase.web;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import clase.datos.Factory;
import clase.datos.Link;
import clase.datos.Message;
import clase.datos.Messages;
import clase.datos.User;
import clase.datos.Userpage;
import clase.datos.Users;

@Path("/usuarios/")
public class GestorUserpage {

	@Context
	private UriInfo uriInfo;
	private Factory factory;


	public GestorUserpage() {
		this.factory = Factory.getInstance();
	}
	
	/*
	//Obtener una lista de todos los mensajes que hay en la pp de un usuario
		@GET
		@Path("{owner}/pagina_personal")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getUserpageMessagesURI(@PathParam("owner") String username) {
			User owner = factory.getUserByUsername(username);
			if (owner == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			
			List<Message> messages = new ArrayList<Message>(factory.getUserpageByUsername(owner.getUsername()).getMessages().values());
			
			Messages m = new Messages();
			List<Link> msgs = m.getMessages();
			for (Message msg : messages) {
				URI uri = UriBuilder.fromPath(uriInfo.getAbsolutePath() + "/" + String.valueOf(msg.getId())).build();
				msgs.add(new Link(uri.toString(), "self"));
			}
			
			if (msgs == null)
				return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron mensajes").build();
			return Response.status(Response.Status.OK).entity(msgs).build();
		}
		*/
		
		//Publicar un nuevo mensaje en la pp
		@POST
		@Path("{username}/pagina_personal")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response addMessageToUserpage(@PathParam("username") String username, Message msg) {
			try {
				if (msg == null || msg.getAuthor() == null || msg.getContent() == null || !msg.getAuthor().equals(username))
					return Response.status(Response.Status.BAD_REQUEST).entity("Existe algun parametro que no tiene valor").build();
				User user = factory.getUserByUsername(username);
				if (user == null)
					return Response.status(Response.Status.NOT_FOUND).build();
				
				factory.sendMessageTo(msg, username);
				String loc = uriInfo.getAbsolutePath() + "/" + user.getUsername() + "/pagina_personal";
				return Response.status(Response.Status.CREATED).entity("El mensaje se ha publicado").header("Location", loc).header("Content-Location", loc).build();
			} catch (Exception e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se ha podido enviar el mensaje").build();
			}
		}
		
		//Borrar mensaje en la pp
		@DELETE
		@Path("{owner}/pagina_personal/{messageId}")
		public Response deleteMessageFromUserpage(	@PathParam("owner") String ownername,
													@PathParam("messageId") int messageId) {
			User owner = factory.getUserByUsername(ownername);
			if (owner == null)
				return Response.status(Response.Status.NOT_FOUND).build();			
			
			if (!factory.getUserpageByUsername(ownername).getMessages().containsKey(messageId))
				return Response.status(Response.Status.NOT_FOUND).build();

			factory.eraseMessage(ownername, messageId);
			
			return Response.status(Response.Status.NO_CONTENT).build();
		}
		
		//Editar mensaje en la pp
		@PUT
		@Path("{username}/pagina_personal/{messageId}")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response editMessageFromUserpage(@PathParam("username") String username, @PathParam("messageId") int msgId, Message msg) {
			if (msg == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			User user = factory.getUserByUsername(username);
			if (user == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			
			Userpage userpage = factory.getUserpageByUsername(user.getUsername());
			Message oldMsg = userpage.getMessages().get(msgId);
			
		    if (oldMsg == null)
		        return Response.status(Response.Status.NOT_FOUND).build();
		    if (!oldMsg.getAuthor().equals(username))
		        return Response.status(Response.Status.FORBIDDEN).entity("No tienes permiso para editar este mensaje").build();
		    
		    oldMsg.setContent(msg.getContent());
			
		    String loc = uriInfo.getAbsolutePath() + "/" + user.getUsername() + "/pagina_personal/" + msgId;
			return Response.status(Response.Status.OK).entity("El mensaje se ha publicado").header("Location", loc).header("Content-Location", loc).build();
		}

		//Lista de todos los mensajes de un Usuario en una pp
		@GET
		@Path("{owner}/pagina_personal")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getUserMessagesURI(@PathParam("owner") String username,
										@QueryParam("user") String usernameFind,
										@QueryParam("fromDate") String fromDateStr,
										@QueryParam("endDate") String endDateStr,
										@QueryParam("limit") @DefaultValue("-1") int limit,
										@QueryParam("offset") @DefaultValue("0") int offset) {
			User owner = factory.getUserByUsername(username);
			if (owner == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			if (usernameFind != null)
			{
				User user = factory.getUserByUsername(usernameFind);
				if (user == null)
					return Response.status(Response.Status.NOT_FOUND).build();
			}
			
			List<Message> messages = new ArrayList<Message>(factory.getUserpageByUsername(owner.getUsername()).getMessages().values());
		    if (usernameFind != null) {
		        messages = messages.stream().filter(msg -> msg.getAuthor().equals(usernameFind)).collect(Collectors.toList());
		    }
			
			final Date fromDate;
			final Date endDate;
			try {
			    fromDate = fromDateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(fromDateStr) : null;
			    endDate = endDateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr) : null;
			} catch(ParseException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format. Use yyyy-MM-dd").build();
			}
			
			//List<Message> messages = new ArrayList<Message>(factory.getUserpageByUsername(owner.getUsername()).getMessages().values());
			//messages = messages.stream().filter(msg -> msg.getAuthor().equals(usernameFind)).collect(Collectors.toList());
			
			if (fromDate != null)
				messages = messages.stream().filter(msg -> msg.getDate().after(fromDate)).collect(Collectors.toList());
			if (endDate != null)
				messages = messages.stream().filter(msg -> msg.getDate().before(endDate)).collect(Collectors.toList());
					
			if (limit > 0) {
				if (offset > 0)
					messages = messages.subList(offset, Math.min(offset + limit, messages.size()));
				else
					messages = messages.subList(0, Math.min(limit, messages.size()));
			}
			
			Users u = new Users();
			List<Link> usuarios = u.getUsuarios();
			for (int i = 0; i < messages.size(); i++) {
				URI uri = UriBuilder.fromPath(uriInfo.getAbsolutePath() + "/" + String.valueOf(messages.get(i).getId())).build(username);
				usuarios.add(new Link(uri.toString(), "self"));
			}

			if (!usuarios.isEmpty())
				return Response.status(Response.Status.OK).entity(usuarios).build();
			return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron mensajes").build();
		}
		
		//Obtener mensaje concreto de una pp
		@GET
		@Path("{owner}/pagina_personal/{id}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getUserMessageId(@PathParam("owner") String username,
										 @PathParam("id") int id) {
			User owner = factory.getUserByUsername(username);
			if (owner == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			
			Message msg = factory.getUserpageByUsername(owner.getUsername()).getMessages().get(id);
			
			if (msg == null)
				return Response.status(Response.Status.NOT_FOUND).entity("No se encontraron mensajes").build();
			return Response.status(Response.Status.OK).entity(msg).build();
		}
		
		// Enviar un mensaje al pp de un amigo
		@POST
		@Path("{sender}/mensaje/{recipient}")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response sendMessageToUserpage(	@PathParam("sender") String senderStr,
		 										@PathParam("recipient") String recipientStr,
												Message msg) {
			try {
				if (msg == null || msg.getAuthor() == null || msg.getContent() == null || !msg.getAuthor().equals(senderStr))
					return Response.status(Response.Status.BAD_REQUEST).entity("Existe algun parametro que no tiene valor").build();
				User sender = factory.getUserByUsername(senderStr);
				if (sender == null)
					return Response.status(Response.Status.NOT_FOUND).build();
				User recipient = factory.getUserByUsername(recipientStr);
				if (recipient == null)
					return Response.status(Response.Status.NOT_FOUND).build();
				
				factory.sendMessageTo(msg, recipientStr);
				//user.getUserpage().addMessage(msg);
				URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(msg.getId())).build();
				return Response.created(uri).build();
			} catch (Exception e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se ha podido enviar el mensaje").build();
			}
		}
}
