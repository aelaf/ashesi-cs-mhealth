package com.ashesi.cs.mhealth;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.TabsPagerAdapter;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Questions;

public class KnowledgeActivity extends FragmentActivity implements ActionBar.TabListener{
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private Questions db;
	private String [] tabs = {"Questions", "Resources"};
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge);
		
		// Load the spinner details
		db = new Questions(this);
		new Categories(this);
		
		//Initialization
		viewPager = (ViewPager)findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
		//actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//Style actionBar
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#428bca"));
		actionBar.setBackgroundDrawable(colorDrawable);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//Adding Tabs
		for(String tab_name: tabs){
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));	
		}
				
		/*
		 * Change respective tab to the selected upon swipe
		 */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				actionBar.setSelectedNavigationItem(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(arg0.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				break;
			case R.id.q_settings:
				Intent i = new Intent(getApplicationContext(), KSettingsActivity.class);
				startActivity(i);
				break;
			case R.id.device_synch:
				Intent deviceIntent = new Intent(getApplicationContext(), WiFiDirectActivity.class);
				startActivity(deviceIntent);
				break;
			case R.id.bluetooth_backup:
				Toast.makeText(getApplicationContext(), "Sending data via Bluetooth", Toast.LENGTH_SHORT).show();
				//MyDiscoveryListener device = new MyDiscoveryListener();
				break;
		}
			//return true;			
	return super.onOptionsItemSelected(item);
	}
	
	public boolean isConnected(){
		Log.d("mHealth", "Posting questions Checking connectivity ...");
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}
	
	public Questions getQuestions(){
		return db;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.question_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}


}
