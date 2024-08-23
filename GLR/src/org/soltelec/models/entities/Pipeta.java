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
public class Pipeta {
    
    public static final String TABLA = "pipetas";
    public static final String ID_PIPETA = "id_pipetas";
    public static final String TIEMPOS = "tiempos";
    public static final String HC = "hc";
    public static final String CO = "co";
    public static final String CO2 = "co2";
    
    private Integer idPipeta;
    private String tiempos;
    private Integer hc;
    private Integer co;
    private Integer co2;

    public Integer getIdPipeta() {
        return idPipeta;
    }

    public void setIdPipeta(Integer idPipeta) {
        this.idPipeta = idPipeta;
    }

    public String getTiempos() {
        return tiempos;
    }

    public void setTiempos(String tiempos) {
        this.tiempos = tiempos;
    }

    public Integer getHc() {
        return hc;
    }

    public void setHc(Integer hc) {
        this.hc = hc;
    }

    public Integer getCo() {
        return co;
    }

    public void setCo(Integer co) {
        this.co = co;
    }

    public Integer getCo2() {
        return co2;
    }

    public void setCo2(Integer co2) {
        this.co2 = co2;
    }
    
    
}
