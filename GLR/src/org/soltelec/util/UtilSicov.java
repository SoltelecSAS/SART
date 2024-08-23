/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

//import com.soltelec.integrador.cliente.ClienteSicov;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
        
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
//import com.soltelec.estandar.EstadoEventosSicov;

/**
 *
 * @author GerenciaDesarrollo
 */
public class UtilSicov {

    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UtilSicov.class);

    public static String cargarDatosSicov(int idPrueba) {
        String serialEquipo1 = "";
        serialEquipo1 = buscarSerialEquipo(idPrueba);        
        logger.info("----------------Inicio idPrueba: " + idPrueba);
        return serialEquipo1;
    }
    
    public static String BusqIpAplPrueba(){
        String iP="";
        try {
            iP= UtilPropiedades.cargarPropiedad("IP", "equipos.properties");
        } catch (IOException ex) { 
            System.out.println("No se logro cargar la ip del equipo");
        }
        return iP;
    }
    public static String BusqSerialRegistrado(String findPropiedad,Integer valida_tipo)
    {
        System.out.println("----------------------------------------------------");
        System.out.println("----------------BusqSerialRegistrado----------------");
        System.out.println("----------------------------------------------------");
        
        String sE="0";        
        try {
            Connection conn = getConnection();      
            String str2 = "select e.serialresolucion from equipos e where e.tipo = ? and e.Pista = ?";
            System.out.println("Consulta serial : ");
            PreparedStatement preparedStatement = conn.prepareStatement(str2);
            System.out.println("Query : " + str2);
            System.out.println(" Parametro 1: " +findPropiedad);
            System.out.println(" Parametro 2: " +valida_tipo);
            preparedStatement.setString(1, findPropiedad);
            preparedStatement.setInt(2, valida_tipo);
            ResultSet data = preparedStatement.executeQuery();           
            while (data.next()) {
                sE = data.getString(1);               
                System.out.println(" COD EQUIPO DEL BD IS " + sE + " Ejecuta" +preparedStatement );
            }
        } catch (Exception ex) { 
            System.out.println("No se logro cargar el serial del equipo");
        }
        return sE;
    }
    
    public static String askDate(){        
        String sE = "";
        String str2 = "SELECT NOW()";
        try {
            Connection conn = getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(str2);

            ResultSet data = preparedStatement.executeQuery();
            while (data.next()) {
                sE = data.getString(1);
                System.out.println(" FECHAServidor " + sE);
            }
        } catch (Exception ex) {
            System.out.println("No se logro cargar la ip del equipo");
        }
        return sE;
     }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        return conexion;
    }

    private static String buscarSerialEquipo(Integer idPrueba) {
        try {
            logger.info("Inicio de envio de evento: " + idPrueba);
            String serialEquipo1 = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            logger.info("Evento enviado con el serial: " + serialEquipo1);
            return serialEquipo1;
        } catch (Exception e) {
            logger.info("Serial no encontrado" + e.getMessage());
            return "Serial no encontrado";
        }
    }
    

}
