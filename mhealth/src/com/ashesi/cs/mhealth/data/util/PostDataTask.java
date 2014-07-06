package com.ashesi.cs.mhealth.data.util;

/*
 * todo: create a callback to be executed on completion of task
 */
		
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PostDataTask extends AsyncTask<String, String, String> {
Activity callBack=null;
	
	private ProgressDialog pd;
	Activity activity;
	int actionType;
	HashMap<String, String>values=null;
	String thePath;
	//boolean mTwoPane;
	
	/**
	 * To post data to a server
	 * @param action  this function can react differently depending on action type supplied, currently only used to customize display message
	 * @param path The full URL to connect to
	 * @param a The Activity on whose UI this thread runs. Also the foreground UI. 
	 * @param values A HashMap of the key value pairs in the POST request.
	 */
	public PostDataTask(int action, String path, Activity a, HashMap<String, String>values){
		thePath=path;
		activity=a;
		this.actionType =action;
		this.values=values;
	//	mTwoPane=noOfPanes;
		
	}
	
	
	@Override
	protected String doInBackground(String... urls) {
		String result=null;
		final String end = "\r\n";
		final String twoHyphens = "--";
		final String boundary = "*****++++++************++++++++++++";
	
		try{
			URL url = new URL(thePath);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
		
			/* setRequestProperty */
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+ boundary);
		
			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());

			for (Map.Entry<String, String> entry: values.entrySet()){
				String key=entry.getKey();
				String value =entry.getValue();
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\""+key+"\""+end+end+value+end);
				
			}

			
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			//fStream.close();
			
			
			
			ds.flush();
			ds.close();
		
			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
				final String msg=conn.getResponseMessage();
			  //Toast.makeText(activity, conn.getResponseMessage(), Toast.LENGTH_LONG).show();  //this line will generate an error which is caught anyway.
				activity.runOnUiThread(new Runnable() {
					  public void run() {
					    Toast.makeText(activity, "Connection Error", Toast.LENGTH_SHORT).show();
					  }
					});
			}
		
			StringBuffer b = new StringBuffer();
			InputStream is = conn.getInputStream();
			int bufferSize = 1024; //defined earlier in commented code
			byte[] data = new byte[bufferSize];
			int leng = -1;
			while((leng = is.read(data)) != -1) {
			  b.append(new String(data, 0, leng));
			}
			result = b.toString();
			
		}catch(Exception e){
			Log.v("PostError", e.toString());
		
			
		}
		
		pd.dismiss();
		return result;
	}

	//
	
	@Override
	protected void onPreExecute() {
		String msg="connecting";
		if (actionType==1){ //update teh actionType value
			msg="Fetching Support Data..";
		}
	
	    pd = ProgressDialog.show(activity, "Connecting...",
	            msg);
	}
	
	@Override
	protected void onProgressUpdate(String... item) {
	 
	}
	
	@Override
	protected void onPostExecute(String result) {
			//simply call an interface method
			callBack = (Activity) activity;
			///////callBack.doAfterPostData(result);  //method to call back when upload is done
		
	}

}
