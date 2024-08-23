/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "tipos_gasolina")
@NamedQueries({
    @NamedQuery(name = "TiposGasolina.findAll", query = "SELECT t FROM TiposGasolina t"),
    @NamedQuery(name = "TiposGasolina.findByFueltype", query = "SELECT t FROM TiposGasolina t WHERE t.fueltype = :fueltype"),
    @NamedQuery(name = "TiposGasolina.findByNombregasolina", query = "SELECT t FROM TiposGasolina t WHERE t.nombregasolina = :nombregasolina")})
public class TiposGasolina implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "FUELTYPE")
    private Integer fueltype;
    @Column(name = "Nombre_gasolina")
    private String nombregasolina;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tiposGasolina")
    private Collection<Vehiculos> vehiculosCollection;

    public TiposGasolina() {
    }

    public TiposGasolina(Integer fueltype) {
        this.fueltype = fueltype;
    }

    public Integer getFueltype() {
        return fueltype;
    }

    public void setFueltype(Integer fueltype) {
        this.fueltype = fueltype;
    }

    public String getNombregasolina() {
        return nombregasolina;
    }

    public void setNombregasolina(String nombregasolina) {
        this.nombregasolina = nombregasolina;
    }

    public Collection<Vehiculos> getVehiculosCollection() {
        return vehiculosCollection;
    }

    public void setVehiculosCollection(Collection<Vehiculos> vehiculosCollection) {
        this.vehiculosCollection = vehiculosCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fueltype != null ? fueltype.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TiposGasolina)) {
            return false;
        }
        TiposGasolina other = (TiposGasolina) object;
        return (this.fueltype != null || other.fueltype == null) && (this.fueltype == null || this.fueltype.equals(other.fueltype));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.TiposGasolina[fueltype=" + fueltype + "]";
    }

}
