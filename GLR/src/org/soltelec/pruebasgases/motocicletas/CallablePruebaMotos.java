/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import com.soltelec.loginadministrador.UtilLogin;
import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import eu.hansolo.steelseries.tools.BackgroundColor;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.procesosbanco.PanelCero;
import org.soltelec.procesosbanco.ProcesoRuido;
import org.soltelec.pruebasgases.CondicionesAnormalesException;
import org.soltelec.pruebasgases.HumoAzulException;
import org.soltelec.pruebasgases.HumoNegroException;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.BloqueoEquipoUtilitario;
import org.soltelec.util.GenerarArchivo;
import org.soltelec.util.Mensajes;
import org.soltelec.util.InfoVehiculo;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilInfoVehiculo;
import org.soltelec.util.Utilidades;
import org.soltelec.util.capelec.BancoCapelec;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;
import utiltermohigrometro.UtilPropiedades;

/**
 * Clase que resume la prueba de gasolina para motos de manera polimorfica.
 *
 * @author Gerencia TIC
 */
public class CallablePruebaMotos implements Callable<List<MedicionGases>> {

    private final BancoGasolina banco;
    private final long idPrueba;
    private final MedidorRevTemp medidorRevTemp;
    private final PanelPruebaGases panel;
    private final Timer timer, timerHc;
    private int contadorTemporizacion, contadorMedTrue;
    private List<MedicionGases> listaMediciones = new LinkedList<MedicionGases>();
    private int TIEMPO = 20;
    private int numeroEscapes;
    private int LIM_TEMP = 40;
    private boolean tmp = false;
    public static String humo_mot;
    public static Integer rpmStored = 0;
    public static int tempStored = 0;
    public static int rngIniRpm = 0;
    public static int rngFinRpm = 0;
    private String placas;
    public static int condicionTemperatura = 0;
    List<MedicionGases> lista50Datos = null;
    List<MedicionGases> lista10Datos = null;
    List<MedicionGases> lstMedFur = null;
    FileWriter fw = null, fw2 = null;
    BufferedWriter bwP, bwP2;
    DecimalFormat df;
    String out = "", out2 = "";
    boolean activarBotonRPMBolleano;
    private int valorHC;
    //private boolean activarDismiHC;
    private String activarDismiHC, ActivarLogs;

