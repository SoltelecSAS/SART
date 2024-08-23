/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *Clase que representa una medida general
 * de cualquier tipo
 * @author Usuario
 */
public class MedidaGeneral {

    private int tipoMedida;
    private double valorMedida;

    public MedidaGeneral() {
    }

    public MedidaGeneral(int tipoMedida,double valorMedida){
        this.tipoMedida = tipoMedida;
        this.valorMedida = valorMedida;
    }

    public int getTipoMedida() {
        return tipoMedida;
    }

    public void setTipoMedida(int tipoMedida) {
        this.tipoMedida = tipoMedida;
    }

    public double getValorMedida() {
        return valorMedida;
    }

    public void setValorMedida(double valorMedida) {
        this.valorMedida = valorMedida;
    }

    
}
