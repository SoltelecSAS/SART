/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgIntegradoLiviano.java
 *
 * Created on 19/12/2011, 04:22:07 PMa
 */
package vistas;

import Utilidades.CMensajes;
import Utilidades.UtilPropiedades;
import Utilidades.Utilidades2;

import com.soltelec.loginadministrador.UtilLogin;
import com.soltelec.modulopuc.utilidades.Mensajes;
import dao.PruebaDefaultDAO;
import dao.PruebasDAO;
import excepciones.NoPersistException;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import modelo.Desviacion;
import modelo.Frenos;
import modelo.Suspension;
import org.jdesktop.swingx.JXLoginPane;

/**
 *
 * @author Gerencia TIC
 */
public class DlgIntegradoLiviano extends javax.swing.JDialog implements ActionListener {

    private double factorescala;
    //private String CalificacionPrueba;
    private double permisiblepruebadesv1;  //Valor permisible para la desviación en el primer eje
    private double permisiblepruebadesv2;  //Valor permisible para la desviación en los demas ejes
    private double permisiblepruebasusp;   //Valor permisible para la suspensión
    private double permisiblepruebafren1;  //Valor permisible para la eficacia total de frenos
    private double permisiblepruebafren2;  //Valor permisible para el freno de mano
    private double permisiblepruebafren3;  //Valor permisible para el desequilibrio tipo A
    private double permisiblepruebafren4;  //Valor permisible para el desequilibrio tipo B
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private boolean enablereg = false; //Habilitación de registro de medidas en el servidor
    private boolean enablebackup = false; //Habilitación de generar backup del registro de medidas
    private boolean enablehwdesv, enablehwsusp, enablehwfren;  //Habilitacion por parte
    private String ordenPruebas = "DSF";
    //del banco de las pruebas a realizar
    private boolean enableswdesv, enableswsusp, enableswfren;  //Habilitacion por parte
    //del servidor de las pruebas a realizar
    private boolean pista_mixta;    //Selección de pista mixta o liviana
    private boolean error_config;   //Indicador de error en el archivo de configuracion
    //para no apertura del dialogo
    private final byte[] buffer = new byte[6000];
    private byte data;
    private int j = 0, i = 0, len = -1, partealta, numtimer1 = 0, t = 0;
    private final int[] ComandoRecibido = new int[9];
    private BufferedReader config;
    private BufferedWriter regdatosd1;
    private BufferedWriter regdatospd1;
    private BufferedWriter regdatospi1;
    private BufferedWriter regdatosfvd1;
    private BufferedWriter regdatosfvi1;
    private BufferedWriter regdatosffd1;
    private BufferedWriter regdatosffi1;
    private BufferedWriter regdatosffda;
    private BufferedWriter regdatosd2;
    private BufferedWriter regdatospd2;
    private BufferedWriter regdatospi2;
    private BufferedWriter regdatosfvd2;
    private BufferedWriter regdatosfvi2;
    public String FrenoInst;
    private BufferedWriter regdatosffd2;
    private BufferedWriter regdatosffi2;
    private BufferedWriter regdatosffia;
    private boolean tecladoactivado = false;
    private final List<Integer> Datos1 = new ArrayList<>(); //Datos correspondientes a la fuerza de frenado derecha
    private final List<Integer> Datos2 = new ArrayList<>(); //Datos correspondientes a la fuerza de frenado izquierda
    private final List<Integer> Datos3 = new ArrayList<>(); //Datos correspondientes al peso derecho
    private final List<Integer> Datos4 = new ArrayList<>(); //Datos correspondientes al peso izquierdo
    private final List<Integer> Datos5 = new ArrayList<>(); //Datos correspondientes a la velocidad del eje derecho
    private final List<Integer> Datos6 = new ArrayList<>(); //Datos correspondientes a la velocidad del eje izquierdo
    private final List<Integer> Datos7 = new ArrayList<>(); //Datos correspondientes a la desviacion

    private List<Double> Datosfil1 = new ArrayList<>();
    //Datos correspondientes al peso derecho filtrado
    private List<Double> Datosfil2 = new ArrayList<>(); //Datos correspondientes al peso izquierdo filtrado

    private double valcalcero1 = 0, valcalcero2 = 0, valcalcero3 = 0, valcalcero4 = 0, valcalcero5 = 0, valcalcero6 = 0, valcalcero7 = 0, maxCeroDesv = 0;
    //variables correspondientes a la calibracion de cero de los sensores de peso derecho e izquierdo,
    //fuerza de frenado derecha e izquierda, velocidad del eje derecho e izquierdo
    //y desviacion repectivamente
    private byte pasomeddesv = 0, numpasos, pasoactual = 0;

    private byte salidamotorderecho, salidamotorizquierdo;
    private final List<Double> pesosd = new ArrayList<>(); //Valores de peso derecho de cada eje
    private final List<Double> pesosi = new ArrayList<>(); //Valores de peso izquierdo de cada eje
    private final List<Double> fuerzasvd = new ArrayList<>(); //Valores de las fuerzas verticales derechas de cada eje
    private final List<Double> fuerzasvi = new ArrayList<>(); //Valores de las fuerzas verticales izquierdas de cada eje
    private final List<Double> fuerzasfd = new ArrayList<>(); //Valores de las fuerzas de frenado derechas de cada eje
    private final List<Double> fuerzasfi = new ArrayList<>(); //Valores de las fuerzas de frenado izquierdas de cada eje
    private final List<Double> desviaciones = new ArrayList<>(); //Valores de desviacion de cada eje    
    private double spanfd, spanfi, spanpd, spanpi, spanvd, spanvi, spand;  //valores de span de los canales obtenidos de la calibración
    private double spanpdTD, spanpdTI, spanpdDD, spanpdDI;  //valores de span de los pesos dinamicos 
    private boolean canal0 = false, canal1 = false;
    private int tiempo_paso = 1;
    private byte salidaalta, salidabaja;
    private double anchoplacadesv;
    private int aplcSenDesv;
    private int teporizadorInercia;
    private int numeroejes = 2, ejemedido = 1, tiempomensajes, numtimerautomatico2 = 0;
    //private boolean ladomedido=true; //true=derecho false=izquierdo
    private double velminimad, velminimai, umbral_velo, umbralPeso;
    private Principal hiloPrincipal = new Principal();
    private boolean frenmano = false; //Indica si se esta haciendo la prueba del freno de mano
    private boolean ensenianza = false;
    private float factor_desq;
    private String tipoPista;
    private final double[] filtro = {0.0091, 0.0131, 0.0245, 0.0416, 0.0619, 0.0821, 0.0993, 0.1108,
        0.1149, 0.1108, 0.0993, 0.0821, 0.0619, 0.0416, 0.0245, 0.0131, 0.0091};
    private String motivoCancelacion;
    private int idPruebadesv = 4, idPruebasusp = 6, idPruebafren = 5;
    private int idUsuario = 1;
    private int resolMin = 1;
    private int resolMax = 1;
    private boolean repetirPrueba;
    ImageIcon imageOn = null;
    ImageIcon imageOff = null;
    boolean aplicFreAux = false;
    private final List<Double> fuerzasfdAux = new ArrayList<>(); //Valores de las fuerzas de frenado aux derechas de cada eje
    private final List<Double> fuerzasfiAux = new ArrayList<>(); //Valores de las fuerzas de frenado aux  izquierdas de cada 
    public static String tramaAuditoria = "";
    public static String tramaAuditoriaSusp = "";
    public static String tramaAuditoriaDesv = "";
    private int aplicTrans = 1;
    private String ipEquipo;
    private String tipoVehiculo;
    public static String Placa;
    public static String NombreUsr;
    public static int activarFlag = 0;
    public static boolean activarFlagFrenos;
    private int ejesDondeTieneFrenoMano = 0; // 0=Ninguno 1=Eje1, 2=Eje2, 3=Ambos ejes
    private Double offsetCeroFuerzaDer = 0.0, offsetCeroFuerzaIzq = 0.0, offsetCeroPesoDer = 0.0, offsetCeroPesoIzq = 0.0;
    private String[] ruedasOEjes = {"1 ", "2 ", "3 ", "4 "};
    private String ejeORueda = "EJE "; 
    private long tiempoFrenado = 0L;
    private long tiempoApagarContactores = 0L;
    private boolean activarPresencia = false; // Indica si se debe activar la presencia desde el software
    private boolean activarUmbralPeso = false; // Indica si se debe activar el umbral de peso desde el software

    private DlgIntegradoLiviano(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize(this.getToolkit().getScreenSize());
        dialogCancelacion.setLocationRelativeTo(null);
        this.setTitle("SART 1.7.3 MOD D.F.S. PARA LIVIANO");
        umbralPeso = 0;
        spanfd = 0;
        spanfi = 0;
        spanpd = 0;
        spanpi = 0;
        spanvd = 0;
        spanvi = 0;
        spand = 0;
        spanpdTD = 0;
        spanpdTI = 0;
        spanpdDD = 0;
        spanpdDI = 0;
        configuracion();
        timerempezar.setRepeats(false);
        if (!error_config) {
            timerempezar.start();
        } else {
            System.out.println("Disculpe; debo Abortar la prueba por error en el archivo de configuración");
        }
    }

    public DlgIntegradoLiviano(java.awt.Frame parent, int idPruebadesv, int idPruebasusp, int idPruebafren, int idUsuario, int idHojaPrueba, Boolean esEnsenanza, int aplicTrans, String ipEquipo, String tipoVehiculo, String Placa, String NombreUsr, int frenoMano) {
        
        this(parent, true);
        this.idPruebadesv = idPruebadesv;
        this.idPruebasusp = idPruebasusp;
        
        this.idPruebafren = idPruebafren;
        this.idUsuario = idUsuario;
        this.ensenianza = esEnsenanza;
        enableswdesv = idPruebadesv > 0;
        enableswfren = idPruebafren > 0;
        enableswsusp = idPruebasusp > 0;
        System.out.println("-------------------------------------------------------------");
        System.out.println("-------------Id_prueba_desv: "+idPruebadesv+"------------------");
        System.out.println("-------------id_prueba_frenos: "+idPruebafren+"----------------");
        System.out.println("-------------idPruebaSusp: "+idPruebasusp+"--------------------");
        System.out.println("-------------------------------------------------------------");
        this.aplicTrans = aplicTrans;
        this.ipEquipo = ipEquipo;
        this.tipoVehiculo = tipoVehiculo;
        this.Placa = Placa;
        this.NombreUsr = NombreUsr;
        offsetCeroFuerzaDer = Utilidades2.leerDoubleDesdeArchivo("calibracion.properties", "offsetceroder");
        offsetCeroFuerzaIzq = Utilidades2.leerDoubleDesdeArchivo("calibracion.properties", "offsetceroizq");
        offsetCeroPesoDer = Utilidades2.leerDoubleDesdeArchivo("calibracion.properties", "offsetCeroPesoDer");
        offsetCeroPesoIzq = Utilidades2.leerDoubleDesdeArchivo("calibracion.properties", "offsetCeroPesoIzq");

        activarPresencia = Utilidades2.leerBooleanDesdeArchivo("calibracion.properties", "activarPresenciaDesdeSoftware");
        activarUmbralPeso = Utilidades2.leerBooleanDesdeArchivo("calibracion.properties", "activarUmbralPesoDesdeSoftware");

        
        //solo funciona para cuatrimotos pequeñas, motocarros y ciclomotores lo siguiente
        tiempoFrenado =  Utilidades2.leerLongDesdeArchivo("calibracion.properties", "tiempoFrenado"); 
        tiempoApagarContactores = Utilidades2.leerLongDesdeArchivo("calibracion.properties", "tiempoApagarContactores");

        System.out.println("Valores desde calibracion.properties: ");
        System.out.println("offsetCeroFuerzaDer: " + offsetCeroFuerzaDer); 
        System.out.println("offsetCeroFuerzaIzq: " + offsetCeroFuerzaIzq);
        System.out.println("offsetCeroPesoDer: " + offsetCeroPesoDer);
        System.out.println("offsetCeroPesoIzq: " + offsetCeroPesoIzq);
        System.out.println("tiempoFrenado: " + tiempoFrenado);
        System.out.println("tiempoApagarContactores: " + tiempoApagarContactores);

        if(tipoVehiculo.equalsIgnoreCase("CUATRIMOTOP")){
            numeroejes = 4;
            ejeORueda = "RUEDA ";
            ruedasOEjes[0] = "DIzq ";
            ruedasOEjes[1] = "TIzq ";
            ruedasOEjes[2] = "DDer ";
            ruedasOEjes[3] = "TDer ";
        }
        this.setTitle("SART 1.7.3 MOD D.F.S. PARA "+tipoVehiculo.toLowerCase());
        LabelPrueba.setText("PRUEBA PARA "+tipoVehiculo.toUpperCase());

        

        if(tipoVehiculo.equalsIgnoreCase("CICLOMOTOR")){
            this.led2.setVisible(false);
            jLabel4.setVisible(false);
            jLabel3.setText("Presencia");
        } 

        // Si ejesDondeTieneFrenoMano es 0, significa que no tiene freno de mano
        // Si ejesDondeTieneFrenoMano es 1, significa que el freno de mano está en el eje 1
        // Si ejesDondeTieneFrenoMano es 2, significa que el freno de mano está en el eje 2
        // Si ejesDondeTieneFrenoMano es 3, significa que el freno de mano está en ambos ejes
        ejesDondeTieneFrenoMano = frenoMano; // 0=Ninguno 1=Eje1, 2=Eje2, 3=Ambos ejes
    }

