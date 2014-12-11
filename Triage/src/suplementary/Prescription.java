package suplementary;

import java.io.Serializable;

/**
 * Prescription class that holds the medication and instructons for a patient's visit.
 * @author Brandon
 */

public class Prescription implements Serializable {

	/**
	 * @author Brandon Pon
	 */

	private String medication;

	private static final long serialVersionUID = -7821318505636506101L;

	/**
	 * @param medication Medication for prescription
	 * 
	 * Create a Prescription object, which has a String of medication.
	 * 
	 */
	public Prescription(String medication) {
		this.medication = medication;
	}

	@Override
	public String toString() {
		return "Medications: " + medication;
	}

	public String getMedication() {
		return medication;
	}

	public void setMedication(String medication) {
		this.medication = medication;
	}

}
