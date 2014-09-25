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
import java.io.StreamCorruptedException;
import java.net.Socket;

import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class TCPServer {

	private int myVersion;
	private boolean rightOfWay;
	private ResourceMaterials resMat;
	private Socket socket;
	  public TCPServer(ResourceMaterials resMat, Socket sock) {
	     myVersion = 0;
	    rightOfWay = false;   //Determine who has the right of way to send files.
	    this.resMat = resMat; 
	    this.socket = sock;
	  }		

	  public void receiveFile() throws IOException{
		    System.out.println("Sending confirmation to the client to send files");
		    
		    BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    PrintWriter out =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

		  //Receive the right of way
		    String msg = in.readLine();
		    System.out.println("Ok gotcha" + msg);
		    
		    //Send Version to the server
			out.println("Waiting for file");	
	      
			//Receive the right of way
		    String fileInfo = in.readLine();
		    System.out.println("from server : " + fileInfo);
	      
	      	String delimit = "[|]";
	        String [] result = fileInfo.split(delimit);
	        int fileId = Integer.parseInt(result[0]);
	        String fileName = result[1];
	        int catId = Integer.parseInt(result[2]);
	        int type = Integer.parseInt(result[3]);
	        String desc = result[4];
	        String tag = result[5];
	        int fileLength = Integer.parseInt(result[6]);
	        
	        resMat.addResMat(fileId, type, catId, fileName, desc, tag);
	        
	        byte[] b = new byte[1024];
	        int len = 0;
	        int bytcount = 0;
	        FileOutputStream inFile = new FileOutputStream(new File(fileName));
	        InputStream is = socket.getInputStream();
	        BufferedInputStream in2 = new BufferedInputStream(is, 1024);
	        //while ((len = in2.read(b, 0, 1024)) != -1 ) {
	        do{
	          len = in2.read(b, 0, 1024);
	          if(len == -1){
	        	  break;
	          }
	          bytcount = bytcount + len;
	          inFile.write(b, 0, len);
	        }while(bytcount < fileLength);
	        System.out.println("Bytes Writen : " + bytcount);
	        
	        socket.shutdownInput();
	        // Sending the response back to the client.
	        out.println("OK");
	        inFile.close();
	        socket.close();
	  }
	  
	  public void sendFile(File file, ResourceMaterial resrc) throws IOException{
		 System.out.println("Waiting to send files to the client");
		 
		 BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		 PrintWriter out =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
		  
		//Receive the right of way
	     String confirm = in.readLine();
	     System.out.println("from client : " + confirm);
		 
	     //If the server has the rightOfway then allow it to send a file    	        
	        out.println(resrc.getId() + "|" + file.getAbsolutePath() + "|" +
	                    resrc.getCatId() + "|" + resrc.getType() + "|" + 
	        		    resrc.getDescription() + "|" +
	                    resrc.getTag() + "|" + file.length());
	        System.out.println("Sending the file");
	        
	        byte[] buf = new byte[1024];
	        OutputStream os = socket.getOutputStream();
	        BufferedOutputStream outStr = new BufferedOutputStream(os, 1024);
	        FileInputStream inStr = new FileInputStream(file);
	        int i = 0;
	        int bytecount = 1024;
	        while ((i = inStr.read(buf, 0, 1024)) != -1 && bytecount<=file.length()) {
	          bytecount = bytecount + 1024;
	          outStr.write(buf, 0, i);
	          outStr.flush();
	        }
	        socket.shutdownOutput();
	        inStr.close();
	        System.out.println("Bytes Sent :" + bytecount);
          
	        String confirmation = in.readLine();
	        System.out.println("from client : " + confirmation);
	        socket.close();
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
	  
	  public void resetSock(Socket sock){
		  this.socket = sock;
	  }
	  
}