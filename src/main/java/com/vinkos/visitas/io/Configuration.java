package com.vinkos.visitas.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

//TODO: log

public class Configuration {

	private static Properties cnf;
	final static Logger logger = Logger.getLogger(Configuration.class);

	public static Object get(String key) {
		if (cnf == null) {
			cnf = Configuration.loadParams(Configuration.class
					.getResource("default.properties").getFile());
			if ( logger.isInfoEnabled() ) {
				logger.info("Loading default configuration file");
			}
		}
		return cnf.get(key);
	}

	public static Properties loadParams(String configFileName) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(configFileName);
			prop.load(input);
		} catch (IOException e) {
			logger.error("IOException reading file" + configFileName, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("IOException closing inputStream", e);
				}
			}
		}
		return prop;
	}

	public static void saveParams(String fileName, Properties prop, String descr) {
		try {
			File f = new File(fileName);
			OutputStream out = new FileOutputStream(f);
			prop.store(out, descr);
		} catch (FileNotFoundException e) {
			logger.error("Error saving properties file", e);
		} catch (IOException e) {
			logger.error("IOException saving properties", e);
		}
	}

	static Properties createDefaultConfig() {
		Properties properties = new Properties();
		properties.put("pathResource", System.getProperty("user.dir"));
		properties.put("backupPath", System.getProperty("user.dir"));
		properties.put("validator", "visitas.json");
		properties.put("url", "127.0.0.1");
		properties.put("username", "admin");
		properties.put("password", "");
		properties.put("headers", "true");
		properties.put("gui", "true");
		return properties;
	}

	public static void main(String[] args) {
		if ( logger.isDebugEnabled() ) {
			System.out.println();
		}
		Properties cnf = createDefaultConfig();
		saveParams("default.properties", cnf,
				"Configuration for ETL process program for visitors\nSFTP asset properties");
		System.out.println(cnf);
		
	}
}
