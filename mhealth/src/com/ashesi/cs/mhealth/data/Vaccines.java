package com.ashesi.cs.mhealth.data;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class Vaccines extends DataClass {
	public final static String VACCINE_ID="vaccine_id";
	public final static String VACCINE_NAME="vaccine_name";
	public final static String VACCINE_SCHEDULE="vaccine_schedule";
	public final static String TABLE_NAME_VACCINES="vaccines";
	
	public Vaccines(Context context){
		super(context);
	}
	
	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_VACCINES +" ( "
				+VACCINE_ID + "  integer primary key , "
				+VACCINE_NAME+ " text, "
				+VACCINE_SCHEDULE+ " integer "
				+")";
		
	}
	
	public static String getInsertSQLString(int id,String vaccineName,int vaccineSchedule){
		return "insert into "+TABLE_NAME_VACCINES +"("
				+VACCINE_ID+", "
				+VACCINE_NAME+", "
				+VACCINE_SCHEDULE
				+") values("+
				+id+","
				+"'"+vaccineName +"'," +
				+vaccineSchedule+
				")";
				
	}

	public HttpURLConnection connect(){
		try{
			return super.connect("vaccineActions.php?cmd=1&deviceId="+mDeviceId);
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * writes the data from server to local database 
	 * @param data
	 */
	public boolean processDownloadData(String data){
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){
				return false;
			}
						
			
			JSONArray jsonArray=obj.getJSONArray("vaccines");
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				int vaccineId=obj.getInt("id");
				String vaccineName=obj.getString("vaccineName");
				int vaccineSchedule=obj.getInt("schedule");
				
				addVaccine(vaccineId,vaccineName,vaccineSchedule);
			}
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
	
	/**
	 * adds vaccine to table
	 * @param vaccineId
	 * @param vaccineName
	 * @param vaccineSchedule
	 * @return
	 */
	public boolean addVaccine(int vaccineId, String vaccineName,int vaccineSchedule){
		try{
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(VACCINE_ID,vaccineId);
			cv.put(VACCINE_NAME, vaccineName);
			cv.put(VACCINE_SCHEDULE, vaccineSchedule);
			if(db.insertWithOnConflict(TABLE_NAME_VACCINES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)<=0){
				return false;
			}
			close();
			return true;
		}catch(Exception ex){
			close();
			return false;
		}
	}
	
	/**
	 * fetch a vaccine information from current cursor and return it as object
	 * @return
	 */
	public Vaccine fetch(){
		try{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(VACCINE_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(VACCINE_NAME);
			String vaccineName=cursor.getString(index);
			index=cursor.getColumnIndex(VACCINE_SCHEDULE);
			int schedule=cursor.getInt(index);
			cursor.moveToNext();
			return new Vaccine(id,vaccineName,schedule);
		}catch(Exception ex){
			close();
			return null;
		}
		
	}
	
	/**
	 * return all vaccines in the table
	 * @return
	 */
	public ArrayList<Vaccine> getVaccines(){
		ArrayList<Vaccine> list=new ArrayList<Vaccine>();
		String[] columns={VACCINE_ID,VACCINE_NAME,VACCINE_SCHEDULE};
		try
		{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_VACCINES, columns,null,null, null, null, null);
			cursor.moveToFirst();
			Vaccine v=fetch();
			while(v!=null){
				list.add(v);
				v=fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			close();
			return list;
		}
	}

	
}
