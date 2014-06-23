package com.ashesi.cs.mhealth;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.Question;
import com.ashesi.cs.mhealth.knowledge.Questions;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchResultsActivity extends Activity{
	
	private ListView list;
	private Questions db;
	private Categories db1;
	ArrayList<Question> qs;
	ArrayList<Category> cat;
	private int onStartCount = 0;
	private ArrayAdapter<String> adapter;
	private String searchQuery;
	private boolean isListEmpty;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		isListEmpty = true;
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
		list.setBackgroundResource(R.drawable.listview_roundcorner_item);
		handleIntent(getIntent());
		
		db = new Questions(this);
		db1 = new Categories(this);

		// Retrieve all questions and categories from the database
		cat = db1.getAllCategories();
		addListenerOnList();
	}
	
	public void addListenerOnList(){
		// theVList is ListView for the questions
					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							
							if(isListEmpty){ //If question list is currently Empty show nothing. 
								Toast.makeText(arg0.getContext(),"Sorry! The list is currently Empty",Toast.LENGTH_SHORT).show();
							}else{	// Put details of the question in Extra and start the activity
							
								//Give feedback to the user
								Toast.makeText(arg0.getContext(),"The question selected is: "+ arg0.getItemAtPosition(arg2).toString(),
										Toast.LENGTH_SHORT).show();
								Intent i = new Intent(getApplicationContext(), ViewQuestionActivity.class);	
								
								// View Question by starting another activity to with the detailss
								CHOs ch = new CHOs(getApplicationContext());
								i.putExtra("ChoName", ch.getCHO(qs.get(arg2).getChoId())
										.getFullname());
								i.putExtra("Question", qs.get(arg2).getContent());
								i.putExtra("datetime", qs.get(arg2).getDate());
								i.putExtra("category", db1.getCategory(qs.get(arg2).getCategoryId()).getCategoryName());
								i.putExtra("guid", qs.get(arg2).getGuid());
								i.putExtra("status", qs.get(arg2).getRecState());
								i.putExtra("choId", qs.get(arg2).getChoId());
								i.putExtra("catID", qs.get(arg2).getCategoryId());
								startActivity(i);
//								Date date1 = new Date();		            
//								DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
//								log.addLog(0201, dt.format(date1), currentCHO.getFullname(), 
//										this.getClass().getName() + ": Click listener for questions in listview", "Trying to view a question [Parameters->\"guid\":" + qs.get(arg2).getGuid() + "]");

							}
						}
					});
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
			searchQuery = intent.getStringExtra(SearchManager.QUERY);
			System.out.println("The search is for " + searchQuery);
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
			qs = db.contains(searchQuery);
				
			
		   if(qs != null ) {			
				if(qs.isEmpty()){
					isListEmpty = true;
					qstn = new String[]{"There are no questions that match your question. \n Would you like to ask this question?"};
					adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,qstn);	
					list.setAdapter(adapter);	
				}else{
					isListEmpty = false;
						QuestionListAdapter qListAdapter = new QuestionListAdapter(this, qs);
						list.setAdapter(qListAdapter);					
				}			
			}
		}catch(Exception ex){
				//Log the question event
//				Date date1 = new Date();		            
//				DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
//				log.addLog(0203, dt.format(date1), currentCHO.getFullname(), 
//			    this.getClass().getName() + ": Method->refreshData()", "Trying to search questions.");
		}
			
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		searchData(true);
	}
	
	

	
	
	
	
}
