/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.models.entities;

/**
 *
 * @author Dany
 */
public class Equipo {

    public static final String TABLA = "equipos";
    public static final String ID_EQUIPO = "id_equipo";
    public static final String PEF = "pef";
    public static final String SERIAL = "serial";
    public static final String SERIAL_RESOLUCION = "serialresolucion";
    public static final String RESOLUCION_AMBIENTAL = "resolucionambiental";
    public static final String MARCA = "marca";
    public static final String NUM_ANALIZADOR = "num_analizador";
    public static final String NUM_SERIAL_BENCH = "num_serial_bench";
  

    private Integer idEquipo;
    private String pef;
    private String serial;
    private String marca;
    private String numAnalizador;
    private String serialresolucion;
    private String resolucionambiental;
    private String serialbench;
    

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getPef() {
        return pef;
    }

    public void setPef(String pef) {
        this.pef = pef;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNumAnalizador() {
        return numAnalizador;
    }

    public void setNumAnalizador(String numAnalizador) {
        this.numAnalizador = numAnalizador;
    }
  
    public String getSerialresolucion() {
        return serialresolucion;
    }

    public void setSerialresolucion(String aSerialresolucion) {
        serialresolucion = aSerialresolucion;
    }

    public String getResolucionambiental() {
        return resolucionambiental;
    }

    public void setResolucionambiental(String resolucionambiental) {
        this.resolucionambiental = resolucionambiental;
    }

    public String getSerialbench() {
        return serialbench;
    }

    public void setSerialbench(String serialbench) {
        this.serialbench = serialbench;
    }
    
}
