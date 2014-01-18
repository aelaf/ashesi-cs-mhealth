package com.ashesi.cs.mhealth.data;



import com.ashesi.cs.mhealth.DataClass;
import android.content.Context;


public class HealthPromotions extends DataClass {
	public static final String REC_DATE="date";
	public static final String REC_NO="rec_no";
	public static final String REC_VENUE="venue";
	public static final String REC_TOPIC="topic";
	public static final String REC_METHOD="method";
	public static final String REC_TARGETAUDIENCE="target_audience";
	public static final String REC_NUMBERAUDIENCE="number_of_audience";
	public static final String REC_REMARKS="remarks";
	public static final String REC_MONTH="month";
	public static final String REC_LATITUDE="latitude";
	public static final String REC_LONGITUDE="longitude";
	public static final String REC_IMAGE="image";
	public static final String REC_IDCHO="idcho";
	public static final String SERVER_REC_NO="server_rec_no";
	public static final String REC_SUBDISTRICTID="subdistrict_id";
	public static final String TABLE_NAME_HEALTH_PROMOTION="health_promotion";
	public HealthPromotions(Context context){
		super(context);
	}
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	
	public static String getCreateSQLString(){
		return "create table "+ TABLE_NAME_HEALTH_PROMOTION +"( "
				+REC_NO +" integer primary key,"
				+REC_DATE+" text, "
				+REC_VENUE +" text, "
				+REC_TOPIC+" text, "
				+REC_METHOD+ " text, "
				+REC_TARGETAUDIENCE+ " text, "
				+REC_NUMBERAUDIENCE+ " text,"
				+REC_REMARKS+" text, "
				+REC_MONTH+" text, "
				+REC_LATITUDE+" text, "
				+REC_LONGITUDE+" text, "
				+REC_IMAGE+" text, "
				+REC_IDCHO+" text, "
				+REC_SUBDISTRICTID+" text, "
				+SERVER_REC_NO+ " integer, "
				+REC_STATE+" integer"
				+" )";
	}
	
	
}
