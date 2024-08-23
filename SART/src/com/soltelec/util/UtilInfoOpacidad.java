/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.soltelec.util.Conex;

/**
 *
 * @author User
 */
public class UtilInfoOpacidad {

    public String obtenerInfoPruebas(String serialResolucion) throws ClassNotFoundException, SQLException {
        StringBuilder sbInfoPruebas = new StringBuilder();
        String numeroPruebasAprobadas = "";
        String numeroPruebasReprobadas = "";
        String numeroPruebasCanceladas = "";

        String consultaInfoPruebas = "SELECT count(p.Id_Pruebas) FROM hoja_pruebas AS hp "
                + "INNER JOIN vehiculos AS v ON hp.Vehiculo_for = v.CAR "
                + "INNER JOIN pruebas  AS p ON p.hoja_pruebas_for = hp.TESTSHEET "
                + "WHERE v.FUELTYPE=3 AND p.Tipo_prueba_for=8 AND "
                + "p.Aprobada = ? AND p.Abortada = ? and p.serialEquipo = ?";

        try (Connection conn = Conex.getConnection()) {
            PreparedStatement psInfoPruebas = conn.prepareStatement(consultaInfoPruebas);
            //Consulta de pruebas aprobadas:
            psInfoPruebas.setString(1, "Y");
            psInfoPruebas.setString(2, "N");
            psInfoPruebas.setString(3, serialResolucion);

            ResultSet rsInfoPruebas = psInfoPruebas.executeQuery();
            if (rsInfoPruebas.next()) {
                numeroPruebasAprobadas = rsInfoPruebas.getString(1);
            }
            //Consulta de pruebas reprobadas
            psInfoPruebas.setString(1, "N");
            psInfoPruebas.setString(2, "N");
            psInfoPruebas.setString(3, serialResolucion);
            rsInfoPruebas = psInfoPruebas.executeQuery();
            if (rsInfoPruebas.next()) {
                numeroPruebasReprobadas = rsInfoPruebas.getString(1);
            }
            //Consulta de pruebas Canceladas(abortadas)
            psInfoPruebas.setString(1, "N");
            psInfoPruebas.setString(2, "Y");
            psInfoPruebas.setString(3, serialResolucion);
            rsInfoPruebas = psInfoPruebas.executeQuery();
            if (rsInfoPruebas.next()) {
                numeroPruebasCanceladas = rsInfoPruebas.getString(1);
            }

            //Armar el resultado
            sbInfoPruebas.append(String.format("%s\t%s\n", "Numero de pruebas aprobadas: ", numeroPruebasAprobadas));
            sbInfoPruebas.append(String.format("%s\t%s\n", "Numero de pruebas reprobadas: ", numeroPruebasReprobadas));
            sbInfoPruebas.append(String.format("%s\t%s\n", "Numero de pruebas abortadas: ", numeroPruebasCanceladas));

            return sbInfoPruebas.toString();
        }
    }//end of method infoPruebas

}
