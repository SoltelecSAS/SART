/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.capelec.cap2500;

/**
 *
 * @author lberna
 */
public enum Coincidencia {

    SIN_COINCIDENCIAS(0),
    DOS_COINCIDENCIAS(1),
    TRES_COINCIDENCIAS(2),
    CUATRO_COINCIDENCIAS(3),
    CINCO_COINCIDENCIAS(4);

    private int valor = 0;

    Coincidencia(int d) {
        this.valor = d;
    }

    public int getValor() {
        return valor;
    }
}
