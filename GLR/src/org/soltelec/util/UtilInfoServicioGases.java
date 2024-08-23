/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.sql.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author GerenciaDesarrollo
 */
public class UtilInfoServicioGases {

    public static String obtenerResolucion(EntityManager eManager) throws SQLException, ClassNotFoundException {
        Query queryNombre = eManager.createNativeQuery("SELECT RESOLUCION FROM cda WHERE id_cda = 1 ");
        return  (String)queryNombre.getSingleResult();    
    }

    /**
     * Consecutivo de las pruebas de gases sin distinguir si son Diesel o Gasolina
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.lang.ClassNotFoundException 
     */
    public static int obtenerConsecutivoGases() throws SQLException, ClassNotFoundException {        
        String strConsulta = "SELECT count(*) FROM pruebas as p inner join hoja_pruebas as hp "
                + " on p.hoja_pruebas_for = hp.TESTSHEET inner join vehiculos as v "
                + "on v.CAR = hp.vehiculo_for where p.Tipo_prueba_for = 8 AND v.FUELTYPE = 1 ";
        int consecutivo = -1;
        
        try (Connection conn = Conex.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(strConsulta);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                consecutivo = rs.getInt(1);
            }
            
            return consecutivo; 
        }
    }    
    
    public static String obtenerNombreCDA(EntityManager eManager) throws SQLException, ClassNotFoundException{      
        Object[] arrayObjetos ;     
        Query queryNombre = eManager.createNativeQuery("SELECT nombre,NIT FROM cda ");
        arrayObjetos = (Object[]) queryNombre.getResultList().iterator().next();             
        return  ((String) arrayObjetos[0]).concat(" ").concat("NIT:  ").concat((String) arrayObjetos[1]);         
    }
    
    public static Object[] getAtributosCDA(EntityManager eManager) throws SQLException, ClassNotFoundException{      
        Object[] arrayObjetos ;     
        Query queryNombre = eManager.createNativeQuery("SELECT proveedor_sicov,usuario_sicov,password_sicov,url_servicio_sicov,url_servicio_encript,id_runt FROM cda ");
        arrayObjetos = (Object[]) queryNombre.getResultList().iterator().next();             
       // return   arrayObjetos[0].toString().concat(";").concat(arrayObjetos[1].toString()).concat(";").concat(arrayObjetos[2].toString()).concat(";").concat(arrayObjetos[3].toString()).concat(";").concat(arrayObjetos[4].toString());
        return   arrayObjetos;
    }
    
    public static boolean isEquipoBloqueado(String serial) throws ClassNotFoundException, ClassNotFoundException, SQLException {
        String strConsulta = "SELECT * FROM equipments WHERE equipments.EQUIPMENT = ?";
        Boolean estado;
        
        try (Connection conn = Conex.getConnection()) {
            PreparedStatement ps = conn.prepareCall(strConsulta);
            ps.setString(1, serial);
            ResultSet rs = ps.executeQuery();
            estado = rs.next();
            
            return estado;
        }
    }    
    
}
