/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.models.controllers;

import com.sun.rowset.CachedRowSetImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JOptionPane;


/**
 *
 * @author GerenciaDesarrollo
 */
public class Conexion {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost/";
    private static String username;
    private static String password;

    public static Connection conectar() throws SQLException, ClassNotFoundException, IOException {
        Class.forName(driver); 
         System.out.println("VOY A LEVANTAR OBJ CONEXION ");
        String db_cda = com.soltelec.modulopuc.configuracion.modelo.Conexion.getBaseDatos();
//        url = "jdbc:mysql://" + properties.getProperty("urljdbc") + "/db_cda";
        url = com.soltelec.modulopuc.configuracion.modelo.Conexion.getUrl();
       // JOptionPane.showMessageDialog(null, "url "+url);
        username = com.soltelec.modulopuc.configuracion.modelo.Conexion.getUsuario();  
       //  JOptionPane.showMessageDialog(null, "usuario "+username);
        password = com.soltelec.modulopuc.configuracion.modelo.Conexion.getContraseña();
       // JOptionPane.showMessageDialog(null, "usuario "+password);
        return DriverManager.getConnection(url, username, password);
    }

    public static CachedRowSetImpl consultar(String sql) throws SQLException, ClassNotFoundException, IOException {
        CachedRowSetImpl crs = new CachedRowSetImpl();
        Connection cn =Conexion.conectar();         
        try {
            crs.setUrl(com.soltelec.modulopuc.configuracion.modelo.Conexion.getUrl());
            crs.setUsername(com.soltelec.modulopuc.configuracion.modelo.Conexion.getUsuario());
            crs.setPassword(com.soltelec.modulopuc.configuracion.modelo.Conexion.getContraseña());
            crs.setCommand(sql);
            crs.execute(cn);          
        } finally {
           
        }
        return crs;
    }
    public static CachedRowSetImpl consultar(String sql,Connection conn) throws SQLException, ClassNotFoundException, IOException {
        CachedRowSetImpl crs = new CachedRowSetImpl();
        try {
             crs.setUrl(com.soltelec.modulopuc.configuracion.modelo.Conexion.getUrl());
            crs.setUsername(com.soltelec.modulopuc.configuracion.modelo.Conexion.getUsuario());
            crs.setPassword(com.soltelec.modulopuc.configuracion.modelo.Conexion.getContraseña());
             crs.setCommand(sql);
            crs.execute(conn);
        } finally {
           
        }
        return crs;
    }

    public static void crear(String tabla, String campos, String valores) throws SQLException, ClassNotFoundException, IOException {
        String sql = "INSERT INTO " + tabla + campos + " VALUES " + valores;
        Connection conn = conectar();
        //System.out.println("sql: " + sql); 
        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate(sql);
        } finally {
            conn.close();
        }
    }

    public static boolean ejecutarSentencia(String sql) throws SQLException, ClassNotFoundException, IOException {
        Connection conn = conectar();
        Statement stm = conn.createStatement();
        boolean estado = false;

        try {
            estado = stm.execute(sql);
        } finally {
            conn.close();
        }

        return estado;
    }

    public static boolean ejecutarVerificarSimulacion(String sql) throws SQLException, ClassNotFoundException, IOException {
        Connection conn = conectar();
        Statement stm = conn.createStatement();

        ResultSet result = stm.executeQuery(sql);
        boolean simulacion = false;

        while (result.next()) {
            simulacion = result.getBoolean(1);
        }

        return simulacion;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(ejecutarVerificarSimulacion("select cont_cda from cda where id_cda = 1"));
    }

}
