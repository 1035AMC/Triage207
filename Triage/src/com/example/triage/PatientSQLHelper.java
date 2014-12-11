package com.example.triage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import suplementary.Patient;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
/**
 * 
 * @author arturomenacruz This class is will manage storage 
 * and retrieval of patients through the usage of a SQLite database.
 *
 */
@SuppressLint("SimpleDateFormat")
public class PatientSQLHelper extends SQLiteOpenHelper {
	private static final String file = "initialized";

	private String[] ALLCOLUMNS = { COLUMN_ID, COLUMN_PATIENT_ID,
			COLUMN_PATIENT_SERIAL };

	private Context context;
	/**
	 * Name of the Database
	 */
	private static final String DATABASE_NAME = "hospital.db";
	/**
	 * Version of Database
	 */
	private static final int DATABASE_VERSION = 1;
	/**
	 * Name of the DATABASE TABLE
	 */
	private static final String DATABASE_TABLE = "patients";

	/**
	 * Column id  
	 */
	public static final String COLUMN_ID = "_id";
	/**
	 * Health card number of the patient
	 */
	public static final String COLUMN_PATIENT_ID = "healthCardNumber";

	/**
	 * Serial number of the patient
	 */
	public static final String COLUMN_PATIENT_SERIAL = "serialNumber";

	/**
	 * String to be used in the creation of the database
	 */
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_PATIENT_SERIAL
			+ " BLOB, " + COLUMN_PATIENT_ID + " text unique" + ");";

	/**
	 * Initializing parent
	 * @param context - activity in which this object is being instantiated.
	 */
	public PatientSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(db);
	}

	/**
	 * Creates a row in the database that will correspond the patient
	 * @param patient Patient to be entered in the database
	 */
	public void createEntry(Patient patient) {
		byte[] serializedPatient = convertToByteArray(patient);
		ContentValues content = new ContentValues();
		content.put(COLUMN_PATIENT_ID, patient.getHealthCard());

		content.put(COLUMN_PATIENT_SERIAL, serializedPatient);
		this.getWritableDatabase().insert(DATABASE_TABLE, null, content);
		this.close();
	}

	/**
	 * Get all the patients from the database
	 * @return all patients in database
	 */
	public ArrayList<Patient> getAllPatients() {
		ArrayList<Patient> patientsinDb = new ArrayList<Patient>();
		Cursor cursor = this.getWritableDatabase().query(DATABASE_TABLE,
				ALLCOLUMNS, null, null, null, null, null);

		int iPatient = cursor.getColumnIndex(COLUMN_PATIENT_SERIAL);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			ByteArrayInputStream bis = new ByteArrayInputStream(
					cursor.getBlob(iPatient));
			ObjectInput in = null;
			try {
				in = new ObjectInputStream(bis);
				try {
					Patient patient = (Patient) in.readObject();
					patientsinDb.add(patient);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					in.close();
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}
		return patientsinDb;
	}

	/**
	 * Get's a specific patient from the database
	 * @param healthcardnum Unique id to help retrieve patient from database
	 * @return patient specified
	 */
	public Patient getPatient(String healthcardnum) {
		Patient patient = null;
		Cursor cursor = this.getWritableDatabase().query(DATABASE_TABLE,
				ALLCOLUMNS, COLUMN_PATIENT_ID + "=?",
				new String[] { healthcardnum }, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			Log.e("running", "curosr while loop enter");

			byte[] temp = (cursor.getBlob(cursor
					.getColumnIndex(COLUMN_PATIENT_SERIAL)));
			ByteArrayInputStream b = new ByteArrayInputStream(temp);
			ObjectInputStream o;
			try {
				o = new ObjectInputStream(b);
				try {
					patient = (Patient) o.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		cursor.close();
		return patient;

	}

	/**
	 * Update information about a patient in the database
	 * @param patient Patient to be updated
	 */
	public void updatePatient(Patient patient) {
		byte[] serializedPatient = convertToByteArray(patient);
		ContentValues args = new ContentValues();
		args.put(COLUMN_PATIENT_SERIAL, serializedPatient);
		this.getWritableDatabase().update(DATABASE_TABLE, args,
				COLUMN_PATIENT_ID + "=" + patient.getHealthCard(), null);
	}

	/**
	 * Converts an instance of patient into a byte array to store in the database.
	 * @param patient Patient to be converted
	 * @return byte array representing patient
	 */
	private byte[] convertToByteArray(Patient patient) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] serializedPatient = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(patient);
			serializedPatient = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serializedPatient;
	}

	/**
	 * Return if database has already loaded the information from the txt file provided in the raw folder
	 * @return is database loaded
	 */
	public boolean isDatabaseFilled() {
		try {
			// opens the file
			FileInputStream filein = context.openFileInput(file);
			BufferedInputStream ois = new BufferedInputStream(filein);
			ois.close();
			filein.close();
			// if the file does not exists then an exception will be thrown
			// and we know that the database hasn't been filled
		} catch (IOException e) {
			Log.d("AllPatients", e.getCause().toString());
			return false;
		}
		return true;
	}

	/**
	 * Fill database with the patients listed in the raw folder.
	 */
	public void fill_database() {
		// if the file hasn't been created then we go through the given file and
		// fill database
		if (!isDatabaseFilled()) {
			try {
				String[] input_file;
				// scan the patient file and get all of them
				Scanner patientscanner = new Scanner(context.getResources()
						.openRawResource(
								context.getResources().getIdentifier(
										"patient_records", "raw",
										context.getPackageName())));
				while (patientscanner.hasNextLine()) {
					input_file = patientscanner.nextLine().split(",");
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Calendar birthday = Calendar.getInstance();
					birthday.setTime(formatter.parse(input_file[2]));
					Log.d("Reading Patient", birthday.toString());
					Patient patient = new Patient(input_file[1], birthday,
							input_file[0]);
					this.createEntry(patient);

				}
				patientscanner.close();
				// Close stream and reader

				// Print to LogCat with tag readFile() if something in reading
				// the
				// file breaks.
			} catch (Exception e) {
				Log.e("readFile()", "File Reading broke it");
				System.exit(1);
			} finally {
				createfile();
			}
		}
		// if the file has been created then the database has already been
		// filled so we do nothing

	}

	/**
	 * Create a file that will signal that the database has been filled. 
	 */
	private void createfile() {
		try {
			// Creates the file to be written to
			FileOutputStream fileOut = context.openFileOutput(file,
					Context.MODE_PRIVATE);
			ObjectOutputStream OOS = new ObjectOutputStream(fileOut);
			// writes the patient instance into the file.
			OOS.writeObject("Database has been initialized");
			fileOut.close();
		} catch (Exception e) {
			Log.d("All", e.getCause().toString());
		}
	}
}
