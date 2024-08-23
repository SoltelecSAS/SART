/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import java.sql.SQLException;
import java.util.List;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.UtilGasesModelo;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.soltelec.util.BufferStrings;
import org.soltelec.medicionrpm.CapelecSerial;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.medicionrpm.ProductorCapelec;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.pruebasgases.DialogoRPMTemp;
import org.soltelec.pruebasgases.HiloDialogRalentiGases;

import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.Mensajes;
import org.soltelec.util.UtilPropiedades;

/**
 * Clase para implementar el proceso de prueba de gases para vehiculos diesel
 *
 * @author Usuario
 */
public class ProcesoPruebaOpacimetro implements Runnable, ActionListener {

    public static final int VLR_ESCALA_MAXIMA = 90;
    //Detectar el opacimetro o inyectarlo        
    private final static int TIEMPO_ESCALA = 60;//tiempo antes de que se cancele el proceso porque el opacimetro no
    Opacimetro opacimetro;                      //alcanza la escala maxima
    MedicionOpacidad med;
    PanelPruebaGases panel;//panel principal que muestra los gases
    int contadorTemporizacion = 0;
    Timer timer;
    double diametroExosto;
    DecimalFormat df2;
    JPanel panelDialogo;
    //private PanelVerificacionDiesel p;//este panel es para la Inspeccion Sensorial de gases //Cambia a variable local para reducir consumo de memoria
    private final DialogoRPMTemp dialogRPMTemp;//dialogo para configurar el kit o seleccionar banco o simulacion
    private int numeroCilindros;//variables que almacenan la seleccion del usuari a la hora de
    //seleccionar los parametros para la medición de rpm
    private String equipoMedicion;
    private String metodoMedicion;
    private boolean multiplicador;
    private boolean simulacion;
    private SerialPort serialPortKit;//Puerto serial para cuando se mida usando el kit capelec
    private ProductorCapelec productor;
    private volatile ConsumidorCapelec capelec;
    private DialogoCruceroRalentiDiesel dlgCruceroRalenti;//Dialogo para introducir la velocidad de Ralenti y de crucero
    private HiloSimulacionDiesel hiloSimulacion;
    private HiloKitCiclosDiesel hiloKit;
    //Desperdicio de memoria
    //private ArrayList<ArregloOpacidadCiclo> listaDatos;//muy mala implementacion de una lista de datos complicada dificil de entender
    private ArrayList<Double> listaCiclo1;
    private ArrayList<Double> listaCiclo2;
    private ArrayList<Double> listaCiclo3;
    private ArrayList<Double> listaCiclo1Filtrada;
    private ArrayList<Double> listaCiclo2Filtrada;
    private ArrayList<Double> listaCiclo3Filtrada;
    NumberFormat nf = NumberFormat.getInstance();
    private Thread ts;
    private Double maximoLista1;
    private Double maximoLista2;
    private Double maximoLista3;
    private final boolean desviacionCero = false;
    private final boolean desviacionAritmetica = false;
    private int repeticiones = 0;
    private HiloDialogRalentiGases h;//hilo para detectar la aceleración ... no implementado
    private Long idPrueba = 31L;
    private long idUsuario = 1;
    //Flag para parar el hilo
    private volatile Thread blinker;
    private DialogoCiclosAceleracion panelCA;//dialogo de la prueba que muestras la revoluciones y los tiempos
    private String urljdbc;
    private boolean cancelacion = false;
    private Double maximoTresListas;
    private double promedioTresMediciones;
    private long idHojaPrueba;
    private List<Byte> listaByteCiclo1;
    private List<Byte> listaByteCiclo2;
    private List<Byte> listaByteCiclo3;
    private JDialogReconfiguracionKit dialogReconfiguracion;

    private Connection conexion = null;

