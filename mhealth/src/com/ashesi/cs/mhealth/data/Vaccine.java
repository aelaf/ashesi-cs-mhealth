package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
	
	public int getId(){
		return vaccineId;
	}
	
	public String getVaccineName(){
		return vaccineName;
	}
	
	public int getVaccineSchedule(){
		return schedule;
	}
	
	/**
	 * Calculates when the vaccination should be given given the birthdate
	 * @param communityMemberId
	 * @param vaccineId
	 * @return
	 */
	public Date getWhenToVaccine(Date date){
		if(schedule<0){	//there is no schedule
			return null;
		}
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR,schedule);
		return calendar.getTime();
	}
	
	public Date getWhenToVaccine(String date){
		if(schedule<0){	//there is no schedule
			return null;
		}
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			java.util.Date d=dateFormat.parse(date);
			return getWhenToVaccine(d);
		}
		catch(Exception ex){
			return null;
		}
	}
	
	public String toString(){
		return vaccineName;
	}
}
