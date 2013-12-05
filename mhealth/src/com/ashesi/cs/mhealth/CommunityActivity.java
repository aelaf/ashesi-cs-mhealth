package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class CommunityActivity extends Activity implements OnClickListener, OnItemSelectedListener,OnItemClickListener {

	CommunityMembers communityMembers=null;
	ProgressBar progressBar;
	TextView textStatus;
	CHO currentCHO;
	ArrayList<Community> listCommunities;
	ArrayList<CommunityMember> listCommunityMembers;
	int page=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_members);
		
		Button b=(Button)findViewById(R.id.buttonCommunityFind);
		b.setOnClickListener(this);
		
		b=(Button)findViewById(R.id.buttonCommunityAddMember);
		b.setOnClickListener(this);
		
		b=(Button)findViewById(R.id.buttonGetAll);
		b.setOnClickListener(this);
		
		b=(Button)findViewById(R.id.buttonCommunityNext);
		b.setOnClickListener(this);
		
		Spinner spinner=(Spinner)findViewById(R.id.spinnerCommunities);
		spinner.setOnItemSelectedListener(this);
		
		progressBar=(ProgressBar)findViewById(R.id.progressBarCommunity);
		textStatus=(TextView)findViewById(R.id.textCommunityStatus);
		
		ListView listCommunityMembers=(ListView)findViewById(R.id.listCommunityMembers);
		//listCommunityMembers.setOnItemSelectedListener(this);
		listCommunityMembers.setOnItemClickListener(this);
		listCommunityMembers.setClickable(true);
		
		Intent intent=getIntent();
		int choId=intent.getIntExtra("choId", 0);
		CHOs chos=new CHOs(getApplicationContext());
		currentCHO=chos.getCHO(choId);
		
		loadCommunitySpinner();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.community, menu);
		return true;
	}
	
	public void onClick(View v){
		int id=v.getId();
		switch(id){
			case R.id.buttonCommunityFind: 
				find();
				break;
			case R.id.buttonCommunityAddMember:
				addCommunityMember();
				break;
			case R.id.listCommunityMembers:
				break;
			case R.id.buttonCommunityNext:
				this.getNext();
				break;
			case R.id.buttonGetAll:
				this.getAllCommunityMembers();
				break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.itemCommunityReport:
				Intent intent=new Intent(this,ReportActivity.class);
				startActivity(intent);
				break;
			case R.id.itemCommunityUpload:
				uploadData();
		}
		return true;
	}
	
	public void getAllCommunityMembers(){
		if(communityMembers==null){
			communityMembers=new CommunityMembers(getApplicationContext());
		}
		
		communityMembers.close();
		int communityId=getSelectedCommunityId();
		if(!communityMembers.getAllCommunityMember(communityId)){
			listCommunityMembers.clear();
			
		}else{
			listCommunityMembers=communityMembers.getArrayList(0);
		}
		page=0;
		ArrayAdapter<CommunityMember> adapter=new ArrayAdapter<CommunityMember>(getApplicationContext(),android.R.layout.simple_list_item_1 ,listCommunityMembers);
		ListView listViewCommunityMembers=(ListView)findViewById(R.id.listCommunityMembers);
		listViewCommunityMembers.setAdapter(adapter);
	
	}
	
	public void getNext(){
		if(communityMembers==null){
			return;
		}
		
		page=page+1;
		listCommunityMembers=communityMembers.getArrayList(page);
		ArrayAdapter<CommunityMember> adapter=new ArrayAdapter<CommunityMember>(getApplicationContext(),android.R.layout.simple_list_item_1 ,listCommunityMembers);
		ListView listViewCommunityMembers=(ListView)findViewById(R.id.listCommunityMembers);
		listViewCommunityMembers.setAdapter(adapter);
		
	}
	public void uploadData(){
		//OPDCaseRecords records=new OPDCaseRecords(getApplicationContext());
		CommunityMembers communityMembers=new CommunityMembers(getApplicationContext());
		int max=(int)(communityMembers.uploadDataSize()/10)+3;
		progressBar.setMax(max);
		textStatus.setText("starting...");
		Integer[] params={max};
		UploadRecords uploadRecords=new UploadRecords(); 
		uploadRecords.execute(params);
	}
	
	public void addCommunityMember(){
		int commmunityId=getSelectedCommunityId();
		EditText txtCommunityName=(EditText)findViewById(R.id.editCommunityMemberSearchName);
		String communityMemberName=txtCommunityName.getText().toString();
		Intent intent=new Intent(this,CommunityMemberRecordActivity.class);
		intent.putExtra("state", CommunityMemberRecordActivity.STATE_NEW_MEMBER);
		intent.putExtra("communityId",commmunityId);
		intent.putExtra("name", communityMemberName);
		intent.putExtra("choId", currentCHO.getId());
		startActivity(intent);
	}
	
	public void find(){
		EditText txtCommunityName=(EditText)findViewById(R.id.editCommunityMemberSearchName);
		String communityMemberName=txtCommunityName.getText().toString();
		CommunityMembers members=new CommunityMembers(getApplicationContext());
		int commmunityId=getSelectedCommunityId();
		listCommunityMembers=members.findCommunityMember( commmunityId, communityMemberName);	
		ArrayAdapter<CommunityMember> adapter=new ArrayAdapter<CommunityMember>(getApplicationContext(),android.R.layout.simple_list_item_1 ,listCommunityMembers);
		ListView listViewCommunityMembers=(ListView)findViewById(R.id.listCommunityMembers);
		listViewCommunityMembers.setAdapter(adapter);
		
	}
	
	public boolean loadCommunitySpinner(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerCommunities);
		Communities communities=new Communities(getApplicationContext());
		Community allCommunity=new Community(0,"All Community");
		listCommunities=communities.getCommunties(currentCHO.getSubdistrictId());
		listCommunities.add(0,allCommunity);
		ArrayAdapter<Community> adapter=new ArrayAdapter<Community>(this,android.R.layout.simple_dropdown_item_1line,listCommunities);

		spinner.setAdapter(adapter);
		
		return true;
		
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View v, int startIndex, long endIndex) {
		// TODO Auto-generated method 
		if(startIndex<0){
			return;
		}
		/*//ArrayAdapter<CommunityMember> array=(ArrayAdapter<CommunityMember>)adapter.getAdapter();
		CommunityMember m=listCommunityMembers.get(startIndex);
		Intent intent=new Intent(this,CommunityMembersRecordActivity.class);
		intent.putExtra("state", CommunityMembersRecordActivity.STATE_RECORD);
		intent.putExtra("id",m.getId());
		intent.putExtra("communityId",m.getCommunityID());
		intent.putExtra("choId", currentCHO.getId());
		intent.putExtra("deviceId", mDeviceId);
		startActivity(intent);*/
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int startIndex, long endItem) {
		// TODO Auto-generated method stub
		if(startIndex<0){
			return;
		}
		//ArrayAdapter<CommunityMember> array=(ArrayAdapter<CommunityMember>)adapter.getAdapter();
		CommunityMember m=listCommunityMembers.get(startIndex);
		Intent intent=new Intent(this,CommunityMemberRecordActivity.class);
		intent.putExtra("state", CommunityMemberRecordActivity.STATE_RECORD);
		intent.putExtra("id",m.getId());
		intent.putExtra("communityId",m.getCommunityID());
		intent.putExtra("choId", currentCHO.getId());
		startActivity(intent);
	}
	
	public int getSelectedCommunityId(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerCommunities);
		int index=(int)spinner.getSelectedItemId();
		if(index<0){
			return 0;
		}
		
		Community community=listCommunities.get(index);
		return community.getId();
	}
 
	private class UploadRecords extends AsyncTask<Integer, Integer, Integer> {
		
		@Override
		protected Integer doInBackground(Integer... n) {
			// TODO Auto-generated method stub
			try{
		
				CommunityMembers communityMembers=new CommunityMembers(getApplicationContext());		
				Integer[] progress={1,1};
				String data;
				String postData=communityMembers.getForUpload();
		    	while(postData.length()>0){
		    		data=communityMembers.upload(postData);
		    		
		    		if(!communityMembers.processUploadData(data)){
		    			return 0;
		    		}
		    		postData=communityMembers.getForUpload();
		    		progress[0]++;
		    		publishProgress(progress);
		    	}
		    	progress[0]++;
		    	progress[1]=2;
				publishProgress(progress);
				return 1;
			}catch(Exception ex){
				Log.e("UploadRecords", ex.getMessage());
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
			 if(progress[1]==1){
				 textStatus.setText("uploading...");
			 }else if(progress[1]==2){
				 textStatus.setText("upload complete");
			 }
			 progressBar.setProgress(progress[0]);
	     }
		
		@Override
		protected void onPostExecute(Integer result){
			
			if(result==0){
				textStatus.setText("error uploading");
			}
			
		}
		
		@Override
		protected void onCancelled(Integer result){
			textStatus.setText("cancelled");
		}
	}

	
}
