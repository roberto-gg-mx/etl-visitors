package com.vinkos.visitas.entity;

import java.util.List;

import static com.vinkos.visitas.etl.Validator.formatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import com.vinkos.visitas.util.HibernateUtil;

public class EstadisticaManager {

	private static SessionFactory factory;

	public static void main(String[] args) {
		try {
			factory = HibernateUtil.getSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		EstadisticaManager ME = new EstadisticaManager();

		/* Add few Estadistica records in database */
		Estadistica obj1 = new Estadistica();
		Estadistica obj2 = new Estadistica();
		Estadistica obj3 = new Estadistica();
		try {
			obj1 = new Estadistica("david.eduardo.vega@hotmail.com", "", "", "",
					formatter.parse("08/02/2013 18:30"),
					formatter.parse("08/02/2013 11:42"), 1, 0, 
					formatter.parse("08/02/2013 00:00"),
					0, 5, "-", "201.156.15.171", "Chrome Generic", "");

			obj2 = new Estadistica("l_pch@yahoo.com", "", "", "",
					formatter.parse("08/02/2013 18:30"), 
					formatter.parse("08/02/2013 11:42"), 1, 0,
					formatter.parse("08/02/2013 00:00"), 0, 5, "-", "201.156.15.171", "Chrome Generic", "");
		
			obj3 = new Estadistica("fxanax6@yahoo.com", "", "", "",
					formatter.parse("08/02/2013 18:30"),
					formatter.parse("08/02/2013 11:42"), 1, 0,
					formatter.parse("08/02/2013 00:00"), 0, 4, "-", "201.156.15.171", "Chrome Generic", "Win7");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Integer empID1 = 0;
		Integer empID2 = 0;
		Integer empID3 = 0;
		
		if (getEstadisticaByEmail(obj1.getEmail()) == null) {
			empID1 = EstadisticaManager.addEstadistica(obj1);
		}
		if (getEstadisticaByEmail(obj2.getEmail()) == null) {
			empID2 = EstadisticaManager.addEstadistica(obj2);
			ME.updateEstadistica(empID1, 42);
			ME.deleteEstadistica(empID2);
		}
		if (getEstadisticaByEmail(obj3.getEmail()) == null) {
			empID3 = EstadisticaManager.addEstadistica(obj3);
			ME.updateEstadistica(empID3, 23);
		}

		/* List down all the statistics */
		ME.listEstadisticas();

		Estadistica est1 = getEstadisticaByEmail("fxanax6@yahoo.com");
		Estadistica est2 = getEstadisticaByEmail("rgg_u2@hotmail.com");
		System.out.println("fxanax6@yahoo.com:" + est1);
		System.out.println("rgg_u2@hotmail.com:" + est2);
		factory.close();
	}

	public static boolean addEstadisticas(List<Estadistica> estadisticas, Session session) {
		Estadistica est;
		for (int i = 0; i < estadisticas.size(); i++) {
			//est = EstadisticaManager.getEstadisticaByEmail(estadisticas.get(i).getEmail());
			//if (est == null) {
				session.save(estadisticas.get(i));
				//} else {
				//estadisticas.get(i).setId(est.getId());
				//session.update(estadisticas.get(i));
				//}
			if (i % 50 == 0) { // Same as the JDBC batch size
				// flush a batch of inserts and release memory:
				session.flush();
				session.clear();
			}
		}
		return true;
	}

	public static Integer addEstadistica(Estadistica estadistica) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer estadisticaID = null;
		try {
			tx = session.beginTransaction();
			estadisticaID = (Integer) session.save(estadistica);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return estadisticaID;
	}

	public void listEstadisticas() {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Estadistica> estadisticas = session.createQuery("FROM Estadistica").list();
			for (Iterator<Estadistica> iterator = estadisticas.iterator(); iterator.hasNext();) {
				Estadistica estadistica = (Estadistica) iterator.next();
				System.out.print("First Name: " + estadistica.getEmail());
				System.out.print("  Last Name: " + estadistica.getBaja());
				System.out.println("  Salary: " + estadistica.getFechaEnvio());
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void updateEstadistica(Integer estadisticaID, int clicks) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Estadistica estadistica = (Estadistica) session.get(Estadistica.class, estadisticaID);
			estadistica.setClicks(clicks);
			session.update(estadistica);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteEstadistica(Integer estadisticaID) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Estadistica estadistica = (Estadistica) session.get(Estadistica.class, estadisticaID);
			session.delete(estadistica);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static Estadistica getEstadisticaByEmail(String email) {
		Session session = factory.openSession();
		Estadistica estadistica = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<Estadistica> estadisticas = session.createQuery("FROM Estadistica e WHERE e.email = :email").setParameter("email", email).list();
			if (!estadisticas.isEmpty()) {
				estadistica = estadisticas.get(0);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return estadistica;
	}

	/*
	 * SELECT fechaOpen FROM Estadistica WHERE email LIKE '%' ORDER BY fechaOpen ASC LIMIT 1
	 * SELECT MIN(fechaOpen) FROM Estadistica WHERE email = :email 
	 * 
	 * SELECT COUNT(opens) FROM Estadistica WHERE email = :email
	 * 
	 * SELECT YEAR(fechaOpen) FROM Estadistica;
	 * SELECT YEAR(NOW());
	 * WHERE t1.website != ''
	 * AND YEAR(time) = YEAR(NOW())
	 * AND MONTH(time) = MONTH(NOW())
	 * 
	 * 
	 * visitas totales
	 * visitas año actual
	 * visitas mes actual
	 * 
	 * SELECT MAX(fechaOpen) FROM Estadistica
	 */
	public List<Visitante> listReport() {
		List<Visitante> visitors = new ArrayList<>();
		Visitante visitor;
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "SELECT id, email, MAX(fechaOpen) lastVisit FROM Estadistica GROUP BY id, email";
			SQLQuery query = session.createSQLQuery(sql);
			//query.setParameter("email", "");
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List data = query.list();

			for (Object object : data) {
				Map<String, Object> row = (Map<String, Object>) object;
				visitors.add(new Visitante(row));
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return visitors;
	}
}