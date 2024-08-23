/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;

/**
 *
 * @author GerenciaDesarrollo
 */
public class Conex {

    public static Connection getConnection() throws ClassNotFoundException {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContraseña());
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        }
        return conexion;
    }
}
