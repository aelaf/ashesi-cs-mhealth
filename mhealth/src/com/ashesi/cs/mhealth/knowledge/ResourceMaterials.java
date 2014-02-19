package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class ResourceMaterials extends DataClass {

	public static final String TABLE_RESOURCE_MATERIALS = "resource_materials";
	public static final String KEY_ID = "resource_id";
	public static final String KEY_TYPE = "resource_type";
	public static final String KEY_CATEGORY_ID = "category_id";
	public static final String KEY_CONTENT = "content";

	String[] columns = { KEY_ID, KEY_TYPE, KEY_CATEGORY_ID, KEY_CONTENT };

	public ResourceMaterials(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static String getCreateQuery() {
		return "create table " + TABLE_RESOURCE_MATERIALS + " (" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TYPE + " int, "
				+ KEY_CATEGORY_ID + " int, " + KEY_CONTENT + " text, "
				+ "FOREIGN KEY( " + KEY_CATEGORY_ID
				+ ") REFERENCES categories(category_id))";
	}

	public static String getInsert(int type, int catId, String content) {

		return "insert into " + TABLE_RESOURCE_MATERIALS + " (" + KEY_TYPE + ", "
				+ KEY_CATEGORY_ID + ", " + KEY_CONTENT + ") values(" + type + "," + catId
				+ "'" + content + "'" + ")";
	}
	

	public boolean addResMat(int type, int catId, String content){
		try
		{
			if(!content.isEmpty()){
				db=getReadableDatabase();
				ContentValues values=new ContentValues();
				values.put(KEY_ID, "");
				values.put(KEY_TYPE, type);
				values.put(KEY_CATEGORY_ID, catId);
				values.put(KEY_CONTENT, content);
				db.insertWithOnConflict(TABLE_RESOURCE_MATERIALS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				return true;
			}else{
				return false;
			}
		}catch(Exception ex){
			return false;
		}			
	}
	
	public ResourceMaterial fetch(){
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
			index=cursor.getColumnIndex(KEY_TYPE);
			int type=cursor.getInt(index);
			index = cursor.getColumnIndex(KEY_CATEGORY_ID);
			int catId = cursor.getInt(index);
			ResourceMaterial resMat=new ResourceMaterial(id,type,catId, content);
			cursor.moveToNext();
			return resMat;
		}catch(Exception ex){
			return null;
		}	
	 }
	

	public ArrayList<ResourceMaterial> getAllLinks(){
		try{
			db=getReadableDatabase();
			cursor=db.query(TABLE_RESOURCE_MATERIALS, columns, null, null, null, null, null, null);
			ResourceMaterial res=fetch();
			ArrayList<ResourceMaterial> list=new ArrayList<ResourceMaterial>();
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
	

	public ResourceMaterial getMaterial(int linkId){
		try{
			db=getReadableDatabase();
			String selection=KEY_ID +"="+linkId;
			cursor=db.query(TABLE_RESOURCE_MATERIALS, columns, selection, null, null, null, null, null);
			ResourceMaterial res=fetch();
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
	 * downloads Res data from server 
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
			
			processDownloadData(obj.getJSONArray("resource_materials"));
			
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
			int type, catId;
			//String aDate;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				content=obj.getString("content");
				id=obj.getInt("resource_id");
				type=obj.getInt(KEY_TYPE);
				catId = obj.getInt(KEY_CATEGORY_ID);
				addResMat(type, catId, content);
			}
		}catch(Exception ex){
			return;
		}
	}
	
	
	
	
	

}
