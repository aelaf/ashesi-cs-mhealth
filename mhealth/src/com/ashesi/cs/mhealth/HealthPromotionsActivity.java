package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.ashesi.cs.mhealth.DataClass;
import com.ashesi.cs.mhealth.MainActivity;
import com.ashesi.cs.mhealth.CommunityMemberRecordActivity.DatePickerFragment;
import com.ashesi.cs.mhealth.CommunityMemberRecordActivity.VaccineFragment;
import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.GPSTracker;
import com.ashesi.cs.mhealth.data.HealthPromotion;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.Vaccine;
import com.ashesi.cs.mhealth.data.VaccineGridAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressLint("SimpleDateFormat") 
public class HealthPromotionsActivity extends Activity implements OnClickListener {




	private TextView textDate;
	private java.util.Date date;
	
	private TextView textMonth;
	private EditText editLongitude;
	private EditText editLatitude;
	private EditText editVenue;
	private EditText editTopic;
	private EditText editMethod;
	private EditText editTargetAudience;
	private EditText editAudienceNumber;
	private EditText editRemarks;
	private TextView textImage;
	private GPSTracker gps;
		
	private Button buttonSave;
	private Button buttonSetLocation;
	private Button buttonSnap;
	private Button buttonSelect;
	private Button buttonRemove;

	
	private Uri selectedImageUri;
	private Bitmap bitmap;
	private int choId=0;
	private int healthPromotionId=0;
	private boolean imageCaptured=false;
	
