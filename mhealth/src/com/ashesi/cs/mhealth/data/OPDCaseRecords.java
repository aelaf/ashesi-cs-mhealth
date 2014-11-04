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
	public static final String NO_CASES="no_cases";
	public static final String TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES="community_members_opd_cases";
	public static final String VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES="view_community_member_opd_cases";
	public static final String VIEW_COMMUNITIY_MEMBERS_IN_OPD_CASES="view_community_members_in_opd_cases";
	
	public static final int GROUP_BY_COMMUNITY=1;
	public static final int GROUP_BY_OPD_CASE=2;
	public static final int GROUP_BY_GENDER=3;
	public static final int GROUP_BY_NONE=0;
	
	//define age range
	private double[] ageLimit={0,0.076712329,1,5,10,15,18,20,35,50,60,70};
	
	public OPDCaseRecords(Context context){
		super(context);
	}
	/**
	 * Returns recorded opd cases for one communityMember 
	 * NNA; modified to return all records if commuityMemberId is 0
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
			if (communityMemberId==0){  //to allow selecting all records.
				selection=null;  
				}
			cursor=db.query(VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES, columns, 
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
								+" from "+ VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES 
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
		String strAgeFilter=" 1 ";
		if(ageRange>0){//if it is not total
			ageRange=ageRange-1;
			if(ageRange==0){
				strAgeFilter=CommunityMembers.AGE+"<" +Double.toString(ageLimit[1]);	//under 28 days year
			}else if(ageRange>=1 && ageRange<10){	//compute range
				strAgeFilter="("+CommunityMembers.AGE+">="+ageLimit[ageRange]+" AND "+CommunityMembers.AGE+"<"+ageLimit[ageRange+1]+")";
			}else{	
				strAgeFilter=CommunityMembers.AGE+">=70";
			}
		}
		
		String strGenderFilter=" 1 ";
		if(gender != null){
			if(!gender.equals("all")){
				strGenderFilter=CommunityMembers.GENDER +" = '"+gender+"'";
			}
		}
		
		//query report for the age range, period grouped by gender and OPD case
		try
		{
			db=getReadableDatabase();
			
			String strQuery="select "+OPDCases.OPD_CASE_ID
								+"," +OPDCases.OPD_CASE_NAME
								+","+CommunityMembers.GENDER
								+", count(" +REC_NO +") AS "+NO_CASES
								+" from "+VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES
								+" where "
								+REC_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
								+REC_DATE +"<=\""+ lastDateOfTheMonth + "\" AND "
								+strAgeFilter+ " AND "
								+strGenderFilter
								+" group by "+OPDCases.OPD_CASE_ID
								+", "+CommunityMembers.GENDER
								+" order by "+OPDCases.OPD_CASE_NAME
								+", "+CommunityMembers.GENDER;
			
			cursor=db.rawQuery(strQuery, null);
			ArrayList<String> list=new ArrayList<String>();		
			
			cursor.moveToFirst();
			
			int indexOPDCaseName=cursor.getColumnIndex(OPDCases.OPD_CASE_NAME);
			int indexNoCases=cursor.getColumnIndex(NO_CASES);
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
	
	/**
	 * Returns a report on the number of total male and female community members on record in the given select condition
	 * @param month
	 * @param year
	 * @param ageRange
	 * @param gender
	 * @return
	 */
	public ArrayList<String> getMontlyTotalsReport(int month,int year, int ageRange, int  newClient){
		//query report for the age range, period grouped by gender and OPD case
		ArrayList<String>list=new ArrayList<String>();
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

		String strAgeFilter=" 1 ";
		if(ageRange>0){//if it is not total
			ageRange=ageRange-1;
			if(ageRange==0){
				strAgeFilter=CommunityMembers.AGE+"<" +Double.toString(ageLimit[1]);	//under 1 year
			}else if(ageRange>=1 && ageRange<10){	//compute range
				strAgeFilter="("+CommunityMembers.AGE+">="+ageLimit[ageRange]+" AND "+CommunityMembers.AGE+"<"+ageLimit[ageRange+1]+")";
			}else{	
				strAgeFilter=CommunityMembers.AGE+">=70";
			}
		}

		String strNewClientFilter=" 1 ";
		if(newClient==1){	//new
			//Registered with in the elected period
			strNewClientFilter= "("+CommunityMembers.FIRST_ACCESS_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
								+CommunityMembers.FIRST_ACCESS_DATE +"<=\""+ lastDateOfTheMonth+"\")";
		}else if(newClient==2){ //old
			//Registered with in the elected period
			strNewClientFilter= "("+CommunityMembers.FIRST_ACCESS_DATE +"<\""+ firstDateOfTheMonth +"\")";
		}
		String filter=" (select "
				+ CommunityMembers.COMMUNITY_MEMBER_ID 
				+ " from "+ OPDCaseRecords.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES 
				+ " where "
				+REC_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
				+REC_DATE +"<=\""+ lastDateOfTheMonth + "\" AND "
				+strAgeFilter +" ) ";	
		
		
		try
		{
			db=getReadableDatabase();
			//get number of community members who had opd cases male and female
			String strQuery="select 'All Communities' as "+Communities.COMMUNITY_NAME
							+ ", "+CommunityMembers.GENDER +", "
							+" count(*) as NO_REC "
							+" from  "
							+CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS
							+" where "
							+ strNewClientFilter  +" AND "
							+ CommunityMembers.COMMUNITY_MEMBER_ID +" in "
							+ filter
							+" group by " +CommunityMembers.GENDER;
		
			cursor=db.rawQuery(strQuery, null);
			
			cursor.moveToFirst();
			int indexCommunityName=cursor.getColumnIndex(Communities.COMMUNITY_NAME);
			int indexGender=cursor.getColumnIndex(CommunityMembers.GENDER);
			int indexNoRecords=cursor.getColumnIndex("NO_REC");
			String str="";
			while(!cursor.isAfterLast()){
				str=cursor.getString(indexCommunityName);	//string 1
				list.add(str);
				str=cursor.getString(indexGender);		
				list.add(str);							//string 2
				str=Integer.toString(cursor.getInt(indexNoRecords));
				list.add(str);							//string 3
				
				cursor.moveToNext();
			}
			
			// get the count group by community
			strQuery="select "
					+Communities.COMMUNITY_NAME +","
				    +CommunityMembers.GENDER 
				    +",count(*) as NO_REC "
				    +" from  "
					+CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS
					+" where "
					+ CommunityMembers.COMMUNITY_MEMBER_ID +" in "
					+ filter
					+" group by "
					+Communities.COMMUNITY_ID +", "	
					+CommunityMembers.GENDER;
			cursor=db.rawQuery(strQuery, null);
			
			cursor.moveToFirst();
			indexCommunityName=cursor.getColumnIndex(Communities.COMMUNITY_NAME);
			indexGender=cursor.getColumnIndex(CommunityMembers.GENDER);
			indexNoRecords=cursor.getColumnIndex("NO_REC");
			
			while(!cursor.isAfterLast()){
				str=cursor.getString(indexCommunityName);	//string 1
				list.add(str);
				str=cursor.getString(indexGender);		
				list.add(str);							//string 2
				str=Integer.toString(cursor.getInt(indexNoRecords));
				list.add(str);							//string 3
				
				cursor.moveToNext();
			}
			return list;

		}catch(Exception ex){
			return list;
		}
	}
	
	public ArrayList<OPDCaseRecord>getMonthReportDetail(int month,int year, int ageRange, String gender,int page){
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
		
		
		String strAgeFilter=" 1 ";
		if(ageRange>0){//if it is not total
			ageRange=ageRange-1;
			if(ageRange==0){
				strAgeFilter=CommunityMembers.AGE+ Double.toString(ageLimit[1]);	//under 1 year
			}else if(ageRange>=1 && ageRange<10){	//compute range
				strAgeFilter="("+CommunityMembers.AGE+">="+ageLimit[ageRange]+" AND "+CommunityMembers.AGE+"<"+ageLimit[ageRange+1]+")";
			}else{	
				strAgeFilter=CommunityMembers.AGE+">=70";
			}
		}
		
		String strGenderFilter=" 1 ";
		if(gender != null){
			if(!gender.equals("all")){
				strGenderFilter=CommunityMembers.GENDER +" = '"+gender+"'";
			}
		}
		
		String limit="";
		if(page>=0){
			page=page*15;
			limit=" limit " +page +",15";
		}
		//query report for the age range, period grouped by gender and OPD case
		try
		{
			db=getReadableDatabase();
			
			String strQuery="select "
								+REC_NO
								+","+ CommunityMembers.COMMUNITY_MEMBER_ID
								+","+ OPDCases.OPD_CASE_ID
								+ ","+CHOs.CHO_ID
								+ ","+ REC_DATE
								+"," +CommunityMembers.COMMUNITY_MEMBER_NAME
								+"," +REC_STATE
								+"," +CommunityMembers.BIRTHDATE
								+"," + "AGE"
								+"," +LAB
								+"," +OPDCases.OPD_CASE_NAME
								+","+CommunityMembers.GENDER
						
								+" from "+VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES
								+" where "
								+REC_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
								+REC_DATE +"<=\""+ lastDateOfTheMonth + "\" AND "
								+strAgeFilter + " AND "
								+strGenderFilter
								+limit;
				
								
			
			cursor=db.rawQuery(strQuery, null);
			ArrayList<OPDCaseRecord> list=new ArrayList<OPDCaseRecord>();
			OPDCaseRecord record=fetch();
			
			while(record!=null){
				list.add(record);
				record=fetch();
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
				+ CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+ CommunityMembers.COMMUNITY_ID +", "
				+ Communities.COMMUNITY_NAME+", "
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
				OPDCases.TABLE_NAME_OPD_CASES +"."+OPDCases.OPD_CASE_ID
				+" left join " +Communities.TABLE_COMMUNITIES +" on " 
				+ CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS + "."+Communities.COMMUNITY_ID +"=" 
				+Communities.TABLE_COMMUNITIES +"." +Communities.COMMUNITY_ID;
	}	
	
	public String fetchSQLDumpToUpload(){
   	 StringBuilder OPDCasesData = new StringBuilder("Replace into community_members_opd_cases  " +
 	 		"(rec_no, community_member_id, opd_case_id, cho_id, rec_date, server_rec_no, rec_state, lab) VALUES ");
   	 
   	getCommunityMemberOPDCases(0); //in order to create a cursor.
 	 ArrayList<OPDCaseRecord> OPDCaseRecordsRawData= getArrayList();
 	 if (OPDCaseRecordsRawData.size()!=0){
	    	 for(OPDCaseRecord oneOPDCaseRecord: OPDCaseRecordsRawData){    		 
	    		 OPDCasesData.append("('"+oneOPDCaseRecord.getRecNo()+"',");  //includes starting brace
	    		 OPDCasesData.append("'"+oneOPDCaseRecord.getCommunityMemberId()+"',");
	    		 OPDCasesData.append("'"+oneOPDCaseRecord.getOPDCaseId()+"',");
	    		 OPDCasesData.append("'"+oneOPDCaseRecord.getCHOId()+"',");
	    		 OPDCasesData.append("'"+oneOPDCaseRecord.getRecDate()+"',");
	    		 OPDCasesData.append("'"+"',"); //server rec no not included
	    		 OPDCasesData.append("'"+"',"); //rec state not included
	    		 OPDCasesData.append("'"+oneOPDCaseRecord.getLab()+"'),");
	    	 }
	    	 OPDCasesData.setLength(Math.max(OPDCasesData.length() - 1, 0))  ; 
	    	 
	    	 return OPDCasesData.toString();
 	 }//if   	 
 	 return null;
	}
}
