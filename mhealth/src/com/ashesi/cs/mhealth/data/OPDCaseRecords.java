package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class OPDCaseRecords extends DataClass {
	public static final String LAB_CONFIRMED="true";
	public static final String LAB_NOT_CONFIRMED="false";
	public static final String REC_NO="rec_no";
	public static final String REC_DATE="rec_date";
	public static final String OPD_CASE_NAME="opd_case_name";
	public static final String LAB="lab";
	public static final String SERVER_REC_NO="server_rec_no";
	public static final String TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES="community_members_opd_cases";
	public OPDCaseRecords(Context context){
		super(context);
	}
	/**
	 * Returns recorded opd cases for one communityMember 
	 * @param communityMemberId
	 * @return
	 */
	public boolean getCommunityMemberOPDCases(int communityMemberId){
		try
		{
			db=getReadableDatabase();
			String[] columns={REC_NO,CommunityMembers.COMMUNITY_MEMBER_ID, OPDCases.OPD_CASE_ID,
						CommunityMembers.COMMUNITY_MEMBER_NAME,OPD_CASE_NAME,REC_DATE,LAB,CHOs.CHO_ID};
			String selection=CommunityMembers.COMMUNITY_MEMBER_ID+"="+communityMemberId;
			
			cursor=db.query(DataClass.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES, columns, 
					selection, null, null, null, null );
				return true;
		}catch(Exception ex){
			Log.e("OPDCaseRecords.getCommunityMemberOPDCases","Exception :"+ex.getMessage());
			return false;
		}
	
	}
	/**
	 * fetches a row from the cursor and return it as object 
	 * @return
	 */
	public OPDCaseRecord fetch(){
		try
		{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			String fullname="";
			String opdCaseName="";
			String lab=OPDCaseRecords.LAB_NOT_CONFIRMED;
			int index=cursor.getColumnIndex(REC_NO);
			int recNo=cursor.getInt(index);
			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_ID);
			int communityMemberId=cursor.getInt(index);
			index=cursor.getColumnIndex(OPDCases.OPD_CASE_ID);
			int opdCaseId=cursor.getInt(index);
			index=cursor.getColumnIndex(REC_DATE);
			String recDate=cursor.getString(index);
			index=cursor.getColumnIndex(CHOs.CHO_ID);
			int choId=cursor.getInt(index);
			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_NAME);
			if(index>=0){
				fullname=cursor.getString(index);
			}
			index=cursor.getColumnIndex(OPD_CASE_NAME);
			if(index>=0){
				opdCaseName=cursor.getString(index);
			}
			index=cursor.getColumnIndex(LAB);
			if(index>=0){
				lab=cursor.getString(index);
			}
			OPDCaseRecord opdCaseRecord=new OPDCaseRecord(recNo,communityMemberId,opdCaseId,recDate,fullname,opdCaseName,choId,lab);
			cursor.moveToNext();
			return opdCaseRecord;
		}catch(Exception ex){
			Log.e("OPDCaseRecord.fetch","Excption ex"+ex.getMessage());
			return null;
		}
					
		
	}
	
	public String getForUpload(){
		String str="";
		try
		{
			db=getReadableDatabase();
			String strQuery="select "+REC_NO
								+"," +OPDCases.OPD_CASE_ID
								+"," +CommunityMembers.COMMUNITY_MEMBER_NAME
								+"," +OPDCases.OPD_CASE_NAME
								+"," +CommunityMembers.COMMUNITY_MEMBER_ID
								+"," +REC_DATE
								+"," +CHOs.CHO_ID
								+" from "+ DataClass.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES 
								+" where "+ DataClass.REC_STATE +"=" +DataClass.REC_STATE_NEW;
								
			
			cursor=db.rawQuery(strQuery, null);
	
			OPDCaseRecord obj=fetch();
			while(obj!=null){
				str+=obj.getJSON();
				obj=fetch();
				if(obj!=null){
					str+=",";
				}
			}
			close();
			return "["+str+"]";
		}catch(Exception ex){
			return str;
		}
		
			
	}
	/**
	 * Get array list of OPD case records in the cursor. Used after getCommunityMemberOpdCases
	 * @return
	 */
	public ArrayList<OPDCaseRecord> getArrayList(){
		ArrayList<OPDCaseRecord> list=new ArrayList<OPDCaseRecord>();
		OPDCaseRecord opdCaseRecord=fetch();
		while(opdCaseRecord!=null){
			list.add(opdCaseRecord);
			opdCaseRecord=fetch();
		}
		close();
		return list;
	}
	
	public boolean removeOPDRecord(int recNo){
		//TODO: after upload, remove should be handled differently 
		db=getWritableDatabase();
		String whereClause= REC_NO+"="+recNo;
		if(db.delete(TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES,whereClause , null)<=0){
			return false;
		}
		close();
		return true;
	}
	
	/**
	 * returns a report is ArrayList to be displayed in a GridView of 3 columns 
	 * @param month	0 this month, 1 this year, 2 Jan, 3 Feb etc
	 * @param year	year of reporting
	 * @param ageRange	
	 * @param gender not used
	 * @return
	 */
	public ArrayList<String> getMontlyReport(int month,int year, int ageRange, String gender){
		//define period for the report
		String firstDateOfTheMonth;
		String lastDateOfTheMonth;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
		Calendar calendar=Calendar.getInstance();
		if(month==0){ //this month
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}else if(month==1){	//this year
			calendar.set(Calendar.YEAR,year);
			calendar.set(Calendar.MONTH,Calendar.JANUARY);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(Calendar.MONTH,Calendar.DECEMBER);
			calendar.set(Calendar.DAY_OF_MONTH,31);
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}else{	//selected month and year
			month=month-2;
			calendar.set(year, month, 1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(year,month,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}
		
		//define age range
		int[] limit={0,1,5,10,15,18,20,35,50,60,70};
		String strAgeFilter=" 1 ";
		if(ageRange>0){//if it is not total
			ageRange=ageRange-1;
			if(ageRange==0){
				strAgeFilter=CommunityMembers.AGE+"<1";	//under 1 year
			}else if(ageRange>=1 && ageRange<10){	//compute range
				strAgeFilter="("+CommunityMembers.AGE+">="+limit[ageRange]+" AND "+CommunityMembers.AGE+"<"+limit[ageRange+1]+")";
			}else{	
				strAgeFilter=CommunityMembers.AGE+">=70";
			}
		}
		//query report for the age range, period grouped by gender and OPD case
		try
		{
			db=getReadableDatabase();
			
			String strQuery="select "+OPDCases.OPD_CASE_ID
								+"," +OPDCases.OPD_CASE_NAME
								+","+CommunityMembers.GENDER
								+", count(" +REC_NO +") AS "+DataClass.NO_CASES
								+" from "+DataClass.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES
								+" where "
								+REC_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
								+REC_DATE +"<=\""+ lastDateOfTheMonth + "\" AND "
								+strAgeFilter
								+" group by "+OPDCases.OPD_CASE_ID
								+", "+CommunityMembers.GENDER
								+" order by "+OPDCases.OPD_CASE_NAME
								+", "+CommunityMembers.GENDER;
			
			cursor=db.rawQuery(strQuery, null);
			ArrayList<String> list=new ArrayList<String>();		
			
			cursor.moveToFirst();
			
			int indexOPDCaseName=cursor.getColumnIndex(OPDCases.OPD_CASE_NAME);
			int indexNoCases=cursor.getColumnIndex(DataClass.NO_CASES);
			int indexGender=cursor.getColumnIndex(CommunityMembers.GENDER);
			String str="";
			while(!cursor.isAfterLast()){
				str=cursor.getString(indexOPDCaseName);
				list.add(str);
				str=cursor.getString(indexGender);
				list.add(str);
				str=Integer.toString(cursor.getInt(indexNoCases));
				list.add(str);
				
				cursor.moveToNext();
			}
			close();
			return list;
		}catch(Exception ex){
			return null;
		}
		
	}
	
	public boolean upload(){
		final int deviceId=mDeviceId;
		Log.d("OPDCases.synch", "synch called");
		new Thread(new Runnable() {
	        public void run() {
	 
	        	String postData=getForUpload();
	        	String data=request("opdcasesActions.php?cmd=2&deviceId="+deviceId, postData);
	            processUploadResult(data);
	        }
	    }).start();
		
		return true;
	}
	
	private void processUploadResult(String data){
		try{
			JSONObject obj=new JSONObject(data);
			if(obj.getInt("result")==0){
				return;
			}
			JSONArray jsonArray=obj.getJSONArray("ids");
			updateRecordsAfterUpload(jsonArray);
		}catch(Exception ex){
			return;
		}
	}
	
	private void updateRecordsAfterUpload(JSONArray jsonArray){
		try
		{
			int recNo=0;
			int serverRecNo=0;
			db=getWritableDatabase();
			for(int i=0;i<jsonArray.length();i++){
				JSONObject obj=jsonArray.getJSONObject(0);
				recNo=obj.getInt("lid");
				serverRecNo=obj.getInt("sid");
				ContentValues values=new ContentValues();
				String whereClause=REC_NO+"="+recNo;
				values.put(SERVER_REC_NO, serverRecNo);
				values.put(DataClass.REC_STATE, DataClass.REC_STATE_UPTODATE);
				db.update(TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES, values, whereClause, null);
			}
			
		
			close();
			
		}catch(Exception ex){
			return;
		}
	}
	
	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES +"( "
				+REC_NO +" integer primary key,"
				+CommunityMembers.COMMUNITY_MEMBER_ID +" integer,"
				+OPDCases.OPD_CASE_ID+" integer,"
				+CHOs.CHO_ID+ " integer, "
				+REC_DATE+ " text, "
				+SERVER_REC_NO+ " integer, "
				+REC_STATE+" integer, "
				+LAB+ " text "
				+" )";
	}
	
	public static String getCreateViewString(){
		return "create view "+ VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES +" as select "
				+ REC_NO +", "
				+ TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES+ "." + CommunityMembers.COMMUNITY_MEMBER_ID +", "
				+ TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES+ "." +OPDCases.OPD_CASE_ID+", "
				+ CHOs.CHO_ID+", "
				+ REC_DATE+", "
				+ CommunityMembers.COMMUNITY_MEMBER_NAME +", "
				+ TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES +"." +REC_STATE+","
				+ CommunityMembers.BIRTHDATE +", "
				+ CommunityMembers.GENDER +", "
				+ " ((julianday("+ REC_DATE +")- julianday("+ CommunityMembers.BIRTHDATE +"))/366) AS AGE, "
				+ OPD_CASE_NAME+", "
				+ LAB 
				+ " from "
				+ TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES
				+ " left join " + CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
				+ " on " + TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID +"=" +
								CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS +"."+CommunityMembers.COMMUNITY_MEMBER_ID
				+ " left join " + OPDCases.TABLE_NAME_OPD_CASES
				+ " ON " + TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES+ "."+ OPDCases.OPD_CASE_ID +"=" +
				OPDCases.TABLE_NAME_OPD_CASES +"."+OPDCases.OPD_CASE_ID;
	}
}
