package com.vinkos.visitas.etl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.vinkos.visitas.entity.Estadistica;

import org.apache.commons.csv.CSVPrinter;
import static com.vinkos.visitas.etl.Validator.formatter;

//TODO: Podemos tener los headers, y con ello saber la longitud
//TODO: rawData

public class ExtractorCSV {

	public static List<List<String>> readCsvFile(String fileName, String[] headers) {
		FileReader fileReader = null;
		CSVParser csvFileParser = null;
		
		//Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(headers);
     
    	//Create a new list of student to be filled by CSV file data 
    	List<List<String>> data = new ArrayList<>();
    	List<String> row;
        
        try {
            //initialize FileReader object
            fileReader = new FileReader(fileName);
            
            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);
            
            //Get a list of CSV file records
            List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (CSVRecord record : csvRecords) {
            	row = new ArrayList<>();
            	for (String column : headers) {
            		row.add(record.get(column));
            	}
            	data.add(row);
			}
            
            //Print the new student list
            //for (Estadistica estadistica : estadisticas) {
			//	System.out.println(estadistica.toString());
			//}
        } 
        catch (Exception e) {
        	System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
            	System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }
        return data;
	}

	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	public static void writeCsvFile(String fileName, String[] headers) {
		//Create a new list of Estadistica objects
		List<Estadistica> rows = new ArrayList<Estadistica>();

		try {
			Estadistica obj1 = new Estadistica(1, "migue_235@yahoo.com","","","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("-"),0,0,
					formatter.parse("-"),0,0,"-","-","-","-");
			Estadistica obj2 = new Estadistica(2, "mrtachi@yahoo.com","","","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("-"),0,0,
					formatter.parse("-"),0,0,"-","-","-","-");
			Estadistica obj3 = new Estadistica(3, "hugo-nieves@yahoo.com","","","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("-"),0,0,
					formatter.parse("-"),0,0,"-","-","-","-");
			Estadistica obj4 = new Estadistica(4, "pekeax@yahoo.com","","","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("-"),0,0,
					formatter.parse("-"),0,0,"-","-","-","-");
			Estadistica obj5 = new Estadistica(5, "fxanax6@yahoo.com","","HARD","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("-"),0,0,
					formatter.parse("-"),0,0,"-","-","-","-");
			Estadistica obj6 = new Estadistica(6, "l_pch@yahoo.com","","","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("-"),0,0,
					formatter.parse("-"),0,0,"-","-","-","-");
			Estadistica obj7 = new Estadistica(6, "david.eduardo.vega@hotmail.com","","","",
					formatter.parse("08/02/2013 18:30"),formatter.parse("08/02/2013 11:42"),1,0,
					formatter.parse("-"),0,0,"-","201.156.15.171","Chrome Generic","Win7");
			
			rows.add(obj7);
			rows.add(obj1);
			rows.add(obj2);
			rows.add(obj3);
			rows.add(obj4);
			rows.add(obj5);
			rows.add(obj6);
	
		} catch(ParseException ex) {
			ex.printStackTrace();
		}
		
		FileWriter fileWriter = null;
		
		CSVPrinter csvFilePrinter = null;
		
		//Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
				
		try {
			
			//initialize FileWriter object
			fileWriter = new FileWriter(fileName);
			
			//initialize CSVPrinter object 
	        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
	        
	        //Create CSV file header
	        csvFilePrinter.printRecord((Object[])headers);
			
			//Write a new student object list to the CSV file
			for (Estadistica row : rows) {
				List<String> rowDataRecord = new ArrayList<String>();
	            rowDataRecord.add(row.getEmail());
	            rowDataRecord.add(row.getJyv());
	            rowDataRecord.add(row.getBadMail());
	            rowDataRecord.add(row.getBaja());
	            rowDataRecord.add(formatter.format(row.getFechaEnvio()));
				rowDataRecord.add(formatter.format(row.getFechaOpen()));
				rowDataRecord.add(String.valueOf(row.getOpens()));
				rowDataRecord.add(String.valueOf(row.getOpensVirales()));
				rowDataRecord.add(String.valueOf(row.getFechaClick()));
				rowDataRecord.add(String.valueOf(row.getClicks()));
				rowDataRecord.add(String.valueOf(row.getClicksVirales()));
				rowDataRecord.add(row.getLink());
				rowDataRecord.add(row.getIp());
				rowDataRecord.add(row.getNavegador());
				rowDataRecord.add(row.getPlataforma());
	            csvFilePrinter.printRecord(rowDataRecord);
			}

			System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String fileName = System.getProperty("user.home")+"/Desktop/report_8.txt";//
		
		//System.out.println("Write CSV file:");
		//ReadCVS.writeCsvFile(fileName);
		
		System.out.println("\nRead CSV file:");
		ExtractorCSV.readCsvFile(fileName, new String[]{
				"email", "jyv", "Badmail", "Baja", "Fecha envio", "Fecha open",
	    		"Opens", "Opens virales", "Fecha click", "Clicks", "Clicks virales",
	    		"Links", "IPs", "Navegadores", "Plataformas"});

	}
}