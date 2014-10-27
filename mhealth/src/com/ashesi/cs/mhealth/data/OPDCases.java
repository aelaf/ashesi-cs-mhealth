package com.ashesi.cs.mhealth.data;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class OPDCases extends DataClass {

	public static final String TABLE_NAME_OPD_CASES="opd_cases";
	public static final String OPD_CASE_ID="opd_case_id";
	public static final String OPD_CASE_NAME="opd_case_name";
	public static final String OPD_CASE_CATEGORY="opd_case_category";
	public static final String OPD_CASE_DISPLAY_ORDER="opd_case_display_order";
	
	private String[] columns={OPD_CASE_ID,OPD_CASE_NAME,OPD_CASE_CATEGORY,OPD_CASE_DISPLAY_ORDER};

	public OPDCases(Context context){
		super(context);
	}
	
	public OPDCase getOPDCase(int id){
		try
		{
			db=getReadableDatabase();
			String selection =OPD_CASE_ID+"=" +id;
			cursor=db.query(TABLE_NAME_OPD_CASES, columns,selection, null, null, null,null);
			cursor.moveToFirst();
			OPDCase obj=fetch();
			close();
			return obj;
		}
		catch(Exception ex){
			Log.d("OPDCases.getOPDCase(int)", "Exception "+ex.getMessage());
			return null;
		}
		
		
	}
	
	public ArrayList<OPDCase> getAllOPDCases(int opdCaseCategory){
		ArrayList<OPDCase> list=new ArrayList<OPDCase>();
		
		try
		{
			db=getReadableDatabase();
			String selection=null;
			if(opdCaseCategory>0){
				selection=OPD_CASE_CATEGORY +"="+opdCaseCategory;
			}
			
			String order=OPD_CASE_DISPLAY_ORDER+", "+OPD_CASE_NAME;
			cursor=db.query("opd_cases", columns, selection, null, null, null, order);
			cursor.moveToFirst();
			OPDCase opdCase=fetch();
			while(opdCase!=null){
				list.add(opdCase);
				opdCase=fetch();
			}
			return list;
		}
		catch(Exception ex){
			Log.d("OPDCases.getAllOPDCases", "Exception "+ex.getMessage());
			return list;
		}
	}
	
	public OPDCase fetch(){
		try
		{
				
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(OPD_CASE_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(OPD_CASE_NAME);
			String name=cursor.getString(index);
			index=cursor.getColumnIndex(OPD_CASE_CATEGORY);
			int category=cursor.getInt(index);
			index=cursor.getColumnIndex(OPD_CASE_DISPLAY_ORDER);
			int displayOrder=0;
			if(index>0){
				displayOrder=cursor.getInt(index);
			}
			cursor.moveToNext();
			return new OPDCase(id,name,category,displayOrder);
		}catch(Exception ex){
			return null;
		}
		
	}
	
	public ArrayList<OPDCase> getArrayList(){
		ArrayList<OPDCase> list=new ArrayList<OPDCase>();
		OPDCase opdCase=fetch();
		while(opdCase!=null){
			list.add(opdCase);
			opdCase=fetch();
		}
		return list;
	}
	
	public OPDCase fetch(Cursor cursor){
		try
		{
			
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(OPD_CASE_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(OPD_CASE_NAME);
			String name=cursor.getString(index);
			index=cursor.getColumnIndex(OPD_CASE_CATEGORY);
			int category=cursor.getInt(index);
			index=cursor.getColumnIndex(OPD_CASE_DISPLAY_ORDER);
			int displayOrder=0;
			if(index>0){
				displayOrder=cursor.getInt(index);
			}
			cursor.moveToNext();
			return new OPDCase(id,name,category,displayOrder);
		}catch(Exception ex){
			return null;
		}
		
	}
	
	public boolean threadedDownload(){
		final int dataversion=0;
		
		new Thread(new Runnable() {
	        public void run() {
	        	
	            String data = request("opdcasesActions.php?cmd=1&v="+dataversion);
	            processDownloadData(data);
	        }
	    }).start();
		
		return true;
	}
	
	public HttpURLConnection connect(){
		try{
			return super.connect("opdcasesActions.php?cmd=1&deviceId="+mDeviceId);
		}catch(Exception ex){
			return null;
		}
	}

	/**
	 * clears every opd case from the table
	 * @return
	 */
	public boolean clearTable(){
		try{
			db=getWritableDatabase();
			db.delete(TABLE_NAME_OPD_CASES, null, null);
			return true;
			
		}catch(Exception ex){
			return false;
		}
	}
	public boolean processDownloadData(String data){
		try{
			
			JSONObject object=new JSONObject(data);
			int result=object.getInt("result");
			if(result==0){
				return false;
			}
			
			clearTable();
			String name;
			int id;
			int opdCaseCategory;
			int displayOrder;
			JSONArray jsonArray=object.getJSONArray("opdcases");
			for(int i=0;i<jsonArray.length();i++){
				JSONObject obj=jsonArray.getJSONObject(i);
				name=obj.getString("opdCaseName");
				id=obj.getInt("id");
				opdCaseCategory=obj.getInt("opdCaseCategory");
				displayOrder=0;
				if(obj.has("opdCaseDisplayOrder")){
					displayOrder=obj.getInt("opdCaseDisplayOrder");
				}
				addOrUpdate(id,name,opdCaseCategory,displayOrder);
				
			}
			return true;
			
		}catch(Exception ex){
			Log.e("Categories.processUpdate", ex.getMessage());
			return false;
		}
		
	}
	
	public boolean addOrUpdate(int id,String opdCaseName,int opdCaseCategory){
		return addOrUpdate(id,opdCaseName,opdCaseCategory,0);
	}
	
	public boolean addOrUpdate(int id,String opdCaseName,int opdCaseCategory, int displayOrder){
		try{
			SQLiteDatabase db=getWritableDatabase();
			ContentValues cv=new ContentValues();

			cv.put(OPD_CASE_NAME, opdCaseName);
			cv.put(OPD_CASE_ID, id);
			cv.put(OPD_CASE_CATEGORY, opdCaseCategory);
			cv.put(OPD_CASE_DISPLAY_ORDER, displayOrder);
			if(db.insertWithOnConflict(TABLE_NAME_OPD_CASES, null,cv,SQLiteDatabase.CONFLICT_REPLACE)<=0){
				return false;
			}
			close();
			return true;
		}catch(Exception ex){
			close();
			return false;
		}
	}
			
	public boolean updateDataVersion(int dataVersion){
		return true;
	}
	
	public static String getCreateSQLString(){
		return "create table " + TABLE_NAME_OPD_CASES + " (" 
				+OPD_CASE_ID + " integer primary key, "
				+OPD_CASE_NAME +" text, "
				+OPD_CASE_CATEGORY +" integer, "
				+OPD_CASE_DISPLAY_ORDER+" integer default 0"
				+" )";
	}
		
	public static String getInsertSQLString(int id, String opdCaseName, int category){
		return "insert into " + TABLE_NAME_OPD_CASES + "(" 
				+OPD_CASE_ID + " ,"
				+OPD_CASE_NAME +", "
				+OPD_CASE_CATEGORY +""
				+" ) values( "
				+id +","
				+"'"+opdCaseName+"', "
				+category
				+" )";
	}
	
	public static String getInsertSQLString(int id, String opdCaseName, int category,int displayOrder){
		return "insert into " + TABLE_NAME_OPD_CASES + "(" 
				+OPD_CASE_ID + " ,"
				+OPD_CASE_NAME +", "
				+OPD_CASE_CATEGORY +","
				+OPD_CASE_DISPLAY_ORDER
				+" ) values( "
				+id +","
				+"'"+opdCaseName+"', "
				+category +", "
				+displayOrder
				+" )";
	}

}
