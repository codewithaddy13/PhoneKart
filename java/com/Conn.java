package com.phonekart.auth;

import java.sql.*;

public class Conn{
	Connection c;
	Statement s;
	public Conn() {
		try {
			
			c = DriverManager.getConnection("jdbc:mysql:///javaproject", "root", "addysql@13");
			s = c.createStatement();
		} catch(Exception e){
			System.out.println(e);
		}
	}
}


