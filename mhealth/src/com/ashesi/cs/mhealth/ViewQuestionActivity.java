package com.ashesi.cs.mhealth;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Answer;
import com.ashesi.cs.mhealth.knowledge.Answers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ViewQuestionActivity extends Activity implements OnClickListener{

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_question);
		
		//Style actionBar
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#428bca"));
		ab.setBackgroundDrawable(colorDrawable);
				
		Intent intent=getIntent();
		
		String choName=intent.getStringExtra("ChoName");
		String question = intent.getStringExtra("Question");
		String date = intent.getStringExtra("datetime");
		String cat = intent.getStringExtra("category");
		String qID = intent.getStringExtra("guid");
		
		TextView choN = (TextView)findViewById(R.id.choName1);
		choN.setText(choName + " - " + cat);
		TextView question1 = (TextView)findViewById(R.id.question1);
		question1.setText(question);
		TextView date1 = (TextView)findViewById(R.id.date1);
		date1.setText("Posted on - " + date);
		
		Answers ansDB = new Answers(getApplicationContext());
		Answer answer = ansDB.getByQuestion(qID);

		TextView ans = (TextView)findViewById(R.id.answer);
		if(answer == (null)){
			ans.setText("Answer still pending.");
		}else{
			ans.setText( "Answer: "+ answer.getAnswer() + " \n " + 
					"Answered on: " + answer.getDate());
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
