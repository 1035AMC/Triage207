package com.example.triage;

import java.util.ArrayList;
import loginDatabase.User;
import suplementary.Patient;
import suplementary.PatientAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This class contains a list of all the patients that have ever visited the ER.
 * It also contains a search function by health card number.
 * @author Connor Yoshimoto
 */
public class AllPatients extends Activity {
	/**
	 * ListView of all Patients
	 */
	private ListView listView;
	/**
	 *  List of all Patients
	 */
	private ArrayList<Patient> patientList = new ArrayList<Patient>();
	/**
	 * Search input
	 */
	private EditText inputSearch;
	/**
	 * Adapter for all patients
	 */
	// Custom adapter for the listView displaying all patients
	private PatientAdapter allPatients;
	/**
	 * AsyncTask to load the files
	 */
	private ReadFilesTask rf = new ReadFilesTask(this);
	/**
	 * Instance of the patient databse
	 */
	PatientSQLHelper pDb = new PatientSQLHelper(this);
	// Current user
	private User user;

	/**
	 * Generate layout, read in patient list from file, create ListView and add to layout
	 * Get the current user and add back button if appropriate.
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_patients);
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra("userKey");
		listView = (ListView) findViewById(R.id.allPatientList);
		rf.execute(null, null, null);

		createSearch();
		if (user.getType().equals("nurse")) {
			addBackButton();
		}
	}

	/**	 
	 *  Creates the menubar at the top of the app. 
	 * @param Menu Contains the menu layout
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_patients, menu);
		return true;
	}

	/**
	 * If the user is a nurse, move back to the NurseMenu
	 * otherwise this back button is not displayed.
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
		// On back button press, move to NurseTriage with user as an extra.
		Intent moveToNurseTriage = new Intent(this, NurseTriage.class);
		moveToNurseTriage.putExtra("userKey", user);
		startActivity(moveToNurseTriage);
		return super.onOptionsItemSelected(item);
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
		if (user.getType().equals("nurse")) {
			Intent moveToNurseTriage = new Intent(this, NurseTriage.class);
			moveToNurseTriage.putExtra("userKey", user);
			startActivity(moveToNurseTriage);
		}
	}

	/**
	 * 
	 * @author arturomenacruz
	 *Handles reading and creating patients from the file through an asynchronous process.
	 *When we execute the AsyncTask it will add all the patients to a database.
	 */
	private class ReadFilesTask extends AsyncTask<Void, Void, Void> {
		Context context;

		public ReadFilesTask(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... args) {
			// TODO Auto-generated method stub

			// Get files from res
			// Create InputStream with inputFile, "raw" folder
			pDb.fill_database();
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			patientList = pDb.getAllPatients();
			allPatients = new PatientAdapter(context, patientList);
			listView.setAdapter(allPatients);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					// ListView Clicked item value
					Patient itemValue = (Patient) listView
							.getItemAtPosition(position);

					// Move to Specific Patient Info
					// The intent contains the patient object as an extra
					Intent moveToPatientInfo = new Intent(AllPatients.this,
							ViewPatientInfo.class);
					moveToPatientInfo.putExtra("patientKey", itemValue);
					moveToPatientInfo.putExtra("userKey", user);

					startActivity(moveToPatientInfo);

				}
			});

		}

	}

	/**
	 * Updates the fields declared at the beginning of this class by getting the intents.
	 */
	public void getIntentExtras() {
		Intent intent = getIntent();
		try {
			user = (User) intent.getSerializableExtra("userKey");
			Log.d("TRIAGE.AllPatients", "Have User Type, = " + user.getType());
		} catch (Exception e) {
			Log.e("TRIAGE.AllPatients", "Error getting user extra = ");
		}
	}

	/**
	 * Creates an editText and assigns a listener to it
	 * The listener tracks what is inputed and updates the listview
	 * to show only patients with health card numbers that
	 * corrospond to the entered text.
	 */
	public void createSearch() {
		// Creating an edittext
		inputSearch = (EditText) findViewById(R.id.listSearch);
		// Adding listener to edittext
		inputSearch.addTextChangedListener(new TextWatcher() {

			/**
			 * @param cs the text currently entered in the search bar
			 * 
			 * @param arg1 Temporary tracking int - Does nothing
			 * @param arg2 Temporary tracking int - Does nothing
			 * @param arg3 Temporary tracking int - Does nothing
			 * Filters the text in the list by text currently in the searchbar
			 */
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changes the Text filter list by said search
				AllPatients.this.allPatients.getFilter().filter(cs);
			}

			/**
			 * @param arg0 the text currently entered in the search bar
			 * @param arg1 Temporary tracking int - unused
			 * @param arg2 Temporary tracking int - unused
			 * @param arg3 Temporary tracking int - unused
			 * Required method to create a TextWatcher() but does nothing
			 */
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			/**
			 * @param arg0 For editable text
			 * Required method to create a TextWatcher() but does nothing
			 */
			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

}
