package com.vinkos.visitas.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

//TODO: log

public class Configuration {

	private static Properties cnf;

	public static Object get(String key) {
		if (cnf == null) {
			cnf = Configuration.loadParams("config.properties");
		}
		return cnf.get(key);
	}

	public static Properties loadParams(String fileName) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(fileName);
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Properties createDefaultConfig() {
		Properties properties = new Properties();
		properties.put("pathResource", "~/");
		properties.put("BackupPath", "~/");
		properties.put("headers", "true");
		//properties.put("driver", "com.mysql.jdbc.Driver");
		//properties.put("uri", "jdbc:mysql://localhost/test");
		//properties.put("user", "admin");
		//properties.put("pwd", "");
		return properties;
	}

	public static void main(String[] args) {
		Properties cnf = createDefaultConfig();
		saveParams("config.properties", cnf,
				"Configuration for ETL process program for visitors\nDB asset properties");
		System.out.println(cnf);
		
	}
}
