package com.example.triage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * This class automatically redirects to the login screen
 * @author Connor Yoshimoto
 *
 */
public class MainActivity extends Activity {

	/**
	 * @param savedInstanceState
	 * Move to login screen immediately.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		moveToLogin();
	}

	/**
	 * Move to the Login Activity
	 */
	public void moveToLogin() {
		Log.d("mainFragment", "moveToLogin");
		Intent toLogin = new Intent(this, Login.class);
		startActivity(toLogin);
	}

}
