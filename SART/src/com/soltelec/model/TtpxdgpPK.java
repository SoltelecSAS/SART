/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Administrador
 */
@Embeddable
public class TtpxdgpPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "TESTTYPE")
    private int testtype;
    @Basic(optional = false)
    @Column(name = "DEFGROUP")
    private int defgroup;

    public TtpxdgpPK() {
    }

    public TtpxdgpPK(int testtype, int defgroup) {
        this.testtype = testtype;
        this.defgroup = defgroup;
    }

    public int getTesttype() {
        return testtype;
    }

    public void setTesttype(int testtype) {
        this.testtype = testtype;
    }

    public int getDefgroup() {
        return defgroup;
    }

    public void setDefgroup(int defgroup) {
        this.defgroup = defgroup;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) testtype;
        hash += (int) defgroup;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TtpxdgpPK)) {
            return false;
        }
        TtpxdgpPK other = (TtpxdgpPK) object;
        if (this.testtype != other.testtype) {
            return false;
        }
        return this.defgroup == other.defgroup;
    }

    @Override
    public String toString() {
        return "com.soltelec.model.TtpxdgpPK[testtype=" + testtype + ", defgroup=" + defgroup + "]";
    }

}
