package suplementary;

import java.io.Serializable;

/**
 * Vital class that holds all possible vitals that nurses can add. It automatically 
 * generates and saves the time when the object is instantized. Can't be extended 
 * further.
 * 
 * @author Priyank
 */
public final class Vital implements Serializable {
	private static final long serialVersionUID = -8272435059307859916L;

	/**
	 * Date and time at which the Vital was saved. Automatically generated when
	 * object instantized.
	 */
	private int[] vitalTime;

	// Temperature of the patient in celsius.
	private double temp;

	// Systolic blood pressure of the patient.
	private int bpSys;

	// Diastolic blood pressure of the patient.
	private int bpDia;

	// Heart rate of the patient.
	private int hr;

	// String holding the patient's symptoms.
	private String symp;

	/**
	 * Construct a Vital object. Automatically produce, and sets the time.
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
	public Vital(double temp, int bpSys, int bpDia, int hr, String symp) {
		setVitalTime(Time.getDateTime());
		this.temp = temp;
		this.bpSys = bpSys;
		this.bpDia = bpDia;
		this.hr = hr;
		this.symp = symp;
	}
	
	@Override
	public String toString()
	{
		String ret = "Visit on: ";

		for (int i = 0; i < 6; i++)
			ret += this.vitalTime[i] + ". ";

		ret += "\n" + getTemp() + " " + getHr() + " " + getBpSys() + " " + getBpDia() + "\n";
		
		return ret;
	}
	
	// ONLY GETTERS+SETTERS BELOW!
	public int[] getVitalTime() {
		return vitalTime;
	}

	public void setVitalTime(int[] vitalTime) {
		this.vitalTime = vitalTime;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public int getBpSys() {
		return bpSys;
	}

	public void setBpSys(int bpSys) {
		this.bpSys = bpSys;
	}

	public int getBpDia() {
		return bpDia;
	}

	public void setBpDia(int bpDia) {
		this.bpDia = bpDia;
	}

	public int getHr() {
		return hr;
	}

	public void setHr(int hr) {
		this.hr = hr;
	}

	public String getSymp() {
		return symp;
	}

	public void setSymp(String symp) {
		this.symp = symp;
	}
	
}
