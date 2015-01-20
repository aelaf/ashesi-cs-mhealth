package com.ashesi.cs.mhealth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.ashesi.cs.mhealth.ReportActivity.ReportFragment;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.id;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;
import com.ashesi.cs.mhealth.data.R.string;
import com.ashesi.cs.mhealth.data.Vaccines;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

public class TasksActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasks);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tasks, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new TasksFragment();
			Bundle args = new Bundle();
			args.putInt(TasksFragment.TASK_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return "Vaccine: This Month";
			case 1:
				return "Vaccine: Next Month";
			case 2:
				return "Other Tasks";
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class TasksFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static String TASK_NUMBER="task_number";
		TextView textStatus;
		View rootView;
		int taskNumber=0;

		public TasksFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_tasks,
					container, false);
			textStatus = (TextView) rootView.findViewById(R.id.textStatus);
			taskNumber=getArguments().getInt(TASK_NUMBER);
			switch(taskNumber){
				case 0:
					showVaccinesNeededThisMonth();
					break;
				case 1:
					showVaccinesNeededNextMonth();
					break;
			}
			return rootView;
		}
		
		public void showVaccinesNeededNextMonth(){
			SimpleDateFormat format=new SimpleDateFormat("MMMM",Locale.UK);

			Calendar calendar=Calendar.getInstance();
			int past=calendar.get(Calendar.DAY_OF_MONTH);
			past=calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-past;
			calendar.add(Calendar.MONTH, 1); //go to next month
			int future=calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+past;
			
			Vaccines vaccines=new Vaccines(getActivity());
			ArrayList<String> list=vaccines.getVaccinesNeeded(past*(-1), future*(-1));
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.mhealth_simple_list_item,list);
			GridView grid= (GridView)rootView.findViewById(R.id.gridView);
			grid.setNumColumns(2);
			grid.setAdapter(adapter);
			textStatus.setText("The following vaccine are needed "+format.format(calendar.getTime()) + " based the indivituals scheduled for vaccination.");
		}
		
		public void showVaccinesNeededThisMonth(){
			SimpleDateFormat format=new SimpleDateFormat("MMMM",Locale.UK);

			Calendar calendar=Calendar.getInstance();
			int past=calendar.get(Calendar.DAY_OF_MONTH);
			int future=calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-past;
			
			Vaccines vaccines=new Vaccines(getActivity());
			ArrayList<String> list=vaccines.getVaccinesNeeded(past, future*(-1));
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.mhealth_simple_list_item,list);
			GridView grid= (GridView)rootView.findViewById(R.id.gridView);
			grid.setNumColumns(2);
			grid.setAdapter(adapter);
			textStatus.setText("The following vaccine are needed "+format.format(calendar.getTime()) + " based the indivituals scheduled for vaccination.");
		}
	}

}
