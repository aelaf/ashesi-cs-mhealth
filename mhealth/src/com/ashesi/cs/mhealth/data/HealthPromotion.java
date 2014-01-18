package com.ashesi.cs.mhealth.data;

public class HealthPromotion {
	private String date;
	private String venue;
	private String topic;
	private String method;
	private String target_audience;
	private int number_of_audience;
	private String remarks;
	private String month;
	private String latitude;
	private String longitude;
	private String image;
	private int idcho;
	private int subdistrict_id;
	
	public HealthPromotion(String date,String venue,String topic,String method,String target_audience,int number_of_audience,
			String remarks, String month, String latitude, String longitude, String image, int idcho, int subdistrict_id){
		this.date=date;
		this.venue=venue;
		this.topic=topic;
		this.method=method;
		this.target_audience=target_audience;
		this.number_of_audience=number_of_audience;
		this.idcho=idcho;
		this.remarks=remarks;
		this.month=month;
		this.latitude=latitude;
		this.longitude=longitude;
		this.image=image;
		this.subdistrict_id=subdistrict_id;
		
	}
	public String getDate(){
		return date;
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
		return target_audience;
	}
	public int getNumberAudience(){
		return number_of_audience;
	}
	
	public int getChoId(){
		return idcho;
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
		return subdistrict_id;
	}
}
