package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.data.FamilyPlanningReport.FamilyPlanningReportRecord;

/**
 * Reads and writes family planning service record of community member to a table
 * @author Aelaf Dafla
 *
 */
public class FamilyPlanningRecords extends DataClass {
	public final static String SERVICE_REC_ID="service_rec_id";
	public final static String SERVICE_NAME="service_name";
	public final static String SERVICE_DATE="service_date";
	public final static String SCHEDULE_DATE="service_schedule_date";
	public final static String QUANTITY="quantity";
	public final static String SERVICE_TYPE="service_type_id";
	public final static String TABLE_NAME_FAMILY_PLANNING_RECORDS="family_planning_records";
	public final static String VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL="view_family_planning_records_detail";
	public final static String AGE="age";
	public final static String AGE_DAYS="age_days";
			
	public FamilyPlanningRecords(Context context){
		super(context);
	}
	
	 /**
	 * records when a community member was received with particular service identified by vaccineId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, String serviceDate,double quanity){
		try{
						
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(CommunityMembers.COMMUNITY_MEMBER_ID, communityMemberId);
			cv.put(FamilyPlanningServices.SERVICE_ID,serviceId);
			cv.put(QUANTITY, quanity);
			cv.put(SERVICE_DATE, serviceDate);
			long id=db.insert(TABLE_NAME_FAMILY_PLANNING_RECORDS, null, cv);
			if(id<=0){
				return null;
			}
			return getServiceRecord((int)id);
			
		}catch(Exception ex){
			close();
			return null;
		}

	}
	
	/**
	 * records when a community member was received with particular service identified by vaccineId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, String serviceDate,double quanity, String scheduleDate){
		try{
						
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(CommunityMembers.COMMUNITY_MEMBER_ID, communityMemberId);
			cv.put(FamilyPlanningServices.SERVICE_ID,serviceId);
			cv.put(QUANTITY, quanity);
			cv.put(SERVICE_DATE, serviceDate);
			cv.put(SCHEDULE_DATE, scheduleDate);
			long id=db.insert(TABLE_NAME_FAMILY_PLANNING_RECORDS, null, cv);
			if(id<=0){
				return null;
			}
			return getServiceRecord((int)id);
			
		}catch(Exception ex){
			close();
			return null;
		}

	}
	
	/**
	 * records when a community member has received with particular service identified by serviceId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, String serviceDate,double quanity, String scheduleDate, int type){
		try{
						
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(CommunityMembers.COMMUNITY_MEMBER_ID, communityMemberId);
			cv.put(FamilyPlanningServices.SERVICE_ID,serviceId);
			cv.put(QUANTITY, quanity);
			cv.put(SERVICE_DATE, serviceDate);
			cv.put(SCHEDULE_DATE, scheduleDate);
			cv.put(SERVICE_TYPE, type);
			long id=db.insert(TABLE_NAME_FAMILY_PLANNING_RECORDS, null, cv);
			if(id<=0){
				return null;
			}
			return getServiceRecord((int)id);
			
		}catch(Exception ex){
			close();
			return null;
		}

	}
		
	public boolean alreadyAcceptor(int communityMemberId){
		try{
			
			db=getReadableDatabase();
			String columns[]={SERVICE_REC_ID};
			String selection=FamilyPlanningServices.SERVICE_ID+"="+Integer.toString(FamilyPlanningServices.NEW_ACCEPTOR_SERVICE_ID)
								+" and "+CommunityMembers.COMMUNITY_MEMBER_ID+"="+Integer.toString(communityMemberId);
			cursor=db.query(TABLE_NAME_FAMILY_PLANNING_RECORDS,columns, selection,null, null, null,null);
			
			if(cursor.getCount()>0){
				return true;
			}
			close();
			return false;
			
		}catch(Exception ex){
			
			close();
			return true;
		}
	}
	
	/**
	 * records when a community member was received with particular service identified by serviceId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, String serviceDate){
		return addRecord(communityMemberId,serviceId,serviceDate,0);

	}
	
	/**
	 * records when a community member was received with particular service identified by serviceId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, Date serviceDate,double quantity){
	   try{
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			String strDate=dateFormat.format(serviceDate);
			return addRecord(communityMemberId,serviceId,strDate,quantity);
		}catch(Exception ex){
			close();
			return null;
		}
			
	}
	
	/**
	 * records when a community member was received with particular service identified by vaccineId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, Date serviceDate,double quantity,Date scheduleDate){
	   try{
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			String strServiceDate=dateFormat.format(serviceDate);
			String strScheduleDate=dateFormat.format(scheduleDate);
			return addRecord(communityMemberId,serviceId,strServiceDate,quantity,strScheduleDate);
		}catch(Exception ex){
			close();
			return null;
		}
			
	}
	
	/**
	 * records when a community member was received with particular service identified by vaccineId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, Date serviceDate,double quantity,Date scheduleDate,int type){
	   try{
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			String strServiceDate=dateFormat.format(serviceDate);
			String strScheduleDate=dateFormat.format(scheduleDate);
			return addRecord(communityMemberId,serviceId,strServiceDate,quantity,strScheduleDate,type);
		}catch(Exception ex){
			close();
			return null;
		}
			
	}
	
	/**
	 * records when a community member was received with particular service identified by vaccineId 
	 * @param communityMemberId
	 * @param serviceId
	 * @param serviceDate
	 * @return
	 */
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, Date serviceDate){
		return addRecord(communityMemberId,serviceId,serviceDate,0);

	}
	
	//removes one service record form table 
	public boolean reomveRecord(int serviceRecId){
		try{
			db=getWritableDatabase();
			String whereClause= SERVICE_REC_ID +"="+serviceRecId;
			if(db.delete(TABLE_NAME_FAMILY_PLANNING_RECORDS, whereClause, null)<=0){
				return false;
			}
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	/**
	 * fetches a record from the current cursor
	 * @return
	 */
	public FamilyPlanningRecord fetch(){
		try{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index=cursor.getColumnIndex(SERVICE_REC_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_ID);
			int communityMemberId=cursor.getInt(index);
			index=cursor.getColumnIndex(FamilyPlanningServices.SERVICE_ID);
			int serviceId=cursor.getInt(index);
			index=cursor.getColumnIndex(SERVICE_DATE);
			String serviceDate=cursor.getString(index);
			index=cursor.getColumnIndex(QUANTITY);
			double quantity=cursor.getDouble(index);
			
			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_NAME);
			String fullname="";
			if(index>=0){
				fullname=cursor.getString(index);
			}
			String serviceName="";
			index=cursor.getColumnIndex(FamilyPlanningServices.SERVICE_NAME);
			if(index>=0){
				serviceName=cursor.getString(index);
			}
			String scheduleDate="";
			index=cursor.getColumnIndex(SCHEDULE_DATE);
			if(index>=0){
				scheduleDate=cursor.getString(index);
			}
			
			int serviceType=0;
			index=cursor.getColumnIndex(SERVICE_TYPE);
			if(index>=0){
				serviceType=cursor.getInt(index);
			}
			
			FamilyPlanningRecord record=new FamilyPlanningRecord(id,communityMemberId,fullname,serviceId,serviceName,serviceDate,quantity,scheduleDate,serviceType);
			cursor.moveToNext();
			return record;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * returns list of service record of one community member. It uses joint query
	 * @param communityMemberId
	 * @return
	 */
	public ArrayList<FamilyPlanningRecord> getServiceRecords(int communityMemberId){
		ArrayList<FamilyPlanningRecord> list=new ArrayList<FamilyPlanningRecord>();
		try{
			db=getReadableDatabase();
			String sql= FamilyPlanningRecords.getServiceRecordSQLString() 
					+" where "
					+TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID
					+"="+communityMemberId;
						
			cursor=db.rawQuery(sql, null);
			cursor.moveToFirst();
			FamilyPlanningRecord record=fetch();
			while(record!=null){
				list.add(record);
				record=fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			return list;
		}
		
	}
	
	public FamilyPlanningRecord getServiceRecord(int communityMemberId, int serviceId){
		try{
			db=getReadableDatabase();
			String sql= FamilyPlanningRecords.getServiceRecordSQLString() 
					+" where "
					+TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID
					+"="+communityMemberId
					+" AND "
					+TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ FamilyPlanningServices.SERVICE_ID +"="+serviceId;
						
			cursor=db.rawQuery(sql, null);
			cursor.moveToFirst();
			FamilyPlanningRecord record=fetch();
			
			close();
			return record;
		}catch(Exception ex){
			return null;
		}
	}
	
	public FamilyPlanningRecord getServiceRecord(int recID){
		try{
			db=getReadableDatabase();
			String sql= FamilyPlanningRecords.getServiceRecordSQLString() 
					+" where "
					+TABLE_NAME_FAMILY_PLANNING_RECORDS+ "." +FamilyPlanningRecords.SERVICE_REC_ID 
					+"=" +recID;
						
			cursor=db.rawQuery(sql, null);
			cursor.moveToFirst();
			FamilyPlanningRecord record=fetch();
			
			close();
			return record;
		}catch(Exception ex){
			return null;
		}
	}
	
	public ArrayList<FamilyPlanningRecord> getMonthlyFamilyPlanningRecords(int month,int year,int ageRange,String gender,int page){
		//define period for the report
		ArrayList<FamilyPlanningRecord> list=new ArrayList< FamilyPlanningRecord>();
		String firstDateOfTheMonth;
		String lastDateOfTheMonth;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
		Calendar calendar=Calendar.getInstance();
		if(month==0){ //this month
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}else if(month==1){	//this year/all year
			calendar.set(Calendar.YEAR, year);
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

		int[] limit={10,15,18,20,35,50,60,70};
		String strAgeFilter=" 1 ";
		if(ageRange>0){//if it is not total
			ageRange=ageRange-1;
			if(ageRange==0){
				strAgeFilter=CommunityMembers.AGE+"<10";	//under 1 year
			}else if(ageRange>=1 && ageRange<7){	//compute range
				strAgeFilter="("+CommunityMembers.AGE+">="+limit[ageRange]+" AND "+CommunityMembers.AGE+"<"+limit[ageRange+1]+")";
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

		String limitClause="";
		if(page>=0){
			page=page*15;
			limitClause=" limit " +page +",15";
		}
		
		try{
			db=getReadableDatabase();
			String strQuery="select "
					+SERVICE_REC_ID+", "
					+FamilyPlanningServices.SERVICE_ID +", "
					+FamilyPlanningServices.SERVICE_NAME+", "
					+CommunityMembers.COMMUNITY_MEMBER_ID+", "
					+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
					+QUANTITY+", "
					+SERVICE_DATE +","
					+CommunityMembers.BIRTHDATE +", "
					+CommunityMembers.COMMUNITY_ID 
					//+","
					//+CommunityMembers.GENDER
					+" from " +FamilyPlanningRecords.VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL
					+" where "
					+"("+FamilyPlanningRecords.SERVICE_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
					+FamilyPlanningRecords.SERVICE_DATE +"<=\""+ lastDateOfTheMonth + "\" )"
					+" AND "
					+strAgeFilter 
					//+" AND "
					//+strGenderFilter there is not gender in family planing record
					+limitClause;
					
			cursor=db.rawQuery(strQuery, null);
			FamilyPlanningRecord record=fetch();
			while(record!=null){
				list.add(record);
				record=fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			return list;
		}
				
				
				
	}
	
	public static String getServiceTypeName(int type){
		if(type==1){
			return "New Acceptor";
		}else if(type==2){
			return "Continuing";
		}else{
			return "Other";
		}
	}
	
	/**
	 * returns a string for creating service_records table
	 * @return
	 */
	public static String getCreateSQLString(){
		return "create table " +TABLE_NAME_FAMILY_PLANNING_RECORDS +" ( "
				+SERVICE_REC_ID+"  integer primary key, "
				+FamilyPlanningServices.SERVICE_ID +" integer, "
				+CommunityMembers.COMMUNITY_MEMBER_ID +" integer, "
				+SERVICE_DATE+" text,"
				+SCHEDULE_DATE+" text, "
				+QUANTITY+" numberic ,"
				+SERVICE_TYPE+" integer default 0,"
				+DataClass.REC_STATE+ " integer "
				+")";
	}
	
	public static String getServiceRecordSQLString(){
		return "select "
				+SERVICE_REC_ID+", "
				+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID+", "
				+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
				+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+FamilyPlanningServices.SERVICE_ID+", "
				+FamilyPlanningServices.SERVICE_NAME+", "
				+QUANTITY+", "
				+SERVICE_DATE +", "
				+SCHEDULE_DATE+ ", "
				+SERVICE_TYPE+","
				+CommunityMembers.GENDER
				+" from "
				+TABLE_NAME_FAMILY_PLANNING_RECORDS + " left join " +CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
				+" on "+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID +"="+
						CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_MEMBER_ID
				+" left join "+FamilyPlanningServices.TABLE_NAME_FAMILY_PLANNING_SERVICES 
				+" on "+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+FamilyPlanningServices.SERVICE_ID +"="
						+FamilyPlanningServices.TABLE_NAME_FAMILY_PLANNING_SERVICES +"." +FamilyPlanningServices.SERVICE_ID;
	}
	
	public static String getCreateViewSQLString(){
		//select service_rec_id, family_planning_records.community_member_id, community_member_name, 
		//family_planning_records.service_id, service_name, service_date from family_planning_records left 
		//join comunity_members on family_planning_records.community_member_id=comunity_members.community_member_id left join vaccines 
		//on family_planning_records.vaccine_id=vaccines.vaccine_id where family_planning_records.community_member_id=1
		return " create view "+VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL+" as select "
				+SERVICE_REC_ID+", "
				+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID+", "
				+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
				+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+FamilyPlanningServices.SERVICE_ID+", "
				+FamilyPlanningServices.SERVICE_NAME+", "
				+"julianday(" + SERVICE_DATE +")-julianday(" +CommunityMembers.BIRTHDATE+") as "+AGE_DAYS +", "
				+SERVICE_DATE+"-"+CommunityMembers.BIRTHDATE +" as "+AGE +", "
				+SERVICE_DATE+", "
				+SCHEDULE_DATE+", "
				+QUANTITY+", "
				+SERVICE_TYPE+", "
				+CommunityMembers.BIRTHDATE +", "
				+CommunityMembers.COMMUNITY_ID 
				+" from "
				+TABLE_NAME_FAMILY_PLANNING_RECORDS + " left join " +CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
				+" on "+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID +"="+
						CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_MEMBER_ID
				+" left join "+FamilyPlanningServices.TABLE_NAME_FAMILY_PLANNING_SERVICES 
				+" on "+ TABLE_NAME_FAMILY_PLANNING_RECORDS+ "."+FamilyPlanningServices.SERVICE_ID +"="
						+FamilyPlanningServices.TABLE_NAME_FAMILY_PLANNING_SERVICES +"." +FamilyPlanningServices.SERVICE_ID;
	}
} 
