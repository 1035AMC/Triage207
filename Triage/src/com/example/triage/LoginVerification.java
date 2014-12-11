package com.example.triage;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import loginDatabase.MySQLiteHelper;
import loginDatabase.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Connor Yoshimoto 
 * This class verifies the inputed login information
 * against the users stored within the database. For this assignment,
 * the database is loaded from the textfile passwords.txt located in
 * res/raw.
 */
public class LoginVerification extends Activity {
	/**
	 * An arraylist containing all of the users of this program.
	 */
	private ArrayList<User> userList = new ArrayList<User>();
	/**
	 * A database containing all information relating to the users
	 * of this program.
	 */
	private static MySQLiteHelper loginDatabase;
	/**
	 * The name of the database.
	 */
	private static final String databaseName = "Users";

	/**
	 * Create the database, read password.txt and populate the database
	 * @param saveInstanceState - bundle contained the saved state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_verification);

		// Get intent extra from Login Screen
		Bundle bundle = getIntent().getExtras();
		// Get inputed loginInfo array from bundle
		String[] loginInfo = bundle.getStringArray("info");
		Log.d("userDBReturn", "Login Info - Username: " + loginInfo[0]
				+ " Password: " + loginInfo[1]);
		loginDatabase = new MySQLiteHelper(this);
		// If the database does not exist (first run) then populate it
		if (!doesDatabaseExist(getApplicationContext(), databaseName)) {
			Log.d("singleDB", "Database does not exist");
			// Create login database
			Log.d("userDB", "creating DB");
			// Read in from password.txt
			readFile();
			// Populate database with users
			populate();
		}

		// if checking the login info causes a runtime error (User not found)
		// Then deny the login and return to the login screen
		try {
			checkLogin(loginInfo);
		} catch (RuntimeException e) {
			Log.d("userDBReturn", "Runtime error");
			Log.d("userDBReturn", "Login Failed");
			Intent loginFail = new Intent(this, Login.class);
			startActivity(loginFail);
		}
	}

	/**
	 * Checks the login information if the program onResume.
	 * @param savedInstanceState - saved state Return to login screen on activity resume.       
	 */
	protected void onResume(Bundle savedInstanceState) {
		try {
			// Get intent extra from Login Screen
			Bundle bundle = getIntent().getExtras();
			// Get inputed loginInfo array from bundle
			String[] loginInfo = bundle.getStringArray("info");
			Log.d("userDBReturn", "Login Info - Username: " + loginInfo[0]
					+ " Password: " + loginInfo[1]);
			checkLogin(loginInfo);
			// If login is not in database, catch RuntimeException
			// and return to login screen.
		} catch (RuntimeException e) {
			Log.d("userDBReturn", "Login Failed");
			Intent loginFail = new Intent(this, Login.class);
			startActivity(loginFail);
			// If intent is missing or other errors, return to login screen.
		} catch (Exception e) {
			Log.d("userDBReturn", "Login Broke");
			Intent loginFail = new Intent(this, Login.class);
			startActivity(loginFail);
		}

	}

	/**
	 * Auto generated method Does nothing
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_verification, menu);
		return true;
	}

	/**
	 * Auto generated method Does nothing
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Disables back button on this screen.
	 */
	@Override
	public void onBackPressed() {
		// Empty method
	}

	/**
	 * Read in password.txt from res/raw and store the information in an
	 * arrayList<user>
	 */
	private void readFile() {
		Log.d("userDB", "Entered File Reader");
		try {
			// Get files from res
			// Create InputStream with inputFile, "raw" folder
			// get all the patient, the visits to the hospital, and the vitals
			String[] input_file;
			// scan the patient file and get all of them
			Log.d("userDB", "Creating scanner");
			Scanner scanner = new Scanner(getResources().openRawResource(
					getResources().getIdentifier("passwords", "raw",
							getPackageName())));
			Log.d("userDB", "Scanning lines");
			while (scanner.hasNextLine()) {
				input_file = scanner.nextLine().split(",");
				// Log.d("userDB", "input_file: " + input_file[0] + " | " +
				// input_file[1] + " | " + input_file[2]);
				userList.add(new User(input_file[0], input_file[1],
						input_file[2]));
			}
			Log.d("userDB", "Scanner Close");
			scanner.close();
			// Close stream and reader

			// Print to LogCat if reading the file breaks.
		} catch (Exception e) {
			Log.e("userDB", "File Reading broke it");
		}
	}

	/**
	 * Populate the database with the information in userList.
	 */
	private void populate() {
		for (User element : userList) {
			loginDatabase.addUser(element);
		}
	}

	/**
	 * Compares the entered username and password against the database.
	 * If they match, move to the approriate screen for the user type.
	 * If they fail to match, return to the login screen.
	 * @param info - The entered login information
	 * @throws RuntimeException - If the username is not found in the database.
	 */
	private void checkLogin(String[] info) throws RuntimeException {

		// Log.d("singleDB", loginDatabase.getAllUsers().toString());
		User user = loginDatabase.getUser(info[0]);
		// Log.d("userDB", "From DB | Username: " + user.getUsername() +
		// " Password: " + user.getPassword() + " Type: " + user.getType());
		// Log.d("userDB", "From info | Username " + info[0] + " Password: " +
		// info[1]);
		if (user.getPassword().equals(info[1])) {
			Log.d("userDB", "Login Successful");

			if (user.getType().equals("nurse")) {
				Log.d("userDB", "Nurse logged on");
				Intent nurse = new Intent(this, NurseTriage.class);
				nurse.putExtra("userKey", user);
				startActivity(nurse);
			} else if (user.getType().equals("physician")) {
				Log.d("userDB", "physician logged on");
				Intent physician = new Intent(this, AllPatients.class);
				physician.putExtra("userKey", user);
				startActivity(physician);
			}

		} else {
			Log.d("userDBReturn", "Login Failed");
			Intent loginFail = new Intent(this, Login.class);
			startActivity(loginFail);
		}

	}

	/**
	 * Checks to see if a database with the given name exists.
	 * @param context - The Application context.
	 * @param dbName - The database you are trying to check.
	 * @return True if the database exists, otherwise false.
	 */
	private static boolean doesDatabaseExist(Context context, String dbName) {
		File dbFile = context.getDatabasePath(dbName);
		Log.d("singleDB", context.getDatabasePath(dbName).toString());
		return dbFile.exists();
	}

}
