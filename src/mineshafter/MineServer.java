package mineshafter;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import javax.swing.JOptionPane;

import mineshafter.proxy.MineProxy;
import mineshafter.util.Resources;
import mineshafter.util.SimpleRequest;

public class MineServer {
	protected static float VERSION = 2.2f;
	protected static int proxyPort = 8071;
	protected static String authServer = Resources.loadString("auth").trim();
	
	public static void main(String[] args) {
		try {
			// Get latest version number from server
            String verstring = new String(SimpleRequest.get(new URL("http://" + authServer + "/update.php?name=server")));
            
            // If server does not return anything, set version to 0
			if(verstring.isEmpty()) {
				verstring = "0";
			}
			
			// Parse the version string out to a float
			float version;
			try {
				version = Float.parseFloat(verstring);
			} catch(Exception e) {
				version = 0;
			}
			
			// Display version to console
			System.out.println("Current proxy version: " + VERSION);
			System.out.println("Gotten proxy version: " + version);
			
			// Check to see if there is a newer version
			if(VERSION < version) {
				// Need update, see about auto downloading in the future
				JOptionPane.showMessageDialog(null, "A new version of Mineshafter Squared is available at http://" + authServer + "\nGo get it.", "Update Available", JOptionPane.PLAIN_MESSAGE);
				System.exit(0);
			}
			
		} catch(Exception e) {
			System.out.println("Error while updating:");
			e.printStackTrace();
			// System.exit(1);
		}
		
		try {
			// Create MineProxy
			MineProxy proxy = new MineProxy(proxyPort, VERSION, authServer);
			proxy.start(); // start Proxy
			
			System.setProperty("http.proxyHost", "127.0.0.1");
			System.setProperty("http.proxyPort", Integer.toString(MineServer.proxyPort));
			System.setProperty("https.proxyHost", "127.0.0.1");
			System.setProperty("https.proxyPort", Integer.toString(MineServer.proxyPort));
			
			//Try to load provided jar from console.  If none is present fall back to default server jar.
			String load;
			try {
				load = args[0];
			} catch(ArrayIndexOutOfBoundsException e) {
				load = "minecraft_server.jar";
			}
			
			Attributes attributes = new JarFile(load).getManifest().getMainAttributes();
			String name = attributes.getValue("Main-Class");
			
			URLClassLoader cl = null;
			Class<?> cls = null;
			Method main = null;
			try {
				cl = new URLClassLoader(new URL[]{ new File(load).toURI().toURL() });
				cls = cl.loadClass(name);
				main = cls.getDeclaredMethod("main", new Class[]{ String[].class });
			} catch(Exception e) {
				System.out.println("Error loading class " + name + " from jar " + load + ":");
				e.printStackTrace();
				System.exit(1);
			}
			String[] nargs;
			try {
				nargs = new String[args.length - 1];
				System.arraycopy(args, 1, nargs, 0, nargs.length);
			} catch(Exception e) {
				nargs = new String[0];
			}
			main.invoke(cls, new Object[]{ nargs });
		} catch(Exception e) {
			System.out.println("Something bad happened:");
			e.printStackTrace();
			System.exit(1);
		}
	}
}