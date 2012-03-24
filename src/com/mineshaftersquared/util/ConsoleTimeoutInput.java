package com.mineshaftersquared.util;

import java.util.concurrent.*;

public class ConsoleTimeoutInput {
	private final int tries;
	private final int timeout;
	private final TimeUnit unit;

	public ConsoleTimeoutInput(int tries, int timeout, TimeUnit unit) {
		this.tries = tries;
		this.timeout = timeout;
		this.unit = unit;
	}

	public String readLine() throws InterruptedException {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		String input = null;
		
		try {
			
			for (int i = 0; i < tries; i++) {
				Future<String> result = ex.submit(new ConsoleInputReadTask());
				
				try {
					input = result.get(timeout, unit);
					break;
				} catch (ExecutionException e) {
					e.getCause().printStackTrace();
				} catch (TimeoutException e) {
					result.cancel(true);
				}
			}
		} finally {
			ex.shutdownNow();
		}
		
		return input;
	}
}