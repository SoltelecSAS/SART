/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

import eu.hansolo.steelseries.tools.BackgroundColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.Timer;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.pruebasgases.PanelPruebaGases;
/**
 *23 Sept 2011
 * La idea es que cada Callable retorne una lista
 * de enteros con el ruido del pito y el ruido del
 * motor
 * @author GerenciaDesarrollo
 */
public class CallableSonSimul implements Callable<List<List<Double>>>{
    private MedidorRevTemp medRevTemp;
    private SimuladorRmpSonometro simuladorRpm;
    private PanelPruebaSonometro panelPruebaGases;
    private int contadorTemporizacion;
    private Timer timer;
    private int T_ANTES_ACELERAR;
    private List<Double> listaCiclo1,listaCiclo2,listaCiclo3;
    private Sonometro sonometro;

    public CallableSonSimul(PanelPruebaSonometro panel, Sonometro sonometro) {
        simuladorRpm = new SimuladorRmpSonometro();
        medRevTemp = simuladorRpm;//cuestion de polimorfismo
        panelPruebaGases = panel;
        timer = new Timer(1000,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        listaCiclo1 = new ArrayList<Double>();
        listaCiclo2 = new ArrayList<Double>();
        listaCiclo3 = new ArrayList<Double>();
        this.sonometro = sonometro;
    }




    @Override
    //lectorHTech y consumidorHtech deben estar funcionando
    public List<List<Double>> call() throws Exception {
        Future futureSimulador = null;
        try{          
           
        ExecutorService exec = Executors.newSingleThreadExecutor();
        futureSimulador = exec.submit(simuladorRpm);
        int intentos = 0;
         while(intentos < 2 ){
                    int ciclos = 0;
                    contadorTemporizacion = 0;
                    timer.start();
                    while(ciclos < 3 ){
                        Thread.sleep(10);
                        T_ANTES_ACELERAR = 2;
                        simuladorRpm.setSimulandoRalenti(true);//un rato en ralenti
                        panelPruebaGases.getMensaje().setText("ACELERE!!!");
                        Thread.sleep(1500);
                        contadorTemporizacion = 0;
                        panelPruebaGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLUE);//azul en ralenti
                            while(contadorTemporizacion < T_ANTES_ACELERAR ){//dos segundos en ralenti
                                Thread.sleep(100);
                                panelPruebaGases.getMensaje().setText("ACELERE!!!");
                                panelPruebaGases.getRadialTacometro().setValue(medRevTemp.getRpm());
                            }//end whileF
                            Thread.sleep(50);//proporciona un punto de salida
                            
                            contadorTemporizacion = 0;
                            simuladorRpm.setContadorSimulacion(0);
                            simuladorRpm.setSimulandoRalenti(false);//simuladasRalenti = false;
                            simuladorRpm.setSubidaAceleracion(true);//subidaAceleracion = true;
                            //INICIO TOMA DE DATOS
                           System.err.println("CICLO: " + ciclos);
                           panelPruebaGases.getPanelMensaje().setVisible(false);
                           panelPruebaGases.getPanelFiguras().setVisible(true);
                           //panel.getMensaje().setText("Acelerando t:" + contadorTemporizacion);
                           contadorTemporizacion = 0;
                            while(contadorTemporizacion < 3 && medRevTemp.getRpm() <= (simuladorRpm.getVelocidadCrucero() - 200)){//raro no?
                                Thread.sleep(50);
                                panelPruebaGases.getMensaje().setText("ACELERE!!!");
                                panelPruebaGases.getRadialTacometro().setValue(medRevTemp.getRpm());
                            }//end of while
                            simuladorRpm.setSubidaAceleracion(false);
                            contadorTemporizacion = 0;
                            simuladorRpm.setSimulandoCrucero(true);//simuladasCrucero = true;//estabilidad en crucero
                            panelPruebaGases.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                            while(contadorTemporizacion < 4 && (medRevTemp.getRpm() >= simuladorRpm.getVelocidadCrucero() -200 && medRevTemp.getRpm() <= 3700 )){
                                Thread.sleep(50);
                                panelPruebaGases.getMensaje().setText("EN RANGO MIDIENDO");
                                panelPruebaGases.getRadialTacometro().setValue(simuladorRpm.getRpm());
                                if(ciclos == 0)
                                    listaCiclo1.add(sonometro.getDecibeles());
                                    ;
                                if(ciclos == 1)
                                    listaCiclo2.add(sonometro.getDecibeles());
                                    ;
                                if(ciclos == 2)
                                    listaCiclo3.add(sonometro.getDecibeles());
                                    ;
                           }
                           simuladorRpm.setSimulandoCrucero(false);//simuladasCrucero = false;
                           simuladorRpm.setBajadaAceleracion(true);//bajadaAceleracion = true;
                           simuladorRpm.setContadorSimulacion(0);//contadorSimulacion = 0;
                           contadorTemporizacion = 0;
                           //dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
                           panelPruebaGases.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                           panelPruebaGases.getMensaje().setText("DESACELERE...!!!");
                           while(contadorTemporizacion <= 3){
                               Thread.sleep(150);
                               panelPruebaGases.getMensaje().setText("DESACELERE!!");
                               panelPruebaGases.getRadialTacometro().setValue(medRevTemp.getRpm());                               ;
                               if(ciclos == 0)
                                    listaCiclo1.add(sonometro.getDecibeles());
                                    
                                if(ciclos == 1)
                                    listaCiclo2.add(sonometro.getDecibeles());
                                    
                                if(ciclos == 2)
                                    listaCiclo3.add(sonometro.getDecibeles());
                                    
                           }
                           simuladorRpm.setBajadaAceleracion(false);//bajadaAceleracion = false;
                           simuladorRpm.setContadorSimulacion(0);//contadorSimulacion = 0;
                           System.err.println("FIN CICLO:" + ciclos);
                           ciclos++;
                    }//end while ciclos
                    Thread.sleep(1500);
                    timer.stop();
                    intentos = 4;
            }//end while intentos
        futureSimulador.cancel(true);
        
        }//end of try
        catch(InterruptedException iexc){
            futureSimulador.cancel(true);
            timer.stop();
            throw new CancellationException();
        }
        finally{//Evita problemas de hilos vivos en caso de excepciones
            futureSimulador.cancel(true);
            timer.stop();
        }
        List<List<Double>> listaRetorno = new ArrayList<List<Double>>(3);
        listaRetorno.add(listaCiclo1);
        listaRetorno.add(listaCiclo2);
        listaRetorno.add(listaCiclo3);
        return listaRetorno;
    }//end of method call
}//end of class
