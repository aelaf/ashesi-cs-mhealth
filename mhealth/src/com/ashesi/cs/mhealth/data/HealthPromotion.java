package com.ashesi.cs.mhealth.data;

public class HealthPromotion {
	private String date;
	private String venue;
	private String topic;
	private String method;
	private String target_audience;
	private String number_of_audience;
	private String remarks;
	private String month;
	private String latitude;
	private String longitude;
	private String image;
	private String idcho;
	private String subdistrict_id;
	private int id;
	
	public HealthPromotion(int id,String date,String venue,String topic,String method,String target_audience,String number_of_audience,
			String remarks, String month, String latitude, String longitude, String image,String idcho, String subdistrict_id){
		this.id=id;
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
	
	public HealthPromotion(int id,String date,String venue,String topic){
		this.id=id;
		this.date=date;
		this.venue=venue;
		this.topic=topic;
		
		
	}
	public HealthPromotion(String date,String venue,String topic){
		this.date=date;
		this.venue=venue;
		this.topic=topic;
		
		
	}
	public int getId(){
		return id;
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
	public String getNumberAudience(){
		return number_of_audience;
	}
	
	public String getChoId(){
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
	public String getSubdistrictid(){
		return subdistrict_id;
	}
}
