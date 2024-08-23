/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Administrador
 */
@Entity
@Table(name = "tasks")
@NamedQueries({
    @NamedQuery(name = "Tasks.findAll", query = "SELECT t FROM Tasks t"),
    @NamedQuery(name = "Tasks.findByTask", query = "SELECT t FROM Tasks t WHERE t.task = :task"),
    @NamedQuery(name = "Tasks.findByTaskcode", query = "SELECT t FROM Tasks t WHERE t.taskcode = :taskcode"),
    @NamedQuery(name = "Tasks.findByTaskname", query = "SELECT t FROM Tasks t WHERE t.taskname = :taskname"),
    @NamedQuery(name = "Tasks.findByTaskdes", query = "SELECT t FROM Tasks t WHERE t.taskdes = :taskdes"),
    @NamedQuery(name = "Tasks.findByTaskvalue", query = "SELECT t FROM Tasks t WHERE t.taskvalue = :taskvalue"),
    @NamedQuery(name = "Tasks.findByEquipment", query = "SELECT t FROM Tasks t WHERE t.equipment = :equipment")})
public class Tasks implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "TASK")
    private Integer task;
    @Basic(optional = false)
    @Column(name = "TASKCODE")
    private String taskcode;
    @Column(name = "TASKNAME")
    private String taskname;
    @Column(name = "TASKDES")
    private String taskdes;
    @Column(name = "TASKVALUE")
    private String taskvalue;
    @Column(name = "EQUIPMENT")
    private Integer equipment;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tasks")
    private Collection<Schedules> schedulesCollection;

    public Tasks() {
    }

    public Tasks(Integer task) {
        this.task = task;
    }

    public Tasks(Integer task, String taskcode) {
        this.task = task;
        this.taskcode = taskcode;
    }

    public Integer getTask() {
        return task;
    }

    public void setTask(Integer task) {
        this.task = task;
    }

    public String getTaskcode() {
        return taskcode;
    }

    public void setTaskcode(String taskcode) {
        this.taskcode = taskcode;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTaskdes() {
        return taskdes;
    }

    public void setTaskdes(String taskdes) {
        this.taskdes = taskdes;
    }

    public String getTaskvalue() {
        return taskvalue;
    }

    public void setTaskvalue(String taskvalue) {
        this.taskvalue = taskvalue;
    }

    public Integer getEquipment() {
        return equipment;
    }

    public void setEquipment(Integer equipment) {
        this.equipment = equipment;
    }

    public Collection<Schedules> getSchedulesCollection() {
        return schedulesCollection;
    }

    public void setSchedulesCollection(Collection<Schedules> schedulesCollection) {
        this.schedulesCollection = schedulesCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (task != null ? task.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tasks)) {
            return false;
        }
        Tasks other = (Tasks) object;
        return (this.task != null || other.task == null) && (this.task == null || this.task.equals(other.task));
    }

    @Override
    public String toString() {
        return "com.soltelec.model.Tasks[task=" + task + "]";
    }

}
