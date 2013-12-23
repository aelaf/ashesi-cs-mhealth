package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;

import com.ashesi.cs.mhealth.DataClass;

/**
 * Reads and writes vaccination record of community member to a table
 * @author Aelaf Dafla
 *
 */
public class VaccineRecords extends DataClass {
	public final static String VACCINE_REC_ID="vaccine_rec_id";
	public final static String VACCINE_NAME="vaccine_name";
	public final static String VACCINE_DATE="vaccine_date";
	public final static String TABLE_NAME_VACCINE_RECORD="vaccine_record";
			
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
			if(db.insert(TABLE_NAME_VACCINE_RECORD, null, cv)<=0){
				return false;
			}
			return true;
		}catch(Exception ex){
			close();
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
			index=cursor.getColumnIndex(CommunityMembers.COMMUNITY_MEMBER_NAME);
			String fullname=cursor.getString(index);
			index=cursor.getColumnIndex(Vaccines.VACCINE_ID);
			int vaccineId=cursor.getInt(index);
			index=cursor.getColumnIndex(Vaccines.VACCINE_NAME);
			String vaccineName=cursor.getString(index);
			index=cursor.getColumnIndex(VACCINE_DATE);
			String vaccineDate=cursor.getString(index);
			VaccineRecord record=new VaccineRecord(id,communityMemberId,fullname,vaccineId,vaccineName,vaccineDate);
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
	 */
	public ArrayList<VaccineRecord> getVaccineRecords(int communityMemberId){
		ArrayList<VaccineRecord> list=new ArrayList<VaccineRecord>();
		try{
			
			String sql="select "
						+VACCINE_REC_ID+", "
						+CommunityMembers.COMMUNITY_MEMBER_ID+", "
						+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
						+Vaccines.VACCINE_ID+", "
						+Vaccines.VACCINE_NAME+", "
						+VACCINE_DATE 
						+" from "
						+TABLE_NAME_VACCINE_RECORD + " left join " +CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
						+" on "+ TABLE_NAME_VACCINE_RECORD+ "."+ CommunityMembers.COMMUNITY_MEMBER_ID +"="+
								CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_MEMBER_ID;
						
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
			
		}
		return list;
	}
	/**
	 * returns a string for creating vaccine_records table
	 * @return
	 */
	public static String getCreateSQLString(){
		return "create table " +TABLE_NAME_VACCINE_RECORD +" ( "
				+VACCINE_REC_ID+"  integer primary key, "
				+Vaccines.VACCINE_ID +" integer, "
				+VACCINE_NAME+" text,"
				+CommunityMembers.COMMUNITY_ID +" integer, "
				+VACCINE_DATE+" text,"
				+DataClass.REC_STATE+ " integer "
				+")";
	}
	
} 
