package com.ashesi.cs.mhealth.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class TCPClient {

	private final Socket socket;
	

	  public TCPClient(Socket socket) {
	    this.socket = socket;
	  }

	  public void sendFile(File file) throws IOException, ClassNotFoundException {
	    // Sending the response back to the client.
	    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

	    //Send the version of database to server 
	    String master;
	    oos.writeObject("1");
	    oos.flush();

	    //Receive the right of way
	    master = (String) ois.readObject();
	    System.out.println("from server : " + master);
	    
	    if(master.equals("continue")){
	        
	        oos.writeObject(file.getAbsolutePath());
	        oos.flush();


	        byte[] buf = new byte[1024];
	        OutputStream os = socket.getOutputStream();
	        BufferedOutputStream out = new BufferedOutputStream(os, 1024);
	        FileInputStream in = new FileInputStream(file);
	        int i = 0;
	        int bytecount = 1024;
	        while ((i = in.read(buf, 0, 1024)) != -1) {
	          bytecount = bytecount + 1024;
	          out.write(buf, 0, i);
	          out.flush();
	        }
	        socket.shutdownOutput(); /* important */
	        System.out.println("Bytes Sent :" + bytecount);

	            ois.skip(Long.MAX_VALUE);
	        String confirmation = (String) ois.readObject();
	        System.out.println("from server : " + confirmation);


	        out.close();
	        in.close();
	    }else{
	        oos.writeObject("Waiting for file");
	        oos.flush();
	        
	        String filename = (String) ois.readObject();
	        System.out.println("from server : " + filename);
	        
	        oos.writeObject("Gotcha");
	        oos.flush();

	        byte[] b = new byte[1024];
	        int len = 0;
	        int bytcount = 1024;
	        FileOutputStream inFile = new FileOutputStream(new File(filename));
	        InputStream is = socket.getInputStream();
	        BufferedInputStream in2 = new BufferedInputStream(is, 1024);
	        while ((len = in2.read(b, 0, 1024)) != -1) {
	          bytcount = bytcount + 1024;
	          inFile.write(b, 0, len);
	        }
	        System.out.println("Bytes Writen : " + bytcount);

	        // Sending the response back to the client.
	        oos.flush();
	        oos.writeObject("ok");
	        System.out.println("Message sent to the client is " + "ok");

	        in2.close();
	        inFile.close();
	        System.out.println("ENDED");
	    }
	  }
}