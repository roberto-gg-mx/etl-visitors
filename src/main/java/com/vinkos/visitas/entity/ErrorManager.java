package com.vinkos.visitas.entity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.List;

import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.vinkos.visitas.entity.Error.Type;
import com.vinkos.visitas.util.HibernateUtil;

public class ErrorManager {

	private static SessionFactory factory;

	public static void main(String[] args) {
		try {
			factory = HibernateUtil.getSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		ErrorManager ME = new ErrorManager();

		Date today = new Date();
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(today);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		Integer empID1 = ErrorManager.addError("david.eduardo.vega@hotmail.com", Type.DATA_VALIDATION);
		Integer empID2 = ErrorManager.addError("l_pch@yahoo.com", Type.DATA_VALIDATION);
		Integer empID3 = ErrorManager.addError("fxanax6@yahoo.com", Type.DATA_VALIDATION);

		/* List down all the employees */
		ME.listErrors();

		/* Update employee's records */
		ME.updateError(empID1, 5000);

		/* Update employee's records */
		ME.updateError(empID3, 5000);

		/* Delete an employee from the database */
		ME.deleteError(empID2);

		/* List down new list of the employees */
		ME.listErrors();
	}

	public static boolean addErrors(List<Error> errors, Session session) {
		for (int i = 0; i < 100000; i++) {
			session.saveOrUpdate(errors.get(i));
			if (i % 50 == 0) { // Same as the JDBC batch size
				// flush a batch of inserts and release memory:
				session.flush();
				session.clear();
			}
		}

		return true;
	}

	public static Integer addError(String registro, Type tipoError) {
		Session session = factory.openSession();
		Transaction tx = null;
		Integer errorID = null;
		try {
			tx = session.beginTransaction();
			errorID = (Integer) session.save(new Error(registro, tipoError));
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return errorID;
	}

	public void listErrors() {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Error> errors = session.createQuery("FROM Error").list();
			for (Iterator<Error> iterator = errors.iterator(); iterator.hasNext();) {
				Error error = (Error) iterator.next();
				System.out.print("  Registro: " + error.getRegistro());
				System.out.println("    Tipo: " + error.getTipoError());
				System.out.println("   Fecha: " + error.getFecha());
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

	public void updateError(Integer errorID, int clicks) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Estadistica estadistica = (Estadistica) session.get(Estadistica.class, errorID);
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

	public void deleteError(Integer errorID) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Error error = (Error) session.get(Error.class, errorID);
			session.delete(error);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
}