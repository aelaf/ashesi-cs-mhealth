/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ashesi.cs.mhealth;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ashesi.cs.mhealth.DeviceListFragment.DeviceActionListener;
import com.ashesi.cs.mhealth.data.CHO;
import com.ashesi.cs.mhealth.data.CHOs;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.TCPClient;
import com.ashesi.cs.mhealth.data.TCPServer;
import com.ashesi.cs.mhealth.knowledge.LogData;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    private String otherAddress = null;
	private ServerSocket serverSock;
	private Socket socket;
	private LogData log;
	private CHO currentCHO;
	private CHOs chos;
	private int choId = 1;
    
    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	log = new LogData(getActivity());
    	chos = new CHOs(getActivity());
    	currentCHO = chos.getCHO(this.choId);
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true);
                ((DeviceActionListener) getActivity()).connect(config);
                Date date1 = new Date();		            
     			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
     			//log.addLog(0501, dt.format(date1), currentCHO.getFullname(), 
//     		    this.getClass().getName() + " Method: onClick()", "Connection initiated. To device: " +  device.deviceAddress );

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

        return mContentView;
    }
    
    public WifiP2pInfo getInfo(){
    	return info;
    }
    
    public ServerSocket serSocket(){
    	return serverSock;
    }
    
    public Socket getSock(){
    	return socket;
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server socket.
        if (info.groupFormed && info.isGroupOwner) {
        	   	try {
					serverSock = new ServerSocket(8988);
					Date date1 = new Date();		            
	     			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
//	     			log.addLog(0502, dt.format(date1), currentCHO.getFullname(), 
//	     		    this.getClass().getName() + " Method: onConnectionInfoAvailable()", "Connection complete. Server/Group Owner: " + info.groupOwnerAddress.getHostAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Date date1 = new Date();		            
	     			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
//	     			log.addLog(0502, dt.format(date1), currentCHO.getFullname(), 
//	     		    this.getClass().getName() + " Method: onConnectionInfoAvailable()", e.getMessage() );
				}
                new ServerTask().execute();
        } else if (info.groupFormed) {
            // The other device acts as the client.
        	Date date1 = new Date();		            
 			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
// 			log.addLog(0502, dt.format(date1), currentCHO.getFullname(), 
// 		    this.getClass().getName() + " Method: onConnectionInfoAvailable()", "Connection complete. Client:");
        	socket = new Socket();
            new ClientTask().execute(); 
         }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
        Log.d("P2p", "starting server and client servers");
    }
    
    public ProgressDialog getDialog(){
    	return progressDialog;
    }
    
    class ServerTask extends AsyncTask<Void, Void, Void> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
    	
    	ProgressDialog diag = getDialog();
    	
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			diag = new ProgressDialog(getActivity());
			diag.setMessage("Please wait...");
			diag.setTitle("Synchronization in progress");
			diag.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			diag.setCancelable(false);
			diag.show(); 			
		}

		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			 try {
				ResourceMaterials resourceMat = new ResourceMaterials(getActivity());
				
				ServerSocket sock = serSocket();
				TCPServer server = new TCPServer(sock.accept(),resourceMat);
				try {
					//Establish the right of way i.e. who will be sending files.
					Log.d("Server Version", String.valueOf(resourceMat.getMaxID()));
					String rightOfWay = server.checkRightOfWay(resourceMat.getMaxID());
					String [] result = rightOfWay.split("[|]");
					System.out.println(result[0]);
					
					
					//Log details of synch
					Date date1 = new Date();		            
		 			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
		 			log.addLog(0503, dt.format(date1), currentCHO.getFullname(), 
		 		    this.getClass().getName() + " Method: ServerTask()", "Exchanged version numbers. Server_Version:" +  
		 		    				String.valueOf(resourceMat.getMaxID()) + " Client_Version: " + result[1]);
		 
					//Initiate sending or receiving.
					if(result[0].equals("server")){  //send
						System.out.println("Starting send");
						int countUp = Integer.parseInt(result[1]) + 1;  //Get starting point of sending process
						int maxId = resourceMat.getMaxID();
						int duration = (maxId - countUp) + 1; 
						diag.setMax(duration);
						while(countUp <= maxId){
							diag.incrementProgressBy(1);
							server.resetSock(sock.accept());
							
							//log send of files
							JSONObject jObj = new JSONObject();
							try {
								jObj.put("filename", resourceMat.getMaterial(countUp).getContent());
								jObj.put("id", resourceMat.getMaterial(countUp).getId());
								jObj.put("type", resourceMat.getMaterial(countUp).getType());
								jObj.put("description", resourceMat.getMaterial(countUp).getDescription());
					 			log.addLog(0503, dt.format(date1), currentCHO.getFullname(), 
					 		    this.getClass().getName() + " Method: ServerTask()", "Sending a file." + jObj.toString());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				 			
							File file = new File(DataClass.getApplicationFolderPath() + resourceMat.getMaterial(countUp).getContent());
							server.sendFile(file, resourceMat.getMaterial(countUp));
							countUp++;
							System.out.println(countUp);
						}
					}
					else{
						int countDown = Integer.parseInt(result[1]);
						int maxId = resourceMat.getMaxID();
						int duration = countDown - maxId;
						diag.setMax(duration);
						while(countDown > maxId){
							diag.incrementProgressBy(1);
							server.resetSock(sock.accept());
							server.receiveFile();
							countDown--;
							System.out.println(countDown);
						}
					}
					sock.close();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (diag != null && diag.isShowing()) {
	            diag.dismiss();
	        }
			Toast.makeText(getActivity(), "Synchronization complete", Toast.LENGTH_LONG).show();
		}
		
		
    	
    }
    
    class ClientTask extends AsyncTask<Void, Void, Void> {

    	ProgressDialog diag = getDialog();
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			diag = new ProgressDialog(getActivity());
			diag.setMessage("Please wait...");
			diag.setTitle("Synchronization in progress");
			diag.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			diag.setCancelable(false);
			diag.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				ResourceMaterials resMat = new ResourceMaterials(getActivity());
			  
				Socket sock = getSock();
				sock = new Socket(getInfo().groupOwnerAddress, 8988);
				TCPClient client = new TCPClient(sock, resMat);
				
				Log.d("Client Version", String.valueOf(resMat.getMaxID()));
				String rightOfWay = client.checkRightOfWay(resMat.getMaxID(), sock);
				
				String [] result = rightOfWay.split("[|]");
				System.out.println(rightOfWay);
				
				//Log details of synch
				Date date1 = new Date();		            
	 			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
	 			log.addLog(0504, dt.format(date1), currentCHO.getFullname(), 
	 		    this.getClass().getName() + " Method: ClientTask()", "Exchanged version numbers. Client_Version:" +  
	 		    				String.valueOf(resMat.getMaxID()) + " Server_Version: " + result[1]);
				
				//Initiate sending or receiving.
				if(result[0].equals("server")){ //recieve
					int countDown = Integer.parseInt(result[1]);
					int maxId = resMat.getMaxID();
					int duration = countDown - maxId;
					diag.setMax(duration);
					
					while(countDown >  maxId){
						diag.incrementProgressBy(1);
						sock = new Socket(getInfo().groupOwnerAddress, 8988);
						client.resetSock(sock);
						client.receiveFile();
						countDown--;
						System.out.println(countDown);
					}
				}else{
					System.out.println("Starting send to the server");
					int countUp = Integer.parseInt(result[1]) + 1;  //Get starting point of sending process
					int maxId = resMat.getMaxID();
					int duration = (maxId - countUp) + 1; //Message to the user about the progress of synch
					diag.setMax(duration);
					while(countUp <= maxId){   //send
						
						//log send of files
						JSONObject jObj = new JSONObject();
						try {
							jObj.put("filename", resMat.getMaterial(countUp).getContent());
							jObj.put("id", resMat.getMaterial(countUp).getId());
							jObj.put("type", resMat.getMaterial(countUp).getType());
							jObj.put("description", resMat.getMaterial(countUp).getDescription());
				 			log.addLog(0504, dt.format(date1), currentCHO.getFullname(), 
				 		    this.getClass().getName() + " Method: ClientTask()", "Sending a file." + jObj.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						diag.incrementProgressBy(1);
						sock = new Socket(getInfo().groupOwnerAddress, 8988);
						client.resetSock(sock);

				    	File file = new File(DataClass.getApplicationFolderPath() + resMat.getMaterial(countUp).getContent());
				    	client.sendFile(file, resMat.getMaterial(countUp));
				    	countUp++;
						System.out.println(countUp);					
					}
				}
				sock.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log details of synch
				Date date1 = new Date();		            
	 			DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.UK);
	 			log.addLog(0504, dt.format(date1), currentCHO.getFullname(), 
	 		    this.getClass().getName() + " Method: ClientTask()", e.getMessage());				
			}           
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (diag != null && diag.isShowing()) {
	            diag.dismiss();
	        }
			Toast.makeText(getActivity(), "Synchronization complete", Toast.LENGTH_LONG).show();

		}
		
		
    	
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }
    
    public void setAddress(String otherAdd){
    	otherAddress = otherAdd;
    }
    
    public String getAddress(){
    	return otherAddress;
    }
    
    public void setChoId(int choId){
    	Log.d("setting ID", String.valueOf(choId));
    	this.choId = choId;
        this.currentCHO = chos.getCHO(this.choId);
    }
    
}
