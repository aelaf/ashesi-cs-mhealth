package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.color;
import com.ashesi.cs.mhealth.knowledge.Answer;
import com.ashesi.cs.mhealth.knowledge.Answers;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.Question;
import com.ashesi.cs.mhealth.knowledge.Questions;

public class PostQuestionActivity extends Activity implements OnClickListener {
	private CHO currentCHO;
	private Spinner spinner, spinner2, spinner3;
	private Questions db;
	private Categories db1;
	private Answers ansDb;
	ArrayList<Question> qs;
	ArrayList<Category> cat;
	ArrayList<Answer> answers;
	private List<String> list, sortList;
	private Button btn, btn_next, btn_prev, btnMyPost, btnAllPost;
	private EditText question;
	private ListView theVList;
	private ArrayAdapter<String> adapter;
	private Switch answered;
	private boolean onlyAnswered;
	private boolean isListEmpty, onlyMyPost;
	private int maxQuestions, counter, choId;
	private LinearLayout ln;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_question);
		Intent intent = getIntent();
		choId = intent.getIntExtra("choId", 0);
		CHOs chos = new CHOs(getApplicationContext());
		currentCHO = chos.getCHO(choId);
		
		//Style actionBar
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#428bca"));
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayHomeAsUpEnabled(true);
		
		// Question TextBox;
		question = (EditText) findViewById(R.id.resource_material);
		
		// Get the list view for the questions
		theVList = (ListView) findViewById(R.id.listView1);
		theVList.setBackgroundResource(R.drawable.listview_roundcorner_item);
		isListEmpty = true;
		btn_prev = new Button(this);
		btn_prev.setText("Prev");
		btn_prev.setHeight(LayoutParams.WRAP_CONTENT);
		btn_prev.setWidth(LayoutParams.WRAP_CONTENT);
		btn_next = new Button(this);
		btn_next.setText("Next");
		
		btn_prev.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(counter > 0){
					counter--;
					refreshData(onlyAnswered);
					CheckEnable();
				}else{
					CheckEnable();
				}
			}
			
		});
		
		//Increase count for more questions.
		maxQuestions = 5;
		counter = 0;
		btn_next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(((counter + 1) * maxQuestions) < qs.size()){
					counter++;
					refreshData(onlyAnswered);
					CheckEnable(); //Check if the next button should be enabled
				}else{
					Toast.makeText(getApplicationContext(), "There are no additional questions", Toast.LENGTH_LONG).show();
					CheckEnable();
				}
			}			
		});
		btn_next.setHeight(LayoutParams.WRAP_CONTENT);
		btn_next.setWidth(LayoutParams.WRAP_CONTENT);
		//Add button to the listView at the footer
		ln =new LinearLayout(this);
		ln.addView(btn_prev);
		ln.addView(btn_next);
		ln.setGravity(Gravity.CENTER);
		theVList.addFooterView(ln);
		// Get switch for answered question
		onlyAnswered = false;
		ansDb = new Answers(this);
		answers = ansDb.getAllAnswers();	
		setSwitchListener();

		// Load the spinner details
		db = new Questions(this);
		db1 = new Categories(this);

		// Retrieve all questions and categories from the database
		qs = db.getAllQuestions();
		cat = db1.getAllCategories();

		// Instantiate a list for the category list and the sort by list
		list = new ArrayList<String>();
		sortList = new ArrayList<String>();

		// Add a default label value for the user to understand
		sortList.add("Sort by:");
		list.add("Choose a Category");

		// Populate the lists (SortList and Choose Category List) with the category names from the Category database
		for (Category ca : cat) {
			String lo = ca.getCategoryName();
			list.add(lo);
			sortList.add(lo);
		}

		// Populate Categories spinner
		addItemsOnSpinner();

		// Add a listener to the Post question button
		addListenerOnButton();

		// Add a listener to the sort By spinner
		addListenerOnList();
		
		//Inflate buttons for changing between user's posts and all posts. 
		btnAllPost = (Button)findViewById(R.id.button2);
		btnMyPost = (Button)findViewById(R.id.knowledgeBtn);
		inflatePostBtns();
		btnMyPost.setSelected(true);
		onlyMyPost = true;
		CheckEnable();
		
	}
	
	private void inflatePostBtns(){
		btnMyPost.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				btnMyPost.setBackgroundColor(color.selectedBtn);
				btnMyPost.setTextColor(color.white);
				btnAllPost.setBackgroundColor(Color.TRANSPARENT);
				btnAllPost.setTextColor(color.Black);
				onlyMyPost =true;
				refreshData(onlyAnswered);
				CheckEnable();
			}		
		});

		btnAllPost.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnAllPost.setBackgroundColor(color.selectedBtn);
				btnAllPost.setTextColor(color.white);
				btnMyPost.setBackgroundColor(Color.TRANSPARENT);
				btnMyPost.setTextColor(color.Black);
				onlyMyPost = false;
				refreshData(onlyAnswered);
			}
			
		});
		
	}
	
	/**
	 * Add an OnClicklistener for the Switch. i.e. Show (only Answered questions/ All questions)
	 */
	private void setSwitchListener() {
		// TODO Auto-generated method stub
		answered = (Switch)findViewById(R.id.switch1);
		answered.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(answered.isChecked()){//;answered.getText().toString().trim().equals("Answered")){
					System.out.println("Answered only");
					onlyAnswered = true;
					refreshData(onlyAnswered);	
				}else if(!answered.isChecked()){
					System.out.println("All");
					onlyAnswered = false;
					refreshData(onlyAnswered);
					
				}
			}
			
		});
	}

	/**
	 * This method will add a listener to the list of posted questions This will
	 * enable the transition from the list of questions to the details of a
	 * selected question
	 */
	private void addListenerOnList() {
		// TODO Auto-generated method stub

		// theVList is ListView for the questions
		theVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
					startActivity(i);
				}
			}
		});
	}

	/**
	 * This method adds a listener to the Post Question Button to enable
	 */
	private void addListenerOnButton() {
		// TODO Auto-generated method stub
		// Retrieve the spinner for choose category and the button for post question
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setPrompt("Choose a Category");
		btn = (Button) findViewById(R.id.save_btn);

		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spinner.getSelectedItemPosition() < 1) { // Prevent the submitting of empty strings
					Toast.makeText(PostQuestionActivity.this,
							"Please choose a category ", Toast.LENGTH_SHORT)
							.show();
				} else if (question.getText().toString().trim().equals("")) { // Prevent the submitting of a question without a category
					Toast.makeText(PostQuestionActivity.this,
							"Please type a question ", Toast.LENGTH_SHORT)
							.show();
				} else if (spinner.getSelectedItemPosition() > 0
						&& !question.getText().toString().trim().equals("")) { // If everything is okay the submit.
					postQuestion();
					refreshData(onlyAnswered);
				}
			}

		});
	}

	/**
	 * Dynamically populate categories spinner
	 */
	public void addItemsOnSpinner() {
		// Retrieve spinners
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner3 = new Spinner(this);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
		// Sort Drop down
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sortList);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter2);
		spinner3.setAdapter(dataAdapter2);
		
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 > 0) { // If the sort dropdown has been selected i.e. not "Sort By:"
					Toast.makeText(	PostQuestionActivity.this,
							"Your are sorting by - "
									+ cat.get(arg2-1).getCategoryName(),
							Toast.LENGTH_SHORT).show();
				}
				refreshData(onlyAnswered);
				CheckEnable();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		

	}

	/**
	 * Refresh list of questions
	 * If the toggle says onlyAnswered then display only answered questions.
	 * @param onlyAnswered
	 */
	private void refreshData(boolean onlyAnswered) {
		CHOs ch = new CHOs(getApplicationContext());
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
				qs = db.getQuestionsby("cho_id=" + choId);
			}else{
				qs = db.getAllQuestions();
			}
		}
		
		if(onlyAnswered && answers.isEmpty()){	//If the user has selected the onlyAnswered questions
			qstn = new String[]{"There are no answered questions."};
			isListEmpty = true;	
			adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,qstn);	
			theVList.setAdapter(adapter);
		}else if(qs != null ) {			
			isListEmpty = false;
			if(qs.isEmpty()){
				qstn = new String[]{"There are no questions under this category."};
				isListEmpty = true;	
				adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,qstn);	
				theVList.setAdapter(adapter);
			}else if(onlyAnswered && answers.isEmpty()){
				qstn = new String[]{"There are no answered questions."};
				isListEmpty = true;		
			}else if(qs != null ) {
				isListEmpty = false;
				if(!onlyAnswered){
					QuestionListAdapter qListAdapter = new QuestionListAdapter(PostQuestionActivity.this, currentList(qs));
					theVList.setAdapter(qListAdapter);
				}else{				
					ArrayList<Question> q = new ArrayList<Question>();
					for (int i = 0; i < qs.size(); i++) {
						if(onlyAnswered){
							//if a question's id exists in the answers DB then add it to the list
							if(!ansDb.getByQuestion(qs.get(i).getGuid()).equals(null)){			 
								q.add(qs.get(i));
							}
						}
					}
					QuestionListAdapter qListAdapter = new QuestionListAdapter(PostQuestionActivity.this, currentList(qs));
					theVList.setAdapter(qListAdapter);
				}
				
			}			
		}
	}
	
	/**
	 * Return the current List of questions (aList) to be shown based on the page number
	 * @param aList
	 * @return
	 */
	private ArrayList<Question> currentList(ArrayList<Question> aList){
		ArrayList<Question> theList = new ArrayList<Question>();
		
		for(int i=counter*maxQuestions; i<maxQuestions * (counter + 1) && (i<aList.size()); i++){
			theList.add(aList.get(i));
		}
		return theList;
	}

	/**
	 * Submit the question to the database
	 */
	private void postQuestion() {
		Category selectedCat = cat.get(spinner.getSelectedItemPosition() - 1);
		db.addQuestion(1, question.getText().toString(), currentCHO.getId(), selectedCat.getID(), "", "", DataClass.REC_STATE_NEW);
		Toast.makeText(PostQuestionActivity.this,selectedCat.getID() + " Your question has submitted under "
						+ selectedCat.getCategoryName(), Toast.LENGTH_SHORT).show();
		
		//Reset the spinner and question
		refreshData(onlyAnswered);
		spinner.setSelection(0);
		question.setText("");
		//new Synchronize().execute();	
		
	}

	@Override
	protected void onResume() {
		refreshData(onlyAnswered);
		super.onResume();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
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
	
	private void saveLastUpdated(String key, String value){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.apply();
	}
	
	private int getlastSaved(String key){
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Integer result = Integer.parseInt(sharedPreferences.getString(key, "0"));
		return result.intValue();
		
	}
	public boolean isConnected(){
		Log.d("mHealth", "Posting questions Checking connectivity ...");
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.question_menu, menu);
		return super.onCreateOptionsMenu(menu);
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
	
	/**
     * Method for enabling and disabling Next and Previous
     */
    private void CheckEnable()
    {
    	if(qs == null){
    		btn_prev.setEnabled(false);
    		btn_next.setEnabled(false);
    	}else if(((counter + 1) * maxQuestions) > qs.size()){
            btn_next.setEnabled(false);
        }else{
        	btn_next.setEnabled(true);
        }
        if(counter == 0)
        {
            btn_prev.setEnabled(false);
        }else{
        	btn_prev.setEnabled(true);
        }
    }
	
}