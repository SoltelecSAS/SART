/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import static conexion.PersistenceController.getEntityManager;
import org.soltelec.pruebasgases.diesel.PanelVerificacionDiesel;
import org.soltelec.procesosbanco.PanelCero;
import org.soltelec.procesosbanco.WorkerCalentamiento;
import org.soltelec.procesosbanco.BancoSensors;
import eu.hansolo.steelseries.tools.BackgroundColor;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.PortSerialUtil;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.eclipse.persistence.transaction.jotm.JotmTransactionController;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.test.Test;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;

/**
 * Clase que implementa el proceso de prueba para las motos
 *
 * @author Usuario
 */

/* Codigos de medidas para motos pd : las motos solamente hacen prueba de ralenti
 *8001 Hidrocarburos en ralenti
 * 8002 Monoxido de carbono en ralenti
 * 8003 Dioxido de carbono en ralenti
 * 8004 oxigeno en ralenti
 * 8005 revolucioens en ralenti
 * 8006 Temperatura en ralenti
 *
 *8018 Hidrocarburos en ralenti dos tiempos
 * 8019 CO2 ralenti para dos tiempos
 * 8020 CO ralenti para dos tiempos
 * 8021 O2 ralenti para dos tiempos
 * 8022 temperatura pra dos tiempos
 * 8028 revoluciones ralenti para dos tiempos
 * permisibles tabla permisibles
 * 10
 * 11
 * 12
 * 13
 * 14
 *
 */
public class HiloPruebaMotos implements Runnable, ActionListener {

    private PanelPruebaGases panel;
    private BancoSensors banco;
    private double[] arregloMedidas = new double[10];
    private double[] arregloFiltradas = new double[10];
    private double rpmAnterior = 0;
    private List<MedicionGases> listaRalentiUno, listaRalentiDos;
    private WorkerCalentamiento workerCalentamiento;//Hilo para el proceso de calentamiento
    private DialogoMotos dialogoMotos;
    private final int TIEMPO_ESPERA_HC_DESCIENDA = 270;
    private int contadorTemporizacion = 0;
    private int limiteHC;
    private Timer timer;
    private PanelVerificacionDiesel p;
    private int REVOLUCIONES_RALENTI = 1950;//esto se debe traer de la base de datos
    private MedicionGases medicion;
    private final static int T_ACEITE = 40;//TODO cambiar limite de temperatura de aceite para produccion
    private volatile Thread blinker;
    private double mediaHC, mediaCO, mediaCO2, mediaO2;
    private boolean simulacion;
    private Timer timerSimulacion;
    private Random random;
    private int contadorSimulacion;
    private double rpmsSimuladas;
    private int idPrueba = 6;//este campo se configura antes de iniciar el hilo
    private int idUsuario = 1;//este campo se configura antes de iniciar el hilo
    private int idHojaPrueba = 4;
    private String urljdbc = "jdbc:mysql://192.168.0.2/db_cda";
    private boolean cancelacion = false;
    private double mediaTemp;
    private double mediaRPM;
    private boolean cuatroTiempos = true;
    private int modelo;
    private int placa;
    private int tiemposMotor;
    private double permisibleCO;
    private double permisibleHC;
    private Date fecha_ingreso_v;

    public HiloPruebaMotos(PanelPruebaGases p) {
        listaRalentiUno = new ArrayList<MedicionGases>();
        listaRalentiDos = new ArrayList<MedicionGases>();
        dialogoMotos = new DialogoMotos();
        this.panel = p;
        panel.getButtonFinalizar().setVisible(false);
        panel.getButtonFinalizar().addActionListener(this);
        random = new Random();
        cargarUrl();
    }

