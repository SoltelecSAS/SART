/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dany
 */
@Entity
@Table(name = "prueba_dos_puntos")
@XmlRootElement
@DiscriminatorValue(value = "2")
public class CalibracionDosPuntos extends Calibraciones implements Serializable {
    @Basic(optional = false)
    @Column(name = "bm_co")
    private double bmCo;
    @Basic(optional = false)
    @Column(name = "bm_co2")
    private double bmCo2;
    @Basic(optional = false)
    @Column(name = "bm_hc")
    private double bmHc;
    @Basic(optional = false)
    @Column(name = "alta_co")
    private double altaCo;
    @Basic(optional = false)
    @Column(name = "alta_co2")
    private double altaCo2;
    @Basic(optional = false)
    @Column(name = "alta_hc")
    private double altaHc;
    @Basic(optional = false)
    @Column(name = "banco_bm_co")
    private double bancoBmCo;
    @Basic(optional = false)
    @Column(name = "banco_bm_co2")
    private double bancoBmCo2;
    @Basic(optional = false)
    @Column(name = "banco_bm_hc")
    private double bancoBmHc;
    @Basic(optional = false)
    @Column(name = "banco_alta_co")
    private double bancoAltaCo;
    @Basic(optional = false)
    @Column(name = "banco_alta_co2")
    private double bancoAltaCo2;
    @Basic(optional = false)
    @Column(name = "banco_alta_hc")
    private double bancoAltaHc;

    public CalibracionDosPuntos() {
    }

    public CalibracionDosPuntos(double bmCo, double bmCo2, double bmHc, double altaCo, double altaCo2, double altaHc, double bancoBmCo, double bancoBmCo2, double bancoBmHc, double bancoAltaCo, double bancoAltaCo2, double bancoAltaHc) {
        this.bmCo = bmCo;
        this.bmCo2 = bmCo2;
        this.bmHc = bmHc;
        this.altaCo = altaCo;
        this.altaCo2 = altaCo2;
        this.altaHc = altaHc;
        this.bancoBmCo = bancoBmCo;
        this.bancoBmCo2 = bancoBmCo2;
        this.bancoBmHc = bancoBmHc;
        this.bancoAltaCo = bancoAltaCo;
        this.bancoAltaCo2 = bancoAltaCo2;
        this.bancoAltaHc = bancoAltaHc;
    }

    public double getBmCo() {
        return bmCo;
    }

    public void setBmCo(double bmCo) {
        this.bmCo = bmCo;
    }

    public double getBmCo2() {
        return bmCo2;
    }

    public void setBmCo2(double bmCo2) {
        this.bmCo2 = bmCo2;
    }

    public double getBmHc() {
        return bmHc;
    }

    public void setBmHc(double bmHc) {
        this.bmHc = bmHc;
    }

    public double getAltaCo() {
        return altaCo;
    }

    public void setAltaCo(double altaCo) {
        this.altaCo = altaCo;
    }

    public double getAltaCo2() {
        return altaCo2;
    }

    public void setAltaCo2(double altaCo2) {
        this.altaCo2 = altaCo2;
    }

    public double getAltaHc() {
        return altaHc;
    }

    public void setAltaHc(double altaHc) {
        this.altaHc = altaHc;
    }

    public double getBancoBmCo() {
        return bancoBmCo;
    }

    public void setBancoBmCo(double bancoBmCo) {
        this.bancoBmCo = bancoBmCo;
    }

    public double getBancoBmCo2() {
        return bancoBmCo2;
    }

    public void setBancoBmCo2(double bancoBmCo2) {
        this.bancoBmCo2 = bancoBmCo2;
    }

    public double getBancoBmHc() {
        return bancoBmHc;
    }

    public void setBancoBmHc(double bancoBmHc) {
        this.bancoBmHc = bancoBmHc;
    }

    public double getBancoAltaCo() {
        return bancoAltaCo;
    }

    public void setBancoAltaCo(double bancoAltaCo) {
        this.bancoAltaCo = bancoAltaCo;
    }

    public double getBancoAltaCo2() {
        return bancoAltaCo2;
    }

    public void setBancoAltaCo2(double bancoAltaCo2) {
        this.bancoAltaCo2 = bancoAltaCo2;
    }

    public double getBancoAltaHc() {
        return bancoAltaHc;
    }

    public void setBancoAltaHc(double bancoAltaHc) {
        this.bancoAltaHc = bancoAltaHc;
    }    
}
