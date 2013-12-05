package com.ashesi.cs.mhealth.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataVerson {
	static String[] columns={"version"};
	static int getDataVersion(SQLiteDatabase db,String dataname){
		
		try
		{
			Cursor cursor=db.query("dataversion", columns, "dataname='categories'", null, null, null, null);
			if(cursor.getColumnCount()==0){
				return 0;
			}
			
			int dataversion=cursor.getInt(0);
			cursor.close();
		
		return dataversion;
		}catch(Exception ex){
			return 0;
		}
	}
}
