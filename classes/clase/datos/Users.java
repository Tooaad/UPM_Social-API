package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

public class Users {
	private ArrayList<Link> usuarios;
	
	public Users() {
		this.usuarios = new ArrayList<Link>();
	}
	
	@XmlElement(name="usuario")
	public ArrayList<Link> getUsuarios() {
		return usuarios;
	}
	
	public void setUsuarios(ArrayList<Link> usuarios) {
		this.usuarios = usuarios;
	}
}
