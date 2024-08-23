/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import eu.hansolo.steelseries.tools.BackgroundColor;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;

import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.Mensajes;

/**
 * Clase para implementar la prueba de gasolina usando el kit centralauto el
 * banco debe estar configurado, es decir con su puerto serial y los flujos de
 * entrada salida. Se abre el puerto serial para el kit CentralAuto y se debe
 * cerrar al finalizar la prueba
 *
 * @author GerenciaDesarrollo
 */
public class CallableValidadorPruebas implements Callable<List<String>> {

    private final PanelPruebaGases panel;
    private MedidorRevTemp medidorRevTemp;
    // private SerialPort puerto;
    private Timer timer;
    private int contadorTemporizacion;
    private final BancoGasolina banco;
    private Boolean converCatalitico;
    String forMed;
    Integer tempStored = 0;
    private SimuladorRpm simuladorRpm;//clase para simular las revoluciones
    boolean simulacion =false;
     private Future futureSimulador;//Future para monitorear la tarea del simulador

    public CallableValidadorPruebas(PanelPruebaGases panel, BancoGasolina banco, MedidorRevTemp medRevTemp,boolean simulacion) {
        this.panel = panel;
        this.banco = banco;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
         if (simulacion == false) {
           this.medidorRevTemp = medRevTemp;
        } else {
              simuladorRpm = new SimuladorRpm();
            medidorRevTemp = simuladorRpm;
            
        }
         this.simulacion=simulacion; 
        panel.getButtonRpm().setEnabled(false);
    }
    @Override
    public List<String> call() throws Exception {
       
        try {
            if (this.simulacion == true) {
                futureSimulador = iniciarSimulador();//Inicia el hilo simulador de las revoluciones                 
            }
            
            int rpm = medidorRevTemp.getRpm();
            int temp = medidorRevTemp.getTemp();
            int LIM_TEMP = 0;
            panel.getButtonRpm().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelFiguras().setVisible(false);
            panel.getMensaje().setText("");
            panel.getProgressBar().setString("");
            panel.getProgressBar().setValue(0);

            //Abrir el puerto serial para el kit centralAto        
            Object objSeleccion = JOptionPane.showInputDialog(
                    null,
                    "¿Como va a Medir la temperatura ?",
                    "SART 1.7.3",
                    JOptionPane.QUESTION_MESSAGE,
                    null, // null para icono defecto
                    new Object[]{"Temperatura de Aceite", "Temperatura de Bloque", "Automovil con Convertidor Catalitico o temperatura indeterminable"},
                    null);

            String strSeleccion = (String) objSeleccion;
            switch (strSeleccion) {
                case "Temperatura de Aceite":
                    LIM_TEMP = BancoGasolina.LIM_TEMP;
                    forMed = "A";
                    converCatalitico = false;
                    break;
                case "Temperatura de Bloque":
                    LIM_TEMP = BancoGasolina.LIM_TEMP_BLOQUE;
                    forMed = "B";
                    converCatalitico = false;
                    break;
                default:
                    int result = medicionCatalitico();
                    if (result == 0) {
                        LIM_TEMP = 0;
                    }
                    forMed = "C";
                    converCatalitico = true;
                    break;
            }// fin del switch
            if (!(strSeleccion.equalsIgnoreCase("Automovil con Convertidor Catalitico o temperatura indeterminable"))) {
                Mensajes.messageWarningTime("INICIANDO VALIDACION DE TEMPERATURA SEGUN LA NORMA DE LA  NTC 4983  ", 5);
                temp = medidorRevTemp.getTemp();
                if (temp < LIM_TEMP) {
                    panel.getPanelMensaje().setText(" TEMPERATURA ACTUAL ES " + temp + "  \n ESPERANDO QUE ALCANZE NIVEL MINIMO DE TEMPERATURA ");
                    Thread.sleep(20000);
                    temp = medidorRevTemp.getTemp();
                    if (temp < LIM_TEMP) {
                        panel.getPanelMensaje().setText(" TEMPERATURA MINIMA NO ALCANZADA SE CUMPLE CON EL NUMERAL  4.1.3.6 DE LA  NTC 4983");
                        Thread.sleep(2500);
                        int result = medicionCatalitico();
                        if (result == 0) {
                            LIM_TEMP = 0;
                        }
                    }
                    tempStored = medidorRevTemp.getTemp();
                } else {
                    tempStored = medidorRevTemp.getTemp();
                }
            }
            if (LIM_TEMP == 0) {
                forMed = "C";
                tempStored = 0;
                converCatalitico = true;
            }
        } catch (InterruptedException ex) {     }
        List<String> lstValMisc = new ArrayList();
        lstValMisc.add(forMed);
        lstValMisc.add(tempStored.toString());
        lstValMisc.add(converCatalitico.toString());
        if (this.simulacion == true) {
            simuladorRpm.setDetener(true);//detiene el simulador  
            simuladorRpm.stop();
            this.futureSimulador.cancel(true);
        }
        return lstValMisc;
    }
    private Future iniciarSimulador() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(simuladorRpm);
    }
    private int medicionCatalitico() 
    {
        try 
        {
            contadorTemporizacion = 0;
            int revoluciones = medidorRevTemp.getRpm();
            double temperatura = medidorRevTemp.getTemp();
            boolean aceleracionTerminada = false;
            panel.getButtonFinalizar().setVisible(true);
            panel.getButtonRpm().setText("Rechazar Por RPM");
            panel.getButtonRpm().setVisible(LeerArchivo.rechazarPorRpm());
            panel.getButtonRpm().setEnabled(LeerArchivo.rechazarPorRpm());
            panel.getPanelMensaje().setText("POR FAVOR ACELERE  A 2500 RPM  DURANTE DOS MINUTOS ");
            Thread.sleep(2800);
            panel.getPanelFiguras().setVisible(true);
            panel.getMensaje().setText("Por favor MANTENGA el Vehiculo en Crucero durante 2 minutos..!");
            while (!aceleracionTerminada) {
                //while(revoluciones >= 2250 && revoluciones <= 2750){
                while ((medidorRevTemp.getRpm() <= BancoGasolina.LIM_CRUCERO - 250) && medidorRevTemp.getRpm() <= 2750) {
                    Thread.sleep(500);
                    revoluciones = medidorRevTemp.getRpm();
                    temperatura = medidorRevTemp.getTemp();
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                    panel.getRadialTacometro().setValue(revoluciones);
                    panel.getLinearTemperatura().setValue(temperatura);
                }
                timer.start();
                contadorTemporizacion = 0;
                //panel.getPanelMensaje().setText("MANTENGA LAS REVOLUCIONES");
                panel.getProgressBar().setMaximum(120);
                panel.getProgressBar().setVisible(true);
                //while( contadorTemporizacion < 120 && revoluciones >= 2250 && revoluciones <=2750){
                while (medidorRevTemp.getRpm() >= (BancoGasolina.LIM_CRUCERO - 250) && (contadorTemporizacion < 120) && medidorRevTemp.getRpm() <= 2750) {//recuerda blinker != null
                    Thread.sleep(1000);
                    revoluciones = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                    panel.getRadialTacometro().setValue(revoluciones);
                    panel.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat("Seg."));
                    panel.getProgressBar().setStringPainted(true);
                    panel.getProgressBar().setValue(contadorTemporizacion);
                }

                //panel.getPanelMensaje().setText("ACELERACION TERMINADA");
                panel.getProgressBar().setString(String.valueOf("ACELERACION TERMINADA"));
                  panel.getMensaje().setText("..!");
                if (contadorTemporizacion >= 120) {
                    aceleracionTerminada = true;
                    return 0;//se toma cualquiera temperatura
                }
            }//fin del while aceleracion terminada

        } catch (InterruptedException ex) {
        }
        return 1;
    }
}