	private final int STATE_VIEW=0;
	private final int STATE_EDIT=1;
	private final int STATE_NEW=2;
	private int state=STATE_VIEW;
	private final int PICTURE_SNAP=100;
	private final int PICTURE_SELECT=101;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_health_promotion);
	   // dc=new DataClass(getApplicationContext());
	    
	    
	    buttonSetLocation=(Button) findViewById(R.id.location_btn);
	    buttonSetLocation.setOnClickListener(this);	    
	    
	    buttonSnap=(Button) findViewById(R.id.healthpromo_pic_snap);
	    buttonSnap.setOnClickListener(this);
	    
	    buttonSave=(Button) findViewById(R.id.save_btn);
	    buttonSave.setOnClickListener(this);
	    buttonSelect=(Button)findViewById(R.id.healthpromo_pic_select);
	    buttonSelect.setOnClickListener(this);
	    
	    buttonRemove=(Button) findViewById(R.id.healthPromotionRemove);
	    buttonRemove.setOnClickListener(this);
	    
	    textDate=(TextView) findViewById(R.id.textHealthPromotionDate);
	    textDate.setTextColor(Color.CYAN);
	    textDate.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
	    
	    Calendar c = Calendar.getInstance();
	    date=c.getTime();
	    showDate();
	    
	    textDate.setOnClickListener(this);
	   
		
		editLongitude=(EditText) findViewById(R.id.longitude_txt);
		editLongitude.setKeyListener(null);
		editLatitude=(EditText) findViewById(R.id.latitude_txt);
		editLatitude.setKeyListener(null);
		
		editVenue=(EditText) findViewById(R.id.venue_txt);
		editTopic=(EditText) findViewById(R.id.topic_txt);
		editMethod=(EditText) findViewById(R.id.method);
		editTargetAudience=(EditText) findViewById(R.id.target_audienc_txt);
		editAudienceNumber=(EditText) findViewById(R.id.audience_number_txt);
		editRemarks=(EditText) findViewById(R.id.remarks_txt);
		textImage=(TextView) findViewById(R.id.textHealthPromoPicture);
		
		choId=getIntent().getIntExtra("choId", 0);
		healthPromotionId=getIntent().getIntExtra("healthPromotionId",0);
		if(healthPromotionId!=0){
			loadHealthPromotion(healthPromotionId);
			state=STATE_VIEW;
		}else{
			state=STATE_NEW;
		}
		
		stateAction();

	}
	
	public boolean loadHealthPromotion(int id){
		HealthPromotions healthPromotions=new HealthPromotions(getApplicationContext());
		HealthPromotion healthPromotion=healthPromotions.getHealthPromotion(id);
		if(healthPromotion==null){
			return false;
		}
		
		editLongitude.setText(healthPromotion.getLongitude());
		editLatitude.setText(healthPromotion.getLatitude());
		editVenue.setText(healthPromotion.getVenue());
		editTopic.setText(healthPromotion.getTopic());
		editMethod.setText(healthPromotion.getMethod());
		editTargetAudience.setText(healthPromotion.getTargetAudience());
		editAudienceNumber.setText(Integer.toString(healthPromotion.getNumberAudience()));
		editRemarks.setText(healthPromotion.getRemarks());
		textDate.setText(healthPromotion.getFormattedDate());
		date=healthPromotion.getPromotionDateDate();
		showDate();
		
		
		selectedImageUri=Uri.parse(healthPromotion.getImage());
		imageCaptured=true;
		showImage();
		
		return true;
		
	}
	
	public void stateAction(){
		boolean enable=true;
		if(state==STATE_NEW){
			enable=true;
			buttonSave.setText("Save");
		}else if(state==STATE_EDIT){
			enable=true;
			buttonSave.setText("Save");
			buttonRemove.setVisibility(View.VISIBLE);
		}else if(state==STATE_VIEW){
			enable=false;
			buttonSave.setText("Edit");
			buttonRemove.setVisibility(View.INVISIBLE);
		}
		
		editVenue.setEnabled(enable);
		editTopic.setEnabled(enable);
		editMethod.setEnabled(enable);
		editTargetAudience.setEnabled(enable);
		editAudienceNumber.setEnabled(enable);
		editRemarks.setEnabled(enable);
		buttonSetLocation.setEnabled(enable);
		buttonSnap.setEnabled(enable);
		textDate.setEnabled(enable);
		buttonSelect.setEnabled(enable);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.save_btn:	
				saveClicked();
				break;
			case R.id.location_btn:
				getLocation();
				break;
			case R.id.textHealthPromotionDate:
				showDatePicker();
				break;
			case R.id.healthpromo_pic_snap:
				snapPicture();
				break;
			case R.id.healthpromo_pic_select:
				selectPicture();
				break;
			case R.id.healthPromotionRemove:
				deleteHealthPromotion();
				break;

		}
		
	}
	
	private void saveClicked(){
		if(state==STATE_VIEW){
			editHealthPromotionRecord();
		}else if(state==STATE_EDIT){
			updateHealthPromotionRecord();
		}else if(state==STATE_NEW){
			addHealthPromotionRecord();
		}
	}
	
	private void showDatePicker(){
		try
		{
			FragmentManager fm=this.getFragmentManager();
			XDatePickerFragment newFragment = new XDatePickerFragment();
			newFragment.date=date;
			newFragment.show(fm, "date_picker");
		    //newFragment.showDateDialog(fm,date);
		}
		catch(Exception ex){
			Log.e("health promotion",ex.getMessage());
			
		}

	}
	
	private void showDate(){
		if(date==null){
			return;
		}

		SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.UK);
		textDate.setText(dateFormat.format(date));
		
	}
	
	protected boolean getLocation(){
		gps = new GPSTracker(HealthPromotionsActivity.this);
        // check if GPS enabled
        if(gps.canGetLocation()){
        	

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // \n is for new line
          editLongitude.setText(String.valueOf(longitude));
          editLatitude.setText(String.valueOf(latitude));
          //  Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
          	return true;
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            return false;
        }
	}
	
	protected boolean snapPicture(){
		Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
		Calendar.getInstance();
		
		String imageFilename="healthpromopic" +sdf.format(Calendar.getInstance().getTime())+".jpg";
		HealthPromotions healthPromotions=new HealthPromotions(this.getApplicationContext());
		File file = new File(healthPromotions.getHealthPromotionPicturePath()+ imageFilename);

		selectedImageUri=Uri.fromFile(file);
	
	    intent.putExtra(MediaStore.EXTRA_OUTPUT,selectedImageUri); // set the image file name

	    // start the image capture Intent
	    startActivityForResult(intent, PICTURE_SNAP);
		return true;
	}
	
	protected boolean selectPicture(){
		Intent intent= new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, PICTURE_SELECT);
		return true;
	}
	
	protected boolean addHealthPromotionRecord(){
		//create a method for this part of the switch, and call it
		//date has to be written in yyyy-mm-dd format 
		if(!imageCaptured){
			return false;
		}
		String date=getDatabaseDateString();
		
		String venue=editVenue.getText().toString();
		String topic=editTopic.getText().toString();
		String method=editMethod.getText().toString();
		String target=editTargetAudience.getText().toString();
		int number=Integer.parseInt(editAudienceNumber.getText().toString());
		String remarks=editRemarks.getText().toString();

		String month="none";
		String image=selectedImageUri.getPath();
		CHOs chos=new CHOs(getApplicationContext());
		CHO cho=chos.getCHO(choId);
		int subdistrictId=cho.getSubdistrictId();

		double latitude=Double.parseDouble(editLatitude.getText().toString());
		double longitude=Double.parseDouble(editLongitude.getText().toString());
		
		HealthPromotions healthPromotions=new HealthPromotions(this.getApplicationContext());
		healthPromotions.addHealthPromotion(date, venue, topic, method, target, number, remarks, month, latitude, longitude, image, choId, subdistrictId);
		state=STATE_VIEW;
		stateAction();
		return true;
	}

	protected boolean updateHealthPromotionRecord(){
		if(!imageCaptured){
			return false;
		}
		String date=getDatabaseDateString();
		String venue=editVenue.getText().toString();
		String topic=editTopic.getText().toString();
		String method=editMethod.getText().toString();
		String target=editTargetAudience.getText().toString();
		int number=Integer.parseInt(editAudienceNumber.getText().toString());
		String remarks=editRemarks.getText().toString();
		String month="none";
		String image=selectedImageUri.toString();
		CHOs chos=new CHOs(getApplicationContext());
		CHO cho=chos.getCHO(choId);
		int subdistrictId=cho.getSubdistrictId();
		
		double latitude=Double.parseDouble(editLatitude.getText().toString());
		double longitude=Double.parseDouble(editLongitude.getText().toString());

		HealthPromotions healthPromotions=new HealthPromotions(this.getApplicationContext());
		healthPromotions.updateHealthPromotion(healthPromotionId,date, venue, topic, method, target, number, remarks, month,latitude, longitude, image, choId, subdistrictId);
		state=STATE_VIEW;
		stateAction();
		return true;
	}
	
	protected boolean editHealthPromotionRecord(){
		state=STATE_EDIT;
		stateAction();
		return true;
	}
	
	protected boolean deleteHealthPromotion(){
		if(healthPromotionId<=0){
			return false;
		}
		try{
			HealthPromotions healthPromotions=new HealthPromotions(this.getApplicationContext());
			if(!healthPromotions.deleteHealhtPromotion(healthPromotionId)){
				return false;
			}
			
			this.finish();
			return true;
		}catch(Exception ex){
			return false;
			
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK){
			return ;
		}

		switch(requestCode){
			case PICTURE_SNAP:
				selectedImageUri=Uri.parse("file://"+selectedImageUri.getPath());
				imageCaptured=true;
				break;
			case PICTURE_SELECT:
				selectedImageUri=data.getData();
				imageCaptured=true;
				break;
		}
		
		showImage();
		
	}
	
	public void updateDate(int year, int month, int day){
		Calendar c=Calendar.getInstance();
		c.set(year, month, day);
		date=c.getTime();
		showDate();

	}
		
	
	protected void showImage(){
		if(!imageCaptured){
			return;
		}
		
		ImageView image=(ImageView)findViewById(R.id.imageHealthPromotion);
		try{
			if(selectedImageUri.getScheme()!=null){
				Bitmap map=Images.Media.getBitmap(getContentResolver(), selectedImageUri);
				image.setImageBitmap(map);
			}else{
				image.setImageURI(selectedImageUri);
			}
			image.postInvalidate();
		}catch(Exception ex){
			return;
		}
	}
	
	public static class XDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		public java.util.Date date=null;
		
		public XDatePickerFragment(){
			
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			
			Calendar c = Calendar.getInstance();
			if(date!=null){
				c.setTime(date);
			}
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			HealthPromotionsActivity h=(HealthPromotionsActivity)getActivity();
			h.updateDate(year, month, day);
		}
		
		public void showDateDialog(FragmentManager fm,java.util.Date date){
			try{
				this.date=date;
		        XDatePickerFragment newFragment = new XDatePickerFragment();
		        newFragment.date=date;
		        newFragment.show(fm, "date_picker");
			}catch(Exception ex){
				Log.e("DatePicker",ex.getMessage());
			}

	    }
	}
	
	
	public String getDatabaseDateString(){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
		return dateFormat.format(calendar.getTime());
	}
	
	

		
}
