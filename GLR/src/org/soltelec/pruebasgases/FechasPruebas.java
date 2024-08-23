/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Usuario
 */
public class FechasPruebas implements Serializable{

    private Date ultimaFechaFugas, ultimaFechaPipetas,ultimaFechaOpacidad;

    public FechasPruebas(){
        this.ultimaFechaFugas = null;
        this.ultimaFechaOpacidad = null;
        this.ultimaFechaPipetas = null;
    }

    public FechasPruebas(boolean noArchivo){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());//hoy
        c.add(Calendar.DAY_OF_MONTH, -3);
        this.ultimaFechaFugas = c.getTime();
        c.add(Calendar.MONTH,-4);
        this.ultimaFechaOpacidad = c.getTime();
        c.setTime( new Date());
        c.add(Calendar.WEEK_OF_MONTH, -1);
        this.ultimaFechaPipetas = c.getTime();

    }

    public Date getUltimaFechaFugas() {
        return ultimaFechaFugas;
    }

    public void setUltimaFechaFugas(Date ultimaFechaFugas) {
        this.ultimaFechaFugas = ultimaFechaFugas;
    }

    public Date getUltimaFechaOpacidad() {
        return ultimaFechaOpacidad;
    }

    public void setUltimaFechaOpacidad(Date ultimaFechaOpacidad) {
        this.ultimaFechaOpacidad = ultimaFechaOpacidad;
    }

    public Date getUltimaFechaPipetas() {
        return ultimaFechaPipetas;
    }

    public void setUltimaFechaPipetas(Date ultimaFechaPipetas) {
        this.ultimaFechaPipetas = ultimaFechaPipetas;
    }



}//end of class FechasPruebas
