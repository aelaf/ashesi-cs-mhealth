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

public class PostQuestionActivity extends Activity implements OnClickListener{
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
		Intent intent=getIntent();
		int choId=intent.getIntExtra("choId", 0);
		CHOs chos=new CHOs(getApplicationContext());
		currentCHO=chos.getCHO(choId);
	
		//Question TextBox;
		question = (EditText)findViewById(R.id.question);
		//Get the list of questions
		theVList = (ListView)findViewById(R.id.listView1);
		
		//Load the spinner details
		db = new Questions(this);
		db1 = new Categories(this);
		
		 qs = db.getAllQuestions();
		 cat = db1.getAllCategories();
		 //refreshData();
		for(Question q : qs){
			String log = q.toString();
			Log.d("Question: ", log);
		}
		
		list = new ArrayList<String>();
		sortList = new ArrayList<String>();
		sortList.add("Sort by:");
		list.add("Choose a Category");
		for(Category ca : cat){
			String lo = ca.getCategoryName();
			System.out.println(lo);
			Log.d("Category: ", lo);
			list.add(lo);
			sortList.add(lo);
		}
		//Populate Categories spinner
		addItemsOnSpinner();
		addListenerOnButton();
		addListenerOnList();
	}
	
	private void addListenerOnList() {
		// TODO Auto-generated method stub

		theVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(arg0.getContext(), 
						"OnItemSelectedListener : " + arg0.getItemAtPosition(arg2).toString(),
						Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(), ViewQuestionActivity.class);
				CHOs ch = new CHOs(getApplicationContext());
				i.putExtra("ChoName", ch.getCHO(qs.get(arg2).getChoId()).getFullname());
				i.putExtra("Question", qs.get(arg2).getContent());
				i.putExtra("datetime", qs.get(arg2).getDate());
				i.putExtra("category", cat.get(arg2).getCategoryName());
				startActivity(i);
			}		
		});
	}

	private void addListenerOnButton() {
		// TODO Auto-generated method stub
		spinner = (Spinner)findViewById(R.id.spinner1);
		spinner.setPrompt("Choose a Category");
		btn = (Button)findViewById(R.id.save_btn);
		
		btn.setOnClickListener(new OnClickListener(){
		
		@Override
		public void onClick(View v){
			if(spinner.getSelectedItemPosition() < 1){
				Toast.makeText(PostQuestionActivity.this, "Please choose a category " 
						, Toast.LENGTH_SHORT).show();
			}else if(question.getText().toString() == "" || spinner.getSelectedItemPosition() < 1){
				Toast.makeText(PostQuestionActivity.this, "Please type a question " 
						, Toast.LENGTH_SHORT).show();
			}else{
				postQuestion();
				Toast.makeText(PostQuestionActivity.this, "Your question has submitted under " + cat.get(spinner.getSelectedItemPosition()).getCategoryName(), Toast.LENGTH_SHORT).show();
				refreshData();
			}
		}
		
		});
	}

	/**
	 * Dynamically populate categories spinner
	 */
	public void addItemsOnSpinner(){
		spinner2 = (Spinner)findViewById(R.id.spinner2);
		spinner = (Spinner)findViewById(R.id.spinner1);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
		//Sort Drop down
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortList);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter2);
	}
	
	
	/**
	 * Refresh data from database
	 */
	private void refreshData(){
		CHOs ch = new CHOs(getApplicationContext());
		qs = db.getAllQuestions();
		String [] qstn = new String[qs.size()];
		for(int i=0; i<qs.size(); i++){
			String temp = ch.getCHO(qs.get(i).getChoId()).getFullname();
			temp += " - " + qs.get(i).getContent();
			temp += " - " + cat.get(i).getCategoryName();
			qstn[i] = temp;
		}
		adapter = new ArrayAdapter<String>(this,
	              android.R.layout.simple_list_item_1, android.R.id.text1, qstn);
		 theVList.setAdapter(adapter);
	}
	
	/**
	 * Submit the question to the database
	 */
	private void postQuestion(){
		int catid= (int)spinner.getSelectedItemId();
		db.addQuestion(1,question.getText().toString(), currentCHO.getId(),catid);//question.getText();
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
