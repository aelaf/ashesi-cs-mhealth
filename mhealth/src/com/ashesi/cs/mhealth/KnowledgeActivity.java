package com.ashesi.cs.mhealth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.SearchView;

import com.ashesi.cs.mhealth.ConfirmDelete.ConfirmDeleteListener;
import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.TabsPagerAdapter;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.LogData;
import com.ashesi.cs.mhealth.knowledge.Questions;

public class KnowledgeActivity extends FragmentActivity implements ActionBar.TabListener, ConfirmDeleteListener{
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private Questions db;
	private String [] tabs = {"Questions", "Resources"};
	private int onStartCount = 0;
	private String deleteGuid = "";
	private LogData log;
	private CHO currentCHO;
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knowledge);
		
		Intent intent = getIntent();
		int choId = intent.getIntExtra("choId", 0);
		CHOs chos = new CHOs(this);
		currentCHO = chos.getCHO(choId);
		
		//Create a log for the questions activity
		log = new LogData(this);
		
		//Animation effect for transitions
		onStartCount = 1;
	        if (savedInstanceState == null) // 1st time
	        {
	        	this.overridePendingTransition(R.anim.anim_slide_in_left,
	                    R.anim.anim_slide_out_left);
	        } else // already created so reverse animation
	        { 
	            onStartCount = 2;
	        }
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
		handleIntent(getIntent());
		
	}
	
	

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		handleIntent(intent);
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
			case R.id.q_settings:		//Settings 
				Intent i = new Intent(getApplicationContext(), KSettingsActivity.class);
				startActivity(i);
				break;
			case R.id.device_synch:    //Wifi direct synchronization
				Intent deviceIntent = new Intent(getApplicationContext(), WiFiDirectActivity.class);
				startActivity(deviceIntent);
				//Log the synchronize event
				Date date1 = new Date();		            
				DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
				log.addLog(0102, dt.format(date1), currentCHO.getFullname(), 
			    this.getClass().getName() , "Openned the Wifi Direct Resource synch.");
				break;
			case R.id.bluetooth_backup:  //Bluetooth Backup
				Toast.makeText(getApplicationContext(), "Sending data via Bluetooth", Toast.LENGTH_SHORT).show();
				break;
		}
			//return true;			
	return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Check to see if the Internet is connected
	 */
	public boolean isConnected(){
		Log.d("mHealth", "Posting questions Checking connectivity ...");
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.question_menu, menu);
		
		//MenuItem searchItem = menu.findItem(R.id.action_search);

	 // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setSubmitButtonEnabled(true);
		return super.onCreateOptionsMenu(menu);
	}
	
	/*
	 * Transition effect for consistency(non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (onStartCount > 1) {
        	 this.overridePendingTransition(R.anim.anim_slide_in_right,
                     R.anim.anim_slide_out_right);                	 
        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Get the guid of the question to be deleted. Next delete that question and refresh the list of questions
	 * @see com.ashesi.cs.mhealth.ConfirmDelete.ConfirmDeleteListener#setGuid(java.lang.String)
	 */
	@Override
	public void setGuid(String aGuid) {
		// TODO Auto-generated method stub	
		QuestionsFragment qfrag = (QuestionsFragment)mAdapter.getItem(0);
		Date date1 = new Date();		            
		DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
		try{
			deleteGuid = aGuid;
			if(db.deleteQuestion(deleteGuid)){
				log.addLog(0101, dt.format(date1), qfrag.getCurrentCHO().getFullname(), 
						this.getClass().getName() + ": Method setGuid()", "Trying to delete a question [Parameters->\"guid\":" + aGuid + "]");
			}else{
				log.addLog(0101, dt.format(date1), qfrag.getCurrentCHO().getFullname(), 
						this.getClass().getName() + ": Method setGuid()", "Trying to delete a question [Parameters->\"guid\":" + aGuid + "] but fragment is null");
			}
			
			if(qfrag != null){
				qfrag.refreshData(qfrag.isOnlyAnswered());
				Toast.makeText(getApplicationContext(), "The question has been deleted!", Toast.LENGTH_SHORT).show();
			}
		}catch(Exception ex){
			log.addLog(0101, dt.format(date1), qfrag.getCurrentCHO().getFullname(), 
					this.getClass().getName() + ": setGuid()", ex.getMessage() + " [Parameters->\"guid\":" + aGuid + "]");
		}
	}

	@Override
	public String getGuid() {
		// TODO Auto-generated method stub
		return deleteGuid;
	}
	
	private void handleIntent(Intent intent){
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
		}
	}

}
