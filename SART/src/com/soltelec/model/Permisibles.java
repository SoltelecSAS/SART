/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "permisibles")
@NamedQueries({
    @NamedQuery(name = "Permisibles.findAll", query = "SELECT p FROM Permisibles p"),
    @NamedQuery(name = "Permisibles.findByIdpermisible", query = "SELECT p FROM Permisibles p WHERE p.idpermisible = :idpermisible"),
    @NamedQuery(name = "Permisibles.findByValorminimo", query = "SELECT p FROM Permisibles p WHERE p.valorminimo = :valorminimo"),
    @NamedQuery(name = "Permisibles.findByValormaximo", query = "SELECT p FROM Permisibles p WHERE p.valormaximo = :valormaximo"),
    @NamedQuery(name = "Permisibles.findByNotin", query = "SELECT p FROM Permisibles p WHERE p.notin = :notin"),
    @NamedQuery(name = "Permisibles.findByCondicionalminimo", query = "SELECT p FROM Permisibles p WHERE p.condicionalminimo = :condicionalminimo"),
    @NamedQuery(name = "Permisibles.findByCondicionalmaximo", query = "SELECT p FROM Permisibles p WHERE p.condicionalmaximo = :condicionalmaximo"),
    @NamedQuery(name = "Permisibles.findByDescripciooncondicion", query = "SELECT p FROM Permisibles p WHERE p.descripciooncondicion = :descripciooncondicion")})
public class Permisibles implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id_permisible")
    private Integer idpermisible;
    @Column(name = "Valor_minimo")
    private Float valorminimo;
    @Column(name = "Valor_maximo")
    private Float valormaximo;
    @Column(name = "NOTIN")
    private String notin;
    @Column(name = "Condicional_minimo")
    private Float condicionalminimo;
    @Column(name = "Condicional_maximo")
    private Float condicionalmaximo;
    @Column(name = "Descripcioon_condicion")
    private String descripciooncondicion;
    @JoinColumn(name = "Tipos_medida_for", referencedColumnName = "MEASURETYPE")
    @ManyToOne
    private TiposMedida tiposMedida;
    @JoinColumn(name = "CARTYPE", referencedColumnName = "CARTYPE")
    @ManyToOne(optional = false)
    private TipoVehiculo tipoVehiculo;
    @JoinColumn(name = "CARDEFAULT", referencedColumnName = "CARDEFAULT")
    @ManyToOne(optional = false)
    private Defectos defectos;

    public Permisibles() {
    }

    public Permisibles(Integer idpermisible) {
        this.idpermisible = idpermisible;
    }

    public Integer getIdpermisible() {
        return idpermisible;
    }

    public void setIdpermisible(Integer idpermisible) {
        this.idpermisible = idpermisible;
    }

    public Float getValorminimo() {
        return valorminimo;
    }

    public void setValorminimo(Float valorminimo) {
        this.valorminimo = valorminimo;
    }

    public Float getValormaximo() {
        return valormaximo;
    }

    public void setValormaximo(Float valormaximo) {
        this.valormaximo = valormaximo;
    }

    public String getNotin() {
        return notin;
    }

    public void setNotin(String notin) {
        this.notin = notin;
    }

    public Float getCondicionalminimo() {
        return condicionalminimo;
    }

    public void setCondicionalminimo(Float condicionalminimo) {
        this.condicionalminimo = condicionalminimo;
    }

    public Float getCondicionalmaximo() {
        return condicionalmaximo;
    }

    public void setCondicionalmaximo(Float condicionalmaximo) {
        this.condicionalmaximo = condicionalmaximo;
    }

    public String getDescripciooncondicion() {
        return descripciooncondicion;
    }

    public void setDescripciooncondicion(String descripciooncondicion) {
        this.descripciooncondicion = descripciooncondicion;
    }

    public TiposMedida getTiposMedida() {
        return tiposMedida;
    }

    public void setTiposMedida(TiposMedida tiposMedida) {
        this.tiposMedida = tiposMedida;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
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
        hash += (idpermisible != null ? idpermisible.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Permisibles)) {
            return false;
        }
        Permisibles other = (Permisibles) object;
        return (this.idpermisible != null || other.idpermisible == null) && (this.idpermisible == null || this.idpermisible.equals(other.idpermisible));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Permisibles[idpermisible=" + idpermisible + "]";
    }

}
