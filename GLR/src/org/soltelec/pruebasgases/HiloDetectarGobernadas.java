/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.util.ShiftRegister;

/**
 * Clase para detectar las aceleraciones gobernadas en el ciclo diesel
 * cuando se pregunta por primera vez la velocidad de Ralenti y la velocidad
 * de crucero. ver Clase DialogoCruceroRalentiDiesel
 * @author Usuario
 */
public class HiloDetectarGobernadas implements Runnable{

    private ConsumidorCapelec consumidor;
    private ShiftRegister sr;
    private boolean terminado = false;
    private boolean interrumpido = false;
    private JTextField txtFieldRalenti, txtFieldGobernadas;
    private Thread blinker;

    public HiloDetectarGobernadas(ConsumidorCapelec consumidor, JTextField txtFieldRalenti, JTextField txtFieldGobernadas) {
        this.consumidor = consumidor;
        this.txtFieldRalenti = txtFieldRalenti;
        this.txtFieldGobernadas = txtFieldGobernadas;
        sr = new ShiftRegister(10,0);
    }//end of constructor



    @Override
    public void run() {

        try{
        while(!terminado){

            while(!interrumpido){
                System.out.println("Deteccion iniciada");
                blinker = Thread.currentThread();
                //detectar que la señal esta estable
                //mientras no este estable llenar el registro de desplazamiento
                while( !sr.isStable() && blinker != null ){
                    Thread.sleep(120);
                    sr.ponerElemento(consumidor.getRpm());//llena la tabla                   
                }//while !sr.isStable()
                System.out.println("Estabilidad : " + sr.isStable() + sr.media() );
                int velRalenti = (int)sr.media();
                txtFieldRalenti.setText( String.valueOf(velRalenti) );//si la medicion es estable
                //entonces serian las velocidad de Ralenti

                //aceleracion mientras no este acelerando no haga nada
                while( !sr.isAcelerando() && blinker != null ){
                    Thread.sleep(100);
                    sr.ponerElemento(consumidor.getRpm());
                    txtFieldGobernadas.setText( "No Aceleracion" );
                }//end while Acelerando
                
                //esta acelerando
                //debe mantenerse acelerando
                int contadorAceleracion = 0;
                do{
                    Thread.sleep(100);
                    sr.ponerElemento(consumidor.getRpm());
                    contadorAceleracion = 0;
                    while(sr.isAcelerando() && blinker != null && contadorAceleracion < 25){
                        Thread.sleep(120);
                        sr.ponerElemento(consumidor.getRpm());
                        contadorAceleracion++;
                   }
                   txtFieldGobernadas.setText("Acelere !!!!!");

                }while(!sr.isAcelerando());
                System.out.println("Acelerando : " + sr.isAcelerando() + sr.media() );
                //ha acelerado por mas de 2.5 segundos y sigue acelerando
                //esperar a que se estabilicae
                while(!sr.isStable() && sr.isAcelerando() && blinker != null){
                    Thread.sleep(100);
                    sr.ponerElemento(consumidor.getRpm());
                }
                System.out.println("Estabilizado o ha dejado de acelerar");
                int velGobernadas = (int)sr.media();
                txtFieldGobernadas.setText( String.valueOf( velGobernadas ) );
                //si ha acelerado por 2.5 segundos entonces esperar a que se estabilice
          }//end of while interrumpido
        }//end of while terminado
     }//end try
     catch(InterruptedException ie){
         System.out.println("Hilo Terminado");
         throw new CancellationException("Hilo finalizado");
     }//end of cathc
    }//end of method run

    public void stop(){
      Thread tmpBlinker = blinker;
        blinker = null;
        if (tmpBlinker != null) {
           tmpBlinker.interrupt();
        }
    }//end of method stop


}//end of class
