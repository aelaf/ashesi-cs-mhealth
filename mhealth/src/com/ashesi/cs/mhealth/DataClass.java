package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.OPDCaseRecords;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.VaccineRecords;
import com.ashesi.cs.mhealth.data.Vaccines;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Questions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataClass extends SQLiteOpenHelper {
	/*
	 * DATABASE VERSION 2
	 * adds LAB text column to community_member_opd_cases table
	 * modifies view_community_member_opd_cases to include LAB column
	 */
	protected static final int DATABASE_VERSION=2; 
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected int mDeviceId;
	protected String mServerUrl="http://192.168.56.2/mHealth/";
	
	Context context;
	
	public  static final String DATABASE_NAME="mhealth";
	public static final String MHEALTH_SETTINGS="mhealth_settings";
	public static final String SERVER_URL="http://192.168.56.2/mHealth/";
	public static final int CONNECTION_TIMEOUT=60000;
	public static final String BACKUP_FOLDER="";
		
	public static final String TABLE_NAME_DATAVERSION="dataversion";
	public static final String VERSION="version";
	public static final String DATANAME="dataname";
	
	
	public static final String VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES="view_community_member_opd_cases";
	public static final String NO_CASES="no_cases";
	
	public static final String REC_STATE="rec_state";
	public static final int REC_STATE_NEW=0;
	public static final int REC_STATE_DIRTY=1;
	public static final int REC_STATE_UPTODATE=2;
	public static final int REC_STATE_DELETED=3;
	private static final int DATAVIRSION = 0;
	

	/**
	 * Creates an object of DataClass and calls getWritableDatabase to force database creation if necessary
	 * @param context
	 */
	public DataClass(Context context){
		
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
		db=getWritableDatabase();
		db.close();
		this.context=context;
		getDeviceId();
		getServerUrl();

		
	}
	
	public HttpURLConnection connect(){
		return null;
	}
	
	public boolean processDownloadData(String data){
		return false;
	}
	
	public HttpURLConnection connect(String urlAddress){
		urlAddress=mServerUrl+urlAddress;
		
		
		HttpURLConnection connection;
				
		try{
			URL url=new URL(urlAddress);
			connection=(HttpURLConnection)url.openConnection();
			if(connection==null){
				Log.d("DataClass.request", "connection did not open");
				return null;
			}
			
			Log.d("DataClass.request", "connection open, getting stream");
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.connect();
			return connection;
		}catch(Exception ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return null;
		}
	}
	
	/**
	 * Using InputStream it reads data from connection 
	 * @param connection open connection
	 * @return
	 */
	public String request(HttpURLConnection connection){

		
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			
		
			InputStream stream=connection.getInputStream();
			Log.d("DataClass.request","stream");
			Reader reader=new InputStreamReader(stream,"UTF-8");
			
			int readLength=1024;
			while(readLength==1024){
				readLength=reader.read(buffer);
				data=data+(new String(buffer));
			}

			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
	}
	
	/**
	 * Opens HTTP GET connection and reads data
	 * @param urlAddress
	 * @return
	 */
	public String request(String urlAddress){
		
		urlAddress=mServerUrl+urlAddress;
		
		HttpURLConnection connection;
		String data="";
		
		try{
			URL url=new URL(urlAddress);
			connection=(HttpURLConnection)url.openConnection();
			if(connection==null){
				Log.d("DataClass.request", "connection did not open");
				return "{\"result\":0,\"message\":\"error connecting\"}";
			}
			
			
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.connect();
		
			data=request(connection);
			
			connection.disconnect();
			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
		
	}
	
	/**
	 * makes a POST request
	 * @param urlAddress appended to server address from setting
	 * @param postData
	 * @return
	 */
	public String request(String urlAddress,String postData){

		urlAddress=mServerUrl+urlAddress;
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("d", postData));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity=response.getEntity();
	        InputStream stream=entity.getContent();
	        Reader reader=new InputStreamReader(stream,"UTF-8");
	        int readLength=1024;
	        while(readLength==1024){
	     		readLength=reader.read(buffer);
	     		data=data+(new String(buffer));
	        }
			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
		
	}

	/**
	 * makes a post request and returns a response
	 * @param urlAddress
	 * @param nameValuePairs
	 * @return
	 */
	public  HttpResponse postRequest(String urlAddress, List<NameValuePair> nameValuePairs){
		urlAddress=mServerUrl+urlAddress;
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);

	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        return response;
			
		}
		catch(IOException ex){
			Log.d("DataClass.postRequest","Exception" + ex.getMessage());
			return null;
		}

	}
	
	/**
	 * gets the response data from HTTP response
	 * @param response
	 * @return
	 */
	public  String request(HttpResponse response){
		
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			HttpEntity entity=response.getEntity();
	        InputStream stream=entity.getContent();
	        Reader reader=new InputStreamReader(stream,"UTF-8");
	        int readLength=1024;
	        while(readLength==1024){
	     		readLength=reader.read(buffer);
	     		data=data+(new String(buffer));
	        }
			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}


	}

	
	public String uploadFile(String urlAddress, File fileToUpload){
		urlAddress=mServerUrl+urlAddress;
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);
			
			MultipartEntityBuilder builder=MultipartEntityBuilder.create();
			builder.setBoundary("****");
			builder.addPart("userfile",new FileBody(fileToUpload));
			//builder.addBinaryBody("userfile", fileToUpload);
	       
	        httppost.setEntity(builder.build());
	      
	        HttpResponse response = httpclient.execute(httppost);
	        InputStream stream=response.getEntity().getContent();
	        Reader reader=new InputStreamReader(stream,"UTF-8");
	        int readLength=1024;
	        while(readLength==1024){
	     		readLength=reader.read(buffer);
	     		data=data+(new String(buffer));
	        }
			return data;
			
		}
		catch(Exception ex){
			Log.d("DataClass.uploadFile","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
	}
	
	/**
	 * get the version of the local data   
	 * @param dataName: the name of the table in the database
	 * @return the version of the named data
	 */
	public int getDataVersion(String dataName){
		try{
			db=getReadableDatabase();
			String[] columns={VERSION};
			Cursor cursor=db.query(TABLE_NAME_DATAVERSION, columns, DATANAME +"=" +VERSION, null, null, null, null);
			if(cursor.getCount()==0){
				return 0;
			}
			
			int dataversion=cursor.getInt(0);
			cursor.close();
			db.close();
			return dataversion;
		}catch(Exception ex){
			Log.e("DataClass.getDataVersion(string)","Exception while quering for "+dataName+" " +ex.getMessage());
			return 0;
		}
	}
	
	/**
	 * it checks the if the local data needs synch
	 * @return true if data needs synch 
	 */
	public boolean needsSynch(){
		int d=getDataVersion(TABLE_NAME_DATAVERSION);
		if(d==0){
			return true;
		}
		return false;
	}
	
	/**
	 * get the version of the local data
	 * @return
	 */
	public int getDataVersion(SQLiteDatabase db, String dataName){
		
		String[] columns={VERSION};
		Cursor cursor=db.query(TABLE_NAME_DATAVERSION, columns, DATANAME +"=" +VERSION, null, null, null, null);
		if(cursor.getColumnCount()==0){
			return 0;
		}
		
		int dataversion=cursor.getInt(0);
		cursor.close();
		
		return dataversion;
	}
	
	/**
	 * get the version of the table or other data item
	 * @param db
	 * @param dataName table name or other data name
	 * @param version
	 * @return
	 */
	protected boolean setDataVersion(SQLiteDatabase db,String dataName,int version){
		ContentValues cv=new ContentValues();
		cv.put(DATANAME, dataName);
		cv.put(VERSION, version);
		db.insert(TABLE_NAME_DATAVERSION, null,cv);
		return true;
	}
	
	/**
	 * get the version of the table or other data item
	 * @param dataName table name or other data name
	 * @param version
	 * @return
	 */
	public boolean setDataVersion(String dataName,int version){
		try
		{
			db=getWritableDatabase();
		
			ContentValues cv=new ContentValues();
			cv.put(DATANAME, dataName);
			cv.put(VERSION, version);
			db.insert(TABLE_NAME_DATAVERSION, null,cv);
			close();
			return true;
		}
		catch(Exception ex){
			return false;
		}
		
	}
	
	/**
	 * closes the cursor and db if they are not null and if they are open
	 */
	public void close(){
		if(cursor!=null){
			if(!cursor.isClosed()){
				cursor.close();
			}
		}
		if(db!=null){
			if(db.isOpen()){
				db.close();
			}
		}
	}

	/**
	 * forces the recreation of database
	 */
	public void resetDataBase(){
		db=getWritableDatabase();
		onCreate(db);
		db.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try
		{
			
			db.execSQL("create table "+ TABLE_NAME_DATAVERSION +" ("
					+VERSION+ " integer primary key, "
					+DATANAME+" text "
					+" )"
					);
			
			setDataVersion(db,TABLE_NAME_DATAVERSION,0);
			
			db.execSQL(Communities.getCreateTable());
			setDataVersion(db,Communities.TABLE_COMMUNITIES,0);
			
			db.execSQL(CommunityMembers.getCreateSQLString());
			
			setDataVersion(db,CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS,0);
			db.execSQL(OPDCases.getCreateSQLString());
			
			setDataVersion(db, OPDCases.TABLE_NAME_OPD_CASES,0);
			/*this cases are added just for testing 
			 * The complete OPD case list should be downloaded using synch
			 * */
			
			db.execSQL(OPDCases.getInsertSQLString(1, "AFP(Polio)",1));
			db.execSQL(OPDCases.getInsertSQLString(10, "U Malaria Lab",2));
			db.execSQL(OPDCases.getInsertSQLString(11, "U Malaria",2));
			db.execSQL(OPDCases.getInsertSQLString(31, "Malnutrition",3));
			db.execSQL(OPDCases.getInsertSQLString(34, "Hypertension",3));
			
			db.execSQL(Categories.getCreateSqlString());

			setDataVersion(db,Categories.TABLE_NAME_CATEGORIES,0);
			
			db.execSQL(OPDCaseRecords.getCreateSQLString());
			setDataVersion(db,OPDCaseRecords.TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES,0);
			
			db.execSQL(CHOs.getCreateQuery());
			db.execSQL(CHOs.getInsert(1, "Eric",1));
			db.execSQL(CHOs.getInsert(2,"Salome",2));
			
			setDataVersion(db,CHOs.TABLE_NAME_CHOS,0);
			
			db.execSQL(Vaccines.getCreateSQLString());
			setDataVersion(db,Vaccines.TABLE_NAME_VACCINES,0);
			
			db.execSQL(Vaccines.getInsertSQLString(1, "BCG", 0));
			db.execSQL(Vaccines.getInsertSQLString(2, "Hepatitis B", 0));
			db.execSQL(Vaccines.getInsertSQLString(3, "OPV-0", 0));
			db.execSQL(Vaccines.getInsertSQLString(4, "OPV-1", 70));
			
			db.execSQL(VaccineRecords.getCreateSQLString());
			setDataVersion(db,VaccineRecords.TABLE_NAME_VACCINE_RECORD,0);
			
			
			Log.d("DataClass.onCreate", "data base created");
			
			//view for opd case records
			db.execSQL(OPDCaseRecords.getCreateViewString());
			//view for community members
			db.execSQL(CommunityMembers.getViewCreateSQLString());
			
			//Create the knowledge - Question table	
			db.execSQL(Questions.getCreateQuery());				
			//Create categories
			db.execSQL(Categories.getCreateSqlString());
			

		}catch(Exception ex){
			Log.e("DataClass.onCreate", "Exception "+ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try
		{
			if(oldVersion==1 && newVersion==2){
				//add lab colunm to table
				String sql="alter table "+ OPDCaseRecords.TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES
						+" add column "+OPDCaseRecords.LAB +" text default '"+OPDCaseRecords.LAB_NOT_CONFIRMED+ "'";
				db.execSQL(sql);
				//re create Record view
				db.execSQL("drop view "+OPDCaseRecords.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES);
				db.execSQL(OPDCaseRecords.getCreateViewString());					
			}
		}catch(Exception ex){
			Log.e("DataClass.onUpgrade", "Exception while upgrading to "+newVersion + " exception= "+ex.getMessage());
		}
		
	}
	
	public String getDataFilePath(){
		db=this.getReadableDatabase();
		String str=db.getPath();
		close();
		return str;
	}
	
	public String getServerUrl(){
		try{
			mServerUrl=PreferenceManager.getDefaultSharedPreferences(context).getString("synch_url", "");
			
		}catch(Exception ex){
			mServerUrl=SERVER_URL;
		}
		return mServerUrl;
	}
	
	public int getDeviceId(){
		try{
			String str=PreferenceManager.getDefaultSharedPreferences(context).getString("device_id", "0");
			mDeviceId=Integer.parseInt(str);
		}catch(Exception ex){
			mDeviceId=0;
		}
		return mDeviceId;
		
	}
	
	
	
}
