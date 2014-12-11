package com.example.triage;

import loginDatabase.User;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Creates the nurse triage view, which contains two fragments
 * and a tab controlled to switch between them.
 * The fragments display patients in ER sorted by urgency and
 * arrival time respectively.
 * @author Connor Yoshimoto
 *
 */
public class NurseTriage extends Activity {
	// used for verify usertype and to pass intent along
	private User user;
	Bundle bundle = new Bundle();

	/**
	 * Get the intent from the previous activity and create a user with it.
	 * Create the tabbed view and the buttons.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nurse_triage);

		// Get intent
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra("userKey");
		// add tabbed view to layout
		bundle.putSerializable("userKey", user);

		addTabs();
	}

	/**
	 * Disables back button on this screen.
	 */
	@Override
	public void onBackPressed() {
		// Empty method
	}

	/**
	 * Creates the action bar and the tabbed view. Allows switching between
	 * fragments associated with these tabs
	 */
	public void addTabs() {
		Log.d("NurseTriage", "addTabs() Entered");
		// Tabs for actionbar
		ActionBar.Tab view_urgency_tab, view_arrival_time_tab;
		// Create fragments that the tabs switch between
		Log.d("NurseTriage", "Creating Fragments");
		Fragment view_urgency = new UrgencyPatientListFragment();
		view_urgency.setArguments(bundle);
		Log.d("NurseTriage", "UrgencyFragment Created");
		Fragment view_arrival_time = new ArrivalTimePatientListFragment();
		view_arrival_time.setArguments(bundle);
		Log.d("NurseTriage", "ArrivalTimeFragment Created");

		// Get Actionbar and set navigation mode to TABS
		ActionBar actionBar1 = getActionBar();
		actionBar1.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create tabs
		view_urgency_tab = actionBar1.newTab().setText("Sorted by Urgency");
		view_arrival_time_tab = actionBar1.newTab().setText(
				"Sorted by Arrival Time");

		// Create tab listener
		view_urgency_tab.setTabListener(new MyTabListener(view_urgency));
		view_arrival_time_tab.setTabListener(new MyTabListener(
				view_arrival_time));

		// Add tabs to action bar
		actionBar1.addTab(view_urgency_tab);
		actionBar1.addTab(view_arrival_time_tab);
	}

	/**
	 * On button press, move to the create new patient screen.
	 * @param view - Current view
	 */
	public void moveToNewPatients(View view) {
		// Create intent
		Intent toNewPatients = new Intent(this, NewPatient.class);
		// Add user as extra
		toNewPatients.putExtra("userKey", user);
		startActivity(toNewPatients);
	}

	/**
	 * On button press, move to allPatientList.
	 * @param view - current view
	 */
	public void moveToAllPatients(View view) {
		// Create intent
		Intent toAllPatientList = new Intent(this, AllPatients.class);
		// Add user as extra
		toAllPatientList.putExtra("userKey", user);
		startActivity(toAllPatientList);
	}
}