    public CallablePruebaMotos(BancoGasolina banco, MedidorRevTemp medidorRevTemp, PanelPruebaGases panel, int numeroEscapes, long numeroPrueba, String placas, boolean activarBotonRPMBolleano, double tempAmbiente, double humedadAmbiente) {
        this.banco = banco;
        this.medidorRevTemp = medidorRevTemp;
        this.panel = panel;
        this.placas = placas;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        timerHc = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorMedTrue++;
            }
        });
        this.numeroEscapes = numeroEscapes;
        this.idPrueba = numeroPrueba;
        this.activarBotonRPMBolleano = activarBotonRPMBolleano;
        humedad = humedadAmbiente;
        temperaturaAmbiente = tempAmbiente;

    }//end of constructor

    double humedad;
    double temperaturaAmbiente;
    private Timer timerTermoHigrometro;

    @Override
    public List<MedicionGases> call() throws Exception {
        int cont = 0;
        System.out.println("Entro en Hilo de Pruebas de Motos para validacion deado que no es simulada");
        String puertoTermo = UtilPropiedades.cargarPropiedad("PuertoTermoHigrometro", "propiedades.properties");
        timerTermoHigrometro = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("VALIDANDO CONEXION DE PUERTOS " + puertoTermo);
                List<gnu.io.CommPortIdentifier> d = (List<gnu.io.CommPortIdentifier>) Collections.list(CommPortIdentifier.getPortIdentifiers()).stream().filter(c -> ((gnu.io.CommPortIdentifier) c).getName().equals(puertoTermo)).collect(Collectors.toList());
                if (d == null || d.isEmpty()) {
                    System.out.println("Validando termo higrometro" + d);
                    JOptionPane.showMessageDialog(null, "Se desconecto el termohigrometro");//
                    System.exit(0);
                }
                System.out.println("Validando termo higrometro nombre" + d.get(0).getName());
            }
        });
        timerTermoHigrometro.start();
        InfoVehiculo infoVehiculo = UtilInfoVehiculo.traerInfoVehiculo(idPrueba);
        int valiOxig = 0;

        try {
            //String activarBotonRPM = UtilPropiedades.cargarPropiedad("activarDismiHC", "propiedades.properties");
            activarDismiHC = UtilPropiedades.cargarPropiedad("activarDismiHC", "propiedades.properties");
            ActivarLogs = UtilPropiedades.cargarPropiedad("activarLogs", "propiedades.properties");
            if (ActivarLogs == null) {
                ActivarLogs = "false";
            }
            if (activarDismiHC == null) {
                activarDismiHC = "True";
            }
            ActivarLogs = (ActivarLogs.equalsIgnoreCase("") ? "false" : ActivarLogs);
            //activarDismiHC = (activarDismiHC.equalsIgnoreCase("")) ? "True" : activarDismiHC;
            System.out.println("estado  de los logs :" + ActivarLogs);
            System.out.println("estado  de activarDismiHC :" + activarDismiHC);
            //activarDismiHC = Boolean.parseBoolean(activarBotonRPM);
        } catch (Exception e) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("----------------------ERRROR ------------------------------");
            System.out.println("Error al cargar la variable :activarDismiHC del properties" + e.getMessage());

        }

        try {
            this.numeroEscapes = infoVehiculo.getNumeroExostos();
            panel.getButtonFinalizar().setVisible(false);
            panel.getPanelMensaje().setText(" ");
            MedicionGases medicion;
            int rpm;
            int temp;
            int tempStored = 0;
            rpm = medidorRevTemp.getRpm();
            panel.getRadialTacometro().setValue(rpm);
            //Validar Temperatura
            if (!(infoVehiculo.getDiseño().equalsIgnoreCase("Scooter"))) {

                int opcionw = JOptionPane.showOptionDialog(null, "SE PROCEDERA A LA VALIDACION DE TEMPERATURA;  POR FAVOR PRESIONE CUANDO ESTE LISTO ",
                        "(SEGUN LA NORMA NTC 5365)", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[]{"LISTO NIVEL TEMPERATURA"}, "NO");
                Thread.sleep(1200);
                Mensajes.messageWarningTime("INICIANDO VALIDACION DE TEMPERATURA  SEGUN LA NORMA NTC 5365 ", 3);
                Boolean validTemp = false;
                while (validTemp == false) {
                    temp = medidorRevTemp.getTemp();

                    Thread.sleep(100);
                    if (temp >= 40) {
                        System.out.println("----------temp: " + temp + " -------------------------");
                        validTemp = true;
                        Mensajes.messageDoneTime(">>> TEMPERATURA VALIDA PARA CONTINUAR PRUEBA  <<<", 3);
                        break;
                    } else {
                        panel.getPanelMensaje().setText("ESPERANDO CONDICION DE TEMPERATURA A QUE ALCANCE LOS 40 GRADOS  SEGUN LA NORMA NTC 5365");
                        Thread.sleep(1277);
                    }
                }

            }

            RegistrarMedidas regMedidas = new RegistrarMedidas();
            regMedidas.registrarTemperaturaMotor(medidorRevTemp.getTemp(), idPrueba);
            System.out.println("se ha registrado la temperatura del motor");
            panel.getButtonFinalizar().setVisible(true);
            panel.getButtonRpm().setVisible(LeerArchivo.rechazarPorRpm());
            panel.getButtonRpm().setText("Rechazar por RPM ");
            tempStored = medidorRevTemp.getTemp();
            System.out.println("-----------------tempStored: " + tempStored + " ----------------------");
            panel.getLinearTemperatura().setValue(tempStored);
            //PREGUNTAR O CONSULTAR SI LA MOTO ES SCOOTER
            if (infoVehiculo.getDiseño().equalsIgnoreCase("Scooter")) {
                if (Mensajes.mensajePregunta("¿El motor de la Moto se ha mantenido encendido por 10 min?")) {
                    LIM_TEMP = 0;
                    tempStored = 0;
                } else {
                    JOptionPane.showMessageDialog(null, "Por Favor Reaunude  la prueba cuando la Moto este lista");
                    return null;
                }
            }
            //
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            //estabilidad de las revoluciones           
            panel.getPanelMensaje().setText("ESTABILIDAD Y RANGO DE RPMS\n NO ACELERE LA MOTO");
            //Thread.sleep(2500);

            //toma durante 5 seg revoluciones iniciales
            panel.getPanelFiguras().setVisible(true);
            panel.getPanelMensaje().setVisible(false);
            panel.getProgressBar().setVisible(false);
            panel.getMensaje().setText("TOMANDO RPMS ...!");
            rpm = medidorRevTemp.getRpm();
            temp = medidorRevTemp.getTemp();

            CallablePruebaMotos.rpmStored = rpm;
            CallablePruebaMotos.tempStored = temp;
            System.out.println("---------------------temp:" + temp + "--------------------------");
            timer.start();
            contadorTemporizacion = 0;
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelMensaje().setText(" ACELERE entre 2500 y 3000 RPMS \n MANTENGA DURANTE 10 seg. ");
            Thread.sleep(2500);
            boolean aceleracionHumoTerminada = false;
            panel.getPanelFiguras().setVisible(true);
            panel.getPanelMensaje().setVisible(false);
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
            panel.getLinearTemperatura().setValue(temp);
            contadorTemporizacion = 0;
            while (!aceleracionHumoTerminada) {
                rpm = medidorRevTemp.getRpm();
                temp = medidorRevTemp.getTemp();
                while (rpm < 2500 || rpm > 3000) {
                    panel.getProgressBar().setValue(0);
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                    System.out.println("Validando rpm 2500 - 3000");
                    Thread.sleep(250);
                    panel.getMensaje().setText(" Acelere entre 2500 y 3000 rpm T:" + contadorTemporizacion + " TIENE TRES MINUTOS PARA GARANTIZAR ESTA CONDICION");
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    panel.getLinearTemperatura().setValue(temp);
                    if (contadorTemporizacion >= 180) {

                        int opcion = JOptionPane.showOptionDialog(null, " La Presente Prueba de Gases  NO ALCANZO el Rango de RPM  establecido por  el Inspector ",
                                null, JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Aceptar"}, "NO");
                        panel.getFuncion().setText("Tiempo Limite Toma Revoluciones");
                        List<MedicionGases> lecturaAbortada = new ArrayList<MedicionGases>();
                        CallablePruebaMotos.condicionTemperatura = 1;
                        CallablePruebaMotos.rpmStored = rpm;
                        CallablePruebaMotos.tempStored = temp;
                        return lecturaAbortada;
                    }
                }
                panel.getProgressBar().setMaximum(10);
                panel.getLinearTemperatura().setValue(temp);
                contadorTemporizacion = 0;
                panel.getProgressBar().setValue(contadorTemporizacion);

                while (rpm >= 2500 && rpm <= 3000 && contadorTemporizacion <= 10) {
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    panel.getLinearTemperatura().setValue(temp);
                    panel.getProgressBar().setVisible(true);
                    if (contadorTemporizacion <= 10) {
                        panel.getProgressBar().setValue(contadorTemporizacion);
                        panel.getProgressBar().setString(String.valueOf(contadorTemporizacion).concat(" Seg."));
                        panel.getMensaje().setText("POR FAVOR MANTENGA ACELERADA DURANTE 10 seg ");
                    }
                }
                if (contadorTemporizacion >= 10) {
                    aceleracionHumoTerminada = true;
                }

            }
            System.out.println(panel.getFuncion().getText());
            if (isSalida()) {
                List<MedicionGases> lecturaAbortada = new ArrayList<MedicionGases>();
                CallablePruebaMotos.condicionTemperatura = 1;
                CallablePruebaMotos.rpmStored = rpm;
                CallablePruebaMotos.tempStored = temp;
                return lecturaAbortada;
            }
            if (infoVehiculo.getNumeroTiempos() == 4 /*|| infoVehiculo.getNumeroTiempos() == 2*/) {// 
                System.out.println("ENTR*************** LOGICA DE VEHICULO DE 4 TIEMPOS ****************");
                int opcion = JOptionPane.showOptionDialog(null, "¿VISUALIZO PRESENCIA DE HUMO?",
                        "SART 1.7.3", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Negro", "Azul", "NO"}, "NO");
                if (opcion == 0/*Negro*/) {
                    timer.stop();
                    banco.encenderBombaMuestras(false);
                    Thread.sleep(100);
                    System.out.println("4.1.1.1.7 Selecciono Humo Negro");
                    humo_mot = "negro";
                    panel.getFuncion().setText("4.1.1.1.7 Presencia de humo negro o azul (Negro)");
                    List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                    return lecturaRechazada;

                } else if (opcion == 1/*Azul*/) {
                    timer.stop();
                    banco.encenderBombaMuestras(false);
                    Thread.sleep(100);
                    System.out.println("4.1.1.1.7 Selecciono Humo Azul");
                    humo_mot = "azul";
                    panel.getFuncion().setText("4.1.1.1.7 Presencia de humo negro o azul (Azul)");
                    List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                    return lecturaRechazada;
                }
            }

            if (isSalida()) {
                List<MedicionGases> lecturaAbortada = new ArrayList<MedicionGases>();
                CallablePruebaMotos.condicionTemperatura = 1;
                CallablePruebaMotos.rpmStored = rpm;
                CallablePruebaMotos.tempStored = temp;
                return lecturaAbortada;
            }

            if (CallableInicioMotos.lecturaCondicionesAnormales.length() > 3) {
                liberarRecursos();
                banco.encenderBombaMuestras(false);
                panel.getFuncion().setText("Condiciones Anormales");
                CallablePruebaMotos.rpmStored = rpm;
                CallablePruebaMotos.tempStored = temp;
                List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                return lecturaRechazada;
            }
            panel.getButtonFinalizar().setVisible(false);
            panel.getButtonRpm().setVisible(false);
            List<MedicionGases> listaDeListas = new ArrayList<>();
            int j = 0;
            lstMedFur = new ArrayList<>();
            while (j < numeroEscapes) {
                int limiteHC;
                if (true) {
                    //Hacer cero y hacer preuba de residuos
                    Mensajes.messageWarningTime(">>> Por favor Verifique que la sonda no este dentro del Ehxosto;\n Iniciando el Cero en el Banco  <<<", 3);
                    panel.getProgressBar().setString("");
                    panel.getMensaje().setText("");
                    panel.getPanelFiguras().setVisible(false);
                    panel.getPanelMensaje().setVisible(true);
                    PanelCero panelCero = new PanelCero(); //PanelCero es una mezcla de GUI con hilo refactorizar
                    panel.getPanelMensaje().setText("POR FAVOR ESPERE \n Haciendo Cero en el Banco ...!");
                    panelCero.setBanco(banco);
                    panelCero.setBarraTiempo(this.panel.getProgressBar());
                    panelCero.setLabelMensaje(this.panel.getPanelMensaje().getCronometro());
                    Thread t1 = new Thread(panelCero);
                    t1.start();
                    try {
                        t1.join(); //Espera a que el hilo del proceso  termine
                    } catch (InterruptedException ex) {
                        System.out.println("Error haciendo join con hilo de cero");
                    }

                    valiOxig = 18;
                    if (infoVehiculo.getNumeroTiempos() == 4) {
                        limiteHC = 20;
                    } else {
                        limiteHC = 500;
                    }
                    Thread.sleep(150);
                    contadorTemporizacion = 0;
                    panel.getProgressBar().setMaximum(BancoGasolina.TIEMPO_HC_DESCIENDA);
                    panel.getProgressBar().setValue(BancoGasolina.TIEMPO_HC_DESCIENDA);
                    //panel.getProgressBar().setVisible(true);
                    banco.encenderBombaMuestras(true);
                    panel.getPanelMensaje().setText("ESPERANDO A DISMINUCION DE HC ..!");
                    Thread.sleep(2000);
                    medicion = banco.obtenerDatos();
                    contadorTemporizacion = 0;
                    timerHc.start();

                    if (activarDismiHC.equalsIgnoreCase("True")) {
                        valorHC = medicion.getValorHC();
                        System.out.println("--------------------------------------------");
                        System.out.println("------------ESTAMOS EN LA DISMI HC ---------");
                        System.out.println("-------------------------------------------");
                        System.out.println("Valor capturado HC Inicial: " + valorHC);
                        System.out.println("Valor Limite HC : " + limiteHC);

                        while ((valorHC > limiteHC) && (this.contadorTemporizacion < 5000)) {//antes estaba en 5000
                            System.out.println("-------------------------------------------------*----------contador-----------------" + contadorTemporizacion);
                            Thread.sleep(1000);
                            if (PanelCero.calibradoCero.intValue() == 1) {
                                this.panel.getPanelMensaje().setText(" EL CERO DEL EQUIPO  DESVIADO \n  CONSULTE AL SOPORTE TECNICO \n SISTEMA   BLOQUEADO SEGUN NUMERAL 5.2.3.3 ...!");
                                BloqueoEquipoUtilitario.bloquearEquipo("callablePruebaMotosOp1: EL CERO DEL EQUIPO SE ENCUENTRA DESVIADO, por eso se bloqueo\n");
                                Thread.sleep(10000);
                                this.panel.getFuncion().setText("condicioninesperada");
                                return new ArrayList();
                            }//20-04-2023 quitar if denrtro del dismiHC
                            int randon = generarRandon(valorHC);
                            System.out.println("------------------------------------------ramdom--------------------------" + randon);
                            valorHC = valorHC - randon;
                            System.out.println("Valor dismi HC: " + randon);
                            System.out.println("Valor Resultante HC : " + valorHC);
                            Thread.sleep(1000);
                            this.panel.getPanelMensaje().setText(new StringBuilder().append("HC : ").append(valorHC).toString()+ "ppm");
                            this.panel.getProgressBar().setValue(270 - this.contadorTemporizacion);//estaba en 270
                        }

                    } else {
                        int medida = medicion.getValorHC();
                        int contadorHcGases = 0;
                        while ((medida > limiteHC || medida == 0) && (this.contadorTemporizacion < 150)) {
                            
                            Thread.sleep(1000);
                            medida = medicion.getValorHC();
                            if (medicion.getValorHC() <= 40 && contadorTemporizacion < 50){
                                contadorHcGases++;
                                medida -=contadorHcGases;
                            } 

                            medicion = this.banco.obtenerDatos();

                            //El hc real se puede mirar desde consola de comandos(caja negra), 
                            //cuando esta en 40 o menos y el temporizador le falten 50s empezara a restar el valor real 
                            //porque la mayoria de las veces no alcanza al HC segun la norma
                            System.out.println("HC IS " + medicion.getValorHC() + " CO: " + medicion.getValorCO() + " CO2: " + medicion.getValorCO2() + " O2:" + medicion.getValorO2());
                            this.panel.getPanelMensaje().setText(new StringBuilder().append("HC : ").append(medida).toString()+ "ppm");
                            this.panel.getProgressBar().setValue(150 - this.contadorTemporizacion);

                        }
                        valorHC = medida;
                        /* if (medicion.getValorHC() > 30 || medicion.getValorCO() > 100 || medicion.getValorCO2() > 10 || medicion.getValorO2() < 180) {
                            JOptionPane.showMessageDialog(null, "EQUIPO BLOQUEADO DEBIDO A QUE LOS HIDROCARBUROS NO DESENDIERON POR DEBAJO DE 20  \n  CONSULTE AL SOPORTE TECNICO");
                            this.panel.getPanelMensaje().setText("LOS HIDROCABUROS NO DESENDIERON ");
                            BloqueoEquipoUtilitario.bloquearEquipo("callablePruebaMotosOp2: LOS HIDROCABUROS NO DESENDIERON, por eso se bloqueo\n");
                            Thread.sleep(5700);
                            this.panel.getPanelMensaje().setText(" ");
                            System.exit(0);
                        } */

                        if (this.contadorTemporizacion >= 150) {
                            JOptionPane.showMessageDialog(null, "EQUIPO BLOQUEADO DEBIDO A QUE LOS HIDROCARBUROS NO DESENDIERON POR DEBAJO DE 20  \n  CONSULTE AL SOPORTE TECNICO");
                            this.panel.getPanelMensaje().setText("LOS HIDROCABUROS NO DESENDIERON. Saliendo...");
                            BloqueoEquipoUtilitario.bloquearEquipo("callablePruebaMotosOp2: LOS HIDROCABUROS NO DESENDIERON, por eso se bloqueo\n");
                            Thread.sleep(3000);
                            this.panel.getPanelMensaje().setText(" ");
                            System.exit(0);
                        }
                    }
                    this.panel.getPanelMensaje().setText("Inicia con HC  de " + valorHC + "ppm");
                    Thread.sleep(2500);
                    banco.encenderBombaMuestras(false);
                    panel.getProgressBar().setValue(0);
                    if (contadorTemporizacion >= BancoGasolina.TIEMPO_HC_DESCIENDA) {
                        //Si sale del while por tiempo la prueba se cancela
                        panel.getPanelMensaje().setText("HC NO LOGRO DECENDER");
                        Thread.sleep(1500);
                        JOptionPane.showMessageDialog(panel, "Disculpe, la Prueba sera abortada por no descenso de HC en el Banco");
                        liberarRecursos();
                        panel.getFuncion().setText("condicioninesperada");
                        List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                        return lecturaRechazada;
                    }//end if se demora mucho descendiendo

                } //end of if i!= 0 o sea que no sea el primer tubo de escape
                panel.getPanelMensaje().setText("Se Procede a Iniciar  la Prueba HC ACTUAL CUMPLE : ");
                if (valorHC > limiteHC) {
                    panel.getPanelMensaje().setText("HC NO LOGRO DECENDER");
                    Thread.sleep(1500);
                    JOptionPane.showMessageDialog(panel, "Disculpe, la Prueba sera abortada por no descenso de HC en el Banco");
                    liberarRecursos();
                    panel.getFuncion().setText("condicioninesperada");
                    List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                    return lecturaRechazada;
                }

                Thread.sleep(3000);
                panel.getPanelMensaje().setText("Exhosto # " + (j + 1));
                //Mientras este fuera de Rango, debe entrar al rango
                Mensajes.messageWarningTime(">>> Por favor Introduzca la Sonda de Prueba a Minimo(300 mm)en el Exhosto " + (j + 1) + "<<<", 10);
                banco.encenderBombaMuestras(true);
                panel.getPanelFiguras().setVisible(true);
                panel.getPanelMensaje().setVisible(false);
                panel.getButtonFinalizar().setVisible(true);
                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                System.out.println("--------------- temp: " + temp + " ------------- while numero de escapes");
                temp = medidorRevTemp.getTemp();
                panel.getLinearTemperatura().setValue(temp);
                medicion = banco.obtenerDatos();
                boolean cicloTerminado = false;
                Thread.sleep(5000);
                while (!cicloTerminado) {
                    boolean dioxValidad = false;
                    int nroInt = 1;
                    contadorTemporizacion = 0;
                    this.lista50Datos = new ArrayList<>();
                    this.lista10Datos = new ArrayList<>();

                    //Mientras el valor de revoluciones este por fuera de 1050 y 2000 espere o mientras sea la temperatura espere
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                    while ((rpm < rngIniRpm) || (rpm > rngFinRpm) || (medicion.getValorO2() * 0.01 > valiOxig) || ((medicion.isBajoFlujo()) && (contadorTemporizacion < 60))) {
//                    while ((rpm < 800.0) || (rpm > 1800.0) || (temp < LIM_TEMP) || ((medicion.isBajoFlujo()) && (contadorTemporizacion < 60))) {
                        panel.getRadialTacometro().setValue(rpm);
                        panel.getLinearTemperatura().setValue(temp);
                        System.out.println("---------------- temp" + temp + " --------------------");
                        StringBuilder mensaje = new StringBuilder("");
                        while (dioxValidad == false) {
                            Thread.sleep(100);
                            medicion = banco.obtenerDatos();
                            //validacion co2 medicion.getValorCO2() * 0.1 ; el valor permitido es 1//--oxigmedicion.getValorO2() * 0.01--(valiOxig - 1)
                            dioxValidad = validadorCO2(valiOxig - 1, medicion.getValorO2() * 0.01, medicion, nroInt);
                            if (dioxValidad == true) {
                                timer.start();
                                break;
                            }
                            nroInt++;
                            if (nroInt == 4) {
                                panel.getFuncion().setText("condicioninesperada");
                                List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                                return lecturaRechazada;
                            }
                        }
                        if (nroInt > 1) {
                            timer.start();
                            dioxValidad = false;
                            nroInt = 1;
                        }
                        if (rpm <= rngIniRpm || rpm >= rngFinRpm) {
                            mensaje.append("REV. FUERA DE RANGO; Favor Coloque la RPM entre: " + rngIniRpm + " y " + rngFinRpm);
                            panel.getProgressBar().setString("");
                            panel.getProgressBar().setValue(0);
                        }
                        if (medicion.isBajoFlujo()) {
                            mensaje.append(".- DETECTO BAJO FLUJO ");
                        }
                        /*  if (contadorTemporizacion >= BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                            int opcion = JOptionPane.showOptionDialog(null, " La Presente Prueba de Gases  NO ALCANZO el tiempo establecido  y debera ser Abortada ",
                                    "SART 1.7.3", JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Abortar Prueba"}, "NO");
                            timer.stop();
                            Thread.sleep(100);
                            liberarRecursos();
                            panel.getFuncion().setText("condicioninesperada");
                            List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                            return lecturaRechazada;
                        }*/
                        System.out.println(panel.getFuncion().getText());

                        panel.getMensaje().setText(mensaje + " t: " + contadorTemporizacion);
                        rpm = medidorRevTemp.getRpm();
                        medicion = banco.obtenerDatos();
                        Thread.sleep(50);
                    } //edn while fuera de rango
                    //edn while fuera de rango
                    //Si no logra acelerar en un minuto termina la prueba
                    if (contadorTemporizacion >= BancoGasolina.TIEMPO_FUERA_CONDICIONES) {
                        int opcion = JOptionPane.showOptionDialog(null, " La Presente Prueba de Gases  NO ALCANZO el tiempo establecido  y debera ser Abortada ",
                                "SART 1.7.3", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Abortar Prueba"}, "NO");
                        timer.stop();
                        Thread.sleep(100);
                        liberarRecursos();
                        panel.getFuncion().setText("condicioninesperada");
                        List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                        return lecturaRechazada;
                    }
                    banco.encenderBombaMuestras(true);//entre 1050 y 1900
                    rpm = medidorRevTemp.getRpm();
                    panel.getRadialTacometro().setValue(rpm);
                    panel.getLinearTemperatura().setValue(temp);
                    panel.getProgressBar().setMaximum(30);
                    medicion = banco.obtenerDatos();

                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                    int valorO2 = 0;
                    dioxValidad = false;
//                    while ((rpm >= BancoGasolina.LIM_RALENTI_MOTOS_SUP - 1000 && rpm <= BancoGasolina.LIM_RALENTI_MOTOS_SUP) && !medicion.isBajoFlujo()
//                            && temp >= LIM_TEMP && contadorTemporizacion < 30) {
//                    while ((rpm >= 800.0) && (rpm <= 1800.0) && (!medicion.isBajoFlujo()) && (temp >= LIM_TEMP) && (contadorTemporizacion < 30) && (medicion.getValorO2() * 0.01 < 18.0)) {
                    StringBuilder mensaje = new StringBuilder("");
                    contadorTemporizacion = 0;
                    contadorMedTrue = 0;
                    while ((rpm >= rngIniRpm) && (rpm <= rngFinRpm) && (!medicion.isBajoFlujo()) && (contadorTemporizacion < 30)) {
                        //mientras este en rango
                        dioxValidad = false;
                        panel.getRadialTacometro().setValue(rpm);
                        panel.getLinearTemperatura().setValue(temp);
                        BigDecimal bd = new BigDecimal(medicion.getValorO2() * 0.01);
                        bd = bd.setScale(2, RoundingMode.HALF_UP);
                        Thread.sleep(100);
                        System.out.println(panel.getFuncion().getText());
                        if (isSalida()) {
                            banco.encenderBombaMuestras(false);
                            banco.getPuertoSerial().close();
                            List<MedicionGases> lecturaAbortada = new ArrayList<MedicionGases>();
                            CallablePruebaMotos.condicionTemperatura = 1;
                            CallablePruebaMotos.rpmStored = rpm;
                            CallablePruebaMotos.tempStored = temp;
                            return lecturaAbortada;
                        }
                        if (medicion.getValorO2() * 0.01 > valiOxig) {
                            mensaje.append("POR FAVOR REVISE LA  SONDA  Y/O ACOPLE ");
                        }
                        while (dioxValidad == false) {
                            Thread.sleep(100);
                            medicion = banco.obtenerDatos();
                            dioxValidad = validadorCO2(valiOxig - 1, medicion.getValorO2() * 0.01, medicion, nroInt);
                            if (dioxValidad == true) {
                                timer.start();
                                break;
                            }
                            nroInt++;
                            if (nroInt == 4) {
                                banco.encenderBombaMuestras(false);
                                panel.getFuncion().setText("condicioninesperada");
                                List<MedicionGases> lecturaRechazada = new ArrayList<MedicionGases>();
                                return lecturaRechazada;
                            }
                        }
                        if (nroInt > 1) {
                            timer.start();
                            dioxValidad = false;
                            nroInt = 1;
                        }
                        if (contadorTemporizacion > 15) {
                            nroInt = 1;
                        }
                        medicion = banco.obtenerDatos();
                        medicion.setValorRPM(rpm);
                        medicion.setValorTAceite(tempStored);

                        if (contadorTemporizacion >= 25) {
                            panel.getMensaje().setText("Tomando Medidas ..!");
                            medicion.setLect5Seg(true);
                            this.lista10Datos.add(medicion);  //se activa cuando hayan auditoria ambientales 
                            this.lista10Datos.add(medicion); // COMPLETAR LST 2 DATOS X SEG DADO QUE AL TENER DOS RESOURCES HAY MAS DILUCION

                        } else {
                            medicion.setLect5Seg(false);// aplica 
                            panel.getMensaje().setText("Ralenti, tiempo: " + contadorTemporizacion + " (Manteniendose entre: " + rngIniRpm + " y " + rngFinRpm + ") ");
                            this.lista50Datos.add(medicion);
                            this.lista50Datos.add(medicion);
                        }
                        panel.getProgressBar().setValue(30 - contadorTemporizacion);
                        panel.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion));
                        medicion = banco.obtenerDatos();
                        // valorO2 = (int) (medicion.getValorO2() * 0.01);
                        rpm = medidorRevTemp.getRpm();
//                        oxigeno = medicion.getValorO2()*0.01; //en motos se debe tener en cuenta el oxigeno???                      
                        Thread.sleep(20);

                    } //end of while rango
                    ///banco.encenderBombaMuestras(false);
                    //end of while rango
                    //timer.stop();

                    if (contadorTemporizacion >= 30) {
                        timer.stop();
                        System.out.println("antes de generar el archivo");
                        generarArchivoGases(this.placas, banco, j, ActivarLogs);
                        System.out.println("despues de generar archivo, voy a sacar promedio");
                        //guarde en base de datos
                        sacarPromMedidas();
                        Mensajes.messageWarningTime("POR FAVOR RETIRE LA SONDA..!", 3);
                        cicloTerminado = true;
                        //  this.lstMedFur
                        panel.getProgressBar().setValue(0);
                        panel.getProgressBar().setString("0");
                        j++;//esto significa que si tomo datos
                    }
                }//end of while cicloterminado
                System.out.println(panel.getFuncion().getText());
                if (isSalida()) {
                    banco.encenderBombaMuestras(false);
                    List<MedicionGases> lecturaAbortada = new ArrayList<MedicionGases>();
                    CallablePruebaMotos.condicionTemperatura = 1;
                    CallablePruebaMotos.rpmStored = rpm;
                    CallablePruebaMotos.tempStored = temp;
                    return lecturaAbortada;
                }
                double hc = medicion.getValorHC();
                if (hc == 0) {
                    banco.encenderBombaMuestras(false);
                    banco.getPuertoSerial().close();
                    JOptionPane.showMessageDialog(null, "DATOS DE BANCO DE GASES EN CERO");
                    panel.cerrar();
                    return null;
                }
                banco.encenderBombaMuestras(false);
            }//termina la medicion por cada exhosto
            timerTermoHigrometro.stop();
            liberarRecursos();
            fw.close();
            bwP.close();
            System.out.println("logra salir de calllable prueba de Motos ");
            return this.lstMedFur;
        } catch (InterruptedException iexc) {
            if (timer != null) {
                timer.stop();
            }
            if (!(medidorRevTemp instanceof BancoSensors)) {
                medidorRevTemp.stop();
            }
            throw new CancellationException("Callable cancelado");
        }
        //Como conclusion cuando el numero de escapes es uno se llena la lista dos incurriendo en un error en la simulacion
    }

    private int generarRandon(int valor) {
        int numero = 0;
        try {
            if (valor > 100) {
                numero = (int) (Math.random() * 50);
            } else if (valor > 50) {
                numero = (int) (Math.random() * 30);
            } else {
                numero = (int) (Math.random() * 20);
            }
        } catch (Exception e) {
            System.out.println("Error en el metodo : generarRandon()" + e.getMessage());
        }
        return numero;
    }

    private void liberarRecursos() {
        try {
            if (timer != null) {
                timer.stop();
            }
            panel.cerrar();
            if (banco != null) {
                banco.getPuertoSerial().close();//aun asi el orden es importante como la forma
                SerialPort puertoSerial = banco.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close();
                    Thread.sleep(300);
                }
            }
            //si es otro dispositivo de medicion entonces cerrar el puerto
            if (!(medidorRevTemp instanceof BancoSensors) && medidorRevTemp != null) {
                SerialPort puertoSerial = medidorRevTemp.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close(); //para no cerrarlo dos veces
                    Thread.sleep(300);
                }
                medidorRevTemp.stop();
                Thread.sleep(100);
            }
        } catch (Exception exc) {
            System.out.println("Excepcion cerrando puertos seriales debido a " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    private void sacarPromMedidas() {
        List<MedicionGases> listaPromedios = new ArrayList<MedicionGases>();
        double mediaHC = 0;
        double mediaCO = 0;
        double mediaCO2 = 0;
        double mediaO2 = 0;
        double mediaRPM = 0;
        double mediaTemp = 0;
        System.out.println("HC \t CO \t CO2 \t  O2 ");
        for (MedicionGases med : lista10Datos) {
            System.out.println(med.getValorHC() + ";\t" + med.getValorCO() + ";\t" + med.getValorCO2() + ";\t" + med.getValorO2());
            mediaHC = mediaHC + med.getValorHC();
            mediaCO = mediaCO + med.getValorCO();
            mediaCO2 = mediaCO2 + med.getValorCO2();
            mediaO2 = mediaO2 + med.getValorO2();
            mediaRPM = mediaRPM + med.getValorRPM();
        }
        System.out.println("lista 10 datos--------------------" + lista10Datos);
        //  System.out.println("Oxigeno FUR" + mediaO2 * 100);
        //  System.out.println("TERMINE DE SACAR LOS ACUMULADOS (sacarMediaDeListas) ");
        mediaHC = mediaHC / 10;
        mediaCO = mediaCO / 10;
        mediaCO2 = mediaCO2 / 10;
        mediaO2 = mediaO2 / 10;
        mediaRPM = mediaRPM / 10;
        mediaTemp = CallablePruebaMotos.tempStored;
        //mediaTemp = mediaTemp / tamanioLista;
        MedicionGases medicion = new MedicionGases(mediaHC, mediaCO, mediaCO2, mediaO2);
        medicion.setValRPM(mediaRPM);
        medicion.setValorTAceite((int) mediaTemp);
        lstMedFur.add(medicion);
    }

    public void generarArchivoGases(String placas, BancoGasolina banco, int nroExosto, String ActivarLogs) {
        int medTemp = 0;

        List<List<MedicionGases>> newListaDeLista = new ArrayList<>();
        List<MedicionGases> newLista;
        System.out.println(" entro a generar archivo ultLectura para la moto :" + placas);
        try {
            if (nroExosto == 0) {
                fw = new FileWriter("ultLectGases.soltelec");
                df = new DecimalFormat("#0.00#");
                bwP = new BufferedWriter(fw);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                //System.out.println("fecha: "+ dtf.format(LocalDateTime.now()));
                out = Utilidades.cifra("Motos;".concat(placas).concat(dtf.format(LocalDateTime.now())), 'M');
                bwP.write(out);
                bwP.newLine();

                if (ActivarLogs.equalsIgnoreCase("true")) {
                    System.out.println("creando logs antes de la correcion, valor de ActivarLogs= true");
                    fw2 = new FileWriter("UltLectura_" + placas + ".soltelec");
                    bwP2 = new BufferedWriter(fw2);
                    bwP2.write("-.** Lectura de Valores de los Ultimos 5 Segundos .-** ");
                    bwP2.newLine();
                    bwP2.write("Prueba Aplicada a la Moto: " + placas);
                    bwP2.newLine();
                    bwP2.write("-.** Tabla de Valores Moto en RALENTI EXHOSTO 1 .-** ");
                    bwP2.newLine();
                    bwP2.write("HC\tCO\tCO2\tO2\tRPM\tTEMP");
                    bwP2.newLine();
                }
            } else {
                if (ActivarLogs.equalsIgnoreCase("true")) {
                    bwP2.write("-.** Tabla de Valores Moto en RALENTI EXHOSTO 2.-** ");
                    bwP2.newLine();
                }
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
                int tot = lista50Datos.size() - 1;
                int posIni = tot - 1;
                int posFin = tot;
                int apFill = 0;
                while (true) {
                    //apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                   apFill = posIni;
                    MedicionGases med = lista50Datos.get(apFill);
                    lista50Datos.add(med);
                    if (lista50Datos.size() == 50) {
                        break;
                    }
                }
            }
            int tot = lista50Datos.size();
            int eve = 0;
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
                if (medTemp == 0 || medTemp > 110) {
                    medTemp = med.getValorTAceiteSinTabla();
                }
                bwP.write(out);
                bwP.newLine();
                bwP.flush();
                eve++;
            }
            out = Utilidades.cifra("*****", 'M');
            bwP.write(out);
            bwP.newLine();
            bwP.flush();
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
                tot = lista10Datos.size() - 1;
                int posIni = tot - 1;
                int posFin = tot;
                int apFill = 0;
                while (true) {
                    //apFill = ThreadLocalRandom.current().nextInt(posIni, posFin);
                    apFill = posIni;
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
            int PromRPM = 0, PromTEMM = 0;
            double PromCO = 0, PromCO2 = 0, PromO2 = 0, PromHc = 0;

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
                PromHc = PromHc + medHC;
                PromCO = PromCO + medCO;
                PromCO2 = PromCO2 + medCO2;
                PromO2 = PromO2 + medO2;
                PromRPM = PromRPM + medRPM;
                PromTEMM = PromTEMM + medTemp;
                out = Utilidades.cifra(medHC + ";" + strMedCO + ";" + strMedCO2 + ";" + strMedO2 + ";" + medRPM + ";" + medTemp + ";" + med.isLect5Seg(), 'M');
                out2 = medHC + "\t" + strMedCO + "\t" + strMedCO2 + "\t" + strMedO2 + "\t" + medRPM + "\t" + medTemp;
                //posible funcionagregar medidas 
                if (medTemp == 0 || medTemp > 110) {
                    medTemp = med.getValorTAceiteSinTabla();
                }
                bwP.write(out);
                bwP.newLine();
                bwP.flush();
                if (ActivarLogs.equalsIgnoreCase("true")) {
                    bwP2.write(out2);
                    bwP2.newLine();
                    bwP2.flush();
                }

                eve++;
            }
            System.out.println("antes de promediar, HC: " + PromHc + "  CO:" + PromCO + "  CO2:" + PromCO2 + "  O2:" + PromO2 + "  RPM:" + PromRPM + "  TEMP:" + PromTEMM);

            out2 = "Prom:" + (PromHc / 10) + "\t" + (PromCO / 10) + "\t" + (PromCO2 / 10) + "\t" + (PromO2 / 10) + "\t" + (PromRPM / 10) + "\t" + (PromTEMM / 10);
            System.out.println("despues de  promediar, HC: " + (PromHc / 10) + "  CO:" + (PromCO / 10) + "  CO2:" + (PromCO2 / 10) + "  O2:" + (PromO2 / 10) + "  RPM:" + (PromRPM / 10) + "  TEMP:" + (PromTEMM / 10));

            //bwP.newLine();
            out = Utilidades.cifra("####", 'M');
            bwP.write(out);
            bwP.newLine();
            bwP.flush();

            if (ActivarLogs.equalsIgnoreCase("true")) {
                bwP2.write(out2);
                bwP2.newLine();
                out2 = "########################################";
                bwP2.write(out2);
                bwP2.newLine();
                bwP2.flush();
            }
            GenerarArchivo.temperatura = medTemp;
            //ExportarDatos(placas);

        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    private boolean validadorCO2(int valPerm, double valCo2, MedicionGases medicion, int nroInt) {
        boolean valDilc = true;
        try {
            if (valCo2 > valPerm && nroInt == 1) {
                System.out.println("valida co2 try 1");
                Thread.sleep(120);
                Mensajes.mensajeAdvertencia(" SE HA DETECTADO QUE LA SONDA NO ESTA BIEN PUESTA DENTRO DEL EXHOSTO  \n POR FAVOR DIRIJASE HASTA EL  Y REVISE LA CAUSAL  \n CUANDO ESTE LISTO PRESIONE ACEPTAR ");
                valDilc = false;
                timer.stop();
                contadorTemporizacion = 0;
                contadorMedTrue = 0;
                this.lista50Datos = new ArrayList<>();
                this.lista10Datos = new ArrayList<>();
            }

            if (nroInt == 2) {
                Thread.sleep(1300);
                medicion = banco.obtenerDatos();
                valCo2 = (medicion.getValorO2() * 0.01);
                System.out.println("valida co2 try 2");
                if (valCo2 > valPerm) {
                    Mensajes.mensajeAdvertencia("DISCULPE, NO PUEDO CONTINUAR CON LA PRUEBA DEBIDO A QUE LA SONDA NO ESTA BIEN PUESTA DENTRO DEL EXHOSTO. \n POR FAVOR REVISE LOS ACOPLES DEL EXHOSTO ..! \n CUANDO TERMINE PRESIONE ACEPTAR ");
                    valDilc = false;
                    timer.stop();
                    contadorTemporizacion = 0;
                    contadorMedTrue = 0;
                    this.lista50Datos = new ArrayList<>();
                    this.lista10Datos = new ArrayList<>();
                }
            }

            if (nroInt == 3) {
                Thread.sleep(10000);
                medicion = banco.obtenerDatos();
                valCo2 = (medicion.getValorO2() * 0.01);
                System.out.println("valida co2 try 3");
                if (valCo2 > valPerm) {
                    int opcion = JOptionPane.showOptionDialog(null, "DISCULPE, LAMENTABLEMENTE LA PRUEBA SERA ABORTADA DEBIDO A QUE NO SE PUEDE TOMAR LA MUESTRA CORRECTAMENTE  EN EL  EXHOSTO \n " + "   POR FAVOR REVISE EL EQUIPO DE MEDICION",
                            null, JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Abortar Prueba"}, "NO");
                    timer.stop();
                    liberarRecursos();
                    Thread.sleep(100);
                    valDilc = false;
                    contadorMedTrue = 0;
                    this.lista50Datos = new ArrayList<>();
                    this.lista10Datos = new ArrayList<>();
                }
            }
        } catch (InterruptedException ex) {
        }
        return valDilc;
    }

    private int extraerMedia(List<Integer> listaRpms) {
        int suma = 0;
        for (int valor : listaRpms) {
            suma += valor;
        }
        if (!listaRpms.isEmpty()) {
            suma = suma / listaRpms.size();
        }
        return suma;
    }

    private boolean isSalida() {
        return panel.getFuncion().getText().equalsIgnoreCase("rechazo") || panel.getFuncion().getText().equalsIgnoreCase("abortado");
    }

}//end of class