    @Override
    public void run() {
        try {

            try {
                if (!cargarParametrosVehiculo()) {
                    JOptionPane.showMessageDialog(panel, "No es posible traer la info del vehiculo");
                    return;
                }
            } catch (ClassNotFoundException ce) {
                JOptionPane.showMessageDialog(panel, "Error no driver de Mysql");
                return;
            } catch (SQLException se) {
                JOptionPane.showMessageDialog(panel, "Error consultando la base de datos");
                se.printStackTrace();
                return;
            } catch (ParseException ex) {
                Logger.getLogger(HiloPruebaMotos.class.getName()).log(Level.SEVERE, null, ex);
            }

            panel.getPanelMensaje().setText("Buscando Banco");
            if (!buscarBanco()) {
                //si no encuentra el banco no sigue
                JOptionPane.showMessageDialog(null, "Banco no encontrado Terminacion anormal");
                panel.cerrar();
                return;
            } //end of method run
            medicion = banco.obtenerDatos(); //obtener una medicion para
            //espera hasta que el otor hilo termine si es que no sabes
            if (medicion.isCalentamiento()) {
                System.out.println("Banco esta en calentamiento");
                //Usando join no lo puedo creer
                panel.getPanelMensaje().setText("Banco En Calentamiento");
                panel.getPanelMensaje().getCronometro().setText("");
                workerCalentamiento = new WorkerCalentamiento(this.banco, this.panel.getProgressBar(), this.panel.getPanelMensaje().getCronometro());
                Thread t = new Thread(workerCalentamiento);
                t.start();
                try {
                    //Uniendo dos hilos
                    t.join(); //espera hasta que el otor hilo termine si es que no sabes
                } catch (InterruptedException ex) {
                    System.out.println("Error join con workerCalentamiento");
                }
            } //end of if calentamiento
            PanelCero panelCero = new PanelCero(); //PanelCero es una mezcla de GUI con hilo refactorizar
            panel.getPanelMensaje().setText("Haciendo Cero en el Banco ...");
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
            //pueden traer de la base de datos
            configurarPanelVerificacion();
            JOptionPane.showMessageDialog(null, p, "Verificacion", JOptionPane.PLAIN_MESSAGE);
            if (p.isDefectoEncontrado()) {
                System.out.println("Defecto " + p.getMensaje().toString());
                JOptionPane.showMessageDialog(panel, p.getMensaje().toString());
                try {
                    registrarDefectosVisuales();
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Error no se encuentra el driver de Mysql");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error insertando los datos en la bd");
                    ex.printStackTrace(System.err);
                } finally {
                    liberarRecursos();
                }
                return;
            }
            panel.getPanelMensaje().getCronometro().setText("");
            panel.getProgressBar().setVisible(false);
            blinker = Thread.currentThread();
            mostrarDialogoMotos(); //del dialogo se traen los parametros y la simulacion solamente una vez se puede indicar simulacion
            panel.getPanelMensaje().setText("Ponga la sonda de muestreo en contacto\n con el Ambiente");
            //Se supone que para limpiar el banco
            Thread.sleep(1500);
            if (dialogoMotos.getNumeroTiempos() == 4) {
                limiteHC = 60;//60 ppm
            } else {
                limiteHC = 500;
            }
            //espera un tiempo determinado a que los niveles  bajen... si no bajan sale automaticamente

            Thread.sleep(1500);
            medicion = banco.obtenerDatos();
            crearNuevoTimer();
            contadorTemporizacion = 0;
            timer.start();
            panel.getProgressBar().setMaximum(TIEMPO_ESPERA_HC_DESCIENDA);
            panel.getProgressBar().setValue(TIEMPO_ESPERA_HC_DESCIENDA);
            banco.encenderBombaMuestras(true);
            panel.getPanelMensaje().setText("Esperando a disminucion de HC");
            Thread.sleep(2000);
            medicion = banco.obtenerDatos();
            while (medicion.getValorHC() > limiteHC && contadorTemporizacion < TIEMPO_ESPERA_HC_DESCIENDA && blinker != null) {
                Thread.sleep(100);
                medicion = banco.obtenerDatos();
                panel.getProgressBar().setValue(TIEMPO_ESPERA_HC_DESCIENDA - contadorTemporizacion);
            } //end while

            banco.encenderBombaMuestras(false);

            panel.getProgressBar().setValue(0);
            timer.stop();
            if (contadorTemporizacion >= TIEMPO_ESPERA_HC_DESCIENDA) {
                //Si sale del while por tiempo la prueba se cancela
                panel.getPanelMensaje().setText("HC no desciende");

                JOptionPane.showMessageDialog(panel, "Prueba abortada por no descenso de HC");
                try {
                    registrarCancelacion("Prueba abortada por no descenso de HC");
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "No se encuentra el driver de MySQL");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error con la base de datos");
                    ex.printStackTrace(System.err);
                } finally {
                    liberarRecursos();
                }
                return;
            }//end if
            panel.getPanelMensaje().setText("Inicio de la Prueba");
            Thread.sleep(1200);
            //JOptionPane.showMessageDialog(panel, "Iniciar la Prueba, verifique acople o extension\n tubo de escape");
            //en este punto los niveles de hc deben estar por debajo de 20 ppm
            panel.getPanelMensaje().setText("Verifique : \n Transmision en neutro.\n" + "Luces encendidad y Accesorios desconectados\n" + "Ahogador / Control manual de choque apagado");
            Thread.sleep(2000);
            panel.getProgressBar().setVisible(true);
            panel.getProgressBar().setMaximum(30);
            panel.getButtonFinalizar().setVisible(true);
//            int opcion;
//            do {
//                opcion = JOptionPane.showOptionDialog(panel, "Vehiculo Preparado", "OK", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
//            } while (opcion == JOptionPane.NO_OPTION);
            //Verificar la temperatura aqui nos jodimos porque no tengo idea como hacerlo
            //TODO vehiculo scooter???

            simulacion = dialogoMotos.isSimulacion();
            int numTiempos = dialogoMotos.getNumeroTiempos();
            if (numTiempos == 4) {
                cuatroTiempos = true;
            } else {
                cuatroTiempos = false;
            }

