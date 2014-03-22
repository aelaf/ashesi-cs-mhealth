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
import java.net.Socket;

public class TCPServer {

	private final Socket socket;

	  public TCPServer(Socket socket) {
	    this.socket = socket;
	  }

	  public void receiveFile(File file) throws IOException, ClassNotFoundException {
	    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	     
	    //Get current Version of the client
	    oos.flush();
	    ois.skip(Long.MAX_VALUE);
	    String message = (String) ois.readObject();
	    System.out.println("from client version is: " + message);
	    
	  //If the version of the client is lesser than the server's version send the files to the client   
	    if(Integer.parseInt(message) < 4){       
	        oos.writeObject("Coming home");
	        oos.flush();
	        
	         ois.skip(Long.MAX_VALUE);
	        String confi = (String) ois.readObject();
	        System.out.println("from client again : " + confi);
	        
	        oos.writeObject(file.getAbsolutePath());
	        oos.flush();
	        
	        System.out.println("Sending the file");
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
	        System.out.println("from client : " + confirmation);


//	        out.close();
//	        in.close();
	    }else{
	        oos.flush();
	        oos.writeObject("continue");
	    
	        oos.flush();
	        ois.skip(Long.MAX_VALUE);
	        String fileName = (String) ois.readObject();
	        System.out.println("from client : " + fileName);

	        
	        byte[] b = new byte[1024];
	        int len = 0;
	        int bytcount = 1024;
	        FileOutputStream inFile = new FileOutputStream(new File(fileName));
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
	    }
	    
	  }
}