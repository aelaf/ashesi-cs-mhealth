package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CommunityMemberAdapter extends BaseAdapter {

	Context mContext;

	ArrayList<CommunityMember> listCommunityMembers;
	public CommunityMemberAdapter(Context context){
		mContext=context;
		listCommunityMembers=new ArrayList<CommunityMember>();

	}
	
	public CommunityMemberAdapter(Context context, ArrayList<CommunityMember> list){
		mContext=context;
		listCommunityMembers=list;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(listCommunityMembers==null){
			return 0;
		}
		return listCommunityMembers.size();
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listCommunityMembers.get(position);
		
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		if(listCommunityMembers==null){
			return -1;
		}
		
		return (long)listCommunityMembers.get(position).getId();	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getNewView(position);
	}
	
	private View getNewView(int position){
		return getTextView(listCommunityMembers.get(position));
	}
	private TextView getTextView(CommunityMember obj){
		TextView textView=new TextView(mContext);
		textView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,GridView.LayoutParams.WRAP_CONTENT));
		textView.setPadding(8, 8, 8, 8);
		
		if(listCommunityMembers==null){
			textView.setText("---");
			return textView;
		}
		
		
		textView.setTextColor(mContext.getResources().getColor(R.color.text_color_black));
		
		if(obj.IsNHISExpiring(0)){
			textView.setText(obj.toString() +"\tNHIS expired");
			textView.setBackgroundColor(mContext.getResources().getColor(R.color.background_error));
		} else if(obj.IsNHISExpiring(3)){
			textView.setText(obj.toString() +"\tNHIS expiring");
			textView.setBackgroundColor(mContext.getResources().getColor(R.color.background_warning));
		}else{
			textView.setText(obj.toString());
		}
		
		return textView;
	}
	
	public void setLsit(ArrayList<CommunityMember> list){
		this.listCommunityMembers=list;
	}

}
