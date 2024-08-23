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

import Utilidades.CreacionCarpetas;
import Utilidades.UtilPropiedades;
import com.soltelec.loginadministrador.UtilLogin;
import com.soltelec.modulopuc.utilidades.Mensajes;
import dao.PruebaDefaultDAO;
import dao.PruebasDAO;
import excepciones.NoPersistException;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Frame;
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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import modelo.Desviacion;
import modelo.Frenos;
import modelo.FrenoMotoCarro;
import static modelo.Frenos.eficacia;
import modelo.Suspension;
import org.jdesktop.swingx.JXLoginPane;
import static vistas.DlgIntegradoPesado.byteToInt;

/**
 *
 * @author Gerencia TIC
 */
public class DlgIntegradoMotoCarro extends javax.swing.JDialog implements ActionListener {

    private double factorescala;
    //private String CalificacionPrueba;

    private double permisiblepruebafren1;  //Valor permisible para la eficacia total de frenos
    private double permisiblepruebafren2;  //Valor permisible para el freno de mano
    private double permisiblepruebafren3;  //Valor permisible para el desequilibrio tipo A
    private double permisiblepruebafren4;  //Valor permisible para el desequilibrio tipo B
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private boolean enablereg = false; //Habilitación de registro de medidas en el servidor
    private boolean enablebackup = false; //Habilitación de generar backup del registro de medidas
    private boolean enablehwdesv, enablehwsusp, enablehwfren;  //Habilitacion por parte
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
    private static String Lado;
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
    private double velminimad, velminimai, umbral_velo, umbral_peso;
    private Principal hiloPrincipal = new Principal();
    private boolean frenmano = false; //Indica si se esta haciendo la prueba del freno de mano
    private boolean ensenianza = false;
    private float factor_desq;
    private String tipoPista = "mixta";
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
    private FrmFrenadoAuxLiv frm;
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

