package com.mineshaftersquared;

import com.mineshaftersquared.util.Logger;

import mineshafter.util.Resources;
import mineshafter.util.SimpleRequest;

public class Heartbeat extends Thread {
	protected static String authServer = Resources.loadString("auth").trim();
	
	public static void main(String[] args)
	{
		(new Heartbeat()).start();
	}
	
	public void run() {
		
		while(true)
		{
			String response = new String(SimpleRequest.get("http://108.166.83.67/serverapi/check"));
		
			if(response.equals("done"))
				Logger.log("Successful Status Update");
			
			try {
				Heartbeat.sleep(1800000);
			} catch (InterruptedException e) {
				Logger.log("Heartbeat Crashed");
			}
		}
	}
}
