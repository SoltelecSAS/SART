/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import org.soltelec.pruebasgases.ListenerCancelacionGases;

/**
 *
 * @author User
 */
public class TestCancelacion {
    
    public static void main (String args[]){        
        ListenerCancelacionGases listenerCancelacion = new ListenerCancelacionGases(null,16,null,null,null,7L);
        listenerCancelacion.actionPerformed(null);
    }
    
}
