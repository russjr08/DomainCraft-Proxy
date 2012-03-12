package mineshaftersquared;

import java.net.MalformedURLException;
import java.net.URL;

import mineshafter.util.SimpleRequest;

public class ServerListener {
	
	public static void main(String[] args)
	{
		try {
			String verstring = new String(SimpleRequest.get(new URL("http://beta.mineshaftersquared.com/serverapi/test")));
			System.out.println(verstring);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
