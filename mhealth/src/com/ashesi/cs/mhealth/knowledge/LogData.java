package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class LogData extends DataClass{

	public static final String TABLE_NAME_LOG = "mlog";
	public static final String KEY_ID = "log_id";
	public static final String KEY_LOG_CODE = "log_code";
	public static final String KEY_DATE = "log_date";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_SOURCE = "source";
	public static final String KEY_MSG = "msg";
	
	String [] columns = {KEY_ID, KEY_LOG_CODE, KEY_DATE, KEY_USERNAME, KEY_SOURCE,KEY_MSG};
	public LogData(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public static String getCreateQuery(){
		return "create table " + TABLE_NAME_LOG + "(" 
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ KEY_LOG_CODE + " INTEGER, " 
				+ KEY_DATE + " DATETIME,"
				+ KEY_USERNAME + " TEXT, " 
				+ KEY_SOURCE + " TEXT, " 
				+ KEY_MSG + " TEXT )";				
	}
	
	public static String getInsert(int log_code, String date, String username, String source, String msg){
		return 	"insert into "
				+ TABLE_NAME_LOG +" ("
				+ KEY_LOG_CODE +", "
				+ KEY_DATE + ", "
				+ KEY_USERNAME + " ," 
				+ KEY_SOURCE + ", " 
				+ KEY_MSG  
				+ ") values("
			    + log_code + ", "
				+ "'"+ date +"','"
				+ username + "','"
				+ source
				+"',' " + msg + "); ";
	}
	
	public boolean addLog(int log_code, String date, String username, String source, String msg){
		try{
			db = getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(KEY_LOG_CODE, log_code);
			values.put(KEY_DATE, date);
			values.put(KEY_USERNAME, username);
			values.put(KEY_MSG, msg);
			values.put(KEY_SOURCE, source);
			db.insertWithOnConflict(TABLE_NAME_LOG, null, values, SQLiteDatabase.CONFLICT_REPLACE);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	public LogD fetch(){
		try{
			if(cursor.isAfterLast()){
				return null;
			}
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();	
			}
			
			int index = cursor.getColumnIndex(KEY_ID);
			int id = cursor.getInt(index);
			index = cursor.getColumnIndex(KEY_LOG_CODE);
			int log_code = cursor.getInt(index);
			index = cursor.getColumnIndex(KEY_DATE);
			String date = cursor.getString(index);
			index = cursor.getColumnIndex(KEY_USERNAME);
			String username = cursor.getString(index);
			index = cursor.getColumnIndex(KEY_SOURCE);
			String source =  cursor.getString(index);
			index = cursor.getColumnIndex(KEY_MSG);
			String msg = cursor.getString(index);
			
			LogD l = new LogD(id, log_code, date, username, source, msg);
			cursor.moveToNext();
			return l;
		}catch(Exception ex){
			return null;
		}		
	}
	
	public ArrayList<LogD> getAllLog(){
		try{
			db = getReadableDatabase();
			cursor = db.query(TABLE_NAME_LOG, columns, null, null, null, null, null);	
			LogD l=fetch();
			ArrayList<LogD> list = new ArrayList<LogD>();
			
			while(l!=null){
				list.add(l);
				l=fetch();
			} 
			close();
			return list;
		}catch(Exception ex){
			return null;
		}
	}

	/**
	 * downloads LogData data from server 
	 */
	public void download(){
		String url="http://cs.ashesi.edu.gh/mhealth/checkLogin/knowledgeAction.php?cmd=7";//&deviceId"+deviceId;
		String data=request(url);
		System.out.println(data);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}		
			processDownloadData(obj.getJSONArray("mlog"));			
		}catch(Exception ex){
			return;
		}
	}
	

	/**
	 * processes the data received from server
	 * @param jsonArray
	 * 
	 */
	private void processDownloadData(JSONArray jsonArray){
		try{	
			JSONObject obj;
			int id, log_code;
			String date, username, source,msg;
			
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				id = obj.getInt(KEY_ID);
				date=obj.getString(KEY_DATE);
				username=obj.getString(KEY_USERNAME);
				msg = obj.getString(KEY_MSG);
				source = obj.getString(KEY_SOURCE);
				log_code = obj.getInt(KEY_LOG_CODE);
				addLog(log_code, date, username, source, msg);
			}
		}catch(Exception ex){
			return;
		}
	}

}
