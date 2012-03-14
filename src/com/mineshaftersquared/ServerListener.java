package com.mineshaftersquared;

import mineshafter.programs.MineServer;

public class ServerListener extends Thread {
	
	@Override
	public void run() {
		MineServer.log("Print From Listen Thread!!!");
	}
}
