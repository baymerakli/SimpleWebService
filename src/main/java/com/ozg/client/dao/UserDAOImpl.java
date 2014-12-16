package com.ozg.client.dao;

//STEP 1. Import required packages
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ozg.client.model.User;
import com.ozg.ws.util.PropertyReader;

public class UserDAOImpl implements UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);
    private String jdbcDriver = "";
    private String dbUrl = "";

    private String dbUser = "";
    private String dbPassword = "";

    public UserDAOImpl() {
	loadConfiguration();
    }

    private void loadConfiguration() {
	try {
	    jdbcDriver = PropertyReader.getProperty("jdbcdriver", "com.mysql.jdbc.Driver");
	    dbUrl = PropertyReader.getProperty("db.url", "jdbc:mysql://localhost/first");
	    dbUser = PropertyReader.getProperty("db.user", "first");
	    dbPassword = PropertyReader.getProperty("db.password", "first");
	} catch (Exception e) {
	    LOGGER.error("Configuration Loading Failed", e);
	}
    }

    @Override
    public List<User> getUsers() {
	Connection conn = null;
	Statement stmt = null;
	List<User> users = new ArrayList<User>();
	try {
	    Class.forName(jdbcDriver);

	    conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
	    stmt = conn.createStatement();
	    String sql;
	    sql = "SELECT first_name,last_name FROM User";
	    ResultSet rs = stmt.executeQuery(sql);

	    while (rs.next()) {
		String firstName = rs.getString("first_name");
		String lastName = rs.getString("last_name");

		User newUser = new User();
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		users.add(newUser);
	    }
	    rs.close();
	    stmt.close();
	    conn.close();
	} catch (SQLException e) {
	    LOGGER.error("Getting user list Failed", e);
	} catch (Exception e) {
	    LOGGER.error("Getting user list Failed", e);
	} finally {
	    try {
		if (stmt != null)
		    stmt.close();
	    } catch (SQLException e) {
		LOGGER.error("Configuration Loading Failed", e);
	    }
	    try {
		if (conn != null)
		    conn.close();
	    } catch (SQLException e) {
		LOGGER.error("Getting user list Failed", e);
	    }
	}
	return users;
    }

}