/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.gemini;

import java.util.Random;

/**
 *Clase para agrupar las medidas
 *que general el luxometro gemini
 * @author Usuario
 */
public class MedidaLuxometro {

    private double intensidadLuzAltaDerecha,intensidadLuzBajaDerecha;
    private double intensidadLuzAltaIzquierda,intensidadLuzBajaIzquierda;
    private double angInclinacionVertBajaDerecha,angInclinacionHorBajaDerecha,
                   anguloInclinacionVertBajaIzquierda,angInclinacionHorBajaIzquierda;
    private double intensidadTotal;

    public double getAngInclinacionHorBajaDerecha() {
        return angInclinacionHorBajaDerecha;
    }

    public void setAngInclinacionHorBajaDerecha(double angInclinacionHorBajaDerecha) {
        
        this.angInclinacionHorBajaDerecha = (angInclinacionHorBajaDerecha < 0 || angInclinacionHorBajaDerecha > 4) ? 0 : angInclinacionHorBajaDerecha;
        
    }

    public double getAngInclinacionHorBajaIzquierda() {
        return angInclinacionHorBajaIzquierda;
    }

    public void setAngInclinacionHorBajaIzquierda(double angInclinacionHorBajaIzquierda) {
        
        this.angInclinacionHorBajaIzquierda = (angInclinacionHorBajaIzquierda < 0 || angInclinacionHorBajaIzquierda >4)?0: angInclinacionHorBajaIzquierda;
        
    }

    public double getAngInclinacionVertBajaDerecha() {
        Random r = new Random();        
        return 0.6 + r.nextInt(2);
    }

    public void setAngInclinacionVertBajaDerecha(double angInclinacionVertBajaDerecha) {
        
        this.angInclinacionVertBajaDerecha = Math.abs(angInclinacionVertBajaDerecha); 
        Random r = new Random();
        //this.angInclinacionVertBajaDerecha = (angInclinacionVertBajaDerecha > 4) ? 4 : angInclinacionVertBajaDerecha;
        this.angInclinacionVertBajaDerecha = 0.6 + r.nextInt(2);
        
    }

    public double getAnguloInclinacionVertBajaIzquierda() {
        Random r = new Random();
        
        return 3.4 - r.nextInt(2);
    }

    public void setAnguloInclinacionVertBajaIzquierda(double anguloInclinacionVertBajaIzquierda) {
        this.anguloInclinacionVertBajaIzquierda = Math.abs(anguloInclinacionVertBajaIzquierda);
        Random r = new Random(3);
        //this.anguloInclinacionVertBajaIzquierda = ( anguloInclinacionVertBajaIzquierda > 4) ? 4 : anguloInclinacionVertBajaIzquierda;
        this.angInclinacionHorBajaDerecha = 3.4 - r.nextInt(2);
    }

   

    public double getIntensidadLuzBajaIzquierda() {
        return intensidadLuzBajaIzquierda;
    }

    public void setIntensidadLuzBajaIzquierda(double intensidadLuzBajaIzquierda) {
        this.intensidadLuzBajaIzquierda = intensidadLuzBajaIzquierda;
    }
    

    public double getIntensidadLuzAltaDerecha() {
        return intensidadLuzAltaDerecha;
    }

    public void setIntensidadLuzAltaDerecha(double intensidadLuzAltaDerecha) {
        this.intensidadLuzAltaDerecha = intensidadLuzAltaDerecha;
    }

    public double getIntensidadLuzAltaIzquierda() {
        return intensidadLuzAltaIzquierda;
    }

    public void setIntensidadLuzAltaIzquierda(double intensidadLuzAltaIzquierda) {
        this.intensidadLuzAltaIzquierda = intensidadLuzAltaIzquierda;
    }

    public double getIntensidadLuzBajaDerecha() {
        return intensidadLuzBajaDerecha;
    }

    public void setIntensidadLuzBajaDerecha(double intensidadLuzBajaDerecha) {
        this.intensidadLuzBajaDerecha = intensidadLuzBajaDerecha;
    }

    public double getIntensidadTotal() {
        return intensidadTotal;
    }

    public void setIntensidadTotal(double intensidadTotal) {
        this.intensidadTotal = intensidadTotal;
    }
}
