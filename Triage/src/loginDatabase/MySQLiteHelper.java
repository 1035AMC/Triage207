package loginDatabase;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is the SQL database manager. It creates the database then adds a
 * table which contains all of the user information.
 * @author Connor Yoshimoto
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

	/**
	 *  Database Version
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 *  Database Name
	 */
	private static final String DATABASE_NAME = "Users";

	/**
	 *  A table called users
	 */
	private static final String TABLE_USERS = "users";

	/**
	 *  The column named id for the users table 
	 */
	private static final String KEY_ID = "id";
	/**
	 *  The column named username for the users table 
	 */
	private static final String KEY_USERNAME = "username";
	/**
	 *  The column named password for the users table 
	 */
	private static final String KEY_PASSWORD = "password";
	/**
	 *  The column named type for the users table 
	 */
	private static final String KEY_TYPE = "type";
	/**
	 *  A String[] containing all the columns for the users table.
	 */
	private static final String[] COLUMNS = { KEY_ID, KEY_USERNAME,
			KEY_PASSWORD, KEY_TYPE };

	/**
	 * Constructor method that creates an instance of this class.
	 * with the database name and version number.
	 * @param context
	 */
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Called when the database is created. Creates a table called "users" 
	 * which has the columns defined in "COLUMNS"
	 * @param db - The database that onCreate is called on.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create table
		String CREATE_USER_TABLE = "CREATE TABLE users ( "
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "username TEXT, "
				+ "password TEXT, " + "type TEXT )";

		// create user table
		db.execSQL(CREATE_USER_TABLE);

	}

	/**
	 * If the database is upgraded, delete the old copy of the table "users" 
	 * and create it again.
	 */
	// This method is never used.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older users table if existed
		db.execSQL("DROP TABLE IF EXISTS users");

		// create fresh user table
		this.onCreate(db);
	}

	/**
	 * Adds a user to the SQL database.
	 * @param user - the user to be added.
	 */
	public void addUser(User user) {
		Log.d("userDB", "addUser entered");

		// get writable db
		SQLiteDatabase db = this.getWritableDatabase();

		// Create ContentValues for columns
		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME, user.getUsername());
		values.put(KEY_PASSWORD, user.getPassword());
		values.put(KEY_TYPE, user.getType());

		// Add values to db
		db.insert(TABLE_USERS, null, values);

		// Close db
		db.close();

	}

	/**
	 * Get a specific user from the database by their username.
	 * @param username - The username to be found.
	 * @return User - the information associated with the given username
	 */
	public User getUser(String username) {
		Log.d("userDB", "getUser entered");
		Log.d("userDB", "Set readable db");
		// 1. get readable db
		SQLiteDatabase db = this.getReadableDatabase();
		Log.d("userDB", "build query");
		// 2. build the query
		Cursor cursor = db.query(TABLE_USERS, COLUMNS, KEY_USERNAME + "=?",
				new String[] { username }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		// Create a user object and set its values according to the database
		User user = new User();
		user.setId(Integer.parseInt(cursor.getString(0)));
		user.setUsername(cursor.getString(1));
		user.setPassword(cursor.getString(2));
		user.setType(cursor.getString(3));
		cursor.close();
		return user;

	}

	/**
	 * Returns a list of all users in the database.
	 * @return a list of all users in the database.
	 */
	public List<User> getAllUsers() {
		List<User> users = new LinkedList<User>();

		// 1. build the query
		String query = "SELECT  * FROM " + TABLE_USERS;

		// 2. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		// 3. go over each row, build user and add it to list
		User user = null;
		if (cursor.moveToFirst()) {
			do {
				user = new User();
				user.setId(Integer.parseInt(cursor.getString(0)));
				user.setUsername(cursor.getString(1));
				user.setPassword(cursor.getString(2));
				user.setType(cursor.getString(3));

				// Add user to users
				users.add(user);
			} while (cursor.moveToNext());
		}

		// return users
		return users;
	}
}
