/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class UtilInfoVehiculo {
    
    public static InfoVehiculo traerInfoVehiculo(long idPrueba) throws FileNotFoundException, IOException {
        
        RegistrarMedidas regMedidas = new RegistrarMedidas();
        Connection cn = null;
        InfoVehiculo infoVehiculo = null;
        try{
            
           cn = regMedidas.getConnection(); 
           infoVehiculo = regMedidas.cargarInfoVehiculo((int)idPrueba,cn );
        
        }catch(ClassNotFoundException | SQLException exc){
            
            if(cn!=null){
                
                try {
                    cn.close();
                } catch (SQLException e) {
                }
            }
            
        }
        return infoVehiculo;
    }
    
}
