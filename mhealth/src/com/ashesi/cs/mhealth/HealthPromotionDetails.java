package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.HealthPromotion;
import com.ashesi.cs.mhealth.data.HealthPromotionDetailsAdapter;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HealthPromotionDetails extends Activity {

	private ListView detailsList;
	private ImageView image;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_health_promotion_details);
		
		detailsList=(ListView) findViewById(R.id.listView1);
		
		//image=(ImageView) findViewById(R.id.imageView1);
	
		
		HealthPromotionsReport activity=new HealthPromotionsReport();
		int id=activity.selectedHealthPromotionID;
		
		Intent i=getIntent();
		Bundle b=i.getExtras();
		int myId=b.getInt("ID");
		
		String[] headers={"--------"};
	
	if(myId!=0){
			HealthPromotions healthPromosDetails = new HealthPromotions(getApplicationContext());
			ArrayList<HealthPromotion> listDetails;
			listDetails = healthPromosDetails.getDetails(myId);

			final HealthPromotionDetailsAdapter adapter = new HealthPromotionDetailsAdapter(getApplicationContext());
			adapter.setList(listDetails);
			adapter.notifyDataSetChanged();
			detailsList.setAdapter(adapter);
		}
			
		else{
			ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, headers);
			detailsList.setAdapter(adapter);
			//image.setVisibility(View.GONE);
		}
	}
	public void onResume(){
		super.onResume();

	}
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {


	}

	public void onNothingSelected(AdapterView<?> parent) {


	}

	public void onClick(View v) {


	}

}
