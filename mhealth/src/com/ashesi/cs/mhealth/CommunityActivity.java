package com.ashesi.cs.mhealth;

import java.util.ArrayList;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.CommunityMemberAdapter;
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
	int queryType=2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_members);
		
		Button b=(Button)findViewById(R.id.buttonCommunityFind);
		b.setOnClickListener(this);
		
		b=(Button)findViewById(R.id.buttonCommunityAddMember);
		b.setOnClickListener(this);
		
		b=(Button)findViewById(R.id.buttonCommunityNext);
		b.setOnClickListener(this);
		
		b=(Button)findViewById(R.id.buttonCommunityPrev);
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
		
		loadSortSpinner();
		loadCommunitySpinner();
		loadSearchTypeSpinner();
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
				page=0;			//new search
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
			case R.id.buttonCommunityPrev:
				this.getPrev();
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
	
	/**
	 * get all community member
	 */
	public void getAllCommunityMembers(){
		queryType=1;

		int communityId=getSelectedCommunityId();
		CommunityMembers members=new CommunityMembers(getApplicationContext());
		listCommunityMembers=members.getAllCommunityMember(communityId,page);	
		CommunityMemberAdapter adapter=new CommunityMemberAdapter(getApplicationContext(),listCommunityMembers);
		//ArrayAdapter<CommunityMember> adapter=new ArrayAdapter<CommunityMember>(getApplicationContext(),android.R.layout.simple_list_item_1 ,listCommunityMembers);
		ListView listViewCommunityMembers=(ListView)findViewById(R.id.listCommunityMembers);
		listViewCommunityMembers.setAdapter(adapter);
	
	}
	
	public void find(){
		queryType=2;
		EditText txtCommunityName=(EditText)findViewById(R.id.editCommunityMemberSearchName);
		String searchText=txtCommunityName.getText().toString();
		CommunityMembers members=new CommunityMembers(getApplicationContext());
		int communityId=getSelectedCommunityId();
		int searchType=getSelectedSearchType();
		int sortType=getSelectedSortType();
		members.setOrder(sortType,0);	//ascending 
		//listCommunityMembers=members.findCommunityMember(commmunityId, communityMemberName);
		switch(searchType){
			case 0:	//get all in community	
				listCommunityMembers=members.getAllCommunityMember(communityId,page);
				break;
			case 1:	//search by name
				listCommunityMembers=members.findCommunityMember(communityId,searchText, page);
				break;
			case 2: //search by card
				listCommunityMembers=members.findCommunityMemberWithCardNo(communityId, searchText, page);
				break;
			case 3: //search NHIS expiring
				listCommunityMembers=members.findCommunityMemberInsuranceExpiring(communityId, page);
				break;
			case 4:	//opd in the last 30 days
				listCommunityMembers=members.findCommunityMemberWithRecord(communityId, page);
				break;
			case 5: //vaccine within 7 days
				listCommunityMembers=members.findCommunityMemberWithScheduled(communityId, 7, page);
				break;
			case 6:// by age
				int ageMax=0;
				int ageMin=0;
				try{
					if(searchText.contains(",")){
						String strs[]=searchText.split(","); //if a comma, that means two numbers min and max
						ageMin=Integer.parseInt(strs[0]);
						ageMax=Integer.parseInt(strs[1]);
					}else{
						ageMin=0;							//if not, then it is just max	
						ageMax=Integer.parseInt(searchText); 
					}
				}catch(Exception ex){
					ageMax=2;		//use default;
					ageMin=0;
					txtCommunityName.setText("under 2 years");
				}
				
				listCommunityMembers=members.findCommunityMemberWithAge(communityId, ageMin,ageMax, page);
				break;
			case 7: // children
				txtCommunityName.setText("under 2 years");
				listCommunityMembers=members.findCommunityMemberWithAge(communityId, 0,2, page);
				break;
			case 8: //count community members
				countCommunityMembers();
				return; // dont go any further because the method deals with display
			case 9:
				listCommunityMembers=members.findCommunityMemberWithFPSchedule(communityId, -2, 30, page);
				break;
			default:
				listCommunityMembers=members.findCommunityMember(communityId,searchText, page);
				break;
				
		}
			
		CommunityMemberAdapter adapter=new CommunityMemberAdapter(getApplicationContext(),listCommunityMembers);
		//ArrayAdapter<CommunityMember> adapter=new ArrayAdapter<CommunityMember>(getApplicationContext(),android.R.layout.simple_list_item_1 ,listCommunityMembers);
		ListView listViewCommunityMembers=(ListView)findViewById(R.id.listCommunityMembers);
		listViewCommunityMembers.setAdapter(adapter);
		
	}
	
	public void getNext(){

		page=page+1;
		switch(queryType){		
			case 1:
				getAllCommunityMembers();
				break;
			case 2:
				find();
				break;
			default:
				getAllCommunityMembers();
		}
		
	}
	
	
	public void getPrev(){

		page=page-1;
		if(page<0){
			page=0;
		}

		switch(queryType){		
			case 1:
				getAllCommunityMembers();
				break;
			case 2:
				find();
				break;
			default:
				getAllCommunityMembers();
		}
		
	}
	/**
	 * displays the number of community members in all community. It uses a string list which is added to List view
	 */
	public void countCommunityMembers(){
		EditText txtCommunityName=(EditText)findViewById(R.id.editCommunityMemberSearchName);
		String searchText=txtCommunityName.getText().toString();
		CommunityMembers members=new CommunityMembers(getApplicationContext());
		int communityId=getSelectedCommunityId();
		
		int ageMax=0;
		int ageMin=0;
		try{
			if(searchText.contains(",")){
				String strs[]=searchText.split(","); //if a comma, that means two numbers min and max
				ageMin=Integer.parseInt(strs[0]);
				ageMax=Integer.parseInt(strs[1]);
			}else{
				ageMin=0;							//if not, then it is just max	
				ageMax=Integer.parseInt(searchText); 
			}
		}catch(Exception ex){
			ageMax=0;		//use default;
			ageMin=0;
		}
		//note that the list hear is a stirng list and not a list of CommunityMembers like like the other search methods
		ArrayList<String> list=members.getCommunityMembersTotalCount(communityId, ageMin, ageMax);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.mhealth_simple_list_item, list);
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
	
	public boolean loadCommunitySpinner(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerCommunities);
		Communities communities=new Communities(getApplicationContext());
		Community allCommunity=new Community(0,"All Community");
		listCommunities=communities.getCommunties(currentCHO.getSubdistrictId());
		listCommunities.add(0,allCommunity);
		ArrayAdapter<Community> adapter=new ArrayAdapter<Community>(this,R.layout.mhealth_simple_spinner,listCommunities);

		spinner.setAdapter(adapter);
		
		return true;
		
	}
	
	
	public boolean loadSearchTypeSpinner(){
		String searchTypes[]={"All in Community","By Name","By Card No","NHIS expiring","OPD in last 30 days", "Vaccine in a week","By Age","Under 2 yr","Count","FP appointment"};
		Spinner spinner=(Spinner)findViewById(R.id.spinnerSearchType);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.mhealth_simple_spinner,searchTypes);
		spinner.setAdapter(adapter);
		return true;
	}
	
	public boolean loadSortSpinner(){
		String searchTypes[]={"Sort by","Name","Card No","ID"};
		Spinner spinner=(Spinner)findViewById(R.id.spinnerSort);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.mhealth_simple_spinner,searchTypes);
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
 
	
	public int getSelectedSearchType(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerSearchType);
		int index=(int)spinner.getSelectedItemId();
		return index;
		
	}
	
	
	public int getSelectedSortType(){
		Spinner spinner=(Spinner)findViewById(R.id.spinnerSort);
		int index=(int)spinner.getSelectedItemId();
		if(index>0){
			index=index-1;
		}
		return index;
		
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
