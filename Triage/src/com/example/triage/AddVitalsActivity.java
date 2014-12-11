package com.example.triage;

import loginDatabase.User;
import suplementary.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.util.*;
import android.widget.*;

/**
 * Back-end for the Add Vitals Activity. Handles the logic behind getting the
 * data from UI and saving it. Blank input is saved as a value of -99.
 * 
 * @author Priyank Purohit
 * @since 19-11-2014
 */
public class AddVitalsActivity extends Activity {
	/**
	 * Declares a patient object for the current user.
	 */
	private Patient patient = null;

	/**
	 * Declares a File Manager to read/write to/from.
	 */
	private PatientSQLHelper patientDb = new PatientSQLHelper(this);

	/**
	 * Declare a User object to perform functions based on the user type (nurse
	 * | physician).
	 */
	private User user;

	/**
	 * Creates a visit object for the visit that user came from.
	 */
	private Visit visit = null;

	/**
	 * Intent to move on to ViewVitals activity.
	 */
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_vitals);
		addBackButton();

		getIntentExtras();

		updateCurrentTime();
		sympToXML();
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
	 * Creates the Action Bar.
	 * 
	 * @param menu
	 *            A menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_patient_info, menu);
		setTitle("New Vitals for " + patient.getName());
		return true;
	}

	/**
	 * @param item
	 *            A menu item Creates the Action Bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent moveToViewVitals = new Intent(this, ViewVitalsActivity.class);

		moveToViewVitals.putExtra("patientKey", patient);
		moveToViewVitals.putExtra("visitKey", visit);
		moveToViewVitals.putExtra("userKey", user);

		startActivity(moveToViewVitals);

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Gets Patient as Serializable extra and creates the patient object.
	 */
	private void getIntentExtras() {
		intent = getIntent();
		try {
			patient = (Patient) intent.getSerializableExtra("patientKey");
			visit = (Visit) intent.getSerializableExtra("visitKey");
			user = (User) intent.getSerializableExtra("userKey");
		} catch (Exception e) {
			Log.e("TRIAGE.AddVitals",
					"Error: AddVitals did not get patient from ViewVitals");
		}
	}

	/**
	 * Updates the current time at the start of activity. Gives the nurses an
	 * approximate time that the app will record when (s)he saves the vitals.
	 */
	private void updateCurrentTime() {
		TextView patientName = (TextView) findViewById(R.id.date);
		patientName.setText(Time.getDateTime()[0]
				+ "/"
				+ Time.getDateTime()[1]
				+ "/"
				+ Time.getDateTime()[2]
				+ " @ "
				+ ViewVitalsActivity.timeToString(Time.getDateTime()[3],
						Time.getDateTime()[4]));
	}

	/**
	 * Gets the input from UI. Checks if the patient is in ER. If the patient is
	 * in ER, adds the vitals to the LAST visit if seenDoc from that last visit
	 * is still false. If any of these conditions are not met, it will not save
	 * any values.
	 * 
	 * @param view
	 *            ?????????????????????????????????????????
	 */
	public void saveVitalsToPatient(View view) {
		// IF THE USERTYPE IS NURSE.
		// if (usertype.getType() == "nurse")

		Intent moveToViewVitals = new Intent(this, ViewVitalsActivity.class);

		// First get all data from XML.
		boolean seenDoctor = seenDoctorFromXML();
		int heartRate = hrFromXML();
		int bpSys = bpSysFromXML();
		int bpDia = bpDiaFromXML();
		double temp = tempFromXML();
		String symp = sympFromXML();

		// Calculates the index of last visit in Patient's ArrayList of Vital.
		int lastVisitIndex = patient.getVisit().size() - 1;

		/*
		 * Iff Vitals are not all blank, AND iff patient in ER, add to last
		 * visit (last visit => current visit).
		 */
		if ((heartRate >= -90 || bpSys >= -90 || bpDia >= -90 || temp >= -90.0
				|| symp != "" || seenDoctor)) {
			// Add Vitals.
			patient.getVisit().get(lastVisitIndex)
					.addVitals(temp, heartRate, bpSys, bpDia, symp);

			// If UI check box is true, patient is out of ER, has seen doctor.
			if (seenDoctor) {
				patient.setInER(false);
				patient.getVisit().get(lastVisitIndex).setSeenDoc(true);
				Log.d("TRIAGE.AddVitals",
						"Visit SeenDoctor ER: FALSE, DOC: TRUE.");
			}

			moveToViewVitals.putExtra("visitKey",
					patient.getVisit().get(lastVisitIndex));

			try {
				patientDb.updatePatient(patient);
			} catch (Exception e) {
				Log.d("TRIAGE.AddVitals", "Error Saving data.");
			}
		} else {
			Log.d("TRIAGE", "Either all blank OR not in er OR seen doctor.");
			moveToViewVitals.putExtra("visitKey", visit);
		}

		moveToViewVitals.putExtra("patientKey", patient);
		moveToViewVitals.putExtra("userKey", user);

		startActivity(moveToViewVitals);
	}

	/**
	 * Gets the status of the Seen Doctor check box from UI.
	 * 
	 * @return True iff the seenDoc check box is checked off.
	 */
	private boolean seenDoctorFromXML() {
		return ((CheckBox) findViewById(R.id.seenDoc)).isChecked();
	}

	/**
	 * Gets the value of temperature from its EditText in UI.
	 * 
	 * @return The value of temp if it exists. If the EditText is blank, return
	 *         -99.0.
	 */
	private double tempFromXML() {
		double temp = -99.0;
		String tempXML = (((EditText) findViewById(R.id.editTextTemp))
				.getText()).toString();

		// Make sure you have a value. If not, return -99.0.
		if (tempXML == null || tempXML.isEmpty()) {
			temp = -99.0;
		} else {
			temp = Double.parseDouble(tempXML);
		}

		return temp;
	}

	/**
	 * Gets the value of heart rate from its EditText in UI.
	 * 
	 * @return The value of HR if it exists. If the EditText is blank, return
	 *         -99.
	 */
	private int hrFromXML() {
		int hr = -99;
		String hrXML = (((EditText) findViewById(R.id.editTextHR)).getText())
				.toString();

		// Make sure you have a value. If not, return -99.
		if (hrXML == null || hrXML.isEmpty()) {
			hr = -99;
		} else {
			hr = Integer.parseInt(hrXML);
		}

		return hr;
	}

	/**
	 * Gets the value of systolic blood pressure from its EditText in UI.
	 * 
	 * @return The value of bpSys if it exists. If the EditText is blank, return
	 *         -99.
	 */
	private int bpSysFromXML() {
		int bpSys = -99;
		String bpSysXML = (((EditText) findViewById(R.id.editTextBPSys))
				.getText()).toString();

		// Make sure you have a value. If not, return -99.
		if (bpSysXML == null || bpSysXML.isEmpty()) {
			bpSys = -99;
		} else {
			bpSys = Integer.parseInt(bpSysXML);
		}

		return bpSys;
	}

	/**
	 * Gets the value of diastolic blood pressure from its EditText in UI.
	 * 
	 * @return The value of bpDia if it exists. If the EditText is blank, return
	 *         -99.
	 */
	private int bpDiaFromXML() {
		int bpDia = -99;
		String bpDiaXML = (((EditText) findViewById(R.id.editTextBPDia))
				.getText()).toString();

		// Make sure you have a value. If not, return -99.
		if (bpDiaXML == null || bpDiaXML.isEmpty()) {
			bpDia = -99;
		} else {
			bpDia = Integer.parseInt(bpDiaXML);
		}

		return bpDia;
	}

	/**
	 * Gets the string value of symptoms from its EditText in UI.
	 * 
	 * @return The value of symptoms if it exists, blank string otherwise.
	 */
	private String sympFromXML() {
		String symp = "";

		symp += (((EditText) findViewById(R.id.editTextSymp)).getText())
				.toString();

		return symp;
	}

	/**
	 * Pre-fills the symptoms in the UI.
	 */
	private void sympToXML() {
		String symp = "";
		int numOfVitals = visit.getVitals().size();
		symp += visit.getVitals().get(numOfVitals - 1).getSymp();

		TextView sympXML = (TextView) findViewById(R.id.editTextSymp);

		sympXML.setText(symp);
	}
}