            if (simulacion) {//TODO implementar la cuestion de la simulación en el dialogo de lso tiempos                
                panel.getPanelMensaje().setText("Temperatura de Aceite... ");
                Thread.sleep(2000);
                panel.getPanelMensaje().setText("Temperatura de Aceite OK ");
                Thread.sleep(2000);
                panel.getPanelMensaje().setText("Realice dos Aceleraciones rápidas \n" + "sostenidas no mayores las 2500 rpm");
                Thread.sleep(4000);
                panel.getPanelMensaje().setVisible(false);
                panel.getPanelFiguras().setVisible(true);
                //crea un timer para la simulacion de la velocidad
                timerSimulacion = new Timer(250, new ActionListener() {
                    int base = 1850 - random.nextInt(700);

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        contadorSimulacion++;

                        rpmsSimuladas = ((base) * (1 - Math.exp(0 - contadorSimulacion * 0.31))) + (0 - 25 - random.nextInt(25));
                        rpmsSimuladas = rpmsSimuladas - rpmsSimuladas % 10;
                    }//end of actionPerformed
                });
                //inicia la simulación para la prueba
                timerSimulacion.start();
                int repeticiones = 0;
                if (dialogoMotos.getNumeroEscapes() > 2)//numero de escapes mayor a 2
                {
                    repeticiones = 0;
                } else if (dialogoMotos.getNumeroEscapes() == 1) {
                    repeticiones = 1;
                } else if (dialogoMotos.getNumeroEscapes() == 2) {
                    repeticiones = 0;
                }
                panel.getLinearTemperatura().setValue(45 - random.nextInt(5));
                do {

                    if (repeticiones < 5) {
                        JOptionPane.showMessageDialog(panel, "Introduzca la sonda en el tubo de escape #" + (repeticiones));
                    } else {
                        JOptionPane.showMessageDialog(panel, "Introduzca la sonda en el tubo de escape" + (repeticiones + 1));
                    }

                    crearNuevoTimer();
                    timer.start();
                    contadorSimulacion = 0;
                    Thread.sleep(350);
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                    //(rpmFiltrada <= REVOLUCIONES_RALENTI - 450 || rpmFiltrada >= REVOLUCIONES_RALENTI - 50)
                    while ((rpmsSimuladas <= REVOLUCIONES_RALENTI - 900 || rpmsSimuladas >= REVOLUCIONES_RALENTI - 50) && contadorTemporizacion < 60 && blinker != null) {
                        Thread.sleep(50);
                        panel.getRadialTacometro().setValue(rpmsSimuladas);
                        panel.getMensaje().setText("Intento: " + 1 + "Rev Fuera de rango:  " + contadorTemporizacion);

                    } //edn while fuera de rango
                    //edn while fuera de rango
                    timer.stop();
                    //Si no logra acelerar en un minuto termina la prueba
                    if (contadorTemporizacion >= 60) {
                        liberarRecursos();
                        return;
                    }
                    contadorTemporizacion = 0;
                    crearNuevoTimer();
                    timer.start();
                    medicion = banco.obtenerDatos();
                    panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                    //(rpmFiltrada >= REVOLUCIONES_RALENTI - 500 && rpmFiltrada <= REVOLUCIONES_RALENTI + 50)
                    banco.encenderBombaMuestras(true);
                    while ((rpmsSimuladas >= REVOLUCIONES_RALENTI - 950 && rpmsSimuladas <= REVOLUCIONES_RALENTI + 50) && !medicion.isBajoFlujo() && contadorTemporizacion < 30 && blinker != null) {
                        //mientras este en rango
                        panel.getRadialTacometro().setValue(rpmsSimuladas);
                        if (contadorTemporizacion >= 25) {
                            panel.getMensaje().setText("Tomando medidas");
                            medicion = banco.obtenerDatos();//
                            medicion.setValorRPM((int) rpmsSimuladas);
                            medicion.setValorTAceite(45 - random.nextInt(5));
                            if (repeticiones == 0) {
                                listaRalentiUno.add(medicion);
                            } else {
                                listaRalentiDos.add(medicion);
                                //llenar la lista
                            }
                            //llenar la lista
                        } else {
                            panel.getMensaje().setText("Ralenti, Intento: " + 1 + "tiempo: " + contadorTemporizacion + "s");
                        }
                        panel.getProgressBar().setValue(30 - contadorTemporizacion);
                        panel.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion));
                        medicion = banco.obtenerDatos();
                        //oxigeno = med.getValorO2()*0.01; en motos se debe tener en cuenta el oxigeno???
                        Thread.sleep(50);
                    } //end of while rango
                    banco.encenderBombaMuestras(false);
                    panel.getProgressBar().setValue(0);
                    panel.getProgressBar().setString("0");
                    timer.stop();
                    repeticiones++;
                } while (repeticiones < 2);

                timerSimulacion.stop();
                //final de la simulacion
            } else {//si no simulacion-----------------------------------------------------------------------------------------------------------------------------------
                medicion = banco.obtenerDatos();
                contadorTemporizacion = 0;
                crearNuevoTimer();
                panel.getPanelMensaje().setText("Temperatura de Aceite... ");
                Thread.sleep(2000);
                timer.start();
                while (medicion.getValorTAceite() < T_ACEITE && blinker != null && contadorTemporizacion < 120) {
                    //esperar un rato a que la temperatura de la moto sea adecuada
                    Thread.sleep(300);
                    //System.out.println("Temperatura de Aceite inadecuada Prueba Abortada: " + "T: " + medicion.getCadenaTAceite());
                    panel.getPanelMensaje().setText("Temperatura: " + medicion.getValorTAceite() + " t: " + (120 - contadorTemporizacion));
                    medicion = banco.obtenerDatos();
                } //end while
                timer.stop();
                if (contadorTemporizacion >= 120) {
                    JOptionPane.showMessageDialog(panel, "Temperatura de Aceite inadecuada");
                    //registrarCancelacion("Temperatura de Aceite inadecuada");
                    liberarRecursos();
                    return;
                }//end if
                panel.getPanelMensaje().setText("Temperatura de Aceite OK ");
                Thread.sleep(2000);
                panel.getPanelMensaje().setText("Realice dos Aceleraciones rápidas \n" + "sostenidas no mayores las 2500 rpm > 1800");//detectar por lo menos una aceleracion para continuar
                //int contador = 0;
                double rpm = filtroRPM(banco.obtenerDatos().getValorRPM());
                crearNuevoTimer();
                contadorTemporizacion = 0;
                timer.start();
                int aceleraciones = 0;
                while (aceleraciones < 2) {
                    while (rpm < 2500 && blinker != null && contadorTemporizacion < 120/*contador < 120*/) {//un temporizador de verdad
                        Thread.sleep(50);
                        rpm = filtroRPM(banco.obtenerDatos().getValorRPM());
                        panel.getPanelMensaje().setText("Aceleracion rápidas " + aceleraciones + " \n RPM :" + (int) rpm + " \nEsperando .... t: " + contadorTemporizacion);//detectar por lo menos una aceleracion para continuar
                    }//end while rpms
                    if (contadorTemporizacion >= 120) {//cancelar la prueba
                        JOptionPane.showMessageDialog(null, "no se detectan acelaraciones");

                        try {
                            registrarCancelacion("no se detectan subida de acelaraciones");
                        } catch (ClassNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, "No se encuentra el driver de mysql");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error insertando datos +" + ex.getMessage());
                        } finally {
                            liberarRecursos();
                        }//end finally
                        return;

                    }//end if
                    while (rpm >= 2500 && blinker != null && contadorTemporizacion < 120 /*contador < 120*/) {//un temporizador de verdad
                        Thread.sleep(200);
                        rpm = filtroRPM(banco.obtenerDatos().getValorRPM());
                        panel.getPanelMensaje().setText("Desacelere \n RPM :" + (int) rpm + " \nEsperando .... t: " + contadorTemporizacion);//detectar por lo menos una aceleracion para continuar
                    }//end while rpms
                    if (contadorTemporizacion >= 120) {//cancelar la preuba
                        JOptionPane.showMessageDialog(null, "no se detectan subida de acelaraciones");
                        try {
                            registrarCancelacion("no se detectan acelaraciones");
                        } catch (ClassNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, "No se encuentra el driver de mysql");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error insertando datos +" + ex.getMessage());
                        } finally {
                            liberarRecursos();
                        }//end finally
                        return;

                    }//end if
                    Thread.sleep(200);
                    rpm = filtroRPM(banco.obtenerDatos().getValorRPM());
                    aceleraciones++;
                }//END WHILE ACELERACIONES
                timer.stop();

                if (contadorTemporizacion > 120) {
                    JOptionPane.showMessageDialog(null, "no se detectan acelaraciones");

                    try {
                        registrarCancelacion("no se detectan acelaraciones");
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(null, "No se encuentra el driver de mysql");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error insertando datos +" + ex.getMessage());
                    } finally {
                        liberarRecursos();
                    }//end finally
                    return;
                }//end if

                panel.getPanelMensaje().setText("OK");
                Thread.sleep(1000);
//            boolean pintarVerde = false;
//            boolean pintarAnterior = false;
                panel.getPanelMensaje().setText("");
                Thread.sleep(500);
                panel.getPanelMensaje().setVisible(false);
                panel.getPanelFiguras().setVisible(true);
                panel.getProgressBar().setVisible(true);
                //panel.getButtonFinalizar().setVisible(true);
//            int valorTemperatura = banco.obtenerDatos().getValorTAceite(); //Aplicar correccion
                int valorRPM = banco.obtenerDatos().getValorRPM();

                double rpmFiltrada = filtroRPM(valorRPM);//tomar unas mediciones para que el filtro no se quede con el ultimmo valor
                rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
                rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
                rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
