package com.mineshaftersquared.util;

import java.io.*;
import java.util.concurrent.Callable;

public class ConsoleInputReadTask implements Callable<String> {
	
	public String call() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input = "NO";
		
		while ("NO".equals(input)) {
			
			try {
				while (!br.ready()) 
				{
					Thread.sleep(200);
				}
				
				input = br.readLine();
			} catch (InterruptedException e) {
				return null;
			}
		}
		
		return input;
	}
}