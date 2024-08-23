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
@Table(name = "emessages")
@NamedQueries({
    @NamedQuery(name = "Emessages.findAll", query = "SELECT e FROM Emessages e"),
    @NamedQuery(name = "Emessages.findByConstrainname", query = "SELECT e FROM Emessages e WHERE e.constrainname = :constrainname"),
    @NamedQuery(name = "Emessages.findByEmessagetext", query = "SELECT e FROM Emessages e WHERE e.emessagetext = :emessagetext")})
public class Emessages implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CONSTRAINNAME")
    private String constrainname;
    @Column(name = "EMESSAGETEXT")
    private String emessagetext;

    public Emessages() {
    }

    public Emessages(String constrainname) {
        this.constrainname = constrainname;
    }

    public String getConstrainname() {
        return constrainname;
    }

    public void setConstrainname(String constrainname) {
        this.constrainname = constrainname;
    }

    public String getEmessagetext() {
        return emessagetext;
    }

    public void setEmessagetext(String emessagetext) {
        this.emessagetext = emessagetext;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (constrainname != null ? constrainname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Emessages)) {
            return false;
        }
        Emessages other = (Emessages) object;
        return (this.constrainname != null || other.constrainname == null) && (this.constrainname == null || this.constrainname.equals(other.constrainname));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Emessages[constrainname=" + constrainname + "]";
    }

}
