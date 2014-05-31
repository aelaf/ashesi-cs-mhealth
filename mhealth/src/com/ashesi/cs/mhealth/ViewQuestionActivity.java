package com.ashesi.cs.mhealth;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Answer;
import com.ashesi.cs.mhealth.knowledge.Answers;
import com.ashesi.cs.mhealth.knowledge.Questions;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewQuestionActivity extends Activity implements OnClickListener{

	private String date, question, choName, qID;
	private EditText editTxt;
	private int choID, catID, status;
	private Button label;
	private int onStartCount = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_question);
		
		//Transition between screens
		onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
        	this.overridePendingTransition(R.anim.anim_slide_in_left,
                    R.anim.anim_slide_out_left);
        } else // already created so reverse animation
        { 
            onStartCount = 2;
        }
        
		//Style actionBar
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#428bca"));
		ab.setBackgroundDrawable(colorDrawable);
				
		Intent intent=getIntent();
		status = intent.getIntExtra("status", 0);
		choName=intent.getStringExtra("ChoName");
		question = intent.getStringExtra("Question");
		date = intent.getStringExtra("datetime");
		String cat = intent.getStringExtra("category");
		qID = intent.getStringExtra("guid");
		choID = intent.getIntExtra("choId", 0);
		catID = intent.getIntExtra("catID", 0);
		
		label = (Button)findViewById(R.id.questiontitle);
		label.setText(choName + " - " + cat);
		TextView date1 = (TextView)findViewById(R.id.date1);
		date1.setText("Posted on - " + date);
		
		if(status>=1){    //This question yet to be uploaded to the server hence should not be editable
			System.out.println("the question id is: " + qID);
			TextView question1 = (TextView)findViewById(R.id.question1);
			question1.setVisibility(View.VISIBLE);
			question1.setText(question);
			
			Answers ansDB = new Answers(getApplicationContext());
			Answer answer = ansDB.getByQuestion(qID);
			
			if(status >=2){
				LinearLayout ln = (LinearLayout)findViewById(R.id.answer_div);
				ln.setVisibility(View.VISIBLE);
				TextView ans = (TextView)findViewById(R.id.answer);
				if(answer == (null)){
					
					ans.setText("Answer still pending.");
				}else{
					System.out.println("The answer is:" + answer.getAnswer());
					ans.setText( "Answer: "+ answer.getAnswer() + " \n " + 
							"Answered on: " + answer.getDate());
				}
			}
		}else if(status == 0){  //The question is yet to be uploaded hence should be editable
			editTxt = (EditText)findViewById(R.id.editquestion);
			editTxt.setVisibility(View.VISIBLE);
			editTxt.setText(question);
			
			Button saveBtn = (Button)findViewById(R.id.saveedit);
			saveBtn.setVisibility(View.VISIBLE);
			saveBtn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Questions qs = new Questions(getApplicationContext());
					qs.addQuestion(0, editTxt.getText().toString(), choID, catID, date, qID, status);
					finish();
				}			
			});
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
	
	

}
