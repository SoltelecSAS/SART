/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import com.soltelec.loginadministrador.UtilLogin;
import com.soltelec.modulopuc.utilidades.Mensajes;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import javax.swing.JOptionPane;
import javax.swing.Timer;
import modelo.Frenos;
import modelo.FrenosMotos;
import org.jdesktop.swingx.JXLoginPane;

/**
 *
 * @author Gerencia TIC
 */
public class DlgFrenoMoto extends javax.swing.JDialog implements ActionListener {

    private PuertoRS232 puerto;
    private String puertotarjeta;
    private final byte[] buffer = new byte[1024];
    private byte data;
    private int j = 0, i = 0, len = -1, partealta, numtimer1 = 0, numtimer2 = 0, numtimer3 = 0, t = 0, conteoreg;
    private final int[] ComandoRecibido = new int[9];
    private BufferedReader config;
    public static String tramaAuditoria;

    private final List<Integer> Datos1 = new ArrayList<>(); //Datos correspondientes al peso
    private final List<Integer> Datos2 = new ArrayList<>(); //Datos correspondientes a la fuerza de frenado
    private final List<Integer> Datos3 = new ArrayList<>(); //Datos correspondientes a la velocidad del eje

    private double valcalcero1 = 0, valcalcero2 = 0, valcalcero3 = 0, ruidomotor;
    private byte pasomedfren = 0, numpasos, pasoactual = 0;
    private final List<Double> pesos = new ArrayList<>(); //Valores de peso de cada eje
    private final List<Double> fuerzas = new ArrayList<>(); //Valores de las fuerzas de frenado de cada eje
    private final List<Double> velocidades = new ArrayList<>(); //Valores de las velocidades de cada eje
    private double eficacia;
    private final List<Double> Datosfil = new ArrayList<>(); //Valores de la salida del filtro
    private double spanf, spanp, spanv;  //valores de span de los canales obtenidos de la calibración
    private boolean canal0 = false;
    private String ejemedido = "Delantero";
    private int tiempo_paso = 1;
    private byte salidaalta, salidabaja;
    private String line;
    private double velminima, umbral, umbral_peso;
    private TFrenmotos hilo1 = new TFrenmotos();
    private boolean paso1s = false;  //Variable que indica si ha pasado 1 s, determinado por el Timer3
    private TFrenmotosPeso2 hilo2 = new TFrenmotosPeso2();
    private EsperaPeso hilo3 = new EsperaPeso();
    private EsperaPeso hilo4 = new EsperaPeso();
    private final double[] filtro = {0.0091, 0.0131, 0.0245, 0.0416, 0.0619, 0.0821, 0.0993, 0.1108,
        0.1149, 0.1108, 0.0993, 0.0821, 0.0619, 0.0416, 0.0245, 0.0131, 0.0091};
    private String motivoCancelacion;
    private int idPrueba;
    private int idUsuario;
    private int idHojaPrueba;
    private int temp = 0;
    private boolean errorConfig;
    private boolean repetirPrueba;
    ImageIcon imageOn = null;
    ImageIcon imageOff = null;
    private int aplicTrans=1;
    private String ipEquipo;
    public static String escrTrans = "";
    public static String Placa;
    public static String NombreUsr;
     private int  numtimerautomatico2 = 0;

    /**
     * Creates new form dalogfrenmotos
     *
     * @param parent
     * @param modal
     */
    public DlgFrenoMoto(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize(this.getToolkit().getScreenSize());
        dialogCancelacion.setLocationRelativeTo(null);
        this.setTitle("SART 1.7.3 MOD D.F.S PARA MOTOS");
        configuracion();
        hilo1.setA(this);
        if (!errorConfig) {
            timerempezar.setRepeats(false);
            timerempezar.start();
        } else {
            System.out.println("Salida de la prueba por error en el archivo de configuración");
        }
    }

    public DlgFrenoMoto(Frame parent, boolean modal, int idPrueba, int idUsuario, int idHojaPrueba,int aplicTrans,String ipEquipo,String Placa,String NombreUsr) {
        this(parent, modal);
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.idHojaPrueba = idHojaPrueba;
         this.aplicTrans= aplicTrans;
        this.ipEquipo=ipEquipo;
        this.Placa=Placa;
        this.NombreUsr =NombreUsr;
    }

