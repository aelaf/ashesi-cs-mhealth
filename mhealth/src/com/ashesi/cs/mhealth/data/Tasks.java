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
	
	public int getNumberCommunityMembersToVaccine(int past, int future){
		try
		{
			db=getReadableDatabase();
			
			String strQuery="select count("+ CommunityMembers.COMMUNITY_MEMBER_ID  +") as NO_REC from "+ 
								"(select  "+ CommunityMembers.COMMUNITY_MEMBER_ID + 
								" from " + Vaccines.VIEW_PENDING_VACCINES +" inner join "
								+ CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS
								+" using ("+CommunityMembers.COMMUNITY_MEMBER_ID+ ") "
								+" where   (scheduled_on> "+past+" AND scheduled_on < "+future
								+") group by "+CommunityMembers.COMMUNITY_MEMBER_ID+")";
								
					
					
			cursor=db.rawQuery(strQuery,null);
			cursor.moveToFirst();
			int n=cursor.getInt(0);
			close();
			return n;
		}catch(Exception ex){
			return -1;
		}
	}
	
	public int getVaccineCountForTheMonth(){
		Calendar calendar=Calendar.getInstance();
		int past=calendar.get(Calendar.DAY_OF_MONTH);
		int future=calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-past;
		
		return getNumberCommunityMembersToVaccine(past,future);
	}
	
	public String getVaccineTask(){
		int n=getVaccineCountForTheMonth();
		if(n<0){
			return "Error while gettting Number of people to vaccine ";
		}else{
			return "Number of community members due for vaccine :"+n;
		}
	}
	
	public int getNumberCommunityMembersScheduledForFamilyPlanning(){
		try{
			db=getReadableDatabase();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			Calendar calendar=Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			String firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			String lastDateOfTheMonth=dateFormat.format(calendar.getTime());
			
			String strQuery="select count(*) as NO_REC from "+FamilyPlanningRecords.TABLE_NAME_FAMILY_PLANNING_RECORDS 
						+" where ("+FamilyPlanningRecords.SCHEDULE_DATE +">\""+firstDateOfTheMonth +"\""
							+" AND "+FamilyPlanningRecords.SCHEDULE_DATE +">\""+lastDateOfTheMonth +"\")";
			
			cursor=db.rawQuery(strQuery,null);
			cursor.moveToFirst();
			int n=cursor.getInt(0);
			close();
			return n;
		}catch(Exception ex){
			return -1;
		}
	}
	
	protected String getFamilyPlanningTask(){
		int n=getNumberCommunityMembersScheduledForFamilyPlanning();
		if(n<0){
			return "Error while gettting number of community members due for FP ";
		}else{
			return "Number of community members due for FP :"+n;
		}
	}
	
	public ArrayList<String> getTaskForTheMonth(){
		ArrayList<String> list=new ArrayList<String>();
		list.add("Tasks for the month" );
		list.add(getVaccineTask());
		list.add(getFamilyPlanningTask());
		return list;
	}
	
	public ArrayAdapter<String> getTaskAdapterForTheMonth(){
		ArrayList<String> list=getTaskForTheMonth();
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(), R.layout.mhealth_simple_list_item, list);
		return adapter;
	}

}
