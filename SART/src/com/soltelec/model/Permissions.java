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
@Table(name = "permissions")
@NamedQueries({
    @NamedQuery(name = "Permissions.findAll", query = "SELECT p FROM Permissions p"),
    @NamedQuery(name = "Permissions.findByPermission", query = "SELECT p FROM Permissions p WHERE p.permission = :permission"),
    @NamedQuery(name = "Permissions.findByReadaccess", query = "SELECT p FROM Permissions p WHERE p.readaccess = :readaccess"),
    @NamedQuery(name = "Permissions.findByUpdateaccess", query = "SELECT p FROM Permissions p WHERE p.updateaccess = :updateaccess"),
    @NamedQuery(name = "Permissions.findByCreateaccess", query = "SELECT p FROM Permissions p WHERE p.createaccess = :createaccess"),
    @NamedQuery(name = "Permissions.findByExecuteaccess", query = "SELECT p FROM Permissions p WHERE p.executeaccess = :executeaccess"),
    @NamedQuery(name = "Permissions.findByDeleteaccess", query = "SELECT p FROM Permissions p WHERE p.deleteaccess = :deleteaccess"),
    @NamedQuery(name = "Permissions.findByWriteaccess", query = "SELECT p FROM Permissions p WHERE p.writeaccess = :writeaccess")})
public class Permissions implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "PERMISSION")
    private Integer permission;
    @Column(name = "READACCESS")
    private String readaccess;
    @Column(name = "UPDATEACCESS")
    private String updateaccess;
    @Column(name = "CREATEACCESS")
    private String createaccess;
    @Column(name = "EXECUTEACCESS")
    private String executeaccess;
    @Column(name = "DELETEACCESS")
    private String deleteaccess;
    @Column(name = "WRITEACCESS")
    private String writeaccess;
    @JoinColumn(name = "GEUSER", referencedColumnName = "GEUSER")
    @ManyToOne(optional = false)
    private Usuarios usuarios;
    @JoinColumn(name = "ENTITY", referencedColumnName = "ENTITY")
    @ManyToOne(optional = false)
    private Entities entities;

    public Permissions() {
    }

    public Permissions(Integer permission) {
        this.permission = permission;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public String getReadaccess() {
        return readaccess;
    }

    public void setReadaccess(String readaccess) {
        this.readaccess = readaccess;
    }

    public String getUpdateaccess() {
        return updateaccess;
    }

    public void setUpdateaccess(String updateaccess) {
        this.updateaccess = updateaccess;
    }

    public String getCreateaccess() {
        return createaccess;
    }

    public void setCreateaccess(String createaccess) {
        this.createaccess = createaccess;
    }

    public String getExecuteaccess() {
        return executeaccess;
    }

    public void setExecuteaccess(String executeaccess) {
        this.executeaccess = executeaccess;
    }

    public String getDeleteaccess() {
        return deleteaccess;
    }

    public void setDeleteaccess(String deleteaccess) {
        this.deleteaccess = deleteaccess;
    }

    public String getWriteaccess() {
        return writeaccess;
    }

    public void setWriteaccess(String writeaccess) {
        this.writeaccess = writeaccess;
    }

    public Usuarios getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (permission != null ? permission.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Permissions)) {
            return false;
        }
        Permissions other = (Permissions) object;
        return (this.permission != null || other.permission == null) && (this.permission == null || this.permission.equals(other.permission));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Permissions[permission=" + permission + "]";
    }

}
