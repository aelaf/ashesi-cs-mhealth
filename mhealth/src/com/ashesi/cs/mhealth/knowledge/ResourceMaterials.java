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
	public static final String KEY_DESC = "description";
	public static final String KEY_CONTENT = "content";	
	public static final String KEY_TAG = "tag";         //This is the topic that the resource material fall under

	String[] columns = { KEY_ID, KEY_TYPE, KEY_CATEGORY_ID, KEY_DESC, KEY_CONTENT, KEY_TAG };

	public ResourceMaterials(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static String getCreateQuery() {
		return "create table " + TABLE_RESOURCE_MATERIALS + " (" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TYPE + " int, "
				+ KEY_CATEGORY_ID + " int, " + KEY_CONTENT + " text, "
				+ KEY_DESC + " text, "
				+ KEY_TAG + " text,"
				+ "FOREIGN KEY( " + KEY_CATEGORY_ID
				+ ") REFERENCES categories(category_id))";
	}

	public static String getInsert(int id, int type, int catId, String content, String desc, String tag) {

		return "insert into " + TABLE_RESOURCE_MATERIALS + " (" + KEY_ID + "," + KEY_TYPE + ", "
				+ KEY_CATEGORY_ID + ", " + KEY_CONTENT + "," + KEY_DESC + ", " + KEY_TAG + ") " +
						"values(" + id + ", " + type + "," + catId
				+ "'" + content + "'" + "'" + desc +  "'" + "'" + tag + "'" + ")";
		
	}
	

	public boolean addResMat(int id, int type, int catId, String content, String desc, String tag){
		try
		{
			if(!content.isEmpty()){
				db=getReadableDatabase();
				ContentValues values=new ContentValues();
				values.put(KEY_ID, id);
				values.put(KEY_TYPE, type);
				values.put(KEY_CATEGORY_ID, catId);
				values.put(KEY_CONTENT, content);
				values.put(KEY_DESC, desc);
				values.put(KEY_TAG, tag);
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
			index = cursor.getColumnIndex(KEY_DESC);
			String desc = cursor.getString(index);
			index = cursor.getColumnIndex(KEY_TAG);
			String tag = cursor.getString(index);
			ResourceMaterial resMat=new ResourceMaterial(id,type,catId, content, desc, tag);
			cursor.moveToNext();
			return resMat;
		}catch(Exception ex){
			return null;
		}	
	 }
	

	public ArrayList<ResourceMaterial> getAllMaterials(){
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
	

	public ResourceMaterial getMaterial(int matId){
		try{
			db=getReadableDatabase();
			String selection=KEY_ID +"="+matId;
			cursor=db.query(TABLE_RESOURCE_MATERIALS, columns, selection, null, null, null, null, null);
			ResourceMaterial res=fetch();
			close();
			return res;
			
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * Return the current max id
	 * @return
	 */
	public int getMaxID(){
		try{
			db = getReadableDatabase();
			cursor = db.rawQuery("select MAX(" + KEY_ID + ") as max_id from " + TABLE_RESOURCE_MATERIALS , null);
			cursor.getColumnIndex("maxId");
			int id = 0;     
		    if (cursor.moveToFirst())
		    {
		        do
		        {           
		            id = cursor.getInt(0);                  
		        } while(cursor.moveToNext());           
		    }
		    return id;
		}catch(Exception e){
			return 0;
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
			String content, desc, tag;
			int id;
			int type, catId;
			//String aDate;
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				content=obj.getString("content");
				id=obj.getInt("resource_id");
				type=obj.getInt(KEY_TYPE);
				catId = obj.getInt(KEY_CATEGORY_ID);
				desc = obj.getString(KEY_DESC);
				tag = obj.getString(KEY_TAG);
				addResMat(id, type, catId, content, desc, tag);
			}
		}catch(Exception ex){
			return;
		}
	}
	
	
	
	
	

}