    private void configuracion() {
        errorConfig = false;

        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));

            while (!config.readLine().startsWith("[DAQ]")) {
            }

            if ((line = config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
            }

            while (!config.readLine().startsWith("[FRENOMETRO]")) {
            }

            if ((line = config.readLine()).startsWith("umbral_velo")) {
                umbral = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length())) / 100;
            }

            if ((line = config.readLine()).startsWith("umbral_peso")) {
                umbral_peso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            }
            
            while (!config.readLine().startsWith("motos:")) {
            }

            if ((line = config.readLine()).startsWith("pasos:")) {
                numpasos = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.length()));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            errorConfig = true;
        } catch (IOException ex) {
            System.out.println("no se pudo abrir" + ex);
            errorConfig = true;
        }

        Properties archivop = new Properties();

        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (IOException e) {
            Mensajes.mostrarExcepcion(e);
            errorConfig = true;
        }

        spanf = Double.parseDouble(archivop.getProperty("spanfd"));
        spanp = Double.parseDouble(archivop.getProperty("spanpd"));
        spanv = Double.parseDouble(archivop.getProperty("spanvd"));
        ruidomotor = Double.parseDouble(archivop.getProperty("cero_fuerzas"));
        System.out.println("spanfd " + spanf + " spanpd " + spanp + " spanvd " + spanv);

        puerto = new PuertoRS232();

        try {
            puerto.connect(puertotarjeta);
            comandoSTOP();
        } catch (Exception e) {
            Mensajes.mostrarExcepcion(e);
            errorConfig = true;
        }
    }

    public void LeePaso() {
        try {
            if ((line = config.readLine()).startsWith("salidas:")) {
                salidaalta = Byte.parseByte(line.substring(line.indexOf(" ") + 1, line.indexOf(",") - 1), 2);
                salidabaja = Byte.parseByte(line.substring(line.indexOf(",") + 1, line.length()), 2);
            }
            if ((line = config.readLine()).startsWith("tiempo:")) {
                tiempo_paso = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            }
        } catch (IOException ex) {
            Mensajes.mostrarExcepcion(ex);
            System.out.println("Se perdio de lineas");
        }
        temp++;
        System.out.println("LeePaso: " + temp);
    }

    public void comandoFREN() {
        try {
            puerto.out.write('H');
            puerto.out.write('A');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de frenometro");
        }
    }

    public void comandoSTOP() {
        try {
            puerto.out.write('H');
            puerto.out.write('B');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de STOP");
        }
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
    }

    public void EnviaSalidas(int a, int b) {
        try {
            puerto.out.write('H');
            puerto.out.write('G');
            puerto.out.write(a);
            puerto.out.write(b);
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error al cargar el valor de las salidas");
        }
    }

    public void comandoCONEX() {
        try {
            puerto.out.write('H');
            puerto.out.write('J');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de conexión a la tarjeta");
        }
    }

    @SuppressWarnings("static-access")
    public boolean PruebaConexion() {
        String respuesta;
        comandoCONEX();
        try {
            Thread.currentThread().sleep(200);
            puerto.in.read(buffer);
        } catch (InterruptedException | IOException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        return respuesta.startsWith("HDAQ1W");
    }

    public void CalibrarCeros(boolean a, boolean b, boolean c) {
        if (a) {
            valcalcero1 = CalcularMedia(Datos1) + ruidomotor;
            System.out.println("cero fuerza: " + (valcalcero1 * spanf) + " N");
        }
        Datos1.clear();
        if (b) {
            valcalcero2 = CalcularMedia(Datos2);
            System.out.println("cero peso: " + (valcalcero2 * spanp) + " N");
        }
        Datos2.clear();
        if (c) {
            valcalcero3 = CalcularMedia(Datos3);
            System.out.println("cero velocidad: " + (valcalcero3 * spanv) + " Km/h");
        }
        Datos3.clear();
    }

    public void MedirFuerza() {
        //double ab = CalcularMaximo(Datos1);
        double ab;
        int bc;
        long s;
        armonizar();
        filtrar(Datos1);
        ab = CalcularMaximo2();

        s = Datos1.size();
        for (t = 0; t < s; t++) {
            System.out.println(Datos1.get(t) + "," + Datosfil.get(t));
        }

        if ((ab - valcalcero1) < 0) {
            fuerzas.add(0.0);
        } else {
            fuerzas.add(ab - valcalcero1);
        }
        bc = fuerzas.size();
        System.out.println("---------------------------------------------------------tamaño de los ejes : " + bc);
        System.out.println("de nuevo el cero de fuerza " + (valcalcero1 * spanf) + " N");
        System.out.println("fuerza sin calibrar " + ejemedido + ": " + (ab * spanf) + " N");
        System.out.println("fuerza del eje " + ejemedido + ": " + (fuerzas.get(bc - 1) * spanf) + " N");
        Datos1.clear();
        Datosfil.clear();
    }

    public void MedirPeso() {
        double ab = CalcularMediana(Datos2);
        int bc;
        pesos.add(ab - valcalcero2);
        bc = pesos.size();
        System.out.println("de nuevo el cero de peso " + (valcalcero2 * spanp) + " N");
        System.out.println("peso sin calibrar " + ejemedido + ": " + (ab * spanp) + " N");
        System.out.println("peso del eje " + ejemedido + ": " + (pesos.get(bc - 1) * spanp) + " N");
        Datos2.clear();
    }

    public void MedirVelocidad() {
        double ab = CalcularMedia(Datos3);
        velocidades.add(ab - valcalcero3);
        System.out.println("de nuevo el cero de velocidad " + (valcalcero3 * spanv) + " Km/h");
        System.out.println("velocidad sin calibrar " + ejemedido + ": " + (ab * spanv) + " Km/h");
        System.out.println("velocidad del eje " + ejemedido + ": " + (velocidades.get(0) * spanv) + " Km/h");
        Datos3.clear();
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
        return c;
    }

    public double CalcularMediana(List<Integer> Datos) {
        int s, r, auxMediana;
        double c;
        s = Datos.size();
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

    public double CalcularMaximo(List<Integer> Datos) {
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

    public double CalcularMaximo2() {
        long s;
        double max = 0;
        s = Datosfil.size();
        for (t = 0; t < s; t++) {
            if (Datosfil.get(t) > max) {
                max = Datosfil.get(t);
            }
        }
        return max;
    }

    public double CalcularDesvEst(List<Integer> Datos) {
        long s;
        double sum = 0, desvest, media;
        media = CalcularMedia(Datos);
        s = Datos.size();
        for (t = 0; t < s; t++) {
            sum += Math.pow(((double) Datos.get(t) - media), 2);
        }
        desvest = sum / (s - 1);
        return desvest;
    }

    public void filtrar(List<Integer> Datos) {
        int s, n, p, q;
        double y;
        int diezmado = 16;
        s = Datos.size();
        n = filtro.length;
        for (p = 0; p < s; p++) {
            y = 0;
            for (q = 0; q < n; q++) {
                if ((p - q) >= 0) {
                    y += filtro[q] * Datos.get(p - q);
                }
            }
            Datosfil.add(y);
        }
    }

    public void armonizar() {
        int s;
        double pendientesig = 0, pendienteant = 0;
        s = Datos1.size();
        for (t = 0; t < s; t++) {
            if (t == 0) {
                if (valcalcero1 == 0) {
                    pendienteant = Datos1.get(t);
                } else {
                    pendienteant = (Datos1.get(t) - valcalcero1) / valcalcero1;
                }
            } else if (t == (s - 1)) {
                if (valcalcero1 == 0) {
                    pendientesig = Datos1.get(t);
                } else {
                    pendientesig = (Datos1.get(t) - valcalcero1) / valcalcero1;
                }
            } else {
                if (Datos1.get(t - 1) == 0) {
                    pendienteant = Datos1.get(t);
                } else {
                    pendienteant = (Datos1.get(t) - Datos1.get(t - 1)) / Datos1.get(t - 1);
                }
                if (Datos1.get(t + 1) == 0) {
                    pendientesig = Datos1.get(t);
                } else {
                    pendientesig = (Datos1.get(t) - Datos1.get(t + 1)) / Datos1.get(t + 1);
                }
            }
            if (Math.abs(pendienteant) > 1 && Math.abs(pendientesig) > 1) {  //Se comprueba si la pendiente de cambio
                //del punto actual respecto al anterior y al siguiente es mayor al 100 %
                if (t == 0) {
                    Datos1.set(t, Datos1.get(t + 1));
                } else if (t == (s - 1)) {
                    Datos1.set(t, Datos1.get(t - 1));
                } else {
                    Datos1.set(t, (Datos1.get(t - 1) + Datos1.get(t + 1)) / 2);
                }
            }
        }
    }

    public void CapturarDatos() {
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

    void RegistrarMedidas() {
        FrenosMotos frenos = new FrenosMotos();
        frenos.setFuerzaDelantera(Math.round(fuerzas.get(0) * spanf));
        frenos.setFuerzaTrasera(Math.round(fuerzas.get(1) * spanf));
        frenos.setPesoDelantero(Math.round(pesos.get(0) * spanp));
        frenos.setPesoTrasero(Math.round(pesos.get(1) * spanp));

        PruebaDefaultDAO frenosDAO = new PruebaDefaultDAO();
        try {
            System.out.println("estoy dentro del metodo registar Medidas: ");
            PruebaDefaultDAO.escrTrans = "@";
            try {            
                repetirPrueba = frenosDAO.persist(frenos, idPrueba, idUsuario,aplicTrans,this.ipEquipo,"Moto","Moto","");
            } catch (ClassNotFoundException ex) {    }
            if (repetirPrueba == false) {
                
                tramaAuditoria = "{\"eficaciaTotal\":\""
                        .concat(String.valueOf(eficacia))
                        .concat("\",").concat("\"eficaciaAuxiliar\":\"")
                        .concat(String.valueOf(" ")).concat("\",");
                
                tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje")
                        .concat(String.valueOf(0 + 1))
                        .concat("Izquierdo\":\"")
                        .concat(String.valueOf(" "))
                        .concat("\",").concat("\"pesoEje")
                        .concat(String.valueOf(0 + 1))
                        .concat("Izquierdo\":\"")
                        .concat(String.valueOf(" "))
                        .concat("\",").concat("\"fuerzaEje")
                        .concat(String.valueOf(0 + 1))
                        .concat("Derecho\":\"")
                        .concat(String.valueOf(frenos.getFuerzaDelantera()))
                        .concat("\",").concat("\"pesoEje")
                        .concat(String.valueOf(0 + 1))
                        .concat("Derecho\":\"")
                        .concat(String.valueOf(frenos.getPesoDelantero()))
                        .concat("\",").concat("\"eje")
                        .concat(String.valueOf(0 + 1))
                        .concat("Desequilibrio\":\"")
                        .concat(String.valueOf(" "))
                        .concat("\",");
                
                tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje")
                        .concat(String.valueOf(2))
                        .concat("Izquierdo\":\"")
                        .concat(String.valueOf(" "))
                        .concat("\",").concat("\"pesoEje")
                        .concat(String.valueOf(2))
                        .concat("Izquierdo\":\"")
                        .concat(String.valueOf(" "))
                        .concat("\",").concat("\"fuerzaEje")
                        .concat(String.valueOf(2))
                        .concat("Derecho\":\"")
                        .concat(String.valueOf(frenos.getFuerzaTrasera()))
                        .concat("\",").concat("\"pesoEje")
                        .concat(String.valueOf(2))
                        .concat("Derecho\":\"")
                        .concat(String.valueOf(frenos.getPesoTrasero()))
                        .concat("\",").concat("\"eje")
                        .concat(String.valueOf(2))
                        .concat("Desequilibrio\":\"")
                        .concat(String.valueOf(" ")).concat("\",");

                for (int k = 3; k < 6; k++) {
                    tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje")
                            .concat(String.valueOf(k))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",").concat("\"pesoEje")
                            .concat(String.valueOf(k))
                            .concat("Izquierdo\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",").concat("\"fuerzaEje")
                            .concat(String.valueOf(k))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",").concat("\"pesoEje")
                            .concat(String.valueOf(k))
                            .concat("Derecho\":\"")
                            .concat(String.valueOf(" "))
                            .concat("\",").concat("\"eje")
                            .concat(String.valueOf(k))
                            .concat("Desequilibrio\":\"")
                            .concat(String.valueOf(" ")).concat("\",");
                }
                tramaAuditoria = tramaAuditoria
                        .concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"")
                        .concat(String.valueOf(idPrueba))
                        .concat("\"}");
            }
        } catch (NoPersistException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
    }

    private void registrarCancelacion(String comentario) throws ClassNotFoundException, SQLException {

        PruebasDAO pruebasDAO = new PruebasDAO();
        Date d = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        pruebasDAO.cancelarPrueba(comentario, idPrueba, idUsuario, new java.sql.Date(c.getTimeInMillis()));
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
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        BotonEnviarCancelacion = new javax.swing.JButton();
        PanelTitulos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        LabelPrueba = new javax.swing.JLabel();
        lblCtxPrueba = new javax.swing.JLabel();
        LabelAviso = new javax.swing.JLabel();
        PanelMensajes = new javax.swing.JPanel();
        LabelEje = new javax.swing.JLabel();
        LabelInfo = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        led1 = new eu.hansolo.steelseries.extras.Led();
        BotonEmpezar = new javax.swing.JButton();
        BotonContinuar = new javax.swing.JButton();
        BotonFinalizar = new javax.swing.JButton();
        BotonCancelar = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();

        dialogCancelacion.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dialogCancelacion.setTitle("CANCELACION DE PRUEBA");
        dialogCancelacion.setMinimumSize(new java.awt.Dimension(630, 320));
        dialogCancelacion.setModal(true);
        dialogCancelacion.setResizable(false);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Por favor describa el motivo de la cancelación de la prueba:");

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
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)))
                    .addGroup(dialogCancelacionLayout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(BotonEnviarCancelacion)))
                .addContainerGap())
        );
        dialogCancelacionLayout.setVerticalGroup(
            dialogCancelacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogCancelacionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BotonEnviarCancelacion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(1024, 91));

        LabelPrueba.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        LabelPrueba.setForeground(new java.awt.Color(0, 102, 255));
        LabelPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelPrueba.setText("PRUEBA PARA MOTOS");
        LabelPrueba.setToolTipText("");
        LabelPrueba.setMinimumSize(new java.awt.Dimension(541, 30));
        LabelPrueba.setPreferredSize(new java.awt.Dimension(1024, 145));

        lblCtxPrueba.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        lblCtxPrueba.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCtxPrueba.setToolTipText("");
        lblCtxPrueba.setMinimumSize(new java.awt.Dimension(541, 30));
        lblCtxPrueba.setOpaque(true);
        lblCtxPrueba.setPreferredSize(new java.awt.Dimension(1024, 145));

        javax.swing.GroupLayout PanelTitulosLayout = new javax.swing.GroupLayout(PanelTitulos);
        PanelTitulos.setLayout(PanelTitulosLayout);
        PanelTitulosLayout.setHorizontalGroup(
            PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LabelPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(lblCtxPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 1014, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelTitulosLayout.setVerticalGroup(
            PanelTitulosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(PanelTitulosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCtxPrueba, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        LabelAviso.setFont(new java.awt.Font("Andalus", 0, 36)); // NOI18N
        LabelAviso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelAviso.setOpaque(true);
        LabelAviso.setPreferredSize(new java.awt.Dimension(280, 116));

        LabelEje.setFont(new java.awt.Font("Andalus", 0, 48)); // NOI18N
        LabelEje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelEje.setText("Eje");
        LabelEje.setPreferredSize(new java.awt.Dimension(700, 100));

        LabelInfo.setFont(new java.awt.Font("Andalus", 0, 47)); // NOI18N
        LabelInfo.setText("Iniciando Sistema...");
        LabelInfo.setPreferredSize(new java.awt.Dimension(700, 142));

        jLabel5.setFont(new java.awt.Font("Andalus", 0, 25)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Presencia");

        led1.setEnabled(false);
        led1.setMaximumSize(new java.awt.Dimension(99, 99));
        led1.setMinimumSize(new java.awt.Dimension(99, 99));

        javax.swing.GroupLayout led1Layout = new javax.swing.GroupLayout(led1);
        led1.setLayout(led1Layout);
        led1Layout.setHorizontalGroup(
            led1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        led1Layout.setVerticalGroup(
            led1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        BotonEmpezar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonEmpezar.setForeground(new java.awt.Color(0, 102, 0));
        BotonEmpezar.setText("EMPEZAR");
        BotonEmpezar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonEmpezar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEmpezarActionPerformed(evt);
            }
        });

        BotonContinuar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonContinuar.setText("CONTINUAR");
        BotonContinuar.setEnabled(false);
        BotonContinuar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonContinuarActionPerformed(evt);
            }
        });

        BotonFinalizar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonFinalizar.setText("FINALIZAR");
        BotonFinalizar.setEnabled(false);
        BotonFinalizar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonFinalizarActionPerformed(evt);
            }
        });

        BotonCancelar.setFont(new java.awt.Font("Andalus", 0, 24)); // NOI18N
        BotonCancelar.setForeground(new java.awt.Color(102, 0, 0));
        BotonCancelar.setText("CANCELAR");
        BotonCancelar.setPreferredSize(new java.awt.Dimension(256, 60));
        BotonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarActionPerformed(evt);
            }
        });

        jProgressBar1.setFont(new java.awt.Font("Andalus", 0, 60)); // NOI18N
        jProgressBar1.setForeground(new java.awt.Color(255, 0, 0));
        jProgressBar1.setMaximum(10);
        jProgressBar1.setPreferredSize(new java.awt.Dimension(1024, 95));
        jProgressBar1.setStringPainted(true);

        javax.swing.GroupLayout PanelMensajesLayout = new javax.swing.GroupLayout(PanelMensajes);
        PanelMensajes.setLayout(PanelMensajesLayout);
        PanelMensajesLayout.setHorizontalGroup(
            PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMensajesLayout.createSequentialGroup()
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 720, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(LabelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BotonEmpezar, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelMensajesLayout.setVerticalGroup(
            PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMensajesLayout.createSequentialGroup()
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(PanelMensajesLayout.createSequentialGroup()
                                .addComponent(BotonFinalizar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(2, 2, 2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMensajesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(BotonEmpezar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(BotonContinuar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PanelMensajesLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(BotonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelAviso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelMensajes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelTitulos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelMensajes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    Timer timerautomatico2 = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            numtimerautomatico2++;
            if (numtimerautomatico2 == 7) {
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

    Timer timerempezar = new Timer(2500, new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            BotonEmpezarActionPerformed(e);
        }
    });

    Timer timer2 = new Timer(500, new ActionListener() {
        @SuppressWarnings("static-access")
        @Override
        public void actionPerformed(ActionEvent e) {
            numtimer2++;
            LabelAviso.setText("RODILLOS");
            if (numtimer2 != 1) {
                jProgressBar1.setValue(conteoreg + 1);
                jProgressBar1.setString((conteoreg + 1) * 10 + "%");
                CapturarDatos();
                if (isCanal0()) {
                    conteoreg++;
                } else {
                    conteoreg = 0;
                    LabelInfo.setText("Por favor mantenga el eje sobre los rodillos");
                }
                if (LabelAviso.getBackground() == PanelTitulos.getBackground()) {
                    LabelAviso.setBackground(Color.YELLOW);
                } else {
                    LabelAviso.setBackground(PanelTitulos.getBackground());
                }
                if (conteoreg == 10) {
                    conteoreg = 0;
                    LabelAviso.setText("");
                    LabelAviso.setBackground(PanelTitulos.getBackground());
                    t2enabled(false);
                    LeePaso();
                    if (tiempo_paso == 0) {
                        JOptionPane.showMessageDialog(DlgFrenoMoto.this, "Error en el archivo de configuración", "SART 1.7.2",
                                JOptionPane.ERROR_MESSAGE);
                        t3enabled(false);
                        comandoSTOP();
                        puerto.close();
                        dispose();
                    } else {
                        LabelInfo.setText("Moviendo rodillos a baja velocidad");
                        try {
                            Thread.currentThread().sleep(100);
                        } catch (InterruptedException ex) {
                            Mensajes.mostrarExcepcion(ex);
                        }
                        comandoFREN();
                        hilo1.start();
                        t3enabled(true);
                        EnviaSalidas(salidaalta, salidabaja);
                    }
                }
            }
        }
    });

    public void t2enabled(boolean a) {
        if (a) {
            this.timer2.start();
        } else {
            this.timer2.stop();
            comandoSTOP();
        }
    }
    Timer timer3 = new Timer(500, new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            numtimer3++;
            if (numtimer3 % 2 == 0) {
                paso1s = true;
            }
        }
    });

    public void t3enabled(boolean a) {
        if (a) {
            this.timer3.start();
        } else {
            this.timer3.stop();
            comandoSTOP();
        }
    }

    Timer timer4 = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            LabelInfo.setText("Midiendo peso...");
        }
    });

    Timer timeraviso = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Color C = LabelAviso.getBackground();
            if (pasoactual == 0 || pasoactual == 5) {
                if (hilo2.isAlive()) {
                    if (C == PanelTitulos.getBackground()) {
                        LabelAviso.setBackground(Color.YELLOW);
                    } else {
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                    }
                } else if (hilo3.isAlive() || hilo4.isAlive()) {
                    if (C == PanelTitulos.getBackground()) {
                        LabelAviso.setBackground(Color.GREEN);
                    } else {
                        LabelAviso.setBackground(PanelTitulos.getBackground());
                    }
                }
            } else if (pasoactual == 2 || pasoactual == 7) {
                if (C == PanelTitulos.getBackground()) {
                    LabelAviso.setBackground(Color.RED);
                    LabelAviso.setIcon(imageOn);
                    LabelAviso.setForeground(Color.white);
                } else {
                    LabelAviso.setIcon(imageOff);
                    LabelAviso.setForeground(Color.BLACK);
                    LabelAviso.setBackground(PanelTitulos.getBackground());
                }
            }
        }
    });
    Timer timerautomatico = new Timer(3000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            BotonContinuar.doClick();
        }
    });

    @SuppressWarnings("static-access")
    private void BotonEmpezarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEmpezarActionPerformed
        // TODO add your handling code here:
        int opcion;
        comandoSTOP();
        if (!PruebaConexion()) {
            opcion = JOptionPane.showOptionDialog(this, "La tarjeta no se encuentra conectada o inicializada."
                    + " Desea conectarla", "Configuración", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (opcion == 0) {
                Tarjeta tar = new Tarjeta();
                tar.setPuerto(puerto);
                tar.IniciaTarjeta();
                if (tar.getNumconex() == 3) {
                    JOptionPane.showMessageDialog(null, "Llame a servicio técnico",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    puerto.close();
                    dispose();
                } else {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException ex) {
                        Mensajes.mostrarExcepcion(ex);
                    }
                    BotonEmpezar.doClick();
                }
            } else {
                puerto.close();
                dispose();
            }
        } else {
            BotonEmpezar.setEnabled(false);
            LabelEje.setText("Eje " + ejemedido);
            timerautomatico2.start();
            JOptionPane.showMessageDialog(this, "Por Favor Asegurese de que la plancha este libre", "Precaución",
                    JOptionPane.WARNING_MESSAGE);
            LabelInfo.setText("CALIBRANDO CERO...!");            
            comandoFREN();
            t1enabled(true);
        }
    }//GEN-LAST:event_BotonEmpezarActionPerformed

    private void BotonContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonContinuarActionPerformed
        // TODO add your handling code here:
        BotonContinuar.setEnabled(false);
        //JOptionPane.showMessageDialog(this,"Asegurese de que el eje "+ejemedido+" este sobre la plancha","Precaución",
        //    JOptionPane.WARNING_MESSAGE);
        LabelInfo.setText("<html>Avance la motocicleta hasta que el eje "  +/*" <br>" +*/ " este sobre la plancha</html>");
        comandoFREN();
        hilo3.start();
        //timer4.setRepeats(false);
        //timer4.start();
        //t1enabled(true);
    }//GEN-LAST:event_BotonContinuarActionPerformed

    private void BotonFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonFinalizarActionPerformed
        // TODO add your handling code here:
        //double fuerzadel, fuerzatra, pesodel, pesotra;
        comandoSTOP();
        puerto.close();
        double fuerzat = 0, pesot = 0;
        if (pasoactual == 10) {
            long s = fuerzas.size();            
            for (t = 0; t < s; t++) {
                pesot += pesos.get(t);
                fuerzat += fuerzas.get(t);
                System.out.println("peso " + (pesos.get(t) * spanp) + " fuerza " + (fuerzas.get(t) * spanf));
            }
            System.out.println("valores totales peso " + (pesot * spanp) + " fuerza " + (fuerzat * spanf));
            /*fuerzadel=fuerzas.get(0)*spanf;    //Valor de la fuerza de frenado maxima delantera
             fuerzatra=fuerzas.get(1)*spanf;    //Valor de la fuerza de frenado maxima trasera
             pesodel=pesos.get(0)*spanp;        //Valor del peso del eje delantero
             pesotra=pesos.get(1)*spanp;        //Valor del peso del eje trasero*/
            //eficacia = Math.round((Math.round(fuerzat * spanf) / Math.round(pesot * spanp)) * 10000)/100; formula general, al dejarla la eficacia aparecia en 0.0 asi que se descompuso en mas operaciones abajo
            double bandera1 = Math.round(fuerzat * spanf);
            double bandera2 = Math.round(pesot * spanp);
            eficacia = Math.round(( bandera1/bandera2 ) * 10000);
            eficacia= (eficacia)/100;
            System.out.println("la eficacia total del sistema de frenos fue de: " + eficacia + " %");
            if (eficacia > 100) {
                Object[] choices = {"Aceptar"};
//                int dato = JOptionPane.showOptionDialog(null, "<html><div><center><img src='file:images/flag-red-icon.png' alt='algo'/><h2 style='font-family: \"Open Sans Condensed Light\"; color:#069'>Falla en el proceso</h2><hr/><p align='justify' style='font-family: 'Open Sans Condensed Light'; font-size: 15px; '>Eficacia superior a el 100%, debe repetir la prueba</p><hr/><br/></center></div></html>", "FALLA EN EL PROCESO", 0, -1, null, choices, choices[0]);
                int dato = JOptionPane.showOptionDialog(null, "<html><div><center><img src='file:images/flag-red-icon.png' alt='algo'/><h2 style='font-family: \"Open Sans Condensed Light\"; color:#069'>Falla en el proceso</h2><hr/><p align='justify' style='font-family: 'Open Sans Condensed Light'; font-size: 15px; '>Debe repetir la prueba</p><hr/><br/></center></div></html>", "FALLA EN EL PROCESO", 0, -1, null, choices, choices[0]);
//            Mensajes.mensajeError("Eficacia superior a el 100% debe volver hacer la prueba");
                repetirPrueba = true;
                this.dispose();
                return;
            }

            RegistrarMedidas();
        } else {
            System.out.println(" validacion de paso 10 no se dio is "+pasoactual);
        }
        this.dispose();
    }//GEN-LAST:event_BotonFinalizarActionPerformed

    private void BotonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarActionPerformed
        // TODO add your handling code here:
        if (puerto != null) {
            comandoSTOP();
        }
        Thread tmpBlinker = hilo3;
        hilo3 = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
        tmpBlinker = hilo4;
        hilo4 = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
        hilo1 = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
        tmpBlinker = hilo2;
        hilo2 = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
        t1enabled(false);
        t2enabled(false);
        t3enabled(false);
        //timer4.stop();
        timeraviso.stop();
        System.out.println("Prueba cancelada");
        puerto.close();
        //SART-27 No solicita usuario y clave para cancelar prueba de frenos en motos
        JXLoginPane.Status status = UtilLogin.loginAdminDBCDA();//muestra el dialogo para el login de la aplicacion, solamente usuario administrador

        if (status == JXLoginPane.Status.SUCCEEDED) {
            dialogCancelacion.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Autenticacion fallida");
        }
    }//GEN-LAST:event_BotonCancelarActionPerformed

    private void BotonEnviarCancelacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEnviarCancelacionActionPerformed
        // TODO add your handling code here:
        motivoCancelacion = jTextArea1.getText();
        try {
            registrarCancelacion(motivoCancelacion);
        } catch (ClassNotFoundException | SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        System.out.println("La prueba fue cancelada por: " + motivoCancelacion);
        dialogCancelacion.dispose();
        this.dispose();

    }//GEN-LAST:event_BotonEnviarCancelacionActionPerformed
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

    /*public static int binToDec(String a){
     int valor;
     StringBuffer b = new StringBuffer(a);

     return valor;
     }*/
    public boolean isCanal0() {
        return canal0;
    }

    public void setCanal0(boolean canal0) {
        this.canal0 = canal0;
    }

    public void setIdPrueba(int idPrueba) {
        this.idPrueba = idPrueba;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DlgFrenoMoto dialog = new DlgFrenoMoto(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel PanelMensajes;
    private javax.swing.JPanel PanelTitulos;
    private javax.swing.JDialog dialogCancelacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblCtxPrueba;
    private eu.hansolo.steelseries.extras.Led led1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        numtimer1++;
        if (numtimer1 == 1) {
            jProgressBar1.setMaximum(10);
        }
        jProgressBar1.setValue(numtimer1);
        jProgressBar1.setString(numtimer1 * 10 + "%");
        CapturarDatos();
        if (numtimer1 == 10) {
            t1enabled(false);
            numtimer1 = 0;
            if (pasomedfren == 0) {
                CalibrarCeros(true, true, true);
                lblCtxPrueba.setText("USER: "+ DlgFrenoMoto.NombreUsr+ "; PLACA: "+ DlgFrenoMoto.Placa );
                LabelInfo.setText("SISTEMA CALIBRADO A CERO..!");
                BotonContinuar.setEnabled(true);
                timerautomatico.setRepeats(false);
                timerautomatico.start();
                pasomedfren++;
            } else {
                MedirPeso();
                Datos2.clear();
                Datos3.clear();
                LabelInfo.setText("<html>Por favor avance la motocicleta hasta que el eje " + ejemedido
                        +/*" <br>" +*/ " este sobre los rodillos</html>");
                comandoFREN();
                t2enabled(true);
            }
        }
    }

    class TFrenmotos extends Thread {

        private DlgFrenoMoto a;

        public void setA(DlgFrenoMoto a) {
            this.a = a;
        }

        @SuppressWarnings("static-access")
        @Override
        public void run() {
            try {
                //se ejecuta mientras haya pasos que ejecutar
                while (pasoactual != numpasos) {
                    if (paso1s == true) {
                        paso1s = false;
                        CapturarDatos();
                        // <editor-fold desc="Se determina si el eje esta sobre el rodillo durante la prueba">
                        if (!isCanal0() && (pasoactual != 4 && pasoactual != 9)) {
                            //A excepción de los pasos en que se debe avanzar de los rodillos
                            LabelAviso.setBackground(PanelTitulos.getBackground());
                            if (pasoactual == 2 || pasoactual == 3 || pasoactual == 7 || pasoactual == 8) {
                                //Se puede dar el caso de que salga la motocicleta por la fuerza de frenado
                                //en los pasos de medir fuerza de frenado y soltar la motocicleta
                                //pero no involucran error en la prueba
                                numtimer3 = tiempo_paso / 500;
                            } else {
                                LabelInfo.setText("El eje se salio del rodillo");
                                led1.setLedOn(false);
                                timeraviso.stop();
                                LabelAviso.setText("");
                                LabelAviso.setBackground(PanelTitulos.getBackground());
                                comandoSTOP();
                                this.sleep(2000);
                                t3enabled(false);
                                LabelInfo.setText("Prueba abortada. Presione finalizar");
                                BotonFinalizar.setEnabled(true);
                                comandoSTOP();
                                break;
                            }
                            //pasoactual=numpasos;
                            //numtimer3=tiempo_paso/500;
                        }
                        // </editor-fold>
                        // <editor-fold desc="Se determina si la velocidad baja a un nivel umbral">
                        if (pasoactual == 2 && ((double) ComandoRecibido[6] - valcalcero3) <= velminima) {
                            numtimer3 = tiempo_paso / 500;
                        }
                        if (pasoactual == 7 && ((double) ComandoRecibido[6] - valcalcero3) <= velminima) {
                            numtimer3 = tiempo_paso / 500;
                        }
                        if (pasoactual == 2 || pasoactual == 7) {
                            System.out.println("velocidad medida: " + ((double) ComandoRecibido[6] - valcalcero3));
                        }
                        // </editor-fold>
                        if (numtimer3 == tiempo_paso / 500) {
                            numtimer3 = 0;
                            pasoactual++;
                            LeePaso();
                            // <editor-fold desc="Se determina si se escribio un tiempo de 0 en el archivo de configuración">
                            if (tiempo_paso == 0) {
                                JOptionPane.showMessageDialog(DlgFrenoMoto.this, "Error en el archivo de configuración", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                t3enabled(false);
                                comandoSTOP();
                                puerto.close();
                                dispose();
                                // </editor-fold>
                            } else {
                                switch (pasoactual) {
                                    // <editor-fold desc="Se sujeto la moto">
                                    case 1:
                                        System.out.println("Moviendo rodillos");
                                        LabelInfo.setText("Determinando velocidad critica");
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se midio la velocidad critica">
                                    case 2:
                                        System.out.println("Empieza frenado");
                                        velminima = (CalcularMedia(Datos3) - valcalcero3) * umbral;
                                        System.out.println("velocidad minima: " + velminima);
                                        LabelInfo.setText("POR FAVOR VAYA FRENANDO..!");
                                        imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoOn.png"));
                                        imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoOf.png"));
                                        timeraviso.start();
                                        LabelAviso.setText("FRENE");
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se midio la fuerza de frenado">
                                    case 3:
                                        System.out.println("Apagando rodillos");
                                        MedirFuerza();
                                        LabelInfo.setText("FUERZA DE FRENADO MEDIDA...!");
                                        ejemedido = "trasero";
                                        timeraviso.stop();
                                        LabelAviso.setIcon(null);
                                        LabelAviso.setText("");
                                        LabelAviso.setBackground(PanelTitulos.getBackground());
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se apago los rodillos">
                                    case 4:
                                        System.out.println("Pasar a siguiente eje");
                                        LabelInfo.setText("Soltando la motocicleta");
                                        LabelEje.setText("Eje " + ejemedido);
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se solto la moto">
                                    case 5:
                                        System.out.println("Midiendo Peso Trasero");
                                        timer3.stop();
                                        //JOptionPane.showMessageDialog(null,"Asegurese de que el eje "+ejemedido+" este sobre la plancha","Precaución",
                                        //    JOptionPane.WARNING_MESSAGE);
                                        LabelInfo.setText("<html>POR  FAVOR AVANCE LA MOTOCICLETA HASTA EL EJE " + ejemedido
                                                +/*" <br>" +*/ " SOBRE LA PLANCHA</html>");
                                        hilo4.start();
                                        try {
                                            hilo4.join();
                                        } catch (InterruptedException ex) {
                                            Mensajes.mostrarExcepcion(ex);
                                        }
                                        //EsperaPeso();
                                        LabelInfo.setText("Midiendo peso...");
                                        hilo2.start();
                                        try {
                                            hilo2.join();
                                        } catch (InterruptedException ex) {
                                            Mensajes.mostrarExcepcion(ex);
                                        }
                                        timer3.start();
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se sujeto la moto">
                                    case 6:
                                        System.out.println("Moviendo rodillos");
                                        LabelInfo.setText("DETERMINANDO VELOCIDAD CRITICA ..!");
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se midio la velocidad critica">
                                    case 7:
                                        System.out.println("Empieza frenado");
                                        velminima = (CalcularMedia(Datos3) - valcalcero3) * umbral;
                                        System.out.println("velocidad minima: " + velminima);
                                        LabelInfo.setText("POR FAVOR VAYA FRENANDO ..!");
                                        timeraviso.start();
                                        LabelAviso.setText("FRENE");
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se midio la fuerza de frenado">
                                    case 8:
                                        System.out.println("Apagando rodillos");
                                        MedirFuerza();
                                        ejemedido = "trasero";
                                        LabelInfo.setText("FUERZA DE FRENADO MEDIDA ..!");
                                        timeraviso.stop();
                                        LabelAviso.setText("");
                                        LabelAviso.setBackground(PanelTitulos.getBackground());
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se apago los rodillos">
                                    case 9:
                                        System.out.println("Ya casi se acaba");
                                        LabelInfo.setText("Soltando la motocicleta");
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        break;
                                    // </editor-fold>
                                    // <editor-fold desc="Se solto la moto">
                                    case 10:
                                        System.out.println("Se acabo");
                                        LabelInfo.setText("<html>Prueba finalizada. Presione finalizar</center></html>");
                                        Datos1.clear();
                                        Datos2.clear();
                                        Datos3.clear();
                                        comandoSTOPAnalogo();
                                        comandoSTOP();
                                        BotonFinalizar.setEnabled(true);
                                        break;
                                    // </editor-fold>
                                    default:
                                        System.out.println("error en el switch");
                                        break;
                                }
                                EnviaSalidas(salidaalta, salidabaja);
                            }
                        }
                    }
                    this.sleep(50);
                }
                if (BotonFinalizar.isEnabled()) {
                    //pasoactual=0;
                    t3enabled(false);
                    comandoSTOPAnalogo();
                    BotonFinalizar.doClick();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted", e);
            }
        }
    }

    class TFrenmotosPeso2 extends Thread {

        public TFrenmotosPeso2() {
            conteoreg = 0;
            numtimer1 = 0;
        }

        @SuppressWarnings("static-access")
        @Override
        public void run() {
            try {
                for (numtimer1 = 0; numtimer1 < 10; numtimer1++) {
                    if (numtimer1 == 1) {
                        jProgressBar1.setMaximum(10);
                    }
                    jProgressBar1.setValue(numtimer1 + 1);
                    jProgressBar1.setString((numtimer1 + 1) * 10 + "%");
                    CapturarDatos();
                    this.sleep(480);
                }
                MedirPeso();
                LabelAviso.setText("RODILLOS");
                timeraviso.start();
                LabelInfo.setText("<html>Por favor avance la motocicleta hasta que el eje " + ejemedido
                        +/*" <br>" +*/ " este sobre los rodillos</html>");
                conteoreg = 0;
                while (conteoreg < 10) {
                    this.sleep(480);
                    numtimer1++;
                    jProgressBar1.setValue(conteoreg + 1);
                    jProgressBar1.setString((conteoreg + 1) * 10 + "%");
                    if (numtimer1 != 1) {
                        CapturarDatos();
                        if (isCanal0()) {
                            conteoreg++;
                        } else {
                            conteoreg = 0;
                            LabelInfo.setText("Por favor mantenga el eje sobre los rodillos");
                        }
                    }
                }
                LabelAviso.setText("");
                timeraviso.stop();
                LabelInfo.setText("Sujetando Motocicleta");
                LabelAviso.setBackground(PanelTitulos.getBackground());
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted", e);
            }
        }
    }

    class EsperaPeso extends Thread {

        @Override
        public void run() {
            double pesomed;
            try {
                LabelAviso.setText("PLANCHA");
                timeraviso.start();
                while (true) {
                    Thread.sleep(1000);
                    CapturarDatos();
                    pesomed = CalcularMediana(Datos2);
                    System.out.println("peso medido " + ((pesomed - valcalcero2) * spanp) + " N");
                    Datos1.clear();
                    Datos2.clear();
                    Datos3.clear();
                    if (((pesomed - valcalcero2) * spanp) >= umbral_peso) {
                        break;
                    }
                }
                if (ejemedido.equals("Delantero")) {
                    t1enabled(true);
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }
            LabelAviso.setText("");
            timeraviso.stop();
            LabelAviso.setBackground(PanelTitulos.getBackground());
        }
    }

    public int getIdHojaPrueba() {
        return idHojaPrueba;
    }

    public void setIdHojaPrueba(int idHojaPrueba) {
        this.idHojaPrueba = idHojaPrueba;
    }

}
