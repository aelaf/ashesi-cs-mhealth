package com.ashesi.cs.mhealth.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
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
	private ArrayList<Vaccine> listVaccine;
	private ArrayList<VaccineRecord> listVaccineRecords;
	private ArrayList<String> vaccinationDate=null;
	private ArrayList<String> column0;
	private ArrayList<String> column1;
	private ArrayList<String> column2;
	private ArrayList<Boolean> column3;


	
	public VaccineGridAdapter(Context context){
		mContext=context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(listVaccine==null){
			return  0;
		}
		//for each vaccine there will be 4 items
		return listVaccine.size()*4;
	}

	@Override
	public Object getItem(int position) {
		// TODO based on position, it could be Vaccine record;
		int index= (int)position/4; 
		if(listVaccine==null){
			return null;
		}
		return listVaccine.get(index);
	
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	return getNewView(position);
            
        } else {
        	return getExistingView(position,convertView);
        }

	}
	
        
	public void setList(ArrayList<Vaccine> list ){
		this.listVaccine=list;
	}
	public void setList(ArrayList<Vaccine> list,CommunityMember cm,ArrayList<VaccineRecord> vaccineRecords){
		this.listVaccine=list;
		this.listVaccineRecords=vaccineRecords;
		this.communityMember=cm;
		prepareVaccinationColumn();
	}
	
	private View getNewView(int position){
		int columnIndex=position%4;
		int index=position/4;
		
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
    
	}
	
	private View getExistingView(int position, View convertView){
		int columnIndex=position%4;
		int index=position/4;
		
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
	}
	private TextView getTextView(String t){
		TextView textView=new TextView(mContext);
		textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,GridView.LayoutParams.WRAP_CONTENT));
		textView.setPadding(8, 8, 8, 8);
		textView.setText(t);
		return textView;
	}
	
	private ImageView getImageView( boolean checked ){
		ImageView image=new ImageView(mContext);
		image.setLayoutParams(new GridView.LayoutParams(24,24));
		image.setPadding(8,8,8,8);
		if(checked){
			image.setImageResource(R.drawable.checked);
		}else{
			image.setImageResource(R.drawable.unchecked);
		}
		
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
	 * prepares the four columns based on list of vaccines and vaccine record
	 */
	private void prepareVaccinationColumn(){
		int vaccineId;
		Vaccine v;
		VaccineRecord r;
		String str;
		column0=new ArrayList<String>(listVaccine.size());
		column1=new ArrayList<String>(listVaccine.size());
		column2=new ArrayList<String>(listVaccine.size());
		column3=new ArrayList<Boolean>(listVaccine.size());
		
		for(int i=0;i<listVaccine.size();i++){
			v=listVaccine.get(i);
			column0.add(i,v.getVaccineName());
			java.util.Date date=v.getWhenToVaccine(communityMember.getBirthdate());
    		column1.add(i,getFormattedDate(date));
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
	 * gets a record for a particlar vaccine from list
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
	
	public VaccineRecord getVaccineRecord(int index){
		Vaccine v=listVaccine.get(index);
		return findVaccineRecord(v.getId());
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
		int index=position/4;
		listVaccineRecords.add(record);
		column2.set(index, record.getVaccineDate());
		column3.set(index, true);
		this.notifyDataSetChanged();
		return true;
	}
	
	public boolean updateRemovedRecord(int position, VaccineRecord record){
		int index=position/4;
		listVaccineRecords.remove(record);
//		for(int i=0;i<listVaccineRecords.size();i++){
//			if(listVaccineRecords.get(i).getId()==record.getId()){
//				listVaccineRecords.remove(index);
//			}
//		}
		
		column2.set(index, "no record");
		column3.set(index, false);
		this.notifyDataSetChanged();
		return true;
	}
	/*
	 * this two function are alternative algorithms
	private View getNewView(int position){
		int columnIndex=position%4;
		int index=position/4;
		Vaccine v=listVaccine.get(index);
		if(columnIndex==0){// column 0: vaccine name
			return (View)getTextView(v.getVaccineName());
    	}else if(columnIndex==1){ //column 1: vaccine schedule
    		java.util.Date date=v.getWhenToVaccine(Calendar.getInstance().getTime());
    		String str=getFormattedDate(date);
    		return (View)getTextView(str);
    	}else if(columnIndex==2){
    		return (View)getTextView(vaccinationDate.get(v.getId()));
    	}else if(columnIndex==3){ //third column in the grid
    		return (View)getCheckBox(true);
		}else {
    		return (View)getTextView("--");
    	}
    
	}
	
	private View getExistingView(int position, View convertView){
		int columnIndex=position%4;
		int index=position/4;
		Vaccine v=listVaccine.get(index);
		if(columnIndex==0){//column 0: name
			TextView textView=(TextView)convertView;
			textView.setText(v.getVaccineName());
	        return (View)textView;
		}else if (columnIndex==1){ //column 1: schedule date based on birthday
			TextView textView=(TextView)convertView;
			java.util.Date date=v.getWhenToVaccine(Calendar.getInstance().getTime());
    		String str=getFormattedDate(date);
    		textView.setText(str);
    		return (View)textView;
		}else if(columnIndex==2){
			TextView textView=(TextView)convertView;
			textView.setText(vaccinationDate.get(v.getId()));
    		return (View)textView;
		}else if(columnIndex==3){	//column 3: vaccine given/not
			CheckBox cb=(CheckBox)convertView;
			cb.setChecked(true);
			return (View)cb;
		}else {
			TextView textView=(TextView)convertView;
			textView.setText("test");
	        return (View)textView;
		}
	}*/
}
