/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import static java.awt.GridBagConstraints.*;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.soltelec.horiba.BancoHoriba;
import org.soltelec.util.capelec.BancoCapelec;
import org.soltelec.util.MedicionGases;

/**
 * Esta clase sirve para hacer lo del panel de cero es una mezcla horrible de
 * GUI con hilos con todo porfavor refactorizar si es que este proyecto sale a
 * flote
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class PanelCero extends JFrame implements ActionListener, Runnable {

    BancoGasolina banco;//clase para hacer las comunicaciones inyectarla no instanciarla
    JProgressBar barraTiempo;
    JLabelPersonalizada labelMensaje;
    int contadorTimer = 0;
    Timer timer;
    public static Integer calibradoCero = 0;

    public PanelCero() {//Un panel con una barra de progreso y unaetiqueta para ponerlo por ahi
        inicializarInterfaz();

    }

    private void inicializarInterfaz() {
        //Un panel con una barra de progreso y unaetiqueta para ponerlo por ahi
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        //llenar todo el espacio horizontal
        this.setLayout(bag);
        c.insets = new Insets(5, 5, 3, 3);
        c.weightx = 1.0;
        c.fill = NONE;
        labelMensaje = new JLabelPersonalizada("INGRESANDO AIRE LIMPIO");
        c.gridx = 0;
        c.gridy = 0;
        this.add(labelMensaje, c);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = HORIZONTAL;
        barraTiempo = new JProgressBar();
        barraTiempo.setBorderPainted(true);
        barraTiempo.setStringPainted(true);
        barraTiempo.setMaximum(30);
        this.add(barraTiempo, c);
    }

    public void iniciarTimer() {
        timer = new Timer(1000, this);
    }

    public void iniciarProceso() {
        try {
            iniciarTimer();
            if (banco instanceof BancoSensors) {
                labelMensaje.setText("Ingresando aire limpio");
                barraTiempo.setMaximum(30);
                timer.start();
                banco.encenderSolenoideUno(true);
                banco.encenderBombaMuestras(true);
                barraTiempo.setStringPainted(true);
                while (contadorTimer < 31) {
                    System.out.println("Contando .." + contadorTimer);
                    barraTiempo.setValue(contadorTimer);
                    barraTiempo.setString(String.valueOf(contadorTimer).concat("%."));
                    Thread.sleep(120);
                    if (contadorTimer > 27) {
                        labelMensaje.setText("Estabilizando");
                        banco.encenderBombaMuestras(false);
                        banco.encenderSolenoideUno(false);
                    } else if (contadorTimer > 27) {
                        labelMensaje.setText("Enviando Comando de Cero");
                    }
                }
                MedicionGases medicion = banco.obtenerDatos();
                Thread.sleep(300);
                System.out.println("HC IS " + medicion.getValorHC() + " CO: " + medicion.getValorCO() + " CO2: " + medicion.getValorCO2() + " O2:" + medicion.getValorO2());
                if (medicion.getValorHC() > 30 || medicion.getValorCO() > 100 || medicion.getValorCO2() > 10 || medicion.getValorO2() < 180) {
                    PanelCero.calibradoCero = 1;
                } else {
                    PanelCero.calibradoCero = 0;
                    banco.ordenarCalibracion(0);//Pone a cero los valores de HC,CO,CO2
                    banco.ordenarCalibracion(1);//calibra usando un solo punto el valor de O2 
                }
                labelMensaje.setText("Cero Terminado");
                timer.stop();
            } else if (banco instanceof BancoCapelec) {
                labelMensaje.setText("Ingresando aire limpio");
                barraTiempo.setMaximum(30);

                timer.start();
                banco.encenderSolenoideUno(true);
                banco.encenderBombaMuestras(true);
                while (contadorTimer < 31) {
                    System.out.println("Contando .." + contadorTimer);
                    barraTiempo.setValue(contadorTimer);
                    barraTiempo.setString(String.valueOf(contadorTimer).concat("%."));
                    Thread.sleep(120);
                    if (contadorTimer > 27) {
                        labelMensaje.setText("Estabilizando");
                        banco.encenderBombaMuestras(false);
                        banco.encenderSolenoideUno(false);
                    } else if (contadorTimer > 27) {
                        labelMensaje.setText("Enviando Comando de Cero");
                        barraTiempo.setString(" ");
                    }
                }
                banco.ordenarCalibracion(0);//Pone a cero los valores de HC,CO,CO2
                banco.ordenarCalibracion(1);//calibra usando un solo punto el valor de O2
                labelMensaje.setText("SE HA CONCLUIDO EL CERO AL BANCO");
                timer.stop();
            } else if (banco instanceof BancoHoriba) {

                ((BancoHoriba) banco).llamarRutinaCero();
                Thread.sleep(2500);
                timer.start();
                MedicionGases medicion = banco.obtenerDatos();

                while (medicion.isCeroEnProgreso()) {
                    Thread.sleep(1000);
                    medicion = banco.obtenerDatos();
                    barraTiempo.setValue(contadorTimer);
                    barraTiempo.setValue(contadorTimer);
                    barraTiempo.setString(String.valueOf(contadorTimer).concat("%."));
                }
                timer.stop();
            }
            barraTiempo.setString(" ");
        } catch (NullPointerException ne) {
            System.out.println("Error comunicandose con el banco");
            ne.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (RuntimeException ie) {
            labelMensaje.setText("Se desconecto el periferico, se debe rehacer la prueba");
            try {
                Thread.sleep(3000L);
                throw new InterruptedException();
            } catch (Exception e) {
            }
        }

    }

    public void setBanco(BancoGasolina banco) {
        this.banco = banco;
    }

    public void actionPerformed(ActionEvent e) {
        barraTiempo.setValue(contadorTimer++);
    }

    public static void main(String args[]) {
        //JFrame app = new JFrame();
        PanelCero panelC = new PanelCero();
        //app.add(panelC);

        //app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //app.setVisible(true);
        //app.pack();
        //app.setLocationRelativeTo(null);
        panelC.iniciarProceso();
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
