/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import eu.hansolo.steelseries.tools.BackgroundColor;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.PanelCero;
import static org.soltelec.pruebasgases.CallableCiclosGasolina.humo;
import org.soltelec.pruebasgases.motocicletas.CallableInicioMotos;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.UtilPropiedades;

/**
 * Primera refactorizacion de la prueba de gases para permitir la cancelacion y
 * manejar excepciones que ocurran al correr la prueba.
 *
 * Retorna un arreglo de lista de mediciones la posicion cero sera la lista de
 * mediciones de Crucero la posicion uno sera la lista de mediciones de Ralenti
 *
 * @author GerenciaDesarrollo
 */
public class CallableSimulacionGasolina implements Callable<List<List<MedicionGases>>> {

    private BancoGasolina banco;//banco para tomar las mediciones
    private SimuladorRpm simuladorRpm;//clase para simular las revoluciones
    private Future futureSimulador;//Future para monitorear la tarea del simulador
    private PanelPruebaGases panelGases;
    public static final int LIM_CRUCERO = 2500;
    private int contadorTemporizacion = 0;
    private Timer timer;
    private MedidorRevTemp medidorRevTemp;
    private List<MedicionGases> listaCrucero, listaRalenti;
    private int LIM_TEMP = 60;
    public boolean rpmmal = false;
    public static String h;
    private final long idPrueba, idUsuario, idHojaPrueba;
    private static final double LIMITE_OXIGENO = 5;
    private static final double LIMITE_DIOXIDO = 7;
    String forMed;
    private PanelVerificacion pv;
    private boolean reiniciarProceso = false;
    private Timer timer2 = null;
    private static final double LIMITE_HC = 20;
    private int contador;
    //variables para registrar el resultado de la prueba    

    public CallableSimulacionGasolina(PanelPruebaGases thePanel, BancoGasolina banco, long idPrueba, long idUsuario, long idHojaPrueba) {
        this.banco = banco;
        simuladorRpm = new SimuladorRpm();
        medidorRevTemp = simuladorRpm;
        panelGases = thePanel;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.idHojaPrueba = idHojaPrueba;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
    }//end of constructor

