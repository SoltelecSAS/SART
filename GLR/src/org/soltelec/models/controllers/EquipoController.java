/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.models.controllers;

import com.soltelec.modulopuc.utilidades.Mensajes;
import com.sun.rowset.CachedRowSetImpl;
import static conexion.PersistenceController.getEntityManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.soltelec.models.entities.Equipo;
import javax.swing.JOptionPane;

/**
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class EquipoController {
    
    private Equipo flllData(CachedRowSetImpl crs) throws SQLException {
        Equipo equipo = new Equipo();
        
        
        equipo.setIdEquipo(crs.getInt(Equipo.ID_EQUIPO));
        equipo.setMarca(crs.getString(Equipo.MARCA));
        equipo.setNumAnalizador(crs.getString(Equipo.NUM_ANALIZADOR));
        equipo.setPef(crs.getString(Equipo.PEF));
        equipo.setSerial(crs.getString(Equipo.SERIAL));
        equipo.setSerialresolucion(crs.getString(Equipo.SERIAL_RESOLUCION));
        equipo.setResolucionambiental(crs.getString(Equipo.RESOLUCION_AMBIENTAL));
        equipo.setSerialbench(crs.getString(Equipo.NUM_SERIAL_BENCH));
        
        
        
        return equipo;
    }
    private com.mysql.jdbc.Connection conexion;
    public Integer findIdEquipoBySerial(String serial, Connection cn) throws SQLException, ClassNotFoundException, IOException 
    {
        String sql = "SELECT " + Equipo.ID_EQUIPO + " FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        System.out.println("SQL IS  "+sql);
         System.out.println("OBJ CONEXION ES "+cn);
        CachedRowSetImpl crs = Conexion.consultar(sql,cn);        
        Integer idEquipo = -1;        
        try {
            if (crs.next()) {
                idEquipo = crs.getInt(Equipo.ID_EQUIPO);
            } else {
                System.out.println("estoy en findId-------------------");
                
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }
        
        return idEquipo;
    }
    
    
     public String findConfigEquip(Long serial, String funcion) throws SQLException, ClassNotFoundException, IOException {
         String sql =null;
         String response="0";
         if(funcion.equalsIgnoreCase("opacimetro")){
              sql = "SELECT config_1,config_2 FROM config_calibracion   WHERE serial= " + serial + "";
         }else{
             sql = "SELECT  * FROM config_calibracion   WHERE serial=" + serial + "";
         }         
         System.out.println("SQL IS  "+sql);
         
         // Connection conn = conectar();
         
         /* PreparedStatement instruccion = conn.prepareStatement(sql);
          instruccion.setLong(1, serial);
            rs = instruccion.executeQuery();*/
         
         
        CachedRowSetImpl crs = Conexion.consultar(sql);          
        try {
            if (crs.next()) {                
                if(funcion.equalsIgnoreCase("opacimetro")){
                   response =  response.concat(String.valueOf(crs.getDouble("config_1")));
                   response =  response.concat(";").concat(String.valueOf(crs.getDouble("config_2")));                   
                }else{
                    response =  response.concat(String.valueOf(crs.getDouble("config_1")));
                   response =  response.concat(";").concat(String.valueOf(crs.getDouble("config_2"))); 
                }                
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }        
        return response;
    }
    
    public String findIdEqpBySerial(String serial) throws SQLException, ClassNotFoundException, IOException {
       // serial="61890";
        String sql = "SELECT " + Equipo.ID_EQUIPO + ",marca FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        CachedRowSetImpl crs = Conexion.consultar(sql);        
        Integer idEquipo = -1;  
        String marca;
        try {
            if (crs.next()) {        
                idEquipo = crs.getInt(Equipo.ID_EQUIPO);
                marca =crs.getString("marca");
                System.out.println("id  equipo es:"+ idEquipo);
                System.out.println("marca es:"+ marca);
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }        
        return idEquipo.toString().concat("&").concat(marca);
    }
    public String naturalezaBco(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT  Cod_Interno FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        System.out.println(" el sql de find is "+sql);
        CachedRowSetImpl crs = Conexion.consultar(sql);        
        String tiempoAnalizador="";            
        try {
            if (crs.next()) {        
                tiempoAnalizador =  crs.getString("Cod_Interno") ; 
                  System.out.println(" back codigo interno "+tiempoAnalizador);
             }       
        } finally {
            crs.close();
        }        
       return tiempoAnalizador;
    }
     public String lastCalib2Pto(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT bm_co,bm_co2,bm_hc,alta_co,alta_co2,alta_hc,banco_bm_co,banco_bm_co2,banco_bm_hc,banco_alta_co,banco_alta_co2,banco_alta_hc  FROM " + Equipo.TABLA + ",calibracion_dos_puntos,calibraciones WHERE  equipos.id_equipo = calibraciones.id_equipo and  calibracion_dos_puntos.CALIBRATION = calibraciones.CALIBRATION and  " + Equipo.SERIAL + " = '" + serial + "' ORDER BY calibracion_dos_puntos.CALIBRATION DESC LIMIT 1" ;
        CachedRowSetImpl crs = Conexion.consultar(sql);        
        String tiempoAnalizador="";           
        try {
            if (crs.next()) {        
                tiempoAnalizador =  String.valueOf(crs.getDouble("bm_co")).concat(";").concat(String.valueOf(crs.getDouble("bm_co2"))).concat(";").concat(String.valueOf(crs.getDouble("bm_hc"))).concat(";").concat(String.valueOf(crs.getDouble("alta_co"))).concat(";").concat(String.valueOf(crs.getDouble("alta_co2"))).concat(";").concat(String.valueOf(crs.getDouble("alta_hc"))).concat(";").concat(String.valueOf(crs.getDouble("banco_bm_co"))).concat(";").concat(String.valueOf(crs.getDouble("banco_bm_co2"))).concat(";").concat(String.valueOf(crs.getDouble("banco_bm_hc"))).concat(";").concat(String.valueOf(crs.getDouble("banco_alta_co"))).concat(";").concat(String.valueOf(crs.getDouble("banco_alta_co2"))).concat(";").concat(String.valueOf(crs.getDouble("banco_alta_hc"))) ;                
             }
        } finally {
            crs.close();
        }        
       return tiempoAnalizador;
    }
    public boolean validadorAnalizador(String serial,int tMotor) throws SQLException, ClassNotFoundException, IOException {      
        String sql = "SELECT  Cod_Interno FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
         
        CachedRowSetImpl crs = Conexion.consultar(sql);        
        int tiempoAnalizador=0;  
        String marca;        
        try {
            if (crs.next()) {        
                tiempoAnalizador =  Integer.parseInt(crs.getString("Cod_Interno").substring(0, 1)) ; 
                if(tiempoAnalizador == tMotor){
                    return true;
                }
             }       
        } finally {
            crs.close();
        }        
       return false;
    }
    
    
    public Integer findIdEquipoBySerial(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT " + Equipo.ID_EQUIPO + " FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        System.out.println("id  equipo es:"+ sql);        
        CachedRowSetImpl crs = Conexion.consultar(sql);           
        Integer idEquipo = -1;  
        String marca;
        try {
            if (crs.next()) {        
                idEquipo = crs.getInt(Equipo.ID_EQUIPO);              
                System.out.println("id  equipo es:"+ idEquipo);               
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }        
        return idEquipo;
    }
     public String findMarcaBySerial(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT " + Equipo.MARCA + " FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        System.out.println("el SQL DEL MARCA CONTROLER :"+sql);
        CachedRowSetImpl crs = Conexion.consultar(sql);
        String marcaBanco = "--";        
        try {
            if (crs.next()) {
                System.out.println("ENTRE EN CONDICIONAL:");
                marcaBanco = crs.getString(Equipo.MARCA);
                 System.out.println("MARCA DEL EQUIPO:"+marcaBanco);
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }
        
        return marcaBanco;
    }
     public String findPEFBySerial(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT " + Equipo.PEF + " FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        CachedRowSetImpl crs = Conexion.consultar(sql);
        String pef = "---";
        
        try {
            if (crs.next()) {
                pef = crs.getString(Equipo.PEF);
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }
        
        return pef;
    }
    
    public Equipo findEquipoBySerial(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL + " = '" + serial + "'";
        CachedRowSetImpl crs = Conexion.consultar(sql);
        Equipo equipo = null;
        
        try {
            if (crs.next()) {
                equipo = flllData(crs);
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }
        
        return equipo;
    }
    
    public Equipo findEquipoById(String serial) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM " + Equipo.TABLA + " WHERE " + Equipo.ID_EQUIPO + " = '" + serial + "'";
        CachedRowSetImpl crs = Conexion.consultar(sql);
        
        Equipo equipo = null;
        
        try {
            if (crs.next()) {
                equipo = flllData(crs);
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }
        System.out.println("equipo serial bench: " + equipo.getSerialbench());
        return equipo;
    }
    
    public Equipo findEquipoBySerialResolucion(String serial) throws Exception {
        String sql = "SELECT * FROM " + Equipo.TABLA + " WHERE " + Equipo.SERIAL+ " = '" + serial + "'";
        CachedRowSetImpl crs = Conexion.consultar(sql);
        Equipo equipo = null;
        
        try {
            if (crs.next()) {
                equipo = flllData(crs);
            } else {
                throw new SQLException("No Existe Equipo con Serial: " + serial);
            }
        } finally {
            crs.close();
        }
        
        return equipo;
    }
        public void ActFecha(Long idPrueba) throws Exception {
          try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = (com.mysql.jdbc.Connection) DriverManager.getConnection(com.soltelec.modulopuc.configuracion.modelo.Conexion.getUrl(),com.soltelec.modulopuc.configuracion.modelo.Conexion.getUsuario() ,com.soltelec.modulopuc.configuracion.modelo.Conexion.getContraseña());
        } catch (ClassNotFoundException | SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }

        try {
            conexion.setAutoCommit(false);
            String statement = "UPDATE pruebas p  SET p.Fecha_prueba = NOW() WHERE p.Id_Pruebas=? ";
            java.sql.PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setLong(1,idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            conexion.commit();
            conexion.setAutoCommit(true);
            conexion.close();
            //System.out.println("Datos enviados");
        } catch (SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        
      
    }
        public void ActFechaFinal(Long idPrueba) throws Exception {
          try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = (com.mysql.jdbc.Connection) DriverManager.getConnection(com.soltelec.modulopuc.configuracion.modelo.Conexion.getUrl(),com.soltelec.modulopuc.configuracion.modelo.Conexion.getUsuario() ,com.soltelec.modulopuc.configuracion.modelo.Conexion.getContraseña());
        } catch (ClassNotFoundException | SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }

        try {
            conexion.setAutoCommit(false);
            String statement = "UPDATE pruebas p  SET p.Fecha_final = NOW() WHERE p.Id_Pruebas=? ";
            java.sql.PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setLong(1,idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            conexion.commit();
            conexion.setAutoCommit(true);
            conexion.close();
            //System.out.println("Datos enviados");
        } catch (SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        
      
    }
    

     
    
}
