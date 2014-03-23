package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.TabsPagerAdapter;
import com.ashesi.cs.mhealth.knowledge.Question;
import com.ashesi.cs.mhealth.knowledge.Questions;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
		
		//Initialization
		viewPager = (ViewPager)findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
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
			case R.id.synch_q:
				if(isConnected()){// && !(db.connect("http://10.10.32.136/mHealth") == null)){
					Toast.makeText(this, "Synching Data", Toast.LENGTH_LONG).show();
					new Synchronize().execute();
				}else{
					Toast.makeText(this, "Sorry the network is down. Try again later!", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.q_settings:
				Intent i = new Intent(getApplicationContext(), KSettingsActivity.class);
				startActivity(i);
				break;
			case R.id.device_synch:
				Intent deviceIntent = new Intent(getApplicationContext(), WiFiDirectActivity.class);
				startActivity(deviceIntent);
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
	
	
	/**
	 * This is to update the data for the application
	 * @author Daniel
	 */
	private class Synchronize extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			JSONArray jArr = new JSONArray();
			try {
				ArrayList<Question> q = db.getAllQuestions();
				for (int i = getlastSaved("lastIDs"); i < q.size(); i++) {
					JSONObject jObj = new JSONObject();
					jObj.put("q_id",q.get(i).getId());
					jObj.put("cho_id", q.get(i).getChoId());
					jObj.put("q_content", q.get(i).getContent());
					jObj.put("category_id",q.get(i).getCategoryId());
					jObj.put("question_date", q.get(i).getDate());
					jObj.put("guid", q.get(i).getGuid());
					jObj.put(DataClass.REC_STATE, q.get(i).getRecState());
					jArr.put(jObj);
					
					Log.d("Current Question", q.get(i).getContent());
					
					if(isConnected()){
			 			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			 			nameValuePairs.add(new BasicNameValuePair("cmd", "6"));
					      nameValuePairs.add(new BasicNameValuePair("questionid",
					          jObj.toString()));
						String response = db.request(db.postRequest("http://10.10.32.136/mHealth/checkLogin/knowledgeAction.php", nameValuePairs));			
										
						//This is to get a response from the request with the current list of answers 
						if(!(response== null)){
					        try{
						        JSONArray jArray = new JSONArray(response);
						        JSONObject json_data= null;
						        
						         
						        for(int j=0;j<jArray.length();j++){
						                json_data = jArray.getJSONObject(j);
						                 if((json_data.getString("message") == "")){
						                	 System.out.println("We are here");
						                	 saveLastUpdated("lastIDs", String.valueOf(i));
						                	 System.out.println(json_data.getString("message") + getlastSaved("lastID"));
						                 }else{
						                	 System.out.println(getlastSaved("lastID"));
						                	 break;
						                 }
						        }					        
					        }catch(Exception e){
					        	System.out.println(e.toString());
					        }
				        }
						//Toast.makeText(getApplicationContext(), String.valueOf(getlastSaved("lastIDs")) + "", Toast.LENGTH_LONG).show() ;
			        }
				}
				System.out.println(String.valueOf(getlastSaved("lastIDs")));
				System.out.println("There are " + q.size() + " questions in the Database");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//saveLastUpdated("lastID", 0);
	        return null;
		}		
		
	}
	
	private int getlastSaved(String key){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Integer result = Integer.parseInt(sharedPreferences.getString(key, "0"));
		return result.intValue();		
	}
	
	private void saveLastUpdated(String key, String value){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.question_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}


}
