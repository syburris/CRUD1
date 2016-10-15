package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Created by stevenburris on 10/15/16.
 */
public class MainTest {
    public Connection startConnection () throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testSelectUser() throws SQLException {
        Connection conn = startConnection();

    }

}