/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.soltelec.conexion_seriales.Conexion;
/**
 *
 * @author GerenciaDesarrollo
 */
public class Conex {

    public static Connection getConnection() throws ClassNotFoundException {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Conexion.setConexionFromFile();
            conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return conexion;
    }
}
