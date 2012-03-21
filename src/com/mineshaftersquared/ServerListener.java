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
	
	public String process(ServerAPICommand command)
	{
		String response = new String();
		switch(command)
		{
			case ping:
				long unixTime = System.currentTimeMillis() / 1000L;
				response += unixTime;
			break;
		}
		
		return response + "\n";
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
				Logger.log("Server API Command: " + command);
				
				// pre-process input
				
				// process input
				ServerAPICommand serverCommand;
				try{
					serverCommand = ServerAPICommand.valueOf(command);
				} catch (IllegalArgumentException e) {
					Logger.log("Invalid Command: " + command);
					response.close();
					continue;
				}
				
				responseString = process(serverCommand);
				
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
