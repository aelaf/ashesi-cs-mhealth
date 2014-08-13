package com.ashesi.cs.mhealth.data;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class FamilyPlanningServices extends DataClass {
	public final static String SERVICE_ID="service_id";
	public final static String SERVICE_NAME="service_name";
	public final static int	NEW_ACCEPTOR_SERVICE_ID=17;

	public final static String TABLE_NAME_FAMILY_PLANNING_SERVICES="family_planning_services";
	
	public FamilyPlanningServices(Context context){
		super(context);
	}
	
	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_FAMILY_PLANNING_SERVICES +" ( "
				+SERVICE_ID + "  integer primary key , "
				+SERVICE_NAME+ " text "
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

				addService(serviceId,serviceName);
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
	 * @param serviceSchedule
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
			cursor.moveToNext();
			return new FamilyPlanningService(id,serviceName);
		}catch(Exception ex){
			close();
			return null;
		}
		
	}
	
	/**
	 * return all services in the table
	 * @return
	 */
	public ArrayList<FamilyPlanningService> geServices(){
		ArrayList<FamilyPlanningService> list=new ArrayList<FamilyPlanningService>();
		String[] columns={SERVICE_ID,SERVICE_NAME};
		try
		{
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_FAMILY_PLANNING_SERVICES, columns,null,null, null, null, null);
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
	
	
	
	
	
	
}
