package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	CHO currentCHO;
	Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
				
		TextView textStatus=(TextView)findViewById(R.id.textStatus);
		//make sure database is created
		DataClass dc=new DataClass(getApplicationContext());
		
		View buttonOpenClose=findViewById(R.id.buttonMainLoginStart);
		buttonOpenClose.setOnClickListener(this);
		View buttonOpenRecord=findViewById(R.id.buttonMainOpenRecord);
		buttonOpenRecord.setOnClickListener(this);
        View buttonOpenKnowledge = findViewById(R.id.buttonMainKnowledge);
        buttonOpenKnowledge.setOnClickListener(this);
		
		loadSpinner();
		textStatus.setText("application ready");
		
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		// TODO Auto-generated method stub
				
			case R.id.buttonMainOpenRecord:
				Intent intent=new Intent(this,CommunityActivity.class);
				intent.putExtra("choId", currentCHO.getId());
				startActivity(intent);
				break;
			case R.id.buttonMainLoginStart:
				loginAndStart();
				break;
			case R.id.buttonMainKnowledge:
				Intent knowledge = new Intent(this,KnowledgeActivity.class);
				knowledge.putExtra("choId", currentCHO.getId());
				startActivity(knowledge);
				break;
		}
	}
	
	public void loginAndStart(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerSelectCHO);
		currentCHO=(CHO)spinner.getSelectedItem();
		findViewById(R.id.buttonMainOpenRecord).setEnabled(true);
		findViewById(R.id.buttonMainKnowledge).setEnabled(true);
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//super.onCreateOptionsMenu(menu);
		//menu.add(0,Menu.FIRST,Menu.NONE,R.string.TopicFilter); 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()){
			case R.id.itemMainActionBarSynch:
				synch();	//you can synch with out logging in
				return true;
			case R.id.itemOPDCases:	
				Log.d("MainActivity","starting activity");
				Intent intent=new Intent(this,CommunityActivity.class);
				intent.putExtra("choId", currentCHO.getId());
				startActivity(intent);
				return true;
			case R.id.action_settings:
				Intent intent2=new Intent(this, SettingsActivity.class);
				startActivity(intent2);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		this.getMenuInflater().inflate(R.menu.menu_topic_source_options, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		return super.onContextItemSelected(item);
		
	}
	
	/**
	 * opens synch activity
	 * @return
	 */
	private boolean synch(){
		Intent intent=new Intent(this,Synch.class);
		startActivity(intent);

		return true;
	}
	
	/**
	 * load CHO list into spinner
	 */
	private void loadSpinner(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerSelectCHO);
		CHOs chos=new CHOs(getApplicationContext());
		ArrayList<CHO> list=chos.getAllCHOs();
		ArrayAdapter<CHO> adapter=new ArrayAdapter<CHO>(getApplicationContext(),android.R.layout.simple_spinner_item,list);
		spinner.setAdapter(adapter); 
	}
	
	
	


}


