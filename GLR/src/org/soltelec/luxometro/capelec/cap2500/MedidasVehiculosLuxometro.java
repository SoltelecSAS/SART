/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.capelec.cap2500;

public class MedidasVehiculosLuxometro {

    private double intensidadLuzBajaDerecha; 
    private double inclinacionLuzBajaDerecha;
    private double intensidadLuzBajaIzquierda;
    private double inclinacionLuzBajaIzquierda;
    
    private double intensidadLuzAltaIzquierda;
    private double intensidadLuzAltaDerecha;
    private double exploradoraDerecha;
    private double exploradoraIzquierda;
    
    private double intensidadLuzBaja3; 
    private double inclinacionLuzBaja3;
    private double intensidadLuzAlta3;

    /*
    
    
    */

    /**
     * @return the inclinacionLuzBajaDerecha
     */
    public double getInclinacionLuzBajaDerecha() {
        return inclinacionLuzBajaDerecha;
    }

    /**
     * @param inclinacionLuzBajaDerecha the inclinacionLuzBajaDerecha to set
     */
    public void setInclinacionLuzBajaDerecha(double inclinacionLuzBajaDerecha) {
        this.inclinacionLuzBajaDerecha = inclinacionLuzBajaDerecha;
    }

    /**
     * @return the doubleencidadLuzBajaDerecha
     */
    public double getIntensidadLuzBajaDerecha() {
        return intensidadLuzBajaDerecha / 1000;
    }

    /**
     * @param doubleencidadLuzBajaDerecha the doubleencidadLuzBajaDerecha to set
     */
    public void setIntensidadLuzBajaDerecha(double doubleencidadLuzBajaDerecha) {
        this.intensidadLuzBajaDerecha = doubleencidadLuzBajaDerecha;
    }

    /**
     * @return the exploradoraDerecha
     */
    public double getExploradoraDerecha() {
        return exploradoraDerecha / 1000;
    }

    /**
     * @param exploradoraDerecha the exploradoraDerecha to set
     */
    public void setExploradoraDerecha(double exploradoraDerecha) {
        this.exploradoraDerecha = exploradoraDerecha;
    }

    /**
     * @return the inclinacionLuzBajaIzquierda
     */
    public double getInclinacionLuzBajaIzquierda() {
        return inclinacionLuzBajaIzquierda;
    }

    /**
     * @param inclinacionLuzBajaIzquierda the inclinacionLuzBajaIzquierda to set
     */
    public void setInclinacionLuzBajaIzquierda(double inclinacionLuzBajaIzquierda) {
        this.inclinacionLuzBajaIzquierda = inclinacionLuzBajaIzquierda;
    }

    /**
     * @return the doubleencidadLuzBajaIzquierda
     */
    public double getIntensidadLuzBajaIzquierda() {
        return intensidadLuzBajaIzquierda / 1000;
    }

    /**
     * @param doubleencidadLuzBajaIzquierda the doubleencidadLuzBajaIzquierda to
     * set
     */
    public void setIntensidadLuzBajaIzquierda(double doubleencidadLuzBajaIzquierda) {
        this.intensidadLuzBajaIzquierda = doubleencidadLuzBajaIzquierda;
    }

    /**
     * @return the exploradoraIzquierda
     */
    public double getExploradoraIzquierda() {
        return exploradoraIzquierda / 1000;
    }

    /**
     * @param exploradoraIzquierda the exploradoraIzquierda to set
     */
    public void setExploradoraIzquierda(double exploradoraIzquierda) {
        this.exploradoraIzquierda = exploradoraIzquierda;
    }

    /**
     * @return the doubleencidadLuzAltaIzquierda
     */
    public double getIntensidadLuzAltaIzquierda() {
        return intensidadLuzAltaIzquierda / 1000;
    }

    /**
     * @param doubleencidadLuzAltaIzquierda the doubleencidadLuzAltaIzquierda to
     * set
     */
    public void setIntensidadLuzAltaIzquierda(double doubleencidadLuzAltaIzquierda) {
        this.intensidadLuzAltaIzquierda = doubleencidadLuzAltaIzquierda;
    }

    /**
     * @return the doubleencidadLuzAltaDerecha
     */
    public double getIntensidadLuzAltaDerecha() {
        return intensidadLuzAltaDerecha / 1000;
    }

    /**
     * @param doubleencidadLuzAltaDerecha the doubleencidadLuzAltaDerecha to set
     */
    public void setIntensidadLuzAltaDerecha(double doubleencidadLuzAltaDerecha) {
        this.intensidadLuzAltaDerecha = doubleencidadLuzAltaDerecha;
    }

    
    
    public double getIntensidadLuzBaja3() {
        return intensidadLuzBaja3 / 1000;
    }

    public void setIntensidadLuzBaja3(double intensidadLuzBaja3) {
        this.intensidadLuzBaja3 = intensidadLuzBaja3;
    }

    public double getInclinacionLuzBaja3() {
        return inclinacionLuzBaja3;
    }

    public void setInclinacionLuzBaja3(double inclinacionLuzBaja3) {
        this.inclinacionLuzBaja3 = inclinacionLuzBaja3;
    }

    public double getIntensidadLuzAlta3() {
        return intensidadLuzAlta3 / 1000;
    }

    public void setIntensidadLuzAlta3(double intensidadLuzAlta3) {
        this.intensidadLuzAlta3 = intensidadLuzAlta3;
    }
    
    
    
    
    

   
    
   
    @Override
    public String toString() {
        //return "MedidasVehiculosLuxometro{" + "inclinacionLuzBajaDerecha=" + inclinacionLuzBajaDerecha + ", intensidadLuzBajaDerecha=" + intensidadLuzBajaDerecha + ", exploradoraDerecha=" + exploradoraDerecha + ", inclinacionLuzBajaIzquierda=" + inclinacionLuzBajaIzquierda + ", intensidadLuzBajaIzquierda=" + intensidadLuzBajaIzquierda + ", exploradoraIzquierda=" + exploradoraIzquierda + ", intensidadLuzAltaIzquierda=" + intensidadLuzAltaIzquierda + ", intensidadinclinacion baja derechaLuzAltaDerecha=" + intensidadLuzAltaDerecha + '}';
        return 
        "MedidasVehiculosLuxometro{" + "intensidadLuzBaja1: " + intensidadLuzBajaDerecha + ", intensidadLuzBaja2 : "+ intensidadLuzBajaIzquierda + ", intensidadLuzBaja3 : "+ intensidadLuzBaja3+ 
        ", inclinacionLuzBaja1 : "+ inclinacionLuzBajaDerecha + ", inclinacionLuzBaja2: " + inclinacionLuzBajaIzquierda + ", inclinacionLuzBaja3: " + inclinacionLuzBaja3 + 
        ", intensidadLuzAlta1: " + intensidadLuzAltaDerecha + ", intensidadLuzAlta2: "+ intensidadLuzAltaDerecha + ", intensidadLuzAlta3: "+ intensidadLuzAlta3 +
        ", exploradoraDerecha:" + exploradoraDerecha + ", exploradoraIzquierda:"+ exploradoraIzquierda + "}";
    }

}
