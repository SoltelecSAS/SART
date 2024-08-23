/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.models.controllers;

import com.sun.rowset.CachedRowSetImpl;
import java.io.IOException;
import java.sql.SQLException;
import org.soltelec.models.entities.Pipeta;

/**
 *
 * @author Dany
 */
public class PipetaController {
    
    private Pipeta fillData(CachedRowSetImpl crs) throws SQLException {
        Pipeta pipeta = new Pipeta();        
        pipeta.setIdPipeta(crs.getInt(Pipeta.ID_PIPETA));
        pipeta.setTiempos(crs.getString(Pipeta.TIEMPOS));
        pipeta.setHc(crs.getInt(Pipeta.HC));
        pipeta.setCo(crs.getInt(Pipeta.CO));
        pipeta.setCo2(crs.getInt(Pipeta.CO2));
        
        return pipeta;
    }
    
    public Pipeta findPipetaByTiempos(String tiempos) throws SQLException, ClassNotFoundException, IOException {
        String sql = "SELECT * FROM pipetas WHERE tiempos LIKE '" + tiempos + "'";
        CachedRowSetImpl crs = Conexion.consultar(sql);
        Pipeta pipeta = null;
        
        if (crs.next()) {
            pipeta = fillData(crs);
        }
        
        return pipeta;
    }
   // public Simulacion finsimulacion 
    
    public void edit(Pipeta pipeta) throws SQLException, ClassNotFoundException, IOException {
        String sql = "UPDATE " + Pipeta.TABLA + " SET " + Pipeta.HC + " = '" + pipeta.getHc() + "', " +
                Pipeta.CO + " = '" + pipeta.getCo() + "', "+ Pipeta.CO2 + " = '" + pipeta.getCo2() + 
                "' WHERE tiempos LIKE '" + pipeta.getTiempos() + "'";
        System.out.println("SQL: " + sql);
        
        Conexion.ejecutarSentencia(sql);
    }
}
