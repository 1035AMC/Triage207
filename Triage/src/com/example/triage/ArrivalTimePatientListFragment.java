package com.example.triage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import loginDatabase.User;
import suplementary.ArrivalAdapter;
import suplementary.Patient;
import suplementary.Visit;
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
 * This class handles the creation of the fragment containing a listView of
 * patients sorted by their arrival time.
 * 
 * @author Connor Yoshimoto, Priyank Purohit
 * 
 */
public class ArrivalTimePatientListFragment extends Fragment {
	private ListView listView;
	private ArrayList<Patient> patientList = new ArrayList<Patient>();
	private ArrivalAdapter arrivalAdapter;
	private View rootView;
	private User user;
	private PatientSQLHelper patientDb;
	private ReadFilesTask rf;

	/**
	 * This method creates the needed database and then adds the patients sorted
	 * by their arrival time to the listView and then displays said listView.
	 * 
	 * @return rootView - The view that is to be displayed
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_main_tab_urgency,
				container, false);
		patientDb = new PatientSQLHelper(getActivity());
		rf = new ReadFilesTask(getActivity());
		rf.execute(null, null, null);
		// Inflate the layout for this fragment
		user = (User) getArguments().getSerializable("userKey");
		return rootView;
	}

	/**
	 * ArrivalTimeComparator handles sorting of the patients based on their
	 * arrival time to the hospital
	 * 
	 * @author arturomenacruz
	 * 
	 */
	private static class ArrivalTimeComparator implements Comparator<Patient> {
		@Override
		public int compare(Patient p1, Patient p2) {
			Visit last1 = p1.getVisit().get(p1.getVisit().size() - 1);
			Visit last2 = p2.getVisit().get(p2.getVisit().size() - 1);
			Calendar d1 = last1.getLastArrival();
			Calendar d2 = last2.getLastArrival();
			if (d1.after(d2)) {
				return 1;
			} else if (d1.before(d2)) {
				return -1;
			}
			return 0;

		}
	}

	/**
	 * 
	 * @author arturomenacruz Handles reading from the database and loading all
	 *         the patients that are in the Er into a list view. When we execute
	 *         the AsyncTask it will add all the patients to a database.
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
				Collections.sort(patientList, new ArrivalTimeComparator());
				Collections.reverse(patientList);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			listView = (ListView) rootView
					.findViewById(R.id.urgencyPatientList);
			arrivalAdapter = new ArrivalAdapter(context, patientList);
			listView.setAdapter(arrivalAdapter);
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