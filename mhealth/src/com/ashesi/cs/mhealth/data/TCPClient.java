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

public class TCPClient {

	private Socket socket;
	private boolean rightOfWay;
	private int myVersion;
	private ResourceMaterials resMat;
	
	  public TCPClient(Socket soc, ResourceMaterials resMat) {
	    this.socket = soc;
	    myVersion = 0;
	    rightOfWay = false;
	    this.resMat = resMat;
	  }

	  public void sendFile(File file, ResourceMaterial resrc) throws IOException{
		  System.out.println("Waiting to send files to the server");
			 
			 BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 PrintWriter out =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
			  
			 out.println("Ready to send");

			//Receive the right of way
		     String confirm = in.readLine();
		     System.out.println("from client : " + confirm);
			 
		     //If the server has the rightOfway then allow it to send a file    	        
		        out.println(resrc.getId() + "|" + file.getAbsolutePath() + "|" + 
		                    resrc.getCatId() + "|" + resrc.getType() + "|" + 
		        		    resrc.getDescription() + "|" + resrc.getTag() + "|" +
		                    file.length());
		        System.out.println("Sending the file");
		        
		        byte[] buf = new byte[1024];
		        OutputStream os = socket.getOutputStream();
		        BufferedOutputStream outStr = new BufferedOutputStream(os, 1024);
		        FileInputStream inStr = new FileInputStream(file);
		        int i = 0;
		        int bytecount = 1024;
		        while ((i = inStr.read(buf, 0, 1024)) != -1) {
		          bytecount = bytecount + i;
		          outStr.write(buf, 0, i);
		          outStr.flush();
		        }
		        
		        inStr.close();
		        socket.shutdownOutput(); /* important */
		        System.out.println("Bytes Sent :" + bytecount);
	            
		        String confirmation = in.readLine();
		        System.out.println("from client : " + confirmation);
		        socket.close();
	  }
	  
	  public void receiveFile() throws IOException{
		    System.out.println("Sending confirmation to the server to send files");
		    
		    BufferedReader in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    PrintWriter out =new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);

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
	        //while ((len = in2.read(b, 0, 1024)) != -1 && bytcount <= fileLength) {
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
	        System.out.println("Sent Ok");
	        inFile.close();
	        socket.close();
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
	  
	  public void resetSocket(Socket sock){
		  this.socket = sock;
	  }
	  
}