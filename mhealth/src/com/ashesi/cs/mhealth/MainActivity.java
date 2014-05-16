package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	CHO currentCHO;
	Menu menu;
	private DataClass dc;
	private int choId=0;
	private Button buttonOpenClose;
	private ResourceMaterials resMaterials;
	
	public static String subdistrictId;
	public TextView textStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
				
		textStatus=(TextView)findViewById(R.id.textStatus);
		//make sure database is created
		dc=new DataClass(getApplicationContext());
			
		buttonOpenClose=(Button)findViewById(R.id.buttonMainLoginStart);
		buttonOpenClose.setOnClickListener(this);
		
		View buttonOpenRecord=findViewById(R.id.buttonMainOpenRecord);
		buttonOpenRecord.setOnClickListener(this);
		

		View buttonAddHealthPromotion=findViewById(R.id.buttonHealthPromotions);
		buttonAddHealthPromotion.setOnClickListener(this);
		
		View buttonOpenKnowledge = findViewById(R.id.buttonMainKnowledge);
        buttonOpenKnowledge.setOnClickListener(this);
		
		choId=0;
		textStatus.setText("enter your name and click open");
		
		//Create Folder for Resource materials
		File folder = new File(Environment.getExternalStorageDirectory() + "/mHealth");
				
		if(!folder.exists()){
			folder.mkdir();
		}
		
		/*Load the current available resources from its text file into the database
		 * This is a description of the resources.
		 */
		loadResources();
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
				
			case R.id.buttonMainOpenRecord:
				Intent intent=new Intent(this,CommunityActivity.class);
				intent.putExtra("choId", currentCHO.getId());
				startActivity(intent);
				break;
			case R.id.buttonMainLoginStart:
				loginAndStart();
				break;
				
			case R.id.buttonHealthPromotions:
				Intent intent1=new Intent(this,HealthPromotionsActivity.class);
				startActivity(intent1);
			case R.id.buttonMainKnowledge:
				Intent knowledge = new Intent(this,KnowledgeActivity.class);
				knowledge.putExtra("choId", currentCHO.getId());
				startActivity(knowledge);
				break;				
		}
	}
	
	public void loginAndStart(){
		if(choId!=0){
			logout();
			return;
		}
		EditText editCHO=(EditText)findViewById(R.id.editCHOName);
		if(editCHO.getText().length()==0){
			textStatus.setText("enter your user name to open");
			textStatus.setTextColor(this.getResources().getColor(R.color.text_color_error));
			return;
		}
		CHOs chos=new CHOs(getApplicationContext());
		currentCHO=chos.getCHO(editCHO.getText().toString());
		if(currentCHO==null){
			textStatus.setText("the name you entred is not found");
			textStatus.setTextColor(this.getResources().getColor(R.color.text_color_error));
			return;
		}
		editCHO.setEnabled(false);
		choId=currentCHO.getId();
		buttonOpenClose.setText(R.string.close);
		findViewById(R.id.buttonMainOpenRecord).setEnabled(true);
		findViewById(R.id.buttonMainKnowledge).setEnabled(true);
		findViewById(R.id.buttonHealthPromotions).setEnabled(true);
		findViewById(R.id.buttonAddCommunity).setEnabled(true);
	}
	
	private void logout(){
		currentCHO=null;
		choId=0;

		buttonOpenClose.setText(R.string.open);
		findViewById(R.id.buttonMainOpenRecord).setEnabled(false);
		findViewById(R.id.buttonMainKnowledge).setEnabled(false);
		findViewById(R.id.buttonHealthPromotions).setEnabled(false);
		findViewById(R.id.buttonAddCommunity).setEnabled(false);
		
		EditText editCHO=(EditText)findViewById(R.id.editCHOName);
		editCHO.setEnabled(true);
		editCHO.setText("");
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
	

	@Override
	public void onItemSelected(AdapterView<?> view, View view1, int position, long arg3) {
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
		
		
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


