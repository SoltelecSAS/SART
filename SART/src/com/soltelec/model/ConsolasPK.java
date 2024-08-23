/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.soltelec.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Administrador
 */
@Embeddable
public class ConsolasPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "CONSOLE")
    private int console;
    @Basic(optional = false)
    @Column(name = "ROAD")
    private int road;

    public ConsolasPK() {
    }

    public ConsolasPK(int console, int road) {
        this.console = console;
        this.road = road;
    }

    public int getConsole() {
        return console;
    }

    public void setConsole(int console) {
        this.console = console;
    }

    public int getRoad() {
        return road;
    }

    public void setRoad(int road) {
        this.road = road;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) console;
        hash += (int) road;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConsolasPK)) {
            return false;
        }
        ConsolasPK other = (ConsolasPK) object;
        if (this.console != other.console) {
            return false;
        }
        return this.road == other.road;
    }

    @Override
    public String toString() {
        return "com.soltelec.model.ConsolasPK[console=" + console + ", road=" + road + "]";
    }

}
