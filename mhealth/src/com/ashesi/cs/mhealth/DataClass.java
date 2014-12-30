package com.ashesi.cs.mhealth;
/**
 * NAmanquah: 
 * a few things to check up on: communityMember data  returned- is incomplete, see code below
 * Also appears existing code throws non fatal error bc local db is not closed explicitly.  <---corrected in Communities.java
 * verify the format of all dates returned.
 * OPDCaseRecords fetch generates a null exception in existing code.
 * 
 */
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

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.CHPSZones;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.FamilyPlanningRecords;
import com.ashesi.cs.mhealth.data.FamilyPlanningServices;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.OPDCaseCategories;
import com.ashesi.cs.mhealth.data.OPDCaseRecord;
import com.ashesi.cs.mhealth.data.OPDCaseRecords;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.VaccineRecord;
import com.ashesi.cs.mhealth.data.VaccineRecords;
import com.ashesi.cs.mhealth.data.Vaccines;
import com.ashesi.cs.mhealth.knowledge.AnswerLinks;
import com.ashesi.cs.mhealth.knowledge.Answers;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.LocalLinks;
import com.ashesi.cs.mhealth.knowledge.LogData;
import com.ashesi.cs.mhealth.knowledge.Questions;
import com.ashesi.cs.mhealth.knowledge.ResourceLinks;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

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
	 * DATABASE VERSION 3
	 * vaccine table is added
	 * vaccine record table is added
	 * DATABASE VERSION 4
	 * community member view and community member opd record view has been changed to compute age using julianday
	 * Database VERSION 6
	 * Questions, Answers, Categories, ResourceMaterials
	 * Datavase VERSION 7
	 * log, family planning, answerlink, local link
	 * Database VERSION 8
	 * update vaccine view to disaggregate  by gender
	 *  * Database VERSION 9
	 * update health promotion table is recreated
	 * version 10
	 * Add column family planning service for service schedule
	 * version 11
	 * creates question table in because in some users did not have questions table event though they had
	 * version 9 and 10 database
	 * version 12
	 * modifies VIEW_COMMUNITY_MEMBERS_OPD_CASES
	 * adds VIEW_COMMUNITY_MEMBERS_IN_OPD_CASES
	 */
	protected static final int DATABASE_VERSION=14; 
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected int mDeviceId;
	//protected String mServerUrl="http://cs2.ashesi.edu.gh/~www_developer/yaresa/";
	
	
	Context context;
	
	public  static final String DATABASE_NAME="mhealth";
	public static final String MHEALTH_SETTINGS="mhealth_settings";
	public static final String SERVER_URL="http://cs2.ashesi.edu.gh/~www_developer/yaresa/";
	public static final String APPLICATION_PATH="/yaresa/";
	protected  String mServerUrl=SERVER_URL;		//keeping the original type as protected
	public static final String MOBILE_APPLICATION_PATH="mhealth_android/mhealth_android.php"; //needs to be added to DataConnection.RECORD_URL
	public static final int CONNECTION_TIMEOUT=60000;
	public static final String BACKUP_FOLDER="";
		
	public static final String TABLE_NAME_DATAVERSION="dataversion";
	public static final String VERSION="version";
	public static final String DATANAME="dataname";
	
	public static final String REC_STATE="rec_state";
	public static final int REC_STATE_NEW=0;
	public static final int REC_STATE_DIRTY=1;
	public static final int REC_STATE_UPTODATE=2;
	public static final int REC_STATE_DELETED=3;
	private static final int DATAVERSION = 0;
	

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
			connection.setReadTimeout(CONNECTION_TIMEOUT);
			connection.connect();
			return connection;
		}catch(Exception ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return null;
		}
	}
	
	/**
	 * Read server stream until #SUCCESS# keyword is seen and return the string without keyword
	 */
	
	public String readStream(InputStream stream){
		try
		{
			char[] buffer=new char[1024];
			String data="";
			boolean stop=false;
			int readLength=0;
			Reader reader=new InputStreamReader(stream,"UTF-8");
			while(!stop){
				readLength=reader.read(buffer);
				if(readLength>0){
					data=data+(new String(buffer,0,readLength));
				}
					if(data.contains("#SUCCESS#")){
					stop=true;
				}
			}
			int end=data.lastIndexOf("#SUCCESS#");
		
			
			return data.substring(0, end);
		}catch(Exception ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
	}
	/**
	 * Using InputStream it reads data from connection 
	 * @param connection open connection
	 * @return
	 */
	public String request(HttpURLConnection connection){

		
		try
		{
			InputStream stream=connection.getInputStream();
			Log.d("DataClass.request","stream");
			return readStream(stream);
		}catch(Exception ex){
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
		
		System.out.println("Making a Post Request");
		Log.d("URL", urlAddress);

		try{
			URL url=new URL(urlAddress);
			connection=(HttpURLConnection)url.openConnection();
			if(connection==null){
				Log.d("DataClass.request", "connection did not open");
				return "{\"result\":0,\"message\":\"error connecting\"}";
			}
			
			
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.setReadTimeout(CONNECTION_TIMEOUT);
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
			
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("d", postData));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity=response.getEntity();
	        InputStream stream=entity.getContent();
	        return readStream(stream);
			
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
		//urlAddress=mServerUrl+urlAddress;  //a fully formed URL is passed. -commented by NA
		
		System.out.println("Making a Post Request");
		Log.d("URL", urlAddress);
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
		
		try{
			HttpEntity entity=response.getEntity();
	        InputStream stream=entity.getContent();
	        return readStream(stream);
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}


	}

	
	public String uploadFile(String urlAddress, File fileToUpload){
		urlAddress=mServerUrl+urlAddress;
		
		
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
	        return readStream(stream);			
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
			
			db.execSQL(CHPSZones.getCreateSQLString());
			setDataVersion(db,CHPSZones.TABLE_CHPS_ZONES,0);
			
			db.execSQL(CommunityMembers.getCreateSQLString());
			
			setDataVersion(db,CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS,0);
			db.execSQL(OPDCases.getCreateSQLString());
			
			db.execSQL(Communities.getInsertSQL(15, "Berekuso", 1));
			db.execSQL(Communities.getInsertSQL(1, "Yaw Duodu", 2));
			
			setDataVersion(db, OPDCases.TABLE_NAME_OPD_CASES,0);
			
						
			/*this cases are added just for testing 
			 * The complete OPD case list should be downloaded using synch
			 * */
			
			db.execSQL(OPDCases.getInsertSQLString(1, "AFP(Polio)",1));
			db.execSQL(OPDCases.getInsertSQLString(10, "U Malaria Lab",2));

			
			db.execSQL(Categories.getCreateSqlString());

			setDataVersion(db,Categories.TABLE_NAME_CATEGORIES,0);
			
			db.execSQL(OPDCaseRecords.getCreateSQLString());
			setDataVersion(db,OPDCaseRecords.TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES,0);
			
			db.execSQL(CHOs.getCreateQuery());
			db.execSQL(CHOs.getInsert(1, "Admin",1));
			
	
			setDataVersion(db,CHOs.TABLE_NAME_CHOS,0);
			
			
			
			//view for opd case records
			db.execSQL(OPDCaseRecords.getCreateViewString());
			//view for community members
			db.execSQL(CommunityMembers.getViewCreateSQLString());
			
			//added in version 3
			db.execSQL(Vaccines.getCreateSQLString());
			setDataVersion(db,Vaccines.TABLE_NAME_VACCINES,0);
			
			db.execSQL(Vaccines.getInsertSQLString(1, "BCG", 0));
			db.execSQL(Vaccines.getInsertSQLString(2, "Hepatitis B", 0));
			
			
			db.execSQL(VaccineRecords.getCreateSQLString());
			setDataVersion(db,VaccineRecords.TABLE_NAME_VACCINE_RECORDS,0);
			
			//view for vaccine records
			db.execSQL(VaccineRecords.getCreateViewSQLString());
			
			Log.d("DataClass.onCreate", "data base created");
			
			//add in version 4, it should not be included in upgraded
			//in version 4 some of the views were corrected, but there was no object
			
			//added in version 5
			db.execSQL(HealthPromotions.getCreateSQLString());
			setDataVersion(db,HealthPromotions.TABLE_NAME_HEALTH_PROMOTION,0);
			
			db.execSQL(Vaccines.getCreateViewPendingVaccinesSQLString());
			setDataVersion(db,DATABASE_NAME,5); 			//note down the data base version
			
			
			//added in version 6
			db.execSQL(Categories.getInsert("Nutrition"));
			db.execSQL(Categories.getInsert("Pharmaceuticals"));
			db.execSQL(Categories.getInsert("Administration"));
			db.execSQL(Categories.getInsert("Diagnosis and Treatment"));
			db.execSQL(Categories.getInsert("Childcare"));
			db.execSQL(Categories.getInsert("General"));
			
			/*Knowledge Section*/
			//Create the Question table	
			db.execSQL(Questions.getCreateQuery());				
			
			//Create resources materials
			db.execSQL(ResourceMaterials.getCreateQuery());
			
			//Create resource links
			db.execSQL(ResourceLinks.getCreateQuery());
			
			//Create answers table
			db.execSQL(Answers.getCreateQuery());
			
			//version 7 additions
			//Family Planning
			//In some of the version distributed family planning services
			//was added. but in other version it was not 
			db.execSQL(FamilyPlanningServices.getCreateSQLString());
			
			db.execSQL(FamilyPlanningServices.getInsertSQLString(1, "Service 1"));
			db.execSQL(FamilyPlanningServices.getInsertSQLString(2, "Service 2"));
			
			db.execSQL(FamilyPlanningRecords.getCreateSQLString());
			db.execSQL(FamilyPlanningRecords.getCreateViewSQLString());
			//end of family planning
			
			//Create Log table
			db.execSQL(LogData.getCreateQuery());
			
			//create answer links table
			db.execSQL(AnswerLinks.getCreateQuery());
			
			//create local links table
			db.execSQL(LocalLinks.getCreateQuery());
			
			//version 8
			//views are updated
			
			//version 9
			//updates views and tables no new addition
			//version 10
			//updates views and tables no new additions
			//version 11
			//creates questions table if it does not exist
			//version 12
			//modify opd cases by adding display order
			//modify view opd case records by adding community member
			//add category table
			db.execSQL(OPDCaseCategories.getCreateSQLString());
			
			setDataVersion(db,DATABASE_NAME,12);
			
			
		}catch(Exception ex){
			Log.e("DataClass.onCreate", "Exception "+ex.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try
		{
			if(oldVersion==1 ){
				//add lab column to table
				upgradeToVersion2(db);
			}
						
			if(oldVersion<=2){
				upgradeToVersion3(db);
			}
			
			if(oldVersion<=3 ){
				upgradeToVersion4(db);
			}
			
			if(oldVersion<=4 ){
				upgradeToVersion5(db);
			}
			if(oldVersion<=5 ){
				upgradeToVersion6(db);
			}
			
			if(oldVersion<=6){
				upgradeToVersion7(db);
			}
			
			if(oldVersion<=7){
				upgradeToVersion8(db);
			}
			
			if(oldVersion<=8){
				upgradeToVersion9(db);
			}
			
			if(oldVersion<=9){
				upgradeToVersion10(db,oldVersion);
			}
			
			if(oldVersion<=10){
				upgradeToVersion11(db,oldVersion);
			}
			
			if(oldVersion<=11){
				upgradeToVersion12(db,oldVersion);
			}

			if(oldVersion<=12){
				upgradeToVersion13(db,oldVersion);
			}
			
			if(oldVersion<=13){
				upgradeToVersion14(db);
			}
		}catch(Exception ex){
			Log.e("DataClass.onUpgrade", "Exception while upgrading to "+newVersion + " exception= "+ex.getMessage());
		}
		
	}
	/**
	 * upgrade the database from version 1 to 2
	 * @param db
	 */
	private void upgradeToVersion2(SQLiteDatabase db){
		//add lab colunm to table
		String sql="alter table "+ OPDCaseRecords.TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES
				+" add column "+OPDCaseRecords.LAB +" text default '"+OPDCaseRecords.LAB_NOT_CONFIRMED+ "'";
		db.execSQL(sql);
		//re create Record view
		db.execSQL("drop view "+OPDCaseRecords.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES);
		db.execSQL(OPDCaseRecords.getCreateViewString());
		setDataVersion(db,DATABASE_NAME,2); 			//note down the database version
	}
	
	/**
	 * upgrade the database from version 2 to 3
	 * @param db
	 */
	private void upgradeToVersion3(SQLiteDatabase db){
		//this is tables are not in DB 2
		db.execSQL(Vaccines.getCreateSQLString());
		setDataVersion(db,Vaccines.TABLE_NAME_VACCINES,0);
				
		db.execSQL(VaccineRecords.getCreateSQLString());
		setDataVersion(db,VaccineRecords.TABLE_NAME_VACCINE_RECORDS,0);
		
		db.execSQL(VaccineRecords.getCreateViewSQLString());
		
		setDataVersion(db,DATABASE_NAME,3); 			//note down the database version
	}
	
	/**
	 * upgrade the database from version 2 to 3
	 * @param db
	 */
	private void upgradeToVersion4(SQLiteDatabase db){
		//this is tables are not in DB 2
		
		db.execSQL("drop view "+OPDCaseRecords.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES);
		db.execSQL(OPDCaseRecords.getCreateViewString());
		
		//db.execSQL("drop view "+CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS);
		//db.execSQL(CommunityMembers.getViewCreateSQLString());	//create in the last update after all changes
		
		setDataVersion(db,DATABASE_NAME,4); 			//note down the database version
	}
	
	private void upgradeToVersion5(SQLiteDatabase db){
				
		db.execSQL(HealthPromotions.getCreateSQLString());
		setDataVersion(db,HealthPromotions.TABLE_NAME_HEALTH_PROMOTION,0);
		
		db.execSQL(Vaccines.getCreateViewPendingVaccinesSQLString());
		
		setDataVersion(db,DATABASE_NAME,5); 			//note down the database version
	}
	
	private void upgradeToVersion6(SQLiteDatabase db){
		//db.execSQL(Categories.getCreateSqlString());
		db.execSQL(Categories.getInsert("Nutrition"));
		db.execSQL(Categories.getInsert("Pharmaceuticals"));
		db.execSQL(Categories.getInsert("Administration"));
		db.execSQL(Categories.getInsert("Diagnosis and Treatement"));
		db.execSQL(Categories.getInsert("Childcare"));
		db.execSQL(Categories.getInsert("General"));
		
		/*Knowledge Section*/
		//Create the Question table	
		//db.execSQL("Drop table questions");
		db.execSQL(Questions.getCreateQuery());				
		
		//Create resources materials
		db.execSQL(ResourceMaterials.getCreateQuery());
		
		//Create resource links
		db.execSQL(ResourceLinks.getCreateQuery());
		
		//Create answers table
		db.execSQL(Answers.getCreateQuery());
		
		//add confirm birthdate column in community members table
		db.execSQL("alter table "+CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS +" add column "+CommunityMembers.IS_BIRTHDATE_CONFIRMED +" integer default "+CommunityMembers.BIRTHDATE_NOT_CONFIRMED);
		
		//recreate community member view with IS_BIRTHDATE_CONFIRM column
		//create in the last update after all changes
		//db.execSQL("drop view "+CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS);
		//db.execSQL(CommunityMembers.getViewCreateSQLString()); 
		setDataVersion(db,DATABASE_NAME,6);
	}
		
	private void upgradeToVersion7(SQLiteDatabase db){
		//Family Planning
		
		db.execSQL(FamilyPlanningServices.getCreateSQLString());
		
		db.execSQL(FamilyPlanningServices.getInsertSQLString(1, "Service 1",30,1));
		db.execSQL(FamilyPlanningServices.getInsertSQLString(2, "Service 2",30,2));
		
		db.execSQL(FamilyPlanningRecords.getCreateSQLString());
		//db.execSQL(FamilyPlanningRecords.getCreateViewSQLString());		//view should be created in the last upgrade that change it
		//Create Log table
		db.execSQL(LogData.getCreateQuery());
		
		//create answer links table
		db.execSQL(AnswerLinks.getCreateQuery());
		
		//create local links table
		db.execSQL(LocalLinks.getCreateQuery());
		setDataVersion(db,DATABASE_NAME,7); 			//note down the database version
	}
	
	private void upgradeToVersion8(SQLiteDatabase db){
		//updates the family planing and vaccine record views for querying based on gender
		db.execSQL("drop view if exists "+ VaccineRecords.VIEW_NAME_VACCINE_RECORDS_DETAIL);
		db.execSQL(VaccineRecords.getCreateViewSQLString());
		//db.execSQL("drop view "+ FamilyPlanningRecords.VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL);
		//view should be created in the last upgrade function
		//db.execSQL(FamilyPlanningRecords.getCreateViewSQLString()); 
		setDataVersion(db,DATABASE_NAME,8); 
	}
	
	private void upgradeToVersion9(SQLiteDatabase db){
		//recreate table
		db.execSQL("drop table "+ HealthPromotions.TABLE_NAME_HEALTH_PROMOTION);
		db.execSQL(HealthPromotions.getCreateSQLString());
		setDataVersion(db,DATABASE_NAME,9); 
	}
	
	private void upgradeToVersion10(SQLiteDatabase db,int oldVersion){
		if(oldVersion>=7){	//Family Planning tables are added on version seven. They have to be updgraded
			db.execSQL("alter table "+FamilyPlanningServices.TABLE_NAME_FAMILY_PLANNING_SERVICES +
					" add column " +FamilyPlanningServices.SERVICE_SCHEDULE +" integer default 0"
				);
		
			db.execSQL("alter table " + FamilyPlanningRecords.TABLE_NAME_FAMILY_PLANNING_RECORDS + 
					" add column " +FamilyPlanningRecords.SCHEDULE_DATE +" text ");
		}
		//view should be create in the last upgrade that changes it
		//db.execSQL("drop view "+FamilyPlanningRecords.VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL);	
		//db.execSQL(FamilyPlanningRecords.getCreateViewSQLString());
		setDataVersion(db,DATABASE_NAME,10); 	
	}
	
	private void upgradeToVersion11(SQLiteDatabase db,int oldVersion){
		db.execSQL(Questions.getCreateQuery());
		setDataVersion(db,DATABASE_NAME,11); 	
	}
	
	private void upgradeToVersion12(SQLiteDatabase db,int oldVersion){
		db.execSQL(" drop view if exists "+OPDCaseRecords.VIEW_NAME_COMMUNITY_MEMBER_OPD_CASES);	//modfied to include community name
		db.execSQL(OPDCaseRecords.getCreateViewString());//two views are created through this statement
		db.execSQL("alter table "+OPDCases.TABLE_NAME_OPD_CASES + " add column "+ OPDCases.OPD_CASE_DISPLAY_ORDER + " integer default 0");
		db.execSQL(OPDCaseCategories.getCreateSQLString());
		if(oldVersion>=7){
			db.execSQL("alter table "+FamilyPlanningRecords.TABLE_NAME_FAMILY_PLANNING_RECORDS+ 
						" add column "+FamilyPlanningRecords.SERVICE_TYPE+" integer default 0");
		}
		db.execSQL(" drop view if exists "+ FamilyPlanningRecords.VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL);
		db.execSQL(FamilyPlanningRecords.getCreateViewSQLString());
		setDataVersion(db,DATABASE_NAME,12); 	
	}
	
	private void upgradeToVersion13(SQLiteDatabase db,int oldVersion){
		if(oldVersion>=7){
			db.execSQL("alter table "+FamilyPlanningServices.TABLE_NAME_FAMILY_PLANNING_SERVICES+ 
				" add column "+FamilyPlanningServices.DISPLAY_ORDER+" integer default 1000");
		}
		db.execSQL(CHPSZones.getCreateSQLString());
		//communitis were organized under sub districts but this has to change to zones
				//to enable proper join query the subdistrict_id had to be replaced by chps_zone_id
				//option 1, add new chps_zone id table and forget about subdistrict_id
				db.execSQL("alter table "+Communities.TABLE_COMMUNITIES+ 
						" add column "+CHPSZones.CHPS_ZONE_ID+" integer default 0");
				//Alternate option is to drop the table and recreate it
				//since communities is a support data it can be re populated from supportdata file
				//db.execSQL("drop table "+Communities.TABLE_COMMUNITIES);
				//db.execSQL(Communities.getCreateTable());
		setDataVersion(db,DATABASE_NAME,13); 
	}
	
	private void upgradeToVersion14(SQLiteDatabase db){
		db.execSQL("alter table "+CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+
						" add column "+CommunityMembers.FIRST_ACCESS_DATE+" text default '1900-01-01'");
		db.execSQL("drop view if exists "+CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS);
		db.execSQL(CommunityMembers.getViewCreateSQLString());
		setDataVersion(db,DATABASE_NAME,14); 
	}
		
	public String getDataFilePath(){
		db=this.getReadableDatabase();
		String str=db.getPath();
		close();
		return str;
	}
	
	public static String getApplicationFolderPath(){
		return Environment.getExternalStorageDirectory().getPath() +DataClass.APPLICATION_PATH;
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
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	

/**
 * Synchronize data with server using data returned from other child classes.
 * @param theMainActivity A reference to the main activity, to provide context for this method.
 * @author namanquah
 * moved to Synch Activity
 */
	

	
	
}
