package com.ashesi.cs.mhealth.data;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.DataClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;




public class CommunityMembers extends DataClass {
	public static final String TABLE_NAME_COMMUNITY_MEMBERS="comunity_members";
	public static final String COMMUNITY_MEMBER_ID="community_member_id";
	public static final String COMMUNITY_ID=Communities.COMMUNITY_ID;
	public static final String COMMUNITY_MEMBER_NAME="community_member_name";
	public static final String AGE="age";
	public static final String BIRTHDATE="birthdate";
	public static final String GENDER="gender";
	public static final String CARD_NO="card_no";
	public static final String SERIAL_NO="serial_no";
    public static final String NHIS_ID="nhis_id";
    public static final String NHIS_EXPIRY_DATE="nhis_expiry_date";
    
    public static final int PAGE_SIZE=10;
	
	public static final String VIEW_NAME_COMMUNITY_MEMBERS="view_community_members";
	
	String[] columns={COMMUNITY_MEMBER_ID,COMMUNITY_ID,COMMUNITY_MEMBER_NAME,BIRTHDATE,GENDER,CARD_NO,REC_STATE,NHIS_ID,NHIS_EXPIRY_DATE};
	
	
	public CommunityMembers(Context context){
		super(context);
	}

	public boolean getAllCommunityMember(int communityId){
		try{
			String selector=null;
			if(communityId!=0){
				selector=COMMUNITY_ID+"="+communityId;
			}
			String[] columns={COMMUNITY_MEMBER_ID,COMMUNITY_ID,Communities.COMMUNITY_NAME,COMMUNITY_MEMBER_NAME,BIRTHDATE,GENDER,CARD_NO,REC_STATE,NHIS_ID,NHIS_EXPIRY_DATE};
			db=getReadableDatabase();
			cursor=db.query(VIEW_NAME_COMMUNITY_MEMBERS, columns, selector,null, null, null, null);
			
			return true;
		}catch(Exception ex){
			Log.e("CommunityMembers.getAllCommunityMember(int)","Exception "+ex.getMessage());
			close();
			return false;
		}
	}
	
	public int getNextId(){
		try
		{
			db=getReadableDatabase();
			String [] columns={"MAX(" +SERIAL_NO+")"};
			cursor=db.query(TABLE_NAME_COMMUNITY_MEMBERS,columns, null, null, null, null, null);
			if(cursor.getCount()<=0){
				close();
				return 1;
			}
		
			cursor.moveToFirst();
			int id=cursor.getInt(0);
			close();
			return id+1;
		}catch(Exception ex){
			close();
			return 0;
		}
	}

	public int addCommunityMember(int id, int community_id, String communityMemberName,Date birthdate,String gender,String cardNo,String nhisId,Date nhisExpiryDate){
		try
		{
			
			
			if(id==0){
				id=getNextId();
			}
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			
			cv.put(COMMUNITY_MEMBER_ID, id);
			cv.put(SERIAL_NO, id);
			cv.put(COMMUNITY_ID, community_id);
			cv.put(COMMUNITY_MEMBER_NAME, communityMemberName);
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-d",Locale.UK);
			cv.put(BIRTHDATE, dateFormat.format(birthdate));
			cv.put(GENDER,gender);
			cv.put(CARD_NO,cardNo);
			cv.put(NHIS_ID,nhisId);
			cv.put(NHIS_EXPIRY_DATE,dateFormat.format(nhisExpiryDate));
			cv.put(DataClass.REC_STATE,DataClass.REC_STATE_NEW);
			if(db.insertWithOnConflict(TABLE_NAME_COMMUNITY_MEMBERS, null, cv, SQLiteDatabase.CONFLICT_FAIL)<=0){
				close();
				return 0;
			}
			close();
			
			return id;
		}catch(Exception ex){
			Log.e("CommunityMembers.addCommunityMember", "Exception "+ex.getMessage());
			return 0;
		}
	}
	
