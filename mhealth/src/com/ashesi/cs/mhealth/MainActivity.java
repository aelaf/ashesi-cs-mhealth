package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class MainActivity extends Activity implements OnClickListener {
	CHO currentCHO;
	Menu menu;
	ResourceMaterials resMaterials;

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
		
		//Create Folder for Resource materials
		File folder = new File(Environment.getExternalStorageDirectory() + "/mHealth");
				
		if(!folder.exists()){
			folder.mkdir();
		}
		
		loadResources();
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
	
	/**
	 * Upload all the added files to the Database
	 */
	private void loadResources(){
		File upload = new File(Environment.getExternalStorageDirectory() + "/mHealth/resourceslist.txt");
		resMaterials = new ResourceMaterials(this);
		try {
			if(upload.exists()){
				Scanner scan = new Scanner(upload);
				String fileDetails;
				String delimit = "[,]";
				while(scan.hasNext()){
					fileDetails = scan.nextLine();
					String [] results = fileDetails.split(delimit);
					Toast.makeText(getApplicationContext(),results[0], Toast.LENGTH_LONG).show();
					resMaterials.addResMat(Integer.parseInt(results[0]), 
							               Integer.parseInt(results[1]), 
							               Integer.parseInt(results[2]), 
							               (Environment.getExternalStorageDirectory() + "/mHealth/" + results[3]), 
							               results[4]);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


