package com.example.triage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * @author Connor Yoshimoto
 * This class contains the back-end code for the login GUI.
 * It gets the information from the input fields and sends them
 * to be verified against the database of users.
 */
public class Login extends Activity {
	/**
	 * String[] containing username and password to be checked
	 */
	private String[] loginInfo;
	/**
	 *  EditText field for user input
	 */
	private EditText username;
	/**
	 *  EditText field for user input
	 */
	private EditText password;

	/**
	 * Creates the layout for the screen.
	 * Gets the strings entered in the username and password field.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// Array size of 2 for username and password
		loginInfo = new String[2];
		// Get username and password from the edittexts.
		username = (EditText) findViewById(R.id.usernameInput);
		password = (EditText) findViewById(R.id.passwordInput);
	}

	/**
	 * Auto generated method
	 * Does nothing
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Auto generated method.
	 * Does nothing
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
	 * 
	 *  Gets the username and password entered and puts
	 *  them in a String array. 
	 *  Passes this String array as part of the intent to be verified
	 *  in loginVerification.
	 *  @param view -  current view
	 */
	public void login(View view) {
		// Get strings from EditTexts
		loginInfo[0] = username.getText().toString();
		loginInfo[1] = password.getText().toString();
		Log.d("userDB", "Login info:|" + loginInfo[0] + "|" + loginInfo[1]);

		// Create a bundle and add the login info as an extra
		Bundle bundle = new Bundle();
		bundle.putStringArray("info",
				new String[] { loginInfo[0], loginInfo[1] });
		Intent login = new Intent(this, LoginVerification.class);
		login.putExtras(bundle);
		startActivity(login);
	}
}
