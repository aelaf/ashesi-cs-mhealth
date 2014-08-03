package com.ashesi.cs.mhealth.knowledge;

import java.util.ArrayList;

import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * A class for categories of health information topics 
 */
public class Categories extends DataClass{
	public static final String TABLE_NAME_CATEGORIES="categories";
	public static final String CATEGORY_ID="category_id";
	public static final String CATEGORY_NAME="category_name";

	
	private String[] columns={CATEGORY_ID,CATEGORY_NAME};
	
	
	
	public Categories(Context context){
		super(context);
	}
	
	/**
	 * gets one category object identified by id
	 * @param id
	 * @return
	 */
	public Category getCategory(int id){
		//query
		try{
		
			String selection= "category_id="+id;
			SQLiteDatabase db=getReadableDatabase();
			Cursor cursor=db.query(TABLE_NAME_CATEGORIES, columns, selection, null, null, null, null);
			
			if(cursor.getCount()==0){
				Log.d("Category.getCategory","empty data set");
				return null;
			}
			
			cursor.moveToFirst();
			id=cursor.getInt(0);
			String category_name=cursor.getString(1);
			Log.d("getCategory","name :"+category_name);
			Category c=new Category(id,category_name);
			cursor.close();
			db.close();
			return c;
		}catch(Exception ex){
			Log.d("getCategory Ex",ex.getMessage());
			return null;
		}
		
	}
	
	/**
	 * returns all categories
	 * @return
	 */
	public ArrayList<Category> getAllCategories(){
		//TODO:find a way to just return boolean rather than array
		try
		{
			SQLiteDatabase db=getReadableDatabase();
			Cursor cursor=db.query(TABLE_NAME_CATEGORIES, columns, null, null, null, null, null);
			
			ArrayList<Category> array=new ArrayList<Category>();
			cursor.moveToFirst();
			Category category=fetch(cursor);
			while(category!=null){		
				array.add(category);
				category=fetch(cursor);
	
			}
			cursor.close();
			db.close();
			return array;
		}catch(Exception ex)
		{
			Log.d("Categories.getAllCategories","Exception " +ex.getMessage());
			return null;
		}
		
		
		
	}
	/**
	 * Adds or updates a category
	 * @param id
	 * @param categoryName
	 * @return
	 */
	public boolean addOrUpdate(int id, String categoryName){
		SQLiteDatabase db=getWritableDatabase();
		ContentValues cv=new ContentValues();
		Log.d("Category.processData",categoryName);
		cv.put("category_name", categoryName);
		cv.put("category_id", id);
		if(db.insertWithOnConflict(TABLE_NAME_CATEGORIES, null,cv,SQLiteDatabase.CONFLICT_REPLACE)<=0){
			return false;
		}
		db.close();
		return true;
	}
	
	/**
	 * Synchronizes the local table by downloading new data
	 * @return
	 */
	public boolean synch(){
		//TODO: add call back function
		//send synch request by sending version number
		final int dataversion=0;//getDataVersion();
		Log.e("Categories.synch", "synch called");
		new Thread(new Runnable() {
	        public void run() {
	        	Log.e("Categories.synch", "request called");
	            String data = request("mhealth/ajax_categories.php?cmd=1&v="+dataversion);
	            processUpdate(data);
	        }
	    }).start();
		                      
		return true;
	}
	
	/**
	 * processes the data from server to update the local database
	 * @param data
	 */
	protected void processUpdate(String data){
		try{
			//TODO: use JSONCategories parser
			JSONObject object=new JSONObject(data);
			int result=object.getInt("result");
			if(result==0){
				Log.d("Category.processUpdate","resilt is 0");
				return;
			}
			int version=object.getInt("version");	//if there is no new data, don't update
			if(version==0){
				Log.d("Category.processUpdate","version is 0");
				return;
			}
			Log.d("Category.processUpdate","calling update");
			JSONCategories jsonCategories=new JSONCategories(object.getJSONArray("categories"),version);
			this.updateData(jsonCategories);
			updateDataVersion(version);
		}catch(Exception ex){
			Log.e("Categories.processUpdate", ex.getMessage());
		}
		
	}
	
	/** 
	 * update the local data base on a JSON array
	 * @param jsonCategories
	 * @return
	 */
	public  boolean updateData(JSONCategories jsonCategories){
		//compare version
		Category c;
		
		int length =jsonCategories.getLength();
		Log.d("Category.processData","Updating "+length);
		for(int i=0; i<length; i++){
			c=jsonCategories.getCategory(i);
			if(c!=null){
				addOrUpdate(c.getID(), c.getCategoryName());
			}
			else
			{
				Log.d("Category.processData","c is null");
			}
		}
		
		return true;
	}
	
	/**
	 * record the version of the local data
	 * @param version
	 * @return
	 */
	public boolean updateDataVersion(int version){
		SQLiteDatabase db=getWritableDatabase();
		ContentValues cv=new ContentValues();
		
		cv.put("dataname", "categories");
		cv.put("version", version);
		if(db.insertWithOnConflict("dataversion", null,cv,SQLiteDatabase.CONFLICT_REPLACE)<=0){
			return false;
		}
		db.close();
		return true;
	}
		
	/**
	 * fetch category from cursor
	 * @param cursor
	 * @return
	 */
	public Category fetch(Cursor cursor){
		//TODO: use a field cursor to fetch for efficiency
		try
		{
			if(cursor==null){
				return null;
			}
				
			if(cursor.isClosed()){
				return null;
			}
			
			if(cursor.isAfterLast()){
				return null;
			}
			int id=cursor.getInt(0);
			String categoryName=cursor.getString(1);
			Category c=new Category(id,categoryName);
			cursor.moveToNext();
			return c;
		}
		catch(Exception ex){
			Log.d("Categories.fetch()", "Exception"+ex.getMessage());
			return null;
		}
		
	}
	
	public static String getCreateSqlString(){
		return "create table "+ TABLE_NAME_CATEGORIES +" ("
				+CATEGORY_ID + " integer primary key, "
				+CATEGORY_NAME+" text "+
				")";
	
	}
	
	public static String getInsert(String cat_name){
		return "insert into " +
				TABLE_NAME_CATEGORIES + " ("
				  + CATEGORY_NAME + 
				" ) values (" + "'" + cat_name + "' )";
	}	
	
}
