/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.modulopuc.utilidades.Mensajes;
import eu.hansolo.steelseries.tools.BackgroundColor;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.soltelec.medicionrpm.CapelecSerial;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.medicionrpm.ProductorCapelec;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.pruebasgases.DialogoRPMTemp;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.BufferStrings;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.PortSerialUtil;

/**
 * Esta clase es la implementacion del proceso de sonometro para vehiculos
 * version 0
 *
 * @author FABIAN CAMARGO
 */
public class HiloPruebaSonometro implements Runnable, ActionListener {

    private BancoGasolina banco;
    PanelPruebaSonometro panel;
    private volatile Thread blinker;
    private JDialogSimulacion dlg;
    private boolean cancelacion;
    private SerialPort serialPort;
    private LectorHTech lectorHTech;
    private ConsumidorHtech consumidorHTech;
    private Thread threadProductor;
    private Thread threadConsumidor;
    private BufferStrings bufferMed;
    private Timer timer;
    private int contadorTemporizacion;
    private boolean simuladasRalenti;
    private double rpmSimuladas;
    private boolean subidaAceleracion;
    private int contadorSimulacion;
    private boolean simuladasCrucero;
    private int velRalenti = 1523;
    private int velCrucero = 2529;
    private boolean bajadaAceleracion;
    private boolean terminado;
    private Timer timerSimulacion;
    private int T_ANTES_ACELERAR;
    private Random random2 = new Random();
    private ArrayList<Double> listaCiclo1;
    private ArrayList<Double> listaCiclo2;
    private ArrayList<Double> listaCiclo3;
    private double promedioRuido;
    private double[] arregloMedidas = new double[10];
    private double[] arregloFiltradas = new double[10];
    private double rpmAnterior;
    private MedicionGases medicion;
    private double rpmFiltradas;
    private String urljdbc;
    private String puertoSonometro;
    private int idPrueba = 4;
    private int idUsuario = 1;
    private int idHojaPrueba = 4;
    private Double maximo1;
    private Double maximo2;
    private Double maximo3;
    private double UMBRAL_DECIBELES = 10;
    private double pito1;
    private double pito2;
    private double pito3;
    private ProductorCapelec productor;
    private ConsumidorCapelec capelec;
    private SerialPort serialPortKit;
    private double rpmKit;
    private List<Double> listaAmbiente;

    public HiloPruebaSonometro(PanelPruebaSonometro p) {
        panel = p;
        p.getButtonFinalizar().addActionListener(this);
        dlg = new JDialogSimulacion(null, true);
        bufferMed = new BufferStrings();
    }

    @Override
    public void run() {
        JDialogReconfiguracionKit dialogReconfiguracion;
        try {
            panel.getProgressBar().setVisible(false);
            //proporcionar un metodo de simulación para el sonometro
            //Un dialogo de inicio que de una bienvenida simplemente para proporcionar una manera de simular            
            try {
                cargarParametros();
            } catch (FileNotFoundException fe) {
                JOptionPane.showMessageDialog(panel, "Archivo configuracion no encontrado");
                fe.printStackTrace();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(panel, "Error leyendo el archivo de configuracion");
                ioe.printStackTrace();
            }
            blinker = Thread.currentThread();
            dlg.setVisible(true);

            ///toda la logica usando simulacion se encuentra encerrada en este if
            if (dlg.getPanelInicioSim().simulacion) {

                panel.getPanelMensaje().setText("Buscando Banco...");
                Thread.sleep(1500);
                try {
                    inicializarPuertoSerial(puertoSonometro);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error conectandose con el sonometro revise propiedades");
                    panel.cerrar();
                }

                panel.getPanelMensaje().setText("Inicio de la prueba Calibrando cero de \n ruido ambiente");
                Thread.sleep(500);
                timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        contadorTemporizacion++;
                    }
                });
                timer.start();
                listaAmbiente = new ArrayList<Double>();
                double valorDb = 0.0;
                while (contadorTemporizacion < 10 && blinker != null) {
                    Thread.sleep(250);
                    valorDb = consumidorHTech.getDecibeles();
                    listaAmbiente.add(valorDb);
                    panel.getPanelMensaje().setText("Calibrando Cero t:" + contadorTemporizacion);
                }//end while

