/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "dbshadows")
@NamedQueries({
    @NamedQuery(name = "Dbshadows.findAll", query = "SELECT d FROM Dbshadows d"),
    @NamedQuery(name = "Dbshadows.findByDbshadow", query = "SELECT d FROM Dbshadows d WHERE d.dbshadow = :dbshadow"),
    @NamedQuery(name = "Dbshadows.findByPath", query = "SELECT d FROM Dbshadows d WHERE d.path = :path")})
public class Dbshadows implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "DBSHADOW")
    private Integer dbshadow;
    @Basic(optional = false)
    @Column(name = "PATH")
    private String path;

    public Dbshadows() {
    }

    public Dbshadows(Integer dbshadow) {
        this.dbshadow = dbshadow;
    }

    public Dbshadows(Integer dbshadow, String path) {
        this.dbshadow = dbshadow;
        this.path = path;
    }

    public Integer getDbshadow() {
        return dbshadow;
    }

    public void setDbshadow(Integer dbshadow) {
        this.dbshadow = dbshadow;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dbshadow != null ? dbshadow.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dbshadows)) {
            return false;
        }
        Dbshadows other = (Dbshadows) object;
        return (this.dbshadow != null || other.dbshadow == null) && (this.dbshadow == null || this.dbshadow.equals(other.dbshadow));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Dbshadows[dbshadow=" + dbshadow + "]";
    }

}
