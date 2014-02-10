package com.ashesi.cs.mhealth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.ashesi.cs.mhealth.CommunityMemberRecordActivity.SectionsPagerAdapter;
import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.CommunityMember;
import com.ashesi.cs.mhealth.data.CommunityMembers;
import com.ashesi.cs.mhealth.data.Note;
import com.ashesi.cs.mhealth.data.Notes;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.id;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;
import com.ashesi.cs.mhealth.data.R.string;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NotesActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	//TabsPagerAdapter mSectionsPagerAdapter;
	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	private String[] tabs = {"Take Note","Show Notes"};
	//ListView lstViewNotes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

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
		// Adding Tabs
        /*for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notes, menu);
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
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			/*
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;*/
			
			 switch (position) {
		        case 0:
		            // Edit Note fragment activity
		            return new EditNoteFragment();
		        case 1:
		            // Show Notes fragment activity
		            return new ShowNotesFragment();
	        }
	        
	        return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.takenote).toUpperCase(l);
			case 1:
				return getString(R.string.recent_notes).toUpperCase(l);
		}
			return null;
	}
}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class EditNoteFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		ArrayList<Community> array_spinner;
		//String[] array_notes;
		ArrayList<Note> myNotes;
		EditText txtAreaNote;
		Spinner s; 
		CHO currentCHO;
		
		public static final String ARG_SECTION_NUMBER = "section_number";

		public EditNoteFragment(){}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(R.layout.fragment_edit_note, container, false);
			
			/*TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));*/
			
			s =  (Spinner)rootView.findViewById(R.id.spnSelectComm);
			
			Communities communities=new Communities(this.getActivity().getApplicationContext());
		        
	        array_spinner = communities.getCommunties(0);//get communities to be loaded into the spinner
		        
	       ArrayAdapter<Community> adapter=new ArrayAdapter<Community>(this.getActivity(),android.R.layout.simple_dropdown_item_1line,array_spinner);
	       s.setAdapter(adapter);
	       
	       
	       s.setOnItemSelectedListener(new OnItemSelectedListener(){
	       String selectedCommunity;
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selectedCommunity = s.getItemAtPosition(arg2).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			} 
	       });
				
	        ListView rec_Notes = (ListView)rootView.findViewById(R.id.lstRecentNotes);
		        
	        final ArrayAdapter<String> notesAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1);
	        rec_Notes.setAdapter(notesAdapter);
		    
	        myNotes = new ArrayList<Note>();
	        
	        Button btnSave = (Button)rootView.findViewById(R.id.btnSave);
	        btnSave.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					txtAreaNote = (EditText)rootView.findViewById(R.id.multEditText);
					if (txtAreaNote.getText().toString() != ""){
						String theNote = txtAreaNote.getText().toString();
						
						Date date = new Date();
	        			SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy h:mm:ss",Locale.UK);
	        			String formattedDate = sdf.format(date);
	        			
	        			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-d",Locale.UK);
	        			String noteDate = sdf2.format(date);
	        			
	        			if (theNote.length()<32){
	        				notesAdapter.add(theNote.substring(0, theNote.length())+" "+"\nDate Created: "+formattedDate);
	        			}else{
	        				notesAdapter.add(theNote.substring(0, 32)+"..."+"\nDate Created: "+formattedDate);
	        			}
						
	        			//get CHO here!
	        			Intent intent=getActivity().getIntent();
	        			int choId=intent.getIntExtra("choId", 0);
	        			
	        			//CHOs chos=new CHOs(getActivity().getApplicationContext());
	        			//currentCHO = chos.getCHO(choId);
	        			
	        			//int sel_Community = selectedCommunity.getId();
	        			int sel_Community = 88;
	        			
	        			Note newNote = new Note(theNote, noteDate, sel_Community,choId);
	        			myNotes.add(newNote);
	        			
	        			Notes noteDb = new Notes(getActivity().getApplicationContext());
	        			noteDb.saveNote(sel_Community, choId, date, theNote);
	        			
	        			Toast.makeText(getActivity().getApplicationContext(),"Note has been saved successfully",Toast.LENGTH_SHORT).show();
	        			txtAreaNote.setText("");
					}else{
						Toast.makeText(getActivity().getApplicationContext(),"Please Write Something in the textfield!",Toast.LENGTH_SHORT).show();
					}
				}
	        	
	        });
	        
	        rec_Notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        	public void onItemClick(AdapterView<?> parentAdapter, View view, int position,long id){
	        		//Toast.makeText(getApplicationContext(),"User CLicked on something",Toast.LENGTH_SHORT).show();
	        		txtAreaNote = (EditText)rootView.findViewById(R.id.multEditText);
	        		txtAreaNote.setText(myNotes.get(position).toString());
	        	}
			});
	        registerForContextMenu(rec_Notes);
			return rootView;
		}
	}
	
	public static class ShowNotesFragment extends Fragment{
		Notes notes;
		ArrayList<Note> listNotes;
		@Override
		    public View onCreateView(LayoutInflater inflater, ViewGroup container,
		            Bundle savedInstanceState) {
		 
		        View rootView = inflater.inflate(R.layout.activity_show_notes, container, false);
		        ListView lstViewNotes = (ListView)rootView.findViewById(R.id.lstSavedNotes);
				ArrayList<Note> myNotes = getAllNotes();
				
				//for (int i=0;i<listNotes.size();i++){
					//System.out.println(listNotes.get(i).toString());
				//}
				ArrayAdapter<Note> noteAdapter=new ArrayAdapter<Note>(this.getActivity().getApplicationContext(),android.R.layout.simple_list_item_1 ,listNotes);
				
				lstViewNotes.setAdapter(noteAdapter);
				
		        return rootView;
		    }
		 
		 public ArrayList<Note> getAllNotes(){
				if(notes==null){
					notes=new Notes(this.getActivity().getApplicationContext());
				}
				//int communityId=getSelectedCommunityId();
				
				boolean notesGot = notes.getAllNotes();
				//if(!notesGot){
					//listNotes.clear();
				//}else{
				System.out.println(notesGot);
				
				listNotes = notes.getArrayList();
				notes.close();
				
				return listNotes;
			}
	}

}
