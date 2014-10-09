package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ashesi.cs.mhealth.DataClass;

public class OPDCaseCategories extends DataClass {
	public static final String TABLE_OPD_CASE_CATEGORIES="opd_case_categories";
	public static final String OPD_CASE_CATEGORY_ID="opd_case_category_id";
	public static final String OPD_CASE_CATEGORY_NAME="opd_case_category_name";
	public OPDCaseCategories(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public boolean add(int id, String opdCaseCategoryName){
		db=getWritableDatabase();
		ContentValues cv=new ContentValues();

		cv.put(OPD_CASE_CATEGORY_ID, id);
		cv.put(OPD_CASE_CATEGORY_NAME, opdCaseCategoryName);
		if(db.insertWithOnConflict(TABLE_OPD_CASE_CATEGORIES, null,cv,SQLiteDatabase.CONFLICT_REPLACE)<=0){
			return false;
		}
		close();
		return true;
	}
	
	public ODPCaseCategory fetch(Cursor cursor){
		try
		{
			
			
			if(cursor.isBeforeFirst()){
				cursor.moveToFirst();
			}
			int index=cursor.getColumnIndex(OPD_CASE_CATEGORY_ID);
			int id=cursor.getInt(index);
			index=cursor.getColumnIndex(OPD_CASE_CATEGORY_NAME);
			String name=cursor.getString(index);
			
			cursor.moveToNext();
			return new  ODPCaseCategory(id,name);
		}catch(Exception ex){
			return null;
		}
		
	}
	
	public ArrayList<ODPCaseCategory> getOPDCaseCategories(){
		ArrayList<ODPCaseCategory> list=new ArrayList<ODPCaseCategory>();
		try{
			String columns[]={OPD_CASE_CATEGORY_ID,OPD_CASE_CATEGORY_NAME};
			db=getReadableDatabase();
			cursor=db.query(TABLE_OPD_CASE_CATEGORIES,columns , null, null, null, null, null);
			ODPCaseCategory opdCaseCategory=fetch(cursor);
			while(opdCaseCategory!=null){
				list.add(opdCaseCategory);
				opdCaseCategory=fetch(cursor);
			}
			return list;
		}catch(Exception ex){
			return list;
		}
	}
	
	/**
	 * This function adds all the default categories of cases to the table
	 * @return
	 */
	public boolean popluateTableWithDefault(){
		try{
			//{"ALL","CI","CNI","NCD","MHC","SC","OGC","RETD","IO","REF","OTH"}
			String query="insert or replace into "+TABLE_OPD_CASE_CATEGORIES+"("+OPD_CASE_CATEGORY_ID +"," 
					+OPD_CASE_CATEGORY_NAME +")"
					+" values "
					+"(1, 'Communicable Imm'),"
					+"(2, 'Communicable Non-Imm'),"
					+"(3, 'Non-Communicable '),"
					+"(4, 'Mental Health '),"
					+"(5, 'Special Condition'),"
					+"(6, 'OGC'),"
					+"(7, 'RETD'),"
					+"(8, 'IO'),"
					+"(9, 'Referral'),"
					+"(10, 'Other Cases'),"
					+"(11, 'Mental Health Extended')";
			db=getWritableDatabase();
			db.execSQL(query);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	static public String getCreateSQLString(){
		return "create table "+ TABLE_OPD_CASE_CATEGORIES+ " ("
					+ OPD_CASE_CATEGORY_ID + " integer primary key, "
					+ OPD_CASE_CATEGORY_NAME +" text, "
					+" )";
				
	}
	

	public class ODPCaseCategory{
		private int id;
		private String opdCaseCategoryName;
		public ODPCaseCategory(){
			
		}
		
		public ODPCaseCategory(int id, String opdCaseCategoryName){
			this.id=id;
			this.opdCaseCategoryName=opdCaseCategoryName;
		}
		
		public int getId(){
			return id;
		}
		
		public String getOPDCaseCategoryName(){
			return opdCaseCategoryName;
		}
		
		public String toString(){
			return opdCaseCategoryName;
		}
	}
}
