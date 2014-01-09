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
import com.ashesi.cs.mhealth.data.VaccinationReport.VaccinationReportRecord;
import com.ashesi.cs.mhealth.data.VaccinationReport;

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
			//OPD and Vaccination report use DummySectionFragment
			//0: OPD
			//1: Vaccination
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_report_opdsection).toUpperCase(l);
			case 1:
				return getString(R.string.title_report_vaccine).toUpperCase(l);
			case 2:
				return getString(R.string.title_report_next).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A fragment for OPD report display
	 */
	public static class DummySectionFragment extends Fragment implements OnClickListener, OnItemSelectedListener {
		
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		int sectionNumber=0;
		public static final String ARG_SECTION_NUMBER = "section_number";
		private String[] ageGroups={"Total","U 1","1-4","5-9","10-14","15-17","18-19","20-34","35-49","50-59","60-69","above 70"};
		private String[] vaccineAgeGroups={"Total","U 1","1-4","5-9","10-14","15-17","18-19","20-34","35-49","50-59","60-69","above 70"};
		private String[] months={"this month","whole year","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_report_opd,container, false);
			sectionNumber=getArguments().getInt(ARG_SECTION_NUMBER);
			displayReport(rootView); 
			return rootView;
		}
		private void displayReport(View rootView){
			
			
			fillAgeGroupSpinner(rootView);
			fillMonthSpinner(rootView);
			fillYearSpinner(rootView);
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			spinner.setOnItemSelectedListener(this);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			spinner.setOnItemSelectedListener(this);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerReportYear);
			spinner.setOnItemSelectedListener(this);
			
			loadData(rootView);
		}
		

		private void loadData(View rootView){
			TextView reportTitle = (TextView) rootView.findViewById(R.id.section_label);
			switch(sectionNumber){
			case 0:
				reportTitle.setText("OPD Cases");
				loadOPDReportData(rootView);
				break;
			case 1:
				reportTitle.setText("Vaccination report");
				loadVaccinationReportData(rootView);
				break;
			default:
				reportTitle.setText("OPD Cases");
				loadOPDReportData(rootView);
				break;
				
		}
		}
		private void loadOPDReportData(View rootView){
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=getSelectedYear();
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
		
		private void loadVaccinationReportData(View rootView){
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=getSelectedYear();
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Vaccination","","no cases"};
			VaccinationReport vaccinationReport=new VaccinationReport(this.getActivity().getApplicationContext());
			ArrayList<VaccinationReportRecord> listRecord=vaccinationReport.getMonthlyVaccinationReport(month,year,ageGroup,null);
			
					
			if(listRecord==null){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, headers);
				gridView.setAdapter(adapter);
			}else{
				ArrayList<String> list=vaccinationReport.getMonthlyVaccinationReportStringList(listRecord);
				list.add(0,headers[2]);
				list.add(0,headers[1]);
				list.add(0,headers[0]);
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, list);
				gridView.setAdapter(adapter);
			}
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
				
			}
		}

		@Override
		public void onItemSelected(AdapterView<?> adapter, View v, int startIndex, long length) {
			loadData(this.getView());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		private void fillAgeGroupSpinner(View rootView){
	
			ArrayAdapter<String> adapter;
			if(sectionNumber==0){
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,ageGroups);
			}else if(sectionNumber==1){
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,vaccineAgeGroups);
			}else{
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,ageGroups);
			}
		
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			spinner.setAdapter(adapter);
			
			
		}
		private void fillMonthSpinner(View rootView){
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,months);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
		}
		
		private void fillYearSpinner(View rootView){
			String[] strYears=new String[2];
			int year=Calendar.getInstance().get(Calendar.YEAR); //this year
			strYears[0]=Integer.toString(year);
			strYears[1]=Integer.toString((year-1));
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportYear);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,strYears);
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
			if(ageGroup<0){
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
		
		private int getSelectedYear(){
			View rootView=getView();
			if(rootView==null){
				return 0;
			}
			int year=Calendar.getInstance().get(Calendar.YEAR); //this year
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportYear);
			int n=spinner.getSelectedItemPosition();
			if(n<0){
				n=0;
				spinner.setSelection(n);
			}
			year=year-n;
			return year;
		}
	}

}
