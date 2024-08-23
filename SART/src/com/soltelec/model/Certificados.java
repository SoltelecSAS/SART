/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "certificados")
@NamedQueries({
    @NamedQuery(name = "Certificados.findAll", query = "SELECT c FROM Certificados c"),
    @NamedQuery(name = "Certificados.findByCertificate", query = "SELECT c FROM Certificados c WHERE c.certificate = :certificate"),
    @NamedQuery(name = "Certificados.findByConsecutive", query = "SELECT c FROM Certificados c WHERE c.consecutive = :consecutive"),
    @NamedQuery(name = "Certificados.findByCerttype", query = "SELECT c FROM Certificados c WHERE c.certtype = :certtype"),
    @NamedQuery(name = "Certificados.findByAnuled", query = "SELECT c FROM Certificados c WHERE c.anuled = :anuled"),
    @NamedQuery(name = "Certificados.findByAnuledcomment", query = "SELECT c FROM Certificados c WHERE c.anuledcomment = :anuledcomment"),
    @NamedQuery(name = "Certificados.findByPrinted", query = "SELECT c FROM Certificados c WHERE c.printed = :printed"),
    @NamedQuery(name = "Certificados.findByTestsheet", query = "SELECT c FROM Certificados c WHERE c.testsheet = :testsheet"),
    @NamedQuery(name = "Certificados.findByPrintdate", query = "SELECT c FROM Certificados c WHERE c.printdate = :printdate"),
    @NamedQuery(name = "Certificados.findByAnuledate", query = "SELECT c FROM Certificados c WHERE c.anuledate = :anuledate")})
public class Certificados implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CERTIFICATE")
    private Integer certificate;
    @Column(name = "CONSECUTIVE")
    private String consecutive;
    @Basic(optional = false)
    @Column(name = "CERTTYPE")
    private String certtype;
    @Column(name = "ANULED")
    private String anuled;
    @Column(name = "ANULEDCOMMENT")
    private String anuledcomment;
    @Column(name = "PRINTED")
    private String printed;
    @Basic(optional = false)
    @Column(name = "TESTSHEET")
    private int testsheet;
    @Basic(optional = false)
    @Column(name = "PRINTDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date printdate;
    @Basic(optional = false)
    @Column(name = "ANULEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date anuledate;

    public Certificados() {
    }

    public Certificados(Integer certificate) {
        this.certificate = certificate;
    }

    public Certificados(Integer certificate, String certtype, int testsheet, Date printdate, Date anuledate) {
        this.certificate = certificate;
        this.certtype = certtype;
        this.testsheet = testsheet;
        this.printdate = printdate;
        this.anuledate = anuledate;
    }

    public Integer getCertificate() {
        return certificate;
    }

    public void setCertificate(Integer certificate) {
        this.certificate = certificate;
    }

    public String getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(String consecutive) {
        this.consecutive = consecutive;
    }

    public String getCerttype() {
        return certtype;
    }

    public void setCerttype(String certtype) {
        this.certtype = certtype;
    }

    public String getAnuled() {
        return anuled;
    }

    public void setAnuled(String anuled) {
        this.anuled = anuled;
    }

    public String getAnuledcomment() {
        return anuledcomment;
    }

    public void setAnuledcomment(String anuledcomment) {
        this.anuledcomment = anuledcomment;
    }

    public String getPrinted() {
        return printed;
    }

    public void setPrinted(String printed) {
        this.printed = printed;
    }

    public int getTestsheet() {
        return testsheet;
    }

    public void setTestsheet(int testsheet) {
        this.testsheet = testsheet;
    }

    public Date getPrintdate() {
        return printdate;
    }

    public void setPrintdate(Date printdate) {
        this.printdate = printdate;
    }

    public Date getAnuledate() {
        return anuledate;
    }

    public void setAnuledate(Date anuledate) {
        this.anuledate = anuledate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (certificate != null ? certificate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Certificados)) {
            return false;
        }
        Certificados other = (Certificados) object;
        return (this.certificate != null || other.certificate == null) && (this.certificate == null || this.certificate.equals(other.certificate));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Certificados[certificate=" + certificate + "]";
    }

}
