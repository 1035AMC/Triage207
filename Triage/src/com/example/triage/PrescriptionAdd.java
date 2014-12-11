package com.example.triage;

import loginDatabase.User;
import android.app.ActionBar;
import android.app.Activity;
import suplementary.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.util.*;
import android.widget.*;

/**
 * 
 * @author Brandon Pon PrescriptionAdd is used by physicians to add
 *         prescriptions to patient's visits. It cannot be used by Nurses.
 */

public class PrescriptionAdd extends Activity {
	/**
	 * File Manager object
	 */
	private PatientSQLHelper patientDb = new PatientSQLHelper(this);
	/**
	 * Intent object
	 */
	private Intent intent;
	/**
	 * Patient object
	 */
	private Patient patient;
	/**
	 * User object
	 */
	private User user;
	/**
	 * Visit object
	 */
	private Visit visit;

	/**
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prescription_add);
		// intent = getIntent();
		// patient = (Patient) intent.getSerializableExtra("patient");
		getIntentExtras();
		addBackButton();
		preFillLastPrescription();
	}

	/**
	 * @param menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		setTitle(patient.getName() + "'s Prescription");
		return true;
	}

	/**
	 * @param item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Intent moveToPatientInfo = new Intent(this, ViewVitalsActivity.class);
		moveToPatientInfo.putExtra("patientKey", patient);
		moveToPatientInfo.putExtra("visitKey", visit);
		moveToPatientInfo.putExtra("userKey", user);
		startActivity(moveToPatientInfo);
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Reads the XML editText medicationText
	 * 
	 * @return meds String object of medications.
	 */
	private String prescriptionFromXML() {
		String meds = "";
		meds += (((EditText) findViewById(R.id.medicationText)).getText())
				.toString();
		return meds;
	}

	/**
	 * Gets Patient as Serializable extra and creates the patient object.
	 */
	private void getIntentExtras() {
		intent = getIntent();
		patient = (Patient) intent.getSerializableExtra("patientKey");
		visit = (Visit) intent.getSerializableExtra("visitKey");
		user = (User) intent.getSerializableExtra("userKey");
		// try {
		//
		// patient = (Patient) intent.getSerializableExtra("patientKey");
		// // user = (User) intent.getSerializableExtra("userKey");
		// } catch (Exception e) {
		// Log.e("TRIAGE.PrescriptionAdd",
		// "Error: PrescriptionAdd did not get patient from ViewVitals");
		// }
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
	 * @param view
	 *            Saves the prescription as the patient's last visit.
	 */
	public void savePrescription(View view) {
		int lastVisitIndex = 0;
		patient = (Patient) intent.getSerializableExtra("patientKey");
		Log.d("PrescriptionAdd", "patient.getVisit().size= "
				+ patient.getVisit().size());
		if (patient.getVisit().size() > 1) {
			lastVisitIndex = patient.getVisit().size() - 1;
		}

		String medications = prescriptionFromXML();

		// Save only if the prescription is not blank.
		if (!medications.equals("")) {
			patient.getVisit().get(lastVisitIndex).addPrescription(medications);
		}

		patientDb.updatePatient(patient);

		Intent moveToViewVitals = new Intent(this, ViewVitalsActivity.class);
		moveToViewVitals.putExtra("visitKey",
				patient.getVisit().get(patient.getVisit().size() - 1));
		moveToViewVitals.putExtra("patientKey", patient);
		moveToViewVitals.putExtra("userKey", user);
		Log.d("PrescriptionAdd", "Passing user " + user.getId()
				+ " correctly to ViewVitalsActivity");

		startActivity(moveToViewVitals);
	}

	/**
	 * Pre-fills the symptoms in the UI.
	 */
	private void preFillLastPrescription() {
		String meds = "";
		int numOfPrescriptions = visit.getPrescriptions().size();

		if (numOfPrescriptions > 0)
			meds += visit.getPrescriptions().get(numOfPrescriptions - 1)
					.getMedication();
		else
			meds += "Medication:\n" + "Instructions:";

		TextView prescriptionXML = (TextView) findViewById(R.id.medicationText);

		prescriptionXML.setText(meds);

	}
}
