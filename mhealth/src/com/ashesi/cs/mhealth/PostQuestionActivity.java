package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.Question;
import com.ashesi.cs.mhealth.knowledge.Questions;

public class PostQuestionActivity extends Activity implements OnClickListener {
	private CHO currentCHO;
	private Spinner spinner, spinner2;
	private Questions db;
	private Categories db1;
	ArrayList<Question> qs;
	ArrayList<Category> cat;
	private List<String> list, sortList;
	private Button btn;
	private EditText question;
	private ListView theVList;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_question);
		Intent intent = getIntent();
		int choId = intent.getIntExtra("choId", 0);
		CHOs chos = new CHOs(getApplicationContext());
		currentCHO = chos.getCHO(choId);

		// Question TextBox;
		question = (EditText) findViewById(R.id.question);

		// Get the list view for the questions
		theVList = (ListView) findViewById(R.id.listView1);
		theVList.setBackgroundResource(R.drawable.listview_roundcorner_item);

		// Load the spinner details
		db = new Questions(this);
		db1 = new Categories(this);

		// Retrieve all questions and categories from the database
		qs = db.getAllQuestions();
		cat = db1.getAllCategories();

		// Log all questions in the log to see what has already been posted
		for (Question q : qs) {
			String log = q.toString();
			Log.d("Question: ", log);
		}

		// Instantiate a list for the category list and the sort by list
		list = new ArrayList<String>();
		sortList = new ArrayList<String>();

		// Add a default label value for the user to understand
		sortList.add("Sort by:");
		list.add("Choose a Category");

		// Populate the lists (SortList and Choose Category List) with the
		// category names from the Category database
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
	 * This method will add a listener to the list of posted questions This will
	 * enable the transition from the list of questions to the details of a
	 * selected question
	 */
	private void addListenerOnList() {
		// TODO Auto-generated method stub

		// theVList is ListView for the questions
		theVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(arg0.getContext(), "The question selected is: "
								+ arg0.getItemAtPosition(arg2).toString(), Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(), ViewQuestionActivity.class);
				
				//Put details of the question in Extra and start the activity View Question
				CHOs ch = new CHOs(getApplicationContext());
				i.putExtra("ChoName", ch.getCHO(qs.get(arg2).getChoId()).getFullname());
				i.putExtra("Question", qs.get(arg2).getContent());
				i.putExtra("datetime", qs.get(arg2).getDate());
				i.putExtra("category", cat.get(arg2).getCategoryName());
				startActivity(i);
			}
		});
	}

	/**
	 * This method adds a listener to the Post Question Button to enable 
	 */
	private void addListenerOnButton() {
		// TODO Auto-generated method stub
		//Retrieve the spinner for choose category and the button for post question
		spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setPrompt("Choose a Category");
		btn = (Button) findViewById(R.id.save_btn);

		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println(spinner.getSelectedItemPosition());
				if (spinner.getSelectedItemPosition() < 1) {   //Prevent the submitting of empty strings
					Toast.makeText(PostQuestionActivity.this,
							"Please choose a category ", Toast.LENGTH_SHORT)
							.show();
				} else if (question.getText().toString().trim().equals("")) {	//Prevent the submitting of a question without a category
					Toast.makeText(PostQuestionActivity.this,
							"Please type a question ", Toast.LENGTH_SHORT)
							.show();
				} else if (spinner.getSelectedItemPosition() > 0 && !question.getText().toString().trim().equals("")) {	//If everything is okay then submit.
					postQuestion();
					refreshData();
				}
			}

		});
	}

	/**
	 * Dynamically populate categories spinner
	 */
	public void addItemsOnSpinner() {
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
				if(arg2 > 0){	//If the 
					Toast.makeText(
							PostQuestionActivity.this,
							"Your are sorting by - "
									+ cat.get(arg2 - 1).getCategoryName(),
							Toast.LENGTH_SHORT).show();
				}
				refreshData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	/**
	 * Refresh data from database
	 */
	private void refreshData() {
		CHOs ch = new CHOs(getApplicationContext());
		System.out.println("The selected index is: "
				+ spinner2.getSelectedItemPosition());
		if (spinner2.getSelectedItemPosition() > 0) {

			System.out.println("The category id is : "
					+ cat.get(spinner2.getSelectedItemPosition() - 1).getID());
			qs = db.getQuestionsby("category_id="
					+ cat.get(spinner2.getSelectedItemPosition() - 1).getID());
			if (qs.size() > 0) {
				System.out.println("From DB the cat Id is: "
						+ cat.get(qs.get(0).getCategoryId()).getCategoryName());
			}
		} else {

			qs = db.getAllQuestions();
		}
		if (qs != null) {
			String[] qstn = new String[qs.size()];
			
			for (int i = 0; i < qs.size(); i++) {
				String temp = ch.getCHO(qs.get(i).getChoId()).getFullname();
				temp += " - " + qs.get(i).getContent();
				temp += " - "
						+ db1.getCategory((qs.get(i).getCategoryId()))
								.getCategoryName();
				System.out.println("To SOrt is: "
						+ db1.getCategory((qs.get(i).getCategoryId()))
								.getCategoryName());
				qstn[i] = temp;
			}
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					qstn);
			theVList.setAdapter(adapter);
		}
	}

	/**
	 * Submit the question to the database
	 */
	private void postQuestion() {
		Category selectedCat = cat.get(spinner.getSelectedItemPosition()-1);
		System.out.println("category selected is : " + spinner.getSelectedItemPosition());
		db.addQuestion(1, question.getText().toString(), currentCHO.getId(),
				selectedCat.getID());// question.getText();
		Toast.makeText(
				PostQuestionActivity.this,
				"Your question has submitted under "
						+ selectedCat.getCategoryName(),
				Toast.LENGTH_SHORT).show();
		spinner.setSelection(0);
		question.setText("");
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onResume() {
		refreshData();
		super.onResume();
	}

}
