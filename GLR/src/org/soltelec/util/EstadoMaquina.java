/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *Clase que encapsula el estado de la maquina
 * especialmente las flags que son necesarias
 * para las distintas rutinas
 * @author Usuario
 */
public class EstadoMaquina {

    boolean calentamiento,zero,newO2,calZero,bajoFlujo,newNox,ADrailed;
    boolean badO2,BadNOx,flagInterna,condensacion,calibracionRequerida,calibracionEnProgreso,zeroEnProgreso;
    boolean HCDataValid,CODataValid,CO2DataValid,O2DataValid;
    boolean pruebaFugasAprobada;
    String mensajes;
    int contadorCalentamiento;
    
    public enum MODO_OPERACION {
        NORMAL,INICIALIZACION,STANDBY,FALLO;
    }
    
    private MODO_OPERACION modo;

    public EstadoMaquina(){
        
    }

    public MODO_OPERACION getModo() {
        return modo;
    }

    public void setModo(MODO_OPERACION modo) {
        this.modo = modo;
    }

    public boolean isPruebaFugasAprobada() {
        return pruebaFugasAprobada;
    }

    public void setPruebaFugasAprobada(boolean pruebaFugasAprobada) {
        this.pruebaFugasAprobada = pruebaFugasAprobada;
    }

    
    
    public boolean isADrailed() {
        return ADrailed;
    }

    public void setADrailed(boolean ADrailed) {
        this.ADrailed = ADrailed;
    }

    public boolean isBadNOx() {
        return BadNOx;
    }

    public void setBadNOx(boolean BadNOx) {
        this.BadNOx = BadNOx;
    }

    public boolean isBadO2() {
        return badO2;
    }

    public void setBadO2(boolean badO2) {
        this.badO2 = badO2;
    }

    public boolean isBajoFlujo() {
        return bajoFlujo;
    }

    public void setBajoFlujo(boolean bajoFlujo) {
        this.bajoFlujo = bajoFlujo;
    }

    public boolean isCalZero() {
        return calZero;
    }

    public void setCalZero(boolean calZero) {
        this.calZero = calZero;
    }

    public boolean isCalentamiento() {
        return calentamiento;
    }

    public void setCalentamiento(boolean calentamiento) {
        this.calentamiento = calentamiento;
    }

    public int getContadorCalentamiento() {
        return contadorCalentamiento;
    }

    public void setContadorCalentamiento(int contadorCalentamiento) {
        this.contadorCalentamiento = contadorCalentamiento;
    }

    public boolean isFlagInterna() {
        return flagInterna;
    }

    public void setFlagInterna(boolean flagInterna) {
        this.flagInterna = flagInterna;
    }

    public String getMensajes() {
        return mensajes;
    }

    public void setMensajes(String mensajes) {
        this.mensajes = mensajes;
    }

    public boolean isNewNox() {
        return newNox;
    }

    public void setNewNox(boolean newNox) {
        this.newNox = newNox;
    }

    public boolean isNewO2() {
        return newO2;
    }

    public void setNewO2(boolean newO2) {
        this.newO2 = newO2;
    }

    public boolean isZero() {
        return zero;
    }

    public void setZero(boolean zero) {
        this.zero = zero;
    }

    public boolean isCondensacion() {
        return condensacion;
    }

    public void setCondensacion(boolean condensacion) {
        this.condensacion = condensacion;
    }

    public boolean isCalibracionEnProgreso() {
        return calibracionEnProgreso;
    }

    public void setCalibracionEnProgreso(boolean calibracionEnProgreso) {
        this.calibracionEnProgreso = calibracionEnProgreso;
    }

    public boolean isCalibracionRequerida() {
        return calibracionRequerida;
    }

    public void setCalibracionRequerida(boolean calibracionRequerida) {
        this.calibracionRequerida = calibracionRequerida;
    }

    public boolean isZeroEnProgreso() {
        return zeroEnProgreso;
    }

    public void setZeroEnProgreso(boolean zeroEnProgreso) {
        this.zeroEnProgreso = zeroEnProgreso;
    }

    public boolean isCO2DataValid() {
        return CO2DataValid;
    }

    public void setCO2DataValid(boolean CO2DataValid) {
        this.CO2DataValid = CO2DataValid;
    }

    public boolean isCODataValid() {
        return CODataValid;
    }

    public void setCODataValid(boolean CODataValid) {
        this.CODataValid = CODataValid;
    }

    public boolean isHCDataValid() {
        return HCDataValid;
    }

    public void setHCDataValid(boolean HCDataValid) {
        this.HCDataValid = HCDataValid;
    }

    public boolean isO2DataValid() {
        return O2DataValid;
    }

    public void setO2DataValid(boolean O2DataValid) {
        this.O2DataValid = O2DataValid;
    }

    
   



}//end of class
