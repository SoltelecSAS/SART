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
@Table(name = "marcas")
@NamedQueries({
    @NamedQuery(name = "Marcas.findAll", query = "SELECT m FROM Marcas m"),
    @NamedQuery(name = "Marcas.findByCarmark", query = "SELECT m FROM Marcas m WHERE m.carmark = :carmark"),
    @NamedQuery(name = "Marcas.findByNombremarca", query = "SELECT m FROM Marcas m WHERE m.nombremarca = :nombremarca")})
public class Marcas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CARMARK")
    private Integer carmark;
    @Basic(optional = false)
    @Column(name = "Nombre_marca")
    private String nombremarca;
    @OneToMany(mappedBy = "marcas")
    private Collection<Vehiculos> vehiculosCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "marcas")
    private Collection<LineasVehiculos> lineasVehiculosCollection;

    public Marcas() {
    }

    public Marcas(Integer carmark) {
        this.carmark = carmark;
    }

    public Marcas(Integer carmark, String nombremarca) {
        this.carmark = carmark;
        this.nombremarca = nombremarca;
    }

    public Integer getCarmark() {
        return carmark;
    }

    public void setCarmark(Integer carmark) {
        this.carmark = carmark;
    }

    public String getNombremarca() {
        return nombremarca;
    }

    public void setNombremarca(String nombremarca) {
        this.nombremarca = nombremarca;
    }

    public Collection<Vehiculos> getVehiculosCollection() {
        return vehiculosCollection;
    }

    public void setVehiculosCollection(Collection<Vehiculos> vehiculosCollection) {
        this.vehiculosCollection = vehiculosCollection;
    }

    public Collection<LineasVehiculos> getLineasVehiculosCollection() {
        return lineasVehiculosCollection;
    }

    public void setLineasVehiculosCollection(Collection<LineasVehiculos> lineasVehiculosCollection) {
        this.lineasVehiculosCollection = lineasVehiculosCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carmark != null ? carmark.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Marcas)) {
            return false;
        }
        Marcas other = (Marcas) object;
        return (this.carmark != null || other.carmark == null) && (this.carmark == null || this.carmark.equals(other.carmark));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Marcas[carmark=" + carmark + "]";
    }

}
