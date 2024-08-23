/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *
 * @author Usuario
 */
public enum TipoAutomovil {
    LIVIANO(1),PESADO(3),MOTO(4),CUATROXCUATRO(2),MOTOCARRO(5);

    private int numeroEnBd;

    private TipoAutomovil(int numeroEnBd) {
        this.numeroEnBd = numeroEnBd;
    }

}
