package com.ashesi.cs.mhealth;

import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.R.layout;
import com.ashesi.cs.mhealth.data.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class DetailReport extends Activity {

	int currentView=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_report);
		Intent intent=this.getIntent();
		currentView=intent.getIntExtra("currentView", -1);
		switch(currentView){
			case -1:
				showStatus("no view");
				break;
			case 0:
				showStatus("opd");
				break;
			case 1:
				showStatus("vaccine");
				break;
			case 2:
				showStatus("family planning service");
				break;	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_report, menu);
		return true;
	}

	protected void showError(String msg){
		TextView textStatus=(TextView)this.findViewById(R.id.textStatus);
		textStatus.setText(msg);
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_error));
		
	}
	
	protected void showStatus(String msg){
		TextView textStatus=(TextView)this.findViewById(R.id.textStatus);
		textStatus.setText(msg);
		textStatus.setTextColor(this.getResources().getColor(R.color.text_color_black));
		
	}
}
