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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author GerenciaDesarrollo
 */
@Entity
@Table(name = "tipo_prueba")
@NamedQueries({
    @NamedQuery(name = "TipoPrueba.findAll", query = "SELECT t FROM TipoPrueba t"),
    @NamedQuery(name = "TipoPrueba.findByTesttype", query = "SELECT t FROM TipoPrueba t WHERE t.testtype = :testtype"),
    @NamedQuery(name = "TipoPrueba.findByNombretipoprueba", query = "SELECT t FROM TipoPrueba t WHERE t.nombretipoprueba = :nombretipoprueba"),
    @NamedQuery(name = "TipoPrueba.findByDescripciontipoprueba", query = "SELECT t FROM TipoPrueba t WHERE t.descripciontipoprueba = :descripciontipoprueba")})
public class TipoPrueba implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "testtype")
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "TESTTYPE")
    private Integer testtype;
    @Basic(optional = false)
    @Column(name = "Nombre_tipo_prueba")
    private String nombretipoprueba;
    @Basic(optional = false)
    @Column(name = "Cod_evento_sicov")
    private Integer CodEventoSicov;  
    @Basic(optional = false)
    @Column(name = "Descripcion_tipo_prueba")
    private String descripciontipoprueba;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tipoPrueba")
    private Collection<Pruebas> pruebasCollection;

    public TipoPrueba() {
    }

    public TipoPrueba(Integer testtype) {
        this.testtype = testtype;
    }

    public TipoPrueba(Integer testtype, String nombretipoprueba, String descripciontipoprueba) {
        this.testtype = testtype;
        this.nombretipoprueba = nombretipoprueba;
        this.descripciontipoprueba = descripciontipoprueba;
    }

    public Integer getTesttype() {
        return testtype;
    }

    public void setTesttype(Integer testtype) {
        this.testtype = testtype;
    }

    public String getNombretipoprueba() {
        return nombretipoprueba;
    }

    public Integer getCodEventoSicov() {
        return CodEventoSicov;
    }

    public void setCodEventoSicov(Integer CodEventoSicov) {
        this.CodEventoSicov = CodEventoSicov;
    }

    public void setNombretipoprueba(String nombretipoprueba) {
        this.nombretipoprueba = nombretipoprueba;
    }

    public String getDescripciontipoprueba() {
        return descripciontipoprueba;
    }

    public void setDescripciontipoprueba(String descripciontipoprueba) {
        this.descripciontipoprueba = descripciontipoprueba;
    }

    public Collection<Pruebas> getPruebasCollection() {
        return pruebasCollection;
    }

    public void setPruebasCollection(Collection<Pruebas> pruebasCollection) {
        this.pruebasCollection = pruebasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (testtype != null ? testtype.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoPrueba)) {
            return false;
        }
        TipoPrueba other = (TipoPrueba) object;
        return (this.testtype != null || other.testtype == null) && (this.testtype == null || this.testtype.equals(other.testtype));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.TipoPrueba[testtype=" + testtype + "]";
    }


}
