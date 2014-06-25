package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.LogData;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class ResourceFragment extends Fragment{

	private ExpandableListView resList;
	private CHO currentCHO;
	private ResourceMaterials resMat;
	private ArrayList<ResourceMaterial> resourcesM;
	private ExpandableResourceAdapter adapter;
	private Button btn_next, btn_prev;
	private int maxQuestions, counter;
	private boolean isListEmpty;
	private LinearLayout ln;
	private Spinner sortSpinner;
	private List<String> sortList;
	private Categories db1;
	private ArrayList<Category> cat;
	private String [] mediaList;
	private MenuItem refreshMenuItem;
	private List<String> listDataHeader;
	private HashMap<String, List<ResourceMaterial>> listDataChild;
	private LogData log;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_resource, container, false);
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Intent intent = getActivity().getIntent();
		int choId = intent.getIntExtra("choId", 0);
		CHOs chos = new CHOs(getActivity());
		currentCHO = chos.getCHO(choId);
		
		log = new LogData(getActivity());
		mediaList = new String[]{"image/*", "video/*"};
		sortList = new ArrayList<String>();
		db1 = new Categories(getActivity());
		cat = db1.getAllCategories();
		sortList.add("Sort by:");
		
		// Populate the lists (SortList and Choose Category List) with the category names from the Category database
		for (Category ca : cat) {
			String lo = ca.getCategoryName();
			sortList.add(lo);
		}
		
		//Populate the resource list
		resMat = new ResourceMaterials(getActivity());		
		resList = (ExpandableListView)getActivity().findViewById(R.id.resourceList);
		resourcesM = resMat.getAllMaterials();
		
		isListEmpty = true;
		
		refreshData();
		addListenerOnList();
		addItemsOnSpinner();
	}
 
	public void addItemsOnSpinner(){
		sortSpinner = (Spinner)getActivity().findViewById(R.id.filterSpin);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, sortList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(dataAdapter);
		
		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (arg2 > 0) { // If the sort dropdown has been selected i.e. not "Sort By:"
					Toast.makeText(	getActivity(),
							"Your are sorting by - "
									+ cat.get(arg2-1).getCategoryName(),
							Toast.LENGTH_SHORT).show();
				}
				refreshData();
				//CheckEnable();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void refreshData(){		
		listDataHeader = resMat.getTags();
		listDataChild = new HashMap<String, List<ResourceMaterial>>();
		
		if(!listDataHeader.isEmpty()){
			for(int i =0; i<listDataHeader.size(); i++){
				String curTag = listDataHeader.get(i);
				listDataChild.put(curTag, resMat.getResourcebyTag(curTag));
			}
			isListEmpty = false;
			adapter = new ExpandableResourceAdapter(getActivity(), listDataHeader, listDataChild);
			resList.setAdapter(adapter);
			resList.setEnabled(true);
		}else{
			isListEmpty = true;
			listDataHeader = new ArrayList<String>();
			listDataHeader.add("Sorry there are currently no resource materails");
			listDataChild.put(listDataHeader.get(0), null);
			adapter = new ExpandableResourceAdapter(getActivity(), listDataHeader, listDataChild);
			resList.setAdapter(adapter);
			resList.setEnabled(false);
		}
	}
	
	@Override
	public void onResume() {
		refreshData();
		super.onResume();
	}
 
	private void addListenerOnList(){
		resList.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if(isListEmpty){
					return false;
				}else{
					Toast.makeText(getActivity().getApplicationContext(), "You have selected: " + 
					               listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getDescription(), 
					               Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
					File file = new File(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getContent());
	                intent.setDataAndType(Uri.parse( file.getAbsolutePath()), 
	                		               mediaList[listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getType()-1]);
	                startActivity(intent);
					return true;
				}
			}
			
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
		case R.id.synch_q:
			refreshMenuItem = item;
			loadResources();
			break;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(getActivity());
			break;
		}			
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Upload all the added files to the Database
	 */
	private void loadResources(){
		File upload = new File(Environment.getExternalStorageDirectory() + "/mHealth/resourceslist.txt");
		try {
			if(upload.exists()){
				int maxId = resMat.getMaxID(), count=0;				
				Scanner scan = new Scanner(upload);
				String fileDetails;
				String delimit = "[,]";
				refreshMenuItem.setActionView(R.layout.action_progressbar);				
				refreshMenuItem.expandActionView();
				while(scan.hasNext()){
					while(scan.hasNextLine() && count<=maxId ){  //progress till you get to the current index in the resource list
						scan.nextLine();
						count++;
					}
					if(scan.hasNextLine()){
						fileDetails = scan.nextLine();
						String [] results = fileDetails.split(delimit);
						Toast.makeText(getActivity().getApplicationContext(),results[0], Toast.LENGTH_LONG).show();
						resMat.addResMat(Integer.parseInt(results[0]), 
						                 Integer.parseInt(results[1]), 
						                 Integer.parseInt(results[2]), 
						                 (Environment.getExternalStorageDirectory() + "/mHealth/" + results[3]), 
						                 results[4], results[5]);
						System.out.println((Environment.getExternalStorageDirectory() + "/mHealth/" + results[3]));
					}else{
						break;
					}
				}
				refreshData();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//Log the synchronize event
			Date date1 = new Date();		            
			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
			log.addLog(0301, dt.format(date1), currentCHO.getFullname(), 
		    this.getClass().getName() + ": Method->loadResources()", "Loading new resources. \n" + e.getMessage());

		}
		refreshMenuItem.collapseActionView();
		refreshMenuItem.setActionView(null);
		Toast.makeText(getActivity(), "Synch complete" , Toast.LENGTH_LONG).show();
	}
	
}
