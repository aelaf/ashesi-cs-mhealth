package com.ashesi.cs.mhealth.data;

import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class HealthPromotionGridAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<HealthPromotion> listHealthPromotion;	//list of scheduled vaccines
	
	
	
	private TextView view;
	public int healthPromoid;

	
	public HealthPromotionGridAdapter(Context context){
		mContext=context;
	}
	@Override
	public int getCount() {
		
		
		if(listHealthPromotion==null){
			return  0;
		}
		//for each health promotion will have 3 properties to display
		return listHealthPromotion.size()*3;	
		
	}

	@Override
	public Object getItem(int position) {
		int index= (int)position/3; 
		if(listHealthPromotion==null){
			return null;
		}
	
		return listHealthPromotion.get(index);
	
	}
	
	
	@Override
	public long getItemId(int position) {
		int index= (int)position/3; 
		
		return this.listHealthPromotion.get(index).getId();
		
		
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getNewView(position);
      

	}
	
	public void setList(ArrayList<HealthPromotion> healthPromotionRecords){
		this.listHealthPromotion=healthPromotionRecords;

	}
	
	/**
	 * It returns a view based on position. 
	 * @param position
	 * @return
	 */
	private View getNewView(int position){
		int columnIndex=position%3;
		int index=position/3;
		//final int id=listHealthPromotion.get(index).getId();
		
		try
		{
				if(columnIndex==0){// column 0: date
					return (View)getTextView(listHealthPromotion.get(index).getDate());
				}else if(columnIndex==1){ //column 1: topic
					view=getTextView(listHealthPromotion.get(index).getTopic());
					//healthPromoid=listHealthPromotion.get(index).getId();
					//view.setTag(Integer.valueOf(position));
					//view.setHint("venue");
					return (View)view;
				}else if(columnIndex==2){
					TextView view=getTextView(listHealthPromotion.get(index).getVenue());
					return (View)view;
				}else {
					return (View)getTextView("---");
				}
			
			
		}catch(Exception ex){
			Log.e("VaccineGridApapter.getNewView", ex.getMessage());
			return null;
		}
    
	}
	

	
	private TextView getTextView(String t){
		TextView textView=new TextView(mContext);
		textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,GridView.LayoutParams.WRAP_CONTENT));
		textView.setPadding(8, 8, 8, 8);
		textView.setText(t);
		textView.setTextColor(Color.BLACK);
		
		
		
		return textView;
	}

	/**
	 * prepares the three columns based on list of vaccines and vaccine record for schedule view
	 */
	
	
	
}
