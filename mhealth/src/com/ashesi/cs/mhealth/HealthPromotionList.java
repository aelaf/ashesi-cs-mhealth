package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.Calendar;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.HealthPromotion;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class HealthPromotionList extends Activity implements OnClickListener, OnItemSelectedListener, OnItemClickListener {

	

	private TextView textStatus;
	private int choId;
	private CHO currentCHO; 
	ArrayAdapter<HealthPromotion> adapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_promotion_list);
		textStatus=(TextView)findViewById(R.id.textStatus);
		textStatus.setText("health promotions");
		
		Intent intent=this.getIntent();
		choId=intent.getIntExtra("choId", 0);
		CHOs chos=new CHOs(this.getApplicationContext());
		currentCHO=chos.getCHO(choId);
		
		Button button=(Button)findViewById(R.id.buttonAdd);
		button.setOnClickListener(this);
		
		fillMonthSpinner();
		fillYearSpinner();
		
		ListView listView=(ListView)findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		
		getData();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.health_promotion_list, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.buttonAdd:
				Intent intent=new Intent(this,HealthPromotionsActivity.class); 
				intent.putExtra("choId", choId);
				startActivity(intent);
				break;
		
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View v, int arg2, long arg3) {
		// TODO Auto-generated method stub
		getData();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(adapter==null){
			return;
		}
		
		HealthPromotion healthPromotion=adapter.getItem(position);
		Intent intent=new Intent(this,HealthPromotionsActivity.class); 
		intent.putExtra("choId", choId);
		intent.putExtra("healthPromotionId",healthPromotion.getId());
		startActivity(intent);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		getData();
		super.onResume();
	}
	
	private void fillMonthSpinner(){
		String[] months={"this month","whole year","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		Spinner spinner=(Spinner)findViewById(R.id.spinnerMonth);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,months);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(0);
	}
	
	private void fillYearSpinner(){
		String[] strYears=new String[2];
	
		int year=Calendar.getInstance().get(Calendar.YEAR); //this year
		strYears[0]=Integer.toString(year);
		strYears[1]=Integer.toString((year-1));
		
		Spinner spinner=(Spinner)findViewById(R.id.spinnerYear);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.mhealth_simple_list_item,strYears);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(0);
	}

	private int getSelectedMonth(){
		
		Spinner spinner=(Spinner)findViewById(R.id.spinnerMonth);
		int month=spinner.getSelectedItemPosition();
		if(month<0){
			month=0;
			spinner.setSelection(month);
		}

		return month;
	}
	
	private int getSelectedYear(){
		
		int year=Calendar.getInstance().get(Calendar.YEAR); //this year
		Spinner spinner=(Spinner)findViewById(R.id.spinnerYear);
		int n=spinner.getSelectedItemPosition();
		if(n<0){
			n=0;
			spinner.setSelection(n);
		}
	
		return year;
	}

	private boolean getData(){
		int month=getSelectedMonth();
		int year=getSelectedYear();
		ListView listView=(ListView)findViewById(R.id.list);

		HealthPromotions healthPromtions=new HealthPromotions(this.getApplicationContext());
		ArrayList<HealthPromotion> list=healthPromtions.getHealthPromotions(month,year);
		
		if(list==null){
			return false;
		}
		adapter=new ArrayAdapter<HealthPromotion>(this,R.layout.mhealth_simple_spinner,list);
		listView.setAdapter(adapter);
		
		return true;
	}


}
