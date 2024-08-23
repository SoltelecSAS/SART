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
@Table(name = "roadtypes")
@NamedQueries({
    @NamedQuery(name = "Roadtypes.findAll", query = "SELECT r FROM Roadtypes r"),
    @NamedQuery(name = "Roadtypes.findByIroad", query = "SELECT r FROM Roadtypes r WHERE r.iroad = :iroad"),
    @NamedQuery(name = "Roadtypes.findByIdescription", query = "SELECT r FROM Roadtypes r WHERE r.idescription = :idescription")})
public class Roadtypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IROAD")
    private Integer iroad;
    @Column(name = "IDESCRIPTION")
    private String idescription;

    public Roadtypes() {
    }

    public Roadtypes(Integer iroad) {
        this.iroad = iroad;
    }

    public Integer getIroad() {
        return iroad;
    }

    public void setIroad(Integer iroad) {
        this.iroad = iroad;
    }

    public String getIdescription() {
        return idescription;
    }

    public void setIdescription(String idescription) {
        this.idescription = idescription;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iroad != null ? iroad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Roadtypes)) {
            return false;
        }
        Roadtypes other = (Roadtypes) object;
        return (this.iroad != null || other.iroad == null) && (this.iroad == null || this.iroad.equals(other.iroad));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Roadtypes[iroad=" + iroad + "]";
    }

}
