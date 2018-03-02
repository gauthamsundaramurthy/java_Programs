package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {

	private Connection conn = null;
	
	public Connector(String database, String userName, String password)
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?" +
				        "user=" + userName + "&password=" + password);
			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
			    System.out.println("SQLState: " + e.getSQLState());
			    System.out.println("VendorError: " + e.getErrorCode());
			
				e.printStackTrace();
			}

			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public Connection getConnector()
	{
		return this.conn;
	}
	
}
