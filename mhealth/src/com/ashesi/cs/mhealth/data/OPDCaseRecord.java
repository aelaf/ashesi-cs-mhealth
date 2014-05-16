package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONObject;

public class OPDCaseRecord {

	private int recNo;
	private int communityMemberId;
	private int opdCaseId;
	private String recDate;
	private String fullname;
	private String opdCaseName;
	private int choId;
	private String lab;
	//private int serverRecNo;
	//private int newRec;
	
	
	public OPDCaseRecord(int recNo,int communityMemberId,int opdCaseId,String recDate,String fullname,String opdCaseName,int choId){
		this.recNo=recNo;
		this.communityMemberId=communityMemberId;
		this.opdCaseId=opdCaseId;
		this.recDate=recDate;
		this.fullname=fullname;
		this.opdCaseName=opdCaseName;
		this.choId=choId;
		this.lab=OPDCaseRecords.LAB_NOT_CONFIRMED;
		
	}
	
	public OPDCaseRecord(int recNo,int communityMemberId,int opdCaseId,String recDate,String fullname,String opdCaseName,int choId,String lab){
		this.recNo=recNo;
		this.communityMemberId=communityMemberId;
		this.opdCaseId=opdCaseId;
		this.recDate=recDate;
		this.fullname=fullname;
		this.opdCaseName=opdCaseName;
		this.choId=choId;
		this.lab=lab;
		
	}
	
	public int getRecNo(){
		return recNo;
	}
	
	public int getCommunityMemberId(){
		return communityMemberId;
	}
	
	public int getOPDCaseId(){
		return opdCaseId;
	}
	
	public String getRecDate(){
		return recDate;
	}
	
	public String getFormatedRecDate(){
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-d",Locale.UK);
			java.util.Date date=dateFormat.parse(recDate);
			dateFormat=new SimpleDateFormat("d/MM/yyyy",Locale.UK);
			return dateFormat.format(date);
		}
		catch(Exception ex){
			return "";
		}
		
	}
	
	
	public String getFullname(){
		return fullname;
	}
	
	public String getOPDCaseName(){
		return opdCaseName;
	}
	
	public int getCHOId(){
		return choId;
	}
	
	public String toString(){
		String str="not lab confirmed";
		if(isLab()){
			str="lab confirmed";
		}
		return fullname +", "+ opdCaseName  +", " +str+", "+getFormatedRecDate();
		
	}
	
	public void setLab(String lab){
		this.lab=lab;
	}
	
	public String getLab(){
		if(this.lab==null){
			return OPDCaseRecords.LAB_NOT_CONFIRMED;
		}
		
		return this.lab;
	}
	
	public boolean isLab(){
		if(this.lab==null){
			return false;
		}
		if(this.lab.equalsIgnoreCase(OPDCaseRecords.LAB_CONFIRMED)){
			return true;
		}else{
			return false;
		}
	}

	public String getJSON(){
		JSONObject obj=new JSONObject();
		try
		{
			obj.put("recNo",recNo);
			obj.put("communityMemberId", communityMemberId);
			obj.put("opdCaseId", opdCaseId);
			obj.put("choId",choId);
			obj.put("recDate",recDate);
			obj.put("lab",lab);
			return obj.toString();
		}catch(Exception ex){
			return "{}";
		}
	
	}
}
