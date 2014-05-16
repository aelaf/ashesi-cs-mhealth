package com.ashesi.cs.mhealth.data;

public class CHO {
	private String fullname;
	private int id;
	private int subdistrictId;
	private String subdistrictName;
	
	public CHO(int id, String fullname,int subdistrictId, String subdistrictName){
		this.id=id;
		this.fullname=fullname;
		this.subdistrictId=subdistrictId;
		this.subdistrictName=subdistrictName;
				
	}
	public int getId(){
		return id;
	}
	
	public String getFullname(){
		return fullname;
	}
	
	public int getSubdistrictId(){
		return subdistrictId;
	}
	
	public String getSubdistrictName(){
		return subdistrictName;
	}
	
	public String toString(){
		return fullname +" - "+ subdistrictName;
	}
	
	public String getAll(){
		return fullname +" - "+ subdistrictId+ " - "+ id;
	}
}
