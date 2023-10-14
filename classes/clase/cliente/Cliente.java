package clase.cliente;

import java.io.StringReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import clase.datos.User;
import clase.datos.Link;
import clase.datos.Message;
import clase.datos.Profile;

public class Cliente {

    private static final String URL = "http://localhost:8080/WebApp/api/";

    private static void createUser(Scanner sc, WebTarget target) {
    	try {
	    	System.out.println("Ingrese el nombre de usuario");
	        String username = sc.nextLine();
	
	        System.out.println("Ingrese el nombre real");
	        String realname = sc.nextLine();
	
	        System.out.println("Ingrese el correo electrónico");
	        String email = sc.nextLine();
	
	        User user = new User(username, realname, email);
	
	        // Peticion HTTP
	        Response r = target.path("usuarios").request().post(Entity.json(user));
	        System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
    	} catch (Exception e) {
    		System.out.println("No se pudo mandar la peticion!");
    	}
    }

    private static void editUser(Scanner sc, WebTarget target) {
        System.out.println("Ingrese el nombre de usuario a editar");
        String username = sc.nextLine();
        
        try {
	        // Peticion HTTP GET
            User u = target.path("usuarios/" + username).request().accept(MediaType.APPLICATION_JSON).get(User.class);
            if (u == null)
                System.out.println("user es nulo");
            else {
            	System.out.println("Ingrese el nombre real");
                String realname = sc.nextLine();
                
                System.out.println("Ingrese el correo electrónico");
                String email = sc.nextLine();
                
                u.setRealname(realname);
                u.setEmail(email);
                
    	        // Peticion HTTP POST
                Response r = target.path("usuarios/" + username).request().put(Entity.entity(u, MediaType.APPLICATION_JSON));
                System.out.println(r.getStatus() + " " + r.getStatusInfo());
                r.close();
            }                
        } catch (BadRequestException e) {
            System.out.println("Bad request, usuario no existe");
        } catch (Exception e) {
    		System.out.println("No se pudo mandar la peticion!");
    	}
    }
    
	private static void existingUsers(Scanner sc, WebTarget target) {
		System.out.println("Filtrar por nombre (campo vacio si no se quiere filtro)");
		String pattern = sc.nextLine();
		
		try {
			Response r;
			if (pattern.equals(""))
		        // Peticion HTTP
				r = target.path("usuarios").request().accept(MediaType.APPLICATION_JSON).get();
			else
		        // Peticion HTTP
				r = target.path("usuarios").queryParam("namePattern", pattern).request().accept(MediaType.APPLICATION_JSON).get();

			JsonArray usuariosJson = r.readEntity(JsonArray.class);
			
			List<String> usuarios = new ArrayList<>();
			for (int i = 0; i < usuariosJson.size(); i++) {
			    JsonObject usuarioJson = usuariosJson.getJsonObject(i);
			    String usuarioUrl = usuarioJson.getString("url");
			    String usuarioNombre = usuarioUrl.substring(usuarioUrl.lastIndexOf('/') + 1);
			    usuarios.add(usuarioNombre);
			}
			
			if (usuarios.isEmpty()) {
				System.out.println("No hay usuarios con ese patron");
				return ;
			}

			System.out.println("Usuarios: " + usuarios);
			System.out.println(r.getStatus() + " " + r.getStatusInfo());
		    r.close();

		} catch (Exception e) {
    		System.out.println("No se pudo mandar la peticion!");
    	}
		
	}
	
	private static void getUserInfo(Scanner sc, WebTarget target) {
		System.out.println("Ingrese nombre del usuario");
		String username = sc.nextLine();
		
		try {
	        // Peticion HTTP
			User u = target.path("usuarios/" + username).request().accept(MediaType.APPLICATION_JSON).get(User.class);
            if (u == null)
                System.out.println("user es nulo");
            else
                System.out.println(u.toString());
            Response r = target.path("usuarios/" + username).request().put(Entity.entity(u, MediaType.APPLICATION_JSON));
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
            r.close();
		} catch (BadRequestException e) {
			System.out.println("Bad request, usuario no existe");
		} catch (Exception e) {
    		System.out.println("No se pudo mandar la peticion!");
    	}
	}
	
