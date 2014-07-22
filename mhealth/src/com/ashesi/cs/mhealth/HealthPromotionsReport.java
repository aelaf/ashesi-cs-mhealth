package com.ashesi.cs.mhealth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.HealthPromotion;
import com.ashesi.cs.mhealth.data.HealthPromotionDetailsAdapter;
import com.ashesi.cs.mhealth.data.HealthPromotionGridAdapter;
import com.ashesi.cs.mhealth.data.HealthPromotions;
import com.ashesi.cs.mhealth.data.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HealthPromotionsReport extends FragmentActivity implements
ActionBar.TabListener, OnClickListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	int selectedHealthPromotionID=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_promo_report);


		// Set up the action bar.
		final ActionBar actionBar =getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			//OPD and Vaccination report use DummySectionFragment
			//0: report
			//1: detail
			Fragment fragment = null;
			if(position==0 ){
				fragment= new ReportFragment();
			}else if(position==1){
				fragment=new HealthPromotionsFragment();
			}else if(position==2){
				fragment=new ReportDetailsFragment();
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_activity_report).toUpperCase(l);
				case 1:
					return getString(R.string.title_add_new_health_promo).toUpperCase(l);
				case 2:
					return "Details";
			
			}
			return null;
		}
	}

	/**
	 * A fragment for OPD report display
	 */


	public static class ReportFragment extends Fragment implements OnClickListener,OnItemClickListener, OnItemSelectedListener{


		View rootView;
		private GridView gridView;
		HealthPromotionGridAdapter adapter;
		public static final String ARG_SECTION_NUMBER = "section_number";	
		public static final int selectedId=0;
		
		public ReportFragment(){

		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


			rootView=inflater.inflate(R.layout.fragment_health_promo_report,null,false);
			gridView = (GridView) rootView.findViewById(R.id.gridView1);


			HealthPromotions healthPromos = new HealthPromotions(getActivity().getApplicationContext());
			ArrayList<HealthPromotion> list;
			list = healthPromos.getReport();
			
			adapter = new HealthPromotionGridAdapter(getActivity().getApplicationContext());
			adapter.setList(list);
			adapter.notifyDataSetChanged();
			gridView.setAdapter(adapter);

			//final HealthPromotionsReport activity=(HealthPromotionsReport)this.getActivity();
			gridView.setOnItemClickListener(this);

			return rootView;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			// TODO Auto-generated method stub
			HealthPromotionsReport activity=(HealthPromotionsReport)this.getActivity();
			activity.selectedHealthPromotionID=(int)adapter.getItemId(position);
			System.out.println(activity.selectedHealthPromotionID);
			Intent i=new Intent(getActivity().getApplicationContext(),HealthPromotionDetails.class);
			i.putExtra("ID", activity.selectedHealthPromotionID);
			startActivity(i);
			//activity.mViewPager.setCurrentItem(2);	
		}
		@Override
		public void onResume(){
			super.onResume();
			HealthPromotions healthPromos = new HealthPromotions(getActivity().getApplicationContext());
			ArrayList<HealthPromotion> list;
			list = healthPromos.getReport();

			final HealthPromotionGridAdapter adapter = new HealthPromotionGridAdapter(getActivity().getApplicationContext());
			adapter.setList(list);
			adapter.notifyDataSetChanged();
			gridView.setAdapter(adapter);
		}
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) {


		}
		@Override
		public void onClick(View v) {


		}

		

	}
	
	public static class ReportDetailsFragment extends Fragment implements OnClickListener, OnItemSelectedListener{
		public static final String ARG_SECTION_NUMBER = "section_number";
		private View rootView2;
		private View rootView;
		private TextView topic_txt;
		private TextView date_txt;
		private TextView number_txt;
		private ImageView image;
		private TextView remarks_txt;
		private TextView audience_txt;
		private ListView detailsList;
		private Bitmap bitmap;
		private String imagepath;
		private int id;
		public ReportDetailsFragment(){

		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			
			rootView = inflater.inflate(R.layout.activity_health_promotion_details,null,false);
			
			topic_txt=(TextView) rootView.findViewById(R.id.txt_topic);
			detailsList=(ListView) rootView.findViewById(R.id.listView1);
			date_txt=(TextView) rootView.findViewById(R.id.txt_date);
			number_txt=(TextView) rootView.findViewById(R.id.txt_number);
			image=(ImageView) rootView.findViewById(R.id.imageView1);
			audience_txt=(TextView) rootView.findViewById(R.id.txt_target);
			remarks_txt=(TextView) rootView.findViewById(R.id.txt_remarks);
			
			HealthPromotionsReport activity=(HealthPromotionsReport)getActivity();
			//int id=activity.selectedHealthPromotionID;
			id=ReportFragment.selectedId;
			
			
			String[] headers={"--------"};
		
		//if(id!=0){
				HealthPromotions healthPromosDetails = new HealthPromotions(getActivity().getApplicationContext());
				ArrayList<HealthPromotion> listDetails;
				listDetails = healthPromosDetails.getDetails(1);

				final HealthPromotionDetailsAdapter adapter = new HealthPromotionDetailsAdapter(getActivity().getApplicationContext());
				adapter.setList(listDetails);
				adapter.notifyDataSetChanged();
				detailsList.setAdapter(adapter);
			//}
			/*else{
				ArrayAdapter<String> adapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, headers);
				detailsList.setAdapter(adapter);
				image.setVisibility(View.GONE);
			}*/
			

			return rootView;
		}
		public void onResume(){
			super.onResume();
			HealthPromotions healthPromosDetails = new HealthPromotions(getActivity().getApplicationContext());
			ArrayList<HealthPromotion> listDetails;
			listDetails = healthPromosDetails.getDetails(id);

			final HealthPromotionDetailsAdapter adapter = new HealthPromotionDetailsAdapter(getActivity().getApplicationContext());
			adapter.setList(listDetails);
			adapter.notifyDataSetChanged();
			detailsList.setAdapter(adapter);
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

	@SuppressLint("SimpleDateFormat") 
	public static class HealthPromotionsFragment extends Fragment implements OnClickListener {

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
		private HealthPromotions HealthPromo;
		private Uri selectedImageUri;
		private String imagepath;
		private Bitmap bitmap;
		View rootView2;
		private ImageView image;
		private ProgressBar mActivityIndicator;
		private TextView status_txt;
		GPSTracker gps;
		private double latitude_double;
		private double longitude_double;
		

		

		/** Called when the activity is first created. */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			if (rootView2==null){
				rootView2= inflater.inflate(R.layout.activity_health_promotion,null,false);
			}
			else {
				ViewGroup parent = (ViewGroup) rootView2.getParent();
				parent.removeView(rootView2);
			}
			create();
			image.setVisibility(View.GONE);
			 mActivityIndicator.setVisibility(View.GONE);
			return rootView2;




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
				String cho_id=MainActivity.choId;
				String subdistrict_id=MainActivity.subdistrictId;


				HealthPromo.addHealthPromotion(date, venue, topic, method_txt, target, number, remarks, month, latitude_str, longitude_str, image, cho_id, subdistrict_id);
				Toast.makeText(getActivity().getApplicationContext(), "Health Promotion Added", Toast.LENGTH_LONG).show();
				break;

			case R.id.location_btn:
				 mActivityIndicator.setVisibility(View.VISIBLE);
				 status_txt.setText("Searching gps......");
				 gps=new GPSTracker(getActivity().getApplicationContext());
				 
				 if(gps.canGetLocation()){
		            	
					 mActivityIndicator.setVisibility(View.GONE);
					 status_txt.setText("GPS search completed!");
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
				startActivityForResult(Intent.createChooser(intent, "Pick an image"), 1);
				break;
			}
		}

		public void onActivityResult(int requestCode, int resultCode, Intent data) {

			if (requestCode == 1 && resultCode == RESULT_OK) {
				//Bitmap photo = (Bitmap) data.getData().getPath();

				selectedImageUri = data.getData();
				imagepath = getPath(selectedImageUri);
				bitmap=BitmapFactory.decodeFile(imagepath);


				List<String> fileArray= new ArrayList<String>(Arrays.asList(imagepath.split("/")));
				//fileArraySplit=imagepath.split("/") ;

				fileArray.get(fileArray.size()-1);
				image.setVisibility(View.VISIBLE);
				image.setImageBitmap(bitmap);
				image_url_txt.setText(imagepath);


			}
		}
		public String getPath(Uri uri) {
			String res = null;
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
			if(cursor.moveToFirst()){;
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
			}
			cursor.close();
			return res;
		}

		public void create(){
			HealthPromo=new HealthPromotions(getActivity().getApplicationContext());
			set_location_btn=(Button)rootView2.findViewById(R.id.location_btn);
			set_location_btn.setOnClickListener(this);	    
			image=(ImageView)rootView2.findViewById(R.id.imageView1);
			image_upload_btn=(ImageButton)rootView2.findViewById(R.id.camera_btn);
			image_upload_btn.setOnClickListener(this);

			save_btn=(Button)rootView2.findViewById(R.id.save_btn);
			save_btn.setOnClickListener(this);

			date_txt=(TextView)rootView2.findViewById(R.id.date_txt);
			date_txt.setTextColor(Color.BLACK);
			date_txt.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
			final Calendar c = Calendar.getInstance();
			year = c.get(Calendar.YEAR);
			month = (c.get(Calendar.MONTH)+1);
			day = c.get(Calendar.DAY_OF_MONTH);
			date_txt.setText(year+"-"+month+"-"+day);

			month_txt=(TextView)rootView2.findViewById(R.id.month_txt);
			month_txt.setTextColor(Color.BLACK);
			month_txt.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
			java.text.SimpleDateFormat df3 = new java.text.SimpleDateFormat("MMMM");
			month_txt.setText(df3.format(c.getTime()));

			longitude_txt=(EditText)rootView2.findViewById(R.id.longitude_txt);
			longitude_txt.setKeyListener(null);

			latitude_txt=(EditText)rootView2.findViewById(R.id.latitude_txt);
			latitude_txt.setKeyListener(null);

			mActivityIndicator =(ProgressBar)rootView2.findViewById(R.id.progressBar1);

			venue_txt=(EditText)rootView2.findViewById(R.id.venue_txt);
			topic_txt=(EditText)rootView2.findViewById(R.id.topic_txt);
			method=(EditText)rootView2.findViewById(R.id.method);
			target_audience=(EditText)rootView2.findViewById(R.id.target_audienc_txt);
			audience_number=(EditText)rootView2.findViewById(R.id.audience_number_txt);
			remarks_txt=(EditText)rootView2.findViewById(R.id.remarks_txt);
			image_url_txt=(EditText)rootView2.findViewById(R.id.image_url_txt);
			status_txt=(TextView)rootView2.findViewById(R.id.txt_status);


		}
		class GPSTracker extends Service implements LocationListener{
			 private final Context mContext;
			 
			    // flag for GPS status
			    boolean isGPSEnabled = false;
			 
			    // flag for network status
			    boolean isNetworkEnabled = false;
			 
			    // flag for GPS status
			    boolean canGetLocation = false;
			 
			    Location location; // location
			    double latitude; // latitude
			    double longitude; // longitude
			 
			    // The minimum distance to change Updates in meters
			    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
			 
			    // The minimum time between updates in milliseconds
			    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
			 
			    // Declaring a Location Manager
			    protected LocationManager locationManager;
			    
			 
			    public GPSTracker(Context context) {
			        this.mContext = context;
			        getLocation();
			    }
			
			    
			    public Location getLocation() {
			        try {
			            locationManager = (LocationManager) mContext
			                    .getSystemService(LOCATION_SERVICE);
			 
			            // getting GPS status
			            isGPSEnabled = locationManager
			                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
			 
			            // getting network status
			            isNetworkEnabled = locationManager
			                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			 
			            if (!isGPSEnabled && !isNetworkEnabled) {
			                // no network provider is enabled
			            } else {
			                this.canGetLocation = true;
			                // First get location from Network Provider
			                if (isNetworkEnabled) {
			                    locationManager.requestLocationUpdates(
			                    	
			                            LocationManager.NETWORK_PROVIDER,
			                            MIN_TIME_BW_UPDATES,
			                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
			                    Log.d("Network", "Network");
			                    if (locationManager != null) {
			                        location = locationManager
			                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			                        if (location != null) {
			                            latitude = location.getLatitude();
			                            longitude = location.getLongitude();
			                        }
			                    }
			                }
			                // if GPS Enabled get lat/long using GPS Services
			                if (isGPSEnabled) {
			                    if (location == null) {
			                        locationManager.requestLocationUpdates(
			                                LocationManager.GPS_PROVIDER,
			                                MIN_TIME_BW_UPDATES,
			                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
			                        Log.d("GPS Enabled", "GPS Enabled");
			                        if (locationManager != null) {
			                            location = locationManager
			                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
			                            if (location != null) {
			                                latitude = location.getLatitude();
			                                longitude = location.getLongitude();
			                            }
			                        }
			                    }
			                }
			            }
			 
			        } catch (Exception e) {
			            e.printStackTrace();
			        }
			 
			        return location;
			    }
			 
			    /**
			     * Stop using GPS listener
			     * Calling this function will stop using GPS in your app
			     * */
			    public void stopUsingGPS(){
			        if(locationManager != null){
			            locationManager.removeUpdates(GPSTracker.this);
			        }
			    }
			 
			    /**
			     * Function to get latitude
			     * */
			    public double getLatitude(){
			        if(location != null){
			        
			            latitude = location.getLatitude();
			        }
			 
			        // return latitude
			        return latitude;
			    }
			 
			    /**
			     * Function to get longitude
			     * */
			    public double getLongitude(){
			        if(location != null){
			            longitude = location.getLongitude();
			        }
			 
			        // return longitude
			        return longitude;
			    }
			 
			    /**
			     * Function to check GPS/wifi enabled
			     * @return boolean
			     * */
			    public boolean canGetLocation() {
			        return this.canGetLocation;
			    }
			 
			    /**
			     * Function to show settings alert dialog
			     * On pressing Settings button will launch Settings Options
			     * */
			    public void showSettingsAlert(){
			        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
			 
			        // Setting Dialog Title
			        alertDialog.setTitle("GPS settings");
			 
			        // Setting Dialog Message
			        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
			 
			        // On pressing Settings button
			        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
			                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			                mContext.startActivity(intent);
			            }
			        });
			 
			        // on pressing cancel button
			        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            dialog.cancel();
			            }
			        });
			 
			        // Showing Alert Message
			        alertDialog.show();
			    }
			 
			    @Override
			    public void onLocationChanged(Location location) {
			    }
			 
			    @Override
			    public void onProviderDisabled(String provider) {
			    }
			 
			    @Override
			    public void onProviderEnabled(String provider) {
			    }
			 
			    @Override
			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    }
			 
			    @Override
			    public IBinder onBind(Intent intent) {
			        return null;
			    }
			    
			 
		}

	}

	@Override
	public void onClick(View v) {

	}
}
