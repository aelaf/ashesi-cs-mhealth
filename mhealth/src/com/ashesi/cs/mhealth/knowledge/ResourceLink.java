package com.ashesi.cs.mhealth.knowledge;

public class ResourceLink {
	private int linkId;
	private String link;
	private int answerId;

	public ResourceLink(int id, String thelink, int answerId) {
		// TODO Auto-generated constructor stub
		linkId = id;
		link = thelink;
		this.answerId  = answerId;
	}

	public int getId() {
		return linkId;
	}

	public String getLink() {
		return link;
	}

	public int getAnswerId() {
		return answerId;
	}
	
	public String toString() {
		return link + " - for the answer: " + answerId;
	}
}
