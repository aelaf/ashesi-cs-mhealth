package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.VaccinationReport.VaccinationReportRecord;



import android.content.Context;

public class FamilyPlanningReport extends FamilyPlanningRecords {
	final static String NO_RECORDS="no_records";
	final static String TOTAL_QUANTITY="total_quanity";
	
	public FamilyPlanningReport(Context context){
		super(context);
		
	}
	
	public ArrayList<String> getMonthlyFamilyReportStringList(ArrayList<FamilyPlanningReportRecord> list){
		ArrayList<String> listString=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			listString.add(list.get(i).getServiceName());
			listString.add(list.get(i).getTotalQuantityString());
			listString.add(Integer.toString(list.get(i).getNumberOfRecords()));
		}
		
		return listString;
	}
	
	public ArrayList<FamilyPlanningReportRecord> getMonthlyFamilyPlanningReport(int month,int year,int ageRange,String gender){
		//define period for the report
		ArrayList<FamilyPlanningReportRecord> list=new ArrayList< FamilyPlanningReportRecord>();
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

		int[] limit={10,15,18,20,35,50,60,70};
		String strAgeFilter=" 1 ";
		if(ageRange>0){//if it is not total
			ageRange=ageRange-1;
			if(ageRange==0){
				strAgeFilter=CommunityMembers.AGE+"<10";	//under 1 year
			}else if(ageRange>=1 && ageRange<7){	//compute range
				strAgeFilter="("+CommunityMembers.AGE+">="+limit[ageRange]+" AND "+CommunityMembers.AGE+"<"+limit[ageRange+1]+")";
			}else{	
				strAgeFilter=CommunityMembers.AGE+">=70";
			}
		}

		try{
			db=getReadableDatabase();
			String strQuery="select "
					+FamilyPlanningServices.SERVICE_ID +", "
					+FamilyPlanningServices.SERVICE_NAME+", "
					+"SUM(" +FamilyPlanningRecords.QUANTITY+") as "+ TOTAL_QUANTITY +", "
					+"count("+FamilyPlanningServices.SERVICE_ID+") as "+NO_RECORDS
					+" from " +FamilyPlanningRecords.VIEW_NAME_FAMILY_PLANING_RECORDS_DETAIL
					+" where "
					+"("+FamilyPlanningRecords.SERVICE_DATE +">=\""+ firstDateOfTheMonth +"\" AND "
					+FamilyPlanningRecords.SERVICE_DATE +"<=\""+ lastDateOfTheMonth + "\" )"
					+" AND "
					+strAgeFilter
					+" group by "+ FamilyPlanningServices.SERVICE_ID
					+" order by "+ FamilyPlanningServices.SERVICE_NAME;
			cursor=db.rawQuery(strQuery, null);
			cursor.moveToFirst();
			int indexId=cursor.getColumnIndex(FamilyPlanningServices.SERVICE_ID);
			int indexServiceName=cursor.getColumnIndex(FamilyPlanningServices.SERVICE_NAME);
			int indexNoRecords=cursor.getColumnIndex(NO_RECORDS);
			int indexTotalQuantity=cursor.getColumnIndex(TOTAL_QUANTITY);
			FamilyPlanningReportRecord record;
			int serviceId;
			String serviceName;
			int noRecords;
			double quantity;
			while(!cursor.isAfterLast()){
				serviceId=cursor.getInt(indexId);
				serviceName=cursor.getString(indexServiceName);
				noRecords=cursor.getInt(indexNoRecords);
				quantity=cursor.getInt(indexTotalQuantity);
				record= new FamilyPlanningReportRecord(month,year,ageRange,gender,serviceId,serviceName,quantity,noRecords);
				list.add(record);
				cursor.moveToNext();
			}
			close();
			return list;
		}catch(Exception ex){
			return list;
		}
				
				
				
	}
	
	public class FamilyPlanningReportRecord{
		private int ageRange; 		//0: total, 1: under 1, 2: above or equal 1 and less than 2, 3: above or equal 2
		private int month;
		private int year;
		private String gender;		//male or female
		private int serviceId;
		private String serviceName;
		private double totalQuantity;
		private int numberOfRecords;
		
		public FamilyPlanningReportRecord(int month,int year,int ageRange,String gender,int serviceId, String serviceName, double totalQuantity, int numberOfRecords){
			this.month=month;
			this.year=year;		
			this.ageRange=ageRange;
			this.gender=gender;
			this.serviceId=serviceId;
			this.serviceName=serviceName;
			this.numberOfRecords=numberOfRecords;
			this.totalQuantity=totalQuantity;
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
		
		public int getServiceId(){
			return serviceId;
		}
		
		public String getServiceName(){
			return serviceName;
		}
		
		public int getNumberOfRecords(){
			return numberOfRecords;
		}
		
		public double getTotalQuantity(){
			return totalQuantity;
		}
		
		public String getTotalQuantityString(){
			return String.format("%,.2f", totalQuantity);
		}
		
		public String toString(){
			return serviceName +" " +numberOfRecords;
		}
	}
}
