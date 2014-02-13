package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class Questions extends DataClass{
    
	public static final String TABLE_NAME_QUESTIONS = "questions";
	public static final String KEY_ID = "q_id";
	public static final String KEY_CONTENT = "q_content";
	public static final String KEY_CHO_ID = "cho_id";
	public static final String KEY_CATEGORY_ID = "category_id";
	
	String[] columns={KEY_ID, KEY_CONTENT, KEY_CHO_ID, KEY_CATEGORY_ID};
	
	
	public Questions(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public static String getCreateQuery(){
		return "create table "+ TABLE_NAME_QUESTIONS +" ("
				+ KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_CONTENT +" text, "
				+ KEY_CATEGORY_ID +" int, "
				+ KEY_CHO_ID +" int ,"
				+ "FOREIGN KEY ("+ KEY_CHO_ID +") REFERENCES chos(cho_id))";
		
	}
	
	public static String getInsert(String content,int choId, int categoryId){
		return "insert into "
				+ TABLE_NAME_QUESTIONS +" ("
				+ ", "
				+ KEY_CONTENT +", "
				+ KEY_CATEGORY_ID + ", "
				+ KEY_CHO_ID 
				+") values("
			    + ", "
				+ "'"+ content +"',"
				+ categoryId + ", "
				+ choId
				+") ";
	}
	
	public boolean addQuestion(int id,String content,int choId, int categoryId){
		try
		{
			if(!content.isEmpty()){
				db=getReadableDatabase();
				ContentValues values=new ContentValues();
				//values.put(KEY_ID, id);
				values.put(KEY_CONTENT, content);
				values.put(KEY_CATEGORY_ID, categoryId);
				values.put(KEY_CHO_ID, choId);
				db.insertWithOnConflict(TABLE_NAME_QUESTIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}
	}
	
	public Question fetch(){
		try
		{
			if(cursor.isAfterLast()){
				return null;
			}
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			
			int index=cursor.getColumnIndex(KEY_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(KEY_CONTENT);
			String content=cursor.getString(index);
			index=cursor.getColumnIndex(KEY_CHO_ID);
			int choId=cursor.getInt(index);
			index=cursor.getColumnIndex(KEY_CATEGORY_ID);
			int catId=cursor.getInt(index);
			Question q=new Question(id,content,choId,catId);
			cursor.moveToNext();
			return q;
		}catch(Exception ex){
			return null;
		}		
	}
	
	public ArrayList<Question> getAllQuestions(){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_QUESTIONS, columns, null, null, null, null, null, null);
			Question q=fetch();
			ArrayList<Question> list=new ArrayList<Question>();
			while(q!=null){
				list.add(q);
				q=fetch();
			}
			close();
			return list;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	public Question getQuestion(int qId){
		try{
			db=getReadableDatabase();
			String selection=KEY_ID +"="+qId;
			cursor=db.query(TABLE_NAME_QUESTIONS, columns, selection, null, null, null, null, null);
			Question q=fetch();
			close();
			return q;
			
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
	 * downloads Question data from server 
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
			
			processDownloadData(obj.getJSONArray("questions"));
			
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
			String content;
			int id;
			int choId;
			int catId;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				content=obj.getString("q_content");
				id=obj.getInt("q_id");
				catId=obj.getInt(KEY_CATEGORY_ID);
				choId = obj.getInt(KEY_CHO_ID);
				addQuestion(id,content,choId, catId);
			}
		}catch(Exception ex){
			return;
		}
	}
	
	

}