//            contadorTemporizacion = 0;
//            crearNuevoTimer();
//            timer.start();
//            ////////////INICIO DE LA PRUEBA
//            JOptionPane.showMessageDialog(panel,"Introduzca la sonda de prueba");
                //esperar un tiempo para que las rpms esten fuera de rango
                //para evitar que pase derecho
                // si las rpms estan fuera de rango se da un tiempo para que ingresen al rango es una detección de aceleracion
//            int intentos;
//            while ((rpmFiltrada <= REVOLUCIONES_RALENTI - 750 || rpmFiltrada >= REVOLUCIONES_RALENTI ) && (contadorTemporizacion < 120) && blinker != null) {
//                Thread.sleep(120);
//                pintarAnterior = pintarVerde;
//                panel.getLinearTemperatura().setValue(valorTemperatura);
//                panel.getRadialTacometro().setValue(rpmFiltrada);
//                //Si entra en rango se pinta de verde
//                if (rpmFiltrada >= (REVOLUCIONES_RALENTI - 800 ) && rpmFiltrada <= (REVOLUCIONES_RALENTI + 50)) {
//                    //sale al no tener en cuenta la temperatura
//                    pintarVerde = true;
//                    //plot.
//                } else {
//                    pintarVerde = false;
//                }
//                if (pintarAnterior != pintarVerde) {
//                    //necesidad de repintar
//                    if (pintarVerde) {
//                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
//                    } else {
//                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
//                    }
//                } //end if
//                rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
//                panel.getMensaje().setText("Rev Fuera Rango : Tiempo transcurrido: " + contadorTemporizacion + " s");
//            } //end while temperatura y revoluciones
//            //end while temperatura y revoluciones
//            //En este momento el carro debe estara caliente es decir con la temperatura apropiada
//            //Mantener la aceleracion por treinta segundos y observar la presencia de humo negro
//            timer.stop();
//            if (contadorTemporizacion >= 120) {
//                JOptionPane.showMessageDialog(panel, "Prueba abortada porque las revoluciones no entran en rango "); // no sucede
//                banco.getPuertoSerial().close();
//                panel.cerrar();
//                return;
//            }
//            panel.getProgressBar().setMaximum(30);
//            panel.getPanelMensaje().setText("Mantenga las revoluciones en: " + REVOLUCIONES_RALENTI + "\n Observe el color del humo ....");
//            rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
                int intentos = 0;//tres intentos de que la moto ingrese al rango
                int repeticiones = 0;//si hay mas de dos escapes se debe probar en cada escape
                //Si existen mas escapes funcionales que no hacer prueba con dos
                if (dialogoMotos.getNumeroEscapes() > 2)//numero de escapes mayor a 2
                {
                    repeticiones = 0;
                } else if (dialogoMotos.getNumeroEscapes() == 1) {
                    repeticiones = 1;
                } else if (dialogoMotos.getNumeroEscapes() == 2) {
                    repeticiones = 0;
                }
                intentos = 0;
                do {
                    //893-640cuando son dos escapes se deben tomar dos medidas y determimar la maxima
                    //tres intentos para sostener la aceleracion/// en ralenti no se debe fallar mucho
                    //if(repeticiones < 5)
                    if (repeticiones == 1) {
                        JOptionPane.showMessageDialog(panel, "Introduzca la sonda en el tubo de escapa" + (repeticiones));
                    }
                    if (repeticiones == 0) {
                        JOptionPane.showMessageDialog(panel, "Introduzca la sonda en el tubo de escape #" + (repeticiones + 1));
                    }
                    //else
                    //JOptionPane.showMessageDialog(panel, "Introduzca la sonda en el tubo de escape" + (repeticiones+1));
                    panel.getLinearTemperatura().setValue(banco.obtenerDatos().getValorTAceite());
                    while (intentos < 3 && blinker != null) {
                        Thread.sleep(50);
                        contadorTemporizacion = 0;
                        crearNuevoTimer();
                        timer.start();
                        //Mientras este fuera de Rango, debe entrar al rango
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLACK);
                        while ((rpmFiltrada <= REVOLUCIONES_RALENTI - 900 || rpmFiltrada >= REVOLUCIONES_RALENTI - 50) && contadorTemporizacion < 60 && blinker != null) {
                            panel.getRadialTacometro().setValue(rpmFiltrada);
                            panel.getMensaje().setText("Intento: " + intentos + "Rev Fuera de rango:  " + contadorTemporizacion);
                            rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
                            Thread.sleep(50);
                        } //edn while fuera de rango
                        //edn while fuera de rango
                        timer.stop();
                        //Si no logra acelerar en un minuto termina la prueba
                        if (contadorTemporizacion >= 60) {
                            JOptionPane.showMessageDialog(null, "Fallo un minuto rev fuera de rango");
                            try {
                                registrarCancelacion("Fallo un minuto rev fuera de rango");
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(HiloPruebaMotos.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(HiloPruebaMotos.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                liberarRecursos();
                            }
                            //intentos++; //jodida manera de salir
                            //break; //quita el intento actual
                            return;
                        }//end if cont
                        rpmFiltrada = filtroRPM(banco.obtenerDatos().getValorRPM());
                        contadorTemporizacion = 0;
                        crearNuevoTimer();
                        timer.start();
                        medicion = banco.obtenerDatos();
                        banco.encenderBombaMuestras(true);//entre 1400 y 1900
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        while ((rpmFiltrada >= REVOLUCIONES_RALENTI - 950 && rpmFiltrada <= REVOLUCIONES_RALENTI + 50) && !medicion.isBajoFlujo() && contadorTemporizacion < 30 && blinker != null && !medicion.isDilucion()) {
                            //mientras este en rango
                            panel.getRadialTacometro().setValue(rpmFiltrada);
                            if (contadorTemporizacion >= 25) {
                                panel.getMensaje().setText("Tomando medidas");
                                medicion = banco.obtenerDatos();
                                if (repeticiones == 0) {
                                    listaRalentiUno.add(medicion);
                                } else {
                                    listaRalentiDos.add(medicion);
                                    //llenar la lista
                                }
                                //llenar la lista
                            } else {
                                panel.getMensaje().setText("Ralenti, Intento: " + intentos + "tiempo: " + contadorTemporizacion + "s");
                            }
                            panel.getProgressBar().setValue(30 - contadorTemporizacion);
                            panel.getProgressBar().setString(String.valueOf(30 - contadorTemporizacion));
                            medicion = banco.obtenerDatos();
                            valorRPM = medicion.getValorRPM();
                            //oxigeno = med.getValorO2()*0.01; en motos se debe tener en cuenta el oxigeno???
                            rpmFiltrada = filtroRPM(valorRPM);
                            Thread.sleep(50);
                        } //end of while rango
                        banco.encenderBombaMuestras(false);
                        //end of while rango
                        timer.stop();
                        if (contadorTemporizacion >= 30) {
                            //la prueba ha sido exitosa
                            break; //sale de while de intentos si fuera continue solo saldria de la iteracion actual
                        } else {
                            intentos++;
                            panel.getMensaje().setText("No se mantuvieron las revoluciones... Intento :" + (intentos + 1));
                        } //end of

                    } //end of while intentos
                    //end of intentos
                    if (intentos < 3) {
                        System.out.println("La prueba ha sido realizada OK en menos de tres intentos");
                    } else {
                        System.out.println("La prueba no se ha finalizado en menos de tres intentos ... muere");
                        break; //sale del dowhile
                    }
                    repeticiones++;
                } while (repeticiones < 2);//dos repeticiones si son dos escapes, tres intentos por repeticion
                //para una salida correcta de los ciclos repeticiones debe ser igual a 2 o igual a 7
                if ((dialogoMotos.getNumeroEscapes() > 1 && repeticiones != 2) || intentos >= 3) {
                    System.out.println("La prueba ha fallado");
                    panel.getPanelMensaje().setText("Prueba ha fallado ");
                    try {
                        //TODO  registrar la razon del fracaso
                        registrarCancelacion("Fallo en numero de intentos");
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(panelCero, "No encuentra el driver Mysql error!!!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(panelCero, "Error conectandose con la base de datos");
                    } finally {
                        liberarRecursos();
                    }
                    return;
                }//end if dialogomotos
            }//end else de simulación
            //TODO procesar los datos.
            procesarDatos();//modifica las variables de instancia dejandolas listas para insertar en la BD
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getPanelMensaje().setText("Prueba Finalizada Con Exito\n Guardando datos");
            Thread.sleep(1500);

            try {
                registrarMedidas();
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Error con el driver");
                ex.printStackTrace(System.err);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error insertando datos en la bd");

                ex.printStackTrace(System.err);
            } finally {
                liberarRecursos();
            }
        } //end of try de cancelacion
        catch (InterruptedException ex) {
            System.out.println("Hilo de la Prueba de Motos Terminado ");
            liberarRecursos();
            String showInputDialog = JOptionPane.showInputDialog("comentario de aborto");
            try {
                registrarCancelacion(showInputDialog);
            } catch (ClassNotFoundException exc) {
                JOptionPane.showMessageDialog(null, "Error con el driver");
                exc.printStackTrace();
            } catch (SQLException exc) {
                JOptionPane.showMessageDialog(null, "Error insertando datos en la bd");
                exc.printStackTrace();
            } finally {
                liberarRecursos();
            }
            //throw new CancellationException("HiloMotosCancelado");
        }
    }//end of method run

    public int getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(int idPrueba) {
        this.idPrueba = idPrueba;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     *
     *
     */
    private void crearNuevoTimer() {

        timer = new Timer(1000, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }//end of method actionPerformed
        });

    }//end of method crearNuevoTimer

    /**
     * Metodo para configurar la variable de clase banco retorna false si no se
     * encuentra el banco
     *
     * @return
     */
    public boolean buscarBanco() {//TODO probar en otro computador

        banco = new BancoSensors();
        HashSet<CommPortIdentifier> puertosSeriales = PortSerialUtil.getAvailableSerialPorts();
        if (puertosSeriales.isEmpty()) {
            System.out.println("No hay puertos Seriales en el banco");
            return false;
        }//end if
        Iterator<CommPortIdentifier> itr = puertosSeriales.iterator();

        while (itr.hasNext()) {
            try {
                CommPortIdentifier portIdentifier = itr.next();
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                banco.setPuertoSerial(serialPort);
                System.out.println("Buscando banco en:" + portIdentifier.getName());
                String serial = banco.numeroSerial();
                System.out.println(serial);
                if (serial != null) {
                    System.out.println("Banco encontrado en" + portIdentifier.getName() + "Serial" + serial);
                    System.out.println("Flujos de Entrada/Salida del banco configurados");
                    return true;
                } else {
                    System.out.println("Banco no encontrado en " + portIdentifier.getName());
                    commPort.close();
                }
            } //end of while
            catch (PortInUseException ex) {

            } catch (UnsupportedCommOperationException ucoe) {
                ucoe.printStackTrace();
            }
        }//end of while
        return false;//retorna falso si sale por aca
    }//end of method run

    /**
     *
     */
    public void procesarDatos() {
        double mediaHCdos = 0;
        double mediaCOdos = 0;
        double mediaCO2dos = 0;
        double mediaO2dos = 0;
        List<Double> listaHC = new ArrayList<Double>();
        List<Double> listaCO = new ArrayList<Double>();
        List<Double> listaCO2 = new ArrayList<Double>();
        List<Double> listaO2 = new ArrayList<Double>();
        for (MedicionGases m : listaRalentiUno) {
            listaHC.add(new Double(m.getValorHC()));
            listaCO.add(new Double(m.getValorCO() * 0.01));
            listaCO2.add(new Double(m.getValorCO2() * 0.1));
            listaO2.add(new Double(m.getValorO2() * 0.01));
        }
        Collections.sort(listaHC);
        Collections.sort(listaCO);
        Collections.sort(listaCO2);
        Collections.sort(listaO2);
        if (listaHC.size() > 0) {
            mediaHC = listaHC.get(listaHC.size() - 1);
            mediaCO = listaCO.get(listaCO.size() - 1);
            mediaCO2 = listaCO2.get(listaCO2.size() - 1);
            mediaO2 = listaO2.get(listaO2.size() - 1);
        }

        ///limpiar las listas
        listaHC.clear();
        listaCO.clear();
        listaCO2.clear();
        listaO2.clear();

        for (MedicionGases m : listaRalentiDos) {
            listaHC.add(new Double(m.getValorHC()));
            listaCO.add(new Double(m.getValorCO() * 0.01));
            listaCO2.add(new Double(m.getValorCO2() * 0.1));
            listaO2.add(new Double(m.getValorO2() * 0.01));
        }

        Collections.sort(listaHC);
        Collections.sort(listaCO);
        Collections.sort(listaCO2);
        Collections.sort(listaO2);

        mediaHCdos = listaHC.get(listaHC.size() - 1);
        mediaCOdos = listaCO.get(listaCO.size() - 1);
        mediaCO2dos = listaCO2.get(listaCO2.size() - 1);
        mediaO2dos = listaO2.get(listaO2.size() - 1);

        //guarda los mayores
        mediaHC = mediaHC < mediaHCdos ? mediaHCdos : mediaHC;
        mediaCO = mediaCO < mediaCOdos ? mediaCOdos : mediaCO;
        mediaCO2 = mediaCO2 < mediaCO2dos ? mediaCO2dos : mediaCO2;
        mediaO2 = mediaO2 < mediaO2dos ? mediaO2dos : mediaO2;

//
        if (dialogoMotos.getRadioCuatroTiempos().isSelected()) {
            if (mediaO2 > 6) {//correccion de CO
                System.out.println("MediaHC" + mediaHC);
                System.out.println("MediaCO" + mediaCO);
                System.out.println("MediaCO2" + mediaCO2);
                System.out.println("MediaO2" + mediaO2);
                mediaHC = (mediaHC) * ((21 - 6) / (21 - mediaO2));
                mediaCO = (mediaCO) * ((21 - 6) / (21 - mediaO2));
                mediaCO2 = (mediaCO2) * ((21 - 6) / (21 - mediaO2));
                //mediaO2 = 6;
                System.out.println("Correcion de CO Cuatro Tiempos");
            }
        }//end if vehiculoDosTiempos
        if (dialogoMotos.getRadioDosTiempos().isSelected()) {
            if (mediaO2 > 11) {//correcion
                System.out.println("MediaHC" + mediaHC);
                System.out.println("MediaCO" + mediaCO);
                System.out.println("MediaCO2" + mediaCO2);
                System.out.println("MediaO2" + mediaO2);
                mediaHC = (mediaHC) * ((21 - 11) / (21 - mediaO2));
                mediaCO = (mediaCO) * ((21 - 11) / (21 - mediaO2));
                mediaCO2 = (mediaCO2) * ((21 - 11) / (21 - mediaCO2));
                //mediaO2 = 11;
                System.out.println("Correcion de CO Dos Tiempos");
            }
        }//end if vehiculoCuatroTiempos
        mediaRPM = calcularMediaRpm();
        mediaTemp = calcularMediaTemp();
        System.out.println("HC: " + mediaHC);
        System.out.println("CO: " + mediaCO);
        System.out.println("CO2: " + mediaCO2);
        System.out.println("O2: " + mediaO2);

    }//end of method procesarDatos

    private double calcularMediaRpm() {
        List<Double> listaUno = new ArrayList<Double>();
        for (MedicionGases m : listaRalentiUno) {
            listaUno.add(new Double(m.getValorRPM()));
        }
        List<Double> listaDos = new ArrayList<Double>();
        for (MedicionGases m : listaRalentiDos) {
            listaDos.add(new Double(m.getValorRPM()));
        }
        return calcularMedia(listaUno, listaDos);

    }

    private double calcularMedia(List<Double> l1, List<Double> l2) {
        double media = 0;
        for (Double d : l1) {
            media += d.doubleValue();
        }
        if (l2 != null) {
            for (Double d : l2) {
                media += d.doubleValue();
            }
            media = media / (l1.size() + l2.size());
            return media;
        } else {
            media = media / l1.size();
            return media;
        }
    }

    /**
     * Metodo para filtrar las rpm
     *
     * @param rpm
     * @return
     */
    private double filtroRPM(double rpm) {// vota generica que se preocupe el hilo
        //un arreglo/ lista / cola de 10 posiciones
        //rpmFiltradas // rpmsinFiltrar
        int filtro = 0;
        for (int cont = 0; cont < 9; cont++) {
            //Corre la lista de mediciones sin filtrar una posición para hacer espacio al
            //ultimo dato leido
            arregloMedidas[cont] = arregloMedidas[cont + 1];//corrimiento a la izquierda esto es un lilo
            //primero en entrar primero en salir
            if (Math.abs(arregloMedidas[cont] - rpm) > 100) {
                filtro++;
            }//end of if
            arregloFiltradas[cont] = arregloFiltradas[cont + 1];
            //saca la media del arreglo
        }//end for

        //---------------------------------------------------------
        //       Hace ajuste por cambio repentino
        //---------------------------------------------------------
        //Si todos los datos tienen una diferencia superior a 100
        //siginifica que el valor real de RPM definitivamente ya cambió
        double media = mean(arregloMedidas);

        if (filtro >= 10) {
            for (int i = 0; i < 10; i++) {
                arregloFiltradas[i] = media;
            }
        }//end if
        else //si no, siginifica que el dato es un valor de ruido o se ha mantenido estable
        //Si la diferencia es menor a el dato anterior en 50 entonces el dato sirve
        if (Math.abs(rpm - rpmAnterior) <= 50) {
            arregloFiltradas[9] = rpmAnterior;
            //si no, el dato puede ser de ruido y hay que seguirlo lentamente
        } else //incrementa de 30 en 30 solamente
        if ((rpm - rpmAnterior) < 0) {
            arregloFiltradas[9] = rpm + 30;
        } else {
            arregloFiltradas[9] = rpm - 30;
        }//end else menor a 50//end else ruido
        arregloMedidas[9] = rpm;
        double rpmRetorno = mean(arregloFiltradas);
        rpmAnterior = rpmRetorno;
        if (cuatroTiempos) {
            return rpmRetorno;
        } else {
            return rpmRetorno / 2;
        }
    }//end of method filtroRPM

    private void stop() {
        cancelacion = true;
        Thread tempBlinker = blinker;
        blinker = null;
        if (tempBlinker != null) {
            tempBlinker.interrupt();
        }
    }//end of method stop

    public static double mean(double[] p) {
        double sum = 0;  // sum of all the elements
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }//end method mean

    private void mostrarDialogoMotos() {
        JDialog d = new JDialog((JDialog) SwingUtilities.getWindowAncestor(panel), true);
        d.setTitle("Parametros Moto");
        d.add(dialogoMotos);
        d.setSize(240, 300);
        d.getRootPane().setDefaultButton(dialogoMotos.getButtonContinuar());
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setLocationRelativeTo(panel);
        d.setVisible(true);
//JOptionPane.showMessageDialog(null,dialogoMotos,"ParamExploradoras", JOptionPane.PLAIN_MESSAGE);
    }

