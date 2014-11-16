package com.ashesi.cs.mhealth.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CHPSZones extends DataClass {
	public static final String TABLE_CHPS_ZONES="chps_zones";
	public static final String CHPS_ZONE_ID="chps_zone_id";
	public static final String CHPS_ZONE_NAME="chps_name";
	public static final String SUBDISTRICT_ID="subdistrict_id";
	public static final String DISTRICT_ID="district_id";
	public CHPSZones(Context context){
		super(context);
	}
	
	static public String getCreateSQLString(){
		return "create table "+ TABLE_CHPS_ZONES +" ("
				+CHPS_ZONE_ID +" integer priamry key, "
				+CHPS_ZONE_NAME+" text,  "
				+SUBDISTRICT_ID+" integer, "
				+DISTRICT_ID+" integer "
				+")";
	}
	
	public boolean addCHPSZone(int chpsZoneId,String chpsZoneName,int subdistrictId,int districtId){
		try{
			db=getWritableDatabase();
			
			ContentValues values=new ContentValues();
			values.put(CHPS_ZONE_ID, chpsZoneId);
			values.put(CHPS_ZONE_NAME, chpsZoneName);
			values.put(SUBDISTRICT_ID, subdistrictId);
			values.put(DISTRICT_ID,districtId);

			db.insertWithOnConflict(TABLE_CHPS_ZONES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			close();
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public boolean reomveCHOCommunity(int chpsZoneId){
		try{
			db=getWritableDatabase();
			String whereClause=CHPS_ZONE_ID+"="+chpsZoneId;
			db.delete(TABLE_CHPS_ZONES, whereClause, null);
			close();
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
	
	public CHPSZone fetch(){
		try{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index=cursor.getColumnIndex(CHPS_ZONE_ID);
			int chpsZoneId=cursor.getInt(index);
			index=cursor.getColumnIndex(CHPS_ZONE_NAME);
			String chpsZoneName=cursor.getString(index);
			index=cursor.getColumnIndex(SUBDISTRICT_ID);
			int subdistrictId=cursor.getInt(index);
			index=cursor.getColumnIndex(DISTRICT_ID);
			int districtId=cursor.getInt(index);
			
			CHPSZone zone=new CHPSZone(chpsZoneId,chpsZoneName,subdistrictId,districtId);
			cursor.moveToNext();
			return zone;
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * processes the data received from server
	 * @param jsonArray
	 */
	public boolean processDownloadData(String data){
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){
				return false;
			}
						
			
			JSONArray jsonArray=obj.getJSONArray("chpsZones");
			String chpsZoneName;
			int id;
			int subdistrictId;
			int districtId;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				chpsZoneName=obj.getString("chpsZoneName");
				id=obj.getInt("chpsZoneId");
				subdistrictId=obj.getInt("subdistrictId");
				districtId=obj.getInt("districtId");
				addCHPSZone(id,chpsZoneName,subdistrictId,districtId);
			}
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public CHPSZone getCHPSZone(int chpsZoneId){

		try{
			db=getReadableDatabase();
			String columns[]={CHPS_ZONE_ID,CHPS_ZONE_NAME,SUBDISTRICT_ID,DISTRICT_ID};
			String selection=CHPS_ZONE_ID+"="+Integer.toString(chpsZoneId);
			cursor=db.query(TABLE_CHPS_ZONES, columns, selection, null, null, null, null);
			CHPSZone zone=fetch();
			close();
			return zone;
		}catch(Exception ex){
			return null;
		}
	}
	
	public class CHPSZone{
		int chpsZoneId;
		String chpsZoneName;
		int subdistrictId;
		int districtId;
		
		public CHPSZone(int id,String chpsZoneName,int subdistrictId, int districtId){
			this.chpsZoneId=id;
			this.chpsZoneName=chpsZoneName;
			this.subdistrictId=subdistrictId;
			this.districtId=districtId;
		}
		
		public int getId(){
			return chpsZoneId;
		}
		public String getCHPSZoneName(){
			return chpsZoneName;
		}
		
		public int getSubDistrictId(){
			return subdistrictId;
		}
		
		public int getDistrictId(){
			return districtId;
		}
		
		public String toString(){
			return chpsZoneName;
		}
		
	}
	
	
}


