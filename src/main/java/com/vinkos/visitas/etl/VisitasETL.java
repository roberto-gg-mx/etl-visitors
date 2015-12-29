package com.vinkos.visitas.etl;

import static com.vinkos.visitas.io.DataSourceManager.createZipFile;
import static com.vinkos.visitas.io.DataSourceManager.dropFile;
import static com.vinkos.visitas.io.DataSourceManager.listFiles;
import static com.vinkos.visitas.io.DataSourceManager.lookupFiles;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.vinkos.visitas.entity.Error;
import com.vinkos.visitas.entity.ErrorManager;
import com.vinkos.visitas.entity.Estadistica;
import com.vinkos.visitas.entity.EstadisticaManager;
import com.vinkos.visitas.entity.Visitante;
import com.vinkos.visitas.entity.VisitanteManager;
import com.vinkos.visitas.io.Configuration;
import com.vinkos.visitas.io.Notifier;
import com.vinkos.visitas.util.HibernateUtil;

public class VisitasETL {

	final static Logger logger = Logger.getLogger(VisitasETL.class);
	public static final String FIRST_VISIT_SQL = "SELECT V.id, V.email, "
			+ "MIN(V.primeraVisita) AS primeraVisita, ultimaVisita, "
			+ "visitasTotales, visitasAnioActual, visitasMesActual "
			+ "FROM Visitante V WHERE id IN (:list) GROUP BY V.id, V.ultimaVisita, "
			+ "V.visitasTotales, V.visitasAnioActual, V.visitasMesActual";
	public static final String LAST_VISIT_SQL = "SELECT V.id, V.email, "
			+ "max(V.primeraVisita) AS primeraVisita, ultimaVisita, "
			+ "visitasTotales, visitasAnioActual, visitasMesActual "
			+ "FROM Visitante V WHERE id IN (:list) GROUP BY V.id, V.ultimaVisita, "
			+ "V.visitasTotales, V.visitasAnioActual, V.visitasMesActual";
	private Validator val;
	private SessionFactory factory;
	private List<List<String>> rawData;
	private List<Estadistica> estadisticas;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public VisitasETL() {
		super();
		val = new Validator(Configuration.get("validator").toString());
		factory = HibernateUtil.getSessionFactory();
		estadisticas = new ArrayList<>();
	}

