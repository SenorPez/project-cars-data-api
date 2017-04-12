package com.senorpez.projectcars;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DB2 {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://pcarsapi.cbwuidepjacv.us-west-2.rds.amazonaws.com:3306/";
    private static final String ADMIN_USER = "root";
    private static final String ADMIN_PASS = "7,U-~N^EQhau8MAH";

    public static void main(String[] args) {
    	Connection conn = null;
	Statement stmt = null;
	String sql;

	try {
	    Class.forName(JDBC_DRIVER);
	    System.out.println("Connecting to database...");
	    conn = DriverManager.getConnection(DB_URL, ADMIN_USER, ADMIN_PASS);
	    stmt = conn.createStatement();

	    System.out.println("Closing...");

	    stmt.close();
	    conn.close();

        } catch (ClassNotFoundException | SQLException e) {
	    e.printStackTrace();
	} finally {
	    try {
	        if (stmt != null) stmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    try {
	        if (conn != null) conn.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
    }
}
