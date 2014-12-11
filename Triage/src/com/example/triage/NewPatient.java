package com.example.triage;

import java.util.Calendar;

import loginDatabase.User;
import suplementary.Patient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewPatient extends Activity implements
		DatePickerFragment.DatePickerListener {
	/**
	 * Birthday of the patient as entered by the user
	 */
	Calendar birthday;
	/**
	 * Name of the patient
	 */
	String name;
	/**
	 * Health card number of the patient
	 */
	String healthcard;
	/**
	 * Database of all patients
	 */
	PatientSQLHelper patientDb;
	/**
	 * Instance of the user that opened this activity
	 */
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_patient);
		patientDb = new PatientSQLHelper(this);
		user = (User) getIntent().getSerializableExtra("userKey");
		addBackButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_patient, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		// On back button press, move to NurseTriage with user as an extra.
		Intent moveToNurseTriage = new Intent(this, NurseTriage.class);
		moveToNurseTriage.putExtra("userKey", user);
		startActivity(moveToNurseTriage);
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Display the Dialog
	 * @param v View that called this listener
	 */
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	@Override
	public void returnDate(Calendar c) {
		birthday = c;
	}

	/**
	 * Add Patient to the database if the information about the patient was valid
	 * @param v View that called this listener
	 */
	public void addPatient(View v) {
		name = (((EditText) findViewById(R.id.name)).getText()).toString();
		healthcard = (((EditText) findViewById(R.id.healthcard)).getText())
				.toString();
		if (birthday != null && name != null && healthcard != null
				&& name.split(" ").length > 1
				&& !birthday.after(Calendar.getInstance())) {
			patientDb.createEntry(new Patient(name, birthday, healthcard));
			Intent intent = new Intent(this, AllPatients.class);
			intent.putExtra("userKey", user);
			startActivity(intent);
		} else {
			Toast.makeText(this, "Input is not valid", Toast.LENGTH_LONG)
					.show();
		}
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
	 * Disables back button on this screen for physicians.
	 * Moves back to NurseTriage for nurses.
	 */
	@Override
	public void onBackPressed() {
		Intent moveToNurseTriage = new Intent(this, NurseTriage.class);
		moveToNurseTriage.putExtra("userKey", user);
		startActivity(moveToNurseTriage);
	}

}
