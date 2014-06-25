package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;

import com.ashesi.cs.mhealth.DataClass;

/**
 * Reads and writes family planning service record of community member to a table
 * @author Aelaf Dafla
 *
 */
public class FamilyPlanningRecords extends DataClass {
	public final static String SERVICE_REC_ID="service_rec_id";
	public final static String SERVICE_NAME="service_name";
	public final static String SERVICE_DATE="service_date";
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
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, String serviceDate){
		try{
						
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(CommunityMembers.COMMUNITY_MEMBER_ID, communityMemberId);
			cv.put(FamilyPlanningServices.SERVICE_ID,serviceId);
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
	public FamilyPlanningRecord addRecord(int communityMemberId, int serviceId, Date serviceDate){
		try{
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			String strDate=dateFormat.format(serviceDate);
			
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(CommunityMembers.COMMUNITY_MEMBER_ID, communityMemberId);
			cv.put(FamilyPlanningServices.SERVICE_ID,serviceId);
			cv.put(SERVICE_DATE, strDate);
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
			
			FamilyPlanningRecord record=new FamilyPlanningRecord(id,communityMemberId,fullname,serviceId,serviceName,serviceDate);
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
				+SERVICE_DATE 
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