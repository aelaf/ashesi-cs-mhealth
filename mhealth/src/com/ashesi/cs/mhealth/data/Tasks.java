package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.ashesi.cs.mhealth.DataClass;

public class Tasks extends DataClass {
	
	public Tasks(Context context){
		super(context);
	}
	
	public String getODPFollowupTask(){
		OPDCaseRecords records=new OPDCaseRecords(getContext());
		int n=records.getOPDCasesFollowupCount();
		if(n<0){
			return "Error getting OPD cases to follow up from last 30 days";
		}else{
			return "OPD cases to follow up from last 30 days: "+n;
		}
	}
	
	public String getVaccineTask(){
		VaccineRecords records=new VaccineRecords(getContext());
		int n=records.getVaccineCountForTheMonth();
		if(n<0){
			return "Error while gettting Number of people to vaccine ";
		}else{
			return "Number of community members due for vaccine :"+n;
		}
	}
	
	protected String getFamilyPlanningTask(){
		FamilyPlanningRecords records=new FamilyPlanningRecords(getContext());
		int n=records.getNumberCommunityMembersScheduledForFamilyPlanning();
		if(n<0){
			return "Error while gettting number of community members due for FP ";
		}else{
			return "Number of community members due for FP :"+n;
		}
	}
	
	public String getCommunityMembersCount(){
		CommunityMembers members=new CommunityMembers(getContext());
		int n=members.getCommunityMembersCount();
		if(n<0){
			return "Error number of community members ";
		}else{
			return "Number of community members: "+n;
		}
	}
	
	public ArrayList<String> getTaskForTheMonth(){
		ArrayList<String> list=new ArrayList<String>();
		list.add("Tasks for the month:" );
		list.add(getVaccineTask());
		list.add(getFamilyPlanningTask());
		list.add(getODPFollowupTask());
		list.add("Other tasks ...");
		list.add("Data Summary:" );
		list.add(getCommunityMembersCount());
		return list;
	}
	
	public ArrayAdapter<String> getTaskAdapterForTheMonth(){
		ArrayList<String> list=getTaskForTheMonth();
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(), R.layout.mhealth_simple_list_item, list);
		return adapter;
	}


}
