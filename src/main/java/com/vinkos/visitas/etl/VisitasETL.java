package com.vinkos.visitas.etl;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

import com.vinkos.visitas.entity.Error;
import com.vinkos.visitas.entity.Estadistica;
import com.vinkos.visitas.entity.EstadisticaManager;
import com.vinkos.visitas.entity.Visitante;
import com.vinkos.visitas.util.HibernateUtil;

/**
 * Source Qualifier:  	reads data from Flat File and Relational Sources
 * Expression:  		performs row-level calculations
 * Filter:  			drops rows conditionally
 * Joiner:  			joins heterogeneous sources 
 * Aggregator:  		performs aggregate calculations
 * Lookup:  			looks up values and passes them to other objects
 * Rank:  				limits records to the top or bottom of a range
 * Router:  			splits rows conditionally
 * Sequence Generator:  generates unique ID values
 * Sorter:  			sorts data
 * App Source Qualifier:reads Application object sources as ERP
 * SQL: 				Executes SQL queries against the database
 * Stored Procedure:  	calls a database stored procedure
 * Transaction Control: Defines Commit and Rollback transactions
 * Union:				Merges data from different databases
 * Update Strategy:  	tags rows for insert, update, delete, reject
*/

public class VisitasETL {// implements ETL 

	private List<List<String>> rawData;
	private List<Estadistica> estadisticas;
	private List<Visitante> visitantes;
	private List<Error> error;

	private Validator val;
	private SessionFactory factory;
	private List<Metadata> meta;

	public VisitasETL() {
		val = new Validator();
		meta = val.getMetadata("visitas.json");
		visitantes = new ArrayList<>();
		error = new ArrayList<>();
		factory = HibernateUtil.getSessionFactory();
		estadisticas = new ArrayList<>();
		visitantes = new ArrayList<>();
		error = new ArrayList<>();
	}

	public void extract(String fileName) {
		rawData = ExtractorCSV.readCsvFile(fileName, val.getHeaders(meta));
	}

	
	public void transform() {
		val.run(rawData);
		for (List<String> row : rawData) {
			estadisticas.add(new Estadistica(row));
		}
	}

	public void load() {
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();

		EstadisticaManager.addEstadisticas(estadisticas, session);

		tx.commit();
		session.close();
		
	}

	public void close() {
		factory.close();
	}
}
