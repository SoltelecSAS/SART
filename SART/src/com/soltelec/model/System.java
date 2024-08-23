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
@Table(name = "system")
@NamedQueries({
    @NamedQuery(name = "System.findAll", query = "SELECT s FROM System s"),
    @NamedQuery(name = "System.findById", query = "SELECT s FROM System s WHERE s.id = :id"),
    @NamedQuery(name = "System.findByGeuser", query = "SELECT s FROM System s WHERE s.geuser = :geuser"),
    @NamedQuery(name = "System.findByRoad", query = "SELECT s FROM System s WHERE s.road = :road"),
    @NamedQuery(name = "System.findByConsole", query = "SELECT s FROM System s WHERE s.console = :console"),
    @NamedQuery(name = "System.findByErrorDescription", query = "SELECT s FROM System s WHERE s.errorDescription = :errorDescription"),
    @NamedQuery(name = "System.findByErrorAction", query = "SELECT s FROM System s WHERE s.errorAction = :errorAction"),
    @NamedQuery(name = "System.findByStartTime", query = "SELECT s FROM System s WHERE s.startTime = :startTime"),
    @NamedQuery(name = "System.findByStopTime", query = "SELECT s FROM System s WHERE s.stopTime = :stopTime")})
public class System implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "GEUSER")
    private Integer geuser;
    @Column(name = "ROAD")
    private Integer road;
    @Column(name = "CONSOLE")
    private Integer console;
    @Column(name = "ERROR_DESCRIPTION")
    private String errorDescription;
    @Column(name = "ERROR_ACTION")
    private String errorAction;
    @Basic(optional = false)
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Basic(optional = false)
    @Column(name = "STOP_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopTime;

    public System() {
    }

    public System(Integer id) {
        this.id = id;
    }

    public System(Integer id, Date startTime, Date stopTime) {
        this.id = id;
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGeuser() {
        return geuser;
    }

    public void setGeuser(Integer geuser) {
        this.geuser = geuser;
    }

    public Integer getRoad() {
        return road;
    }

    public void setRoad(Integer road) {
        this.road = road;
    }

    public Integer getConsole() {
        return console;
    }

    public void setConsole(Integer console) {
        this.console = console;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorAction() {
        return errorAction;
    }

    public void setErrorAction(String errorAction) {
        this.errorAction = errorAction;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
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
        if (!(object instanceof System)) {
            return false;
        }
        System other = (System) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.System[id=" + id + "]";
    }

}
