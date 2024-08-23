/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

/**
 *
 * @author User
 */
class TempFueraRangoException extends Exception {

    public TempFueraRangoException() {
    }

    @Override
    public String getMessage() {
        return "TEMPERATURA FUERA DE RANGO";
    }
    
    
}
