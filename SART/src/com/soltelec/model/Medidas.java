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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "medidas")
@NamedQueries({
    @NamedQuery(name = "Medidas.findAll", query = "SELECT m FROM Medidas m"),
    @NamedQuery(name = "Medidas.findByMeasure", query = "SELECT m FROM Medidas m WHERE m.measure = :measure"),
    @NamedQuery(name = "Medidas.findByValormedida", query = "SELECT m FROM Medidas m WHERE m.valormedida = :valormedida"),
    @NamedQuery(name = "Medidas.findByCondicion", query = "SELECT m FROM Medidas m WHERE m.condicion = :condicion")})
public class Medidas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "MEASURE")
    private Integer measure;
    @Column(name = "Valor_medida")
    private Float valormedida;
    @Column(name = "Condicion")
    private Float condicion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medidas")
    private Collection<Defectoxmedida> defectoxmedidaCollection;
    @JoinColumn(name = "TEST", referencedColumnName = "Id_Pruebas")
    @ManyToOne(optional = false)
    private Pruebas pruebas;
    @JoinColumn(name = "MEASURETYPE", referencedColumnName = "MEASURETYPE")
    @ManyToOne(optional = false)
    private TiposMedida tiposMedida;

    public Medidas() {
    }

    public Medidas(Integer measure) {
        this.measure = measure;
    }

    public Integer getMeasure() {
        return measure;
    }

    public void setMeasure(Integer measure) {
        this.measure = measure;
    }

    public Float getValormedida() {
        return valormedida;
    }

    public void setValormedida(Float valormedida) {
        this.valormedida = valormedida;
    }

    public Float getCondicion() {
        return condicion;
    }

    public void setCondicion(Float condicion) {
        this.condicion = condicion;
    }

    public Collection<Defectoxmedida> getDefectoxmedidaCollection() {
        return defectoxmedidaCollection;
    }

    public void setDefectoxmedidaCollection(Collection<Defectoxmedida> defectoxmedidaCollection) {
        this.defectoxmedidaCollection = defectoxmedidaCollection;
    }

    public Pruebas getPruebas() {
        return pruebas;
    }

    public void setPruebas(Pruebas pruebas) {
        this.pruebas = pruebas;
    }

    public TiposMedida getTiposMedida() {
        return tiposMedida;
    }

    public void setTiposMedida(TiposMedida tiposMedida) {
        this.tiposMedida = tiposMedida;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (measure != null ? measure.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Medidas)) {
            return false;
        }
        Medidas other = (Medidas) object;
        return (this.measure != null || other.measure == null) && (this.measure == null || this.measure.equals(other.measure));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Medidas[measure=" + measure + "]";
    }

}
