package com.ashesi.cs.mhealth.data;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Communities extends DataClass {
	public static final String TABLE_COMMUNITIES="communities";
	public static final String COMMUNITY_ID="community_id";
	public static final String COMMUNITY_NAME="community_name";
	public static final String SUBDISTRICT_ID="subdistrict_id";
	public static final String LATITUDE="latitude";
	public static final String LONGITUDE="longitude";
	public static final String POPULATION="population";
    public static final String HOUSEHOLD="household";

    		
    
	public Communities(Context context){
		 super(context);
	}

	
	/**
	 * returns a community object from the cursor. 
	 * @return
	 */
	
	public Community fetch(){
		try{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index=cursor.getColumnIndex(COMMUNITY_ID);
			int communityId=cursor.getInt(index);
			index=cursor.getColumnIndex(COMMUNITY_NAME);
			String communityName=cursor.getString(index);
			index=cursor.getColumnIndex(SUBDISTRICT_ID);
			int subdistrictId=cursor.getInt(index);
			index=cursor.getColumnIndex(LATITUDE);
			String latitude=cursor.getString(index);
			index=cursor.getColumnIndex(LONGITUDE);
			String longitude=cursor.getString(index);
			index=cursor.getColumnIndex(POPULATION);
			int population=cursor.getInt(index);
			index=cursor.getColumnIndex(HOUSEHOLD);
			int household=cursor.getInt(index);
			Community cm=new Community(communityId,communityName,subdistrictId,latitude,longitude,household,population);
			cursor.moveToNext();
			return cm;
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * returns list of community object
	 * @param subdistrictId if 0, it returns all communities 
	 * @return
	 */
	public ArrayList<Community> getCommunties(int subdistrictId){
		try{
			db=getReadableDatabase();
			ArrayList<Community> list=new ArrayList<Community>();
			String[] columns={COMMUNITY_ID,COMMUNITY_NAME,SUBDISTRICT_ID,LATITUDE,LONGITUDE,POPULATION,HOUSEHOLD};
			String selection=null;
			if(subdistrictId!=0){
				selection=SUBDISTRICT_ID +"="+ subdistrictId;
			}
			cursor=db.query(TABLE_COMMUNITIES, columns, selection, null, null, null, null, null);
			Community community=fetch();
			while(community!=null){
				list.add(community);
				community=fetch();
			}
					
			return list;
		}catch(Exception ex){
			return null;
		}
	}
	
	static public String getCreateTable(){
		return "create table "+TABLE_COMMUNITIES +"("
				+COMMUNITY_ID +" integer primary key, "
				+COMMUNITY_NAME +" text, "
				+SUBDISTRICT_ID+" integer, "
				+LATITUDE +" text, "
				+LONGITUDE +" text, "
				+POPULATION +" integer, "
				+HOUSEHOLD + " integer "
				+")";
		
	}	
	
	public HttpURLConnection connect(){
		try{
			return super.connect("communityActions.php?cmd=5&sid=0&deviceId="+mDeviceId);
		}catch(Exception ex){
			return null;
		}
	}
	
	public void threadedDownload(){
		new Thread(new Runnable() {
	        public void run() {
	        	String data=request("communityActions.php?cmd=5&sid=0&deviceId="+mDeviceId);
	    	    processDownloadData(data);
	        }
	    }).start();
	}
			
	/**
	 * 
	 * @param id
	 * @param communityName
	 * @param subdistrictId
	 * @param longitude
	 * @param latitude
	 * @param population
	 * @param household
	 * @return
	 */
	public boolean addCommunity(int id,String communityName,int subdistrictId,String longitude,String latitude,int population, int household){
		try{
			db=getWritableDatabase();
			
			ContentValues values=new ContentValues();
			values.put(COMMUNITY_ID, id);
			values.put(COMMUNITY_NAME, communityName);
			values.put(SUBDISTRICT_ID, subdistrictId);
			values.put(LONGITUDE, longitude);
			values.put(LATITUDE, latitude);
			values.put(POPULATION, population);
			values.put(HOUSEHOLD, household);
			db.insertWithOnConflict(TABLE_COMMUNITIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
		
	/**
	 * 
	 * @param data
	 */
	public boolean processDownloadData(String data){
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){
				return false;
			}
						
			
			JSONArray jsonArray=obj.getJSONArray("communities");
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				int communityId=obj.getInt("communityId");
				String communityName=obj.getString("communityName");
				int subdistrictId=obj.getInt("subdistrictId");
				String latitude=obj.getString("latitude");
				String longitude=obj.getString("longitude");
				int population=obj.getInt("population");
				int household=obj.getInt("household");
				addCommunity(communityId,communityName,subdistrictId,latitude,longitude,population,household);
			}
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}

}
