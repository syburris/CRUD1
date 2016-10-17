package com.theironyard;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.get(
                "/",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("userName");
                    User user = selectUser(conn,name);

                    HashMap m = new HashMap();
                    if (user != null) {
                        m.put("name", user.name);
                    }
                    ArrayList<Client> clients = selectClients(conn, user);
                    m.put("clients", clients);
                    selectClients(conn,user);
                    return new ModelAndView(m,"home.html");
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("userName");
                    String password = request.queryParams("password");

                    User user = selectUser(conn,name);
                    if (user == null) {
                        insertUser(conn,name,password);
                    }
                    else if (!password.equals(user.password)) {
                        response.redirect("/");
                        return null;
                    }

                    Session session = request.session();
                    session.attribute("userName", name);
                    response.redirect("/");

                    return null;
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();;
                    response.redirect("/");
                    return null;
                }
        );

        Spark.post(
                "/create-client",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("username");
                    User user = selectUser(conn, userName);

                    return null;
                }
        );
    }



    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS clients (id IDENTITY, name VARCHAR, hospital VARCHAR, " +
                "email VARCHAR, phone VARCHAR, street VARCHAR, city VARCHAR, state VARCHAR, zip VARCHAR)");
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1,name);
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id,name,password);
        }
        return null;
    }
    public static ArrayList<Client> selectClients(Connection conn, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM clients");
        ResultSet results = stmt.executeQuery();
        ArrayList<Client> clients = new ArrayList<>();
        while (results.next()) {
            int id = results.getInt("id");
            String name = results.getString("name");
            String hospital = results.getString("hospital");
            String email = results.getString("email");
            String phone = results.getString("phone");
            String street = results.getString("street");
            String city = results.getString("city");
            String state = results.getString("state");
            String zip = results.getString("zip");
            int userID = user.id;
            Client client = new Client(id, name, hospital, email, phone, street, city, state, zip);
            clients.add(client);
        }
        return clients;
    }
    public void insertClients(Connection conn, String name, String hospital, String email, String phone, String street,
                              String city, String state, String zip, User user) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO clients VALUES (null)");
    }
}
