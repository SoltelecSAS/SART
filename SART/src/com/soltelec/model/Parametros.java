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
@Table(name = "parametros")
@NamedQueries({
    @NamedQuery(name = "Parametros.findAll", query = "SELECT p FROM Parametros p"),
    @NamedQuery(name = "Parametros.findByParamname", query = "SELECT p FROM Parametros p WHERE p.paramname = :paramname"),
    @NamedQuery(name = "Parametros.findByParamvalue", query = "SELECT p FROM Parametros p WHERE p.paramvalue = :paramvalue"),
    @NamedQuery(name = "Parametros.findByDefaultvalue", query = "SELECT p FROM Parametros p WHERE p.defaultvalue = :defaultvalue")})
public class Parametros implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PARAMNAME")
    private String paramname;
    @Column(name = "PARAMVALUE")
    private String paramvalue;
    @Basic(optional = false)
    @Column(name = "DEFAULTVALUE")
    private String defaultvalue;

    public Parametros() {
    }

    public Parametros(String paramname) {
        this.paramname = paramname;
    }

    public Parametros(String paramname, String defaultvalue) {
        this.paramname = paramname;
        this.defaultvalue = defaultvalue;
    }

    public String getParamname() {
        return paramname;
    }

    public void setParamname(String paramname) {
        this.paramname = paramname;
    }

    public String getParamvalue() {
        return paramvalue;
    }

    public void setParamvalue(String paramvalue) {
        this.paramvalue = paramvalue;
    }

    public String getDefaultvalue() {
        return defaultvalue;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (paramname != null ? paramname.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Parametros)) {
            return false;
        }
        Parametros other = (Parametros) object;
        return (this.paramname != null || other.paramname == null) && (this.paramname == null || this.paramname.equals(other.paramname));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Parametros[paramname=" + paramname + "]";
    }

}
