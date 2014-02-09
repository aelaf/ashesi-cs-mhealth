package com.ashesi.cs.mhealth.data;

import java.util.Calendar;
import java.util.Date;

public class Vaccine {
	private int vaccineId;
	private String vaccineName;
	private int schedule;
	
	public Vaccine(){
		
	}
	
	public Vaccine(int vaccineId, String vaccineName, int schedule){
		this.vaccineId=vaccineId;
		this.vaccineName=vaccineName;
		this.schedule=schedule;
	}
	
	int getId(){
		return vaccineId;
	}
	
	String getVaccineName(){
		return vaccineName;
	}
	
	int getVaccineSchedule(){
		return schedule;
	}
	
	/**
	 * Calculates when the vaccination should be given given the birthdate
	 * @param communityMemberId
	 * @param vaccineId
	 * @return
	 */
	public Date getWhenToVaccine(Date date){
		return Calendar.getInstance().getTime();
	}
	
	public String toString(){
		return vaccineName;
	}
}
