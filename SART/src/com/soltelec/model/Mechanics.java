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
@Table(name = "mechanics")
@NamedQueries({
    @NamedQuery(name = "Mechanics.findAll", query = "SELECT m FROM Mechanics m"),
    @NamedQuery(name = "Mechanics.findByMechanic", query = "SELECT m FROM Mechanics m WHERE m.mechanic = :mechanic"),
    @NamedQuery(name = "Mechanics.findByMecname", query = "SELECT m FROM Mechanics m WHERE m.mecname = :mecname"),
    @NamedQuery(name = "Mechanics.findByInactiveflag", query = "SELECT m FROM Mechanics m WHERE m.inactiveflag = :inactiveflag")})
public class Mechanics implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MECHANIC")
    private Integer mechanic;
    @Column(name = "MECNAME")
    private String mecname;
    @Column(name = "INACTIVEFLAG")
    private String inactiveflag;

    public Mechanics() {
    }

    public Mechanics(Integer mechanic) {
        this.mechanic = mechanic;
    }

    public Integer getMechanic() {
        return mechanic;
    }

    public void setMechanic(Integer mechanic) {
        this.mechanic = mechanic;
    }

    public String getMecname() {
        return mecname;
    }

    public void setMecname(String mecname) {
        this.mecname = mecname;
    }

    public String getInactiveflag() {
        return inactiveflag;
    }

    public void setInactiveflag(String inactiveflag) {
        this.inactiveflag = inactiveflag;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mechanic != null ? mechanic.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Mechanics)) {
            return false;
        }
        Mechanics other = (Mechanics) object;
        return (this.mechanic != null || other.mechanic == null) && (this.mechanic == null || this.mechanic.equals(other.mechanic));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Mechanics[mechanic=" + mechanic + "]";
    }

}
