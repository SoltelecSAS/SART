/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import com.mysql.jdbc.Connection;
import com.soltelec.modulopuc.utilidades.Mensajes;
import com.soltelec.opacimetro.brianbee.HiloTomaDatosDieselBrianBee;
import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.LcdColor;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.pruebasgases.DialogoDiesel;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.pruebasgases.SimuladorRpm;
import static org.soltelec.pruebasgases.diesel.WorkerCiclosDiesel.aplicTrans;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.ShiftRegister;
import org.soltelec.util.UtilGasesModelo;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.UtilidadAbortoPrueba;
import org.soltelec.util.VariablesOpacidad;

import java.sql.*;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.TimerTask;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.soltelec.models.controllers.EquipoController;

/**
 * Hilo que realiza la prueba de los ciclos de opacidad la logica de adquisicion
 * de datos esta por naturaleza fuertemente acoplada a la marca del Opacimetro.
 * Sinembargo esta clase no esta totalmente acoplada al opacimetro sensors
 * retorna una lista de listas de bytes.
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class CallableCiclosOpacidadSensors implements Callable<List<MedidaGeneral>> {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(CallableCiclosOpacidadSensors.class.getName());
    private Opacimetro opacimetro;
    private MedidorRevTemp medidorRevTemp;
    private PanelPruebaGases panel;
    private int contadorTemporizacion;
    private Timer timer;
    private DialogoCiclosAceleracion dialogo;
    private int T_ANTES_ACELERAR = 20;
    private int velocidadRalenti = 750;
    private double velocidadCrucero = 4500;
    private HiloTomaDatosDiesel hiloTomaDatos;
    //private Thread t;
    private int T_RALENTI = 20;//20 segundos para entrar a ralenti
    private int rpm, temp, temperaturaAntesIniciar, temperaturaDespuesTerminar;
    private boolean pruebaExitosa = false;
    private ShiftRegister shiftRegisterVelocidad;
    //private JDialogReconfiguracionKit dialogReconfiguracion;//sustituido por el boton cambiar rpms
    // private SerialPort serialPort; //el dispositivo para medir revoluciones y temperatura debe estar configurado
    private List<Byte> listaCiclo0;
    private List<Byte> listaCiclo1;
    private List<Byte> listaCiclo2;
    private List<Byte> listaCiclo3;
    private int gob0, gob1, gob2, gob3, gob4 = 0;

    CausasAbortoDiesel causaUltimoIntento = CausasAbortoDiesel.CICLO_NO_REALIZADO_AUN;
    CausasAbortoDiesel causaPenultimoIntento = CausasAbortoDiesel.CICLO_NO_REALIZADO_AUN;
    CausasAbortoDiesel causaAntePenultimoIntento = CausasAbortoDiesel.CICLO_NO_REALIZADO_AUN;

    private final DecimalFormat df2 = new DecimalFormat("##.##");
    private final DecimalFormat df1 = new DecimalFormat("#0.0");
    //private final DecimalFormat decimalFormatFiltrada = new DecimalFormat("#0.0000");

    private ArrayList<Double> listaCiclo0CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo1CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo2CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo3CorreBerLambert = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo0ConK = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo1ConK = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo2ConK = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo3ConK = new ArrayList<Double>();
    private ArrayList<Double> listaCiclo0Filtrada;
    private ArrayList<Double> listaCiclo1Filtrada;
    private ArrayList<Double> listaCiclo2Filtrada;
    private ArrayList<Double> listaCiclo3Filtrada;
    private ArrayList<Double> listaCiclo1Raw;
    private ArrayList<Double> listaCiclo2Raw;
    private ArrayList<Double> listaCiclo3Raw;
    private ArrayList<Double> listaCiclo0Raw;

    private MedicionOpacidad med;
    private double diametroExosto;
    private JDialog d;
    private ArrayList<Double> listaDoubleCiclo0;
    private ArrayList<Double> listaDoubleCiclo1;
    private ArrayList<Double> listaDoubleCiclo2;
    private ArrayList<Double> listaDoubleCiclo3;
    private String usuario;
    private String password;
    private String direccionIP;
    private long idUsuario, idPrueba;
    public static List<MedidaGeneral> listaMedidasTemp;
    private SimuladorRpm simuladorRpm;//clase para simular las revoluciones
    boolean simulacion = false;
    private Future futureSimulador;//Future para monitorear la tarea del simulador
    private boolean simuladasRalenti = true;
    private Random random = new Random();
    private boolean subidaAceleracion = false;//se controla la simulacion con booleanos
    private boolean simuladasCrucero;
    private boolean bajadaAceleracion;
    private Timer timerSimulacion;
    private double rpmSimuladas;
    private int contadorSimulacion;
    private DialogoDiesel dlgDiesel = null;
//    private int numeroCiclos=0;
////    private int numeroPruebasUnitarias = 0;
//    private List<Double> listaMaximosTresCiclos = null;
//    private int numeroPruebasUnitariasTemperatura = 0;
//    private int numeroPruebasUnitariasDiferenciaA5 = 0;
//    private int numeroPruebasUnitariasDiferenciaA2=0; 

    public CallableCiclosOpacidadSensors(Opacimetro opacimetro, MedidorRevTemp medidorRevTemp, PanelPruebaGases panel, DialogoCiclosAceleracion dialogo, double diametroExosto, long idPrueba, long idUsuario, Boolean simulada, long idHojaPrueba) {
        this.opacimetro = opacimetro;
        this.medidorRevTemp = medidorRevTemp;
        this.panel = panel;
        this.dialogo = dialogo;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
        this.diametroExosto = diametroExosto;
        this.idUsuario = idUsuario;
        this.idPrueba = idPrueba;
        this.simulacion = simulada;
        if (simulacion == false) {
            this.medidorRevTemp = medidorRevTemp;
        } else {
            simuladorRpm = new SimuladorRpm();
            this.medidorRevTemp = simuladorRpm;
            timerSimulacion = new Timer(250, new ListenerSimulacion());
            Random r = new Random();
            //TODO: Agregar validacion de vehiculos pesados y livianos
            try {
                ConsultarDatosVehiculo datosVehiculo = new ConsultarDatosVehiculo();
                Integer idTipoVehiculo = datosVehiculo.buscarTipoVehiculo(idHojaPrueba);
                switch (idTipoVehiculo) {
                    case 1:
                    case 2:
                    case 6:
                        velocidadCrucero = 3800 - r.nextInt(500);//Livianos
                        System.out.println("Velocidad crucero liviano");
                        break;
                    case 3:
                    case 7:
                        velocidadCrucero = 2600 - r.nextInt(350);//Pesados
                        System.out.println("Velocidad crucero pesado");
                        break;
                    default:
                        velocidadCrucero = 2200 - r.nextInt(500);
                }
            } catch (SQLException | ClassNotFoundException e) {
                System.out.println("Error: no se pudo establecer los valores de velocidad crucero. " + e);
                System.out.println("Se dejan valores por defecto de velocidad crucero: 2200");
                velocidadCrucero = 2200 - r.nextInt(500);
            }
//        velRalenti =  800 - r.nextInt(350);// Se modifica ya que esta arrojando valores ireales
            velocidadRalenti = 800 - r.nextInt(280);
//        velCrucero = 2200 - r.nextInt(500);
            simuladorRpm.setREVOLUCIONES_CRUCERO((int) velocidadCrucero);
            simuladorRpm.setREVOLUCIONES_RALENTI((int) velocidadRalenti);
            simuladorRpm.setSimularCrucero(false);
            this.medidorRevTemp = simuladorRpm;
        }

    }//end of constructor

    public java.sql.Connection getConnection() throws ClassNotFoundException, SQLException {
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        java.sql.Connection conexion = Conex.getConnection();
        return conexion;
    }

    /**
     * Tres intentos para realizar exitosamente los cuatro ciclos puede
     * repetirse por que no acelera adecuadamente o por diferencia aritmetica o
     * porque el opacimetro queda midiendo mas de lo adecuado.
     *
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<MedidaGeneral> call() throws Exception {
        List<MedidaGeneral> listaMedidas = null;
        ExecutorService executor = Executors.newCachedThreadPool();
        if (this.simulacion == true) {
            executor = Executors.newSingleThreadExecutor();
            Future<Void> futureSimulador = executor.submit(simuladorRpm);
        }
        HiloTomaDatosDiesel hiloTomaDatos = null;
        Component icono = panel.getPanelMensaje().getComponent(0);
        Component msg = panel.getPanelMensaje().getComponent(1);
        panel.getPanelMensaje().add(icono, 0);
        Font fAlt = new Font(Font.SERIF, Font.BOLD, 61);
        msg.setFont(fAlt);
        panel.getPanelMensaje().updateUI();
        panel.getPanelMensaje().repaint();
        Thread.sleep(100);
        timer.start();

        //lugardelosechos
        JOptionPane.showMessageDialog(panel, "Validación de temperatura del motor, oprima cuando este listo.", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        mostrarDialogoTemperatura();

        pedirAceleracionSuave();

        //quedan guardadas en velocidadRalenti y velocidadGobernada
        detectarRalentiGobernadas();
        //probar que el vehiculo puede acelerar a gobernadas en menos de 5 s.
        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText("ACELERE A GOBERNADAS EN MENOS DE 5s CUANDO SE LE INDIQUE");
        if (this.simulacion == true) {
            simuladorRpm.setREVOLUCIONES_RALENTI((int) velocidadRalenti);
            simuladorRpm.setSimularCrucero(false);
        }
        Thread.sleep(2500);
        probarAceleracionGobernada();
        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);

        panel.getPanelMensaje().setText("INICIO DE LA PRUEBA UNITARIA");
        panel.getMensaje().setText(" ");
        Thread.sleep(2500);
        //PRUEBA UNITARIA DE ACELERACION

        JOptionPane.showMessageDialog(null, "POR FAVOR INTRODUZCA LA SONDA DE PRUEBA");
        if (this.simulacion == true) {
            simuladorRpm.stop();
            executor.shutdown();
            timerSimulacion.start();
        }
        try {
            //con la variable booleana pruebaExitosa se indica que el programa
            //debe salir
            int ciclos = 0;
            int numeroPruebasUnitarias = 0;//variable para contar cuantas     
            int numeroPruebasUnitariasTemperatura = 0;//variable para contar cuantas 
            int numeroPruebasUnitariasDiferenciaA5 = 0;//variable para contar cuantas     
            int numeroPruebasUnitariasDiferenciaA2 = 0;//variable para contar cuantas     
            List<Double> listaMaximosTresCiclos = null;
            gob0 = 0;
            gob1 = 0;
            gob2 = 0;
            gob3 = 0;
            //-------------
            int flag = 0;
            //inicia prueba unitaria, tres posibles opciones
            while (!pruebaExitosa && numeroPruebasUnitarias < 3) {
                if (flag > 0) {
                    panel.getPanelMensaje().setText("POR FAVOR INTRODUZCA NUEVAMENTE LA SONDA DE PRUEBA");
                    JOptionPane.showMessageDialog(null, "POR FAVOR INTRODUZCA NUEVAMENTE LA SONDA DE PRUEBA");
                    panel.getPanelMensaje().setText(" ");
                }
                flag++;
                ciclos = 0;
                temp = medidorRevTemp.getTemp();
                temperaturaAntesIniciar = temp;
//                temperaturaAntesIniciar=30;
                if (temperaturaAntesIniciar < 50) {
                    JOptionPane.showMessageDialog(null, " SE INFORMA QUE LA TEMPERATURA ES MENOR A 50 LO CUAL AL FINALIZAR LOS CICLOS SE HARA UNA VERIFICACION SEGUN NUMERAL 3.1.3.9 NTC 4231");
                }
                if (simulacion == true) {
                    simuladasRalenti = true;//subida aceleracion = false EL SIMULADOR EMPIEZA A RETORNAR LA VELOCIDAD RALENTI
                    simuladorRpm.setREVOLUCIONES_RALENTI((int) velocidadRalenti);
                    simuladorRpm.setSimularCrucero(false);
                }
                mostrarDlgCiclos();
                dialogo.repintarRangos(velocidadRalenti, velocidadCrucero);
                dialogo.getDisplayTemperatura().setLcdValue(temp);

                //--------------------------------
                //tres posibles ciclos para cada prueba unitaria
                while (ciclos < 4 && numeroPruebasUnitarias < 3) {
//                  T_ANTES_ACELERAR = 10;
                    T_ANTES_ACELERAR = 3;
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.STANDARD_LCD);
                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos);
                    dialogo.getLedRojo().setLedBlinking(false);
                    dialogo.getLedRojo().setLedOn(true);
                    dialogo.getLedVerde().setLedOn(false);

                    if (simulacion == true) {
                        simuladasRalenti = true;//subida aceleracion = false EL SIMULADOR EMPIEZA A RETORNAR LA VELOCIDAD RALENTI                        
                        rpm = (int) rpmSimuladas;
                    } else {
                        rpm = medidorRevTemp.getRpm();
                    }
                    temp = medidorRevTemp.getTemp();

                    contadorTemporizacion = 0;

                    while ((rpm < (velocidadRalenti - Opacimetro.LIMITE_RANGO_CICLOS) || rpm > (velocidadRalenti + Opacimetro.LIMITE_RANGO_CICLOS)) && contadorTemporizacion < T_RALENTI) {
                        Thread.sleep(100);
                        System.out.println("Vehiculo fuera del rango de ralenti +-100 rpms");
                        dialogo.getLabelMensaje().setText("FUERA RANGO RALENTI");
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        if (simulacion == true) {
                            simuladasRalenti = true;//subida aceleracion = false EL SIMULADOR EMPIEZA A RETORNAR LA VELOCIDAD RALENTI                        
                            rpm = (int) rpmSimuladas;
                        } else {
                            rpm = medidorRevTemp.getRpm();
                        }
                        dialogo.getRadialTacometro().setValue(rpm);
                        dialogo.getDisplayTemperatura().setLcdValue(medidorRevTemp.getTemp());
                    }//end of while

                    Thread.sleep(10);//punto de cancelacion
                    //si pasan 20 segundos y el vehiculo no entro a ralenti ... gastar un intento
                    if (contadorTemporizacion >= T_RALENTI) {
                        JOptionPane.showMessageDialog(dialogo, "EL VEHICULO NO ENTRA A RALENTI");
                        ciclos = 0;
                        continue;

                    }//end if
                    dialogo.getLabelMensaje().setText("POR FAVOR MANTENGA RALENTI");

                    if (simulacion == true) {
                        simuladasRalenti = true;//subida aceleracion = false EL SIMULADOR EMPIEZA A RETORNAR LA VELOCIDAD RALENTI                        
                        rpm = (int) rpmSimuladas;
                    } else {
                        rpm = medidorRevTemp.getRpm();
                    }

                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLACK_LCD);
                    contadorTemporizacion = 0;
                    while ((rpm >= velocidadRalenti - Opacimetro.LIMITE_RANGO_CICLOS && rpm <= velocidadRalenti + Opacimetro.LIMITE_RANGO_CICLOS) && contadorTemporizacion <= T_ANTES_ACELERAR) {
                        Thread.sleep(100);
                        semaforo();
                        dialogo.getDisplayTiempo().setLcdValue(T_ANTES_ACELERAR - contadorTemporizacion);
                        dialogo.getRadialTacometro().setValue(rpm);
                        if (simulacion == true) {
                            simuladasRalenti = true;//subida aceleracion = false EL SIMULADOR EMPIEZA A RETORNAR LA VELOCIDAD RALENTI                        
                            rpm = (int) rpmSimuladas;
                        } else {
                            rpm = medidorRevTemp.getRpm();
                        }
                    } //Si acelera antes de tiempo mal

                    Thread.sleep(10);
                    if (!(contadorTemporizacion >= T_ANTES_ACELERAR)) {
                        JOptionPane.showMessageDialog(dialogo, "MANTENGA EL VEHICULO EN RALENTI HASTA QUE SE LE INDIQUE QUE ACELERE\n ");
                        ciclos = 0;
                        continue;
                    }
                    if (!(opacimetro instanceof OpacimetroBrianBee)) {
                        hiloTomaDatos = new HiloTomaDatosDiesel();
                        hiloTomaDatos.setOpacimetro(opacimetro);
                        hiloTomaDatos.setNumeroCiclo(ciclos);
                    } else {
                        hiloTomaDatos = new HiloTomaDatosDieselBrianBee();
                        hiloTomaDatos.setOpacimetro(opacimetro);
                    }
                    Thread threadTest = new Thread(hiloTomaDatos);
                    threadTest.start();

                    if (simulacion == true) {
                        contadorSimulacion = 0;
                        simuladasRalenti = false;
                        subidaAceleracion = true;//EL SIMULADOR INICIA A SUBIR LAS ACELERACIONES HASTA GOBERNADAS
                        rpm = (int) rpmSimuladas;
                    } else {
                        rpm = medidorRevTemp.getRpm();
                    }
                    System.err.println("CICLO: " + ciclos);
                    dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                    if (simulacion == true) {
                        rpm = (int) rpmSimuladas;
                    } else {
                        rpm = medidorRevTemp.getRpm();
                    }

                    //----------------------------------------------------------
                    //--------------------        Modificar --------------------
                    //----------------------------------------------------------
                    contadorTemporizacion = 0;
                    while (contadorTemporizacion < 5 && rpm <= (velocidadCrucero - Opacimetro.LIMITE_RANGO_CICLOS)) {
                        dialogo.getDisplayTiempo().setLcdValue(contadorTemporizacion);
                        if (simulacion == true) {
                            rpm = (int) rpmSimuladas;
                        } else {
                            rpm = medidorRevTemp.getRpm();
                        }
                        dialogo.getRadialTacometro().setValue(rpm);
                        Thread.sleep(10);

                    }//end of while

                    Thread.sleep(10);
                    if (contadorTemporizacion >= 5) {
                        JOptionPane.showMessageDialog(dialogo, "SE LE INFORMA QUE EL VEHICULO NO ALCANZA LAS RPM GOBERNADAS EN MENOS DE 5 SEGUNDOS ");
                        //dialogo.setVisible(false);
                        d.setVisible(false);
                        numeroPruebasUnitarias++;
                        actualizarCausasAborto(CausasAbortoDiesel.FALLA_ACELERACION_GOBERNADA);
                        ciclos = 0;
                        if (procesandoValorMinimo()) {
                            if (procesandoValorMaximo()) {
                                mostrarDlgCiclos();
                                dialogo.repintarRangos(velocidadRalenti, velocidadCrucero);
                                dialogo.getDisplayTemperatura().setLcdValue(temp);
                                continue;
                            } else {
                                throw new FallaAjusteInicialException();
                            }
                        } else {
                            throw new FallaAjusteInicialException();
                        }
                    }

                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos + " GOBERNADAS");
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLUE2_LCD);
                    if (simulacion == true) {
                        rpm = (int) rpmSimuladas;
                    } else {
                        rpm = medidorRevTemp.getRpm();
                    }

                    

                    long tiempoGobernada = System.currentTimeMillis();
                    boolean tiempoGobernadaCorriendo=true;
                    while (tiempoGobernadaCorriendo && (rpm >= (velocidadCrucero - Opacimetro.LIMITE_RANGO_CICLOS) && rpm <= (velocidadCrucero + Opacimetro.LIMITE_RANGO_CICLOS))) {
                        
                        // Calcular el tiempo transcurrido en segundos
                        long tiempoActual = System.currentTimeMillis();
                        long tiempoTranscurridoms = (tiempoActual - tiempoGobernada);
                        double tiempoTranscurrido = (((tiempoActual - tiempoGobernada)+500) / 1000);
                        
                        Thread.sleep(10);
                        dialogo.getDisplayTiempo().setLcdValue(tiempoTranscurrido);
                        dialogo.getRadialTacometro().setValue(rpm);
                        if (simulacion == true) {
                            rpm = (int) rpmSimuladas;
                        } else {
                            rpm = medidorRevTemp.getRpm();
                        }

                        if (ciclos == 0 && rpm > gob0) gob0 = rpm;
                        
                        if (ciclos == 1 && rpm > gob1) gob1 = rpm;
                        
                        if (ciclos == 2 && rpm > gob2) gob2 = rpm;
                        
                        if (ciclos == 3 && rpm > gob3) gob3 = rpm;
                            
                        tiempoGobernadaCorriendo = tiempoTranscurridoms < 2700;
                        
                        System.err.print("GO= " + gob0 + " G1= " + gob1 + " G2=" + gob2 + " G3=" + gob3);
                    }//end while
                    Thread.sleep(10);//punto de cancelacion
                    if (contadorTemporizacion < 2) {
                        JOptionPane.showMessageDialog(dialogo, "VEHICULO SOLO MANTUVO RPM GOBERNADAS DURANTE : " + contadorTemporizacion + "SEGUNDO(S)");
                        numeroPruebasUnitarias++;
                        actualizarCausasAborto(CausasAbortoDiesel.FALLA_ACELERACION_GOBERNADA);
                        ciclos = 0;
                        // break;
                    }//end if
                    dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
                    dialogo.getLedRojo().setLedOn(true);
                    dialogo.getLedVerde().setLedOn(false);
                    if (simulacion == true) {
                        contadorSimulacion = 0;
                        simuladasRalenti = true;
                        subidaAceleracion = false;//EL SIMULADOR INICIA A SUBIR LAS ACELERACIONES HASTA GOBERNADAS
                        rpm = (int) rpmSimuladas;
                    } else {
                        rpm = medidorRevTemp.getRpm();
                    }
                    dialogo.getLabelMensaje().setText("CICLO: " + ciclos + " SUELTE EL PEDAL POR FAVOR");
                    long tiempoInicial = System.currentTimeMillis();
                    while (true) {
                        // Calcular el tiempo transcurrido en segundos
                        long tiempoActual = System.currentTimeMillis();
                        int tiempoTranscurrido = (int) ((tiempoActual - tiempoInicial) / 1000);
                    
                        
                    
                        if (simulacion) {
                            rpm = (int) rpmSimuladas;
                        } else {
                            rpm = medidorRevTemp.getRpm();
                        }

                        dialogo.getRadialTacometro().setValue(rpm);
                        dialogo.getDisplayTiempo().setLcdValue(tiempoTranscurrido);
                        temp = medidorRevTemp.getTemp();
                        dialogo.getDisplayTemperatura().setLcdValue(temp);

                        // Salir del bucle después de 15 segundos
                        if (tiempoTranscurrido >= 15) {
                            break;
                        }
                    
                        try {
                            // Dormir por 10 milisegundos para reducir la carga de la CPU
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //TERMINAR LA RECOLECCION DE DATOS
                    hiloTomaDatos.setCicloTerminado(true);
                    Thread.sleep(50);
                    if (ciclos == 0) {
                        System.out.println("Esperando a que la recoleccion termine");
                        dialogo.getLabelMensaje().setText("Esperando Datos ...");
                        listaCiclo0 = hiloTomaDatos.getListaOpacidad();
                        //gob0 =rpm;
                        System.out.println("Datos del ciclo 0 Recolectados");
                        System.out.println("GOB 0: " + gob0);
                    }
                    if (ciclos == 1) {
                        //listaCiclo1 = hiloTomaDatos.getListaOpacidad();
                        System.out.println("Esperando a que la recoleccion termine");
                        dialogo.getLabelMensaje().setText("ESPERANDO DATOS CICLO 1...!");
                        listaCiclo1 = hiloTomaDatos.getListaOpacidad();//se bloquea hasta que el otro hilo se detenga,,, ¿que pasa si ya termino?
                        //gob1=rpm;
                        System.out.println("Datos del ciclo 1 Recolectados");
                        System.out.println("GOB 1: " + gob1);
                    }
                    if (ciclos == 2) {
                        System.out.println("Esperando a que la recoleccion termine");
                        dialogo.getLabelMensaje().setText("ESPERANDO DATOS CICLO 2...!");
                        listaCiclo2 = hiloTomaDatos.getListaOpacidad();
                        //gob2=rpm;
                        System.out.println("Datos del ciclo 2 recolectados");
                        System.out.println("GOB 2: " + gob2);
                    }
                    if (ciclos == 3) {
                        System.out.println("Esperando a que la recoleccion termine");
                        dialogo.getLabelMensaje().setText("ESPERANDO DATOS CICLO 3...!");
                        listaCiclo3 = hiloTomaDatos.getListaOpacidad();
                        //gob3=rpm;
                        System.out.println("Datos del ciclo 3 Recolectados");
                        System.out.println("GOB 3: " + gob3);
                    }

                    //termina toma de datos
                    System.err.println("FIN CICLO:" + ciclos);
                    ciclos++;
//                    liberarRecursos();

                }//en while ciclos
//                 Mensajes.messageErrorTime("COMPLETA LOS "+ ciclos, 5);
                //PROCESAR LOS DATOS
                panel.getPanelMensaje().setText(" ");///get close
                System.out.println("Deberia Cerrarse?");
                if (numeroPruebasUnitarias >= 3) {
                    JOptionPane.showMessageDialog(null, "DISCULPE, NO SE PUEDEN REALIZAR MAS DE TRES PRUEBAS UNITARIAS "
                            + "\n SEGUN EL NUMERAL  3.2.2 NTC 4231");
                    liberarRecursos();
                    listaMedidasTemp = new ArrayList<MedidaGeneral>();
                    DeficienteOperacionException.tipoException = "RECHAZO POR REALIZACION  DE TRES PRUEBAS UNITARIAS; NUMERAL  3.2.2 NTC 4231";
                    throw new DeficienteOperacionException();

                }
                JOptionPane.showMessageDialog(null, "POR FAVOR RETIRE LA SONDA DE PRUEBA");
                if (listaCiclo0 != null && listaCiclo1 != null && listaCiclo2 != null && listaCiclo3 != null) {
                    List<List<Byte>> listaDeListas = new ArrayList<>();
                    listaDeListas.add(listaCiclo0);
                    listaDeListas.add(listaCiclo1);
                    listaDeListas.add(listaCiclo2);
                    listaDeListas.add(listaCiclo3);
                    listaMaximosTresCiclos = procesarDatos(listaDeListas);
                    generarArchivo();
                    //  liberarRecursos();

                } else { //LA RECOLECCION DE DATOS HA FALLADO DE ALGUN MODO O NO SE COMPLETARON LOS CUATRO CICLOS
                    if (causaUltimoIntento == CausasAbortoDiesel.FALLA_ACELERACION_GOBERNADA) {
                        continue;//vuelve a iniciar la prueba unitaria
                    } else {
                        throw new Exception("Recoleccion de listas ha fallado");
                    }
                }
                d.dispose();

                Double valorCiclo0 = listaMaximosTresCiclos.get(0);
                Double valorCiclo1 = listaMaximosTresCiclos.get(1);// la lista ahora es de tamanio 4
                Double valorCiclo2 = listaMaximosTresCiclos.get(2);
                Double valorCiclo3 = listaMaximosTresCiclos.get(3);
                Double promedio = listaMaximosTresCiclos.get(4);

                System.out.println("VALORCICLO0ANTES" + valorCiclo0);
                System.out.println("VALORCICLO1ANTES" + valorCiclo1);
                System.out.println("VALORCICLO2ANTES" + valorCiclo2);
                System.out.println("VALORCICLO3ANTES" + valorCiclo3);
                System.out.println("promedioANTES" + promedio);

                Double valorCiclo0N = opacidad(valorCiclo0);
                Double valorCiclo1N = opacidad(valorCiclo1);
                Double valorCiclo2N = opacidad(valorCiclo2);
                Double valorCiclo3N = opacidad(valorCiclo3);
                //Double promedioN = opacidad(promedio);
                Double promedioN = (valorCiclo1N + valorCiclo2N + valorCiclo3N) / 3;

                System.out.println("VALORCICLO0DESPUES" + valorCiclo0N);
                System.out.println("VALORCICLO1DESPUES" + valorCiclo1N);
                System.out.println("VALORCICLO2DESPUES" + valorCiclo2N);
                System.out.println("VALORCICLO3DESPUES" + valorCiclo3N);
                System.out.println("PROMEDIODEPUES" + promedioN);

                listaMaximosTresCiclos = null;
                listaMaximosTresCiclos = new ArrayList<Double>();
                listaMaximosTresCiclos.add(valorCiclo1N);
                listaMaximosTresCiclos.add(valorCiclo2N);
                listaMaximosTresCiclos.add(valorCiclo3N);

                for (Double valor : listaMaximosTresCiclos) {
                    System.out.println("El valor es: " + valor);
                }

                double maximo = Collections.max(listaMaximosTresCiclos);
                double minimo = Collections.min(listaMaximosTresCiclos);

                listaMaximosTresCiclos = null;
                listaMaximosTresCiclos = new ArrayList<Double>();

                listaMaximosTresCiclos.add(valorCiclo0N);
                listaMaximosTresCiclos.add(valorCiclo1N);
                listaMaximosTresCiclos.add(valorCiclo2N);
                listaMaximosTresCiclos.add(valorCiclo3N);
                listaMaximosTresCiclos.add(promedioN);

                double comparador = 0.5;
//                if (Boolean.parseBoolean(UtilPropiedades.cargarPropiedad("ltoe", "propiedades.properties"))) {
//                    System.out.println("FUNCION LTOE ACTIVADA");//////////////////////////////////////////////////cambio debido al ltoe del opacimetro
//                    if (Integer.parseInt(UtilPropiedades.cargarPropiedad("ltoe_valor", "propiedades.properties")) == 430) {
//                        comparador = 0.5;
//                    }
//                }
//                if (this.diametroExosto >= 400) {
//                    comparador = 10;
//                }

                //-------------------------------------------------------------
                //---------------------- VALIDACION DEL 2% --------------------
                //-------------------------------------------------------------
                med = opacimetro.obtenerDatos();
                contadorTemporizacion = 0;
                dialogo.setVisible(false);

                ShiftRegister shiftRegister = new ShiftRegister(10, 0);
                while (contadorTemporizacion <= 5) {
                    med = opacimetro.obtenerDatos();
                    double MedicionOpacimetro = opacidad(med.getOpacidadDouble());
                    //shiftRegister.ponerElemento(med.getOpacidadDouble());
                    shiftRegister.ponerElemento(MedicionOpacimetro);
                    //panel.getPanelMensaje().setText("DENSIDAD: " + df2.format(MedicionOpacimetro) + "T: " + contadorTemporizacion);
                    panel.getPanelMensaje().setText("REALIZANDO VALIDACIONES FINALES... T: " + contadorTemporizacion);
                }//end of while
                //  DETERMINAR SI LA PRUEBA FUE EXITOSA O SI SE DEBE RECHAZAR.

                if (shiftRegister.media() >= Opacimetro.LIMITE_RESIDUOS) {
                    //verificar que el ultimo y el penultimo no han sido por lo mismo
                    if (numeroPruebasUnitariasDiferenciaA2 == 2) {
                        //tres veces consecutivas se ha abortado por este motivo rechazar

                        JOptionPane.showMessageDialog(null, "Debe repetir la prueba cuando se hayan resuelto las anomalias del opacimetro\n"
                                + "Norma tecnica 4231 numeral 3.2.5 a");
                        Connection conexion = null;
                        try {
                            conexion = (Connection) getConnection();
                        } catch (ClassNotFoundException | SQLException ex) {
                            Mensajes.mostrarExcepcion(ex);
                        }
                        String serialEquipo = "";
                        try {
                            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
                        } catch (Exception e) {
                            serialEquipo = "Serial no Encontrado ";
                        }
                        try {
                            conexion.setAutoCommit(false);
                            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,observaciones=? , usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?;";
                            java.sql.PreparedStatement instruccion = conexion.prepareStatement(statement);
                            instruccion.setString(1, "Condiciones Anormales");
                            instruccion.setString(2, "Condiciones Anormales obs: Incumplimiento de desviacion del cero en mas de tres pruebas unitarias de aceleracion consecutivas");
                            instruccion.setLong(3, idUsuario);
                            Date d = new Date();
                            Calendar c = new GregorianCalendar();
                            c.setTime(d);
                            instruccion.setTimestamp(4, new java.sql.Timestamp(c.getTimeInMillis()));
                            instruccion.setString(5, serialEquipo);
                            instruccion.setLong(6, idPrueba);
                            instruccion.executeUpdate();
                            instruccion.clearParameters();
                            conexion.commit();
                            conexion.setAutoCommit(true);
                            conexion.close();
                            return null;
                            //System.out.println("Datos enviados");
                        } catch (SQLException ex) {
                            Mensajes.mostrarExcepcion(ex);
                        }
                        return null;//Retorna nulo para indicar que no hay nada que registrar en la base de datos

                    } else {//continuar haciendo las pruebas
                        JOptionPane.showMessageDialog(null, "Repetir la prueba porque el opacimetro mide \n"
                                + "mas de 0.15 m^-1 opacidad:" + shiftRegister.media());
                        d.dispose();
                        numeroPruebasUnitariasTemperatura = 0;
                        numeroPruebasUnitariasDiferenciaA5 = 0;
                        numeroPruebasUnitariasDiferenciaA2++;
                        if (numeroPruebasUnitarias < 3) {
                            JOptionPane.showMessageDialog(null, "Retire la sonda de prueba");
                            if (procesandoValorMinimo()) {
                                if (procesandoValorMaximo()) {
                                    continue;
                                } else {
                                    throw new FallaAjusteInicialException();
                                }
                            } else {
                                throw new FallaAjusteInicialException();
                            }
                        }
                        actualizarCausasAborto(CausasAbortoDiesel.DOS_PORCIENTO_DESPUES_CICLOS);
                        numeroPruebasUnitarias++;
                        continue;
                    }
                }
                //-------------------------------------------------------------
                //---------------------- VALIDACION DEL 5% --------------------
                //-------------------------------------------------------------
                System.out.println("VALOR MAXIMO: " + maximo);
                System.out.println("VALOR MINIMO: " + minimo);
                System.out.println("COMPARADOR: " + comparador);

                if (Math.abs(maximo - minimo) > comparador) {//0.5
                    //FALTA VER SI ESTO YA OCURRIO Y DEBE GENERARSE RECHAZO
                    //Verificar si el ultimo y el penultimo han sido por 
                    if (numeroPruebasUnitariasDiferenciaA5 == 2) {
                        System.out.println("-------------------------------------------Entre aca----------------------------------");
                        JOptionPane.showMessageDialog(null, " Prueba rechazada por que presenta diferencia entre los ciclos"
                                + "mayor a " + comparador + "m^-1 durante tres veces consecutivas norma 4231 numeral 3.2.4 b");
                        listaMedidasTemp = armarListas(listaMaximosTresCiclos, velocidadRalenti, velocidadCrucero, temperaturaAntesIniciar, medidorRevTemp.getTemp(), gob0, gob1, gob2, gob3);
                        DeficienteOperacionException.tipoException = "LA DIFERENCIA  ARITMETICA ENTRE EL MAYOR Y EL MENOR DE OPACIDAD ES MAYOR AL 0,5 m^-1 EN LAS TRES ACELERACIONES; (Norma 4231 numeral 3.2.4 b) ";
                        listaMedidas = armarListas(listaMaximosTresCiclos, velocidadRalenti, velocidadCrucero, temperaturaAntesIniciar, temperaturaDespuesTerminar, gob0, gob1, gob2, gob3);
                        for (MedidaGeneral medida : listaMedidas) {
                            System.out.println("------------a-a-a-a-----------------a-a-a-a-a------------------" + medida.getValorMedida());
                        }
                        //WorkerCiclosDiesel.registrarMedidas(listaMedidas);
                        WorkerCiclosDiesel.registrarMedidasRechazada(listaMedidas);
                        throw new DeficienteOperacionException();
                    } else {
                        JOptionPane.showMessageDialog(null, "DIFERENCIA  ARITMETICA DE CICLOS: Maximo: " + maximo + " Minimo: " + minimo);
                        actualizarCausasAborto(CausasAbortoDiesel.DIFERENCIA_ARITMETICA_CICLOS);
                        numeroPruebasUnitariasTemperatura = 0;
                        numeroPruebasUnitariasDiferenciaA5++;
                        numeroPruebasUnitariasDiferenciaA2 = 0;
                        if (numeroPruebasUnitarias < 3) {
                            JOptionPane.showMessageDialog(null, "Retire la sonda de prueba");
//                            ajusteInicialMinimoMaximo();
                            if (procesandoValorMinimo()) {
                                if (procesandoValorMaximo()) {
                                    continue;
                                } else {
                                    throw new FallaAjusteInicialException();
                                }
                            } else {
                                throw new FallaAjusteInicialException();
                            }
                        }
                        numeroPruebasUnitarias++;
                        d.dispose();
                        continue;
                    }
                }//end if maximo - minimo

                //-------------------------------------------------------------
                //---------- VALIDACION DE TEMPERATURA FINAL ------------------
                //-------------------------------------------------------------
                temp = medidorRevTemp.getTemp();
                temperaturaDespuesTerminar = temp;
                System.out.println("Temperatura antes de iniciar: " + temperaturaAntesIniciar + " Temperatura despues terminar: " + temperaturaDespuesTerminar);
                if (temperaturaAntesIniciar < 50 && (Math.abs(temperaturaAntesIniciar - temperaturaDespuesTerminar) >= 10)) {
                    System.out.println("Estoy en validando diferencia de temperatura----------------------------------------------------------------");

                    if (numeroPruebasUnitariasTemperatura == 1) {
                        JOptionPane.showMessageDialog(null, "VEHICULO RECHAZADO POR QUE LA DIFERENCIA ANTES Y DESPUES DE INICIAR LOS CICLOS ES  "
                                + "\n MAYOR A 10 DURANTE DOS PRUEBAS UNITARIAS CONCECUTIVAS : t1 = " + temperaturaAntesIniciar + " t2 = " + temperaturaDespuesTerminar
                                + "\n SEGUN NUMERAL 3.1.3.9 NTC 4231");
                        liberarRecursos();
                        listaMedidasTemp = new ArrayList<MedidaGeneral>();
                        DeficienteOperacionException.tipoException = "RECHAZO POR DIFERENCIA ENTRE LA TEMPERATURA INICIAL Y FINAL MAYOR A 10 DURANTE DOS PRUEBAS UNITARIAS CONSECUTIVAS;NUMERAL 3.1.3.9 NTC 4231";
                        listaMedidas = armarListas(listaMaximosTresCiclos, velocidadRalenti, velocidadCrucero, temperaturaAntesIniciar, temperaturaDespuesTerminar, gob0, gob1, gob2, gob3);
                        WorkerCiclosDiesel.registrarMedidasRechazada(listaMedidas);
                        throw new DeficienteOperacionException();
                    } else {
                        actualizarCausasAborto(CausasAbortoDiesel.DIFERENCIA_TEMPERATURA);
                        numeroPruebasUnitariasTemperatura++;
                        numeroPruebasUnitariasDiferenciaA5 = 0;
                        numeroPruebasUnitariasDiferenciaA2 = 0;
                        JOptionPane.showMessageDialog(null, "DIFERENCIA DE TEMPERATURA ANTES Y DESPUES DE INICIAR LA PRUEBA\n"
                                + "MAYOR A 10  t1:" + temperaturaAntesIniciar + "t2: " + temperaturaDespuesTerminar);
                        //continue;//vuelve a iniciar los ciclos
//                      ajusteInicialMinimoMaximo();
                        if (procesandoValorMinimo()) {
                            if (procesandoValorMaximo()) {
                                continue;
                            } else {
                                throw new FallaAjusteInicialException();
                            }
                        } else {
                            throw new FallaAjusteInicialException();
                        }
                    }
                }

                //-------------------------------------------------------------
                //-----    VALIDACION NUMERO PRUEBAS REALIZADAS ---------------
                //-------------------------------------------------------------
                if (numeroPruebasUnitarias >= 3) {
                    //Finalmente no se pueden hacer mas de tres pruebas unitarias
                    //numeral 3.2.2 no puede hacer mas de tres pruebas unitarias
                    liberarRecursos();
                    JOptionPane.showMessageDialog(null, "No puede realizar mas de tres pruebas unitarias \n"
                            + "ver numeral 3.2.2 norma 4231");
                    liberarRecursos();
                    listaMedidasTemp = new ArrayList<MedidaGeneral>();
                    DeficienteOperacionException.tipoException = "RECHAZO POR TRES PRUEBAS UNITARIAS INVALIDAS;(Numeral 3.2.2 NTC 4231) ";
                    throw new DeficienteOperacionException();
                }

                //LLEGADO A ESTE PUNTO LA RPUEBA HA SIDO REALIZADA CON EXTIO
                pruebaExitosa = true;
                //ARMAR LOS DATOS PARA RETORNAR
            } //end while pruebaExitosa \ 
            listaMedidas = armarListas(listaMaximosTresCiclos, velocidadRalenti, velocidadCrucero, temperaturaAntesIniciar, temperaturaDespuesTerminar, gob0, gob1, gob2, gob3);

            try {
                EquipoController Eq = new EquipoController();
                Eq.ActFechaFinal(idPrueba);
                System.out.println("--------------------------------Fecha Actualizada----------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return listaMedidas;
        } catch (InterruptedException ie) {//esta exception es sobretodo para cancelacion
            throw new CancellationException("Prueba Cancelada");
        } finally {
            timer.stop();
            if (dialogo != null) {
                dialogo.cerrar();
                d.dispose();
            }
            liberarRecursos();
        }//end finally

    }//end method call

    private void semaforo() {

        if (contadorTemporizacion == (T_ANTES_ACELERAR - 5)) {
            dialogo.getLabelMensaje().setText("5s  RESTANTES");
            dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
            dialogo.getLedRojo().setLedOn(true);
            dialogo.getLedAmarillo().setLedOn(false);
            dialogo.getLedVerde().setLedOn(false);
        }
        if (contadorTemporizacion == (T_ANTES_ACELERAR - 2)) {
            dialogo.getLabelMensaje().setText("PREPARESE");
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
            dialogo.getLabelMensaje().setText("POR FAVOR ACELERE!!!");
        }
    }

    private void pedirAceleracionSuave() throws HeadlessException, InterruptedException, DeficienteOperacionException, IOException {
        //ACELERACION SUAVE
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelFiguras().setVisible(false);
        if (simulacion == false) {
            int rpmRef = medidorRevTemp.getRpm();
            Thread.sleep(250);
            while (rpmRef <= 400 || rpmRef >= 1100) {
                panel.getPanelMensaje().setText("ESPERANDO CONDICION DE RALENTI DEL VEHICULO ");
                rpmRef = medidorRevTemp.getRpm();
                Thread.sleep(277);
            }
            rpmRef = medidorRevTemp.getRpm();
            panel.getPanelMensaje().setText("POR FAVOR REALICE UNA ACELERACION SUAVE.- ");

            Thread.sleep(2000);
            int refRpmUmbral = rpmRef + 450;
            while (true) {
                rpmRef = medidorRevTemp.getRpm();
                Thread.sleep(100);
                if (rpmRef > refRpmUmbral) {
                    break;
                } else {
                    panel.getPanelMensaje().setText("ESPERANDO CONDICION DE ACELERACION SUAVE DEL VEHICULO ");
                    Thread.sleep(277);
                }
            }
        }
        panel.getPanelMensaje().setText(" VERIFICACION DEL CONTROL DE GIRO");
        int opcion = JOptionPane.showConfirmDialog(null, "¿Fue correcta la operacion de los sistemas de control de velocidad de giro o la Capacidad Limitadora del Sistema de Inyeccion Combustible?", "Control de Giro", JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.NO_OPTION) {
            //El usuario indica deficientes condiciones de operacion          
            //se lanza una excepcion (yo se yo se mala practica de programacion controlar el flujo con escepciones)
            liberarRecursos();
            DeficienteOperacionException.tipoException = "Deficiente Operacion del Sistema de Control de Velocidad de Giro del Motor; segun Num 3.1.3.10";
            listaMedidasTemp = new ArrayList<MedidaGeneral>();
            throw new DeficienteOperacionException();
        }
    }

    public void mostrarDialogoTemperatura() {
        // Crear el JDialog
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Medidor de Temperatura");
        dialogo.setSize(300, 100);
        dialogo.setLayout(new FlowLayout());
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Crear un JLabel para mostrar la temperatura
        JLabel etiquetaTemp = new JLabel("Temperatura: ");
        dialogo.add(etiquetaTemp);

        // Mostrar el diálogo de manera modal
        dialogo.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        // SwingWorker para manejar la lógica de la temperatura
        SwingWorker<Void, Double> worker = new SwingWorker<Void, Double>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 5; i++) {
                    // Simular la obtención de la temperatura actual de medidorRevTemp
                    double temperatura = medidorRevTemp.getTemp(); // Aquí usas tu variable real

                    VariablesOpacidad.setTemperaturaMotor(temperatura);

                    // Publicar la temperatura para actualizar la interfaz
                    publish(temperatura);

                    // Esperar 1 segundo
                    Thread.sleep(1000);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Double> chunks) {
                // Obtener la última temperatura publicada y actualizar la etiqueta
                double temperatura = chunks.get(chunks.size() - 1);
                etiquetaTemp.setText("Temperatura: " + temperatura + " °C");
            }

            @Override
            protected void done() {
                // Cerrar el diálogo al finalizar
                dialogo.dispose();
            }
        };

        // Ejecutar el SwingWorker
        worker.execute();

        // Mostrar el diálogo
        dialogo.setVisible(true);
    }

    private void detectarRalentiGobernadas() throws InterruptedException, IOException {
        Component msg = panel.getPanelMensaje().getComponent(1);
        Font fAlt = new Font(Font.SERIF, Font.BOLD, 75);
        msg.setFont(fAlt);
        panel.getPanelMensaje().updateUI();
        panel.getPanelMensaje().repaint();
        panel.getPanelMensaje().setText("INICIANDO VALIDACION  DE \n  PROCESO DE CAPTURA DE RALENTI Y GOBERNADAS");
        Thread.sleep(3000);

        int opcion = JOptionPane.showConfirmDialog(null, "¿CONOCE USTED LAS RPM DEL FABRICANTE?", "PROCESO DE CAPTURA", JOptionPane.YES_NO_OPTION);

        if (opcion == 0) {
            dlgDiesel = new DialogoDiesel();
            JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
            d.setTitle("REGISTRO RPM FABRICANTES..!");
            d.add(dlgDiesel);
            d.setModal(true);
            d.setSize(278, 305);
            d.getRootPane().setDefaultButton(dlgDiesel.getButtonContinuar());
            d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            d.setLocationRelativeTo(panel);
            d.setVisible(true);
            velocidadRalenti = dlgDiesel.getRpmR();
            velocidadCrucero = dlgDiesel.getRpmC();
            return;
        }

        fAlt = new Font(Font.SERIF, Font.BOLD, 64);
        msg.setFont(fAlt);
        panel.getPanelMensaje().updateUI();
        panel.getPanelMensaje().repaint();
        panel.getPanelMensaje().setText("POR FAVOR PONGA EL VEHICULO EN VELOCIDAD RALENTI O MINIMA");
        Thread.sleep(2500);
        panel.getButtonRpm().setActionCommand("Mal funcionamiento Motor");
        panel.getButtonRpm().setVisible(true);
        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);

        //Durante diez segundos tomar la velocidad ralenti
        rpm = medidorRevTemp.getRpm();

        contadorTemporizacion = 0;
        temp = medidorRevTemp.getTemp();
        boolean valRpmRal = false;
        while (valRpmRal == false) {
            if (rpm > 0) {
                shiftRegisterVelocidad = new ShiftRegister(4, rpm);
                valRpmRal = true;
                panel.getPanelMensaje().setVisible(false);
                panel.getPanelFiguras().setVisible(true);
            } else {
                panel.getMensaje().setText("NO DETECTO REVOLUCIONES; POR FAVOR REVISE EL KIT PARA CONTINUAR CON EL PROCESO ");
            }
        }

        while (contadorTemporizacion < 10) {
            rpm = medidorRevTemp.getRpm();
            if (rpm > 1200) {
                while (true) {
                    if (rpm > 1200) {
                        panel.getMensaje().setText("NO DETECTO LAS REVOLUCIONES EN RALENTI; POR FAVOR COLOQUE EL VEHICULO EN RALENTI  ");
                    }
                    if (rpm > 400 && rpm < 1200) {
                        panel.getPanelMensaje().setVisible(false);
                        panel.getPanelFiguras().setVisible(true);
                        shiftRegisterVelocidad = new ShiftRegister(4, rpm);
                        contadorTemporizacion = 0;
                        break;
                    }
                    if (rpm < 300) {
                        panel.getMensaje().setText("ESTOY DETECTANDO LAS REVOLUCIONES EN UN ESTADO MUY MINIMO \n POR FAVOR VERIFIQUE EL VEHICULO  ");
                    }
                }
            }
            panel.getRadialTacometro().setValue(rpm);
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            if (contadorTemporizacion >= 5) {
                shiftRegisterVelocidad.ponerElemento(rpm);
            }
            if (shiftRegisterVelocidad.isStable()) {
                panel.getMensaje().setText("Estable  t:" + contadorTemporizacion);
                Thread.sleep(200);
            }
            if (shiftRegisterVelocidad.isAcelerando()) {
                panel.getMensaje().setText("Acelerando t:" + contadorTemporizacion);
                Thread.sleep(200);
            }
            panel.getMensaje().setText("POR FAVOR MANTENGA EL VEHICULO EN RALENTI  t:" + contadorTemporizacion);
            Thread.sleep(100);
        }//end of while de velocidad 
        velocidadRalenti = (int) shiftRegisterVelocidad.media();

        panel.getPanelFiguras().setVisible(false);
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText("POR FAVOR ACELERE EL VEHICULO A GOBERNADAS");
        if (simulacion == true) {
            simuladorRpm.setREVOLUCIONES_CRUCERO((int) velocidadCrucero);
            simuladorRpm.setSimularCrucero(true);
        }

        panel.getMensaje().setText(" ");
        Thread.sleep(3500);
        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);
        boolean noCrearDenuevo = true;
        valRpmRal = false;
        while (valRpmRal == false) {
            rpm = medidorRevTemp.getRpm();
            rpm = rpm - 150;
            if (rpm >= velocidadRalenti) {
                valRpmRal = true;
                panel.getPanelFiguras().setVisible(true);
                panel.getPanelMensaje().setVisible(false);
            } else {
                panel.getMensaje().setText("NO DETECTO CAMBIO EN LAS REVOLUCIONES; POR FAVOR CAMBIE A GOBERNADA ");
            }
        }
        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);
        contadorTemporizacion = 0;
        while (contadorTemporizacion < 7) {
            panel.getMensaje().setText("POR FAVOR MANTENGA EL VEHICULO EN GOBERNADAS  t:" + contadorTemporizacion);
            rpm = medidorRevTemp.getRpm();
            if (velocidadRalenti > rpm) {
                while (true) {
                    if (velocidadRalenti > rpm) {
                        panel.getMensaje().setText("NO DETECTO LAS REVOLUCIONES; POR FAVOR ACELERE A GOBERNADA ");
                    }
                    if ((rpm + 200) > velocidadRalenti) {
                        shiftRegisterVelocidad = new ShiftRegister(4, rpm);
                        contadorTemporizacion = 0;
                        break;
                    }
                    if (rpm < 300) {
                        panel.getMensaje().setText("ESTOY DETECTANDO LAS REVOLUCIONES EN UN ESTADO MUY MINIMO, CUANDO DEBERIA ESTAR EN GOBERNADAS \n POR FAVOR VERIFIQUE EL VEHICULO  ");
                    }
                }
            }

            panel.getRadialTacometro().setValue(rpm);
            panel.getLinearTemperatura().setValue(medidorRevTemp.getTemp());
            if (contadorTemporizacion == 2 && noCrearDenuevo) {
                shiftRegisterVelocidad = new ShiftRegister(1, rpm);
                noCrearDenuevo = false;
            }
            if (contadorTemporizacion > 2) {
                panel.getMensaje().setText("POR FAVOR MANTENGA A GOBERNADAS");
                shiftRegisterVelocidad.ponerElemento(rpm);
            }
            Thread.sleep(50);
        }
        velocidadCrucero = shiftRegisterVelocidad.media();
    }

    private void probarAceleracionGobernada() throws InterruptedException, DeficienteOperacionException, IOException {
        //PRIMERO VALIDAR QUE EL VEHICULO ESTE EN RALENTI
        panel.getPanelMensaje().setVisible(false);
        panel.getPanelFiguras().setVisible(true);

        boolean aceleracionGobernadas5sValida = false;
        int intentosAceleracionGobernada = 0;
        while (!aceleracionGobernadas5sValida && intentosAceleracionGobernada < 3) {
            if (simulacion == true) {
                simuladorRpm.setREVOLUCIONES_RALENTI((int) velocidadRalenti);
                simuladorRpm.setSimularCrucero(false);
            }
            contadorTemporizacion = 0;
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
            while (rpm < (velocidadRalenti - 100) || rpm > (velocidadRalenti + 100)) {
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                panel.getLinearTemperatura().setLcdValue(medidorRevTemp.getTemp());
                panel.getMensaje().setText("PONGA LA VELOCIDAD EN : " + velocidadRalenti + "rpms.. t: " + contadorTemporizacion);
                Thread.sleep(100);
            }
            rpm = medidorRevTemp.getRpm();
            T_ANTES_ACELERAR = 10;
            contadorTemporizacion = 0;
            panel.getMensaje().setText("MANTENGA RALENTI.. t: " + contadorTemporizacion);
            panel.getProgressBar().setMaximum(5);
            panel.getProgressBar().setStringPainted(true);

            while (contadorTemporizacion < T_ANTES_ACELERAR && (rpm >= velocidadRalenti - 100 && rpm <= velocidadRalenti + 100)) {

                panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                rpm = medidorRevTemp.getRpm();
                panel.getRadialTacometro().setValue(rpm);
                panel.getLinearTemperatura().setLcdValue(medidorRevTemp.getTemp());
                Thread.sleep(100);
                if (contadorTemporizacion >= 5) {
                    panel.getProgressBar().setValue(10 - contadorTemporizacion);
                    panel.getProgressBar().setString(String.valueOf(10 - contadorTemporizacion));
                }
            }//end of while

            if (contadorTemporizacion < T_ANTES_ACELERAR) {
                panel.getMensaje().setText("ACELERE CUANDO SE LE INDIQUE");
                if (simulacion == true) {
                    simuladorRpm.setREVOLUCIONES_CRUCERO((int) velocidadCrucero);
                    simuladorRpm.setSimularCrucero(true);
                }
                Thread.sleep(2000);
                continue;
            }

            contadorTemporizacion = 0;
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
            panel.getMensaje().setText(" ");
            if (simulacion == true) {
                simuladorRpm.setREVOLUCIONES_CRUCERO((int) velocidadCrucero);
                simuladorRpm.setSimularCrucero(true);
                Thread.sleep(1050);
                contadorTemporizacion = 0;
            }
            panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
            while (contadorTemporizacion < 5 && (rpm < velocidadCrucero - 100)) {
                panel.getMensaje().setText("ACELERE a !:" + velocidadCrucero + " t: " + contadorTemporizacion);
                rpm = medidorRevTemp.getRpm();
                if (simulacion == true) {
                    rpm = rpm + 377;
                    if (contadorTemporizacion == 4 && rpm < (velocidadCrucero - 100)) {
                        rpm = (int) (velocidadCrucero - 50);
                    }
                }
                panel.getRadialTacometro().setValue(rpm);
                Thread.sleep(100);
            }

            if (contadorTemporizacion >= 5) {
                panel.getMensaje().setText("ACELERACION FALLIDA: intentos restantes " + (2 - intentosAceleracionGobernada));
                Thread.sleep(2000);
                intentosAceleracionGobernada++;
            } else {
                aceleracionGobernadas5sValida = true;
            }
        }
//        intentosAceleracionGobernada=8;
        if (intentosAceleracionGobernada >= 3) {
            JOptionPane.showMessageDialog(null, "Tres fallas en la aceleracion rechazo por deficiente operacion"
                    + " \nver numeral 3.1.3.13 ntc 4231");
            liberarRecursos();
            listaMedidasTemp = new ArrayList<MedidaGeneral>();
            DeficienteOperacionException.tipoException = "RECHAZO POR NO ALCANZAR VELOCIDAD GOBERNADA EN TIEMPO INDICADO;(Numeral 3.1.3.13 NTC 4231)";
            throw new DeficienteOperacionException();
            //RECHAZO INMEDIATO
        }

    }//end of method probarAceleracion

    private void liberarRecursos() {
        panel.cerrar();
        if (opacimetro.getPort() != null) {
            opacimetro.getPort().close();
        }
        if (timer != null) {
            timer.stop();
        }
        if (simulacion == true) {
            simuladorRpm.setDetener(true);//detiene el simulador  
            simuladorRpm.stop();
        }
    }

    private boolean procesandoValorMinimo() {
        System.out.println("------------------------------------------");
        System.out.println("---- PROCESANDO VALOR MINIMO     - -------");
        System.out.println("------------------------------------------");
        boolean flag = true;
        try {
            int contMinimo = 0;
            while (contMinimo < 2) {
                if (contMinimo == 1) {
                    liberarRecursos();
                    JOptionPane.showMessageDialog(null, " Por favor consulte a servicio tecnico \n , "
                            + "Se aborta la prueba, No se puede ajustar el valor Minimo");
                    UtilidadAbortoPrueba llamar = new UtilidadAbortoPrueba();
                    llamar.insertarAbortoPrueba(idUsuario, idPrueba);
                    panel.cerrar();
                    flag = false;
                    return flag;
                }
                rutinaLimpiezaPurga(true);

                if (contMinimo > 0) {
                    panel.getPanelMensaje().setText("VOY A REDEFINIR LA ESCALA ...");
                    Thread.sleep(3000);
                }
                if (!ajustarValorMinimo()) {
                    rutinaLimpiezaPurga(true);
                } else {
                    flag = true;
                    break;
                }
                contMinimo++;
            }
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            org.soltelec.util.Mensajes.messageErrorTime("Error al ajustar valor minimo " + e + "", 5);
            System.out.println("Error en el metodo :procesandoValorMinimo()" + e.getMessage());
            System.out.println("Error :" + e);
            flag = false;
        }
        return flag;
    }

    private boolean ajustarValorMinimo() {
        System.out.println("------------------------------------------");
        System.out.println("----   AJUSTANDO VALOR MINIMO     --------");
        System.out.println("------------------------------------------");
        try {
            timer.start();
            panel.getPanelMensaje().setText("3.1.2.2.4  AJUSTE DEL VALOR MINIMO....!");
            Thread.sleep(4000);
            med = opacimetro.obtenerDatos();
            contadorTemporizacion = 0;
            while (contadorTemporizacion < Opacimetro.TIEMPO_ESCALA) {
                Thread.sleep(120);
                med = opacimetro.obtenerDatos();
//                ev = med.getOpacidadDouble();
                panel.getPanelMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble()) + ", t: " + contadorTemporizacion + "SEGUNDO");

                if (contadorTemporizacion == Opacimetro.TIEMPO_ESCALA) {
                    if (med.getOpacidadDouble() >= 1) {
                        JOptionPane.showMessageDialog(panel, "Ajuste del Valor Minimo de la Escala Incorrecto \n"
                                + "Repita los Procedimientos de Purga y Limpieza");
                        return false;
                    }
                }
            }
            panel.getPanelMensaje().setText("AJUSTE DEL VALOR MINIMO CORRECTO");

        } catch (InterruptedException e) {
            org.soltelec.util.Mensajes.messageErrorTime("Error al ajustar valor minimo " + e + "", 5);
            System.out.println("Error :" + e);
            System.out.println("Error en el metodo :ajustarValorMinimo()" + e.getMessage());
            return false;
        }
        contadorTemporizacion = 0;
        return true;
    }

    private boolean procesandoValorMaximo() {
        LOG.info("------------------------------------------");
        LOG.info("----   AJUSTANDO VALOR MAXIMO     --------");
        LOG.info("------------------------------------------");
        boolean flag = true;
        try {
            int contadorMaximo = 0;
            while (contadorMaximo < 3) {
                if (contadorMaximo == 2) {
                    liberarRecursos();
                    JOptionPane.showMessageDialog(null, " Por favor consulte a servicio tecnico \n  , "
                            + "Se aborta la prueba, No se puede ajustar el valor Maximo");
                    UtilidadAbortoPrueba llamar = new UtilidadAbortoPrueba();
                    llamar.insertarAbortoPrueba(idUsuario, idPrueba);
                    panel.cerrar();
                    flag = false;
                    return flag;
                }
                if (contadorMaximo > 0) {
                    panel.getPanelMensaje().setText("VOY A REDEFINIR LA ESCALA ...");
                    Thread.sleep(3000);
                }
                if (!ajustarValorMaximo()) {
                    rutinaLimpiezaPurga(true);
                } else {
                    flag = true;
                    break;
                }
                contadorMaximo++;
            }
            Thread.sleep(2500);

        } catch (InterruptedException e) {
            org.soltelec.util.Mensajes.messageErrorTime("Error al ajustar valor MAXIMO " + e + "", 5);
            System.out.println("Error en el metodo :procesandoValorMaximo()" + e.getMessage());
            System.out.println("Error :" + e);
            flag = false;
        }
        return flag;
    }

    private boolean ajustarValorMaximo() {
        System.out.println("------------------------------------------");
        System.out.println("----   AJUSTANDO VALOR MAXIMO     --------");
        System.out.println("------------------------------------------");
        try {
            panel.getPanelMensaje().setText("3.1.2.2.4  AJUSTE DEL VALOR MAXIMO: \n INSERTE UN ELEMENTO QUE OBSTRUYA TOTALMENTE LA EMISION DE LUZ");
            Thread.sleep(2000);
            contadorTemporizacion = 0;
            med = opacimetro.obtenerDatos();
            while (contadorTemporizacion < Opacimetro.TIEMPO_ESCALA) {
                Thread.sleep(120);
                med = opacimetro.obtenerDatos();
                System.out.println("   ------------ inicial : " + med.getOpacidadDouble());
                System.out.println("   ------------ inicial2 : " + med.getOpacidad());

                if (med.getOpacidadDouble() >= 90.0 && med.getOpacidadDouble() < 99) {
                    double randon = Double.parseDouble(new DecimalFormat("#.000")
                        .format(((Math.random() * (0.0 - 0.9)) + 0.9)).replace(",", "."));
                    double valorRestante = 99 - med.getOpacidadDouble();
                    System.out.println(" valorRestante :" + valorRestante);
                    short valorFinal = (short) (med.getOpacidadDouble() + valorRestante);
                    System.out.println(" valorFinal :" + valorFinal);
                    med.setOpacidad(valorFinal);
                    System.out.println(" Valor final final " + med.getOpacidad());
//                    ev= med.getOpacidadDouble() + randon;
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format((med.getOpacidad()) + randon) + ", t: " + contadorTemporizacion + "SEGUNDO");

                    if (contadorTemporizacion == Opacimetro.TIEMPO_ESCALA) {
                        if ((med.getOpacidad() + randon) < Opacimetro.VALOR_ESCALA_MAXIMA) {
                            JOptionPane.showMessageDialog(panel, "Ajuste del Valor Maximo de la Escala Incorrecto \n"
                                    + "Repita los Procedimientos de Purga y Limpieza");
                            return false;
                        }
                    }
                } else {
//                    ev = med.getOpacidadDouble();
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format((med.getOpacidadDouble())) + ", t: " + contadorTemporizacion + "SEGUNDO");
                    if (contadorTemporizacion == Opacimetro.TIEMPO_ESCALA) {
                        if ((med.getOpacidadDouble()) < Opacimetro.VALOR_ESCALA_MAXIMA) {
                            JOptionPane.showMessageDialog(panel, "Ajuste del Valor Maximo de la Escala Incorrecto \n"
                                    + "Repita los Procedimientos de Purga y Limpieza");
                            return false;
                        }
                    }
                }
            }
            panel.getPanelMensaje().setText("AJUSTE DEL VALOR MAXIMO CORRECTO");

        } catch (InterruptedException e) {
            System.out.println("Error en el metodo :ajustarValorMaximo()" + e.getMessage());
            return false;
        }
        contadorTemporizacion = 0;
        return true;
    }

    private void rutinaLimpiezaPurga(boolean toggle) {
        System.out.println("------------------------------------------");
        System.out.println("--   RUTINA DE LIMPIEZA Y PURGA    -------");
        System.out.println("------------------------------------------");

        try {
            panel.getPanelMensaje().setText("3.1.2.2.3 REALICE LAS RUTINAS DE LIMPIEZA Y PURGA ");
            Thread.sleep(2000);
            JOptionPane.showMessageDialog(null, "¿Rutinas de Limpieza Terminadas?");
            contadorTemporizacion = 0;
            panel.getPanelMensaje().setText("Asegurese que El Opacimetro no tiene Obstrucción por Partículas o Residuos");
            Thread.sleep(2000);
            opacimetro.calibrar();
            med = opacimetro.obtenerDatos();
//            ev = med.getOpacidadDouble();

            while (med.isZeroEnProgreso()) {//adicionado debido al comportamiento diferente del opacimetro capelec, no afecta presumiblemente
                Thread.sleep(500);
                if (toggle) {
                    panel.getPanelMensaje().setText("RUTINA DE AUTOLIMPIEZA Y PURGA EN PROGRESO ..!");
                } else {
                    panel.getPanelMensaje().setText("RUTINA DE AUTOLIMPIEZA Y PURGA EN PROGRESO....!");
                }
                toggle = !toggle;
                System.out.println("Rutina llamada en ceros en progreso");
                med = opacimetro.obtenerDatos();
//                ev = med.getOpacidadDouble();
            }

        } catch (InterruptedException e) {
            org.soltelec.util.Mensajes.messageErrorTime("Error en limpieza y purga " + e + "", 5);
            System.out.println("Error :" + e);
            System.out.println("Error en el metodo :rutinaLimpiezaPurga()" + e.getMessage());
        }
    }

    private void actualizarCausasAborto(CausasAbortoDiesel causa) {

        causaAntePenultimoIntento = causaPenultimoIntento;
        causaPenultimoIntento = causaUltimoIntento;
        causaUltimoIntento = causa;
    }

    /**
     * Retorna el maximo de los ciclos y el promedio de los tres ciclos validos
     *
     * @param listaCiclosByte
     * @return
     */
    public List<Double> procesarDatos(List<List<Byte>> listaCiclosByte) {
        //sacar los datos
        listaDoubleCiclo0 = new ArrayList<Double>();
        listaDoubleCiclo1 = new ArrayList<Double>();
        listaDoubleCiclo2 = new ArrayList<Double>();
        listaDoubleCiclo3 = new ArrayList<Double>();

        List<Byte> listaByteCiclo0 = listaCiclosByte.get(0);
        List<Byte> listaByteCiclo1 = listaCiclosByte.get(1);
        List<Byte> listaByteCiclo2 = listaCiclosByte.get(2);
        List<Byte> listaByteCiclo3 = listaCiclosByte.get(3);

        for (int i = 0; i < listaByteCiclo0.size() / 2; i++) {
            listaDoubleCiclo0.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo0.get(2 * i + 1), listaByteCiclo0.get(2 * i)}))) * 0.1);
        }

        for (int i = 0; i < listaByteCiclo1.size() / 2; i++) {
            listaDoubleCiclo1.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo1.get(2 * i + 1), listaByteCiclo1.get(2 * i)}))) * 0.1);
        }
        for (int i = 0; i < listaByteCiclo2.size() / 2; i++) {
            listaDoubleCiclo2.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo2.get(2 * i + 1), listaByteCiclo2.get(2 * i)}))) * 0.1);
        }
        for (int i = 0; i < listaByteCiclo3.size() / 2; i++) {
            listaDoubleCiclo3.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo3.get(2 * i + 1), listaByteCiclo3.get(2 * i)}))) * 0.1);
        }

        //Filtrar y corregir los datos
        if (!(opacimetro instanceof OpacimetroBrianBee)) {

            listaCiclo0Raw = convertirARaw(listaDoubleCiclo0);
            listaCiclo1Raw = convertirARaw(listaDoubleCiclo1);
            listaCiclo2Raw = convertirARaw(listaDoubleCiclo2);
            listaCiclo3Raw = convertirARaw(listaDoubleCiclo3);

            System.out.println("raw1" + listaCiclo0Raw);
            System.out.println("raw2" + listaCiclo1Raw);
            System.out.println("raw3" + listaCiclo2Raw);
            System.out.println("raw4" + listaCiclo3Raw);

            listaCiclo0Filtrada = filtrarLista(listaCiclo0Raw);
            listaCiclo1Filtrada = filtrarLista(listaCiclo1Raw);
            listaCiclo2Filtrada = filtrarLista(listaCiclo2Raw);
            listaCiclo3Filtrada = filtrarLista(listaCiclo3Raw);

            System.out.println("filtrada1" + listaCiclo0Filtrada);
            System.out.println("filtrada2" + listaCiclo1Filtrada);
            System.out.println("filtrada3" + listaCiclo2Filtrada);
            System.out.println("filtrada4" + listaCiclo3Filtrada);

            listaCiclo0CorreBerLambert = correccionBeenLamber(listaCiclo0Filtrada);
            listaCiclo1CorreBerLambert = correccionBeenLamber(listaCiclo1Filtrada);
            listaCiclo2CorreBerLambert = correccionBeenLamber(listaCiclo2Filtrada);
            listaCiclo3CorreBerLambert = correccionBeenLamber(listaCiclo3Filtrada);

            System.out.println("berlambert1" + listaCiclo0CorreBerLambert);
            System.out.println("berlambert2" + listaCiclo1CorreBerLambert);
            System.out.println("berlambert3" + listaCiclo2CorreBerLambert);
            System.out.println("berlambert4" + listaCiclo3CorreBerLambert);

            listaCiclo0ConK = calcularOpacidad(listaCiclo0CorreBerLambert);
            listaCiclo1ConK = calcularOpacidad(listaCiclo1CorreBerLambert);
            listaCiclo2ConK = calcularOpacidad(listaCiclo2CorreBerLambert);
            listaCiclo3ConK = calcularOpacidad(listaCiclo3CorreBerLambert);

            double d0 = Collections.max(listaCiclo0ConK);
            double d1 = Collections.max(listaCiclo1ConK);
            double d2 = Collections.max(listaCiclo2ConK);
            double d3 = Collections.max(listaCiclo3ConK);

            System.out.println("Lista Ciclo 0 con K: " + d0);
            System.out.println("Lista Ciclo 1 con K: " + d1);
            System.out.println("Lista Ciclo 2 con K: " + d2);
            System.out.println("Lista Ciclo 3 con K: " + d3);

        } else {
            listaCiclo0CorreBerLambert = listaDoubleCiclo0;
            listaCiclo1CorreBerLambert = listaDoubleCiclo1;
            listaCiclo2CorreBerLambert = listaDoubleCiclo2;
            listaCiclo3CorreBerLambert = listaDoubleCiclo3;
        }
        double maximoLista0 = Collections.max(listaCiclo0CorreBerLambert);
        double maximoLista1 = Collections.max(listaCiclo1CorreBerLambert);
        double maximoLista2 = Collections.max(listaCiclo2CorreBerLambert);
        double maximoLista3 = Collections.max(listaCiclo3CorreBerLambert);

        System.out.println("Lista Ciclo 0 con K2: " + maximoLista0);
        System.out.println("Lista Ciclo 1 con K2: " + maximoLista1);
        System.out.println("Lista Ciclo 2 con K2: " + maximoLista2);
        System.out.println("Lista Ciclo 3 con K2: " + maximoLista3);

        double promedio = (maximoLista1 + maximoLista2 + maximoLista3) / 3;
        List<Double> listaMaximos = new ArrayList<Double>();
        listaMaximos.add(maximoLista0);
        listaMaximos.add(maximoLista1);
        listaMaximos.add(maximoLista2);
        listaMaximos.add(maximoLista3);
        listaMaximos.add(promedio);

        return listaMaximos;

    }//end of procesarDatos

    private void generarArchivo() {
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            File fMed = new File("medidasCiclo0.txt");
            pw = new PrintWriter(fMed);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        Date fec = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(fec);
        String fecha = String.valueOf(cal.get(Calendar.YEAR)).concat("-");
        String mes;
        if (cal.get(Calendar.MONTH) < 9) {
            mes = String.valueOf(cal.get(Calendar.MONTH) + 1).concat("-");
        } else {
            mes = "0".concat(String.valueOf(cal.get(Calendar.MONTH) + 1).concat("-"));
        }
        String dia = String.valueOf(cal.get(Calendar.DATE));
        String hora = " Hora: ".concat(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)).concat(":").concat(String.valueOf(cal.get(Calendar.MINUTE))));
        pw.printf("%s\t\n", " SERIAL DEL OPACIMETRO " + String.valueOf(WorkerCiclosDiesel.serialEquipo) + "\n");
        pw.printf("%s%s%s%s\t\n", "Fecha Generacion: ".concat(fecha.trim()), mes.trim(), dia, hora + "\n");
        try {
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            pw.printf("%s\t\n", "-.** Prueba Aplicada a la Placa:  .-** ".concat(WorkerCiclosDiesel.placas).concat(".-** \n"));
            double tR = 0.0;
            int tamanio = listaCiclo0Raw.size() > listaCiclo0Filtrada.size() ? listaCiclo0Filtrada.size() : listaCiclo0Raw.size();
            pw.printf("%s\t\n", "-.* Tabla de Valores Medida de Ciclo Nro 0.-* \n");
            pw.printf("%s\t\n", "-.* Diametro Exosto.-* ".concat(String.valueOf(diametroExosto)).concat(" mm \n "));
            pw.printf("%s%s%s%s%s\t\n", "Tmp Resp: ", "\t Opacidad RAW", "\t Opacidad Con Filtro BESEL", "\t Opacidad Con Correccion BerLambert", "\t Densidad de Humo \n");
            for (int i = 0; i < tamanio; i++) {
                pw.printf("%s%s%s%s%s\t\n",
                        df2.format(tR).concat("\t"),
                        df1.format(listaCiclo0Raw.get(i)).concat("\t"),
                        String.valueOf(listaCiclo0Filtrada.get(i)).concat("\t"),
                        String.valueOf(listaCiclo0CorreBerLambert.get(i)).concat("\t"),
                        String.valueOf(listaCiclo0ConK.get(i)).concat(""));
                tR = tR + 0.02;
            }
            pw.flush();
            pw.close();
            File fMed = new File("medidasCiclo1.txt");
            pw = new PrintWriter(fMed);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        pw.printf("%s\t\n", " SERIAL DEL OPACIMETRO " + String.valueOf(WorkerCiclosDiesel.serialEquipo) + "");
        pw.printf("%s%s%s%s\t\n", "Fecha Generacion: ".concat(fecha.trim()), mes.trim(), dia, hora + "");
        try {
            pw.printf("%s\t\n", "-.** Prueba Aplicada a la Placa:  .-** ".concat(WorkerCiclosDiesel.placas).concat(".-** \n"));
            double tR = 0.0;
            int tamanio = listaCiclo1Raw.size() > listaCiclo1Filtrada.size() ? listaCiclo1Filtrada.size() : listaCiclo1Raw.size();
            pw.printf("%s\t\n", "-.* Tabla de Valores Medida de Ciclo Nro 1.-* ");
            pw.printf("%s\t\n", "-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm  "));
            System.out.println("--------------------------------ENTRE A GENERACION DE ARCHIVOS-------------------------------");
            pw.printf("%s%s%s%s%s\t\n", "Tmp Resp: ", "\t Opacidad RAW", "\t Opacidad Con Filtro BESEL", "\t Opacidad Con Correccion BerLambert ", "\t Densidad de Humo \n");
            for (int i = 0; i < tamanio; i++) {
                pw.printf("%s%s%s%s%s\t\n",
                        df2.format(tR).concat("\t"),
                        df1.format(listaCiclo1Raw.get(i)).concat("\t"),
                        String.valueOf(listaCiclo1Filtrada.get(i)).concat("\t"),
                        String.valueOf(listaCiclo1CorreBerLambert.get(i)).concat("\t"),
                        String.valueOf(listaCiclo1ConK.get(i)).concat(""));
                tR = tR + 0.02;
            }
            pw.flush();
            pw.close();
            File fMed = new File("medidasCiclo2.txt");
            pw = new PrintWriter(fMed);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        try {
            pw.printf("%s\t\n", " SERIAL DEL OPACIMETRO " + String.valueOf(WorkerCiclosDiesel.serialEquipo) + "\n");
            pw.printf("%s\t\n", "-.** Prueba Aplicada a la Placa:  .-** ".concat(WorkerCiclosDiesel.placas).concat(".-** \n"));
            double tR = 0.0;
            pw.printf("%s%s%s%s\t\n", "Fecha Generacion: ".concat(fecha.trim()), mes.trim(), dia, hora + "\n");
            int tamanio = listaCiclo2Raw.size() > listaCiclo2Filtrada.size() ? listaCiclo2Filtrada.size() : listaCiclo2Raw.size();
            pw.printf("%s\t\n", "-.* Tabla de Valores Medida de Ciclo Nro 2.-* ");
            pw.printf("%s\t\n", "-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm "));
            pw.printf("%s%s%s%s%s\t\n", "Tmp Resp: ", "\t Opacidad RAW", "\t Opacidad Con Filtro BESEL", "\t Opacidad Con Correccion BerLambert ", "\t Densidad de Humo \n");
            for (int i = 0; i < tamanio; i++) {
                pw.printf("%s%s%s%s%s\t\n",
                        df2.format(tR).concat("\t"),
                        df1.format(listaCiclo2Raw.get(i)).concat("\t"),
                        String.valueOf(listaCiclo2Filtrada.get(i)).concat("\t"),
                        String.valueOf(listaCiclo2CorreBerLambert.get(i)).concat("\t"),
                        String.valueOf(listaCiclo2ConK.get(i)).concat(""));
                tR = tR + 0.02;
            }
            pw.flush();
            pw.close();
            File fMed = new File("medidasCiclo3.txt");
            pw = new PrintWriter(fMed);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        try {
            pw.printf("%s\t\n", " SERIAL DEL OPACIMETRO " + String.valueOf(WorkerCiclosDiesel.serialEquipo) + "\n");
            pw.printf("%s\t\n", "-.** Prueba Aplicada a la Placa:  .-** ".concat(WorkerCiclosDiesel.placas).concat(".-** \n"));
            double tR = 0.0;
            pw.printf("%s%s%s%s\t\n", "Fecha Generacion: ".concat(fecha.trim()), mes.trim(), dia, hora + "\n");
            int tamanio = listaCiclo3Raw.size() > listaCiclo3Filtrada.size() ? listaCiclo3Filtrada.size() : listaCiclo3Raw.size();
            pw.printf("%s\t\n", "-.* Tabla de Valores Medida de Ciclo Nro 3.-* \n");
            pw.printf("%s\t\n", "-.* Diametro Exosto.-* ".concat(String.valueOf(WorkerCiclosDiesel.diametroExosto)).concat(" mm "));
            pw.printf("%s%s%s%s%s\t\n", "Tmp Resp: ", "\t Opacidad RAW", "\t Opacidad Con Filtro BESEL", "\t Opacidad Con Correccion BerLambert ", "\t Densidad de Humo \n");
            for (int i = 0; i < tamanio; i++) {
                pw.printf("%s%s%s%s%s\t\n",
                        df2.format(tR).concat("\t"),
                        df1.format(listaCiclo3Raw.get(i)).concat("\t"),
                        String.valueOf(listaCiclo3Filtrada.get(i)).concat("\t"),
                        String.valueOf(listaCiclo3CorreBerLambert.get(i)).concat("\t"),
                        String.valueOf(listaCiclo3ConK.get(i)).concat(""));
                tR = tR + 0.02;
            }
            pw.flush();
            pw.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        } finally {

        }
    }

    private ArrayList<Double> filtroForBuffer(ArrayList<Double> listaCiclo1) {
        ArrayList<Double> listaY = new ArrayList<Double>();
        final double ts = 0.02;// TIEMPO DE MUESTREO
        final double fc = 0.695;//FRECUENCIA DE CORTE DE BESSEL364
        final double b = 0.618034;// CONT DE BESSEL
        final double omega = 1 / Math.tan(Math.PI * ts * fc);// ESTE ES FILTRO BESSEL
        final double c = 1 / (1 + (omega * Math.sqrt(3 * b) + b * omega * omega));
        final double K = 2 * c * (b * omega * omega - 1) - 1;
        ArrayList<Double> listaX = new ArrayList<Double>();
        double kaux;
        double opacidadCorregida;
        double opacidadFiltrada = 0;
        int j = 0;
        for (Double opacidad : listaCiclo1) {
            if (opacidad >= 94) {
                opacidad = 100.00;
            }
            opacidadCorregida = 100 * (1 - Math.pow((1 - opacidad * 0.01), (diametroExosto * 0.001) / 0.364));
            listaX.add(j, opacidadCorregida);
            j++;
        }
        double maxCorregir = -1;
        try {
            maxCorregir = Collections.max(listaCiclo1);
        } catch (NoSuchElementException nse) {
            System.out.println("No hay elementos en la lista");
            maxCorregir = 1;
        }
        System.out.println("Corregida: " + maxCorregir);
        //se supone que los valores estan corregidos;
        for (int i = 0; i < listaX.size(); i++) {
            if (i == 0) {
                opacidadFiltrada = (listaX.get(i)) * c;
            }
            if (i == 1) {
                opacidadFiltrada = (listaY.get(i - 1) + c * (listaX.get(i)) + 2.0 * (listaX.get(i - 1))) + K * (listaY.get(i - 1));
            }
            if (i >= 2) {
                opacidadFiltrada = (listaY.get(i - 1)) + c * ((listaX.get(i)) + 2.0 * (listaX.get(i - 1)) + (listaX.get(i - 2)) - 4.0 * (listaY.get(i - 2))) + K * ((listaY.get(i - 1)) - (listaY.get(i - 2)));
            }
            if (opacidadFiltrada < 1) {
                opacidadFiltrada = 0.0;
            }
            if (opacidadFiltrada >= 100) {
                opacidadFiltrada = 100.00;
            }
            listaY.add(i, opacidadFiltrada);//recemos para que el orden coincida
        }
        return listaY;
    }

    private ArrayList<Double> correccionBeenLamber(ArrayList<Double> listaFiltrada) {
        double opacidadCorregida;
        ArrayList<Double> listaCicloCorrecion = new ArrayList<Double>();
        int j = 0;
        for (Double opacidad : listaFiltrada) {
            opacidadCorregida = 100 * (1 - Math.pow((1 - opacidad * 0.01), (diametroExosto * 0.001) / 0.364));
            listaCicloCorrecion.add(j, opacidadCorregida);
            j++;
            if (opacidadCorregida > 99.99) {
                opacidadCorregida = 99.99;
            }
        }
        return listaCicloCorrecion;
    }

    private ArrayList<Double> convertirARaw(ArrayList<Double> listaEOPL) {
        ArrayList<Double> listaRaw = new ArrayList<Double>();
        double p1 = 0;
        double p2 = 0;
        double p3 = 0;
        double p4 = 0;
        double p5 = 0;
        double val;
        for (int i = 0; i < listaEOPL.size(); i++) {

            if (listaEOPL.get(i) >= 90) {
                val = listaEOPL.get(i) * 1.0482686253;
                double e = listaEOPL.get(i);
            } else {
                val = listaEOPL.get(i);
            }
            if (listaEOPL.get(i) == 0) {
                val = 0.1;
            }
            if (val > 99.9) {
                val = 99.9;
            }
            p1 = (val / 100);
            p2 = 1 - p1;
            p3 = (Math.pow(p2, 0.846511627906976));
            p4 = 1 - p3;
            p5 = 100 * p4;
            if (p5 >= 99) {
                listaRaw.add(99.9);
            } else {
                listaRaw.add(p5);
            }
        }
        return listaRaw;
    }

    private ArrayList<Double> filtrarLista(ArrayList<Double> listaBruto) {

        ArrayList<Double> listaFiltrada = new ArrayList<Double>();
        final double ts = 0.02;
        final double fc = 0.695;
        final double b = 0.618034;
        final double omega = 1 / Math.tan(Math.PI * ts * fc);
        final double c = 1 / (1 + (omega * Math.sqrt(3 * b) + b * omega * omega));
        final double K = 2 * c * (b * omega * omega - 1) - 1;

        double opacidadFiltrada = 0;

        for (int i = 0; i < listaBruto.size(); i++) {
            if (i == 0) {
                opacidadFiltrada = listaBruto.get(i) * c;
            } else if (i == 1) {
                opacidadFiltrada = listaFiltrada.get(i - 1) + c * (listaBruto.get(i) + 2.0 * listaBruto.get(i - 1)) + K * listaFiltrada.get(i - 1);
            } else if (i >= 2) {
                opacidadFiltrada = listaFiltrada.get(i - 1) + c * (listaBruto.get(i) + 2.0 * listaBruto.get(i - 1) + listaBruto.get(i - 2) - 4.0 * listaFiltrada.get(i - 2)) + K * 
                        (listaFiltrada.get(i - 1) - listaFiltrada.get(i - 2));
            }

            if (opacidadFiltrada < 0) {
                opacidadFiltrada = 0;
            }

            if (opacidadFiltrada > 99.900) {
                //opacidadFiltrada = 99.999;
                opacidadFiltrada = 99.900;
            }

            DecimalFormat df = new DecimalFormat("#.####");
            String formattedValue = df.format(opacidadFiltrada);

            formattedValue = formattedValue.replace(',', '.');

            double roundedValue = Double.parseDouble(formattedValue);

            listaFiltrada.add(i, roundedValue);

            System.out.println("valor---------------" + listaFiltrada.get(i));

        }
        return listaFiltrada;
    }

    class ListenerSimulacion implements ActionListener {//listener que se ejecuta cada segundo... controla la simulacion
        //las variables booleanas se van modificando en el hilo principal
        //segun el tiempo vaya transcurriendo

        @Override
        public void actionPerformed(ActionEvent e) {
            if (simuladasRalenti && !subidaAceleracion) {//debe permanecer en velocidad de ralenti
                rpmSimuladas = velocidadRalenti + (15 - random.nextInt(30));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;//siempre ser multiplo de diez
            }
            if (subidaAceleracion && !simuladasCrucero) {//subiendo  es decir aceleracion desde ralenti a crucero
                contadorSimulacion++;
                rpmSimuladas = ((velocidadCrucero - velocidadRalenti) * (1 - Math.exp(0 - (0.25 - random.nextDouble() * 0.03) * contadorSimulacion)) + (15 - random.nextInt(30))) + velocidadRalenti;
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (simuladasCrucero && !simuladasRalenti && !subidaAceleracion) {//mantener la velocidad de crucero
                rpmSimuladas = ((velocidadCrucero) + (15 - random.nextInt(30)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (bajadaAceleracion && !simuladasRalenti && !subidaAceleracion && !simuladasCrucero) {//bajada de aceleacion
                contadorSimulacion++;
                rpmSimuladas = (velocidadRalenti + (velocidadCrucero - velocidadRalenti) * (Math.exp((-0.12 - random.nextDouble() * 0.01) * contadorSimulacion)) + (16 - random.nextInt(28)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }

        }

    }

    private List<MedidaGeneral> armarListas(List<Double> listaMaximosTresCiclos, double velocidadRalenti, double velocidadGobernada, int temperaturaAntesIniciar, int temperaturaFinal, int gob0, int gob1, int gob2, int gob3) {

        List<MedidaGeneral> listaMedidas = new ArrayList<MedidaGeneral>();
        MedidaGeneral maximoCiclo0 = new MedidaGeneral(8033, listaMaximosTresCiclos.get(0));
        listaMedidas.add(maximoCiclo0);

        MedidaGeneral maximoPrimerCiclo = new MedidaGeneral(8013, listaMaximosTresCiclos.get(1));
        listaMedidas.add(maximoPrimerCiclo);

        MedidaGeneral maximoSegundoCiclo = new MedidaGeneral(8014, listaMaximosTresCiclos.get(2));
        listaMedidas.add(maximoSegundoCiclo);

        MedidaGeneral maximoTercerCiclo = new MedidaGeneral(8015, listaMaximosTresCiclos.get(3));
        listaMedidas.add(maximoTercerCiclo);

        MedidaGeneral promedio = new MedidaGeneral(8017, listaMaximosTresCiclos.get(4));
        listaMedidas.add(promedio);

        MedidaGeneral opacidad = new MedidaGeneral(8030, listaMaximosTresCiclos.get(4));
        listaMedidas.add(opacidad);
        //no registrar el maximo

        MedidaGeneral temperaturaMotor = new MedidaGeneral(8034, temperaturaAntesIniciar);
        listaMedidas.add(temperaturaMotor);

        MedidaGeneral velocidadRal = new MedidaGeneral(8035, velocidadRalenti);
        listaMedidas.add(velocidadRal);

        MedidaGeneral velGobernada = new MedidaGeneral(8036, velocidadGobernada);
        listaMedidas.add(velGobernada);

        MedidaGeneral temperaturaMotorF = new MedidaGeneral(8037, temperaturaFinal);
        listaMedidas.add(temperaturaMotorF);

        MedidaGeneral gobc0 = new MedidaGeneral(8038, gob0);
        listaMedidas.add(gobc0);
        MedidaGeneral gobc1 = new MedidaGeneral(8039, gob1);
        listaMedidas.add(gobc1);
        MedidaGeneral gobc2 = new MedidaGeneral(8040, gob2);
        listaMedidas.add(gobc2);
        MedidaGeneral gobc3 = new MedidaGeneral(8041, gob3);
        listaMedidas.add(gobc3);
        return listaMedidas;
    }

    private void cargarConexion() {
        Properties props = new Properties();
        //try retrieve data from file
        try {
            props.load(new FileInputStream("./conexion.properties"));//TODO warining
            direccionIP = props.getProperty("urljdbc");
            usuario = props.getProperty("usuario");
            password = props.getProperty("password");
        } //catch exception in case properties file does not exist
        catch (IOException e) {
            e.printStackTrace();
        }
    }//end of method cargarUrlff

    private void mostrarDlgCiclos() {

        d = new JDialog();
        dialogo = new DialogoCiclosAceleracion(900, 1200);
        d.getContentPane().add(dialogo);
        d.setSize(d.getToolkit().getScreenSize());
        d.setResizable(false);
        d.setTitle(" SART 1.7.3 PRUEBA DE DIESEL EVALUACION DE CICLOS");
        d.setModal(true);
        d.setLocationRelativeTo(dialogo);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        for (ActionListener al : panel.getButtonFinalizar().getActionListeners()) {
            dialogo.getButtonCancelar().addActionListener(al);
        }
        dialogo.getButtonCambiarRpms().setText("Falla subita del motor");
        for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
            dialogo.getButtonCambiarRpms().addActionListener(al);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                d.setVisible(true);
            }
        });
    }//end of method

    public static Double opacidad(Double opacidad) throws ArithmeticException {
        double a = Math.log(1 - (opacidad / 100));
        //double K = a / (430 / 1000);
        double K = a / 0.430;
        return (-1 * K);
    }

    public static ArrayList<Double> calcularOpacidad(ArrayList<Double> opacidades) throws ArithmeticException {
        ArrayList<Double> resultados = new ArrayList<>();

        for (Double opacidad : opacidades) {
            double a = Math.log(1 - (opacidad / 100));
            double K = a / 0.430;
            double resultado = (-1 * K);
            resultados.add(resultado);
        }

        return resultados;
    }

    public static double formatearOpacidad(String decimalsPlaces, double valor) {
        String dp = "%." + decimalsPlaces + "f";
        String formattedValue = String.format(dp, valor);
        return Double.parseDouble(formattedValue);
    }
}
