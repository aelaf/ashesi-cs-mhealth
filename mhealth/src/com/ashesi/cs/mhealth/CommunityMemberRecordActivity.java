package com.ashesi.cs.mhealth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.OPDCase;
import com.ashesi.cs.mhealth.data.OPDCaseRecord;
import com.ashesi.cs.mhealth.data.OPDCaseRecords;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.Vaccine;
import com.ashesi.cs.mhealth.data.VaccineGridAdapter;
import com.ashesi.cs.mhealth.data.VaccineRecord;
import com.ashesi.cs.mhealth.data.VaccineRecords;
import com.ashesi.cs.mhealth.data.Vaccines;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

public class CommunityMemberRecordActivity extends FragmentActivity implements ActionBar.TabListener {

	public static final int STATE_RECORD=0;
	public static final int STATE_NEW_MEMBER=1;
	public static final int STATE_EDIT_MEMBER=2;
	
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
	
	int communityMemberId=0;
	int communityId;
	int state=0;
	int choId=0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_member_record);

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
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		Intent intent=getIntent();
		communityId=intent.getIntExtra("communityId",0);
		choId=intent.getIntExtra("choId",0);
		state=intent.getIntExtra("state", 1);
		communityMemberId=intent.getIntExtra("id",0);
				
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.community_member_record, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,FragmentTransaction fragmentTransaction) {
	}

	
	
	public int getCommunityId(){
		return this.communityId;
	}
	/**
	 * use this method to set community id if it changes in the fragments
	 * @param id
	 */
	public void setCommunityId(int id){
		this.communityId=id;
	}
	/**
	 * use this method to set state if it changes in the fragments
	 * @param id
	 */
	public void setState(int state){
		this.state=state;
	}
	/**
	 * get state as stored in the activity
	 * @return
	 */
	public int getState(){
		return this.state;
	}
	
	/**
	 * use this method to set community member id if it changes in the fragments
	 * @param id
	 */
	public void setCommunityMemberId(int id){
		this.communityMemberId=id;
	}
	/**
	 * get the community member stored in the activity
	 * @return
	 */
	public int getCommunityMemberId(){
		return this.communityMemberId;
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
			
			Fragment fragment;
			if(position==0 ){
				fragment= new MainSectionFragment();
			}else if(position==1){
				fragment=new OtherFragment();
			}else if(position==2){
				fragment=new VaccineFragment();
			}else if(position==3){
				fragment=new FamilyPlanFragment();
			}else{
				fragment= new MainSectionFragment(); //default
			}
			
			Bundle args = new Bundle();
			args.putInt("state",state);
			args.putInt("id", communityMemberId);
			args.putInt("communityId", communityId);
			args.putInt("choId", choId);
			
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_cm_record_main).toUpperCase(l);
				case 1:
					return getString(R.string.title_cm_record_other).toUpperCase(l);
				case 2:
					return getString(R.string.title_cm_record_vaccine).toUpperCase(l);
				case 3:
					return "Family Planning";
			}
			return null;
		}
	}

	/**
	 * A main fragment representing a section the main view client
	 */
	public static class MainSectionFragment extends Fragment implements OnDateChangedListener,  OnClickListener, OnItemSelectedListener,OnFocusChangeListener {
		
		ArrayList<Community> listCommunities;
		
		private int state=0;
		private int communityMemberId=0;
		
		private EditText editAge;
		private DatePicker dpBirthdate;
		private EditText editFullname;
		private EditText editCardNo;
		private EditText editNHISId;
		private DatePicker dpNHISExpiryDate;
		private Spinner spinnerCommunities;
		private Spinner spinnerAgeUnit;
		private CheckBox useAge;
		private TextView textStatus;

		private CHO currentCHO;
		private int communityId;
		private boolean birthDateNotConfirmed=true;
		
		boolean disableDateUpdate=false;
		
		private View rootView;
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public MainSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_community_members_record, container,false);
			
			//TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			//dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			
			communityMemberId=getArguments().getInt("id");
			state=getArguments().getInt("state");
			communityId=getArguments().getInt("communityId");
			this.rootView=rootView;
			create();
			return rootView;
		}
		
		@Override
		public void onResume(){
			super.onResume();
			//if a new community is added get the id 
			state=this.getState();
			communityMemberId=this.getCommunityMemberId();
			
			
		}
		
		@Override
		public void onFocusChange(View v, boolean focus) {
			if(v.getId()==R.id.editCommunityMemberAge && !focus){
				computeBirthdate();
			}
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View v, int arg2, long arg3) {
			if(arg0.getId()==R.id.spinnerCommunityMemberAgeUnits){
				computeBirthdate();
			}
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClick(View v) {
			
			switch(v.getId()){
				case R.id.buttonAddCommunityMember:
					buttonClicked();
					break;
				case R.id.checkUseAge:
					useAgeClicked();
					break;
					
			}
		}
		
		@Override
		public void onDateChanged(DatePicker dp, int year, int month, int day) {
			if(!useAge.isChecked()){
				computeAge();
				this.birthDateConfirmed();	//the birth date set is assumed correct
			}
		}
		
		public CommunityMemberRecordActivity getHostActivity(){
			return(CommunityMemberRecordActivity)getActivity();
		}
		
		public int getState(){
			CommunityMemberRecordActivity activity=(CommunityMemberRecordActivity)getActivity();
			return activity.getState();
		}
			
		public int getCommunityMemberId(){
			CommunityMemberRecordActivity activity=(CommunityMemberRecordActivity)getActivity();
			return activity.getCommunityMemberId();
		}
		
		public void setState(int state){
			CommunityMemberRecordActivity activity=(CommunityMemberRecordActivity)getActivity();
			activity.setState(state);
		}
		
		public void setCommunityMemberId(int id){
			CommunityMemberRecordActivity activity=(CommunityMemberRecordActivity)getActivity();
			activity.setCommunityMemberId(id);
			
		}
		
		public void setActivityCommunityId(int id){
			CommunityMemberRecordActivity activity=(CommunityMemberRecordActivity)getActivity();
			activity.setCommunityId(id);
		}
		
		public int getActivityCommunityId(){
			CommunityMemberRecordActivity activity=(CommunityMemberRecordActivity)getActivity();
			return activity.getCommunityId();
		}
		
		public void create(){
			
			textStatus=(TextView)rootView.findViewById(R.id.textAddCommunityStatus);
			
			int choId=getArguments().getInt("choId");
			getCurrentCHO(choId);
			
			Button button=(Button)rootView.findViewById(R.id.buttonAddCommunityMember);
			button.setOnClickListener(this);
			
			useAge=(CheckBox)rootView.findViewById(R.id.checkUseAge);
			useAge.setOnClickListener(this);
			
			editFullname=(EditText)rootView.findViewById(R.id.editFullname);
			dpBirthdate=(DatePicker)rootView.findViewById(R.id.dpBirthDate);
			editCardNo=(EditText)rootView.findViewById(R.id.editCardNo);
			editNHISId=(EditText)rootView.findViewById(R.id.editNHISId);
			dpNHISExpiryDate=(DatePicker)rootView.findViewById(R.id.dpNhisExpiryDate);
			spinnerCommunities=(Spinner)rootView.findViewById(R.id.spinnerCommunities);
			spinnerAgeUnit=(Spinner)rootView.findViewById(R.id.spinnerCommunityMemberAgeUnits);
			editAge=(EditText)rootView.findViewById(R.id.editCommunityMemberAge);
			
			editAge.setOnFocusChangeListener(this);
			
			//initialize datepicker
			Calendar c=Calendar.getInstance();
			dpBirthdate.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),this);
			fillAgeUnitSpinner();
			
			
			if(state==STATE_NEW_MEMBER){
				fillCommunitiesSpinner(communityId);
				this.birthDateNotConfirmed();
			}else{
				//load client information
				CommunityMembers members=new CommunityMembers(getActivity().getApplicationContext());
				CommunityMember cm=members.getCommunityMember(communityMemberId);
				editFullname.setText(cm.getFullname());
				//if the birthdate is not confirmed, the user can confirm it;
				if(!cm.IsBirthDateConfirmed()){
					this.birthDateNotConfirmed();
				}else{
					this.birthDateConfirmed();
				}
				setBirthdate(cm.getBirthdateDate());
				computeAge();
				
				editCardNo.setText(cm.getCardNo());
				editNHISId.setText(cm.getNHISId());
				setNHISExpiryDate(cm.getNHISExpiryDateDate());
				setGender(cm.getGender());
				fillCommunitiesSpinner(cm.getCommunityID());
				
				
			}

			stateAction();
			
		}
		
		public void useAgeClicked(){
			if(state!=STATE_EDIT_MEMBER && state!=STATE_NEW_MEMBER){
				return;
			}
			if(useAge.isChecked()){
				confirmUseAge();
			}else{
				editAge.setEnabled(false);
				spinnerAgeUnit.setEnabled(false);
				dpBirthdate.setEnabled(true);
			}
		}
		
		public void useAge(){
			//alert user that using age is not advisable
			editAge.setEnabled(true);
			spinnerAgeUnit.setEnabled(true);
			dpBirthdate.setEnabled(false);
		}
		
		private boolean confirmUseAge(){
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
			builder.setMessage(R.string.confirmUseAge);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   useAge();
			        	   dialog.dismiss();
			           }
			       });
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   useAge.setChecked(false);
			        	   dialog.dismiss();
			           }
			       });
			
			AlertDialog dialog = builder.create();
			dialog.show();
			return true;
		}
		
		public void editMember(){
			state=STATE_EDIT_MEMBER;
			stateAction();
			setState(state);
		}
		
		protected void fillAgeUnitSpinner(){
			String units[]={"years","months"};
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,units);
			spinnerAgeUnit.setAdapter(adapter);
			
			spinnerAgeUnit.setOnItemSelectedListener(this);
			
		}

		public boolean fillCommunitiesSpinner(int selectedId){
			
			Communities communities=new Communities(getActivity().getApplicationContext());
			listCommunities=communities.getCommunties(currentCHO.getSubdistrictId());
			ArrayAdapter<Community> adapter=new ArrayAdapter<Community>(getActivity(),android.R.layout.simple_dropdown_item_1line,listCommunities);
			spinnerCommunities.setAdapter(adapter);
			for(int i=0;i<listCommunities.size();i++){
				Community obj=listCommunities.get(i);
				if(obj.getId()==selectedId){
					spinnerCommunities.setSelection(i);
					break;
				}
			}
			
			return true;
			
			
		}
		
		public void computeBirthdate(){
			
			try
			{
				java.util.Calendar c=java.util.Calendar.getInstance();
				//if the user has decided to use age, then use age to get birth date 
				if(!useAge.isChecked()){ 
					return;
				}
				//if birth date is computed from age, then it is considered not confirmed
				birthDateNotConfirmed();
				String temp=editAge.getText().toString();
				if(temp.isEmpty()){
					return;
				}

				int age=Integer.parseInt(temp);
				int unit=spinnerAgeUnit.getSelectedItemPosition();
				if(unit==1){
					c.add(java.util.Calendar.MONTH,(-1)*age);
					c.set(Calendar.DAY_OF_MONTH, 1);
				}else{
					c.add(java.util.Calendar.YEAR,(-1)*age);
					c.set(Calendar.DAY_OF_MONTH, 1);
					c.set(Calendar.MONTH, Calendar.JANUARY);
				}

				setBirthdate(c.getTime());
				
			}catch(Exception ex){
				return;
			}
			
		}

		public void computeAge(){
						
			java.util.Date date=getBirthdate();
			if(date==null){
				editAge.setText("");
				return;
			}
			
			Calendar c=Calendar.getInstance();
			

			long thisTime=c.getTimeInMillis();
			c.setTime(date);
			long birthTime=c.getTimeInMillis();
			long ym= 31622400000L; 
			int age=(int)((thisTime-birthTime)/ym); 

			
			spinnerAgeUnit.setSelection(0);	//year
			if(age<1){
				ym= 2592000000L; 
				age=(int)((thisTime-birthTime)/ym);
				spinnerAgeUnit.setSelection(1);	//month
			}

			editAge.setText(Integer.toString(age));
			
		}
		
		public void buttonClicked(){
			if(state==STATE_NEW_MEMBER || state==STATE_EDIT_MEMBER){
				addCommunityMember();
			}else if(state==STATE_RECORD){
				editMember();
			}else{
				
			}
		}
		
		/**
		 * This method is used to save community member personal information after add or edit
		 * 
		 */
		public void addCommunityMember(){
			//get fullname
			EditText editFullname=(EditText)rootView.findViewById(R.id.editFullname);
			String name=editFullname.getText().toString();
			
			if(name.length()<=0){
				showError("enter the patients name");
				return;
			}
			
			if(useAge.isChecked()){
				//if age is used then, the birthdate is not confirmed 
				computeBirthdate();
			}
			//get birthdate
			java.util.Date birthdate=getBirthdate();
			if(birthdate==null){
				showError("enter the patients birth date");
				return;
			}
			communityId=getCommunityId();
			if(communityId==0){
				showError("select the patients community number");
				return;
			}
			String gender=getSelectedGender();
			if(gender==null){
				showError("select the patients gender");
				return;
			}

			EditText editCardNo=(EditText)rootView.findViewById(R.id.editCardNo);	
			String cardNo=editCardNo.getText().toString();
			if(cardNo.length()<=0){
				cardNo="none";
			}
			String nhisId=editNHISId.getText().toString();
			if(nhisId.length()<=0){
				nhisId="none";
			}
			
			java.util.Date nhisExpiryDate=getNHISExpiryDate();
			if(nhisExpiryDate==null){
				nhisExpiryDate=Calendar.getInstance().getTime();
			}
			
			CommunityMembers members=new CommunityMembers(getActivity().getApplicationContext());
			
			boolean confirmed=(!birthDateNotConfirmed);
			
			if(state==STATE_NEW_MEMBER){
				int id=members.addCommunityMember(0, communityId, name, birthdate,confirmed, gender,cardNo,nhisId,nhisExpiryDate);
				if(id!=0){
					communityMemberId=id;
					setCommunityMemberId(id); //make the new id available to the other fragments through the activity
					state=STATE_RECORD;
					stateAction();	
					setState(state);	//set activity state
					setActivityCommunityId(communityId);	//make the new id available to the other fragments through the activity
					
				}
				
			}else if(state==STATE_EDIT_MEMBER){
				//isBirthDateConfirmed tells us that when editing age was not used;
				//birthDateNotConfimed if true tells us that original birth date was not confirmed and there was no attempt to confirm
				//if false, it tells the original was not confirmed 
				
				int id=members.updateCommunityMember(communityMemberId, communityId, name, birthdate,confirmed, gender,cardNo,nhisId,nhisExpiryDate);
				if(id!=0){
					state=STATE_RECORD;
					stateAction();
					setState(state);	//set activity state
					setActivityCommunityId(communityId);	//make the new id available to the other fragments through the activity
				}
			}else{
				//??
			}
			
			computeAge();

		}
		
		protected String getSelectedGender(){
			
			RadioButton radioMale=(RadioButton)rootView.findViewById(R.id.radioCommunityMemberFemale);
			RadioButton radioFemale=(RadioButton)rootView.findViewById(R.id.radioCommunityMemberRecordMale);
			
			if(radioFemale.isChecked()){
				return CommunityMember.FEMALE;
			}else if(radioMale.isChecked()){
				return CommunityMember.MALE;
			}else{
				return null;
			}
			
		}
		
		protected int getCommunityId(){

			int index=spinnerCommunities.getSelectedItemPosition();
			if(index<0){
				return 0;
			}
			Community community=listCommunities.get(index);
			return community.getId();
		}
		
		protected void setBirthdate(java.util.Date date){
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			dpBirthdate.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			
		}
		
		protected java.util.Date getBirthdate(){
			
			try
			{
				int year=dpBirthdate.getYear();
				int month=dpBirthdate.getMonth();
				int day=dpBirthdate.getDayOfMonth();
				
				Calendar c=Calendar.getInstance();
				c.set(year, month, day);
				return c.getTime();

			}
			catch(Exception ex){
				return null;
			}
		}
		
		protected void stateAction(){
			

			Button button=(Button)rootView.findViewById(R.id.buttonAddCommunityMember);
			RadioButton radioMale=(RadioButton)rootView.findViewById(R.id.radioCommunityMemberRecordMale);
			RadioButton radioFemale=(RadioButton)rootView.findViewById(R.id.radioCommunityMemberFemale);
			editAge.setEnabled(false);
			spinnerAgeUnit.setEnabled(false);
			useAge.setChecked(false);
			
			switch(state){
				case STATE_RECORD:
					//existing member, record
					editFullname.setEnabled(false);
					dpBirthdate.setEnabled(false);
					spinnerCommunities.setEnabled(false);
					radioMale.setEnabled(false);
					radioFemale.setEnabled(false);
					editCardNo.setEnabled(false);
					editNHISId.setEnabled(false);
					dpNHISExpiryDate.setEnabled(false);
					button.setText(R.string.editClient);
					useAge.setEnabled(true);
					break;
				case STATE_NEW_MEMBER:
					//new member
					//client personal record fields
					editFullname.setEnabled(true);
					dpBirthdate.setEnabled(true);
					spinnerCommunities.setEnabled(true);
					radioMale.setEnabled(true);
					radioFemale.setEnabled(true);
					editCardNo.setEnabled(true);
					editNHISId.setEnabled(true);
					dpNHISExpiryDate.setEnabled(true);
					button.setText(R.string.addCommunityMember);
					useAge.setEnabled(true);
					break;
				case STATE_EDIT_MEMBER:
					//edit
					//client personal record fields
					editFullname.setEnabled(true);
					dpBirthdate.setEnabled(true);
					spinnerCommunities.setEnabled(true);
					radioMale.setEnabled(true);
					radioFemale.setEnabled(true);
					editCardNo.setEnabled(true);
					editNHISId.setEnabled(true);
					dpNHISExpiryDate.setEnabled(true);
					button.setText(R.string.saveCommunityMember);
					useAge.setEnabled(true);
					break;
					
			}
		}

		public void setGender(String gender){
			
			RadioButton radioMale=(RadioButton)rootView.findViewById(R.id.radioCommunityMemberRecordMale);
			RadioButton radioFemale=(RadioButton)rootView.findViewById(R.id.radioCommunityMemberFemale);
			if(gender.equals(CommunityMember.MALE)){
				radioMale.setChecked(true);
				radioFemale.setChecked(false);
			}else{
				radioMale.setChecked(false);
				radioFemale.setChecked(true);
			}
		}

		protected void getCurrentCHO(int choId){
		
			CHOs chos=new CHOs(getActivity().getApplicationContext());
			currentCHO=chos.getCHO(choId);
		}

		protected void setNHISExpiryDate(java.util.Date date){
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			dpNHISExpiryDate.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
			
		}
		
		protected java.util.Date getNHISExpiryDate(){

			try
			{
				Calendar c=Calendar.getInstance();
				c.set(dpNHISExpiryDate.getYear(), dpNHISExpiryDate.getMonth(),dpNHISExpiryDate.getDayOfMonth());
				
				return c.getTime();
			}
			catch(Exception ex){
				return null;
			}
			
			
		}

		protected void showError(String msg){
			textStatus.setText(msg);
			textStatus.setTextColor(rootView.getResources().getColor(R.color.text_color_error));
		}
		
		protected void showStatus(String msg){
			textStatus.setText(msg);
			textStatus.setTextColor(rootView.getResources().getColor(R.color.text_color_black));
		}

		protected void birthDateConfirmed(){
			this.dpBirthdate.setBackgroundColor(this.getResources().getColor(R.color.Green));
			this.birthDateNotConfirmed=false;
		}
		
		protected void birthDateNotConfirmed(){
			this.dpBirthdate.setBackgroundColor(this.getResources().getColor(R.color.Red));
			this.birthDateNotConfirmed=true;
		}

	}
	
	public static class OtherFragment extends Fragment implements OnClickListener, OnItemSelectedListener{
		
		ArrayList<OPDCase> listOPDCases;
		String[] opdCaseCategories={"ALL","CI","CNI","NCD","MHC","SC","OGC","RETD","IO","REF","OTH"}; 
		
		View rootView;
		int communityMemberId=0;
		public OtherFragment(){
			
		}
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_community_member_record_other, container,false);
			
			
			ListView listView=(ListView)rootView.findViewById(R.id.list);
			this.registerForContextMenu(listView);
			Button button=(Button)rootView.findViewById(R.id.buttonCommunityMemberRecordOPDCase);
			button.setOnClickListener(this);
			
			communityMemberId=getArguments().getInt("id");
			
			this.rootView=rootView;
			getListOfOPDCases();
			this.fillOPDCaseCategoriesSpinner();
			this.fillOPDCaseSpinner();
			return rootView;
		}

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.buttonCommunityMemberRecordOPDCase){
				recordOPDCase();
			}
		}
		
		@Override
		public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo){
			super.onCreateContextMenu(menu, v, menuInfo);
			getActivity().getMenuInflater().inflate(R.menu.menu_community_members_record_context, menu);
		}
		
		@Override
		public boolean onContextItemSelected(MenuItem item) {
			switch(item.getItemId()){
				case R.id.itemRemoveRecord:
					removeRecord();
					break;
			
					
			}
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch(item.getItemId()){
			case R.id.itemRemoveRecord:
				removeRecord();
				break;
	
			}
			return true;
		
		}
		
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			fillOPDCaseSpinner();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onResume(){
			super.onResume();
			//if a new community is added get the id 
			CommunityMemberRecordActivity a=(CommunityMemberRecordActivity)this.getActivity();
			communityMemberId=a.getCommunityMemberId();
		}

		public boolean fillOPDCaseSpinner(){
			
			Spinner spinnerCategories=(Spinner)rootView.findViewById(R.id.spinnerOPDCaseCategories);
			int opdCaseCategory=spinnerCategories.getSelectedItemPosition();
			OPDCases opdCases=new OPDCases(getActivity().getApplicationContext());
			listOPDCases=opdCases.getAllOPDCases(opdCaseCategory);
			listOPDCases.add(0, new OPDCase(0,"select one"));
			ArrayAdapter<OPDCase> adapter=new ArrayAdapter<OPDCase>(getActivity(),android.R.layout.simple_dropdown_item_1line,listOPDCases);
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDCases);
			spinner.setAdapter(adapter);
			return true;
		}
		
		public boolean fillOPDCaseCategoriesSpinner(){
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDCaseCategories);
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,opdCaseCategories);
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(this);
			return true;
		}

		public boolean removeRecord(){
			ListView listView=(ListView)rootView.findViewById(R.id.list);
			//OPDCaseRecord record=(OPDCaseRecord)listView.getSelectedItem();
			int index=listView.getCheckedItemPosition();
			if(index<0){
				return false;
			}
			OPDCaseRecord record=(OPDCaseRecord)listView.getItemAtPosition(index);
			if(record==null){
				return false;
			}
			
			OPDCaseRecords opdCaseRecords=new OPDCaseRecords(getActivity().getApplicationContext());
			if(!opdCaseRecords.removeOPDRecord(record.getRecNo())){
				return false;
			}
			getListOfOPDCases();
			//call remove
			return true;
		}
		
		protected void recordOPDCase(){
			
			if(communityMemberId==0){
				CommunityMemberRecordActivity a=(CommunityMemberRecordActivity)this.getActivity();
				communityMemberId=a.getCommunityMemberId();
				if(communityMemberId==0){
					return;
				}
				
			}
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerOPDCases);
			if(spinner.getSelectedItemPosition()<=0){
				return;
			}
			OPDCase opdCase=(OPDCase)spinner.getSelectedItem();
			if(opdCase.getID()==0){
				return;
			}
			CommunityMembers members=new CommunityMembers(getActivity().getApplicationContext());
			Calendar calendar=Calendar.getInstance();
			
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			String todaysDate=dateFormat.format(calendar.getTime());
			
			CheckBox cbLab=(CheckBox)rootView.findViewById(R.id.cbLab);
			
			members.recordOPDCase(communityMemberId, opdCase.getID(),todaysDate,1,cbLab.isChecked());
			//ArrayAdapter<OPDCase> adapter=(ArrayAdapter<OPDCase>)spinner.getAdapter();
			getListOfOPDCases();
			
		}
		
		protected void getListOfOPDCases(){
			if(communityMemberId<=0){
				return;
			}
			OPDCaseRecords opdCaseRecords=new OPDCaseRecords(getActivity().getApplicationContext());
			if(!opdCaseRecords.getCommunityMemberOPDCases(communityMemberId)){
				return;
			}
			ArrayList<OPDCaseRecord> list=opdCaseRecords.getArrayList();
			ListView listView=(ListView)rootView.findViewById(R.id.list);
			ArrayAdapter<OPDCaseRecord> adapter=new ArrayAdapter<OPDCaseRecord>(getActivity(),android.R.layout.simple_list_item_single_choice,list); 
			listView.setAdapter(adapter);
			
			return;
		}
		
		

	}
	
	public static class VaccineFragment extends Fragment implements OnClickListener, OnItemSelectedListener{
		
		
		VaccineGridAdapter adapter; 
		
		View rootView;
		int communityMemberId=0;
		
		public VaccineFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_community_member_record_vaccine, container,false);
			this.rootView=rootView;
			
			communityMemberId=getArguments().getInt("id");
			
			fillVaccineSpinner();
			showSchedule();
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView);
			gridView.setOnItemClickListener(new OnItemClickListener() {
		        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		            //Toast.makeText(getActivity().getApplicationContext(), "Position=" + position, Toast.LENGTH_SHORT).show();
		            itemClicked(parent,v,position,id);
		        }
		    });
			
			RadioGroup radioGroup=(RadioGroup)rootView.findViewById(R.id.radioGroup1);
			radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(RadioGroup raidoGroup, int checkedId) {
					// TODO Auto-generated method stub
					showSchedule();
				}
			});
			
			Button buttonAddVaccine=(Button)rootView.findViewById(R.id.buttonAddVaccine);
			buttonAddVaccine.setOnClickListener(this);
			return rootView;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.buttonAddVaccine:
					showDatePicker(-1);	//add vaccines to record
					break;
			}
		}
		
		@Override
		public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo){
			super.onCreateContextMenu(menu, v, menuInfo);
			//getActivity().getMenuInflater().inflate(R.menu.menu_community_members_record_context, menu);
		}
		
		@Override
		public boolean onContextItemSelected(MenuItem item) {
			
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			
			return true;
		
		}
		
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onResume(){
			super.onResume();
			CommunityMemberRecordActivity a=(CommunityMemberRecordActivity)this.getActivity();
			communityMemberId=a.getCommunityMemberId();
		}
				
		private void fillVaccineSpinner(){
			
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerRecordVaccinVaccines);
			Vaccines vaccines=new Vaccines(getActivity().getApplicationContext());
			ArrayList<Vaccine> listVaccines=vaccines.getUnscheduledVaccines();
			ArrayAdapter<Vaccine> adapter=new ArrayAdapter<Vaccine>(getActivity(), android.R.layout.simple_dropdown_item_1line,listVaccines);
			spinner.setAdapter(adapter);
			
		
		}
		
		private void showSchedule(){
			if(communityMemberId==0){
				return;
			}
			
			
			GridView gridView=(GridView)rootView.findViewById(R.id.gridView);

			CommunityMembers communityMembers=new CommunityMembers(getActivity().getApplicationContext());
			CommunityMember cm=communityMembers.getCommunityMember(communityMemberId);
			
			Vaccines vaccines=new Vaccines(getActivity().getApplicationContext());
			ArrayList<Vaccine> listVaccines=vaccines.getScheduledVaccines();
	
			VaccineRecords vaccineRecords=new VaccineRecords(getActivity().getApplicationContext());
			ArrayList<VaccineRecord>listVaccineRecords=vaccineRecords.getVaccineRecords(communityMemberId);
			adapter=new VaccineGridAdapter(getActivity().getApplicationContext());
			
			adapter.setList(listVaccines,cm,listVaccineRecords,getSelectedViewMode());
			adapter.setTextColor(getResources().getColor(R.color.black_text_color));
			gridView.setAdapter(adapter);
	
		}
		
		private void itemClicked(AdapterView<?> parent, View v, int position, long id){
			if(communityMemberId<=0){
				//if community member is not know, there is nothing to do
				return;
			}
			int columnIndex=position%4;
			if(columnIndex!=3){ //if the click is not on 4th column there is nothing to do
				return;
			}
			if(adapter.getMode()!=VaccineGridAdapter.SCHEDULE_LIST){
				removeVaccineRecord(position);
				return;
			}else{

				if(!adapter.getStatus(position)){	//vaccination is not recored, hence record
					showDatePicker(position);
				}else{
					removeVaccineRecord(position);	//its recored, so remove it. 
				}

			}
		}
		
		private void showDatePicker(int position){
			DatePickerFragment datePicker=new DatePickerFragment();
			datePicker.vf=this;
			datePicker.position=position;
			if(adapter.getMode()==VaccineGridAdapter.SCHEDULE_LIST){
			
				Vaccine vaccine=adapter.getVaccine(position);
				CommunityMembers communityMembers=new CommunityMembers(getActivity().getApplicationContext());
				CommunityMember cm=communityMembers.getCommunityMember(communityMemberId);
				java.util.Date date=vaccine.getWhenToVaccine(cm.getBirthdateDate());
				datePicker.setDate(date);
				
			}
			
			datePicker.show(this.getActivity().getSupportFragmentManager(), "datePicker");
			
			
		}
		
		private void recordVaccine(int position,String date){
			if(communityMemberId<=0){
				//if community member is not know, there is nothing to do
				return;
			}
			Vaccine vaccine;
			if(adapter.getMode()==VaccineGridAdapter.RECORD_LIST){
				vaccine=getSelectedVaccine();
			}else{
				vaccine=adapter.getVaccine(position);
			}
				
			int vaccineId=vaccine.getId();
			VaccineRecords vaccineRecords=new VaccineRecords(getActivity().getApplicationContext());
			//check if its already recorded
			VaccineRecord record=vaccineRecords.getVaccineRecord(communityMemberId, vaccineId);
			if(record!=null && vaccine.getVaccineSchedule()>=0){
				//Already recorded
				return;
			}
			//get todays date
		
//			DatePickerFragment datePicker=new DatePickerFragment();
//			datePicker.show(this.getActivity().getSupportFragmentManager(), "datePicker");
//			Calendar c=Calendar.getInstance();
//			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
//			
//			String todaysDate=dateFormat.format(c.getTime());//datePicker.getDateString();
			//record
			if(!vaccineRecords.addRecord(communityMemberId,vaccineId,date)){
				return;
			}
			//update adapter
			record=vaccineRecords.getVaccineRecord(communityMemberId,vaccineId);
			adapter.updateNewRecord(position, record);
		
			
		}
		
		private void removeVaccineRecord(int position){
			if(communityMemberId<=0){
				//if community member is not know, there is nothing to do
				return;
			}
			VaccineRecord record;
			VaccineRecords vaccineRecords=new VaccineRecords(getActivity().getApplicationContext());
			if(adapter.getMode()==VaccineGridAdapter.RECORD_LIST){
				record=adapter.getVaccineRecord(position);

				
			}else{
				Vaccine vaccine=adapter.getVaccine(position);	
				int vaccineId=vaccine.getId();
				//check if its already recorded
				record=vaccineRecords.getVaccineRecord(communityMemberId, vaccineId);
			
			}
			
			if(record==null){
				return; 
			}
			
			if(!vaccineRecords.reomveRecord(record.getId())){
				return;
			}
			
			adapter.updateRemovedRecord(position, record);
			
			
		}
		
		public Vaccine getSelectedVaccine(){
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerRecordVaccinVaccines);
			return (Vaccine)spinner.getSelectedItem();
		}
	
		private int getSelectedViewMode(){
			RadioGroup radioGroup=(RadioGroup)rootView.findViewById(R.id.radioGroup1);
			Button buttonAddVaccine=(Button)rootView.findViewById(R.id.buttonAddVaccine);
			Spinner spinner=(Spinner)rootView.findViewById(R.id.spinnerRecordVaccinVaccines);
			int selected=radioGroup.getCheckedRadioButtonId();
			if(selected==R.id.radioVaccineRecordView){
				buttonAddVaccine.setEnabled(true);
				buttonAddVaccine.setVisibility(View.VISIBLE);
				spinner.setVisibility(View.VISIBLE);
				return VaccineGridAdapter.RECORD_LIST;
			}else{
				buttonAddVaccine.setEnabled(false);
				buttonAddVaccine.setVisibility(View.INVISIBLE);
				spinner.setVisibility(View.INVISIBLE);
				return VaccineGridAdapter.SCHEDULE_LIST;
			}
		}
	}
	
	public static class FamilyPlanFragment extends Fragment implements OnClickListener, OnItemSelectedListener{
		
		int communityMemberId=0;
		
		public FamilyPlanFragment(){
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_community_member_record_vaccine, container,false);
			return rootView;
		}
		
		@Override
		public void onResume(){
			super.onResume();
			CommunityMemberRecordActivity a=(CommunityMemberRecordActivity)this.getActivity();
			communityMemberId=a.getCommunityMemberId();
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		
		Calendar calendar;
	
		public VaccineFragment vf;
		public int position;
		
		public DatePickerFragment(){
			calendar = Calendar.getInstance();
		}
		
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
			
			//calendar = Calendar.getInstance();
	
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
			calendar.set(year, month, day);
			this.vf.recordVaccine(position,getDateString());
		}
		
		public void setDate(java.util.Date date){
			if(calendar==null){
				calendar = Calendar.getInstance();
			}
			calendar.setTime(date);
		}
		
		public java.util.Date getDate(){
			return calendar.getTime();
		}
		
		public String getDateString(){
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
			return dateFormat.format(calendar.getTime());
		}
		
		public String getFormattedDateString(){
			SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
			return dateFormat.format(calendar.getTime());
		}
		
	}
}
