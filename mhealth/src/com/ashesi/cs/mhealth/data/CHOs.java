package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class CHOs extends DataClass {
	String[] columns={CHO_ID,CHO_NAME,SUBDISTRICT_ID};
	
	public static final String TABLE_NAME_CHOS="chos";
	public static final String CHO_ID="cho_id";
	public static final String CHO_NAME="cho_name";
	public static final String SUBDISTRICT_ID="subdistrict_id";
	
	public CHOs(Context context){
		super(context);
	}
	
	public static String getCreateQuery(){
		return "create table "+TABLE_NAME_CHOS +" ("
				+ CHO_ID +" int primary key, "
				+ CHO_NAME +" text, "
				+ SUBDISTRICT_ID +" int "
				+ " )";
		
	}
	
	public static String getInsert(int id,String choName,int subdistrictId){
		return "insert into "
				+ TABLE_NAME_CHOS +" ("
				+ CHO_ID +", "
				+ CHO_NAME +", "
				+SUBDISTRICT_ID
				+") values("
				+ id + ", "
				+ "'"+ choName +"',"
				+subdistrictId
				+") ";
	}
	
	public boolean addCHO(int id,String choName, int subdistrictId){
		try
		{
			db=getReadableDatabase();
			ContentValues values=new ContentValues();
			values.put(CHO_ID, id);
			values.put(CHO_NAME, choName);
			values.put(SUBDISTRICT_ID, subdistrictId);
			
			db.insertWithOnConflict(TABLE_NAME_CHOS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public CHO fetch(){
		try
		{
			if(cursor.isAfterLast()){
				return null;
			}
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			
			int index=cursor.getColumnIndex(CHO_ID);
			int choId=cursor.getInt(index);
			index=cursor.getColumnIndex(CHO_NAME);
			String choName=cursor.getString(index);
			index=cursor.getColumnIndex(SUBDISTRICT_ID);
			int subdistrictId=cursor.getInt(index);
			CHO cho=new CHO(choId,choName,subdistrictId,"");
			cursor.moveToNext();
			return cho;
		}catch(Exception ex){
			return null;
		}
		
	}
	
	public ArrayList<CHO> getAllCHOs(){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_CHOS, columns, null, null, null, null, null, null);
			CHO cho=fetch();
			ArrayList<CHO> list=new ArrayList<CHO>();
			while(cho!=null){
				list.add(cho);
				cho=fetch();
			}
			close();
			return list;
			
		}catch(Exception ex){
			return null;
		}
	}

	public CHO getCHO(int choId){
		try{
			
			db=getReadableDatabase();
			String selection=CHO_ID +"="+choId;
			cursor=db.query(TABLE_NAME_CHOS, columns, selection, null, null, null, null, null);
			CHO cho=fetch();
			close();
			return cho;
			
		}catch(Exception ex){
			return null;
		}
	}
	/**
	 * calls download from a thread
	 */
	public void threadedDownload(){
		new Thread(new Runnable() {
	        public void run() {
	        	download();
	        }
		}).start();
	}
	/**
	 * downloads CHO data from server 
	 */
	public void download(){
		final int deviceId=mDeviceId;
		String url="choAction?cmd=2&deviceId"+deviceId;
		String data=request(url);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}
			
			processDownloadData(obj.getJSONArray("chos"));
			
		}catch(Exception ex){
			return;
		}
	}
	
	/**
	 * processes the data received from server
	 * @param jsonArray
	 */
	private void processDownloadData(JSONArray jsonArray){
		try{
			JSONObject obj;
			String choName;
			int id;
			int subdistrictId;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				choName=obj.getString("choName");
				id=obj.getInt("id");
				subdistrictId=obj.getInt("subdistrictId");
				addCHO(id,choName,subdistrictId);
			}
		}catch(Exception ex){
			return;
		}
	}
}
