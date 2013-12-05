package com.ashesi.cs.mhealth.data;

public class OPDCase {
	private int id;
	private String opdCaseName;
	private int category;
	
	public OPDCase(){
		
	}
	public OPDCase(int id, String opdCaseName){
		this.id=id;
		this.opdCaseName=opdCaseName;
	}
	
	public OPDCase(int id, String opdCaseName, int category){
		this.id=id;
		this.opdCaseName=opdCaseName;
		this.category=category;
	}
	
	public int getID(){
		return id;
	}
	
	public String getOPDCaseName(){
		return opdCaseName;
	}
	
	public int getCategory(){
		return category;
	}
	
	public String toString(){
		return opdCaseName;
	}
	
	
	
}
