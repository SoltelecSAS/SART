/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *
 * @author Usuario
 */
public enum ComandosGases {
    
    PROCESS_STATUS((short)0);
    private short numeroComando;
    
    ComandosGases(short elNumeroDeComando){
        this.numeroComando = elNumeroDeComando;
    }
}
