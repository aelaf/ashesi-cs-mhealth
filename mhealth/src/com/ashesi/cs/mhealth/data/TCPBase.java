package com.ashesi.cs.mhealth.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

import android.util.Log;

import com.ashesi.cs.mhealth.knowledge.ResourceMaterial;
import com.ashesi.cs.mhealth.knowledge.ResourceMaterials;

public class TCPBase {
	protected int myVersion;
	protected boolean rightOfWay;
	protected ResourceMaterials resMat;
	protected Socket socket;
	protected int BUFFER_SIZE = 1024;

	public TCPBase(Socket soc, ResourceMaterials resMat) {
		this.socket = soc;
		myVersion = 0;
		rightOfWay = false;
		this.resMat = resMat;
	}

	public void receiveFile() throws IOException {
		System.out.println("Sending confirmation to the client to send files");

		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		// Send Version to the server
		out.writeUTF("Waiting for file");

		// Receive the right of way
		String fileInfo = in.readUTF();
		System.out.println("from server : " + fileInfo);
		
		// Reading header information
		String delimit = "[|]";
		String[] result = fileInfo.split(delimit);
		int fileId = Integer.parseInt(result[0]);
		String fileName = result[1];
		int catId = Integer.parseInt(result[2]);
		int type = Integer.parseInt(result[3]);
		String desc = result[4];
		String tag = result[5];
		Long fileLength = Long.parseLong(result[6]);
		
		//Read File data
		byte[] buffer = new byte[1024];
		int lengthOfBytesRead;
		Long bytesReceived = 0L;
		FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName)); 
		
		System.out.println("Receiving: " + fileName + " estimated bytes: " + fileLength);

		while(((lengthOfBytesRead = in.read(buffer, 0, BUFFER_SIZE)) != -1) && (bytesReceived <= fileLength)){
            fileOutputStream.write(buffer, 0, lengthOfBytesRead);
            bytesReceived += lengthOfBytesRead;
            System.out.println(lengthOfBytesRead + ": received " + ". Total Received: " + bytesReceived);
        }
		
		System.out.println("File completed");
		
		resMat.addResMat(fileId, type, catId, fileName, desc, tag); // record the transfer
		
		//Close sockets
		fileOutputStream.close();		
		socket.shutdownInput();
		
		// Sending the response back to the client.
		out.writeUTF("OK");

		socket.close();
	}

	public void sendFile(File fileToBeSent, ResourceMaterial resourceMaterial) throws IOException {
		System.out.println("Waiting to send files to the client");

		DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

		// Receive the right of way
		String rightOfWayConfirmation = dataInputStream.readUTF();
		System.out.println("from client : " + rightOfWayConfirmation);

		// If the server has the rightOfway then allow it to send a file
		//Send Header Information
		dataOutputStream.writeUTF(resourceMaterial.getId() + "|" + fileToBeSent.getAbsolutePath() + "|"
				                + resourceMaterial.getCatId() + "|" + resourceMaterial.getType() + "|"
				                + resourceMaterial.getDescription() + "|" + resourceMaterial.getTag() + "|"
				                + fileToBeSent.length());
		
		System.out.println("Sending file with name: " + resourceMaterial.getContent());
		dataOutputStream.flush();
		
		Long filesize = fileToBeSent.length();
		byte[] buffer = new byte[BUFFER_SIZE];
		OutputStream outputStream = socket.getOutputStream();
		FileInputStream fileInputStream = new FileInputStream(fileToBeSent);
		
		int lengthOfBytesRead;
		while(((lengthOfBytesRead = fileInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) && (filesize >= 0)){
            outputStream.write(buffer, 0, lengthOfBytesRead);
            filesize -= lengthOfBytesRead;
            System.out.println(lengthOfBytesRead + " sent. " + " Amount left: " + filesize);
        }
		
		socket.shutdownOutput();
		fileInputStream.close();

		String confirmation = dataInputStream.readUTF();
		System.out.println("from client : " + confirmation);
		socket.close();
	}

	public void resetSock(Socket sock) {
		this.socket = sock;
	}

}
