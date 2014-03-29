package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;

public class ResourceListAdapter extends BaseAdapter{
	Context context;
	ArrayList<ResourceMaterial> resources;
	CHOs ch;
	Categories cat;
	String [] mediaList;
	
	public ResourceListAdapter(Context context, ArrayList<ResourceMaterial> resource){
		this.context = context;
		resources = resource;
		ch = new CHOs(context);
		cat = new Categories(context);
		mediaList = new String[]{"Picture", "Video", "Webpage"};
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return resources.size();
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
		ResourceMaterial r = resources.get(position);
	
		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.resource_row, null);
		}
		
		TextView type = (TextView)convertView.findViewById(R.id.res_type);
		type.setText(mediaList[r.getType()-1]);
		TextView category = (TextView)convertView.findViewById(R.id.category_);
		category.setText(cat.getCategory(r.getCatId()).getCategoryName());
		TextView resourceM = (TextView)convertView.findViewById(R.id.resource_mat);
		resourceM.setText(r.getDescription());
		return convertView;
	}

}