    private DlgIntegradoMotoCarro(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize(this.getToolkit().getScreenSize());
        dialogCancelacion.setLocationRelativeTo(null);
        this.setTitle("SART 1.7.3 MOD FRENOS PARA MOTOCARRO");
        umbral_peso = 0;
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

    public DlgIntegradoMotoCarro(java.awt.Frame parent, int idPruebadesv, int idPruebasusp, int idPruebafren, int idUsuario, int idHojaPrueba, Boolean esEnsenanza, int aplicTrans, String ipEquipo, String tipoVehiculo, String Placa, String NombreUsr) {
        this(parent, true);
        this.idPruebadesv = idPruebadesv;
        this.idPruebasusp = idPruebasusp;
        this.idPruebafren = idPruebafren;
        this.idUsuario = idUsuario;
        this.ensenianza = esEnsenanza;
        enableswdesv = idPruebadesv > 0;
        enableswfren = idPruebafren > 0;
        enableswsusp = idPruebasusp > 0;
        this.aplicTrans = aplicTrans;
        this.ipEquipo = ipEquipo;
        this.tipoVehiculo = tipoVehiculo;
        this.Placa = Placa;
        this.NombreUsr = NombreUsr;
    }

    /**
     *
     * Autor ELKIN B
     */
    private void leyendoArchivoConfiguraciones() {
        System.out.println("----------------------------------------------------");
        System.out.println("----------leyendoArchivoConfiguraciones-------------");
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

            System.out.println("--------------------------------------------------------------leyendo [BANCO]-----------------------------------------------------------------------------");

            while (!config.readLine().startsWith("[BANCO]")) {
            }
            while (!config.readLine().startsWith("suspension:")) {
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

            System.out.println("-------------Lectura de configuración de parametros para el software--------------------------------------");

            while (!config.readLine().startsWith("[SOFTWARE]")) {
            }
            if ((line = config.readLine()).startsWith("pruebas_unidas:")) {
                System.out.println(line.substring(line.indexOf(" ") + 1, line.length()) + " se haran las pruebas integradas");
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo pruebas_unidas.... Llame a servicio técnico",
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
                enablereg = (line.substring(line.indexOf(" ") + 1, line.length())).equalsIgnoreCase("si");
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

            if ((line = config.readLine()).startsWith("rodillo_motocarro:")) {
                Lado = line.substring(line.indexOf(" ") + 1, line.length());
                Lado = Lado.toUpperCase();
                System.out.println("se van a tomar las medidas de del eje uno del motocarro por el lado " + Lado);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo rodillo_motocarro:. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
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
                umbral_peso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
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
            //aqui se esta llamando los datos de motocarro del archivo de configuracion
            while (!config.readLine().startsWith("motocarro:")) {
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

                if (isEnablehwfren()) {
                    //validamos si la carpeta ./BackUpMotocarro existe, si no se realiza la creacion de esta en la misma ruta donde se encuentra el sart
                    System.out.println(CreacionCarpetas.CrearCarpeta("./BackUpMotocarro", "BackUpMotocarro"));

                    //validamos si existen los archivos de peso para cada uno de los ejes, sino el aplicativo los crea automaticamente                    
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup_Datos_Peso_Eje_1", ".txt"));
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup_Datos_Peso_Eje_2_Derecho", ".txt"));
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup_Datos_Peso_Eje_2_Izquierdo", ".txt"));

                    //validamos si existen los archivos de datos para freno para cada uno de los ejes, sino el aplicativo los crea automaticamente
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup_Datos_Freno_Eje_1", ".txt"));
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup_Datos_Freno_Derecho_Eje_2", ".txt"));
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup_Datos_Freno_Izquierdo_Eje_2", ".txt"));
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup Datos Freno de mano Derecha", ".txt"));
                    System.out.println(CreacionCarpetas.CrearArchivo("./BackUpMotocarro/", "Backup Datos Frenos de mano Izquierda", ".txt"));

                    regdatospd1 = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup_Datos_Peso_Eje_1.txt")));
                    regdatospd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE DELANTERO");
                    regdatospd1.newLine();
                    regdatospd1.flush();

                    regdatospd2 = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup_Datos_Peso_Eje_2_Derecho.txt")));
                    regdatospd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE TRASERO");
                    regdatospd2.newLine();
                    regdatospd2.flush();

                    regdatospi2 = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup_Datos_Peso_Eje_2_Izquierdo.txt")));
                    regdatospi2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO IZQUIERDO DEL EJE TRASERO");
                    regdatospi2.newLine();
                    regdatospi2.flush();

                    regdatosffd1 = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup_Datos_Freno_Eje_1.txt")));
                    regdatosffd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DEL EJE DELANTERO");
                    regdatosffd1.newLine();
                    regdatosffd1.flush();

                    regdatosffd2 = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup_Datos_Freno_Derecho_Eje_2.txt")));
                    regdatosffd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DERECHA DEL EJE TRASERO");
                    regdatosffd2.newLine();
                    regdatosffd2.flush();

                    regdatosffi2 = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup_Datos_Freno_Izquierdo_Eje_2.txt")));
                    regdatosffi2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO IZQUIERDA DEL EJE TRASERO");
                    regdatosffi2.newLine();
                    regdatosffi2.flush();

                    regdatosffda = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup Datos Freno de mano Derecha.txt")));
                    regdatosffda.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DE MANO DERECHA");
                    regdatosffda.newLine();
                    regdatosffda.flush();

                    regdatosffia = new BufferedWriter(new FileWriter(new File("./BackUpMotocarro/Backup Datos Frenos de mano Izquierda.txt")));
                    regdatosffia.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DE MANO IZQUIERDA");
                    regdatosffia.newLine();
                    regdatosffia.flush();

                    System.out.println("termine de crear los archivos de backup para motocarro");
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
            spanfd = (Flag == null) ? Double.parseDouble(archivop.getProperty("spanfd")) : Double.parseDouble(archivop.getProperty("spanfdl"));
            spanfi = (Flag2 == null) ? Double.parseDouble(archivop.getProperty("spanfi")) : Double.parseDouble(archivop.getProperty("spanfil"));
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
            System.out.println("LOS SPAN DINAMICOS DE PESOS SON  spanpdTD" + spanpdTD + " spanpdTI " + spanpdTI + " spanpdDD " + spanpdDD + " spanpdDD " + spanpdDI);

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
            leyendoArchivoConfiguraciones();
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
                System.out.println("salida alta: " + salidaalta);
                salidabaja = Byte.parseByte(line.substring(line.indexOf(",") + 1, line.length()), 2);
                System.out.println("salida baja:" + salidabaja);
            }
            if ((line = config.readLine()).startsWith("tiempo:")) {
                tiempo_paso = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            }
        } catch (IOException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Se perdio de lineas");
        }
        //System.out.println("sa "+salidaalta+" sb "+salidabaja+" tiempo paso "+tiempo_paso);
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
    public void CalibrarCeros(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, String lado) {
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
            valcalcero3 = CalcularMediana(Datos3);
            System.out.println("cero peso derecho: " + (valcalcero3 * spanpd) + " N");
        }
        Datos3.clear();
        if (d) {
            valcalcero4 = CalcularMediana(Datos4);
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
                    for (this.t = 0; this.t < s; this.t += 1) {
                        this.regdatosffd1.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos1.get(this.t)).toString());
                        this.regdatosffd1.newLine();
                    }
                    this.regdatosffd1.close();
                } else if (this.ejemedido == 2) {
                    if (!isFrenmano()) {
                        regdatosffd2.write(new StringBuilder().append("el cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                        this.regdatosffd2.newLine();
                        this.regdatosffd2.write(new StringBuilder().append("el span de fuerza derecha es: ").append(this.spanfd).toString());
                        this.regdatosffd2.newLine();
                        this.regdatosffd2.flush();
                        for (this.t = 0; this.t < s; this.t += 1) {
                            this.regdatosffd2.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos1.get(this.t)).toString());
                            this.regdatosffd2.newLine();
                        }
                        this.regdatosffd2.close();
                    } else {
                        this.regdatosffda.write(new StringBuilder().append("(Freno Mano) El cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                        this.regdatosffda.newLine();
                        this.regdatosffda.write(new StringBuilder().append("(Freno Mano) El span de fuerza derecha es: ").append(this.spanfd).toString());
                        this.regdatosffda.newLine();
                        this.regdatosffda.flush();
                        for (this.t = 0; this.t < s; this.t += 1) {
                            this.regdatosffda.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos1.get(this.t)).toString());
                            this.regdatosffda.newLine();
                        }
                        this.regdatosffda.close();
                    }
                }
                if (this.ejemedido == 1) {
                    this.regdatosffi1.write(new StringBuilder().append("el cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                    this.regdatosffi1.newLine();
                    this.regdatosffi1.write(new StringBuilder().append("el span de fuerza izquierda es: ").append(this.spanfi).toString());
                    this.regdatosffi1.newLine();
                    this.regdatosffi1.flush();
                    for (this.t = 0; this.t < s; this.t += 1) {
                        this.regdatosffi1.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos2.get(this.t)).toString());
                        this.regdatosffi1.newLine();
                    }
                    regdatosffi1.close();
                } else if (this.ejemedido == 2) {
                    if (!isFrenmano()) {
                        this.regdatosffi2.write(new StringBuilder().append("el cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                        this.regdatosffi2.newLine();
                        this.regdatosffi2.write(new StringBuilder().append("el span de fuerza izquierda es: ").append(this.spanfi).toString());
                        this.regdatosffi2.newLine();
                        this.regdatosffi2.flush();
                        for (this.t = 0; this.t < s; this.t += 1) {
                            //revisar linea
                            this.regdatosffi2.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos2.get(this.t)).toString());
                            this.regdatosffi2.newLine();
                        }
                        this.regdatosffi2.close();
                    } else {
                        this.regdatosffia.write(new StringBuilder().append("(Freno Mano) El cero de fuerza izquierda es: ").append(this.valcalcero2).toString());
                        this.regdatosffia.newLine();
                        this.regdatosffia.write(new StringBuilder().append("(Freno Mano) El span de fuerza izquierda es: ").append(this.spanfi).toString());
                        this.regdatosffia.newLine();
                        this.regdatosffia.flush();
                        for (this.t = 0; this.t < s; this.t += 1) {
                            this.regdatosffia.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos2.get(this.t)).toString());
                            this.regdatosffia.newLine();
                        }
                        this.regdatosffia.close();
                    }
                }
            } catch (IOException ex) {
                System.out.println(new StringBuilder().append("No se pudo escribir el dato de peso en el backup ").append(ex).toString());
            }
        }
        if (auxfuerzad - this.valcalcero1 < 0.0) {
            if (freAux == false) {
                System.out.println("Entro Trata de Fuerza Menor A Cero " + auxfuerzad);
                this.fuerzasfd.add(Math.abs(auxfuerzad - this.valcalcero1));
                // this.fuerzasfd.add(Double.valueOf(ThreadLocalRandom.current().nextDouble(15.10, 25.99)));
            } else {
                fuerzasfdAux.add(Math.abs(auxfuerzad - this.valcalcero1));
            }
        } else {
            if (freAux == false) {
                this.fuerzasfd.add(Double.valueOf(auxfuerzad - this.valcalcero1));
            } else {
                fuerzasfdAux.add(auxfuerzad - valcalcero1);
            }
        }

        if (auxfuerzai - this.valcalcero2 < 0.0) {
            if (freAux == false) {
                System.out.println("EntroTratadeFuerzaMenorACero " + auxfuerzai);
                // this.fuerzasfi.add(Double.valueOf(ThreadLocalRandom.current().nextDouble(15.10, 25.99)));
                this.fuerzasfi.add(Math.abs(auxfuerzai - this.valcalcero2));
            } else {
                fuerzasfiAux.add(Math.abs(auxfuerzai - this.valcalcero2));
            }
        } else {
            if (freAux == false) {
                this.fuerzasfi.add(Double.valueOf(auxfuerzai - this.valcalcero2));
            } else {
                fuerzasfiAux.add(auxfuerzai - valcalcero2);
            }
        }
        if (freAux == false) {
            bc = fuerzasfd.size();
        } else {
            bc = fuerzasfdAux.size();
        }
        System.out.println(new StringBuilder().append("de nuevo los cero de fuerza derecho e izquierdo ").append(this.valcalcero1 * this.spanfd).append(" N ").append(this.valcalcero2 * this.spanfi).append(" N").toString());

        System.out.println(new StringBuilder().append("fuerza sin calibrar ").append(this.ejemedido).append(" derecha e izquierda: ").append(auxfuerzad * this.spanfd).append(" N ").append(auxfuerzai * this.spanfi).append(" N").toString());

        //System.out.println(new StringBuilder().append("fuerza del eje ").append(this.ejemedido).append(" derecha e izquierda: ").append(((Double) this.fuerzasfd.get(bc - 1)).doubleValue() * this.spanfd).append(" N ").append(((Double) this.fuerzasfi.get(bc - 1)).doubleValue() * this.spanfi).append(" N").toString());
        this.Datos1.clear();
        this.Datos2.clear();
        this.Datosfil1.clear();
        this.Datosfil2.clear();
    }

    public void medirFuerzaFrenadoEje1(boolean freAux) {
        //implementar
        armonizar(this.Datos1, this.valcalcero1);
        this.Datosfil1 = filtrar(this.Datos1);
        double auxfuerzad = CalcularMaximo(this.Datosfil1);
        auxfuerzad = round(auxfuerzad, 5);
        System.out.println(new StringBuilder().append("Maximo fuerza der: ").append(auxfuerzad).toString());
        long s = this.Datosfil1.size() - 1;

        int bc;
        if (this.enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatosffd1.write(new StringBuilder().append("el cero de fuerza derecha es: ").append(this.valcalcero1).toString());
                    regdatosffd1.newLine();
                    regdatosffd1.write(new StringBuilder().append("el span de fuerza derecha es: ").append(this.spanfd).toString());
                    regdatosffd1.newLine();
                    regdatosffd1.flush();
                    for (this.t = 0; this.t < s; this.t += 1) {
                        this.regdatosffd1.write(new StringBuilder().append(((Double) this.Datosfil1.get(this.t)).toString()).append("  ").append(this.Datos1.get(this.t)).toString());
                        this.regdatosffd1.newLine();
                    }
                    this.regdatosffd1.close();
                }

            } catch (IOException ex) {
                System.out.println(new StringBuilder().append("No se pudo escribir el dato de peso en el backup ").append(ex).toString());
            }
        }
        if (auxfuerzad - this.valcalcero1 < 0.0) {
            if (freAux == false) {
                System.out.println("Entro Trata de Fuerza Menor A Cero " + auxfuerzad);
                this.fuerzasfd.add(Math.abs(auxfuerzad - this.valcalcero1));
                // this.fuerzasfd.add(Double.valueOf(ThreadLocalRandom.current().nextDouble(15.10, 25.99)));
            } else {
                fuerzasfdAux.add(Math.abs(auxfuerzad - this.valcalcero1));
            }
        } else {
            if (freAux == false) {
                this.fuerzasfd.add(Double.valueOf(auxfuerzad - this.valcalcero1));
            } else {
                fuerzasfdAux.add(auxfuerzad - valcalcero1);
            }
        }

        if (freAux == false) {
            bc = fuerzasfd.size();
        } else {
            bc = fuerzasfdAux.size();
        }

        this.Datos1.clear();
        //this.Datos2.clear();
        this.Datosfil1.clear();
        //this.Datosfil2.clear();
    }

    public void MedirFuerzaVertical(String lado) throws InterruptedException {
        //double ab = CalcularMaximo(Datos1);
        double auxfuerzad, auxfuerzai;
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
                auxfuerzad = CalcularMinimo(Datos3, contoff);

                System.out.println("-----------------------------");
                System.out.println("- AUXILIAR FUERZA DERECHA ---");
                System.out.println("--" + auxfuerzad + "--");
                System.out.println("-----------------------------");

                System.out.println(">>>>>>>>>>Valor Minimo DEL EJE :Derecho " + auxfuerzad);
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
                            regdatosfvd1.write(">>>>>>>>>>Valor Minimo DEL EJE 1: " + auxfuerzad);
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
                            regdatosfvd2.write(">>>>>>>>>>Valor Minimo DEL EJE 2: " + auxfuerzad);
                            regdatosfvd2.newLine();
                            regdatosfvd2.close();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension derecha en el backup " + ex);
                    }
                }

                if ((auxfuerzad - valcalcero3) < 0) {
                    fuerzasvd.add(0.0);
                    System.out.println("ENTRO EN CALCULOS CERO DE FUERZAS ..!");
                } else {//pesosd.get(ejemedido-1)
                    Double factorPesoMov = auxfuerzad;// -  valcalcero3; 
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
                auxfuerzai = CalcularMinimo(Datos4, contoff);
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
                            regdatosfvi1.write(">>>>>>>>>>Valor Minimo DEL EJE 1: " + auxfuerzai);
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
                            regdatosfvi2.write(">>>>>>>>>>Valor Minimo DEL EJE 2: " + auxfuerzai);
                            regdatosfvi2.newLine();
                            regdatosfvi2.close();
                        }
                    } catch (IOException ex) {
                        System.out.println("No se pudo escribir el dato de suspension izquierda en el backup " + ex);
                    }
                }

                if ((auxfuerzai - valcalcero4) < 0) {
                    System.out.println("La operacion algebraica es " + (auxfuerzai - valcalcero4));
                    fuerzasvi.add(0.0);
                } else {    //pesosi.get(ejemedido-1);                
                    Double factorPesoMov = auxfuerzai; //- valcalcero4;    
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
                System.out.println("fuerza sin calibrar " + ejemedido + " izquierda: " + (auxfuerzai) + " MV");
                System.out.println("fuerza del eje " + ejemedido + " izquierda: " + (fuerzasvi.get(bc - 1)) + "");
                break;
        }
        Datos3.clear();
        Datos4.clear();
        Datosfil1.clear();
        Datosfil2.clear();
    }

    public void medirPesoEje1() throws InterruptedException {
        double auxpesod, auxpesoi;
        int bc;
        long s, r;
        LabelInfo.setText("MIDIENDO PESO...!");
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
        CapturarDatos("ceros");
        s = Datos3.size();
        r = Datos4.size();
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }

        if (enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatospd1.write("el cero de peso derecho es: " + valcalcero3);
                    regdatospd1.newLine();
                    regdatospd1.write("el span de peso derecho es: " + spanpd);
                    regdatospd1.newLine();
                    regdatospd1.flush();
                    for (t = 0; t < s; t++) {
                        regdatospd1.write(Datos3.get(t).toString());
                        regdatospd1.newLine();
                    }
                    regdatospd1.close();
                }

            } catch (IOException ex) {
                System.out.println("No se pudo escribir el dato de peso en el backup " + ex);
            }
        }
        auxpesod = CalcularMediana(Datos3);
        System.out.println("  ");
        System.out.println("  ");
        System.out.println("Tabla de Valores MIDIENDO PESOS ");
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
        System.out.println("valcalcero4 es: " + valcalcero4);
        System.out.println("*****************************");
        System.out.println("spanpd es: " + spanpd);
        System.out.println("spanpi es: " + spanpi);
        System.out.println("*****************************");
        System.out.println("Captura Peso Derecho es: " + auxpesod + " Mv");
        System.out.println("*****************************");
        System.out.println("peso medido en lado Derecho " + ((auxpesod - valcalcero3) * spanpd) + " Newton");
        System.out.println("--------------------");
        System.out.println("-------------------");
        System.out.println("  ");
        System.out.println("  ");

        if ((auxpesod - valcalcero3) < 0) {
            pesosd.add(0.0);
        } else {
            pesosd.add(auxpesod - valcalcero3);
        }

        bc = pesosd.size();
        // System.out.println("de nuevo el cero de peso derecho e izquierdo " + (valcalcero3 * spanpd) + " Newton "   + (valcalcero4 * spanpi) + " N");
        // System.out.println("peso sin calibrar " + ejemedido + " derecho e izquierdo: " + (auxpesod * spanpd) + " Newton " + (auxpesoi * spanpi) + " N");
        //System.out.println("peso del eje " + ejemedido + " derecho e izquierdo: " + (pesosd.get(bc - 1) * spanpd));
        Thread.sleep(100);
        Datos3.clear();
        Datos4.clear();
    }

    public void MedirPeso() throws InterruptedException {
        double auxpesod, auxpesoi;
        int bc;
        long s, r;
        LabelInfo.setText("MIDIENDO PESO...!");
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
        CapturarDatos("ceros");
        s = Datos3.size();
        r = Datos4.size();
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }

        if (enablebackup) {
            try {
                if (ejemedido == 1) {
                    regdatospd1.write("el cero de peso derecho es: " + valcalcero3);
                    regdatospd1.newLine();
                    regdatospd1.write("el span de peso derecho es: " + spanpd);
                    regdatospd1.newLine();
                    regdatospd1.flush();
                    for (t = 0; t < s; t++) {
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
                    for (t = 0; t < s; t++) {
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
                    for (t = 0; t < r; t++) {
                        regdatospi1.write(Datos4.get(t).toString());
                        regdatospi1.newLine();
                    }
                    System.out.println("Tamaño  " + r);
                    regdatospi1.close();
                } else if (ejemedido == 2) {
                    regdatospi2.write("el cero de peso izquierdo es: " + valcalcero4);
                    regdatospi2.newLine();
                    regdatospi2.write("el span de peso izquierdo es: " + spanpi);
                    regdatospi2.newLine();
                    regdatospi2.flush();
                    for (t = 0; t < r; t++) {
                        regdatospi2.write(Datos4.get(t).toString());
                        regdatospi2.newLine();
                    }
                    regdatospi2.close();
                }
            } catch (IOException ex) {
                System.out.println("No se pudo escribir el dato de peso en el backup " + ex);
            }
        }
        auxpesod = CalcularMediana(Datos3);
        auxpesoi = CalcularMediana(Datos4);
        System.out.println("  ");
        System.out.println("  ");
        System.out.println("Tabla de Valores MIDIENDO PESOS ");
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
        System.out.println("valcalcero4 es: " + valcalcero4);
        System.out.println("*****************************");
        System.out.println("spanpd es: " + spanpd);
        System.out.println("spanpi es: " + spanpi);
        System.out.println("*****************************");
        System.out.println("Captura Peso Derecho es: " + auxpesod + " Mv");
        System.out.println("Captura Peso Izquierdo es: " + auxpesoi + " Mv");
        System.out.println("*****************************");
        System.out.println("peso medido en lado Derecho " + ((auxpesod - valcalcero3) * spanpd) + " Newton");
        System.out.println("peso medido en lado Izquierdo " + ((auxpesoi - valcalcero4) * spanpi) + " Newton");
        System.out.println("--------------------");
        System.out.println("-------------------");
        System.out.println("  ");
        System.out.println("  ");

        if ((auxpesod - valcalcero3) < 0) {
            pesosd.add(0.0);
        } else {
            pesosd.add(auxpesod - valcalcero3);
        }
        if ((auxpesoi - valcalcero4) < 0) {
            pesosi.add(0.0);
        } else {
            pesosi.add(auxpesoi - valcalcero4);
        }

        bc = pesosd.size();
        // System.out.println("de nuevo el cero de peso derecho e izquierdo " + (valcalcero3 * spanpd) + " Newton "   + (valcalcero4 * spanpi) + " N");
        // System.out.println("peso sin calibrar " + ejemedido + " derecho e izquierdo: " + (auxpesod * spanpd) + " Newton " + (auxpesoi * spanpi) + " N");
        //System.out.println("peso del eje " + ejemedido + " derecho e izquierdo: " + (pesosd.get(bc - 1) * spanpd) + " Newton " + (pesosi.get(bc - 1) * spanpi) + " Newton");
        Thread.sleep(100);
        Datos3.clear();
        Datos4.clear();
    }

    /*
    public void MedirDesviacion() throws InterruptedException {
        System.out.println("entre metodo de desviacion");
        double auxdesv;
        int bc;
        LabelInfo.setText("Midiendo Desviación...");
        System.out.println(" voy a claer al puerto");
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

        Double valDesv = (((auxdesv - valcalcero7) * spand) / anchoplacadesv);
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
    }*/
    public void EsperaPeso() throws InterruptedException {
        double pesomedd, pesomedi;
        double calcPesoDer = 0;
        double calcPesoIzq = 0;
        Double ceroChanel3 = 0.0;
        boolean neceReinTrama = false;
        System.out.println("Entre Metodo esperaPeso ");
        LabelInfo.setText("ESPERANDO EJE " + ejemedido + " EN LA PLANCHA DE PESO..!");
        LabelAviso.setText("PLANCHA");
        timeraviso.start();
        comandoCEROS(0);
        Thread.sleep(500);
        while (true) {
            CapturarDatos("ceros");
            pesomedd = CalcularMediana(Datos3);
            pesomedi = CalcularMediana(Datos4);
            System.out.println("  ");
            System.out.println("  ");
            System.out.println("Tabla de Valores Leidos para el Comand Suspension ");
            System.out.println("----------------------");
            System.out.println("----------------------");
            System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
            System.out.println("valcalcero4 es: " + valcalcero4 + " Mv");
            System.out.println("*****************************");
            System.out.println("spanpd es: " + spanpd);
            System.out.println("spanpi es: " + spanpi);
            System.out.println("*****************************");
            System.out.println("Peso Medido Derecho: " + pesomedd + " Mv");
            System.out.println("Peso Medido Izquierdo: " + pesomedi + " Mv");
            System.out.println("*****************************");
            System.out.println("peso medido en lado Derecho " + ((pesomedd - valcalcero3) * spanpd) + " Newton");
            System.out.println("peso medido en lado Izquierdo " + ((pesomedi - valcalcero4) * spanpi) + " Newton");
            System.out.println("--------------------");
            System.out.println("-------------------");
            System.out.println("umbral peso: " + umbral_peso + " Newton.");
            System.out.println("-------------------");
            System.out.println("tot BYTES CANAL 3: " + Datos3.size() + " UND");
            System.out.println("tot BYTES CANAL 4: " + Datos4.size() + " UND");
            if (Datos3.size() > 21) {
                calcPesoDer = (pesomedd - valcalcero3) * spanpd;
            } else {
                calcPesoDer = 0;
                neceReinTrama = true;
                System.out.println(" reiniciando trama X derecho");
            }
            if (Datos4.size() > 21) {
                calcPesoIzq = (pesomedi - valcalcero4) * spanpi;
            } else {
                calcPesoIzq = 0;
                neceReinTrama = true;
                System.out.println(" reiniciando trama X Izquierdo");
            }
            if (calcPesoDer < 0 || calcPesoIzq < 0) {
                neceReinTrama = true;
                System.out.println(" reiniciando trama X valores en NEGATIVOS");
            }
            if (calcPesoDer == 0 || calcPesoIzq == 0) {
                neceReinTrama = true;
                System.out.println(" reiniciando trama X valores LISTAS VACIOS");
            }
            if (neceReinTrama == true) {
                comandoSTOP();
                try {
                    puerto.clearFlujoEnt();
                    puerto.setFlujoEnt(puerto);
                    Thread.sleep(1900);
                    comandoCEROS(0);
                    Thread.sleep(500);
                    neceReinTrama = false;
                } catch (InterruptedException ex) {
                }
            } else {
                puerto.clearFlujoEnt();
                puerto.setFlujoEnt(puerto);
                Thread.sleep(540);
            }
            Datos3.clear();
            Datos4.clear();
            if (calcPesoDer >= umbral_peso && calcPesoIzq >= umbral_peso) {
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
        }
        Datos3.clear();
        Datos4.clear();
        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
    }

    public void EsperaPeso(Integer Ejemedido, String Lado) throws InterruptedException {
        double pesomedd, pesomedi;
        double calcPesoDer = 0;
        double calcPesoIzq = 0;
        boolean neceReinTrama = false;
        System.out.println("Entre Metodo esperaPeso ");
        LabelInfo.setText((ejemedido == 1) ? "ESPERANDO EJE " + ejemedido + " EN LA PLANCHA DE PESO " + Lado.toUpperCase() : "ESPERANDO EJE " + ejemedido + " EN LA PLANCHA DE PESO..!");
        LabelAviso.setText("PLANCHA");
        timeraviso.start();
        comandoCEROS(0);
        Thread.sleep(500);
        while (true) {

            if (Lado.equalsIgnoreCase("DERECHO") && ejemedido == 1) {//AQUI SE TOMAN LOS DATOS DEL EJE 1 CUANDO SE MIDE POR EL LADO DERECHO
                CapturarDatos("ceros");
                pesomedd = CalcularMediana(Datos3);
                System.out.println("  ");
                System.out.println("  ");
                System.out.println("Tabla de Valores Leidos para el Comand Suspension eje 1 lado " + Lado);
                System.out.println("----------------------");
                System.out.println("----------------------");
                System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
                System.out.println("spanpd es: " + spanpd);
                System.out.println("Peso Medido Derecho: " + pesomedd + " Mv");
                System.out.println("*****************************");
                System.out.println("peso medido en lado Derecho " + ((pesomedd - valcalcero3) * spanpd) + " Newton");
                System.out.println("--------------------");
                System.out.println("-------------------");
                System.out.println("umbral peso: " + umbral_peso + " Newton.");
                System.out.println("-------------------");
                System.out.println("tot BYTES CANAL 3: " + Datos3.size() + " UND");
                if (Datos3.size() > 21) {
                    calcPesoDer = (pesomedd - valcalcero3) * spanpd;
                } else {
                    calcPesoDer = 0;
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X derecho");
                }
                if (calcPesoDer < 0) {
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X valores en NEGATIVOS");
                }
                if (calcPesoDer == 0) {
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X valores LISTAS VACIOS");
                }
                if (neceReinTrama == true) {
                    comandoSTOP();
                    try {
                        puerto.clearFlujoEnt();
                        puerto.setFlujoEnt(puerto);
                        Thread.sleep(1900);
                        comandoCEROS(0);
                        Thread.sleep(500);
                        neceReinTrama = false;
                    } catch (InterruptedException ex) {
                    }
                } else {
                    puerto.clearFlujoEnt();
                    puerto.setFlujoEnt(puerto);
                    Thread.sleep(540);
                }
                Datos3.clear();
                Datos4.clear();
                if (calcPesoDer >= umbral_peso) {
                    break;
                }

            } else if (Lado.equalsIgnoreCase("IZQUIERDO") && ejemedido == 1) {//AQUI SE TOMAN LOS DATOS DEL EJE 1 CUANDO SE MIDE POR EL LADO IZQUIERO
                CapturarDatos("ceros");
                pesomedi = CalcularMediana(Datos4);
                System.out.println("  ");
                System.out.println("  ");
                System.out.println("Tabla de valores leidos para el comando suspension eje 1 lado " + Lado);
                System.out.println("----------------------");
                System.out.println("----------------------");
                System.out.println("valcalcero4 es: " + valcalcero4 + " Mv");
                System.out.println("*****************************");
                System.out.println("spanpi es: " + spanpi);
                System.out.println("*****************************");
                System.out.println("Peso Medido Izquierdo: " + pesomedi + " Mv");
                System.out.println("*****************************");
                System.out.println("peso medido en lado Izquierdo " + ((pesomedi - valcalcero4) * spanpi) + " Newton");
                System.out.println("--------------------");
                System.out.println("-------------------");
                System.out.println("umbral peso: " + umbral_peso + " Newton.");
                System.out.println("-------------------");
                System.out.println("tot BYTES CANAL 4: " + Datos4.size() + " UND");

                if (Datos4.size() > 21) {
                    calcPesoIzq = (pesomedi - valcalcero4) * spanpi;
                } else {
                    calcPesoIzq = 0;
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X Izquierdo");
                }
                if (calcPesoIzq < 0) {
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X valores en NEGATIVOS");
                }
                if (calcPesoIzq == 0) {
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X valores LISTAS VACIOS");
                }
                if (neceReinTrama == true) {
                    comandoSTOP();
                    try {
                        puerto.clearFlujoEnt();
                        puerto.setFlujoEnt(puerto);
                        Thread.sleep(1900);
                        comandoCEROS(0);
                        Thread.sleep(500);
                        neceReinTrama = false;
                    } catch (InterruptedException ex) {
                    }
                } else {
                    puerto.clearFlujoEnt();
                    puerto.setFlujoEnt(puerto);
                    Thread.sleep(540);
                }
                Datos4.clear();
                if (calcPesoIzq >= umbral_peso) {
                    break;
                }
            } else if (ejemedido == 2) {//AQUI SE TOMAN LOS DATOS DEL EJE 2 
                CapturarDatos("ceros");
                pesomedd = CalcularMediana(Datos3);
                pesomedi = CalcularMediana(Datos4);
                System.out.println("  ");
                System.out.println("  ");
                System.out.println("Tabla de valores leidos para el comando suspension eje 2 ");
                System.out.println("----------------------");
                System.out.println("----------------------");
                System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
                System.out.println("valcalcero4 es: " + valcalcero4 + " Mv");
                System.out.println("*****************************");
                System.out.println("spanpd es: " + spanpd);
                System.out.println("spanpi es: " + spanpi);
                System.out.println("*****************************");
                System.out.println("Peso Medido Derecho: " + pesomedd + " Mv");
                System.out.println("Peso Medido Izquierdo: " + pesomedi + " Mv");
                System.out.println("*****************************");
                System.out.println("peso medido en lado Derecho " + ((pesomedd - valcalcero3) * spanpd) + " Newton");
                System.out.println("peso medido en lado Izquierdo " + ((pesomedi - valcalcero4) * spanpi) + " Newton");
                System.out.println("--------------------");
                System.out.println("-------------------");
                System.out.println("umbral peso: " + umbral_peso + " Newton.");
                System.out.println("-------------------");
                System.out.println("tot BYTES CANAL 3: " + Datos3.size() + " UND");
                System.out.println("tot BYTES CANAL 4: " + Datos4.size() + " UND");
                if (Datos3.size() > 21) {
                    calcPesoDer = (pesomedd - valcalcero3) * spanpd;
                } else {
                    calcPesoDer = 0;
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X derecho");
                }
                if (Datos4.size() > 21) {
                    calcPesoIzq = (pesomedi - valcalcero4) * spanpi;
                } else {
                    calcPesoIzq = 0;
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X Izquierdo");
                }
                if (calcPesoDer < 0 || calcPesoIzq < 0) {
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X valores en NEGATIVOS");
                }
                if (calcPesoDer == 0 || calcPesoIzq == 0) {
                    neceReinTrama = true;
                    System.out.println(" reiniciando trama X valores LISTAS VACIOS");
                }
                if (neceReinTrama == true) {
                    comandoSTOP();
                    try {
                        puerto.clearFlujoEnt();
                        puerto.setFlujoEnt(puerto);
                        Thread.sleep(1900);
                        comandoCEROS(0);
                        Thread.sleep(500);
                        neceReinTrama = false;
                    } catch (InterruptedException ex) {
                    }
                } else {
                    puerto.clearFlujoEnt();
                    puerto.setFlujoEnt(puerto);
                    Thread.sleep(540);
                }
                Datos3.clear();
                Datos4.clear();
                if (calcPesoDer >= umbral_peso && calcPesoIzq >= umbral_peso) {
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
            }
            Datos3.clear();
            Datos4.clear();
            LabelAviso.setText("");
            timeraviso.stop();
            LabelAviso.setBackground(PanelTitulos.getBackground());
        }
    }

    public void esperarPesoEje1() throws InterruptedException {
        double pesomedd;
        double calcPesoDer = 0;
        boolean neceReinTrama = false;
        System.out.println("Entre Metodo esperaPeso ");
        LabelInfo.setText("ESPERANDO EJE " + ejemedido + " EN LA PLANCHA DE PESO ");
        LabelAviso.setText("PLANCHA");
        timeraviso.start();
        comandoCEROS(0);
        Thread.sleep(500);
        while (true) {
            CapturarDatos("ceros");
            pesomedd = CalcularMediana(Datos3);
            System.out.println("  ");
            System.out.println("  ");
            System.out.println("Tabla de Valores Leidos para el Comand Suspension eje 1 lado " + Lado);
            System.out.println("----------------------");
            System.out.println("----------------------");
            System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
            System.out.println("spanpd es: " + spanpd);
            System.out.println("Peso Medido Derecho: " + pesomedd + " Mv");
            System.out.println("*****************************");
            System.out.println("peso medido en lado Derecho " + ((pesomedd - valcalcero3) * spanpd) + " Newton");
            System.out.println("--------------------");
            System.out.println("-------------------");
            System.out.println("umbral peso: " + umbral_peso + " Newton.");
            System.out.println("-------------------");
            System.out.println("tot BYTES CANAL 3: " + Datos3.size() + " UND");
            if (Datos3.size() > 21) {
                calcPesoDer = (pesomedd - valcalcero3) * spanpd;
            } else {
                calcPesoDer = 0;
                neceReinTrama = true;
                System.out.println(" reiniciando trama X derecho");
            }
            if (calcPesoDer < 0) {
                neceReinTrama = true;
                System.out.println(" reiniciando trama X valores en NEGATIVOS");
            }
            if (calcPesoDer == 0) {
                neceReinTrama = true;
                System.out.println(" reiniciando trama X valores LISTAS VACIOS");
            }
            if (neceReinTrama == true) {
                comandoSTOP();
                try {
                    puerto.clearFlujoEnt();
                    puerto.setFlujoEnt(puerto);
                    Thread.sleep(1900);
                    comandoCEROS(0);
                    Thread.sleep(500);
                    neceReinTrama = false;
                } catch (InterruptedException ex) {
                }
            } else {
                puerto.clearFlujoEnt();
                puerto.setFlujoEnt(puerto);
                Thread.sleep(540);
            }
            Datos3.clear();
            Datos4.clear();
            if (calcPesoDer >= umbral_peso) {
                break;
            }
        }
    }

    public void capturarDatosEje1() {
        try {
            len = puerto.in.read(buffer);
            if (len > 0) {
                for (i = 0; i < len; i++) {
                    data = buffer[i];
                    if (data == 72) {
                        ComandoRecibido[0] = data;
                        j = 1;
                    } else if (j == 1) {
                        if (data <= 15) {
                            ComandoRecibido[1] = (byteToInt(data) & 0x03);
                        } else {
                        }
                        j = 2;
                    } else if (j == 2) {
                        partealta = byteToInt(data);
                        j = 3;
                    } else if (j == 3) {
                        ComandoRecibido[2] = partealta * 256 + byteToInt(data);
                        if (ComandoRecibido[2] <= 4095) {
                            Datos1.add(ComandoRecibido[2]);
                        } else {
                        }
                        j = 4;
                    } else if (j == 4) {
                        partealta = byteToInt(data);
                        j = 5;
                    } else if (j == 5) {
                        ComandoRecibido[3] = partealta * 256 + byteToInt(data);
                        //Canal ignorado en prueba de motos
                        j = 6;
                    } else if (j == 6) {
                        partealta = byteToInt(data);
                        j = 7;
                    } else if (j == 7) {
                        ComandoRecibido[4] = partealta * 256 + byteToInt(data);
                        if (ComandoRecibido[4] <= 4095) {
                            Datos2.add(ComandoRecibido[4]);
                        } else {
                        }
                        j = 8;
                    } else if (j == 8) {
                        partealta = byteToInt(data);
                        j = 9;
                    } else if (j == 9) {
                        ComandoRecibido[5] = partealta * 256 + byteToInt(data);
                        //Canal ignorado en prueba de motos
                        j = 10;
                    } else if (j == 10) {
                        partealta = byteToInt(data);
                        j = 11;
                    } else if (j == 11) {
                        ComandoRecibido[6] = partealta * 256 + byteToInt(data);
                        if (ComandoRecibido[6] <= 4095) {
                            Datos3.add(ComandoRecibido[6]);
                        } else {
                        }
                        j = 12;
                    } else if (j == 12) {
                        partealta = byteToInt(data);
                        j = 13;
                    } else if (j == 13) {
                        ComandoRecibido[7] = partealta * 256 + byteToInt(data);
                        //Canal ignorado en prueba de motos
                        j = 14;
                    } else if (j == 14) {
                        ComandoRecibido[8] = data;
                        j = 0;
                        if (data == 'W') {
                            setCanal0((ComandoRecibido[1] & 0x01) > 0);//diferencia con liviano
                        }
                    }
                }
                //setCanal0((ComandoRecibido[1] & 0x01) >0);
                led1.setLedOn(isCanal0());
            } else {
                comandoFREN();
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void capturarDatoFrenometroEje1() {

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

    public void esperarRodillosMoto() throws InterruptedException {
        int conteoreg = 0;
        LabelInfo.setText("ESPERANDO EJE  " + ejemedido + " EN LOS RODILLO..!");
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
            if (isCanal0()) {
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
    }
    
    public void esperarRodillosMotoIzq() throws InterruptedException {
        int conteoreg = 0;
        LabelInfo.setText("ESPERANDO EJE  " + ejemedido + " EN LOS RODILLO..!");
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
            if (isCanal1()) {
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
    }

    public void EsperaRodillos() throws InterruptedException {
        int conteoreg = 0;
        LabelInfo.setText("ESPERANDO EJE  " + ejemedido + " EN LOS RODILLO..!");
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
            if (isCanal0() && isCanal1()) {
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

    public void MoverRodillosEje1(Boolean freAux) throws InterruptedException {
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
                    capturarDatosFrenometroEje1();

                    if (((!isCanal0())) && (this.pasoactual != 6)) {
                        this.LabelAviso.setBackground(this.PanelTitulos.getBackground());
                        if ((this.pasoactual == 4) || (this.pasoactual == 5)) {
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
                        System.out.println(new StringBuilder().append("velocidad derecha: ").toString());
                    }
                }
                switch (this.pasoactual) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        this.velminimad = ((CalcularMedia(this.Datos5) - this.valcalcero5) * this.umbral_velo);

                        System.out.println(new StringBuilder().append("velocidad minima derecha: ").toString());
                        this.timeraviso.start();
                        this.LabelAviso.setText("FRENE");
                        break;
                    case 4:
                        medirFuerzaFrenadoEje1(freAux);
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
    }
    
    public void MoverRodillosEje1Izq(Boolean freAux) throws InterruptedException {
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
                    capturarDatosFrenometroEje1();

                    if (((!isCanal1())) && (this.pasoactual != 6)) {
                        this.LabelAviso.setBackground(this.PanelTitulos.getBackground());
                        if ((this.pasoactual == 4) || (this.pasoactual == 5)) {
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
                        System.out.println(new StringBuilder().append("velocidad derecha: ").toString());
                    }
                }
                switch (this.pasoactual) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        this.velminimad = ((CalcularMedia(this.Datos5) - this.valcalcero5) * this.umbral_velo);

                        System.out.println(new StringBuilder().append("velocidad minima derecha: ").toString());
                        this.timeraviso.start();
                        this.LabelAviso.setText("FRENE");
                        break;
                    case 4:
                        medirFuerzaFrenadoEje1(freAux);
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
    }

    public void MoverRodillos(Boolean freAux) throws InterruptedException {
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
                        if ((this.pasoactual == 4) || (this.pasoactual == 5)) {
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
                        break;
                    case 4:
                        MedirFuerzaFrenado(freAux);
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
    }

    /*  public double CalcularDesviacion(double media, List<Double> Datosfil) {
        double max = 0, min = 4095, c;
        long s;
        s = Datosfil.size();
        //System.out.println("El numero de datos es: "+s);
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
    }*/
    public double CalcularMedia(List<Integer> Datos) {
        long b, s;
        double c;
        s = Datos.size();
        System.out.println("El numero de datos es: " + s);
        b = 0;
        for (t = 0; t < s; t++) {
            b += Datos.get(t);
        }
        c = (double) b / s;
        //System.out.println(c);
        return c;
    }

    public double CalcularMediana(List<Integer> Datos) {
        int s, r, auxMediana;
        double c;
        s = Datos.size();
        //System.out.println("s: "+s);
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
        } else {
            c = (double) Datos.get(s / 2);
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

    public void capturarDatosFrenometroEje1() {
        try {
            len = puerto.in.read(buffer);
            System.out.println("largo de len: " + len);
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
                            Datos3.add(ComandoRecibido[4]);
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
                            Datos4.add(ComandoRecibido[5]);
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
                        }
                    }
                    // </editor-fold>
                }
                //setCanal0((ComandoRecibido[1] & 0x01) >0);
                //setCanal1((ComandoRecibido[1] & 0x02) >0);
                led1.setLedOn(isCanal0());
            } else {
                comandoFREN();
            }

        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR CAPTURANDO LOS DATOS " + ex);
        }
    }
    
    public void capturarDatosFrenometroEje1Izq() {
        try {
            len = puerto.in.read(buffer);
            System.out.println("largo de len: " + len);
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
                            Datos3.add(ComandoRecibido[4]);
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
                            Datos4.add(ComandoRecibido[5]);
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
                            setCanal1((ComandoRecibido[1] & 0x02) > 0);
                        }
                    }
                    // </editor-fold>
                }
                //setCanal0((ComandoRecibido[1] & 0x01) >0);
                //setCanal1((ComandoRecibido[1] & 0x02) >0);
                led2.setLedOn(isCanal1());
            } else {
                comandoFREN();
            }

        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            System.out.println("ERROR CAPTURANDO LOS DATOS " + ex);
        }
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
            System.out.println("largo de len: " + len);
            switch (opc) {
                // <editor-fold desc="Obtener tramas de comando CEROS">
                case 1:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            data = buffer[i];

                            if (data == 72) {
                                ComandoRecibido[0] = data;
                                j = 1;

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
                                    Datos3.add(ComandoRecibido[3]);
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
                                    Datos4.add(ComandoRecibido[4]);
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
                                    Datos3.add(ComandoRecibido[4]);
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
                                    Datos4.add(ComandoRecibido[5]);
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
                                        Datos3.add(ComandoRecibido[1]);
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
                                        Datos4.add(ComandoRecibido[2]);
                                        j = 5;
                                    } else {
                                    }
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
                    } else {
                        comandoSUSP();
                    }
                    break;
                // </editor-fold>
                // <editor-fold desc="Obtener tramas de comando DESV">
                case 4:
                    if (len > 0) {
                        for (i = 0; i < len; i++) {
                            data = buffer[i];
                            // <editor-fold desc="Se valida la cabecera de la trama">
                            if (data == 72) {
                                ComandoRecibido[0] = data;
                                j = 1;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte alta de la medida de desviacion">
                            } else if (j == 1) {
                                partealta = byteToInt(data);
                                j = 2;
                                // </editor-fold>
                                // <editor-fold desc="Se recibe la parte baja y se valida la medida de desviacion">
                            } else if (j == 2) {
                                ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                                if (ComandoRecibido[1] <= 4095) {
                                    Datos7.add(ComandoRecibido[1]);
                                    j = 3;
                                } else {
                                }
                                // </editor-fold>
                                // <editor-fold desc="Se valida la cola de la trama">
                            } else if (j == 3) {
                                ComandoRecibido[2] = data;
                                j = 0;
                                if (data == 'W') {
                                }
                            }
                            // </editor-fold>
                        }
                    } else {
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
                                    Datos3.add(ComandoRecibido[1]);
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
                                    Datos4.add(ComandoRecibido[2]);
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
            System.out.println("ERROR CAPTURANDO LOS DATOS " + ex);
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
                repetirPrueba = desviacionDAO.persist(desviacion, idPruebadesv, idUsuario, aplicTrans, this.ipEquipo, tipoPista, tipoVehiculo, this.Placa);
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

        for (int k = 0; k < fuerzasvd.size(); k++) {
            System.out.println(" Valor de fuerza derecha " + fuerzasvd.get(k) + " en la posicion " + k);
            suspension.setFuerzaDerecha(fuerzasvd.get(k));
            System.out.println(" Valor de fuerza izquierda " + fuerzasvi.get(k) + " en la posicion " + k);
            suspension.setFuerzaIzquierda(fuerzasvi.get(k));
        }

        for (int k = 0; k < pesosd.size(); k++) {
            System.out.println(" Valor de peso derecho " + pesosd.get(k) * spanpd + " en la posicion " + k);
            suspension.setPesoDerecho(pesosd.get(k) * spanpd);

            System.out.println(" Valor de peso izquierdo " + pesosi.get(k) * spanpd + " en la posicion " + k);
            suspension.setPesoIzquierdo(pesosi.get(k) * spanpi);
        }

        System.out.println("voy a registrar suspension DLGintegradoLiviano ");

        try {
            PruebaDefaultDAO.escrTrans = "@";
            try {
                repetirPrueba = suspensionDAO.persist(suspension, idPruebasusp, idUsuario, aplicTrans, this.ipEquipo, tipoPista, tipoVehiculo, this.Placa);
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
        FrenoMotoCarro frenos = new FrenoMotoCarro(factor_desq);
        PruebaDefaultDAO frenosDAO = new PruebaDefaultDAO();
        pesosi.add(0, 0.0);
        System.out.println("-----------------------" + pesosi);
        System.out.println("-------------------------" + fuerzasfi);
        fuerzasfi.add(0, 0.0);
        //uñtimo cambio
        //FrenoMotoCarro.desequilibrio.add(0,0.0);

        if (FrenoInst.equalsIgnoreCase("True") && isEnsenianza()) {//SI EL VEHICULO ES DE ENSEÑANZA Y LA VARIBLE ESTA ACTIVA HACE DOBLE FRENADO
            for (int k = 1; k < fuerzasfd.size(); k += 2) {
                frenos.setFuerzaDerechaEnseñanza(Math.floor(Math.round(fuerzasfd.get(k) * spanfd)));
                frenos.setFuerzaIzquierdaEnseñanza(Math.floor(Math.round(fuerzasfi.get(k) * spanfi)));
            }
            facEnseñanza = 2;
        }
        for (int k = 0; k < fuerzasfdAux.size(); k++) {
            frenos.setFuerzaDerechaAux(Math.floor(Math.round(fuerzasfdAux.get(k) * spanfd)));
            //frenos.setFuerzaIzquierdaAux(Math.floor(Math.round(fuerzasfiAux.get(k) * spanfi)));
        }
        /*for añadido 4/08/2022*/
        for (int k = 0; k < fuerzasfdAux.size(); k++) {
            //frenos.setFuerzaDerechaAux(Math.floor(Math.round(fuerzasfdAux.get(k) * spanfd)));
            frenos.setFuerzaIzquierdaAux(Math.floor(Math.round(fuerzasfiAux.get(k) * spanfi)));
        }/*fin de for*/

        for (int k = 0; k < (fuerzasfd.size() / facEnseñanza); k++) {
            frenos.setFuerzaDerecha(Math.floor(Math.round(fuerzasfd.get(k * facEnseñanza) * spanfd)));
            //frenos.setFuerzaIzquierda(Math.floor(Math.round(fuerzasfi.get(k * facEnseñanza) * spanfi)));
        }
        /*for añadido 3/08/2022*/
        System.out.println("fuerza freno derecha:" + fuerzasfd.size());
        System.out.println("fuerza freno izaquierda:" + fuerzasfi.size());

        for (int k = 0; k < (fuerzasfi.size() / facEnseñanza); k++) {
            frenos.setFuerzaIzquierda(Math.floor(Math.round(fuerzasfi.get(k * facEnseñanza) * spanfi)));
        }/*fin de for*/

        for (int k = 0; k < pesosd.size(); k++) {
            frenos.setPesoDerecho(Math.floor(Math.round(pesosd.get(k) * spanpd)));
            //frenos.setPesoIzquierdo(Math.floor(Math.round(pesosi.get(k) * spanpi)));
        }
        /*for añadido 3/08/2022*/
        for (int k = 0; k < pesosi.size(); k++) {
            frenos.setPesoIzquierdo(Math.floor(Math.round(pesosi.get(k) * spanpi)));
        }/*fin de for*/
        System.out.println("fuerza freno derecha:" + pesosd.size());
        System.out.println("fuerza freno izaquierda:" + pesosi.size());
        try {
            System.out.println("toma el marcado de confirmacion FRENADOS LIVIANO ");
            PruebaDefaultDAO.escrTrans = "@";

            try {
                repetirPrueba = frenosDAO.persist(frenos, idPruebafren, idUsuario, aplicTrans, this.ipEquipo, tipoPista, tipoVehiculo, Placa);
            } catch (ClassNotFoundException ex) {
                System.out.println("message error" + ex.getMessage());
            }
            if (repetirPrueba == false) {
                tramaAuditoria = "{\"eficaciaTotal\":\""
                        .concat(String.valueOf(FrenoMotoCarro.eficacia))
                        .concat("\",").concat("\"eficaciaAuxiliar\":\"")
                        .concat(String.valueOf(FrenoMotoCarro.eficaciaFrenoMano))
                        .concat("\",");
                for (int k = 0; k < frenos.getPesoDerecho().size(); k++) {
                    tramaAuditoria = tramaAuditoria
                            .concat("\"fuerzaEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(frenos.getFuerzaIzquierda().get(k)))
                            .concat("\",")
                            .concat("\"pesoEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(frenos.getPesoIzquierdo().get(k)))
                            .concat("\",")
                            .concat("\"fuerzaEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(frenos.getFuerzaDerecha().get(k)))
                            .concat("\",")
                            .concat("\"pesoEje")
                            .concat(String.valueOf(k + 1))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(frenos.getPesoDerecho().get(k)))
                            .concat("\",")
                            .concat("\"eje")
                            .concat(String.valueOf(k + 1))
                            .concat("Desequilibrio\":\"")
                            .concat(String.valueOf(/*FrenoMotoCarro.desequilibrio.get(k))*/""))
                            .concat("\",");
                }
                for (int k = frenos.getPesoDerecho().size() + 1; k < 6; k++) {
                    tramaAuditoria = tramaAuditoria
                            .concat("\"fuerzaEje")
                            .concat(String.valueOf(k))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",")
                            .concat("\"pesoEje")
                            .concat(String.valueOf(k))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",")
                            .concat("\"fuerzaEje")
                            .concat(String.valueOf(k))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",")
                            .concat("\"pesoEje")
                            .concat(String.valueOf(k))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",")
                            .concat("\"eje")
                            .concat(String.valueOf(k))
                            .concat("Desequilibrio\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",");
                }
                tramaAuditoria = tramaAuditoria
                        .concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"")
                        .concat(String.valueOf(idPruebafren))
                        .concat("\"}");
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

        LabelInfo.setFont(new java.awt.Font("Andalus", 0, 36)); // NOI18N
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
        LabelPrueba.setText("PRUEBA PARA MOTOCARRO");
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
            System.out.println("Error al cargar la variable :activarFlagFrenos del properties" + e.getMessage());

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
            LabelEje.setText("EJE " + ejemedido);
            if (tiempomensajes == 0) {
                JOptionPane.showMessageDialog(this, "POR FAVOR ASEGURESE DE QUE LAS PLANCHAS ESTEN LIBRES", "SART 1.7.3",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                if (enableswfren == true) {
                    frm = new FrmFrenadoAuxLiv(this, true);
                    frm.setModal(true);
                    frm.setLocationRelativeTo(null);
                    frm.setVisible(true);
                    System.out.println(" VALOR DE FRENO DE MANO " + frm.jCheckBox1.isSelected() + " y el otro " + frm.jCheckBox2.isSelected());
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
            RegistrarMedidasFrenos();
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

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isEnableswfren() {
        return enableswfren;
    }

    public void setEnableswfren(boolean enableswfren) {
        this.enableswfren = enableswfren;
    }

    public boolean isEnablehwfren() {
        return enablehwfren;
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
                DlgIntegradoMotoCarro dialog = new DlgIntegradoMotoCarro(new javax.swing.JFrame(), true);
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
            CalibrarCeros(
                    isEnablehwfren() && isEnableswfren(),//valor booleano a
                    isEnablehwfren() && isEnableswfren(),//valor booleano b
                    isEnablehwfren() && isEnableswfren(),//valor booleano c
                    isEnablehwfren() && isEnableswfren(),//valor booleano d
                    isEnablehwfren() && isEnableswfren(),//valor booleano e
                    isEnablehwfren() && isEnableswfren(),//valor booleano f
                    Lado
            );

            LabelInfo.setText("CALIBRANDO EL SISTEMA  A CEROS ..!");
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

    class Principal extends Thread {

        @Override
        public void run() {
            try {
                try//LEE VARIABLE FrenoEnse DEL ARCHIVO propiedades.properties PARA ACTIVAR O DESACTIVAR EL FRENO DEL INSTRUCTOR
                {
                    FrenoInst = UtilPropiedades.cargarPropiedad("FrenoEnse", "propiedades.properties");
                    FrenoInst = (FrenoInst == null) ? "True" : FrenoInst;
                } catch (IOException ex) {
                    FrenoInst = "True";
                }
                ejemedido = 1;
                LabelEje.setText("EJE: " + ejemedido);
                while (true) {
                    System.out.println("prueba------------------");
                    if (ejemedido > 1) {
                        Mensajes.messageWarningTime("Por Favor Retire el vehiculo de las maquinas, para iniciar con el eje " + DlgIntegradoMotoCarro.this.ejemedido, 6);
                    }
                    if (enablehwfren && enableswfren) {
                        LabelPrueba.setText("PRUEBA FRENOS MOTOCARRO ");
                        lblCtxPrueba.setText("USER: " + DlgIntegradoMotoCarro.NombreUsr + "; PLACA: " + DlgIntegradoMotoCarro.Placa);
                    }
                    if (ejemedido == 1) {
                        esperarPesoEje1();
                        medirPesoEje1();
                    } else {
                        EsperaPeso();
                        MedirPeso();
                    }

                    if (enablehwfren && enableswfren) {
                        if (ejemedido == 1) {
                            if (frm.jCheckBox1.isSelected()) {
                                aplicFreAux = true;
                            }
                        }
                        if (ejemedido == 2) {
                            if (frm.jCheckBox2.isSelected()) {
                                aplicFreAux = true;
                            }
                        }
                        LabelPrueba.setText("PRUEBA FRENOS MOTOCARRO ");
                        lblCtxPrueba.setText("USER: " + DlgIntegradoMotoCarro.NombreUsr + "; PLACA: " + DlgIntegradoMotoCarro.Placa);
                        imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoOn.png"));
                        imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoOf.png"));

                        if (ejemedido == 1) {
                            if (Lado.equalsIgnoreCase("derecho")) {
                                esperarRodillosMoto();
                                MoverRodillosEje1(false);
                            }else if(Lado.equalsIgnoreCase("izquierdo")){
                                esperarRodillosMotoIzq();
                                MoverRodillosEje1Izq(false);
                            }
                        } else {
                            EsperaRodillos();
                            MoverRodillos(false);
                        }

//JFM               Freno del instructor                                     
                        if (FrenoInst.equalsIgnoreCase("True") && isEnsenianza()) //SI EL VEHICULO ES DE ENSEÑANZA Y LA VARIBLE ESTA ACTIVA HACE DOBLE FRENADO
                        {
                            LabelEje.setText("Eje " + ejemedido + " (ENSEÑANZA)");
                            LabelInfo.setText("Ahora la prueba del freno del instructor");
                            Thread.sleep(2000);
                            EsperaRodillos();
                            MoverRodillos(false);
                            LabelEje.setText("Eje " + ejemedido);
                        }
//JFM                                  
                        if (aplicFreAux == true) {
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
                        if (ejemedido == numeroejes) {
                            if (!BotonFinalizar.isEnabled()) {
                                comandoSTOPAnalogo();
                                BotonFinalizar.setEnabled(true);
                                timerfinalizar.setRepeats(false);
                                timerfinalizar.start();
                            }
                        }
                    }

                    if (ejemedido == numeroejes) {
                        Thread.sleep(100);
                        if (!BotonFinalizar.isEnabled()) {
                            comandoSTOPAnalogo();
                            BotonFinalizar.setEnabled(true);
                            timerfinalizar.setRepeats(false);
                            timerfinalizar.start();
                        }
                        break;
                    } else {
                        ejemedido++;
                        aplicFreAux = false;
                        LabelEje.setText("EJE: " + ejemedido);
                    }
                }// END WHILE
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }

        }
    }

    public class FrmFrenadoAuxLiv extends javax.swing.JDialog {

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
        public FrmFrenadoAuxLiv(javax.swing.JDialog frame, boolean modal) {
            super(frame, modal);
            initComponents();
            setResizable(false);
            this.frame = frame;

        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
        private void initComponents() {

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
            jLabel2.setText("<html><center>APLICACION <br/>FRENO AUX.</center></html>");

            jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/rueda.png"))); // NOI18N

            jPanel7.setToolTipText("");
            jPanel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

            jLabel4.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
            jLabel4.setText("Eje1:");

            jLabel10.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
            jLabel10.setText("Eje2:");

            jCheckBox2.setSelected(true);

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
        /**
         * @param args the command line arguments
         */
        // Variables declaration - do not modify                     
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
    }

}
