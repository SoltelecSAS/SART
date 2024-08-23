/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "equipments")
@NamedQueries({
    @NamedQuery(name = "Equipments.findAll", query = "SELECT e FROM Equipments e"),
    @NamedQuery(name = "Equipments.findByEquipment", query = "SELECT e FROM Equipments e WHERE e.equipment = :equipment"),
    @NamedQuery(name = "Equipments.findByConsole", query = "SELECT e FROM Equipments e WHERE e.console = :console"),
    @NamedQuery(name = "Equipments.findByRoad", query = "SELECT e FROM Equipments e WHERE e.road = :road"),
    @NamedQuery(name = "Equipments.findByEqtype", query = "SELECT e FROM Equipments e WHERE e.eqtype = :eqtype")})
public class Equipments implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "EQUIPMENT")
    private Integer equipment;
    @Basic(optional = false)
    @Column(name = "CONSOLE")
    private int console;
    @Basic(optional = false)
    @Column(name = "ROAD")
    private short road;
    @Basic(optional = false)
    @Column(name = "EQTYPE")
    private String eqtype;

    public Equipments() {
    }

    public Equipments(Integer equipment) {
        this.equipment = equipment;
    }

    public Equipments(Integer equipment, int console, short road, String eqtype) {
        this.equipment = equipment;
        this.console = console;
        this.road = road;
        this.eqtype = eqtype;
    }

    public Integer getEquipment() {
        return equipment;
    }

    public void setEquipment(Integer equipment) {
        this.equipment = equipment;
    }

    public int getConsole() {
        return console;
    }

    public void setConsole(int console) {
        this.console = console;
    }

    public short getRoad() {
        return road;
    }

    public void setRoad(short road) {
        this.road = road;
    }

    public String getEqtype() {
        return eqtype;
    }

    public void setEqtype(String eqtype) {
        this.eqtype = eqtype;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipment != null ? equipment.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipments)) {
            return false;
        }
        Equipments other = (Equipments) object;
        return (this.equipment != null || other.equipment == null) && (this.equipment == null || this.equipment.equals(other.equipment));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Equipments[equipment=" + equipment + "]";
    }

}
