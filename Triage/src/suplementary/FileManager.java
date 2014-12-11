package suplementary;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

/**
 * FileManager handles storing and writing instances of patients into the internal 
 * of the device
 * @author arturomenacruz
 */
public class FileManager {
	/**
	 * Context of the activity where FileManager is instantiated from
	 */
	private Context context;

	/**
	 * Instantiates FileManager
	 * @param context activity where the FileManager was instantiated.
	 */
	public FileManager(Context context) {
		this.context = context;
	}

	/**
	 * Given a specific patient, FileManager will create a file 
	 * and save the instance of that class. If the file already exists
	 * it will overwrite that file with any changes you have made to the patient's 
	 * information.
	 * @param patient patient to be saved in the internal storage
	 */
	public void writeData(Patient patient) {
		try {
			// Creates the file to be written to
			FileOutputStream fileOut = context.getApplicationContext()
					.openFileOutput(patient.getHealthCard(),
							Context.MODE_PRIVATE);
			ObjectOutputStream OOS = new ObjectOutputStream(fileOut);
			// writes the patient instance into the file.
			OOS.writeObject(patient);
			fileOut.close();
		} catch (Exception e) {
			Log.d("writing problem is caused by", e.getCause().toString());
		}
	}

	/**
	 * Given a unique id, in this case the healthCardNum, the patient instance is retrieved
	 * @param healthCardNum Patient's healthCardNum
	 * @return Patient instance if the patient is on file. Otherwise returns null.
	 */
	public Patient readData(String healthCardNum) {
		Patient patient = null;
		try {
			// opens the file requested
			FileInputStream filein = context.openFileInput(healthCardNum);
			ObjectInputStream ois = new ObjectInputStream(filein);
			// returns the patient with the associated healthCardNum
			patient = (Patient) ois.readObject();
			ois.close();
			filein.close();
		} catch (Exception e) {
			Log.d("Reading error caused by:", e.getCause().toString());
		}
		return patient;
	}

}
