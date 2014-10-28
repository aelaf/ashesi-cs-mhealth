package com.ashesi.cs.mhealth.data;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;


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
	public static final String SERVER_REC_NO="server_rec_no";
	public static final String TABLE_NAME_HEALTH_PROMOTION="health_promotion";
	public static final String HEALTH_PROMOTION_PIC_PATH="hp/";
	
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
				+REC_NO +" integer primary key , "
				+REC_DATE+" text, "
				+REC_VENUE +" text, "
				+REC_TOPIC+" text, "
				+REC_METHOD+ " text, "
				+REC_TARGETAUDIENCE+ " text, "
				+REC_NUMBERAUDIENCE+ " int, "
				+REC_REMARKS+" text, "
				+REC_MONTH+" text, "
				+REC_LATITUDE+" text, "
				+REC_LONGITUDE+" text, "
				+REC_IMAGE+" text, "
				+CHOs.CHO_ID+" integer, "
				+CHOs.SUBDISTRICT_ID+" integer "
				+" )";
	}
	
	public static String getInsert(String date,String venue,String topic, String method, String target,  int audianceNumber, String remarks, String month, String lat, String longitude, String image_url, String cho_id, String subdistrict_id){
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
				+CHOs.CHO_ID+ ", "
				+CHOs.SUBDISTRICT_ID
				+") values("
				+ date + ", "
				+ venue+", "
				+topic+ ", "
				+method+ ", "
				+target+ ", "
				+audianceNumber+ ", "
				+remarks+", "
				+month+", "
				+lat+ ", "
				+longitude+", "
				+image_url+", "
				+cho_id+", "				
				+subdistrict_id
				+") ";
	}
	
	public boolean addHealthPromotion(String date,String venue,String topic, String method, String target, int audianceNumber, String remarks, String month, double latitude, double longitude, String imageURL, int choId, int subdistrictId){
		try
		{
			db=getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put(REC_DATE, date);
			values.put(REC_VENUE, venue);
			values.put(REC_TOPIC, topic);
			values.put(REC_METHOD, method);
			values.put(REC_TARGETAUDIENCE, target);
			values.put(REC_NUMBERAUDIENCE, audianceNumber);
			values.put(REC_REMARKS, remarks);
			values.put(REC_MONTH, month);
			values.put(REC_LATITUDE, Double.toString(latitude));
			values.put(REC_LONGITUDE, Double.toString(longitude));
			values.put(REC_IMAGE, imageURL);
			values.put(CHOs.CHO_ID, choId);
			values.put(CHOs.SUBDISTRICT_ID, subdistrictId);
			
			db.insertWithOnConflict(TABLE_NAME_HEALTH_PROMOTION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public boolean updateHealthPromotion(int id,String date,String venue,String topic, String method, String target, int audianceNumber, String remarks, String month, double latitude, double longitude, String imageURL, int choId, int subdistrictId){
		try
		{
			db=getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put(REC_NO, id);
			values.put(REC_DATE, date);
			values.put(REC_VENUE, venue);
			values.put(REC_TOPIC, topic);
			values.put(REC_METHOD, method);
			values.put(REC_TARGETAUDIENCE, target);
			values.put(REC_NUMBERAUDIENCE, audianceNumber);
			values.put(REC_REMARKS, remarks);
			values.put(REC_MONTH, month);
			values.put(REC_LATITUDE, Double.toString(latitude));
			values.put(REC_LONGITUDE, Double.toString(longitude));
			values.put(REC_IMAGE, imageURL);
			values.put(CHOs.CHO_ID, choId);
			values.put(CHOs.SUBDISTRICT_ID, subdistrictId);
			
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
			index=cursor.getColumnIndex(REC_DATE);
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
			int audenceNumber=cursor.getInt(index);
			index=cursor.getColumnIndex(REC_REMARKS);
			String remarks=cursor.getString(index);
			index=cursor.getColumnIndex(REC_MONTH);
			String month=cursor.getString(index);
			index=cursor.getColumnIndex(REC_LATITUDE);
			String latitude=cursor.getString(index);
			index=cursor.getColumnIndex(REC_LONGITUDE);
			String longitude=cursor.getString(index);
			index=cursor.getColumnIndex(REC_IMAGE);
			String imageURL=cursor.getString(index);
			index=cursor.getColumnIndex(CHOs.CHO_ID);
			int choId=cursor.getInt(index);
		
			HealthPromotion healthpromo=new HealthPromotion(id,date,venue,topic,method,target,audenceNumber,
					remarks,month,latitude,longitude, imageURL, choId, 0);
			cursor.moveToNext();
			return healthpromo;
		}catch(Exception ex){
			return null;
		}
		
	}
	
	public String getHealthPromotionPicturePath(){
		return  DataClass.getApplicationFolderPath()+"hp/";
	}
	
	public boolean deleteHealhtPromotion(int id){
		try{
			db=getWritableDatabase();
			String whereClause=REC_NO+"="+Integer.toString(id);
			db.delete(TABLE_NAME_HEALTH_PROMOTION, whereClause, null);
			close();
			return true;
		}catch(Exception ex){
			close();
			return false;
		}
	}
	public HealthPromotion getHealthPromotion(int id){
		try{
			String columns[]={REC_DATE,REC_NO,REC_VENUE,REC_TOPIC,REC_METHOD,REC_TARGETAUDIENCE,REC_NUMBERAUDIENCE,
						REC_REMARKS,REC_MONTH,REC_LATITUDE,REC_LONGITUDE,REC_IMAGE,CHOs.CHO_ID};
			String selection=REC_NO+"="+Integer.toString(id);
			String orderBy=REC_DATE+ " DESC";
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_HEALTH_PROMOTION, columns, selection, null, null, null, orderBy);
			HealthPromotion healthPromotion=fetch();
			close();
			return healthPromotion;
			
		}catch(Exception ex){
			close();
			return null;
		}
	}
	
	public ArrayList<HealthPromotion> getHealthPromotions(int month,int year){
		ArrayList<HealthPromotion> list=new ArrayList<HealthPromotion>();
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
		
		try{
			String columns[]={REC_DATE,REC_NO,REC_VENUE,REC_TOPIC,REC_METHOD,REC_TARGETAUDIENCE,REC_NUMBERAUDIENCE,
						REC_REMARKS,REC_MONTH,REC_LATITUDE,REC_LONGITUDE,REC_IMAGE,CHOs.CHO_ID};
			String selection="("+REC_DATE+">=\""+ firstDateOfTheMonth +"\" AND " +REC_DATE +"<=\""+ lastDateOfTheMonth + "\" )";
			String orderBy=REC_DATE+ " DESC";
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_HEALTH_PROMOTION, columns, selection, null, null, null, orderBy);
			HealthPromotion healthPromotion=fetch();
			while(healthPromotion!=null){
				list.add(healthPromotion);
				healthPromotion=fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			close();
			return list;
		}
		
		
	}
}
