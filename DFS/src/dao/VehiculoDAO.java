/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static com.soltelec.modulopuc.persistencia.conexion.DBUtil.executeQuery;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  sout    
 * @author User
 */
public class VehiculoDAO {

    public static int  consultarNumeroEjes(String placa) throws SQLException {
        ResultSet data=null;
        try {
            data = executeQuery(String.format("select Numero_ejes from db_cda.vehiculos v where v.CARPLATE = '%s' ", placa));
            
            
        } catch (Exception e) {
            System.out.println(e);
        }
        return data.getInt(1);
    }
}
