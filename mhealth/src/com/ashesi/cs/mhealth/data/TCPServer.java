package com.ashesi.cs.mhealth.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class TCPServer extends TCPBase {


	public TCPServer(Socket soc, ResourceMaterials resMat) {
		super(soc,resMat);
	}		


	public String checkRightOfWay(int aVersion) throws IOException, ClassNotFoundException{
		myVersion = aVersion;
		System.out.println("Waiting for version from client");

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

		// Get current Version of the client
		String version = in.readLine();
		System.out.println("from client version is: " + version);

		//If the servers version is greater than the client's version
		if(Integer.parseInt(version) < myVersion){  
			rightOfWay = true;
			out.println("receive|" + myVersion);

			return "server" + "|" + version;   //This means the server has the right of way
		}else{
			rightOfWay = false;
			out.println("continue|" + myVersion);
			return "client" + "|" + version;	//This means that the client has the right Of way
		}		  
	}

	

}