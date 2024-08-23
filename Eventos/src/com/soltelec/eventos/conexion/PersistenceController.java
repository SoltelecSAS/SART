/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.eventos.conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PersistenceController {

   static EntityManager  em = null;

    public static EntityManager getEntityManager() {
//        Properties properties = new Properties();
        if (em == null) {
            try {
                Conexion conexion = Conexion.getInstance();//          
                HashMap map = new HashMap();
                map.put("javax.persistence.jdbc.url", Conexion.getUrl());
                map.put("javax.persistence.jdbc.user", Conexion.getUsuario());
                map.put("javax.persistence.jdbc.password", Conexion.getContrasena());
                em = Persistence.createEntityManagerFactory("ProyectoPU", map).createEntityManager();
            } catch (Exception ex) {
                Logger.getLogger(PersistenceController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return em;
    }

    /* String resourceName = "conexion.properties"; // could also be a constant
     ClassLoader loader = Thread.currentThread().getContextClassLoader();
     Properties properties = new Properties();
     try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
     properties.load(resourceStream);
     }*/
}