                double media = 0;
                for (Double d : listaAmbiente) {
                    media += d;
                }
                if (listaAmbiente.size() > 0) {
                    media /= listaAmbiente.size();
                    System.out.println("Media " + media);
                }
                if (media < 0) {//TODO liberarRecursos
                    JOptionPane.showMessageDialog(panel, "no se tomaron mediciones del sonometro prueba abortada");
                    liberarRecursos();
                    this.stop();
                }
                boolean blink = false;//variable para hacer que el textfield haga blink
                int contador = 0;
                while (consumidorHTech.getDecibeles() < media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 1");
                        blink = false;
                    } else {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 1 \n...");
                        blink = true;
                    }
                    Thread.sleep(250);
                    contador++;
                }
                pito1 = consumidorHTech.getDecibeles();
                Thread.sleep(1000);
                while (consumidorHTech.getDecibeles() >= media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                        blink = false;
                    } else {
                        blink = true;
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
                    }
                    Thread.sleep(250);
                    contador++;
                }

                while (consumidorHTech.getDecibeles() < media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 2");
                        blink = false;
                    } else {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 2\n...");
                        blink = true;
                    }
                    Thread.sleep(250);
                    contador++;
                }
                pito2 = consumidorHTech.getDecibeles();
                Thread.sleep(1000);
                while (consumidorHTech.getDecibeles() >= media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                        blink = false;
                    } else {
                        blink = true;
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
                    }
                    Thread.sleep(250);
                    contador++;
                }

                while (consumidorHTech.getDecibeles() < media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 3");
                        blink = false;
                    } else {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 3\n...");
                        blink = true;
                    }
                    Thread.sleep(250);
                    contador++;
                }
                pito3 = consumidorHTech.getDecibeles();
                Thread.sleep(1000);
                while (consumidorHTech.getDecibeles() >= media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                        blink = false;
                    } else {
                        blink = true;
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
                    }
                    Thread.sleep(250);
                    contador++;
                }

                //simulacion de las rpms
                listaCiclo1 = new ArrayList<Double>();
                listaCiclo2 = new ArrayList<Double>();
                listaCiclo3 = new ArrayList<Double>();
                panel.getPanelMensaje().setVisible(false);
                panel.getPanelFiguras().setVisible(true);
                Thread.sleep(50);
                JOptionPane.showMessageDialog(panel, "Inicie Aceleracion");

                Thread.sleep(20);
                timerSimulacion = new Timer(150, new ListenerSimulacion());
                //se dan tres intentos para realizar la prueba al terminar los intentos
                //la prueba se da por terminada con rechazo

                int intentos = 0; // lo que haga falta
                velRalenti = 1950 - random2.nextInt(950);
                velCrucero = 3700 - random2.nextInt(1200);
                while (intentos < 2 && blinker != null) {

                    int ciclos = 0;
                    contadorTemporizacion = 0;
                    while (ciclos < 3 && blinker != null) {
                        Thread.sleep(10);
                        T_ANTES_ACELERAR = 1;
                        simuladasRalenti = true;//para que permanezca en cero un tiempo
                        panel.getMensaje().setText("ACELERE!!!");
                        Thread.sleep(500);
                        timerSimulacion.start();
                        //dialogo.getLabelMensaje().setText("MANTENGA RALENTI");
                        contadorTemporizacion = 0;
                        //dialogo.getDisplayTiempo().setLcdColor(LcdColor.BLACK_LCD);
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLUE);//azul en ralenti
                        while (contadorTemporizacion < T_ANTES_ACELERAR && blinker != null) {
                            Thread.sleep(100);
                            panel.getMensaje().setText("ACELERE!!!");
                            panel.getRadialTacometro().setValue(rpmSimuladas);
                        }//end whileF
                        Thread.sleep(50);//proporciona un punto de salida
                        //ACELERACION EN MENOS DE 5S
                        contadorTemporizacion = 0;
                        contadorSimulacion = 0;
                        simuladasRalenti = false;
                        subidaAceleracion = true;
                        //INICIO TOMA DE DATOS

                        System.err.println("CICLO: " + ciclos);
                        //panel.getMensaje().setText("Acelerando t:" + contadorTemporizacion);
                        contadorTemporizacion = 0;
                        while (contadorTemporizacion < 3 && rpmSimuladas <= velCrucero - 200 && blinker != null) {
                            Thread.sleep(50);
                            panel.getMensaje().setText("ACELERE!!!");
                            panel.getRadialTacometro().setValue(rpmSimuladas);
                        }//end of while
                        subidaAceleracion = false;
                        contadorTemporizacion = 0;
                        simuladasCrucero = true;

                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        while (contadorTemporizacion < 4 && (rpmSimuladas >= velCrucero - 200 && rpmSimuladas <= 3700) && blinker != null) {
                            Thread.sleep(50);
                            panel.getMensaje().setText("EN RANGO MIDIENDO");
                            panel.getRadialTacometro().setValue(rpmSimuladas);
                            if (ciclos == 0) {
                                listaCiclo1.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 1) {
                                listaCiclo2.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 2) {
                                listaCiclo3.add(consumidorHTech.getDecibeles());
                            }
                        }
                        simuladasCrucero = false;
                        bajadaAceleracion = true;
                        contadorSimulacion = 0;
                        contadorTemporizacion = 0;
                        //dialogo.getDisplayTiempo().setLcdColor(LcdColor.RED_LCD);
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        panel.getMensaje().setText("DESACELERE...!!!");
                        while (contadorTemporizacion <= 3 && blinker != null) {
                            Thread.sleep(150);
                            panel.getMensaje().setText("DESACELERE!!");
                            panel.getRadialTacometro().setValue(rpmSimuladas);;
                            if (ciclos == 0) {
                                listaCiclo1.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 1) {
                                listaCiclo2.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 2) {
                                listaCiclo3.add(consumidorHTech.getDecibeles());
                            }
                        }

                        bajadaAceleracion = false;
                        contadorSimulacion = 0;
                        System.err.println("FIN CICLO:" + ciclos);
                        ciclos++;
                    }//end while ciclos
                    Thread.sleep(1500);
                    timer.stop();
                    timerSimulacion.stop();
                    intentos = 4;
                }//end while intentos

            } else {//fin simulacion
                //sino simulacion medir con el banco o medir con el kit
//             Object objSeleccionMotivo = JOptionPane.showInputDialog(
//                                null,
//                                "Seleccione Dispositivo",
//                                "Medir con",
//                                JOptionPane.QUESTION_MESSAGE,
//                                null,  // null para icono defecto
//                                new Object[] {"Banco","Kit de revoluciones"},
//                                "Kit de revoluciones");
//            String strSeleccionMotivo = (String)objSeleccionMotivo;
                //TOMAR LA MEDICION DE LA BOcina
                //panel.getPanelMensaje().setText("Buscando Banco...");
                Thread.sleep(1500);
                try {
                    inicializarPuertoSerial(puertoSonometro);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error conectandose con el sonometro revise propiedades");
                    panel.cerrar();
                }

                panel.getPanelMensaje().setText("Inicio de la prueba Calibrando cero de \n ruido ambiente");
                Thread.sleep(500);
                timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        contadorTemporizacion++;
                    }
                });
                timer.start();
                List<Double> listaAmbiente = new ArrayList<Double>();
                double valorDb = 0.0;
                while (contadorTemporizacion < 10 && blinker != null) {
                    Thread.sleep(250);
                    valorDb = consumidorHTech.getDecibeles();
                    listaAmbiente.add(valorDb);
                    panel.getPanelMensaje().setText("Calibrando Cero t:" + contadorTemporizacion);
                }//end while

                double media = 0;
                for (Double d : listaAmbiente) {
                    media += d;
                }
                if (listaAmbiente.size() > 0) {
                    media /= listaAmbiente.size();
                    System.out.println("Media " + media);
                }
                if (media < 0) {//TODO liberarRecursos
                    JOptionPane.showMessageDialog(panel, "no se tomaron mediciones del sonometro prueba abortada");
                    liberarRecursos();
                    this.stop();
                }
                boolean blink = false;//variable para hacer que el textfield haga blink
                int contador = 0;
                while (consumidorHTech.getDecibeles() < media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 1");
                        blink = false;
                    } else {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 1 \n...");
                        blink = true;
                    }
                    Thread.sleep(250);
                    contador++;
                }
                pito1 = consumidorHTech.getDecibeles();
                Thread.sleep(1000);
                while (consumidorHTech.getDecibeles() >= media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                        blink = false;
                    } else {
                        blink = true;
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
                    }
                    Thread.sleep(250);
                    contador++;
                }

                while (consumidorHTech.getDecibeles() < media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 2");
                        blink = false;
                    } else {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 2\n...");
                        blink = true;
                    }
                    Thread.sleep(250);
                    contador++;
                }
                pito2 = consumidorHTech.getDecibeles();
                Thread.sleep(1000);
                while (consumidorHTech.getDecibeles() >= media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                        blink = false;
                    } else {
                        blink = true;
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
                    }
                    Thread.sleep(250);
                    contador++;
                }

                while (consumidorHTech.getDecibeles() < media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 3");
                        blink = false;
                    } else {
                        panel.getPanelMensaje().setText("ACCIONE LA BOCINA  # 3\n...");
                        blink = true;
                    }
                    Thread.sleep(250);
                    contador++;
                }
                pito3 = consumidorHTech.getDecibeles();
                Thread.sleep(1000);
                while (consumidorHTech.getDecibeles() >= media + UMBRAL_DECIBELES) {
                    if (blink && contador % 2 == 0) {
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA");
                        blink = false;
                    } else {
                        blink = true;
                        panel.getPanelMensaje().setText("DISMINUIR RUIDO MEDICION TOMADA....");
                    }
                    Thread.sleep(250);
                    contador++;
                }
                DialogoRPMTemp dlgRpmTemp = new DialogoRPMTemp(idHojaPrueba);
                JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(panel));
                dlg.getContentPane().add(dlgRpmTemp);
                dlg.setSize(500, 250);
                dlg.setResizable(false);
                dlg.setModal(true);
                dlg.setLocationRelativeTo(panel);
                dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dlg.setVisible(true);
                //dlgRpmTemp.setVisible(true);
                int numeroCilindros = dlgRpmTemp.getNumeroCilindros();
                String equipoMedicion = dlgRpmTemp.getEquipo();
                String metodoMedicion = dlgRpmTemp.getMetodoMedicion();
                boolean multiplicador = dlgRpmTemp.isUsaMultiplicador();
                boolean simulacion = dlgRpmTemp.simulacion;

                if (equipoMedicion.equals("banco")) {//banco o kit

                    //falta buscar el banco
                    panel.getPanelMensaje().setText("Buscando Banco....");
                    if (!buscarBanco()) {//configura la variable de instancia
                        JOptionPane.showMessageDialog(null, "Banco no encontrado en puertos seriales \n terminacion anormal");
                        liberarRecursos();
                        return;
                    }
                    blinker = Thread.currentThread();
                    panel.getPanelMensaje().setVisible(false);
                    panel.getPanelFiguras().setVisible(true);
                    Thread.sleep(10);
                    medicion = banco.obtenerDatos();
                    for (int i = 0; i < 10; i++) {
                        rpmFiltradas = filtroRPM(medicion.getValorRPM());
                        medicion = banco.obtenerDatos();
                    }
                    int ciclos = 0;
                    listaCiclo1 = new ArrayList<Double>();
                    listaCiclo2 = new ArrayList<Double>();
                    listaCiclo3 = new ArrayList<Double>();
                    while (ciclos < 3 && blinker != null) {

                        //mientras este por debajo de 2500 y por encima de 3700
                        medicion = banco.obtenerDatos();
                        if (medicion != null) {
                            rpmFiltradas = filtroRPM(medicion.getValorRPM());
                        }
                        contadorTemporizacion = 0;
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLUE);
                        while (rpmFiltradas < 2525 || rpmFiltradas > 3700 && blinker != null) {
                            Thread.sleep(100);
                            if (rpmFiltradas < 2525) {
                                panel.getMensaje().setText("ACELERE !!! t:" + contadorTemporizacion);
                            }
                            if (rpmFiltradas > 3700) {
                                panel.getMensaje().setText("DESAACELERE !!! t:" + contadorTemporizacion);
                            }
                            medicion = banco.obtenerDatos();
                            if (medicion != null) {
                                rpmFiltradas = filtroRPM(medicion.getValorRPM());
                            }
                            panel.getRadialTacometro().setValue(rpmFiltradas);
                        }
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        contadorTemporizacion = 0;
                        while ((rpmFiltradas > 2500 || rpmFiltradas < 3700) && contadorTemporizacion < 3 && blinker != null) {
                            Thread.sleep(100);
                            panel.getMensaje().setText("EN RANGO TOMANDO MEDICIONES");
                            medicion = banco.obtenerDatos();
                            rpmFiltradas = filtroRPM(medicion.getValorRPM());
                            panel.getRadialTacometro().setValue(rpmFiltradas);
                            if (ciclos == 0) {
                                listaCiclo1.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 1) {
                                listaCiclo2.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 2) {
                                listaCiclo3.add(consumidorHTech.getDecibeles());
                            }

                        }
                        if (contadorTemporizacion < 2) {
                            panel.getMensaje().setText("ERROR SOSTENGA DURANTE 2 S");
                            Thread.sleep(2000);
                            continue;
                        }
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        while (rpmFiltradas > 2526 && blinker != null) {
                            Thread.sleep(100);
                            panel.getMensaje().setText("DESACELERE!!!!! ");
                            medicion = banco.obtenerDatos();
                            if (medicion != null) {
                                rpmFiltradas = filtroRPM(medicion.getValorRPM());
                            }
                            if (ciclos == 0) {
                                listaCiclo1.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 1) {
                                listaCiclo2.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 2) {
                                listaCiclo3.add(consumidorHTech.getDecibeles());
                            }
                            panel.getRadialTacometro().setValue(rpmFiltradas);
                        }
                        ciclos++;
                    }//end while ciclos
                }//fin de medir con el banco
                else {//Medir con el kit de revoluciones
                    try {
                        iniciarKit(dlgRpmTemp.getCommPortIdentifier(), metodoMedicion, numeroCilindros);// configura las variables de clase para el kit capelec
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error al iniciar el kit" + e.getMessage());
                        liberarRecursos();
                        return;
                    }
                    panel.getPanelMensaje().setText("INICIANDO KIT");
                    capelec.setRadialRPM(panel.getRadialTacometro());
                    Thread.sleep(5000);
                    boolean efecto = true;
                    while (capelec.isInicializando() && blinker != null) {
                        if (efecto) {
                            panel.getPanelMensaje().setText("INICIANDO KIT ...");
                            efecto = !efecto;
                        } else {
                            panel.getPanelMensaje().setText("INICIANDO KIT");
                            efecto = !efecto;
                        }
                        Thread.sleep(1000);
                    }//Esta rutina  funciona bien... hasta el 18/04/2011
                    //Sin Embargo si es origen de errores cambiarlo por temporización fija
                    Thread.sleep(10);
                    panel.getPanelMensaje().setText("INICIALIZADO");//mostrar el dialogo para probar las rpms

                    //Mostrar un dialogo para probar la aceleracion
                    dialogReconfiguracion = new JDialogReconfiguracionKit(capelec, serialPortKit);
                    dialogReconfiguracion.setVisible(true);
                    panel.getPanelMensaje().setVisible(false);
                    panel.getPanelFiguras().setVisible(true);
                    Thread.sleep(10);
                    rpmKit = capelec.getRpm();
                    int ciclos = 0;
                    listaCiclo1 = new ArrayList<Double>();
                    listaCiclo2 = new ArrayList<Double>();
                    listaCiclo3 = new ArrayList<Double>();
                    while (ciclos < 3 && blinker != null) {
                        //mientras este por debajo de 2500 y por encima de 3700
                        rpmKit = capelec.getRpm();
                        contadorTemporizacion = 0;
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.BLUE);
                        while (rpmKit < 2525 || rpmKit > 3700 && blinker != null) {
                            Thread.sleep(100);
                            if (rpmKit < 2525) {
                                panel.getMensaje().setText("ACELERE !!! t:" + contadorTemporizacion);
                            }
                            if (rpmKit > 3700) {
                                panel.getMensaje().setText("DESAACELERE !!! t:" + contadorTemporizacion);
                            }
                            rpmKit = capelec.getRpm();
                            panel.getRadialTacometro().setValue(rpmKit);
                        }
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.GREEN);
                        rpmKit = capelec.getRpm();
                        contadorTemporizacion = 0;
                        while ((rpmKit > 2500 || rpmKit < 3700) && contadorTemporizacion < 3 && blinker != null) {
                            Thread.sleep(100);
                            panel.getMensaje().setText("EN RANGO TOMANDO MEDICIONES");
                            rpmKit = capelec.getRpm();
                            panel.getRadialTacometro().setValue(rpmKit);
                            if (ciclos == 0) {
                                listaCiclo1.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 1) {
                                listaCiclo2.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 2) {
                                listaCiclo3.add(consumidorHTech.getDecibeles());
                            }

                        }
                        if (contadorTemporizacion < 2) {
                            panel.getMensaje().setText("ERROR SOSTENGA DURANTE 2 S");
                            Thread.sleep(2000);
                            continue;
                        }
                        panel.getRadialTacometro().setBackgroundColor(BackgroundColor.RED);
                        rpmKit = capelec.getRpm();
                        while (rpmKit > 2526 && blinker != null) {
                            Thread.sleep(100);
                            panel.getMensaje().setText("DESACELERE!!!!! ");
                            rpmKit = capelec.getRpm();
                            if (ciclos == 0) {
                                listaCiclo1.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 1) {
                                listaCiclo2.add(consumidorHTech.getDecibeles());
                            }
                            if (ciclos == 2) {
                                listaCiclo3.add(consumidorHTech.getDecibeles());
                            }
                            panel.getRadialTacometro().setValue(rpmKit);
                        }
                        ciclos++;
                    }//end while ciclos

                }//fin de else medir con el kit
            }//fin else simulacion
            panel.getPanelFiguras().setVisible(false);
            panel.getPanelMensaje().setText("Prueba finalizada guardando datos");
            Thread.sleep(1500);
            if (!cancelacion) {
                procesarDatos();
                try {
                    boolean registro = verificarMedidas(maximo1, maximo2, maximo3, pito1, pito2);//Verifica si una medida es menor o igual a cero
                    if (!registro) {
                        JOptionPane.showMessageDialog(null, "Alguno o todos los datos son invalidos debe reiniciar la prueba?");
                        return;
                    }
                    registrarMedidas();
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(panel, "No se encuentra el driver de MySQL");
                    ex.printStackTrace(System.err);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Error insertando los datos en la base de datos");
                    ex.printStackTrace(System.err);
                } finally {
                    liberarRecursos();
                }//fin finally

            }//fin try de cancelacion
            else {
                Thread.currentThread().interrupt();
            }
        }//end try total
        catch (InterruptedException ie) {
            System.out.println("Hilo de la Prueba de Motos Terminado ");
            //String showInputDialog = JOptionPane.showInputDialog("comentario de aborto");
            String showInputDialog = "Sonometro deshabilitado";
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
            }//fin finally
        }//fin catch de cancelacion

    }//end of method run

    public void setIdPrueba(int idPrueba) {
        this.idPrueba = idPrueba;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdHojaPrueba(int idHojaPrueba) {
        this.idHojaPrueba = idHojaPrueba;
    }

    private void iniciarKit(CommPortIdentifier commPortIdentifier, String metodo, int numeroCilindros) throws Exception {
        //En que puerto serial esta el banco?
        String portName = commPortIdentifier.getName();
        SerialPort serialPortKit = connectKit(portName, 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, commPortIdentifier, metodo, numeroCilindros);

        if (serialPortKit != null) {
            System.out.println("Puerto Serial conectado");
        }

    }//end of method iniciarKit

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

            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (UnsupportedCommOperationException ucoe) {
                ucoe.printStackTrace();
            }
        }//end of while
        return false;//retorna falso si sale por aca
    }//end of method buscarBanco

    private void stop() {
        cancelacion = true;
        Thread tempBlinker = blinker;
        blinker = null;
        if (tempBlinker != null) {
            tempBlinker.interrupt();
        }
    }//end of method stop

    @Override
    public void actionPerformed(ActionEvent e) {
        cancelacion = true;
        this.stop();
    }//end of method actionPerformed

    private void inicializarPuertoSerial(String portName) throws Exception {
        serialPort = connect(portName, 2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (serialPort != null) {
            //textAreaRPM.append("Conexion con" + serialPort.getName());
            System.out.println("Puerto Serial conectado");
        }
        //capelecServicios.setPuertoSerial(serialPort);
        //buttonConectar.setEnabled(false);
    }//end of inicializarPuertoSerial

    private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception {

        SerialPort serialPort = null;
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;

                //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.setDTR(false);
                serialPort.setRTS(false);
                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
                //serialPort.notifyOnDataAvailable(true);
                lectorHTech = new LectorHTech(serialPort, bufferMed);
                consumidorHTech = new ConsumidorHtech(bufferMed, lectorHTech);
                threadProductor = new Thread(lectorHTech);
                threadProductor.start();
                threadConsumidor = new Thread(consumidorHTech);
                threadConsumidor.start();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    private void liberarRecursos() {
        if (lectorHTech != null) {
            lectorHTech.stop();
        }
        if (consumidorHTech != null) {
            consumidorHTech.stop();
        }
        if (serialPort != null) {
            serialPort.close();
        }
        if (banco != null) {
            banco.getPuertoSerial().close();
        }
        if (timer != null) {
            timer.stop();
        }
        if (productor != null) {
            productor.setTerminado(true);
        }
        if (capelec != null) {
            capelec.setTerminado(true);
        }
        if (serialPortKit != null) {
            serialPortKit.close();
        }
        panel.cerrar();
        //blinker.interrupt();
    }

    private void procesarDatos() {
        Collections.sort(listaCiclo1);
        Collections.sort(listaCiclo2);
        Collections.sort(listaCiclo3);
        if (listaCiclo1.size() > 0 && listaCiclo2.size() > 0 && listaCiclo3.size() > 0) {
            maximo1 = listaCiclo1.get(listaCiclo1.size() - 1);
            maximo2 = listaCiclo2.get(listaCiclo2.size() - 1);
            maximo3 = listaCiclo3.get(listaCiclo3.size() - 1);
            System.out.println("maximos " + maximo1 + "\n" + maximo2 + "\n" + maximo3);
            promedioRuido = maximo1 + maximo2 + maximo3;
            promedioRuido /= 3;
        }//end of size
        System.out.println("Promedio " + promedioRuido);
    }//end of method procesarDatos

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
        return rpmRetorno;
    }//end of method filtroRPM

    public static double mean(double[] p) {
        double sum = 0;  // sum of all the elements
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }//end method mean

    private void cargarParametros() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        //try retrieve data from file
        props.load(new FileInputStream("./propiedades.properties"));//TODO warining
        urljdbc = props.getProperty("urljdbc");
        puertoSonometro = props.getProperty("PuertoSonometro");
        System.out.println(urljdbc);
        System.out.println(puertoSonometro);
    }//end of method cargarUrl

    private void registrarMedidas() throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        System.out.println("Conectandose con:" + "jdbc:mysql://" + urljdbc + "/db_cda");
        conexion.setAutoCommit(false);
        //Crear objeto preparedStatement para realizar la consulta con la base de datos
        //String statementBorrar = ("DELETE  FROMmedidas WHERE TEST = ?");
        //PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
        //instruccionBorrar.setInt(1,idPrueba);
        //instruccionBorrar.executeUpdate();
        boolean registro = verificarMedidas(maximo1, maximo2, maximo3, pito1, pito2);//Verifica si una medida es menor o igual a cero
        if (!registro) {
            JOptionPane.showMessageDialog(null, "Alguno o todos los datos son invalidos debe reiniciar la prueba?");
            return;
        }

        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 7003);//Presion sonora tubo de escape ciclo uno
        instruccion.setDouble(2, maximo1);
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 7004);//Presion sonora tubo de escape ciclo dos
        instruccion.setDouble(2, maximo2);
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 7005);//Presion sonora tubo de escape ciclo tres
        instruccion.setDouble(2, maximo3);
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 7000);
        instruccion.setDouble(2, pito1);//Presion sonora de la bocina ciclo 1
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 7001);
        instruccion.setDouble(2, pito2);//Presion sonora de la bocina ciclo 2
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 7002);
        instruccion.setDouble(2, pito1);//Presion sonora de la bocina ciclo 3
        instruccion.setInt(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        //Un objeto ResultSet, almacena los datos de resultados de una <span class="IL_AD" id="IL_AD11">consulta</span>
        String statement2 = "UPDATE pruebas SET Finalizada='Y',Aprobada='Y',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
        instruccion2.setInt(1, idUsuario);
        instruccion2.setString(2, serialEquipo);
        instruccion2.setInt(3, idPrueba);
        instruccion2.executeUpdate();
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        Mensajes.messageDoneTime("Se ha Registrado la Prueba del Sonometro de una manera Exitosa ..¡",3);
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba, EstadoEventosSicov.FINALIZADO, "",serialEquipo);
        // FIN EVENTOS SICOV

    }//end of method registarMedidas

    public boolean verificarMedidas(double... medidas) {
        for (double d : medidas) {
            if (d <= 0) {
                return false;
            }
        }
        return true;
    }

    private void registrarCancelacion(String comentario) throws ClassNotFoundException, SQLException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        //Insertar las medidas dependiendo del numero de tiempos del motor
        String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='Y',Abortada='N',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
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

    private SerialPort connectKit(String portName, int baudRate, int dataBits, int stopBits, int parity, CommPortIdentifier portIdentifier, String metodoMedicion, int numeroCilindros) throws Exception {
        BufferStrings bufferMed = new BufferStrings();
        serialPortKit = null;
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPortKit = (SerialPort) commPort;
                serialPortKit.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                InputStream in = serialPortKit.getInputStream();
                OutputStream out = serialPortKit.getOutputStream();
                CapelecSerial capelecServicios = new CapelecSerial();
                capelecServicios.setOut(out);
                capelecServicios.enviarComandoNoParametros(CapelecSerial.PROGRAM_STOP);
                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
                //serialPort.notifyOnDataAvailable(true);
                productor = new ProductorCapelec(in, bufferMed);
                capelec = new ConsumidorCapelec(bufferMed, productor);
                switch (metodoMedicion) {
                    case "Bateria":
                        capelecServicios.medirUsandoBateria();
                        break;
                    case "Vibracion":
                        capelecServicios.medirUsandoVibracion();
                        break;
                }
                if (numeroCilindros != 4) {
                    capelecServicios.cambiarCilindros(numeroCilindros);
                }
                (new Thread(productor)).start();
                (new Thread(capelec)).start();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    class ListenerSimulacion implements ActionListener {

        Random random = new Random();

        @Override
        public void actionPerformed(ActionEvent e) {
            if (simuladasRalenti && !subidaAceleracion) {
                rpmSimuladas = velRalenti + (15 - random.nextInt(30));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (subidaAceleracion && !simuladasCrucero) {
                contadorSimulacion++;
                rpmSimuladas = ((velCrucero - velRalenti) * (1 - Math.exp(0 - (0.25 - random.nextDouble() * 0.03) * contadorSimulacion)) + (15 - random.nextInt(30))) + velRalenti;
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (simuladasCrucero && !simuladasRalenti && !subidaAceleracion) {
                rpmSimuladas = ((velCrucero) + (15 - random.nextInt(30)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if (bajadaAceleracion && !simuladasRalenti && !subidaAceleracion && !simuladasCrucero) {
                contadorSimulacion++;
                rpmSimuladas = (velRalenti + (velCrucero - velRalenti) * (Math.exp((-0.12 - random.nextDouble() * 0.01) * contadorSimulacion)) + (16 - random.nextInt(28)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }

        }

    }//end of method

}
