package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.data.VaccinationReport.VaccinationReportRecord;

/**
 * Reads and writes vaccination record of community member to a table
 * @author Aelaf Dafla
 *
 */
public class VaccineRecords extends DataClass {
	public final static String VACCINE_REC_ID="vaccine_rec_id";
	public final static String VACCINE_NAME="vaccine_name";
	public final static String VACCINE_DATE="vaccine_date";
	public final static String TABLE_NAME_VACCINE_RECORDS="vaccine_records";
	public final static String VIEW_NAME_VACCINE_RECORDS_DETAIL="view_vaccine_records_detail";
	public final static String AGE="age";
	public final static String AGE_DAYS="age_days";
			
	public VaccineRecords(Context context){
		super(context);
	}
	
	/**
	 * records when a community member was vaccinated with particular vaccine identified by vaccineId 
	 * @param communityMemberId
	 * @param vaccineId
	 * @param vaccineDate
	 * @return
	 */
	public boolean addRecord(int communityMemberId, int vaccineId, String vaccineDate){
		try{
						
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(CommunityMembers.COMMUNITY_MEMBER_ID, communityMemberId);
			cv.put(Vaccines.VACCINE_ID,vaccineId);
			cv.put(VACCINE_DATE, vaccineDate);
			if(db.insert(TABLE_NAME_VACCINE_RECORDS, null, cv)<=0){
				return false;
			}
			return true;
		}catch(Exception ex){
			close();
			return false;
		}

	}
	//removes one vaccine record form table 
	public boolean reomveRecord(int vaccineRecId){
		try{
			db=getWritableDatabase();
			String whereClause= VACCINE_REC_ID +"="+vaccineRecId;
			if(db.delete(TABLE_NAME_VACCINE_RECORDS, whereClause, null)<=0){
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
	public VaccineRecord fetch(){
		try{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index=cursor.getColumnIndex(VACCINE_REC_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_ID);
			int communityMemberId=cursor.getInt(index);
			index=cursor.getColumnIndex(Vaccines.VACCINE_ID);
			int vaccineId=cursor.getInt(index);
			index=cursor.getColumnIndex(VACCINE_DATE);
			String vaccineDate=cursor.getString(index);

			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_NAME);
			String fullname="";
			if(index>=0){
				fullname=cursor.getString(index);
			}
			String vaccineName="";
			index=cursor.getColumnIndex(Vaccines.VACCINE_NAME);
			if(index>=0){
				vaccineName=cursor.getString(index);
			}
			index=cursor.getColumnIndex(CommunityMembers.GENDER);
			String gender="";
			if(index>=0){
				gender=cursor.getString(index);
			}
			VaccineRecord record=new VaccineRecord(id,communityMemberId,fullname,vaccineId,vaccineName,vaccineDate,gender);
			cursor.moveToNext();
			return record;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * returns list of vaccination record of one community member. It uses joint query
	 * @param communityMemberId
	 * @return
	 * 
	 * modified by NAmanquah to return all rows if communityMemberID is 0
	 */
	public ArrayList<VaccineRecord> getVaccineRecords(int communityMemberId){
		ArrayList<VaccineRecord> list=new ArrayList<VaccineRecord>();
		try{
			db=getReadableDatabase();
			String sql= VaccineRecords.getVaccineRecordSQLString() 
					+" where "
					+TABLE_NAME_VACCINE_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID
					+"="+communityMemberId;
			//NNA addition:
			if (communityMemberId==0){
				 sql= VaccineRecords.getVaccineRecordSQLString(); 
			}
			cursor=db.rawQuery(sql, null);
			cursor.moveToFirst();
			VaccineRecord record=fetch();
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
	
	
	public VaccineRecord getVaccineRecord(int communityMemberId, int vaccineId){
		try{
			db=getReadableDatabase();
			String sql= VaccineRecords.getVaccineRecordSQLString() 
					+" where "
					+TABLE_NAME_VACCINE_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID
					+"="+communityMemberId
					+" AND "
					+TABLE_NAME_VACCINE_RECORDS+ "."+ Vaccines.VACCINE_ID +"="+vaccineId;
						
			cursor=db.rawQuery(sql, null);
			cursor.moveToFirst();
			VaccineRecord record=fetch();
			
			close();
			return record;
		}catch(Exception ex){
			return null;
		}
	}
	
	public ArrayList< VaccineRecord> getMonthlyVaccinationRecord(int month,int year,int ageRange,String gender,int page){
		//define period for the report
		ArrayList< VaccineRecord> list=new ArrayList< VaccineRecord>();
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
		int[] limit={0,12,24};
		String strAgeFilter=" 1 ";

		if(ageRange==0){ //all total
			strAgeFilter=" 1 "; 
		}else if(ageRange==1){ //under 1 year
			strAgeFilter=" "+ AGE+"<1";	
		}else if(ageRange==2){	//between 1 and less than 2 years 
			strAgeFilter=" ("+AGE+">=1 AND "+AGE+"< 2) "; 
		}else if(ageRange==3){
			strAgeFilter=" "+AGE+">=2 ";
		}else{
			strAgeFilter=" 1 "; 
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
					+VACCINE_REC_ID+", "
					+Vaccines.VACCINE_ID +", "
					+Vaccines.VACCINE_NAME+", "
					+CommunityMembers.COMMUNITY_MEMBER_ID+", "
					+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
					+VACCINE_DATE+", "
					+CommunityMembers.BIRTHDATE +", "
					+AGE +","
					+CommunityMembers.COMMUNITY_ID +","
					+CommunityMembers.GENDER
					+" from " +VaccineRecords.VIEW_NAME_VACCINE_RECORDS_DETAIL
					+" where "
					+"("+VaccineRecords.VACCINE_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
					+VaccineRecords.VACCINE_DATE +"<=\""+ lastDateOfTheMonth + "\" )"
					+" AND "
					+strAgeFilter + " AND "
					+strGenderFilter
					+limitClause;
					
			cursor=db.rawQuery(strQuery, null);
			VaccineRecord record=fetch();	
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
	
	/**
	 * returns a string for creating vaccine_records table
	 * @return
	 */
	public static String getCreateSQLString(){
		return "create table " +TABLE_NAME_VACCINE_RECORDS +" ( "
				+VACCINE_REC_ID+"  integer primary key, "
				+Vaccines.VACCINE_ID +" integer, "
				+CommunityMembers.COMMUNITY_MEMBER_ID +" integer, "
				+VACCINE_DATE+" text,"
				+DataClass.REC_STATE+ " integer "
				+")";
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
	
	public static String getVaccineRecordSQLString(){
		return "select "
				+VACCINE_REC_ID+", "
				+ TABLE_NAME_VACCINE_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID+", "
				+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
				+ TABLE_NAME_VACCINE_RECORDS+ "."+Vaccines.VACCINE_ID+", "
				+Vaccines.VACCINE_NAME+", "
				+VACCINE_DATE 
				+" from "
				+TABLE_NAME_VACCINE_RECORDS + " left join " +CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
				+" on "+ TABLE_NAME_VACCINE_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID +"="+
						CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_MEMBER_ID
				+" left join "+Vaccines.TABLE_NAME_VACCINES 
				+" on "+ TABLE_NAME_VACCINE_RECORDS+ "."+Vaccines.VACCINE_ID +"="
						+Vaccines.TABLE_NAME_VACCINES +"." +Vaccines.VACCINE_ID;
	}
	
	public static String getCreateViewSQLString(){
		return " create view "+VIEW_NAME_VACCINE_RECORDS_DETAIL+" as select "
				+VACCINE_REC_ID+", "
				+ TABLE_NAME_VACCINE_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID+", "
				+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
				+ TABLE_NAME_VACCINE_RECORDS+ "."+Vaccines.VACCINE_ID+", "
				+Vaccines.VACCINE_NAME+", "
				+"julianday(" + VACCINE_DATE +")-julianday(" +CommunityMembers.BIRTHDATE+") as "+AGE_DAYS +", "
				+VACCINE_DATE+"-"+CommunityMembers.BIRTHDATE +" as "+AGE +", "
				+VACCINE_DATE+", "
				+CommunityMembers.BIRTHDATE +", "
				+CommunityMembers.COMMUNITY_ID+","
				+CommunityMembers.GENDER
				+" from "
				+TABLE_NAME_VACCINE_RECORDS + " left join " +CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
				+" on "+ TABLE_NAME_VACCINE_RECORDS+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID +"="+
						CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_MEMBER_ID
				+" left join "+Vaccines.TABLE_NAME_VACCINES 
				+" on "+ TABLE_NAME_VACCINE_RECORDS+ "."+Vaccines.VACCINE_ID +"="
						+Vaccines.TABLE_NAME_VACCINES +"." +Vaccines.VACCINE_ID;
	}
} 
