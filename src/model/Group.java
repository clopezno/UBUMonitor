package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase Group para distinguir los grupos que hay en un curso, así como los
 * grupos en los que se encuentra un usuario.
 * 
 * @author Claudia Martínez Herrero
 * @author Yi Peng Ji
 * @version 2.0.1
 * @since 2.0.1
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private DescriptionFormat descriptionFormat;

	private Set<EnrolledUser> enrolledUsers;
	private Course course;

	public Group() {
		this.enrolledUsers=new HashSet<>();
	}
	
	/**
	 * Constructor de la clase Group. Establece un grupo.
	 * 
	 * @param id
	 *            Id del grupo.
	 * @param name
	 *            Nombre del grupo.
	 * @param description
	 *            Descripción del grupo.
	 * 
	 */
	public Group(int id, String name, String description, DescriptionFormat descriptionFormat) {
		this();
		this.id = id;
		this.name = name;
		this.description = description;
		this.descriptionFormat = descriptionFormat;
		
	}
	
	public Group(int id) {
		this();
		this.id=id;
	}

	/**
	 * Devuelve el id del grupo.
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del grupo.
	 * 
	 * @param id
	 *            El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del grupo.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Modifica el nombre del grupo.
	 * 
	 * @param name
	 *            El nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve la descripción del grupo.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Modifica la descripción del grupo.
	 * 
	 * @param description
	 *            La descripción.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public DescriptionFormat getDescriptionFormat() {
		return descriptionFormat;
	}

	public void setDescriptionFormat(DescriptionFormat descriptionFormat) {
		this.descriptionFormat = descriptionFormat;
	}

	public Set<EnrolledUser> getEnrolledUsers() {
		return enrolledUsers;
	}

	public void setEnrolledUsers(Set<EnrolledUser> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
	}
	
	
	public Course getCourse() {
		return course;
	}

	public void addEnrolledUser(EnrolledUser user) {
		enrolledUsers.add(user);
	}
	

	public void setCourse(Course course) {
		this.course = course;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return id;
	}


	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id != other.id)
			return false;
		return true;
	}


}