	private static void eraseUser(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese nombre del usuario a borrar");
	    String username = sc.nextLine();
	    
	    try {
	        // Peticion HTTP
	    	Response r = target.path("usuarios/" + username).request().delete();
	        if (r.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
	            System.out.println("Usuario " + username + " borrado correctamente.");
	        else if (r.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
	            System.out.println("Usuario " + username + " no encontrado.");
	        else
	            System.out.println("Ha ocurrido un error al borrar el usuario " + username);
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición!");
	    }
	}
	
	private static void addFriend(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese el nombre de usuario");
	    String username = sc.nextLine();
	    
	    System.out.println("Ingrese el nombre de su amigo");
	    String friend = sc.nextLine();
	    
	    try {
	        Response r = target.path("usuarios/" + username + "/amigos/" + friend).request().post(Entity.json(""));
	        if (r.getStatus() == Response.Status.CREATED.getStatusCode()) {
	            String responseStr = r.readEntity(String.class);
	            System.out.println(responseStr);
	        } else if (r.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
	            System.out.println("No se pudo agregar al amigo porque uno o ambos usuarios no existen.");
	        else
	            System.out.println("Error al agregar al amigo."); 
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}
	
	private static void eraseFriend(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese el nombre del usuario que quiere eliminar a un amigo:");
	    String username = sc.nextLine();
	    
	    System.out.println("Ingrese el nombre del amigo a eliminar:");
	    String friend = sc.nextLine();

	    try {
	    	Response r = target.path("usuarios/" + username + "/amigos/" + friend).request().delete();

	        if (r.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
	            System.out.println("Se eliminó al amigo " + friend + " correctamente.");
	        else if (r.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
	            System.out.println("No se encontró al usuario o al amigo especificado.");
	        else
	            System.out.println("No se pudo eliminar al amigo " + friend + ".");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}
	
	private static void showFriendList(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese el nombre de usuario:");
	    String username = sc.nextLine();

	    try {
	    	Response r = target.path("usuarios/" + username + "/amigos").request().accept(MediaType.APPLICATION_JSON).get();
	        if (r.getStatus() == 200) {
	        	JsonArray friendListJson = r.readEntity(JsonArray.class);
				
				List<String> friends = new ArrayList<>();
				for (int i = 0; i < friendListJson.size(); i++) {
				    JsonObject usuarioJson = friendListJson.getJsonObject(i);
				    String usuarioUrl = usuarioJson.getString("url");
				    String usuarioNombre = usuarioUrl.substring(usuarioUrl.lastIndexOf('/') + 1);
				    friends.add(usuarioNombre);
				}
				System.out.println("Amigos: " + friends);
	        } else
	            System.out.println("No se encontró el usuario " + username);
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}
		    
	private static void postMsg(Scanner sc, WebTarget target) {
		System.out.println("Ingrese su nombre de usuario:");
		String author = sc.nextLine();
		
		System.out.println("Ingrese el mensaje:");
		String text = sc.nextLine();
		
		Message msg = new Message(text, author);
		
		try {
			Response r= target.path("usuarios/" + author + "/pagina_personal").request().post(Entity.entity(msg, MediaType.APPLICATION_JSON));
			
			if (r.getStatus() == Response.Status.CREATED.getStatusCode())
				System.out.println("Mensaje enviado correctamente");
			else
				System.out.println("No se pudo enviar el mensaje");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
		} catch (Exception e) {
			System.out.println("No se pudo mandar la petición");
		}	
	}
	
	private static void editMsg(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese el nombre del usuario:");
	    String username = sc.nextLine();
	    System.out.println("Ingrese el id del mensaje:");
	    String messageId = sc.nextLine();
	    System.out.println("Ingrese el nuevo mensaje:");
	    String newContent = sc.nextLine();

	    try {
	    	Message msg = target.path("usuarios/" + username + "/pagina_personal/" + messageId).request().accept(MediaType.APPLICATION_JSON).get(Message.class);
	    	msg.setContent(newContent);
	    	
	    	Response r= target.path("usuarios/" + username + "/pagina_personal/" + messageId).request().put(Entity.entity(msg, MediaType.APPLICATION_JSON));

	        if (r.getStatus() == Response.Status.CREATED.getStatusCode())
	            System.out.println("Mensaje editado correctamente");
	        else
	            System.out.println("No se pudo editar el mensaje");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}
	
	private static void eraseMsg(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese el nombre del usuario:");
	    String username = sc.nextLine();
	    System.out.println("Ingrese el id del mensaje:");
	    String messageId = sc.nextLine();
	    
	    try {
	        Response r = target.path("usuarios/" + username + "/pagina_personal/" + messageId).request().delete();

	        if (r.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
	            System.out.println("Mensaje eliminado correctamente");
	        else
	            System.out.println("No se pudo eliminar el mensaje");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}
	
	private static void findMsgList(Scanner sc, WebTarget target) {
	    System.out.println("Ingrese el nombre del usuario de la pagina personal:");
	    String owner = sc.nextLine();
	    System.out.println("Ingrese el nombre del usuario que envió los mensajes:");
	    String username = sc.nextLine();
	    
	    try {
	    	Response r;
	    	if (username.equals(""))
	    		r = target.path("usuarios/" + owner + "/pagina_personal").request().accept(MediaType.APPLICATION_JSON).get();
	    	else
	    		r = target.path("usuarios/" + owner + "/pagina_personal").queryParam("user", username).request().accept(MediaType.APPLICATION_JSON).get();
	        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
	            JsonArray messagesJson = r.readEntity(JsonArray.class);

	            List<String> messages = new ArrayList<>();
	            for (int i = 0; i < messagesJson.size(); i++) {
	                JsonObject messageJson = messagesJson.getJsonObject(i);
	                String messageUrl = messageJson.getString("url");
	                String messageId = messageUrl.substring(messageUrl.lastIndexOf('/') + 1);
	                messages.add(messageId);
	            }
	            System.out.println("Id de mensajes de " + username + ": " + messages);
	        } else
	            System.out.println("No se encontraron mensajes");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (BadRequestException e) {
	        System.out.println("Bad request, usuario no existe");
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}
	
	private static void findMsgInfo(Scanner sc, WebTarget target) {
		System.out.println("Ingrese su nombre de usuario:");
	    String owner = sc.nextLine();

	    //System.out.println("Ingrese el nombre de usuario que envió el mensaje:");
	    //String usernameFind = sc.nextLine();

	    System.out.println("Ingrese el ID del mensaje:");
	    int id = Integer.parseInt(sc.nextLine());
	    
	    try {
	        Response r = target.path("usuarios/" + owner + "/pagina_personal/" + id).request().accept(MediaType.APPLICATION_JSON).get();
	        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
	            Message msg = r.readEntity(Message.class);
	            System.out.println("Información del mensaje:");
	            System.out.println("Autor: " + msg.getAuthor());
	            System.out.println("Destinatario: " + owner);
	            System.out.println("Mensaje: " + msg.getContent());
	            System.out.println("Fecha: " + msg.getDate());
	        }
	        else 
	            System.out.println("No se encontró el mensaje");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	        e.printStackTrace();
	    }
	}
	
	private static void sendMsgFriend(Scanner sc, WebTarget target) {
		System.out.println("Usuario que enviar el mensaje:");
		String sender = sc.nextLine();
		
		System.out.println("Usuario que recibe el mensaje:");
		String recipient = sc.nextLine();
		
		System.out.println("Ingrese el mensaje:");
		String text = sc.nextLine();
		
		Message msg = new Message(text, sender);
		try {
			Response r = target.path("usuarios/" + sender + "/mensaje/" + recipient).request().post(Entity.entity(msg, MediaType.APPLICATION_JSON));
			
			if (r.getStatus() == Response.Status.CREATED.getStatusCode())
				System.out.println("Mensaje enviado correctamente");
			else
				System.out.println("No se pudo enviar el mensaje");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
		} catch (Exception e) {
			System.out.println("No se pudo mandar la petición");
		}	
	}
	
	private static void getLatestMsg(Scanner sc, WebTarget target) {
		System.out.println("Usuario:");
		String user = sc.nextLine();
		
		System.out.println("Ingresa la fecha con el formato: YYYY-MM-DD");
		String dateStr = sc.nextLine();
		
	    try {
	        Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
	    } catch (ParseException e) {
	        System.out.println("Fecha inválida. Use el formato yyyy-MM-dd");
	        return;
	    }
		
	    try {
	    	Response r= target.path("usuarios/" + user + "/amigos/nuevos_mensajes")
	    			.queryParam("fromDate", dateStr)
	    			.request().accept(MediaType.APPLICATION_JSON).get();
	    	if (r.getStatus() == Response.Status.OK.getStatusCode()) {
	            JsonArray messagesJson = r.readEntity(JsonArray.class);

	            List<Link> messageLinks = new ArrayList<>();
	            for (int i = 0; i < messagesJson.size(); i++) {
	                JsonObject messageJson = messagesJson.getJsonObject(i);
	                String messageUrl = messageJson.getString("url");
	                Link messageLink = new Link(messageUrl, "self");
	                messageLinks.add(messageLink);
	            }
	            for (Link link : messageLinks)
	                System.out.println(link.getUrl());
	            
	        } else
	            System.out.println("No se encontraron mensajes");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (BadRequestException e) {
	        System.out.println("Bad request, usuario no existe");
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }	
	}
	
	private static void findTextMsg(Scanner sc, WebTarget target) {
		System.out.println("Usuario:");
		String user = sc.nextLine();
		
		System.out.println("Texto que se desea buscar");
		String content = sc.nextLine();
		
	    try {
	    	Response r= target.path("usuarios/" + user + "/amigos/mensajes")
	    			.queryParam("content", content)
	    			.request().accept(MediaType.APPLICATION_JSON).get();
	    	if (r.getStatus() == Response.Status.OK.getStatusCode()) {
	            JsonArray messagesJson = r.readEntity(JsonArray.class);

	            List<Link> messageLinks = new ArrayList<>();
	            for (int i = 0; i < messagesJson.size(); i++) {
	                JsonObject messageJson = messagesJson.getJsonObject(i);
	                String messageUrl = messageJson.getString("url");
	                Link messageLink = new Link(messageUrl, "self");
	                messageLinks.add(messageLink);
	            }
	            for (Link link : messageLinks)
	                System.out.println(link.getUrl());
	            
	        } else
	            System.out.println("No se encontraron mensajes");
            System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (BadRequestException e) {
	        System.out.println("Bad request, usuario no existe");
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }	
	}
	
    public static void main(String[] args) {

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(URL);

        Scanner sc = new Scanner(System.in);
        boolean loop = true;

        while (loop) {
        	System.out.println();
            System.out.println("0. Termina el programa");
            System.out.println("A. Crear un usuario");
            System.out.println("B. Editar un usuario");
            System.out.println("C. Borrar un usuario");
            System.out.println("D. Mirar datos de un usuario");
            System.out.println("E. Obtener una lista de usuarios existentes");
            System.out.println("F. Publicar un mensaje en tu pagina personal");
            System.out.println("G. Editar un mensaje en tu pagina personal");
            System.out.println("H. Borrar un mensaje en tu pagina personal");
            System.out.println("I. Obtener una lista de los mensajes escritos de "+ '\n' + "   una persona pagina personal");
            System.out.println("J. Mirar un mensaje existente");
            System.out.println("K. Anadir una amigo");
            System.out.println("L. Eliminar un amigo");
            System.out.println("M. Obtener una lista de nuestros amigos");
            System.out.println("N. Enviar un mensaje a la pagina personal de un amigo");
            System.out.println("O. Obtener una lista de los ultimos mensajes publicados por tus amigos en tu pagina personal");
            System.out.println("P. Buscar un texto entre todos los mensajes de tus amigos");
            System.out.println("Q. Mirar el perfil de un usuario");
            
            char in = sc.next().charAt(0);
            sc.nextLine();

            switch (in) {
                case '0':
                	System.out.println("Terminando");
                    loop = false;
                    break;
                case 'A':
                    createUser(sc, target);
                    break;
                case 'B':
                	editUser(sc, target);
                    break;
                case 'C':
                	eraseUser(sc, target);                	
                	break;
                case 'D':
                	getUserInfo(sc, target);
                	break;
                case 'E':
                	existingUsers(sc, target);
                	break;
                case 'F':
                	postMsg(sc, target);
                	break;
                case 'G':
                	editMsg(sc, target);
                	break;
                case 'H':
                	eraseMsg(sc, target);
                	break;
                case 'I':
                	findMsgList(sc, target);
                	break;
                case 'J':
                	findMsgInfo(sc, target);
                	break;
                case 'K':
                	addFriend(sc, target);
                	break;
                case 'L':
                	eraseFriend(sc, target);
                	break;
                case 'M':
                	showFriendList(sc, target);
                	break;
                case 'N':
                	sendMsgFriend(sc, target);
                	break;
                case 'O':
                	getLatestMsg(sc, target);
                	break;
                case 'P':
                	findTextMsg(sc, target);
                	break;
                case 'Q':
                	getProfile(sc, target);
                	break;
                default:
                    break;
            }
        }
    }

	private static void getProfile(Scanner sc, WebTarget target) {
		System.out.print("Ingresa el username del usuario a consultar: ");
	    String username = sc.nextLine();
	    Response r= target.path("usuarios/" + username + "/perfil").request().get();
	    try {
		    if (r.getStatus() == Response.Status.OK.getStatusCode()) {
		        Profile profile = r.readEntity(Profile.class);
		        System.out.println("Username: " + profile.getUsername());
		        System.out.println("Nombre real: " + profile.getRealname());
		        System.out.println("Email: " + profile.getEmail());
		        System.out.println("Número de amigos: " + profile.getAmigos());
		        System.out.println("Último mensaje en su userpage: " + profile.getLatestMsg().getContent());
		        List<Message> latestFriendMsgs = profile.getLatestFriendMsgs();
		        System.out.println("Mensajes de sus amigos:");
		        for (Message msg : latestFriendMsgs)
		            System.out.println(msg.getAuthor() + ": " + msg.getContent());
		    }
		    else if (r.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
		        System.out.println(r.readEntity(String.class));
		    else
		        System.out.println("Error obteniendo el perfil del usuario.");
		    
	        System.out.println(r.getStatus() + " " + r.getStatusInfo());
	        r.close();
	    } catch (Exception e) {
	        System.out.println("No se pudo mandar la petición");
	    }
	}


}
