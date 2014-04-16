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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ashesi.cs.mhealth.DeviceListFragment.DeviceActionListener;
import com.ashesi.cs.mhealth.data.R;
import com.ashesi.cs.mhealth.data.TCPClient;
import com.ashesi.cs.mhealth.data.TCPServer;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    private String otherAddress = null;
	private ServerSocket serverSock;
	private Socket socket;
	private String filePath;
    
    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d("URI", uri.toString());
        filePath = uri.toString();
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
    
    public String getFilename(){
    	return filePath;
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
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
        	   	try {
					serverSock = new ServerSocket(8988);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                new ServerTask().execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
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
				TCPServer server = new TCPServer(resourceMat, sock.accept());
				try {
					//Establish the right of way i.e. who will be sending files.
					Log.d("Server Version", String.valueOf(resourceMat.getMaxID()));
					String rightOfWay = server.checkRightOfWay(resourceMat.getMaxID());
					String [] result = rightOfWay.split("[|]");
					System.out.println(result[0]);
						
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
							File file = new File(resourceMat.getMaterial(countUp).getContent());
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
				
				//Initiate sending or receiving.
				if(result[0].equals("server")){ //recieve
					int countDown = Integer.parseInt(result[1]);
					int maxId = resMat.getMaxID();
					int duration = countDown - maxId;
					diag.setMax(duration);
					while(countDown >  maxId){
						diag.incrementProgressBy(1);
						sock = new Socket(getInfo().groupOwnerAddress, 8988);
						client.resetSocket(sock);
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
						diag.incrementProgressBy(1);
						sock = new Socket(getInfo().groupOwnerAddress, 8988);
						client.resetSocket(sock);
						File file = new File(resMat.getMaterial(countUp).getContent());
						client.sendFile(file, resMat.getMaterial(countUp));
						countUp++;
						System.out.println(countUp);
					}
				}
				sock.close();
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
    
}
