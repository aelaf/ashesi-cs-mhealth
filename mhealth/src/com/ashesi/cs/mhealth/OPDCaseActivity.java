package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.OPDCase;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.id;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class OPDCaseActivity extends Activity {

	ArrayList<OPDCase> listOPDCases;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opdcase);
		Log.d("OPDCaseActivity.onCreate","creating object");
		
		TextView textODPCasesStatus=(TextView)findViewById(R.id.textViewOpdCaseStatus);
		textODPCasesStatus.setText("object created");
		loadOPDcases();
		loadCommunities();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opdcase, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.opdCasesSynch:
				//show filter
				synch();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean synch(){
		OPDCases opdCases=new OPDCases(this.getApplicationContext());
		return opdCases.threadedDownload();
	}
	
	public boolean loadOPDcases(){
		Spinner spinnerCategories=(Spinner)findViewById(R.id.spinnerOPDCaseCategories);
		int category=spinnerCategories.getSelectedItemPosition();
		Spinner spinner=(Spinner)findViewById(R.id.spinnerOPDCases); 
		OPDCases opdCases=new OPDCases(this.getApplicationContext());
		listOPDCases=opdCases.getAllOPDCases(category);
		ArrayAdapter<OPDCase> adapter=new ArrayAdapter<OPDCase>(this, android.R.layout.simple_dropdown_item_1line,listOPDCases);
		spinner.setAdapter(adapter);
		return true;
		
	}
	
	public boolean loadCommunities(){
//		String[] communities={"Community 1","Community 2"};
//		Spinner spinner=(Spinner)findViewById(R.id.spinnerSelectOPDCase); 
//		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
//		adapter.addAll(communities);
//		spinner.setAdapter(adapter);
		
		return false;
	}
}
