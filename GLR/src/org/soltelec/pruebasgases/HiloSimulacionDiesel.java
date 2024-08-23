/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import eu.hansolo.steelseries.tools.LcdColor;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.soltelec.medicionrpm.ConsumidorCapelec;

/**
 *Clase para hacer la prueba diesel de aceleracion libre 
 * simulando las revoluciones de ralenti
 * @author Usuario
 */


public class HiloSimulacionDiesel implements ActionListener,Runnable{
    private double ralenti;
    private double crucero;
    private int contadorSimulacion;
    private Timer timerSimulacion;
    private int contadorTemporizacion;
    private double tiempo;
    private double rpmSimuladas;
    private Timer timer;
    private DialogoCiclosAceleracion dialogo;//la interfaz grafica
    private  int T_ANTES_ACELERAR = 18;
    private int T_PARA_RALENTI = 20;
    private boolean simuladasRalenti = true;
    private double velRalenti = 420;
    private Random random = new Random();
    private boolean subidaAceleracion = false;//se controla la simulacion con booleanos
    private double velCrucero = 4500;
    private boolean simuladasCrucero;
    private boolean bajadaAceleracion;
    //private HiloTomaDatosDiesel hiloTomaDatos;
    //private Thread t;
    private Opacimetro opacimetro;
    private List<Byte> listaDatosCiclo1, listaDatosCiclo2,listaDatosCiclo3;
    private List[] arregloListaCiclos = new ArrayList[3];
    //private ArrayList<ArregloOpacidadCiclo> listaDatos;
    private volatile boolean terminado = false;
    int intentos = 0;

    private volatile Thread blinker;
    
    public HiloSimulacionDiesel(DialogoCiclosAceleracion d,OpacimetroSensors op){
        dialogo = d;
        dialogo.getButtonCancelar().addActionListener(this);
        this.opacimetro = op;               
    }//end of constructor   

