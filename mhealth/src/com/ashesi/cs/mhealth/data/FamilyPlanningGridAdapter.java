package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class FamilyPlanningGridAdapter extends BaseAdapter {

	private Context mContext;
	private int textColor;

	private ArrayList<FamilyPlanningRecord> records;
	
	public FamilyPlanningGridAdapter(Context context){
		textColor=R.color.black_text_color;
		this.mContext=context;
	}
	@Override
	public int getCount() {
		if(records==null){
			return 0;
		}
		return records.size()*4;
		
	}

	@Override
	public Object getItem(int position) {
		if(records==null){
			return null;
		}
		int index=position/4;
		return records.get(index);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if(records==null){
			return 0;
		}
		int index=position/4;
		return records.get(index).getId();
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int columnIndex=position%4;
		int index=position/4;
		
		if(columnIndex==0){
			return (View)getTextView(records.get(index).getServiceName());
		}else if(columnIndex==1){
			return (View)getTextView(records.get(index).getFormattedServiceDate());
		}else if(columnIndex==2){
			return (View)getTextView("----");
		}else if(columnIndex==3){
			return (View)getImageViewRemove();
		}
		
		return null;
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
	
	private ImageView getImageViewRemove(){
		ImageView image=new ImageView(mContext);
		image.setLayoutParams(new GridView.LayoutParams(30,30));
		image.setPadding(8,8,8,8);
		image.setImageResource(R.drawable.remove);
		
		return image;
		
	}
	
	public boolean updateNewRecord( FamilyPlanningRecord record){

		records.add(record);
		this.notifyDataSetChanged();
		return true;
	}
	
	public boolean updateReomve(int position){
		int index=position/4;
		records.remove(index);
		this.notifyDataSetChanged();
		return true;
	}
	
	public void setList(ArrayList<FamilyPlanningRecord> list){
		this.records=list;
	}
	
	public void setTextColor(int textColor){
		this.textColor=textColor;
	}

}
