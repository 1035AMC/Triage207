package com.example.triage;

import java.util.ArrayList;
import java.util.Collections;

import loginDatabase.User;
import suplementary.Patient;
import suplementary.UrgencyAdapter;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * This class handles the creation of the fragment containing
 * a listView of patients sorted by their urgency.
 * @author Connor Yoshimoto, Priyank Purohit
 *
 */
public class UrgencyPatientListFragment extends Fragment {
	/**
	 * A listView that displays an ArrayList of patients, sorted by urgency.
	 */
	private ListView listView;
	/**
	 * An ArrayList of patients, sorted by urgency.
	 */
	private ArrayList<Patient> patientList = new ArrayList<Patient>();
	/**
	 * A custom display adapter. 
	 */
	private UrgencyAdapter urgencyAdapter;
	/**
	 * The view that we are creating the fragment in.
	 */
	private View rootView;
	/**
	 * A database containing the patients
	 */
	private PatientSQLHelper patientDb;
	/**
	 * The user currently logged on to the program
	 */
	private User user;
	/**
	 * An async task to read in files
	 */
	private ReadFilesTask rf;

	/**
	 * This method creates the needed database and then adds the patients sorted by
	 * their urgency to the listView and then displays said listView.
	 * @return rootView - The view that is to be displayed
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_main_tab_urgency,
				container, false);
		patientDb = new PatientSQLHelper(getActivity());
		user = (User) getArguments().getSerializable("userKey");
		rf = new ReadFilesTask(getActivity());
		rf.execute(null, null, null);
		return rootView;
	}

	/**
	 * 
	 * @author arturomenacruz
	 *Handles reading from the database and loading all the patients that are in the Er into a list view.
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
			patientList.clear();
			patientDb.fill_database();
			ArrayList<Patient> temp = patientDb.getAllPatients();
			for (int i = 0; i < temp.size(); i++) {
				Patient p = temp.get(i);
				if (p.isInER()) {
					Log.d("ArrivalTime", p.calculateUrgency() + "");
					patientList.add(p);
				}
				Collections.sort(patientList);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			listView = (ListView) rootView
					.findViewById(R.id.urgencyPatientList);
			urgencyAdapter = new UrgencyAdapter(context, patientList);
			listView.setAdapter(urgencyAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					// ListView Clicked item value
					Patient itemValue = (Patient) listView
							.getItemAtPosition(position);

					// Move to Specific Patient Info
					// The intent contains the patient object as an extra
					Intent moveToPatientInfo = new Intent(getActivity(),
							ViewPatientInfo.class);
					moveToPatientInfo.putExtra("patientKey", itemValue);
					moveToPatientInfo.putExtra("userKey", user);

					startActivity(moveToPatientInfo);
				}
			});
		}
	}
}
