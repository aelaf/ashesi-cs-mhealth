package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.DataClass;

import android.database.Cursor;

public class OPDCaseReportRecord {

	public int ageRange;
	public int month;
	public int year;
	public String gender;
	ArrayList<String> list;
	public OPDCaseReportRecord(){
		
	}
	
	public OPDCaseReportRecord(Cursor cursor){
		
		try{
			list=new ArrayList<String>();		
			if(cursor.isBeforeFirst()){
				cursor.moveToNext();
			}
			int indexOPDCaseName=cursor.getColumnIndex(OPDCases.OPD_CASE_NAME);
			int indexNoCases=cursor.getColumnIndex(OPDCaseRecords.NO_CASES);
			
			while(!cursor.isAfterLast()){
				list.add(cursor.getString(indexOPDCaseName));
				list.add(String.valueOf(cursor.getInt(indexNoCases)));
				cursor.moveToNext();
			}
		}
		catch(Exception ex){
			
		}
	}
}
