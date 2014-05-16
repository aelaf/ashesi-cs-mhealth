package com.ashesi.cs.mhealth.knowledge;

public class Answer {
	private int answerId;
	private String answer;
	private int userId;
	private String qID;
	private String date;
	
	public Answer(int id, String content, int userId, String qId, String theDate) {
		// TODO Auto-generated constructor stub
		answerId = id;
		answer = content;
		this.userId = userId;
		qID = qId;
		date = theDate;
	}
	
	public String getId(){
		return qID;
	}
	
	public String getAnswer(){
		return answer;
	}
	
	public int getAnswerId(){
		return answerId;
	}
	
	public int getUserId(){
		return userId;
	}
	
	public String toString(){
		return  "User ID: " + userId  + " answer to : " +  qID + " is: " + answer;
	}
	
	public String getDate(){
		return date;
	}
}
