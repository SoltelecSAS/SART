/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "defaultresume")
@NamedQueries({
    @NamedQuery(name = "Defaultresume.findAll", query = "SELECT d FROM Defaultresume d"),
    @NamedQuery(name = "Defaultresume.findByGroupok", query = "SELECT d FROM Defaultresume d WHERE d.groupok = :groupok"),
    @NamedQuery(name = "Defaultresume.findByDefgroup", query = "SELECT d FROM Defaultresume d WHERE d.defaultresumePK.defgroup = :defgroup"),
    @NamedQuery(name = "Defaultresume.findByCounta", query = "SELECT d FROM Defaultresume d WHERE d.counta = :counta"),
    @NamedQuery(name = "Defaultresume.findByCountb", query = "SELECT d FROM Defaultresume d WHERE d.countb = :countb"),
    @NamedQuery(name = "Defaultresume.findByTestsheet", query = "SELECT d FROM Defaultresume d WHERE d.defaultresumePK.testsheet = :testsheet")})
public class Defaultresume implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DefaultresumePK defaultresumePK;
    @Column(name = "GROUPOK")
    private String groupok;
    @Column(name = "COUNTA")
    private Integer counta;
    @Column(name = "COUNTB")
    private Integer countb;

    public Defaultresume() {
    }

    public Defaultresume(DefaultresumePK defaultresumePK) {
        this.defaultresumePK = defaultresumePK;
    }

    public Defaultresume(int defgroup, int testsheet) {
        this.defaultresumePK = new DefaultresumePK(defgroup, testsheet);
    }

    public DefaultresumePK getDefaultresumePK() {
        return defaultresumePK;
    }

    public void setDefaultresumePK(DefaultresumePK defaultresumePK) {
        this.defaultresumePK = defaultresumePK;
    }

    public String getGroupok() {
        return groupok;
    }

    public void setGroupok(String groupok) {
        this.groupok = groupok;
    }

    public Integer getCounta() {
        return counta;
    }

    public void setCounta(Integer counta) {
        this.counta = counta;
    }

    public Integer getCountb() {
        return countb;
    }

    public void setCountb(Integer countb) {
        this.countb = countb;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (defaultresumePK != null ? defaultresumePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Defaultresume)) {
            return false;
        }
        Defaultresume other = (Defaultresume) object;
        return (this.defaultresumePK != null || other.defaultresumePK == null) && (this.defaultresumePK == null || this.defaultresumePK.equals(other.defaultresumePK));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Defaultresume[defaultresumePK=" + defaultresumePK + "]";
    }

}
