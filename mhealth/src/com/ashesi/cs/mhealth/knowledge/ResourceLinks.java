package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.DataConnection;

public class ResourceLinks extends DataClass{
	public static final String TABLE_RESOURCE_LINKS = "resource_links";
	public static final String KEY_ID = "link_id";
	public static final String KEY_ANSWER_ID = "answer_id";
	public static final String KEY_LINK = "link";
	
	String[] columns={KEY_ID, KEY_ANSWER_ID, KEY_LINK};
	
	public ResourceLinks(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public static String getCreateQuery(){
		return "create table "+ TABLE_RESOURCE_LINKS +" ("
				+ KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_ANSWER_ID +" int, "
				+ KEY_LINK +" text, "
				+ "FOREIGN KEY( "+ KEY_ANSWER_ID + ") REFERENCES answers(answer_id))";
		
	}
	

	public static String getInsert(String link,int answerId){
		
		return "insert into "
				+ TABLE_RESOURCE_LINKS +" ("
				+ KEY_LINK +", "
				+ KEY_ANSWER_ID
				+ ") values("
			    + "'"+ link +"',"
				+ answerId + ")";
	}
	
	public boolean addResLink(String link,int answerId){
		try
		{
			if(!link.isEmpty()){
				db=getReadableDatabase();
				ContentValues values=new ContentValues();
				values.put(KEY_ID, "");
				values.put(KEY_LINK, link);
				values.put(KEY_ANSWER_ID, answerId);
				db.insertWithOnConflict(TABLE_RESOURCE_LINKS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}
			
	}
	

	public ResourceLink fetch(){
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
			index=cursor.getColumnIndex(KEY_LINK);
			String content=cursor.getString(index);
			index=cursor.getColumnIndex(KEY_ANSWER_ID);
			int ansId=cursor.getInt(index);
			ResourceLink resLink=new ResourceLink(id,content,ansId);
			cursor.moveToNext();
			return resLink;
		}catch(Exception ex){
			return null;
		}	
	 }
	

	public ArrayList<ResourceLink> getAllLinks(){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_RESOURCE_LINKS, columns, null, null, null, null, null, null);
			ResourceLink res=fetch();
			ArrayList<ResourceLink> list=new ArrayList<ResourceLink>();
			while(res!=null){
				list.add(res);
				res=fetch();
			}
			close();
			return list;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	public ResourceLink getLink(int linkId){
		try{
			db=getReadableDatabase();
			String selection=KEY_ID +"="+linkId;
			cursor=db.query(TABLE_RESOURCE_LINKS, columns, selection, null, null, null, null, null);
			ResourceLink res=fetch();
			close();
			return res;
			
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
	 * downloads Links data from server 
	 */
	public void download(){
		final int deviceId=mDeviceId;
		String url=DataConnection.KNOWLEDGE_URL+"choAction?cmd=2&deviceId"+deviceId;
		String data=request(url);
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){	//error 
				return;
			}
			
			processDownloadData(obj.getJSONArray("resource_links"));
			
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
			String link;
			int id;
			int answerId;
			//String aDate;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				link=obj.getString("link");
				id=obj.getInt("link_id");
				answerId=obj.getInt(KEY_ANSWER_ID);
				addResLink(link,answerId);
			}
		}catch(Exception ex){
			return;
		}
	}
	
	

}
