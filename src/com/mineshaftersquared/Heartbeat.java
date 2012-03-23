package com.mineshaftersquared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mineshaftersquared.util.Logger;
import mineshafter.util.Resources;
import mineshafter.util.SimpleRequest;

public class Heartbeat extends Thread {
	protected static String authServer = Resources.loadString("auth").trim();
	protected static String subdomain = "serverlist";
	protected static String mainUpdateCommand = "heartbeat";
	
	protected boolean enabled = true;
	protected int interval = 1800000;
	protected enum commands {DONE, INTERVAL, SET_INTERVAL, INIT, EXIT, CREATE}
	protected enum userInputType {Y, N, USER_NOT_FOUND, FAILED, OK}
	protected String sendData = new String();
	
	public static void main(String[] args)
	{
		(new Heartbeat()).start();
	}
	
	public void run() {
		try {
			// first command at startup
			sendData = "init";
			
			while(enabled)
			{
				// hit server for updates and command
				String[] responseArray = sendBeat(sendData);
				String response = responseArray[0];
				Logger.logln(response);
				// blank out send data
				sendData = new String();
				
				// what to do on each response
				switch(commands.valueOf(response))
				{
					// nothing left to do.  wait until next heart beat
					case DONE:
						Logger.logln("Successful Status Update");
						Heartbeat.sleep(interval);
					break;
					
					case INIT:
						Logger.logln("Server List Updater Shutting Down");
					break;
					
					case CREATE:
						createServer();
					break;
					
					// interval commands
					case INTERVAL:
						sendData = "interval/" + interval;
					break;
					
					case SET_INTERVAL:
						interval = Integer.parseInt(responseArray[1]);
						sendData = "interval/confirm/" + interval;
					break;
					
					case EXIT:
						Logger.logln("Server List Updater Shutting Down");
						enabled = false;
					break;
				}
				
			}
			
		} catch (InterruptedException e) {
			Logger.logln("Heartbeat Crashed");
		}
	}
	
	private String[] sendBeat(String request)
	{
		String baseRequest = "http://" + Heartbeat.subdomain + "." + Heartbeat.authServer;
		
		if(request.isEmpty())
		{
			request = baseRequest + "/" + mainUpdateCommand;
		}
		else
		{
			request = baseRequest + "/" + request;
		}
		
		Logger.logln("Heart Beat: " + request);
		String response = new String(SimpleRequest.get(request));
		String[] returnArr = response.split(":");
		
		return returnArr;
	}
	
	private boolean createServer()
	{
		// get input handler
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		// create question
		Boolean inputLoop = true;
		String userInput = new String();
		Logger.logln("New Server Detected!");
		Logger.log("Would you like to add this server to the Mineshafter Squared Server List? (Y/N):");
		
		while(inputLoop)
		{
			try {
				userInput = br.readLine().toUpperCase();
				
				if(!userInput.trim().isEmpty())
				{
					userInputType answer = userInputType.valueOf(userInput);
				
					if(answer == userInputType.Y)
						inputLoop = false;
					else if(answer == userInputType.N)
						return false;
					else
						throw new IOException();
				}
				else
					throw new IOException();
					
			} catch (IOException e) {
				Logger.log("Bad Input: Please Enter (Y or N):");
			}
		}
		
		// get admin username
		inputLoop = true;
		userInput = new String();
		Logger.logln("What username will you use to admin the server?");
		Logger.log("(This has to be a valid Mineshafter Squared Account):");
		
		while(inputLoop)
		{
			try {
				userInput = br.readLine();
				
				if(!userInput.trim().isEmpty())
				{
					// send create request
					String[] response = sendBeat("create/" + userInput);
					userInputType input = userInputType.valueOf(response[0]);
					
					switch(input)
					{
						case USER_NOT_FOUND:
							Logger.logln("User account not found. Try logging into the site first.");
							Logger.log("What username will you use to admin the server? ");
						break;
						
						case FAILED:
							Logger.log(response[1]);
						return false;
						
						case OK:
							inputLoop = false;
						break;
					}
				}
			} catch (IOException e) {
				Logger.log("Bad Input. Please try again:");
			}
		}
		
		
		// get admin username
		inputLoop = true;
		userInput = new String();
		Logger.logln("Server Created Successfully!");
		Logger.logln("Please follow the following steps to finish up:");
		Logger.logln("[1] Log into the site using your admin account.");
		Logger.logln("[2] Go to the \"Manage Server\" page. (Click on your name in the top right to access the menu).");
		Logger.logln("[3] Configure your server list settings for the first time.");
		Logger.log("[Press Enter When Completed]");
		
		while(inputLoop)
		{
			try {
				userInput = br.readLine();
				
			} catch (IOException e) {
				
			}
		}
		
		return true;
	}
}
