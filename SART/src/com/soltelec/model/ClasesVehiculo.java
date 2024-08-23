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
@Table(name = "clases_vehiculo")
@NamedQueries({
    @NamedQuery(name = "ClasesVehiculo.findAll", query = "SELECT c FROM ClasesVehiculo c"),
    @NamedQuery(name = "ClasesVehiculo.findByClass1", query = "SELECT c FROM ClasesVehiculo c WHERE c.class1 = :class1"),
    @NamedQuery(name = "ClasesVehiculo.findByNombreclase", query = "SELECT c FROM ClasesVehiculo c WHERE c.nombreclase = :nombreclase")})
public class ClasesVehiculo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CLASS")
    private Integer class1;
    @Basic(optional = false)
    @Column(name = "Nombre_clase")
    private String nombreclase;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "clasesVehiculo")
    private Collection<Vehiculos> vehiculosCollection;

    public ClasesVehiculo() {
    }

    public ClasesVehiculo(Integer class1) {
        this.class1 = class1;
    }

    public ClasesVehiculo(Integer class1, String nombreclase) {
        this.class1 = class1;
        this.nombreclase = nombreclase;
    }

    public Integer getClass1() {
        return class1;
    }

    public void setClass1(Integer class1) {
        this.class1 = class1;
    }

    public String getNombreclase() {
        return nombreclase;
    }

    public void setNombreclase(String nombreclase) {
        this.nombreclase = nombreclase;
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
        hash += (class1 != null ? class1.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClasesVehiculo)) {
            return false;
        }
        ClasesVehiculo other = (ClasesVehiculo) object;
        return (this.class1 != null || other.class1 == null) && (this.class1 == null || this.class1.equals(other.class1));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.ClasesVehiculo[class1=" + class1 + "]";
    }

}