//    private void dormir(int tiempos) {
//        try {
//            Thread.sleep(tiempos);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(HiloPruebaMotos.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private void liberarRecursos() {
        if (timer != null) {
            timer.stop();
        }
        if (timerSimulacion != null) {
            timerSimulacion.stop();
        }
        if (banco != null) {
            banco.getPuertoSerial().close();
        }
        panel.cerrar();//Algo mas?
    }//end of method liberarRecursos

    private void configurarPanelVerificacion() {
        p = new PanelVerificacionDiesel();
        p.getCheckBox7().setVisible(false);
        p.getLbl7().setVisible(false);
//        p.getLbl10().setVisible(false);
//        p.getCheckBox8().setVisible(false);
//        p.getLbl8().setVisible(false);
        p.getCheckBox10().setVisible(false);
        p.getCheckBox4().setVisible(true);
        p.getCheckBox6().setVisible(false);
        p.getCheckBox9().setVisible(false);
        p.getLbl9().setVisible(false);
        p.getLbl6().setVisible(false);
        p.getLbl4().setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.stop();
    }

    private void cargarUrl() {
        Properties props = new Properties();
        //try retrieve data from file
        try {
            props.load(new FileInputStream("./propiedades.properties"));//TODO warining
            urljdbc = props.getProperty("urljdbc");
            System.out.println(urljdbc);
        }//catch exception in case properties file does not exist
        catch (IOException e) {
            e.printStackTrace();
        }
    }//end of method cargarUrl

    private void registrarCancelacion(String comentario) throws ClassNotFoundException, SQLException {
        Connection conexion = null;

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        conexion = Conex.getConnection();
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setString(1, comentario);
        instruccion.setInt(2, idUsuario);
        instruccion.setString(4, serialEquipo);
        instruccion.setInt(5, idPrueba);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setTimestamp(3, new java.sql.Timestamp(c.getTimeInMillis()));
        int n = instruccion.executeUpdate();
        conexion.close();
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.CANCELADO, comentario,serialEquipo);
        // FIN EVENTOS SICOV
    }//end of method registrarCancelacion

    private void registrarMedidas() throws ClassNotFoundException, SQLException {
        boolean registro = verificarMedidas(mediaCO, mediaCO2, mediaHC, mediaO2, mediaRPM, mediaTemp);//Verifica si una medida es menor o igual a cero

        if (!registro) {
            JOptionPane.showMessageDialog(null, "Alguno o todos los datos son invalidos debe reiniciar la prueba? ");
            return;
        }
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        System.out.println("Conectandose con:" + "jdbc:mysql://" + urljdbc + "/db_cda");
        conexion.setAutoCommit(false);
        //Crear objeto preparedStatement para realizar la consulta con la base de datos
//        String statementBorrar = ("DELETE  FROMmedidas WHERE TEST = ?");
//        PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
//        instruccionBorrar.setInt(1,idPrueba);
//        instruccionBorrar.executeUpdate();
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);

        if (dialogoMotos.getRadioCuatroTiempos().isSelected()) {//insertar las medidas correspondientes a cuatro tiempos
            instruccion.setInt(1, 8001);//HC cuatro tiempos
            instruccion.setDouble(2, mediaHC);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8002);//CO cuatro tiempos
            instruccion.setDouble(2, mediaCO);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8003);//CO2 ralenti
            instruccion.setDouble(2, mediaCO2);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8004);//O2 en ralenti
            instruccion.setDouble(2, mediaO2);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8005);//revoluciones ralenti
            instruccion.setInt(2, (int) mediaRPM);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8006);//temperatura en ralenti
            instruccion.setInt(2, (int) mediaTemp);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
        } else {
            instruccion.clearParameters();
            instruccion.setInt(1, 8018);//HC dos tiempos
            instruccion.setDouble(2, mediaHC);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8020);//CO dos tiempos
            instruccion.setDouble(2, mediaCO);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8019);//CO2 dos tiempos
            instruccion.setDouble(2, mediaCO2);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8021);//O2 dos tiempos
            instruccion.setDouble(2, mediaO2);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8028);//rpm dos tiempos
            instruccion.setInt(2, (int) mediaRPM);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8022);
            instruccion.setDouble(2, (int) mediaTemp);//temperatura dos tiempos
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
        }
        boolean pruebaAprobada = false;
        if (mediaCO <= permisibleCO && mediaHC <= permisibleHC) {
            System.out.println("-----------------------VALOR PARA PERMISIBLECO: " + permisibleCO);
            System.out.println("-----------------------valor para permisibleHC " + permisibleHC);
            System.out.println("valor para mediaCO: " + mediaCO);
            System.out.println("valor para mediaHc: " + mediaHC);
            pruebaAprobada = true;
        } else {
            pruebaAprobada = false;
        }
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        //Un objeto ResultSet, almacena los datos de resultados de una <span class="IL_AD" id="IL_AD11">consulta</span>
        String statement2 = "UPDATE pruebas SET Fecha_prueba = ?,Finalizada = 'Y',Aprobada=?,Abortada='N', usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
        Date d = new Date();
        instruccion2.setTimestamp(1, new java.sql.Timestamp(d.getTime()));
        if (pruebaAprobada) {
            instruccion2.setString(2, "Y");
        } else {
            instruccion2.setString(2, "N");
        }
        instruccion2.setInt(3, idUsuario);
        instruccion2.setString(4, serialEquipo);
        instruccion2.setInt(5, idPrueba);
        instruccion2.setFetchDirection(limiteHC);
        int executeUpdate = instruccion2.executeUpdate();
        if (!pruebaAprobada) {
            String strDefecto = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES (?,?)";
            PreparedStatement psDefecto = conexion.prepareStatement(strDefecto);
            psDefecto.setInt(1, 84018);
            psDefecto.setInt(2, idPrueba);
            //psDefecto.setInt(3,idHojaPrueba);
            psDefecto.executeUpdate();
        }
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
    }//end of method registarMedidas

    public boolean verificarMedidas(double... medidas) {
        for (double d : medidas) {
            if (d <= 0) {
                return false;
            }
        }
        return true;
    }

    private double calcularMediaTemp() {
        List<Double> listaUno = new ArrayList<Double>();
        for (MedicionGases m : listaRalentiUno) {
            if (simulacion) {
                listaUno.add(new Double(m.getValorTAceiteSinTabla()));
            } else {
                listaUno.add(new Double(m.getValorTAceite()));
            }
        }
        List<Double> listaDos = new ArrayList<Double>();
        for (MedicionGases m : listaRalentiDos) {
            if (simulacion) {
                listaDos.add(new Double(m.getValorTAceiteSinTabla()));
            } else {
                listaDos.add(new Double(m.getValorTAceite()));
            }
        }
        return calcularMedia(listaUno, listaDos);
    }//end of method calcularMediaTemp

    private Date consultarFechaIngresoVehiculo(int idPrueba) {
        String statement = "SELECT hp.Fecha_ingreso_vehiculo FROM pruebas as p INNER JOIN hoja_pruebas as hp ON p.hoja_pruebas_for = hp.TESTSHEET INNER JOIN vehiculos as v ON hp.Vehiculo_for = v.CAR WHERE hp.TESTSHEET = ?";
        try {
            Connection cn = cargarConexion();
            PreparedStatement consultaFechaIngreso = cn.prepareStatement(statement);
            consultaFechaIngreso.setInt(1, idPrueba);
            ResultSet rs = consultaFechaIngreso.executeQuery();
            while (rs.first()) {
                System.out.println("-------sin parsear" + rs.getDate("Fecha_ingreso_vehiculo"));
                System.out.println("-------to string" + rs.getDate("Fecha_ingreso_vehiculo").toString());
                return rs.getDate("Fecha_ingreso_vehiculo");
            }
        } catch (SQLException e) {
            System.err.println("Error en el metodo : consultarFecha()" + e.getMessage() + e.getLocalizedMessage());
        }
        return null;
    }

    private boolean cargarParametrosVehiculo() throws ClassNotFoundException, SQLException, ParseException {
        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Date fechaInicioResolucion = sdf.parse("2022-08-18 00:00:00");
        Date fechaInicioResolucion0762Tabla27 = sdf.parse("2023-02-07 23:59:59");
        Date fechaDeIngresoVehiculo = sdf.parse(sdf.format(consultarFechaIngresoVehiculo(idHojaPrueba)));

        String statement = "SELECT v.Modelo,v.CAR,v.Tiempos_motor,hp.TESTSHEET FROM pruebas as p INNER JOIN hoja_pruebas as hp ON p.hoja_pruebas_for = hp.TESTSHEET INNER JOIN vehiculos as v ON hp.Vehiculo_for = v.CAR WHERE p.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, idPrueba);
        ResultSet rs = instruccion.executeQuery();
        if (rs.first()) {
            modelo = rs.getInt("Modelo");
            placa = rs.getInt("CAR");
            tiemposMotor = rs.getInt("Tiempos_motor");
            //fecha_ingreso_v = rs.getDate("Fecha_ingreso_vehiculo");

            /*try {
                fechaInicioResolucion = sdf.parse("2022-08-18");
                fechaDeIngresoVehiculo = sdf.parse(fecha_ingreso_v.toString());
            } catch (ParseException ex) {
                Logger.getLogger(HiloPruebaMotos.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            System.out.println("----------------Fecha_ingreso_vehiculo:" + fechaDeIngresoVehiculo);
            //String strResolucion = ("SELECT permisibles.Resolucion FROM permisibles WHERE Id_permisible = ?");
            String strPermisible = ("SELECT permisibles.Valor_maximo FROM permisibles WHERE Id_permisible = ?");
            PreparedStatement statementCO = conexion.prepareStatement(strPermisible);//permisible de CO
            PreparedStatement statementHC = conexion.prepareStatement(strPermisible);//permisible de HC
            if (tiemposMotor == 2) {
                if (modelo <= 2009) {
                    if (fechaDeIngresoVehiculo.after(fechaInicioResolucion0762Tabla27)) {
                        statementCO.setInt(1, 328);
                        statementHC.setInt(1, 329);
                    } else {
                        statementCO.setInt(1, 10);
                        statementHC.setInt(1, 12);
                    }
                } else {
                    //ajustar permisibles
                    if (fechaDeIngresoVehiculo.after(fechaInicioResolucion0762Tabla27)) {
                        statementCO.setInt(1, 331);
                        statementHC.setInt(1, 330);
                    } else {
                        statementCO.setInt(1, 11);
                        statementHC.setInt(1, 13);
                    }
                }
            } else {
                if (fechaDeIngresoVehiculo.after(fechaInicioResolucion0762Tabla27) && fechaDeIngresoVehiculo.before(fechaInicioResolucion0762Tabla27)) {
                    //ajustar permisibles
                    statementCO.setInt(1, 326);//14
                    statementHC.setInt(1, 327);//13
                } else if(fechaDeIngresoVehiculo.after(fechaInicioResolucion0762Tabla27)){
                    statementCO.setInt(1, 332);
                    statementHC.setInt(1, 333);
                }
                else {
                    statementCO.setInt(1, 14);//326
                    statementHC.setInt(1, 13);//327
                }
            }
            ResultSet co = statementCO.executeQuery();
            if (co.first()) {
                permisibleCO = co.getDouble("Valor_maximo");
                System.out.println("permisible CO: " + permisibleCO);
            } else {
                JOptionPane.showMessageDialog(null, "NO se pueden cargar los datos de permisible de CO");
            }
            ResultSet hc = statementHC.executeQuery();
            if (hc.first()) {
                permisibleHC = hc.getDouble("Valor_maximo");
                System.out.println("permisible HC: " + permisibleHC);
            } else {
                JOptionPane.showMessageDialog(null, "NO se pueden cargar los datos de permisible de HC");
            }
            conexion.close();
            return true;
        } else {
            conexion.close();
            return false;
        }
    }//end of cargarParametrosVehiculo

    private void registrarDefectosVisuales() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        //Insertar las medidas dependiendo del numero de tiempos del motor

        String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        Set<Integer> cd = p.getConjuntoDefectos();
        for (Integer def : cd) {
            instruccion.clearParameters();
            instruccion.setInt(1, def);
            instruccion.setInt(2, idPrueba);
            instruccion.executeUpdate();
        }
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        String strFinalizarPrueba = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement psFinalizar = conexion.prepareStatement(strFinalizarPrueba);
        psFinalizar.setInt(1, idUsuario);
        psFinalizar.setString(3, serialEquipo);
        psFinalizar.setInt(4, idPrueba);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        psFinalizar.setTimestamp(2, new java.sql.Timestamp(c.getTimeInMillis()));
        int n = psFinalizar.executeUpdate();
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
    }//end of method registrarDefectosVisuales

    private Connection cargarConexion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}//end of class HiloPruebaMotos
