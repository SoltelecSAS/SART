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
@Table(name = "roads")
@NamedQueries({
    @NamedQuery(name = "Roads.findAll", query = "SELECT r FROM Roads r"),
    @NamedQuery(name = "Roads.findByRoad", query = "SELECT r FROM Roads r WHERE r.road = :road"),
    @NamedQuery(name = "Roads.findByRoadtype", query = "SELECT r FROM Roads r WHERE r.roadtype = :roadtype"),
    @NamedQuery(name = "Roads.findByDescription", query = "SELECT r FROM Roads r WHERE r.description = :description"),
    @NamedQuery(name = "Roads.findByResolution", query = "SELECT r FROM Roads r WHERE r.resolution = :resolution")})
public class Roads implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ROAD")
    private Integer road;
    @Basic(optional = false)
    @Column(name = "ROADTYPE")
    private String roadtype;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "RESOLUTION")
    private String resolution;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roads")
    private Collection<Consolas> consolasCollection;

    public Roads() {
    }

    public Roads(Integer road) {
        this.road = road;
    }

    public Roads(Integer road, String roadtype) {
        this.road = road;
        this.roadtype = roadtype;
    }

    public Integer getRoad() {
        return road;
    }

    public void setRoad(Integer road) {
        this.road = road;
    }

    public String getRoadtype() {
        return roadtype;
    }

    public void setRoadtype(String roadtype) {
        this.roadtype = roadtype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Collection<Consolas> getConsolasCollection() {
        return consolasCollection;
    }

    public void setConsolasCollection(Collection<Consolas> consolasCollection) {
        this.consolasCollection = consolasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (road != null ? road.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Roads)) {
            return false;
        }
        Roads other = (Roads) object;
        return (this.road != null || other.road == null) && (this.road == null || this.road.equals(other.road));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Roads[road=" + road + "]";
    }

}
