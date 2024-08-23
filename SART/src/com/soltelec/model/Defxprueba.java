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
 * @author GerenciaDesarollo
 */
@Entity
@Table(name = "defxprueba")
@NamedQueries({
    @NamedQuery(name = "Defxprueba.findAll", query = "SELECT d FROM Defxprueba d"),
    @NamedQuery(name = "Defxprueba.findByIdDefecto", query = "SELECT d FROM Defxprueba d WHERE d.defxpruebaPK.idDefecto = :idDefecto"),
    @NamedQuery(name = "Defxprueba.findByIdPrueba", query = "SELECT d FROM Defxprueba d WHERE d.defxpruebaPK.idPrueba = :idPrueba") })
public class Defxprueba implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DefxpruebaPK defxpruebaPK;
    @Column(name = "Tipo_Defecto")
    private String tipoDefecto;
    @JoinColumn(name = "id_defecto", referencedColumnName = "CARDEFAULT", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Defectos defectos;
    @JoinColumn(name = "id_prueba", referencedColumnName = "Id_Pruebas", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Pruebas pruebas;

    public Defxprueba() {
    }

    public Defxprueba(DefxpruebaPK defxpruebaPK) {
        this.defxpruebaPK = defxpruebaPK;
    }

    public Defxprueba(int idDefecto, int idPrueba, int idHojapruebaFor) {
        this.defxpruebaPK = new DefxpruebaPK(idDefecto, idPrueba);
    }

    public DefxpruebaPK getDefxpruebaPK() {
        return defxpruebaPK;
    }

    public void setDefxpruebaPK(DefxpruebaPK defxpruebaPK) {
        this.defxpruebaPK = defxpruebaPK;
    }

    public Pruebas getPruebas() {
        return pruebas;
    }

    public void setPruebas(Pruebas pruebas) {
        this.pruebas = pruebas;
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
        hash += (defxpruebaPK != null ? defxpruebaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Defxprueba)) {
            return false;
        }
        Defxprueba other = (Defxprueba) object;
        return (this.defxpruebaPK != null || other.defxpruebaPK == null) && (this.defxpruebaPK == null || this.defxpruebaPK.equals(other.defxpruebaPK));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Defxprueba[defxpruebaPK=" + defxpruebaPK + "]";
    }
}