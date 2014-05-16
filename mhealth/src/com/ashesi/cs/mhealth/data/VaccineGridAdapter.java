package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class VaccineGridAdapter extends BaseAdapter {
	private Context mContext;
	private CommunityMember communityMember=null;
	private ArrayList<Vaccine> listVaccine;	//list of scheduled vaccines
	private ArrayList<VaccineRecord> listVaccineRecords;
	
	//There are 4 columns in GridView of vaccine records. In SCHEDULE_LIST mode these string arrays
	//will be used as data source. In RECORD_LIST mode, the data comes from listVaccineRecord
	private ArrayList<String> column0;
	private ArrayList<String> column1;
	private ArrayList<String> column2;
	private ArrayList<Boolean> column3;
	
	public static final int  SCHEDULE_LIST=2;
	public static final int  RECORD_LIST=1;

	private int mMode=SCHEDULE_LIST;
	
	private int textColor;
	
	public VaccineGridAdapter(Context context){
		mContext=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mMode==RECORD_LIST){
			if(listVaccineRecords==null){
				return  0;
			}
			//for each vaccine there will be 4 items
			return listVaccineRecords.size()*4;
		}else{
			if(listVaccine==null){
				return  0;
			}
			//for each vaccine there will be 4 items
			return listVaccine.size()*4;	
		}
		
	}

	@Override
	public Object getItem(int position) {
		int index= (int)position/4; 
		if(listVaccine==null){
			return null;
		}
		// TODO: in case of RECORD_LIST view, it should return VaccineRecord from listVaccineRecord
		return listVaccine.get(index);
	
	}

	@Override
	public long getItemId(int position) {
		int index= (int)position/4; 
		if(mMode==RECORD_LIST){
			return this.listVaccineRecords.get(index).getId();
		}else{
			return this.listVaccine.get(index).getId();
		}
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getNewView(position);
        //the best method would be to update existing views if they exists
		//but it is not working in RECORD_LIST mode when attempting to delete two records 
		//if (convertView == null) {  // if it's not recycled, initialize some attributes
        //	return getNewView(position);  
        //} else {
        //	return getExistingView(position,convertView);
        //}

	}
	
	public int getMode(){
		return mMode;
	}
	
	public void setMode(int mode){
		mMode=mode;
		this.notifyDataSetChanged();
		
	}
        
	public void setList(ArrayList<VaccineRecord> vaccineRecords ){
		this.listVaccineRecords=vaccineRecords;
		mMode=VaccineGridAdapter.SCHEDULE_LIST;
	}
	
	public void setList(ArrayList<Vaccine> vaccines,CommunityMember cm,ArrayList<VaccineRecord> vaccineRecords){
		this.listVaccine=vaccines;
		this.listVaccineRecords=vaccineRecords;
		this.communityMember=cm;
		mMode=SCHEDULE_LIST;
		prepareVaccinationColumn();
	}
	
	public void setList(ArrayList<Vaccine> vaccines,CommunityMember cm,ArrayList<VaccineRecord> vaccineRecords, int mode){
		this.listVaccine=vaccines;
		this.listVaccineRecords=vaccineRecords;
		this.communityMember=cm;
		this.mMode=mode;
		
		prepareVaccinationColumn();
	}
	/**
	 * It returns a view based on position. 
	 * @param position
	 * @return
	 */
	private View getNewView(int position){
		int columnIndex=position%4;
		int index=position/4;
		try
		{
			
			if(mMode==SCHEDULE_LIST){
				if(columnIndex==0){// column 0: vaccine name
					return (View)getTextView(column0.get(index));
				}else if(columnIndex==1){ //column 1: vaccine schedule
					TextView view=getTextView(column1.get(index));
					view.setHint("vaccine schedule based on birthdate");
					return (View)view;
				}else if(columnIndex==2){
					TextView view=getTextView(column2.get(index));
					view.setHint("date vaccination recored");
					return (View)view;
				}else if(columnIndex==3){ //third column in the grid
					return (View)getImageView(column3.get(index));
				}else {
					return (View)getTextView("---");
				}
			}else{	//simple list
				//Log.d("VaccineGRidAdapter.ExcistingView", "NP=" +position);
				if(columnIndex==0){// column 0: vaccine name
					//Log.d("VaccineGRidAdapter.NewView", "text 0");
					return (View)getTextView(listVaccineRecords.get(index).getVaccineName());
				}else if(columnIndex==1){ //column 1: vaccine schedule
					TextView view=getTextView("---");
					//Log.d("VaccineGRidAdapter.NewView", "text 1");
					view.setHint("vaccine schedule based on birthdate");
					return (View)view;
				}else if(columnIndex==2){
					TextView view=getTextView(listVaccineRecords.get(index).getVaccineDate());
					//Log.d("VaccineGRidAdapter.NewView", "text 2");
					view.setHint("date vaccination recored");
					return (View)view;
				}else if(columnIndex==3){ //third column in the grid
					//Log.d("VaccineGRidAdapter.NewView", "image 3");
					return (View)getImageViewRemove();
				}else {
					//Log.d("VaccineGRidAdapter.NewView", "text default");
					return (View)getTextView("---");
				}
			}
		}catch(Exception ex){
			//Log.e("VaccineGridApapter.getNewView", ex.getMessage());
			return null;
		}
    
	}
	
	public void setTextColor(int c){
		textColor=c;
	}
	
	private TextView getTextView(String t){
		TextView textView=new TextView(mContext);
		textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,GridView.LayoutParams.WRAP_CONTENT));
		textView.setPadding(8, 8, 8, 8);
		textView.setText(t);
		if(textColor!=0){
			textView.setTextColor(textColor);
		}
		
		return textView;
	}
	
	private ImageView getImageView(boolean checked ){
		ImageView image=new ImageView(mContext);
		image.setLayoutParams(new GridView.LayoutParams(30,30));
		image.setPadding(8,8,8,8);
		if(checked){
			image.setImageResource(R.drawable.checked);
		}else{
			image.setImageResource(R.drawable.unchecked);
		}
		
		return image;
		
	}
	private ImageView getImageViewRemove(){
		ImageView image=new ImageView(mContext);
		image.setLayoutParams(new GridView.LayoutParams(30,30));
		image.setPadding(8,8,8,8);
		image.setImageResource(R.drawable.remove);
		
		return image;
		
	}
	/**
	 * formats a date for display
	 * @param date
	 * @return
	 */
	private String getFormattedDate(java.util.Date date){
		try
		{
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
			return dateFormat.format(date);
		}
		catch(Exception ex){
			return "";
		}
	}
	/**
	 * prepares the four columns based on list of vaccines and vaccine record for schedule view
	 */
	private void prepareVaccinationColumn(){
		
		//prepare for schedule view
		Vaccine v;
		VaccineRecord r;

		column0=new ArrayList<String>(listVaccine.size());
		column1=new ArrayList<String>(listVaccine.size());
		column2=new ArrayList<String>(listVaccine.size());
		column3=new ArrayList<Boolean>(listVaccine.size());
		
		for(int i=0;i<listVaccine.size();i++){
			v=listVaccine.get(i);
			column0.add(i,v.getVaccineName());
			if(v.getVaccineSchedule()>=0){
				java.util.Date date=v.getWhenToVaccine(communityMember.getBirthdate());
				column1.add(i,getFormattedDate(date));
			}else{
				column1.add(i,"---");
			}
    		r=findVaccineRecord(v.getId());
    		if(r==null){
    			column2.add(i,"no record");
    			column3.add(i,false);
    		}else{
    			column2.add(i,r.getVaccineDate());
    			column3.add(i,true);
    		}
		}
		
		
	}
	/**
	 * gets a record for a particular vaccine from list
	 * @param vaccineId
	 * @return
	 */
	private VaccineRecord findVaccineRecord(int vaccineId){
		for(int j=0;j<listVaccineRecords.size();j++){
			if(listVaccineRecords.get(j).getVaccineId()==vaccineId){
				return listVaccineRecords.get(j);
			}
		}
		return null;
	}
	
	public VaccineRecord getVaccineRecord(int position){
		int index=position/4;
		if(mMode==RECORD_LIST){
			return listVaccineRecords.get(index);
		}else{
			Vaccine v=listVaccine.get(index);
			return findVaccineRecord(v.getId());
		}
		
	}
	
	public boolean getStatus(int position){
		int index=position/4;
		return column3.get(index);
		
	}
	
	public Vaccine getVaccine(int position){
		int index=position/4;
		if(index>=listVaccine.size()){
			return null;
		}
		return listVaccine.get(index);
	}
	
	public boolean updateNewRecord(int position, VaccineRecord record){

		listVaccineRecords.add(record);
		if(mMode==SCHEDULE_LIST){
			int index=position/4;
			column2.set(index, record.getVaccineDate());
			column3.set(index, true);
		}
		this.notifyDataSetChanged();
		return true;
	}
	
	public boolean updateRemovedRecord(int position, VaccineRecord record){
		
		
		listVaccineRecords.remove(record);

		if(mMode==SCHEDULE_LIST){
			int index=position/4;
			column2.set(index, "no record");
			column3.set(index, false);
		}
		this.notifyDataSetChanged();
		return true;
	}
	/*
	private View getExistingView(int position, View convertView){
		int columnIndex=position%4;
		int index=position/4;
		try
		{
			if(mMode==SCHEDULE_LIST){
				if(columnIndex==0){//column 0: name
					TextView textView=(TextView)convertView;
					textView.setText(column0.get(index));
					return (View)textView;
				}else if (columnIndex==1){ //column 1: schedule date based on birthday
					TextView textView=(TextView)convertView;
					textView.setText(column1.get(index));
					return (View)textView;
				}else if(columnIndex==2){
					TextView textView=(TextView)convertView;
					textView.setText(column2.get(index));
					return (View)textView;
				}else if(columnIndex==3){	//column 3: vaccine given/not
					ImageView image=(ImageView)convertView;
					if(column3.get(index)){
						image.setImageResource(R.drawable.checked);
					}else{
						image.setImageResource(R.drawable.unchecked);
					}
					return (View)image;
				}else {
					TextView textView=(TextView)convertView;
					textView.setText("test");
					return (View)textView;
				}
			}else{
			
				Log.d("VaccineGRidAdapter.ExcistingView", "P=" +position);
				if(columnIndex==0){//column 0: name
					TextView textView=(TextView)convertView;
					textView.setText(listVaccineRecords.get(index).getVaccineName());
					Log.d("VaccineGRidAdapter.ExcistingView", "text 0");
					return (View)textView;
				}else if (columnIndex==1){ //column 1: schedule date based on birthday
					TextView textView=(TextView)convertView;
					textView.setText("---");
					Log.d("VaccineGRidAdapter.ExcistingView", "text 1");
					return (View)textView;
				}else if(columnIndex==2){
					TextView textView=(TextView)convertView;
					textView.setText(listVaccineRecords.get(index).getFormattedVaccineDate());
					Log.d("VaccineGRidAdapter.ExcistingView", "text 2");
					return (View)textView;
				}else if(columnIndex==3){	//column 3: vaccine given/not
					ImageView image=(ImageView)convertView;
					image.setImageResource(R.drawable.remove);
					Log.d("VaccineGRidAdapter.ExcistingView", "image 3");
					return (View)image;
				}else {
					TextView textView=(TextView)convertView;
					textView.setText("---");
					Log.d("VaccineGRidAdapter.ExcistingView", "text default");
					return (View)textView;
				}	
			}
		}catch(Exception ex){
			Log.e("VaccineGridAddpapter.getExisitngView", ex.getMessage());
			return null;
		}
	}
	*/

}
