/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Dany
 */
@Entity
@Table(name = "equipos")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Equipos.findAll", query = "SELECT e FROM Equipos e"),
    @NamedQuery(name = "Equipos.findByIdEquipo", query = "SELECT e FROM Equipos e WHERE e.idEquipo = :idEquipo"),
    @NamedQuery(name = "Equipos.findByPef", query = "SELECT e FROM Equipos e WHERE e.pef = :pef"),
    @NamedQuery(name = "Equipos.findBySerial", query = "SELECT e FROM Equipos e WHERE e.serial = :serial"),
    @NamedQuery(name = "Equipos.findByMarca", query = "SELECT e FROM Equipos e WHERE e.marca = :marca"),
    @NamedQuery(name = "Equipos.findByNumAnalizador", query = "SELECT e FROM Equipos e WHERE e.numAnalizador = :numAnalizador")})
public class Equipos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_equipo")
    private Integer idEquipo;
    @Basic(optional = false)
    @Column(name = "pef")
    private int pef;
    @Basic(optional = false)
    @Column(name = "serial")
    private String serial;
    @Column(name = "resolucionambiental")
    private String resolucionambiental;
    @Basic(optional = false)
    @Column(name = "marca")
    private String marca;
    @Basic(optional = false)
    
    
    @Column(name = "num_serial_bench")
    private String serialbench;
    @Basic(optional = false)
    
    @Column(name = "num_analizador")
    private String numAnalizador;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idEquipo")
    private List<Calibraciones> calibracionesList;

    public Equipos() {
    }

    public Equipos(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Equipos(Integer idEquipo, int pef, String serial, String marca, String numAnalizador) {
        this.idEquipo = idEquipo;
        this.pef = pef;
        this.serial = serial;
        this.marca = marca;
        this.numAnalizador = numAnalizador;
    }

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public int getPef() {
        return pef;
    }

    public void setPef(int pef) {
        this.pef = pef;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNumAnalizador() {
        return numAnalizador;
    }

    public void setNumAnalizador(String numAnalizador) {
        this.numAnalizador = numAnalizador;
    }

    @XmlTransient
    public List<Calibraciones> getCalibracionesList() {
        return calibracionesList;
    }

    public void setCalibracionesList(List<Calibraciones> calibracionesList) {
        this.calibracionesList = calibracionesList;
    }

    public String getSerialbench() {
        return serialbench;
    }

    public void setSerialbench(String serialbench) {
        this.serialbench = serialbench;
    }

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEquipo != null ? idEquipo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipos)) {
            return false;
        }
        Equipos other = (Equipos) object;
        return (this.idEquipo != null || other.idEquipo == null) && (this.idEquipo == null || this.idEquipo.equals(other.idEquipo));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Equipos[ idEquipo=" + idEquipo + " ]";
    }

    /**
     * @return the resolucionambiental
     */
    public String getResolucionambiental() {
        return resolucionambiental;
    }

    /**
     * @param resolucionambiental the resolucionambiental to set
     */
    public void setResolucionambiental(String resolucionambiental) {
        this.resolucionambiental = resolucionambiental;
    }
    
}
