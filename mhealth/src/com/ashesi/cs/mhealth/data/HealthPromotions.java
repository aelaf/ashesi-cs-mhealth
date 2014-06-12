package com.ashesi.cs.mhealth.data;




import java.util.ArrayList;



import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class HealthPromotions extends DataClass {
	public static final String REC_DATE="date";
	public static final String REC_NO="rec_no";
	public static final String REC_VENUE="venue";
	public static final String REC_TOPIC="topic";
	public static final String REC_METHOD="method";
	public static final String REC_TARGETAUDIENCE="target_audience";
	public static final String REC_NUMBERAUDIENCE="number_of_audience";
	public static final String REC_REMARKS="remarks";
	public static final String REC_MONTH="month";
	public static final String REC_LATITUDE="latitude";
	public static final String REC_LONGITUDE="longitude";
	public static final String REC_IMAGE="image";
	public static final String REC_IDCHO="idcho";
	public static final String SERVER_REC_NO="server_rec_no";
	public static final String REC_SUBDISTRICTID="subdistrict_id";
	public static final String TABLE_NAME_HEALTH_PROMOTION="health_promotion";
	
	public HealthPromotions(Context context){
		super(context);
	}
	
	
	/**
	 *
	 * 
	 * @return
	 */
	
	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_HEALTH_PROMOTION +" ("
				+REC_NO +" int primary key, "
				+REC_DATE+" text, "
				+REC_VENUE +" text, "
				+REC_TOPIC+" text, "
				+REC_METHOD+ " text, "
				+REC_TARGETAUDIENCE+ " text, "
				+REC_NUMBERAUDIENCE+ " text, "
				+REC_REMARKS+" text, "
				+REC_MONTH+" text, "
				+REC_LATITUDE+" text, "
				+REC_LONGITUDE+" text, "
				+REC_IMAGE+" text, "
				+REC_IDCHO+" text, "
				+REC_SUBDISTRICTID+" text "
				+" )";
	}
	
	public static String getInsert(String date,String venue,String topic, String method, String target, String number, String remarks, String month, String lat, String longitude, String image_url, String cho_id, String subdistrict_id){
		return "insert into "
				+ TABLE_NAME_HEALTH_PROMOTION +" ("
				+ REC_DATE +", "
				+ REC_VENUE +", "
				+REC_TOPIC +", "
				+REC_METHOD +", "
				+REC_TARGETAUDIENCE+ ", "
				+REC_NUMBERAUDIENCE+ ", "
				+REC_REMARKS+ ", "
				+REC_MONTH+ ", "
				+REC_LATITUDE+ ", "
				+REC_LONGITUDE+ ", "
				+REC_IMAGE+ ", "
				+REC_IDCHO+ ", "
				+REC_SUBDISTRICTID
				+") values("
				+ date + ", "
				+ venue+", "
				+topic+ ", "
				+method+ ", "
				+target+ ", "
				+number+ ", "
				+remarks+", "
				+month+", "
				+lat+ ", "
				+longitude+", "
				+image_url+", "
				+cho_id+", "				
				+subdistrict_id
				+") ";
	}
	
	public boolean addHealthPromotion(String date,String venue,String topic, String method, String target, String number, String remarks, String month, String lat, String longitude, String image_url, String cho_id, String subdistrict_id){
		try
		{
			db=getReadableDatabase();
			ContentValues values=new ContentValues();
			values.put(REC_DATE, date);
			values.put(REC_VENUE, venue);
			values.put(REC_TOPIC, topic);
			values.put(REC_METHOD, method);
			values.put(REC_TARGETAUDIENCE, target);
			values.put(REC_NUMBERAUDIENCE, number);
			values.put(REC_REMARKS, remarks);
			values.put(REC_MONTH, month);
			values.put(REC_LATITUDE, lat);
			values.put(REC_LONGITUDE, longitude);
			values.put(REC_IMAGE, image_url);
			values.put(REC_IDCHO, cho_id);
			values.put(REC_SUBDISTRICTID, subdistrict_id);
			
			db.insertWithOnConflict(TABLE_NAME_HEALTH_PROMOTION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	
	public HealthPromotion fetch(){
		try
		{
			if(cursor.isAfterLast()){
				return null;
			}
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(REC_NO);
			
			int id=cursor.getInt(index);
			//index=cursor.getColumnIndex(REC_NO);
			String date=cursor.getString(index);
			index=cursor.getColumnIndex(REC_VENUE);
			String venue=cursor.getString(index);
			index=cursor.getColumnIndex(REC_TOPIC);
			String topic=cursor.getString(index);
			index=cursor.getColumnIndex(REC_METHOD);
			String method=cursor.getString(index);
			index=cursor.getColumnIndex(REC_TARGETAUDIENCE);
			String target=cursor.getString(index);
			index=cursor.getColumnIndex(REC_NUMBERAUDIENCE);
			String number=cursor.getString(index);
			index=cursor.getColumnIndex(REC_REMARKS);
			String remarks=cursor.getString(index);
			index=cursor.getColumnIndex(REC_MONTH);
			String month=cursor.getString(index);
			index=cursor.getColumnIndex(REC_LATITUDE);
			String latitude=cursor.getString(index);
			index=cursor.getColumnIndex(REC_LONGITUDE);
			String longitude=cursor.getString(index);
			index=cursor.getColumnIndex(REC_IMAGE);
			String image_url=cursor.getString(index);
			index=cursor.getColumnIndex(REC_IDCHO);
			String cho_id=cursor.getString(index);
			index=cursor.getColumnIndex(REC_SUBDISTRICTID);
			String subdistrict_id=cursor.getString(index);
			HealthPromotion healthpromo=new HealthPromotion(id,date,venue,topic,method,target,number,
					remarks,month,latitude,longitude, image_url, cho_id, subdistrict_id);
			cursor.moveToNext();
			return healthpromo;
		}catch(Exception ex){
			return null;
		}
		
	}
	public ArrayList<HealthPromotion> getArrayList(){
		ArrayList<HealthPromotion> list=new ArrayList<HealthPromotion>();
		HealthPromotion healthPromotion=fetch();
		while(healthPromotion!=null){
			list.add(healthPromotion);
			healthPromotion=fetch();
		}
		close();
		return list;
	}
	
	public ArrayList<HealthPromotion> getReport(){
	
		
		try
		{
			db=getReadableDatabase();
			
			String strQuery="select "+HealthPromotions.REC_NO
								+","+HealthPromotions.REC_DATE
								+"," +HealthPromotions.REC_TOPIC
								+","+HealthPromotions.REC_VENUE
								+" from "+HealthPromotions.TABLE_NAME_HEALTH_PROMOTION;
			
			cursor=db.rawQuery(strQuery, null);
			ArrayList<HealthPromotion> list=new ArrayList<HealthPromotion>();		
			
			cursor.moveToFirst();
			
			int indexHealthPromoID=cursor.getColumnIndex(HealthPromotions.REC_NO);
			int indexHealthPromoDate=cursor.getColumnIndex(HealthPromotions.REC_DATE);
			int indexHealthPromoTopic=cursor.getColumnIndex(HealthPromotions.REC_TOPIC);
			int indexHealthPromoVenue=cursor.getColumnIndex(HealthPromotions.REC_VENUE);
			HealthPromotion record = null ;	
			HealthPromotion record2 = null ;
			record2= new HealthPromotion("DATE","TOPIC","VENUE");
			list.add(record2);
			int healthPromoId = 0;
			String healthPromoDate =null;
			String healthPromoVenue = null;
			String healthPromoTopic = null;
			while(!cursor.isAfterLast()){
				record= new HealthPromotion(healthPromoId,healthPromoDate,healthPromoVenue,healthPromoTopic);
				healthPromoId=cursor.getInt(indexHealthPromoID);
				healthPromoDate=cursor.getString(indexHealthPromoDate);
				healthPromoVenue=cursor.getString(indexHealthPromoTopic);
				healthPromoTopic=cursor.getString(indexHealthPromoVenue);
				list.add(record);
				
				cursor.moveToNext();
				
			}
			
			close();
			return list;
		}catch(Exception ex){
			return null;
		}
		
		
	}
	
}
