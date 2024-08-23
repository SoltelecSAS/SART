/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

/**
 *
 * @author User
 */
class RpmFueraRangoException extends Exception {

    public RpmFueraRangoException() {
    }

    @Override
    public String getMessage() {
        return "RPMS FUERA DE RANGO";
    }
    
    
}
