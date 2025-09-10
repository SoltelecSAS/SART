/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.dao.conexion;

//import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import com.soltelec.modulopuc.configuracion.modelo.Conexion;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PersistenceController {
    protected static EntityManager em;
    public static EntityManager getEntityManager() {
        if (Conexion.getBaseDatos() == null) {
            Conexion.getInstance();
            HashMap map = new HashMap();
            map.put("javax.persistence.jdbc.url", Conexion.getUrl());
            map.put("javax.persistence.jdbc.user", Conexion.getUsuario());
            map.put("javax.persistence.jdbc.password", Conexion.getContraseña());
            em = Persistence.createEntityManagerFactory("ProyectoCDAPU", map).createEntityManager();
        }
        return em;
    }
}
