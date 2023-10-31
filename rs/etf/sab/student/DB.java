/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author glisha
 */
public class DB {
    
    private static final String username = "sa";
    private static final String password = "123";
    private static final String database="prodavnica";
    private static final int port= 1433;
    private static final String server="localhost";
    
    private static final String connectionUrl="jdbc:sqlserver://"+server+":"+port+";databaseName="+database +
            ";integratedSecurity=false;trustServerCertificate=true;";
    
    private static DB db = null;
    private Connection connection = null;
    
    private DB(){
        try {
            connection = DriverManager.getConnection(connectionUrl,username,password);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection(){
        return this.connection;
    }
    
    public static DB getInstance(){
        if(db == null){
            db = new DB();
        }
        
        return db;
    }
    
}