    @Override
    public void run() {
    Thread t = null;
    System.out.println("Iniciando el hilo");
    blinker = Thread.currentThread();
    HiloTomaDatosDiesel hiloTomaDatos = null;
    System.out.println("Blinker diferente de null"  + blinker!= null);
    System.out.println("Teminado:" + !terminado);
    try{
        while(!terminado && blinker != null){
        //Antes de iniciar el ciclo de bariido se dan 20 SEGUNDOS para entrar al carro
        //en los ultimos 5 segundos se da la indicacion del semaforo
        timerSimulacion = new Timer(250,new ListenerSimulacion());
        timerSimulacion.start();
        crearNuevoTimer();
        //se dan tres intentos para realizar la prueba al terminar los intentos
        //la prueba se da por terminada con rechazo
        while(intentos < 2 && blinker != null){
                    int ciclos = 0;
                    timer.start();//timer que lleva el tiempo de la prueba
                    while(ciclos < 4  && blinker != null){

                        if(ciclos >=1){
                            hiloTomaDatos = new HiloTomaDatosDiesel();//instancias distintas ¿Solo almacena la ultima?
                            hiloTomaDatos.setNumeroCiclo(ciclos);
                            hiloTomaDatos.setOpacimetro((OpacimetroSensors) opacimetro);
                            t = new Thread(hiloTomaDatos);//inicia un nuevlo hilo por ciclo deberia ser una variable local
                        }
                            if(ciclos == 0)
                                T_ANTES_ACELERAR = 20;//20 segundos al inicio
                            else
                                T_ANTES_ACELERAR = 8;
                            simuladasRalenti = true;
                            contadorTemporizacion = 0;
                            dialogo.getDisplayTiempo().setLcdColor(LcdColor.STANDARD_LCD);
                            dialogo.getLabelMensaje().setText("Ciclo: "+ ciclos );
                            dialogo.getLedRojo().setLedBlinking(false);
                            dialogo.getLedRojo().setLedOn(false);
                            //en este momento se espera que el vehiculo este en ralenti
                            dialogo.getLabelMensaje().setText("MANTENGA RALENTI");
                            contadorTemporizacion = 0;
                            dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLACK_LCD);
                            while(contadorTemporizacion < T_ANTES_ACELERAR && blinker != null){
                                Thread.sleep(100);
                                semaforo();//metodo que enciende o apaga los leds segun el caso
                                dialogo.getDisplayTiempo().setLcdValue(T_ANTES_ACELERAR - contadorTemporizacion);
                                dialogo.getRadialTacometro().setValue(rpmSimuladas);
                            }//end while
                            Thread.sleep(50);//proporciona un punto de salida
                            semaforo();
                            //ACELERACION EN MENOS DE 5S
                            contadorTemporizacion = 0;
                            contadorSimulacion = 0;
                            simuladasRalenti = false;
                            subidaAceleracion = true;
                            //INICIO TOMA DE DATOS                            
                           if(ciclos >= 1){
                                t.start();
                                hiloTomaDatos.setNumeroCiclo(ciclos);
                           }
                           System.err.println("CICLO: " + ciclos);
                           dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                           //Aceleracion o subida
                            while(contadorTemporizacion < 5 && rpmSimuladas <= velCrucero - 125 && blinker != null){
                                Thread.sleep(50);
                                dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                                dialogo.getRadialTacometro().setValue(rpmSimuladas);                                
                            }//end of while
                            subidaAceleracion = false;
                            contadorTemporizacion = 0;
                            simuladasCrucero = true;
                            dialogo.getLabelMensaje().setText("CICLO:"+ciclos+" GOBERNADAS!!!");
                            dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLUE2_LCD);
                            //Mantener la aceleracion
                            while(contadorTemporizacion < 4 && (rpmSimuladas >= (velCrucero - 250) && rpmSimuladas <= (velCrucero +50) ) && blinker != null){
                                Thread.sleep(50);
                                dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                                dialogo.getRadialTacometro().setValue(rpmSimuladas);                               
                           }
                           simuladasCrucero = false;
                           bajadaAceleracion = true;
                           contadorSimulacion = 0;
                           contadorTemporizacion = 0;
                           dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
                           dialogo.getLedRojo().setLedOn(true);
                           dialogo.getLedVerde().setLedOn(false);
                           dialogo.getLabelMensaje().setText("CICLO: "+ciclos+"SUELTE PEDAL!");
                           //bajada de la aceleracion
                           while(contadorTemporizacion <= 15 && blinker != null){
                               Thread.sleep(150);
                               dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                               dialogo.getRadialTacometro().setValue(rpmSimuladas);                              
                           }
                           if(ciclos > 0){
                               hiloTomaDatos.setCicloTerminado(true);//finalizaria la toma de datos
                               Thread.sleep(900);
                               if(ciclos == 1)
                               listaDatosCiclo1 = hiloTomaDatos.getListaOpacidad();//Son hilos distintos todos
                               if(ciclos == 2)
                               listaDatosCiclo2 = hiloTomaDatos.getListaOpacidad();//pero la toma de datos no se afecta
                               if(ciclos == 3)
                               listaDatosCiclo3 = hiloTomaDatos.getListaOpacidad();//entonces diseñar acorde la clase
                               hiloTomaDatos.setCicloTerminado(true);
                               hiloTomaDatos.setInterrumpido(true);
                               hiloTomaDatos.setTerminado(true);                               
                           }//termina toma de datos
                           bajadaAceleracion = false;
                           contadorSimulacion = 0;
                           System.err.println("FIN CICLO:" + ciclos);
                           ciclos++;
                    }//en while ciclos
                    timer.stop();
                    timerSimulacion.stop();
                    arregloListaCiclos[0] = listaDatosCiclo1;
                    arregloListaCiclos[1] = listaDatosCiclo2;
                    arregloListaCiclos[2] = listaDatosCiclo3;
                    Thread.sleep(1500);                            
        dialogo.cerrar();
        intentos = 4;
        }//end while intentos       
        }
        } catch(InterruptedException ie){
            this.salir();
            hiloTomaDatos.setCicloTerminado(true);
            hiloTomaDatos.setInterrumpido(true);
            hiloTomaDatos.setTerminado(true);
            dialogo.cerrar();
            throw new CancellationException("prueba Cancelada");
        }
    }//end of method run

    private void semaforo() {

        if (contadorTemporizacion == (T_ANTES_ACELERAR - 5)) {
            dialogo.getLabelMensaje().setText("5s RESTANTES");
            dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
            dialogo.getLedRojo().setLedOn(true);
            dialogo.getLedAmarillo().setLedOn(false);
            dialogo.getLedVerde().setLedOn(false);
        }
        if (contadorTemporizacion == (T_ANTES_ACELERAR - 3)) {
            dialogo.getLabelMensaje().setText("3s!");
            dialogo.getLedAmarillo().setLedOn(true);
            //dialogo.getDisplayTiempo().setLcdColor(LcdColor.YELLOW_LCD);
            dialogo.getLedRojo().setLedOn(false);
            dialogo.getLedVerde().setLedOn(false);
        }
        if (contadorTemporizacion == (T_ANTES_ACELERAR)) {
            dialogo.getDisplayTiempo().setLcdColor(LcdColor.YELLOW_LCD);
            dialogo.getLedVerde().setLedOn(true);
            dialogo.getLedRojo().setLedOn(false);
            dialogo.getLedAmarillo().setLedOn(false);
            dialogo.getLabelMensaje().setText("ACELERE!!!!!!");
        }
    }

    private void crearNuevoTimer() {
        timer = new Timer(1000,new ActionListener(){
           public void actionPerformed(ActionEvent ae){
               contadorTemporizacion++;
           }
        });

    }//end of method

     @Override
    public void actionPerformed(ActionEvent e) {

        this.stop();

    }