    /**
     *
     * Autor ELKIN B
     */
    private void leyendoArchivoCobfiguraciones() {
        System.out.println("----------------------------------------------------");
        System.out.println("----------leyendoArchivoCobfiguraciones-------------");
        System.out.println("----------------------------------------------------");

        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            String line;

            System.out.println("--Lectura de la configuración de la tarjeta de adquisición de datos----");

            while (!config.readLine().startsWith("[DAQ]")) {
            }
            if ((line = config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo puerto. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }

            while (!config.readLine().startsWith("[BANCO]")) {
            }

            

            if ((line = config.readLine()).startsWith("desviacion:")) {
                setEnablehwdesv((line.substring(line.indexOf(" ") + 1, line.length())).equals("si"));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo desviación. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("suspension:")) {
                setEnablehwsusp((line.substring(line.indexOf(" ") + 1, line.length())).equals("si"));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo suspensión. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }

            if ((line = config.readLine()).startsWith("frenos:")) {
                setEnablehwfren((line.substring(line.indexOf(" ") + 1, line.length())).equals("si"));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo frenos. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }

            if ((line = config.readLine()).startsWith("pista_mixta:")) {
                setPista_mixta((line.substring(line.indexOf(" ") + 1, line.length())).equals("si"));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo pista_mixta. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }

            if ((line = config.readLine()).startsWith("orden:")) {
                ordenPruebas = line.split(" ")[1];
            }

            System.out.println("-------------Lectura de configuración de parametros para el software----");

            while (!config.readLine().startsWith("[SOFTWARE]")) {
            }
            if ((line = config.readLine()).startsWith("pruebas_unidas")) {
                System.out.println(line.substring(line.indexOf(" ") + 1, line.length()) + " se haran las pruebas integradas");
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo pruebas_unidas. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("tiempo_mensajes:")) {
                tiempomensajes = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo tiempo_mensajes. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("registrar_medidas:")) {
                enablereg = (line.substring(line.indexOf(" ") + 1, line.length())).equals("si");
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo registrar_medidas. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("backup_registro:")) {
                enablebackup = (line.substring(line.indexOf(" ") + 1, line.length())).equals("si");
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo backup_registro. Por favor  Llame al Equipo de Soporte Técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }

            System.out.println("------Lectura de la configuración de la prueba de desviacion------------");

            while (!config.readLine().startsWith("[DESVIACION]")) {
            }
            if ((line = config.readLine()).startsWith("ancho_placa:")) {
                anchoplacadesv = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo ancho_placa. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("aplica_sensor:")) {

                aplcSenDesv = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo aplica_sensor. Por Favor Llame Soporte Tecnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("resol_min:")) {
                resolMin = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
                System.out.println(" RESOL MIN " + resolMin);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo resol_min para Desv.. Por Favor Llame Soporte Tecnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("resol_max:")) {
                resolMax = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
                System.out.println(" RESOL MIN " + resolMax);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo resol_max para Desv.. Por Favor Llame Soporte Tecnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }

            System.out.println("------Lectura de la configuración de la prueba de suspensión------------");

            while (!config.readLine().startsWith("[SUSPENSION]")) {
            }
            if ((line = config.readLine()).startsWith("umbral_peso_susp")) {
                umbralPeso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo umbral_peso_susp. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("factor_escala_susp")) {
                factorescala = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo factor_escala_susp. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("temporizador_inercia")) {
                teporizadorInercia = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo temporizador_inercia. Llame a servicio técnico  " + line,
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("salida_motor_derecho")) {
                salidamotorderecho = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.length()), 2);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo salida_motor_derecho. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("salida_motor_izquierdo")) {
                salidamotorizquierdo = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.length()), 2);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo salida_motor_izquierdo. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            while (!config.readLine().startsWith("[TIPOPISTA]")) {
            }
            if ((line = config.readLine()).startsWith("valida_tipo:")) {
                if ((line = config.readLine()).startsWith("tipo_pista:")) {
                    tipoPista = line.substring(line.indexOf(" ") + 1, line.length());
                } else {
                    JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo (tipo_pista xx-yy). Llame a servicio técnico",
                            "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                    setError_config(true);
                }
            }

            System.out.println("------Lectura de la configuración de la prueba de frenos----------------");

            while (!config.readLine().startsWith("[FRENOMETRO]")) {
            }
            if ((line = config.readLine()).startsWith("umbral_velo")) {
                umbral_velo = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length())) / 100;
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo umbral_velo. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("umbral_peso_fren")) {
                umbralPeso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo umbral_peso_fren. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("factor_desq")) {
                factor_desq = Float.parseFloat(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo (ajuste_dsq). Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            while (!config.readLine().startsWith("livianos:")) {
            }
            if ((line = config.readLine()).startsWith("pasos:")) {
                numpasos = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.length()));
            }
            System.out.println("TAKE NUM PASOS IS " + numpasos);

            if (config.markSupported()) {
                config.mark(1000);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(" Error en el metodo : leyendoArchivoCobfiguraciones()" + ex.getMessage() + ex.getLocalizedMessage());
            System.out.println("error en el archivo de configuración " + ex);
            setError_config(true);
        } catch (IOException ex) {
            System.out.println(" Error en el metodo : leyendoArchivoCobfiguraciones()");
            System.out.println("no se pudo abrir " + ex);
            setError_config(true);
        }
    }

    /**
     * Autor ELKIN B
     */
    private void backupDatos() {
        System.out.println("----------------------------------------------------");
        System.out.println("-----------------------backupDatos------------------");
        System.out.println("----------------------------------------------------");

        if (enablebackup) {
            try {
                if (isEnablehwdesv()) {
                    System.out.println("--Creación de los archivos backup para los datos de desviación--");

                    regdatosd1 = new BufferedWriter(new FileWriter(new File("Backup Datos Desviacion 1.txt")));
                    regdatosd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA");
                    regdatosd1.newLine();
                    regdatosd1.flush();
                    regdatosd2 = new BufferedWriter(new FileWriter(new File("Backup Datos Desviacion 2.txt")));
                    regdatosd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE DESVIACION DEL EJE TRASERO");
                    regdatosd2.newLine();
                    regdatosd2.flush();
                }
                if (isEnablehwsusp() || isEnablehwfren()) {
                    System.out.println("--Creación de los archivos backup de peso para las pruebas de suspension y frenos--");

                    regdatospd1 = new BufferedWriter(new FileWriter(new File("Backup Datos Peso Derecho 1.txt")));
                    regdatospd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE DELANTERO");
                    regdatospd1.newLine();
                    regdatospd1.flush();
                    regdatospi1 = new BufferedWriter(new FileWriter(new File("Backup Datos Peso Izquierdo 1.txt")));
                    regdatospi1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO IZQUIERDO DEL EJE DELANTERO");
                    regdatospi1.newLine();
                    regdatospi1.flush();
                    regdatospd2 = new BufferedWriter(new FileWriter(new File("Backup Datos Peso Derecho 2.txt")));
                    regdatospd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE TRASERO");
                    regdatospd2.newLine();
                    regdatospd2.flush();
                    regdatospi2 = new BufferedWriter(new FileWriter(new File("Backup Datos Peso Izquierdo 2.txt")));
                    regdatospi2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO IZQUIERDO DEL EJE TRASERO");
                    regdatospi2.newLine();
                    regdatospi2.flush();

                    if (isEnablehwsusp()) {
                        System.out.println("--Creación de los archivos backup para los datos de suspensión--");

                        regdatosfvd1 = new BufferedWriter(new FileWriter(new File("Backup Datos Suspension Derecha 1.txt")));
                        regdatosfvd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA VERTICAL DERECHA DEL EJE DELANTERO");
                        regdatosfvd1.newLine();
                        regdatosfvd1.flush();
                        regdatosfvi1 = new BufferedWriter(new FileWriter(new File("Backup Datos Suspension Izquierda 1.txt")));
                        regdatosfvi1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA VERTICAL IZQUIERDA DEL EJE DELANTERO");
                        regdatosfvi1.newLine();
                        regdatosfvi1.flush();
                        regdatosfvd2 = new BufferedWriter(new FileWriter(new File("Backup Datos Suspension Derecha 2.txt")));
                        regdatosfvd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA VERTICAL DERECHA DEL EJE TRASERO");
                        regdatosfvd2.newLine();
                        regdatosfvd2.flush();
                        regdatosfvi2 = new BufferedWriter(new FileWriter(new File("Backup Datos Suspension Izquierda 2.txt")));
                        regdatosfvi2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA VERTICAL IZQUIERDA DEL EJE TRASERO");
                        regdatosfvi2.newLine();
                        regdatosfvi2.flush();

                    }
                    if (isEnablehwfren()) {
                        System.out.println("--Creación de los archivos backup para los datos de frenos--");

                        regdatosffd1 = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos Derecha 1.txt")));
                        regdatosffd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DERECHA DEL EJE DELANTERO");
                        regdatosffd1.newLine();
                        regdatosffd1.flush();
                        regdatosffi1 = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos Izquierda 1.txt")));
                        regdatosffi1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO IZQUIERDA DEL EJE DELANTERO");
                        regdatosffi1.newLine();
                        regdatosffi1.flush();
                        regdatosffd2 = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos Derecha 2.txt")));
                        regdatosffd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DERECHA DEL EJE TRASERO");
                        regdatosffd2.newLine();
                        regdatosffd2.flush();
                        regdatosffi2 = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos Izquierda 2.txt")));
                        regdatosffi2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO IZQUIERDA DEL EJE TRASERO");
                        regdatosffi2.newLine();
                        regdatosffi2.flush();
                        regdatosffda = new BufferedWriter(new FileWriter(new File("Backup Datos Freno de mano Derecha.txt")));
                        regdatosffda.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DE MANO DERECHA");
                        regdatosffda.newLine();
                        regdatosffda.flush();
                        regdatosffia = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos de mano Izquierda.txt")));
                        regdatosffia.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DE MANO IZQUIERDA");
                        regdatosffia.newLine();
                        regdatosffia.flush();//</editor-fold>                        
                    }
                }
            } catch (IOException ex) {
                System.err.println("Error en el metodo: backupDatos() " + ex.getLocalizedMessage() + ex.getMessage());
                System.out.println("no se pudo crear el archivo de datos" + ex);
                setError_config(true);
            }
        }

    }

    /**
     * Autor ELKIN B
     */
    private void leyendoSpanConfiguracion() {
        System.out.println("----------------------------------------------------");
        System.out.println("------------leyendo Span Dinamicos-----------------");
        System.out.println("----------------------------------------------------");

        try {
            Properties archivop = new Properties();
            try {
                archivop.load(new FileInputStream("calibracion.properties"));
            } catch (IOException ex) {
                System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o está protegido " + ex);
                setError_config(true);
            }
            //Lectura de los span para las mediciones en el banco y de la URL del servidor
            System.out.println("-----------------------------------------------------");
            System.out.println("-LEYENDO LOS SPAN DINAMICOS DEL ARCHIVO : calibracion.properties");
            System.out.println("-----------------------------------------------------");

            String Flag, Flag2;
            Flag = archivop.getProperty("spanfdl");
            Flag2 = archivop.getProperty("spanfil");
            System.out.println("valor de flag 2= " + Flag2 + " valor de flag 1= " + Flag);
            spanfd = (Flag == null) ? Double.parseDouble(archivop.getProperty("spanfd")) : Double.parseDouble(archivop.getProperty("spanfdl"));
            spanfi = (Flag2 == null) ? Double.parseDouble(archivop.getProperty("spanfi")) : Double.parseDouble(archivop.getProperty("spanfil"));
            // spanfd = Double.parseDouble(archivop.getProperty("spanfd"));
            //spanfi = Double.parseDouble(archivop.getProperty("spanfi"));

            spanpd = Double.parseDouble(archivop.getProperty("spanpd"));

            spanpi = Double.parseDouble(archivop.getProperty("spanpi"));
            spanvd = Double.parseDouble(archivop.getProperty("spanvd"));
            spanvi = Double.parseDouble(archivop.getProperty("spanvi"));
            spand = Double.parseDouble(archivop.getProperty("spand"));
            spanpdTD = Double.parseDouble(archivop.getProperty("spanpdTD"));
            spanpdTI = Double.parseDouble(archivop.getProperty("spanpdTI"));
            spanpdDD = Double.parseDouble(archivop.getProperty("spanpdDD"));
            spanpdDI = Double.parseDouble(archivop.getProperty("spanpdDI"));

            System.out.println("spanfd " + spanfd + " spanfi " + spanfi + " spanpd " + spanpd + " spanpi " + spanpi
                    + " spanvd " + spanvd + " spanvi " + spanvi + " spand " + spand);
            System.out.println("LOS SPAN DINAMICOS DE PESOS SON  spanpdTD" + spanpdTD + " spanpdTI " + spanpdTI + " spanpdDD " + spanpdDD + " spanpdDI " + spanpdDI);

        } catch (Exception e) {
            System.err.println("Error en el metodo : leyendoSpanConfiguracion()");
        }

    }

    /**
     *
     * Autor ELKIN B
     */
    private void configuracion() {
        System.out.println("----------------------------------------------------");
        System.out.println("-----------------configuracion----------------------");
        System.out.println("----------------------------------------------------");

        try {
            setError_config(false);
            leyendoArchivoCobfiguraciones();
            backupDatos();
            leyendoSpanConfiguracion();

            puerto = new PuertoRS232();
            try {
                if (!error_config) {
                    puerto.connect(puertotarjeta);
                    comandoSTOP();
                }
            } catch (Exception ex) {
                System.out.println(ex);
                setError_config(true);
            }
            this.addKeyListener(new java.awt.event.KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (tecladoactivado) {
                        if (e.getKeyChar() == '2') {

                        }
                        tecladoactivado = false;
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("Error rn el metodo : configuracion()" + e.getMessage() + e.getLocalizedMessage());
        }

    }

    public void LeePaso() {
        String line;
        try {
            if ((line = config.readLine()).startsWith("salidas:")) {
                salidaalta = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.indexOf(",") - 1), 2);
                salidabaja = Byte.parseByte(line.substring(line.indexOf(",") + 1, line.length()), 2);
            }
            if ((line = config.readLine()).startsWith("tiempo:")) {
                tiempo_paso = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            }
        } catch (IOException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Se perdio de lineas");
        }
    }

    public void comandoFREN() {
        try {
            puerto.out.write('H');
            puerto.out.write('A');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de Frenometro");
        }
        System.out.println("Comando Frenometro");
    }

    public void comandoSTOP() {
        try {
            puerto.out.write('H');
            puerto.out.write('B');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de STOP");
        }
        System.out.println("Comando STOP");
    }

    public void comandoSTOPAnalogo() {
        try {
            puerto.out.write('H');
            puerto.out.write('B');
            puerto.out.write('A');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de STOP Analogo");
        }
        System.out.println("Comando STOP Analogo");
    }

    public void comandoSTOPDigital() {
        try {
            puerto.out.write('H');
            puerto.out.write('B');
            puerto.out.write('D');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de STOP Digital");
        }
        System.out.println("Comando STOP Digital");
    }

    public void comandoSUSP() {
        try {
            puerto.out.write('H');
            puerto.out.write('C');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de Suspension");
        }
        System.out.println("Comando Suspensión");
    }

    public void comandoDESV() {
        try {
            puerto.out.write('H');
            puerto.out.write('D');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de Desviacion");
        }
        System.out.println("Comando Desviación");
    }

    public void EnviaSalidas(int a, int b) throws InterruptedException {
        try {
            puerto.out.write('H');
            puerto.out.write('G');
            puerto.out.write(a);
            puerto.out.write(b);
            puerto.out.write('W');
            Thread.sleep(300);
        } catch (IOException ex) {
            System.out.println("Error en el comando de Enviar Salidas");
        }
        System.out.println("salida alta: " + a + ", salida baja: " + b);
    }

    public void comandoCONEX() {
        try {
            puerto.out.write('H');
            puerto.out.write('J');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de conexión a la tarjeta");
        }
        System.out.println("Comando Conexión");
    }

    public void comandoCEROS(int a) {
        try {
            puerto.out.write('H');
            puerto.out.write('K');
            puerto.out.write(a);
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de Ceros");
        }
        System.out.println("Comando de Ceros");
    }

    public boolean PruebaConexion() {
        String respuesta;
        /*comandoSTOP();
         try {
         Thread.currentThread().sleep(200);
         } catch (InterruptedException ex) {
         Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        comandoCONEX();
        try {
            Thread.currentThread().sleep(200);
            //java.lang.Thread.sleep(200);
            puerto.in.read(buffer);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        return respuesta.startsWith("HDAQ1W");
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     * @param g
     */
    public void CalibrarCeros(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g) {
        if (a) {

            valcalcero1 = CalcularMedia(Datos1)/*+ruidomotor*/;
            System.out.println("cero fuerza derecha: " + (valcalcero1 * spanfd) + " N");
        }
        Datos1.clear();
        if (b) {
            valcalcero2 = CalcularMedia(Datos2)/*+ruidomotor*/;
            System.out.println("cero fuerza izquierda: " + (valcalcero2 * spanfi) + " N");
        }
        Datos2.clear();
        if (c) {
            valcalcero3 = CalcularMedia(Datos3);
            System.out.println("cero peso derecho: " + (valcalcero3 * spanpd) + " N");
        }
        Datos3.clear();
        if (d) {
            valcalcero4 = CalcularMedia(Datos4);
            System.out.println("cero peso izquierdoen MV..: " + (valcalcero4) + " mv");
            System.out.println("cero peso izquierdo: " + (valcalcero4 * spanpi) + " N");
        }
        Datos4.clear();
        if (e) {
            valcalcero5 = CalcularMedia(Datos5);
            System.out.println("cero velocidad derecha: " + (valcalcero5 * spanvd) + " Km/h");
        }
        Datos5.clear();
        if (f) {
            valcalcero6 = CalcularMedia(Datos6);
            System.out.println("cero velocidad izquierda: " + (valcalcero6 * spanvi) + " Km/h");
        }
        Datos6.clear();
        if (g) {
            valcalcero7 = CalcularMediana(Datos7);//CAMBIO DE FUNCION DE MEDIA A MEDIANA PARA MEJOR CALCUYLO DE MEDIDA POR PROBLEMA DE QUE NO DA CERO
            
            //maxCeroDesv = CalcularMaximoIn(Datos7);
            System.out.println("cero desviacion: " + (valcalcero7 * spand) + " mm");
            System.out.println("cero desviacion: " + (valcalcero7) + " mm");

        }
        Datos7.clear();
    }

    private double round(double value, int places) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void MedirFuerzaFrenado(boolean freAux) {
        armonizar(this.Datos1, this.valcalcero1);
        armonizar(this.Datos2, this.valcalcero2);
        this.Datosfil1 = filtrar(this.Datos1);
        this.Datosfil2 = filtrar(this.Datos2);
        double auxfuerzad = CalcularMaximo(this.Datosfil1);
        auxfuerzad = round(auxfuerzad, 5);
        double auxfuerzai = CalcularMaximo(this.Datosfil2);
        auxfuerzai = round(auxfuerzai, 5);
        System.out.println(new StringBuilder().append("Maximo fuerza der: ").append(auxfuerzad).toString());
        System.out.println(new StringBuilder().append("Maximo fuerza izq: ").append(auxfuerzai).toString());
        long s = this.Datosfil1.size() - 1;
        long r = this.Datosfil2.size() - 1;

        int bc;
        if (this.enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatosffd1.write(new StringBuilder().append("el cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                    regdatosffd1.newLine();
                    regdatosffd1.write(new StringBuilder().append("el span de fuerza derecha es: ").append(this.spanfd).toString());
                    regdatosffd1.newLine();
                    regdatosffd1.flush();
                    
                    int minSize = Math.min(Datosfil1.size(), Datos1.size());
                    int maxIndex = (int) Math.min(s, minSize);
        
                    for (this.t = 0; this.t < maxIndex; this.t++) {
                        regdatosffd1.write(new StringBuilder()
                            .append(this.Datosfil1.get(this.t).toString())
                            .append("  ")
                            .append(this.Datos1.get(this.t))
                            .toString());
                        regdatosffd1.newLine();
                    }
                    regdatosffd1.close();
                } else if (this.ejemedido == 2) {
                    if (!isFrenmano()) {
                        regdatosffd2.write(new StringBuilder().append("el cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                        regdatosffd2.newLine();
                        regdatosffd2.write(new StringBuilder().append("el span de fuerza derecha es: ").append(this.spanfd).toString());
                        regdatosffd2.newLine();
                        regdatosffd2.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos1.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffd2.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos1.get(this.t))
                                .toString());
                            regdatosffd2.newLine();
                        }
                        regdatosffd2.close();
                    } else {
                        regdatosffda.write(new StringBuilder().append("(Freno Mano) El cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                        regdatosffda.newLine();
                        regdatosffda.write(new StringBuilder().append("(Freno Mano) El span de fuerza derecha es: ").append(this.spanfd).toString());
                        regdatosffda.newLine();
                        regdatosffda.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos1.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffda.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos1.get(this.t))
                                .toString());
                            regdatosffda.newLine();
                        }
                        regdatosffda.close();
                    }
                }
                if (this.ejemedido == 1) {
                    regdatosffi1.write(new StringBuilder().append("el cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                    regdatosffi1.newLine();
                    regdatosffi1.write(new StringBuilder().append("el span de fuerza izquierda es: ").append(this.spanfi).toString());
                    regdatosffi1.newLine();
                    regdatosffi1.flush();
                    
                    int minSize = Math.min(Datosfil1.size(), Datos2.size());
                    int maxIndex = (int) Math.min(s, minSize);
        
                    for (this.t = 0; this.t < maxIndex; this.t++) {
                        regdatosffi1.write(new StringBuilder()
                            .append(this.Datosfil1.get(this.t).toString())
                            .append("  ")
                            .append(this.Datos2.get(this.t))
                            .toString());
                        regdatosffi1.newLine();
                    }
                    regdatosffi1.close();
                } else if (this.ejemedido == 2) {
                    if (!isFrenmano()) {
                        regdatosffi2.write(new StringBuilder().append("el cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                        regdatosffi2.newLine();
                        regdatosffi2.write(new StringBuilder().append("el span de fuerza izquierda es: ").append(this.spanfi).toString());
                        regdatosffi2.newLine();
                        regdatosffi2.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos2.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffi2.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos2.get(this.t))
                                .toString());
                            regdatosffi2.newLine();
                        }
                        regdatosffi2.close();
                    } else {
                        regdatosffia.write(new StringBuilder().append("(Freno Mano) El cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                        regdatosffia.newLine();
                        regdatosffia.write(new StringBuilder().append("(Freno Mano) El span de fuerza izquierda es: ").append(this.spanfi).toString());
                        regdatosffia.newLine();
                        regdatosffia.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos2.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffia.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos2.get(this.t))
                                .toString());
                            regdatosffia.newLine();
                        }
                        regdatosffia.close();
                    }
                }
            } catch (IOException ex) {
                System.out.println(new StringBuilder().append("No se pudo escribir el dato de peso en el backup ").append(ex).toString());
            }
        }
        
        double ceroFuerzaDer = this.valcalcero1 + offsetCeroFuerzaDer;
        double ceroFuerzaIzq = this.valcalcero2 + offsetCeroFuerzaIzq;
        double fuerzaDerSinSpan = Math.abs(auxfuerzad - ceroFuerzaDer);
        double fuerzaIzqSinSpan = Math.abs(auxfuerzai - ceroFuerzaIzq);

        if (freAux == false) {
            this.fuerzasfd.add(Math.abs(fuerzaDerSinSpan));
            System.out.println("fuerzaDer sin cero: " + auxfuerzad + " mV. Valor cero: "+ valcalcero1 + " mV. span: "+spanfd +". offset: "+ offsetCeroFuerzaDer+" mV.");
            Utilidades2.medidasCuatrimotoPequena[ejemedido - 1][1] = fuerzaDerSinSpan * spanfd;
            Utilidades2.parametros[ejemedido - 1][0] = valcalcero1;
            Utilidades2.parametros[ejemedido - 1][1] = auxfuerzad;
            Utilidades2.parametros[ejemedido - 1][2] = offsetCeroFuerzaDer;
            Utilidades2.parametros[ejemedido - 1][3] = spanfd;
        } else {
            fuerzasfdAux.add(Math.abs(fuerzaDerSinSpan));
            System.out.println("fuerzaDerMano sin cero: " + auxfuerzad + " mV. Valor cero: "+ valcalcero1 + " mV. span: "+spanfd +". offset: "+ offsetCeroFuerzaDer+" mV.");
            Utilidades2.medidasCuatrimotoPequena[4][0] = fuerzaDerSinSpan * spanfd;
            Utilidades2.parametros[4][0] = valcalcero1;
            Utilidades2.parametros[4][1] = auxfuerzad;
            Utilidades2.parametros[4][2] = offsetCeroFuerzaDer;
            Utilidades2.parametros[4][3] = spanfd;
        }
        
        if (freAux == false) {
            this.fuerzasfi.add(Math.abs(fuerzaIzqSinSpan));
            System.out.println("fuerzaIzq sin cero: " + auxfuerzai + " mV. Valor cero: "+ valcalcero2 + " mV. span: "+spanfi +". offset: "+ offsetCeroFuerzaIzq+" mV.");
            Utilidades2.medidasCuatrimotoPequena[ejemedido + 1][1] = fuerzaIzqSinSpan * spanfi;
            Utilidades2.parametros[ejemedido + 1][0] = valcalcero2;
            Utilidades2.parametros[ejemedido + 1][1] = auxfuerzai;
            Utilidades2.parametros[ejemedido + 1][2] = offsetCeroFuerzaIzq;
            Utilidades2.parametros[ejemedido + 1][3] = spanfi;
        } else {
            fuerzasfiAux.add(Math.abs(fuerzaIzqSinSpan));
            System.out.println("fuerzaIzqMano sin cero: " + auxfuerzai + " mV. Valor cero: "+ valcalcero2 + " mV. span: "+spanfi +". offset: "+ offsetCeroFuerzaIzq+" mV.");
            Utilidades2.medidasCuatrimotoPequena[4][1] = fuerzaIzqSinSpan * spanfi;
            Utilidades2.parametros[5][0] = valcalcero1;
            Utilidades2.parametros[5][1] = auxfuerzad;
            Utilidades2.parametros[5][2] = offsetCeroFuerzaDer;
            Utilidades2.parametros[5][3] = spanfd;
        }
        
        if (freAux == false) {
            bc = fuerzasfd.size();
        } else {
            bc = fuerzasfdAux.size();
        }

        if (tipoVehiculo.equals("Motocarro")) {
            System.out.println("Ejemedido: " + ejemedido);
            String logDer = 
                "LogFuerzaDerecha"+ejeORueda+ruedasOEjes[ejemedido-1]+"\n"+
                "\nCero con offset (cero+offset) { "+rd2(valcalcero1)+" + "+rd2(offsetCeroFuerzaDer)+" } = "+ ceroFuerzaDer + " mV.\n"+
                "Fuerza con cero sin span(fuerzaSinCero-CeroConOffset) { "+rd2(auxfuerzad)+" - "+rd2(ceroFuerzaDer)+" } = "+ fuerzaDerSinSpan + " mV.\n"+
                "Fuerza total(FuerzaConCeroSinSpan*Span) { "+rd2(fuerzaDerSinSpan)+" * "+rd2(spanfd)+" } = "+ (fuerzaDerSinSpan* spanfd) + " NEWTON.\n";
            System.out.println(logDer);

            String logIzq = 
                "LogFuerzaIzquierda"+ejeORueda+ruedasOEjes[ejemedido-1]+"\n"+
                "\nCero con offset (cero+offset) { "+rd2(valcalcero2)+" + "+rd2(offsetCeroFuerzaIzq)+" } = "+ ceroFuerzaIzq + " mV.\n"+
                "Fuerza con cero sin span(fuerzaSinCero-CeroConOffset) { "+rd2(auxfuerzai)+" - "+rd2(ceroFuerzaIzq)+" } = "+ fuerzaIzqSinSpan + " mV.\n"+
                "Fuerza total(FuerzaConCeroSinSpan*Span) { "+rd2(fuerzaIzqSinSpan)+" * "+rd2(spanfi)+" } = "+ (fuerzaIzqSinSpan* spanfi) + " NEWTON.\n";
            System.out.println(logIzq);

            imprimirDatosHastaElMomento();
        }else{
            System.out.println(new StringBuilder().append("de nuevo los cero de fuerza derecho e izquierdo ").append(this.valcalcero1 * this.spanfd).append(" N ").append(this.valcalcero2 * this.spanfi).append(" N").toString());
            System.out.println(new StringBuilder().append("fuerza sin calibrar ").append(this.ejemedido).append(" derecha e izquierda: ").append(auxfuerzad * this.spanfd).append(" N ").append(auxfuerzai * this.spanfi).append(" N").toString());
            System.out.println(new StringBuilder().append("fuerza del eje ").append(this.ejemedido).append(" derecha e izquierda: ").append(((Double) this.fuerzasfd.get(bc - 1)).doubleValue() * this.spanfd).append(" N ").append(((Double) this.fuerzasfi.get(bc - 1)).doubleValue() * this.spanfi).append(" N").toString());
        }
        
        this.Datos1.clear();
        this.Datos2.clear();
        this.Datosfil1.clear();
        this.Datosfil2.clear();
    }

    public void MedirFuerzaFrenado(boolean freAux, String lado) {
        armonizar(this.Datos1, this.valcalcero1);
        armonizar(this.Datos2, this.valcalcero2);
        this.Datosfil1 = filtrar(this.Datos1);
        this.Datosfil2 = filtrar(this.Datos2);
        double auxfuerzad = CalcularMaximo(this.Datosfil1);
        auxfuerzad = round(auxfuerzad, 5);
        double auxfuerzai = CalcularMaximo(this.Datosfil2);
        auxfuerzai = round(auxfuerzai, 5);
        System.out.println(new StringBuilder().append("Maximo fuerza der: ").append(auxfuerzad).toString());
        System.out.println(new StringBuilder().append("Maximo fuerza izq: ").append(auxfuerzai).toString());
        long s = this.Datosfil1.size() - 1;
        long r = this.Datosfil2.size() - 1;

        int bc;
        if (this.enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatosffd1.write(new StringBuilder().append("el cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                    regdatosffd1.newLine();
                    regdatosffd1.write(new StringBuilder().append("el span de fuerza derecha es: ").append(this.spanfd).toString());
                    regdatosffd1.newLine();
                    regdatosffd1.flush();
                    
                    int minSize = Math.min(Datosfil1.size(), Datos1.size());
                    int maxIndex = (int) Math.min(s, minSize);
        
                    for (this.t = 0; this.t < maxIndex; this.t++) {
                        regdatosffd1.write(new StringBuilder()
                            .append(this.Datosfil1.get(this.t).toString())
                            .append("  ")
                            .append(this.Datos1.get(this.t))
                            .toString());
                        regdatosffd1.newLine();
                    }
                    regdatosffd1.close();
                } else if (this.ejemedido == 2) {
                    if (!isFrenmano()) {
                        regdatosffd2.write(new StringBuilder().append("el cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                        regdatosffd2.newLine();
                        regdatosffd2.write(new StringBuilder().append("el span de fuerza derecha es: ").append(this.spanfd).toString());
                        regdatosffd2.newLine();
                        regdatosffd2.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos1.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffd2.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos1.get(this.t))
                                .toString());
                            regdatosffd2.newLine();
                        }
                        regdatosffd2.close();
                    } else {
                        regdatosffda.write(new StringBuilder().append("(Freno Mano) El cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                        regdatosffda.newLine();
                        regdatosffda.write(new StringBuilder().append("(Freno Mano) El span de fuerza derecha es: ").append(this.spanfd).toString());
                        regdatosffda.newLine();
                        regdatosffda.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos1.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffda.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos1.get(this.t))
                                .toString());
                            regdatosffda.newLine();
                        }
                        regdatosffda.close();
                    }
                }
                if (this.ejemedido == 1) {
                    regdatosffi1.write(new StringBuilder().append("el cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                    regdatosffi1.newLine();
                    regdatosffi1.write(new StringBuilder().append("el span de fuerza izquierda es: ").append(this.spanfi).toString());
                    regdatosffi1.newLine();
                    regdatosffi1.flush();
                    
                    int minSize = Math.min(Datosfil1.size(), Datos2.size());
                    int maxIndex = (int) Math.min(s, minSize);
        
                    for (this.t = 0; this.t < maxIndex; this.t++) {
                        regdatosffi1.write(new StringBuilder()
                            .append(this.Datosfil1.get(this.t).toString())
                            .append("  ")
                            .append(this.Datos2.get(this.t))
                            .toString());
                        regdatosffi1.newLine();
                    }
                    regdatosffi1.close();
                } else if (this.ejemedido == 2) {
                    if (!isFrenmano()) {
                        regdatosffi2.write(new StringBuilder().append("el cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                        regdatosffi2.newLine();
                        regdatosffi2.write(new StringBuilder().append("el span de fuerza izquierda es: ").append(this.spanfi).toString());
                        regdatosffi2.newLine();
                        regdatosffi2.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos2.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffi2.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos2.get(this.t))
                                .toString());
                            regdatosffi2.newLine();
                        }
                        regdatosffi2.close();
                    } else {
                        regdatosffia.write(new StringBuilder().append("(Freno Mano) El cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                        regdatosffia.newLine();
                        regdatosffia.write(new StringBuilder().append("(Freno Mano) El span de fuerza izquierda es: ").append(this.spanfi).toString());
                        regdatosffia.newLine();
                        regdatosffia.flush();
                        
                        int minSize = Math.min(Datosfil1.size(), Datos2.size());
                        int maxIndex = (int) Math.min(s, minSize);
        
                        for (this.t = 0; this.t < maxIndex; this.t++) {
                            regdatosffia.write(new StringBuilder()
                                .append(this.Datosfil1.get(this.t).toString())
                                .append("  ")
                                .append(this.Datos2.get(this.t))
                                .toString());
                            regdatosffia.newLine();
                        }
                        regdatosffia.close();
                    }
                }
            } catch (IOException ex) {
                System.out.println(new StringBuilder().append("No se pudo escribir el dato de peso en el backup ").append(ex).toString());
            }
        }

        boolean isDerecho = lado.equalsIgnoreCase("derecho");
        
        double fuerzaDerSinSpan = 0.0;
        double fuerzaIzqSinSpan = 0.0;
        
        if (isDerecho) {
            double ceroFuerzaDer = this.valcalcero1 + offsetCeroFuerzaDer;
            fuerzaDerSinSpan = Math.abs(auxfuerzad - ceroFuerzaDer);
            System.out.println("\n\nFUERZA DERECHA "+ejeORueda+ruedasOEjes[ejemedido-1]+"--(RECORDAR QUE LOS VALORES NEGATIVOS SE LES SACA VALOR ABSOLUTO Y LOS CALCULOS TIENEN EN CUENTA TODAS LAS CIFRAS DECIMALES)-------------------\n");
            if (freAux == false) {
                this.fuerzasfd.add(fuerzaDerSinSpan);
                System.out.println("fuerzaDer sin cero: " + auxfuerzad + " mV. Valor cero: "+ valcalcero1 + " mV. span: "+spanfd +". offset: "+ offsetCeroFuerzaDer+" mV.");
                Utilidades2.medidasCuatrimotoPequena[ejemedido - 1][1] = fuerzaDerSinSpan * spanfd;
                Utilidades2.parametros[ejemedido - 1][0] = valcalcero1;
                Utilidades2.parametros[ejemedido - 1][1] = auxfuerzad;
                Utilidades2.parametros[ejemedido - 1][2] = offsetCeroFuerzaDer;
                Utilidades2.parametros[ejemedido - 1][3] = spanfd;
            } else {
                fuerzasfdAux.add(fuerzaDerSinSpan);
                System.out.println("fuerzaDerMano sin cero: " + auxfuerzad + " mV. Valor cero: "+ valcalcero1 + " mV. span: "+spanfd +". offset: "+ offsetCeroFuerzaDer+" mV.");
                Utilidades2.medidasCuatrimotoPequena[4][0] = fuerzaDerSinSpan * spanfd;
                Utilidades2.medidasCuatrimotoPequena[4][0] = fuerzaDerSinSpan * spanfd;
                Utilidades2.parametros[4][0] = valcalcero1;
                Utilidades2.parametros[4][1] = auxfuerzad;
                Utilidades2.parametros[4][2] = offsetCeroFuerzaDer;
                Utilidades2.parametros[4][3] = spanfd;
            }
            String logDer = 
                "\nCero con offset (cero+offset) { "+rd2(valcalcero1)+" + "+rd2(offsetCeroFuerzaDer)+" } = "+ ceroFuerzaDer + " mV.\n"+
                "Fuerza con cero sin span(fuerzaSinCero-CeroConOffset) { "+rd2(auxfuerzad)+" - "+rd2(ceroFuerzaDer)+" } = "+ fuerzaDerSinSpan + " mV.\n"+
                "Fuerza total(FuerzaConCeroSinSpan*Span) { "+rd2(fuerzaDerSinSpan)+" * "+rd2(spanfd)+" } = "+ (fuerzaDerSinSpan* spanfd) + " NEWTON.\n";
            System.out.println(logDer);
        }else{
            double ceroFuerzaIzq = this.valcalcero2 + offsetCeroFuerzaIzq;
            fuerzaIzqSinSpan = Math.abs(auxfuerzai - ceroFuerzaIzq);
            System.out.println("\n\nFUERZA IZQUIERDA "+ejeORueda+ruedasOEjes[ejemedido-1]+"--(RECORDAR QUE LOS VALORES NEGATIVOS SE LES SACA VALOR ABSOLUTO Y LOS CALCULOS TIENEN EN CUENTA TODAS LAS CIFRAS DECIMALES)-------------------\n");
            if (freAux == false) {
                this.fuerzasfi.add(fuerzaIzqSinSpan);
                System.out.println("fuerzaIzq sin cero: " + auxfuerzai + " mV. Valor cero: "+ valcalcero2 + " mV. span: "+spanfi +". offset: "+ offsetCeroFuerzaIzq+" mV.");
                Utilidades2.medidasCuatrimotoPequena[ejemedido - 1][1] = fuerzaIzqSinSpan * spanfi;
                Utilidades2.parametros[ejemedido - 1][0] = valcalcero2;
                Utilidades2.parametros[ejemedido - 1][1] = auxfuerzai;
                Utilidades2.parametros[ejemedido - 1][2] = offsetCeroFuerzaIzq;
                Utilidades2.parametros[ejemedido - 1][3] = spanfi;
            } else {
                fuerzasfiAux.add(fuerzaIzqSinSpan);
                System.out.println("fuerzaIzqMano sin cero: " + auxfuerzai + " mV. Valor cero: "+ valcalcero2 + " mV. span: "+spanfi +". offset: "+ offsetCeroFuerzaIzq+" mV.");
                Utilidades2.medidasCuatrimotoPequena[4][1] = fuerzaIzqSinSpan * spanfi;
                Utilidades2.parametros[5][0] = valcalcero1;
                Utilidades2.parametros[5][1] = auxfuerzad;
                Utilidades2.parametros[5][2] = offsetCeroFuerzaDer;
                Utilidades2.parametros[5][3] = spanfd;
            }
            String logIzq = 
                "\nCero con offset (cero+offset) { "+rd2(valcalcero2)+" + "+rd2(offsetCeroFuerzaIzq)+" } = "+ ceroFuerzaIzq + " mV.\n"+
                "Fuerza con cero sin span(fuerzaSinCero-CeroConOffset) { "+rd2(auxfuerzai)+" - "+rd2(ceroFuerzaIzq)+" } = "+ fuerzaIzqSinSpan + " mV.\n"+
                "Fuerza total(FuerzaConCeroSinSpan*Span) { "+rd2(fuerzaIzqSinSpan)+" * "+rd2(spanfi)+" } = "+ (fuerzaIzqSinSpan* spanfi) + " NEWTON.\n";
            System.out.println(logIzq);
        }

        imprimirDatosHastaElMomento();
        
        if (freAux == false) {
            bc = fuerzasfd.size();
        } else {
            bc = fuerzasfdAux.size();
        }
        this.Datos1.clear();
        this.Datos2.clear();
        this.Datosfil1.clear();
        this.Datosfil2.clear();
    }

    public static double rd2(double valor) { // Redondeo a 2 decimales
        BigDecimal bd = new BigDecimal(Double.toString(valor));
        bd = bd.setScale(2, RoundingMode.HALF_UP); // Redondeo clásico
        return bd.doubleValue();
    }

    public void MedirFuerzaVertical(String lado) throws InterruptedException {
        //double ab = CalcularMaximo(Datos1);
        double auxfuerzadVariable, auxfuerzaiVariable;
        int bc;
        long s;
        LabelInfo.setText("MIDIENDO FUERZA VERTICAL...!");
        Thread.sleep(teporizadorInercia);
        //  Thread.sleep(1777);
        comandoSUSP();
        for (i = 0; i < 12; i++) {
            if (i == 1) {
                jProgressBar1.setMaximum(12);
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString(Math.round((i + 1) * 8.334) + "%");
            Thread.sleep(500);
        }
        Thread.sleep(teporizadorInercia);
        CapturarDatos("fuerzaVertical");
        comandoSTOP();
        switch (lado) {
            case "derecho":
                if (enablebackup) {
                    System.out.println("ENTRE BACKUP DERECHO BRUTO ");
                    try {
                        if (ejemedido == 1) {
                            regdatosfvd1.write(">>> .******* VALORES RECOGIDOS EN BRUTO .<<<<<**** ");
                            regdatosfvd1.newLine();
                            regdatosfvd1.write(">>> .**** FACTOR AJUSTE: " + spanpdDD + " ****");
                            regdatosfvd1.newLine();
                            regdatosfvd1.write(">> CANT. VALORES <<" + Datos3.size());
                            regdatosfvd1.newLine();
                            regdatosfvd1.flush();
                            s = Datos3.size();
                            for (t = 0; t < s; t++) {
                                regdatosfvd1.write(Datos3.get(t).toString());
                                regdatosfvd1.newLine();
                                regdatosfvd1.flush();
                            }
                            regdatosfvd1.write(">>> FIN  DE ARCHIVO  DE VALORES BRUTO <<< ");
                            regdatosfvd1.newLine();
                            regdatosfvd1.flush();
                        }
                        if (ejemedido == 2) {
                            regdatosfvd2.write(">>> .******* VALORES RECOGIDOS EN BRUTO .<<<<<**** ");
                            regdatosfvd2.newLine();
                            regdatosfvd2.write(">>> .**** FACTOR AJUSTE: " + spanpdDD + " ****");
                            regdatosfvd2.newLine();
                            regdatosfvd2.write(">> CANT. VALORES <<" + Datos3.size());
                            regdatosfvd2.newLine();
                            regdatosfvd2.flush();
                            s = Datos3.size();
                            for (t = 0; t < s; t++) {
                                regdatosfvd2.write(Datos3.get(t).toString());
                                regdatosfvd2.newLine();
                                regdatosfvd2.flush();
                            }
                            regdatosfvd2.write(">>> FIN  DE ARCHIVO  DE VALORES BRUTO <<< ");
                            regdatosfvd2.newLine();
                            regdatosfvd2.flush();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension derecha en el backup " + ex);
                    }
                }
                // hacer paso a paso para mirar comportamiento
                // accion de mejora 

                //
                armonizar(Datos3, valcalcero3);
                if (enablebackup) {
                    try {
                        if (ejemedido == 1) {
                            System.out.println("ENTRE BACKUP ARMONIZAR ");
                            regdatosfvd1.write(">>> .******* VALORES RECOGIDOS ARMONIZAR .<<<<<**** ");
                            regdatosfvd1.newLine();
                            regdatosfvd1.write(">> CANT. VALORES <<" + Datos3.size());
                            regdatosfvd1.newLine();
                            regdatosfvd1.flush();
                            s = Datos3.size();
                            s = s - 0;
                            for (t = 0; t < s; t++) {
                                regdatosfvd1.write(Datos3.get(t).toString());
                                regdatosfvd1.newLine();
                                regdatosfvd1.flush();
                            }
                            regdatosfvd1.write(">>> FIN  DE ARCHIVO DE VALORES ARMONIZADOS <<<<< ");
                            regdatosfvd1.newLine();
                            regdatosfvd1.flush();
                        } else if (ejemedido == 2) {
                            System.out.println("ENTRE BACKUP ARMONIZAR ");
                            regdatosfvd2.write(">>> .******* VALORES RECOGIDOS ARMONIZAR .<<<<<**** ");
                            regdatosfvd2.newLine();
                            regdatosfvd2.write(">> CANT. VALORES <<" + Datos3.size());
                            regdatosfvd2.newLine();
                            regdatosfvd2.flush();
                            s = Datos3.size();
                            s = s - 0;
                            for (t = 0; t < s; t++) {
                                regdatosfvd2.write(Datos3.get(t).toString());
                                regdatosfvd2.newLine();
                                regdatosfvd2.flush();
                            }
                            regdatosfvd2.write(">>> FIN  DE ARCHIVO DE VALORES ARMONIZADOS <<<<< ");
                            regdatosfvd2.newLine();
                            regdatosfvd2.flush();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension derecha en el backup " + ex);
                    }
                }
                // Datosfil1 = filtrar(Datos3);
                s = Datos3.size();//Datosfil1.size();
                //s=Datos3.size() -269;
                //Obtiene el valor minimo de la lista de las fuerzas
                double sum = 0;
                int contoff = 0;
                for (i = 0; i < Datos3.size(); i++) {
                    sum += Datos3.get(i);
                }
                sum = sum / Datos3.size();
                for (i = 0; i < Datos3.size(); i++) {
                    if ((Math.abs(sum - Datos3.get(i)) / Datos3.size()) > 0.30) {//0.65
                        contoff += 1;
                    }
                }
                //
                System.out.println("Vamos a descartar los " + contoff + " primeros datos");
                auxfuerzadVariable = CalcularMinimo(Datos3, contoff);

                System.out.println("-----------------------------");
                System.out.println("- AUXILIAR FUERZA DERECHA ---");
                System.out.println("--" + auxfuerzadVariable + "--");
                System.out.println("-----------------------------");

                System.out.println(">>>>>>>>>>Valor Minimo DEL EJE :Derecho " + auxfuerzadVariable);
                if (enablebackup) {
                    try {
                        if (ejemedido == 1) {
                            regdatosfvd1.write("TEST. -el cero de peso derecho es: " + valcalcero3);
                            regdatosfvd1.newLine();
                            regdatosfvd1.write("el span de peso derecho es: " + spanpd);
                            regdatosfvd1.newLine();
                            regdatosfvd1.flush();
                            System.out.println("Valor de s(DATOS): " + s);
                            for (t = 0; t < s; t++) {
                                regdatosfvd1.write(Datos3.get(t).toString());
                                regdatosfvd1.newLine();
                                regdatosfvi1.flush();
                            }
                            regdatosfvd1.write(">>>>>>>>>>Valor Minimo DEL EJE 1: " + auxfuerzadVariable);
                            regdatosfvd1.newLine();
                            regdatosfvd1.close();
                        } else if (ejemedido == 2) {
                            regdatosfvd2.write("el cero de peso derecho es: " + valcalcero3);
                            regdatosfvd2.newLine();
                            regdatosfvd2.write("el span de peso derecho es: " + spanpd);
                            regdatosfvd2.newLine();
                            regdatosfvd2.flush();
                            for (t = 0; t < s; t++) {
                                regdatosfvd2.write(Datos3.get(t).toString());
                                regdatosfvd2.newLine();
                                regdatosfvd2.flush();
                            }
                            regdatosfvd2.write(">>>>>>>>>>Valor Minimo DEL EJE 2: " + auxfuerzadVariable);
                            regdatosfvd2.newLine();
                            regdatosfvd2.close();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension derecha en el backup " + ex);
                    }
                }

                if ((auxfuerzadVariable - valcalcero3) < 0) {
                    fuerzasvd.add(0.0);
                    System.out.println("ENTRO EN CALCULOS CERO DE FUERZAS ..!");
                } else {//pesosd.get(ejemedido-1)
                    Double factorPesoMov = auxfuerzadVariable - valcalcero3;
                    System.out.println("de nuevo el cero de fuerza derecho " + (valcalcero3) + " MV ");
                    System.out.println(">>>>>>>>>>Valor Minimo :Derecho Aplicando Resta del Cero" + factorPesoMov);

                    if (ejemedido == 1) {
                        System.out.println(">>>>>>>>>>Spand Dinamico Derecho Delantero " + spanpdDD);
//                        fuerzasvd.add((factorPesoMov-valcalcero3)*spanpdDD);
                        fuerzasvd.add(factorPesoMov * spanpdDD);
                        System.out.println(">>>>>>>>>>Valor Fuerza VerticalDerecha Delantero" + factorPesoMov * spanpdDD);

                    } else {
                        System.out.println(">>>>>>>>>>Spand Dianmico Derecho Trasero " + spanpdTD);
//                        fuerzasvd.add((factorPesoMov-valcalcero3)*spanpdTD);
                        fuerzasvd.add(factorPesoMov * spanpdTD);
                        System.out.println(">>>>>>>>>>Valor Fuerza Vertical  Trasera" + factorPesoMov * spanpdTD);
                    }
                }
                bc = fuerzasvd.size();
                break;
            case "izquierdo":
                if (enablebackup) {
                    System.out.println("ENTRE BACKUP IZQUIERDO BRUTO ");
                    try {
                        if (ejemedido == 1) {
                            regdatosfvi1.write(">>> .******* VALORES RECOGIDOS EN BRUTO .<<<<<**** ");
                            regdatosfvi1.newLine();
                            regdatosfvi1.write(">> CANT. VALORES <<" + Datos4.size());
                            regdatosfvi1.newLine();
                            regdatosfvi1.flush();
                            s = Datos4.size();
                            for (t = 0; t < s; t++) {
                                regdatosfvi1.write(Datos4.get(t).toString());
                                regdatosfvi1.newLine();
                                regdatosfvi1.flush();
                            }
                            regdatosfvi1.write(">>> FIN  DE ARCHIVO  DE VALORES BRUTO <<< ");
                            regdatosfvi1.newLine();
                            regdatosfvi1.flush();
                        } else if (ejemedido == 2) {
                            regdatosfvi2.write(">>> .******* VALORES RECOGIDOS EN BRUTO .<<<<<**** ");
                            regdatosfvi2.newLine();
                            regdatosfvi2.write(">>> .**** FACTOR AJUSTE: " + spanpdDD + " ****");
                            regdatosfvi2.newLine();
                            regdatosfvi2.write(">> CANT. VALORES <<" + Datos4.size());
                            regdatosfvi2.newLine();
                            regdatosfvi2.flush();
                            s = Datos4.size();
                            for (t = 0; t < s; t++) {
                                regdatosfvi2.write(Datos4.get(t).toString());
                                regdatosfvi2.newLine();
                                regdatosfvi2.flush();
                            }
                            regdatosfvi2.write(">>> FIN  DE ARCHIVO  DE VALORES BRUTO <<< ");
                            regdatosfvi2.newLine();
                            regdatosfvi2.flush();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension derecha en el backup " + ex);
                    }
                }
                armonizar(Datos4, valcalcero4);
                if (enablebackup) {
                    try {
                        if (ejemedido == 1) {
                            regdatosfvi1.write(">>> .******* VALORES RECOGIDOS ARMONIZADOS .<<<<<**** ");
                            regdatosfvi1.newLine();
                            regdatosfvi1.write(">>> .**** FACTOR AJUSTE: " + spanpdDD + " ****");
                            regdatosfvi1.newLine();
                            regdatosfvi1.write(">> CANT. VALORES <<" + Datos4.size());
                            regdatosfvi1.newLine();
                            regdatosfvi1.flush();
                            s = Datos4.size();
                            //       s= s-269;
                            for (t = 0; t < s; t++) {
                                regdatosfvi1.write(Datos4.get(t).toString());
                                regdatosfvi1.newLine();
                                regdatosfvi1.flush();
                            }
                            regdatosfvi1.write(">>> FIN  DE ARCHIVO  DE VALORES ARMONIZADOS <<< ");
                            regdatosfvi1.newLine();
                            regdatosfvi1.flush();
                        } else if (ejemedido == 2) {
                            regdatosfvi2.write(">>> .******* VALORES RECOGIDOS ARMONIZADOS .<<<<<**** ");
                            regdatosfvi2.newLine();
                            regdatosfvi2.write(">>> .**** FACTOR AJUSTE: " + spanpdDD + " ****");
                            regdatosfvi2.newLine();
                            regdatosfvi2.write(">> CANT. VALORES <<" + Datos4.size());
                            regdatosfvi2.newLine();
                            regdatosfvi2.flush();
                            s = Datos4.size();
                            //       s= s-269;
                            for (t = 0; t < s; t++) {
                                regdatosfvi2.write(Datos4.get(t).toString());
                                regdatosfvi2.newLine();
                                regdatosfvi2.flush();
                            }
                            regdatosfvi2.write(">>> FIN  DE ARCHIVO  DE VALORES ARMONIZADOS <<< ");
                            regdatosfvi2.newLine();
                            regdatosfvi2.flush();
                        }

                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension derecha en el backup " + ex);
                    }
                }
                //Datosfil2 = filtrar(Datos4);
                s = Datos4.size();// Datosfil2.size();

                sum = 0;
                contoff = 0;
                for (i = 0; i < s; i++) {
                    sum += Datos4.get(i);
                }
                sum = sum / Datos4.size();
                for (i = 0; i < s; i++) {
                    if ((Math.abs(sum - Datos4.get(i)) / Datos4.size()) > 0.65) {
                        contoff += 1;
                    }
                }
                System.out.println("De la izquierda se eliminan " + contoff + " datos iniciales");
                auxfuerzaiVariable = CalcularMinimo(Datos4, contoff);
                if (enablebackup) {
                    try {
                        if (ejemedido == 1) {
                            regdatosfvi1.write("el cero de peso izquierdo 1 es: " + valcalcero4);
                            regdatosfvi1.newLine();
                            regdatosfvi1.write("el span de peso izquierdo 1 es: " + spanpi);
                            regdatosfvi1.newLine();
                            regdatosfvi1.flush();
                            for (t = 0; t < s; t++) {
                                regdatosfvi1.write(Datos4.get(t).toString());
                                regdatosfvi1.newLine();
                            }
                            regdatosfvi1.write(">>>>>>>>>>Valor Minimo DEL EJE 1: " + auxfuerzaiVariable);
                            regdatosfvi1.newLine();
                            regdatosfvi1.close();
                        } else if (ejemedido == 2) {
                            regdatosfvi2.write("el cero de peso izquierdo 2 es: " + valcalcero4);
                            regdatosfvi2.newLine();
                            regdatosfvi2.write("el span de peso izquierdo 2 es: " + spanpi);
                            regdatosfvi2.newLine();
                            regdatosfvi2.flush();
                            for (t = 0; t < s; t++) {
                                regdatosfvi2.write(Datos4.get(t).toString());
                                regdatosfvi2.newLine();
                            }
                            regdatosfvi2.write(">>>>>>>>>>Valor Minimo DEL EJE 2: " + auxfuerzaiVariable);
                            regdatosfvi2.newLine();
                            regdatosfvi2.close();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension izquierda en el backup " + ex);
                    }
                }

                if ((auxfuerzaiVariable - valcalcero4) < 0) {
                    System.out.println("La operacion algebraica es " + (auxfuerzaiVariable - valcalcero4));
                    fuerzasvi.add(0.0);
                } else {    //pesosi.get(ejemedido-1);                
                    Double factorPesoMov = auxfuerzaiVariable - valcalcero4;
                    System.out.println(">>>>>>>>>>Valor Minimo :Izquierdo Aplicando Resta del Cero" + factorPesoMov);
                    if (ejemedido == 1) {
                        System.out.println(">>>>>>>>>>Spand Dinamico Izquierdo Delantero " + spanpdDI);
                        //fuerzasvi.add((factorPesoMov-valcalcero4)*spanpdDD);
                        fuerzasvi.add(factorPesoMov * spanpdDI);
                        System.out.println(">>>>>>>>>>Valor Fuerza VerticalDerecha Delantero" + factorPesoMov * spanpdDI);
                    } else {
                        System.out.println(">>>>>>>>>>Spand Dinamico Izquierdo Izquierdo " + spanpdTI);
                        //fuerzasvi.add((factorPesoMov-valcalcero4)*spanpdDD); 
                        fuerzasvi.add(factorPesoMov * spanpdTI);
                        System.out.println(">>>>>>>>>>Valor Fuerza VerticalIzquierdo Trasero" + factorPesoMov * spanpdTI);
                    }
                }
                bc = fuerzasvi.size();
                System.out.println("de nuevo el cero de fuerza izquierdo " + (valcalcero4) + " MV");
                System.out.println("fuerza sin calibrar " + ejemedido + " izquierda: " + (auxfuerzaiVariable) + " MV");
                System.out.println("fuerza del eje " + ejemedido + " izquierda: " + (fuerzasvi.get(bc - 1)) + "");
                break;
        }
        Datos3.clear();
        Datos4.clear();
        Datosfil1.clear();
        Datosfil2.clear();
    }

    public void MedirPeso() throws InterruptedException {
        double pesoDerMv, pesoIqzMv;
        int bc;
        long NumDatosDer, NumDatosIzq;
        LabelInfo.setText("MIDIENDO PESO...!");
        mostrarProgressBard();
        CapturarDatos("ceros");
        NumDatosDer = Datos3.size();
        NumDatosIzq = Datos4.size();
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }

        guardarDatosRespaldo(NumDatosDer, NumDatosIzq);

        pesoDerMv = CalcularMedia(Datos3);
        pesoIqzMv = CalcularMedia(Datos4);

        mostrarMedidasPesos(pesoDerMv, pesoIqzMv);

        System.out.println("\n\npesoDerMv: " + pesoDerMv + " mV. Valcalcero3: " + valcalcero3 + " mV. Spanpd: " + spanpd + ". offsetCeroDer: "+offsetCeroPesoDer +" mV.");
        double valorCeroDer = valcalcero3 + offsetCeroPesoDer;
        Double pesoSinSpamDer = Math.abs(pesoDerMv - valorCeroDer) ;
        System.out.println("pesoIqzMv: " + pesoIqzMv + " mV. Valcalcero4: " + valcalcero4 + " mV. Spanpi: " + spanpi + ". offsetCeroIzq: "+offsetCeroPesoIzq +" mV.");
        double valorCeroIzq = valcalcero4 + offsetCeroPesoIzq;
        Double pesoSinSpamIzq = Math.abs(pesoIqzMv - valorCeroIzq) ;
        
        pesosd.add(pesoSinSpamDer);
        pesosi.add(pesoSinSpamIzq);

        String logDer = 
            "logDerecho "+ejeORueda+ruedasOEjes[ejemedido-1]+"\n"+
            "Cero total(valorCero+offsetCero){ "+rd2(valcalcero3)+" + "+rd2(offsetCeroPesoDer)+" } = "+ valorCeroDer+ "mV.\n"+
            "Peso con cero sin span(pesoDerMv-ceroTotal){ "+rd2(pesoDerMv)+" - "+rd2(valorCeroDer)+" } = "+ pesoSinSpamDer+" mV.\n"+
            "Peso total(pesoSinSpam*spanpd){ "+rd2(pesoSinSpamDer)+" * "+rd2(spanpd)+" } = "+ (pesoSinSpamDer * spanpd)+" Newton.\n";
        System.out.println(logDer);

        String logIzq = 
            "logIzquierdo "+ejeORueda+ruedasOEjes[ejemedido-1]+"\n"+
            "Cero total(valorCero+offsetCero){ "+rd2(valcalcero4)+" + "+rd2(offsetCeroPesoIzq)+" } = "+ valorCeroIzq+ "mV.\n"+
            "Peso con cero sin span(pesoIqzMv-ceroTotal){ "+rd2(pesoIqzMv)+" - "+rd2(valorCeroIzq)+" } = "+ pesoSinSpamIzq+" mV.\n"+
            "Peso total(pesoSinSpam*spanpi){ "+rd2(pesoSinSpamIzq)+" * "+rd2(spanpi)+" } = "+ (pesoSinSpamIzq * spanpi)+" Newton.\n";
        System.out.println(logIzq);

        Utilidades2.medidasCuatrimotoPequena[ejemedido-1][0] = pesoSinSpamDer * spanpd;
        Utilidades2.medidasCuatrimotoPequena[ejemedido+1][0] = pesoSinSpamIzq * spanpi;

        bc = pesosd.size();
        System.out.println("\n\nLiviano solamente:\n peso del eje " + ejemedido + " derecho e izquierdo: " + (pesosd.get(bc - 1) * spanpd) + " Newton " + (pesosi.get(bc - 1) * spanpi) + " Newton");
        Thread.sleep(100);
        Datos3.clear();
        Datos4.clear();
    }

    public void MedirPeso(String lado) throws InterruptedException {
        boolean isDerecho = lado.equalsIgnoreCase("derecho");
        double pesoDerMv, pesoIqzMv;
        int bc;
        long NumDatosDer, NumDatosIzq;
        LabelInfo.setText("MIDIENDO PESO...!");
        mostrarProgressBard();
        CapturarDatos("ceros");
        NumDatosDer = Datos3.size();
        NumDatosIzq = Datos4.size();
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }

        guardarDatosRespaldo(NumDatosDer, NumDatosIzq);

        Double pesoSinSpamDer = 0.0;
        Double pesoSinSpamIzq = 0.0;
        if (isDerecho) {
            pesoDerMv = CalcularMediana(Datos3);
            pesoIqzMv = 0.0;
            double valorCeroDer = valcalcero3 + offsetCeroPesoDer;
            pesoSinSpamDer = Math.abs(pesoDerMv - valorCeroDer) ;
            System.out.println("\n\nPESO DERECHO "+ejeORueda+ruedasOEjes[ejemedido-1]+"--(RECORDAR QUE LOS VALORES NEGATIVOS SE LES SACA VALOR ABSOLUTO Y LOS CALCULOS TIENEN EN CUENTA TODAS LAS CIFRAS DECIMALES)---------------------\n");
            System.out.println("pesoDerMv: " + pesoDerMv + " mV. Valor cero: " + valcalcero3 + " mV. Spanpd: " + spanpd + ". offsetCeroDer: "+offsetCeroPesoDer);

            String logDer = 
                "Cero total(valorCero+offsetCero){ "+rd2(valcalcero3)+" + "+rd2(offsetCeroPesoDer)+" } = "+ valorCeroDer+ "mV.\n"+
                "Peso con cero sin span(pesoDerMv-ceroTotal){ "+rd2(pesoDerMv)+" - "+rd2(valorCeroDer)+" } = "+ pesoSinSpamDer+" mV.\n"+
                "Peso total(pesoSinSpam*spanpd){ "+rd2(pesoSinSpamDer)+" * "+rd2(spanpd)+" } = "+ (pesoSinSpamDer * spanpd)+" Newton.\n";

            System.out.println(logDer);
            Utilidades2.medidasCuatrimotoPequena[ejemedido-1][0] = pesoSinSpamDer * spanpd;
        } else {
            pesoDerMv = 0.0;
            pesoIqzMv = CalcularMediana(Datos4);
            double valorCeroIzq = valcalcero4 + offsetCeroPesoIzq;
            pesoSinSpamIzq = Math.abs(pesoIqzMv - valorCeroIzq) ;
            System.out.println("\n\nPESO IZQUIERDO "+ejeORueda+ruedasOEjes[ejemedido-1]+"--(RECORDAR QUE LOS VALORES NEGATIVOS SE LES SACA VALOR ABSOLUTO Y LOS CALCULOS TIENEN EN CUENTA TODAS LAS CIFRAS DECIMALES)-------------------\n");
            System.out.println("\n\npesoIqzMv: " + pesoIqzMv + " mV. Valcalcero4: " + valcalcero4 + " mV. Spanpi: " + spanpi + ". offsetCeroIzq: "+offsetCeroPesoIzq);
        
            String logIzq = 
                "Cero total(valorCero+offsetCero){ "+rd2(valcalcero4)+" + "+rd2(offsetCeroPesoIzq)+" } = "+ valorCeroIzq+ "mV.\n"+
                "Peso con cero sin span(pesoIqzMv-ceroTotal){ "+rd2(pesoIqzMv)+" - "+rd2(valorCeroIzq)+" } = "+ pesoSinSpamIzq+" mV.\n"+
                "Peso total(pesoSinSpam*spanpi){ "+rd2(pesoSinSpamIzq)+" * "+rd2(spanpi)+" } = "+ (pesoSinSpamIzq * spanpi)+" Newton.\n";

            System.out.println(logIzq);
            Utilidades2.medidasCuatrimotoPequena[ejemedido-1][0] = pesoSinSpamIzq * spanpi;
        }

        imprimirDatosHastaElMomento();

        mostrarMedidasPesos(pesoDerMv, pesoIqzMv);

        pesosd.add(pesoSinSpamDer);
        pesosi.add(pesoSinSpamIzq);

        bc = pesosd.size();
        System.out.println("peso del eje " + ejemedido + " derecho e izquierdo: " + (pesosd.get(bc - 1) * spanpd) + " Newton " + (pesosi.get(bc - 1) * spanpi) + " Newton");
        Thread.sleep(100);
        Datos3.clear();
        Datos4.clear();
    }

    private void imprimirDatosHastaElMomento(){
        System.out.println("DATOS HASTA EL MOMENTO START------------------------------------\n");
        for (int i = 0; i < Utilidades2.medidasCuatrimotoPequena.length; i++) {
            if (i<4) System.out.println(ejeORueda + ruedasOEjes[i] + ": Peso = " + Utilidades2.medidasCuatrimotoPequena[i][0] + ", Fuerza = " + Utilidades2.medidasCuatrimotoPequena[i][1]);
            else System.out.println("Fuerza frenoMano der = " + Utilidades2.medidasCuatrimotoPequena[i][0] + ", Fuerza frenoMano izq = " + Utilidades2.medidasCuatrimotoPequena[i][1]);
        }
        System.out.println("DATOS HASTA EL MOMENTO END------------------------------------\n");
    }

    private void guardarDatosRespaldo(double numDatosDer, double numDatosIzq) {
        if (enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatospd1.write("el cero de peso derecho es: " + valcalcero3);
                    regdatospd1.newLine();
                    regdatospd1.write("el span de peso derecho es: " + spanpd);
                    regdatospd1.newLine();
                    regdatospd1.flush();
                    for (t = 0; t < numDatosDer; t++) {
                        regdatospd1.write(Datos3.get(t).toString());
                        regdatospd1.newLine();
                    }
                    regdatospd1.close();
                } else if (ejemedido == 2) {
                    regdatospd2.write("el cero de peso derecho es: " + valcalcero3);
                    regdatospd2.newLine();
                    regdatospd2.write("el span de peso derecho es: " + spanpd);
                    regdatospd2.newLine();
                    regdatospd2.flush();
                    for (t = 0; t < numDatosDer; t++) {
                        regdatospd2.write(Datos3.get(t).toString());
                        regdatospd2.newLine();
                    }
                    regdatospd2.close();
                }
                if (ejemedido == 1) {
                    regdatospi1.write("el cero de peso izquierdo es: " + valcalcero4);
                    regdatospi1.newLine();
                    regdatospi1.write("el span de peso izquierdo es: " + spanpi);
                    regdatospi1.newLine();
                    regdatospi1.flush();
                    for (t = 0; t < numDatosIzq; t++) {
                        regdatospi1.write(Datos4.get(t).toString());
                        regdatospi1.newLine();
                    }
                    System.out.println("Tamaño  " + numDatosIzq);
                    regdatospi1.close();
                } else if (ejemedido == 2) {
                    regdatospi2.write("el cero de peso izquierdo es: " + valcalcero4);
                    regdatospi2.newLine();
                    regdatospi2.write("el span de peso izquierdo es: " + spanpi);
                    regdatospi2.newLine();
                    regdatospi2.flush();
                    for (t = 0; t < numDatosIzq; t++) {
                        regdatospi2.write(Datos4.get(t).toString());
                        regdatospi2.newLine();
                    }
                    regdatospi2.close();
                }
            } catch (IOException ex) {
                System.out.println("No se pudo escribir el dato de peso en el backup " + ex);
            }
        }
    }

    private void mostrarProgressBard() throws InterruptedException{
        for (i = 0; i < 10; i++) {
            if (i == 1) {
                jProgressBar1.setMaximum(10);
            }
            if (i == 7) {
                puerto.setFlujoEnt(puerto);
                comandoCEROS(0);
                Thread.sleep(170);
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) * 10 + "%");
            Thread.sleep(500);
        }
    }

    private void mostrarMedidasPesos(double pesoDer, double pesoIzq) {
        System.out.println("\n\n\nTabla de Valores MIDIENDO PESOS ");
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
        System.out.println("valcalcero4 es: " + valcalcero4);
        System.out.println("*****************************");
        System.out.println("spanpd es: " + spanpd);
        System.out.println("spanpi es: " + spanpi);
        System.out.println("*****************************");
        System.out.println("Captura Peso Derecho es: " + pesoDer + " Mv");
        System.out.println("Captura Peso Izquierdo es: " + pesoIzq + " Mv");
        System.out.println("*****************************");
        System.out.println("peso medido en lado Derecho " + ((pesoDer - valcalcero3) * spanpd) + " Newton");
        System.out.println("peso medido en lado Izquierdo " + ((pesoIzq - valcalcero4) * spanpi) + " Newton");
        System.out.println("----------------------");
        System.out.println("----------------------\n\n\n");
    }

    public void MedirDesviacion() throws InterruptedException {
        System.out.println("entre metodo de desviacion");
        double auxdesv;
        int bc;
        LabelInfo.setText("Midiendo Desviación...");
        System.out.println(" voy a craer al puerto");
        puerto.clearFlujoEnt();
        puerto.setFlujoEnt(puerto);
        Thread.sleep(187);
        System.out.println(" confirmado clear del puerto");
        Datos7.clear();
        Datosfil1.clear();
        comandoDESV();
        System.out.println(" call al cmd desviacion");
        for (i = 0; i < 10; i++) {
            System.out.println(" estoe en bucle con " + i);
            if (i == 1) {
                jProgressBar1.setMaximum(10);
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) * 10 + "%");
            Thread.sleep(500);
        }
        System.out.println(" sali del bucle ");
        comandoSTOP();
        System.out.println(" call cmd stop ");
        CapturarDatos("desviacion");
        System.out.println(" capturo datos ");
        Datosfil1 = filtrar(Datos7);
        auxdesv = CalcularDesviacion(valcalcero7, Datosfil1);
        //Se calcula la desviacion teniendo en cuenta el span y el ancho de placa
        System.out.println("-------------------------Valor medido en voltios-----------------" + auxdesv);
        System.out.println("-------------------------Valor calculado en voltios-----------------" + valcalcero7);
        System.out.println("-------------------------Valor span Desvioacion en nada-----------------" + spand);
        System.out.println("-------------------------Valor ancho plancha-----------------" + anchoplacadesv);
        Double valDesv = (((auxdesv - valcalcero7) * spand) / anchoplacadesv);
        System.out.println("---------Resultado" + valDesv);
        desviaciones.add(valDesv);
        bc = desviaciones.size();
        System.out.println(".-******-.");
        System.out.println("desviacion del eje " + ejemedido);
        System.out.println(" auxdesv " + (auxdesv));
        System.out.println(" EL CERO IS " + valcalcero7);
        System.out.println(" EL SPAN DE DESVIACION " + spand);
        System.out.println("ANCHO DE PLANCHA " + anchoplacadesv);
        System.out.println("opr. (auxdesv - valcalcero7) * spand / anchoplacadesv " + valDesv);

        System.out.println(".-******-.");
        LabelInfo.setText("Desviación del eje No." + this.ejemedido + " medida");
        //auxvalmeddesv=(auxvalmeddesv-valcalcero7)*spand/(anchoplacadesv);
        //desviacion.add(auxvalmeddesv*factorescala);
        Datos7.clear();
        Datosfil1.clear();
    }

    public void EsperarPeso() throws InterruptedException {
        double persoDer, pesoIzq;
        double calPesoDerConSpan = 0;
        double calPesoIzqConSpan = 0;
        System.out.println("Entrando al metodo EsperaPeso()");
        //LabelInfo.setText("ESPERANDO EJE " + ejemedido + " EN LA PLANCHA DE PESO..!");
        //LabelAviso.setText("PLANCHA");
        timeraviso.start();
        comandoCEROS(0);
        Thread.sleep(500);
        int count = 0;
        int count2 = 0;

        double persoDerRef = 0;
        double pesoIzqRef = 0;
        boolean recalculoCero = false;

        while (true) {
            CapturarDatos("ceros");
            persoDer = CalcularMedia(Datos3);
            pesoIzq = CalcularMedia(Datos4);

            double valorCeroDer = valcalcero3 + offsetCeroPesoDer;
            double valorCeroIzq = valcalcero4 + offsetCeroPesoIzq;

            double calPesoDerSinSpan = persoDer - valorCeroDer;
            double calPesoIzqSinSpan = pesoIzq - valorCeroIzq;


            if (((calPesoDerSinSpan) < 0 && (calPesoIzqSinSpan) < 0 && (calPesoDerSinSpan) > -20 && (calPesoIzqSinSpan) > -20 && count == 0) || recalculoCero) { //valores entre 15 y -15 para comprobar que los valores de peso en cero son correctos
                System.out.println("Esperando eje " + ejemedido + " en la plancha de peso..!");
                if (count2 == 0) {
                    LabelInfo.setText("ESPERANDO EJE " + ejemedido + " EN LA PLANCHA DE PESO..!");
                    LabelAviso.setText("PLANCHA");
                }else {
                    LabelInfo.setText("DETECTANDO PESO..!");
                    LabelAviso.setText(count2 * 20 + "% persoDerRef en "+ Math.abs(persoDerRef-persoDer) + " pesoIzqRef en " + Math.abs(pesoIzqRef-pesoIzq) + "(0 a 20) ");
                }
                recalculoCero = true;
                count = 0;
            } else {
                System.out.println("Valores de peso en la plancha fuera de rango, reiniciando referencias de peso");
                LabelInfo.setText("RECALCULANDO CERO, NO COLOQUE PESO ");
                LabelAviso.setText(count * 20 + "%");
                if(count == 0) count++;
            }

            if (persoDer + 20 > persoDerRef && pesoIzq + 20 > pesoIzqRef && persoDer - 20 < persoDerRef && pesoIzq - 20 < pesoIzqRef && count != 0) { //valores entre 10 y -10
                count++;
            } else if (count != 0) {
                System.out.println("Reiniciando contador a 1. Peso persoDerRef: " + persoDerRef + ", Peso pesoIzqRef: " + pesoIzqRef + ", Peso persoDer: " + persoDer + ", Peso pesoIzq: " + pesoIzq);
                persoDerRef = persoDer;
                pesoIzqRef = pesoIzq;
                count = 1;
            }

            System.out.println("Valor de count: " + count);

            if (count > 4) {
                System.out.println("Reiniciando referencias de peso porque se detecto un cambio brusco en los valores de cero del peso");
                valcalcero3 = persoDerRef + 20;
                valcalcero4 = pesoIzqRef + 20;
                count = 0;
                LabelInfo.setText("CERO CAMBIADO CON ÉXITO");
                LabelAviso.setText("PLANCHA");
                recalculoCero = true;
            }



            mostrarInformacionSensores(persoDer, pesoIzq);

            

            calPesoDerConSpan = (calPesoDerSinSpan) * spanpd;
            calPesoIzqConSpan = (calPesoIzqSinSpan) * spanpi;

            if (isReiniciarTrama(calPesoDerConSpan, calPesoIzqConSpan, "ambos")) {
                calPesoDerConSpan = 0;
                calPesoIzqConSpan = 0;
                reiniciarTrama();
            } else {
                puerto.clearFlujoEnt();
                puerto.setFlujoEnt(puerto);
                Thread.sleep(540);
            }
            limpiarDatosPeso();
            System.out.println("umbralPeso: " + umbralPeso);
            System.out.println("activarUmbralPeso: " + activarUmbralPeso);
            if (activarUmbralPeso || (calPesoDerConSpan >= umbralPeso && calPesoIzqConSpan >= umbralPeso && recalculoCero)) {
                if (activarUmbralPeso) {
                    CMensajes.mensajeAdvertencia("Si esta es una prueba real, por favor aborte la prueba y contacte al soporte Soltelec.\n"+
                    "Propiedad 'activarUmbralPesoDesdeSoftware' de peso activada. Desactivarla desde el archivo de calibracion.properties poniendola en false\n"+
                    "Esto solo se usa para pruebas de calibración en cero.\n");
                }
                LabelInfo.setText("DETECTANDO PESO..!");
                LabelAviso.setText("PLANCHA");
                count2++;
                System.out.println("Peso detectado en la plancha, count2: " + count2);
            }else {
                count2 = 0;
                System.out.println("Peso no detectado en la plancha, count2 reiniciado a 0");
            }

            if (count2 >= 4) {
                System.out.println("Calibracion en cero terminada, se ha detectado peso en la plancha");
                break;
            }
        }
        System.out.println("  ");
        System.out.println("  ");
        System.out.println("Me sali del ciclo del UMBRAL");
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        limpiarDatosPeso();
        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
    }

    //Metodo para esperar el peso en la plancha de peso izquierdo o derecho pero no ambos
    public void EsperarPeso(String lado) throws InterruptedException {
        boolean isDerecho = lado.equalsIgnoreCase("derecho");
        double persoDer, pesoIzq;
        double calPesoDer = 0;
        double calPesoIzq = 0;
        System.out.println("Entrando al metodo EsperaPeso(String lado): " + lado);
        LabelInfo.setText("ESPERANDO "+ejeORueda + ruedasOEjes[ejemedido-1] + "EN LA PLANCHA DE PESO "+lado.toUpperCase()+"..!");
        LabelAviso.setText("PLANCHA");
        timeraviso.start();
        comandoCEROS(0);
        Thread.sleep(500);
        while (true) {
            CapturarDatos("ceros");

            if (isDerecho) {
                persoDer = CalcularMediana(Datos3);
                pesoIzq = 0;
                calPesoDer = (persoDer - (valcalcero3+offsetCeroPesoDer)) * spanpd;
                calPesoIzq = 0;
            }else{
                persoDer = 0;
                pesoIzq = CalcularMediana(Datos4);
                calPesoDer = 0;
                calPesoIzq = (pesoIzq - (valcalcero4+offsetCeroPesoDer)) * spanpi;
            }

            mostrarInformacionSensores(persoDer, pesoIzq);

            System.out.println("lado medido: " + lado);
            System.out.println("calPesoDer: " + calPesoDer);
            System.out.println("calPesoIzq: " + calPesoIzq);
            
            if (isReiniciarTrama(calPesoDer, calPesoIzq, lado)) {
                calPesoDer = 0;
                calPesoIzq = 0;
                reiniciarTrama();
            } else {
                puerto.clearFlujoEnt();
                puerto.setFlujoEnt(puerto);
                Thread.sleep(540);
            }
            limpiarDatosPeso();
            System.out.println("umbralPeso: " + umbralPeso);
            System.out.println("activarUmbralPeso: " + activarUmbralPeso);
            
            if (activarUmbralPeso || calPesoDer >= umbralPeso || calPesoIzq >= umbralPeso ) {
                if (activarUmbralPeso) {
                    CMensajes.mensajeAdvertencia("Si esta es una prueba real, por favor aborte la prueba y contacte al soporte Soltelec.\n"+
                    "Propiedad 'activarUmbralPesoDesdeSoftware' de peso activada. Desactivarla desde el archivo de calibracion.properties poniendola en false\n"+
                    "Esto solo se usa para pruebas de calibración en cero.\n");
                }
                break;
            }
        }
        System.out.println("  ");
        System.out.println("  ");
        System.out.println("Me sali del ciclo del UMBRAL");
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        limpiarDatosPeso();
        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
    }

    private void mostrarInformacionSensores(double pesomedd, double pesomedi) {
        String pesoDerEnMv = pesomedd == 0 ? "No se esta midiendo" : String.valueOf(pesomedd) + " mV"; // mV o Mv representa milivoltios
        String pesoIzqEnMv = pesomedi == 0 ? "No se esta midiendo" : String.valueOf(pesomedi) + " mV";// mV o Mv representa milivoltios
        System.out.println("\n\nTabla de Valores Leidos para el Comand Suspension");
        System.out.println("----------------------");
        System.out.println("valcalcero3 es: " + valcalcero3 + " mV");// mV o Mv representa milivoltios
        System.out.println("valcalcero4 es: " + valcalcero4 + " mV");// mV o Mv representa milivoltios
        System.out.println("spanpd es: " + spanpd);
        System.out.println("spanpi es: " + spanpi);
        System.out.println("Peso Medido Derecho sin cero: " + pesoDerEnMv);
        System.out.println("Peso Medido Izquierdo sin cero: " + pesoIzqEnMv);
        double pesoConOffsetDer = valcalcero3+offsetCeroPesoDer;
        double pesoConOffsetIzq = valcalcero4+offsetCeroPesoIzq;
        System.out.println("Cero con offset (ValorCero:"+valcalcero3+"+offset:"+offsetCeroPesoDer+"): " + (pesoConOffsetDer) + " mV"); 
        System.out.println("Cero con offset (ValorCero:"+valcalcero4+"+offset:"+offsetCeroPesoIzq+"): " + (pesoConOffsetIzq) + " mV");
        System.out.println("Peso Medido Derecho con cero: " + (pesomedd - pesoConOffsetDer) + " mV");
        System.out.println("Peso Medido Izquierdo con cero: " + (pesomedi - pesoConOffsetIzq) + " mV");
        System.out.println("Peso medido en lado Derecho con span: " + ((pesomedd - pesoConOffsetDer) * spanpd) + " Newton");
        System.out.println("Peso medido en lado Izquierdo con span: " + ((pesomedi - pesoConOffsetIzq) * spanpi) + " Newton");
        System.out.println("Umbral peso: " + umbralPeso + " Newton.");
        
        System.out.println("Total BYTES CANAL 3: " + Datos3.size());
        System.out.println("Total BYTES CANAL 4: " + Datos4.size());

    }

    private boolean isReiniciarTrama(double pesoDerEnMv, double pesoIzqEnMv, String lado) {
        if (lado.equalsIgnoreCase("derecho")) {
            return isReiniciarTramaDer(pesoDerEnMv);
        } else if (lado.equalsIgnoreCase("izquierdo")) {
            return isReiniciarTramaIqz(pesoIzqEnMv);
        } else{
            return isReiniciarTramaDer(pesoDerEnMv) || isReiniciarTramaIqz(pesoIzqEnMv);
        }
    }

    private boolean isReiniciarTramaDer(double pesoDerEnMv) {
        if (
            (Datos3.size() <= 21 ||
            pesoDerEnMv < 0 ||
            pesoDerEnMv == 0)
            && !activarUmbralPeso
        ) {
            System.out.println("Reiniciando trama Valores por alguna de las siguientes razones plancha derecha: ");
            System.out.println("-Trama por derecho debe ser mayor a 21. Valores: " + Datos3.size());
            System.out.println("-Los valores de peso no pueden ser menor o igual a cero. Valor: " + pesoDerEnMv);
            return true;
        } return false;
    }

    private boolean isReiniciarTramaIqz(double pesoIzqEnMv) {
        if (
            (Datos4.size() <= 21 || 
            pesoIzqEnMv < 0 ||
            pesoIzqEnMv == 0) 
            && !activarUmbralPeso
        ) {
            System.out.println("Reiniciando trama Valores por alguna de las siguientes razones plancha izquierda: ");
            System.out.println("-Trama por izquierdo debe ser mayor a 21. Valores: " + Datos4.size());
            System.out.println("-Los valores de peso no pueden ser menor o igual a cero. Valor: " + pesoIzqEnMv);
            return true;
        } return false;
    }

    private void reiniciarTrama() {
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            puerto.setFlujoEnt(puerto);
            Thread.sleep(1900);
            comandoCEROS(0);
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarDatosPeso(){
        Datos3.clear();
        Datos4.clear();
    }

    public void EsperaDesviacion() {
        LabelAviso.setText("PLACA");
        timeraviso.start();
        if (tiempomensajes == 0) {
            JOptionPane.showMessageDialog(this, "Asegurese de que el eje No." + this.ejemedido + " del vehiculo pase sobre la plancha de desviación", "Precaución", JOptionPane.WARNING_MESSAGE);
        } else {
            if (aplcSenDesv == 1) {
                timerPreseDesv.start();
            } else {
                timerautomatico2.start();
            }
            JOptionPane.showMessageDialog(this, "Asegurese de que el eje No." + this.ejemedido + " del vehiculo pase sobre la plancha de desviación", "Precaución", JOptionPane.WARNING_MESSAGE);
        }
        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
        //LabelInfo.setText("Midiendo desviación del eje No."+this.ejemedido+" ...");
    }

    public void EsperaRodillos() throws InterruptedException {
        System.out.println("Esperando rodillos start");
        int conteoreg = 0;
        LabelInfo.setText("ESPERANDO "+ ejeORueda + ruedasOEjes[ejemedido-1] + "EN LOS RODILLOS..!");
        LabelAviso.setText("RODILLOS");
        timeraviso.start();
        jProgressBar1.setMaximum(10);
        puerto.setFlujoEnt(puerto);
        comandoFREN();
        while (true) {
            Thread.sleep(500);
            jProgressBar1.setValue(conteoreg + 1);
            jProgressBar1.setString((conteoreg + 1) * 10 + "%");
            CapturarDatos("frenometro");
            if ((isCanal0() && isCanal1()) || activarPresencia) {
                if (activarPresencia) {
                    CMensajes.mensajeAdvertencia("Si esta es una prueba real, por favor aborte la prueba y contacte al soporte Soltelec.\n"+
                    "Propiedad 'activarPresenciaDesdeSoftware' de peso activada. Desactivarla desde el archivo de calibracion.properties poniendola en false\n"+
                    "Esto solo se usa para activar las presencias automaticas en pruebas de calibración en cero.\n");
                }
                conteoreg++;
            } else {
                conteoreg = 0;
                LabelInfo.setText("Por favor MANTENGA el eje sobre los rodillos");
            }
            Datos1.clear();
            Datos2.clear();
            Datos3.clear();
            Datos4.clear();
            Datos5.clear();
            Datos6.clear();
            if (conteoreg == 10) {
                break;
            }
        }
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }
        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
        System.out.println("Esperando rodillos end");
    }

    public void EsperaRodillos(String lado) throws InterruptedException {
        boolean isDerecho = lado.equalsIgnoreCase("derecho");
        System.out.println("Esperando rodillos start");
        int conteoreg = 0;
        LabelInfo.setText("ESPERANDO "+ ejeORueda + ruedasOEjes[ejemedido-1] + " EN EL RODILLO "+lado.toUpperCase()+"..!");
        LabelAviso.setText("RODILLOS");
        timeraviso.start();
        jProgressBar1.setMaximum(10);
        puerto.setFlujoEnt(puerto);
        comandoFREN();
        while (true) {
            Thread.sleep(500);
            jProgressBar1.setValue(conteoreg + 1);
            jProgressBar1.setString((conteoreg + 1) * 10 + "%");
            CapturarDatos("frenometro");
            if ((isCanal0() && isDerecho) || (isCanal1() && !isDerecho) || activarPresencia) {
                if (activarPresencia) {
                    CMensajes.mensajeAdvertencia("Si esta es una prueba real, por favor aborte la prueba y contacte al soporte Soltelec.\n"+
                    "Propiedad 'activarPresenciaDesdeSoftware' de peso activada. Desactivarla desde el archivo de calibracion.properties poniendola en false\n"+
                    "Esto solo se usa para activar las presencias automaticas en pruebas de calibración en cero.\n");
                }
                conteoreg++;
            } else {
                conteoreg = 0;
                LabelInfo.setText("Por favor MANTENGA el eje sobre los rodillos");
            }
            Datos1.clear();
            Datos2.clear();
            Datos3.clear();
            Datos4.clear();
            Datos5.clear();
            Datos6.clear();
            if (conteoreg == 10) {
                break;
            }
        }
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }
        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
        System.out.println("Esperando rodillos end");
    }

    public void MoverSuspension(String lado) throws InterruptedException {
        switch (lado) {
            case "derecho":
                if (ejemedido == 2) {
                    LabelInfo.setText("ESPERE UN MOMENTO POR FAVOR ..!");
                    Thread.sleep(2800);
                }
                EnviaSalidas(0, salidamotorderecho);
                LabelInfo.setText("MOVIENDO EJE No." + ejemedido + " EN EL LADO DERECHO");
                break;
            case "izquierdo":
                LabelInfo.setText("ESPERE UN MOMENTO POR FAVOR ..!");
                Thread.sleep(3977);
                EnviaSalidas(0, salidamotorizquierdo);
                LabelInfo.setText("MOVIENDO EJE No." + ejemedido + " EN EL LADO IZQUIERDO");
                break;
        }
        for (i = 0; i < 8; i++) {
            if (i == 1) {
                jProgressBar1.setMaximum(8);
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) * 12.5 + "%");
            Thread.sleep(500);
        }
        comandoSTOPDigital();
        puerto.setFlujoEnt(puerto);
    }

    public void MoverRodillos(Boolean freAux) throws InterruptedException{
        MoverRodillos(freAux, "ambos");
    }

    public void MoverRodillos(Boolean freAux, String lado) throws InterruptedException {
        System.out.println("moviendo rodillos start");
        puerto.setFlujoEnt(puerto);
        comandoFREN();
        for (this.pasoactual = 1; this.pasoactual <= this.numpasos / 2; this.pasoactual = ((byte) (this.pasoactual + 1))) {
            LeePaso();
            if (this.tiempo_paso == 0) {
                JOptionPane.showMessageDialog(this, "Disculpe, Consegui un ERROR en el archivo de configuración", "SART 1.7.3", 0);
                comandoSTOP();
                this.puerto.close();
                dispose();
            } else {
                EnviaSalidas(this.salidaalta, this.salidabaja);

                switch (this.pasoactual) {
                    case 1:
                        System.out.println("Moviendo Rodillos");
                        this.LabelInfo.setText(" MOVIENDO RODILLOS..!");
                        break;
                    case 2:
                        if (isPista_mixta()) {
                            System.out.println("Cambiando velocidad");
                            this.LabelInfo.setText("CAMBIANDO VELOCIDAD..!");
                        } else {
                            System.out.println("Manteniendo velocidad");
                            this.LabelInfo.setText("MANTENIENDO VELOCIDAD..!");
                        }
                        break;
                    case 3:
                        System.out.println("Determinando velocidad critica");
                        this.LabelInfo.setText("DETERMINANDO VELOCIDAD CRITICA..!");
                        break;
                    case 4:
                        System.out.println("Vaya frenando gradualmente");
                        this.LabelInfo.setText("POR FAVOR VAYA FRENANDO ..!");
                        
                        break;
                    case 5:
                        System.out.println("Apagando contactores");
                        this.LabelInfo.setText("APAGANDO CONTACTORES..!");
                        if (!lado.equalsIgnoreCase("ambos")) Thread.sleep(tiempoApagarContactores);
                        break;
                    case 6:
                        System.out.println("Fuerza de frenado medida");
                        this.LabelInfo.setText("TOMADA FUERZA DE FRENADO..!");
                        break;
                    default:
                        System.out.println("ouch");
                        this.LabelInfo.setText("ouch");
                }

                for (int cont1s = 0; cont1s < this.tiempo_paso / 1000; cont1s++) {
                    Thread.sleep(1000L);
                    System.out.println(new StringBuilder().append("Paso 1s,i: ").append(this.pasoactual).append(" j: ").append(cont1s).toString());
                    CapturarDatos("frenometro");

                    if (((!isCanal0()) || (!isCanal1())) && (this.pasoactual != 6)) {
                        this.LabelAviso.setBackground(this.PanelTitulos.getBackground());
                        if ((this.pasoactual == 4) || (this.pasoactual == 5) || isVehiculoMedidoPorUnaLlanta() || activarPresencia) {
                            cont1s = this.tiempo_paso / 1000;
                        } else {
                            this.LabelInfo.setText("El eje se salio de los rodillos");
                            this.led1.setLedOn(false);
                            this.led2.setLedOn(false);
                            this.timeraviso.stop();
                            this.LabelAviso.setText("");
                            this.LabelAviso.setBackground(this.PanelTitulos.getBackground());
                            comandoSTOP();
                            Thread.sleep(2000L);
                            this.LabelInfo.setText("Prueba abortada. Presione finalizar");
                            this.tecladoactivado = true;
                            this.BotonFinalizar.setEnabled(true);
                            break;
                        }
                    }
                    if (this.pasoactual == 4) {
                        if ((this.ComandoRecibido[6] - this.valcalcero5 <= this.velminimad) || (this.ComandoRecibido[7] - this.valcalcero6 <= this.velminimai)) {
                            cont1s = this.tiempo_paso / 1000;
                        }
                        System.out.println(new StringBuilder().append("velocidad derecha: ").append(this.ComandoRecibido[6] - this.valcalcero5).append(" velocidad izquierda: ").append(this.ComandoRecibido[7] - this.valcalcero6).toString());
                    }
                }
                switch (this.pasoactual) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        this.velminimad = ((CalcularMedia(this.Datos5) - this.valcalcero5) * this.umbral_velo);
                        this.velminimai = ((CalcularMedia(this.Datos6) - this.valcalcero6) * this.umbral_velo);
                        System.out.println(new StringBuilder().append("velocidad minima derecha: ").append(this.velminimad).append(" velocidad minima izquierda: ").append(this.velminimai).toString());
                        this.timeraviso.start();
                        this.LabelAviso.setText("FRENE");
                        System.out.println("Frenando. tiempo de frenado: " + this.tiempoFrenado);
                        if (!lado.equalsIgnoreCase("ambos")) Thread.sleep(tiempoFrenado);
                        break;
                    case 4:

                        if (lado.equalsIgnoreCase("ambos")) {
                            MedirFuerzaFrenado(freAux);
                        }else{
                            MedirFuerzaFrenado(freAux, lado);
                        }
                        
                        this.timeraviso.stop();
                        this.LabelAviso.setText("");
                        this.LabelAviso.setIcon(null);
                        this.LabelAviso.setBackground(this.PanelTitulos.getBackground());
                        break;
                    case 5:
                        break;
                    case 6:
                }
                this.Datos1.clear();
                this.Datos2.clear();
                this.Datos3.clear();
                this.Datos4.clear();
                this.Datos5.clear();
                this.Datos6.clear();
            }
        }
        comandoSTOP();
        try {
            this.config.reset();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Thread.sleep(100L);
        comandoSTOP();
        System.out.println("moviendo rodillos end");
    }

    public double CalcularDesviacion(double media, List<Double> Datosfil) {
        double max = 0, min = 4095, c;
        long s;
        s = Datosfil.size();
        System.out.println("El numero de datos es: " + s);
        for (t = 0; t < s; t++) {
            if (Datosfil.get(t) > max) {
                max = Datosfil.get(t);
            } else if (Datosfil.get(t) < min) {
                min = Datosfil.get(t);
            }
        }
        if (Math.abs(max - media) > Math.abs(media - min)) {
            c = max;
        } else {
            c = min;
        }
        //System.out.println(c);
        if (enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatosd1.write("El cero de desviacion es: " + media);
                    regdatosd1.newLine();
                    regdatosd1.write("El span de desviacion es: " + spand);
                    regdatosd1.newLine();
                    regdatosd1.write("El ancho de la placa de desviacion es: " + anchoplacadesv);
                    regdatosd1.newLine();
                    regdatosd1.flush();
                    for (t = 0; t < s; t++) {
                        regdatosd1.write(Datosfil.get(t).toString());
                        regdatosd1.newLine();
                    }
                    regdatosd1.close();
                } else if (ejemedido == 2) {
                    regdatosd2.write("El cero de desviacion es: " + media);
                    regdatosd2.newLine();
                    regdatosd2.write("El span de desviacion es: " + spand);
                    regdatosd2.newLine();
                    regdatosd2.write("El ancho de la placa de desviacion es: " + anchoplacadesv);
                    regdatosd2.newLine();
                    regdatosd2.flush();
                    for (t = 0; t < s; t++) {
                        regdatosd2.write(Datosfil.get(t).toString());
                        regdatosd2.newLine();
                    }
                    regdatosd2.close();
                }
            } catch (IOException ex) {
                System.out.println("No se pudo escribir el dato de desviación en el backup " + ex.getMessage());
            }
        }
        return c;
    }

    public double CalcularMedia(List<Integer> Datos) {
        System.out.println("\nDATOS====================================================\n\n" + Datos + "\n");

        // Copiar y ordenar los datos
        List<Integer> ordenados = new ArrayList<>(Datos);
        Collections.sort(ordenados);

        // Calcular Q1 y Q3 (percentiles 25% y 75%)
        double q1 = calcularPercentil(ordenados, 40);
        double q3 = calcularPercentil(ordenados, 75);
        double iqr = q3 - q1;

        double limiteInferior = q1 - 1 * iqr;
        double limiteSuperior = q3 + 1.5 * iqr;

        // Filtrar datos dentro del rango IQR
        List<Integer> filtrados = new ArrayList<>();
        for (int valor : Datos) {
            if (valor >= limiteInferior && valor <= limiteSuperior) {
                filtrados.add(valor);
            }
        }

        // Mostrar resumen
        System.out.println("Q1: " + q1 + ", Q3: " + q3);
        System.out.println("Limite inferior: " + limiteInferior + ", Limite superior: " + limiteSuperior);
        System.out.println("Datos filtrados: " + filtrados);
        System.out.println("NUMERO DE DATOS FILTRADOS: " + filtrados.size());
        System.out.println("==========================================================================\n\n");

        return calcularPromedio(filtrados);
    }

    // Método auxiliar para calcular el promedio
    private double calcularPromedio(List<Integer> lista) {
        if (lista.isEmpty()) return 0;
        long suma = 0;
        for (int valor : lista) suma += valor;
        return (double) suma / lista.size();
    }

    // Método auxiliar para calcular un percentil (asumiendo lista ordenada)
    private double calcularPercentil(List<Integer> lista, double percentil) {
        int n = lista.size();
        double rank = (percentil / 100.0) * (n - 1);
        int bajo = (int) Math.floor(rank);
        int alto = (int) Math.ceil(rank);

        if (bajo == alto) {
            return lista.get(bajo);
        } else {
            double peso = rank - bajo;
            return lista.get(bajo) * (1 - peso) + lista.get(alto) * peso;
        }
    }

    public double CalcularMediana(List<Integer> Datos) {
        int s, r, auxMediana;
        double c;
        s = Datos.size();
        System.out.println("s: " + s);
        if (s == 0) {
            return 0;
        }
        if (s == 1) {
            return Datos.get(0);
        }
        for (t = 0; t < s; t++) {
            for (r = t + 1; r < s; r++) {
                if (Datos.get(r) > Datos.get(t)) {
                    auxMediana = Datos.get(t);
                    Datos.set(t, Datos.get(r));
                    Datos.set(r, auxMediana);
                }
            }
        }

        if (s % 2 == 0) {
            c = (double) (Datos.get((s - 1) / 2) + Datos.get((s - 1) / 2 + 1)) / 2;
            System.out.println("---------------------Lista" + c);
        } else {
            c = (double) Datos.get(s / 2);
            System.out.println("---------------------Lista" + c);
        }
        return c;
    }

    public double CalcularMaximo(List<Double> Datos) {
        long s;
        double max = 0;
        s = Datos.size();
        for (t = 0; t < s; t++) {
            if (Datos.get(t) > max) {
                max = Datos.get(t);
            }
        }
        return max;
    }

    public Integer CalcularMaximoIn(List<Integer> Datos) {
        Integer s;
        Integer max = 0;
        s = Datos.size();
        for (t = 0; t < s; t++) {
            if (Datos.get(t) > max) {
                max = Datos.get(t);
            }
        }
        return max;
    }

    public double CalcularMinimo(List<Integer> Datos, int offset) {
        long s;
        double min = 4095;

        s = Datos.size();
        for (t = offset; t < s; t++) {
            if (Datos.get(t) < min) {
                min = Datos.get(t);
            }
        }
        return min;
    }

    public List<Double> filtrar(List<Integer> Datos) {
        //Esta funcion arroja un arreglo de datos filtrado a partir de los datos de
        //entrada, con un tamaño de la entrada ya que ignora los primeros y ultimos
        //datos que fueron calculados a partir de ceros
        int s, n, p, q;
        double y;
        //int diezmado=17;
        List<Double> Datosfil = new ArrayList<>(); //Valores de la salida del filtro
        s = Datos.size();
        n = filtro.length;
        System.out.println("   " + "n: " + n + " s: " + s);

        for (p = 0; p < s; p++) {
            y = 0;
            for (q = 0; q < n; q++) {
                if ((p - q) >= 0) {
                    y += filtro[q] * Datos.get(p - q);
                }
            }
            if (p >= (n - 1)) {
                Datosfil.add(y);
            }
        }
        return Datosfil;
    }

    //Filtro Suspension
    //
    public void armonizar(List<Integer> Datos, double promedio) {
        try {
            int s1;
            double pendientesig, pendienteant;
            //Datos = new ArrayList<Integer>();
            s1 = Datos.size();
            for (t = 0; t < s1; t++) {
                if (t == 0) {
                    if (promedio == 0) {
                        pendienteant = Datos.get(t);
                    } else {
                        pendienteant = (Datos.get(t) - promedio) / promedio;
                    }
                    if (Datos.get(1) == 0) {
                        pendientesig = Datos.get(t);
                    } else {
                        pendientesig = (Datos.get(t) - Datos.get(t + 1)) / (new Double(Datos.get(t + 1)));
                    }
                } else if (t == (s1 - 1)) {
                    if (promedio == 0) {
                        pendientesig = Datos.get(t);
                    } else {
                        pendientesig = (Datos.get(t) - promedio) / promedio;
                    }
                    if (Datos.get(t - 1) == 0) {
                        pendienteant = Datos.get(t);
                    } else {
                        pendienteant = (Datos.get(t) - Datos.get(t - 1)) / (new Double(Datos.get(t - 1)));
                    }
                } else {
                    if (Datos.get(t - 1) == 0) {
                        pendienteant = Datos.get(t);
                    } else {
                        pendienteant = (Datos.get(t) - Datos.get(t - 1)) / (new Double(Datos.get(t - 1)));
                    }
                    if (Datos.get(t + 1) == 0) {
                        pendientesig = Datos.get(t);
                    } else {
                        pendientesig = (Datos.get(t) - Datos.get(t + 1)) / (new Double(Datos.get(t + 1)));
                    }
                }

                if (Math.abs(pendienteant) > 0.24 && Math.abs(pendientesig) > 0.24) {  //Se comprueba si la pendiente de cambio
                    //del punto actual respecto al anterior y al siguiente es mayor al 100%
                    if (t == 0) {
                        Datos.set(t, Datos.get(t + 1));
                    } else if (t == (s1 - 1)) {
                        Datos.set(t, Datos.get(t - 1));
                    } else {
                        Datos.set(t, (Datos.get(t - 1) + Datos.get(t + 1)) / 2);
                    }
                }
            }
            // filtrar(Datos);
        } catch (Exception e) {
        }
    }

    public static int byteToInt(byte b) {
        String cadenaLSB = Integer.toBinaryString(b & 0xFF);
        StringBuilder lsb = new StringBuilder();
        for (int i = cadenaLSB.length() - 1; i < 7; i++) {
            lsb.append('0');
        }
        lsb.append(cadenaLSB);
        int valor = Integer.parseInt(cadenaLSB, 2);
        return valor;
    }

    public void CapturarDatos(String opcion) {
        int opc = 0;
        switch (opcion) {
            case "ceros":
                opc = 1;
                break;
            case "frenometro":
                opc = 2;
                break;
            case "suspension":
                opc = 3;
                break;
            case "desviacion":
                opc = 4;
                break;
            case "fuerzaVertical":
                opc = 5;
                break;
        }
        try {
            len = puerto.in.read(buffer);
            switch (opc) {
                // <editor-fold desc="Obtener tramas de comando CEROS">
                case 1:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            data = buffer[i];
                            // <editor-fold desc="Se valida la cabecera de la trama">
                            if (data == 72) {
                                ComandoRecibido[0] = data;
                                j = 1;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de fuerza derecha">
                            } else if (j == 1) {
                                partealta = byteToInt(data);
                                j = 2;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de fuerza derecha">
                            } else if (j == 2) {
                                ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[1] <= 4095) {
                                    Datos1.add(ComandoRecibido[1]);
                                    j = 3;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de fuerza izquierda">
                            } else if (j == 3) {
                                partealta = byteToInt(data);
                                j = 4;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de fuerza izquierda">
                            } else if (j == 4) {
                                ComandoRecibido[2] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[2] <= 4095) {
                                    Datos2.add(ComandoRecibido[2]);
                                    j = 5;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso derecho">
                            } else if (j == 5) {
                                partealta = byteToInt(data);
                                j = 6;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso derecho">
                            } else if (j == 6) {
                                int baja = byteToInt(data);
                                ComandoRecibido[3] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[3] <= 4095) {
                                    Datos3.add(ComandoRecibido[3]); //recibidos datos3
                                    j = 7;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso izquierdo">
                            } else if (j == 7) {
                                partealta = byteToInt(data);
                                j = 8;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso izquierdo">
                            } else if (j == 8) {
                                ComandoRecibido[4] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[4] <= 4095) {
                                    Datos4.add(ComandoRecibido[4]); //recibidos datos4
                                    j = 9;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de velocidad derecha">
                            } else if (j == 9) {
                                partealta = byteToInt(data);
                                j = 10;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de velocidad derecha">
                            } else if (j == 10) {
                                ComandoRecibido[5] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[5] <= 4095) {
                                    Datos5.add(ComandoRecibido[5]);
                                    j = 11;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de velocidad izquierda">
                            } else if (j == 11) {
                                partealta = byteToInt(data);
                                j = 12;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de velocidad izquierda">
                            } else if (j == 12) {
                                ComandoRecibido[6] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[6] <= 4095) {
                                    Datos6.add(ComandoRecibido[6]);
                                    j = 13;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de desviacion">
                            } else if (j == 13) {
                                partealta = byteToInt(data);
                                j = 14;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de desviacion">
                            } else if (j == 14) {
                                ComandoRecibido[7] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[7] <= 4095) {
                                    Datos7.add(ComandoRecibido[7]);
                                    j = 15;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se valida la cola de la trama">
                            } else if (j == 15) {
                                ComandoRecibido[8] = data;
                                j = 0;
                                if (data == 'W') {
                                }
                            }
                            // </editor-fold>
                        }
                        Utilidades2.writeListToFile(Datos3, "datos3-1.txt");
                        Utilidades2.writeListToFile(Datos4, "datos4-1.txt");
                    } else {
                        comandoCEROS(0);
                    }
                    break;
                // </editor-fold>
                // <editor-fold desc="Obtener tramas de comando FREN">
                case 2:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            data = buffer[i];
                            // <editor-fold desc="Se valida la cabecera de la trama">
                            if (data == 72) {
                                ComandoRecibido[0] = data;
                                j = 1;
                                // </editor-fold>
                                // <editor-fold desc="Se valida y reciben las entradas digitales">
                            } else if (j == 1) {
                                if (data <= 15) {
                                    ComandoRecibido[1] = (byteToInt(data) & 0x03);
                                    j = 2;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de fuerza derecha">
                            } else if (j == 2) {
                                partealta = byteToInt(data);
                                j = 3;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de fuerza derecha">
                            } else if (j == 3) {
                                ComandoRecibido[2] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[2] <= 4095) {
                                    Datos1.add(ComandoRecibido[2]);
                                    j = 4;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de fuerza izquierda">
                            } else if (j == 4) {
                                partealta = byteToInt(data);
                                j = 5;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de fuerza izquierda">
                            } else if (j == 5) {
                                ComandoRecibido[3] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[3] <= 4095) {
                                    Datos2.add(ComandoRecibido[3]);
                                    j = 6;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso derecho">
                            } else if (j == 6) {
                                partealta = byteToInt(data);
                                j = 7;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso derecho">
                            } else if (j == 7) {
                                ComandoRecibido[4] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[4] <= 4095) {
                                    Datos3.add(ComandoRecibido[4]); //recibidos datos3
                                    j = 8;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso izquierdo">
                            } else if (j == 8) {
                                partealta = byteToInt(data);
                                j = 9;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso izquierdo">
                            } else if (j == 9) {
                                ComandoRecibido[5] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[5] <= 4095) {
                                    Datos4.add(ComandoRecibido[5]); //recibidos datos4 posible no
                                    j = 10;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de velocidad derecha">
                            } else if (j == 10) {
                                partealta = byteToInt(data);
                                j = 11;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de velocidad derecha">
                            } else if (j == 11) {
                                ComandoRecibido[6] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[6] <= 4095) {
                                    Datos5.add(ComandoRecibido[6]);
                                    j = 12;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de velocidad izquierda">
                            } else if (j == 12) {
                                partealta = byteToInt(data);
                                j = 13;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de velocidad izquierda">
                            } else if (j == 13) {
                                ComandoRecibido[7] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[7] <= 4095) {
                                    Datos6.add(ComandoRecibido[7]);
                                    j = 14;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se valida la cola de la trama">
                            } else if (j == 14) {
                                ComandoRecibido[8] = data;
                                j = 0;
                                if (data == 'W') {
                                    setCanal0((ComandoRecibido[1] & 0x01) > 0);
                                    setCanal1((ComandoRecibido[1] & 0x02) > 0);
                                }
                            }
                            // </editor-fold>
                        }
                        //setCanal0((ComandoRecibido[1] & 0x01) >0);
                        //setCanal1((ComandoRecibido[1] & 0x02) >0);
                        led1.setLedOn(isCanal0());
                        led2.setLedOn(isCanal1());

                        Utilidades2.writeListToFile(Datos3, "datos3-2.txt");
                        Utilidades2.writeListToFile(Datos4, "datos4-2.txt");
                    } else {
                        comandoFREN();
                    }
                    break;
                // </editor-fold>
                // <editor-fold desc="Obtener tramas de comando SUSP">
                case 3:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            data = buffer[i];
                            // <editor-fold desc="Se valida la cabecera de la trama">
                            if (data == 72) {
                                ComandoRecibido[0] = data;
                                j = 1;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso derecho">
                            } else if (j == 1) {
                                partealta = byteToInt(data);
                                j = 2;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso derecho">
                            } else if (j == 2) {
                                if (data > 0) {
                                    ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                                    if (ComandoRecibido[1] <= 4095) {
                                        Datos3.add(ComandoRecibido[1]); //recibidos datos3
                                        j = 3;
                                    } else {
                                    }
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso izquierdo">
                            } else if (j == 3) {
                                partealta = byteToInt(data);
                                j = 4;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso izquierdo">
                            } else if (j == 4) {
                                if (data > 0) {
                                    ComandoRecibido[2] = partealta * 256 + byteToInt(data);
                                    if (ComandoRecibido[2] <= 4095) {
                                        Datos4.add(ComandoRecibido[2]); //recibidos datos4
                                        j = 5;
                                    } else {
                                    }
                                    System.out.println("datos: "+Datos4);
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se valida la cola de la trama">
                            } else if (j == 5) {
                                ComandoRecibido[6] = data;
                                j = 0;
                                if (data == 'W') {
                                }
                            }
                            // </editor-fold>

                        }

                        Utilidades2.writeListToFile(Datos3, "datos3-3.txt");
                        Utilidades2.writeListToFile(Datos4, "datos4-3.txt");
                    } else {
                        comandoSUSP();
                    }
                    break;
                // </editor-fold>
                // <editor-fold desc="Obtener tramas de comando DESV">
                case 4:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            System.out.println("------------------------------------------------estoy dentro del for de desviacion---------------------------------------");
                            data = buffer[i];
                            // <editor-fold desc="Se valida la cabecera de la trama">
                            if (data == 72) {
                                System.out.println("------------------------------------------------estoy antes del j=1---------------------------------------" + j);
                                ComandoRecibido[0] = data;
                                j = 1;
                                System.out.println("------------------------------------------------estoy despues del j=1---------------------------------------" + j);
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de desviacion">
                            } else if (j == 1) {
                                System.out.println("------------------------------------------------estoy antes del j=2---------------------------------------" + j);
                                partealta = byteToInt(data);
                                j = 2;
                                System.out.println("------------------------------------------------estoy despues del j=2---------------------------------------" + j);
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de desviacion">
                            } else if (j == 2) {
                                System.out.println("------------------------------------------------estoy antes del j=3---------------------------------------" + j);
                                ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[1] <= 4095) {
                                    Datos7.add(ComandoRecibido[1]);
                                    j = 3;
                                    System.out.println("------------------------------------------------estoy despues del j=3---------------------------------------" + j);
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se valida la cola de la trama">
                            } else if (j == 3) {
                                System.out.println("------------------------------------------------estoy antes del j=0---------------------------------------" + j);
                                ComandoRecibido[2] = data;
                                j = 0;
                                System.out.println("------------------------------------------------estoy despues del j=0---------------------------------------" + j);
                                if (data == 'W') {
                                }
                            }
                            // </editor-fold>
                        }
                    } else {
                        System.out.println("------------------------------------------------sali de los j, ejecutando comando de desviacion---------------------------------------" + j);
                        comandoDESV();

                    }
                    break;
                case 5:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            data = buffer[i];
                            // <editor-fold desc="Se valida la cabecera de la trama">
                            if (data == 72) {
                                ComandoRecibido[0] = data;
                                j = 1;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso derecho">
                            } else if (j == 1) {
                                partealta = byteToInt(data);
                                j = 2;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso derecho">
                            } else if (j == 2) {
                                ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[1] <= 4095) {
                                    Datos3.add(ComandoRecibido[1]); //recibidos datos3
                                    j = 3;
                                } else {
                                }

                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de peso izquierdo">
                            } else if (j == 3) {
                                partealta = byteToInt(data);
                                j = 4;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de peso izquierdo">
                            } else if (j == 4) {
                                ComandoRecibido[2] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[2] <= 4095) {
                                    Datos4.add(ComandoRecibido[2]); //recibidos datos4 posible no
                                    j = 5;
                                } else {
                                }

                                // </editor-fold>
                                // <editor-fold desc="Se valida la cola de la trama">
                            } else if (j == 5) {
                                ComandoRecibido[6] = data;
                                j = 0;
                                if (data == 'W') {
                                }
                            }
                            // </editor-fold>
                        }

                        Utilidades2.writeListToFile(Datos4, "datos3-5.txt");
                        Utilidades2.writeListToFile(Datos4, "datos4-5.txt");
                    } else {
                        comandoSUSP();
                    }
                    break;
                // </editor-fold>
                default:
                    System.out.println("Error en el uso de Capturar Datos");
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    void RegistrarMedidasDesviacion() {
        Desviacion desviacion = new Desviacion();
        PruebaDefaultDAO desviacionDAO = new PruebaDefaultDAO();
        for (Double desviacione : desviaciones) {
//            desviacion.setDesviacion(Math.abs(desviacione));            
            desviacion.setDesviacion(desviacione);
            desviacion.setResolMin(resolMin);
            desviacion.setResolMax(resolMax);
        }
        try {
            System.out.println("toma el marcado de confirmacion DESVIACION LIVIANO ");
            PruebaDefaultDAO.escrTrans = "@";

            try {
                repetirPrueba = desviacionDAO.persist(desviacion, idPruebadesv, idUsuario, aplicTrans, this.ipEquipo, tipoPista, tipoVehiculo, this.Placa, "desviaciones dlgLiviano");
            } catch (ClassNotFoundException ex) {
            }
            if (repetirPrueba == false) {
                tramaAuditoriaDesv = "";
                for (int k = 0; k < desviacion.getDesviacion().size(); k++) {
                    tramaAuditoriaDesv = tramaAuditoriaDesv.concat("{\"eje").concat(String.valueOf(k + 1)).concat("\":\"").concat(String.valueOf(desviacion.getDesviacion().get(k))).concat("\",");
                }
                tramaAuditoriaDesv = tramaAuditoriaDesv.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPruebadesv)).concat("\"}");
            } else {
                PruebaDefaultDAO.escrTrans = "@@@";
            }
        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    /**
     *
     */
    void RegistrarMedidasSuspension() {
        System.out.println("---------------------------------------------------");
        System.out.println("-------------- Registrar medidas suspension -------");
        System.out.println("---------------------------------------------------");

        Suspension suspension = new Suspension();
        PruebaDefaultDAO suspensionDAO = new PruebaDefaultDAO();

//        for (int k = 0; k < fuerzasvd.size(); k++) 
//        {
//            System.out.println(" Valor de fuerza derecha " + fuerzasvd.get(k) +" en la posicion "+ k);
//            suspension.setFuerzaDerecha(fuerzasvd.get(k)); 
//             System.out.println(" Valor de fuerza izquierda " + fuerzasvi.get(k) +" en la posicion "+ k);
//            suspension.setFuerzaIzquierda(fuerzasvi.get(k));
//        }
//        
//        for (int k = 0; k < pesosd.size(); k++)
//        {
//            System.out.println(" Valor de peso derecho " + pesosd.get(k) * spanpd +" en la posicion "+ k);
//            suspension.setPesoDerecho(pesosd.get(k) * spanpd);
//            
//            System.out.println(" Valor de peso izquierdo " + pesosi.get(k) * spanpd +" en la posicion "+ k);
//            suspension.setPesoIzquierdo(pesosi.get(k) * spanpi);
//        }
        boolean valoresNulosOCeros = false;

        for (int i = 0; i < fuerzasvd.size(); i++) {
            System.out.println("Valor de fuerza derecha " + fuerzasvd.get(i) + " en la posición " + i);
            suspension.setFuerzaDerecha(fuerzasvd.get(i), "RegistrarMedidasSuspension desde DlgIntegradoLiviano con i="+i);

            System.out.println("Valor de fuerza izquierda " + fuerzasvi.get(i) + " en la posición " + i);
            suspension.setFuerzaIzquierda(fuerzasvi.get(i), "RegistrarMedidasSuspension desde DlgIntegradoLiviano con i="+i);

            if (fuerzasvd.get(i) == null || fuerzasvi.get(i) == null || fuerzasvd.get(i) == 0 || fuerzasvi.get(i) == 0) {
                valoresNulosOCeros = true;
                //break;
            }
        }

        for (int k = 0; k < pesosd.size(); k++) {
            System.out.println("Valor de peso derecho " + pesosd.get(k) * spanpd + " en la posición " + k);
            suspension.setPesoDerecho(pesosd.get(k) * spanpd, "RegistrarMedidasSuspension desde DlgIntegradoLiviano con k="+k);

            System.out.println("Valor de peso izquierdo " + pesosi.get(k) * spanpd + " en la posición " + k);
            suspension.setPesoIzquierdo(pesosi.get(k) * spanpi, "RegistrarMedidasSuspension desde DlgIntegradoLiviano con k="+k);

            if (pesosd.get(k) == null || pesosi.get(k) == null || pesosd.get(k) == 0 || pesosi.get(k) == 0) {
                valoresNulosOCeros = true;
                //break;
            }
        }

        /* if (valoresNulosOCeros) {
            JOptionPane.showMessageDialog(null, "Hay valores nulos o en ceros. La prueba se repite.");
            System.out.println("Hay valores nulos o en ceros. La prueba se repite.");
        } else {
            System.out.println("voy a registrar suspension DLGintegradoLiviano ");
        } */

        try {
            PruebaDefaultDAO.escrTrans = "@";
            try {
                repetirPrueba = suspensionDAO.persist(suspension, idPruebasusp, idUsuario, aplicTrans, this.ipEquipo, tipoPista, tipoVehiculo, this.Placa, "suspencion dlg livianos");
            } catch (ClassNotFoundException ex) {
            }
            if (repetirPrueba == false) {
                tramaAuditoriaSusp = "";
                tramaAuditoriaSusp = tramaAuditoriaSusp.concat("{\"delanteraIzquierda\":\"").concat(String.valueOf(Suspension.suspension.get(0))).concat("\",").concat("\"delanteraDerecha\":\"").concat(String.valueOf(Suspension.suspension.get(0 + 1))).concat("\",").concat("\"traseraIzquierda\":\"").concat(String.valueOf(Suspension.suspension.get(0 + 2))).concat("\",").concat("\"traseraDerecha\":\"").concat(String.valueOf(Suspension.suspension.get(0 + 3)));
                tramaAuditoriaSusp = tramaAuditoriaSusp.concat("\",\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPruebasusp)).concat("\"}");
            } else {
                PruebaDefaultDAO.escrTrans = "@@@";
            }

        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    void RegistrarMedidasFrenos() {
        int facEnseñanza = 1;
        Frenos frenos = new Frenos(factor_desq, tipoVehiculo);
        PruebaDefaultDAO frenosDAO = new PruebaDefaultDAO();

        if (FrenoInst.equalsIgnoreCase("True") && isEnsenianza()) {//SI EL VEHICULO ES DE ENSEÑANZA Y LA VARIBLE ESTA ACTIVA HACE DOBLE FRENADO
            for (int k = 1; k < fuerzasfd.size(); k += 2) {
                frenos.setFuerzaDerechaEnseñanza(Math.floor(Math.round(fuerzasfd.get(k) * spanfd)));
                frenos.setFuerzaIzquierdaEnseñanza(Math.floor(Math.round(fuerzasfi.get(k) * spanfi)));
            }
            facEnseñanza = 2;
        }
        for (int k = 0; k < fuerzasfdAux.size(); k++) {
            frenos.setFuerzaDerechaAux(Math.floor(Math.round(fuerzasfdAux.get(k) * spanfd)));
            frenos.setFuerzaIzquierdaAux(Math.floor(Math.round(fuerzasfiAux.get(k) * spanfi)));
        }
        for (int k = 0; k < (fuerzasfd.size() / facEnseñanza); k++) {
            frenos.setFuerzaDerecha(Math.floor(Math.round(fuerzasfd.get(k * facEnseñanza) * spanfd)));
            frenos.setFuerzaIzquierda(Math.floor(Math.round(fuerzasfi.get(k * facEnseñanza) * spanfi)));
        }

        for (int k = 0; k < pesosd.size(); k++) {
            frenos.setPesoDerecho(Math.floor(Math.round(pesosd.get(k) * spanpd)));
            frenos.setPesoIzquierdo(Math.floor(Math.round(pesosi.get(k) * spanpi)));
        }
        try {
            System.out.println("toma el marcado de confirmacion FRENADOS LIVIANO ");
            PruebaDefaultDAO.escrTrans = "@";

            try {
                repetirPrueba = frenosDAO.persist(frenos, idPruebafren, idUsuario, aplicTrans, this.ipEquipo, tipoPista, tipoVehiculo, this.Placa, "frenos dlgLiviano");
            } catch (ClassNotFoundException ex) {
            }
            if (repetirPrueba == false) {
                tramaAuditoria = "{\"eficaciaTotal\":\"".concat(String.valueOf(Frenos.eficaciaVariable)).concat("\",").concat("\"eficaciaAuxiliar\":\"").concat(String.valueOf(Frenos.eficaciaFrenoMano)).concat("\",");
                for (int k = 0; k < frenos.getPesoDerecho().size(); k++) {
                    tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(frenos.getFuerzaIzquierda().get(k)))
                            .concat("\",").concat("\"pesoEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(frenos.getPesoIzquierdo().get(k)))
                            .concat("\",").concat("\"fuerzaEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(frenos.getFuerzaDerecha().get(k)))
                            .concat("\",")
                            .concat("\"pesoEje").concat(String.valueOf(k + 1))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(frenos.getPesoDerecho().get(k)))
                            .concat("\",").concat("\"eje")
                            .concat(String.valueOf(k + 1))
                            .concat("Desequilibrio\":\"")
                            .concat(String.valueOf(Frenos.desequilibrio.get(k)))
                            .concat("\",");
                }
                for (int k = frenos.getPesoDerecho().size() + 1; k < 6; k++) {
                    tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje")
                            .concat(String.valueOf(k))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",").concat("\"pesoEje")
                            .concat(String.valueOf(k))
                            .concat("Izquierdo\":\"").concat(String.valueOf(" "))
                            .concat("\",").concat("\"fuerzaEje")
                            .concat(String.valueOf(k)).concat("Derecho\":\"")
                            .concat(String.valueOf(" ")).concat("\",")
                            .concat("\"pesoEje").concat(String.valueOf(k))
                            .concat("Derecho\":\"").concat(String.valueOf(" "))
                            .concat("\",").concat("\"eje")
                            .concat(String.valueOf(k)).concat("Desequilibrio\":\"")
                            .concat(String.valueOf(" ")).concat("\",");
                }
                tramaAuditoria = tramaAuditoria.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPruebafren)).concat("\"}");
            } else {
                PruebaDefaultDAO.escrTrans = "@@@";
            }

        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogCancelacion = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        BotonEnviarCancelacion = new javax.swing.JButton();
        PanelTitulos = new javax.swing.JPanel();
        LabelAviso = new javax.swing.JLabel();
        PanelMensajes = new javax.swing.JPanel();
        LabelEje = new javax.swing.JLabel();
        LabelInfo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        led1 = new eu.hansolo.steelseries.extras.Led();
        led2 = new eu.hansolo.steelseries.extras.Led();
        PanelInformacion = new javax.swing.JPanel();
        BotonEmpezar = new javax.swing.JButton();
        BotonContinuar = new javax.swing.JButton();
        BotonFinalizar = new javax.swing.JButton();
        BotonCancelar = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        lblCtxPrueba = new javax.swing.JLabel();
        LabelPrueba = new javax.swing.JLabel();

        dialogCancelacion.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogCancelacion.setTitle("CANCELACION DE PRUEBA");
        dialogCancelacion.setMinimumSize(new java.awt.Dimension(630, 320));
        dialogCancelacion.setModal(true);
        dialogCancelacion.setResizable(false);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Por favor describa el motivo de la cancelación de la prueba:");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        BotonEnviarCancelacion.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonEnviarCancelacion.setText("ENVIAR");
        BotonEnviarCancelacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEnviarCancelacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dialogCancelacionLayout = new javax.swing.GroupLayout(dialogCancelacion.getContentPane());
        dialogCancelacion.getContentPane().setLayout(dialogCancelacionLayout);
        dialogCancelacionLayout.setHorizontalGroup(
            dialogCancelacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogCancelacionLayout.createSequentialGroup()
                .addGroup(dialogCancelacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogCancelacionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(dialogCancelacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)))
                    .addGroup(dialogCancelacionLayout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(BotonEnviarCancelacion)))
                .addContainerGap())
        );
        dialogCancelacionLayout.setVerticalGroup(
            dialogCancelacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogCancelacionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BotonEnviarCancelacion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        LabelAviso.setFont(new java.awt.Font("Andalus", 0, 36)); // NOI18N
        LabelAviso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelAviso.setOpaque(true);
        LabelAviso.setPreferredSize(new java.awt.Dimension(280, 116));

        PanelMensajes.setPreferredSize(new java.awt.Dimension(309, 70));

        LabelEje.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        LabelEje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelEje.setText("Eje");
        LabelEje.setMinimumSize(new java.awt.Dimension(49, 24));
        LabelEje.setPreferredSize(new java.awt.Dimension(700, 100));

        LabelInfo.setFont(new java.awt.Font("Andalus", 0, 55)); // NOI18N
        LabelInfo.setText("Iniciando sistema...");
        LabelInfo.setMinimumSize(new java.awt.Dimension(309, 24));
        LabelInfo.setPreferredSize(new java.awt.Dimension(700, 142));

        jLabel4.setFont(new java.awt.Font("Andalus", 0, 38)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Presencia IZQ");

        jLabel3.setFont(new java.awt.Font("Andalus", 0, 38)); // NOI18N
        jLabel3.setText("Presencia DER");

        led1.setMinimumSize(new java.awt.Dimension(96, 96));

        javax.swing.GroupLayout led1Layout = new javax.swing.GroupLayout(led1);
        led1.setLayout(led1Layout);
        led1Layout.setHorizontalGroup(
            led1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );
        led1Layout.setVerticalGroup(
            led1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );

        led2.setMinimumSize(new java.awt.Dimension(96, 96));

        javax.swing.GroupLayout led2Layout = new javax.swing.GroupLayout(led2);
        led2.setLayout(led2Layout);
        led2Layout.setHorizontalGroup(
            led2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );
        led2Layout.setVerticalGroup(
            led2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 96, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelMensajesLayout = new javax.swing.GroupLayout(PanelMensajes);
        PanelMensajes.setLayout(PanelMensajesLayout);
        PanelMensajesLayout.setHorizontalGroup(
            PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMensajesLayout.createSequentialGroup()
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LabelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 545, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(37, 37, 37))
        );
        PanelMensajesLayout.setVerticalGroup(
            PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMensajesLayout.createSequentialGroup()
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel4))
                    .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        PanelInformacion.setPreferredSize(new java.awt.Dimension(1210, 447));

        BotonEmpezar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonEmpezar.setForeground(new java.awt.Color(0, 102, 0));
        BotonEmpezar.setText("EMPEZAR");
        BotonEmpezar.setMinimumSize(new java.awt.Dimension(273, 20));
        BotonEmpezar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonEmpezar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEmpezarActionPerformed(evt);
            }
        });

        BotonContinuar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonContinuar.setText("CONTINUAR");
        BotonContinuar.setEnabled(false);
        BotonContinuar.setMinimumSize(new java.awt.Dimension(307, 20));
        BotonContinuar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonContinuarActionPerformed(evt);
            }
        });

        BotonFinalizar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonFinalizar.setText("FINALIZAR");
        BotonFinalizar.setEnabled(false);
        BotonFinalizar.setMinimumSize(new java.awt.Dimension(289, 20));
        BotonFinalizar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonFinalizarActionPerformed(evt);
            }
        });

        BotonCancelar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonCancelar.setForeground(new java.awt.Color(102, 0, 0));
        BotonCancelar.setText("CANCELAR");
        BotonCancelar.setMinimumSize(new java.awt.Dimension(291, 20));
        BotonCancelar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelInformacionLayout = new javax.swing.GroupLayout(PanelInformacion);
        PanelInformacion.setLayout(PanelInformacionLayout);
        PanelInformacionLayout.setHorizontalGroup(
            PanelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInformacionLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(BotonEmpezar, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(BotonFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 95, Short.MAX_VALUE))
        );
        PanelInformacionLayout.setVerticalGroup(
            PanelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(BotonEmpezar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(BotonContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(BotonFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(BotonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jProgressBar1.setFont(new java.awt.Font("Andalus", 0, 60)); // NOI18N
        jProgressBar1.setForeground(new java.awt.Color(255, 0, 0));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(1024, 95));
        jProgressBar1.setStringPainted(true);

        javax.swing.GroupLayout PanelTitulosLayout = new javax.swing.GroupLayout(PanelTitulos);
        PanelTitulos.setLayout(PanelTitulosLayout);
        PanelTitulosLayout.setHorizontalGroup(
            PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 1306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addGroup(PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelInformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 1468, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 1393, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelTitulosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 1590, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelTitulosLayout.setVerticalGroup(
            PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(PanelMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelInformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N
        jLabel1.setMinimumSize(new java.awt.Dimension(171, 51));

        lblCtxPrueba.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        lblCtxPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCtxPrueba.setToolTipText("");
        lblCtxPrueba.setMinimumSize(new java.awt.Dimension(541, 30));
        lblCtxPrueba.setOpaque(true);
        lblCtxPrueba.setPreferredSize(new java.awt.Dimension(1024, 145));

        LabelPrueba.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        LabelPrueba.setForeground(new java.awt.Color(0, 102, 255));
        LabelPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelPrueba.setText("PRUEBA PARA LIVIANOS");
        LabelPrueba.setMinimumSize(new java.awt.Dimension(541, 30));
        LabelPrueba.setPreferredSize(new java.awt.Dimension(1024, 145));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LabelPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 1070, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblCtxPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 1052, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 5, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(LabelPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCtxPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(PanelTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    Timer timer1 = new Timer(500, this);

    public void t1enabled(boolean a) {
        if (a) {
            this.timer1.start();
        } else {
            this.timer1.stop();
            comandoSTOP();
        }
    }

    Timer timerempezar = new Timer(2500, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            BotonEmpezarActionPerformed(e);
        }
    });

    Timer timerautomatico = new Timer(3000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            BotonContinuarActionPerformed(e);
        }
    });

    Timer timerPreseDesv = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                timerPreseDesv.stop();
                puerto.clearFlujoEnt();
                puerto.setFlujoEnt(puerto);

                System.out.println(">>> ENTRANDO TIMER PRESEV DESV:");
                CapturarDatos("frenometro");
                Thread.currentThread().sleep(200);
                comandoSTOP();
                if (espSenalPres() == true) {
                    try {
                        Robot r = new Robot();
                        r.keyPress(KeyEvent.VK_ENTER);
                    } catch (AWTException ex) {
                    }
                } else {
                    timerPreseDesv.start();
                    System.out.println(">>> SALI TIMER PRESEV DESV:");
                }
            } catch (InterruptedException ex) {
            }
        }
    }
    );
    Timer timerautomatico2 = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            numtimerautomatico2++;
            if (numtimerautomatico2 == tiempomensajes / 500) {
                timerautomatico2.stop();
                try {
                    Robot r = new Robot();
                    r.keyPress(KeyEvent.VK_ENTER);
                } catch (AWTException ex) {
                    System.out.println(ex);
                }
                numtimerautomatico2 = 0;
            }
        }
    });

    Timer timeraviso = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Color C = LabelAviso.getBackground();
            switch (LabelAviso.getText()) {
                case "PLACA":
                    if (C == PanelTitulos.getBackground()) {
                        LabelAviso.setBackground(Color.BLUE);
                    } else {
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                    }
                    break;
                case "PLANCHA":
                    if (C == PanelTitulos.getBackground()) {
                        LabelAviso.setBackground(Color.GREEN);
                    } else {
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                    }
                    break;
                case "RODILLOS":
                    if (C == PanelTitulos.getBackground()) {
                        LabelAviso.setBackground(Color.YELLOW);
                    } else {
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                    }
                    break;
                case "FRENE":
                    if (C == PanelTitulos.getBackground()) {
                        LabelAviso.setBackground(Color.RED);
                        LabelAviso.setIcon(imageOn);
                        LabelAviso.setForeground(Color.white);
                    } else {
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                        LabelAviso.setIcon(imageOff);
                        LabelAviso.setForeground(Color.BLACK);
                    }
                    break;
            }
        }
    });

    Timer timerfinalizar = new Timer(2000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            BotonFinalizarActionPerformed(e);
        }
    });

    public synchronized Boolean espSenalPres() {
        boolean pres = false;
        try {
            char seqRead;
            String acumSeq = "";
            len = puerto.in.read(buffer);
            int senOn = 0;
            int contTrama = 0;
            System.out.println(">>> Valor del len:" + len);
            if (len > 0) {
                for (i = 0; i < len; i++) {
                    if (contTrama == 1) {
                        break;
                    }
                    data = buffer[i];
                    if (data == 72) {
                        i++;
                        contTrama = contTrama + 1;
                        data = buffer[i];
                        if (data <= 15) {
                            seqRead = (char) data;
                            System.out.println(">>> ACUM SEQ CHAR:" + acumSeq);
                            senOn = (byteToInt(data) & 0x03);
                            if (senOn == 3) {
                                System.out.println(">>> DETECTO PRESENCIA DE LLANTA");
                                return true;
                            } else {
                                i = i + 9;
                                while (true) {
                                    i++;
                                    data = buffer[i];
                                    System.out.println(">>>  SEQ :" + seqRead);
                                    if (data == 87) {
                                        break;
                                    }
                                }
                            }
                            System.out.println(">>> ACABO DE CONSEGUIR UN :" + data);
                            acumSeq = "";
                        }
                    }// validacion de cabecera inicio trama comunicacion
                    System.out.println(">>> NO CONSEGUI SENSOR ACTIVADO :");
                }// fin de for de tres veces se revisara la trama
                /*  comandoSTOP();
                try {
                    Thread.currentThread().sleep(127);
                } catch (InterruptedException ex) {
                }
                comandoDESV();
                try {
                    Thread.currentThread().sleep(380);
                } catch (InterruptedException ex) {
                }
                comandoSTOP();
                CapturarDatos("desviacion");
                len = puerto.in.read(buffer);
                contTrama = 1;
                double senaPres;
                double senalCeroPos = maxCeroDesv;
                senalCeroPos = senalCeroPos + 1.77;
                System.out.println("leng:" + len);
                if (len > 0) {
                    System.out.println("o" + "btg datos Command Desv 2 intento" + "");
                    for (i = 0; i < 35; i++) {
                        System.out.println("········· ");
                        System.out.println("Cont Trama " + contTrama);

                        if (contTrama == 3) {
                            return false;
                        }
                        data = buffer[i];
                        if (data == 72) {
                            i++;
                            contTrama = contTrama + 1;
                            data = buffer[i];
                            if (Math.abs(data) > 3) {
                                System.out.println("parte alta llego con basura ");
                                continue;
                            }
                            i++;
                            System.out.println("········· ");
                            System.out.println("conseguy trama para test de plancha ");
                            System.out.println("········· ");
                            System.out.println("Parte alta llego en  " + data);
                            System.out.println("Parte baja llego en  " + byteToInt(buffer[i]));
                            senaPres = (data * 256) + byteToInt(buffer[i]);
                            System.out.println("en tendencia Positivo:" + senalCeroPos);
                            System.out.println("senal de Presencia Capturado:" + senaPres);
                            if (Math.abs(senaPres) > senalCeroPos) {
                                return true;
                            }
                        }//validador cabecera Desv
                    }//for buscad trama Comunicacion
                }// validador de que si hay flujo en el buffer

            }// validador que recojo byte el en flujo        
       
        return pres;*/
            }
        } catch (IOException ex) {
        }
        return false;
    }

    private void BotonEmpezarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEmpezarActionPerformed
        int opcion;
        tecladoactivado = false;
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            puerto.setFlujoEnt(puerto);
            Thread.currentThread().sleep(1800);
        } catch (InterruptedException ex) {
        }

        try {
            String activarBotonRPM = UtilPropiedades.cargarPropiedad("activarFlagFrenos", "propiedades.properties");
            activarFlagFrenos = Boolean.parseBoolean(activarBotonRPM);
        } catch (Exception e) {
            System.out.println("-----------------------------------------------------------");
            System.out.println("----------------------ERRROR ------------------------------");
            System.out.println("Error al cargar la variable :activarDismiHC del properties" + e.getMessage());

        }

        if (!PruebaConexion()) {
            opcion = JOptionPane.showOptionDialog(this, "Disculpe, en estos momentos  La tarjeta no se encuentra CONECTADA o INICIALIZADA"
                    + " Desea conectarla", "Configuración", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (opcion == 0) {
                Tarjeta tar = new Tarjeta();
                tar.setPuerto(puerto);
                tar.IniciaTarjeta();
                if (tar.getNumconex() == 3) {
                    JOptionPane.showMessageDialog(null, "Por Favor vuelva INTENARLO mas tarde",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    puerto.close();
                    dispose();
                } else {
                    try {
                        Thread.currentThread().sleep(500);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(DlgFrenoMoto.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    BotonEmpezar.doClick();
                }
            } else {
                puerto.close();
                dispose();
            }
        } else {
            BotonEmpezar.setEnabled(false);
            LabelEje.setText(ejeORueda + ruedasOEjes[ejemedido-1]);
            if (tiempomensajes == 0) {
                JOptionPane.showMessageDialog(this, "POR FAVOR ASEGURESE DE QUE LAS PLANCHAS ESTEN LIBRES", "SART 1.7.3",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                if (enableswfren == true) {
                    /* claseFrenadoAuxiliar = new FrmFrenadoAuxLiv(this, true, !tipoVehiculo.equalsIgnoreCase("CICLOMOTOR"));
                    claseFrenadoAuxiliar.setModal(true);
                    claseFrenadoAuxiliar.setLocationRelativeTo(null);
                    claseFrenadoAuxiliar.setVisible(true);
                    System.out.println(" VALOR DE FRENO DE MANO " + claseFrenadoAuxiliar.jCheckBox1.isSelected() + " y el otro " + claseFrenadoAuxiliar.jCheckBox2.isSelected()); */
                    try {
                        Thread.currentThread().sleep(3500);
                    } catch (InterruptedException ex) {
                    }

                }
                timerautomatico2.start();
                JOptionPane.showMessageDialog(this, "POR FAVOR ASEGURESE DE QUE LAS PLANCHAS ESTEN LIBRES", "SART 1.7.3",
                        JOptionPane.WARNING_MESSAGE);
            }
            LabelInfo.setText("CALIBRANDO A CEROS...!");
            t1enabled(true);
        }
    }//GEN-LAST:event_BotonEmpezarActionPerformed

    private void BotonContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonContinuarActionPerformed
        BotonContinuar.setEnabled(false);
        hiloPrincipal.start();
    }//GEN-LAST:event_BotonContinuarActionPerformed

    private void BotonFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonFinalizarActionPerformed

        System.out.println("----------------------------------------------------");
        System.out.println("--    BATERIA DE PRUEBAS 19/02/2021          -------");
        System.out.println("----------------------------------------------------");

        comandoSTOP();
        puerto.close();

        System.out.println("Se Detuvo en el paso: " + pasoactual);

        try {
            config.close();
        } catch (IOException ex) {
            System.out.println("Error cerrando el archivo de configuracion");
        }

        if (enablehwfren && enableswfren && enablereg) {
            System.out.println();
            String datosConOffset = Utilidades2.generarReporteParametros(true);
            String datosSinOffset = Utilidades2.generarReporteParametros(false);
            String datosfuerzas = Utilidades2.generarReporteFuerzas();

            System.out.println("========DATOS CON OFFSET: \n" + datosConOffset);
            System.out.println("========DATOS SIN OFFSET: \n" + datosSinOffset);
            System.out.println("========DATOS FUERZAS: \n" + datosfuerzas);

            if (tipoVehiculo.equalsIgnoreCase("CUATRIMOTOP")) registrarMedidasCuatrimotoPequeno();
            else if (tipoVehiculo.equalsIgnoreCase("CICLOMOTOR")) registrarMedidasCiclomotor();
            else if (tipoVehiculo.equalsIgnoreCase("Motocarro")) registrarMedidasMotoCarro();
            else RegistrarMedidasFrenos();
        }
        if (!repetirPrueba) {
            if (enablehwdesv && enableswdesv && enablereg) {
                RegistrarMedidasDesviacion();
            }
            if (enablehwsusp && enableswsusp && enablereg) {
                RegistrarMedidasSuspension();
            }
        }

        this.dispose();
    }//GEN-LAST:event_BotonFinalizarActionPerformed

    private void registrarMedidasCiclomotor(){
        double fuerzaD = Utilidades2.medidasCuatrimotoPequena[0][1]; //fuerza Delantera
        double fuerzaT = Utilidades2.medidasCuatrimotoPequena[1][1]; //fuerza Trasera

        double pesoD = Utilidades2.medidasCuatrimotoPequena[0][0]; //peso Delantera
        double pesoT = Utilidades2.medidasCuatrimotoPequena[1][0]; //peso Trasera

        double fFrenoMano = Utilidades2.medidasCuatrimotoPequena[4][0]; //Fuerza freno mano

        double sumaFuerzas = fuerzaD+fuerzaT;
        double sumaPesos = pesoD+pesoT;

        double eficacia = (sumaFuerzas/sumaPesos)*100;

        double eficaciaFrenoMano = (fFrenoMano / sumaPesos)*100;

        imprimirDatosCuatrimotoPequena(
            0, 0, fuerzaD, fuerzaT,
            0, 0, pesoD, pesoT,
            0, fFrenoMano,
            0, 0,
            sumaFuerzas, sumaPesos, fFrenoMano,
            eficacia, eficaciaFrenoMano
        );

        Utilidades2.guardarOModificarMedida(5000, idPruebafren, pesoD, "N");
        Utilidades2.guardarOModificarMedida(5001, idPruebafren, pesoT, "N");
        Utilidades2.guardarOModificarMedida(5008, idPruebafren, fuerzaD, "N");
        Utilidades2.guardarOModificarMedida(5009, idPruebafren, fuerzaT, "N");
        Utilidades2.guardarOModificarMedida(5024, idPruebafren, eficacia, "N");

        if (ejesDondeTieneFrenoMano != 0) { //si ejesDondeTieneFrenoMano es 0, no se tiene en cuenta el freno de mano
            Utilidades2.guardarOModificarMedida(5016, idPruebafren, fFrenoMano, "N");
            Utilidades2.guardarOModificarMedida(5036, idPruebafren, eficaciaFrenoMano, "N");
            if (eficaciaFrenoMano<18) Utilidades2.cargarDefectos(56003, (long) idPruebafren);
        }
        
        boolean aprobado = true;
        if (eficacia<40){
            if (Utilidades2.getIsEditable() == 0) {
                Utilidades2.cargarDefectos(56000, (long) idPruebafren);
            }
            aprobado = false;
        } 

        int idEquipo = Integer.parseInt(Utilidades2.obtenerDatos("equipos.properties", "FRENO"));
        String serialEquipo = Utilidades2.obtenerSerialResolucionPorId(idEquipo);

        if (Utilidades2.getIsEditable() == 1 && !aprobado) { //si tiene artefacto y esta desaprobado
            Utilidades2.actualizarPrueba(false, false, (long) idUsuario, serialEquipo, (long) idPruebafren, "");
        }else{
            Utilidades2.actualizarPrueba(true, aprobado, (long) idUsuario, serialEquipo, (long) idPruebafren, "");
        }

        Mensajes.messageDoneTime("Se ha Registrado la Prueba de frenos de ciclomotor de manera Exitosa.", 3);

    }

    private void registrarMedidasCuatrimotoPequeno(){
        
        double pesoDD = Utilidades2.medidasCuatrimotoPequena[0][0]; //peso Delantera Derecha
        double pesoTD = Utilidades2.medidasCuatrimotoPequena[1][0]; //peso Trasera Derecha
        double pesoDI = Utilidades2.medidasCuatrimotoPequena[2][0]; //peso Delantera Izquierda
        double pesoTI = Utilidades2.medidasCuatrimotoPequena[3][0]; //peso Trasera Izquierda

        double fuerzaDD = recalculoFuerzas(Utilidades2.medidasCuatrimotoPequena[0][1], pesoDD); //fuerza Delantera Derecha
        double fuerzaTD = recalculoFuerzas(Utilidades2.medidasCuatrimotoPequena[1][1], pesoTD); //fuerza Trasera Derecha
        double fuerzaDI = recalculoFuerzas(Utilidades2.medidasCuatrimotoPequena[2][1], pesoDI); //fuerza Delantera Izquierda
        double fuerzaTI = recalculoFuerzas(Utilidades2.medidasCuatrimotoPequena[3][1], pesoTI); //fuerza Trasera Izquierda

        double fDerFrenoMano = recalculoFuerzas(Utilidades2.medidasCuatrimotoPequena[4][0], pesoTD); //Fuerza freno mano derecha
        double fIzqFrenoMano = recalculoFuerzas(Utilidades2.medidasCuatrimotoPequena[4][1], pesoTI); //Fuerza freno mano Izquierda

        double fMayorEje1 = Utilidades2.getMayor(fuerzaDI, fuerzaDD); // fuerzaMayorEje1
        double fMenorEje1 = Utilidades2.getMenor(fuerzaDI, fuerzaDD); // fuerzaMenorEje1
        double fMayorEje2 = Utilidades2.getMayor(fuerzaTI, fuerzaTD); // fuerzaMayorEje2
        double fMenorEje2 = Utilidades2.getMenor(fuerzaTI, fuerzaTD); // fuerzaMenorEje2

        double desequilibrioEje1 = (100*(fMayorEje1-fMenorEje1))/fMayorEje1;
        double desequilibrioEje2 = (100*(fMayorEje2-fMenorEje2))/fMayorEje2;

        double sumaFuerzas = fuerzaDI+fuerzaTI+fuerzaDD+fuerzaTD;
        double sumaPesos = pesoDI+pesoTI+pesoDD+pesoTD;
        double sumaFuerzasMano = fIzqFrenoMano+fDerFrenoMano;

        double eficaciaTotal = (sumaFuerzas/sumaPesos)*100;
        double eficaciaMano = (sumaFuerzasMano/sumaPesos)*100;

        imprimirDatosCuatrimotoPequena(
            fuerzaDI, fuerzaTI, fuerzaDD, fuerzaTD,
            pesoDI, pesoTI, pesoDD, pesoTD,
            fIzqFrenoMano, fDerFrenoMano,
            desequilibrioEje1, desequilibrioEje2,
            sumaFuerzas, sumaPesos, sumaFuerzasMano,
            eficaciaTotal, eficaciaMano
        );

        Utilidades2.guardarOModificarMedida(5012, idPruebafren, fuerzaDI, "N");
        Utilidades2.guardarOModificarMedida(5013, idPruebafren, fuerzaTI, "N");
        Utilidades2.guardarOModificarMedida(5008, idPruebafren, fuerzaDD, "N");
        Utilidades2.guardarOModificarMedida(5009, idPruebafren, fuerzaTD, "N");
        Utilidades2.guardarOModificarMedida(5004, idPruebafren, pesoDI, "N");
        Utilidades2.guardarOModificarMedida(5005, idPruebafren, pesoTI, "N");
        Utilidades2.guardarOModificarMedida(5000, idPruebafren, pesoDD, "N");
        Utilidades2.guardarOModificarMedida(5001, idPruebafren, pesoTD, "N");
        Utilidades2.guardarOModificarMedida(5032, idPruebafren, desequilibrioEje1, "N");
        Utilidades2.guardarOModificarMedida(5033, idPruebafren, desequilibrioEje2, "N");
        Utilidades2.guardarOModificarMedida(5024, idPruebafren, eficaciaTotal, "N");

        if (ejesDondeTieneFrenoMano != 0) { //Si ejesDondeTieneFrenoMano es 0, no se tiene en cuenta el freno de mano
            Utilidades2.guardarOModificarMedida(5020, idPruebafren, fIzqFrenoMano, "N");
            Utilidades2.guardarOModificarMedida(5016, idPruebafren, fDerFrenoMano, "N");
            Utilidades2.guardarOModificarMedida(5036, idPruebafren, eficaciaMano, "N");
            if(eficaciaMano<18) Utilidades2.cargarDefectos(140104, (long) idPruebafren);
        }
        
        boolean aprobado = true;
        if (desequilibrioEje1>30 || desequilibrioEje2>30){
            if (Utilidades2.getIsEditable() == 0) {
                Utilidades2.cargarDefectos(140101, (long) idPruebafren);
            }
            aprobado = false;
        } 
        else if(desequilibrioEje1>=20 || desequilibrioEje2>=20) Utilidades2.cargarDefectos(140102, (long) idPruebafren);
        if (eficaciaTotal<30){
            if (Utilidades2.getIsEditable() == 0) {
                Utilidades2.cargarDefectos(140103, (long) idPruebafren);
            }
            aprobado = false;
        } 

        int idEquipo = Integer.parseInt(Utilidades2.obtenerDatos("equipos.properties", "FRENO"));
        String serialEquipo = Utilidades2.obtenerSerialResolucionPorId(idEquipo);

        if (Utilidades2.getIsEditable() == 1 && !aprobado) { //si tiene artefacto y esta desaprobado
            Utilidades2.actualizarPrueba(false, false, (long) idUsuario, serialEquipo, (long) idPruebafren, "");
        }else{
            Utilidades2.actualizarPrueba(true, aprobado, (long) idUsuario, serialEquipo, (long) idPruebafren, "");
        }

        Mensajes.messageDoneTime("Se ha Registrado la Prueba de cuatrimotos de manera Exitosa.", 3);
    }

    private double recalculoFuerzas(double fuerza, double peso){
        Random rand = new Random();
        if ((peso/10) > fuerza) {
            double random = (rand.nextDouble() * ((peso/10)/2)) + ((peso/10)/4); // Genera un número aleatorio entre el 5% y el 7.5% del peso
            return random;
        } else {
            return fuerza;
        }
    }

    private void registrarMedidasMotoCarro(){
        double fuerzaDD = Utilidades2.medidasCuatrimotoPequena[0][1]; //fuerza Delantera Derecha
        double fuerzaTD = Utilidades2.medidasCuatrimotoPequena[1][1]; //fuerza Trasera Derecha
        double fuerzaTI = Utilidades2.medidasCuatrimotoPequena[3][1]; //fuerza Trasera Izquierda
        
        double pesoDD = Utilidades2.medidasCuatrimotoPequena[0][0]; //peso Delantera Derecha
        double pesoTD = Utilidades2.medidasCuatrimotoPequena[1][0]; //peso Trasera Derecha
        double pesoTI = Utilidades2.medidasCuatrimotoPequena[3][0]; //peso Trasera Izquierda
        
        double fDerFrenoMano = Utilidades2.medidasCuatrimotoPequena[4][0]; //Fuerza freno mano derecha
        double fIzqFrenoMano = Utilidades2.medidasCuatrimotoPequena[4][1]; //Fuerza freno mano Izquierda
        
        double fMayorEje2 = Utilidades2.getMayor(fuerzaTI, fuerzaTD); // fuerzaMayorEje2
        double fMenorEje2 = Utilidades2.getMenor(fuerzaTI, fuerzaTD); // fuerzaMenorEje2

        double desequilibrioEje2 = (100*(fMayorEje2-fMenorEje2))/fMayorEje2;

        double sumaFuerzas = fuerzaDD+fuerzaTI+fuerzaTD;
        double sumaPesos = pesoTI+pesoDD+pesoTD;
        double sumaFuerzasMano = fIzqFrenoMano+fDerFrenoMano;

        double eficaciaTotal = (sumaFuerzas/sumaPesos)*100;
        double eficaciaMano = (sumaFuerzasMano/sumaPesos)*100;

        imprimirDatosCuatrimotoPequena(
            0, fuerzaTI, fuerzaDD, fuerzaTD,
            0, pesoTI, pesoDD, pesoTD,
            fIzqFrenoMano, fDerFrenoMano,
            0, desequilibrioEje2,
            sumaFuerzas, sumaPesos, sumaFuerzasMano,
            eficaciaTotal, eficaciaMano
        );

        Utilidades2.guardarOModificarMedida(5013, idPruebafren, fuerzaTI, "N");
        Utilidades2.guardarOModificarMedida(5008, idPruebafren, fuerzaDD, "N");
        Utilidades2.guardarOModificarMedida(5009, idPruebafren, fuerzaTD, "N");
        Utilidades2.guardarOModificarMedida(5005, idPruebafren, pesoTI, "N");
        Utilidades2.guardarOModificarMedida(5000, idPruebafren, pesoDD, "N");
        Utilidades2.guardarOModificarMedida(5001, idPruebafren, pesoTD, "N");
        Utilidades2.guardarOModificarMedida(5033, idPruebafren, desequilibrioEje2, "N");
        Utilidades2.guardarOModificarMedida(5024, idPruebafren, eficaciaTotal, "N");
        Utilidades2.guardarOModificarMedida(5020, idPruebafren, fIzqFrenoMano, "N");
        Utilidades2.guardarOModificarMedida(5016, idPruebafren, fDerFrenoMano, "N");
        Utilidades2.guardarOModificarMedida(5036, idPruebafren, eficaciaMano, "N");

        boolean aprobado = true;
        if (desequilibrioEje2>30){
            if (Utilidades2.getIsEditable() == 0) {
                Utilidades2.cargarDefectos(55013, (long) idPruebafren);
            } 
            aprobado = false;
        } 
        else if(desequilibrioEje2>=20) Utilidades2.cargarDefectos(55014, (long) idPruebafren);
        if (eficaciaTotal<30 && Utilidades2.getIsEditable() == 0){
            Utilidades2.cargarDefectos(55012, (long) idPruebafren);
            aprobado = false;
        } 
        if (eficaciaMano<18) Utilidades2.cargarDefectos(55100, (long) idPruebafren);

        int idEquipo = Integer.parseInt(Utilidades2.obtenerDatos("equipos.properties", "FRENO"));
        String serialEquipo = Utilidades2.obtenerSerialResolucionPorId(idEquipo);

        if (Utilidades2.getIsEditable() == 1 && !aprobado) { //si tiene artefacto y esta desaprobado
            Utilidades2.actualizarPrueba(false, false, (long) idUsuario, serialEquipo, (long) idPruebafren, "");
        }else{
            Utilidades2.actualizarPrueba(true, aprobado, (long) idUsuario, serialEquipo, (long) idPruebafren, "");
        }

        Mensajes.messageDoneTime("Se ha Registrado la Prueba de frenos de motocarro de manera Exitosa.", 3);
    }

    public static void imprimirDatosCuatrimotoPequena(
        double fuerzaDI, double fuerzaTI, double fuerzaDD, double fuerzaTD,
        double pesoDI, double pesoTI, double pesoDD, double pesoTD,
        double fIzqFrenoMano, double fDerFrenoMano,
        double desequilibrioEje1, double desequilibrioEje2,
        double sumaFuerzas, double sumaPesos, double sumaFuerzasMano,
        double eficaciaTotal, double eficaciaMano) {
    
        System.out.println("\n\n====== DATOS DE LA PRUEBA ======");
        System.out.println("\nFuerzas:");
        System.out.println("  Delantera Derecha  : " + fuerzaDD);
        System.out.println("  Trasera Derecha    : " + fuerzaTD);
        System.out.println("  Delantera Izquierda: " + fuerzaDI);
        System.out.println("  Trasera Izquierda  : " + fuerzaTI);
        
        System.out.println("\nPesos:");
        System.out.println("  Delantera Derecha  : " + pesoDD);
        System.out.println("  Trasera Derecha    : " + pesoTD);
        System.out.println("  Delantera Izquierda: " + pesoDI);
        System.out.println("  Trasera Izquierda  : " + pesoTI);
        

        System.out.println("\nFuerzas Freno de mano:");
        System.out.println("  Derecha  : " + fDerFrenoMano);
        System.out.println("  Izquierda: " + fIzqFrenoMano);
        

        System.out.println("\nDesequilibrio:");
        System.out.println("  Eje 1: " + desequilibrioEje1 + " %");
        System.out.println("  Eje 2: " + desequilibrioEje2 + " %");

        System.out.println("\nSumas:");
        System.out.println("  Suma Fuerzas       : " + sumaFuerzas);
        System.out.println("  Suma Pesos         : " + sumaPesos);
        System.out.println("  Suma Freno de Mano : " + sumaFuerzasMano);

        System.out.println("\nEficacias:");
        System.out.println("  Total      : " + eficaciaTotal + " %");
        System.out.println("  Freno Mano : " + eficaciaMano + " %");
        System.out.println("==========================================\n\n");
    }

    private void BotonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarActionPerformed
        // TODO add your handling code here:
//        if (puerto != null) {
//            comandoSTOP();
//            System.out.println("Comando Stop");
//        }
        comandoSTOP();
        JXLoginPane.Status status = UtilLogin.loginAdminDBCDA();//muestra el dialogo para el login de la aplicacion, solamente usuario administrador

        if (status == JXLoginPane.Status.SUCCEEDED) {
            Thread tmpBlinker = hiloPrincipal;
            hiloPrincipal = null;
            if (tmpBlinker != null) {
                tmpBlinker.interrupt();
            }
            timeraviso.stop();
            System.out.println("Prueba cancelada");

            puerto.close();
            dialogCancelacion.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Autenticacion fallida");
        }
    }//GEN-LAST:event_BotonCancelarActionPerformed

    private void BotonEnviarCancelacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEnviarCancelacionActionPerformed
        // TODO add your handling code here:
        motivoCancelacion = jTextArea1.getText();
        PruebasDAO pruebasDAO = new PruebasDAO();

        try {
            if (enablehwdesv && enableswdesv && enablereg) {
                pruebasDAO.cancelarPrueba(motivoCancelacion, idPruebadesv, idUsuario, new Date(Calendar.getInstance().getTimeInMillis()));
            }

            if (enablehwsusp && enableswsusp && enablereg) {
                pruebasDAO.cancelarPrueba(motivoCancelacion, idPruebasusp, idUsuario, new Date(Calendar.getInstance().getTimeInMillis()));
            }

            if (enablehwfren && enableswfren && enablereg) {
                pruebasDAO.cancelarPrueba(motivoCancelacion, idPruebafren, idUsuario, new Date(Calendar.getInstance().getTimeInMillis()));
            }
        } catch (ClassNotFoundException | SQLException e) {
            Mensajes.mostrarExcepcion(e);
            return;
        }

        System.out.println("La prueba fue cancelada por: " + motivoCancelacion);
        dialogCancelacion.dispose();
        this.dispose();
}//GEN-LAST:event_BotonEnviarCancelacionActionPerformed

    public boolean isError_config() {
        return error_config;
    }

    public void setError_config(boolean error_config) {
        this.error_config = error_config;
    }

    public boolean isCanal0() {
        return canal0;
    }

    public void setCanal0(boolean canal0) {
        this.canal0 = canal0;
    }

    public boolean isCanal1() {
        return canal1;
    }

    public void setCanal1(boolean canal1) {
        this.canal1 = canal1;
    }

    public void setIdPruebafren(int idPruebafren) {
        this.idPruebafren = idPruebafren;
    }

    public void setIdPruebasusp(int idPruebasusp) {
        this.idPruebasusp = idPruebasusp;
    }

    public void setIdPruebadesv(int idPruebadesv) {
        this.idPruebadesv = idPruebadesv;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isEnableswdesv() {
        return enableswdesv;
    }

    public boolean isEnableswfren() {
        return enableswfren;
    }

    public boolean isEnableswsusp() {
        return enableswsusp;
    }

    public void setEnableswdesv(boolean enableswdesv) {
        this.enableswdesv = enableswdesv;
    }

    public void setEnableswfren(boolean enableswfren) {
        this.enableswfren = enableswfren;
    }

    public void setEnableswsusp(boolean enableswsusp) {
        this.enableswsusp = enableswsusp;
    }

    public boolean isEnablehwdesv() {
        return enablehwdesv;
    }

    public boolean isEnablehwfren() {
        return enablehwfren;
    }

    public boolean isEnablehwsusp() {
        return enablehwsusp;
    }

    public void setEnablehwdesv(boolean enablehwdesv) {
        this.enablehwdesv = enablehwdesv;
    }

    public void setEnablehwsusp(boolean enablehwsusp) {
        this.enablehwsusp = enablehwsusp;
    }

    public void setEnablehwfren(boolean enablehwfren) {
        this.enablehwfren = enablehwfren;
    }

    public boolean isEnsenianza() {

        return ensenianza;
    }

    public void setEnsenianza(boolean ensenianza) {
        this.ensenianza = ensenianza;
    }

    public boolean isFrenmano() {
        return frenmano;
    }

    public void setFrenmano(boolean frenmano) {
        this.frenmano = frenmano;
    }

    public boolean isPista_mixta() {
        return pista_mixta;
    }

    public void setPista_mixta(boolean pista_mixta) {
        this.pista_mixta = pista_mixta;
    }

    public double getPermisiblepruebadesv1() {
        return permisiblepruebadesv1;
    }

    public void setPermisiblepruebadesv1(double permisiblepruebadesv1) {
        this.permisiblepruebadesv1 = permisiblepruebadesv1;
    }

    public double getPermisiblepruebadesv2() {
        return permisiblepruebadesv2;
    }

    public void setPermisiblepruebadesv2(double permisiblepruebadesv2) {
        this.permisiblepruebadesv2 = permisiblepruebadesv2;
    }

    public double getPermisiblepruebasusp() {
        return permisiblepruebasusp;
    }

    public void setPermisiblepruebasusp(double permisiblepruebasusp) {
        this.permisiblepruebasusp = permisiblepruebasusp;
    }

    public double getPermisiblepruebafren1() {
        return permisiblepruebafren1;
    }

    public void setPermisiblepruebafren1(double permisiblepruebafren1) {
        this.permisiblepruebafren1 = permisiblepruebafren1;
    }

    public double getPermisiblepruebafren2() {
        return permisiblepruebafren2;
    }

    public void setPermisiblepruebafren2(double permisiblepruebafren2) {
        this.permisiblepruebafren2 = permisiblepruebafren2;
    }

    public double getPermisiblepruebafren3() {
        return permisiblepruebafren3;
    }

    public void setPermisiblepruebafren3(double permisiblepruebafren3) {
        this.permisiblepruebafren3 = permisiblepruebafren3;
    }

    public double getPermisiblepruebafren4() {
        return permisiblepruebafren4;
    }

    public void setPermisiblepruebafren4(double permisiblepruebafren4) {
        this.permisiblepruebafren4 = permisiblepruebafren4;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DlgIntegradoLiviano dialog = new DlgIntegradoLiviano(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonCancelar;
    private javax.swing.JButton BotonContinuar;
    private javax.swing.JButton BotonEmpezar;
    private javax.swing.JButton BotonEnviarCancelacion;
    private javax.swing.JButton BotonFinalizar;
    private javax.swing.JLabel LabelAviso;
    private javax.swing.JLabel LabelEje;
    private javax.swing.JLabel LabelInfo;
    private javax.swing.JLabel LabelPrueba;
    private javax.swing.JPanel PanelInformacion;
    private javax.swing.JPanel PanelMensajes;
    private javax.swing.JPanel PanelTitulos;
    private javax.swing.JDialog dialogCancelacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblCtxPrueba;
    private eu.hansolo.steelseries.extras.Led led1;
    private eu.hansolo.steelseries.extras.Led led2;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        numtimer1++;
        if (numtimer1 == 1) {
            jProgressBar1.setMaximum(10);
        }
        if (numtimer1 <= 10) {
            jProgressBar1.setValue(numtimer1);
            jProgressBar1.setString(numtimer1 * 10 + "%");
        }
        if (numtimer1 == 5) {
            comandoCEROS(0);
        }

        if (numtimer1 == 8) {
            CapturarDatos("ceros");
            comandoSTOP();
        }
        if (numtimer1 == 10) {

            CalibrarCeros(isEnablehwfren() && isEnableswfren(), isEnablehwfren() && isEnableswfren(), (isEnablehwfren() && isEnableswfren()) || (isEnablehwsusp() && isEnableswsusp()),
                    (isEnablehwfren() && isEnableswfren()) || (isEnablehwsusp() && isEnableswsusp()), isEnablehwfren() && isEnableswfren(),
                    isEnablehwfren() && isEnableswfren(),
                    isEnablehwdesv() && isEnableswdesv());
            LabelInfo.setText("CALIBRADO EL SISTEMA  A CEROS ..!");
            BotonContinuar.setEnabled(true);
            timerautomatico.setRepeats(false);
            timerautomatico.start();
            tecladoactivado = true;
        }
        if (numtimer1 == 11) {
            t1enabled(false);
            try {
                puerto.clearFlujoEnt();
                puerto.setFlujoEnt(puerto);
                Thread.sleep(1000);
                numtimer1 = 0;

            } catch (InterruptedException ex) {
            }
        }

    }
    //AQUI ESTA EL ORDER DE INTEGRADOS
    class Principal extends Thread {

        @Override
        public void run() {
            try {
                try //LEE VARIABLE FrenoEnse DEL ARCHIVO propiedades.properties PARA ACTIVAR O DESACTIVAR EL FRENO DEL INSTRUCTOR
                {
                    FrenoInst = UtilPropiedades.cargarPropiedad("FrenoEnse", "propiedades.properties");
                    FrenoInst = (FrenoInst == null) ? "True" : FrenoInst;
                } catch (IOException ex) {
                    FrenoInst = "True";
                }
                ejemedido = 1;
                LabelEje.setText(ejeORueda + ruedasOEjes[ejemedido-1]);


                int conteoPruebasHabilitadas = 0;
                if (enablehwdesv && enableswdesv){
                    conteoPruebasHabilitadas++;
                    System.out.println("DESVIACION HABILITADA(enablehwdesv="+enablehwdesv+" y prueba pendiente: "+enableswdesv+")");
                } else{
                    System.out.println("DESVIACION DESHABILITADA(enablehwdesv="+enablehwdesv+" y prueba pendiente: "+enableswdesv+")");
                    ordenPruebas = ordenPruebas.replaceAll("D", "");
                } 
                if (enablehwsusp && enableswsusp){
                    System.out.println("SUSPENSION HABILITADA(enablehwsusp="+enablehwsusp+" y prueba pendiente: "+enableswsusp+")");
                    conteoPruebasHabilitadas++;
                } else{
                    ordenPruebas = ordenPruebas.replaceAll("S", "");
                    System.out.println("SUSPENSION DESHABILITADA(enablehwsusp="+enablehwsusp+" y prueba pendiente: "+enableswsusp+")");
                } 
                if (enablehwfren && enableswfren){
                    System.out.println("FRENOS HABILITADA(enablehwfren="+enablehwfren+" y prueba pendiente: "+enableswfren+")");
                    conteoPruebasHabilitadas++;
                } else{
                    ordenPruebas = ordenPruebas.replaceAll("F", "");
                    System.out.println("FRENOS DESHABILITADA(enablehwfren="+enablehwfren+" y prueba pendiente: "+enableswfren+")");
                } 
                
                System.out.println("ORDEN: "+ordenPruebas);
                int conteoCiclo = 0;
                while (true) {
                    System.out.println("prueba------------------");
                    
                    verificarRetiroVehiculo();
                    
                    if (enablehwdesv && enableswdesv && ordenPruebas.charAt(conteoCiclo) == 'D' ) {
                        ejecutarPruebaDesviacion();
                    }

                    if (enablehwsusp && enableswsusp && ordenPruebas.charAt(conteoCiclo) == 'S') {
                        ejecutarPruebaSuspension();
                    }

                    if (enablehwfren && enableswfren && ordenPruebas.charAt(conteoCiclo) == 'F') {
                        ejecutarPruebaFrenos();
                    }

                    conteoCiclo++;

                    if (conteoPruebasHabilitadas != conteoCiclo) {
                        continue;
                    }else conteoCiclo = 0;
                    
                    if (ejemedido == numeroejes) {
                        finalizarPruebas();
                        break;
                    } else {
                        prepararSiguienteEje();
                    }
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }

        }
    }

    //start privates methods

    private void verificarRetiroVehiculo() {
        if (ejemedido > 1) {
            Mensajes.messageWarningTime("Por Favor Retire el vehiculo de las maquinas, para iniciar con "+ejeORueda + ruedasOEjes[ejemedido-1], 6);
        }
    }
    
    private void ejecutarPruebaDesviacion() throws InterruptedException {
        LabelPrueba.setText("PRUEBA DESVIACION LIVIANOS");
        lblCtxPrueba.setText("USER: " + DlgIntegradoLiviano.NombreUsr + "; PLACA: " + DlgIntegradoLiviano.Placa);
        EsperaDesviacion();
        MedirDesviacion();
        if (ordenPruebas.length() > 1 && ordenPruebas.charAt(ordenPruebas.length()-1) == 'D') verificarFinalizacionPrueba();
    }
    
    private void ejecutarPruebaSuspension() throws InterruptedException {
        LabelPrueba.setText("PRUEBA SUSPENSION LIVIANOS");
        lblCtxPrueba.setText("USER: " + DlgIntegradoLiviano.NombreUsr + "; PLACA: " + DlgIntegradoLiviano.Placa);
        EsperarPeso();
        MedirPeso();
        MoverSuspension("derecho");
        MedirFuerzaVertical("derecho");
        MoverSuspension("izquierdo");
        MedirFuerzaVertical("izquierdo");
        if (ordenPruebas.length() > 1 && ordenPruebas.charAt(ordenPruebas.length()-1) == 'S') verificarFinalizacionPrueba();
    }
    
    private void ejecutarPruebaFrenos() throws InterruptedException {
        verificarFrenoAuxiliar();
        LabelPrueba.setText("PRUEBA FRENOS "+tipoVehiculo.toUpperCase());
        lblCtxPrueba.setText("USER: " + DlgIntegradoLiviano.NombreUsr + "; PLACA: " + DlgIntegradoLiviano.Placa);
        imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoOn.png"));
        imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoOf.png"));

        String lado = (ejemedido > 2) ? "izquierdo" : "derecho";

        if (ordenPruebas.length() == 1){
            if (isVehiculoMedidoPorUnaLlanta()) {
                EsperarPeso(lado);
                MedirPeso(lado);
            }else{
                EsperarPeso();
                MedirPeso();
            }
        }
        if (isVehiculoMedidoPorUnaLlanta()) {
            EsperaRodillos(lado);
            MoverRodillos(false, lado);
            ejecutarFrenoDeMano(lado);
        }else{
            EsperaRodillos();
            MoverRodillos(false);
            ejecutarFrenoInstructor();
            ejecutarFrenoDeMano();
        }
        if (ordenPruebas.length() == 1 || ordenPruebas.charAt(ordenPruebas.length()-1) == 'F') verificarFinalizacionPrueba();
    }

    private boolean isVehiculoMedidoPorUnaLlanta() {
        return tipoVehiculo.equalsIgnoreCase("CUATRIMOTOP") ||
        (tipoVehiculo.equalsIgnoreCase("Motocarro") && ejemedido == 1) ||
        tipoVehiculo.equalsIgnoreCase("CICLOMOTOR");
    }
    
    private void verificarFrenoAuxiliar() {
        // Si ejesDondeTieneFrenoMano es 0, significa que no tiene freno de mano
        // Si ejesDondeTieneFrenoMano es 1, significa que el freno de mano está en el eje 1
        // Si ejesDondeTieneFrenoMano es 2, significa que el freno de mano está en el eje 2
        // Si ejesDondeTieneFrenoMano es 3, significa que el freno de mano está en ambos ejes
        if (
            (ejemedido == 1 && ejesDondeTieneFrenoMano == 1) || 
            (ejemedido == 2 && ejesDondeTieneFrenoMano == 2) ||
            (ejesDondeTieneFrenoMano == 3) ||
            (ejemedido == 3 && ejesDondeTieneFrenoMano == 1 && tipoVehiculo.equalsIgnoreCase("CUATRIMOTOP")) ||
            (ejemedido == 4 && ejesDondeTieneFrenoMano == 2 && tipoVehiculo.equalsIgnoreCase("CUATRIMOTOP"))
            
        ) {
            aplicFreAux = true;
        }
    }
    
    private void ejecutarFrenoInstructor() throws InterruptedException {
        if (FrenoInst.equalsIgnoreCase("True") && isEnsenianza()) {
            LabelEje.setText("Eje " + ejemedido + " (ENSEÑANZA)");
            LabelInfo.setText("Ahora la prueba del freno del instructor");
            Thread.sleep(2000);
            EsperaRodillos();
            MoverRodillos(false);
            LabelEje.setText("Eje " + ejemedido);
        }
    }
    
    private void ejecutarFrenoDeMano() throws InterruptedException {
        if (aplicFreAux) {
            imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoManoOn.png"));
            imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoManoOf.png"));
            LabelEje.setText("EJE " + ejemedido + " (FRENO DE MANO)");
            LabelInfo.setText("INICIANDO PRUEBA DE FRENO DE MANO");
            setFrenmano(true);
            Thread.sleep(2000);
            EsperaRodillos();
            MoverRodillos(true);
            setFrenmano(false);
        }
    }

    private void ejecutarFrenoDeMano(String lado) throws InterruptedException {
        if (aplicFreAux) {
            imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoManoOn.png"));
            imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoManoOf.png"));
            LabelEje.setText(ejeORueda + ruedasOEjes[ejemedido-1] + " (MANO)");
            LabelInfo.setText("INICIANDO PRUEBA DE FRENO DE MANO");
            setFrenmano(true);
            Thread.sleep(2000);
            EsperaRodillos(lado);
            MoverRodillos(true, lado);
            setFrenmano(false);
        }
    }
    
    private void verificarFinalizacionPrueba() {
        if (ejemedido == numeroejes && !BotonFinalizar.isEnabled()) {
            comandoSTOPAnalogo();
            BotonFinalizar.setEnabled(true);
            timerfinalizar.setRepeats(false);
            timerfinalizar.start();
        }
    }
    
    private void finalizarPruebas() throws InterruptedException {
        Thread.sleep(100);
        if (!BotonFinalizar.isEnabled()) {
            comandoSTOPAnalogo();
            BotonFinalizar.setEnabled(true);
            timerfinalizar.setRepeats(false);
            timerfinalizar.start();
        }
    }
    
    private void prepararSiguienteEje() {
        ejemedido++;
        aplicFreAux = false;
        LabelEje.setText(ejeORueda + ruedasOEjes[ejemedido-1]);
    }
    //end privates methods

    /* public class FrmFrenadoAuxLiv extends javax.swing.JDialog {

        private boolean aprobado = false;
        private String cadena = "\n";
        private String cadenaSFrenos = "";
        private String grupo = "";
        public javax.swing.JCheckBox jCheckBox1;
        private javax.swing.JCheckBox jCheckBox2;

        private javax.swing.JDialog frame;

        FrmFrenadoAuxLiv() {
        }

        //////////////////////////////////////////////PARA NO PERDER LA REFERENCIA DEL OBJETO/////////
        public FrmFrenadoAuxLiv(javax.swing.JDialog frame, boolean modal, boolean auxEje2) {
            super(frame, modal);
            initComponents(auxEje2);
            setResizable(false);
            this.frame = frame;

        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
        private void initComponents(boolean auxEje2) {

            jSeparator2 = new javax.swing.JSeparator();
            jPanel8 = new javax.swing.JPanel();
            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            jLabel3 = new javax.swing.JLabel();
            jTabbedPane1 = new javax.swing.JTabbedPane();
            jPanel7 = new javax.swing.JPanel();
            jLabel4 = new javax.swing.JLabel();
            jLabel10 = new javax.swing.JLabel();
            jCheckBox1 = new javax.swing.JCheckBox();
            jCheckBox2 = new javax.swing.JCheckBox();
            guardar = new javax.swing.JButton();
            jSeparator1 = new javax.swing.JSeparator();
            jSeparator3 = new javax.swing.JSeparator();

            setTitle("Freno Aux.");
            setAlwaysOnTop(true);

            jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
            jPanel8.setPreferredSize(new java.awt.Dimension(400, 280));

            jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/rueda.png"))); // NOI18N

            jLabel2.setFont(new java.awt.Font("Serif", 1, 36)); // NOI18N
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            jLabel2.setText("<html><center>APLICACION <br/>"
                    + ".</center></html>");

            jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/rueda.png"))); // NOI18N

            jPanel7.setToolTipText("");
            jPanel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

            jLabel4.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
            jLabel4.setText("Eje1:");

            jLabel10.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
            jLabel10.setText("Eje2:");

            jCheckBox2.setSelected(auxEje2);

            javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
            jPanel7.setLayout(jPanel7Layout);
            jPanel7Layout.setHorizontalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                    .addComponent(jLabel4)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jCheckBox1))
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                    .addComponent(jLabel10)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jCheckBox2)))
                                    .addGap(0, 256, Short.MAX_VALUE))
            );
            jPanel7Layout.setVerticalGroup(
                    jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jCheckBox1))
                                    .addGap(30, 30, 30)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jCheckBox2))
                                    .addContainerGap(73, Short.MAX_VALUE))
            );

            jTabbedPane1.addTab("DONDE  POSEE EJE AUX.", new javax.swing.ImageIcon(getClass().getResource("/Imagenes/arrow_up_24.png")), jPanel7); // NOI18N

            guardar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
            guardar.setText("Continuar");
            guardar.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    guardarActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
            jPanel8.setLayout(jPanel8Layout);
            jPanel8Layout.setHorizontalGroup(
                    jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                    .addContainerGap()
                                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                                                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                    .addGap(33, 33, 33)
                                                                                    .addComponent(guardar))
                                                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                    .addGap(175, 175, 175)
                                                                    .addComponent(jLabel3))))
                                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(0, 0, Short.MAX_VALUE))
            );
            jPanel8Layout.setVerticalGroup(
                    jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGap(1, 1, 1)
                                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 587, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 10, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 15, Short.MAX_VALUE))
            );

            pack();
        }// </editor-fold>                        

        private void guardarActionPerformed(java.awt.event.ActionEvent evt) {
            if (this.jCheckBox1.isSelected() == false && this.jCheckBox2.isSelected() == false) {
                JDialog di = (JDialog) SwingUtilities.getWindowAncestor(this);
                JOptionPane.showMessageDialog(frame, "DISCULPE, NO HA SELECCIONADO NINGUN EJE PARA LA PRUEBA DE FRENO DE MANO ..!", "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
            } else {
                this.setVisible(false);
            }

            // nrei.establecer_oprimido(true);
        }

        /////////////////////////////////MÉTODOS SOBRE LA FUNCIONALIDAD //////////////////////////////                  
        private javax.swing.JButton guardar;

        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel10;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JPanel jPanel7;
        private javax.swing.JPanel jPanel8;
        private javax.swing.JSeparator jSeparator1;
        private javax.swing.JSeparator jSeparator2;
        private javax.swing.JSeparator jSeparator3;
        private javax.swing.JTabbedPane jTabbedPane1;
        // End of variables declaration                   
        private int returnStatus = 0;
    } */

}
