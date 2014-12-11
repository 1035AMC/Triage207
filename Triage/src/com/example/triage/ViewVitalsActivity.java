package com.example.triage;

import java.util.*;

import loginDatabase.User;

import suplementary.*;
import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.view.*;
import android.util.*;
import android.widget.*;

import graphview.GraphView;
import graphview.GraphView.GraphViewData;
import graphview.GraphViewSeries;
import graphview.GraphViewSeries.GraphViewSeriesStyle;
import graphview.LineGraphView;

/**
 * Back end of the ViewVitals UI. It handles the creation of the graphs, and
 * displays symptoms as well. Depending on the user logged in, it will also
 * display option to addVitals or addPrescription.
 * 
 * @author: Priyank Purohit
 * @since: 19-11-2014
 */
public class ViewVitalsActivity extends Activity {
	/**
	 * Creates a patient object for the current user.
	 */
	private Patient patient = null;

	/**
	 * Creates a visit object for the visit that needs to be displayed.
	 */
	private Visit visit = null;

	/**
	 * Creates an ArrayList of vitals from the visit that needs to be displayed.
	 */
	private ArrayList<Vital> vitals = null;

	/**
	 * Creates an ArrayList of prescriptions from the visit that needs to be
	 * displayed.
	 */
	private ArrayList<Prescription> prescriptions = null;

	/**
	 * Declare a User object to perform functions based on user type (nurse |
	 * physician).
	 */
	private User user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("ViewVitalsActivity", "onCreate is working");
		setContentView(R.layout.activity_view_vitals);
		addBackButton();

		getIntentExtras();
		vitals = visit.getVitals();
		prescriptions = visit.getPrescriptions();

		updateArrivalAndDocTime();
		updateInfo();

		try {
			Button buttonSelector = (Button) findViewById(R.id.buttonSelector);
			if ((user.getType()).equals("physician")) {
				buttonSelector.setText("Add Prescription");
			} else {
				buttonSelector.setText("Add Vitals");
			}
		} catch (Exception e) {
			Log.e("Triage.ViewVitalsActivity", "user/usertype error");
		}

		// Declare the three GraphView objects.
		GraphView heartRateGraph;
		GraphView tempGraph;
		GraphView bloodPressureGraph;

		// Instantize the three GraphView objects.
		heartRateGraph = new LineGraphView(this, "");
		tempGraph = new LineGraphView(this, "");
		bloodPressureGraph = new LineGraphView(this, "");

		// Set true the option to plot points.
		((LineGraphView) heartRateGraph).setDrawDataPoints(true);
		((LineGraphView) tempGraph).setDrawDataPoints(true);
		((LineGraphView) bloodPressureGraph).setDrawDataPoints(true);

		// Set the radius of the plotted points large enough to see.
		((LineGraphView) heartRateGraph).setDataPointsRadius(10f);
		((LineGraphView) tempGraph).setDataPointsRadius(10f);
		((LineGraphView) bloodPressureGraph).setDataPointsRadius(10f);

		// Get vitals into correct format (GraphViewSeries) for graphing.
		GraphViewSeries heartRateSeries = heartRateToGraphSeries(vitals);
		GraphViewSeries tempSeries = tempToGraphSeries(vitals);
		GraphViewSeries bpSysSeries = bpSysToGraphSeries(vitals);
		GraphViewSeries bpDiaSeries = bpDiaToGraphSeries(vitals);

		// Add the data series to the GraphView objects.
		heartRateGraph.addSeries(heartRateSeries);
		tempGraph.addSeries(tempSeries);
		bloodPressureGraph.addSeries(bpSysSeries);
		bloodPressureGraph.addSeries(bpDiaSeries);

