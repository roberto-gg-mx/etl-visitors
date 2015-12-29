package com.vinkos.visitas.io;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class JDBCTransaction {

	private static Connection dbConnection = null;
	final static Logger logger = Logger.getLogger(JDBCTransaction.class);

	private static Connection getDBConnection() {
		if (dbConnection != null) {
			return dbConnection;
		}

		try {
			Class.forName(Configuration.get("driver").toString());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

		try {
			dbConnection = DriverManager.getConnection(Configuration.get("uri").toString(),
					Configuration.get("user").toString(), Configuration.get("pwd").toString());
			return dbConnection;
		} catch (SQLException e) {
			logger.error("Establishing connection", e);
		}

		return dbConnection;
	}

	public static void doTransaction() throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatementInsert = null;
		PreparedStatement preparedStatementUpdate = null;

		String insertTableSQL = "INSERT INTO DBUSER"
				+ "(USER_ID, USERNAME, CREATED_BY, CREATED_DATE) VALUES"
				+ "(?,?,?,?)";

		String updateTableSQL = "UPDATE DBUSER SET USERNAME =? "
				+ "WHERE USER_ID = ?";

		try {
			dbConnection = getDBConnection();

			dbConnection.setAutoCommit(false);

			preparedStatementInsert = dbConnection.prepareStatement(insertTableSQL);
			preparedStatementInsert.setInt(1, 999);
			preparedStatementInsert.setString(2, "mkyong101");
			preparedStatementInsert.setString(3, "system");
			preparedStatementInsert.setTimestamp(4, getCurrentTimeStamp());
			preparedStatementInsert.executeUpdate();

			preparedStatementUpdate = dbConnection.prepareStatement(updateTableSQL);
			// preparedStatementUpdate.setString(1,
			// "A very very long string caused db error");
			preparedStatementUpdate.setString(1, "new string");
			preparedStatementUpdate.setInt(2, 999);
			preparedStatementUpdate.executeUpdate();

			dbConnection.commit();

			System.out.println("Done!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());
			dbConnection.rollback();

		} finally {

			if (preparedStatementInsert != null) {
				preparedStatementInsert.close();
			}

			if (preparedStatementUpdate != null) {
				preparedStatementUpdate.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}

	private static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}
}
