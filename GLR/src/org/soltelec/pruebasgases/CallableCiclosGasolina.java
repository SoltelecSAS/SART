/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import eu.hansolo.steelseries.tools.BackgroundColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.PanelCero;
import org.soltelec.pruebasgases.motocicletas.CallableInicioMotos;

import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.GenerarArchivo;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.Utilidades;
import org.soltelec.util.capelec.BancoCapelec;

/**
 * Clase para implementar la prueba de gasolina usando el kit centralauto el
 * banco debe estar configurado, es decir con su puerto serial y los flujos de
 * entrada salida. Se abre el puerto serial para el kit CentralAuto y se debe
 * cerrar al finalizar la prueba
 *
 * @author Gerencia TIC
 */
public class CallableCiclosGasolina implements Callable<List<List<MedicionGases>>> {

    private final PanelPruebaGases panel;
    private final MedidorRevTemp medidorRevTemp;
    // private SerialPort puerto;
    private final Timer timer;
    private Timer timer2 = null;
    private Timer timerSimulacion;
    private int contadorTemporizacion, contVerHumo;
    private final long idPrueba, idUsuario, idHojaPrueba;
    private final BancoGasolina banco;
    private final List<MedicionGases> listaCrucero;
    private final List<MedicionGases> listaRalenti;
    private final int INTENTOS = 3;
    public static String humo;
    private static final double LIMITE_HC = 20;
    private static final double LIMITE_OXIGENO = 5;
    private static final double LIMITE_DIOXIDO = 7;
    private int contador;
    private boolean reiniciarProceso = false;
    private boolean converCatalitico;
    private PanelVerificacion pv;
    String forMed;
    Integer tempStored = 0;
    public static int rngIniRpm = 0;
    public static int rngFinRpm = 0;
    private String placas;
    List<MedicionGases> lista50Datos = null;
    List<MedicionGases> lista10Datos = null;

    private SimuladorRpm simuladorRpm;//clase para simular las revoluciones
    boolean simulacion = false;
    private Future futureSimulador;//Future para monitorear la tarea del simulador

    FileWriter fw = null, fw2 = null;
    BufferedWriter bwP, bwP2;
    DecimalFormat df;
    String out = ""/*,out2 =""*/;
//Condiciones Anormales Encotradas .- 40103071 Existencia de fugas en el tubo, uniones del múltiple y silenciador del sistema de escape del vehículo; 

    public CallableCiclosGasolina(PanelPruebaGases panel, BancoGasolina banco, MedidorRevTemp medRevTemp, long idPrueba, long idUsuario, long idHojaPrueba, String forMed, Integer tempStored, boolean converCatalitico, String placas, boolean simulacion, double tempAmbiente, double humedadAmbiente) {
        this.panel = panel;
        this.banco = banco;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        listaCrucero = new ArrayList<>();
        listaRalenti = new ArrayList<>();
        if (simulacion == false) {
            this.medidorRevTemp = medRevTemp;
        } else {
            simuladorRpm = new SimuladorRpm();
            medidorRevTemp = simuladorRpm;
        }
        this.simulacion = simulacion;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.idHojaPrueba = idHojaPrueba;
        this.forMed = forMed;
        this.tempStored = tempStored;
        this.converCatalitico = converCatalitico;
        this.placas = placas;
        panel.getButtonRpm().setEnabled(false);
    }

