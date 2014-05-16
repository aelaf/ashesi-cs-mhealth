package com.ashesi.cs.mhealth;

import com.ashesi.cs.mhealth.data.GPSTracker;
import com.ashesi.cs.mhealth.data.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddCommunityActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	private EditText latitude_txt;
	private EditText longitude_txt;
	private Button set_location_btn;
	private EditText household_txt;
	private EditText population_txt;
	private EditText community_txt;
	private Spinner subdistrict_spinner;
	private Button save_btn;
	private String[] menuItems;
	private String subdistrict_id;
	private GPSTracker gps;
	private double latitude_double;
	private double longitude_double;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.add_community);
	    
	    latitude_txt=(EditText) findViewById(R.id.txt_latitude);
	    longitude_txt=(EditText) findViewById(R.id.txt_longitude);
	    household_txt=(EditText) findViewById(R.id.txt_household);
	    population_txt=(EditText) findViewById(R.id.txt_population);
	    community_txt=(EditText) findViewById(R.id.txt_community_name);
	    
	    subdistrict_spinner=(Spinner) findViewById(R.id.spinner_subdistrict);
	    subdistrict_spinner.setOnItemSelectedListener(this);
	    
	    set_location_btn=(Button) findViewById(R.id.location_btn);
	    set_location_btn.setOnClickListener(this);
	    
	    save_btn=(Button) findViewById(R.id.submit_btn);
	    save_btn.setOnClickListener(this);
	    
	    menuItems=new String[]{"Aburi","Pokrum"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, menuItems);
        subdistrict_spinner.setAdapter(adapter); 
	   
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.location_btn:
			gps = new GPSTracker(AddCommunityActivity.this);
            // check if GPS enabled
            if(gps.canGetLocation()){
            	

                latitude_double = gps.getLatitude();
                longitude_double = gps.getLongitude();

                // \n is for new line
              longitude_txt.setText(String.valueOf(longitude_double));
              latitude_txt.setText(String.valueOf(latitude_double));
			break;
		
            }
            
		case R.id.submit_btn:
			String latitude_value=latitude_txt.getText().toString();
			String longitude_value=longitude_txt.getText().toString();
			String household_value=household_txt.getText().toString();
			String population_value=population_txt.getText().toString();
			String community_value=community_txt.getText().toString();
			String subdistrict_id_value=subdistrict_id;
			break;
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		 subdistrict_id=null;
		if(position==0){
			subdistrict_id.equals("1");
		}else if(position==1){
			subdistrict_id.equals("2");
		}
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		
	}

}
