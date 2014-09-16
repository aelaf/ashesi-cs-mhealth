package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class AnswerLinks extends DataClass{
	private static final String TABLE_NAME_ANSWERLINKS = "answer_links";
	private static final String KEY_ID = "link_id";
	private static final String KEY_LINK = "link";
	private static final String KEY_ANSWER_ID = "answer_id";
	
	String [] columns = {KEY_ID, KEY_LINK, KEY_ANSWER_ID};

	public AnswerLinks(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public static String getCreateQuery(){
		return "create table " + TABLE_NAME_ANSWERLINKS + "( "
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ KEY_LINK + " text,"
				+ KEY_ANSWER_ID + " INTEGER, "
				+ "FOREIGN KEY (" + KEY_ANSWER_ID + ") REFERENCES answers(" 
				+  KEY_ANSWER_ID + "))";
	}	
	
	public static String getInsert(String link, int id, int answerid){
		return "insert into " + TABLE_NAME_ANSWERLINKS 
				+ "( " + KEY_ID + ", " + KEY_LINK + ", " + KEY_ANSWER_ID + ") values (" 
				+ id + " , '" + link + "', " + answerid + ")";
	}
	
	public boolean addlink(int id, String link, int answerid){
		try{
			if(!link.isEmpty()){
				db = getReadableDatabase();
				ContentValues contentValues = new ContentValues();
				contentValues.put(KEY_ID, id);
				contentValues.put(KEY_LINK, link);
				contentValues.put(KEY_ANSWER_ID, answerid);
				db.insertWithOnConflict(TABLE_NAME_ANSWERLINKS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}
	}
	
	public AnswerLink fetch(){
		try{
			if(cursor.isAfterLast()){
				return null;
			}
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index = cursor.getColumnIndex(KEY_ID);
			int id = cursor.getInt(index);
			index = cursor.getColumnIndex(KEY_LINK);
			String link = cursor.getString(index);
			index = cursor.getColumnIndex(KEY_ANSWER_ID);
			int answerid = cursor.getInt(index);
			AnswerLink an = new AnswerLink(id, link, answerid);
			cursor.moveToNext();
			return an;
		}catch(Exception ex){
			return null;
		}
	}
	
	public ArrayList<AnswerLink> getAllLinks(){
		try{
			db= getReadableDatabase();
			cursor = db.query(TABLE_NAME_ANSWERLINKS, columns, null, null, null, null, null);
			AnswerLink an = fetch();
			ArrayList<AnswerLink> list = new ArrayList<AnswerLink>();
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
	
	public ArrayList<AnswerLink> getLinkByAnswer(int answerid){
		try{
			db= getReadableDatabase();
			cursor = db.query(TABLE_NAME_ANSWERLINKS, columns, KEY_ANSWER_ID + " = " + answerid, null, null, null, null);
			AnswerLink an = fetch();
			ArrayList<AnswerLink> list = new ArrayList<AnswerLink>();
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
	 * downloads AnswerLinks data from server 
	 */
	public void download(){
		final int deviceId=mDeviceId;

		String url= "mhealth/checkLogin/knowledgeAction.php?cmd=8";
		String data=request(url);
		System.out.println(data);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}		
			processDownloadData(obj.getJSONArray("answerlinks"));			
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
			String link;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				link=obj.getString("link");
				answerid=obj.getInt("answer_id");
				id=obj.getInt(KEY_ID);
				addlink(id, link, answerid);
			}
		}catch(Exception ex){
			return;
		}
	}

}
