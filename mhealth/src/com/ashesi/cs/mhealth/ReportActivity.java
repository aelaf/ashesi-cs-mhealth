          package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.OPDCaseRecords;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.id;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;
import com.ashesi.cs.mhealth.data.R.string;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

public class ReportActivity extends FragmentActivity implements
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
		setContentView(R.layout.activity_report);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
		getMenuInflater().inflate(R.menu.report, menu);
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
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_report_opdsection).toUpperCase(l);
			case 1:
				return getString(R.string.title_report_other).toUpperCase(l);
			case 2:
				return getString(R.string.title_report_next).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment implements OnClickListener, OnItemSelectedListener {
		
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private String[] ageGroups={"Total","U 1","1-4","5-9","10-14","15-17","18-19","20-34","35-49","50-59","60-69","above 70"};
		private String[] months={"this month","this year","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_report_opd,container, false);
			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			int n=getArguments().getInt(ARG_SECTION_NUMBER);
			dummyTextView.setText(Integer.toString(n));
			
			if(n==1){
				opdCaseReport(rootView); //prepares the fragment for opd case report
			}
			
			return rootView;
		}
		private void opdCaseReport(View rootView){
			
			
			fillAgeGroupSpinner(rootView);
			fillMonthSpinner(rootView);
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			spinner.setOnItemSelectedListener(this);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			spinner.setOnItemSelectedListener(this);
			
			loadData(rootView);
		}
		
		private void loadData(View rootView){
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=Calendar.getInstance().get(Calendar.YEAR);
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"OPD Case","Gender","no cases"};
			OPDCaseRecords opdCaseRecords=new OPDCaseRecords(this.getActivity().getApplicationContext());
			ArrayList<String> list;
			list=opdCaseRecords.getMontlyReport(month, year,ageGroup, CommunityMember.MALE);
			if(list==null){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, headers);
				gridView.setAdapter(adapter);
			}else{
				list.add(0,headers[2]);
				list.add(0,headers[1]);
				list.add(0,headers[0]);
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, list);
				gridView.setAdapter(adapter);
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
				
			}
		}

		@Override
		public void onItemSelected(AdapterView<?> adapter, View v, int startIndex, long length) {
			// TODO Auto-generated method stub
			
			loadData(this.getView());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		private void fillAgeGroupSpinner(View rootView){
	
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,ageGroups);
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			spinner.setAdapter(adapter);
			
			
		}
		private void fillMonthSpinner(View rootView){
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,months);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
		}
		
		private int getSelectedAgeGroup(){
			View rootView=getView();
			if(rootView==null){
				return 0;	//all
			}
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			int ageGroup=spinner.getSelectedItemPosition();
			if(ageGroup<0 || ageGroup>=ageGroups.length){
				ageGroup=0;	
				spinner.setSelection(0);
			}
			return ageGroup;
		}
		
		private int getSelectedMonth(){
			View rootView=getView();
			if(rootView==null){
				return 0;
			}
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			int month=spinner.getSelectedItemPosition();
			if(month<0){
				month=0;
				spinner.setSelection(month);
			}
			
			return month;
		}
	}

}
