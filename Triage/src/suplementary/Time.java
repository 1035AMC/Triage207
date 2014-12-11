package suplementary;

import java.util.Calendar;

/**
 * Handles getting current date and time. Cannot be extended further.
 * 
 * @author	Priyank Purohit
 * @since	16-11-2014
 */
public final class Time {
	/**
	 * Returns current date and time as an array of int. Time is returned in 24
	 * hour format.
	 * 
	 * @return Current date and time. Format is [DD, MM, YYYY, HH, MM, SS].
	 */
	public static int[] getDateTime() {
		Calendar c = Calendar.getInstance();

		int date = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);

		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);

		return new int[] { date, month, year, hour, minute, second };
	}
}
