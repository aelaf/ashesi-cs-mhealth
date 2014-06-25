package com.ashesi.cs.mhealth.knowledge;

public class LocalLink {
	private int link_id;
	private int answerid;
	
	public LocalLink(int id, int answerid){
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
