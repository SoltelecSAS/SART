/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.eventos.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author User
 */
@Entity
@Table(name = "estados_evento")
@NamedQueries({
    @NamedQuery(name = "EstadosEvento.findAll", query = "SELECT e FROM EstadosEvento e")})
public class EstadosEvento implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)////----
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    
    @Column(name = "DESCRIPCION")
    private String descripcion;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idEstado")
    private List<EventoCda> eventoCdaList;

    public EstadosEvento() {
    }

    public EstadosEvento(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<EventoCda> getEventoCdaList() {
        return eventoCdaList;
    }

    public void setEventoCdaList(List<EventoCda> eventoCdaList) {
        this.eventoCdaList = eventoCdaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EstadosEvento)) {
            return false;
        }
        EstadosEvento other = (EstadosEvento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        //return "com.soltelec.model.EstadosEvento[ id=" + id + " ]";
        return String.valueOf(id);
    }
    
}
