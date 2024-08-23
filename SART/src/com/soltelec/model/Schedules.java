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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "schedules")
@NamedQueries({
    @NamedQuery(name = "Schedules.findAll", query = "SELECT s FROM Schedules s"),
    @NamedQuery(name = "Schedules.findBySchedule", query = "SELECT s FROM Schedules s WHERE s.schedule = :schedule"),
    @NamedQuery(name = "Schedules.findByPeriodicity", query = "SELECT s FROM Schedules s WHERE s.periodicity = :periodicity"),
    @NamedQuery(name = "Schedules.findByTasktime", query = "SELECT s FROM Schedules s WHERE s.tasktime = :tasktime"),
    @NamedQuery(name = "Schedules.findByActiveflag", query = "SELECT s FROM Schedules s WHERE s.activeflag = :activeflag"),
    @NamedQuery(name = "Schedules.findByPerioddays", query = "SELECT s FROM Schedules s WHERE s.perioddays = :perioddays"),
    @NamedQuery(name = "Schedules.findByLasttime", query = "SELECT s FROM Schedules s WHERE s.lasttime = :lasttime"),
    @NamedQuery(name = "Schedules.findByNexttime", query = "SELECT s FROM Schedules s WHERE s.nexttime = :nexttime"),
    @NamedQuery(name = "Schedules.findByEveryxn", query = "SELECT s FROM Schedules s WHERE s.everyxn = :everyxn"),
    @NamedQuery(name = "Schedules.findByEquipment", query = "SELECT s FROM Schedules s WHERE s.equipment = :equipment")})
public class Schedules implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "SCHEDULE")
    private Integer schedule;
    @Column(name = "PERIODICITY")
    private String periodicity;
    @Basic(optional = false)
    @Column(name = "TASKTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tasktime;
    @Column(name = "ACTIVEFLAG")
    private String activeflag;
    @Column(name = "PERIODDAYS")
    private Integer perioddays;
    @Basic(optional = false)
    @Column(name = "LASTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lasttime;
    @Basic(optional = false)
    @Column(name = "NEXTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nexttime;
    @Column(name = "EVERYXN")
    private String everyxn;
    @Lob
    @Column(name = "DESCRIPTION")
    private byte[] description;
    @Column(name = "EQUIPMENT")
    private Integer equipment;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "schedules")
    private Pendingtasks pendingtasks;
    @JoinColumn(name = "TASK", referencedColumnName = "TASK")
    @ManyToOne(optional = false)
    private Tasks tasks;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "schedules")
    private Collection<Taskhistory> taskhistoryCollection;

    public Schedules() {
    }

    public Schedules(Integer schedule) {
        this.schedule = schedule;
    }

    public Schedules(Integer schedule, Date tasktime, Date lasttime, Date nexttime) {
        this.schedule = schedule;
        this.tasktime = tasktime;
        this.lasttime = lasttime;
        this.nexttime = nexttime;
    }

    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        this.schedule = schedule;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public Date getTasktime() {
        return tasktime;
    }

    public void setTasktime(Date tasktime) {
        this.tasktime = tasktime;
    }

    public String getActiveflag() {
        return activeflag;
    }

    public void setActiveflag(String activeflag) {
        this.activeflag = activeflag;
    }

    public Integer getPerioddays() {
        return perioddays;
    }

    public void setPerioddays(Integer perioddays) {
        this.perioddays = perioddays;
    }

    public Date getLasttime() {
        return lasttime;
    }

    public void setLasttime(Date lasttime) {
        this.lasttime = lasttime;
    }

    public Date getNexttime() {
        return nexttime;
    }

    public void setNexttime(Date nexttime) {
        this.nexttime = nexttime;
    }

    public String getEveryxn() {
        return everyxn;
    }

    public void setEveryxn(String everyxn) {
        this.everyxn = everyxn;
    }

    public byte[] getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public Integer getEquipment() {
        return equipment;
    }

    public void setEquipment(Integer equipment) {
        this.equipment = equipment;
    }

    public Pendingtasks getPendingtasks() {
        return pendingtasks;
    }

    public void setPendingtasks(Pendingtasks pendingtasks) {
        this.pendingtasks = pendingtasks;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public void setTasks(Tasks tasks) {
        this.tasks = tasks;
    }

    public Collection<Taskhistory> getTaskhistoryCollection() {
        return taskhistoryCollection;
    }

    public void setTaskhistoryCollection(Collection<Taskhistory> taskhistoryCollection) {
        this.taskhistoryCollection = taskhistoryCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (schedule != null ? schedule.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Schedules)) {
            return false;
        }
        Schedules other = (Schedules) object;
        return (this.schedule != null || other.schedule == null) && (this.schedule == null || this.schedule.equals(other.schedule));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Schedules[schedule=" + schedule + "]";
    }

}
