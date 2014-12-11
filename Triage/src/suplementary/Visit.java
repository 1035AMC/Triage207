package suplementary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

/**
 * An object that holds all information about a single visit.
 * @author Priyank
 *
 */
public class Visit implements Serializable {
	private static final long serialVersionUID = 2249249578873524084L;

	/**
	 * Patient's arrival time for the current visit. Set automatically when
	 * Visit is instantized.
	 */
	private int[] arrivalTime;

	/**
	 * The time when Patient got to see the doctor. Set automatically.
	 */
	private int[] seenDocTime;

	/**
	 * Patient's status in ER. True iff the patient is out of ER and with a
	 * doctor.
	 */
	private boolean seenDoc;

	/**
	 * List of Vital for the current visit.
	 */
	private ArrayList<Vital> allVitals;

	/**
	 * List of Prescriptions for the current visit.
	 */
	private ArrayList<Prescription> allPrescriptions;

	/**
	 * Constructs a Visit object. Auto-generates time. Instantizes a blank list
	 * of Vital.
	 */
	public Visit() {
		this.arrivalTime = Time.getDateTime();
		this.seenDoc = false;
		this.allVitals = new ArrayList<Vital>();
		this.addVitals(37.0, 75, 120, 80, "");
		this.addVitals(37.0, 75, 120, 80, "");
		this.allPrescriptions = new ArrayList<Prescription>();
	}

	/**
	 * Adds a new Vital object to the visit.
	 * 
	 * @param temp The temperature of patient in degrees celsius.
	 * 
	 * @param hr Heart rate of patient.
	 * 
	 * @param bpSys Systolic blood pressure of patient.
	 * 
	 * @param bpDia Diastolic blood pressure of patient
	 * 
	 * @param sym presented symptoms of patient
	 */
	public void addVitals(double temp, int hr, int bpSys, int bpDia, String sym) {
		this.allVitals.add(new Vital(temp, bpSys, bpDia, hr, sym));
	}

	/**
	 * Go backwards through the collected vitals and returns the latest recorded
	 * value of temperature. If no temperature was recorded, return -10.
	 * 
	 * @return last recorded temp, return -10 if none ever recorded.
	 */
	public double getLastTemp() {
		int len = this.allVitals.size();

		// going backwards
		for (int i = len - 1; i >= 0; i--) {
			if (this.allVitals.get(i).getTemp() >= 0)
				return this.allVitals.get(i).getTemp();
		}
		return -99.0;
	}

	/**
	 * Go backwards through the collected vitals and returns the latest recorded
	 * value of heart rate. If no heart rate was recorded, return -10.
	 * 
	 * @return last recorded HR, return -10 if none ever recorded.
	 */
	public int getLastHr() {
		int len = this.allVitals.size();

		// going backwards
		for (int i = len - 1; i >= 0; i--) {
			if (this.allVitals.get(i).getHr() >= 0)
				return this.allVitals.get(i).getHr();
		}
		return -99;
	}

	/**
	 * Go backwards through the collected vitals and returns the latest recorded
	 * value of systolic BP. If no systolic BP was recorded, return -10.
	 * 
	 * @return last recorded bpSys, return -10 if none ever recorded.
	 */
	public int getLastBpSys() {
		int len = this.allVitals.size();

		// going backwards
		for (int i = len - 1; i >= 0; i--) {
			if (this.allVitals.get(i).getBpSys() >= 0)
				return this.allVitals.get(i).getBpSys();
		}
		return -99;
	}

	/**
	 * Go backwards through the collected vitals and returns the latest recorded
	 * value of diastolic BP. If no diastolic BP was recorded, return -10.
	 * 
	 * @return last recorded bpDia, return -10 if none ever recorded.
	 */
	public int getLastBpDia() {
		int len = this.allVitals.size();

		// going backwards
		for (int i = len - 1; i >= 0; i--) {
			if (this.allVitals.get(i).getBpDia() >= 0)
				return this.allVitals.get(i).getBpDia();
		}
		return -99;
	}

	@Override
	public String toString() {
		String ret = "Visit on: ";

		for (int i = 0; i < 6; i++)
			ret += this.arrivalTime[i] + ". ";

		ret += "\n\n";

		for (Vital v : this.allVitals) {
			ret += v.toString() + "\n";
		}
		return ret;
	}

	public void addPrescription(String meds) {
		this.allPrescriptions.add(new Prescription(meds));
	}

	// ONLY GETTERS-SETTERS BELOW
	public int[] getArrivalTime() {
		return arrivalTime;
	}

	public int[] getSeenDocTime() {
		if (seenDocTime == null)
			return new int[] { 0, 0, 0, 0, 0, 0 };
		return seenDocTime;
	}

	public ArrayList<Vital> getVitals() {
		return this.allVitals;
	}

	public void setVitals(ArrayList<Vital> vitals) {
		this.allVitals = vitals;
	}

	public boolean isSeenDoc() {
		return seenDoc;
	}

	public void setSeenDoc(boolean seenDoc) {
		this.seenDoc = seenDoc;

		// If patient has seen doctor, set the value.
		if (this.seenDoc && seenDoc && this.seenDocTime == null) {
			this.seenDocTime = Time.getDateTime();
			Log.d("Triage.Visit", "SeenDocTime added!");
		}
	}

	public void setPrescription(ArrayList<Prescription> prescription) {
		this.allPrescriptions = prescription;
	}

	public ArrayList<Prescription> getPrescriptions() {
		return this.allPrescriptions;
	}

	public Calendar getLastArrival() {
		Calendar date = Calendar.getInstance();
		int[] val = getArrivalTime();
		date.set(val[2], val[1], val[0], val[4], val[5]);
		return date;
	}
}