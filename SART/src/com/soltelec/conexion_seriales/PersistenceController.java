/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.conexion_seriales;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.soltelec.util.CMensajes;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PersistenceController {

    static EntityManager  em = null;

    public static EntityManager getEntityManager() {
        if (em == null) {
            try {
                Conexion.setConexionFromFile(); //establece conexion desde el archivo Conexion.soltelec
                HashMap<String, String> map = new HashMap<>();
                map.put("javax.persistence.jdbc.url", Conexion.getUrl());
                map.put("javax.persistence.jdbc.user", Conexion.getUsuario());
                map.put("javax.persistence.jdbc.password", Conexion.getContrasena());

                em = Persistence.createEntityManagerFactory("ServidorPU", map).createEntityManager();
            } catch (Exception ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
                CMensajes.mensajeError("No se pudo establecer conexion con la base de datos, revise el archivo Conexion.soltelec o contactese con soporte");
            }
        }
        return em;
    }
}
