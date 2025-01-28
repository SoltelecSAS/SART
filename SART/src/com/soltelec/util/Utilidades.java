/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.soltelec.conexion_seriales.Conexion;

/**
 *
 * @author 
 */
public class Utilidades {
    private static char patron='M';

    public static String cifra(String cadena, char patron) {
        char[] secStr = cadena.toCharArray();
        String strEncript = "";
        for (int n = 0; n < secStr.length; n++) {
            char c = (char) (secStr[n] ^ patron);
            strEncript = strEncript + c;
        }
        return strEncript;
    }
    
    public static String deCifrar(String cadena) {
        char[] secStr = cadena.toCharArray();
        String strEncript = "";
        for (int n = 0; n < secStr.length; n++) {
            char c = (char) (secStr[n] ^ patron);
            strEncript = strEncript + c;
        }
        return strEncript;
    }    
     public static void servicio() {
         
     }

     /* public static String[] obtenerUltimaPruebaNoAutorizada(int testSheet, int testType) {
        Conexion.setConexionFromFile();
        Connection connection = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;
        
        try {
            // Establecer conexión
            String url = Conexion.getUrl(); 
            String user = Conexion.getUsuario(); 
            String password = Conexion.getContrasena();
            connection = DriverManager.getConnection(url, user, password);

            // Consulta SQL
            String selectSql = "SELECT p.Id_Pruebas, p.abortada, p.usuario_for, p.finalizada\r\n" + //
                                    "FROM pruebas p\r\n" + //
                                    "WHERE p.autorizada = 'N'\r\n" + //
                                    "AND p.hoja_pruebas_for = ?\r\n" + //
                                    "AND p.Tipo_prueba_for = ?\r\n" + //
                                    "ORDER BY p.Fecha_prueba DESC\r\n" + //
                                    "LIMIT 1;";
            
            selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, testSheet);
            selectStatement.setInt(2, testType);
            
            resultSet = selectStatement.executeQuery();

            // Retornar el resultado si existe
            if (resultSet.next()) {
                return new String[]{
                    resultSet.getString("idPruebas"),
                    resultSet.getString("abortada"),
                    resultSet.getString("geuser"),
                    resultSet.getString("finalizada")
                };
            } else {
                System.out.println("No se encontró ninguna prueba no autorizada con los criterios dados.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener la prueba no autorizada");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (selectStatement != null) selectStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    } */

}
