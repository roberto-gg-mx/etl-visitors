package com.vinkos.visitas.etl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Function;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
// TODO: the format of the dates can change, make it configurable from properties

public class Validator {

	List<Metadata> metadata;
	public static final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

	public List<Metadata> getMetadata(String fileName) {
		File meta = getFileReader.apply(fileName);
		metadata = readColumnDescr(meta);
		return metadata;
	}

	/**
	 * Retrieve a file with specified name
	 */
	public Function<String, File> getFileReader = filename -> {
	    File file = new File(filename);
	    return file;
	};

	public static List<Metadata> readColumnDescr(File file) {
		JSONParser parser = new JSONParser();
		List<Metadata> columns = new ArrayList<>();
		try (Reader is = new FileReader(file)) {
		    JSONArray jsonArray = (JSONArray) parser.parse(is);

		    //columns = (Map<String, Metadata>) jsonArray.stream().collect(
		    //		Collectors.toMap(key_estadistica, value_request));
		    for (int i = 0; i < jsonArray.size(); i++) {
		    	columns.add(value_request.apply((JSONObject)jsonArray.get(i)));
		    }
		} catch (IOException | ParseException e) {
		    e.printStackTrace();
		}
		return columns;
	}

	public String[] getHeaders(List<Metadata> metadata) {
		String[] headers = new String[metadata.size()];
		for (int i = 0; i < metadata.size(); i++) {
			headers[i] = metadata.get(i).getColumnName();
		}
		return headers;
	}

	/**
	 * Read the JSON entry and return the request Customer
	 */
	private static Function<JSONObject, Metadata> value_request = json -> {
		Metadata column = new Metadata();
	    column.setColumnName(json.get("columnName").toString());
	    column.setFormat(Pattern.compile(json.get("format").toString()));
	    column.setMandatory( Boolean.valueOf(json.get("mandatory").toString() ));
	    column.setDefaultValue( json.get("default").toString() );
	    column.setMultivalue( Boolean.valueOf(json.get("multivalue").toString() ));
	 
	    return column;
	};

	public void run(List<List<String>> data) {
		Matcher matcher;
		Metadata meta;
		Date today = new Date();
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(today);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		for (List<String> row : data) {
			/*for (int i = 0; i < metadata.size(); i++) {
				matcher = metadata.get(i).getFormat().matcher(row.get(i));
				if (matcher.matches()) {
					while (matcher.find()) {
						//System.out.println("group 1: " + matcher.group(1));
						//System.out.println("group 2: " + matcher.group(2));
						//System.out.println("group 3: " + matcher.group(3));
					}
				}
			}*/
			// validate data
			// data format
			for (int i = 0; i < metadata.size(); i++) {
				meta = metadata.get(i);
				matcher = meta.getFormat().matcher(row.get(i));
				if (!matcher.matches()) {
					row.set(i, meta.getDefaultValue());
					//if (meta.isMandatory()) {// validate mandatory
					//TODO: Error
					//} else {
					// Assign default value
					//row.set(i, meta.getDefaultValue());
					//}
				}
			}
		}
	}
}
