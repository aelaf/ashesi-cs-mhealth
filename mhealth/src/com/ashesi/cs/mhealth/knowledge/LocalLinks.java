package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.DataConnection;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class LocalLinks extends DataClass{
	private static final String TABLE_NAME_LOCALLINKS = "local_links";
	private static final String KEY_ID = "resourceid";
	private static final String KEY_ANSWER_ID = "answerid";
	
	String [] columns = {KEY_ID, KEY_ANSWER_ID};

	public LocalLinks(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public static String getCreateQuery(){
		return "create table " + TABLE_NAME_LOCALLINKS + " ( "
				+ KEY_ID + " INTEGER," 
				+ KEY_ANSWER_ID + " INTEGER, " +
					"PRIMARY KEY(resourceid, answerid), "
				+ "FOREIGN KEY (" + KEY_ID + ") REFERENCES resource_materials(resource_id))";
	}	
	
	public static String getInsert(int id, int answerid){
		return "insert into " + TABLE_NAME_LOCALLINKS 
				+ "( " + KEY_ID + ", " +  KEY_ANSWER_ID + ") values (" 
				+ id + "," + answerid + ")";
	}
	
	public boolean addlink(int id, int answerid){
		try{
				db = getReadableDatabase();
				ContentValues contentValues = new ContentValues();
				contentValues.put(KEY_ID, id);
				contentValues.put(KEY_ANSWER_ID, answerid);
				db.insertWithOnConflict(TABLE_NAME_LOCALLINKS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
				return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public LocalLink fetch(){
		try{
			if(cursor.isAfterLast()){
				return null;
			}
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index = cursor.getColumnIndex(KEY_ID);
			int id = cursor.getInt(index);
			index = cursor.getColumnIndex(KEY_ANSWER_ID);
			int answerid = cursor.getInt(index);
			LocalLink an = new LocalLink(id, answerid);
			cursor.moveToNext();
			return an;
		}catch(Exception ex){
			return null;
		}
	}
	
	public ArrayList<LocalLink> getAllLinks(){
		try{
			db= getReadableDatabase();
			cursor = db.query(TABLE_NAME_LOCALLINKS, columns, null, null, null, null, null);
			LocalLink an = fetch();
			ArrayList<LocalLink> list = new ArrayList<LocalLink>();
			while(an!=null){
				list.add(an);
				an = fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			return null;
		}
	}
	
	public ArrayList<LocalLink> getLinkByAnswer(int answerid){
		try{
			db= getReadableDatabase();
			cursor = db.query(TABLE_NAME_LOCALLINKS, columns, KEY_ANSWER_ID + " = " + answerid, null, null, null, null);
			LocalLink an = fetch();
			ArrayList<LocalLink> list = new ArrayList<LocalLink>();
			while(an!=null){
				list.add(an);
				an = fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * downloads LocalLinks data from server 
	 */
	public void download(){
		final int deviceId=mDeviceId;

		String url=DataConnection.KNOWLEDGE_URL+"checkLogin/knowledgeAction.php?cmd=9";
		String data=request(url);
		System.out.println(data);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}		
			processDownloadData(obj.getJSONArray("local_links"));			
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
			int id;
			int answerid;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				answerid=obj.getInt("answer_id");
				id=obj.getInt(KEY_ID);
				addlink(id, answerid);
			}
		}catch(Exception ex){
			return;
		}
	}

}
