/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "ttpxdgp")
@NamedQueries({
    @NamedQuery(name = "Ttpxdgp.findAll", query = "SELECT t FROM Ttpxdgp t"),
    @NamedQuery(name = "Ttpxdgp.findByTesttype", query = "SELECT t FROM Ttpxdgp t WHERE t.ttpxdgpPK.testtype = :testtype"),
    @NamedQuery(name = "Ttpxdgp.findByDefgroup", query = "SELECT t FROM Ttpxdgp t WHERE t.ttpxdgpPK.defgroup = :defgroup")})
public class Ttpxdgp implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TtpxdgpPK ttpxdgpPK;
    @JoinColumn(name = "TESTTYPE", referencedColumnName = "TESTTYPE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private TipoPrueba tipoPrueba;
    @JoinColumn(name = "DEFGROUP", referencedColumnName = "DEFGROUP", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Grupos grupos;

    public Ttpxdgp() {
    }

    public Ttpxdgp(TtpxdgpPK ttpxdgpPK) {
        this.ttpxdgpPK = ttpxdgpPK;
    }

    public Ttpxdgp(int testtype, int defgroup) {
        this.ttpxdgpPK = new TtpxdgpPK(testtype, defgroup);
    }

    public TtpxdgpPK getTtpxdgpPK() {
        return ttpxdgpPK;
    }

    public void setTtpxdgpPK(TtpxdgpPK ttpxdgpPK) {
        this.ttpxdgpPK = ttpxdgpPK;
    }

    public TipoPrueba getTipoPrueba() {
        return tipoPrueba;
    }

    public void setTipoPrueba(TipoPrueba tipoPrueba) {
        this.tipoPrueba = tipoPrueba;
    }

    public Grupos getGrupos() {
        return grupos;
    }

    public void setGrupos(Grupos grupos) {
        this.grupos = grupos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ttpxdgpPK != null ? ttpxdgpPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ttpxdgp)) {
            return false;
        }
        Ttpxdgp other = (Ttpxdgp) object;
        return (this.ttpxdgpPK != null || other.ttpxdgpPK == null) && (this.ttpxdgpPK == null || this.ttpxdgpPK.equals(other.ttpxdgpPK));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Ttpxdgp[ttpxdgpPK=" + ttpxdgpPK + "]";
    }

}
