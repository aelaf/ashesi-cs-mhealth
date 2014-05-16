package com.ashesi.cs.mhealth.data;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CHPSZones extends DataClass {
	public static final String TABLE_CHPS_ZONES="chps_zones";
	public static final String CHPS_ZONE_ID="chps_zone_id";
	public static final String CHPS_ZONE_NAME="chps_name";
	public CHPSZones(Context context){
		super(context);
	}
	
	static public String getCreateSQLString(){
		return "create talbe "+ TABLE_CHPS_ZONES +" ("
				+CHPS_ZONE_ID +" integer priamry key, "
				+CHPS_ZONE_NAME+" text  "
				+")";
	}
	
	public boolean addCommunityToCHO(int chpsZoneId,String chpsZoneName){
		try{
			db=getWritableDatabase();
			
			ContentValues values=new ContentValues();
			values.put(CHPS_ZONE_ID, chpsZoneId);
			values.put(CHPS_ZONE_NAME, chpsZoneName);
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
	
}


