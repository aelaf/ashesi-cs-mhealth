package com.ashesi.cs.mhealth.data;

import com.ashesi.cs.mhealth.QuestionsFragment;
import com.ashesi.cs.mhealth.ResourceFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	private QuestionsFragment q;
	private ResourceFragment r;
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		q = new QuestionsFragment();
		q.setHasOptionsMenu(true);
		
		r = new ResourceFragment();
		r.setHasOptionsMenu(true);
		
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return q;
		case 1:
			// Games fragment activity
			return r;
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
