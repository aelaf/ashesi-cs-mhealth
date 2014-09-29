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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class TCPBase {
	protected int myVersion;
	protected boolean rightOfWay;
	protected ResourceMaterials resMat;
	protected Socket socket;

	public TCPBase(Socket soc, ResourceMaterials resMat) {
		this.socket = soc;
		myVersion = 0;
		rightOfWay = false;
		this.resMat = resMat;
	}

	public void receiveFile() throws IOException{
		System.out.println("Sending confirmation to the client to send files");

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

		byte[] buf = new byte[1024];
		int len = 0;
		int bytcount = 0;
		boolean fileEnded = false;
		FileOutputStream inFile = new FileOutputStream(new File(fileName));
		InputStream is = socket.getInputStream();
		BufferedInputStream in2 = new BufferedInputStream(is, 1024);
		len = in2.read(buf, 0, 1024);
		
		while(!fileEnded){
			if(buf[0] == 1 && buf[1] == 9 && buf[2] == 1 && buf[3] == 9 || (len == -1)){
				fileEnded = true;
				break;
			}
			bytcount = bytcount + len;
			inFile.write(buf, 0, len);
			len = in2.read(buf, 0, 1024);
		}
		System.out.println("Bytes Writen : " + bytcount);

		resMat.addResMat(fileId, type, catId, fileName, desc, tag);

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

		int bytecount = 0;
		int len=inStr.read(buf, 0, 1024);
		while (len!=-1) {
			bytecount = bytecount + len;
			outStr.write(buf, 0, len);
			outStr.flush();
			len=inStr.read(buf, 0, 1024);
		}
		buf[0] = 1;
		buf[1] = 9;
		buf[2] = 1;
		buf[3] = 9;
		outStr.write(buf, 0, 4);
		outStr.flush();
		bytecount += 4;
		
		socket.shutdownOutput();
		inStr.close();
		System.out.println("Bytes Sent :" + bytecount);

		String confirmation = in.readLine();
		System.out.println("from client : " + confirmation);
		socket.close();
	}

	public void resetSock(Socket sock){
		this.socket = sock;
	}
	
			  
	

}
