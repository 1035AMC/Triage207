package loginDatabase;

import java.io.Serializable;

/**
 * The user object contains an id, username, password, and type
 * along with getter and setter methods.
 * @author Connor Yoshimoto
 *
 */
public class User implements Serializable {

	// Generated serialID
	private static final long serialVersionUID = 997525634390160109L;
	private int id;
	private String username;
	private String password;
	private String type;

	/**
	 * An empty constructor that does not assign any parameters.
	 */
	public User() {

	}

	/**
	 * Create a user object.
	 * @param username - The name of the user.
	 * @param password - The user's password.
	 * @param type - The user's type.
	 */
	public User(String username, String password, String type) {
		super();
		this.username = username;
		this.password = password;
		this.type = type;
	}

	/**
	 * Prints out the user's username, password, type, and id.
	 */
	@Override
	public String toString() {
		return ("Username: " + username + " Password: " + password + " Type: "
				+ type + " ID: " + id);
	}

	// Only Getters and Setters below here
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
