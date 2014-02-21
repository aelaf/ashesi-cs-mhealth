package com.ashesi.cs.mhealth.data;

import java.util.Date;

public class Note {
	private int note_id;
	private String note;
	private String dateTaken;
	private int theCommunityId;
	private int choId;
	
	public Note(String note,String datetaken,int comm,int cho){
		this.note = note;
		this.dateTaken = datetaken;
		this.theCommunityId = comm;
		this.choId = cho;
	}
	
	public Note(int noteId,String note, String datetaken,int comm,int cho){
		this.note_id = noteId;
		this.note = note;
		this.dateTaken = datetaken;
		this.theCommunityId = comm;
		this.choId = cho;
	}
	
	/*public String getNote(){
		return note;
	}*/
	
	public Note getNote(){
		return this;
	}
	
	public String getDate(){
		return dateTaken;
	}
	
	public int theCommunity(){
		return theCommunityId;
	}
	
	public String toString(){
		return note;
	}
	
	public int getCHO(){
		return choId;
	}
	
	public void setNoteId(int noteID){
		this.note_id = noteID;
	}
	
	public int getNoteId(){
		return this.note_id;
	}

}
