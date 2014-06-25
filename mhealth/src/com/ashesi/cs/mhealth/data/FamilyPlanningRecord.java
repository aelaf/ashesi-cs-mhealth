package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.widget.GridView;
import android.widget.ImageView;

/**
 * represents a single family planning record
 * @author Aelaf Dafla
 *
 */
public class FamilyPlanningRecord {
	private int familyPlanningRecordId;
	private int communityMemberId;
	private int familyPlanningServiceId;
	private String serviceDate;
	private String fullname;
	private String familyPlanningServiceName;
	
	public FamilyPlanningRecord(){
		
	}
	
	public FamilyPlanningRecord(int familyPlanningRecordId,int communityMemberId,String fullname,int familyPlanningServiceId, String familyPlanningServiceName, String serviceDate){
		this.familyPlanningRecordId=familyPlanningRecordId;
		this.communityMemberId=communityMemberId;
		this.fullname=fullname;
		this.familyPlanningServiceId=familyPlanningServiceId;
		this.serviceDate=serviceDate;
		this.familyPlanningServiceName=familyPlanningServiceName;
	}
	
	public int getId(){
		return this.familyPlanningRecordId;
	}
	
	public int getFamilyPlanningServiceId(){
		return familyPlanningServiceId;
	}
	
	public int getCommunityMemberId(){
		return communityMemberId;
	}
	
	public String getServiceName(){
		return familyPlanningServiceName;
	}
	
	public String getFullname(){
		return fullname;
	}
	
	public String getServiceDate(){
		return serviceDate;
	}
	
	public String getFormattedServiceDate(){
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			java.util.Date date=dateFormat.parse(serviceDate);
			dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
			return dateFormat.format(date);
		}
		catch(Exception ex){
			return "";
		}
	}
	

	public String toString(){
		return fullname +" "+familyPlanningServiceName;
	}
	
	public boolean equals(FamilyPlanningRecord record){
		if(record.getId()==familyPlanningRecordId){
			return true;
		}
		return false;
	}
}
