/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * Clase para registrar defectos visuales de una inspeccion.
 *
 * @author GerenciaDesarrollo
 */
public class UtilDefectosVisuales {

    public static void registrarDefectosVisuales(Set<Integer> conjuntoDefectos, String urljdbc, long idPrueba, long idUsuario,String msgRechazo,Integer idDefecto) {
        Connection conexion = null;
        try {
            //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
            conexion = Conex.getConnection();
            conexion.setAutoCommit(false);
            String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
          //  for (Integer def : conjuntoDefectos) {
              //  instruccion.clearParameters();
                instruccion.setInt(1,idDefecto);
                instruccion.setLong(2, idPrueba);
                instruccion.executeUpdate();
            //}
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            String strFinalizarPrueba = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N', Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement psFinalizar = conexion.prepareStatement(strFinalizarPrueba);
            psFinalizar.setString(1, "Condiciones Anormales");
            System.out.println("Condiciones Anormales vistas "+msgRechazo);
            psFinalizar.setString(2, "Condiciones Anormales Encontradas .-"+msgRechazo);
            psFinalizar.setLong(3, idUsuario);           
            psFinalizar.setString(4, serialEquipo);
            psFinalizar.setLong(5, idPrueba);
            int n = psFinalizar.executeUpdate();
            conexion.commit();
            conexion.setAutoCommit(true);
        } catch (ClassNotFoundException clexc) {
            JOptionPane.showMessageDialog(null, " El driver de mysql no se encuentra");
            Logger.getRootLogger().error("El driver de mysql no se encuentra", clexc);
        } catch (SQLException sqlexc) {
            JOptionPane.showMessageDialog(null, "Error insertando defectos visuales diesel");
            Logger.getRootLogger().error("Error insertando defectos visuales diesel ", sqlexc);
        } finally {
            try {
                if (conexion != null) {
                    conexion.close();
                }
            } catch (Exception e) {    }
        }

    }//end of method registrarDefectosVisuales

}
