package com.ashesi.cs.mhealth.knowledge;

public class ResourceMaterial {
	private int id, catId, type;
	private String content, description;

	public ResourceMaterial(int id, int type, int catId, String content, String desc) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.catId = catId;
		this.type  = type;
		this.content = content;
		this.description = desc;
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
