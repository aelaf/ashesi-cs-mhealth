package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.OPDCases;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class Synch extends Activity implements OnClickListener {
	ProgressBar progressBar;
	TextView textStatus;
	Button buttonSynchCommunities;
	Button buttonSynchOPDCases;
	Button buttonSynchBackup;
	Button buttonSynchCancel;
	AsyncTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synch);
		
		progressBar=(ProgressBar)findViewById(R.id.progressBarSynchCommunity);
		textStatus=(TextView)findViewById(R.id.textSynchStatus);
		buttonSynchCommunities=(Button)findViewById(R.id.buttonSynchCommunities);
		buttonSynchCommunities.setOnClickListener(this);
		buttonSynchOPDCases=(Button)findViewById(R.id.buttonSynchOPDCases);
		buttonSynchOPDCases.setOnClickListener(this);
		buttonSynchCancel=(Button)findViewById(R.id.buttonSynchCancel);
		buttonSynchCancel.setOnClickListener(this);
		buttonSynchBackup=(Button)findViewById(R.id.buttonSynchBackup);
		buttonSynchBackup.setOnClickListener(this);
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
			case R.id.buttonSynchOPDCases:
				downloadOPDcases();
				break;
			case R.id.buttonSynchCommunities:
				downloadCommunities();
				break;
			case R.id.buttonSynchBackup:
				backupData();
				break;
			case R.id.buttonSynchCancel:
				cancel();
				break;
		}
	}
	
	public void downloadCommunities(){
		if(task!=null){
			cancel();
		}
		disableButtons();
		DownloadCommunities download=new DownloadCommunities();
		Integer[] n={1};
		download.execute(n);
		task=download;
		
	}
	
	public void downloadOPDcases(){
		if(task!=null){
			cancel();
		}
		disableButtons();
		DownloadCommunities download=new DownloadCommunities();
		Integer[] n={2};
		download.execute(n);
		
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
			textStatus.setText("reading data file...");
			byte[] buffer=new byte[fis.available()];
			fis.read(buffer);
			progressBar.setProgress(4);
			textStatus.setText("writeing backup file "+backupFile+"...");
			fos.write(buffer);
			fos.close();
			fis.close();
			progressBar.setProgress(5);
			textStatus.setText("local backup complete");
		}catch(Exception ex){
			textStatus.setText("local backup fialed");
		}
		
	}
	public void cancel(){
		if(task==null){
			return;
		}
		
		task.cancel(true);
		task=null;
		
	}
	
	public void disableButtons(){
		buttonSynchCommunities.setEnabled(false);
		buttonSynchOPDCases.setEnabled(false);
		buttonSynchBackup.setEnabled(false);
	}
	public void enableButtons(){
		buttonSynchCommunities.setEnabled(true);
		buttonSynchOPDCases.setEnabled(true);
		buttonSynchBackup.setEnabled(true);
	}
	
	private class DownloadCommunities extends AsyncTask<Integer, Integer, Integer> {

		String strErrorMessage;
		@Override
		protected Integer doInBackground(Integer... n) {
			// TODO Auto-generated method stub
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
				 textStatus.setText("connected, downloading...");
			 }else if(progress[0]==3){
				 textStatus.setText("download complete, updating...");
			 }else if(progress[0]==5){
				 textStatus.setText("download complete");
			 }
			 progressBar.setProgress(progress[0]);
	     }
		
		@Override
		protected void onPostExecute(Integer result){
			
			if(result==0){
				textStatus.setText("error downloading");
			}
			enableButtons();
		}
		
		@Override
		protected void onCancelled(Integer result){
			textStatus.setText("cancelled");
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
				textStatus.setText("error uploading backup data " +strResultMessage);
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
				 textStatus.setText("checking data file...");
			 }else if(progress[0]==2){
				 textStatus.setText("uploading...");
			 }else if(progress[0]==5){
				 textStatus.setText("backup complete");
			 }
			 progressBar.setProgress(progress[0]);
	     }
		
		
	}
}
