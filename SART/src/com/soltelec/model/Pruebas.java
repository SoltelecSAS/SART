/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
/**
 *
 * @author GerenciaDesarrollo
 */
@Entity
@Table(name = "pruebas")
@NamedQueries({
    @NamedQuery(name = "Pruebas.findAll", query = "SELECT p FROM Pruebas p"),
    @NamedQuery(name = "Pruebas.findByIdPruebas", query = "SELECT p FROM Pruebas p WHERE p.idPruebas = :idPruebas"),
    @NamedQuery(name = "Pruebas.findByFechaprueba", query = "SELECT p FROM Pruebas p WHERE p.fechaprueba = :fechaprueba"),
    @NamedQuery(name = "Pruebas.findByAutorizada", query = "SELECT p FROM Pruebas p WHERE p.autorizada = :autorizada"),
    @NamedQuery(name = "Pruebas.findByAprobada", query = "SELECT p FROM Pruebas p WHERE p.aprobada = :aprobada"),
    @NamedQuery(name = "Pruebas.findByFinalizada", query = "SELECT p FROM Pruebas p WHERE p.finalizada = :finalizada"),
    @NamedQuery(name = "Pruebas.findByAbortada", query = "SELECT p FROM Pruebas p WHERE p.abortada = :abortada"),
    @NamedQuery(name = "Pruebas.findByFechaaborto", query = "SELECT p FROM Pruebas p WHERE p.fechaaborto = :fechaaborto"),
    @NamedQuery(name = "Pruebas.findByComentarioaborto", query = "SELECT p FROM Pruebas p WHERE p.comentarioaborto = :comentarioaborto"),
    @NamedQuery(name = "Pruebas.findByObservaciones", query = "SELECT p FROM Pruebas p WHERE p.observaciones = :observaciones"),
    @NamedQuery(name = "Pruebas.findByPista", query = "SELECT p FROM Pruebas p WHERE p.pista = :pista")
})
public class Pruebas implements Serializable {    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    @Column(name = "Id_Pruebas")
    private Integer idPruebas;
    @Basic(optional = false)
    @Column(name = "Fecha_prueba")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaprueba;
    @Column(name = "Fecha_final")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaFinal;
    @Column(name = "Autorizada")
    private String autorizada;
    @Column(name = "Aprobada")
    private String aprobada;
    @Column(name = "serialEquipo")
    private String serialEquipo;
    @Column(name = "Finalizada")
    private String finalizada;
    @Column(name = "Abortada")
    private String abortada;
    @Column(name = "Fecha_aborto")
    private String fechaaborto;
    @Column(name = "Comentario_aborto")
    private String comentarioaborto;
    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "Pista")
    private Short pista;
    @JoinColumn(name = "hoja_pruebas_for")
    private HojaPruebas hojaPruebas;
    @JoinColumn(name = "usuario_for", referencedColumnName = "GEUSER")
    @ManyToOne
    private Usuarios usuarios;
    @JoinColumn(name = "Tipo_prueba_for", referencedColumnName = "TESTTYPE")
    @ManyToOne(optional = false)
    private TipoPrueba tipoPrueba;   
     @OneToMany(cascade = CascadeType.ALL, mappedBy = "pruebas")
    private List<Defxprueba> defxpruebaList;

    public Pruebas() {
    }

    public Pruebas(Integer idPruebas) {
        this.idPruebas = idPruebas;
    }

    public Pruebas(Integer idPruebas, Date fechaprueba) {
        this.idPruebas = idPruebas;
        this.fechaprueba = fechaprueba;
    }

    public Integer getIdPruebas() {
        return idPruebas;
    }

    public void setIdPruebas(Integer idPruebas) {
        this.idPruebas = idPruebas;
    }

    public Date getFechaprueba() {
        return fechaprueba;
    }

    public void setFechaprueba(Date fechaprueba) {
        this.fechaprueba = fechaprueba;
    }

    public String getAutorizada() {
        return autorizada;
    }

    public void setAutorizada(String autorizada) {
        this.autorizada = autorizada;
    }

    public String getAprobada() {
        return aprobada;
    }

    public void setAprobada(String aprobada) {
        this.aprobada = aprobada;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(String finalizada) {
        this.finalizada = finalizada;
    }

    public String getSerialEquipo() {
        return serialEquipo;
    }

    public void setSerialEquipo(String serialEquipo) {
        this.serialEquipo = serialEquipo;
    }

    public String getAbortada() {
        return abortada;
    }

    public void setAbortada(String abortada) {
        this.abortada = abortada;
    }

    public String getFechaaborto() {
        return fechaaborto;
    }

    public void setFechaaborto(String fechaaborto) {
        this.fechaaborto = fechaaborto;
    }

    public String getComentarioaborto() {
        return comentarioaborto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public void setComentarioaborto(String comentarioaborto) {
        this.comentarioaborto = comentarioaborto;
    }

    public Short getPista() {
        return pista;
    }

    public void setPista(Short pista) {
        this.pista = pista;
    }

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }
    
    public List<Defxprueba> getDefxpruebaList() {
        return defxpruebaList;
    }

    public void setDefxpruebaList(List<Defxprueba> defxpruebaList) {
        this.defxpruebaList = defxpruebaList;
    }

    public TipoPrueba getTipoPrueba() {
        return tipoPrueba;
    }

    public void setTipoPrueba(TipoPrueba tipoPrueba) {
        this.tipoPrueba = tipoPrueba;
    }

    public HojaPruebas getHojaPruebas() {
        return hojaPruebas;
    }

    public void setHojaPruebas(HojaPruebas hojaPruebas) {
        this.hojaPruebas = hojaPruebas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPruebas != null ? idPruebas.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pruebas)) {
            return false;
        }
        Pruebas other = (Pruebas) object;
        return (this.idPruebas != null || other.idPruebas == null) && (this.idPruebas == null || this.idPruebas.equals(other.idPruebas));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Pruebas[idPruebas=" + idPruebas + "]";
    }

}
