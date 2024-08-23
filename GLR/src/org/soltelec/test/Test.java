/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.soltelec.util.Conex;

/**
 *
 * @author SOLTELEC
 */
public class Test {

    private Connection cn;

    public static void main(String[] Args) throws ClassNotFoundException, SQLException {
        Test.consulta(101842);
    }
    
    public  Connection getConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            cn = DriverManager.getConnection("jdbc:mysql://192.168.1.15:3306/db_cda", "root", "50lt3l3c545");
        }catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException e) {
                    // ignore    
                }
            }
        }
        return cn;
    }

    public static void consulta(int idPrueba) throws ClassNotFoundException, SQLException {
        Connection conexion = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://192.168.1.15:3306/db_cda", "root", "50lt3l3c545");
            String statement = "SELECT v.Modelo,v.CAR,v.Tiempos_motor,hp.TESTSHEET, hp.Fecha_ingreso_vehiculo FROM pruebas as p INNER JOIN hoja_pruebas as hp ON p.hoja_pruebas_for = hp.TESTSHEET INNER JOIN vehiculos as v ON hp.Vehiculo_for = v.CAR WHERE p.Id_Pruebas = ?";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setInt(1, idPrueba);
            ResultSet rs = instruccion.executeQuery();
            if (rs.first()) {
                Date fecha1 = sdf.parse("2022-08-18");
                Date fecha2 = sdf.parse(rs.getDate("Fecha_ingreso_vehiculo").toString());

                System.out.println("------fecha 2 " + fecha2.toString() + "---------");
                System.out.println("------fecha 1 " + fecha1.toString() + "---------");
                System.out.println("------fecha sin formato " + rs.getTimestamp("Fecha_ingreso_vehiculo") + "---------");

                if (fecha1.compareTo(fecha2) < 0) {
                    System.out.println("fecha 1 es menor que fecha 2");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    // ignore    
                }
            }
        }

    }
}
