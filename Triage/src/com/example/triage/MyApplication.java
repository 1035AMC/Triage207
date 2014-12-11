package com.example.triage;

import java.util.ArrayList;

import suplementary.Patient;
import android.app.Application;

public class MyApplication extends Application{
	private ArrayList<Patient> allPatients = new ArrayList<Patient>();
	
	public ArrayList<Patient> getAllPatients(){
		return allPatients;
	}
	public void setAllPatients(ArrayList<Patient> allPatients){
		this.allPatients = allPatients;
	}
	
	public void insertPatient(Patient patient){
		if (!allPatients.contains(patient)){
			allPatients.add(patient);
		}
	}
	
}
