package com.ashesi.cs.mhealth.data.util;
/*
 * todo:
 * "Status" of Online Mode should be in MainActivity so i can read it here.
 * next remove first line after class definition.
 */
import java.util.HashMap;

import android.util.Log;
import android.widget.Toast;



public class DataPoster implements MHealthConstants {
int Status=OnlineMode;  //this shoiuld be removed.

	/**
	 * Posts data to remote server using PostDataTask Object
	 * @param actionType type of action/post request. Maybe one in this case. Different POST requests can be send.
	 * @param values the key-value pairs to send to server
	 */
	public void postData(int actionType, HashMap<String,String>values){//send data excluding image to server
		//This calls the PostDataAsyncTask

	
		int TargetPane;
		

		String addr=null;
		int action=-1;
		switch ( actionType){
		 case    actionUPLOAD_OFFLINE_DATA:
		 	 addr=BaseURL+"?action=UPLOAD_OFFLINE_DATA";
			 action=actionType;
			 break;
		 case actionCHANGE_PASSWORD:
			 addr=BaseURL+"?action=CHANGE_PASSWORD"; 
			 action=actionType;
			 break;
		 }
		if (Status==OnlineMode){
			Log.v("postData msg","Online_");
			new PostDataTask(action,addr,getApplicationContext() , values).execute();
		}
		if (Status ==OfflineMode){  //is offline. Currently saves only visit data
			Log.v("postData msg","OFFline_processing");
			switch ( actionType){
			 case    actionCHANGE_PASSWORD:
	
			 //save locally
				
				 break;
			 	 
			default:
				//Toast.makeText(this, "Sorry, You can do this online only", Toast.LENGTH_LONG).show();
					 
			}//switch
		} //offline mode

		
	}
	
}
