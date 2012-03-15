package com.mineshaftersquared;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.mineshaftersquared.util.Logger;

public class ServerListener extends Thread {
	
	protected ServerSocket socket;
	protected final int port = 9005;
	protected Socket connection;
	protected BufferedReader input;
	protected DataOutputStream response;
	protected InputStreamReader inputStream;
	protected String command = new String();
	protected String responseString = new String();
	
	@Override
	public void run() {
		Logger.log("Listener Thread Start");
		
		try {
			socket = new ServerSocket(port);
			
			
			while(true)
			{
				// open socket
				connection = socket.accept();
				// get input reader
				inputStream = new InputStreamReader(connection.getInputStream());
				input = new BufferedReader(inputStream);
				// get output handler
				response = new DataOutputStream(connection.getOutputStream());
				
				// wait for input
				command = input.readLine();
				
				// process input
				responseString = command + " It Works!";
				
				// send response
				response.writeBytes(responseString);
				
			}
			
		} catch (IOException e) {
			Logger.log("Listener Thread had a problem");
			e.printStackTrace();
		}
	}
}
