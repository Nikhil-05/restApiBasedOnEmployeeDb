package com.project.DbUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {
	
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/my_database";
	private static final String JDBC_USERNAME = "root";
	private static final String JDBC_PASSWORD = "Nikhil@hell2608";
	
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
	}
	
	//Execute Query 
	
	public static ResultSet executeQuery(String sql, Object...params) {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			setParameters(statement,params);
			return statement.executeQuery();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	// method to insert, update or delete 
	
	public static int executeUpdate(String sql, Object...params) {
		try {
			Connection connection = getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			setParameters(statement, params);
			return statement.executeUpdate();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
	}

	private static void setParameters(PreparedStatement statement, Object... params) throws SQLException {
		// TODO Auto-generated method stub
		if(params != null) {
			for(int i = 0;i<params.length;i++) {
				statement.setObject(i+1, params[i]);
			}
		}
		
	}

}
