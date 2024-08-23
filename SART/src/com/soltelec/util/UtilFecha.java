/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.util;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Usuario
 */
public class UtilFecha {

    public static Date fechaHoy(EntityManager em){

        Query c =em.createNativeQuery("SELECT NOW()");
        return (Date)c.getSingleResult();

    }

}
