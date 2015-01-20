package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.Tasks;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	CHO currentCHO;
	Menu menu;
	private int choId=0;
	private Button buttonOpenClose;
	public static String subdistrictId;
	public TextView textStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
				
		textStatus=(TextView)findViewById(R.id.textStatus);
		new DataClass(getApplicationContext());
			
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
		File folder = new File(DataClass.getApplicationFolderPath());
				
		if(!folder.exists()){
			folder.mkdir();
		}
		folder=new File(DataClass.getApplicationFolderPath() + "hp");
		if(!folder.exists()){
			folder.mkdir();
		}
	
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if(currentCHO!=null){
			ListView list=(ListView)findViewById(R.id.list);
			Tasks tasks=new Tasks(this);
			list.setAdapter(tasks.getTaskAdapterForTheMonth());
		}
		super.onStart();
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
				Intent intentHealthPromotion=new Intent(this,HealthPromotionList.class);
				intentHealthPromotion.putExtra("choId", currentCHO.getId());
				startActivity(intentHealthPromotion);
				break;
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
		//findViewById(R.id.buttonAddCommunity).setEnabled(true);
		textStatus.setText("application open");
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_black));
		ListView list=(ListView)findViewById(R.id.list);
		Tasks tasks=new Tasks(this);
		list.setAdapter(tasks.getTaskAdapterForTheMonth());
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				openRecordFromTask(position);
			}
		});
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
		ListView list=(ListView)findViewById(R.id.list);
		list.setAdapter(null);
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
		if(choId==0){
			return false;
		}
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
		intent.putExtra("choId",choId);
		startActivity(intent);

		return true;
	}
	
	private void openRecordFromTask(int position){
		Intent intent=new Intent();
		intent.putExtra("choId", currentCHO.getId());
		switch(position){
			case 1:
				intent.setClass(this,CommunityActivity.class);
				intent.putExtra("searchOption", 6);
				startActivity(intent);
				break;
			case 3:
				intent.setClass(this,CommunityActivity.class);
				intent.putExtra("searchOption", 4);
				startActivity(intent);
				break;
			case 2:
				intent.setClass(this,CommunityActivity.class);
				intent.putExtra("searchOption", 8);
				startActivity(intent);
				break;
			case 4:
				intent.setClass(this,TasksActivity.class);
				startActivity(intent);
				break;
			case 6:
				intent.setClass(this,CommunityActivity.class);
				intent.putExtra("searchOption", 0);
				startActivity(intent);
				break;
		}
	
	
	}
	
	@Override
	public void onItemSelected(AdapterView<?> view, View view1, int position, long arg3) {
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> view) {
		
		
	}


}


