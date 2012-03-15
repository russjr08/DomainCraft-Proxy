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
	protected String command = new String();
	protected String responseString = new String();
	
	public static void main(String[] args)
	{
		(new ServerListener()).start();
	}
	
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
				InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
				BufferedReader input = new BufferedReader(inputStream);
				// get output handler
				DataOutputStream response = new DataOutputStream(connection.getOutputStream());
				
				// get input
				command = input.readLine();
				
				// process input
				Logger.log("Command: " + command);
				responseString = command + " MC2 It Works!";
				
				// send response
				response.writeBytes(responseString);
				response.flush();
				response.close();
			}
			
		} catch (IOException e) {
			Logger.log("Listener Thread had a problem");
			e.printStackTrace();
		}
	}
}
