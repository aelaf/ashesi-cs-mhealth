package com.ashesi.cs.mhealth.data;

public class FamilyPlanningService {

	private int id;
	private String serviceName;
	
	public FamilyPlanningService(){
		
	}
	
	public FamilyPlanningService(int id,String serviceName){
		this.id=id;
		this.serviceName=serviceName;
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
	

}
