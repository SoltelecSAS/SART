/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco.verificacion;

import java.util.logging.Logger;

/**
 *
 * @author User
 */
public enum TipoMuestra {
    
    BAJA_4T( 300,1,6) ,
    ALTA_4T(1200,4,12),
    BAJA_2T(300,1,6),
    ALTA_2T(3200,8,12);
    
    private int valorHC;
    private double valorCO;
    private double valorCO2;

    private TipoMuestra(int valorHC, double valorCO, double valorCO2) {
        this.valorHC = valorHC;
        this.valorCO = valorCO;
        this.valorCO2 = valorCO2;
    }

    public int getValorHC() {
        return valorHC;
    }

    public double getValorCO() {
        return valorCO;
    }

    public double getValorCO2() {
        return valorCO2;
    }
    
       
}
