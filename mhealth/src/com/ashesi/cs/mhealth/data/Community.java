package com.ashesi.cs.mhealth.data;

public class Community {
	private int id;
	private String communityName;
	private int subdistrictId;
	private String latitude;
	private String longitude;
	private int household;
	private int population;
	
	public Community(int id, String communityName){
		this.id=id;
		this.communityName=communityName;
		this.subdistrictId=0;
		this.latitude="0";
		this.longitude="0";
		this.household=0;
		this.population=0;
	}
	
	public Community(int id, String communityName, int subdistrictId,String latitude,String longitude,int household,int population){
		this.id=id;
		this.communityName=communityName;
		this.subdistrictId=subdistrictId;
		this.latitude=latitude;
		this.longitude=longitude;
		this.household=household;
		this.population=population;
		
	}
	
	public int getId(){
		return id;
	}
	
	public String getCommunityName(){
		return communityName;
	}
	
	public String toString(){
		return communityName;
	}
	
	public int getSubdistrictId(){
		return subdistrictId;
	}
	
	public String getLatitude(){
		return latitude;
	}
	
	public String getLongitude(){
		return longitude;
	}
	
	public int getPopulation(){
		return population;
	}
	public int getHousehold(){
		return household;
	}
	
	public boolean equals(Community community){
		if(id==community.getId()){
			return true;
		}
		return false;
	}
	
	
	
}
