/*
 * N Amanquah
 * To hold commonly used functions that will aid data manipulation eg date and string conversions.
 */
package com.ashesi.cs.mhealth.data.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;



public class UtiltyFunctions {

	
	
	/**
	 * This creates a unique ID that can be used eg as primary key even in offline mode.
	 * It is based on timestamp
	 * 
	 * @param qualifier adds a string at end to qualify the identifier
	 * - in case i want to have different qualified uniqueIDs.
	 * @return the uniqueID as a string
	 */
	public static String uniqueID(String qualifier){
		
		Calendar cal= Calendar.getInstance();
    	long timeStamp=cal.getTimeInMillis();
    	//String uniqueID=""+timeStamp+"_"+Login.UserID+qualifier;  //makes use of an accessible UserID; pass blank as qualifier if not needed
    	String uniqueID=""+timeStamp+"_"+qualifier;  //pass userID as the qualifier
		return uniqueID;
	}
	
	
	
/**
 * To store passwords for offline login, i need to be able to evaluate md5	
 */
    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * The MySQL date uses -. Local SQLite db expects /
     * This function converts - to /
     */
    public static final String SQLdateToAndroidStringDate(String sqlDate){
    	return sqlDate.replace("-", "/");
    }
    
    
}
