package com.ashesi.cs.mhealth.data.util;
/*
 * Author: N Amanquah
 * This interface holds shared constants eg the remote server address 
 * 
 */
public interface MHealthConstants {

	String BaseURL="http://10.0.2.2/mHealth/utilityfunctions.php";
	
	/*
	 * differnt post requests. All make use of the same method.
	 */
	int actionUPLOAD_OFFLINE_DATA=1;
	int actionCHANGE_PASSWORD=2;
	
	
	//the (online) status of the app.
	int OnlineMode=100;
	int OfflineMode=101;
			
	
}
