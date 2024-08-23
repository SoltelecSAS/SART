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

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "tipos_medida")
@NamedQueries({
    @NamedQuery(name = "TiposMedida.findAll", query = "SELECT t FROM TiposMedida t"),
    @NamedQuery(name = "TiposMedida.findByMeasuretype", query = "SELECT t FROM TiposMedida t WHERE t.measuretype = :measuretype"),
    @NamedQuery(name = "TiposMedida.findByTesttype", query = "SELECT t FROM TiposMedida t WHERE t.testtype = :testtype"),
    @NamedQuery(name = "TiposMedida.findByNombremedida", query = "SELECT t FROM TiposMedida t WHERE t.nombremedida = :nombremedida"),
    @NamedQuery(name = "TiposMedida.findByDescripcionmedida", query = "SELECT t FROM TiposMedida t WHERE t.descripcionmedida = :descripcionmedida"),
    @NamedQuery(name = "TiposMedida.findByUnidad", query = "SELECT t FROM TiposMedida t WHERE t.unidad = :unidad")})
public class TiposMedida implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "MEASURETYPE")
    private Integer measuretype;
    @Basic(optional = false)
    @Column(name = "TESTTYPE")
    private int testtype;
    @Basic(optional = false)
    @Column(name = "Nombre_medida")
    private String nombremedida;
    @Column(name = "Descripcion_medida")
    private String descripcionmedida;
    @Column(name = "Unidad")
    private String unidad;
    @OneToMany(mappedBy = "tiposMedida")
    private Collection<Permisibles> permisiblesCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tiposMedida")
    private Collection<Medidas> medidasCollection;

    public TiposMedida() {
    }

    public TiposMedida(Integer measuretype) {
        this.measuretype = measuretype;
    }

    public TiposMedida(Integer measuretype, int testtype, String nombremedida) {
        this.measuretype = measuretype;
        this.testtype = testtype;
        this.nombremedida = nombremedida;
    }

    public Integer getMeasuretype() {
        return measuretype;
    }

    public void setMeasuretype(Integer measuretype) {
        this.measuretype = measuretype;
    }

    public int getTesttype() {
        return testtype;
    }

    public void setTesttype(int testtype) {
        this.testtype = testtype;
    }

    public String getNombremedida() {
        return nombremedida;
    }

    public void setNombremedida(String nombremedida) {
        this.nombremedida = nombremedida;
    }

    public String getDescripcionmedida() {
        return descripcionmedida;
    }

    public void setDescripcionmedida(String descripcionmedida) {
        this.descripcionmedida = descripcionmedida;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Collection<Permisibles> getPermisiblesCollection() {
        return permisiblesCollection;
    }

    public void setPermisiblesCollection(Collection<Permisibles> permisiblesCollection) {
        this.permisiblesCollection = permisiblesCollection;
    }

    public Collection<Medidas> getMedidasCollection() {
        return medidasCollection;
    }

    public void setMedidasCollection(Collection<Medidas> medidasCollection) {
        this.medidasCollection = medidasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (measuretype != null ? measuretype.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TiposMedida)) {
            return false;
        }
        TiposMedida other = (TiposMedida) object;
        return (this.measuretype != null || other.measuretype == null) && (this.measuretype == null || this.measuretype.equals(other.measuretype));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.TiposMedida[measuretype=" + measuretype + "]";
    }

}
