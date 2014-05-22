package com.ashesi.cs.mhealth;

import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import java.util.ArrayList;

import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Question;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestionListAdapter extends BaseAdapter{
	
	Context context;
	ArrayList<Question> questionList;
	CHOs ch;
	Categories cat;
	
	public QuestionListAdapter(Context context, ArrayList<Question> questions){
		this.context = context;
		questionList = questions;
		ch = new CHOs(context);
		cat = new Categories(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return questionList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Question q = questionList.get(position);
	
		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.list_row, null);
		}
		TextView username = (TextView)convertView.findViewById(R.id.username);
		username.setText(ch.getCHO(q.getChoId()).getFullname()  + " - " + cat.getCategory(q.getCategoryId()).getCategoryName());
		TextView date = (TextView)convertView.findViewById(R.id.q_date);
		date.setText(q.getDate());
		TextView question = (TextView)convertView.findViewById(R.id.resource_material);
		question.setText(q.getContent());
		ImageView img = (ImageView)convertView.findViewById(R.id.checkImage);
		int result = q.getRecState();
		if( result == 1){
			img.setVisibility(View.VISIBLE);
			img.setImageResource(R.drawable.checkmarkk);
		}else if(result == 2){
			img.setVisibility(View.VISIBLE);
			img.setImageResource(R.drawable.doublecheckmark);
		}else{
			img.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}
