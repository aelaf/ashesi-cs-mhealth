package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.knowledge.Categories;
import com.ashesi.cs.mhealth.knowledge.Category;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class ResourceFragment extends Fragment{

	private ListView resList;
	private ResourceMaterials resMat;
	private ArrayList<ResourceMaterial> resourcesM;
	private ResourceListAdapter adapter;
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
		resList = (ListView)getActivity().findViewById(R.id.resourceList);
		resList.setBackgroundResource(R.drawable.listview_roundcorner_item);
		resourcesM = resMat.getAllMaterials();
		
		isListEmpty = true;
		btn_prev = new Button(getActivity());
		btn_prev.setText("Prev");
		btn_prev.setHeight(LayoutParams.WRAP_CONTENT);
		btn_prev.setWidth(LayoutParams.WRAP_CONTENT);
		btn_next = new Button(getActivity());
		btn_next.setText("Next");
		
		btn_prev.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(counter > 0){
					counter--;
					refreshData();
					CheckEnable();
				}else{
					CheckEnable();
				}
			}				
		});
		
		//Increase count for more questions.
		maxQuestions = 15;
		counter = 0;
		btn_next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(((counter + 1) * maxQuestions) < resourcesM.size()){
					counter++;
					refreshData();
					CheckEnable(); //Check if the next button should be enabled
				}else{
					Toast.makeText(getActivity(), "There are no additional questions", Toast.LENGTH_LONG).show();
					CheckEnable();
				}
			}			
		});
		btn_next.setHeight(LayoutParams.WRAP_CONTENT);
		btn_next.setWidth(LayoutParams.WRAP_CONTENT);
		//Add button to the listView at the footer
		ln =new LinearLayout(getActivity());
		ln.addView(btn_prev);
		ln.addView(btn_next);
		ln.setGravity(Gravity.CENTER);
		resList.addFooterView(ln);
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
				CheckEnable();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void refreshData(){
		resourcesM = resMat.getAllMaterials();
		
		if(resourcesM.isEmpty() || resourcesM == (null)){
			String [] list = new String[]{"There are no resource materials available."};
			isListEmpty = true;	
			 ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, android.R.id.text1, list);	
			resList.setAdapter(adapter1);
		}else{
		    isListEmpty = false;
			adapter = new ResourceListAdapter(getActivity(), currentList(resourcesM));
			resList.setAdapter(adapter);
		}
	}
	
	@Override
	public void onResume() {
		refreshData();
		super.onResume();
	}
	
	/**
     * Method for enabling and disabling Next and Previous
     */
    private void CheckEnable()
    {
    	if(resourcesM == null){
    		btn_prev.setEnabled(false);
    		btn_next.setEnabled(false);
    	}else if(((counter + 1) * maxQuestions) > resourcesM.size()){
            btn_next.setEnabled(false);
        }else{
        	btn_next.setEnabled(true);
        }
        if(counter == 0)
        {
            btn_prev.setEnabled(false);
        }else{
        	btn_prev.setEnabled(true);
        }
    }
    
    /**
	 * Return the current List of resources (aList) to be shown based on the page number
	 * @param resourcesM2
	 * @return
	 */
	private ArrayList<ResourceMaterial> currentList(ArrayList<ResourceMaterial> resourcesM2){
		ArrayList<ResourceMaterial> theList = new ArrayList<ResourceMaterial>();
		
		for(int i=counter*maxQuestions; i<maxQuestions * (counter + 1) && (i<resourcesM2.size()); i++){
			theList.add(resourcesM2.get(i));
		}
		return theList;
	}
	
	private void addListenerOnList(){
		resList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(isListEmpty){
					Toast.makeText(arg0.getContext(), "Sorry! The List is currently Empty", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(arg0.getContext(), "The resource selected is: " +
			                   arg0.getItemAtPosition(arg2) + 
			                   "with a path: " + currentList(resourcesM).get(arg2).getContent(), 
			                    Toast.LENGTH_LONG).show();
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
					File file = new File(currentList(resourcesM).get(arg2).getContent());
	                intent.setDataAndType(Uri.parse( file.getAbsolutePath()), 
	                		               mediaList[currentList(resourcesM).get(arg2).getType()-1]);
	                startActivity(intent);
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
				Scanner scan = new Scanner(upload);
				String fileDetails;
				String delimit = "[,]";
				refreshMenuItem.setActionView(R.layout.action_progressbar);				
				refreshMenuItem.expandActionView();
				while(scan.hasNext()){
					fileDetails = scan.nextLine();
					String [] results = fileDetails.split(delimit);
					Toast.makeText(getActivity().getApplicationContext(),results[0], Toast.LENGTH_LONG).show();
					resMat.addResMat(Integer.parseInt(results[0]), 
					                 Integer.parseInt(results[1]), 
					                 Integer.parseInt(results[2]), 
					                 (Environment.getExternalStorageDirectory() + "/mHealth/" + results[3]), 
					                 results[4], results[5]);
					System.out.println((Environment.getExternalStorageDirectory() + "/mHealth/" + results[3]));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refreshData();
		refreshMenuItem.collapseActionView();
		refreshMenuItem.setActionView(null);
		Toast.makeText(getActivity(), "Synch complete" , Toast.LENGTH_LONG).show();
	}
	
}
