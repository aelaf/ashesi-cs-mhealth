package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FamilyPlanningService {

	private int id;
	private String serviceName;
	private int schedule=0;
	private int displayOrder;
	
	
	public FamilyPlanningService(){
		
	}
	
	public FamilyPlanningService(int id,String serviceName){
		this.id=id;
		this.serviceName=serviceName;
		this.schedule=0;
	}
	
	public FamilyPlanningService(int id,String serviceName,int schedule,int displayOrder){
		this.id=id;
		this.serviceName=serviceName;
		this.schedule=schedule;
		this.displayOrder=displayOrder;
	}
	
	public int getId(){
		return id;
	}
	
	public String getItemName(){
		return serviceName;
	}
	
	public String toString(){
		return serviceName;
	}
	
	public int getSchedule(){
		return schedule;
	}
	
	public int getDisplayOrder(){
		return displayOrder;
	}
	
	public Date getScheduleDate(Date date){
		Calendar calendar=Calendar.getInstance();
		if(date!=null){
			calendar.setTime(date);
		}
		calendar.add(Calendar.DAY_OF_MONTH, schedule);
		return calendar.getTime();
	}
	
	public String getFormatedScheduleDate(Date date){
		SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
		date=getScheduleDate(date);
		return format.format(date);
		
	}
	
}
