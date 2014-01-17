package com.ashesi.cs.mhealth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.Communities;
import com.ashesi.cs.mhealth.data.Community;
import com.ashesi.cs.mhealth.data.Note;
import com.ashesi.cs.mhealth.data.Notes;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class EditNoteFragment extends Fragment {
	ArrayList<Community> array_spinner;
	//String[] array_notes;
	ArrayList<Note> myNotes;
	EditText txtAreaNote;
	Spinner s; 
	CHO currentCHO;
	public EditNoteFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_edit_note, container, false);
        
		s =  (Spinner)rootView.findViewById(R.id.spnSelectComm);
		
		Communities communities=new Communities(this.getActivity().getApplicationContext());
	        
        array_spinner = communities.getCommunties(0);//get communities to be loaded into the spinner
	        
       ArrayAdapter<Community> adapter=new ArrayAdapter<Community>(this.getActivity(),android.R.layout.simple_dropdown_item_1line,array_spinner);
       s.setAdapter(adapter);
			
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
	        			
	        			Community selectedCommunity = (Community)s.getSelectedItem();
	        			int sel_Community = selectedCommunity.getId();
	        			
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
