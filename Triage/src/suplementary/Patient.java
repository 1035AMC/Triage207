package suplementary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Object to hold all information related to a single patient.
 * 
 * @author Priyank
 */
public class Patient implements Serializable, Comparable<Patient> {
	private static final long serialVersionUID = -2030334700581385768L;
	/**
	 * name of the patient
	 */
	private boolean inER;
	/**
	 * name of the patient
	 */
	private String name;
	/**
	 * Date of birth of the patient
	 */
	private Calendar birthday;
	/**
	 * Health Card number of the patient
	 */
	private String healthCard;
	/**
	 * A record consisting of every visit that the patient has made to the
	 * hospital
	 */
	private ArrayList<Visit> visits = new ArrayList<Visit>();

	/**
	 * Initialize the patient class
	 * 
	 * @param name
	 *            name of the patient
	 * @param birthday
	 *            Date of birth
	 * @param healthCard
	 *            Health Card number
	 */
	public Patient(String name, Calendar birthday, String healthCard) {
		this.name = name;
		this.birthday = birthday;
		this.healthCard = healthCard;
	}

	/**
	 * Empty Constructor
	 */
	public Patient() {
	}

	@Override
	public String toString() {
		String ret = "";

		for (Visit v : this.visits)
			ret += v.toString();

		return getHealthCard() + " VISITS: " + ret;
	}

	/*
	 * Calculate the urgency rank of the Patient's last visit IF (s)he is in ER.
	 * 
	 * -- Notes: Describe here how the calculations are done. Use the Visit
	 * method's getLast___() methods. Ignore any values that are returned as
	 * -99.
	 * 
	 * @return urgencyLevel of the Patient
	 */
	public int calculateUrgency() {
		int urgency = 0;
		if (this.visits == null || this.visits.isEmpty()) {
			return 0;
		}
		Visit visit = this.visits.get(this.visits.size() - 1);
		if (getAge(birthday) < 2) {
			urgency++;
		}
		if (visit.getLastTemp() >= 39) {
			urgency++;
		}
		if (visit.getLastBpSys() >= 140 || visit.getLastBpDia() >= 90) {
			urgency++;
		}
		if (visit.getLastHr() >= 100 || visit.getLastHr() <= 50) {
			if (visit.getLastHr() > 0) {
				urgency++;
			}
		}
		return urgency;
	}

	public void newVisit() {
		Visit newVisit = new Visit();

		this.inER = true;
		this.visits.add(newVisit);
	}

	// ONLY GETTERS-SETTERS BELOW
	public boolean isInER() {
		return inER;
	}

	public void setInER(boolean inER) {
		this.inER = inER;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {

		return birthday.get(Calendar.DATE) + "/" + birthday.get(Calendar.MONTH)
				+ "/" + birthday.get(Calendar.YEAR);
	}

	public String getBirthdayForDisplay() {

		return birthday.get(Calendar.DATE) + "/"
				+ (birthday.get(Calendar.MONTH) + 1) + "/"
				+ birthday.get(Calendar.YEAR);
	}

	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	public String getHealthCard() {
		return healthCard;
	}

	public void setHealthCard(String healthCard) {
		this.healthCard = healthCard;
	}

	public ArrayList<Visit> getVisit() {
		return visits;
	}

	public void setVisit(ArrayList<Visit> visits) {
		this.visits = visits;
	}

	/**
	 * Calculates how old is a patient
	 * @param birthday
	 * @return age of the patient
	 */
	public int getAge(Calendar birthday) {

		Calendar today = Calendar.getInstance();
		Calendar birthDate;

		int age = 0;

		birthDate = (birthday);

		age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

		if (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH)) {
			age--;

		} else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
				&& (birthDate.get(Calendar.DAY_OF_MONTH) > today
						.get(Calendar.DAY_OF_MONTH))) {
			age--;
		}

		return age;
	}

	@Override
	public int compareTo(Patient another) {
		// TODO Auto-generated method stub
		int urgency1 = this.calculateUrgency();
		int urgency2 = another.calculateUrgency();

		if (urgency1 < urgency2) {
			return -1;
		} else if (urgency1 == urgency2) {
			return 0;
		}
		return 1;
	}
}
