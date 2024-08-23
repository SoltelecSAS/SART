/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Dany
 */
@Entity
@Table(name = "tipo_calibracion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoCalibracion.findAll", query = "SELECT t FROM TipoCalibracion t"),
    @NamedQuery(name = "TipoCalibracion.findByIdTipoCalibracion", query = "SELECT t FROM TipoCalibracion t WHERE t.idTipoCalibracion = :idTipoCalibracion"),
    @NamedQuery(name = "TipoCalibracion.findByTipoCalibracion", query = "SELECT t FROM TipoCalibracion t WHERE t.tipoCalibracion = :tipoCalibracion")})
public class TipoCalibracion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tipo_calibracion")
    private Integer idTipoCalibracion;
    @Basic(optional = false)
    @Column(name = "tipo_calibracion")
    private String tipoCalibracion;
    @OneToMany(mappedBy = "idTipoCalibracion")
    private List<Calibraciones> calibracionesList;

    public TipoCalibracion() {
    }

    public TipoCalibracion(Integer idTipoCalibracion) {
        this.idTipoCalibracion = idTipoCalibracion;
    }

    public TipoCalibracion(Integer idTipoCalibracion, String tipoCalibracion) {
        this.idTipoCalibracion = idTipoCalibracion;
        this.tipoCalibracion = tipoCalibracion;
    }

    public Integer getIdTipoCalibracion() {
        return idTipoCalibracion;
    }

    public void setIdTipoCalibracion(Integer idTipoCalibracion) {
        this.idTipoCalibracion = idTipoCalibracion;
    }

    public String getTipoCalibracion() {
        return tipoCalibracion;
    }

    public void setTipoCalibracion(String tipoCalibracion) {
        this.tipoCalibracion = tipoCalibracion;
    }

    @XmlTransient
    public List<Calibraciones> getCalibracionesList() {
        return calibracionesList;
    }

    public void setCalibracionesList(List<Calibraciones> calibracionesList) {
        this.calibracionesList = calibracionesList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTipoCalibracion != null ? idTipoCalibracion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoCalibracion)) {
            return false;
        }
        TipoCalibracion other = (TipoCalibracion) object;
        return (this.idTipoCalibracion != null || other.idTipoCalibracion == null) && (this.idTipoCalibracion == null || this.idTipoCalibracion.equals(other.idTipoCalibracion));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.TipoCalibracion[ idTipoCalibracion=" + idTipoCalibracion + " ]";
    }
    
}
