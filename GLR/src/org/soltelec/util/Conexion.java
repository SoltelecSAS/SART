/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author soltelec
 */
public class Conexion {
    public static Connection getConnection()throws ClassNotFoundException {
        Connection conexion = null;
        try {      
            Properties properties = new Properties();
            properties.load(new FileInputStream("./conexion.properties"));
            Class.forName("com.mysql.jdbc.Driver");        
            conexion = DriverManager.getConnection("jdbc:mysql://" + properties.getProperty("urljdbc") + ":3306/db_cda",properties.getProperty("usuario"),properties.getProperty("password"));
        } catch (IOException | SQLException e) {
            e.printStackTrace(System.err);
        }  
        
        return conexion;
    }
}
