/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "dummy")
@NamedQueries({
    @NamedQuery(name = "Dummy.findAll", query = "SELECT d FROM Dummy d"),
    @NamedQuery(name = "Dummy.findByDummy", query = "SELECT d FROM Dummy d WHERE d.dummy = :dummy"),
    @NamedQuery(name = "Dummy.findByDate1", query = "SELECT d FROM Dummy d WHERE d.date1 = :date1")})
public class Dummy implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "DUMMY")
    private Integer dummy;
    @Basic(optional = false)
    @Column(name = "DATE1")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date1;

    public Dummy() {
    }

    public Dummy(Integer dummy) {
        this.dummy = dummy;
    }

    public Dummy(Integer dummy, Date date1) {
        this.dummy = dummy;
        this.date1 = date1;
    }

    public Integer getDummy() {
        return dummy;
    }

    public void setDummy(Integer dummy) {
        this.dummy = dummy;
    }

    public Date getDate1() {
        return date1;
    }

    public void setDate1(Date date1) {
        this.date1 = date1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dummy != null ? dummy.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dummy)) {
            return false;
        }
        Dummy other = (Dummy) object;
        return (this.dummy != null || other.dummy == null) && (this.dummy == null || this.dummy.equals(other.dummy));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Dummy[dummy=" + dummy + "]";
    }

}
