/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;

/**
 *
 * @author jupiter
 */
public class MySQLConn {

        Connection connection;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        @Inject
         public MySQLConn() {
                try {
                        Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                        System.err.println("Where is your MySQL JDBC Driver?");
                        e.printStackTrace();
                        return;
                }
                try {
                        //sessionVariables=wait_timeout=600
                        connection = DriverManager
//                                .getConnection("jdbc:mysql://localhost:3306/facebookdb?relaxAutoCommit=true&wait_timeout=2147483&autoReconnect=true&character_set_server=utf8mb4&useSSL=false", "root", "root");
                                .getConnection("jdbc:mysql://localhost:3306/facebookdb?relaxAutoCommit=true&wait_timeout=2147483&autoReconnect=true&character_set_server=utf8mb4&useSSL=false", "root", "hasan!xin");
//                    .getConnection("jdbc:mysql://localhost:3306/facebookdb?relaxAutoCommit=true&sessionVariables=wait_timeout=2147483&autoReconnect=true", "root", "meanwhile2015DBLab");

                } catch (SQLException e) {
                        System.err.println("Connection Failed! Check output console");
                        e.printStackTrace();
                }
                
                if (connection == null) {
                        System.err.println("Failed to make connection!");
                }
        }

        public Connection getConn() {
                return connection;
        }

        public int getTwitterMaxHistoryId() {
                int historyId = 0;
                Statement postStmt1 = null;
                try {
                        postStmt1 = connection.createStatement();
                        ResultSet rs1 = postStmt1.executeQuery("select max(id) from twitterhistory");
                        while (rs1.next()) {
                                historyId = rs1.getInt(1);
                        }
                } catch (Exception e) {
                } finally {
                        if (postStmt1 != null) {
                                try {
                                        postStmt1.close();
                                } catch (Exception e) {
                                }
                        }
                }
                return historyId;
        }

        public void saveTwitterKeyword(String filterKeywords, String fromDate, String toDate, String eventFrom, String eventTo, String location, String searchResources, String sourceFrom, String queryCatlog) {
//                System.out.println(filterKeywords);
                  PreparedStatement preparedStatement = null;
                try {

                        preparedStatement = connection
                                .prepareStatement("insert into twitterhistory values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        preparedStatement.setString(1, queryCatlog);
                        preparedStatement.setString(2, filterKeywords);
                        preparedStatement.setInt(3, 0);
                        if (fromDate.isEmpty()) {
                                preparedStatement.setTimestamp(4, null);
                        } else {
                                Date fromTimeStamp = dateFormat.parse(fromDate);
                                preparedStatement.setTimestamp(4, new Timestamp(fromTimeStamp.getTime()));
                        }
                        if (toDate.isEmpty()) {
                                preparedStatement.setTimestamp(5, null);
                        } else {
                                Date toTimeStamp = dateFormat.parse(toDate);
                                preparedStatement.setTimestamp(5, new Timestamp(toTimeStamp.getTime()));
                        }
                        if (eventFrom.isEmpty()) {
                                preparedStatement.setTimestamp(6, null);
                        } else {
                                Date fromTimeStamp = dateFormat.parse(eventFrom);
                                preparedStatement.setTimestamp(6, new Timestamp(fromTimeStamp.getTime()));
                        }
                        if (eventTo.isEmpty()) {
                                preparedStatement.setTimestamp(7, null);
                        } else {
                                Date toTimeStamp = dateFormat.parse(eventTo);
                                preparedStatement.setTimestamp(7, new Timestamp(toTimeStamp.getTime()));
                        }
                        preparedStatement.setString(8, location);
                        preparedStatement.setString(9, searchResources);
                        preparedStatement.setString(10, sourceFrom);
                        preparedStatement.executeUpdate();
                        connection.commit();
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                         if (preparedStatement != null) {
                                try {
                                        preparedStatement.close();
                                } catch (Exception e) {
                                }
                        }
                }
        }
        
        
        
        
}