    @Override
    public List<List<MedicionGases>> call() throws Exception {
        try {
            int rpm;
            int temp;
            //Abrir el puerto serial para el kit centralAto        
            panel.getPanelMensaje().setText(" ");
            panel.getPanelFiguras().setVisible(false);
            if (this.simulacion == true) {
                futureSimulador = iniciarSimulador();//Inicia el hilo simulador de las revoluciones                  
            }
            //Inspeccion Sensorial por parte del operarion
            //Si se indica mediante los checkboxes que hay defectos entonces el programa los registra
            // y sale de la prueba
            pv = new PanelVerificacion();
            JOptionPane.showMessageDialog(null, pv, "SART 1.7.3.- Inspeccion Sensorial", JOptionPane.PLAIN_MESSAGE);
            List<String> defectos = new ArrayList<String>();
            if (pv.isDefectoEncontrado()) {
                System.out.println("Defecto " + pv.getMensaje().toString());
                JOptionPane.showMessageDialog(panel, pv.getMensaje().toString().split("</html>"));
                try {
                    registrarDefectosVisuales();
                } finally {
                    cerrarPuertosHilos();
                }
                panel.getFuncion().setText("Condiciones Anormales");
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            }
            panel.getPanelMensaje().setText("POR FAVOR \n MANTENGA EL VEHICULO EN RALENTI  ");
            Thread.sleep(2500);
            rpm = medidorRevTemp.getRpm();
            if (this.simulacion == true) {
                simuladorRpm.setSimularCrucero(false);//se debe mantener en ralenti
                simuladorRpm.setREVOLUCIONES_RALENTI(450);//se debe mantener en ralenti
            }
            int cont = 0;
//            int rale=0;
            Mensajes.messageWarningTime(" VALIDANDO RPM EN RALENTI SEGUN LA NORMA DE LA  NTC 4983 NUMERAL 4.1.3.9  ", 8);
            while (cont < 4) {
                panel.getPanelMensaje().setText("POR FAVOR ESPERE \n VERIFICANDO RALENTI,VALOR ACTUAL: " + medidorRevTemp.getRpm());
//                if ((medidorRevTemp.getRpm() >= CallableCiclosGasolina.rngIniRpm && medidorRevTemp.getRpm() <= CallableCiclosGasolina.rngFinRpm)) 
                if ((medidorRevTemp.getRpm() >= CallableCiclosGasolina.rngIniRpm && medidorRevTemp.getRpm() <= CallableCiclosGasolina.rngFinRpm)) {
                    int rpmBajo = rpm - 150;
                    int rpmAlto = rpm + 150;
                    if ((medidorRevTemp.getRpm() <= rpmBajo) && (medidorRevTemp.getRpm() >= rpmAlto)) {
                        Mensajes.messageWarningTime(" Se ha detectado REVOLUCIONES INESTABLES se procede a dar cumplimiento a la norma NTC 4983 NUMERAL 4.1.3.9 esta prueba queda RECHAZADA  ", 5);
                        cerrarPuertosHilos();
                        banco.encenderBombaMuestras(false);
                        Thread.sleep(100);
                        System.out.println("RECHAZO DE PRUEBAS POR REVOLUCIONES INESTABLES");
                        panel.getFuncion().setText("3.1.1.1.10 Revoluciones fuera de rango.");
                        List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                        return lecturaRechazada;
                    }
                } else {
//                    Mensajes.messageWarningTime(" Se ha detectado REVOLUCIONES INESTABLES se procede a dar cumplimiento a la norma NTC 4983 NUMERAL 4.1.3.9 esta prueba queda RECHAZADA  " + medidorRevTemp.getRpm(), 5);
                    Mensajes.messageWarningTime(" Se ha detectado REVOLUCIONES INESTABLES se procede a dar cumplimiento a la norma NTC 4983 NUMERAL 4.1.3.9 esta prueba queda RECHAZADA  " + 300, 10);
                    cerrarPuertosHilos();
                    banco.encenderBombaMuestras(false);
                    Thread.sleep(100);
                    System.out.println("RECHAZO DE PRUEBAS POR REVOLUCIONES INESTABLES");
                    panel.getFuncion().setText("3.1.1.1.10 Revoluciones fuera de rango.");
                    List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                    return lecturaRechazada;
                }
                cont++;
                Thread.sleep(2500);
            }
            panel.getPanelMensaje().setText("REVOLUCIONES DENTRO DEL RANGO: " + medidorRevTemp.getRpm());
            Thread.sleep(2000);
//<editor-fold defaultstate="collapsed" desc="Proceso de humos">
//</editor-fold>j        
            //Aceleracion a 2500 rpm para limpiar el exhosto
            //Aceleracion de limpieza
            System.out.println("Ingreso al proceso: HUMO NEGRO");
            panel.getPanelMensaje().setText("HUMO NEGRO \n Por Favor Acelere entre 2250 a 2750 RPM \n DURANTE 20 seg.");
            Thread.sleep(3000);
            panel.getBtnContHumo().setVisible(true);
            panel.getBtnContHumo().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tempVerfHumo.start();
                }
            });



            //AQUI TODO ES IGUAL UNA VEZ CONFIGURADO EL KIT deberia usar polimorfismo
            panel.getPanelMensaje().setVisible(false);
            panel.getPanelMensaje().setText("");
            panel.getPanelFiguras().setVisible(true);
            panel.getButtonFinalizar().setVisible(true);
            panel.getButtonRpm().setText("Rechazar Por RPM");
            panel.getButtonRpm().setVisible(LeerArchivo.rechazarPorRpm());
            panel.getButtonRpm().setEnabled(LeerArchivo.rechazarPorRpm());

            panel.getProgressBar().setVisible(true);
            panel.getProgressBar().setValue(0);
            panel.getProgressBar().setMaximum(20);
            boolean pintarVerde = false;
            boolean pintarAnterior;
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
            timer.start();
            boolean aceleracionHumoNegroTerminada = false;
            panel.getMensaje().setText("Por Favor ACELERE en el Rango de (2250 a 2750) RPM  DURANTE 20 Seg.");
            Thread.sleep(500);

            if (this.simulacion == true) {
                simuladorRpm.setSimularCrucero(true);//se debe mantener en ralenti               
                simuladorRpm.setREVOLUCIONES_CRUCERO(2720);
            }

            panel.getBtnContHumo().setVisible(true);
            while (!aceleracionHumoNegroTerminada) {
                if (tempVerfHumo.isRunning()) {
                    tempVerfHumo.stop();
                    panel.getBtnContHumo().setText("10 Seg.");
                    contVerHumo = 0;
                }
                panel.getProgressBar().setString("0 Seg.");
                panel.getProgressBar().setValue(0);
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                while ((medidorRevTemp.getRpm() <= 2250) && medidorRevTemp.getRpm() <= 2750) {//rango de rpm para avansar el progreso 
                    Thread.sleep(120);
                    panel.getMensaje().setText("Por Favor ACELERE en el Rango de (2250 a 2750) RPM   DURANTE 20 Seg.");
                    if (isSalidaPrueba()) {
                        return null;
                    }
                    if (this.simulacion == true) {
                        panel.getRadialTacometro().setValue(simuladorRpm.getRpm());
                    } else {
                        panel.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    }

                    System.out.println(" RPM X INTERFACE IS " + medidorRevTemp.getRpm());
                    pintarAnterior = pintarVerde;
                    pintarVerde = /*rpmFiltrada*/ medidorRevTemp.getRpm() >= 2250 && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= 2750; //plot.
                    if (pintarAnterior != pintarVerde) {//necesidad de repintar
                        if (pintarVerde) {
                            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        } else {
                            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        }
                    }//end if
                }//end while de calentamiento temperatura y alcanzar revoluciones

                System.out.println(" RPM X INTERFACE IS " + medidorRevTemp.getRpm());
                contadorTemporizacion = 0;
                while (medidorRevTemp.getRpm() >= (BancoGasolina.LIM_CRUCERO - 250) && (contadorTemporizacion <= 20) && medidorRevTemp.getRpm() <= 2750) {//recuerda blinker != null
                    if (contadorTemporizacion <= 20) {
                        panel.getProgressBar().setValue(contadorTemporizacion);
                        panel.getProgressBar().setStringPainted(true);
                        panel.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat(" Seg."));
                        panel.getMensaje().setText(" Aceleracion se MANTIENE en el rango  de (2250 a 2750) RPM ");
                    }
                    Thread.sleep(120);
                    if (isSalidaPrueba()) {
                        return null;
                    }
                    if (this.simulacion == true) {
                        panel.getRadialTacometro().setValue(simuladorRpm.getRpm());
                    } else {
                        panel.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    }
                    pintarAnterior = pintarVerde;
                    if (/*rpmFiltrada*/medidorRevTemp.getRpm() >= 2250 && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= 2750) {
                        pintarVerde = true;
                        //plot.
                    } else {
                        pintarVerde = false;
                    }
                    if (pintarAnterior != pintarVerde) {//necesidad de repintar
                        if (pintarVerde) {
                            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        } else {
                            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        }
                    }//end if
                }//end while de calen               
                if (contadorTemporizacion >= 20) {
                    aceleracionHumoNegroTerminada = true;
                }
            }
            if (isSalidaPrueba()) {
                return null;
            }
            panel.getPanelMensaje().setText(" HUMO NEGRO \nPOR FAVOR  MANTENGA EL VEHICULO EN RALENTI \n DURANTE 20 Seg.");
            if (this.simulacion == true) {
                simuladorRpm.setSimularCrucero(true);
                simuladorRpm.setREVOLUCIONES_CRUCERO(1100);
            }
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getProgressBar().setVisible(false);
            panel.getButtonFinalizar().setVisible(false);
            panel.getButtonRpm().setVisible(true);
            panel.getBtnContHumo().setVisible(false);
            panel.getMensaje().setText("");
            Thread.sleep(3000);

            panel.getProgressBar().setValue(0);
            panel.getProgressBar().setString("");
            aceleracionHumoNegroTerminada = false;
            panel.getPanelMensaje().setVisible(false);
            panel.getPanelFiguras().setVisible(true);
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
            Thread.sleep(120);            //
            rpm = medidorRevTemp.getRpm();
            temp = medidorRevTemp.getTemp();
            timer.start();
            panel.getButtonFinalizar().setVisible(true);
            //panel.getButtonRpm().setVisible(true);
            panel.getBtnContHumo().setVisible(true);
            panel.getProgressBar().setVisible(true);
            panel.getButtonRpm().setVisible(true);
            aceleracionHumoNegroTerminada = false;
            if (tempVerfHumo.isRunning()) {
                tempVerfHumo.stop();
                panel.getBtnContHumo().setText("10 Seg.");
                contVerHumo = 0;
            }
            while (!aceleracionHumoNegroTerminada) {
                panel.getMensaje().setText("Por Favor MANTENGA RALENTI  entre " + CallableCiclosGasolina.rngIniRpm + " a " + CallableCiclosGasolina.rngFinRpm + " RPM \n DURANTE 20 Seg.");
                panel.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                panel.getProgressBar().setString("0 Seg.");
                panel.getProgressBar().setValue(0);
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                Thread.sleep(120);
                if (tempVerfHumo.isRunning()) {
                    tempVerfHumo.stop();
                    panel.getBtnContHumo().setText("10 Seg.");
                    contVerHumo = 0;
                }
                if (isSalidaPrueba()) {
                    return null;
                }
                contadorTemporizacion = 0;
                while (medidorRevTemp.getRpm() >= CallableCiclosGasolina.rngIniRpm && (contadorTemporizacion <= 20) && (medidorRevTemp.getRpm() <= CallableCiclosGasolina.rngFinRpm)) {//recuerda blinker != null
                    if (contadorTemporizacion <= 20) {
                        panel.getProgressBar().setVisible(true);
                        panel.getMensaje().setText("El Ralenti  se MANTIENE en el Rango de (" + CallableCiclosGasolina.rngIniRpm + " a " + CallableCiclosGasolina.rngFinRpm + ") RPM ");
                        panel.getProgressBar().setValue(contadorTemporizacion);
                        panel.getProgressBar().setStringPainted(true);
                        panel.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat(" Seg."));
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        if (isSalidaPrueba()) {
                            return null;
                        }
                    }
                    Thread.sleep(50);
                    panel.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    Thread.sleep(120);

                }//end while de calen                
                if (contadorTemporizacion >= 20) {
                    aceleracionHumoNegroTerminada = true;
                }
                panel.getProgressBar().setVisible(false);
            }
            if (tempVerfHumo.isRunning()) {
                tempVerfHumo.stop();
            }
            if (isSalidaPrueba()) {
                return null;
            }
            panel.getButtonFinalizar().setVisible(false);
            panel.getButtonRpm().setVisible(false);
            panel.getBtnContHumo().setVisible(false);
            panel.getMensaje().setText(" ");
            panel.getProgressBar().setVisible(false);
            int opcion = JOptionPane.showOptionDialog(null, "¿Visualizo Presencia de Humo? y si es asi ¿Cual fue su Color?", "SART 1.7.3 ", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Negro", "Azul", "NO"}, "NO");
            if (opcion == JOptionPane.YES_OPTION) {
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                System.out.println("Selecciono Humo negro");
                humo = "negro";
                JOptionPane.showMessageDialog(panel, "En Cumplimiento al Numeral NTC 4.1.3.10 esta prueba sera RECHADA  por presentar HUMO NEGRO");
                panel.getFuncion().setText("3.1.1.1.9 Presencia de humo negro o azul (Negro)");

                //modificacion 3/12/20 ELKIN B 
//                registrarDefectoHumo("Condicion anormal 4.1.1.7 : presencia de humo negro");
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;

            } else if (opcion == JOptionPane.NO_OPTION) {
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                System.out.println("Selecciono Humo Azul");
                humo = "azul";
                JOptionPane.showMessageDialog(panel, "En Cumplimiento al Numeral NTC 4.1.3.10 esta prueba sera RECHADA  por presentar HUMO AZUL");
                panel.getFuncion().setText("3.1.1.1.9 Presencia de humo negro o azul (Azul)");

                //modificacion 3/12/20 ELKIN B 
//                registrarDefectoHumo("Condicion anormal 4.1.1.7 : presencia de humo Azul");
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;

            } else {
                //no haga nada
            }
            MedicionGases medicion;
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelFiguras().setVisible(false);
            panel.getButtonRpm().setVisible(false);
            panel.getProgressBar().setVisible(true);
            panel.getBtnContHumo().setVisible(false);
            if (isSalidaPrueba()) {
                return null;
            }

            while (!reiniciarProceso) {
                PanelCero panelCero = new PanelCero();
                panel.getPanelMensaje().setText("Por Favor Espere. \n Haciendo Cero en el Banco ...!");
                panelCero.setBanco(banco);
                panelCero.setBarraTiempo(this.panel.getProgressBar());
                panelCero.setLabelMensaje(this.panel.getPanelMensaje().getCronometro());

                Thread t1 = new Thread(panelCero);
                t1.start();
                try {
                    t1.join();
                    //Realizar calibracion de cero//sincronizar con hilo principal
                } catch (InterruptedException ex) {
                    System.out.println("Error haciendo join con hilo de cero");
                }

                panel.getProgressBar().setVisible(false);
                panel.setVisible(true);
                //lista calibracion de cero
                panel.getPanelMensaje().setText("Esperando a que el Equipo de Medicion\n  Registre " + "HC menor que 20 ....");
                medicion = banco.obtenerDatos();
                //Enviar comando de comando de encender bomba para tomar lectura de HC
                banco.encenderBombaMuestras(true);
                System.out.println("Encendiendo Bomba");
                // medicion = banco.obtenerDatos();
                contadorTemporizacion = 0;
                crearNuevoTimer();
                timer2.start();
                panel.getPanelMensaje().setText("La Medicion de HC \n Es Mayor a 20 ppm.");
                panel.getPanelMensaje().getCronometro().setText("Tomando mediciones... " + contadorTemporizacion);
                Thread.sleep(1600);//Simplemente para que sea visible
                /*DISMINUCION DE HC*///
                //Cuando disminuye sale del while
                while (medicion.getValorHC() > LIMITE_HC && contadorTemporizacion < 150) {//repita mientras el valor sea mayor a 20 ppm
                    medicion = banco.obtenerDatos();//tome medicion
                    //kiss no timer
                    Thread.sleep(120);
                    System.out.println("HC mayor a 20 ppm intentos" + contador + "");
                    panel.getPanelMensaje().getCronometro().setText("HC Mayor a 20 ppm, esperando...! " + contadorTemporizacion);
                }//HC ha bajado de los 20 ppm o tiempo de espera cumplido
                timer2.stop();
                if (contadorTemporizacion >= 149) {//si el tiempo de espera cumplido
                    panel.getPanelMensaje().setText("Verifique los Filtros");
                    System.out.println("Posibles filtros sucios en la linea de muestra");
                    int opcionSeleccionada = JOptionPane.showConfirmDialog(panel, "Posibles filtros sucios en la linea de muestra \n Cancelar la prueba?", "", JOptionPane.YES_NO_OPTION);
                    switch (opcionSeleccionada) {
                        case JOptionPane.NO_OPTION://seguir intentando que baje el HC
                            reiniciarProceso = false;
                            break;
                        case JOptionPane.YES_OPTION://volver a la pantalla de Prueba/Servicio
                            //TODO motivo cancelacion prueba registrar
                            Window w = SwingUtilities.getWindowAncestor(panel);
                            //Window w1 = SwingUtilities.getWindowAncestor(w);
                            w.dispose();
                            //if(w1 != null)
                            //w1.dispose();
                            //timer.stop();
                            banco.getPuertoSerial().close();
                            timer.stop();
                            panel.cerrar();
                            ////System.gc();
                            return null;//sale del metodo run
                        case JOptionPane.CANCEL_OPTION:
                            break;
                    }//end switch
                }//end if
                else {
                    reiniciarProceso = true;
                }
            }
            //<editor-fold defaultstate="collapsed" desc="ZERO">

            Thread.sleep(10);//punto de cancelacion
            panel.getPanelMensaje().setText("HC menor a 20 ppm ...\n continua prueba");
            panel.getPanelMensaje().getCronometro().setText("La medicion de HC ha disminuido");
            Thread.sleep(1000);
            banco.encenderBombaMuestras(false);
            //flujo exitoso del proceso
            //</editor-fold>
            //SONDA DE MUESTRA
            System.out.println("Ingreso al proceso: SONDA MUESTRA");
            panel.getPanelFiguras().setVisible(false);
            panel.getButtonRpm().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelMensaje().setText("Sonda de Muestra");
            Thread.sleep(1500);
            JOptionPane.showMessageDialog(panel, "Por Favor INTRODUZCA la sonda en el Tubo de Escape");
            banco.encenderBombaMuestras(true);
            panel.getPanelMensaje().setText("El nivel de O2 debe disminuir a menos de 5%");
            Thread.sleep(1500);
            //DISMINUCION DE OXIGENO
            System.out.println("Ingreso al proceso: DISMINUCION DE OXIGENO");
            int intentos = 0;

            medicion = banco.obtenerDatos();//TODO descomentar banco
            //Thread.sleep(1500); //sugerencia colocar tiempo de espera
            intentos = 0;

            panel.getProgressBar().setValue(0);
            panel.getProgressBar().setString(String.valueOf(0));
            boolean oxigenoDisminuyo = false;
            while (!oxigenoDisminuyo && intentos < 2) {
                int contadorO2 = 0;
                while (medicion.getValorO2() * 0.01 > BancoGasolina.LIMITE_OXIGENO && contadorO2 < BancoGasolina.RETARDOO2) {
                    Thread.sleep(420);
                    contadorO2++;
                    panel.getPanelMensaje().getCronometro().setText("Tiempo transcurrido: " + String.valueOf(contadorO2 / 2) + "Seg.");
                    medicion = banco.obtenerDatos();
                }//end of while O2 mayor a 21
                if (contadorO2 >= BancoGasolina.RETARDOO2) {
                    // throw new DilucionException();
                    oxigenoDisminuyo = true;
                } else {
                    oxigenoDisminuyo = true;
                }//end else
            }//end while

            //PRUEBA DE CRUCERO
            System.out.println(">>INICIO PRUEBA CRUCERO>>");
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            if (this.simulacion == true) {
                simuladorRpm.setSimularCrucero(true);//se debe mantener en ralenti                
                simuladorRpm.setREVOLUCIONES_CRUCERO(2700);
            }
            panel.getPanelMensaje().setText("INICIO DE LA PRUEBA DE CRUCERO\n " + " POR FAVOR ACELERE HASTA 2500 rpm  \n Y  MANTENGALO DURANTE 30 Seg.");
            Thread.sleep(3500);
            //Iniciar el timer
            panel.getMensaje().setText("POR FAVOR ACELERE HASTA 2500 rpm y MANTENGALO DURANTE 30 Seg.");
            panel.getPanelMensaje().setVisible(false);
            panel.getPanelFiguras().setVisible(true);
            panel.getButtonFinalizar().setVisible(true);
            panel.getButtonRpm().setVisible(true);
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            intentos = 0;
            timer.start();
            this.lista50Datos = new ArrayList<>();
            this.lista10Datos = new ArrayList<>();
            while (intentos < INTENTOS) {
                String mensajeProblemas = "";
                medicion = banco.obtenerDatos();
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                temp = medidorRevTemp.getTemp();
                panel.getLinearTemperatura().setValue(temp);
                panel.getProgressBar().setString("0 Seg.");
                panel.getProgressBar().setValue(0);
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                boolean dioxValidad = false;
                int nroInt = 1;
                //MIENTRAS EXISTA DILUCION BAJO FLUJO O REVOLUCIONES FUERA DE RANGO
                //   O TEMPERATURA menor a 60 grados y no hayan transcurrido tres minutos
                //|| temp < LIM_TEMP
                while ((rpm < 2250 || rpm > 2750) && contadorTemporizacion < BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                    mensajeProblemas = "";
                    Thread.sleep(120);

                    while (dioxValidad == false) {
                        medicion = banco.obtenerDatos();
                        Thread.sleep(100);
                        dioxValidad = validadorO2(18 - 1, medicion.getValorO2() * 0.01);
                        if (dioxValidad == true) {
                            timer.start();
                            break;
                        }
                    }
                    if (rpm <= (BancoGasolina.LIM_CRUCERO - 250) || rpm >= (BancoGasolina.LIM_CRUCERO + 250)) { //2300 y 2700
                        mensajeProblemas += "SE DETECTA REVOLUCIONES FUERA DE RANGO; ";
                    }
                    if (medicion.isBajoFlujo()) {
                        mensajeProblemas += " SE DETECTA BAJO FLUJO; ";
                    }
                    
                    
                    
                    Connection conexion = null;
                    try {
                        conexion = Conex.getConnection();
                        PreparedStatement instruccion = conexion.prepareStatement("SELECT v.FUELTYPE FROM vehiculos v "
                                + "INNER JOIN hoja_pruebas hp ON hp.Vehiculo_for = v.CAR "
                                + "WHERE hp.TESTSHEET=?");
                        instruccion.setLong(1, idHojaPrueba);
                        ResultSet resultSet = instruccion.executeQuery();

                        while (resultSet.next()) {
                            String fuelType = resultSet.getString("FUELTYPE");
                            // Haz algo con el fuelType obtenido, por ejemplo, imprimirlo
                            System.out.println("---------------------------------------------------------------------FUELTYPE: " + fuelType);
                        }
                    } catch (SQLException sqlex) {
                        JOptionPane.showMessageDialog(null, "Error al ejecutar la consulta: " + sqlex.getMessage());
                        Logger.getLogger(this.getClass()).error("Error al ejecutar la consulta", sqlex);
                    } catch (ClassNotFoundException cexc) {
                        JOptionPane.showMessageDialog(null, "Falta el driver de mysql");
                        Logger.getLogger(this.getClass()).error("Falta el driver de mysql", cexc);
                    } finally {
                        try {
                            if (conexion != null) {
                                conexion.close();
                            }
                        } catch (SQLException e) {
                            Logger.getLogger(this.getClass()).error("Error al cerrar la conexión", e);
                            e.printStackTrace();
                        }
                    }
                    
                    
                    
                    
                    
                    if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 < LIMITE_DIOXIDO) {
                        mensajeProblemas += "";
                    }
                    if (mensajeProblemas.length() > 0) {
                        panel.getMensaje().setText(mensajeProblemas + "  T " + contadorTemporizacion + " Seg. (POR FAVOR  ACELERE ENTRE 2250 Y 2750 RPM )");
                    } else {
                        panel.getMensaje().setText("POR FAVOR  ACELERE ENTRE 2250 Y 2750 RPM  MANTENGALO DURANTE 30 Seg.");
                    }
                    if (isSalidaPrueba()) {
                        return null;
                    }
                    medicion = banco.obtenerDatos();
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    temp = medidorRevTemp.getTemp();
                    panel.getLinearTemperatura().setValue(temp);
                    Thread.sleep(110);
                    mensajeProblemas = "";
                }//end while
                if (isSalidaPrueba()) {
                    return null;
                }
                Thread.sleep(10);//punto de salida
                contadorTemporizacion = 0;
                panel.getProgressBar().setMaximum(30);
                panel.getProgressBar().setValue(30);
                panel.getProgressBar().setString("30 Seg.");
                medicion = banco.obtenerDatos();
                double oxigeno = medicion.getValorO2() * 0.01;
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
                Thread.sleep(100);//punto de salida
                //Condiciones OK
                //&& temp >= LIM_TEMP
                panel.getProgressBar().setVisible(true);
                while (rpm >= 2250 && rpm <= 2750 && contadorTemporizacion < 30) {
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                    medicion = banco.obtenerDatos();
                    medicion.setValorTAceite(tempStored);
                    medicion.setConvertidorCatalitico(converCatalitico);
                    medicion.setValorRPM(rpm);

                    while (dioxValidad == false) {
                        medicion = banco.obtenerDatos();
                        Thread.sleep(100);
                        dioxValidad = validadorO2(18 - 1, medicion.getValorO2() * 0.01);
                        if (dioxValidad == true) {
                            timer.start();
                            break;
                        }
                    }

                    if (contadorTemporizacion >= 25) {
                        panel.getMensaje().setText("Capturando Datos del Banco..!");
                        medicion.setLect5Seg(true);
                        this.lista10Datos.add(medicion);  //se activa cuando hayan auditoria ambientales 
                        this.lista10Datos.add(medicion); // COMPLETAR LST 2 DATOS X SEG DADO QUE AL TENER DOS RESOURCES 
                    } else {
                        medicion.setLect5Seg(false);
                        this.lista50Datos.add(medicion);  //se activa cuando hayan auditoria ambientales 
                        this.lista50Datos.add(medicion);
                        if (medicion.isBajoFlujo()) {
                            panel.getMensaje().setText("ADVERTENCIA SE DETECTA BAJO FLUJO, Manteniendo Crucero (2250 a 2750) RPM  en  " + intentos + " Intentos");
                        } else {
                            panel.getMensaje().setText("Manteniendo Crucero (2250 a 2750) RPM  en  " + intentos + " Intentos");
                        }
                    }

                    panel.getProgressBar().setValue(30 - contadorTemporizacion);
                    panel.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion).concat(" Seg."));
                    oxigeno = medicion.getValorO2() * 0.01;
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    temp = medidorRevTemp.getTemp();
                    panel.getLinearTemperatura().setValue(temp);
                    /*rpmFiltrada = filtroRPM(revolucionesSimuladas);*/
                }//end while condiciones ok
                Thread.sleep(10);//punto de cancelacion
                if (contadorTemporizacion < 30 || medicion.isBajoFlujo()/* || oxigeno >= BancoGasolina.LIMITE_OXIGENO*/) {
                    intentos++;
                } else {
                    break;
                }
            }//end while intentos
            if (isSalidaPrueba()) {
                return null;
            }
            Thread.sleep(100);
            if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 < LIMITE_DIOXIDO) {
                panel.getMensaje().setText("SE HA DETECTADO DILUCION EN LA MUESTRA ");
                Thread.sleep(1000);
                panel.getFuncion().setText("DILUCION DE MUESTRA");
            }
            //Pasar los throws segun validaciones
            if (intentos >= INTENTOS) {
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                timer.stop();
                if (isSalidaPrueba()) {
                    return null;
                }
                if (medicion.isBajoFlujo()) {
                    Mensajes.messageWarningTime(" Se ha detectado BAJO FLUJO en el Proceso; Esta prueba sera ABORTADA", 5);
                    panel.getFuncion().setText("BAJO FLUJO DEL EQUIPO");
                    panel.getFuncion().setText("abortado");
                }

                if (medidorRevTemp.getRpm() > (BancoGasolina.LIM_CRUCERO)) {
                    Mensajes.messageWarningTime("  Se ha detectado REVOLUCIONES POR ENCIMA  de lo establecido por la norma NTC 4983 (Prueba Crucero) \n esta prueba queda RECHAZADA  ", 5);
                    panel.getFuncion().setText("rechazo");
                    panel.getFuncion().setText("3.1.1.1.10 Revoluciones fuera de rango.");
                }
                if (medidorRevTemp.getRpm() < 2100) {
                    Mensajes.messageWarningTime("  Se ha detectado REVOLUCIONES POR DEBAJO  de lo establecido por la norma NTC 4983 (Prueba Crucero) \n esta prueba quedara RECHAZADA  ", 5);
                    panel.getFuncion().setText("rechazo");
                    panel.getFuncion().setText("3.1.1.1.10 Revoluciones fuera de rango.");
                }
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            }//end if intentos
            System.out.println("<<FIN PRUEBA CRUCERO>>");
            generarArchivoGases(this.placas, banco, 0);
            sacarPromMedidas(0);

            //PRUEBA DE RALENTI
            System.out.println("\n>>INICIO PRUEBA RALENTI>>");
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getProgressBar().setVisible(true);
            panel.getMensaje().setText("");
            panel.getProgressBar().setValue(0);
            panel.getProgressBar().setString("");
            panel.getPanelMensaje().setText("INICIO DE PRUEBA DE RALENTI");
            Thread.sleep(1800);
            panel.getPanelMensaje().setVisible(false);
            panel.getPanelFiguras().setVisible(true);
            panel.getButtonRpm().setVisible(true);
            intentos = 0;
            this.lista50Datos = new ArrayList<>();
            this.lista10Datos = new ArrayList<>();
            if (this.simulacion == true) {
                simuladorRpm.setSimularCrucero(true);//se debe mantener en ralenti                
                simuladorRpm.setREVOLUCIONES_CRUCERO(1100);
            }
            while (intentos < INTENTOS) {
                boolean dioxValidad = false;
                //simuladorRpm.setSimularCrucero(false);
                if (isSalidaPrueba()) {
                    return null;
                }
                contadorTemporizacion = 0;
                panel.getProgressBar().setString("0 Seg.");
                panel.getProgressBar().setValue(0);
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                medicion = banco.obtenerDatos();
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                temp = medidorRevTemp.getTemp();
                panel.getLinearTemperatura().setValue(temp);
                //ESPERAR MIENTRAS LAS CONDICIONES DE LA PRUEBA SEAN INAPROPIADAS
                Thread.sleep(77);
                while ((rpm < CallableCiclosGasolina.rngIniRpm || rpm > (CallableCiclosGasolina.rngFinRpm - 50) /*|| temp < LIM_TEMP*/) && contadorTemporizacion < BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                    String mensaje = "";
                    if (isSalidaPrueba()) {
                        return null;
                    }
                    panel.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    panel.getLinearTemperatura().setValue(temp);
                    Thread.sleep(40);

                    while (dioxValidad == false) {
                        medicion = banco.obtenerDatos();
                        Thread.sleep(100);
                        dioxValidad = validadorO2(18 - 1, medicion.getValorO2() * 0.01);
                        if (dioxValidad == true) {
                            timer.start();
                            break;
                        }
                    }

                    if (rpm < (CallableCiclosGasolina.rngIniRpm) || rpm >= CallableCiclosGasolina.rngFinRpm) {
                        mensaje += "SE DETECTA REVOLUCIONES FUERA DE RANGO; ";
                    }
                    if (medicion.isBajoFlujo()) {
                        mensaje += " SE DETECTA BAJO FLUJO; ";
                    }
                    if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 > LIMITE_DIOXIDO) {
                        mensaje += "";
                    }
                    if (mensaje.length() > 0) {
                        panel.getMensaje().setText(mensaje + "  Tiempo " + contadorTemporizacion + " Seg. POR FAVOR MANTENGA VEHICULO EN RALENTI  ENTRE (".concat(String.valueOf(CallableCiclosGasolina.rngIniRpm)).concat(" y ").concat(String.valueOf(CallableCiclosGasolina.rngFinRpm)).concat(") DURANTE 30 Seg."));
                    } else {
                        panel.getMensaje().setText("POR FAVOR MANTENGA VEHICULO EN RALENTI  ENTRE (".concat(String.valueOf(CallableCiclosGasolina.rngIniRpm)).concat(" y ").concat(String.valueOf(CallableCiclosGasolina.rngFinRpm)).concat(") DURANTE 30 Seg."));
                    }
                    if (isSalidaPrueba()) {
                        return null;
                    }
                    medicion = banco.obtenerDatos();
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    temp = medidorRevTemp.getTemp();
                    panel.getLinearTemperatura().setValue(temp);
                    Thread.sleep(100);
                    mensaje = "";
                }//end while

                Thread.sleep(10);//punto de Salida
                /*rpmFiltrada = filtroRPM(revolucionesSimuladas);*/
                if (isSalidaPrueba()) {
                    return null;
                }
                medicion = banco.obtenerDatos();
                double oxigeno = medicion.getValorO2() * 0.01;
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                temp = medidorRevTemp.getTemp();
                panel.getLinearTemperatura().setValue(temp);
                panel.getProgressBar().setValue(30);
                panel.getProgressBar().setString("30 Seg.");
                //Si si cumple las condiciones
                contadorTemporizacion = 0;
                while ((rpm >= CallableCiclosGasolina.rngIniRpm && rpm <= CallableCiclosGasolina.rngFinRpm) && contadorTemporizacion < 30) {
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);

                    if (isSalidaPrueba()) {
                        return null;
                    }
                    if (medicion.isBajoFlujo()) {
                        panel.getMensaje().setText("SE DETECTA BAJO FLUJO en  " + intentos + " Intentos;  Tiempo " + contadorTemporizacion + "Seg.");
                    }

                    while (dioxValidad == false) {
                        medicion = banco.obtenerDatos();
                        Thread.sleep(100);
                        dioxValidad = validadorO2(18 - 1, medicion.getValorO2() * 0.01);
                        if (dioxValidad == true) {
                            timer.start();
                            break;
                        }
                    }

                    medicion = banco.obtenerDatos();
                    medicion.setValorTAceite(tempStored);
                    medicion.setConvertidorCatalitico(converCatalitico);
                    medicion.setValorRPM(rpm);
                    if (contadorTemporizacion >= 25) {
                        panel.getMensaje().setText("Capturando Datos del Banco..!");
                        medicion.setLect5Seg(true);
                        this.lista10Datos.add(medicion);  //se activa cuando hayan auditoria ambientales 
                        this.lista10Datos.add(medicion); // COMPLETAR LST 2 DATOS X SEG DADO QUE AL TENER DOS RESOURCES HAY MAS DILACION
                    } else {
                        medicion.setLect5Seg(false);
                        if (medicion.isBajoFlujo()) {
                            panel.getMensaje().setText("Advertencia, SE DETECTA BAJO FLUJO;  Manteniendo Ralenti (" + CallableCiclosGasolina.rngIniRpm + " - " + CallableCiclosGasolina.rngFinRpm + ") RPM  en  " + intentos + " Intentos");
                        } else {
                            panel.getMensaje().setText("Manteniendo Ralenti (" + CallableCiclosGasolina.rngIniRpm + " - " + CallableCiclosGasolina.rngFinRpm + ") RPM  en  " + intentos + " Intentos");
                        }
                        this.lista50Datos.add(medicion);
                        this.lista50Datos.add(medicion);
                    }

                    panel.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    panel.getProgressBar().setValue(30 - contadorTemporizacion);
                    panel.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion).concat(" Seg."));
                    if (isSalidaPrueba()) {
                        return null;
                    }
                    oxigeno = medicion.getValorO2() * 0.01;
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    temp = medidorRevTemp.getTemp();
                    panel.getLinearTemperatura().setValue(temp);
                }//end while temporizacion
                Thread.sleep(10);//punto de cancelacion
                if (contadorTemporizacion < 30 || medicion.isBajoFlujo()) {
                    intentos++;
                } else {
                    break;
                }
            }//end while intentos
            if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 < LIMITE_DIOXIDO) {
                panel.getMensaje().setText("SE HA DETECTADO DILUCION EN LA MUESTRA ");
                Thread.sleep(1000);
                panel.getFuncion().setText("DILUCION DE MUESTRA");
            }
            if (intentos >= INTENTOS) {
                if (isSalidaPrueba()) {
                    return null;
                }
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                timer.stop();
                if (medicion.isBajoFlujo()) {
                    Mensajes.messageWarningTime(" Se ha detectado BAJO FLUJO en el Proceso; Esta prueba sera ABORTADA", 5);
                    panel.getFuncion().setText("abortado");
                }
                if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 < LIMITE_DIOXIDO) {
                    Mensajes.messageWarningTime(" Se ha detectado que la SONDA DE MUESTRA no esta INSTALADA correctamente", 5);
                    //panel.getFuncion().setText("abortado");
                }
                if (medidorRevTemp.getRpm() > (BancoGasolina.LIM_RALENTI)) {
                    Mensajes.messageWarningTime("  Se ha detectado REVOLUCIONES POR ENCIMA  de lo establecido por la norma NTC 4983 (Prueba RALENTI) \n esta prueba queda RECHAZADA  ", 5);
                    panel.getFuncion().setText("rechazo");
                }
                if (medidorRevTemp.getRpm() < 401) {
                    Mensajes.messageWarningTime("  Se ha detectado REVOLUCIONES POR DEBAJO  de lo establecido por la norma NTC 4983 (Prueba RALENTI) \n esta prueba queda RECHAZADA  ", 5);
                    panel.getFuncion().setText("rechazo");
                }
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            }
            generarArchivoGases(this.placas, banco, 1);
            sacarPromMedidas(1);
        } catch (InterruptedException iexc) {
            //System.out.println("Prueba de gases cancelada externamente");
            //throw new CancellationException("Prueba cancelada");
            throw new InterruptedException();
        }
        if (isSalidaPrueba()) {
            return null;
        }
        System.out.println("PRUEBA DE GASES USANDO KIT CENTRALAUTO TERMINADA CON EXITO");
        Thread.sleep(1500);
        List<List<MedicionGases>> arregloListas = new ArrayList<List<MedicionGases>>();
        arregloListas.add(0, listaCrucero);
        arregloListas.add(1, listaRalenti);
        banco.encenderBombaMuestras(false);
        banco.getPuertoSerial().close();
        panel.cerrar();

        Connection conexion = null;
        try {
            conexion = Conex.getConnection();
            PreparedStatement instruccion = conexion.prepareStatement("UPDATE hoja_pruebas SET forma_med_temp =? where hoja_pruebas.TESTSHEET=?");
            instruccion.setString(1, forMed);
            instruccion.setLong(2, idHojaPrueba);
            int n = instruccion.executeUpdate();
        } catch (ClassNotFoundException cexc) {
            JOptionPane.showMessageDialog(null, "Falta el driver de mysql");
            Logger.getLogger(this.getClass()).error("Falta el driver de mysql", cexc);
        } finally {
            try {
                conexion.close();
            } catch (Exception e) {
            }
        }

        if (this.simulacion == true) {
            simuladorRpm.setDetener(true);//detiene el simulador  
            simuladorRpm.stop();
            this.futureSimulador.cancel(true);
        }
        return arregloListas;
    }

    private Future iniciarSimulador() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(simuladorRpm);
    }

    private boolean validadorO2(int valPerm, double valO2) {
        boolean valDilc = true;
        try {
            if (valO2 > valPerm) {
                System.out.println("valida o2 try 1");
                Thread.sleep(120);
                timer.stop();
                contadorTemporizacion = 0;
                Mensajes.mensajeAdvertencia(" SE HA DETECTADO QUE LA SONDA NO ESTA BIEN PUESTA DENTRO DEL EXHOSTO  \n POR FAVOR DIRIJASE HASTA EL  Y REVISE LA CAUSAL  \n CUANDO ESTE LISTO PRESIONE ACEPTAR ");
                Thread.sleep(8000);
                valDilc = false;
                contadorTemporizacion = 0;
                this.lista50Datos = new ArrayList<>();
                this.lista10Datos = new ArrayList<>();
            }
        } catch (InterruptedException ex) {
        }
        return valDilc;
    }

    private void cerrarPuertosHilos() {
        //banco.encenderBombaMuestras(false);
        //banco.getPuertoSerial().close();        
        if (timer != null) {
            timer.stop();
        }
        if (timerSimulacion != null) {
            timerSimulacion.stop();
        }

        if (this.simulacion == true) {
            simuladorRpm.setDetener(true);//detiene el simulador  
            simuladorRpm.stop();
            this.futureSimulador.cancel(true);
        }
    }

    private void sacarPromMedidas(int ctxPrueba) {
        List<MedicionGases> listaPromedios = new ArrayList<MedicionGases>();
        double mediaHC = 0;
        double mediaCO = 0;
        double mediaCO2 = 0;
        double mediaO2 = 0;
        double mediaRPM = 0;
        double mediaTemp = 0;
        int tamanioLista = 0;
        System.out.println("TAMAÑO DE LA LST MET sacarMediaDeListas es " + tamanioLista);
        System.out.println("HC \t CO \t CO2 ;\t  O2 ");
        for (MedicionGases med : lista10Datos) {
            System.out.println(med.getValorHC() + ";\t" + med.getValorCO() + ";\t" + med.getValorCO2() + ";\t" + med.getValorO2());
            mediaHC = mediaHC + med.getValorHC();
            mediaCO = mediaCO + med.getValorCO();
            mediaCO2 = mediaCO2 + med.getValorCO2();
            mediaO2 = mediaO2 + med.getValorO2();
            mediaRPM = mediaRPM + med.getValorRPM();

            //PROMEDIOS DE LA PRUEBA DE GASES PARA LAS MOTOS
        }
        //  System.out.println("Oxigeno FUR" + mediaO2 * 100);
        //  System.out.println("TERMINE DE SACAR LOS ACUMULADOS (sacarMediaDeListas) ");
        mediaHC = mediaHC / 10;
        mediaCO = mediaCO / 10;
        mediaCO2 = mediaCO2 / 10;
        mediaO2 = mediaO2 / 10;
        mediaRPM = mediaRPM / 10;
        mediaTemp = this.tempStored;
        //mediaTemp = mediaTemp / tamanioLista;
        MedicionGases medicion = new MedicionGases(mediaHC, mediaCO, mediaCO2, mediaO2);
        medicion.setValRPM(mediaRPM);
        medicion.setValorTAceite((int) mediaTemp);
        medicion.setLect5Seg(true);
        if (ctxPrueba == 0) {
            listaCrucero.add(medicion);
        } else {
            listaRalenti.add(medicion);
        }
    }

    private void registrarDefectosVisuales() {
        Connection conexion = null;
        try {
            //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
            conexion = Conex.getConnection();
            conexion.setAutoCommit(false);
            /* String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.clearParameters();
            instruccion.setInt(1,80000);
            instruccion.setLong(2, idPrueba);
            instruccion.executeUpdate();   */
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            String strFinalizarPrueba = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N', Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement psFinalizar = conexion.prepareStatement(strFinalizarPrueba);
            psFinalizar.setString(1, "Condiciones Anormales");
            psFinalizar.setString(2, "Condiciones Anormales Encotradas .-" + pv.getMensaje2());
            psFinalizar.setLong(3, idUsuario);
            psFinalizar.setString(4, serialEquipo);
            psFinalizar.setLong(5, idPrueba);
            int n = psFinalizar.executeUpdate();
            conexion.commit();
            conexion.setAutoCommit(true);
            CallableInicioMotos.lecturaCondicionesAnormales = pv.getDetallesRechazos().toString();
        } catch (ClassNotFoundException cexc) {
            JOptionPane.showMessageDialog(null, "Falta el driver de mysql");
            Logger.getLogger(this.getClass()).error("Falta el driver de mysql", cexc);

        } catch (SQLException exc) {

            JOptionPane.showMessageDialog(null, "Error insertando defectos visuales");
            Logger.getRootLogger().error("Error insertando defectos visuales", exc);

        } finally {
            try {
                conexion.close();
            } catch (Exception e) {
            }

        }
    }//end of method registrarDefectosVisuales

    private int medicionCatalitico() throws IOException {
        try {
            contadorTemporizacion = 0;
            int revoluciones = medidorRevTemp.getRpm();
            double temperatura = medidorRevTemp.getTemp();
            boolean aceleracionTerminada = false;
            panel.getPanelMensaje().setText("ACELERE DURANTE DOS MINUTOS");
            Thread.sleep(2500);
            panel.getPanelFiguras().setVisible(true);
            panel.getButtonRpm().setVisible(true);
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
                    panel.getProgressBar().setString(String.valueOf(contadorTemporizacion));
                    panel.getProgressBar().setStringPainted(true);
                    panel.getProgressBar().setValue(contadorTemporizacion);
                }

                //panel.getPanelMensaje().setText("ACELERACION TERMINADA");
                panel.getProgressBar().setString(String.valueOf("ACELERACION TERMINADA"));
                if (contadorTemporizacion >= 120) {
                    aceleracionTerminada = true;
                    return 0;//se toma cualquiera temperatura
                }
            }//fin del while aceleracion terminada

        } catch (InterruptedException ex) {
        }
        return 1;
    }

    /**
     * Este metodo crea un nuevo timer de 1 segundo
     */
    Timer tempVerfHumo = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            contVerHumo++;
            if (contVerHumo == 11) {
                tempVerfHumo.stop();
                panel.getMensaje().setText("");
                contVerHumo = 0;
                panel.getBtnContHumo().setText("10 Seg.");
            } else {
                panel.getBtnContHumo().setText("Verificacion de Humo en:" + contVerHumo + " Seg.");
            }
        }
    });

    public void generarArchivoGases(String placas, BancoGasolina banco, int ctxPrueba) {
        int medTemp = 0;
        System.out.println("entro a generar archivo en callableciclosgasolina");
        List<List<MedicionGases>> newListaDeLista = new ArrayList<>();
        List<MedicionGases> newLista;
        try {
            if (ctxPrueba == 0) {
                fw = new FileWriter("ultLectGases.soltelec");
                // fw2 = new FileWriter(placas +ctxPrueba +".soltelec");

                //String titulo = "HC\tCO\tCO2\tO2\tRPM\tTEMP\t";
                //String formato = "%10d;%10s;%10s;%10s;%10d;%10d\n";
                df = new DecimalFormat("#0.00#");
                bwP = new BufferedWriter(fw);
                // bwP2 = new BufferedWriter(fw2);
                out = Utilidades.cifra("Vehiculo;".concat(placas), 'M');
                // out2 = "Vehiculo;".concat(placas);
                bwP.write(out);
                // bwP2.write(out2);
                bwP.newLine();
                // bwP2.newLine();
            }

            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            int cnt = 1;
            newLista = new ArrayList<>();
            if (lista50Datos.size() > 50) {
                Integer lngLst = lista50Datos.size() - 50;
                for (int i = 0; i < lngLst; i++) {
                    lista50Datos.remove(0);
                    if (lista50Datos.size() == 50) {
                        break;
                    }
                }
            }

            if (lista50Datos.size() < 50) {
                int tot = lista50Datos.size() - 3;
                int posIni = tot - 1;
                int posFin = tot;
                int apFill = 0;
                while (true) {
                    apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                    MedicionGases med = lista50Datos.get(apFill);
                    lista50Datos.add(med);
                    if (lista50Datos.size() == 50) {
                        break;
                    }
                }
            }
            int tot = lista50Datos.size();
            int eve = 0;
            //  newListaDeLista.add(newLista);
            for (MedicionGases med : lista50Datos) {
                int medHC = med.getValorHC();
                double medCO = med.getValorCO() * 0.01;
                String strMedCO = String.valueOf(df.format(medCO));
                double medCO2 = 0;
                if (banco instanceof BancoCapelec) {
                    medCO2 = (med.getValorCO2() * 0.1) / 10;
                } else {
                    medCO2 = med.getValorCO2() * 0.1;
                }
                String strMedCO2 = String.valueOf(df.format(medCO2));
                double medO2 = med.getValorO2() * 0.01;
                String strMedO2 = String.valueOf(df.format(medO2));
                int medRPM = med.getValorRPM();
                medTemp = med.getValorTAceiteSinTabla();
                newLista.add(med);
                out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";" + med.isLect5Seg(), 'M');
                // out2= medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg();
                if (medTemp == 0 || medTemp > 110) {
                    medTemp = med.getValorTAceiteSinTabla();
                }
                bwP.write(out);
                // bwP2.write(out2);
                bwP.newLine();
                // bwP2.newLine();
                bwP.flush();
                // bwP2.flush();
                eve++;
            }
            out = Utilidades.cifra("*****", 'M');
            // out2 = "*****";
            bwP.write(out);
            // bwP2.write(out2);
            bwP.newLine();
            //  bwP2.newLine();
            bwP.flush();
            // bwP2.flush();
            cnt++;

            if (lista10Datos.size() > 10) {
                Integer lngLst = lista10Datos.size() - 10;
                for (int i = 0; i < lngLst; i++) {
                    lista10Datos.remove(0);
                    if (lista10Datos.size() == 10) {
                        break;
                    }
                }
            }

            if (lista10Datos.size() < 10) {
                tot = lista10Datos.size() - 3;
                int posIni = tot - 1;
                int posFin = tot;
                int apFill = 0;
                while (true) {
                    apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                    MedicionGases med = lista10Datos.get(apFill);
                    lista10Datos.add(med);
                    if (lista10Datos.size() == 10) {
                        break;
                    }
                }
            }
            tot = lista10Datos.size();
            eve = 0;
            newListaDeLista.add(newLista);
            for (MedicionGases med : lista10Datos) {
                int medHC = med.getValorHC();
                double medCO = med.getValorCO() * 0.01;
                String strMedCO = String.valueOf(df.format(medCO));
                double medCO2 = 0;
                if (banco instanceof BancoCapelec) {
                    medCO2 = (med.getValorCO2() * 0.1) / 10;
                } else {
                    medCO2 = med.getValorCO2() * 0.1;
                }
                String strMedCO2 = String.valueOf(df.format(medCO2));
                double medO2 = med.getValorO2() * 0.01;
                String strMedO2 = String.valueOf(df.format(medO2));
                int medRPM = med.getValorRPM();
                medTemp = med.getValorTAceiteSinTabla();
                newLista.add(med);
                out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";" + med.isLect5Seg(), 'M');
                // out2= medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";"+med.isLect5Seg();
                if (medTemp == 0 || medTemp > 110) {
                    medTemp = med.getValorTAceiteSinTabla();
                }
                bwP.write(out);
                // bwP2.write(out2);
                bwP.newLine();
                //  bwP2.newLine(); 
                bwP.flush();
                //  bwP2.flush();
                eve++;
            }

            out = Utilidades.cifra("####", 'M');
            // out2= "####";
            bwP.write(out);
            // bwP2.write(out2);
            bwP.newLine();
            // bwP2.newLine();
            bwP.flush();
            // bwP2.flush();
            GenerarArchivo.temperatura = medTemp;

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void crearNuevoTimer() {
        timer2 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            } //end actionPerformed

        });
    }

    /**
     * Metodo que inserta un texto a la prueba de gases en la columna
     * observaciones cuando se evidencia que hay evidencia de humo azul o negro
     *
     * @autor ELKIN B
     * @param cn
     * @param medida
     */
    private void registrarDefectoHumo(String medida) throws ClassNotFoundException {
        System.out.println("----------------------------------------------------");
        System.out.println("---------- --registrarDefectoHumo ------------------");
        System.out.println("----------------------------------------------------");

        String texto = "";
        Connection conexion = null;
        String consultaObservacion = "SELECT P.observaciones  FROM  pruebas  P WHERE P.Id_Pruebas=?";
        String actualizacionObservacion = "UPDATE pruebas  P SET P.observaciones=?  WHERE P.Id_Pruebas=?";

        try {
            conexion = Conex.getConnection();
            PreparedStatement Ps = conexion.prepareStatement(consultaObservacion);
            Ps.setLong(5, idPrueba);
            ResultSet r = Ps.executeQuery();

            while (r.next()) {
                texto = r.getString(1);
            }

            texto = texto.concat(medida);
            Ps.clearParameters();

            Ps = conexion.prepareStatement(actualizacionObservacion);
            Ps.setString(1, texto);
            Ps.setLong(2, idPrueba);
            Ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error en el metodo : ()" + e.getMessage());
            System.out.println("Error en el metodo : ()" + e.getStackTrace());
            System.out.println("Error en el metodo : ()" + e.getSQLState());
        }

    }

    private boolean isSalidaPrueba() {
        return panel.getFuncion().getText().equalsIgnoreCase("rechazo")
                || panel.getFuncion().getText().equalsIgnoreCase("abortado")
                || panel.getFuncion().getText().equalsIgnoreCase("rpm");
    }

}
