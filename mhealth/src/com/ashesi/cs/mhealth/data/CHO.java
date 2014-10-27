package com.ashesi.cs.mhealth.data;

public class CHO {
	private String fullname;
	private int id;
	private int chpsZoneId;
	private String chpsZoneName;
	
	public CHO(int id, String fullname,int chpsZoneId, String chpsZoneName){
		this.id=id;
		this.fullname=fullname;
		this.chpsZoneId=chpsZoneId;
		this.chpsZoneName= chpsZoneName;
				
	}
	public int getId(){
		return id;
	}
	
	public String getFullname(){
		return fullname;
	}
	
	public int getSubdistrictId(){
		return chpsZoneId;
	}
	
	public int getCHPSZoneId(){
		return chpsZoneId;
	}
	
	public String getSubdistrictName(){
		return chpsZoneName;
	}
	
	public String toString(){
		return fullname +" - "+ chpsZoneName;
	}
	
	public String getAll(){
		return fullname +" - "+ chpsZoneName;
	}
}
