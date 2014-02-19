package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ViewQuestionActivity extends Activity implements OnClickListener{

	private ArrayList<String> details;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_question);
		Intent intent=getIntent();
		
		String choName=intent.getStringExtra("ChoName");
		String question = intent.getStringExtra("Question");
		String date = intent.getStringExtra("datetime");
		String cat = intent.getStringExtra("category");
		
		//CHOs chos=new CHOs(getApplicationContext());
		//currentCHO=chos.getCHO(choId);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
