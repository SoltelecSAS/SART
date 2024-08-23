/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.soltelec.util.Conex;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.UtilConexion;
import org.soltelec.util.UtilPropiedades;

/**
 *
 * @author soltelec
 */





public class UtilRegistrarVerificacion {
    
    public static final String APROBADA = "Prueba Dos Puntos Aprobada";
    public static final String REPROBADA = "Prueba Dos Puntos Reprobada";
    
    public static void registrar(MedicionGases medicionBaja, MedicionGases medicionAlta,String pef,String serial,String marca,boolean aprobada,int idUsuario){
        Connection cn = null;
        DecimalFormat df = new DecimalFormat("#.##");
        try{
            
           String url = UtilPropiedades.cargarPropiedad("urljdbc", "propiedades.properties");
           cn = getConnection(url);
           
           String sql = "INSERT INTO calibraciones(TESTTYPE,GEUSER,VALOR1,"
                   + "VALOR2,VALOR3,VALOR4,VALOR5,VALOR6"
                   + ",SERIALEQ,BRAND,PEFVALUE,DESCRIPTION) "
                   + " VALUES ('8',?,?,?,?,?,?,?,?,?,?,?)";
           
           PreparedStatement ps = cn.prepareStatement(sql);
           //poner los valores de la muestra baja.
           ps.setInt(1, idUsuario);
           ps.setInt(2,medicionBaja.getValorHC());
           ps.setString(3, df.format(medicionBaja.getValorCO()*0.01) );
           ps.setString(4, df.format(medicionBaja.getValorCO2()*0.1));
           //poner los valores de la muestra alta
           ps.setInt(5,medicionAlta.getValorHC());
           ps.setString(6,df.format(medicionAlta.getValorCO()*0.01));
           ps.setString(7,df.format(medicionAlta.getValorCO2()*0.1));
           //serial
           ps.setString(8, serial);
           //marca
           ps.setString(9,marca.substring(0, 2));//error en la base de datos
           //pef
           ps.setString(10,pef);
           //aprobada o reprobada
           if(aprobada){
             ps.setString(11, APROBADA);   
           } else {
             ps.setString(11, REPROBADA);  
           }
            
           ps.executeUpdate();
           
           
        } catch(Exception exc){
            
            JOptionPane.showMessageDialog(null, "No se puede registrar la verificacion en la base de datos");
            
            Logger.getRootLogger().error("No se puede registrar la verificacion en la base de datos", exc);
            exc.printStackTrace();
            
        } finally{
            try {
                if(cn!=null)
                cn.close();
            } catch (Exception e) {
            }
        }
        
        
        
    }
    
    public static Connection getConnection(String url) throws ClassNotFoundException, SQLException{
        
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        return conexion;
    }

    public static void bloquearEquipo(String serial) {
        
        Connection cn = null;
        
        try {
            cn = UtilConexion.obtenerConexion();
            String consulta = "INSERT INTO equipments(EQUIPMENT) VALUES (?)";
            PreparedStatement ps = cn.prepareStatement(consulta);
            ps.setString(1, serial);
            ps.executeUpdate();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        finally{
            if(cn!=null) {
                try {
                cn.close();
            } catch (SQLException ex) {
                //java.util.logging.Logger.getLogger(UtilRegistrarVerificacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
    }
   
        public static void desBloquearEquipo(String serial){
            
            Connection cn = null;
        
        try {
            cn = UtilConexion.obtenerConexion();
            String consulta = "DELETE FROM equipments WHERE EQUIPMENT = ?";
            PreparedStatement ps = cn.prepareStatement(consulta);
            ps.setString(1, serial);
            ps.executeUpdate();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(cn!=null) {
                try {
                cn.close();
            } catch (SQLException ex) {
                //java.util.logging.Logger.getLogger(UtilRegistrarVerificacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }//end finally    
}
        
        
        
    
}
