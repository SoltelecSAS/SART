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
@Table(name = "consolas")
@NamedQueries({
    @NamedQuery(name = "Consolas.findAll", query = "SELECT c FROM Consolas c"),
    @NamedQuery(name = "Consolas.findByConsole", query = "SELECT c FROM Consolas c WHERE c.consolasPK.console = :console"),
    @NamedQuery(name = "Consolas.findByIp", query = "SELECT c FROM Consolas c WHERE c.ip = :ip"),
    @NamedQuery(name = "Consolas.findByRoad", query = "SELECT c FROM Consolas c WHERE c.consolasPK.road = :road"),
    @NamedQuery(name = "Consolas.findByDescripcion", query = "SELECT c FROM Consolas c WHERE c.descripcion = :descripcion")})
public class Consolas implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ConsolasPK consolasPK;
    @Column(name = "IP")
    private String ip;
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @JoinColumn(name = "ROAD", referencedColumnName = "ROAD", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Roads roads;

    public Consolas() {
    }

    public Consolas(ConsolasPK consolasPK) {
        this.consolasPK = consolasPK;
    }

    public Consolas(int console, int road) {
        this.consolasPK = new ConsolasPK(console, road);
    }

    public ConsolasPK getConsolasPK() {
        return consolasPK;
    }

    public void setConsolasPK(ConsolasPK consolasPK) {
        this.consolasPK = consolasPK;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Roads getRoads() {
        return roads;
    }

    public void setRoads(Roads roads) {
        this.roads = roads;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (consolasPK != null ? consolasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Consolas)) {
            return false;
        }
        Consolas other = (Consolas) object;
        return (this.consolasPK != null || other.consolasPK == null) && (this.consolasPK == null || this.consolasPK.equals(other.consolasPK));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Consolas[consolasPK=" + consolasPK + "]";
    }

}
