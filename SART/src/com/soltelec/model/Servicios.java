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
@Table(name = "servicios")
@NamedQueries({
    @NamedQuery(name = "Servicios.findAll", query = "SELECT s FROM Servicios s"),
    @NamedQuery(name = "Servicios.findByService", query = "SELECT s FROM Servicios s WHERE s.service = :service"),
    @NamedQuery(name = "Servicios.findByNombreservicio", query = "SELECT s FROM Servicios s WHERE s.nombreservicio = :nombreservicio")})
public class Servicios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SERVICE")
    private Integer service;
    @Basic(optional = false)
    @Column(name = "Nombre_servicio")
    private String nombreservicio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "servicios")
    private Collection<Vehiculos> vehiculosCollection;

    public Servicios() {
    }

    public Servicios(Integer service) {
        this.service = service;
    }

    public Servicios(Integer service, String nombreservicio) {
        this.service = service;
        this.nombreservicio = nombreservicio;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer service) {
        this.service = service;
    }

    public String getNombreservicio() {
        return nombreservicio;
    }

    public void setNombreservicio(String nombreservicio) {
        this.nombreservicio = nombreservicio;
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
        hash += (service != null ? service.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Servicios)) {
            return false;
        }
        Servicios other = (Servicios) object;
        return (this.service != null || other.service == null) && (this.service == null || this.service.equals(other.service));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Servicios[service=" + service + "]";
    }

}
