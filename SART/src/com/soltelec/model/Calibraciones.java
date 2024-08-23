/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dany
 */
@Entity
@Table(name = "calibraciones")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Calibraciones.findAll", query = "SELECT c FROM Calibraciones c"),
    @NamedQuery(name = "Calibraciones.findByCalibration", query = "SELECT c FROM Calibraciones c WHERE c.calibration = :calibration"),
    @NamedQuery(name = "Calibraciones.findByCurdate", query = "SELECT c FROM Calibraciones c WHERE c.curdate = :curdate"),
    @NamedQuery(name = "Calibraciones.findByAprobada", query = "SELECT c FROM Calibraciones c WHERE c.aprobada = :aprobada")})
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "id_tipo_calibracion", discriminatorType = DiscriminatorType.INTEGER)

public class Calibraciones implements Serializable {    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "CALIBRATION")
    private Integer calibration;
    @Basic(optional = false)
    @Column(name = "CURDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date curdate;
    @Basic(optional = false)
    @Column(name = "aprobada")
    private boolean aprobada;
    @JoinColumn(name = "id_tipo_calibracion", referencedColumnName = "id_tipo_calibracion")
    @ManyToOne
    private TipoCalibracion idTipoCalibracion;
    @JoinColumn(name = "GEUSER", referencedColumnName = "GEUSER")
    @ManyToOne(optional = false)
    private Usuarios geuser;
    @JoinColumn(name = "id_equipo", referencedColumnName = "id_equipo")
    @ManyToOne(optional = false)
    private Equipos idEquipo;

    public Calibraciones() {
    }

    public Calibraciones(Integer calibration) {
        this.calibration = calibration;
    }

    public Calibraciones(Integer calibration, Date curdate, boolean aprobada) {
        this.calibration = calibration;
        this.curdate = curdate;
        this.aprobada = aprobada;
    }

    public Integer getCalibration() {
        return calibration;
    }

    public void setCalibration(Integer calibration) {
        this.calibration = calibration;
    }

    public Date getCurdate() {
        return curdate;
    }

    public void setCurdate(Date curdate) {
        this.curdate = curdate;
    }

    public boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(boolean aprobada) {
        this.aprobada = aprobada;
    }

    public TipoCalibracion getIdTipoCalibracion() {
        return idTipoCalibracion;
    }

    public void setIdTipoCalibracion(TipoCalibracion idTipoCalibracion) {
        this.idTipoCalibracion = idTipoCalibracion;
    }

    public Usuarios getGeuser() {
        return geuser;
    }

    public void setGeuser(Usuarios geuser) {
        this.geuser = geuser;
    }

    public Equipos getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Equipos idEquipo) {
        this.idEquipo = idEquipo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (calibration != null ? calibration.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Calibraciones)) {
            return false;
        }
        Calibraciones other = (Calibraciones) object;
        return (this.calibration != null || other.calibration == null) && (this.calibration == null || this.calibration.equals(other.calibration));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Calibraciones[ calibration=" + calibration + " ]";
    }
    
}
