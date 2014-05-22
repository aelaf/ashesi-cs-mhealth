package com.ashesi.cs.mhealth.knowledge;

public class ResourceMaterial {
	private int id, catId, type;
	private String content, description, tag;

	public ResourceMaterial(int id, int type, int catId, String content, String desc, String tag) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.catId = catId;
		this.type  = type;
		this.content = content;
		this.description = desc;
		this.tag = tag;
	}

	public String getTag(){
		return this.tag;
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
	
	public String getDescription(){
		return description;
	}
	
	public String toString() {
		return "Resource Material ID: " + id + " - " + content +  " - of type: " + type + "under category: " + catId + " Description: " + description;
	}
}
