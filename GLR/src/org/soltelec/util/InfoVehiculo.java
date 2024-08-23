/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *Clase para guardar informacion del vehiculo
 * esencialmente es una clase envoltoria
 * en donde se trae el modelo, el tipo
 * de combustible, el numero de tiempos, el
 * numero de exhostos, etc;
 * @author Gerencia de Desarrollo de Soluciones Tecnologicas
 */
public class InfoVehiculo {

    private int numeroTiempos, modelo , tipoCombustible,tipoVehiculo;
    private int numeroCilindros,numeroExostos;
    private String placa;
    private String diseño;
    private int numeroLinea;
    private int rpmIni;
    private int rpmFin;
    private String Catalizador;
     
    

    public InfoVehiculo() {
    }

    public InfoVehiculo(int numeroTiempos,  int modelo, int tipoCombustible, int tipoVehiculo , String placa) {
        this.numeroTiempos = numeroTiempos;
        this.tipoVehiculo = tipoVehiculo;
        this.modelo = modelo;
        this.tipoCombustible = tipoCombustible;
        this.placa = placa;
    }

    
    public int getModelo() {
        return modelo;
    }

    public void setModelo(int modelo) {
        this.modelo = modelo;
    }

   public void setNumeroTiempos(int numeroTiempos) {
        this.numeroTiempos = numeroTiempos;
    }

    public int getNumeroTiempos() {
        return numeroTiempos;
    }
    
    public void setNroLinea(int numeroLinea) {
        this.numeroLinea = numeroLinea;
    }   

    public int getNroLinea() {
        return numeroLinea;
    }  
    
    public void setRpmIni(int rpmIni) {
        this.rpmIni = rpmIni;
    }   

    public int getRpmIni() {
        return rpmIni;
    }
    
    public void setRpmFin(int rpmFin) {
        this.rpmFin = rpmFin;
    }   

    public int getRpmFin() {
        return rpmFin;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(int tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public int getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(int tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public int getNumeroCilindros() {
        return numeroCilindros;
    }

    public void setNumeroCilindros(int numeroCilindros) {
        this.numeroCilindros = numeroCilindros;
    }

    public int getNumeroExostos() {
        return numeroExostos;
    }

    public void setNumeroExostos(int numeroExostos) {
        this.numeroExostos = numeroExostos;
    }

    public String getDiseño() {
        return diseño;
    }

    public void setDiseño(String diseño) {
        this.diseño = diseño;
    }

    public String getCatalizador() {
        return Catalizador;
    }

    public void setCatalizador(String Catalizador) {
        this.Catalizador = Catalizador;
    }

    
}
