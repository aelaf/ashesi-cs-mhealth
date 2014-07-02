package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.MainActivity;
import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.GPSTracker;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("SimpleDateFormat") 
public class HealthPromotionsActivity extends Activity implements OnClickListener {
	private CHO currentCHO;
	private Button set_location_btn;
	private ImageButton image_upload_btn;
	private Button save_btn;
	private TextView date_txt;
	private int year;
	private int month;
	private int day;
	private TextView month_txt;
	private EditText longitude_txt;
	private EditText latitude_txt;
	private EditText venue_txt;
	private EditText topic_txt;
	private EditText method;
	private EditText target_audience;
	private EditText audience_number;
	private EditText remarks_txt;
	private EditText image_url_txt;
	private GPSTracker gps;
	private double latitude_double;
	private double longitude_double;
	//private DataClass dc;
	private HealthPromotions HealthPromo;
	private Uri selectedImageUri;
	private String imagepath;
	private Bitmap bitmap;
	private String fileNameValue;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_health_promotion);
	   // dc=new DataClass(getApplicationContext());
	    
	   HealthPromo=new HealthPromotions(this.getApplicationContext());
	    set_location_btn=(Button) findViewById(R.id.location_btn);
	    set_location_btn.setOnClickListener(this);	    
	    
	    image_upload_btn=(ImageButton) findViewById(R.id.camera_btn);
	    image_upload_btn.setOnClickListener(this);
	    
	    save_btn=(Button) findViewById(R.id.save_btn);
	    save_btn.setOnClickListener(this);
	    
	    date_txt=(TextView) findViewById(R.id.date_txt);
	    date_txt.setTextColor(Color.BLACK);
	    date_txt.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
	    final Calendar c = Calendar.getInstance();
	    year = c.get(Calendar.YEAR);
	    month = (c.get(Calendar.MONTH)+1);
	    day = c.get(Calendar.DAY_OF_MONTH);
	    date_txt.setText(year+"-"+month+"-"+day);
	    
	    month_txt=(TextView) findViewById(R.id.month_txt);
	    month_txt.setTextColor(Color.BLACK);
	    month_txt.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
	    java.text.SimpleDateFormat df3 = new java.text.SimpleDateFormat("MMMM");
		month_txt.setText(df3.format(c.getTime()));
		
		longitude_txt=(EditText) findViewById(R.id.longitude_txt);
		longitude_txt.setKeyListener(null);
		
		latitude_txt=(EditText) findViewById(R.id.latitude_txt);
		latitude_txt.setKeyListener(null);
		
		venue_txt=(EditText) findViewById(R.id.venue_txt);
		topic_txt=(EditText) findViewById(R.id.topic_txt);
		method=(EditText) findViewById(R.id.method);
		target_audience=(EditText) findViewById(R.id.target_audienc_txt);
		audience_number=(EditText) findViewById(R.id.audience_number_txt);
		remarks_txt=(EditText) findViewById(R.id.remarks_txt);
		image_url_txt=(EditText) findViewById(R.id.image_url_txt);
		
		
		Intent intent=getIntent();
		int choId=intent.getIntExtra("choId", 0);
		CHOs chos=new CHOs(getApplicationContext());
		currentCHO=chos.getCHO(choId);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.save_btn:	
			//create a method for this part of the switch, and call it
			//date has to be written in yyyy-mm-dd format 
			String date=date_txt.getText().toString();
			String venue=venue_txt.getText().toString();
			String topic=topic_txt.getText().toString();
			String method_txt=method.getText().toString();
			String target=target_audience.getText().toString();
			String number=audience_number.getText().toString();
			String remarks=remarks_txt.getText().toString();
			String month=month_txt.getText().toString();
			String latitude_str=latitude_txt.getText().toString();
			String longitude_str=longitude_txt.getText().toString();
			String image=image_url_txt.getText().toString();
			String cho_id=Integer.toString(currentCHO.getId());
			String subdistrict_id=Integer.toString(currentCHO.getSubdistrictId());
			
			
			HealthPromo.addHealthPromotion(date, venue, topic, method_txt, target, number, remarks, month, latitude_str, longitude_str, image, cho_id, subdistrict_id);
			 Toast.makeText(getApplicationContext(), "Health Promotion Added", Toast.LENGTH_LONG).show();
			break;
		
		case R.id.location_btn:
			gps = new GPSTracker(HealthPromotionsActivity.this);
            // check if GPS enabled
            if(gps.canGetLocation()){
            	

                latitude_double = gps.getLatitude();
                longitude_double = gps.getLongitude();

                // \n is for new line
              longitude_txt.setText(String.valueOf(longitude_double));
              latitude_txt.setText(String.valueOf(latitude_double));
              //  Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
			break;
			
		case R.id.camera_btn:
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
			break;
		}
		
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         
	        if (requestCode == 1 && resultCode == RESULT_OK) {
	            //Bitmap photo = (Bitmap) data.getData().getPath();
	           
	            selectedImageUri = data.getData();
	            imagepath = getPath(selectedImageUri);
	            bitmap=BitmapFactory.decodeFile(imagepath);
	          //  imageview.setImageBitmap(bitmap);
	      
	          List<String> fileArray= new ArrayList<String>(Arrays.asList(imagepath.split("/")));
	    		//fileArraySplit=imagepath.split("/") ;
	    		
	    	//	fileArray{fileArraySplit};
	          fileNameValue=fileArray.get(fileArray.size()-1);
	          image_url_txt.setText(imagepath);
	             
	        }
	    }
	         @SuppressWarnings("deprecation")
			public String getPath(Uri uri) {
	                String[] projection = { MediaStore.Images.Media.DATA };
	                Cursor cursor = managedQuery(uri, projection, null, null, null);
	                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	                cursor.moveToFirst();
	              
	                return cursor.getString(column_index);
	            }
	         
	    

}
