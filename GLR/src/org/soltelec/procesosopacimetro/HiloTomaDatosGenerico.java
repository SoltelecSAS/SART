/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author DANIELA SANA
 */
public abstract class HiloTomaDatosGenerico implements Callable<List<Byte>>{
    protected Opacimetro opacimetro;
    

    public HiloTomaDatosGenerico() {
    }
   
    public HiloTomaDatosGenerico(Opacimetro opacimetro) {
        this.opacimetro = opacimetro;
    }

    public void setOpacimetro(Opacimetro opacimetro){
        this.opacimetro = opacimetro;
    }

    

    


}
