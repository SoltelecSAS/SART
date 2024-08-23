/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

/**
 *
 * @author Usuario
 */
public class ArregloOpacidadCiclo {

    private byte[] arregloOpacidad;
    private int numCiclo;

    public ArregloOpacidadCiclo(byte[] arregloOpacidad, int numCiclo) {
        this.arregloOpacidad = arregloOpacidad;
        this.numCiclo = numCiclo;
    }

    public byte[] getArregloOpacidad() {
        return arregloOpacidad;
    }

    public void setArregloOpacidad(byte[] arregloOpacidad) {
        this.arregloOpacidad = arregloOpacidad;
    }

    public int getNumCiclo() {
        return numCiclo;
    }

    public void setNumCiclo(int numCiclo) {
        this.numCiclo = numCiclo;
    }
    
}
