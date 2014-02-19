package com.ashesi.cs.mhealth.knowledge;

public class ResourceMaterial {
	private int id, catId, type;
	private String content;

	public ResourceMaterial(int id, int type, int catId, String content) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.catId = catId;
		this.type  = type;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getCatId() {
		return catId;
	}
	
	public String getContent(){
		return content;
	}
	
	public String toString() {
		return "Resource Material ID: " + id + " - " + content +  " - of type: " + type + "under category: " + catId;
	}
}
