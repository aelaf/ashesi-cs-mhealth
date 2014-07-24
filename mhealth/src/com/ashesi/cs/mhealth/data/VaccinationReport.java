package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;

public class VaccinationReport extends VaccineRecords {
	final static String NO_RECORDS="no_records";
	public VaccinationReport(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList< VaccinationReportRecord> getMonthlyVaccinationReport(int month,int year,int ageRange,String gender){
		//define period for the report
		ArrayList< VaccinationReportRecord> list=new ArrayList< VaccinationReportRecord>();
		String firstDateOfTheMonth;
		String lastDateOfTheMonth;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
		Calendar calendar=Calendar.getInstance();
		if(month==0){ //this month
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}else if(month==1){	//this year/all year
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH,Calendar.JANUARY);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(Calendar.MONTH,Calendar.DECEMBER);
			calendar.set(Calendar.DAY_OF_MONTH,31);
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}else{	//selected month and year
			month=month-2;
			calendar.set(year, month, 1);
			firstDateOfTheMonth=dateFormat.format(calendar.getTime());
			calendar.set(year,month,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			lastDateOfTheMonth=dateFormat.format(calendar.getTime());
		}

		//define age range
		int[] limit={0,12,24};
		String strAgeFilter=" 1 ";

		if(ageRange==0){ //all total
			strAgeFilter=" 1 "; 
		}else if(ageRange==1){ //under 1 year
			strAgeFilter=" "+ AGE+"<1";	
		}else if(ageRange==2){	//between 1 and less than 2 years 
			strAgeFilter=" ("+AGE+">=1 AND "+AGE+"< 2) "; 
		}else if(ageRange==3){
			strAgeFilter=" "+AGE+">=2 ";
		}else{
			strAgeFilter=" 1 "; 
		}
		
		String strGenderFilter=" 1 ";
		if(gender != null){
			if(!gender.equals("all")){
				strGenderFilter=CommunityMembers.GENDER +" = '"+gender+"'";
			}
		}

		try{
			db=getReadableDatabase();
			String strQuery="select "
					+Vaccines.VACCINE_ID +", "
					+Vaccines.VACCINE_NAME+", "
					+"count("+Vaccines.VACCINE_ID+") as "+NO_RECORDS
					+" from " +VaccineRecords.VIEW_NAME_VACCINE_RECORDS_DETAIL
					+" where "
					+"("+VaccineRecords.VACCINE_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
					+VaccineRecords.VACCINE_DATE +"<=\""+ lastDateOfTheMonth + "\" )"
					+" AND "
					+strAgeFilter+ " AND "
					+strGenderFilter
					+" group by "+ Vaccines.VACCINE_ID
					+" order by "+ Vaccines.VACCINE_ID;
			
			cursor=db.rawQuery(strQuery, null);
			cursor.moveToFirst();
			int indexId=cursor.getColumnIndex(Vaccines.VACCINE_ID);
			int indexVaccineName=cursor.getColumnIndex(Vaccines.VACCINE_NAME);
			int indexNoRecords=cursor.getColumnIndex(NO_RECORDS);
			VaccinationReportRecord record;
			int vaccineId;
			String vaccineName;
			int noRecords;
			while(!cursor.isAfterLast()){
				vaccineId=cursor.getInt(indexId);
				vaccineName=cursor.getString(indexVaccineName);
				noRecords=cursor.getInt(indexNoRecords);
				record= new VaccinationReportRecord(month,year,ageRange,gender,vaccineId,vaccineName,noRecords);
				list.add(record);
				cursor.moveToNext();
			}
			close();
			return list;
		}catch(Exception ex){
			return list;
		}
				
				
				
	}
	
	public ArrayList<String> getMonthlyVaccinationReportStringList(ArrayList<VaccinationReportRecord> list){
		ArrayList<String> listString=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			listString.add(list.get(i).getVaccineName());
			listString.add("");
			listString.add(Integer.toString(list.get(i).getNumberOfRecords()));
		}
		
		return listString;
	}
	
	public class VaccinationReportRecord{
		private int ageRange; 		//0: total, 1: under 1, 2: above or equal 1 and less than 2, 3: above or equal 2
		private int month;
		private int year;
		private String gender;		//male or female
		private int vaccineId;
		private String vaccineName;
		private int numberOfRecords;
		
		public VaccinationReportRecord(int month,int year,int ageRange,String gender,int vaccineId, String vaccineName, int numberOfRecords){
			this.month=month;
			this.year=year;		
			this.ageRange=ageRange;
			this.gender=gender;
			this.vaccineId=vaccineId;
			this.vaccineName=vaccineName;
			this.numberOfRecords=numberOfRecords;
		}
		
		public int getMonth(){
			return month;
		}
		
		public int getYear(){
			return year;
		}
		
		public int getAgeRange(){
			return ageRange;
		}
		
		public String getGender(){
			return gender;
		}
		
		public int getVaccineId(){
			return vaccineId;
		}
		
		public String getVaccineName(){
			return vaccineName;
		}
		
		public int getNumberOfRecords(){
			return numberOfRecords;
		}
		
		public String toString(){
			return vaccineName +" " +numberOfRecords;
		}
	}
}
