package com.ashesi.cs.mhealth.knowledge;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * represents one category record
 * @author Aelaf Dafla
 *
 */
public class Category {
	 
	private String categoryName;
	private int id;
	
	public Category(){
		
	}
	
	/**
	 * create from JSON object
	 * @param jsonCategory
	 * @throws JSONException
	 */
	public Category(JSONObject jsonCategory) throws JSONException{
		categoryName=jsonCategory.getString("categoryName");
		id=jsonCategory.getInt("id");
		
	}
	/**
	 * create Category object
	 * @param id
	 * @param categoryName
	 */
	public Category(int id,String categoryName){
		this.id=id;
		this.categoryName=categoryName;
	}
	/**
	 * read from a cursor and initialize the object. Cursor has to be positioned before calling method
	 * @param cursor
	 * @return
	 */
	public boolean initFromDB(Cursor cursor){
		try{
			int index=cursor.getColumnIndex("category_id");
			id=cursor.getInt(index);
			index=cursor.getColumnIndex("category_name");
			categoryName=cursor.getString(index);
			return true;
		}
		catch(Exception ex){
			//Log.d("Category.initFromDB","Exception "+ex.getMessage());
			return false;
		}
	}
	/**
	 * saves the object to database
	 * @param db
	 * @return
	 */
	public boolean saveToDb(SQLiteDatabase db){
		ContentValues cv=new ContentValues();
		cv.put("category_name", categoryName);
		cv.put("category_id", id);
		if(db.insert("categories", null, cv)<=0){
			return false;
		}
		
		return true;
	}
	/**
	 * returns the current category name
	 * @return
	 */
	public String getCategoryName(){
		return categoryName;
	}
	/**
	 * returns the current category id
	 * @return
	 */
	public int getID(){
		return id;
	}
	/**
	 * sets the category name
	 * @param categoryName
	 */
	public void setCagegoryName(String categoryName){
		this.categoryName=categoryName;
	}
	/**
	 * sets the category id
	 * @param id
	 */
	public void setID(int id){
		this.id=id;
	}
	

}
