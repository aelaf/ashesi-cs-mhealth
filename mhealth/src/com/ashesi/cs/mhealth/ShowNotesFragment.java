package com.ashesi.cs.mhealth;


import com.ashesi.cs.mhealth.data.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShowNotesFragment extends Fragment{
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View rootView = inflater.inflate(R.layout.activity_show_notes, container, false);
	         
	        return rootView;
	    }
}