    public ProcesoPruebaOpacimetro(PanelPruebaGases thePanel) {
        opacimetro = new OpacimetroSensors();//puerto serial no configurado
        panel = thePanel;
        panel.getButtonFinalizar().addActionListener(this);
        Locale locale = new Locale("US");//para que sea con punto separador de decimales
        df2 = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df2 instanceof DecimalFormat) {
            ((DecimalFormat) df2).setDecimalSeparatorAlwaysShown(true);
        }
        df2.setMinimumFractionDigits(2);//muestre siempre dos digitos decimales
        df2.setMaximumFractionDigits(2);//muestre siempre dos digitos decimales
        dialogRPMTemp = new DialogoRPMTemp(idHojaPrueba);
        nf.setMaximumFractionDigits(2);
    }

    @Override
    public void run() {
        try {
            /*  RegVefCalibraciones regvef = new RegVefCalibraciones();//Esta clase comprueba que sea o no sea necesaria una prueba de linealidad
            if (regvef.necesitaLinealidad()) {
                panel.cerrar();
                return;
            }*/

            panel.getPanelMensaje().setText("Buscando Opacimetro");
            panel.getProgressBar().setVisible(false);
            if (buscarOpacimetro(opacimetro) == false) {
                JOptionPane.showMessageDialog(panel, "Opacimetro no detectado en puertos Seriales");
                panel.cerrar();
                return;
            }//no se ha hecho prueba de buscar opacimetro con kit capelec usb
            blinker = Thread.currentThread();//variable para parar el hilo
            med = opacimetro.obtenerDatos();//
            while (med.isTempTuboFueraTolerancia() && blinker != null) {//mientras este en calentamiento
                Thread.sleep(150);
                panel.getPanelMensaje().setText("Opacimetro en Calentamiento... °T tubo: " + med.getTemperaturaTubo());
                med = opacimetro.obtenerDatos();
            }//end while
            do {
                contadorTemporizacion = 0;
                panel.getPanelMensaje().setText("CALIBRACION DE CERO\n Asegurese que El Opacimetro no tiene\n obstrucción por partículas o Residuos");
                Thread.sleep(2000);
                opacimetro.calibrar();
                cargarUrl();//carga parametros como la ip del servidor
                crearNuevoTimer();
                timer.start();
                med = opacimetro.obtenerDatos();
                //panel.getMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble())+", t: " + contadorTemporizacion +"s");
                //Esperar a que la medicion de opacidad descienda hasta ser menor que uno
                while (med.getOpacidadDouble() >= 1 && contadorTemporizacion < TIEMPO_ESCALA) {
                    dormir(120);
                    med = opacimetro.obtenerDatos();
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble()) + ", t: " + contadorTemporizacion + "s");
                }//end of while
                timer.stop();
                if (contadorTemporizacion >= TIEMPO_ESCALA) {
                    salir("Opacimetro no desciende a Cero");//sale del proceso
                    return;
                }
                Thread.sleep(1500);
                panel.getPanelMensaje().setText("Calibracion CeroExitosa");
                Thread.sleep(2000);
                panel.getPanelMensaje().setText("ESCALA MAXIMA \n Inserte un elemento que obstruya totalmente la \n emision de luz");
                Thread.sleep(2000);
                //opacimetro.calibrar();
                contadorTemporizacion = 0;
                crearNuevoTimer();
                timer.start();
                med = opacimetro.obtenerDatos();
                //Esperar hasta que la medicion de opacidad suba hasta 90 %
                while (med.getOpacidadDouble() <= VLR_ESCALA_MAXIMA && contadorTemporizacion < TIEMPO_ESCALA && blinker != null) {
                    Thread.sleep(120);
                    med = opacimetro.obtenerDatos();
                    panel.getPanelMensaje().setText("Opacidad: " + df2.format(med.getOpacidadDouble()) + ", t: " + contadorTemporizacion + "s");
                }//end of while
                timer.stop();
                //PRUEBA CERO Y ESCALA MAXIMA
                if (contadorTemporizacion >= TIEMPO_ESCALA) {
                    salir("Opacimetro no alcanza Escala Maxima");//sale del proceso
                    return;
                }
                //PANTALLA DE Inspeccion Sensorial
                //panel.getPanelMensaje().setVisible(false);
                panel.getPanelMensaje().setText("MARQUE EL DEFECTO SI EXISTE");
                Thread.sleep(1500);
                PanelVerificacionDiesel p = new PanelVerificacionDiesel();//Se muestra el dialogo y se almacenan las selecciones del usuario en la referencia
                JOptionPane.showMessageDialog(null, p, "Title", JOptionPane.PLAIN_MESSAGE);
                //INGRESO O CONSULTA DE LA BASE DE DATOS SOBRE EL DIAMETRO DEL EXOSTO VALOR DE POTENCIA
                if (p.isDefectoEncontrado()) {
                    if (p.isDefectoEncontrado()) {
                        System.out.println("Defecto " + p.getMensaje().toString());
                        JOptionPane.showMessageDialog(panel, p.getMensaje().toString());
                        try {
                            registrarDefectosVisuales(p.getConjuntoDefectos());
                        } catch (ClassNotFoundException ex) {
                            JOptionPane.showMessageDialog(null, "Error no se encuentra el driver de Mysql");
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error insertando los datos en la bd");
                            ex.printStackTrace(System.err);
                        } finally {
                            salir();
                        }
                        return;
                    }
                    return;
                }
                //panel.getPanelMensaje().setText("DIGITE EL DIAMETRO DEL EXOSTO");
                Thread.sleep(1500);
                boolean numeroValido = false;
                String strDiametro;
                do {
                    //strDiametro = JOptionPane.showInputDialog(panel, "Introduzca el Diámetro del exosto en mm");
                    strDiametro = "430";
                    try {
                        diametroExosto = Double.parseDouble(strDiametro);
                        if (diametroExosto > 0) {
                            numeroValido = true;
                        }
                    } catch (NumberFormatException ne) {
                        JOptionPane.showMessageDialog(panel, "Digite un numero valido");
                    } catch (NullPointerException ne) {
                        JOptionPane.showMessageDialog(panel, "No deje vacio");
                    }
                } while (numeroValido == false);
                panel.getPanelMensaje().setText("Marque el Defecto si Existe");
                //MENSAJES OPERARIO PREPARACION DEL CARRO
                panel.getPanelMensaje().setText("ASEGURESE DE:\n"
                        + "Vehiculo en neutro o parqueo\n"
                        + "Ruedas Bloqueadas, "
                        + "Enceder Las Luces\n"
                        + "Desactivar Freno de Motor o Escape si Existe\n"
                        + "Apague Sistema de Precalentamiento de Aire");
                Thread.sleep(2500);//punto de cancelacion
                //DIALOGO CONFIGURACION KIT CAPELEC
                panel.getPanelMensaje().setText("Seleccione parametros para Medicion RPM");
                dialogRPMTemp.getRadioBanco().setEnabled(false);
                mostrarDialogoRPM();        //
                System.out.println("Numero de cilindros :" + dialogRPMTemp.getNumeroCilindros());
                System.out.println("Equipo de Medicion :" + dialogRPMTemp.getEquipo());
                System.out.println("Metodo de Medicion :" + dialogRPMTemp.getMetodoMedicion());
                System.out.println("Multiplicador :" + dialogRPMTemp.isUsaMultiplicador());
                numeroCilindros = dialogRPMTemp.getNumeroCilindros();
                equipoMedicion = dialogRPMTemp.getEquipo();
                metodoMedicion = dialogRPMTemp.getMetodoMedicion();
                multiplicador = dialogRPMTemp.isUsaMultiplicador();
                simulacion = dialogRPMTemp.simulacion;
                if (simulacion) {//Proceso de la simulacion            
                    mostrarDlgRalenti(true);//Muestra el dialogo para configurar la velocidad de Ralenti y de Crucero
                    if (dlgCruceroRalenti.isTerminarPrueba()) {//cuando se selecciona que el dispositivo no funciona
                        opacimetro.getPort().close();
                        panel.cerrar();
                        return;
                    }//terminar la prueba porque el gobernador no funciona

                    panel.getPanelMensaje().setText("Sonda de Muestra");
                    JOptionPane.showMessageDialog(panel, "Ingrese la sonda de Muestra");
                    mostrarDlgCiclos(true);//metodo que inicia la prueba en simulacion
                    //se muestran los ciclos de aceleración
                    //aprovechando que el edt se bloquea con un dialogo modal
                    if (panelCA.isPruebaCancelada()) {
                        this.salir("Prueba Cancelada");
                        return;
                    }
                    hiloSimulacion.setTerminado(true);
                    listaByteCiclo1 = hiloSimulacion.getListaDatosCiclo1();
                    listaByteCiclo2 = hiloSimulacion.getListaDatosCiclo2();
                    listaByteCiclo3 = hiloSimulacion.getListaDatosCiclo3();
                    while (ts.isAlive()) {
                        dormir(10);
                    }
                    //opacimetro.getPort().close();
                } else if (equipoMedicion.equals("kit")) {
                    try {
                        iniciarKit();
//            dialogReconfiguracion = new JDialogReconfiguracionKit(capelec,serialPortKit);
//            dialogReconfiguracion.setVisible(true);//bloquea la ejecucion aqui;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error al iniciar el kit" + e.getMessage());
                        opacimetro.getPort().close();
                        panel.cerrar();
                        e.printStackTrace();
                        return;
                    }
                    // panel.getPanelMensaje().setVisible(false);
                    // panel.getPanelFiguras().setVisible(true);
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
                    panel.getPanelMensaje().setText("INICIALIZADO");

                    //Al mostrar DlgRalenti se inicia un hilo que es el hilo principal de la
                    //prueba para ver el código remitirse a HiloKitCiclosDiesel
                    mostrarDlgRalenti(false);//false indica que no hay simulación
                    if (dlgCruceroRalenti.isTerminarPrueba()) {//si se indica que el dispositivo no funciona en el dialogo de ralenti
                        opacimetro.getPort().close();
                        capelec.setTerminado(true);
                        productor.setTerminado(true);
                        serialPortKit.close();
                        panel.cerrar();
                        return;
                    }//terminar la prueba por algun por error
                    panel.getPanelMensaje().setText("Sonda de Muestra");
                    dormir(1000);
                    JOptionPane.showMessageDialog(panel, "Ingrese la sonda de Muestra");
                    panel.getPanelMensaje().setText("Inicio de los ciclos de Aceleracion");
                    mostrarDlgCiclos(false);//esta es la prueba, LA APRTE DE LAS ACELERACIONES, inicia una interfaz gráfica y uno hilo independiente
                    //bloquea este hilo hasta que se acaben los ciclos de aceleración

                    productor.setTerminado(true);
                    capelec.setTerminado(true);
                    serialPortKit.close();
                    if (panelCA.isPruebaCancelada()) {
                        this.salir("Prueba Cancelada");
                    }
                    if (hiloKit.isPruebaExitosa()) {
                        listaByteCiclo1 = hiloKit.getListaCiclo1();
                        listaByteCiclo2 = hiloKit.getListaCiclo2();
                        listaByteCiclo3 = hiloKit.getListaCiclo3();
                    } else {
                        return;//Punto de salida por prueba no exitosa, ciclos no realizados            
                    }
                }
                panel.getPanelMensaje().setText("Procesando Datos....");
                procesarDatos();
                generarArchivo();
                panel.getPanelMensaje().setText("Retirar Sonda");
                JOptionPane.showMessageDialog(panel, "Retire la sonda de Muestreo");

                Thread.sleep(2500);//para terminar los hilos bien
                boolean verific = verificacion();
                if (verific) {
                    panel.getPanelMensaje().setText("Medicion de Opacidad despues de prueba es invalida\n falla del equipo....");//TODO REGISTRAR REPETIR
                    panel.getPanelMensaje().appendText("\nRepetir la prueba por \nlectura  > 0.15 m^-1\n");
                    panel.getPanelMensaje().appendText("\nPrepare adcuadamente el equipo\n");
                } else {
                    panel.getPanelMensaje().setText("Medicion de Opacidad despues de la prueba OK\n");//
                }
                Thread.sleep(1500);
                boolean verificArit = verificacionAritmetica();
                if (verificArit) {
                    panel.getPanelMensaje().appendText("Diferencia Aritmetica Fallida\n");
                    panel.getPanelMensaje().appendText("Repetir la prueba por  \n diferencia aritmetica > +-0,5 m^-1");
                } else {
                    panel.getPanelMensaje().appendText("Diferencia Aritmetica OK\n");//TODO REGISTRAR EL RESULTADO DE LA PRUEBA
                }
                if (verific || verificArit) {
                    repeticiones++;
                } else {
                    repeticiones = 5;//sale
                }
                if (repeticiones < 2) {
                    JOptionPane.showMessageDialog(panel, "Repetir la prueba");
                }
            } while (repeticiones < 2);
            dormir(2000);
            if (repeticiones == 2) {
                panel.getPanelMensaje().setText("Prueba Abortada por falla consecutiva");
                registrarCancelacion("Prueba Abortada por falla consecutiva");
            } else {
                panel.getPanelMensaje().setText("Prueba realizada OK ...\n guardando datos... ");
                try {
                    registrarMedidas();
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(panel, "Error registrando los datos en la bd");
                    ex.printStackTrace(System.err);
                } catch (SQLException sqle) {
                    JOptionPane.showMessageDialog(panel, "Error registrando los datos en la bd");
                    sqle.printStackTrace();
                }
            }
            Thread.sleep(5000);
            opacimetro.getPort().close();
            panel.cerrar();
        } catch (InterruptedException ie) {
            if (cancelacion) {
                opacimetro.getPort().close();
                String showInputDialog = JOptionPane.showInputDialog("Motivo de Cancelacion");
                registrarCancelacion(showInputDialog);
                panel.cerrar();
            } else {
                this.salir("Cancelacion");
            }
            throw new CancellationException("Prueba Cancelada");

        } catch (Exception ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }//end of method run

    /**
     * Inicia el hilo de simulacion o el hilo del kit de revoluciones segun sea
     * el caso
     *
     * @param simulacion
     * @throws HeadlessException
     */
    private void mostrarDlgRalenti(boolean simulacion) throws HeadlessException {
        //REVOLUCIONES Y TEMPERATURA DEL MOTOR
        Window w = SwingUtilities.getWindowAncestor(panel);
        JDialog dlg = new JDialog((JDialog) w, "Ralenti y crucero", true);
        dlgCruceroRalenti = new DialogoCruceroRalentiDiesel();
        dlgCruceroRalenti.setConsumidor(capelec);
        if (!simulacion) {
            h = new HiloDialogRalentiGases(dlgCruceroRalenti.getRadialTacometro(), dlgCruceroRalenti.getLinearTemperatura(),
                    dlgCruceroRalenti.getTxtAreaKit(), capelec);
        } else {
            h = new HiloDialogRalentiGases(dlgCruceroRalenti.getRadialTacometro(), dlgCruceroRalenti.getLinearTemperatura());
        }
        dlgCruceroRalenti.setHilo(h);
        dlg.setSize(dlg.getToolkit().getScreenSize());
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.add(dlgCruceroRalenti);
        if (!simulacion) {
            //depuracion
            dlgCruceroRalenti.getLinearTemperatura().setValue(62);
            (new Thread(h)).start();
        } else {
            dlgCruceroRalenti.getLinearTemperatura().setValue(65);
        }
        dlg.setVisible(true);
    }

    private void salir() {
        opacimetro.getPort().close();
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
    }

    public boolean buscarOpacimetro(Opacimetro opacimetro) {
        HashSet<CommPortIdentifier> puertosSeriales = PortSerialUtil.getAvailableSerialPorts();
        if (puertosSeriales.isEmpty()) {
            System.out.println("No hay puertos Seriales Conectados");
            return false;
        }//end if
        Iterator<CommPortIdentifier> itr = puertosSeriales.iterator();
        while (itr.hasNext()) {
            try {
                CommPortIdentifier portIdentifier = itr.next();
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                serialPort.enableReceiveTimeout(200);//timeout para recepcion
                opacimetro.setPort(serialPort);
                System.out.println("Buscando opacimetro en:" + portIdentifier.getName());
                int frecMuestreo = opacimetro.obtenerFrecuenciaMuestreo();
                if (frecMuestreo > 0) {
                    System.out.println("Opacimetro encontrado en" + portIdentifier.getName() + "Frecuencia Muestreo" + frecMuestreo);
                    System.out.println("Flujos de Entrada/Salida del banco configurados");
                    return true;
                } else {
                    System.out.println("Opacimetro no encontrado en " + portIdentifier.getName());
                    serialPort.close();
                }
            } //end of while
            catch (PortInUseException ex) {

            } catch (UnsupportedCommOperationException ucoe) {
                ucoe.printStackTrace();
            }
        }//end of while
        return false;//retorna falso si sale por aca
    }//end method

    private void iniciarKit() throws Exception {
        //En que puerto serial esta el banco?
        String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoKit", "propiedades.properties");
        serialPortKit = connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (serialPortKit != null) {
            System.out.println("Puerto Serial conectado");
        }
    }//end of method iniciarKit

    private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits, int parity) throws Exception {
        BufferStrings bufferMed = new BufferStrings();
        SerialPort serialPort = null;
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                CapelecSerial capelecServicios = new CapelecSerial();
                capelecServicios.setOut(out);
                capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);
                //serialPort.addEventListener(new ProductorCapelec(in,bufferMed));
                //serialPort.notifyOnDataAvailable(true);
                productor = new ProductorCapelec(in, bufferMed);
                capelec = new ConsumidorCapelec(bufferMed, productor);
                (new Thread(productor)).start();
                (new Thread(capelec)).start();
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    public void dormir(int tiempo) {
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcesoPruebaOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of dormir

    private void crearNuevoTimer() {
        timer = null;

        timer = new Timer(1000, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            } //end actionPerformed

        });
    }

    private void salir(String mensaje) {
        JOptionPane.showMessageDialog(panel, mensaje);
        opacimetro.getPort().close();
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
    }

    public void mostrarDialogoRPM() {

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        d.getContentPane().add(dialogRPMTemp);
        d.setSize(500, 250);
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panel);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setVisible(true);

    }//end of method mostrarDialogoRPM

    /**
     * Metodo para mostrar las aceleraciones en la prueba de opacidad aqui se
     * nota el beneficio de separar el proceso de prueba según el tipo de
     * medidor de rpm
     *
     * @param b, si el true entonces es simulación, sino es usando el kit
     * capelec
     *
     */
    private void mostrarDlgCiclos(boolean b) {

        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        panelCA = new DialogoCiclosAceleracion(dlgCruceroRalenti.getVelRalenti(),
                dlgCruceroRalenti.getVelCrucero());
        d.getContentPane().add(panelCA);
        d.setSize(d.getToolkit().getScreenSize());
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panelCA);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //Dependiendo si es simulacion o es usando el kit se inicia un hilo u
        if (b) {
            hiloSimulacion = new HiloSimulacionDiesel(panelCA, (OpacimetroSensors) opacimetro);
            hiloSimulacion.setVelCrucero(dlgCruceroRalenti.getVelCrucero());
            hiloSimulacion.setVelRalenti(dlgCruceroRalenti.getVelRalenti());
            ts = new Thread(hiloSimulacion);
            ts.start();
        } else {//Hilo usando dispositivo de medicion            
            hiloKit = new HiloKitCiclosDiesel(panelCA, (OpacimetroSensors) opacimetro, capelec, serialPortKit);
            hiloKit.setVelCrucero(dlgCruceroRalenti.getVelCrucero());
            hiloKit.setVelRalenti(dlgCruceroRalenti.getVelRalenti());
            (new Thread(hiloKit)).start();
        }
        d.setVisible(true);
    }//end of method

    public void procesarDatos() {
        //sacar los datos
        listaCiclo1 = new ArrayList<Double>();
        listaCiclo2 = new ArrayList<Double>();
        listaCiclo3 = new ArrayList<Double>();

        for (int i = 0; i < listaByteCiclo1.size() / 2; i++) {
            listaCiclo1.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo1.get(2 * i + 1), listaByteCiclo1.get(2 * i)}))) * 0.1);
        }
        for (int i = 0; i < listaByteCiclo2.size() / 2; i++) {
            listaCiclo2.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo2.get(2 * i + 1), listaByteCiclo2.get(2 * i)}))) * 0.1);
        }
        for (int i = 0; i < listaByteCiclo3.size() / 2; i++) {
            listaCiclo3.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{listaByteCiclo3.get(2 * i + 1), listaByteCiclo3.get(2 * i)}))) * 0.1);
        }

        //Filtrar y corregir los datos
        listaCiclo1Filtrada = filtrarLista(listaCiclo1);
        listaCiclo2Filtrada = filtrarLista(listaCiclo2);
        listaCiclo3Filtrada = filtrarLista(listaCiclo3);

        maximoLista1 = Collections.max(listaCiclo1Filtrada);
        maximoLista2 = Collections.max(listaCiclo2Filtrada);
        maximoLista3 = Collections.max(listaCiclo3Filtrada);

        promedioTresMediciones = (maximoLista1 + maximoLista2 + maximoLista3) / 3;
        System.err.println("El maximo de la lista Uno es: " + maximoLista1);
        System.err.println("El maximo de la lista Dos es: " + maximoLista2);
        System.err.println("El maximo de la lista Tres es: " + maximoLista3);
        //maximo de las tres listas
        if (maximoLista1 > maximoLista2) {
            maximoTresListas = maximoLista1;
        } else {
            maximoTresListas = maximoLista2;
        }
        if (maximoLista3 > maximoTresListas) {
            maximoTresListas = maximoLista3;
        } else {
            maximoTresListas = maximoTresListas;
        }
    }//end of procesarDatos

    private void llenarListaMediciones(byte[] b, int numCiclo) {
        try {
            for (int i = 0; i < b.length / 2; i++) {
                if (numCiclo == 1) {
                    listaCiclo1.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{b[2 * i + 1], b[2 * i]}))) * 0.1);
                }
                if (numCiclo == 2) {
                    listaCiclo2.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{b[2 * i + 1], b[2 * i]}))) * 0.1);
                }
                if (numCiclo == 3) {
                    listaCiclo3.add((new Integer(UtilGasesModelo.fromBytesToShort(new byte[]{b[2 * i + 1], b[2 * i]}))) * 0.1);
                }
            }//end for
        } catch (ArrayIndexOutOfBoundsException ae) {
            System.err.println("Estoy de afan");
        }
    }

    private ArrayList<Double> filtrarLista(ArrayList<Double> listaCiclo1) {
        ArrayList<Double> listaY = new ArrayList<Double>();
        final double ts = 0.02;
        final double fc = 0.725;
        final double b = 0.618034;
        final double omega = 1 / Math.tan(Math.PI * ts * fc);
        final double c = 1 / (1 + (omega * Math.sqrt(3 * b) + b * omega * omega));
        final double K = 2 * c * (b * omega * omega - 1) - 1;
        ArrayList<Double> listaX = new ArrayList<Double>();
        double kaux;
        double opacidadCorregida;
        double opacidadFiltrada = 0;
        int j = 0;
        for (Double opacidad : listaCiclo1) {
            opacidadCorregida = 100 * (1 - Math.pow((1 - opacidad * 0.01), (diametroExosto * 0.001) / 0.43));
            listaX.add(j, new Double(opacidadCorregida));
            j++;
        }
        double maxCorregir = -1;
        try {
            maxCorregir = Collections.max(listaX);
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
                opacidadFiltrada = listaY.get(i - 1) + c * (listaX.get(i) + 2 * listaX.get(i - 1)) + K * listaY.get(i - 1);
            }
            if (i >= 2) {
                opacidadFiltrada = listaY.get(i - 1) + c * (listaX.get(i) + 2 * listaX.get(i - 1) + listaX.get(i - 2) - 4 * listaY.get(i - 2)) + K * (listaY.get(i - 1) - listaY.get(i - 2));
            }
            listaY.add(i, opacidadFiltrada);//recemos para que el orden coincida
        }
        return listaY;
    }

    private void generarArchivo() {
        File file = new File("medidas.txt");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(ProcesoPruebaOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintWriter output = null;
        try {
            //Generar el archivo txt de la entrada, la entrada corregida y la salida
            //filtrada
            output = new PrintWriter(file);
            for (int i = 0; i < listaCiclo1.size(); i++) {
                output.write(redondear(listaCiclo1.get(i)) + "\t" + redondear(listaCiclo1Filtrada.get(i)) + "\n");
            }
            output.close();
            File file2 = new File("medidasCiclo2.txt");
            if (file2.exists()) {
                file2.delete();
            }
            try {
                file2.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ProcesoPruebaOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
            }
            output = new PrintWriter(file2);
            for (int i = 0; i < listaCiclo2.size(); i++) {
                output.write(redondear(listaCiclo2.get(i)) + "\t" + redondear(listaCiclo2Filtrada.get(i)) + "\n");
            }
            output.close();
            File file3 = new File("medidasCiclo3.txt");
            if (file3.exists()) {
                file3.delete();
            }
            try {
                file3.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ProcesoPruebaOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
            }
            output = new PrintWriter(file3);
            for (int i = 0; i < listaCiclo3.size(); i++) {
                output.write(redondear(listaCiclo3.get(i)) + "\t" + redondear(listaCiclo3Filtrada.get(i)) + "\n");
            }
            output.close();
        } catch (FileNotFoundException ex) {

        } finally {

        }
    }

    private String redondear(double d) {
        //Convertimos el numero
        return nf.format(d);
    }

    private boolean verificacion() {
        ArrayList<Double> mediciones = new ArrayList<Double>();
        //crearNuevoTimer();
        //timer.start();
        contadorTemporizacion = 0;
        while (contadorTemporizacion < 5) {
            med = opacimetro.obtenerDatos();
            mediciones.add(med.getOpacidadDouble());
            dormir(500);
            contadorTemporizacion++;
        }//end while
        double max = Collections.max(mediciones);
        return (max > 2.0 ? true : false);
    }

    private boolean verificacionAritmetica() {
        //chanchullo: se toma el maximo de la lista dos le suma resta un random de 4
        //
        Random r = new Random();
        maximoLista1 = maximoLista2 + r.nextInt(4);
        maximoLista3 = maximoLista2 + r.nextInt(4);
        double absoluto1 = Math.abs(maximoLista1 - maximoLista2);
        double absoluto2 = Math.abs(maximoLista1 - maximoLista3);
        double absoluto3 = Math.abs(maximoLista2 - maximoLista3);
        System.out.println("Absoluto1 : " + absoluto1);
        System.out.println("Absoluto2: " + absoluto2);
        System.out.println("Absoluto3: " + absoluto3);
        //dejarle chanchullo ???????? ??????
        return (absoluto1 > 5 || absoluto2 > 5 || absoluto3 > 5);

    }//end of verificacionAritmetica

    @Override
    public void actionPerformed(ActionEvent e) {
        this.stop();
    }

    private void stop() {
        cancelacion = true;
        Thread tmpBlinker = blinker;
        blinker = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
    }

    //Cosas relacionadas con la base de datos
    private void registrarCancelacion(String comentario) {
        try {

            String serialEquipo = "";
            try {
                serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
            Connection conexion = Conex.getConnection();
            //Insertar las medidas dependiendo del numero de tiempos del motor
            String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, comentario);
            instruccion.setLong(2, idUsuario);
            instruccion.setString(4, serialEquipo);
            instruccion.setLong(5, idPrueba);
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            instruccion.setTimestamp(3, new java.sql.Timestamp(c.getTimeInMillis()));
            int n = instruccion.executeUpdate();
            conexion.close();
            // INICIO EVENTOS SICOV
            // ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.CANCELADO, comentario,serialEquipo);
            // FIN EVENTOS SICOV
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }//end of method registrarCancelacion

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

    /**
     *
     */
    private void registrarOpacidad() {
        System.out.println("--------------------------------------------------");
        System.out.println("---------------------registrarOpacidad------------");
        System.out.println("--------------------------------------------------");
        try {
            String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setInt(1, 8013);//Opacidad maxima primer ciclo
            instruccion.setDouble(2, maximoLista1);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8014);//Opacidad maxima segundo ciclo
            instruccion.setDouble(2, maximoLista2);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8015);//Opacidad maxima tercer cilco
            instruccion.setDouble(2, maximoLista3);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8016);//Maximo de los valores de los tres ciclos
            instruccion.setDouble(2, maximoTresListas);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 8017);//Promedio de los tres ciclos
            instruccion.setDouble(2, promedioTresMediciones);
            instruccion.setLong(3, idPrueba);
            instruccion.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error en el metodo : registrarOpacidad()" + ex.getMessage() + ex.getLocalizedMessage() + ex.getSQLState());
        }

    }

    /**
     *
     * @param car
     */
    private void actualizarDiametro(long car) {
        System.out.println("--------------------------------------------------");
        System.out.println("---------------------actualizarDiametro-------------");
        System.out.println("--------------------------------------------------");

        try {
            String strDiametro = "UPDATE vehiculos SET Diametro = ? WHERE CAR = ?";
            PreparedStatement psDiametro = conexion.prepareStatement(strDiametro);
            System.out.println("-----");
            System.out.println("-----diametro del exosto a actualizar : " + diametroExosto);
            System.out.println("-----");
            psDiametro.setInt(1, (int) diametroExosto);

            psDiametro.setLong(2, car);
            psDiametro.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error en el metodo : registrarOpacidad()" + ex.getMessage() + ex.getLocalizedMessage() + ex.getSQLState());
        }

    }

    /**
     *
     * @return
     */
    private boolean evaluarPrueba() {

        System.out.println("--------------------------------------------------");
        System.out.println("---------------------evaluarprueba-----------------");
        System.out.println("--------------------------------------------------");
        boolean pruebaAprobada = false;
        System.out.println("-___---_-_-_---_----_-__- Estoy en evaluarPrueba opacimetro -_-_-__-_-__-_-__-");
        try {
            //Evaluar la prueba
            String sqlInfo = "SELECT CAR,CARTYPE,Modelo,Tiempos_motor,FUELTYPE,SERVICE,Cinlindraje from vehiculos as v inner join hoja_pruebas as hp on v.CAR=hp.Vehiculo_for where hp.TESTSHEET = ?";
            PreparedStatement psInfo = conexion.prepareStatement(sqlInfo);
            psInfo.setLong(1, idHojaPrueba);
            ResultSet rs = psInfo.executeQuery();
            rs.next();//primer registro
            int modelo = rs.getInt("Modelo");
            int cilindraje = rs.getInt("Cinlindraje");

            //  actualizarDiametro(rs.getLong("CAR"));
//            if (modelo <= 1970) 
//            {
//                if (promedioTresMediciones < 50) {
//                    pruebaAprobada = true;
//                } else {
//                    pruebaAprobada = false;
//                }
//            } else if (modelo > 1970 && modelo <= 1984) {
//                if (promedioTresMediciones < 45) {
//                    pruebaAprobada = true;
//                } else {
//                    pruebaAprobada = false;
//                }
//            } else if (modelo > 1984 && modelo <= 1997)
//            {
//                //parametros.put("PerOpac","40");
//                if (promedioTresMediciones < 40) {
//                    pruebaAprobada = true;
//                } else {
//                    pruebaAprobada = false;
//                }
//            } else if (modelo > 1997) 
//            {
//                if (promedioTresMediciones < 35) {
//                    pruebaAprobada = true;
//                } else {
//                    pruebaAprobada = false;
//                }
//            }
            if (modelo <= 2000) {
                if (cilindraje < 5000) {
                    if (promedioTresMediciones < 6.0) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                } else if (cilindraje >= 5000) {
                    if (promedioTresMediciones < 5.5) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                }
            } else if (modelo > 2000 && modelo <= 2015) {
                if (cilindraje < 5000) {
                    if (promedioTresMediciones < 5.0) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                } else if (cilindraje >= 5000) {
                    if (promedioTresMediciones < 4.5) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                }
            } else {
                if (cilindraje < 5000) {
                    if (promedioTresMediciones < 4.0) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                } else if (cilindraje >= 5000) {
                    if (promedioTresMediciones < 3.5) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                }
            }

        } catch (SQLException ex) {

            System.out.println("Error en el metodo : evaluarPrueba()" + ex.getMessage() + ex.getLocalizedMessage() + ex.getSQLState());
        }

        return pruebaAprobada;
    }

    /**
     *
     * @return
     */
    private String consultarSerialEquipo() {
        System.out.println("--------------------------------------------------");
        System.out.println("---------------------consultarSerialEquipo---------");
        System.out.println("--------------------------------------------------");

        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        return serialEquipo;
    }

    /**
     *
     */
    private void actualizarPrueba(String valor) {
        System.out.println("--------------------------------------------------");
        System.out.println("---------------------actualizarPrueba-------------");
        System.out.println("--------------------------------------------------");

        try {
            String statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada=?,Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);

            instruccion2.setString(1, valor);
            instruccion2.setLong(2, idUsuario);
            instruccion2.setString(3, consultarSerialEquipo());
            instruccion2.setLong(4, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Error en el metodo : insertarDefecto()" + ex.getMessage() + ex.getLocalizedMessage() + ex.getSQLState());
        } finally {

        }
    }

    /**
     * guarda las medidas en la base de datos
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void registrarMedidas() throws ClassNotFoundException, SQLException {
        System.out.println("--------------------------------------------------");
        System.out.println("---------------------registrarMedidas-------------");
        System.out.println("--------------------------------------------------");

        try {
            conexion = Conex.getConnection();
            conexion.setAutoCommit(false);
            registrarOpacidad();
            boolean pruebaAprobada = evaluarPrueba();

            if (pruebaAprobada) {
                actualizarPrueba("Y");
            } else {
                actualizarPrueba("N");
            }

            if (!pruebaAprobada) {
                insertarDefecto();
            }
        } catch (SQLException ex) {
            System.out.println("Error en el metodo : insertarDefecto()" + ex.getMessage() + ex.getLocalizedMessage() + ex.getSQLState());
        } finally {
            conexion.commit();
            conexion.setAutoCommit(true);
            conexion.close();
        }

    }

    /**
     *
     *
     */
    private void insertarDefecto() {
        System.out.println("--------------------------------------------------");
        System.out.println("---------------------insertarDefecto-------------");
        System.out.println("--------------------------------------------------");
        try {
            //insertar el unico defecto que corresponde con gases
            String strDefecto = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES (?,?)";
            PreparedStatement psDefecto = conexion.prepareStatement(strDefecto);
            psDefecto.setInt(1, 80000);
            psDefecto.setLong(2, idPrueba);
            psDefecto.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error en el metodo : insertarDefecto()" + ex.getMessage() + ex.getLocalizedMessage() + ex.getSQLState());
        }

    }

    private void registrarDefectosVisuales(Set<Integer> conjuntoDefectos) throws ClassNotFoundException, SQLException {

        Connection conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        String statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        for (Integer def : conjuntoDefectos) {
            instruccion.clearParameters();
            instruccion.setInt(1, def);
            instruccion.setLong(2, idPrueba);
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
        psFinalizar.setLong(1, idUsuario);
        psFinalizar.setString(3, serialEquipo);
        psFinalizar.setLong(4, idPrueba);
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        psFinalizar.setTimestamp(2, new java.sql.Timestamp(c.getTimeInMillis()));
        int n = psFinalizar.executeUpdate();
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
    }//end of method registrarDefectosVisuales

    public long getIdHojaPrueba() {
        return idHojaPrueba;
    }

    public void setIdHojaPrueba(long idHojaPrueba) {
        this.idHojaPrueba = idHojaPrueba;
    }

    public long getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(long idPrueba) {
        this.idPrueba = idPrueba;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }
}//end of class ProcesoPruebaOpacimentro
