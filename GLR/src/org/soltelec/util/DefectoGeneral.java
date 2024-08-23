/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.util;

/**
 *Clase para agrupar un defecto y su tipo
 *  @author Usuario
 */
public class DefectoGeneral {

    private int codigoDefecto;
    private TipoDefecto tipoDefecto;

    public DefectoGeneral(int codigoDefecto, TipoDefecto tipoDefecto) {
        this.codigoDefecto = codigoDefecto;
        this.tipoDefecto = tipoDefecto;
    }

    public int getCodigoDefecto() {
        return codigoDefecto;
    }

    public void setCodigoDefecto(int codigoDefecto) {
        this.codigoDefecto = codigoDefecto;
    }

    public TipoDefecto getTipoDefecto() {
        return tipoDefecto;
    }

    public void setTipoDefecto(TipoDefecto tipoDefecto) {
        this.tipoDefecto = tipoDefecto;
    }



   
}
