package com.botregistrar.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class MySqlDataSource {
	
	private static MySqlDataSource datasource;
    private BasicDataSource ds;

    private MySqlDataSource()  {
    	
    	String dbName = "registry";
		String dbHost = "localhost";
		String dbUsername = "root";
		String dbPassword = "root";
		
        ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername(dbUsername);
        ds.setPassword(dbPassword);
        ds.setUrl("jdbc:mysql://"+dbHost+"/"+dbName);
    }

    public static MySqlDataSource getInstance()  {
        if (datasource == null) {
            datasource = new MySqlDataSource();
        }
        return datasource;
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

	

}
