package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Date;
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
	private String scheduleDate;
	private String fullname;
	private String familyPlanningServiceName;
	private double quantity;
	
	public FamilyPlanningRecord(){
		this.quantity=0;
	}
	
	public FamilyPlanningRecord(int familyPlanningRecordId,int communityMemberId,String fullname,int familyPlanningServiceId, String familyPlanningServiceName, String serviceDate, double quantity){
		this.familyPlanningRecordId=familyPlanningRecordId;
		this.communityMemberId=communityMemberId;
		this.fullname=fullname;
		this.familyPlanningServiceId=familyPlanningServiceId;
		this.serviceDate=serviceDate;
		this.familyPlanningServiceName=familyPlanningServiceName;
		this.quantity=quantity;
	}
	
	public FamilyPlanningRecord(int familyPlanningRecordId,int communityMemberId,String fullname,int familyPlanningServiceId, String familyPlanningServiceName, String serviceDate, double quantity,String scheduleDate){
		this.familyPlanningRecordId=familyPlanningRecordId;
		this.communityMemberId=communityMemberId;
		this.fullname=fullname;
		this.familyPlanningServiceId=familyPlanningServiceId;
		this.serviceDate=serviceDate;
		this.familyPlanningServiceName=familyPlanningServiceName;
		this.quantity=quantity;
		this.scheduleDate=scheduleDate;
	}
	
	public FamilyPlanningRecord(int familyPlanningRecordId,int communityMemberId,String fullname,int familyPlanningServiceId, String familyPlanningServiceName, String serviceDate){
		this.familyPlanningRecordId=familyPlanningRecordId;
		this.communityMemberId=communityMemberId;
		this.fullname=fullname;
		this.familyPlanningServiceId=familyPlanningServiceId;
		this.serviceDate=serviceDate;
		this.familyPlanningServiceName=familyPlanningServiceName;
		this.quantity=0;
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
	
	public String getScheduleDate(){
		return this.scheduleDate;
	}
	
	public String getFormattedScheduleDate(){
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			Date date=dateFormat.parse(scheduleDate);
			dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
			return dateFormat.format(date);
		}
		catch(Exception ex){
			return "";
		}
	}
	
	public double getQuantity(){
		return quantity;
	}
	
	public int getQuantityInt(){
		return (int)quantity;
	}
	
	public String getQuantityString(){
		return String.format("%,.2f",quantity);
	}
	
	public String toString(){
		return fullname +" "+familyPlanningServiceName +" "+getFormattedServiceDate()+" for "+ this.getFormattedScheduleDate();
	}
	
	public boolean equals(FamilyPlanningRecord record){
		if(record.getId()==familyPlanningRecordId){
			return true;
		}
		return false;
	}
}
