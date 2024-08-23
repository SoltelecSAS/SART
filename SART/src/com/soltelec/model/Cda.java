/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author Gerencia Desarrollo Soluciones Tecnologicas
 */
@Entity
@Table(name = "cda")
@NamedQueries({         
    @NamedQuery(name = "cda.findAplicaTrans", query = "SELECT c FROM Cda c")})
public class Cda implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cda")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @Column(name = "NIT")
    private String nit;
    @Basic(optional = false)
    @Column(name = "direccion")
    private String direccion;
    @Basic(optional = false)
    @Column(name = "telefono")
    private String telefono;
    @Basic(optional = false)
    @Column(name = "RESOLUCION")
    private String resolucion;
    @Basic(optional = false)
    @Column(name = "nom_resp_certificados")
    private String nombreResponsable;
    @Basic(optional = false)
    @Column(name = "Certificado_conformidad")
    private String certificado;
    @Basic(optional = false)
    @Column(name = "ciudad")
    private String ciudad;
    @Basic(optional=true)
    @Column(name="usuario_resp")
    private Integer responsable;
    @Basic(optional = false)
    @Column(name = "proveedor_sicov")
    private String proveedorSicov;
    @Basic(optional = false)
    @Column(name = "usuario_sicov")
    private String usuarioSicov;
    @Basic(optional = false)
    @Column(name = "url_servicio_sicov")
    private String urlServicioSicov;
     @Basic(optional = false)
    @Column(name = "url_servicio_sicov2")
    private String urlServicioSicov2;    
    @Column(name = "url_servicio_encript")
    private String urlServicioEncript;   
    @Column(name = "password_sicov")
     private String passwordSicov;
    @Basic(optional=true)
    @Column(name="id_runt")
    private Integer idRunt;    
    @Column(name="divipola")
    private Integer divipola;
     @Basic(optional=true)
    @Column(name="cont_test")
    private Integer contTest;
    
    
    public Cda(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer idCda) {
        this.id = idCda;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public Integer getContTest() {
        return contTest;
    }

    public void setContTest(Integer contTest) {
        this.contTest = contTest;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getUrlServicioSicov2() {
        return urlServicioSicov2;
    }

    public void setUrlServicioSicov2(String urlServicioSicov2) {
        this.urlServicioSicov2 = urlServicioSicov2;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getIdRunt() {
        return idRunt;
    }

    public void setIdRunt(Integer idRunt) {
        this.idRunt = idRunt;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public Integer getDivipola() {
        return divipola;
    }

    public void setDivipola(Integer divipola) {
        this.divipola = divipola;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nomRespCertificados) {
        this.nombreResponsable = nomRespCertificados;
    }

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificadoconformidad) {
        this.certificado = certificadoconformidad;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Integer getResponsable() {
        return responsable;
    }

    public void setResponsable(Integer idUsuarioResponsable) {
        this.responsable = idUsuarioResponsable;
    }

    public String getProveedorSicov() {
        return proveedorSicov;
    }

    public void setProveedorSicov(String proveedorSicov) {
        this.proveedorSicov = proveedorSicov;
    }

    public String getUsuarioSicov() {
        return usuarioSicov;
    }

    public String getPasswordSicov() {
        return passwordSicov;
    }

    public void setPasswordSicov(String passwordSicov) {
        this.passwordSicov = passwordSicov;
    }
 
    public void setUsuarioSicov(String usuarioSicov) {
        this.usuarioSicov = usuarioSicov;
    }

    public String getUrlServicioSicov() {
        return urlServicioSicov;
    }

    public void setUrlServicioSicov(String urlServicioSicov) {
        this.urlServicioSicov = urlServicioSicov;
    }

    public String getUrlServicioEncript() {
        return urlServicioEncript;
    }

    public void setUrlServicioEncript(String urlServicioEncript) {
        this.urlServicioEncript = urlServicioEncript;
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
        if (!(object instanceof Cda)) {
            return false;
        }
        Cda other = (Cda) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Cda[idCda=" + id + "]";
    }

}
