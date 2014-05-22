package com.ashesi.cs.mhealth.knowledge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

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
	public static final String KEY_DATE = "question_date";
	public static final String KEY_GUID = "guid";
	
	
	String[] columns={KEY_ID, KEY_CONTENT, KEY_CHO_ID, KEY_CATEGORY_ID, KEY_DATE, KEY_GUID, DataClass.REC_STATE};
	
	
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
				+ KEY_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
				+ KEY_GUID + " BLOB UNIQUE, "
				+ DataClass.REC_STATE+" integer, "
				+ "FOREIGN KEY( "+ KEY_CATEGORY_ID + ") REFERENCES categories(category_id), "
				+ "FOREIGN KEY("+ KEY_CHO_ID +") REFERENCES chos(cho_id))";
		
	}
	
	public static String getInsert(String content,int choId, int categoryId, UUID guid){
		Date date = new Date();
        DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
		return "insert into "
				+ TABLE_NAME_QUESTIONS +" ("
				+ KEY_CONTENT +", "
				+ KEY_CATEGORY_ID + ", "
				+ KEY_CHO_ID + " ," 
				+ KEY_DATE + ", " 
				+ KEY_GUID + ", "  
				+ DataClass.REC_STATE
				+ ") values("
			    + ", "
				+ "'"+ content +"',"
				+ categoryId + ", "
				+ choId
				+" , " + dt.format(date) + ", "
				+ guid + "," + DataClass.REC_STATE_NEW + ") ";
		
	}
	
	public boolean addQuestion(int id,String content,int choId, int categoryId, String date, String guid, int rec_state){
		try
		{
			if(!content.isEmpty()){
				db=getReadableDatabase();
				ContentValues values=new ContentValues();
				values.put(KEY_CONTENT, content);
				values.put(KEY_CATEGORY_ID, categoryId);
				values.put(KEY_CHO_ID, choId);
				if(date ==  ""){		//if the question generated locally then generate a time stamp for it.
					Date date1 = new Date();		            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
					values.put(KEY_DATE, dt.format(date1));
				}else{ 
					values.put(KEY_DATE, date);
				}
				UUID g_uid = null;
				if(guid == ""){	//If a guid is not provided
					g_uid = UUID.randomUUID();
					values.put(KEY_GUID, g_uid.toString());
				}else{
					values.put(KEY_GUID, guid);
				}		
				values.put(DataClass.REC_STATE, rec_state);
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
			index = cursor.getColumnIndex(KEY_DATE);
			String theDate = cursor.getString(index);
			index= cursor.getColumnIndex(KEY_GUID);
			String guid = cursor.getString(index);
			index = cursor.getColumnIndex(DataClass.REC_STATE);
			int rec_st = cursor.getInt(index);
			
	
			Question q=new Question(id,content,choId,catId, theDate, guid, rec_st);
			cursor.moveToNext();
			return q;
		}catch(Exception ex){
			return null;
		}		
	}
	
	public ArrayList<Question> getAllQuestions(){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_QUESTIONS, columns, null, null, null, null, KEY_DATE + " DESC", null);
			Question q=fetch();
			ArrayList<Question> list=new ArrayList<Question>();
			
			System.out.println(q.toString());
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
	
	public ArrayList<Question> getQuestionsby(String filter){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_QUESTIONS, columns, filter, null, null, null, KEY_DATE + " DESC", null);
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
	
	public void changeStatus(String id, int status){
		try{
			db=getReadableDatabase();
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(DataClass.REC_STATE, status);
	        db.update(TABLE_NAME_QUESTIONS, initialValues, KEY_GUID + "= ?", new String[] {String.valueOf(status)});
	        db.close();
		}catch(Exception e){
			db.close();
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
	
	//customize this for the question
	/**
	 * downloads Question data from server 
	 */
	public void download(){
		//final int deviceId=mDeviceId;
		String url="http://cs.ashesi.edu.gh/mhealth/checkLogin/knowledgeAction.php?cmd=5";
		System.out.println("Starting Post request");
		String data=request(url);
		System.out.println(data);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}
			System.out.println(obj.getJSONArray("questions").toString());
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
			int choId;
			int catId;
			int recState;
			String aDate;
			String guid;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				guid = obj.getString(KEY_GUID);
				content=obj.getString("q_content");
				catId=obj.getInt(KEY_CATEGORY_ID);
				choId = obj.getInt(KEY_CHO_ID);
				aDate = obj.getString(KEY_DATE);
				recState = obj.getInt(DataClass.REC_STATE);
				addQuestion(0, content,choId, catId, aDate, guid, recState);
			}
		}catch(Exception ex){
			return;
		}
	}
}
