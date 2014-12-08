package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.FamilyPlanningRecord;
import com.ashesi.cs.mhealth.data.FamilyPlanningRecords;
import com.ashesi.cs.mhealth.data.OPDCaseRecord;
import com.ashesi.cs.mhealth.data.OPDCaseRecords;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;
import com.ashesi.cs.mhealth.data.VaccineRecord;
import com.ashesi.cs.mhealth.data.VaccineRecords;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DetailReport extends Activity implements OnClickListener {

	int currentView=-1;
	int page=0;
	AdapterContextMenuInfo info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_report);
		
		Button next=(Button)findViewById(R.id.buttonNext);
		next.setOnClickListener(this);
		Button prev=(Button)findViewById(R.id.buttonPrev);
		prev.setOnClickListener(this);
		
		ListView list=(ListView)this.findViewById(R.id.listView);
		this.registerForContextMenu(list);
		
		Intent intent=this.getIntent();
		currentView=intent.getIntExtra("currentView", -1);
		switch(currentView){
			case -1:
				showStatus("no view");
				break;
			case 0:
				showOPDDetail();
				break;
			case 1:
				showOPDDetail();
				break;
			case 2:
				showVaccineDetail();
				break;
			case 3:
				showFamilyPlanningDetail();
				break;	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_report, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_community_members_record_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    info = (AdapterContextMenuInfo) item.getMenuInfo();
	    remove();
	    
	    return true;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.buttonNext:
				next();
				break;
			case R.id.buttonPrev:
				prev();
				break;
		}
	}
	
	protected void next(){
		page=page+1;
		switch(currentView){
			case -1:
				break;
			case 0:
				showOPDDetail();
				break;
			case 1:
				showVaccineDetail();
				break;
			case 2:
				showFamilyPlanningDetail();
				break;	
		}
	}
	
	protected void prev(){
		if(page==0){
			return;
		}
		page=page-1;
		switch(currentView){
			case -1:
				break;
			case 0:
				showOPDDetail();
				break;
			case 1:
				showVaccineDetail();
				break;
			case 2:
				showFamilyPlanningDetail();
				break;	
		}
	}
	
	protected void showOPDDetail(){
		Intent intent=this.getIntent();
		int month=intent.getIntExtra("month", 0);
		int ageGroup=intent.getIntExtra("ageGroup", 0);
		int year=intent.getIntExtra("year", 0);
		int gender=intent.getIntExtra("gender", 0);
		String strGender=null;
		if(gender==1){
			strGender="male";
		}else if(gender==2){
			strGender="female";
		}else {
			strGender="all";
		}
		showStatus("Showing OPD detail ");
		
		OPDCaseRecords records=new OPDCaseRecords(this.getApplicationContext());
		ArrayList<OPDCaseRecord> listRecords=records.getMonthReportDetail(month, year, ageGroup, strGender, page);
		ArrayAdapter<OPDCaseRecord> adapter=new ArrayAdapter<OPDCaseRecord>(this,android.R.layout.simple_list_item_activated_1,listRecords);
		
		ListView list=(ListView)this.findViewById(R.id.listView);
		list.setAdapter(adapter);
		
	}
		
	protected void showVaccineDetail(){
		Intent intent=this.getIntent();
		int month=intent.getIntExtra("month", 0);
		int ageGroup=intent.getIntExtra("ageGroup", 0);
		int year=intent.getIntExtra("year", 0);
		int gender=intent.getIntExtra("gender", 0);
		String strGender=null;
		if(gender==1){
			strGender="male";
		}else if(gender==2){
			strGender="female";
		}else {
			strGender="all";
		}
		showStatus("Showing Vaccine detail ");
		
		VaccineRecords records=new VaccineRecords(this.getApplicationContext());
		ArrayList<VaccineRecord> listRecords=records.getMonthlyVaccinationRecord(month, year, ageGroup, strGender, page);
		ArrayAdapter<VaccineRecord> adapter=new ArrayAdapter<VaccineRecord>(this,android.R.layout.simple_list_item_activated_1,listRecords);
		
		ListView list=(ListView)this.findViewById(R.id.listView);
		list.setAdapter(adapter);
		
	}
	
	protected void showFamilyPlanningDetail(){
		Intent intent=this.getIntent();
		int month=intent.getIntExtra("month", 0);
		int ageGroup=intent.getIntExtra("ageGroup", 0);
		int year=intent.getIntExtra("year", 0);
		int gender=intent.getIntExtra("gender", 0);
		String strGender=null;
		if(gender==1){
			strGender="male";
		}else if(gender==2){
			strGender="female";
		}else {
			strGender="all";
		}
		showStatus("Showing Family Planning record detail ");
		FamilyPlanningRecords records=new FamilyPlanningRecords(this.getApplicationContext());
		ArrayList<FamilyPlanningRecord> listRecords=records.getMonthlyFamilyPlanningRecords(month, year, ageGroup, strGender, page);
		ArrayAdapter<FamilyPlanningRecord> adapter=new ArrayAdapter<FamilyPlanningRecord>(this,android.R.layout.simple_list_item_activated_1,listRecords);
		
		ListView list=(ListView)this.findViewById(R.id.listView);
		list.setAdapter(adapter);
	}
	
	protected void showError(String msg){
		TextView textStatus=(TextView)this.findViewById(R.id.textStatus);
		textStatus.setText(msg);
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_error));
		
	}
	
	protected void showStatus(String msg){
		TextView textStatus=(TextView)this.findViewById(R.id.textStatus);
		textStatus.setText(msg);
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_black));
		
	}

	protected void removeRecord(){
		switch(currentView){
			case 0:
				removeOPDCaseRecord(info);
				break;
			case 1:
				removeVaccineRecord(info);
				break;
			case 2:
				removeFamilyPlanningRecord(info);
				break;
			default:
				break;
		}
	}
	
	protected void removeOPDCaseRecord(AdapterContextMenuInfo info){
		ListView list=(ListView)this.findViewById(R.id.listView);

		ArrayAdapter<OPDCaseRecord> adapter=(ArrayAdapter<OPDCaseRecord>)list.getAdapter();
		OPDCaseRecord obj=adapter.getItem(info.position);
		OPDCaseRecords records=new OPDCaseRecords(getApplicationContext());
		records.removeOPDRecord(obj.getRecNo());
		//reload the page
		showOPDDetail();

	}

	protected void removeVaccineRecord(AdapterContextMenuInfo info){
		ListView list=(ListView)this.findViewById(R.id.listView);

		ArrayAdapter<VaccineRecord> adapter=(ArrayAdapter<VaccineRecord>)list.getAdapter();
		VaccineRecord obj=adapter.getItem(info.position);
		VaccineRecords records=new VaccineRecords(getApplicationContext());
		records.reomveRecord(obj.getId());
		//reload the page
		showVaccineDetail();
	}
	
	protected void removeFamilyPlanningRecord(AdapterContextMenuInfo info){
		ListView list=(ListView)this.findViewById(R.id.listView);

		ArrayAdapter<FamilyPlanningRecord> adapter=(ArrayAdapter<FamilyPlanningRecord>)list.getAdapter();
		FamilyPlanningRecord obj=adapter.getItem(info.position);
		FamilyPlanningRecords records=new FamilyPlanningRecords(getApplicationContext());
		records.reomveRecord(obj.getId());
		//reload the page
		showFamilyPlanningDetail();
	}
	
	protected void remove(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ListView list=(ListView)this.findViewById(R.id.listView);
		
		String str=list.getItemAtPosition(info.position).toString();
		builder.setMessage("Are you sure you want to remove the record? "+str );
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   removeRecord();
		        	   dialog.dismiss();
		           }
		       });
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
