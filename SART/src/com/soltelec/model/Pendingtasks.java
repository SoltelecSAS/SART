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
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "pendingtasks")
@NamedQueries({
    @NamedQuery(name = "Pendingtasks.findAll", query = "SELECT p FROM Pendingtasks p"),
    @NamedQuery(name = "Pendingtasks.findByExpdate", query = "SELECT p FROM Pendingtasks p WHERE p.expdate = :expdate"),
    @NamedQuery(name = "Pendingtasks.findByPendingtask", query = "SELECT p FROM Pendingtasks p WHERE p.pendingtask = :pendingtask")})
public class Pendingtasks implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "EXPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expdate;
    @Id
    @Basic(optional = false)
    @Column(name = "PENDINGTASK")
    private Integer pendingtask;
    @JoinColumn(name = "SCHEDULE", referencedColumnName = "SCHEDULE")
    @OneToOne(optional = false)
    private Schedules schedules;

    public Pendingtasks() {
    }

    public Pendingtasks(Integer pendingtask) {
        this.pendingtask = pendingtask;
    }

    public Pendingtasks(Integer pendingtask, Date expdate) {
        this.pendingtask = pendingtask;
        this.expdate = expdate;
    }

    public Date getExpdate() {
        return expdate;
    }

    public void setExpdate(Date expdate) {
        this.expdate = expdate;
    }

    public Integer getPendingtask() {
        return pendingtask;
    }

    public void setPendingtask(Integer pendingtask) {
        this.pendingtask = pendingtask;
    }

    public Schedules getSchedules() {
        return schedules;
    }

    public void setSchedules(Schedules schedules) {
        this.schedules = schedules;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pendingtask != null ? pendingtask.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pendingtasks)) {
            return false;
        }
        Pendingtasks other = (Pendingtasks) object;
        return (this.pendingtask != null || other.pendingtask == null) && (this.pendingtask == null || this.pendingtask.equals(other.pendingtask));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Pendingtasks[pendingtask=" + pendingtask + "]";
    }

}
