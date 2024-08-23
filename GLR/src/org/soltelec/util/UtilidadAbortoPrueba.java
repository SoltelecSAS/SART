/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;

/**
 *
 * @author SOLTELEC
 */
public class UtilidadAbortoPrueba 
{
        /**
     * Permite tomar los parametros de conexion del archivo properties 
     * y crear la conexion con la db
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException 
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException 
    {
        Connection conexion = Conex.getConnection();
        return conexion;
    }

    /**
     * Metodo permite ihacer un update a la db de aborto de prueba por caida 
     * de validacion del minimo  o maximo
     */
    public void insertarAbortoPrueba(long idUsuario,long idPrueba)
    {
        System.out.println("-----------------------------------------");
        System.out.println("-------- insertarAbortoPrueba -----------");
        System.out.println("-----------------------------------------");
        String query = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,observaciones=? , usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?;";
        try 
        {
            System.out.println("query :" + query);
            Connection conexion = Conex.getConnection();
            conexion.setAutoCommit(false);
            PreparedStatement instruccion =conexion.prepareStatement(query);
            instruccion.setString(1, "Ajuste del Valor Minimo de la Escala Incorrecto");
            instruccion.setString(2, "Ajuste del Valor Minimo de la Escala Incorrecto");
            instruccion.setLong(3, idUsuario);
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            instruccion.setTimestamp(4, new java.sql.Timestamp(c.getTimeInMillis()));
            instruccion.setString(5, buscarSerialEquipo(idPrueba));
            instruccion.setLong(6, idPrueba);
            instruccion.executeUpdate();
            conexion.commit();
            conexion.setAutoCommit(true);
            conexion.close();
            System.out.println("Se inserto el aborto exitoso");
        } catch (ClassNotFoundException e) 
        {
            System.out.println("Error en el metodo : insertarAbortoPrueba()" + e);
            System.out.println("Error " + e.getMessage() + e.getException() + e.getCause());
            java.util.logging.Logger.getLogger(UtilidadAbortoPrueba.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) 
        {
            System.out.println("Error en el metodo : insertarAbortoPrueba()" + ex);
            System.out.println("Error " + ex.getMessage() + ex.getSQLState() + ex.getCause());
            java.util.logging.Logger.getLogger(UtilidadAbortoPrueba.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Fin insertarAbortoPrueba");
    }
    
    /**
     * 
     * @return 
     */
    private String buscarSerialEquipo(Long idPrueba) 
    {
        System.out.println("-----------------------------------------");
        System.out.println("-------- insertarAbortoPrueba -----------");
        System.out.println("-----------------------------------------");
        
        String serialEquipo = "";
        try
        {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            System.out.println(" idPrueba :" + idPrueba);
            System.out.println(" Serial Equipo encontrado : " + serialEquipo);
        } catch (Exception e) {
            serialEquipo = "Serial no Encontrado ";
        }
        return serialEquipo;
    }
    
    
    public static void main(String[] args) 
    {
        UtilidadAbortoPrueba llamar= new UtilidadAbortoPrueba();
        long idUsuario=1;
        long idPrueba=2859;
        
        llamar.insertarAbortoPrueba(idUsuario,idPrueba);
    }
}
