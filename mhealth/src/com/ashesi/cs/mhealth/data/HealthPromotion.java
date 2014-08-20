package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HealthPromotion {
	private int id;
	private String date;
	private String venue;
	private String topic;
	private String method;
	private String targetAudience;
	private int audienceNumber;
	private String remarks;
	private String month;
	private String latitude;
	private String longitude;
	private String image;
	private int choId;
	private int subdistrictId;
	
	public HealthPromotion(int recId,String date,String venue,String topic,String method,String targetAudience,int audienceNumber,
			String remarks, String month, String latitude, String longitude, String image,int choId, int subdistrictId){
		this.id=recId;
		this.date=date;
		this.venue=venue;
		this.topic=topic;
		this.method=method;
		this.targetAudience=targetAudience;
		this.audienceNumber=audienceNumber;
		this.choId=choId;
		this.remarks=remarks;
		this.month=month;
		this.latitude=latitude;
		this.longitude=longitude;
		this.image=image;
		this.subdistrictId=subdistrictId;
		
	}
	public int getId(){
		return id;
	}
	public String getDate(){
		return date;
	}
	
	public java.util.Date getPromotionDateDate(){
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			return dateFormat.parse(date);
		}
		catch(Exception ex){
			return null;
		}
		
	}
	
	public String getFormattedDate(){
		try
		{
			java.util.Date d=getPromotionDateDate();
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
			return dateFormat.format(d);
		}catch(Exception ex){
			return date;
		}
	}
	
	public String getVenue(){
		return venue;
	}
	
	public String getTopic(){
		return topic;
	}
	
	public String getMethod(){
		return method;
	}
	
	public String getTargetAudience(){
		return targetAudience;
	}
	
	public int getNumberAudience(){
		return audienceNumber;
	}
	
	public int getChoId(){
		return choId;
	}
	public String getRemarks(){
		return remarks;
	}
	
	public String getMonth(){
		return month;
	}
	public String getLatitude(){
		return latitude;
	}
	public String getLongitude(){
		return longitude;
	}
	public String getImage(){
		return image;
	}
	public int getSubdistrictid(){
		return subdistrictId;
	}
	
	public String toString(){
		return getFormattedDate() +"\t"+ topic + "\t " +method + "\t " +targetAudience + "\t " +venue;
	}
}
