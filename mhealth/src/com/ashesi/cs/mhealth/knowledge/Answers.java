package com.ashesi.cs.mhealth.knowledge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.KnowledgeActivity;

public class Answers extends DataClass {
	public static final String TABLE_NAME_ANSWERS = "answers";
	public static final String KEY_ID = "answer_id";
	public static final String KEY_CONTENT = "answer";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_QUESTION_ID = "question_id";
	public static final String KEY_ANSWER_DATE = "answer_date";

	String[] columns = { KEY_ID, KEY_CONTENT, KEY_USER_ID, KEY_QUESTION_ID,
			KEY_ANSWER_DATE };

	public Answers(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static String getCreateQuery() {
		return "create table " + TABLE_NAME_ANSWERS + " (" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CONTENT
				+ " text, " + KEY_USER_ID + " int," + KEY_QUESTION_ID + " BLOB UNIQUE,"
				+ KEY_ANSWER_DATE + " DATETIME, " + "FOREIGN KEY ("
				+ KEY_USER_ID + ") REFERENCES users(user_id), "
				+ "FOREIGN KEY (" + KEY_QUESTION_ID
				+ ") REFERENCES questions(guid))";

	}

	public static String getInsert(String answer, int userId, int questionID) {

		return "insert into " + TABLE_NAME_ANSWERS + " (" + KEY_CONTENT
				+ ", " + KEY_USER_ID + ", " + KEY_QUESTION_ID + ", "
				+ KEY_ANSWER_DATE + ") values(" + "'" + answer + "'," + userId
				+ ", " + questionID + " , NOW()) ";
	}

	public boolean addAnswer(int id, String answer, int userId, String questionID, String date) {
		try {
			if (!answer.isEmpty()) {
				db = getReadableDatabase();
				ContentValues values = new ContentValues();
				// values.put(KEY_ID, id);
				values.put(KEY_CONTENT, answer);
				values.put(KEY_USER_ID, userId);
				values.put(KEY_QUESTION_ID, questionID);
				if(date ==  ""){		//if the question generated locally then generate a time stamp for it.
					Date date1 = new Date();		            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
					values.put(KEY_ANSWER_DATE, dt.format(date1));
				}else{ 
					values.put(KEY_ANSWER_DATE, date);
				}
				db.insertWithOnConflict(TABLE_NAME_ANSWERS, null, values,
						SQLiteDatabase.CONFLICT_REPLACE);
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}
	}
	

	public Answer fetch(){
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
			index=cursor.getColumnIndex(KEY_USER_ID);
			int userId=cursor.getInt(index);
			index=cursor.getColumnIndex(KEY_QUESTION_ID);
			String qID=cursor.getString(index);
			index = cursor.getColumnIndex(KEY_ANSWER_DATE);
			String theDate = cursor.getString(index);
			Answer ans =new Answer(id,content,userId, qID, theDate);
			cursor.moveToNext();
			return ans;
		}catch(Exception ex){
			return null;
		}		
	}
	
	public ArrayList<Answer> getAllAnswers(){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_ANSWERS, columns, null, null, null, null, null, null);
			Answer a=fetch();
			ArrayList<Answer> list=new ArrayList<Answer>();
			while(a!=null){
				list.add(a);
				a=fetch();
			}
			close();
			return list;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	public Answer getAnswer(int aId){
		try{
			db=getReadableDatabase();
			String selection=KEY_ID +"="+aId;
			cursor=db.query(TABLE_NAME_ANSWERS, columns, selection, null, null, null, null, null);
			Answer ans=fetch();
			close();
			return ans;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	public boolean checkIfExists(String qID){
		System.out.println(qID);
		try{
			db=getReadableDatabase();		
			String Query = "Select * from " + TABLE_NAME_ANSWERS + " where " + KEY_QUESTION_ID + "=" + qID;
		    cursor = db.rawQuery(Query, null);
		    System.out.println(cursor.getCount());
            if(cursor.getCount()<=0){
            	close();
            	return false;
           }
           close();
		   return true;
		}catch(Exception ex){
			close();
			return false;
		}
	}
	public Answer getByQuestion(String qID){
		System.out.println(qID);
		try{
			db=getReadableDatabase();
			String Query = "Select * from " + TABLE_NAME_ANSWERS + " where " + KEY_QUESTION_ID + "='" + qID + "'";
		    cursor = db.rawQuery(Query, null);
		    Answer ans=fetch();
		    Log.d("Current Answer", ans.toString());
			close();
			return ans;			
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
	 * downloads Answer data from server 
	 */
	public void download(){
		final int deviceId=mDeviceId;

		String url="http://cs.ashesi.edu.gh/mhealth/checkLogin/knowledgeAction.php?cmd=7";//&deviceId"+deviceId;
		String data=request(url);
		System.out.println(data);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}
			
			processDownloadData(obj.getJSONArray("answers"));
			
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
			Questions qs = new Questions(getContext());
			
			JSONObject obj;
			String answer;
			int id;
			int userId;
			String questionId;
			String aDate;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				answer=obj.getString("answer");
				id=obj.getInt("answer_id");
				userId=obj.getInt(KEY_USER_ID);
				questionId = obj.getString(KEY_QUESTION_ID);
				aDate = obj.getString(KEY_ANSWER_DATE);
				qs.changeStatus(questionId, 3);
				addAnswer(id, answer,userId, questionId, aDate);
			}
		}catch(Exception ex){
			return;
		}
	}
}