	/**
	 * updates the record changing REC_STATE to DIRTY state
	 * @param id
	 * @param community_id
	 * @param communityMemberName
	 * @param birthdate
	 * @param gender
	 * @param cardNo
	 * @return
	 */
	public int updateCommunityMember(int id, int community_id, String communityMemberName,Date birthdate,String gender,String cardNo,String nhisId,Date nhisExpiryDate){
		try
		{
			CommunityMember cm=getCommunityMember(id);
			int currentState=cm.getRecState();
			
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			
			
			cv.put(COMMUNITY_ID, community_id);
			cv.put(COMMUNITY_MEMBER_NAME, communityMemberName);
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-d",Locale.UK);
			cv.put(BIRTHDATE, dateFormat.format(birthdate));
			cv.put(GENDER,gender);
			cv.put(CARD_NO,cardNo);
			cv.put(NHIS_ID,nhisId);
			cv.put(NHIS_EXPIRY_DATE,dateFormat.format(nhisExpiryDate));
			if(currentState!=DataClass.REC_STATE_NEW){	//if the record is new, leave it as new 
				cv.put(DataClass.REC_STATE,DataClass.REC_STATE_DIRTY);
			}
			String whereClause=COMMUNITY_MEMBER_ID+"="+id;
			
			
			if(db.update(TABLE_NAME_COMMUNITY_MEMBERS, cv,whereClause,null)<=0){
				close();
				return 0;
			}
			close();
			
			return id;
		}catch(Exception ex){
			Log.e("CommunityMembers.addCommunityMember", "Exception "+ex.getMessage());
			return 0;
		}
	}
	
	public boolean updateNHISRecord(int id,String nhisID, Date nhisExpiryDate){
		try
		{
			CommunityMember cm=getCommunityMember(id);
			int currentState=cm.getRecState();
			
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-d",Locale.UK);
			
			cv.put(NHIS_ID,nhisID);
			cv.put(NHIS_EXPIRY_DATE,dateFormat.format(nhisExpiryDate));
			if(currentState!=DataClass.REC_STATE_NEW){	//if the record is new, leave it as new 
				cv.put(DataClass.REC_STATE,DataClass.REC_STATE_DIRTY);
			}
			String whereClause=COMMUNITY_MEMBER_ID+"="+id;
			
			
			if(db.update(TABLE_NAME_COMMUNITY_MEMBERS, cv,whereClause,null)<=0){
				close();
				return false;
			}
			close();
			
			return true;
		}catch(Exception ex){
			Log.e("CommunityMembers.udateNHISRecord", "Exception "+ex.getMessage());
			return false;
		}
	}
	
	/**
	 * finds a community member in the selected community
	 * @param communityID if 0, it searches in all communities
	 * @param communityMemberName like operator is used to search
	 * @return true if successful
	 */
	public ArrayList<CommunityMember> findCommunityMember(int communityID,String communityMemberName){
		ArrayList<CommunityMember> list=new ArrayList<CommunityMember>();
		try{
			String[] columns={COMMUNITY_MEMBER_ID,COMMUNITY_ID,Communities.COMMUNITY_NAME,COMMUNITY_MEMBER_NAME,BIRTHDATE,GENDER,CARD_NO,REC_STATE,NHIS_ID,NHIS_EXPIRY_DATE};
			String selector=COMMUNITY_MEMBER_NAME +" LIKE '%"+ communityMemberName +"%' ";
			if(communityID!=0){
				selector+= " AND "+ COMMUNITY_ID+"="+communityID;
			}
			
			db=getReadableDatabase();
			cursor=db.query(CommunityMembers.VIEW_NAME_COMMUNITY_MEMBERS, columns,selector,null, null, null, null);
			cursor.moveToFirst();
			CommunityMember c=fetch();
			while(c!=null){
				list.add(c);
				c=fetch();
			}
			close();
			return list;
			
		}catch(Exception ex){
			Log.e("CommunityMembers.findCommunityMember(int)","Exception "+ex.getMessage());
			close();
			return list;
		}
	}
	
	public int uploadDataSize(){
		try{
			String strQuery= "select count(*) as no_rec from " 
							+TABLE_NAME_COMMUNITY_MEMBERS 
							+" where "
							+DataClass.REC_STATE +"=" +DataClass.REC_STATE_NEW +" or "+DataClass.REC_STATE +"=" +DataClass.REC_STATE_DIRTY;
			db=getReadableDatabase();
			cursor=db.rawQuery(strQuery,null);
			cursor.moveToFirst();
			return cursor.getInt(0);
			
		}catch(Exception ex){
			return -1;
		}
	}
	
	public String getForUpload(){
		try{
			String selector= DataClass.REC_STATE +"=" +DataClass.REC_STATE_NEW +" or "+DataClass.REC_STATE +"=" +DataClass.REC_STATE_DIRTY;
			db=getReadableDatabase();
			cursor=db.query(TABLE_NAME_COMMUNITY_MEMBERS, columns,selector,null, null, null, null,"10");
			if(cursor.getCount()<=0){
				return "";
			}
			CommunityMember obj=fetch();
			String str="[";
			while(obj!=null){
				str+=obj.getJSON();
				obj=fetch();
				if(obj!=null){
					str+=",";
				}
			}
			str+="]";
			close();
			return str;
		}catch(Exception ex){
			Log.e("CommunityMembers.findCommunityMember(int)","Exception "+ex.getMessage());
			close();
			return "";
		}
	}
	
