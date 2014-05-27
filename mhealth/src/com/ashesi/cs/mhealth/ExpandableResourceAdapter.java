package com.ashesi.cs.mhealth;

import java.util.HashMap;
import java.util.List;

import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableResourceAdapter extends BaseExpandableListAdapter{
	private Context _context;
	private List<String> _listDataHeader;  //contains tags title
	private HashMap<String, List<ResourceMaterial>> _listDataChild;
	private Categories cat;
	private String [] mediaList;
	
	public ExpandableResourceAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<ResourceMaterial>> listChildData){
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		new CHOs(context);
		cat = new Categories(context);
		mediaList = new String[]{"Picture", "Video", "Webpage"};
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ResourceMaterial r = (ResourceMaterial)getChild(groupPosition, childPosition);
		
		if(r == null){
			return null;
		}
		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
        return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return _listDataHeader.size();
	}

	@Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
	
	@Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	/*
	 * This is referenced from http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
	 * */
}
