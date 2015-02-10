package com.ashesi.cs.mhealth.data;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ashesi.cs.mhealth.DataClass;

public class FamilyPlanningServices extends DataClass {
	public final static String SERVICE_ID="service_id";
	public final static String SERVICE_NAME="service_name";
	public final static String SERVICE_SCHEDULE="service_schedule";
	public final static String DISPLAY_ORDER="display_order";
	public final static int	NEW_ACCEPTOR_SERVICE_ID=17;

	public final static String TABLE_NAME_FAMILY_PLANNING_SERVICES="family_planning_services";
	private static final String SERVICE_SCEHDULE = null;
	
	public FamilyPlanningServices(Context context){
		super(context);
	}
	
	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_FAMILY_PLANNING_SERVICES +" ( "
				+SERVICE_ID + "  integer primary key , "
				+SERVICE_NAME+ " text, "
				+SERVICE_SCHEDULE +" integer default 0, "
				+DISPLAY_ORDER +" integer default 1000"
				+")";
		
	}
	
	public static String getInsertSQLString(int id,String serviceName){
		return "insert into "+TABLE_NAME_FAMILY_PLANNING_SERVICES +"("
				+SERVICE_ID+", "
				+SERVICE_NAME
				+") values("+
				+id+","
				+"'"+serviceName+"'" 
				+")";
				
	}
	
	public static String getInsertSQLString(int id,String serviceName,int schedule, int displayOrder){
		return "insert into "+TABLE_NAME_FAMILY_PLANNING_SERVICES +"("
				+SERVICE_ID+", "
				+SERVICE_NAME+", "
				+SERVICE_SCHEDULE+", "
				+DISPLAY_ORDER
				+") values( "+
				+id+","
				+"'"+serviceName+"',"
				+schedule+","
				+displayOrder
				+" )";
				
	}

	public HttpURLConnection connect(){
		try{
			return super.connect("family_planning_servicesActions.php?cmd=1&deviceId="+mDeviceId);
		}catch(Exception ex){
			return null;
		}
	}
	
	/**
	 * writes the data from server to local database 
	 * @param data
	 */
	public boolean processDownloadData(String data){
		try{
			JSONObject obj=new JSONObject(data);
			int result=obj.getInt("result");
			if(result==0){
				return false;
			}
						
			
			JSONArray jsonArray=obj.getJSONArray("familyPlanningServices");
			for(int i=0;i<jsonArray.length();i++){
				obj=jsonArray.getJSONObject(i);
				int serviceId=obj.getInt("id");
				String serviceName=obj.getString("serviceName");
				int schedule=0;
				if(obj.has("schedule")){
					schedule=obj.getInt("schedule");
				}
				int displayOrder=1000;
				if(obj.has("displayOrder")){
					displayOrder=obj.getInt("displayOrder");
				}
				addService(serviceId,serviceName,schedule,displayOrder);
			}
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
	
	/**
	 * adds service to table
	 * @param serviceId
	 * @param serviceName
	 * @return
	 */
	public boolean addService(int serviceId, String serviceName){
		try{
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(SERVICE_ID,serviceId);
			cv.put(SERVICE_NAME, serviceName);

			if(db.insertWithOnConflict(TABLE_NAME_FAMILY_PLANNING_SERVICES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)<=0){
				return false;
			}
			close();
			return true;
		}catch(Exception ex){
			close();
			return false;
		}
	}
	
	/**
	 * adds service to table
	 * @param serviceId
	 * @param serviceName
	 * @param schedule
	 * @return
	 */
	public boolean addService(int serviceId, String serviceName, int schedule,int displayOrder){
		try{
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			cv.put(SERVICE_ID,serviceId);
			cv.put(SERVICE_NAME, serviceName);
			cv.put(SERVICE_SCHEDULE, schedule);
			cv.put(DISPLAY_ORDER, displayOrder);

			if(db.insertWithOnConflict(TABLE_NAME_FAMILY_PLANNING_SERVICES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)<=0){
				return false;
			}
			close();
			return true;
		}catch(Exception ex){
			Log.d("FamilyPlanningServices",ex.getMessage());
			close();
			return false;
		}
	}
	
	/**
	 * fetch a services information from current cursor and return it as object
	 * @return
	 */
	public FamilyPlanningService fetch(){
		try{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(SERVICE_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(SERVICE_NAME);
			String serviceName=cursor.getString(index);
			index=cursor.getColumnIndex(SERVICE_SCHEDULE);
			int schedule=0;
			if(index>=0){
				schedule=cursor.getInt(index);
			}
			int displayOrder=1000;
			index=cursor.getColumnIndex(DISPLAY_ORDER);
			if(index>=0){
				 displayOrder=cursor.getInt(index);
			}
			cursor.moveToNext();
			return new FamilyPlanningService(id,serviceName,schedule, displayOrder);
		}catch(Exception ex){
			close();
			return null;
		}
		
	}
	
	/**
	 * return all services in the table
	 * @return
	 */
	public ArrayList<FamilyPlanningService> getServices(){
		ArrayList<FamilyPlanningService> list=new ArrayList<FamilyPlanningService>();
		String[] columns={SERVICE_ID,SERVICE_NAME,SERVICE_SCHEDULE};
		try
		{
			db=getReadableDatabase();
			String order=DISPLAY_ORDER +", "+SERVICE_NAME;
			String where=DISPLAY_ORDER +">=0";
			cursor=db.query(TABLE_NAME_FAMILY_PLANNING_SERVICES, columns,where,null, null, null, order);
			cursor.moveToFirst();
			FamilyPlanningService v=fetch();
			while(v!=null){
				list.add(v);
				v=fetch();
			}
			close();
			return list;
		}catch(Exception ex){
			close();
			return list;
		}
	}
	
	/**
	 * returns a service identified by an id
	 * @param id
	 * @return
	 */
	public FamilyPlanningService getService(int id){
		
		String[] columns={SERVICE_ID,SERVICE_NAME,SERVICE_SCHEDULE};
		try
		{
			db=getReadableDatabase();
			String selector="SERVICE_ID="+id;
			cursor=db.query(TABLE_NAME_FAMILY_PLANNING_SERVICES, columns,selector,null, null, null, null);
			cursor.moveToFirst();
			FamilyPlanningService service=fetch();
			
			close();
			return service;
		}catch(Exception ex){
			close();
			return null;
		}
	}
	
	
	
}
