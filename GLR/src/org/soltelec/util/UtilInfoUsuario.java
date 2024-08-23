/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * Clase para consultar el id de usuario de la base de datos
 * @author GerenciaDesarrollo
 */
public class UtilInfoUsuario {
    
    
    public int obtenerIdUsuarioPorNombre(String nombreUsuario) throws SQLException, Exception{        
        int idUsuario = -1;
        Connection cn = null;
        try {            
            cn = UtilConexion.obtenerConexion();
            String consulta = "SELECT GEUSER FROM usuarios WHERE Nick_usuario = ?";
            PreparedStatement ps = cn.prepareStatement(consulta);
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            rs.next();
            idUsuario = rs.getInt("GEUSER");        
        }
        finally{            
            if(cn!=null){
                try {
                    cn.close();
                } catch (Exception e) {
                    Logger.getRootLogger().error(e);
                }                
            }            
        }   
        return idUsuario;
    }    
    
    public String obtenerSerialResolucion(String serial) throws SQLException, Exception{        
        String SerialResolucion ="";
        Connection cn = null;
        try {            
            cn = UtilConexion.obtenerConexion();
            String consulta = "SELECT serialresolucion FROM equipos WHERE serial = ?";
            PreparedStatement ps = cn.prepareStatement(consulta);
            ps.setString(1, serial);
            ResultSet rs = ps.executeQuery();
            rs.next();
            SerialResolucion = rs.getString("serialresolucion");        
        }
        finally{            
            if(cn!=null){
                try {
                    cn.close();
                } catch (Exception e) {
                    Logger.getRootLogger().error(e);
                }                
            }            
        }   
        return SerialResolucion;
    }    
}