	public VisitasETL(String configFile) {
		this();
		Configuration.loadParams(configFile);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public void close() {
		if (!factory.isClosed()) {
			factory.close();
		}
	}

	public boolean extract(String fileName) {
		try {
			rawData = ExtractorCSV.readCsvFile(fileName, val.getHeaders());
		} catch (Exception ex) {
			logger.error("Error in extraction", ex);
			ErrorManager.addError("fileName", Error.Type.SOURCE_FILE);
		}
		return rawData.size() != 0;
	}

	/**
	 * Load (beginTransaction, ..., commit, backup, manage files)
	 */
	public boolean load() {
		Map<Integer, Visitante> firstVisits;
		Map<Integer, Visitante> lastVisits;
		Visitante first;
		Visitante last;
		Visitante newVisitor;
		boolean status = true;

		Calendar newDate = GregorianCalendar.getInstance();
		Calendar lastDate = GregorianCalendar.getInstance();

		estadisticas = EstadisticaManager.add(estadisticas);

		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			// Recuperar fecha primera visita en tabla visitante de todos
			firstVisits = VisitanteManager.getVisits(estadisticas, FIRST_VISIT_SQL, session);
			// consultar fecha ultima visita
			lastVisits = VisitanteManager.getVisits(estadisticas, LAST_VISIT_SQL, session);

			// por cada estadistica:
			for (Estadistica e : estadisticas) {
				try {
					// si existe primera visita:
					if (firstVisits.containsKey(e.getId())) {
						first = firstVisits.get(e.getId());
						last = lastVisits.get(e.getId());
						newDate.setTime(e.getFechaOpen());
						lastDate.setTime(last.getUltimaVisita());
						// si la fecha de la última visita es del mismo año:
						if (newDate.get(Calendar.YEAR) == lastDate.get(Calendar.YEAR)) {
							// si es del mismo mes:
							if (newDate.get(Calendar.MONTH) == lastDate.get(Calendar.MONTH)) {
								// crear visitante sumando el acumulado
								last.setUltimaVisita(e.getFechaOpen());
								last.setVisitasTotales(last.getVisitasTotales() + 1);
								last.setVisitasAnioActual(last.getVisitasAnioActual() + 1);
								last.setVisitasMesActual(last.getVisitasMesActual() + 1);
								VisitanteManager.update(last, session);
							} else {
								// crear registro de visitante del mes
								newVisitor = new Visitante();
								newVisitor.setEmail(e.getEmail());
								newVisitor.setPrimeraVisita(first.getPrimeraVisita());
								newVisitor.setUltimaVisita(e.getFechaOpen());
								newVisitor.setVisitasTotales(last.getVisitasTotales() + 1);
								newVisitor.setVisitasAnioActual(last.getVisitasAnioActual() + 1);
								newVisitor.setVisitasMesActual(1);
								VisitanteManager.add(newVisitor, session);
							}
						} else { // es la primera vez en el año
							// registrar fechaUltimaVisita, visitasTotales +
							// new, visitasAnioActual + new, visitasMesActual +
							// new
							newVisitor = new Visitante();
							newVisitor.setEmail(e.getEmail());
							newVisitor.setPrimeraVisita(first.getPrimeraVisita());
							newVisitor.setUltimaVisita(e.getFechaOpen());
							newVisitor.setVisitasTotales(last.getVisitasTotales() + 1);
							newVisitor.setVisitasAnioActual(1);
							newVisitor.setVisitasMesActual(1);
							VisitanteManager.add(newVisitor, session);
						}
					} else { // es la primera vez
						// registrar en visitante esta última visita
						newVisitor = new Visitante();
						newVisitor.setEmail(e.getEmail());
						newVisitor.setPrimeraVisita(e.getFechaOpen());
						newVisitor.setUltimaVisita(e.getFechaOpen());
						newVisitor.setVisitasTotales(1);
						newVisitor.setVisitasAnioActual(1);
						newVisitor.setVisitasMesActual(1);
						VisitanteManager.add(newVisitor, session);
					}
				} catch (Exception ex) {
					ErrorManager.addError(estadisticas.toString(), Error.Type.STORE_INCONSITENCY);
				}
			}

			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
			status = false;
		} finally {
			session.close();
		}
		return status;
	}

	public void process() {
		String backupPath = Configuration.get("backupPath").toString();
		String pathResource = Configuration.get("pathResource").toString();
		String username = Configuration.get("username").toString();
		String password = Configuration.get("password").toString();
		String ipv4 = Configuration.get("url").toString();
		String emailAdmin = Configuration.get("emailAdmin").toString();
		Set<String> backedUp = listFiles(backupPath, ".zip");
		for (String file : lookupFiles(pathResource, ipv4, username, password, 
				backupPath, backedUp)) {
			if (extract(file) && transform(file) && load() && createZipFile(backupPath, file)) {
				logger.info("File '" + file + "' has been processed succesfully!");
			} else {
				logger.warn("File '" + file + "' has not been processed syccesfully :/");
				Notifier.sendSimpleEmail(emailAdmin, "roberto.gg.mx@gmail.com",
						"localhost", "Visitas Error", "Error in ETL process for file" + file);
			}
			dropFile(backupPath, file);// Local deletion file
		}
		close();
	}

	/**
	 * Validation: Rules, format, dictionaries, nulls.
	 */
	public boolean transform(String fileName) {
		val.run(rawData);
		for (List<String> row : rawData) {
			try {
				estadisticas.add(new Estadistica(row));
			} catch (Exception ex) {
				logger.error("Error in transform", ex);
				ErrorManager.addError(fileName, Error.Type.BACKUP_DELETION);
			}
		}
		return estadisticas.size() != 0;
	}
}
