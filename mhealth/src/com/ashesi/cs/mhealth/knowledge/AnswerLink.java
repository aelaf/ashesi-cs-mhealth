package com.ashesi.cs.mhealth.knowledge;

public class AnswerLink{
	private int link_id;
	private String link;
	private int answerid;
	
	public AnswerLink(int id, String link, int answerid){
		this.link = link;
		this.link_id =id;
		this.answerid = answerid;
	}

	/**
	 * @return the link_id
	 */
	public int getLink_id() {
		return link_id;
	}

	/**
	 * @param link_id the link_id to set
	 */
	public void setLink_id(int link_id) {
		this.link_id = link_id;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the answerid
	 */
	public int getAnswerid() {
		return answerid;
	}

	/**
	 * @param answerid the answerid to set
	 */
	public void setAnswerid(int answerid) {
		this.answerid = answerid;
	}
	
	
	
}
