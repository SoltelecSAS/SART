/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import eu.hansolo.steelseries.tools.BackgroundColor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import javax.swing.Timer;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.pruebasgases.PanelPruebaGases;

/**
 *
 * @author DANIELA SANA
 */
public class CallableAceleraciones implements Callable<List<List<Double>>> {

    private final MedidorRevTemp medRevTemp;
    //private ConsumidorCapelec consumidorCapelec;
    //private ProductorCapelec productorCapelec;
    private final PanelPruebaSonometro panel;
    private int contadorTemporizacion;
    private final Timer timer;
    private final Sonometro sonometro;
    private int rangoInicial = 2550;
    private int rangoFinal = 5000;
    private String nombregasolina;

    public CallableAceleraciones(PanelPruebaSonometro panel, Sonometro sonometro, MedidorRevTemp medidorRevTemp) {
        this.panel = panel;
        this.sonometro = sonometro;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        this.medRevTemp = medidorRevTemp;
    }

    CallableAceleraciones(PanelPruebaSonometro panel, Sonometro sonometro, MedidorRevTemp medidorRevTemp, String nombregasolina) {
        this.panel = panel;
        this.nombregasolina = nombregasolina;
        this.sonometro = sonometro;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        this.medRevTemp = medidorRevTemp;
       rangoInicial = 2550;
        rangoFinal = 5999;
    }

    @Override
    public List<List<Double>> call() throws Exception {
        int rpm = 0;
        try {
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelFiguras().setVisible(false);
            panel.getMensaje().setText("");
            panel.getProgressBar().setString("");
            panel.getProgressBar().setValue(0);
            panel.getPanelMensaje().setVisible(false);
            panel.getPanelFiguras().setVisible(true);
            Thread.sleep(10);
            int ciclos = 0;
            List<Double> listaCiclo1 = new ArrayList<>();
            List<Double> listaCiclo2 = new ArrayList<>();
            List<Double> listaCiclo3 = new ArrayList<>();
            timer.start();
            System.out.println("Contador iniciado");

            while (ciclos < 3) {
                //mientras este por debajo de 2500 y por encima de 3700
                rpm = medRevTemp.getRpm();
                contadorTemporizacion = 0;
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLUE);
                while (rpm < rangoInicial || rpm > rangoFinal) {
                    Thread.sleep(100);
                    if (rpm < (rangoInicial - 25)) {
                        panel.getMensaje().setText("ACELERE !!! t:" + contadorTemporizacion);
                        
                    }
                    if (rpm > (rangoFinal + 50)) {
                        panel.getMensaje().setText("DESACELERE !!! t:" + contadorTemporizacion);
                        
                    }

                    rpm = medRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                }
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                contadorTemporizacion = 0;

                while ((rpm > (rangoInicial - 50) || rpm < (rangoFinal + 50)) && contadorTemporizacion < 3) {
                    Thread.sleep(100);
                    panel.getMensaje().setText("EN RANGO TOMANDO MEDICIONES");
                     panel.getMensaje().setFont( new Font( "Helvetica", Font.BOLD, 15 ) );

                    if (ciclos == 0) {
                        listaCiclo1.add(sonometro.getDecibeles());
                    }
                    if (ciclos == 1) {
                        listaCiclo2.add(sonometro.getDecibeles());
                    }
                    if (ciclos == 2) {
                        listaCiclo3.add(sonometro.getDecibeles());
                    }
                    rpm = medRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                }
                if (contadorTemporizacion < 2) {
                    panel.getMensaje().setText("ERROR SOSTENGA DURANTE 2 S");
                    Thread.sleep(2000);
                    continue;//no aumenta la variable de los ciclos
                }
//                                                private int rangoInicial = 2550;
//    private int rangoFinal = 3650;
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                while (rpm > (rangoInicial - 50)) {
                    Thread.sleep(100);
                     panel.getMensaje().setFont( new Font( "Helvetica", Font.BOLD, 25 ) );
                    panel.getMensaje().setText("DESACELERE!!!!! ");
                    rpm = medRevTemp.getRpm();
                    if (ciclos == 0) {
                        listaCiclo1.add(sonometro.getDecibeles());
                    }
                    if (ciclos == 1) {
                        listaCiclo2.add(sonometro.getDecibeles());
                    }
                    if (ciclos == 2) {
                        listaCiclo3.add(sonometro.getDecibeles());
                    }
                    rpm = medRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                }
                ciclos++;
            }//end while ciclos
            timer.stop();
            //medRevTemp.getPuertoSerial().close();
            //sonometro.stop();
            List<List<Double>> listaDeListas = new ArrayList<>();
            listaDeListas.add(listaCiclo1);
            listaDeListas.add(listaCiclo2);
            listaDeListas.add(listaCiclo3);

            return listaDeListas;

        } catch (InterruptedException iexc) {
            cancelacion();
            throw new CancellationException();
        }
    }

    private void cancelacion() {

//        medRevTemp.getPuertoSerial().close();
//        panel.getPanelFiguras().setVisible(false);
//        panel.getPanelMensaje().setVisible(true);
//        if(timer != null)
        timer.stop();

    }
    //

}
