package com.ashesi.cs.mhealth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.color;
import com.ashesi.cs.mhealth.knowledge.Answer;
import com.ashesi.cs.mhealth.knowledge.Answers;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.LogData;
import com.ashesi.cs.mhealth.knowledge.Question;
import com.ashesi.cs.mhealth.knowledge.Questions;

public class QuestionsFragment extends Fragment{
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
	private MenuItem refreshMenuItem;
	private int selectedquestion;
	private LogData log;
	
	
	/**
	 * @return the onlyAnswered
	 */
	public boolean isOnlyAnswered() {
		return onlyAnswered;
	}

	/**
	 * @param onlyAnswered the onlyAnswered to set
	 */
	public void setOnlyAnswered(boolean onlyAnswered) {
		this.onlyAnswered = onlyAnswered;
	}

	private boolean isListEmpty, onlyMyPost;
	private int maxQuestions, counter, choId;
	private LinearLayout ln;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.activity_questions, container, false);
			
			return view;
		}
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);

			log = new LogData(getActivity());
			
			selectedquestion = 0;
			Intent intent = this.getActivity().getIntent();
			choId = intent.getIntExtra("choId", 0);
			CHOs chos = new CHOs(getActivity());
			currentCHO = chos.getCHO(choId);
			
			// Question TextBox;
			question = (EditText) getActivity().findViewById(R.id.resource_material);
			
			// Get the list view for the questions
			theVList = (ListView) getActivity().findViewById(R.id.listView1);
			theVList.setBackgroundResource(R.drawable.listview_roundcorner_item);
			isListEmpty = true;
			btn_prev = new Button(getActivity());
			btn_prev.setText("Prev");
			btn_prev.setHeight(LayoutParams.WRAP_CONTENT);
			btn_prev.setWidth(LayoutParams.WRAP_CONTENT);
			btn_next = new Button(getActivity());
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
			maxQuestions = 15;
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
						Toast.makeText(getActivity(), "There are no additional questions", Toast.LENGTH_LONG).show();
						CheckEnable();
					}
				}			
			});
			btn_next.setHeight(LayoutParams.WRAP_CONTENT);
			btn_next.setWidth(LayoutParams.WRAP_CONTENT);
			//Add button to the listView at the footer
			ln =new LinearLayout(getActivity());
			ln.addView(btn_prev);
			ln.addView(btn_next);
			ln.setGravity(Gravity.CENTER);
			theVList.addFooterView(ln);
			// Get switch for answered question
			onlyAnswered = false;
			ansDb = new Answers(getActivity());
			answers = ansDb.getAllAnswers();	
			setSwitchListener();

			// Load the spinner details
			db = new Questions(getActivity());
			db1 = new Categories(getActivity());

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
			btnAllPost = (Button)getActivity().findViewById(R.id.button2);
			btnMyPost = (Button)getActivity().findViewById(R.id.knowledgeBtn);
			inflatePostBtns();
			btnMyPost.setSelected(true);
			onlyMyPost = true;
			CheckEnable();
			
		}
		
		/*
		 * Alternate colors for when the user selects all posts verses my posts.
		 */
		private void inflatePostBtns(){
			btnMyPost.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					btnMyPost.setBackgroundColor(color.background);
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
					btnAllPost.setBackgroundColor(color.background);
					btnAllPost.setTextColor(color.White);
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
			answered = (Switch)getActivity().findViewById(R.id.switch1);
			answered.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg1){
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
						Intent i = new Intent(getActivity(), ViewQuestionActivity.class);	
						
						// View Question by starting another activity to with the detailss
						CHOs ch = new CHOs(getActivity());
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
						Date date1 = new Date();		            
						DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
						log.addLog(0201, dt.format(date1), currentCHO.getFullname(), 
								this.getClass().getName() + ": Click listener for questions in listview", "Trying to view a question [Parameters->\"guid\":" + qs.get(arg2).getGuid() + "]");

					}
				}
			});
			theVList.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					if(qs.get(arg2).getRecState() < 1){  //Check if the question has been uploaded to the server, if not allow delete
						ConfirmDelete confirm = new ConfirmDelete(qs.get(selectedquestion).getGuid());
						confirm.show(getActivity().getFragmentManager(), "QuestionFragment");
						selectedquestion = arg2;						
					}else{
						Toast.makeText(getActivity(), "This question cannot be deleted.", Toast.LENGTH_SHORT).show();
					}
					return false;
				}
				
			});
		}

		/**
		 * This method adds a listener to the Post Question Button to enable
		 */
		private void addListenerOnButton() {
			// TODO Auto-generated method stub
			// Retrieve the spinner for choose category and the button for post question
			spinner = (Spinner) getActivity().findViewById(R.id.spinner1);
			spinner.setPrompt("Choose a Category");
			btn = (Button) getActivity().findViewById(R.id.save_btn);

			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (spinner.getSelectedItemPosition() < 1) { // Prevent the submitting of empty strings
						Toast.makeText(getActivity(),
								"Please choose a category ", Toast.LENGTH_SHORT)
								.show();
					} else if (question.getText().toString().trim().equals("")) { // Prevent the submitting of a question without a category
						Toast.makeText(getActivity(),
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
			spinner2 = (Spinner) getActivity().findViewById(R.id.spinner2);
			spinner = (Spinner) getActivity().findViewById(R.id.spinner1);
			spinner3 = new Spinner(getActivity());

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			
			// Sort Drop down
			ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getActivity(),
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
						Toast.makeText(	getActivity(),
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
		public void refreshData(boolean onlyAnswered) {
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
						qs = db.getQuestionsby("cho_id=" + choId);
					}else{
						qs = db.getAllQuestions();
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
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId()){
			case R.id.synch_q:
				if(isConnected()){
					refreshMenuItem = item;
					Toast.makeText(getActivity(), "Synching Data", Toast.LENGTH_LONG).show();
					new Synchronize().execute();
				}else{
					Toast.makeText(getActivity(), "Sorry the network is down. Try again later!", Toast.LENGTH_LONG).show();
				}
				break;
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(getActivity());
				break;
			}			
			return super.onOptionsItemSelected(item);
		}
		
		public boolean isConnected(){
			Log.d("mHealth", "Posting questions Checking connectivity ...");
			ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			 
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		    return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		}
		
		/**
		 * This is to update the data for the application
		 * @author Daniel
		 */
		private class Synchronize extends AsyncTask<String, Void, String>{
			
			@Override
			protected void onPreExecute(){
				refreshMenuItem.setActionView(R.layout.action_progressbar);
				
				refreshMenuItem.expandActionView();
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				Log.d("Synching", "Getting the questions & Answers from server");
				
				//Retrieve answers from the database
				Questions temp = new Questions(getActivity());
				temp.download();
				
				Answers tempAns = new Answers(getActivity());
				tempAns.download();
			
				JSONArray jArr = new JSONArray();
				try {
					//Post new questions to the server
					ArrayList<Question> q = new ArrayList<Question>();
					q = getQuestions().getAllNewQuestions();
					if(q==null || q.isEmpty()){
						return "empty";
					}
					for (int i = 0; i < q.size(); i++) {
						if(q.get(i).getRecState() < 1){
							if(isConnected()){
								JSONObject jObj = new JSONObject();
								jObj.put("q_id",q.get(i).getId());
								jObj.put("cho_id", q.get(i).getChoId());
								jObj.put("q_content", q.get(i).getContent());
								jObj.put("category_id",q.get(i).getCategoryId());
								jObj.put("question_date", q.get(i).getDate());
								jObj.put("guid", q.get(i).getGuid());
								jObj.put(DataClass.REC_STATE, 1);
								jArr.put(jObj);
								Log.d("Current Question", q.get(i).getContent());
								
					 			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					 			nameValuePairs.add(new BasicNameValuePair("cmd", "6"));
							      nameValuePairs.add(new BasicNameValuePair("questionid",
							          jObj.toString()));
								String data = db.request(db.postRequest("http://cs.ashesi.edu.gh/mhealth/checkLogin/knowledgeAction.php", nameValuePairs));			
								JSONArray resultArr = new JSONArray(data);
								int result = resultArr.getJSONObject(1).getInt("result");  //if the upload was successful then update the status of the question to 1
								if(result == 1){
									Questions temp1 = new Questions(getActivity());
									System.out.println(q.get(i).getContent());
									temp1.changeStatus(q.get(i).getGuid(), 1);
								}
							}
						}
					}
					//Log the synchronize event
					JSONObject jObj = new JSONObject();
					
						jObj.put("cho", currentCHO.getFullname());
						Date date1 = new Date();		            
						DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
						log.addLog(0204, dt.format(date1), currentCHO.getFullname(), 
								this.getClass().getName() + ": Method->Synchronize()", "Synchronizing questions. \n" + jObj.toString());
				
					return "Done";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Log the synchronize event
					JSONObject jObj = new JSONObject();
					try {
						jObj.put("cho", currentCHO.getFullname());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Date date1 = new Date();		            
					DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
					log.addLog(0204, dt.format(date1), currentCHO.getFullname(), 
							this.getClass().getName() + ": Method->Synchronize()", "Synchronizing questions. \n" + jObj.toString() + " \n" + e.getMessage());
				}
				//saveLastUpdated("lastID", 0);
		        return null;
			}	
			
			@Override
			protected void onPostExecute(String result){
				refreshMenuItem.collapseActionView();
				refreshMenuItem.setActionView(null);
				Toast.makeText(getActivity(), "Synch complete" , Toast.LENGTH_LONG).show();
				refreshData(isOnlyAnswered());
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
			Toast.makeText(getActivity(),selectedCat.getID() + " Your question has submitted under "
							+ selectedCat.getCategoryName(), Toast.LENGTH_SHORT).show();
			
			//Reset the spinner and question
			refreshData(onlyAnswered);
			spinner.setSelection(0);
			question.setText("");
			
			//Log the question event
			JSONObject jObj = new JSONObject();
			try {
				jObj.put("question",  question.getText().toString());
				jObj.put("cho", currentCHO.getFullname());
				jObj.put("category", selectedCat.getCategoryName());
				Date date1 = new Date();		            
				DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
				log.addLog(0202, dt.format(date1), currentCHO.getFullname(), 
						this.getClass().getName() + ": Method->postQuestion()", "Trying to post a question. \n" + jObj.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onResume() {
			super.onResume();
			refreshData(isOnlyAnswered());
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
	    
	    public Questions getQuestions(){
			return db;
		}
	    

		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onStart()
		 */
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			refreshData(isOnlyAnswered());
		}

		/**
		 * @return the currentCHO
		 */
		public CHO getCurrentCHO() {
			return currentCHO;
		}  	    
		
		

}
