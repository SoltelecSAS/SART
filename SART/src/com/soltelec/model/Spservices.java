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
@Table(name = "spservices")
@NamedQueries({
    @NamedQuery(name = "Spservices.findAll", query = "SELECT s FROM Spservices s"),
    @NamedQuery(name = "Spservices.findBySpservice", query = "SELECT s FROM Spservices s WHERE s.spservice = :spservice"),
    @NamedQuery(name = "Spservices.findBySpservname", query = "SELECT s FROM Spservices s WHERE s.spservname = :spservname"),
    @NamedQuery(name = "Spservices.findBySpservexptime", query = "SELECT s FROM Spservices s WHERE s.spservexptime = :spservexptime")})
public class Spservices implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SPSERVICE")
    private Integer spservice;
    @Basic(optional = false)
    @Column(name = "SPSERVNAME")
    private String spservname;
    @Basic(optional = false)
    @Column(name = "SPSERVEXPTIME")
    private int spservexptime;

    public Spservices() {
    }

    public Spservices(Integer spservice) {
        this.spservice = spservice;
    }

    public Spservices(Integer spservice, String spservname, int spservexptime) {
        this.spservice = spservice;
        this.spservname = spservname;
        this.spservexptime = spservexptime;
    }

    public Integer getSpservice() {
        return spservice;
    }

    public void setSpservice(Integer spservice) {
        this.spservice = spservice;
    }

    public String getSpservname() {
        return spservname;
    }

    public void setSpservname(String spservname) {
        this.spservname = spservname;
    }

    public int getSpservexptime() {
        return spservexptime;
    }

    public void setSpservexptime(int spservexptime) {
        this.spservexptime = spservexptime;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (spservice != null ? spservice.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Spservices)) {
            return false;
        }
        Spservices other = (Spservices) object;
        return (this.spservice != null || other.spservice == null) && (this.spservice == null || this.spservice.equals(other.spservice));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Spservices[spservice=" + spservice + "]";
    }

}
