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
@Table(name = "equipmenttypes")
@NamedQueries({
    @NamedQuery(name = "Equipmenttypes.findAll", query = "SELECT e FROM Equipmenttypes e"),
    @NamedQuery(name = "Equipmenttypes.findByEquipmentid", query = "SELECT e FROM Equipmenttypes e WHERE e.equipmentid = :equipmentid"),
    @NamedQuery(name = "Equipmenttypes.findByDescription", query = "SELECT e FROM Equipmenttypes e WHERE e.description = :description")})
public class Equipmenttypes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "EQUIPMENTID")
    private Integer equipmentid;
    @Column(name = "DESCRIPTION")
    private String description;

    public Equipmenttypes() {
    }

    public Equipmenttypes(Integer equipmentid) {
        this.equipmentid = equipmentid;
    }

    public Integer getEquipmentid() {
        return equipmentid;
    }

    public void setEquipmentid(Integer equipmentid) {
        this.equipmentid = equipmentid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (equipmentid != null ? equipmentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipmenttypes)) {
            return false;
        }
        Equipmenttypes other = (Equipmenttypes) object;
        return (this.equipmentid != null || other.equipmentid == null) && (this.equipmentid == null || this.equipmentid.equals(other.equipmentid));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Equipmenttypes[equipmentid=" + equipmentid + "]";
    }

}
