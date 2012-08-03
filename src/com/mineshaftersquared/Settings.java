package com.mineshaftersquared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import mineshafter.util.Resources;

public class Settings 
{
	public Properties properties  = new Properties();
	
	public Settings(File rootFolder)
	{
		String file = rootFolder + "/mineshaftersquared.properties";
		
		try {
			Logger.logln(rootFolder + "/mineshaftersquared.properties");
			properties.load(new FileInputStream(file));
		} catch (IOException e) {
			Logger.logln("No properties file: creating with defaults");
			purgeFiles(file);
			createWithDefaults(file);
		}
	}
	
	private void createWithDefaults(String filePath)
	{
		try {
			properties.setProperty("auth", Resources.loadString("auth").trim());
			properties.store(new FileOutputStream(filePath), null);
		} catch (FileNotFoundException e1) {
			Logger.logln("Error creating properties file" + e1);
		} catch (IOException e1) {
			Logger.logln("Error creating properties file" + e1);
		}
	}
	
	private void purgeFiles(String file)
	{
		File ms2 = new File(file + "/minecraft.jar");
		File ms2_modified = new File(file + "/minecraft_modified.jar");
		
		ms2.delete();
		ms2_modified.delete();
	}
	
	public String get(String key)
	{
		return properties.getProperty(key);
	}
}
