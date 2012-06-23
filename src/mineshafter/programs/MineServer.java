package mineshafter.programs;

import java.io.File;
import java.net.URL;
import sun.applet.Main;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import javax.swing.JOptionPane;
import com.mineshaftersquared.util.Logger;
import mineshafter.proxy.MineProxy;
import mineshafter.util.Resources;
import mineshafter.util.SimpleRequest;

@SuppressWarnings("restriction")
public class MineServer {
	protected static float VERSION = 3.5f;
	
	protected static String authServer =  Resources.loadString("auth").trim();
	protected static final String logName = "[MineshafterSquared]";

	public static void main(String[] args) {
		// Get latest version number from server & see if there is an update
		try {
			//*> updateInfo string for use with the open mineshaftersquared auth
			//*> server is "http://" + authServer + "/update.php?name=server"
			byte[] verdata = SimpleRequest.get("http://" + authServer + "/update/server");
			
			// If server does not return anything, set version to 0
			String verstring = new String();
			if(verdata == null) 
				verstring = "0";
			else 
				verstring = new String(verdata);
			
			if (verstring.isEmpty())
				verstring = "0";

			// Parse the version string out to a float
			float version;
			try {
				version = Float.parseFloat(verstring);
			} catch (Exception e) {
				version = 0;
			}

			// Display version to console
			Logger.logln("Current proxy version: " + VERSION);
			Logger.logln("Gotten proxy version: " + version);

			// Check to see if there is a newer version
			if (VERSION < version) {
				// Need update, see about auto downloading in the future
				Logger.logln("A new version of Mineshafter Squared is available at http://" + authServer + "\nPlease download it and re-launch the server.");
				JOptionPane.showMessageDialog(null, "A new version of Mineshafter Squared is available at http://" + authServer + "\nPlease download it and re-launch the server.", 
													"Update Available", 
													JOptionPane.PLAIN_MESSAGE);
				// shut down the server
				System.exit(0);
			}

		} catch (Exception e) {
			Logger.logln("Error while updating:");
			e.printStackTrace();
			//System.exit(1);
		}
		
		try {
			// Create MineProxy
			MineProxy proxy = new MineProxy(VERSION, authServer);
			proxy.start(); // start Proxy
			int proxyPort = proxy.getPort();
			
			System.setProperty("http.proxyHost", "127.0.0.1");
			System.setProperty("http.proxyPort", Integer.toString(proxyPort));
			
			//System.setProperty("https.proxyHost", "127.0.0.1");
			//System.setProperty("https.proxyPort", Integer.toString(proxyPort));

			// Try to load provided jar from console. If none is present fall
			// back to default server jar.
			String load;
			try {
				load = args[0];
			} catch (ArrayIndexOutOfBoundsException e) {
				load = "minecraft_server.jar";
			}

			Attributes attributes = new JarFile(load).getManifest().getMainAttributes();
			String name = attributes.getValue("Main-Class");
			
			URLClassLoader cl = null;
			Class<?> cls = null;
			Method main = null;
			try {
				cl = new URLClassLoader(new URL[] { new File(load).toURI().toURL() }, Main.class.getClassLoader());
				cls = cl.loadClass(name);
				main = cls.getDeclaredMethod("main", new Class[] { String[].class });
			} catch (Exception e) {
				System.out.println("Error loading class " + name + " from jar " + load + ":");
				e.printStackTrace();
				System.exit(1);
			}
			
			String[] nargs;
			try {
				nargs = new String[args.length - 1];
				System.arraycopy(args, 1, nargs, 0, nargs.length);
			} catch (Exception e) {
				nargs = new String[0];
			}
			
			main.invoke(cls, new Object[] { nargs });
		} catch (Exception e) {
			Logger.logln("Something bad happened:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}