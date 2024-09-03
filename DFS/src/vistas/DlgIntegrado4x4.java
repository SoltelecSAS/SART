/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * esperdialoginteglivianos.java
 *
 * Created on 19/12/2011, 04:22:07 PM
 */
package vistas;

import com.soltelec.loginadministrador.UtilLogin;
import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import modelo.Desviacion;
import modelo.Frenos;
import modelo.Suspension;
import org.jdesktop.swingx.JXLoginPane;
import static vistas.DlgIntegradoLiviano.byteToInt;
import static vistas.DlgIntegradoPesado.byteToInt;

/**
 *
 * @author Gerencia TIC
 */
public class DlgIntegrado4x4 extends javax.swing.JDialog implements ActionListener {

    private int idPrueba;
    private double factorescala;
    //private String CalificacionPrueba;
    private double permisiblepruebadesv1;   //Valor permisible para la desviación en el primer eje
    private double permisiblepruebadesv2;  //Valor permisible para la desviación en los demas ejes
    private double permisiblepruebasusp;   //Valor permisible para la suspensión
    private double permisiblepruebafren1;  //Valor permisible para la eficacia total de frenos
    private double permisiblepruebafren2;  //Valor permisible para el freno de mano
    private double permisiblepruebafren3;  //Valor permisible para el desequilibrio tipo A
    private double permisiblepruebafren4;  //Valor permisible para el desequilibrio tipo B
    private String URLServidor;
    private boolean frenmano = false;
    private FrmFrenadoAuxLiv frm;
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private boolean enablereg = false; //Habilitación de registro de medidas en el servidor
    private boolean enablehwdesv, enablehwsusp, enablehwfren;  //Habilitacion por parte
    //del banco de las pruebas a realizar
    private boolean enableswdesv, enableswsusp, enableswfren;  //Habilitacion por parte
    //del servidor de las pruebas a realizar
    private boolean pista_mixta;    //Selección de pista mixta o liviana
    private boolean error_config;   //Indicador de error en el archivo de configuracion
    private boolean enablebackup = false; //Habilitación de generar backup del registro de medidas
    private BufferedWriter regdatospd1;
    private BufferedWriter regdatospi1;
    private BufferedWriter regdatospd2;
    private BufferedWriter regdatospi2;
    private BufferedWriter regdatosffd1;
    private BufferedWriter regdatosffi1;
    private BufferedWriter regdatosffd2;
    private BufferedWriter regdatosffi2;
    private BufferedWriter regdatosffda;
    private BufferedWriter regdatosffia;
    //para no apertura del dialogo
    private byte[] buffer = new byte[6000];
    private byte data;
    private int j = 0, i = 0, len = -1, partealta, numtimer1 = 0, t = 0;
    private final int[] ComandoRecibido = new int[9];
    private BufferedReader config;
    private boolean tecladoactivado = false;
    private final List<Integer> Datos1 = new ArrayList<>(); //Datos correspondientes a la fuerza de frenado derecha
    private final List<Integer> Datos2 = new ArrayList<>(); //Datos correspondientes a la fuerza de frenado izquierda
    private final List<Integer> Datos3 = new ArrayList<>(); //Datos correspondientes al peso derecho
    private final List<Integer> Datos4 = new ArrayList<>(); //Datos correspondientes al peso izquierdo
    private final List<Integer> Datos5 = new ArrayList<>(); //Datos correspondientes a la velocidad del eje derecho
    private final List<Integer> Datos6 = new ArrayList<>(); //Datos correspondientes a la velocidad del eje izquierdo
    private final List<Integer> Datos7 = new ArrayList<>(); //Datos correspondientes a la desviacion

    private List<Double> Datosfil1 = new ArrayList<>(); //Datos correspondientes al peso derecho filtrado
    private List<Double> Datosfil2 = new ArrayList<>(); //Datos correspondientes al peso izquierdo filtrado

    private double valcalcero1 = 0, valcalcero2 = 0, valcalcero3 = 0, valcalcero4 = 0, valcalcero5 = 0, valcalcero6 = 0, valcalcero7 = 0;
    //variables correspondientes a la calibracion de cero de los sensores de peso derecho e izquierdo,
    //fuerza de frenado derecha e izquierda, velocidad del eje derecho e izquierdo
    //y desviacion repectivamente
    private byte pasomeddesv = 0, numpasos, pasoactual = 0;
    private float ajusteDsq;
     private String tipoPista;
      private String tipoVehiculo;
    public static String Placa;
    public static String NombreUsr;

    private byte salidamotorderecho, salidamotorizquierdo;
    private final List<Double> pesosd = new ArrayList<>(); //Valores de peso derecho de cada eje
    private final List<Double> pesosi = new ArrayList<>(); //Valores de peso izquierdo de cada eje
    private final List<Double> fuerzasvd = new ArrayList<>(); //Valores de las fuerzas verticales derechas de cada eje
    private final List<Double> fuerzasvi = new ArrayList<>(); //Valores de las fuerzas verticales izquierdas de cada eje
    private final List<Double> fuerzasfd = new ArrayList<>(); //Valores de las fuerzas de frenado derechas de cada eje
    private final List<Double> fuerzasfi = new ArrayList<>(); //Valores de las fuerzas de frenado izquierdas de cada eje
    private final List<Double> desviaciones = new ArrayList<>(); //Valores de desviacion de cada eje
    private double spanfd, spanfi, spanpd, spanpi, spanvd, spanvi, spand;  //valores de span de los canales obtenidos de la calibración
    private boolean canal0 = false, canal1 = false;
    private int tiempo_paso = 1;
    private byte salidaalta, salidabaja;
    private double anchoplacadesv;
    private int aplcSenDesv;
    private int numeroejes = 2, ejemedido = 1, tiempomensajes, numtimerautomatico2 = 0;
    //private boolean ladomedido=true; //true=derecho false=izquierdo
    private double velminimad, velminimai, umbral_velo, umbral_peso;
    private Principal hiloPrincipal = new Principal();
    private boolean ensenianza = false;
    private final double[] filtro = {0.0091, 0.0131, 0.0245, 0.0416, 0.0619, 0.0821, 0.0993, 0.1108,
        0.1149, 0.1108, 0.0993, 0.0821, 0.0619, 0.0416, 0.0245, 0.0131, 0.0091};
    private String motivoCancelacion;
    private int idPruebadesv = 4, idPruebasusp = 6, idPruebafren = 5;
    private int idUsuario = 1;
    ImageIcon imageOn = null;
    ImageIcon imageOff = null;
    boolean aplicFreAux = false;
    private final List<Double> fuerzasfdAux = new ArrayList<>(); //Valores de las fuerzas de frenado aux derechas de cada eje
    private final List<Double> fuerzasfiAux = new ArrayList<>(); //Valores de las fuerzas de frenado aux  izquierdas de cada
    public static String tramaAuditoria = "";
    public static String tramaAuditoriaSusp = "";
    public static String tramaAuditoriaDesv = "";
    private boolean repetirPrueba;
    private int aplicTrans = 1;
    private int resolMin = 1;
    private int resolMax = 1;
    public static String escrTrans = "";
    private String ipEquipo;
    private int teporizadorInercia;