    @Override
    public List<List<MedicionGases>> call() throws DilucionException, HumoNegroException, HumoAzulException {

        try {
            futureSimulador = iniciarSimulador();//Inicia el hilo simulador de las revoluciones    
            simuladorRpm.setSimularCrucero(false);//se debe mantener en ralenti
            panelGases.getButtonFinalizar().setVisible(true);//permitir la cancelacion
            panelGases.getButtonRpm().setVisible(LeerArchivo.rechazarPorRpm());//permitir el cambio de rpms
            panelGases.getButtonRpm().setText("Rechazar Por RPM");
            //Aceleracion de limpieza
            panelGases.getPanelMensaje().setVisible(true);
            panelGases.getPanelFiguras().setVisible(false);
            panelGases.getProgressBar().setVisible(true);
            panelGases.getMensaje().setText("");
            panelGases.getProgressBar().setString(" ");
            panelGases.getProgressBar().setValue(0);
            boolean converCatalitico;
            int tempStored = 0;
            int temp = medidorRevTemp.getTemp();
            timer.start();
            //Preguntar por como desea medir la temperatura : En el Aceite, en el Bloque o mediante Opcion cuando
            //el vehiculo es de convertidor catalitico
            Object objSeleccion = JOptionPane.showInputDialog(
                    null,
                    "¿Como va a Medir la temperatura ?",
                    "SART 1.7.3",
                    JOptionPane.QUESTION_MESSAGE,
                    null, // null para icono defecto
                    new Object[]{"Temperatura de Aceite", "Temperatura de Bloque", "Automovil con Convertidor Catalitico o temperatura indeterminable"}, " ");

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
                    forMed = "C";
                    converCatalitico = true;
                    contadorTemporizacion = 0;
                    int revoluciones = simuladorRpm.getRpm();
                    panelGases.getPanelMensaje().setText("POR FAVOR ACELERE  A 2500 RPM  DURANTE DOS MINUTOS ");
                    Thread.sleep(2600);
                    panelGases.getProgressBar().setMaximum(120);
                    panelGases.getPanelMensaje().setVisible(false);
                    panelGases.getPanelFiguras().setVisible(true);
                    panelGases.getProgressBar().setVisible(true);
                    panelGases.getMensaje().setText("");
                    panelGases.getProgressBar().setString(" ");
                    Thread.sleep(2500);
                    revoluciones = simuladorRpm.getRpm();
                    simuladorRpm.setREVOLUCIONES_CRUCERO(2720);
                    simuladorRpm.setSimularCrucero(true);

                    while (revoluciones < 2250) {
                        Thread.sleep(500);
                        panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        revoluciones = simuladorRpm.getRpm();
                        panelGases.getRadialTacometro().setValue(revoluciones);
                    }

                    contadorTemporizacion = 0;
                    while (contadorTemporizacion < 120 && revoluciones >= 2250) {
                        Thread.sleep(1000);
                        revoluciones = simuladorRpm.getRpm();
                        panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        panelGases.getProgressBar().setValue(contadorTemporizacion);
                        panelGases.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat("Seg."));
                        panelGases.getProgressBar().setStringPainted(true);
                        panelGases.getRadialTacometro().setValue(revoluciones);
                        panelGases.getMensaje().setText("Por Favor. Mantenga el Vehiculo en Crucero durante 2 minutos..!");
                    }
                    panelGases.getPanelFiguras().setVisible(false);
                    panelGases.getPanelMensaje().setVisible(true);
                    Thread.sleep(2500);
                    panelGases.getPanelMensaje().setText("ACELERACION TERMINADA");
                    break;
            }
            if (!(strSeleccion.equalsIgnoreCase("Automovil con Convertidor Catalitico o temperatura indeterminable"))) {
                Mensajes.messageWarningTime("INICIANDO VALIDACION DE TEMPERATURA SEGUN LA NORMA DE LA  NTC 4983  ", 5);
                temp = medidorRevTemp.getTemp();
                if (temp < LIM_TEMP) {
                    panelGases.getPanelMensaje().setText(" TEMPERATURA ACTUAL ES " + temp + "  \n ESPERANDO QUE ALCANZE NIVEL MINIMO DE TEMPERATURA SEGUN LA NORMA DE LA  NTC 4983");
                    Thread.sleep(20000);
                    temp = medidorRevTemp.getTemp();
                    if (temp < LIM_TEMP) {
                        panelGases.getPanelMensaje().setText(" Temperatura Minima no alcanzada se cumple con el numeral 4.1.3.6 de la NTC 4983");
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
            panelGases.getPanelMensaje().setText(" ");
            panelGases.getPanelFiguras().setVisible(false);
            Thread.sleep(800);
            //Inspeccion Sensorial por parte del operarion
            pv = new PanelVerificacion();
            //Si se indica mediante los checkboxes que hay defectos entonces el programa los registra
            // y sale de la prueba
            JOptionPane.showMessageDialog(null, pv, "SART 1.7.3.- Inspeccion Sensorial", JOptionPane.PLAIN_MESSAGE);
            List<String> defectos = new ArrayList<String>();
            if (pv.isDefectoEncontrado()) {
                System.out.println("Defecto " + pv.getMensaje().toString());
                JOptionPane.showMessageDialog(panelGases, pv.getMensaje().toString().split("</html>"));
                try {
                    registrarDefectosVisuales();
                } finally {
                    cerrarPuertosHilos();
                    panelGases.cerrar();
                }
                panelGases.getFuncion().setText("Condiciones Anormales");
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            }
            panelGases.getPanelMensaje().setText("POR FAVOR \n MANTENGA EL VEHICULO EN RALENTI  ");
            Thread.sleep(2500);
            Mensajes.messageWarningTime(" VALIDANDO RPM EN RALENTI SEGUN LA NORMA DE LA  NTC 4983 NUMERAL 4.1.3.9  ", 5);
            panelGases.getPanelMensaje().setText("HUMO NEGRO \n Por Favor Acelere entre 2250 a 2750 RPM \n DURANTE 20 seg.");
            Thread.sleep(3000);

            panelGases.getPanelMensaje().setVisible(false);
            panelGases.getPanelMensaje().setText("");
            panelGases.getPanelFiguras().setVisible(true);
            panelGases.getProgressBar().setValue(0);
            boolean pintarVerde = false;
            boolean pintarAnterior = false;
            panelGases.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);

            simuladorRpm.setSimularCrucero(true);

            boolean aceleracionHumoNegroTerminada = false;

            simuladorRpm.setREVOLUCIONES_CRUCERO(3050);
            panelGases.getMensaje().setText("Por Favor ACELERE en el Rango de (2250 a 2750) RPM  DURANTE 20 Seg.");
            Thread.sleep(900);
            while (!aceleracionHumoNegroTerminada) {
                while (medidorRevTemp.getRpm() <= BancoGasolina.LIM_CRUCERO) {//recuerda blinker != null
                    Thread.sleep(120);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    pintarAnterior = pintarVerde;
                    if (/*rpmFiltrada*/medidorRevTemp.getRpm() >= 2250 && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= 2750) {
                        pintarVerde = true;
                    } else {
                        pintarVerde = false;
                    }
                    if (pintarAnterior != pintarVerde) {//necesidad de repintar
                        if (pintarVerde) {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        } else {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        }
                    }//end if
                }//end while de calentamiento temperatura y alcanzar revoluciones
                contadorTemporizacion = 0;
                panelGases.getProgressBar().setMaximum(20);
                while (medidorRevTemp.getRpm() >= BancoGasolina.LIM_CRUCERO && contadorTemporizacion <= 20) {//recuerda blinker != null
                    Thread.sleep(120);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    pintarAnterior = pintarVerde;

                    if (/*rpmFiltrada*/medidorRevTemp.getRpm() >= 2250 && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= 2750) {
                        pintarVerde = true;
                        panelGases.getMensaje().setText(" Aceleracion se MANTIENE en el rango  de (2250 a 2750) RPM  ");
                        panelGases.getProgressBar().setValue(contadorTemporizacion);
                        panelGases.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat(" Seg."));
                        //plot.
                    } else {
                        pintarVerde = false;
                        panelGases.getMensaje().setText(" Aceleracion No se MANTUVO el rango  de (2250 a 2750) RPM  ");
                        contadorTemporizacion = 0;
                        medidorRevTemp.setRpm(2600);
                        panelGases.getProgressBar().setString(String.valueOf("0").concat(" Seg."));

                    }
                    if (pintarAnterior != pintarVerde) {//necesidad de repintar
                        if (pintarVerde) {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);

                        } else {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        }
                    }//end if

                }//end while de calen

                if (contadorTemporizacion >= 20) {
                    aceleracionHumoNegroTerminada = true;
                }
            }
//                
//        }    
            panelGases.getPanelFiguras().setVisible(false);
            panelGases.getPanelMensaje().setVisible(true);
            panelGases.getMensaje().setText("");
            panelGases.getProgressBar().setValue(0);
            panelGases.getProgressBar().setString("");

