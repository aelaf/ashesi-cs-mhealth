package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HealthPromotionDetailsAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HealthPromotion> listHealthPromotionDetails;

	
	public HealthPromotionDetailsAdapter(Context context){
		mContext=context;
	}
	@Override
	public int getCount() {

		if(listHealthPromotionDetails==null){
			return  0;
		}
		//for each health promotion will have 3 properties to display
		return listHealthPromotionDetails.size()*11;	
		
	}

	@Override
	public Object getItem(int position) {
		int index= (int)position/11; 
		if(listHealthPromotionDetails==null){
			return null;
		}
	
		return listHealthPromotionDetails.get(index);
	
	}

	@Override
	public long getItemId(int position) {
		int index= (int)position/11; 
		
		return this.listHealthPromotionDetails.get(index).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getNewView(position);
	}
	public void setList(ArrayList<HealthPromotion> healthPromotionRecords){
		this.listHealthPromotionDetails=healthPromotionRecords;

	}
	
	private View getNewView(int position){
		int columnIndex=position%11;
		int index=position/11;
		//final int id=listHealthPromotion.get(index).getId();
		
		try
		{
				if(columnIndex==0){// column 0: date
					return (View)getTextView("Date: "+listHealthPromotionDetails.get(index).getDate());
				}else if(columnIndex==1){ //column 1: topic
					return (View)getTextView("Topic: "+listHealthPromotionDetails.get(index).getTopic());
				}else if(columnIndex==2){
					return (View)getTextView("Method Used: "+listHealthPromotionDetails.get(index).getMethod());
				}else if(columnIndex==3){
					return (View)getTextView("Location Coordinates: "+listHealthPromotionDetails.get(index).getLatitude());
				}else if(columnIndex==4){
					return (View)getTextView("Location Coordinates: "+listHealthPromotionDetails.get(index).getLongitude());
				}else if(columnIndex==5){
					return (View)getTextView("Month: "+listHealthPromotionDetails.get(index).getMonth());
				}else if(columnIndex==6){
					return (View)getTextView("Number of people who attended: "+listHealthPromotionDetails.get(index).getNumberAudience());
				}else if(columnIndex==7){
					return (View)getTextView("Remarks: "+listHealthPromotionDetails.get(index).getRemarks());
				}else if(columnIndex==8){
					return (View)getTextView("Target Audience: "+listHealthPromotionDetails.get(index).getTargetAudience());
				}else if (columnIndex==9){
					return (View)getTextView("Venue: "+listHealthPromotionDetails.get(index).getVenue());
					
				}else if (columnIndex==10){
				  	return (View)getImageView(listHealthPromotionDetails.get(index).getImage());
				}
				else {
					return (View)getTextView("-------");
				}
			
			
		}catch(Exception ex){
			Log.e("HealthPromotionDetailsAdapter.getNewView", ex.getMessage());
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
	private ImageView getImageView(String imagePath){
		ImageView imageView=new ImageView(mContext);
		imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,GridView.LayoutParams.WRAP_CONTENT));
		imageView.setPadding(8, 8, 8, 8);
		Bitmap b=BitmapFactory.decodeFile(imagePath);
		imageView.setImageBitmap(b);
		
		
		
		return imageView;
	}
	
}
