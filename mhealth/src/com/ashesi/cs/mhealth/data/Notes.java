package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.text.DateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import android.text.format.DateFormat;
import android.util.Log;

import com.ashesi.cs.mhealth.DataClass;

//import android.content.Context;

public class Notes extends DataClass {
	public static final String TABLE_NAME_NOTES = "notes"; 
	public static final String NOTE_ID = "note_id";
	public static final String NOTE = "note";
	public static final String DATE_TAKEN = "date_taken";
	public static final String COMMUNITY_ID = Communities.COMMUNITY_ID;
	public static final String CHO_ID = CHOs.CHO_ID;
	
	public static final String VIEW_NAME_NOTES = "view_notes";
	
	String[] columns = {NOTE_ID,NOTE,DATE_TAKEN,COMMUNITY_ID,CHO_ID};
	
	
	
	public Notes(Context context){
		super(context);
	}
	
	public static String getCreateQuery(){
		return "create table "+TABLE_NAME_NOTES +" ("
				+ NOTE_ID +" int primary key, "
				+ NOTE +" text, "
				+ DATE_TAKEN +" text, "
				+ COMMUNITY_ID +" text, "
				+ CHO_ID +" text, "
				+ " )";
		
	}
	
	/*
	public static String getViewCreateSQLString(){
		return "create view "+VIEW_NAME_NOTES+ " as select "
				SELECT * FROM TABLE_NAME_NOTES INNER JOIN 
	}*/
	
	public boolean getAllNotes(){
		try{
			String[] columns ={NOTE_ID,NOTE,DATE_TAKEN,COMMUNITY_ID,CHO_ID}; 
			db=getReadableDatabase();
			cursor=db.query(VIEW_NAME_NOTES, columns, null,null, null, null, null);
			return true;
			
		}catch(Exception ex){
			Log.e("Notes.getAllNOtes()","Exception "+ex.getMessage());
			close();
			return false;
			//cursor=db.query(TABLE_NAME_NOTES, columns, NOTE_ID+"="+note_id,null, null, null, null);	
		}
	}
	
	
	public int saveNote(int communityId,int cho_ID,Date noteDate,String note){
		try{
			int noteID;// = 0;
			
			noteID=getNextId();
			
			db=getWritableDatabase();
			ContentValues cv=new ContentValues();
			
			cv.put(NOTE_ID, noteID);
			cv.put(COMMUNITY_ID, communityId);
			cv.put(CHO_ID, cho_ID);
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-d",Locale.UK);
			cv.put(DATE_TAKEN, dateFormat.format(noteDate));
			
			cv.put(NOTE, note);
			
			if(db.insertWithOnConflict(TABLE_NAME_NOTES, null, cv, SQLiteDatabase.CONFLICT_FAIL)<=0){
				close();
				return 0;
			}
			close();
			
			return noteID;
			
		}catch(Exception ex){
			Log.e("Notes.SaveNote","Exception "+ex.getMessage());
			return 0;
		}
		
	}
	
	public int getNextId(){
		try
		{
			db=getReadableDatabase();
			String [] columns={"MAX(" +NOTE_ID+")"};
			cursor=db.query(TABLE_NAME_NOTES,columns, null, null, null, null, null);
			if(cursor.getCount()<=0){
				close();
				return 1;
			}
			
			cursor.moveToFirst();
			int id=cursor.getInt(0);
			close();
			return id+1;
			
		}catch(Exception e){
			close();
			return 0;
		}
	}
	
	public Note fetch(){
		try{
			if(cursor.isAfterLast()){
				return null;
			}
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			
			int index=cursor.getColumnIndex(NOTE_ID);
			int choId=cursor.getInt(index);
			
			index=cursor.getColumnIndex(NOTE);
			String theNote = cursor.getString(index);
			
			index=cursor.getColumnIndex(DATE_TAKEN);
			String date =cursor.getString(index);
			
			index=cursor.getColumnIndex(COMMUNITY_ID);
			int comm_ID =cursor.getInt(index);
			
			index=cursor.getColumnIndex(CHO_ID);
			int cho_id = cursor.getInt(index);
			
			 
			Note note=new Note(theNote,date,comm_ID,cho_id);
			
			cursor.moveToNext();
			return note;
			
		}catch(Exception e){
			return null;
		}
	}
	
	public ArrayList<Note> getArrayList(){
		ArrayList<Note> list = new ArrayList<Note>();
		Note n = fetch();
		
		return null;
	}
	
}
