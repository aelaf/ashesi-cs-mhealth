package com.ashesi.cs.mhealth;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Question;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class SearchResultsActivity extends Activity{
	
	private ListView list;
	private int onStartCount = 0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
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
		//get action bar
		ActionBar actionBar = getActionBar();
		
		//Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#428bca"));
		actionBar.setBackgroundDrawable(colorDrawable);
		
		list = (ListView)findViewById(R.id.searchlist);
		
		handleIntent(getIntent());
		
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent){
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
		}
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
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.question_menu, menu);
		
		// Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Refresh list of questions
	 * If the toggle says onlyAnswered then display only answered questions.
	 * @param onlyAnswered
	 */
	public void searchData(boolean onlyAnswered) {
		try{
			System.out.println("Refreshing questions list");
			String[] qstn = new String[]{"The list is currently empty."};
			
			//Filter questions based on the criterion selected by the user.
			if (spinner2.getSelectedItemPosition() > 0) {
				if(onlyMyPost){
					qs = db.getQuestionsby("category_id=" + cat.get(spinner2.getSelectedItemPosition() - 1).getID() + " AND cho_id=" + choId);
				}else{
					qs = db.getQuestionsby("category_id=" + cat.get(spinner2.getSelectedItemPosition() - 1).getID());
				}
			}else {
				if(onlyMyPost){
					qs = db.contains(searchQuery);
				}else{
					qs = db.contains(searchQuery);
				}
			}
			
			if(onlyAnswered && answers.isEmpty()){	//If the user has selected the onlyAnswered questions
				qstn = new String[]{"There are no answered questions."};
				isListEmpty = true;	
				adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1,qstn);	
				theVList.setAdapter(adapter);
			}else if(qs != null ) {			
				isListEmpty = false;
				if(qs.isEmpty()){
					qstn = new String[]{"There are no questions under this category."};
					isListEmpty = true;	
					adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1,qstn);	
					theVList.setAdapter(adapter);
				}else if(onlyAnswered && answers.isEmpty()){
					qstn = new String[]{"There are no answered questions."};
					isListEmpty = true;		
				}else if(qs != null ) {
					isListEmpty = false;
					if(!onlyAnswered){
						QuestionListAdapter qListAdapter = new QuestionListAdapter(getActivity(), currentList(qs));
						theVList.setAdapter(qListAdapter);
					}else{				
						ArrayList<Question> q = new ArrayList<Question>();
						
						for (int i = 0; i < qs.size(); i++) {
							if(onlyAnswered){
								//if a question's id exists in the answers DB then add it to the list
								if(qs.get(i).getRecState() == 2){			 
									q.add(qs.get(i));
								}
							}
						}
						QuestionListAdapter qListAdapter = new QuestionListAdapter(getActivity(), currentList(q));
						theVList.setAdapter(qListAdapter);
					}
					
				}			
			}
		}catch(Exception ex){
				//Log the question event
				Date date1 = new Date();		            
				DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
				log.addLog(0203, dt.format(date1), currentCHO.getFullname(), 
			    this.getClass().getName() + ": Method->refreshData()", "Trying to refresh questions.");
		}
			
	}

	
	
	
	
}
