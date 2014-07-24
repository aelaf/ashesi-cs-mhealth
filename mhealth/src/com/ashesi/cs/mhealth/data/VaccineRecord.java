package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * represents a single vaccination record
 * @author Aelaf Dafla
 *
 */
public class VaccineRecord {
	private int vaccineRecordId;
	private int communityMemberId;
	private int vaccineId;
	private String vaccineDate;
	private String fullname;
	private String vaccineName;
	private String gender;
	
	public VaccineRecord(){
		
	}
	
	public VaccineRecord(int vaccineRecordId,int communityMemberId,String fullname,int vaccineId, String vaccineName, String vaccineDate){
		this.vaccineRecordId=vaccineRecordId;
		this.communityMemberId=communityMemberId;
		this.fullname=fullname;
		this.vaccineId=vaccineId;
		this.vaccineDate=vaccineDate;
		this.vaccineName=vaccineName;
		
	}
	
	public VaccineRecord(int vaccineRecordId,int communityMemberId,String fullname,int vaccineId, String vaccineName, String vaccineDate, String gender){
		this.vaccineRecordId=vaccineRecordId;
		this.communityMemberId=communityMemberId;
		this.fullname=fullname;
		this.vaccineId=vaccineId;
		this.vaccineDate=vaccineDate;
		this.vaccineName=vaccineName;
		this.gender=gender;
		
	}
	
	public int getId(){
		return this.vaccineRecordId;
	}
	
	public int getVaccineId(){
		return vaccineId;
	}
	
	public int getCommunityMemberId(){
		return communityMemberId;
	}
	
	public String getVaccineName(){
		return vaccineName;
	}
	
	public String getFullname(){
		return fullname;
	}
	
	public String getVaccineDate(){
		return vaccineDate;
	}
	
	public String getFormattedVaccineDate(){
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			java.util.Date date=dateFormat.parse(vaccineDate);
			dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
			return dateFormat.format(date);
		}
		catch(Exception ex){
			return "";
		}
	}

	public String toString(){
		return fullname +" "+vaccineName +" " +gender;
	}
	
	public boolean equals(VaccineRecord record){
		if(record.getId()==vaccineRecordId){
			return true;
		}
		return false;
	}
}
