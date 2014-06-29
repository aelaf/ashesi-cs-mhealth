package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.HealthPromotionsReport.ReportFragment;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class HealthPromotionReportDetails extends Activity implements OnClickListener, OnItemSelectedListener{
	
	//VaccineAdapter adapter; 
	

	public static final String ARG_SECTION_NUMBER = "section_number";
	
	private TextView topic_txt;
	private TextView date_txt;
	private TextView number_txt;
	private ImageView image;
	private TextView remarks_txt;
	private TextView audience_txt;
	private ListView detailsList;
	private Bitmap bitmap;
	private String imagepath;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_health_promotion_details);


	    topic_txt=(TextView) findViewById(R.id.txt_topic);
		detailsList=(ListView) findViewById(R.id.listView1);
		date_txt=(TextView) findViewById(R.id.txt_date);
		number_txt=(TextView) findViewById(R.id.txt_number);
		image=(ImageView) findViewById(R.id.imageView1);
		audience_txt=(TextView) findViewById(R.id.txt_target);
		remarks_txt=(TextView) findViewById(R.id.txt_remarks);
	 
	ReportFragment report=new ReportFragment();
	HealthPromotions healthPromos=new HealthPromotions(this);
	ArrayList<String> list;
	list=healthPromos.getDetails(report.id);
	String[] headers={"--------"};

	if(list==null){
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, headers);
		detailsList.setAdapter(adapter);
		image.setVisibility(View.GONE);
	}else{
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		detailsList.setAdapter(adapter);
		imagepath=list.get(7);
		 bitmap=BitmapFactory.decodeFile(imagepath);
		image.setImageBitmap(bitmap);
		
	}
	
	 
	}

	
	public void onResume(){
		super.onResume();
		
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	
		
	}
	@Override
	public void onClick(View v) {
		
		
	}
	
	}
