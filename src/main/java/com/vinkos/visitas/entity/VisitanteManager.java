package com.vinkos.visitas.entity;

import java.util.List;

import static com.vinkos.visitas.etl.Validator.formatter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import com.vinkos.visitas.util.HibernateUtil;

public class VisitanteManager {

	private static SessionFactory factory = HibernateUtil.getSessionFactory();

	public static List<Visitante> add(List<Visitante> visitantes) {
		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();
		for (int i = 0; i < visitantes.size(); i++) {
			session.save(visitantes.get(i));
			if (i % 50 == 0) { // Same as the JDBC batch size
				// flush a batch of inserts and release memory:
				session.flush();
				session.clear();
			}
		}
		tx.commit();
		session.close();

		return visitantes;
	}

	public static Integer add(Visitante visitante, Session session) {
		return (Integer) session.save(visitante);
	}

	public static void list() {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Visitante> visitantes = session.createQuery("FROM Visitante").list();
			for (Iterator<Visitante> iterator = visitantes.iterator(); iterator.hasNext();) {
				Visitante visitante = (Visitante) iterator.next();
				System.out.print("First Name: " + visitante.getEmail());
				System.out.print("  Last Name: " + visitante.getVisitasAnioActual());
				System.out.println("  Salary: " + visitante.getVisitasMesActual());
				System.out.println("  Salary: " + visitante.getVisitasTotales());
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

	public static void update(Integer visitanteID, int visitasAnioActual) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Visitante visitante = (Visitante) session.get(Visitante.class, visitanteID);
			visitante.setVisitasAnioActual(visitasAnioActual);
			session.update(visitante);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public static void delete(Integer visitanteID) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Visitante visitante = (Visitante) session.get(Visitante.class, visitanteID);
			session.delete(visitante);
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
	public static Visitante getVisitanteByEmail(String email) {
		Session session = factory.openSession();
		Visitante visitante = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<Visitante> visitantes = session.createQuery("FROM Visitante e WHERE e.email = :email")
					.setParameter("email", email).list();
			if (!visitantes.isEmpty()) {
				visitante = visitantes.get(0);
				System.out.println(visitante);
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return visitante;
	}

	/*
	 * SELECT fechaOpen FROM Estadistica WHERE email LIKE '%' ORDER BY fechaOpen
	 * ASC LIMIT 1 SELECT MIN(fechaOpen) FROM Estadistica WHERE email = :email
	 * 
	 * SELECT COUNT(opens) FROM Visitante WHERE email = :email
	 * 
	 * SELECT YEAR(fechaOpen) FROM Visitante; SELECT YEAR(NOW()); WHERE
	 * t1.website != '' AND YEAR(time) = YEAR(NOW()) AND MONTH(time) =
	 * MONTH(NOW())
	 * 
	 * SELECT id, mail, fechaPrimeraVisita, fechaUltimaVisita, visitasTotales,
	 * visitasAnioActual, visitasMesActual FROM Visitante, GROUP BY id, mail,
	 * fechaPrimeraVisita, fechaUltimaVisita, visitasTotales, visitasAnioActual,
	 * visitasMesActual
	 * 
	 * visitas totales visitas año actual visitas mes actual
	 * 
	 * SELECT MAX(fechaOpen) FROM Estadistica
	 */
	/**
	 * begin_transaction() Recuperar fecha primera visita en tabla visitante de
	 * todos for each fila: try: si existe primera visita: consultar fecha
	 * ultima visita si es del mismo mes: actualizar fechaUltimaVisita,
	 * visitasTotales + new, visitasAnioActual + new, visitasMesActual + new de
	 * lo contrario: si la fecha de la última visita es del mismo año: crear
	 * visitante sumando el acumulado de lo contrario: crear visitante de lo
	 * contrario:// es la primera vez registrar en visitante esta última visita
	 * catch: Tabla_error -> Registrar error commit()
	 */
	@SuppressWarnings("unchecked")
	public static List<Visitante> listReport(List<Estadistica> estadistica) {
		String sql = "SELECT id, MAX(fechaPrimeraVisita) lastVisit FROM Visitante GROUP BY id, email";
		List<Visitante> visitors = new ArrayList<>();
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			SQLQuery query = session.createSQLQuery(sql);
			// query.setParameter("email", "");
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			List<Visitante> data = (List<Visitante>) query.list();

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

	@SuppressWarnings("unchecked")
	public static Map<Integer, Visitante> getVisits(List<Estadistica> estadisticas, String hql, Session session) {
		Map<Integer, Visitante> data = new HashMap<>();

		SQLQuery query = session.createSQLQuery(hql);
		query.setParameterList("list", estadisticas);
		// query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		List<Visitante> visitante = (List<Visitante>) query.list();

		for (Visitante v : visitante) {
			data.put(v.getId(), (Visitante) v);
		}

		return data;
	}

	public static void update(Visitante visitor, Session session) {
		Visitante visitante = (Visitante) session.get(Visitante.class, visitor.getId());
		visitante.setVisitasAnioActual(visitor.getVisitasAnioActual());
		session.update(visitante);
	}

	public static void main(String[] args) {
		try {
			factory = HibernateUtil.getSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		Session session = factory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			/* Add few Visitante records in database */
			Visitante obj1 = new Visitante();
			Visitante obj2 = new Visitante();
			Visitante obj3 = new Visitante();

			obj1 = new Visitante("david.eduardo.vega@hotmail.com", formatter.parse("08/02/2013 18:30"),
					formatter.parse("08/02/2013 11:42"), 1, 0, 0);

			obj2 = new Visitante("l_pch@yahoo.com", formatter.parse("08/02/2013 18:30"),
					formatter.parse("08/02/2013 11:42"), 1, 0, 0);

			obj3 = new Visitante("fxanax6@yahoo.com", formatter.parse("08/02/2013 18:30"),
					formatter.parse("08/02/2013 11:42"), 1, 0, 0);

			Integer empID1 = 0;
			Integer empID2 = 0;
			Integer empID3 = 0;

			if (getVisitanteByEmail(obj1.getEmail()) == null) {
				empID1 = (Integer) session.save(obj1);
			}
			if (getVisitanteByEmail(obj2.getEmail()) == null) {
				empID2 = (Integer) session.save(obj2);
				update(empID1, 42);
				delete(empID2);
			}
			if (getVisitanteByEmail(obj3.getEmail()) == null) {
				empID3 = (Integer) session.save(obj3);
				update(empID3, 23);
			}

			/* List down all the statistics */
			list();

			Visitante vi1 = getVisitanteByEmail("fxanax6@yahoo.com");
			Visitante vi2 = getVisitanteByEmail("rgg_u2@hotmail.com");
			System.out.println("fxanax6@yahoo.com:" + vi1);
			System.out.println("rgg_u2@hotmail.com:" + vi2);

			tx.commit();
		} catch (HibernateException | ParseException ex) {
			if (tx != null)
				tx.rollback();
			ex.printStackTrace();
		} finally {
			session.close();
		}

		factory.close();
	}
}