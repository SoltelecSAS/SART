/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Usuario
 */


public class ProcesoDrenaje implements ActionListener,Runnable{
    BancoGasolina banco;//clase para hacer las comunicaciones inyectarla no instanciarla
    JProgressBar barraTiempo;
    JLabelPersonalizada labelMensaje;
    int contadorTimer = 0;
    Timer timer;

    public ProcesoDrenaje(BancoGasolina banco, JProgressBar barraTiempo, JLabelPersonalizada labelMensaje) {
        this.banco = banco;
        this.barraTiempo = barraTiempo;
        this.labelMensaje = labelMensaje;
    }//end of constructor


   public void iniciarTimer(){
        timer = new Timer (1000,this);
    }

    public void iniciarProceso(){
        try{
            labelMensaje.setText("Limpiando Banco");
            barraTiempo.setMaximum(20);
            iniciarTimer();
            timer.start();
            banco.encenderCalSol1(false);
            banco.encenderCalSol2(false);
            banco.encenderSolenoideUno(true);
            banco.encenderSolenoide2(false);
            banco.encenderBombaMuestras(true);
            banco.encenderDrainPump(true);
            while(contadorTimer < 21) {
                Thread.sleep(250);//para no matar la cpu

                barraTiempo.setValue(contadorTimer);

            }
            banco.encenderBombaMuestras(false);
            banco.encenderDrainPump(false);
            banco.encenderDrainPump(false);
            banco.encenderSolenoideUno(false);
            labelMensaje.setText("Limpieza Terminada");
            timer.stop();
        }catch(NullPointerException ne){
            System.out.println("Error comunicandose con el banco");
            Logger.getRootLogger().error("Error haciendo el proceso de limpieza, ne");
            ne.printStackTrace();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }

    }

    public void setBanco(BancoSensors banco) {
        this.banco = banco;
    }


    public void actionPerformed(ActionEvent e) {
        barraTiempo.setValue(contadorTimer++);//no dentro del Event Dispatch Thread substance se queja

    }

    public JProgressBar getBarraTiempo() {
        return barraTiempo;
    }

    public void setBarraTiempo(JProgressBar barraTiempo) {
        this.barraTiempo = barraTiempo;
    }

    public JLabel getLabelMensaje() {
        return labelMensaje;
    }

    public void setLabelMensaje(JLabelPersonalizada labelMensaje) {
        this.labelMensaje = labelMensaje;
    }

    public void run() {
        iniciarProceso();
    }

}//end of class JPanel

