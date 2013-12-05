package com.ashesi.cs.mhealth.knowledge;

import org.json.JSONArray;
import org.json.JSONObject;


import android.util.Log;

public class JSONCategories  {
	private JSONArray jsonCategories;
	private int version;
	private String strError;
	
	public JSONCategories(){
		
	}
	
	
	public JSONCategories(JSONArray jsonCategories, int version){
		this.jsonCategories=jsonCategories;
		this.version=version;
	}
	public int getVersion()
	{
		return version;
	}
	
	public String getError()
	{
		return strError;
	}
	
	public int getLength(){
		return jsonCategories.length();
	}
	public Category getCategory(int index){
		try{
			JSONObject object=jsonCategories.getJSONObject(index);
			return new Category(object);
		}catch(Exception ex){
			Log.d("Category",ex.getMessage());
			return null;
		}
	}
	
	public boolean parse(String data){
		try{
			JSONObject object=new JSONObject(data);
			int result=object.getInt("result");
			if(result==0){
				Log.d("JSONCategory.parse","reult is 0");
				return false;
			}
			version=object.getInt("version");	//if there is no new data, don't update
			if(version==0){
				Log.d("JSONCategory.parse","version is 0");
				return false;
			}
			
			jsonCategories=object.getJSONArray("categories");
			
			return true;
		}catch(Exception ex){
			Log.e("Categories.processUpdate", ex.getMessage());
			return false;
		}
		
	}
	
}

	
	
	

	
