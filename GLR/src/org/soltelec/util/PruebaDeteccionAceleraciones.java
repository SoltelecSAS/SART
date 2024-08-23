/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 * Clase para probar Aceleraciones
 * @author Usuario
 */
public class PruebaDeteccionAceleraciones {

    public static void main(String args[]) throws InterruptedException{
        double temperaturaSimulada;
        ShiftRegister sr = new ShiftRegister(10,0);
        int contadorTemperatura = 0;
        while (contadorTemperatura < 100){
            temperaturaSimulada =  (100 - 100*Math.exp(0 - contadorTemperatura * 0.1));
           // temperaturaSimulada =  (100*Math.exp(0 - contadorTemperatura * 0.1));
            Thread.sleep(250);
            contadorTemperatura++;
            sr.ponerElemento(temperaturaSimulada);
            //sr.ponerElemento(120);
            System.out.println("Lista : " + sr );
            System.out.println("Es estable: " + sr.isStable());
            System.out.println("Esta Acelerando: " + sr.isAcelerando() );
        }
    }
}
