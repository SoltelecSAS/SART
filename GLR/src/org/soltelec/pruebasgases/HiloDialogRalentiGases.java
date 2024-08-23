/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.soltelec.medicionrpm.ConsumidorCapelec;

/**
 *
 * @author Usuario
 */
public class HiloDialogRalentiGases implements Runnable{
    Radial radialRPM;
    Linear linearTemperatura;
    ConsumidorCapelec consumidor;
    private boolean terminado;
    private JTextArea txtArea;

    public HiloDialogRalentiGases(Radial r,Linear l, JTextArea txtArea,ConsumidorCapelec c){
        this.radialRPM = r;
        this.linearTemperatura = l;
        consumidor = c;
        consumidor.setTxtArea(txtArea);
        consumidor.setRadialRPM(radialRPM);
   }

    public HiloDialogRalentiGases(Radial r,Linear l){
        this.radialRPM = r;
        this.linearTemperatura = l;
          }

    public void run(){

        while(!terminado){
           // radialRPM.setValue(consumidor.getRpm());
            linearTemperatura.setValue(consumidor.getTemp());
            dormir(200);
        }//end of while
   }//end of method run

    private void dormir(int tiempo){
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloDialogRalentiGases.class.getName()).log(Level.SEVERE, null, ex);
        }
        }//edn of method dormir

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public void setTxtArea(JTextArea txt) {
        this.txtArea = txt;
        consumidor.setTxtArea(txt);
    }

    
}//end of class
