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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "colores")
@NamedQueries({
    @NamedQuery(name = "Colores.findAll", query = "SELECT c FROM Colores c"),
    @NamedQuery(name = "Colores.findByColor", query = "SELECT c FROM Colores c WHERE c.color = :color"),
    @NamedQuery(name = "Colores.findByNombrecolor", query = "SELECT c FROM Colores c WHERE c.nombrecolor = :nombrecolor")})
public class Colores implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "COLOR")
    private Integer color;
    @Basic(optional = false)
    @Column(name = "Nombre_color")
    private String nombrecolor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "colores")
    private Collection<Vehiculos> vehiculosCollection;

    public Colores() {
    }

    public Colores(Integer color) {
        this.color = color;
    }

    public Colores(Integer color, String nombrecolor) {
        this.color = color;
        this.nombrecolor = nombrecolor;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getNombrecolor() {
        return nombrecolor;
    }

    public void setNombrecolor(String nombrecolor) {
        this.nombrecolor = nombrecolor;
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
        hash += (color != null ? color.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Colores)) {
            return false;
        }
        Colores other = (Colores) object;
        return (this.color != null || other.color == null) && (this.color == null || this.color.equals(other.color));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Colores[color=" + color + "]";
    }

}
