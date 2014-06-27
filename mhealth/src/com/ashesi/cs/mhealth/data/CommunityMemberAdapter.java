package com.ashesi.cs.mhealth.data;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
		return getViewGroup(listCommunityMembers.get(position));//getTextView(listCommunityMembers.get(position));
	}
	
	private LinearLayout getViewGroup(CommunityMember obj){
		final int communityMemberId=obj.getId();
		
		//create a linear layout as container
		LinearLayout l=new LinearLayout(mContext);
		l.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,GridView.LayoutParams.WRAP_CONTENT));
		l.setPadding(0, 0, 0, 0);
		
		//
		TextView textView=new TextView(mContext);
		LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		p.weight=0.9F;
		textView.setLayoutParams(p);
		textView.setPadding(8, 8, 8, 8);
		
		
		if(listCommunityMembers==null){
			textView.setText("---");
			l.addView(textView);
			return l;
			
		}
		
		//String str=obj.getId() +" "+ obj.getFullname() +"\t"+ obj.getFormatedBirthdate() +"\t"+ obj.getCardNo()+"\t" +obj.getCommunity();
		String str=String.format("%-6d %-35s %-12s %-7s %s",obj.getId(), obj.getFullname(), obj.getFormatedBirthdate(),  obj.getCardNo(),obj.getCommunity());
		textView.setTextColor(mContext.getResources().getColor(R.color.text_color_black));
		textView.setText(str);
		
		p=new LinearLayout.LayoutParams(30,30);
		//birth date status
		ImageView image=new ImageView(mContext);
		image.setLayoutParams(p);
		image.setPadding(4,4,4,4);
		if(obj.IsBirthDateConfirmed()){
			image.setImageResource(R.drawable.bdate_ok);
		}else{
			image.setImageResource(R.drawable.bdate_caution);
			image.setOnClickListener(new OnClickListener(){
				int id=communityMemberId;	
				@Override
				public void onClick(View v) {
					ImageView iv=(ImageView)v;
					CommunityMembers communityMembers=new CommunityMembers(v.getContext());
					CommunityMember cm=communityMembers.getCommunityMember(id);
					if(!cm.IsBirthDateConfirmed()){
						communityMembers.confirmBirthDate(id);
						iv.setImageResource(R.drawable.bdate_ok);
					}else{
						communityMembers.unconfirmBirthDate(id);
						iv.setImageResource(R.drawable.bdate_caution);
					}

				}

			});
		}

		l.addView(image);
		//nhis status
		image=new ImageView(mContext);
		image.setLayoutParams(p);
		image.setPadding(4,4,4,4);
		
		if(obj.IsNHISExpiring(0)){
			image.setImageResource(R.drawable.nhis_expired);
		} else if(obj.IsNHISExpiring(3)){
			image.setImageResource(R.drawable.nhis_expiring);
		}else{
			image.setImageResource(R.drawable.nhis_ok);
		}
		l.addView(image);
		
		//gender
		image=new ImageView(mContext);
		image.setLayoutParams(p);
		image.setPadding(4,4,4,4);
		if(obj.getGender().equalsIgnoreCase("male")){
			image.setImageResource(R.drawable.male);
		}else{
			image.setImageResource(R.drawable.female);
		}
		l.addView(image);
		
		l.addView(textView);
			

		
			
		
		return l;
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
	
	private ImageView getImageView( ){
		ImageView image=new ImageView(mContext);
		image.setLayoutParams(new GridView.LayoutParams(30,30));
		image.setPadding(8,8,8,8);
		image.setImageResource(R.drawable.caution);
		
		return image;
		
	}
	
	public void setLsit(ArrayList<CommunityMember> list){
		this.listCommunityMembers=list;
	}
	
	

}