	public void threadedUpload(){
	
		new Thread(new Runnable() {
	        public void run() {
	        	//upload 10 at a time
	        	uploadNewCommunityMembers();
	        }
	    }).start();
	}
	
	public String upload(String postData){
		return request("communityMemberActions.php?cmd=2&deviceId="+mDeviceId, postData);
	}
	
	public void uploadNewCommunityMembers(){
		
		String postData=getForUpload();
    	while(postData.length()>0){
    		String data=request("communityMemberActions.php?cmd=2&deviceId="+mDeviceId, postData);
    		processUploadData(data);
    		postData=getForUpload();
    	}
	}
	
	public boolean processUploadData(String data){
		try{
			JSONObject obj=new JSONObject(data);
			if(obj.getInt("result")==0){
				return false;
			}
			JSONArray jsonArray=obj.getJSONArray("ids");
			return updateRecordsAfterUpload(jsonArray);
		}catch(Exception ex){
			return false;
		}
	}
	
	private boolean updateRecordsAfterUpload(JSONArray jsonArray){
		try
		{
			int localId=0;
			int serverId=0;
			db=getWritableDatabase();
			for(int i=0;i<jsonArray.length();i++){
				JSONObject obj=jsonArray.getJSONObject(i);
				localId=obj.getInt("lid");
				serverId=obj.getInt("sid");
				if(serverId!=0){//if server Id == 0 then server has not successfully updated
					ContentValues values=new ContentValues();
					
					String whereClause=COMMUNITY_MEMBER_ID+"="+localId;
					if(localId!=serverId){//if new id is issued, update the id
						values.put(COMMUNITY_MEMBER_ID, serverId);
					}
					values.put(DataClass.REC_STATE, DataClass.REC_STATE_UPTODATE);
					db.update(TABLE_NAME_COMMUNITY_MEMBERS, values, whereClause, null);
					if(localId!=serverId){	//if the id changes, update all other records of community member
						values=new ContentValues();
						values.put(COMMUNITY_MEMBER_ID, serverId);
						db.update(OPDCaseRecords.TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES, values, whereClause, null);
					}
				}
			}
			
		
			close();
			return true;
			
		}catch(Exception ex){
			return false;
		}
	}
	
	public CommunityMember getCommunityMember(int id){
		try{
			String[] columns={COMMUNITY_MEMBER_ID,COMMUNITY_ID,Communities.COMMUNITY_NAME,COMMUNITY_MEMBER_NAME,BIRTHDATE,GENDER,CARD_NO,REC_STATE,NHIS_ID,NHIS_EXPIRY_DATE};
			String selector= COMMUNITY_MEMBER_ID+"="+id; 
			db=getReadableDatabase();
			cursor=db.query(VIEW_NAME_COMMUNITY_MEMBERS, columns,selector,null, null, null, null);
			if(cursor.getCount()<=0){
				close();
				return null;
			}
			
			CommunityMember cm=fetch();
			close();
			return cm;
		}catch(Exception ex){
			Log.e("CommunityMembers.findCommunityMember(int)","Exception "+ex.getMessage());
			close();
			return null;
		}
	}
	
	public CommunityMember fetch(){
		
		
		try
		{
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(COMMUNITY_MEMBER_ID);
			int id=cursor.getInt(index);
			
			index=cursor.getColumnIndex(COMMUNITY_ID);
			int communityID=cursor.getInt(index);
			
			index=cursor.getColumnIndex(COMMUNITY_MEMBER_NAME);
			String name=cursor.getString(index);
			
			index=cursor.getColumnIndex(BIRTHDATE);
			String birthdate=cursor.getString(index);
			
			index=cursor.getColumnIndex(GENDER);
			String gender=cursor.getString(index);
			
			index=cursor.getColumnIndex(CARD_NO);
			String cardNo=cursor.getString(index);
			
			index=cursor.getColumnIndex(REC_STATE);
			int recState=cursor.getInt(index);
			
			index=cursor.getColumnIndex(NHIS_ID);
			String nhisId=cursor.getString(index);
			
			index=cursor.getColumnIndex(NHIS_EXPIRY_DATE);
			String nhisExpiryDate=cursor.getString(index);
			
			index=cursor.getColumnIndex(Communities.COMMUNITY_NAME);
			String communityName="";
			if(index>=0){
				communityName=cursor.getString(index);
			}
			
			
			CommunityMember c=new CommunityMember(id,communityID,name,birthdate,gender,cardNo,recState,communityName,nhisId,nhisExpiryDate);
			cursor.moveToNext();
			return c;
		}
		catch(Exception ex){
			return null;
		}
	}
	
