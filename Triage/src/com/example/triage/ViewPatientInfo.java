package com.example.triage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import suplementary.*;
import loginDatabase.User;
import java.util.ArrayList;

//import java.util.Iterator;

/**
 * ViewPatientInfo views a Patient's Visits and creates a populates a ListView
 * of Visits, which will be be sent to ViewVitalsActivity.
 * 
 * @author Brandon Pon
 * 
 */

public class ViewPatientInfo extends Activity {

	/**
	 * ListView for list of visits.
	 */
	private ListView listView;

	/**
	 * ArrayList to iterate for visits.
	 */
	private ArrayList<Visit> visitList = new ArrayList<Visit>();

	/**
	 * PatientDatabase object initialization.
	 */
	private PatientSQLHelper patientDb = new PatientSQLHelper(this);

	/**
	 * Temporary patient object for intent.
	 */
	private Patient patient = null;

	/**
	 * ArrayList for visits.
	 */
	private ArrayList<Visit> visits = new ArrayList<Visit>();

	/**
	 * VisitAdapter for ListView
	 */
	private VisitAdapter visitView;

	private User user;

	/**
	 * @param savedInstanceState
	 *            a bundle When activity is moved to and created, creates Action
	 *            Bar and calls readData, createList, and addBackButton methods.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("onCreate", "onCreate is called");
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_view_patient_info);
		// readData();
		// createList();
		// addBackButton();
	}

	/**
	 * @param Menu
	 *            a menu contains the menu layout Used for when a menu item is
	 *            selected. When the back button is pressed, return to
	 *            mainActivity.
	 */
	@Override
	protected void onResume() {
		Log.d("onResume", "onResume is called");
		super.onResume();
		setContentView(R.layout.activity_view_patient_info);
		readData();
		createList();
		addBackButton();
		Log.d("ViewPatientInfo", patient.calculateUrgency() + "");

		Button btn = (Button) findViewById(R.id.moveToVitals);

		if (user.getType().equals("physician")) {
			btn.setEnabled(false);
			btn.setVisibility(View.INVISIBLE);
		} else {
			btn.setEnabled(true);
			btn.setVisibility(View.VISIBLE);
		}

		TextView basic_info = (TextView) findViewById(R.id.basic_info);
		basic_info.setText("Date of Birth: " + patient.getBirthdayForDisplay()
				+ "\nHealthcard #: " + patient.getHealthCard());
	}

	/**
	 * @param menu
	 *            a menu Creates the Action Bar.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_patient_info, menu);
		setTitle(patient.getName() + "'s Visits");
		return true;
	}

	/**
	 * @param item
	 *            a menu item Creates the Action Bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		Intent moveToAllPatients = new Intent(this, AllPatients.class);
		moveToAllPatients.putExtra("userKey", user);
		startActivity(moveToAllPatients);
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Reads passed Patient Intent from AllPatients activity.
	 * 
	 */
	public void readData() {
		Intent intent = getIntent();
		try {
			patient = (Patient) intent.getSerializableExtra("patientKey");
			visits = patientDb.getPatient(patient.getHealthCard()).getVisit();
			// user = (User) intent.getSerializableExtra("userKey");
			// Log.d("TRIAGE.ViewPatientInfo","User intent extra is: " +
			// user.getType());
			Log.d("TRIAGE.ViewPatientInfo",
					"PATIENT RECEIVED is: " + patient.toString()
							+ " with visit count = "
							+ patient.getVisit().size());
			// Re-read the patient
			patient = patientDb.getPatient(patient.getHealthCard());

			user = (User) intent.getSerializableExtra("userKey");
			Log.d("TRIAGE.ViewPatientInfo",
					"PATIENT RE-READ is: " + patient.toString()
							+ " with visit count = "
							+ patient.getVisit().size());
		} catch (Exception e) {
			Log.e("TRIAGE", "Error reading patient data, doesn't exist?");
		}

		try {
			user = (User) intent.getSerializableExtra("userKey");
			Log.d("TRIAGE.ViewPatientInfo",
					"User intent extra is: " + user.getType());
		} catch (Exception e) {
			Log.e("TRIAGE.ViewPatientInfo", "Error getting user intent extra");
		}
		visitList.clear();
		for (Visit v : visits) {
			Log.d("", "entered for loop");
			visitList.add(v);

		}
		Log.d("checking values", "size:" + visitList.size());
	}

	/**
	 * Changes activity to ViewVitalsActivity, creates a new visit
	 */
	public void moveToVitals(View view) {
		Intent moveToVitals = new Intent(this, ViewVitalsActivity.class);

		patient.newVisit();
		patient.setInER(true);

		patientDb.updatePatient(patient);
		patient = patientDb.getPatient(patient.getHealthCard());

		// pass last visit, patient, and user
		moveToVitals.putExtra("visitKey",
				patient.getVisit().get(patient.getVisit().size() - 1));
		moveToVitals.putExtra("patientKey", patient);
		moveToVitals.putExtra("userKey", user);

		startActivity(moveToVitals);
	}

	/**
	 * Creates a back button to go back to a specific activity defined in
	 * onOptionsItemSelected()
	 */
	private void addBackButton() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 
	 * Give a ListView a custom adapter and goes to activity ViewsVitalActivity
	 * when an item is clicked, sending a Patient object and Visit as intent.
	 * 
	 */
	public void createList() {
		// Create listView
		listView = (ListView) findViewById(R.id.VisitList);

		// Using custom adapter to show visit dates
		visitView = new VisitAdapter(this, visitList);
		listView.setAdapter(visitView);

		// For moving from the listView to a particular visit
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// ListView Clicked item value
				Visit itemValue = (Visit) listView.getItemAtPosition(position);

				try {
					Intent moveToVitals = new Intent(ViewPatientInfo.this,
							ViewVitalsActivity.class);

					// Pass the visit in question, patient, and user.
					moveToVitals.putExtra("visitKey", itemValue);
					moveToVitals.putExtra("patientKey", patient);
					Log.d("TRIAGE.ViewPatientInfo", "PATIENT PASSED ON is: "
							+ patient.toString() + " with visit count = "
							+ patient.getVisit().size());
					moveToVitals.putExtra("userKey", user);

					startActivity(moveToVitals);
				} catch (Exception e) {
					Log.e("TRIAGE", "Problem with putExtra");
				}
			}
		});
	}
}
