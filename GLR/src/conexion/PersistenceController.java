/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexion;

import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import java.io.FileInputStream;
import java.io.IOException;
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

    protected static EntityManager em;

    public static EntityManager getEntityManager() {      
        if (em== null) {
            HashMap map = new HashMap();
            Conexion cn = Conexion.getInstance();
            map.put("javax.persistence.jdbc.url", Conexion.getUrl());
            map.put("javax.persistence.jdbc.user", Conexion.getUsuario());
            map.put("javax.persistence.jdbc.password", Conexion.getContraseña());
            em = Persistence.createEntityManagerFactory("ProyectoCDAPU", map).createEntityManager();
        }
        System.out.println(Conexion.getUrl());
        return em;
    }
}