            panelGases.getPanelMensaje().setText("HUMO NEGRO \nPOR FAVOR  MANTENGA EL VEHICULO EN RALENTI \n DURANTE 20 Seg.");
            listaRalenti = new ArrayList<MedicionGases>();
            Thread.sleep(2500);
            aceleracionHumoNegroTerminada = false;
            panelGases.getPanelMensaje().setVisible(false);
            panelGases.getPanelFiguras().setVisible(true);

            panelGases.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);

            simuladorRpm.setSimularCrucero(true);

            simuladorRpm.setREVOLUCIONES_CRUCERO(1100);
            while (!aceleracionHumoNegroTerminada) {
                panelGases.getMensaje().setText("Por Favor MANTENGA RALENTI  entre "+CallableCiclosGasolina.rngIniRpm +" a "+CallableCiclosGasolina.rngFinRpm +" RPM \n DURANTE 20 Seg.");
                
                while (medidorRevTemp.getRpm() <= BancoGasolina.LIM_RALENTI - 250) {//recuerda blinker != null
                    Thread.sleep(120);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    pintarAnterior = pintarVerde;
                    if (/*rpmFiltrada*/medidorRevTemp.getRpm() >= CallableCiclosGasolina.rngIniRpm && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= CallableCiclosGasolina.rngFinRpm) {
                        pintarVerde = true;
                        //plot.
                    } else {
                        pintarVerde = false;
                    }
                    if (pintarAnterior != pintarVerde) {//necesidad de repintar
                        if (pintarVerde) {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        } else {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        }
                    }//end if
                }//end while de calentamiento temperatura y alcanzar revoluciones            

                contadorTemporizacion = 0;
                panelGases.getProgressBar().setMaximum(20);
                while (medidorRevTemp.getRpm() >= 500 && contadorTemporizacion < 20) {//recuerda blinker != null
                    System.err.println(medidorRevTemp.getRpm());
                    Thread.sleep(120);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    pintarAnterior = pintarVerde;
                    if (/*rpmFiltrada*/medidorRevTemp.getRpm() >= CallableCiclosGasolina.rngIniRpm && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= CallableCiclosGasolina.rngFinRpm) {
                        pintarVerde = true;
                        panelGases.getMensaje().setText("El Ralenti  se MANTIENE en el Rango de ("+CallableCiclosGasolina.rngIniRpm+" a "+CallableCiclosGasolina.rngFinRpm+") RPM ");
                    } else {
                        pintarVerde = false;
                    }
                    if (pintarAnterior != pintarVerde) {//necesidad de repintar
                        if (pintarVerde) {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        } else {
                            panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        }
                    }//end if
                    panelGases.getProgressBar().setValue(contadorTemporizacion);
                    panelGases.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat(" Seg."));
                }//end while de calen

                if (contadorTemporizacion >= 20) {
                    aceleracionHumoNegroTerminada = true;
                }
            }
            int opcion = JOptionPane.showOptionDialog(null, "¿Visualizo Presencia de Humo? y si es asi ¿Cual fue su Color?", "SART 1.7.3 ", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Negro", "Azul", "NO"}, "NO");

            if (opcion == JOptionPane.YES_OPTION) {
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                System.out.println("Selecciono Humo negro");
                humo = "negro";
                JOptionPane.showMessageDialog(panelGases, "En Cumplimiento al Numeral NTC 4.1.3.10 esta prueba sera RECHADA  por presentar HUMO NEGRO");
                panelGases.getFuncion().setText("3.1.1.1.9 Presencia de humo negro o azul (Negro)");
                aceleracionHumoNegroTerminada = true;
                simuladorRpm.setDetener(true);//detiene el simulador
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            } else if (opcion == JOptionPane.NO_OPTION) {
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(100);
                simuladorRpm.setDetener(true);//detiene el simulador
                System.out.println("Selecciono Humo Azul");
                humo = "azul";
                aceleracionHumoNegroTerminada = true;
                JOptionPane.showMessageDialog(panelGases, "En Cumplimiento al Numeral NTC 4.1.3.10 esta prueba sera RECHADA  por presentar HUMO AZUL");
                panelGases.getFuncion().setText("3.1.1.1.9 Presencia de humo negro o azul (Azul)");
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            } else {
                //no haga nada
            }
            panelGases.getPanelFiguras().setVisible(false);
            panelGases.getPanelMensaje().setVisible(true);
            panelGases.getMensaje().setText(" ");
            simuladorRpm.setREVOLUCIONES_CRUCERO(2700);//valor minimo 24009+6w6666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666
            simuladorRpm.setSimularCrucero(false);//simula hasta ralenti
            MedicionGases medicion;
            while (!reiniciarProceso) {
                PanelCero panelCero = new PanelCero();
                panelGases.getPanelMensaje().setText("Por Favor Espere. \n Haciendo Cero en el Banco ...!");
                panelCero.setBanco(banco);
                panelCero.setBarraTiempo(this.panelGases.getProgressBar());
                panelCero.setLabelMensaje(this.panelGases.getPanelMensaje().getCronometro());

                Thread t1 = new Thread(panelCero);
                t1.start();
                try {
                    t1.join();
                    //Realizar calibracion de cero//sincronizar con hilo principal
                } catch (InterruptedException ex) {
                    out.println("Error haciendo join con hilo de cero");
                }

                panelGases.getProgressBar().setVisible(false);
                panelGases.getButtonFinalizar().setVisible(true);
                //lista calibracion de cero
                panelGases.getPanelMensaje().setText("Esperando a que el \n Equipo de Medicion registe \n"
                        + "HC menor que 20 ....");
                medicion = banco.obtenerDatos();
                //Enviar comando de comando de encender bomba para tomar lectura de HC
                banco.encenderBombaMuestras(true);
                out.println("Encendiendo Bomba");
                // medicion = banco.obtenerDatos();
                contadorTemporizacion = 0;
                crearNuevoTimer();
                timer2.start();
                panelGases.getPanelMensaje().setText("La Medicion de HC \n Es Mayor a 20ppm");
                panelGases.getPanelMensaje().getCronometro().setText("Tomando mediciones... " + contadorTemporizacion);
                Thread.sleep(1600);//Simplemente para que sea visible
                /*DISMINUCION DE HC*///
                //Cuando disminuye sale del while
                while (medicion.getValorHC() > LIMITE_HC && contadorTemporizacion < 150) {//repita mientras el valor sea mayor a 20 ppm
                    medicion = banco.obtenerDatos();//tome medicion
                    //kiss no timer
                    Thread.sleep(120);
                    out.println("HC mayor a 20 ppm intentos" + contador + "");
                    panelGases.getPanelMensaje().getCronometro().setText("HC Mayor a 20 ppm, esperando... " + contadorTemporizacion);
                }//HC ha bajado de los 20 ppm o tiempo de espera cumplido
                timer2.stop();
                if (contadorTemporizacion >= 149) {//si el tiempo de espera cumplido
                    panelGases.getPanelMensaje().setText("Verifique los Filtros");
                    out.println("Posibles filtros sucios en la linea de muestra");
                    int opcionSeleccionada = JOptionPane.showConfirmDialog(panelGases, "Posibles filtros sucios en la linea de muestra \n Cancelar la prueba?", "", JOptionPane.YES_NO_OPTION);
                    switch (opcionSeleccionada) {
                        case JOptionPane.NO_OPTION://seguir intentando que baje el HC
                            reiniciarProceso = false;
                            break;
                        case JOptionPane.YES_OPTION://volver a la pantalla de Prueba/Servicio
                            //TODO motivo cancelacion prueba registrar
                            Window w = SwingUtilities.getWindowAncestor(panelGases);
                            //Window w1 = SwingUtilities.getWindowAncestor(w);
                            w.dispose();
                            //if(w1 != null)
                            //w1.dispose();
                            //timer.stop();
                            banco.getPuertoSerial().close();
                            timer.stop();
                            panelGases.cerrar();
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
            //SONDA DE MUESTRA
            panelGases.getPanelFiguras().setVisible(false);
            panelGases.getPanelMensaje().setVisible(true);
            panelGases.getPanelMensaje().setText("Sonda de Muestra");
            Thread.sleep(1500);
            JOptionPane.showMessageDialog(panelGases, "Por Favor Introduzca la sonda en el Tubo de Escape");
            banco.encenderBombaMuestras(true);//TODO descomentar banco
            panelGases.getPanelMensaje().setText("El nivel de O2 debe disminuir a menos de 5%");
            Thread.sleep(1500);

            //DISMINUCION DE OXIGENO
            int intentos = 0;

            medicion = banco.obtenerDatos();
            //panel.mostrarBotonDilucion();///terminar por Dilucion no cancelar habilitado
            //panel.getButtonRpm().addActionListener(new ManejadorDilucion());
            boolean oxigenoDisminuyo = false;
            while (!oxigenoDisminuyo) {
                int contadorO2 = 0;
                while (medicion.getValorO2() * 0.01 > BancoGasolina.LIMITE_OXIGENO && contadorO2 < BancoGasolina.RETARDOO2) {
                    Thread.sleep(420);
                    contadorO2++;
                    panelGases.getPanelMensaje().getCronometro().setText("Tiempo transcurrido: " + String.valueOf(contadorO2 / 2) + "Seg.");
                    medicion = banco.obtenerDatos();//TODO descomentar banco

                }//end of while O2 mayor a 21

                if (contadorO2 >= BancoGasolina.RETARDOO2) {

                    //futureSimulador.cancel(true);
                    //timer.stop();
                    //banco.encenderBombaMuestras(false);
                    //Thread.sleep(250);
                    //banco.getPuertoSerial().close();
                    //throw new DilucionException();
                    oxigenoDisminuyo = true;
                }//
                else {
                    oxigenoDisminuyo = true;
                }//end else

            }//end while

            //PRUEBA DE CRUCERO
            listaCrucero = new ArrayList<MedicionGases>();
            panelGases.getPanelFiguras().setVisible(false);
            panelGases.getPanelMensaje().setVisible(true);
            panelGases.getPanelMensaje().setText("INICIO DE LA PRUEBA DE CRUCERO\n " + " POR FAVOR ACELERE HASTA 2500 RPM  \n Y  MANTENGALO DURANTE 30 Seg.");
            Thread.sleep(2000);

            //Iniciar el timer
            panelGases.getMensaje().setText("Por Favor ACELERE hasta 2500 RPM   MANTENGALO Durante 30 Seg.");
            simuladorRpm.setSimularCrucero(true);
            //Iniciar el timer

            panelGases.getMensaje().setText("");
            panelGases.getPanelMensaje().setVisible(false);
            panelGases.getPanelFiguras().setVisible(true);
            panelGases.getProgressBar().setValue(0);
            panelGases.getProgressBar().setVisible(true);
            panelGases.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            intentos = 0;
            timer.start();
            String mensajeProblemas = "";
            while (intentos < 3) {
                contadorTemporizacion = 0;
                medicion = banco.obtenerDatos();
                panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                panelGases.getProgressBar().setValue(0);
                //MIENTRAS EXISTA DILUCION BAJO FLUJO O REVOLUCIONES FUERA DE RANGO
                while ((medidorRevTemp.getRpm() <= (BancoGasolina.LIM_CRUCERO - 250) || medidorRevTemp.getRpm() > (BancoGasolina.LIM_CRUCERO + 250) || medicion.isBajoFlujo()) && contadorTemporizacion < BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                    mensajeProblemas = "";
                    Thread.sleep(120);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    if (medidorRevTemp.getRpm() <= (BancoGasolina.LIM_CRUCERO - 250) || medidorRevTemp.getRpm() > (BancoGasolina.LIM_CRUCERO + 250)) {
                        mensajeProblemas += "REV FUERA DE RANGO*";
                        rpmmal = true;
                    }
                    if (medicion.isBajoFlujo()) {
                        mensajeProblemas += "BAJO FLUJO";//panelGases.getMensaje().setText(" BAJO FLUJO : "  + contadorTemporizacion +"s");
                    }
                    /* if (medicion.getValorO2() * 0.01 > BancoGasolina.LIMITE_OXIGENO) {
                     mensajeProblemas += "SE HA DETECTADO DILUCION EN LA MUESTRA";
                     }*/
                    medicion = banco.obtenerDatos();
                    panelGases.getMensaje().setText(mensajeProblemas + " " + contadorTemporizacion + " s");

                }//end while

                Thread.sleep(10);//punto de salida
                contadorTemporizacion = 0;
                panelGases.getProgressBar().setMaximum(30);
                panelGases.getProgressBar().setValue(30);
                panelGases.getProgressBar().setString("30 Seg.");
                panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                medicion = banco.obtenerDatos();
                double oxigeno = medicion.getValorO2() * 0.01;
                while ((medidorRevTemp.getRpm() >= (BancoGasolina.LIM_CRUCERO - 250) && /*rpmFiltrada*/ medidorRevTemp.getRpm() <= (BancoGasolina.LIM_CRUCERO + 250)) && !medicion.isBajoFlujo() /*&&(oxigeno < BancoGasolina.LIMITE_OXIGENO)*/ && contadorTemporizacion < 30) {
                    Thread.sleep(120);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    medicion.setValorRPM((int) medidorRevTemp.getRpm());//TODO descomentar para proceso con banco
                    if (contadorTemporizacion >= 25) {
                        panelGases.getMensaje().setText("Capturando Datos del Banco..!");                      
                        if (!strSeleccion.equals("Automovil con Convertidor Catalitico o temperatura indeterminable")) {
                            medicion.setValorTAceite(medidorRevTemp.getTemp());
                        } else {
                            medicion.setValorTAceite(0);
                        }
                       medicion.setLect5Seg(true);
                    } else {
                        if (medicion.isBajoFlujo()) {
                            mensajeProblemas += "BAJO FLUJO";//panelGases.getMensaje().setText(" BAJO FLUJO : "  + contadorTemporizacion +"s");
                        }
                       /* if ((medicion.getValorO2() * 0.01) > BancoGasolina.LIMITE_OXIGENO) {
                            mensajeProblemas += "SE HA DETECTADO DILUCION EN LA MUESTRA";
                        }*/
                        if (mensajeProblemas.length() > 1) {
                            panelGases.getMensaje().setText(mensajeProblemas);
                        } else {
                            panelGases.getMensaje().setText("Manteniendo Crucero (2250 a 2750) RPM  en  " + intentos + " Intentos");
                        }
                        medicion.setLect5Seg(false);
                    }
                    listaCrucero.add(medicion);//TODO descomentar
                    panelGases.getProgressBar().setValue(30 - contadorTemporizacion);
                    panelGases.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion).concat(" Seg."));
                    medicion = banco.obtenerDatos();//TODO descomentar procesos del banco
                    oxigeno = medicion.getValorO2() * 0.01;//TODO descomentar procesos del banco
                    mensajeProblemas = "";
                    Thread.sleep(70);//punto de cancelacion
                    /*rpmFiltrada = filtroRPM(revolucionesSimuladas);*/
                }//end while condiciones ok
                
                if (contadorTemporizacion < 30 /*|| medicion.isBajoFlujo() || oxigeno >= LIMITE_OXIGENO*/) {
                    intentos++;
                } else {
                    break;
                }
            }//end while intentos
            System.out.println("INTENTO: " + intentos);
            Thread.sleep(10);
            if (intentos >= 3) {
                StringBuilder sb = new StringBuilder();
                if (medicion.isBajoFlujo()) {
                    sb.append(" BAJO FLUJO");
                    Mensajes.messageWarningTime(" Se ha detectado BAJO FLUJO en el Proceso; Esta prueba sera ABORTADA", 5);
                    panelGases.getFuncion().setText("Detencion BAJO FLUJO ");
                }
                //if(medicion.getValorHC())
                medicion.getCadenaHC();

                futureSimulador.cancel(true);
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(250);
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            }
            //PRUEBA DE RALENTI

            if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 < LIMITE_DIOXIDO) {
                   System.out.println("Marco Dilucion en Ralenty  " );
                panelGases.getMensaje().setText("SE HA DETECTADO DILUCION EN LA MUESTRA ..!");
                Thread.sleep(1000);                
                panelGases.getFuncion().setText("DILUCION DE MUESTRA");
            }
            panelGases.getPanelFiguras().setVisible(false);
            panelGases.getPanelMensaje().setVisible(true);
            panelGases.getMensaje().setText("");
            panelGases.getProgressBar().setValue(0);
            panelGases.getProgressBar().setString("");

            panelGases.getPanelMensaje().setText(" PRUEBA DE RALENTI ");
            listaRalenti = new ArrayList<MedicionGases>();
            Thread.sleep(1500);

            panelGases.getPanelMensaje().setVisible(false);
            panelGases.getPanelFiguras().setVisible(true);
            while (intentos < 3) {
                simuladorRpm.setSimularCrucero(false);//empieza a simular las revoluciones de ralenti
                contadorTemporizacion = 0;
                panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                medicion = banco.obtenerDatos();
                panelGases.getProgressBar().setValue(30);
                panelGases.getProgressBar().setString("30 Seg.");
                //ESPERAR MIENTRAS LAS CONDICIONES DE LA PRUEBA SEAN INAPROPIADAS
                while ((medidorRevTemp.getRpm() > (BancoGasolina.LIM_RALENTI - 50) /*|| medicion.isBajoFlujo() || medicion.getValorO2()*0.01 > BancoGasolina.LIMITE_OXIGENO*/) && contadorTemporizacion < BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                    Thread.sleep(120);
                    String mensaje = "";
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    if (medicion.isBajoFlujo()) {
                        mensaje += " BAJO FLUJO";
                    }//                   
                    if (medidorRevTemp.getRpm() > (BancoGasolina.LIM_RALENTI - 50)) {
                        mensaje += " REV FUERA DE RANGO";
                    }
                    panelGases.getMensaje().setText(mensaje);
                    medicion = banco.obtenerDatos();

                }//end while
                Thread.sleep(10);//punto de interrupcion

                contadorTemporizacion = 0;
                medicion = banco.obtenerDatos();
                double oxigeno = medicion.getValorO2() * 0.01;
                panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                Thread.sleep(120);
                while ((medidorRevTemp.getRpm() >= CallableCiclosGasolina.rngIniRpm && medidorRevTemp.getRpm() <= CallableCiclosGasolina.rngFinRpm) && !medicion.isBajoFlujo() /*&& oxigeno <= BancoGasolina.LIMITE_OXIGENO*/ && contadorTemporizacion < 30) {
                    panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                    Thread.sleep(180);
                    if (contadorTemporizacion >= 25) {
                        panelGases.getMensaje().setText("Capturando Datos del Banco...!");
                        medicion.setValorRPM((int) medidorRevTemp.getRpm());
                        if (!strSeleccion.equals("Automovil con Convertidor Catalitico o temperatura indeterminable")) {
                            medicion.setValorTAceite(medidorRevTemp.getTemp());
                        } else {
                            medicion.setValorTAceite(0);
                        }
                        medicion.setLect5Seg(true);                       
                    } else {
                        if (medicion.isBajoFlujo()) {
                            mensajeProblemas += "BAJO FLUJO";//panelGases.getMensaje().setText(" BAJO FLUJO : "  + contadorTemporizacion +"s");
                        }                       
                        if (mensajeProblemas.length() > 0) {
                            panelGases.getMensaje().setText(mensajeProblemas);
                        } else {
                          panelGases.getMensaje().setText("\"Manteniendo Ralenti ("+CallableCiclosGasolina.rngIniRpm +" - "+ CallableCiclosGasolina.rngFinRpm+") RPM  en  " + intentos + " Intentos");
                        }
                        medicion.setLect5Seg(false);
                    }
                    listaRalenti.add(medicion);
                    panelGases.getRadialTacometro().setValue(medidorRevTemp.getRpm());
                    panelGases.getProgressBar().setValue(30 - contadorTemporizacion);
                    panelGases.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion).concat(" Seg."));
                    medicion = banco.obtenerDatos();
                    oxigeno = medicion.getValorO2() * 0.01;
                    mensajeProblemas = "";
                    
                }//end while temporizacion
                /*rpmFiltrada = filtroRPM(revolucionesSimuladas);*/
              
                if (contadorTemporizacion < 30 || medicion.isBajoFlujo() /*|| oxigeno > BancoGasolina.LIMITE_OXIGENO*/) {
                    intentos++;
                } else {
                    break;
                }
            }//end while intentos
           
            if (intentos > 2) {
                if (medicion.isBajoFlujo()) {
                    Mensajes.messageWarningTime(" SE HA DETECTADO BAJO FLUJO EN EL EQUIPO MEDICION; Esta prueba sera ABORTADA", 5);
                    panelGases.getFuncion().setText("BAJO FLUJO DEL EQUIPO");
                } else {
                    JOptionPane.showMessageDialog(null, "HUBO UN FALLO EN LA PRUEBA DE RALENTI\n POR FAVOR VERIFIQUE SI SE PRESENTO 1) RPM FUERA RANGO ");
                }
                futureSimulador.cancel(true);
                timer.stop();
                banco.encenderBombaMuestras(false);
                Thread.sleep(250);
                List<List<MedicionGases>> lecturaRechazada = new ArrayList<List<MedicionGases>>();
                return lecturaRechazada;
            }
            if ((medicion.getValorO2() * 0.01 > LIMITE_OXIGENO) || medicion.getValorCO2() * 0.1 < LIMITE_DIOXIDO) {
                   panelGases.getMensaje().setText("SE HA DETECTADO DILUCION EN LA MUESTRA ..!");
                Thread.sleep(1000);                
                panelGases.getFuncion().setText("DILUCION DE MUESTRA");                
            }
        }//end of try de cancelacion//end of try de cancelacion
        catch (InterruptedException iexc) {//Cambio de revoluciones o cancelacion de la prueba
            System.out.println("Hilo Cancelado");
            cancelacion();
            throw new CancellationException("Callable interrumpido");
        }

        simuladorRpm.setDetener(true);//detiene el simulador
        timer.stop();
        List<List<MedicionGases>> arregloListas = new ArrayList<List<MedicionGases>>();
        arregloListas.add(0, listaCrucero);
        arregloListas.add(1, listaRalenti);
        banco.encenderBombaMuestras(false);
        banco.getPuertoSerial().close();//si todo va normal cierra el puerto serial del banco
        panelGases.cerrar();
        return arregloListas;
    }//end of call

    private Future iniciarSimulador() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        return executor.submit(simuladorRpm);
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
            instruccion.executeUpdate(); */ 
            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            String strFinalizarPrueba = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N', Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement psFinalizar = conexion.prepareStatement(strFinalizarPrueba);
            psFinalizar.setString(1, "Condiciones Anormales");
            psFinalizar.setString(2, "Condiciones Anormales Encotradas .-" + pv.getDetallesRechazos());
            psFinalizar.setLong(3, idUsuario);            
            psFinalizar.setString(4, serialEquipo);
            psFinalizar.setLong(5, idPrueba);
            int n = psFinalizar.executeUpdate();
            conexion.commit();
            conexion.setAutoCommit(true);
            CallableInicioMotos.lecturaCondicionesAnormales=pv.getDetallesRechazos().toString();
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

    private int medicionCatalitico() {
        try {
            contadorTemporizacion = 0;
            int revoluciones = medidorRevTemp.getRpm();
            double temperatura = medidorRevTemp.getTemp();
            boolean aceleracionTerminada = false;
            panelGases.getPanelMensaje().setText("ACELERE DURANTE DOS MINUTOS");
            Thread.sleep(2500);
            panelGases.getPanelFiguras().setVisible(true);
            while (!aceleracionTerminada) {

                //while(revoluciones >= 2250 && revoluciones <= 2750){
                while ((medidorRevTemp.getRpm() <= BancoGasolina.LIM_CRUCERO - 250) && medidorRevTemp.getRpm() <= 2750) {
                    Thread.sleep(500);
                    revoluciones = medidorRevTemp.getRpm();
                    temperatura = medidorRevTemp.getTemp();
                    panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                    panelGases.getRadialTacometro().setValue(revoluciones);
                    panelGases.getLinearTemperatura().setValue(temperatura);
                }
                contadorTemporizacion = 0;
                //panel.getPanelMensaje().setText("MANTENGA LAS REVOLUCIONES");
                panelGases.getProgressBar().setMaximum(120);
                panelGases.getProgressBar().setVisible(true);

                //while( contadorTemporizacion < 120 && revoluciones >= 2250 && revoluciones <=2750){
                while (medidorRevTemp.getRpm() >= (BancoGasolina.LIM_CRUCERO - 250) && (contadorTemporizacion < 120) && medidorRevTemp.getRpm() <= 2750) {//recuerda blinker != null
                    Thread.sleep(1000);
                    revoluciones = medidorRevTemp.getRpm();
                    panelGases.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                    panelGases.getRadialTacometro().setValue(revoluciones);
                    panelGases.getProgressBar().setString(String.valueOf(contadorTemporizacion));
                    panelGases.getProgressBar().setStringPainted(true);
                    panelGases.getProgressBar().setValue(contadorTemporizacion);
                }

                //panel.getPanelMensaje().setText("ACELERACION TERMINADA");
                panelGases.getProgressBar().setString(String.valueOf("ACELERACION TERMINADA"));
                if (contadorTemporizacion >= 120) {
                    aceleracionTerminada = true;
                    return 0;//se toma cualquiera temperatura
                }
            }//fin del while aceleracion terminada

        } catch (InterruptedException ex) {

        }
        return 1;
    }

    private void cerrarPuertosHilos() {
        //banco.encenderBombaMuestras(false);
        //banco.getPuertoSerial().close();
        panelGases.cerrar();
        if (timer != null) {
            timer.stop();
        }
        simuladorRpm.stop();
        this.futureSimulador.cancel(true);
        
    }

    private void crearNuevoTimer() {
        timer2 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            } //end actionPerformed
        });
    }

    private void cancelacion() {
        panelGases.getPanelFiguras().setVisible(false);
        panelGases.getPanelMensaje().setVisible(true);
        panelGases.getPanelMensaje().setText("Interrumpido");
        panelGases.getMensaje().setText("");
        panelGases.getProgressBar().setValue(0);
        panelGases.getProgressBar().setString("");
        futureSimulador.cancel(true);
        banco.encenderBombaMuestras(false);
        if (timer != null) {
            timer.stop();
        }
    }
}
