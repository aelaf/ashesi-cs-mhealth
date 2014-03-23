package com.ashesi.cs.mhealth.data;

import com.ashesi.cs.mhealth.QuestionsFragment;
import com.ashesi.cs.mhealth.ResourceFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new QuestionsFragment();
		case 1:
			// Games fragment activity
			return new ResourceFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
