package com.ashesi.cs.mhealth;

import com.ashesi.cs.mhealth.data.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ConfirmDelete extends DialogFragment{
	public String guid;
	public ConfirmDelete (String guid){
		this.guid = guid;
	}
	public interface ConfirmDeleteListener {
		public String guid ="";
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
		public void setGuid(String aGuid);
		public String getGuid();
	}
	
	ConfirmDeleteListener mListener;
	
	/* (non-Javadoc)
	 * @see android.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		   // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm_delete)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogPositiveClick(ConfirmDelete.this);
                       mListener.setGuid(guid);
                   }
               })
               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   mListener.onDialogNegativeClick(ConfirmDelete.this);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
	}

	/* (non-Javadoc)
	 * @see android.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		//Verify that the host activity implements the interface
		try{
			//Instantiate the confirmdeletelistener so we can send events to the host
			mListener = (ConfirmDeleteListener) activity;
		}catch (ClassCastException e){
			//The activity must implement this interface
			throw new ClassCastException(activity.toString() +
				" must implement ConfirmDeleteListener");
		}
	}
	
	
	
}