	/**
   	 * returns community member records in cursor as ArrayList
	 * @return
	 */
	public ArrayList<CommunityMember> getArrayList(){
		
		ArrayList<CommunityMember> list=new ArrayList<CommunityMember>();
		CommunityMember c=fetch();
		while(c!=null){
			list.add(c);
			c=fetch();
		}
		close();
		return list;
	}
	/**
	 * gets a set of records limited by PAGE SIZE for display. Cursor should be open  
	 * @param page 0 index page number
	 * @return
	 */
	public ArrayList<CommunityMember> getArrayList(int page){
		ArrayList<CommunityMember> list=new ArrayList<CommunityMember>();
		
		try{
			int position=page*PAGE_SIZE;
			if(position>=cursor.getCount()){
				position=0;
			}
			cursor.moveToPosition(position);
			
			CommunityMember c=fetch();
			int index=0;
			while(c!=null && index<PAGE_SIZE){
				list.add(c);
				c=fetch();
				index++;
			}
			
			return list;
		}catch(Exception ex){
			return list;
		}
	}
	/**
	 * get list of community members in cursor as string array of full name 
	 * @return
	 */
	public ArrayList<String> getStringArray(){
		
		ArrayList<String> list=new ArrayList<String>();
		CommunityMember c=fetch();
		while(c!=null){
			list.add(c.getFullname());
			c=fetch();
		}
		close();
		return list;
	}
	
	/**
	 * Records OPD case for a community member
	 * @param communityMemberId community member id from table
	 * @param opdCaseId OPD case id form table
	 * @param date date the information was recored
	 * @param choId the CHO recording it
	 * @return
	 */
	public boolean recordOPDCase(int communityMemberId, int opdCaseId, String date, int choId){
		try
		{
			db=getWritableDatabase();
			ContentValues values=new ContentValues();
			values.put(COMMUNITY_MEMBER_ID, communityMemberId);
			values.put(OPDCases.OPD_CASE_ID, opdCaseId);
			values.put(CHOs.CHO_ID,choId);
			values.put(OPDCaseRecords.REC_DATE, date);
			values.put(OPDCaseRecords.SERVER_REC_NO, 0);
			values.put(DataClass.REC_STATE, DataClass.REC_STATE_NEW);
			
			if(db.insert(OPDCaseRecords.TABLE_NAME_COMMUNITY_MEMBER_OPD_CASES, null, values)<=0){
				return false;
			}
			return true;
		}catch(Exception ex){
			Log.e("CommunityMembers.recordOPDCase", "Exception ex" +ex.getMessage());
			return false;
		}
		
		
	}

	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_COMMUNITY_MEMBERS +"(" 
				+COMMUNITY_MEMBER_ID + " integer primary key,"
				+SERIAL_NO+" integer, "
				+COMMUNITY_ID +" integer,"
				+COMMUNITY_MEMBER_NAME +" text, "
				+BIRTHDATE +" text, "
				+GENDER +" text, "
				+CARD_NO +" text, "
				+NHIS_ID+" text, "
				+NHIS_EXPIRY_DATE+" text, "
				+DataClass.REC_STATE+" integer "
				+" )";
		
	
	}
	
	public static String getViewCreateSQLString(){
		return "create view "+VIEW_NAME_COMMUNITY_MEMBERS+ " as select "
				+CommunityMembers.COMMUNITY_MEMBER_ID+ ", "
				+CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_ID +", "
				+CommunityMembers.COMMUNITY_MEMBER_NAME+", "
				+CommunityMembers.GENDER+", "
				+CommunityMembers.BIRTHDATE+", "
				+CommunityMembers.CARD_NO+", "
				+NHIS_ID+", "
				+NHIS_EXPIRY_DATE+", "
				+CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+DataClass.REC_STATE+", "
				+" (date()-"+CommunityMembers.BIRTHDATE+") as "+CommunityMembers.AGE+", "
				+Communities.COMMUNITY_NAME
				+" from "+ CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS
				+ " left join "+ Communities.TABLE_COMMUNITIES
				+ " on " +CommunityMembers.TABLE_NAME_COMMUNITY_MEMBERS+"."+CommunityMembers.COMMUNITY_ID+"="+Communities.TABLE_COMMUNITIES+"."+Communities.COMMUNITY_ID;
	}
}
