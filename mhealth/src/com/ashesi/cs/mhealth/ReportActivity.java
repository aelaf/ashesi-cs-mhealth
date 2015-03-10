          package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.FamilyPlanningReport;
import com.ashesi.cs.mhealth.data.FamilyPlanningReport.FamilyPlanningReportRecord;
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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

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
	
	int mYear=0;
	int mMonth=0;
	int mAgeGroup=0;
	int mGender=0;

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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.itemDetailReport:

				Intent intent=new Intent(this,DetailReport.class);
				intent.putExtra("currentView", this.mViewPager.getCurrentItem());
				
				intent.putExtra("month", mMonth);
				intent.putExtra("year", mYear);
				intent.putExtra("ageGroup",mAgeGroup);
				intent.putExtra("gender",mGender);
				
				startActivity(intent);
				break;
		}
		return true;
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
			//0: OPD Cases
			//1: OPD Number
			//2: Vaccination
			//3: FamilyPlanning
			Fragment fragment = new ReportFragment();
			Bundle args = new Bundle();
			args.putInt(ReportFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_report_opdsection).toUpperCase(l);
			case 1:
				return "No Clients";
			case 2:
				return getString(R.string.title_report_vaccine).toUpperCase(l);
			case 3:
				return "Family Plan";//getString("Family Plan").toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A fragment for OPD report display
	 */
	public static class ReportFragment extends Fragment implements OnClickListener, OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
		
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		int sectionNumber=0;
		int mode=0;
		public static final String ARG_SECTION_NUMBER = "section_number";
		private String[] ageGroups={"Total","under 28 days", "1m-11m","1-4","5-9","10-14","15-17","18-19","20-34","35-49","50-59","60-69","above 70yr"};
		private String[] vaccineAgeGroups={"Total","under 12m","12-23","above 24m"};
		private String[] familyPlanAgeGroups={"Total","under 10yr","10-14","15-19","20-24","25-29","30-34","above 35yr"};
		
		private String[] months={"this month","whole year","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		private String[] genderOptions={"all","male","female"};

		public ReportFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_report_opd,container, false);
			sectionNumber=getArguments().getInt(ARG_SECTION_NUMBER);
			displayReport(rootView); 
			return rootView;
		}
		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			
			ReportActivity activity=(ReportActivity)this.getActivity();
			View rootView=getView();
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			spinner.setSelection(activity.mMonth);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerReportYear);
			spinner.setSelection(activity.mYear);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerGender);
			spinner.setSelection(activity.mGender);
			//spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			//spinner.setSelection(activity.mAgeGroup);
			super.onResume();
			
		}

		private void displayReport(View rootView){
			
			fillAgeGroupSpinner(rootView);
			fillMonthSpinner(rootView);
			fillYearSpinner(rootView);
			fillGenderSpinner(rootView);
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDReportAgeGroup);
			spinner.setOnItemSelectedListener(this);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerReportMonth);
			spinner.setOnItemSelectedListener(this);
			spinner=(Spinner)rootView.findViewById(R.id.spinnerReportYear);
			spinner.setOnItemSelectedListener(this);
			Button button=(Button)rootView.findViewById(R.id.buttonMode);
			button.setOnClickListener(this);
			
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
					loadReportTotalData(rootView);
					break;
				case 2:
					if(mode>=1){
						reportTitle.setText("Vaccination: No Community Members");
						loadVaccinationReportTotalData(rootView);
					}else{
						reportTitle.setText("Vaccination: No Records");
						loadVaccinationReportData(rootView);
					}

					break;
				case 3:
					if(mode>=1){
						reportTitle.setText("Family Planning: No Community Members");
						loadFamilyPlanningReportTotalData(rootView);
					}else{
						reportTitle.setText("Family Planning: No Records");
						loadFamilyPlanningReportData(rootView);
					}
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
			String strGender=getSelectedGender();
			
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Cases","Gender","No"};
			OPDCaseRecords opdCaseRecords=new OPDCaseRecords(this.getActivity().getApplicationContext());
			ArrayList<String> list;
			list=opdCaseRecords.getMontlyReport(month, year,ageGroup, strGender);
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
		
		private void loadReportTotalData(View rootView){
			TextView reportTitle = (TextView) rootView.findViewById(R.id.section_label);
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=getSelectedYear();
			
			
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Community","Gender","No Members"};
			OPDCaseRecords opdCaseRecords=new OPDCaseRecords(this.getActivity().getApplicationContext());
			ArrayList<String> list;
			if(mode==OPDCaseRecords.REPORT_MODE_ALL){
				reportTitle.setText("Number of community members");
			}else if(mode==OPDCaseRecords.REPORT_MODE_NEW_CLIENT_INSURED){
				reportTitle.setText("Number of new insured (NHIS expiry not considered)  community members");
			}else if(mode==OPDCaseRecords.REPORT_MODE_NEW_CLIENT_NON_INSURED){
				reportTitle.setText("Number of new non-insured (NHIS expiry not considered) community members");
			}else if(mode==OPDCaseRecords.REPORT_MODE_OLD_CLIENT_INSURED){
				reportTitle.setText("Number of old insured (NHIS expiry not considered) community members");
			}else if(mode==OPDCaseRecords.REPORT_MODE_OLD_CLIENT_NON_INSURED){
				reportTitle.setText("Number of old non-insured (NHIS expiry not considered) community members");
			}
			
			list=opdCaseRecords.getMontlyTotalsReport(month, year, ageGroup,mode);
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
			String strGender=getSelectedGender();
			
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Vaccination","","no cases"};
			VaccinationReport vaccinationReport=new VaccinationReport(this.getActivity().getApplicationContext());
			ArrayList<VaccinationReportRecord> listRecord=vaccinationReport.getMonthlyVaccinationReport(month,year,ageGroup,strGender);
			
					
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
		
		private void loadVaccinationReportTotalData(View rootView){
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=getSelectedYear();
			String strGender=getSelectedGender();
			
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Community","Gender","No Members"};
			VaccinationReport vaccinationReport=new VaccinationReport(this.getActivity().getApplicationContext());
			ArrayList<String> list=vaccinationReport.getMonthlyVaccinationReportTotals(month,year,ageGroup);
			
					
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
		
		private void loadFamilyPlanningReportData(View rootView){
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=getSelectedYear();
			String strGender=getSelectedGender();
			
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Service","Quantity","No Cases"};
			FamilyPlanningReport familyPlanningReport=new FamilyPlanningReport(this.getActivity().getApplicationContext());
			ArrayList<FamilyPlanningReportRecord> listRecord=familyPlanningReport.getMonthlyFamilyPlanningReport(month,year,ageGroup,null);
			
					
			if(listRecord==null){
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, headers);
				gridView.setAdapter(adapter);
			}else{
				ArrayList<String> list=familyPlanningReport.getMonthlyFamilyReportStringList(listRecord);
				list.add(0,headers[2]);
				list.add(0,headers[1]);
				list.add(0,headers[0]);
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, list);
				gridView.setAdapter(adapter);
			}
		}
		
		private void loadFamilyPlanningReportTotalData(View rootView){
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView1);
			
			int ageGroup=getSelectedAgeGroup();
			int month=getSelectedMonth();
			int year=getSelectedYear();
			String strGender=getSelectedGender();
			
			//GridView gridView=(GridView) rootView.findViewById(R.id.gridView1);
			String[] headers={"Community","Gender","No Mmembers"};
			FamilyPlanningReport familyPlanningReport=new FamilyPlanningReport(this.getActivity().getApplicationContext());
			ArrayList<String> list=familyPlanningReport.getMonthlyFamilyPlanningReportTotals(month, year, ageGroup);
			
					
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

		private void modeButtonClicked(){
			switch(sectionNumber){
				case 0:			//opd cases
					mode=0;
					break;
				case 1:			//opd cases community members count
					mode=mode+1;
					if(mode>4){	
						mode=0;
					}
					break;
				case 2:			//vaccine
					mode=mode+1;
					if(mode>1){		
						mode=0;
					}
					break;
				case 3:			//family planning
					mode=mode+1;
					if(mode>1){		
						mode=0;
					}
					break;
				default:
					mode=0;
					break;
					
			}
			
			loadData(this.getView());
		}
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.buttonMode:
					modeButtonClicked();
					break;
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
		
		@Override
		public void onCheckedChanged(CompoundButton v, boolean state) {
			// TODO Auto-generated method stub
			loadData(this.getView());
			
		}
		
		private void fillAgeGroupSpinner(View rootView){
	
			ArrayAdapter<String> adapter;
			if(sectionNumber==0){
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,ageGroups);
			}else if(sectionNumber==1){
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,ageGroups);
			}else if(sectionNumber==2){
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,vaccineAgeGroups);
			}else if(sectionNumber==3){
				adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,familyPlanAgeGroups);
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
			String[] strYears=new String[3];
			//TODO: year drop down should be field based on records available  
			int year=Calendar.getInstance().get(Calendar.YEAR); //this year
			strYears[0]=Integer.toString(year);
			strYears[1]=Integer.toString((year-1));
			strYears[2]=Integer.toString((year-2));
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerReportYear);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,strYears);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
		}
		
		private void fillGenderSpinner(View rootView){
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerGender);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1,genderOptions);
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
			ReportActivity activity=(ReportActivity)this.getActivity();
			activity.mAgeGroup=ageGroup;
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
			ReportActivity activity=(ReportActivity)this.getActivity();
			activity.mMonth=month;
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
			ReportActivity activity=(ReportActivity)this.getActivity();
			activity.mYear=n;	
			return year;
		}
	
		private String getSelectedGender(){
			View rootView=getView();
			if(rootView==null){
				return "all";
			}
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerGender);
			int n=spinner.getSelectedItemPosition();
			ReportActivity activity=(ReportActivity)this.getActivity();
			activity.mGender=n;
			return genderOptions[n];
		}

		
	}

}
