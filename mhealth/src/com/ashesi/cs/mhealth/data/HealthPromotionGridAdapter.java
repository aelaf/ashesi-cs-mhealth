package com.ashesi.cs.mhealth.data;

import android.widget.BaseAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

public class HealthPromotionGridAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<HealthPromotion> listHealthPromotion;	//list of scheduled vaccines
	//private ArrayList<HealthPromotion> listDetailsHealthPromotion;
	
	//There are 4 columns in GridView of vaccine records. In SCHEDULE_LIST mode these string arrays
	//will be used as data source. In RECORD_LIST mode, the data comes from listVaccineRecord

	
	public static final int  PROMOTION_LIST=2;
	public static final int  DETAIL_LIST=1;

	private int mMode=PROMOTION_LIST;
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
		//for each vaccine there will be 4 items
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
        
	
	
	public void setList(ArrayList<HealthPromotion> healthPromotionRecords){
		this.listHealthPromotion=healthPromotionRecords;
		//HealthPromotionColumn();
		
	}
	
	/**
	 * It returns a view based on position. 
	 * @param position
	 * @return
	 */
	private View getNewView(int position){
		int columnIndex=position%3;
		int index=position/3;
		try
		{
				if(columnIndex==0){// column 0: date
					return (View)getTextView(listHealthPromotion.get(index).getDate());
				}else if(columnIndex==1){ //column 1: topic
					view=getTextView(listHealthPromotion.get(index).getTopic());
				 healthPromoid=listHealthPromotion.get(index).getId();
					view.setTag(Integer.valueOf(position));
					//view.setHint("venue");
					return (View)view;
				}else if(columnIndex==2){
					TextView view=getTextView(listHealthPromotion.get(index).getVenue());
					//view.setHint("date vaccination recored");
					return (View)view;
				}else {
					return (View)getTextView("---");
				}
			
			
		}catch(Exception ex){
			//Log.e("VaccineGridApapter.getNewView", ex.getMessage());
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
