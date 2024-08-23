/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Column;
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
@Table(name = "defectoxmedida")
@NamedQueries({
    @NamedQuery(name = "Defectoxmedida.findAll", query = "SELECT d FROM Defectoxmedida d"),
    @NamedQuery(name = "Defectoxmedida.findByCardefault", query = "SELECT d FROM Defectoxmedida d WHERE d.defectoxmedidaPK.cardefault = :cardefault"),
    @NamedQuery(name = "Defectoxmedida.findByMeasure", query = "SELECT d FROM Defectoxmedida d WHERE d.defectoxmedidaPK.measure = :measure"),
    @NamedQuery(name = "Defectoxmedida.findByGroupok", query = "SELECT d FROM Defectoxmedida d WHERE d.groupok = :groupok")})
public class Defectoxmedida implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DefectoxmedidaPK defectoxmedidaPK;
    @Column(name = "GROUPOK")
    private String groupok;
    @JoinColumn(name = "MEASURE", referencedColumnName = "MEASURE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Medidas medidas;
    @JoinColumn(name = "CARDEFAULT", referencedColumnName = "CARDEFAULT", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Defectos defectos;

    public Defectoxmedida() {
    }

    public Defectoxmedida(DefectoxmedidaPK defectoxmedidaPK) {
        this.defectoxmedidaPK = defectoxmedidaPK;
    }

    public Defectoxmedida(int cardefault, int measure) {
        this.defectoxmedidaPK = new DefectoxmedidaPK(cardefault, measure);
    }

    public DefectoxmedidaPK getDefectoxmedidaPK() {
        return defectoxmedidaPK;
    }

    public void setDefectoxmedidaPK(DefectoxmedidaPK defectoxmedidaPK) {
        this.defectoxmedidaPK = defectoxmedidaPK;
    }

    public String getGroupok() {
        return groupok;
    }

    public void setGroupok(String groupok) {
        this.groupok = groupok;
    }

    public Medidas getMedidas() {
        return medidas;
    }

    public void setMedidas(Medidas medidas) {
        this.medidas = medidas;
    }

    public Defectos getDefectos() {
        return defectos;
    }

    public void setDefectos(Defectos defectos) {
        this.defectos = defectos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (defectoxmedidaPK != null ? defectoxmedidaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Defectoxmedida)) {
            return false;
        }
        Defectoxmedida other = (Defectoxmedida) object;
        return (this.defectoxmedidaPK != null || other.defectoxmedidaPK == null) && (this.defectoxmedidaPK == null || this.defectoxmedidaPK.equals(other.defectoxmedidaPK));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Defectoxmedida[defectoxmedidaPK=" + defectoxmedidaPK + "]";
    }

}
