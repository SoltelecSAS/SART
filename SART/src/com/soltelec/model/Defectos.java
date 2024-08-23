/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author GerenciaDesarrollo
 */
@Entity
@Table(name = "defectos")
@NamedQueries({
    @NamedQuery(name = "Defectos.findAll", query = "SELECT d FROM Defectos d"),
    @NamedQuery(name = "Defectos.findByCardefault", query = "SELECT d FROM Defectos d WHERE d.cardefault = :cardefault"),
    @NamedQuery(name = "Defectos.findByNombreproblema", query = "SELECT d FROM Defectos d WHERE d.nombreproblema = :nombreproblema"),
    @NamedQuery(name = "Defectos.findByTipodefecto", query = "SELECT d FROM Defectos d WHERE d.tipodefecto = :tipodefecto")})
public class Defectos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CARDEFAULT")
    private Integer cardefault;
    @Column(name = "Nombre_problema")
    private String nombreproblema;
    @Basic(optional = false)
    @Column(name = "Tipo_defecto")
    private String tipodefecto;
    @Basic(optional = false)
    @Column(name = "Codigo_Super")
    private String codigoSuperintendencia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "defectos")
    private Collection<Defectoxmedida> defectoxmedidaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "defectos")
    private Collection<Permisibles> permisiblesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "defectos")
    private Collection<Defxprueba> defxpruebaCollection;
     
    @JoinColumn(name = "DEFGROUPSSUB", referencedColumnName = "SCDEFGROUPSUB")
    @ManyToOne(optional = false)
    private SubGrupos subGrupos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "defectos")
    private Collection<Defxplaca> defxplacaCollection;

    public Defectos() {
    }

    public Defectos(Integer cardefault) {
        this.cardefault = cardefault;
    }

    public Defectos(Integer cardefault, String tipodefecto) {
        this.cardefault = cardefault;
        this.tipodefecto = tipodefecto;
    }

    public Integer getCardefault() {
        return cardefault;
    }

    public void setCardefault(Integer cardefault) {
        this.cardefault = cardefault;
    }

    public String getNombreproblema() {
        return nombreproblema;
    }

    public void setNombreproblema(String nombreproblema) {
        this.nombreproblema = nombreproblema;
    }

    public String getTipodefecto() {
        return tipodefecto;
    }

    public void setTipodefecto(String tipodefecto) {
        this.tipodefecto = tipodefecto;
    }

    public String getCodigoSuperintendencia() {
        return codigoSuperintendencia;
    }

    public void setCodigoSuperintendencia(String codigoSuperintendencia) {
        this.codigoSuperintendencia = codigoSuperintendencia;
    }

    public Collection<Defectoxmedida> getDefectoxmedidaCollection() {
        return defectoxmedidaCollection;
    }

    public void setDefectoxmedidaCollection(Collection<Defectoxmedida> defectoxmedidaCollection) {
        this.defectoxmedidaCollection = defectoxmedidaCollection;
    }

    public Collection<Permisibles> getPermisiblesCollection() {
        return permisiblesCollection;
    }

    public void setPermisiblesCollection(Collection<Permisibles> permisiblesCollection) {
        this.permisiblesCollection = permisiblesCollection;
    }

    public Collection<Defxprueba> getDefxpruebaCollection() {
        return defxpruebaCollection;
    }

    public void setDefxpruebaCollection(Collection<Defxprueba> defxpruebaCollection) {
        this.defxpruebaCollection = defxpruebaCollection;
    }

   

    public SubGrupos getSubGrupos() {
        return subGrupos;
    }

    public void setSubGrupos(SubGrupos subGrupos) {
        this.subGrupos = subGrupos;
    }

    public Collection<Defxplaca> getDefxplacaCollection() {
        return defxplacaCollection;
    }

    public void setDefxplacaCollection(Collection<Defxplaca> defxplacaCollection) {
        this.defxplacaCollection = defxplacaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cardefault != null ? cardefault.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Defectos)) {
            return false;
        }
        Defectos other = (Defectos) object;
        return (this.cardefault != null || other.cardefault == null) && (this.cardefault == null || this.cardefault.equals(other.cardefault));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Defectos[cardefault=" + cardefault + "]";
    }

}
