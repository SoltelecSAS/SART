/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.eventos.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author SOLTELEC
 */
@Entity
@Table(name = "evento_cda")
@NamedQueries({
    @NamedQuery(name = "EventoCda.findAll", query = "SELECT e FROM EventoCda e"),
    @NamedQuery(name = "EventoCda.findByIdEvento", query = "SELECT e FROM EventoCda e WHERE e.idEvento = :idEvento"),
    @NamedQuery(name = "EventoCda.findByDescripcion", query = "SELECT e FROM EventoCda e WHERE e.descripcion = :descripcion"),
    @NamedQuery(name = "EventoCda.findByAccion", query = "SELECT e FROM EventoCda e WHERE e.accion = :accion"),
    @NamedQuery(name = "EventoCda.findByPista", query = "SELECT e FROM EventoCda e WHERE e.pista = :pista"),
    @NamedQuery(name = "EventoCda.findByFinicial", query = "SELECT e FROM EventoCda e WHERE e.finicial = :finicial"),
    @NamedQuery(name = "EventoCda.findByFfinal", query = "SELECT e FROM EventoCda e WHERE e.ffinal = :ffinal")})
public class EventoCda implements Serializable {
   
    private static final long serialVersionUID = 1;
           
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_evento")
    private Integer idEvento;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "accion")
    private String accion;
    
    @Column(name = "finicial")
    @Temporal(TemporalType.DATE)
    private Date finicial;
    
    @Column(name = "ffinal")
    @Temporal(TemporalType.DATE)
    private Date ffinal;
    
    @Column(name = "pista")
    private String pista;
    
    @Column(name = "usuario")
    private String usuario="prueba";
    
    @JoinColumn(name = "id_estado", referencedColumnName = "ID")
    @ManyToOne(optional = false, cascade = CascadeType.MERGE) //---
    private EstadosEvento idEstado;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
   
    public EventoCda() {
    }

    public EventoCda(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public Integer getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Integer idEvento) {
        this.idEvento = idEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

//    public String getEstado() {///
//        return estado;
//    }

//    public void setEstado(String estado) {
//        this.estado = estado;
//    }
    public String getPista() {
        return pista;
    }

    public void setPista(String pista) {
        this.pista = pista;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEvento != null ? idEvento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventoCda)) {
            return false;
        }
        EventoCda other = (EventoCda) object;
        if ((this.idEvento == null && other.idEvento != null) || (this.idEvento != null && !this.idEvento.equals(other.idEvento))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.soltelec.model.EventoCda[idEvento=" + idEvento + "]";
    }

    public Date getFinicial() {
        return finicial;
    }

    public void setFinicial(Date finicial) {
        this.finicial = finicial;
    }

    public Date getFfinal() {
        return ffinal;
    }

    public void setFfinal(Date ffinal) {
        this.ffinal = ffinal;
    }

    public EstadosEvento getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(EstadosEvento idEstado) {
        this.idEstado = idEstado;
    }

}