		// Get the locations of the graphs, and symptoms from XML.
		LinearLayout layout1 = (LinearLayout) findViewById(R.id.graph1);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.graph2);
		LinearLayout layout3 = (LinearLayout) findViewById(R.id.graph3);
		TextView symps = (TextView) findViewById(R.id.symptoms);

		// Add the GraphView objects, and the symptoms to the UI.

		layout1.addView(heartRateGraph);
		layout2.addView(tempGraph);
		layout3.addView(bloodPressureGraph);
		symps.setText(vitals.get(vitals.size() - 1).getSymp());

		// Prescription Stuff.
		if (!prescriptions.isEmpty()) {
			TextView prescripts = (TextView) findViewById(R.id.prescriptions);
			if (prescriptions.size() > 1) {
				prescripts.setText(prescriptions.get(prescriptions.size() - 1)
						.getMedication());
			} else {
				prescripts.setText(prescriptions.get(0).getMedication());
			}

		} else {
			Log.d("TRIAGE.ViewVitals",
					"No prescriptions exists! Can't show any....");
		}
	}

	private void updateInfo() {
		int lastVisitIndex = patient.getVisit().size() - 1;

		int[] lastVisitTime = patient.getVisit().get(lastVisitIndex)
				.getArrivalTime();
		int[] currVisitTime = visit.getArrivalTime();
		TextView info = (TextView) findViewById(R.id.info);

		if ((user.getType()).equals("physician")) {
			if (!((lastVisitTime[0] == currVisitTime[0])
					&& (lastVisitTime[1] == currVisitTime[1])
					&& (lastVisitTime[2] == currVisitTime[2])
					&& (lastVisitTime[3] == currVisitTime[3])
					&& (lastVisitTime[4] == currVisitTime[4]) && (lastVisitTime[5] == currVisitTime[5]))) {
				info.setText("This visit can't be modified. A newer visit exists!");
				// Hide the button
				Button button = (Button) findViewById(R.id.buttonSelector);
				button.setVisibility(View.INVISIBLE);
			}
		} else {
			if (!((lastVisitTime[0] == currVisitTime[0])
					&& (lastVisitTime[1] == currVisitTime[1])
					&& (lastVisitTime[2] == currVisitTime[2])
					&& (lastVisitTime[3] == currVisitTime[3])
					&& (lastVisitTime[4] == currVisitTime[4]) && (lastVisitTime[5] == currVisitTime[5]))) {
				info.setText("This visit can't be modified. A newer visit exists!");
				// Hide the button
				Button button = (Button) findViewById(R.id.buttonSelector);
				button.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Adds the patient's arrival time to the top of the screen for the nurses'
	 * convenience.
	 */
	private void updateArrivalAndDocTime() {
		TextView visitTimes = (TextView) findViewById(R.id.patArrivalTime);

		// Get the arrival time.
		int[] time = visit.getArrivalTime();

		String textToDisplay = "Arrival Time: " + time[0] + "/" + time[1] + "/"
				+ time[2] + " @ " + timeToString(time[3], time[4]) + "\n";

		// Get the time when patient first sees a doctor.
		time = visit.getSeenDocTime();

		// Only if the patient has actually seen a doctor.
		if (!(time[2] == 0)) {
			textToDisplay += "First Saw Doctor At: " + time[0] + "/" + time[1]
					+ "/" + time[2] + " @ " + timeToString(time[3], time[4]);
		}

		textToDisplay += "Latest Urgency Rank: " + patient.calculateUrgency();

		visitTimes.setText(textToDisplay);
	}

	/**
	 * Gets Patient, Visit, and user as Serializable extra.
	 */
	private void getIntentExtras() {
		try {
			Log.d("TRIAGE.ViewVitals", "Getting user extra");
			Intent intent = getIntent();
			patient = (Patient) intent.getSerializableExtra("patientKey");
			visit = (Visit) intent.getSerializableExtra("visitKey");
			user = (User) intent.getSerializableExtra("userKey");
			Log.d("Triage.ViewVitalsActivity", "User is working, type is="
					+ user.getType());
		} catch (Exception e) {
			Log.e("TRIAGE.ViewVitals",
					"Error getting serialized patient or visit");
		}
	}

	/**
	 * Turns the numeric value of hours and minutes to String for GraphView
	 * labels.
	 */
	public static String timeToString(int hour, int minute) {
		String ret = "";

		if (hour < 10) {
			ret += "0" + hour;
		} else {
			ret += hour;
		}
		ret += ":";

		if (minute < 10) {
			ret += "0" + minute;
		} else {
			ret += minute;
		}
		return ret;
	}

	/**
	 * Changes action of button onclick.
	 * 
	 * @param view
	 */
	public void buttonSelector(View view) {
		int lastVisitIndex = patient.getVisit().size() - 1;
		int[] lastVisitTime = patient.getVisit().get(lastVisitIndex)
				.getArrivalTime();

		int[] currVisitTime = visit.getArrivalTime();

		TextView info = (TextView) findViewById(R.id.info);

		if ((user.getType()).equals("physician")) {
			if (!((lastVisitTime[0] == currVisitTime[0])
					&& (lastVisitTime[1] == currVisitTime[1])
					&& (lastVisitTime[2] == currVisitTime[2])
					&& (lastVisitTime[3] == currVisitTime[3])
					&& (lastVisitTime[4] == currVisitTime[4]) && (lastVisitTime[5] == currVisitTime[5]))) {
				info.setText("This visit can't be modified. A newer visit exists.");
			} else {
				moveToAddPrescription(view);
			}
		} else {
			if (!((lastVisitTime[0] == currVisitTime[0])
					&& (lastVisitTime[1] == currVisitTime[1])
					&& (lastVisitTime[2] == currVisitTime[2])
					&& (lastVisitTime[3] == currVisitTime[3])
					&& (lastVisitTime[4] == currVisitTime[4]) && (lastVisitTime[5] == currVisitTime[5]))) {
				info.setText("This visit can't be modified. A newer visit exists.");
			} else {
				moveToAddVitals(view);
			}
		}
	}

	/**
	 * If Physician is the user, the button should be renamed to
	 * "Add Prescription" if the visit is the patient's last visit. In that
	 * case, the button should also go the AddPrescription activity.
	 */
	private void moveToAddPrescription(View view) {
		Intent toAddPrescription = new Intent(this, PrescriptionAdd.class);
		toAddPrescription.putExtra("patientKey", patient);
		toAddPrescription.putExtra("visitKey", visit);
		toAddPrescription.putExtra("userKey", user);
		startActivity(toAddPrescription);
	}

	/**
	 * Puts the patient object as extra and starts the addVitals activity.
	 * 
	 * @param
	 */
	public void moveToAddVitals(View view) {
		Intent toAddVitals = new Intent(this, AddVitalsActivity.class);
		toAddVitals.putExtra("patientKey", patient);
		toAddVitals.putExtra("visitKey", visit);
		toAddVitals.putExtra("userKey", user);
		startActivity(toAddVitals);
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
		setTitle(patient.getName() + "'s Vitals");
		return true;
	}

	/**
	 * @param item
	 *            A menu item Creates the Action Bar.
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

		Intent moveToPatientInfo = new Intent(this, ViewPatientInfo.class);
		moveToPatientInfo.putExtra("patientKey", patient);
		moveToPatientInfo.putExtra("userKey", user);
		startActivity(moveToPatientInfo);
		return super.onOptionsItemSelected(item);
	}

	// TODO: HEART RATE
	/**
	 * Takes the stored vitals in the ArrayList of Vital and converts them to
	 * GraphViewSeries that is required for plotting on GraphView objects.
	 * In short, GraphViewData parameters are (X-coordinate, Y-coordinate,
	 * X-coordinate-label).
	 * 
	 * 
	 * @param allVitals
	 *            A list of vitals from the visit of interest.
	 * 
	 * @return GraphViewSeries format of the vitals from visit of interest.
	 */
	private GraphViewSeries heartRateToGraphSeries(ArrayList<Vital> allVitals) {
		// Declare the GVData object.
		GraphViewData[] GVData = null;

		// Number of total vitals for the visit.
		int numOfVitals = allVitals.size();

		// Number of valid vitals, a subset of the total vitals.
		int validVitals = 0;

		// Calculate the number of valid vitals to allocate GVData size.
		for (Vital v : allVitals)
			if (v.getHr() >= -90)
				validVitals++;

		// Allocate the GVData size, and instantiate it.
		GVData = new GraphViewData[validVitals];

		// Counter for the valid values added.
		int k = 0;

		// Add all VALID vitals to GVData.
		for (int i = 0; i < numOfVitals; i++) {
			if (allVitals.get(i) != null) {
				int data = allVitals.get(i).getHr();
				if (data >= -90) {
					String time = timeToString(
							allVitals.get(i).getVitalTime()[3], allVitals
									.get(i).getVitalTime()[4]);

					GVData[k] = new GraphViewData(k, data, time);
					k++; // Add 1 to keep track of valid data added.
				}
			}
		}

		// Construct a GraphViewSeries and return it.
		return new GraphViewSeries("HR", new GraphViewSeriesStyle(
				Color.MAGENTA, 3), GVData);
	}

	// TODO: TEMPERATURE
	/**
	 * Takes the stored vitals in the ArrayList of Vital and converts them to
	 * GraphViewSeries that is required for plotting on GraphView objects.
	 * In short, GraphViewData parameters are (X-coordinate, Y-coordinate,
	 * X-coordinate-label).
	 * 
	 * 
	 * @param allVitals
	 *            A list of vitals from the visit of interest.
	 * 
	 * @return GraphViewSeries format of the vitals from visit of interest.
	 */
	private GraphViewSeries tempToGraphSeries(ArrayList<Vital> allVitals) {
		// Declare the GVData object.
		GraphViewData[] GVData = null;

		// Number of total vitals for the visit.
		int numOfVitals = allVitals.size();

		// Number of valid vitals, a subset of the total vitals.
		int validVitals = 0;

		// Calculate the number of valid vitals to allocate GVData size.
		for (Vital v : allVitals)
			if (v.getTemp() >= -90)
				validVitals++;

		// Allocate the GVData size, and instantiate it.
		GVData = new GraphViewData[validVitals];

		// Counter for the valid values added.
		int k = 0;

		// Add all VALID vitals to GVData.
		for (int i = 0; i < numOfVitals; i++) {
			if (allVitals.get(i) != null) {
				double data = allVitals.get(i).getTemp();
				if (data >= -90) {
					String time = timeToString(
							allVitals.get(i).getVitalTime()[3], allVitals
									.get(i).getVitalTime()[4]);

					GVData[k] = new GraphViewData(k, data, time);
					k++; // Add 1 to keep track of valid data added.
				}
			}
		}

		// Construct a GraphViewSeries and return it.
		return new GraphViewSeries("TEMP", new GraphViewSeriesStyle(Color.CYAN,
				3), GVData);
	}

	// TODO: SYSTOLIC BP
	/**
	 * Takes the stored vitals in the ArrayList of Vital and converts them to
	 * GraphViewSeries that is required for plotting on GraphView objects.
	 * In short, GraphViewData parameters are (X-coordinate, Y-coordinate,
	 * X-coordinate-label).
	 * 
	 * 
	 * @param allVitals
	 *            A list of vitals from the visit of interest.
	 * 
	 * @return GraphViewSeries format of the vitals from visit of interest.
	 */
	private GraphViewSeries bpSysToGraphSeries(ArrayList<Vital> allVitals) {
		// Declare the GVData object.
		GraphViewData[] GVData = null;

		// Number of total vitals for the visit.
		int numOfVitals = allVitals.size();

		// Number of valid vitals, a subset of the total vitals.
		int validVitals = 0;

		// Calculate the number of valid vitals to allocate GVData size.
		for (Vital v : allVitals)
			if (v.getBpSys() >= 0)
				validVitals++;

		// Allocate the GVData size, and instantiate it.
		GVData = new GraphViewData[validVitals];

		// Counter for the valid values added.
		int k = 0;

		// Add all VALID vitals to GVData.
		for (int i = 0; i < numOfVitals; i++) {
			if (allVitals.get(i) != null) {
				int data = allVitals.get(i).getBpSys();
				if (data >= 0) {
					String time = timeToString(
							allVitals.get(i).getVitalTime()[3], allVitals
									.get(i).getVitalTime()[4]);

					GVData[k] = new GraphViewData(k, data, time);
					k++; // Add 1 to keep track of valid data added.
				}
			}
		}

		// Construct a GraphViewSeries and return it.
		return new GraphViewSeries("BP SYS", new GraphViewSeriesStyle(
				Color.RED, 3), GVData);
	}

	// TODO: DIASTOLIC BP
	/**
	 * Takes the stored vitals in the ArrayList of Vital and converts them to
	 * GraphViewSeries that is required for plotting on GraphView objects.
	 * In short, GraphViewData parameters are (X-coordinate, Y-coordinate,
	 * X-coordinate-label).
	 * 
	 * 
	 * @param allVitals
	 *            A list of vitals from the visit of interest.
	 * 
	 * @return GraphViewSeries format of the vitals from visit of interest.
	 */
	private GraphViewSeries bpDiaToGraphSeries(ArrayList<Vital> allVitals) {
		// Declare the GVData object.
		GraphViewData[] GVData = null;

		// Number of total vitals for the visit.
		int numOfVitals = allVitals.size();

		// Number of valid vitals, a subset of the total vitals.
		int validVitals = 0;

		// Calculate the number of valid vitals to allocate GVData size.
		for (Vital v : allVitals)
			if (v.getBpDia() >= 0)
				validVitals++;

		// Allocate the GVData size, and instantiate it.
		GVData = new GraphViewData[validVitals];

		// Counter for the valid values added.
		int k = 0;

		// Add all VALID vitals to GVData.
		for (int i = 0; i < numOfVitals; i++) {
			if (allVitals.get(i) != null) {
				int data = allVitals.get(i).getBpDia();
				if (data >= 0) {
					String time = timeToString(
							allVitals.get(i).getVitalTime()[3], allVitals
									.get(i).getVitalTime()[4]);

					GVData[k] = new GraphViewData(k, data, time);
					k++; // Add 1 to keep track of valid data added.
				}
			}
		}

		// Construct a GraphViewSeries and return it.
		return new GraphViewSeries("BP DIA", new GraphViewSeriesStyle(
				Color.GREEN, 3), GVData);
	}
}