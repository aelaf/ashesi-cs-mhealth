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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Answer;
import com.ashesi.cs.mhealth.knowledge.Answers;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.Question;
import com.ashesi.cs.mhealth.knowledge.Questions;

public class PostQuestionActivity extends Activity implements OnClickListener {
	private CHO currentCHO;
	private Spinner spinner, spinner2;
	private Questions db;
	private Categories db1;
	private Answers ansDb;
	ArrayList<Question> qs;
	ArrayList<Category> cat;
	ArrayList<Answer> answers;
	private List<String> list, sortList;
	private Button btn;
	private EditText question;
	private ListView theVList;
	private ArrayAdapter<String> adapter;
	private Switch answered;
	private boolean onlyAnswered;
	private boolean isListEmpty;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_question);
		Intent intent = getIntent();
		int choId = intent.getIntExtra("choId", 0);
		CHOs chos = new CHOs(getApplicationContext());
		currentCHO = chos.getCHO(choId);
		
		//Style actionBar
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#428bca"));
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayHomeAsUpEnabled(true);
		
		// Question TextBox;
		question = (EditText) findViewById(R.id.question);

		// Get the list view for the questions
		theVList = (ListView) findViewById(R.id.listView1);
		theVList.setBackgroundResource(R.drawable.listview_roundcorner_item);
		isListEmpty = true;
		
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

		// Log all questions in the log to see what has already been posted
		//		if(qs!=null){
		//		for (Question q : qs) {
		//			String log = q.toString();
		//			Log.d("Question: ", log);
		//		}}

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
					
					// View Question
					CHOs ch = new CHOs(getApplicationContext());
					i.putExtra("ChoName", ch.getCHO(qs.get(arg2).getChoId())
							.getFullname());
					i.putExtra("Question", qs.get(arg2).getContent());
					i.putExtra("datetime", qs.get(arg2).getDate());
					i.putExtra("category", cat.get(arg2).getCategoryName());
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
		// Retrieve the spinner for choose category and the button for post
		// question
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setPrompt("Choose a Category");
		btn = (Button) findViewById(R.id.save_btn);

		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (spinner.getSelectedItemPosition() < 1) { // Prevent the
																// submitting of
																// empty strings
					Toast.makeText(PostQuestionActivity.this,
							"Please choose a category ", Toast.LENGTH_SHORT)
							.show();
				} else if (question.getText().toString().trim().equals("")) { // Prevent
																				// the
																				// submitting
																				// of
																				// a
																				// question
																				// without
																				// a
																				// category
					Toast.makeText(PostQuestionActivity.this,
							"Please type a question ", Toast.LENGTH_SHORT)
							.show();
				} else if (spinner.getSelectedItemPosition() > 0
						&& !question.getText().toString().trim().equals("")) { // If
																				// everything
																				// is
																				// okay
																				// then
																				// submit.
					postQuestion();
					refreshData(onlyAnswered);
					//synch();
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

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		// Sort Drop down
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sortList);
		dataAdapter2
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter2);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 > 0) { // If the sort dropdown has been selected i.e. not "Sort By:"
					Toast.makeText(	PostQuestionActivity.this,
							"Your are sorting by - "
									+ cat.get(arg2 - 1).getCategoryName(),
							Toast.LENGTH_SHORT).show();
				}
				refreshData(onlyAnswered);
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
			qs = db.getQuestionsby("category_id=" + cat.get(spinner2.getSelectedItemPosition() - 1).getID());
		} else {
			qs = db.getAllQuestions();
		}
		
		if(onlyAnswered && answers.isEmpty()){
			qstn = new String[]{"There are no answered questions."};
			isListEmpty = true;		
		}else if (qs != null ) {
			isListEmpty = false;
			qstn = new String[qs.size()];
			for (int i = 0; i < qs.size(); i++) {
				if(onlyAnswered){
					//if a question's id exists in the answers DB then add it to the list
					if(!ansDb.getByQuestion(qs.get(i).getId()).equals(null)){			 
						String temp = ch.getCHO(qs.get(i).getChoId()).getFullname();
						temp += " - " + qs.get(i).getContent();
						temp += " - " + db1.getCategory((qs.get(i).getCategoryId())).getCategoryName();
						temp += " on \t" + qs.get(i).getDate();
						qstn[i] = temp;
					}
				}else {
					String temp = ch.getCHO(qs.get(i).getChoId()).getFullname();
					temp += " - " + qs.get(i).getContent();
					temp += " - " + db1.getCategory((qs.get(i).getCategoryId())).getCategoryName();
					temp += " on \t" + qs.get(i).getDate();
					qstn[i] = temp;
				}
			}
		}
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,qstn);
		theVList.setAdapter(adapter);
	}

	/**
	 * Submit the question to the database
	 */
	private void postQuestion() {
		Category selectedCat = cat.get(spinner.getSelectedItemPosition() - 1);
		db.addQuestion(1, question.getText().toString(), currentCHO.getId(), selectedCat.getID(), "", "");
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
	 *
	 */
	private class Synchronize extends AsyncTask<String, Integer, Double>{
		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONArray jArr = new JSONArray();
			try {
				ArrayList<Question> q = db.getAllQuestions();
				for (int i = 0; i < q.size(); i++) {
					JSONObject jObj = new JSONObject();
					jObj.put("q_id",q.get(i).getId());
					jObj.put("cho_id", q.get(i).getChoId());
					jObj.put("q_content", q.get(i).getContent());
					jObj.put("category_id",q.get(i).getCategoryId());
					jObj.put("question_date", q.get(i).getDate());
					jObj.put("guid", q.get(i).getGuid());
					jArr.put(jObj);
				}
				System.out.println("There are " + q.size() + " questions in the Database");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(isConnected()){
	 			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	 			nameValuePairs.add(new BasicNameValuePair("cmd", "6"));
			      nameValuePairs.add(new BasicNameValuePair("questionid",
			          jArr.toString()));
				String response = db.request(db.postRequest("http://10.10.32.136/mHealth/checkLogin/knowledgeAction.php", nameValuePairs));			
				
				//This is to get a response from the request with the current list of answers 
		        try{
		        JSONArray jArray = new JSONArray(response);
		        JSONObject json_data=null;
		         
		        for(int i=0;i<jArray.length();i++){
		                json_data = jArray.getJSONObject(i);
		               Log.d("q_id", "" + json_data.getInt("q_id"));
		               Log.d("q_content", json_data.getString("q_content"));
		               Log.d("category_id", "" + json_data.getInt("category_id"));
		               Log.d("cho_id", "" + json_data.getInt("cho_id"));
		               Log.d("question_date", json_data.getString("question_date"));
		               Log.d("guid", json_data.getString("guid"));
		             }
		        
		        }catch(Exception e){
		        	System.out.println(e.toString());
		        }
	        }
	        return null;
		}
		
		
		
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
				if(isConnected() && !(db.connect("http://10.10.32.136/mHealth") == null)){
					Toast.makeText(this, "Synching Data", Toast.LENGTH_LONG).show();
					new Synchronize().execute();
				}else{
					Toast.makeText(this, "Sorry the network is down. Try again later!", Toast.LENGTH_LONG).show();
				}
				break;
		}
			//return true;			
	return super.onOptionsItemSelected(item);
	}
	
}
