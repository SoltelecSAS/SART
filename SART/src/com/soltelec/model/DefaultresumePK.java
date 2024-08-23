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
public class DefaultresumePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "DEFGROUP")
    private int defgroup;
    @Basic(optional = false)
    @Column(name = "TESTSHEET")
    private int testsheet;

    public DefaultresumePK() {
    }

    public DefaultresumePK(int defgroup, int testsheet) {
        this.defgroup = defgroup;
        this.testsheet = testsheet;
    }

    public int getDefgroup() {
        return defgroup;
    }

    public void setDefgroup(int defgroup) {
        this.defgroup = defgroup;
    }

    public int getTestsheet() {
        return testsheet;
    }

    public void setTestsheet(int testsheet) {
        this.testsheet = testsheet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) defgroup;
        hash += (int) testsheet;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DefaultresumePK)) {
            return false;
        }
        DefaultresumePK other = (DefaultresumePK) object;
        if (this.defgroup != other.defgroup) {
            return false;
        }
        return this.testsheet == other.testsheet;
    }

    @Override
    public String toString() {
        return "com.soltelec.model.DefaultresumePK[defgroup=" + defgroup + ", testsheet=" + testsheet + "]";
    }

}
