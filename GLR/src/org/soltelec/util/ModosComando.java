/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *
 * @author Usuario
 */
public enum ModosComando {
    NORMAL((short)0),PETICION_DATOS((short)1);
    private short modoComando;
    ModosComando( short elModoComando){
        this.modoComando = elModoComando;
    }

}