    private DlgIntegrado4x4(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize(this.getToolkit().getScreenSize());
        this.setTitle("SART 1.7.3 MOD D.F.S. PARA 4X4");
        configuracion();
        timerempezar.setRepeats(false);
        if (!error_config) {
            timerempezar.start();
        } else {
            System.out.println("Salida de la prueba por error en el archivo de configuración");
        }
    }

    public DlgIntegrado4x4(java.awt.Frame parent, int idPruebadesv, int idPruebasusp, int idPruebafren, int idUsuario, int idHojaPrueba, int aplicTrans, String ipEquipo,String tipoVehiculo,String Placa,String NombreUsr) {
        this(parent, true);
        this.idPruebadesv = idPruebadesv;
        this.idPruebasusp = idPruebasusp;
        this.idPruebafren = idPruebafren;
        this.idUsuario = idUsuario;
        enableswdesv = idPruebadesv > 0;
        enableswfren = idPruebafren > 0;
        enableswsusp = idPruebasusp > 0;
        this.aplicTrans = aplicTrans;
        this.ipEquipo = ipEquipo;
         this.tipoVehiculo=tipoVehiculo;
        this.Placa=Placa;
        this.NombreUsr =NombreUsr;
    }

    private void configuracion() {
        setError_config(false);
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            String line;

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
                enablehwdesv = (line.substring(line.indexOf(" ") + 1, line.length())).equals("si");
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo desviación. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("suspension:")) {
                enablehwsusp = (line.substring(line.indexOf(" ") + 1, line.length())).equals("si");
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo suspensión. Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            if ((line = config.readLine()).startsWith("frenos:")) {
                enablehwfren = (line.substring(line.indexOf(" ") + 1, line.length())).equals("si");
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
//</editor-fold>
//<editor-fold desc="Lectura de la configuración de la prueba de desviacion">            
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
                System.out.println(" RESOL MIN "+resolMin);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo resol_min para Desv.. Por Favor Llame Soporte Tecnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
              if ((line = config.readLine()).startsWith("resol_max:")) {
                  resolMax = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
                  System.out.println(" RESOL MIN "+resolMax);
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo resol_max para Desv.. Por Favor Llame Soporte Tecnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
//</editor-fold>
//<editor-fold desc="Lectura de la configuración de la prueba de suspensión">
            while (!config.readLine().startsWith("[SUSPENSION]")) {
            }
            if ((line = config.readLine()).startsWith("umbral_peso_susp")) {
                umbral_peso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
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
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo temporizador_inercia. Llame a servicio técnico  " +line,
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
            if ((line = config.readLine()).startsWith("valida_tipo:"))              
            if ((line = config.readLine()).startsWith("tipo_pista:")) {
                tipoPista = line.substring(line.indexOf(" ") + 1, line.length());
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo (tipo_pista). Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
//</editor-fold>
//<editor-fold desc="Lectura de la configuración de la prueba de frenos">
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
                ajusteDsq = Float.parseFloat(line.substring(line.indexOf(" ") + 1, line.length()));
            } else {
                JOptionPane.showMessageDialog(null, "Falla en el archivo de configuración campo (ajuste_dsq). Llame a servicio técnico",
                        "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                setError_config(true);
            }
            while (!config.readLine().startsWith("4x4:")) {
            }
            if ((line = config.readLine()).startsWith("pasos:")) {
                numpasos = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.length()));
            }

            if (config.markSupported()) {
                config.mark(1000);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("error en el archivo de configuración " + ex);
            setError_config(true);
        } catch (IOException ex) {
            System.out.println("no se pudo abrir " + ex);
            setError_config(true);
        }
        Properties archivop = new Properties();
        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (IOException e) {
            System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o está protegido");
        }
//<editor-fold desc="Lectura de los span para las mediciones en el banco y de la URL del servidor">        
        spanfd = Double.parseDouble(archivop.getProperty("spanfd"));
        spanfi = Double.parseDouble(archivop.getProperty("spanfi"));
        spanpd = Double.parseDouble(archivop.getProperty("spanpd"));
        spanpi = Double.parseDouble(archivop.getProperty("spanpi"));
        spanvd = Double.parseDouble(archivop.getProperty("spanvd"));
        spanvi = Double.parseDouble(archivop.getProperty("spanvi"));
        spand = Double.parseDouble(archivop.getProperty("spand"));
        URLServidor = archivop.getProperty("URL");
//</editor-fold>         
        System.out.println("spanfd " + spanfd + " spanfi " + spanfi + " spanpd " + spanpd + " spanpi " + spanpi
                + " spanvd " + spanvd + " spanvi " + spanvi + " spand " + spand);
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

        if (isEnablehwsusp() || isEnablehwfren()) {
//<editor-fold desc="Creación de los archivos backup para los datos de frenos"> 
            try {
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
                regdatosffia.flush();
            } catch (IOException ex) {
                System.out.println("no se pudo crear el archivo de datos" + ex);
                setError_config(true);
            }
        }

        this.addKeyListener(
                new java.awt.event.KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e
                    ) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e
                    ) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e
                    ) {
                        if (tecladoactivado) {
                            if (e.getKeyChar() == '2') {

                            }
                            tecladoactivado = false;
                        }
                    }
                }
        );
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
            Logger.getLogger(DlgFrenoMoto.class
                    .getName()).log(Level.SEVERE, null, ex);
            System.out.println(
                    "Se perdio de lineas");
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
        Thread.sleep(300);
        try {
            puerto.out.write('H');
            puerto.out.write('G');
            puerto.out.write(a);
            puerto.out.write(b);
            puerto.out.write('W');
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
        comandoCONEX();
        try {
            Thread.currentThread().sleep(200);
            puerto.in.read(buffer);

        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(DlgFrenoMoto.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        return respuesta.startsWith("HDAQ1W");
    }

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
            valcalcero7 = CalcularMedia(Datos7);
            System.out.println("cero desviacion: " + (valcalcero7 * spand) + " mm");
        }
        Datos7.clear();
    }

    public void MedirFuerzaFrenado(String lado, Boolean freAux) {
        double auxfuerzad, auxfuerzai;
        int bc;
        //long s;
        switch (lado) {
            case "derecho":
                armonizar(Datos1, valcalcero1);
                Datosfil1 = filtrar(Datos1);
                auxfuerzad = CalcularMaximo(Datosfil1);
                if ((auxfuerzad - valcalcero1) < 0) {
                    if (freAux == false) {
                        this.fuerzasfd.add(Double.valueOf(0.0));
                    } else {
                        fuerzasfdAux.add(0.0);
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
                    System.out.println("Cero Fuerza Derecha: " + (valcalcero1 * spanfd) + " Newton ");
                    System.out.println("Fuerza sin calibrar " + ejemedido + " derecha: " + (auxfuerzad * spanfd) + " Newton ");
                    System.out.println("Fuerza del eje " + ejemedido + " derecha: " + (fuerzasfd.get(bc - 1) * spanfd) + " Newton ");
                } else {
                    bc = fuerzasfdAux.size();
                    System.out.println("Fuerza AUX  del eje " + ejemedido + " derecha: " + (fuerzasfdAux.get(bc - 1) * spanfd) + " Newton ");
                }

                break;

            case "izquierdo":
                armonizar(Datos2, valcalcero2);
                Datosfil2 = filtrar(Datos2);
                auxfuerzai = CalcularMaximo(Datosfil2);
                if (auxfuerzai - this.valcalcero2 < 0.0) {
                    if (freAux == false) {
                        this.fuerzasfi.add(Double.valueOf(0.0));
                    } else {

                        fuerzasfiAux.add(0.0);
                    }
                } else {
                    if (freAux == false) {
                        this.fuerzasfi.add(Double.valueOf(auxfuerzai - this.valcalcero2));
                    } else {
                        fuerzasfiAux.add(auxfuerzai - valcalcero2);
                    }
                }
                if (freAux == false) {
                    bc = fuerzasfi.size();
                    System.out.println("Cero Fuerza izquierda: " + (valcalcero2 * spanfi) + " Newton");
                    System.out.println("Fuerza sin calibrar " + ejemedido + " izquierda: " + (auxfuerzai * spanfi) + " Newton");
                    System.out.println("Fuerza del eje " + ejemedido + " izquierda: " + (fuerzasfi.get(bc - 1) * spanfi) + " Newton");
                } else {
                    bc = fuerzasfiAux.size();
                    System.out.println("Fuerza AUX del eje " + ejemedido + " IZQUIERDA: " + (fuerzasfiAux.get(bc - 1) * spanfi) + " Newton");
                }

                break;
        }
        Datos1.clear();

        Datos2.clear();

        Datosfil1.clear();

        Datosfil2.clear();
    }

    public void MedirFuerzaVertical(String lado) throws InterruptedException {
        //double ab = CalcularMaximo(Datos1);
        double auxfuerzad, auxfuerzai;
        int bc;
        long s;
        LabelInfo.setText("MIDIENDO FUERZA VERTICAL...!");
        Thread.sleep(teporizadorInercia);
        comandoSUSP();
        for (i = 0; i < 12; i++) {
            if (i == 1) {
                jProgressBar1.setMaximum(12);
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString(Math.round((i + 1) * 8.334) + "%");
            Thread.sleep(500);
        }
        CapturarDatos("fuerzaVertical");
        comandoSTOP();
        switch (lado) {
            case "derecho":
                armonizar(Datos3, valcalcero3);
                Datosfil1 = filtrar(Datos3);
                auxfuerzad = CalcularMinimo(Datosfil1, 17) * factorescala;
                if ((auxfuerzad - valcalcero3) < 0) {
                    fuerzasvd.add(0.0);
                } else {
                    fuerzasvd.add(auxfuerzad - valcalcero3);
                }
                bc = fuerzasvd.size();
                System.out.println("de nuevo el cero de fuerza derecho " + (valcalcero3 * spanpd) + " N ");
                System.out.println("fuerza sin calibrar " + ejemedido + " derecha: " + (auxfuerzad * spanpd) + " N ");
                System.out.println("fuerza del eje " + ejemedido + " derecha: " + (fuerzasvd.get(bc - 1) * spanpd) + " N ");
                break;
            case "izquierdo":
                armonizar(Datos4, valcalcero4);
                Datosfil2 = filtrar(Datos4);
                auxfuerzai = CalcularMinimo(Datosfil2, 17) * factorescala;
                if ((auxfuerzai - valcalcero4) < 0) {
                    fuerzasvi.add(0.0);
                } else {
                    fuerzasvi.add(auxfuerzai - valcalcero4);
                }
                bc = fuerzasvi.size();
                System.out.println("de nuevo el cero de fuerza izquierdo " + (valcalcero4 * spanpi) + " N");
                System.out.println("fuerza sin calibrar " + ejemedido + " izquierda: " + (auxfuerzai * spanpi) + " N");
                System.out.println("fuerza del eje " + ejemedido + " izquierda: " + (fuerzasvi.get(bc - 1) * spanpi) + " N");
                break;
        }
        Datos3.clear();
        Datos4.clear();
        Datosfil1.clear();
        Datosfil2.clear();
    }

    public void MedirPeso() throws InterruptedException {
        double auxpesod, auxpesoi;
        int bc;
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
        comandoSTOP();
        try {
            puerto.clearFlujoEnt();
            Thread.sleep(1000);
            numtimer1 = 0;
        } catch (InterruptedException ex) {
        }
        long s = Datos3.size();
        long r = Datos4.size();
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
        System.out.println("TABLA de Valores MIDIENDO PESOS ");
        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("valcalcero3 es: " + valcalcero3 + " Mv");
        System.out.println("valcalcero4 es: " + valcalcero4);
        System.out.println("*****************************");
        System.out.println("spanpd es: " + spanpd);
        System.out.println("spanpi es: " + spanpi);
        System.out.println("*****************************");
        System.out.println("Captura Peso Derecho es: " + auxpesod + " Mv");
        System.out.println("Captura Peso Derecho es: " + auxpesoi + " Mv");
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
        System.out.println("de nuevo el cero de peso derecho e izquierdo " + (valcalcero3 * spanpd) + " Newton "
                + (valcalcero4 * spanpi) + " N");
        System.out.println("peso sin calibrar " + ejemedido + " derecho e izquierdo: " + (auxpesod * spanpd) + " Newton "
                + (auxpesoi * spanpi) + " N");
        System.out.println("peso del eje " + ejemedido + " derecho e izquierdo: " + (pesosd.get(bc - 1) * spanpd) + " Newton "
                + (pesosi.get(bc - 1) * spanpi) + " Newton");
        Thread.sleep(100);
        Datos3.clear();
        Datos4.clear();
    }

    public void MedirDesviacion() throws InterruptedException {
        double auxdesv;
        int bc;
        LabelInfo.setText("Midiendo desviación...");
        comandoDESV();
        for (i = 0; i < 10; i++) {
            if (i == 1) {
                jProgressBar1.setMaximum(10);
            }
            jProgressBar1.setValue(i + 1);
            jProgressBar1.setString((i + 1) * 10 + "%");
            Thread.sleep(500);
        }
        comandoSTOP();
        CapturarDatos("desviacion");
        Datosfil1 = filtrar(Datos7);
        auxdesv = CalcularDesviacion(valcalcero7, Datosfil1);
        //Se calcula la desviacion teniendo en cuenta el span y el ancho de placa
        desviaciones.add((auxdesv - valcalcero7) * spand / anchoplacadesv);
        bc = desviaciones.size();
        System.out.println("de nuevo el cero de desviacion " + (valcalcero7 * spand) + " mm");
        System.out.println("desviacion sin calibrar " + ejemedido + " : " + (auxdesv * spand) + " mm");
        System.out.println("desviacion del eje " + ejemedido + " : " + (desviaciones.get(bc - 1)) + " mm/Km");
        LabelInfo.setText("Desviación del eje No." + this.ejemedido + " medida");
        //auxvalmeddesv=(auxvalmeddesv-valcalcero7)*spand/(anchoplacadesv);
        //desviacion.add(auxvalmeddesv*factorescala);
        Datos7.clear();
        Datosfil1.clear();
    }

    public void EsperaPeso() throws InterruptedException {
        double pesomedd, pesomedi;
        double calcPesoDer = 0;
        double calcPesoIzq = 0;
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
            if (Datos3.size() > 27) {
                calcPesoDer = (pesomedd - valcalcero3) * spanpd;
            } else {
                calcPesoDer = 0;
                neceReinTrama = true;
                System.out.println(" reiniciando trama X derecho");
            }
            if (Datos4.size() > 27) {
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

    public void EsperaDesviacion() {
        LabelAviso.setText("PLACA");
        timeraviso.start();
        if (tiempomensajes == 0) {
            JOptionPane.showMessageDialog(this, "Asegurese de que el eje No." + this.ejemedido + " del vehiculo pase sobre la plancha de desviación", "Precaución",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            if (aplcSenDesv == 1) {
                timerPreseDesv.start();
            } else {
                timerautomatico2.start();
            }
            Mensajes.mensajeAdvertencia("Asegurese de que el eje No." + this.ejemedido + " del vehiculo pase sobre la plancha de desviación");
        }

        LabelAviso.setText("");
        timeraviso.stop();
        LabelAviso.setBackground(PanelTitulos.getBackground());
    }

    public void EsperaRodillos() throws InterruptedException {
        int conteoreg = 0;
        puerto.setFlujoEnt(puerto);
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
                LabelInfo.setText("Por favor mantenga el eje sobre los rodillos");
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
                EnviaSalidas(0, salidamotorderecho);
                LabelInfo.setText("Moviendo eje No." + ejemedido + " en el lado derecho");
                break;
            case "izquierdo":
                EnviaSalidas(0, salidamotorizquierdo);
                LabelInfo.setText("Moviendo eje No." + ejemedido + " en el lado izquierdo");
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

    public void MoverRodillos(Boolean freAux) throws InterruptedException {
        puerto.setFlujoEnt(puerto);
        int cont1s;
        comandoFREN();
        for (pasoactual = 1; pasoactual <= (numpasos / 2); pasoactual++) {
            LeePaso();
            if (tiempo_paso == 0) {
                JOptionPane.showMessageDialog(this, "Disculpe, Consegui un ERROR en el archivo de configuración", "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
                comandoSTOP();
                puerto.close();
                dispose();
            } else {
                //LabelInfo.setText("Moviendo rodillos a baja velocidad");
                //Thread.sleep(100);
                EnviaSalidas(salidaalta, salidabaja);
                // <editor-fold desc="Mensajes durante la secuencia de frenos">
                switch (pasoactual) {
                    case 1:
                        System.out.println("Moviendo Rodillos en un sentido");
                        LabelInfo.setText("MOVIENDO RODILLOS EN UN SENTIDO..!");
                        break;
                    case 2:
                        if (isPista_mixta()) {
                            System.out.println("Cambiando velocidad");
                            LabelInfo.setText("CAMBIANDO VELOCIDAD..!");
                        } else {
                            System.out.println("Manteniendo velocidad");
                            LabelInfo.setText("MANTENIENDO VELOCIDAD..!");
                        }
                        break;
                    case 3:
                        System.out.println("Determinando velocidad critica");
                        LabelInfo.setText("DETERMINANDO VELOCIDAD CRITICA..!");
                        break;
                    case 4:
                        System.out.println("Vaya frenando gradualmente");
                        LabelInfo.setText("POR FAVOR VAYA FRENANDO ..!");
                        break;
                    case 5:
                        System.out.println("Apagando contactores");
                        LabelInfo.setText("APAGANDO CONTACTORES..!");
                        break;
                    case 6:
                        System.out.println("Fuerza de frenado medida para la rueda derecha");
                        LabelInfo.setText("<html>FUERZA DE FRENADO MEDIDA PARA LA RUEDA DERECHA</html>");
                        break;
                    case 7:
                        System.out.println("Moviendo Rodillos en el otro sentido");
                        LabelInfo.setText("MOVIENDO RODILLOS EN EL OTRO SENTIDO..!");
                        break;
                    case 8:
                        if (isPista_mixta()) {
                            System.out.println("Cambiando velocidad");
                            LabelInfo.setText("CAMBIANDO VELOCIDAD");
                        } else {
                            System.out.println("Manteniendo velocidad");
                            LabelInfo.setText("MANTENIENDO VELOCIDAD..!");
                        }
                        break;
                    case 9:
                        System.out.println("Determinando velocidad critica");
                        LabelInfo.setText("DETERMINANDO VELOCIDAD CRITICA..!");
                        break;
                    case 10:
                        System.out.println("Vaya frenando gradualmente");
                        LabelInfo.setText("POR FAVOR VAYA FRENANDO ..!");
                        break;
                    case 11:
                        System.out.println("Apagando contactores");
                        LabelInfo.setText("APAGANDO CONTACTORES..!");
                        break;
                    case 12:
                        System.out.println("Fuerza de frenado medida para la rueda izquierda");
                        LabelInfo.setText("<html>FUERZA DE FRENADO MEDIDA PARA LA RUEDA IZQUIERDA</html>");
                        break;
                    default:
                        System.out.println("ouch");
                        LabelInfo.setText("ouch");
                        break;
                }
// </editor-fold>
                //System.out.println("El tiempo paso es: "+(tiempo_paso/1000));
                for (cont1s = 0; cont1s < (tiempo_paso / 1000); cont1s++) {
                    Thread.sleep(1000);
                    System.out.println("Paso 1s,i: " + pasoactual + " j: " + cont1s);
                    CapturarDatos("frenometro");
                    // <editor-fold desc="Se determina si el eje esta sobre los rodillos durante la prueba">
                    if ((!isCanal0() || !isCanal1()) && pasoactual != 12) {
                        //A excepción de los pasos en que se debe avanzar de los rodillos
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                        if (pasoactual == 4 || pasoactual == 5 || pasoactual == 10 || pasoactual == 11) {
                            //Se puede dar el caso de que salga el vehiculo por la fuerza de frenado
                            //en los pasos de medir fuerza de frenado y soltar la motocicleta
                            //pero no involucran error en la prueba
                            cont1s = tiempo_paso / 1000;
                        } else {
                            LabelInfo.setText(" DETECTO QUE EL EJE SE SALIO DE LOS RODILLOS..!");
                            led1.setLedOn(false);
                            led2.setLedOn(false);
                            timeraviso.stop();
                            LabelAviso.setText("");
                            LabelAviso.setBackground(PanelTitulos.getBackground());
                            comandoSTOP();
                            Thread.sleep(2577);
                            LabelInfo.setText("SE INFORMA QUE LA PRUEBA FUE ABORTADA . PRESIONE FINALIZAR");
                            tecladoactivado = true;
                            BotonFinalizar.setEnabled(true);
                            break;
                        }
                    }
                    // </editor-fold>
                    // <editor-fold desc="Se determina si la velocidad baja a un nivel umbral">
                    if (pasoactual == 4 || pasoactual == 10) {
                        if ((((double) ComandoRecibido[6] - valcalcero5) <= velminimad) || (((double) ComandoRecibido[7] - valcalcero6) <= velminimai)) {
                            cont1s = tiempo_paso / 1000;
                        }
                        System.out.println("velocidad derecha: " + ((double) ComandoRecibido[6] - valcalcero5)
                                + " velocidad izquierda: " + ((double) ComandoRecibido[7] - valcalcero6));
                    }
                    // </editor-fold>                    
                }
                switch (pasoactual) {
                    // <editor-fold desc="Se movieron los rodillos a baja velocidad">
                    case 1:
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se hace el cambio de velocidades pero con un reposo para los contactores o se mantiene la velocidad">
                    case 2:
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se midio la fuerza de frenado">
                    case 3:
                        velminimad = (CalcularMedia(Datos5) - valcalcero5) * umbral_velo;
                        velminimai = (CalcularMedia(Datos6) - valcalcero6) * umbral_velo;
                        System.out.println("velocidad minima derecha: " + velminimad
                                + " velocidad minima izquierda: " + velminimai);
                        timeraviso.start();
                        LabelAviso.setText("FRENE");
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se apago los rodillos">
                    case 4:
                        MedirFuerzaFrenado("derecho", freAux);
                        timeraviso.stop();
                        LabelAviso.setText("");
                        LabelAviso.setIcon(null);
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se solto la moto">
                    case 5: //LabelEje.setText("Eje "+ejemedido);
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 6:
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 7:
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 8:
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 9:
                        velminimad = (CalcularMedia(Datos5) - valcalcero5) * umbral_velo;
                        velminimai = (CalcularMedia(Datos6) - valcalcero6) * umbral_velo;
                        System.out.println("velocidad minima derecha: " + velminimad
                                + " velocidad minima izquierda: " + velminimai);
                        timeraviso.start();
                        LabelAviso.setText("FRENE");
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 10:
                        MedirFuerzaFrenado("izquierdo", freAux);
                        timeraviso.stop();
                        LabelAviso.setText("");
                        this.LabelAviso.setIcon(null);
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 11:
                        break;
                    // </editor-fold>
                    // <editor-fold desc="Se sujeto la moto">
                    case 12:
                        break;
                    // </editor-fold>                    
                }
                Datos1.clear();
                Datos2.clear();
                Datos3.clear();
                Datos4.clear();
                Datos5.clear();
                Datos6.clear();
            }
            //comandoSTOP();
        }
        comandoSTOP();
        try {
            config.reset();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Thread.sleep(100);
        comandoSTOP();
    }

    public double CalcularDesviacion(double media, List<Double> Datosfil) {
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
        return c;
    }

    public double CalcularMedia(List<Integer> Datos) {
        long b, s;
        double c;
        s = Datos.size();
        //System.out.println("El numero de datos es: "+s);
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

    public double CalcularMinimo(List<Double> Datos, int offset) {
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
        int s, n, p, q;
        double y;
        //int diezmado=17;
        List<Double> Datosfil = new ArrayList<>(); //Valores de la salida del filtro
        s = Datos.size();
        n = filtro.length;
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

    public void armonizar(List<Integer> Datos, double promedio) {    //La lista de datos esta pasando por referencia
        int s1;
        double pendientesig, pendienteant;
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
                    pendienteant = (Datos.get(t) - Datos.get(t - 1)) / Datos.get(t - 1);
                }
                if (Datos.get(t + 1) == 0) {
                    pendientesig = Datos.get(t);
                } else {
                    pendientesig = (Datos.get(t) - Datos.get(t + 1)) / Datos.get(t + 1);
                }
            }
            if (Math.abs(pendienteant) > 1 && Math.abs(pendientesig) > 1) {  //Se comprueba si la pendiente de cambio
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
                        int eve = 0;
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
        }
    }

    void ConsultarPermisibles() throws SQLException, ClassNotFoundException {
        String statement;
        ResultSet rs;
        Connection conexion = DBUtil.getConnection();
        System.out.println("Conectandose con:" + "jdbc:mysql://" + URLServidor + ":3306/db_cda");
        conexion.setAutoCommit(false);
        //<editor-fold desc="Consulta los permisibles para la prueba de desviación">
        if (isEnableswdesv()) {
            statement = "SELECT * FROM permisibles WHERE Id_permisible=58";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            rs = instruccion.executeQuery();
            if (rs.next()) {
                setPermisiblepruebadesv1(rs.getDouble(4));
            }
            System.out.println("El permisible de la prueba de desviación para el primer eje es: " + getPermisiblepruebadesv1());
            statement = "SELECT * FROM permisibles WHERE Id_permisible=58";
            instruccion = conexion.prepareStatement(statement);
            rs = instruccion.executeQuery();
            if (rs.next()) {
                setPermisiblepruebadesv2(rs.getDouble(4));
            }
            System.out.println("El permisible de la prueba de desviación para los demas ejes es: " + getPermisiblepruebadesv2());
        }
        //</editor-fold>
        //<editor-fold desc="Consulta los permisibles para la prueba de suspensión">
        if (isEnableswsusp()) {
            statement = "SELECT * FROM permisibles WHERE Id_permisible=48";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            rs = instruccion.executeQuery();
            if (rs.next()) {
                setPermisiblepruebasusp(rs.getDouble(3));
            }
            System.out.println("El permisible de la prueba de suspensión es: " + getPermisiblepruebasusp());
        }
        //</editor-fold>
        //<editor-fold desc="Consulta los permisibles para la prueba de frenos">
        if (isEnableswfren()) {
            statement = "SELECT * FROM permisibles WHERE Id_permisible=46";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            rs = instruccion.executeQuery();
            if (rs.next()) {
                setPermisiblepruebafren1(rs.getDouble(3));
            }
            System.out.println("El permisible de la prueba de eficacia total de frenos es: " + getPermisiblepruebafren1());
            statement = "SELECT * FROM permisibles WHERE Id_permisible=47";
            instruccion = conexion.prepareStatement(statement);
            rs = instruccion.executeQuery();
            if (rs.next()) {
                setPermisiblepruebafren2(rs.getDouble(3));
            }
            System.out.println("El permisible de la prueba de freno de mano es: " + getPermisiblepruebafren2());
            /*Por ahora como constante mientras se arregla la base de datos
             statement ="SELECT * FROM permisibles WHERE Id_permisible=48";           
             instruccion = conexion.prepareStatement(statement);
             rs=instruccion.executeQuery();
             if(rs.next()){
             setPermisiblepruebafren3(rs.getDouble(4));
             }*/
            setPermisiblepruebafren3(30);
            System.out.println("El permisible de la prueba de desequilibrio de frenos tipo A es: " + getPermisiblepruebafren3());
            statement = "SELECT * FROM permisibles WHERE Id_permisible=44";
            instruccion = conexion.prepareStatement(statement);
            rs = instruccion.executeQuery();
            if (rs.next()) {
                setPermisiblepruebafren4(rs.getDouble(4));
            }
            System.out.println("El permisible de la prueba de desequilibrio de frenos tipo B es: " + getPermisiblepruebafren4());
        }
        //</editor-fold>
    }

    void RegistrarMedidasDesviacion() {
        Desviacion desviacion = new Desviacion();
        PruebaDefaultDAO desviacionDAO = new PruebaDefaultDAO();

        for (Double desviacione : desviaciones) {
//            desviacion.setDesviacion(Math.abs(desviacione));            
            desviacion.setDesviacion(desviacione * spand);
            desviacion.setResolMin(resolMin);
            desviacion.setResolMax(resolMax);
        }
        try {
            PruebaDefaultDAO.escrTrans = "@";
            try {
                repetirPrueba = desviacionDAO.persist(desviacion, idPruebadesv, idUsuario, aplicTrans, this.ipEquipo,tipoPista,tipoVehiculo,this.Placa, "desviaciones DlgIntegrado4x4");
            } catch (ClassNotFoundException ex) { }
            if (repetirPrueba == false) {
                tramaAuditoriaDesv = "{";
                for (int k = 0; k < desviacion.getDesviacion().size(); k++) {
                    tramaAuditoriaDesv = tramaAuditoriaDesv.concat("{\"eje").concat(String.valueOf(k + 1)).concat("\":\"").concat(String.valueOf(desviacion.getDesviacion().get(k))).concat("\",");
                }
                tramaAuditoriaDesv = tramaAuditoriaDesv.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPruebadesv)).concat("\"}");
            }
        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    void RegistrarMedidasSuspension() {
        Suspension suspension = new Suspension();
        PruebaDefaultDAO suspensionDAO = new PruebaDefaultDAO();

        for (int k = 0; k < fuerzasvd.size(); k++) {
            suspension.setFuerzaDerecha(Math.floor(Math.round(fuerzasvd.get(k) * spanpd)));
            suspension.setFuerzaIzquierda(Math.floor(Math.round(fuerzasvi.get(k) * spanpi)));
        }

        for (int k = 0; k < pesosd.size(); k++) {
            suspension.setPesoDerecho(Math.floor(Math.round(pesosd.get(k) * spanpd)));
            suspension.setPesoIzquierdo(Math.floor(Math.round(pesosi.get(k) * spanpi)));
        }

        try {
            PruebaDefaultDAO.escrTrans = "@";
            try {
                repetirPrueba = suspensionDAO.persist(suspension, idPruebasusp, idUsuario, aplicTrans, this.ipEquipo,tipoPista,tipoVehiculo,this.Placa, "Suspencion dlgIntegrado4x4");
            } catch (ClassNotFoundException ex) {  }
            if (repetirPrueba == false) {
                tramaAuditoriaSusp = "";
                tramaAuditoriaSusp = tramaAuditoriaSusp.concat("{\"delanteraIzquierda\":\"").concat(String.valueOf(Suspension.suspension.get(0))).concat("\",").concat("\"delanteraDerecha\":\"").concat(String.valueOf(Suspension.suspension.get(0 + 1))).concat("\",").concat("\"traseraIzquierda\":\"").concat(String.valueOf(Suspension.suspension.get(0 + 2))).concat("\"traseraDerecha\":\"").concat(String.valueOf(Suspension.suspension.get(0 + 3)));
                tramaAuditoriaSusp = tramaAuditoriaSusp.concat("\",\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPruebasusp)).concat("\"}");
            }
        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    void RegistrarMedidasFrenos() {
        int facEnseñanza = 1;
        Frenos frenos = new Frenos(ajusteDsq);
        PruebaDefaultDAO frenosDAO = new PruebaDefaultDAO();
        if (isEnsenianza()) {
            for (int k = 1; k < fuerzasfd.size(); k += 2) {
                frenos.setFuerzaDerechaEnseñanza(Math.floor(Math.round(fuerzasfd.get(k) * spanfd)));
                frenos.setFuerzaIzquierdaEnseñanza(Math.floor(Math.round(fuerzasfi.get(k) * spanfi)));
            }
            facEnseñanza = 2;
        }
        for (int k = 0; k < fuerzasfdAux.size(); k++) {
            frenos.setFuerzaDerechaAux(Math.floor(Math.round(fuerzasfdAux.get(k * facEnseñanza) * spanfd)));
            frenos.setFuerzaIzquierdaAux(Math.floor(Math.round(fuerzasfiAux.get(k * facEnseñanza) * spanfi)));
        }
        for (int k = 0; k < fuerzasfd.size(); k++) {
            frenos.setFuerzaDerecha(Math.floor(Math.round(fuerzasfd.get(k * facEnseñanza) * spanfd)));
            frenos.setFuerzaIzquierda(Math.floor(Math.round(fuerzasfi.get(k * facEnseñanza) * spanfi)));
        }

        for (int k = 0; k < pesosd.size(); k++) {
            frenos.setPesoDerecho(Math.floor(Math.round(pesosd.get(k) * spanpd)));
            frenos.setPesoIzquierdo(Math.floor(Math.round(pesosi.get(k) * spanpi)));
        }

        try {
            PruebaDefaultDAO.escrTrans = "@";
            try {
                repetirPrueba = frenosDAO.persist(frenos, idPruebafren, idUsuario, aplicTrans, this.ipEquipo,tipoPista,tipoVehiculo,this.Placa, "frenos DlgIntegrado4x4");
            } catch (ClassNotFoundException ex) {    }
            if (repetirPrueba                                                == false) {
                DlgIntegrado4x4.tramaAuditoria = "{\"eficaciaTotal\":\"".concat(String.valueOf(Frenos.eficacia)).concat("\",").concat("\"eficaciaAuxiliar\":\"").concat(String.valueOf(Frenos.eficaciaFrenoMano)).concat("\",");
                for (int k = 0; k < frenos.getPesoDerecho().size(); k++) {
                    DlgIntegrado4x4.tramaAuditoria = DlgIntegrado4x4.tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(k + 1)).concat("Izquierdo\":\"").concat(String.valueOf(frenos.getFuerzaIzquierda().get(k))).concat("\",").concat("\"pesoEje").concat(String.valueOf(k + 1)).concat("Izquierdo\":\"").concat(String.valueOf(frenos.getPesoIzquierdo().get(k))).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(k + 1)).concat("Derecho\":\"").concat(String.valueOf(frenos.getFuerzaDerecha().get(k))).concat("\",").concat("\"pesoEje").concat(String.valueOf(k + 1)).concat("Derecho\":\"").concat(String.valueOf(frenos.getPesoDerecho().get(k))).concat("\",").concat("\"eje").concat(String.valueOf(k + 1)).concat("Desequilibro\":\"").concat(String.valueOf(Frenos.desequilibrio.get(k))).concat("\",");
                }
                for (int k = frenos.getPesoDerecho().size() + 1; k < 6; k++) {
                    tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(k)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(k)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(k)).concat("Derecho\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(k)).concat("Derecho\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"eje").concat(String.valueOf(k)).concat("Desequilibro\":\"").concat(String.valueOf(" ")).concat("\",");
                }
                DlgIntegrado4x4.tramaAuditoria = DlgIntegrado4x4.tramaAuditoria.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPruebafren)).concat("\"}");
            }
        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }

        frenos.imprimirValores();
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
        PanelInformacion = new javax.swing.JPanel();
        PanelMensajes = new javax.swing.JPanel();
        LabelEje = new javax.swing.JLabel();
        LabelInfo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        led1 = new eu.hansolo.steelseries.extras.Led();
        jLabel4 = new javax.swing.JLabel();
        led2 = new eu.hansolo.steelseries.extras.Led();
        BotonEmpezar = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        BotonContinuar = new javax.swing.JButton();
        BotonFinalizar = new javax.swing.JButton();
        BotonCancelar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        LabelPrueba = new javax.swing.JLabel();
        lblCtxPrueba = new javax.swing.JLabel();

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

        LabelAviso.setFont(new java.awt.Font("Andalus", 0, 32)); // NOI18N
        LabelAviso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelAviso.setOpaque(true);
        LabelAviso.setPreferredSize(new java.awt.Dimension(280, 116));

        javax.swing.GroupLayout PanelTitulosLayout = new javax.swing.GroupLayout(PanelTitulos);
        PanelTitulos.setLayout(PanelTitulosLayout);
        PanelTitulosLayout.setHorizontalGroup(
            PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addComponent(LabelAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 1569, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelTitulosLayout.setVerticalGroup(
            PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelAviso, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
        );

        PanelInformacion.setPreferredSize(new java.awt.Dimension(1210, 447));

        PanelMensajes.setPreferredSize(new java.awt.Dimension(309, 70));

        LabelEje.setFont(new java.awt.Font("Andalus", 0, 41)); // NOI18N
        LabelEje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelEje.setText("Eje");
        LabelEje.setMinimumSize(new java.awt.Dimension(49, 24));
        LabelEje.setPreferredSize(new java.awt.Dimension(700, 100));

        LabelInfo.setFont(new java.awt.Font("Andalus", 0, 55)); // NOI18N
        LabelInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelInfo.setText("Iniciando sistema...");
        LabelInfo.setMinimumSize(new java.awt.Dimension(309, 24));
        LabelInfo.setPreferredSize(new java.awt.Dimension(700, 142));

        jLabel3.setFont(new java.awt.Font("Andalus", 0, 30)); // NOI18N
        jLabel3.setText("Presencia DER");

        led1.setMaximumSize(new java.awt.Dimension(96, 96));
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

        jLabel4.setFont(new java.awt.Font("Andalus", 0, 30)); // NOI18N
        jLabel4.setText("Presencia IZQ");

        led2.setMaximumSize(new java.awt.Dimension(96, 96));
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
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 665, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMensajesLayout.createSequentialGroup()
                        .addComponent(LabelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 1534, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        PanelMensajesLayout.setVerticalGroup(
            PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMensajesLayout.createSequentialGroup()
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelMensajesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel3)
                                .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMensajesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(26, 26, 26)))
                .addComponent(LabelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout PanelInformacionLayout = new javax.swing.GroupLayout(PanelInformacion);
        PanelInformacion.setLayout(PanelInformacionLayout);
        PanelInformacionLayout.setHorizontalGroup(
            PanelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInformacionLayout.createSequentialGroup()
                .addComponent(PanelMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 1544, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelInformacionLayout.setVerticalGroup(
            PanelInformacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

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

        jProgressBar1.setFont(new java.awt.Font("Andalus", 0, 60)); // NOI18N
        jProgressBar1.setForeground(new java.awt.Color(255, 0, 0));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(1024, 95));
        jProgressBar1.setStringPainted(true);

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

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N
        jLabel1.setMinimumSize(new java.awt.Dimension(171, 51));
        jLabel1.setPreferredSize(new java.awt.Dimension(1024, 91));

        LabelPrueba.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        LabelPrueba.setForeground(new java.awt.Color(0, 102, 255));
        LabelPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelPrueba.setText("PRUEBA PARA 4x4");
        LabelPrueba.setToolTipText("");
        LabelPrueba.setMinimumSize(new java.awt.Dimension(541, 30));
        LabelPrueba.setPreferredSize(new java.awt.Dimension(1024, 145));

        lblCtxPrueba.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        lblCtxPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCtxPrueba.setToolTipText("");
        lblCtxPrueba.setMinimumSize(new java.awt.Dimension(541, 30));
        lblCtxPrueba.setOpaque(true);
        lblCtxPrueba.setPreferredSize(new java.awt.Dimension(1024, 145));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelTitulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelInformacion, javax.swing.GroupLayout.DEFAULT_SIZE, 1569, Short.MAX_VALUE))
                .addContainerGap(34, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LabelPrueba, javax.swing.GroupLayout.DEFAULT_SIZE, 1038, Short.MAX_VALUE)
                    .addComponent(lblCtxPrueba, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BotonEmpezar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BotonContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BotonFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(BotonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 1516, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LabelPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCtxPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelInformacion, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonEmpezar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                System.out.println(">>> voy a timer:");
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

    private void BotonEmpezarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEmpezarActionPerformed
        // TODO add your handling code here:
        int opcion;
        tecladoactivado = false;
        comandoSTOP();
        puerto.clearFlujoEnt();
        puerto.setFlujoEnt(puerto);
        try {
            Thread.currentThread().sleep(1500);
        } catch (InterruptedException ex) {
        }
        if (!PruebaConexion()) {
            opcion = JOptionPane.showOptionDialog(this, "Disculpe, en estos momentos  La tarjeta no se encuentra CONECTADA o INICIALIZADA."
                    + " Desea conectarla", "SART 1.7.3", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (opcion == 0) {
                Tarjeta tar = new Tarjeta();
                tar.setPuerto(puerto);
                tar.IniciaTarjeta();
                if (tar.getNumconex() == 3) {
                    JOptionPane.showMessageDialog(null, "Por Favor vuelva INTENARLO mas tarde",
                            "SART 1.7.3", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Asegurese de que las planchas esten libres", "SART 1.7.3",
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
                JOptionPane mensaje1 = new JOptionPane();
                timerautomatico2.start();
                mensaje1.showMessageDialog(this, "Por favor ASEGURESE de que las Planchas esten Libres", "SART 1.7.3",
                        JOptionPane.WARNING_MESSAGE);
            }
            LabelInfo.setText("CALIBRANDO A CEROS...!");
            t1enabled(true);
        }
    }//GEN-LAST:event_BotonEmpezarActionPerformed

    private void BotonContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonContinuarActionPerformed
        // TODO add your handling code here:
        BotonContinuar.setEnabled(false);
        hiloPrincipal.start();
    }//GEN-LAST:event_BotonContinuarActionPerformed

    private void BotonFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonFinalizarActionPerformed
        // TODO add your handling code here:
        int p, a;
        comandoSTOP();
        puerto.close();
        try {
            config.close();
        } catch (IOException ex) {
            System.out.println("Error cerrando el archivo de configuracion");
        }
//<editor-fold desc="Califica la prueba de desviacion">
        if (enablehwdesv && enableswdesv && enablereg) {
            RegistrarMedidasDesviacion();
        }

        if (enablehwsusp && enableswsusp && enablereg) {
            RegistrarMedidasSuspension();
        }
//</editor-fold>
//<editor-fold desc="Califica la prueba y calcula los valores de frenos">
        if (enablehwfren && enableswfren && enablereg) {
            RegistrarMedidasFrenos();
        }
//</editor-fold>
        this.dispose();
    }//GEN-LAST:event_BotonFinalizarActionPerformed

    private void BotonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarActionPerformed
        comandoSTOP();

        JXLoginPane.Status status = UtilLogin.loginAdminDBCDA();//muestra el dialogo para el login de la aplicacion, solamente usuario administrador

        if (status == JXLoginPane.Status.SUCCEEDED) {
            Thread tmpBlinker = hiloPrincipal;
//            hiloPrincipal = null;
            if (tmpBlinker != null) {
                tmpBlinker.interrupt();
            }
            timeraviso.stop();
            puerto.close();
            System.out.println("Prueba cancelada");

            dialogCancelacion.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Autenticacion fallida");
        }
    }//GEN-LAST:event_BotonCancelarActionPerformed
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

    private void registrarCancelacion(String comentario) throws ClassNotFoundException, SQLException {

        PruebasDAO pruebasDAO = new PruebasDAO();
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        pruebasDAO.cancelarPrueba(comentario, idPruebafren, idUsuario, new java.sql.Date(c.getTimeInMillis()));
    }

    private void BotonEnviarCancelacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEnviarCancelacionActionPerformed
        // TODO add your handling code here:
        motivoCancelacion = jTextArea1.getText();
        /*try {
         registrarCancelacion(motivoCancelacion);
         } catch (ClassNotFoundException ex) {
         Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
         } catch (SQLException ex) {
         Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
         }*/
        try {
            registrarCancelacion(motivoCancelacion);

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DlgIntegrado4x4.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    public int getNumeroejes() {
        return numeroejes;
    }

    public void setNumeroejes(int numeroejes) {
        this.numeroejes = numeroejes;
    }

    public boolean isEnsenianza() {
        return ensenianza;
    }

    public void setEnsenianza(boolean ensenianza) {
        this.ensenianza = ensenianza;
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
                DlgIntegrado4x4 dialog = new DlgIntegrado4x4(new javax.swing.JFrame(), true);
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

        if (numtimer1 == 7) {
            CapturarDatos("ceros");
        }
        if (numtimer1 == 8) {
            CapturarDatos("ceros");
            comandoSTOP();
        }
        if (numtimer1 == 10) {
            CalibrarCeros(isEnablehwfren() && isEnableswfren(),
                    isEnablehwfren() && isEnableswfren(),
                    (isEnablehwfren() && isEnableswfren()) || (isEnablehwsusp() && isEnableswsusp()),
                    (isEnablehwfren() && isEnableswfren()) || (isEnablehwsusp() && isEnableswsusp()),
                    isEnablehwfren() && isEnableswfren(),
                    isEnablehwfren() && isEnableswfren(),
                    isEnablehwdesv() && isEnableswdesv());
            LabelInfo.setText("SISTEMA CALIBRADO POR CEROS..!");
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
            //double pesomed;
            try {
                /* while (true) {
                   if (enablehwdesv && enableswdesv) {
                        if (ejemedido > 1) {
                            Mensajes.messageWarningTime("Por Favor Retire el vehiculo de las maquinas, para iniciar con el eje " + DlgIntegrado4x4.this.ejemedido, 6);
                        }
                        LabelPrueba.setText("PRUEBA DESVIACION PARA 4X4 ");
                        lblCtxPrueba.setText("USER: " + DlgIntegrado4x4.NombreUsr + "; PLACA: " + DlgIntegrado4x4.Placa);
                        EsperaDesviacion();
                        MedirDesviacion();

                        if (ejemedido == numeroejes) {
                            Thread.sleep(100);
                            if ((enablehwsusp && enableswsusp) || (enablehwfren && enableswfren)) {
                                break;
                            } else {
                                if (!BotonFinalizar.isEnabled()) {
                                    comandoSTOPAnalogo();
                                    BotonFinalizar.setEnabled(true);
                                    timerfinalizar.setRepeats(false);
                                    timerfinalizar.start();
                                    return;
                                }
                            }
                        } else {
                            ejemedido++;
                            LabelEje.setText("EJE: " + ejemedido);
                        }
                    }else{
                         break;
                    }
                }*/
                ejemedido=1;
                LabelEje.setText("EJE: " + ejemedido);
                while (true) {
                    if (ejemedido > 1) {
                        Mensajes.messageWarningTime("POR FAVOR RETIRE EL VEHICULO DE LAS MAQUINAS, PARA INICIAR CON EL EJE" + ejemedido, 6);
                    }
                    if (enablehwdesv && enableswdesv) {                        
                         LabelPrueba.setText("PRUEBA DESVIACION PARA 4X4");
                         
                        lblCtxPrueba.setText("USER: " + DlgIntegrado4x4.NombreUsr + "; PLACA: " + DlgIntegrado4x4.Placa);
                        EsperaDesviacion();
                        MedirDesviacion();
                    } 
                    if ((enablehwsusp && enableswsusp) || (enablehwfren && enableswfren)) {
                        if (enablehwsusp && enableswsusp) {
                            LabelPrueba.setText("PRUEBA DE SUSPENSION PARA 4x4");
                            lblCtxPrueba.setText("USER: "+ DlgIntegrado4x4.NombreUsr+ "; PLACA: "+ DlgIntegrado4x4.Placa );
                        } else if (enablehwfren && enableswfren) {
                            LabelPrueba.setText("PRUEBA DE FRENOS PARA 4x4");
                            lblCtxPrueba.setText("USER: "+ DlgIntegrado4x4.NombreUsr+ "; PLACA: "+ DlgIntegrado4x4.Placa );
                        }
                        EsperaPeso();
                        MedirPeso();
                        if (enablehwsusp && enableswsusp) {
                            LabelPrueba.setText("PRUEBA DE SUSPENSION PARA 4x4");
                            MoverSuspension("derecho");
                            MedirFuerzaVertical("derecho");
                            MoverSuspension("izquierdo");
                            MedirFuerzaVertical("izquierdo");
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
                            LabelPrueba.setText("PRUEBA DE FRENOS PARA 4x4");
                            lblCtxPrueba.setText("USER: "+ DlgIntegrado4x4.NombreUsr+ "; PLACA: "+ DlgIntegrado4x4.Placa );
                            imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoOn.png"));
                            imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoOf.png"));
                            EsperaRodillos();
                            MoverRodillos(false);
                            if (isEnsenianza()) {
                                LabelEje.setText("EJE " + ejemedido + " (ENSEÑANZA)");
                                LabelInfo.setText("Ahora la prueba del freno del instructor");
                                Thread.sleep(2000);
                                EsperaRodillos();
                                MoverRodillos(false);
                                LabelEje.setText("EJE " + ejemedido);
                            }
                            
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

                            if (ejemedido == getNumeroejes()) {                            
                                if (!BotonFinalizar.isEnabled()) {
                                    comandoSTOPAnalogo();
                                    BotonFinalizar.setEnabled(true);
                                    timerfinalizar.setRepeats(false);
                                    timerfinalizar.start();
                                }
                            }
                        }
                    }
                    if (ejemedido == getNumeroejes()) {
                        //RegistrarMedidas();
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
                        LabelEje.setText("Eje " + ejemedido);
                    }
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }
        }
    }
    public boolean isFrenmano() {
        return frenmano;
    }

    public void setFrenmano(boolean frenmano) {
        this.frenmano = frenmano;
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
