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
			index=cursor.getColumnIndex(CHPSZones.CHPS_ZONE_ID);
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
			String[] columns={COMMUNITY_ID,COMMUNITY_NAME,CHPSZones.CHPS_ZONE_ID,LATITUDE,LONGITUDE,POPULATION,HOUSEHOLD};
			String selection=null;
			if(subdistrictId!=0){
				selection=CHPSZones.SUBDISTRICT_ID +"="+ subdistrictId;
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
	
	/**
	 * returns list of community object
	 * @param subdistrictId if 0, it returns all communities 
	 * @return
	 */
	public ArrayList<Community> getCommunties(int chpsZoneId,int subdistrictId,int districtId){
		ArrayList<Community>list=new ArrayList<Community>();
		try{
			String query="select "
						+COMMUNITY_ID+","
						+COMMUNITY_NAME+","
						+LATITUDE+","
						+LONGITUDE+","
						+POPULATION+","
						+HOUSEHOLD+","
						+Communities.TABLE_COMMUNITIES+"."+CHPSZones.CHPS_ZONE_ID +","
						+CHPSZones.TABLE_CHPS_ZONES+"."+CHPSZones.SUBDISTRICT_ID +","
						+CHPSZones.DISTRICT_ID
						+ " from "+Communities.TABLE_COMMUNITIES 
						+ " left join "+CHPSZones.TABLE_CHPS_ZONES 
						+ " on "+Communities.TABLE_COMMUNITIES+"."+CHPSZones.CHPS_ZONE_ID +"="
						+CHPSZones.TABLE_CHPS_ZONES+"."+CHPSZones.CHPS_ZONE_ID; 
					
			if(chpsZoneId!=0){
				query+=" where "+Communities.TABLE_COMMUNITIES+"."+CHPSZones.CHPS_ZONE_ID +"="+Integer.toString(chpsZoneId);
			}else if(subdistrictId!=0){
				query+=" where "+CHPSZones.TABLE_CHPS_ZONES+"."+CHPSZones.SUBDISTRICT_ID +"="+Integer.toString(subdistrictId);
			}else{
				query+=" where "+CHPSZones.DISTRICT_ID +"="+Integer.toString(districtId);
			}
			
			db=getReadableDatabase();
		
			cursor=db.rawQuery(query, null);
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
				+CHPSZones.CHPS_ZONE_ID+" integer, "
				+LATITUDE +" text, "
				+LONGITUDE +" text, "
				+POPULATION +" integer, "
				+HOUSEHOLD + " integer "
				+")";
		
	}
	
	static public String getInsertSQL(int id,String communityName,int subdistrictId){
		return "insert into "+TABLE_COMMUNITIES +"("
				+COMMUNITY_ID +" , "
				+COMMUNITY_NAME +" , "
				+CHPSZones.CHPS_ZONE_ID+", "
				+LATITUDE +", "
				+LONGITUDE +" , "
				+POPULATION +", "
				+HOUSEHOLD 
				+") values("
				+id+", "
				+"'" +communityName+"', "
				+subdistrictId +", 0,0,0,0)";

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
	public boolean addCommunity(int id,String communityName,int chpsZoneId,String longitude,String latitude,int population, int household){
		try{
			db=getWritableDatabase();
			
			ContentValues values=new ContentValues();
			values.put(COMMUNITY_ID, id);
			values.put(COMMUNITY_NAME, communityName);
			values.put(CHPSZones.CHPS_ZONE_ID,  chpsZoneId);
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
				int chpsZoneId=obj.getInt("chpsZoneId");
				String latitude=obj.getString("latitude");
				String longitude=obj.getString("longitude");
				int population=obj.getInt("population");
				int household=obj.getInt("household");
				addCommunity(communityId,communityName,chpsZoneId,latitude,longitude,population,household);
			}
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}

}
