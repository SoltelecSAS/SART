/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

/**
 *Clase representando valores de punto de calibracion
 * @author GerenciaDesarrollo de Soluciones Tecnologicas
 */
public class PuntoCalibracion {
    private short valorOxigeno;
    private short valorCO;
    private short valorCO2;
    private short valorHC;
    private short valorHiHC;
    private Double bancoCo;
    private Double bancoCo2;
    private Double bancoO2;
    private Double bancoHc;

    public PuntoCalibracion(short valorOxigeno, short valorCO, short valorCO2, short valorHC) {
        this.valorOxigeno = valorOxigeno;
        this.valorCO = valorCO;
        this.valorCO2 = valorCO2;
        this.valorHC = valorHC;
    }

    public PuntoCalibracion() {//todos cero
        this.valorCO = 0;
        this.valorCO2 = 0;
        this.valorHC = 0;
        this.valorHiHC = 0;
    }//end of constructor

    public short getValorCO() {
        return valorCO;
    }

    public void setValorCO(short valorCO) {
        this.valorCO = valorCO;
    }

    public short getValorCO2() {
        return valorCO2;
    }

    public void setValorCO2(short valorCO2) {
        this.valorCO2 = valorCO2;
    }

    public short getValorHC() {
        return valorHC;
    }

    public void setValorHC(short valorHC) {
        this.valorHC = valorHC;
    }

    public short getValorHiHC() {
        return valorHiHC;
    }

    public void setValorHiHC(short valorHiHC) {
        this.valorHiHC = valorHiHC;
    }

    public short getValorOxigeno() {
        return valorOxigeno;
    }

    public void setValorOxigeno(short valorOxigeno) {
        this.valorOxigeno = valorOxigeno;
    }

    public Double getBancoCo() {
        return bancoCo;
    }

    public void setBancoCo(Double bancoCo) {
        this.bancoCo = bancoCo;
    }

    public Double getBancoCo2() {
        return bancoCo2;
    }

    public void setBancoCo2(Double bancoCo2) {
        this.bancoCo2 = bancoCo2;
    }

    public Double getBancoHc() {
        return bancoHc;
    }

    public void setBancoHc(Double bancoHc) {
        this.bancoHc = bancoHc;
    }

    public Double getBancoO2() {
        return bancoO2;
    }

    public void setBancoO2(Double bancoO2) {
        this.bancoO2 = bancoO2;
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n-----> Punto Calibracion <-----");
        sb.append("\nValor HC: ").append(this.valorHC).append(" ppm");
        sb.append("\nValor CO: ").append(this.valorCO * 0.01).append(" %");
        sb.append("\nValor CO2: ").append(this.valorCO2 * 0.1).append(" %");
        sb.append("\n-----> Punto Calibracion Equipo <-----");
        sb.append("\nEquipo HC: ").append(this.bancoHc).append(" ppm");
        sb.append("\nEquipo CO: ").append(this.bancoCo).append(" %");
        sb.append("\nEquipo CO2: ").append(this.bancoCo2).append(" %\n");
        //sb.append("\nEquipo O2: ").append(this.bancoO2).append(" %\n");
        sb.append("\nEquipo CO2: ").append(this.valorOxigeno).append(" %\n");
        return sb.toString();
     }

    public void setValorHiHC(int i) {
        String shortBinario = Integer.toString(i&0xFFFF, 2);
        this.valorHiHC = (short) Integer.parseInt(shortBinario, 2);

    }
}//end of class