//    private void dormir(int i) {
//        try {
//            Thread.sleep(i);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(HiloSimulacionDiesel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }//end of dormir

    public void setVelCrucero(double velCrucero) {
        this.velCrucero = velCrucero;
    }

    public void setVelRalenti(double velRalenti) {
        this.velRalenti = velRalenti;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

   
    private void stop() {//blinker seguido de sleep
        Thread tmpBlinker = blinker;
        blinker = null;
        if (tmpBlinker != null) {
           tmpBlinker.interrupt();
        }
    }

    private void salir() {
        timer.stop();
        timerSimulacion.stop();
        //cerrar los puertos
        dialogo.setPruebaCancelada(true);
        System.out.println("Prueba Cancelada");
        JOptionPane.showMessageDialog(dialogo, "Prueba Cancelada");
    }

    class ListenerSimulacion implements ActionListener{//listener que se ejecuta cada segundo... controla la simulacion
        //las variables booleanas se van modificando en el hilo principal
        //segun el tiempo vaya transcurriendo

        @Override
        public void actionPerformed(ActionEvent e) {
            if(simuladasRalenti && !subidaAceleracion){//debe permanecer en velocidad de ralenti
                rpmSimuladas = velRalenti + (15-random.nextInt(30));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;//siempre ser multiplo de diez
            }
            if(subidaAceleracion&& !simuladasCrucero){//subiendo  es decir aceleracion desde ralenti a crucero
                contadorSimulacion++;
                rpmSimuladas = ( (velCrucero-velRalenti)*(1-Math.exp(0-(0.25-random.nextDouble()*0.03)*contadorSimulacion))+(15-random.nextInt(30)) ) + velRalenti;
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if(simuladasCrucero && !simuladasRalenti &&!subidaAceleracion){//mantener la velocidad de crucero
                rpmSimuladas = ((velCrucero ) + (15-random.nextInt(30)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if(bajadaAceleracion&& !simuladasRalenti &&!subidaAceleracion &&!simuladasCrucero){//bajada de aceleacion
                contadorSimulacion++;
                rpmSimuladas = (velRalenti + (velCrucero-velRalenti)*(Math.exp((-0.12-random.nextDouble()*0.01)*contadorSimulacion)) +(16-random.nextInt(28)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }

        }

    }

    public List[] getArregloListaCiclos() {       
        return arregloListaCiclos;
    }

    public List<Byte> getListaDatosCiclo1() {
        return listaDatosCiclo1;
    }

    public List<Byte> getListaDatosCiclo2() {
        return listaDatosCiclo2;
    }

    public List<Byte> getListaDatosCiclo3() {
        return listaDatosCiclo3;
    }
    
    
    
}//end of class HiloSimulacionDiesel
