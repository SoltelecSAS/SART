/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Void;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.soltelec.horiba.BancoHoriba;
import org.soltelec.util.BloqueoEquipoUtilitario;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.capelec.BancoCapelec;

/**
 *
 * Clase para implementar el proceso de hacer cero en el banco
 * usando SwingWorker todo para que funcione con Substance Look and Feel
 * @author GerenciaDesarrollo
 */
public class WorkerCero extends SwingWorker<Void,Integer>{

    private JLabel labelMensaje;
    //barratiempo se hace por fuera
    private Timer timer;
    private BancoGasolina banco;
    private int contadorTimer = 0;

    public WorkerCero(JLabel labelMensaje, BancoGasolina banco) {
        this.labelMensaje = labelMensaje;
        this.banco = banco;
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTimer++;
            }
        });
    }



    @Override
    protected Void doInBackground() throws Exception {        
        if(banco instanceof BancoSensors){
         labelMensaje.setText("Ingresando aire limpio...!");
         timer.start();
            banco.encenderSolenoideUno(true);
            banco.encenderBombaMuestras(true);
            while(contadorTimer < 31){
                System.out.println("Contando .." + contadorTimer);
                //barraTiempo.setValue(contadorTimer);
                Thread.sleep(250);
                setProgress(contadorTimer);
                if (contadorTimer > 29){
                    labelMensaje.setText("Estabilizando ..!");
                    banco.encenderBombaMuestras(false);
                    banco.encenderSolenoideUno(false);
                } else if(contadorTimer >26 ){
                    labelMensaje.setText("Enviando Comando de Cero ..!");                    
                }
            }
            Thread.sleep(300);
             MedicionGases medicion = banco.obtenerDatos();
               System.out.println("HC IS " +medicion.getValorHC() +" CO: "+medicion.getValorCO()+" CO2: "+medicion.getValorCO2()+" O2:"+medicion.getValorO2());
            if (medicion.getValorHC() > 30 || medicion.getValorCO()  > 100 || medicion.getValorCO2() > 10 || medicion.getValorO2()<180) {
                JOptionPane.showMessageDialog(null, "EL CERO DEL EQUIPO  DESVIADO \n  CONSULTE AL SOPORTE TECNICO \n SISTEMA   BLOQUEADO SEGUN NUMERAL 5.2.3.3 ...!");
                 labelMensaje.setText("EL CERO DEL EQUIPO SE ENCUENTRA DESVIADO "); 
                 BloqueoEquipoUtilitario.bloquearEquipo(
                    "WorkerCero: EL CERO DEL EQUIPO SE ENCUENTRA DESVIADO, por eso se bloqueo\n");
                 Thread.sleep(5700);
                labelMensaje.setText(" ");
            } else {
                banco.ordenarCalibracion(0);//Pone a cero los valores de HC,CO,CO2
                banco.ordenarCalibracion(1);//calibra usando un solo punto el valor de O2
                labelMensaje.setText("Proceso Cero Finalizado con Exito ..!");
            }       
            timer.stop();
            
        }
        else if(banco instanceof BancoCapelec){
         labelMensaje.setText("Ingresando aire limpio");
         timer.start();
            banco.encenderSolenoideUno(true);
            banco.encenderBombaMuestras(true);
            while(contadorTimer < 31){
                System.out.println("Contando .." + contadorTimer);
                //barraTiempo.setValue(contadorTimer);
                Thread.sleep(250);
                setProgress(contadorTimer);
                if (contadorTimer > 27){
                    labelMensaje.setText("Estabilizando");
                    banco.encenderBombaMuestras(false);
                    banco.encenderSolenoideUno(false);
                } else if(contadorTimer >27 ){
                    labelMensaje.setText("Enviando Comando de Cero");
                }
            }
            banco.ordenarCalibracion(0);//Pone a cero los valores de HC,CO,CO2
            banco.ordenarCalibracion(1);//calibra usando un solo punto el valor de O2
            labelMensaje.setText("Cero Terminado");
            timer.stop();
            
        }
        
        else if(banco instanceof BancoHoriba){
            
            ((BancoHoriba)banco).llamarRutinaCero();
            Thread.sleep(2500);
            timer.start();
            MedicionGases medicion = banco.obtenerDatos();
            
            while(medicion.isCeroEnProgreso()){
                
                Thread.sleep(1000);
                medicion = banco.obtenerDatos();
                setProgress(contadorTimer);
            }
            
            
        }
        
        setProgress(100);
        return null;
    }

}
