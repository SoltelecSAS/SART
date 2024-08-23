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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "taskhistory")
@NamedQueries({
    @NamedQuery(name = "Taskhistory.findAll", query = "SELECT t FROM Taskhistory t"),
    @NamedQuery(name = "Taskhistory.findByCurdate", query = "SELECT t FROM Taskhistory t WHERE t.curdate = :curdate"),
    @NamedQuery(name = "Taskhistory.findByTaskhistory", query = "SELECT t FROM Taskhistory t WHERE t.taskhistory = :taskhistory"),
    @NamedQuery(name = "Taskhistory.findByValue1", query = "SELECT t FROM Taskhistory t WHERE t.value1 = :value1"),
    @NamedQuery(name = "Taskhistory.findByValue2", query = "SELECT t FROM Taskhistory t WHERE t.value2 = :value2"),
    @NamedQuery(name = "Taskhistory.findByValue3", query = "SELECT t FROM Taskhistory t WHERE t.value3 = :value3"),
    @NamedQuery(name = "Taskhistory.findByValue4", query = "SELECT t FROM Taskhistory t WHERE t.value4 = :value4"),
    @NamedQuery(name = "Taskhistory.findByValue5", query = "SELECT t FROM Taskhistory t WHERE t.value5 = :value5"),
    @NamedQuery(name = "Taskhistory.findByText1", query = "SELECT t FROM Taskhistory t WHERE t.text1 = :text1")})
public class Taskhistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @Column(name = "CURDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date curdate;
    @Id
    @Basic(optional = false)
    @Column(name = "TASKHISTORY")
    private Integer taskhistory;
    @Column(name = "VALUE1")
    private Float value1;
    @Column(name = "VALUE2")
    private Float value2;
    @Column(name = "VALUE3")
    private Float value3;
    @Column(name = "VALUE4")
    private Float value4;
    @Column(name = "VALUE5")
    private Float value5;
    @Column(name = "TEXT1")
    private String text1;
    @JoinColumn(name = "SCHEDULE", referencedColumnName = "SCHEDULE")
    @ManyToOne(optional = false)
    private Schedules schedules;

    public Taskhistory() {
    }

    public Taskhistory(Integer taskhistory) {
        this.taskhistory = taskhistory;
    }

    public Taskhistory(Integer taskhistory, Date curdate) {
        this.taskhistory = taskhistory;
        this.curdate = curdate;
    }

    public Date getCurdate() {
        return curdate;
    }

    public void setCurdate(Date curdate) {
        this.curdate = curdate;
    }

    public Integer getTaskhistory() {
        return taskhistory;
    }

    public void setTaskhistory(Integer taskhistory) {
        this.taskhistory = taskhistory;
    }

    public Float getValue1() {
        return value1;
    }

    public void setValue1(Float value1) {
        this.value1 = value1;
    }

    public Float getValue2() {
        return value2;
    }

    public void setValue2(Float value2) {
        this.value2 = value2;
    }

    public Float getValue3() {
        return value3;
    }

    public void setValue3(Float value3) {
        this.value3 = value3;
    }

    public Float getValue4() {
        return value4;
    }

    public void setValue4(Float value4) {
        this.value4 = value4;
    }

    public Float getValue5() {
        return value5;
    }

    public void setValue5(Float value5) {
        this.value5 = value5;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public Schedules getSchedules() {
        return schedules;
    }

    public void setSchedules(Schedules schedules) {
        this.schedules = schedules;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskhistory != null ? taskhistory.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Taskhistory)) {
            return false;
        }
        Taskhistory other = (Taskhistory) object;
        return (this.taskhistory != null || other.taskhistory == null) && (this.taskhistory == null || this.taskhistory.equals(other.taskhistory));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Taskhistory[taskhistory=" + taskhistory + "]";
    }

}
