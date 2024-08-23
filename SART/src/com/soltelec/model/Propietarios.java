/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "propietarios")
@NamedQueries({
    @NamedQuery(name = "Propietarios.findAll", query = "SELECT p FROM Propietarios p"),
    @NamedQuery(name = "Propietarios.findByCarowner", query = "SELECT p FROM Propietarios p WHERE p.carowner = :carowner"),
    @NamedQuery(name = "Propietarios.findByTipoidentificacion", query = "SELECT p FROM Propietarios p WHERE p.tipoidentificacion = :tipoidentificacion"),
    @NamedQuery(name = "Propietarios.findByApellidos", query = "SELECT p FROM Propietarios p WHERE p.apellidos = :apellidos"),
    @NamedQuery(name = "Propietarios.findByNombres", query = "SELECT p FROM Propietarios p WHERE p.nombres = :nombres"),
    @NamedQuery(name = "Propietarios.findByGeuser", query = "SELECT p FROM Propietarios p WHERE p.geuser = :geuser"),
    @NamedQuery(name = "Propietarios.findByFechaRegistro", query = "SELECT p FROM Propietarios p WHERE p.fechaRegistro = :fechaRegistro"),
    @NamedQuery(name = "Propietarios.findByNumerotelefono", query = "SELECT p FROM Propietarios p WHERE p.numerotelefono = :numerotelefono"),
    @NamedQuery(name = "Propietarios.findByDireccion", query = "SELECT p FROM Propietarios p WHERE p.direccion = :direccion"),
    @NamedQuery(name = "Propietarios.findByNumerolicencia", query = "SELECT p FROM Propietarios p WHERE p.numerolicencia = :numerolicencia"),
    @NamedQuery(name = "Propietarios.findByTipolicencia", query = "SELECT p FROM Propietarios p WHERE p.tipolicencia = :tipolicencia")})
public class Propietarios implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CAROWNER")
    private Long carowner;
    @Column(name = "Tipo_identificacion")
    private String tipoidentificacion;
    @Column(name = "Apellidos")
    private String apellidos;
    @Basic(optional = false)
    @Column(name = "Nombres")
    private String nombres;
    @Basic(optional = false)
    @Column(name = "GEUSER")
    private int geuser;
    @Basic(optional = false)
    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;
    @Basic(optional = false)
    @Column(name = "Numero_telefono")
    private String numerotelefono;
    @Basic(optional = false)
    @Column(name = "Direccion")
    private String direccion;
    @Column(name = "Numero_licencia")
    private String numerolicencia;
    @Column(name = "Tipo_licencia")
    private String tipolicencia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propietarios")
    private Collection<Vehiculos> vehiculosCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propietarios")
    private Collection<HojaPruebas> hojaPruebasCollection;

    public Propietarios() {
    }

    public Propietarios(Long carowner) {
        this.carowner = carowner;
    }

    public Propietarios(Long carowner, String nombres, int geuser, Date fechaRegistro, String numerotelefono, String direccion) {
        this.carowner = carowner;
        this.nombres = nombres;
        this.geuser = geuser;
        this.fechaRegistro = fechaRegistro;
        this.numerotelefono = numerotelefono;
        this.direccion = direccion;
    }

    public Long getCarowner() {
        return carowner;
    }

    public void setCarowner(Long carowner) {
        this.carowner = carowner;
    }

    public String getTipoidentificacion() {
        return tipoidentificacion;
    }

    public void setTipoidentificacion(String tipoidentificacion) {
        this.tipoidentificacion = tipoidentificacion;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public int getGeuser() {
        return geuser;
    }

    public void setGeuser(int geuser) {
        this.geuser = geuser;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNumerotelefono() {
        return numerotelefono;
    }

    public void setNumerotelefono(String numerotelefono) {
        this.numerotelefono = numerotelefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumerolicencia() {
        return numerolicencia;
    }

    public void setNumerolicencia(String numerolicencia) {
        this.numerolicencia = numerolicencia;
    }

    public String getTipolicencia() {
        return tipolicencia;
    }

    public void setTipolicencia(String tipolicencia) {
        this.tipolicencia = tipolicencia;
    }

    public Collection<Vehiculos> getVehiculosCollection() {
        return vehiculosCollection;
    }

    public void setVehiculosCollection(Collection<Vehiculos> vehiculosCollection) {
        this.vehiculosCollection = vehiculosCollection;
    }

    public Collection<HojaPruebas> getHojaPruebasCollection() {
        return hojaPruebasCollection;
    }

    public void setHojaPruebasCollection(Collection<HojaPruebas> hojaPruebasCollection) {
        this.hojaPruebasCollection = hojaPruebasCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carowner != null ? carowner.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Propietarios)) {
            return false;
        }
        Propietarios other = (Propietarios) object;
        return (this.carowner != null || other.carowner == null) && (this.carowner == null || this.carowner.equals(other.carowner));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Propietarios[carowner=" + carowner + "]";
    }

}
