package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataConnection {

	protected int mDeviceId;
	protected String mServerUrl="http://cs2.ashesi.edu.gh/~www_developer/yaresa/";
	
	public static final String MHEALTH_SETTINGS="mhealth_settings";
	public static final String SERVER_URL="http://cs2.ashesi.edu.gh/~www_developer/yaresa/";
	public static final int CONNECTION_TIMEOUT=60000;
	public static final String KNOWLEDGE_URL="knowledge/";
	public static final String RECORD_URL="record/";
	
	Context context;
	String strError;
	
	public String getError(){
		return strError;
	}
	
	public DataConnection(Context context){
		this.context=context;
		mDeviceId=getDeviceId();
		mServerUrl=getServerUrl();
	}
	
	public HttpURLConnection connect(){
		return null;
	}
	
	public boolean processDownloadData(String data){
		return false;
	}
	
	public HttpURLConnection connect(String urlAddress){
		urlAddress=mServerUrl+urlAddress;
		
		HttpURLConnection connection;
				
		try{
			URL url=new URL(urlAddress);
			connection=(HttpURLConnection)url.openConnection();
			if(connection==null){
				Log.d("DataClass.request", "connection did not open");
				strError="connection did not open";
				return null;
			}
			
			Log.d("DataClass.request", "connection open, getting stream");
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.connect();
			return connection;
		}catch(Exception ex){
			strError=ex.getMessage();
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return null;
		}
	}
         	
	/**
	 * Using InputStream it reads data from connection 
	 * @param connection open connection
	 * @return
	 */
	public String request(HttpURLConnection connection){

		
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
	
			InputStream stream=connection.getInputStream();
			Reader reader=new InputStreamReader(stream,"UTF-8");
			
			int readLength=1024;
			while(readLength==1024){
				readLength=reader.read(buffer);
				data=data+(new String(buffer));
			}

			return data;
			
		}
		catch(IOException ex){
			strError=ex.getMessage();
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error reading from server\"}";
		}
	}
	
	/**
	 * Opens HTTP GET connection and reads data
	 * @param urlAddress
	 * @return
	 */
	public String request(String urlAddress){
		
		urlAddress=mServerUrl+urlAddress;
		
		HttpURLConnection connection;
		String data="";
		
		try{
			URL url=new URL(urlAddress);
			connection=(HttpURLConnection)url.openConnection();
			if(connection==null){
				Log.d("DataClass.request", "connection did not open");
				return "{\"result\":0,\"message\":\"error connecting\"}";
			}
			
			
			connection.setConnectTimeout(CONNECTION_TIMEOUT);
			connection.connect();
		
			data=request(connection);
			
			connection.disconnect();
			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
		
	}
	
	/**
	 * makes a POST request and return data read
	 * @param urlAddress appended to server address from setting
	 * @param postData
	 * @return
	 */
	public String request(String urlAddress,String postData){

		urlAddress=mServerUrl+urlAddress;
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);

	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	        nameValuePairs.add(new BasicNameValuePair("d", postData));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity=response.getEntity();
	        InputStream stream=entity.getContent();
	        Reader reader=new InputStreamReader(stream,"UTF-8");
	        int readLength=1024;
	        while(readLength==1024){
	     		readLength=reader.read(buffer);
	     		data=data+(new String(buffer));
	        }
			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
		
	}

	/**
	 * makes a post request and returns an HTTP response
	 * @param urlAddress
	 * @param nameValuePairs
	 * @return
	 */
	public  HttpResponse postRequest(String urlAddress, List<NameValuePair> nameValuePairs){
		urlAddress=mServerUrl+urlAddress;
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);

	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost);
	        return response;
			
		}
		catch(IOException ex){
			Log.d("DataClass.postRequest","Exception" + ex.getMessage());
			return null;
		}

	}
	
	/**
	 * gets the response data from HTTP response
	 * @param response
	 * @return
	 */
	public  String request(HttpResponse response){
		
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			HttpEntity entity=response.getEntity();
	        InputStream stream=entity.getContent();
	        Reader reader=new InputStreamReader(stream,"UTF-8");
	        int readLength=1024;
	        while(readLength==1024){
	     		readLength=reader.read(buffer);
	     		data=data+(new String(buffer));
	        }
			return data;
			
		}
		catch(IOException ex){
			Log.d("DataClass.request","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}


	}
	
	public String uploadFile(String urlAddress, File fileToUpload){
		urlAddress=mServerUrl+urlAddress;
		char buffer[]=new char[1024];
		
		String data="";
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlAddress);
			
			MultipartEntityBuilder builder=MultipartEntityBuilder.create();
			builder.setBoundary("****");
			builder.addPart("userfile",new FileBody(fileToUpload));
			//builder.addBinaryBody("userfile", fileToUpload);
	       
	        httppost.setEntity(builder.build());
	      
	        HttpResponse response = httpclient.execute(httppost);
	        InputStream stream=response.getEntity().getContent();
	        Reader reader=new InputStreamReader(stream,"UTF-8");
	        int readLength=1024;
	        while(readLength==1024){
	     		readLength=reader.read(buffer);
	     		data=data+(new String(buffer));
	        }
			return data;
			
		}
		catch(Exception ex){
			Log.d("DataClass.uploadFile","Exception" + ex.getMessage());
			return "{\"result\":0,\"message\":\"error connecting\"}";
		}
	}
	
	public String getServerUrl(){
		try{
			mServerUrl=PreferenceManager.getDefaultSharedPreferences(context).getString("synch_url", "");
			
		}catch(Exception ex){
			mServerUrl=SERVER_URL;
		}
		return mServerUrl;
	}
	
	public int getDeviceId(){
		try{
			String str=PreferenceManager.getDefaultSharedPreferences(context).getString("device_id", "0");
			mDeviceId=Integer.parseInt(str);
		}catch(Exception ex){
			mDeviceId=0;
		}
		return mDeviceId;
		
	}
}
