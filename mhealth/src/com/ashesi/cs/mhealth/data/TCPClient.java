package com.ashesi.cs.mhealth.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class TCPClient extends TCPBase {

	public TCPClient(Socket soc, ResourceMaterials resMat) {
		super(soc,resMat);
	}


	public String checkRightOfWay(int aVersion, Socket soc) throws IOException{
		myVersion = aVersion;
		System.out.println("Sending the version to the server");

		BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

		//Send Version to the server
		out.println(String.valueOf(myVersion));		    
		System.out.println("Sent version: " + myVersion + " to Server");

		//Receive the right of way
		String master = in.readLine();
		System.out.println("from server : " + master);

		String [] result = master.split("[|]");

		if(result[0].equals("continue")){
			rightOfWay = true;	
			return "client|" + result[1];
		}else{
			rightOfWay = false;
			return "server|" + result[1];
		}	
	}


}