package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.CHPSZones;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.FamilyPlanningServices;
import com.ashesi.cs.mhealth.data.OPDCaseCategories;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;
import com.ashesi.cs.mhealth.data.Vaccines;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Synch extends Activity implements OnClickListener {
	ProgressBar progressBar;
	TextView textStatus;
	Button buttonStartTask;
	Button buttonSynchOPDCases;
	Button buttonSynchVaccine;
	Button buttonSynchBackup;
	Button buttonSynchCancel;
	Button buttonSynchRestore;
	Spinner spinnerTasks;
	int choId;
	
	AsyncTask task;
	static final String SUPPORT_DATA_FILENAME="/mhealthsupportdata";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synch);
		
		choId=getIntent().getIntExtra("choId", 0);
		
		progressBar=(ProgressBar)findViewById(R.id.progressBarSynchCommunity);
		textStatus=(TextView)findViewById(R.id.textSynchStatus);
		spinnerTasks=(Spinner)findViewById(R.id.spinnerTasks);
		fillTaskSpinner();
		try
		{
			buttonStartTask=(Button)findViewById(R.id.buttonStartTask);
			buttonStartTask.setOnClickListener(this);
		}catch(Exception ex){
			Log.e("Synch", ex.getMessage());
		}
		
		buttonSynchCancel=(Button)findViewById(R.id.buttonSynchCancel);
		buttonSynchCancel.setOnClickListener(this);
		buttonSynchBackup=(Button)findViewById(R.id.buttonSynchBackup);
		buttonSynchBackup.setOnClickListener(this);
		buttonSynchRestore=(Button)findViewById(R.id.buttonSynchRestore);
		buttonSynchRestore.setOnClickListener(this);
		View buttonSychronizeData=findViewById(R.id.buttonSynchronizeData);
        buttonSychronizeData.setOnClickListener(this);
		task=null;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.synch, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		progressBar.setMax(5);
		progressBar.setProgress(0);
		switch(v.getId()){
			
			case R.id.buttonStartTask:
				startTask();
				break;
			case R.id.buttonSynchBackup:
				backupData();
				break;
			case R.id.buttonSynchRestore:
				confirmRestore();
				break;
			case R.id.buttonSynchCancel:
				cancel();
				break;
			case R.id.buttonSynchronizeData: //synch data
				DataClass data= new DataClass(getApplicationContext());
				Log.v("mhealthDebug1_main activity","before thread");
				data.threadedPost(Synch.this);
				Log.v("mhealthDebug1_main activity","after thread");
				break;
		}
	}
	
	public void showStatus(String msg){
		textStatus.setText(msg);
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_black));
		
	}
	
	public void showError(String msg){
		textStatus.setText(msg);
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_error));
	}
	
	public void startTask(){
		//String tasks[]={"update community members", "update CHOs", "update OPD cases", "update vaccines","update all"};
		int n=spinnerTasks.getSelectedItemPosition();
		switch(n){
			case 0:
				downloadCommunities();
				break;
			case 1:
				loadCHOFromFile();
				break;
			case 2:
				downloadOPDcases();
				break;
			case 3:
				downloadVaccine();
				break;
			case 4:
				loadFamilyPlanningServicesFromFile();
				break;
		}
	}
	
	public void downloadCommunities(){
		if(task!=null){
			cancel();
		}
		RadioButton radioLocalBackup=(RadioButton)findViewById(R.id.radioSynchLocalBackup);
		if(radioLocalBackup.isChecked()){
			loadCommuntiesFromFile();
			return;
		}
		
		//download from server
		showStatus("downloading communities...");
		disableButtons();
		DownloadCommunities download=new DownloadCommunities();
		Integer[] n={1};
		download.execute(n);
		task=download;
		
	}
	
	public void loadCommuntiesFromFile()
	{
		try{
			progressBar.setMax(5);
			progressBar.setProgress(0);
			showStatus("starting...");
			
			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String communityFilename=downloadPath.getPath() + SUPPORT_DATA_FILENAME; 
			FileInputStream fis=new FileInputStream(communityFilename);
			
			progressBar.setProgress(2);
			showStatus("reading data file...");
			
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			String data=new String(buffer);
			progressBar.setProgress(4);
			showStatus("loading...");
			
			Communities communities=new Communities(getApplicationContext());
			communities.processDownloadData(data);
			
			CHOs chos=new CHOs(getApplicationContext());
			chos.processDownloadData(data);
			
			CHPSZones zones=new CHPSZones(getApplicationContext());
			zones.processDownloadData(data);
			
			progressBar.setProgress(5);
			showStatus("complete");
			fis.close();
		}catch(Exception ex){
			textStatus.setText("loading from file failed");
		}
	}
	
	public void loadCHOFromFile(){
		try{
			progressBar.setMax(5);
			progressBar.setProgress(0);
			showStatus("starting...");
			
			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String communityFilename=downloadPath.getPath() + SUPPORT_DATA_FILENAME; 
			FileInputStream fis=new FileInputStream(communityFilename);
			
			progressBar.setProgress(2);
			showStatus("reading data file...");
			
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			String data=new String(buffer);
			progressBar.setProgress(4);
			showStatus("loading...");
						
			CHOs chos=new CHOs(getApplicationContext());
			chos.processDownloadData(data);
			
			CHPSZones zones=new CHPSZones(getApplicationContext());
			zones.processDownloadData(data);
			
			progressBar.setProgress(5);
			showStatus("complete");
			fis.close();
		}catch(Exception ex){
			textStatus.setText("loading from file failed");
		}
	}
	
	public void downloadOPDcases(){
		
		if(task!=null){
			cancel();
		}
		
		RadioButton radioLocalBackup=(RadioButton)findViewById(R.id.radioSynchLocalBackup);
		if(radioLocalBackup.isChecked()){
			loadOPDCasesFromFile();
			return;
		}
		
		
		showStatus("downloading OPD cases...");
		disableButtons();
		DownloadCommunities download=new DownloadCommunities();
		Integer[] n={2};
		download.execute(n);
		
	}
	
	public void loadOPDCasesFromFile(){
		try{
			progressBar.setMax(5);
			progressBar.setProgress(0);
			showStatus("starting...");
			
			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String communityFilename=downloadPath.getPath() + SUPPORT_DATA_FILENAME; 
			FileInputStream fis=new FileInputStream(communityFilename);
			
			progressBar.setProgress(2);
			showStatus("reading data file...");
			
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			String data=new String(buffer);
			progressBar.setProgress(4);
			showStatus("loading...");
			
			OPDCases opdCases=new OPDCases(getApplicationContext());
			if(!opdCases.processDownloadData(data)){
				showError("processing data from the file failed");
				fis.close();
				return;
			}
			
			progressBar.setProgress(5);
			showStatus("complete");
			fis.close();
			OPDCaseCategories categories=new OPDCaseCategories(getApplicationContext());
			categories.popluateTableWithDefault();
		}catch(Exception ex){
			showError("loading from file failed");
		}
	}
	
	public void downloadVaccine(){
		if(task!=null){
			cancel();
		}
		
		RadioButton radioLocalBackup=(RadioButton)findViewById(R.id.radioSynchLocalBackup);
		if(radioLocalBackup.isChecked()){
			loadVaccinesFromFile();
			return;
		}
		
		showStatus("downloading vaccine list...");
		disableButtons();
		DownloadCommunities download=new DownloadCommunities();
		Integer[] n={3};	//vaccine
		download.execute(n);
		
	}
	
	public void loadVaccinesFromFile(){
		try{
			progressBar.setMax(5);
			progressBar.setProgress(0);
			showStatus("starting...");
			
			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String communityFilename=downloadPath.getPath() + SUPPORT_DATA_FILENAME; 
			FileInputStream fis=new FileInputStream(communityFilename);
			
			progressBar.setProgress(2);
			showStatus("reading data file...");
			
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			String data=new String(buffer);
			progressBar.setProgress(4);
			showStatus("loading...");
			
			Vaccines vaccines=new Vaccines(getApplicationContext());
			if(!vaccines.processDownloadData(data)){
				showError("processing data from data file failed");
				fis.close();
				return;
			}
			
			progressBar.setProgress(5);
			showStatus("complete");
			fis.close();
		}catch(Exception ex){
			showError("loading from data file failed");
		}
	}
	
	public void loadFamilyPlanningServicesFromFile(){
		try{
			progressBar.setMax(5);
			progressBar.setProgress(0);
			showStatus("starting...");

			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String communityFilename=downloadPath.getPath() + SUPPORT_DATA_FILENAME; 
			FileInputStream fis=new FileInputStream(communityFilename);

			progressBar.setProgress(2);
			showStatus("reading data file...");

			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			String data=new String(buffer);
			progressBar.setProgress(4);
			showStatus("loading...");

			FamilyPlanningServices services=new FamilyPlanningServices(getApplicationContext());
			if(!services.processDownloadData(data)){
				showError("processing data from data file failed");
				fis.close();
				return;
			}

			progressBar.setProgress(5);
			showStatus("complete");
			fis.close();
		}catch(Exception ex){
			showError("loading from data file failed");
		}
	}
	
	public void backupData(){
		RadioButton radioLocalBackup=(RadioButton)findViewById(R.id.radioSynchLocalBackup);
		if(radioLocalBackup.isChecked()){
			localBackup();
		}else{
			cancel();
			BackupData backup=new BackupData();
			Integer[] n={0};
			backup.execute(n);
			task=backup;
		}
	}
	
	public void localBackup(){
		try{
			progressBar.setMax(5);
			progressBar.setProgress(0);
			textStatus.setText("starting...");
			DataClass dc=new DataClass(getApplicationContext());
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
			Calendar.getInstance();
			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String backupFilename="mhealthbackup" +sdf.format(Calendar.getInstance().getTime());
			File backupFile=new File(downloadPath.getPath(),backupFilename);
			FileOutputStream fos=new FileOutputStream(backupFile);
			FileInputStream fis=new FileInputStream(dc.getDataFilePath());
			progressBar.setProgress(2);
			showStatus("reading data file...");
			//TODO:limit the buffer size to fixed number
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			progressBar.setProgress(4);
			showStatus("writeing backup file "+backupFile+"...");
			fos.write(buffer);
			fos.close();
			fis.close();
			progressBar.setProgress(5);
			showStatus("local backup complete");
			//correctBirthdate(); 					//call  to correct birth dates recorded in yyyy-mm-d form instead of yyyy-mm-dd 	
		}catch(Exception ex){
			showError("local backup fialed");
		}
		
	}
	
	public void localRestore(){
		try{
			localBackup();
			progressBar.setMax(5);
			progressBar.setProgress(0);
			showStatus("starting...");
			DataClass dc=new DataClass(getApplicationContext());
			File downloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			String restoreFilename=downloadPath.getPath() + "/mhealthbackup"; 
			FileInputStream fis=new FileInputStream(restoreFilename);
			String dbFilepath=dc.getDataFilePath();
			
			File dbFile=new File(dbFilepath);
			if(dbFile.delete()){
				Log.d("Synch", "db file deleted");
			}
			
			FileOutputStream fos=new FileOutputStream(dbFile,false);
			
			progressBar.setProgress(2);
			showStatus("reading backfile file...");
			//TODO: find other way of coping the file into the database file location, or limit the buffer
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			progressBar.setProgress(4);
			showStatus("restoring...");
			
			fos.write(buffer);
			fos.close();
			fis.close();
			progressBar.setProgress(5);
			showStatus("local resotre complete");
		
		}catch(Exception ex){
			showError("restore was not successful");
		}
		
	}
	
	private boolean confirmRestore(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("The current data will be replaced by your backup file. Before restoring the current data will be backedup. Do you want to continue?" );
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   localRestore();
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
		return true;
	}
	
	private void createUniqueCommunityMemberID(){
		int done=0;
		try{
			done=this.getPreferences(MODE_PRIVATE).getInt("iss5", 0);
			
		}catch(Exception ex){
			done=0;
		}
		if(done!=0){
			return;
		}
		if(choId==0){
			return;
		}
		CHOs chos=new CHOs(getApplicationContext());
		CHO cho=chos.getCHO(choId);
		if(cho==null){
			textStatus.setText("fixing community member id failed");
			return;
		}
		int chpsZoneId=cho.getCHPSZoneId();
		CommunityMembers communityMembers=new CommunityMembers(getApplicationContext());
		SharedPreferences.Editor editor=this.getPreferences(MODE_PRIVATE).edit();
		if(communityMembers.createUniqueCommunityMemberId(chpsZoneId)){
			editor.putInt("iss4", 1);
			textStatus.setText("fixing community member id done");
		}else{
			editor.putInt("iss4", 0);
			textStatus.setText("fixing community member id failed");
		}
		editor.commit();
		
		
	}
	/*
	// this method runs once only to correct birth dates stored in the wrong format
	 
	private void correctBirthdate(){
		int done=0;
		try{
			done=this.getPreferences(MODE_PRIVATE).getInt("iss4", 0);
			
		}catch(Exception ex){
			done=0;
		}
		if(done!=0){
			return;
		}
		CommunityMembers communityMembers=new CommunityMembers(getApplicationContext());
		SharedPreferences.Editor editor=this.getPreferences(MODE_PRIVATE).edit();
		if(communityMembers.correctBirthdate()){
			editor.putInt("iss4", 1);
			textStatus.setText("local backup complete birthdate corrected");
		}else{
			editor.putInt("iss4", 0);
			textStatus.setText("local backup complete birthdate failed to correct");
		}
		editor.commit();
	}
	*/
	public void cancel(){
		if(task==null){
			return;
		}
		
		task.cancel(true);
		task=null;
		
	}
	
	public void disableButtons(){
		buttonStartTask.setEnabled(false);
		buttonSynchBackup.setEnabled(false);
	}
	
	public void enableButtons(){
		buttonStartTask.setEnabled(true);
	}
	
	public void fillTaskSpinner(){
		String tasks[]={"update community members", "update CHOs", "update OPD cases", "update vaccines","update all"};
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.mhealth_simple_spinner,tasks);
		spinnerTasks.setAdapter(adapter);
	}
	
	private class DownloadCommunities extends AsyncTask<Integer, Integer, Integer> {

		String strErrorMessage;
		@Override
		protected Integer doInBackground(Integer... n) {

			try
			{
				if(n==null){
					return 0;
				}
				if(n.length<=0){
					return 0;
				}
				DataClass obj;
				Integer[] progress={1};
				switch(n[0]){
					case 1:
						obj=new Communities(getApplicationContext());
						break;
					case 2:
						obj= new OPDCases(getApplicationContext());
						break;
					case 3:
						obj=new Vaccines(getApplicationContext());
						break;
					default:
						return 0;
						
				}
				HttpURLConnection connection=obj.connect();
				if(connection==null){
					return 0;
				}
				
				publishProgress(progress);
				String data=obj.request(connection);
				try
				{
					JSONObject objResult=new JSONObject(data);
					if(objResult.getInt("result")==0){
						
						strErrorMessage=objResult.getString("message");
						Log.e("DownloadCommunities", strErrorMessage);
						return 0;
					}
				}catch(Exception ex){
					return 0;
				}
				progress[0]=3;
				publishProgress(progress);
				obj.processDownloadData(data);
				progress[0]=5;
				publishProgress(progress);
				return 1;
			}catch(Exception ex){
				Log.e("DownloadCommunities", ex.getMessage());
				return 0;
			}
			
		}
		
		protected void onProgressUpdate(Integer... progress) {
			
			 if(progress==null){
				 return;
			 }
			 if(progress.length<=0){
				 return;
			 }
			 if(progress[0]==1){
				 showStatus("connected, downloading...");
			 }else if(progress[0]==3){
				 showStatus("download complete, updating...");
			 }else if(progress[0]==5){
				 showStatus("download complete");
			 }
			 progressBar.setProgress(progress[0]);
	     }
		
		@Override
		protected void onPostExecute(Integer result){
			
			if(result==0){
				showError("error downloading");
			}
			enableButtons();
		}
		
		@Override
		protected void onCancelled(Integer result){
			showStatus("cancelled");
			enableButtons();
		}
		
	 }
	
	private class BackupData extends AsyncTask<Integer,Integer,Integer>{

		String strResultMessage;
		@Override
		protected Integer doInBackground(Integer... n) {
			// TODO Auto-generated method stub
			try
			{
				DataClass dc=new DataClass(getApplicationContext());
				String urlAddress="devicesAction.php?cmd=2&choId=2&deviceId="+dc.getDeviceId();
				Integer[] progress={1};
				publishProgress(progress);
				File file=new File(dc.getDataFilePath());
				if(!file.exists()){
					strResultMessage="file not found";
					return 0;
				}
				progress[0]=3;
				String data=dc.uploadFile(urlAddress, file);
				JSONObject obj=new JSONObject(data);
				int result=obj.getInt("result");
				strResultMessage=obj.getString("message");
				return result;
			}catch(Exception ex){
				Log.e("BackupData.doInBackground",ex.getMessage());
				return 0;
			}
		}
		
		@Override
		protected void onPostExecute(Integer result){
			
			if(result==0){
				showError("error uploading backup data " +strResultMessage);
				progressBar.setProgress(5);
			}else{
				showStatus("backup completed successfully");
				progressBar.setProgress(5);
			}
			enableButtons();
		}
		
		 protected void onProgressUpdate(Integer... progress) {
				
			 if(progress==null){
				 return;
			 }
			 if(progress.length<=0){
				 return;
			 }
			 if(progress[0]==1){
				 showStatus("checking data file...");
			 }else if(progress[0]==2){
				 showStatus("uploading...");
			 }else if(progress[0]==5){
				 showStatus("backup complete");
			 }
			 progressBar.setProgress(progress[0]);
	     }
		
		
	}
}
