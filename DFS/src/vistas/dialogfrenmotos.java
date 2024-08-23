/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * dalogfrenmotos.java
 *
 * Created on 20/04/2011, 12:45:11 PM
 */
package vistas;

//import com.mysql.jdbc.PreparedStatement;
import com.soltelec.modulopuc.utilidades.Mensajes;
import dao.PruebaDefaultDAO;
import dao.PruebasDAO;
import excepciones.NoPersistException;
import java.awt.Color;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import modelo.Frenos;
import modelo.FrenosMotos;



/**
 *
 * @author GerenciaDesarrollo
 */
public class dialogfrenmotos extends javax.swing.JDialog implements ActionListener {

    private PuertoRS232 puerto;
    private String puertotarjeta;
    private byte[] buffer = new byte[1024];
    private byte data;
    private int j = 0, i = 0, len = -1, partealta, numtimer1 = 0, numtimer2 = 0, t = 0;
    private int[] ComandoRecibido = new int[9];
    private BufferedReader config;
    private boolean tecladoactivado = false;

    private List<Integer> Datos1 = new ArrayList<>(); //Datos correspondientes a la fuerza de frenado
    private List<Integer> Datos2 = new ArrayList<>(); //Datos correspondientes al peso
    private List<Integer> Datos3 = new ArrayList<>(); //Datos correspondientes al peso
    private double eficacia;

    private double valcalcero1 = 0, valcalcero2 = 0, ruidomotor;
    private byte pasomedfren = 0;
    private List<Double> pesos = new ArrayList<>(); //Valores de peso de cada eje
    private List<Double> fuerzas = new ArrayList<>(); //Valores de las fuerzas de frenado de cada eje
    private List<Double> Datosfil = new ArrayList<>(); //Valores de la salida del filtro
    private double spanf, spanp, spanv;  //valores de span de los canales obtenidos de la calibración
    private int rangob, rangoa;
    private final List<Double> velocidades = new ArrayList<>();
    private String ejemedido = "Delantero";
    private String line;
    private double umbral_peso;
    private double[] filtro = {0.0091, 0.0131, 0.0245, 0.0416, 0.0619, 0.0821, 0.0993, 0.1108,
        0.1149, 0.1108, 0.0993, 0.0821, 0.0619, 0.0416, 0.0245, 0.0131, 0.0091};
    private double valcalcero3 = 0;
    private String motivoCancelacion;
    private EsperaPeso hilo1 = new EsperaPeso();
    private EsperaFuerza hilo2 = new EsperaFuerza();
    private EsperaPeso hilo3 = new EsperaPeso();
    private EsperaFuerza hilo4 = new EsperaFuerza();

    private int idUsuario;
    private int idPrueba;
    private boolean repetirPrueba;
    private BufferedWriter regdatosffd1;
    private BufferedWriter regdatosffd2;
    private BufferedWriter regdatospd1;
    private BufferedWriter regdatospd2;
    ImageIcon imageOn = null;
    ImageIcon imageOff = null;
    public static String tramaAuditoria;
    private int aplicTrans=1;
    private String ipEquipo;
     public static String escrTrans = "";

    /**
     * Creates new form dalogfrenmotos
     */
    public dialogfrenmotos(java.awt.Frame parent, boolean modal, int idPrueba, int idUsuario, int idHojaPruebaLocal,int aplicTrans,String ipEquipo) {
        super(parent, modal);
        initComponents();
        //this.setSize(new JFrame().getMaximumSize());
        configuracion();
        jProgressBar1.setFont(new java.awt.Font("Tahoma", 0, 30));
        tecladoactivado = true;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
         this.aplicTrans= aplicTrans;
        this.ipEquipo=ipEquipo;
    }

    private void configuracion() {
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            while (!config.readLine().startsWith("[DAQ]")) {
            }
            if ((line = config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
            }
            //System.out.println(puertotarjeta);
            while (!config.readLine().startsWith("[FRENOMETRO]")) {
            }
            if ((line = config.readLine()).startsWith("umbral_peso")) {
                umbral_peso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            }
            while (!config.readLine().startsWith("motos:")) {
            }
            //if((line=config.readLine()).startsWith("pasos:"))
            //    numpasos=Byte.parseByte(line.substring(line.indexOf(" ")+1, line.length()));
            //System.out.println("numpasos "+numpasos);
            crearEncavezadosValoresMedidas();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("no se pudo abrir" + ex);
        }
        Properties archivop = new Properties();
        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (Exception e) {
            System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o está protegido");
        }
        spanf = Double.parseDouble(archivop.getProperty("spanfd"));
        spanp = Double.parseDouble(archivop.getProperty("spanpd"));
        spanv = Double.parseDouble(archivop.getProperty("spanvd"));
        rangob = Integer.parseInt(archivop.getProperty("rangob"));
        rangoa = Integer.parseInt(archivop.getProperty("rangoa"));

        puerto = new PuertoRS232();
        try {
            puerto.connect(puertotarjeta);
        } catch (Exception e) {
            Mensajes.mostrarExcepcion(e);
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
                        if (BotonEmpezar.isEnabled()) {
                            BotonEmpezar.doClick();
                        } else if (BotonContinuar.isEnabled()) {
                            BotonContinuar.doClick();
                        } else if (BotonFinalizar.isEnabled()) {
                            BotonFinalizar.doClick();
                        }
                    }
                    tecladoactivado = false;
                }
            }
        });
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
            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        if (respuesta.startsWith("HDAQ1W")) {
            return true;
        } else {
            return false;
        }
    }

    public void CalibrarCeros(boolean a, boolean b) {
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
        System.out.println("el numero de datos es: " + s);
        /*for(t=0;t<s;t++){
         System.out.println(Datos1.get(t)+","+Datosfil.get(t));
         }*/
        if ((ab - valcalcero1) < 0) {
            fuerzas.add(0.0);
        } else {
            fuerzas.add(ab - valcalcero1);
        }
        bc = fuerzas.size();
        System.out.println("de nuevo el cero de fuerza " + (valcalcero1 * spanf) + " N");
        System.out.println("fuerza sin calibrar " + ejemedido + ": " + (ab * spanf) + " N");
        System.out.println("fuerza del eje " + ejemedido + ": " + (fuerzas.get(bc - 1) * spanf) + " N");
        medirValoresMedidasFuerza();
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
        medirValoresMedidasPeso();
        Datos2.clear();
    }

    public double CalcularMedia(List<Integer> Datos) {
        long b = 0, s;
        double c = 0;
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
        //System.out.println("El tamaño de los datos es: "+s);
        if (s == 0) {
            return 0;
        }
        if (s == 1) {
            return (double) Datos.get(0);
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
            c = (double) (Datos.get((s - 1) / 2) + Datos.get(((s - 1) / 2) + 1)) / 2;
        } else {
            c = (double) Datos.get((s - 1) / 2);
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
        s = Datos.size();
        System.out.println("el numero de datos en el filtro es: " + s);
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
                pendienteant = (Datos1.get(t) - valcalcero1) / valcalcero1;
            } else if (t == (s - 1)) {
                pendientesig = (Datos1.get(t) - valcalcero1) / valcalcero1;
            } else {
                pendienteant = (Datos1.get(t) - Datos1.get(t - 1)) / Datos1.get(t - 1);
                pendientesig = (Datos1.get(t) - Datos1.get(t + 1)) / Datos1.get(t + 1);
            }
            if (Math.abs(pendienteant) > 1 && Math.abs(pendientesig) > 1) {
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
                            //canal ignorado en prueba de motos con plancha
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
                        }
                    }
                }
                //setCanal0((ComandoRecibido[1] & 0x01) >0);
            } else {
                comandoFREN();
            }
        } catch (IOException ex) {
            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearEncavezadosValoresMedidas() {
        if (Boolean.parseBoolean(System.getProperty("com.soltelec.detalle.prueba", "false"))) {
            try {
//<editor-fold desc="Creación de los archivos backup para los datos de frenos">                        
                regdatospd1 = new BufferedWriter(new FileWriter(new File("Backup Datos Peso Derecho 1.txt")));
                regdatospd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE DELANTERO");
                regdatospd1.newLine();
                regdatospd1.flush();
                regdatospd2 = new BufferedWriter(new FileWriter(new File("Backup Datos Peso Derecho 2.txt")));
                regdatospd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE PESO DERECHO DEL EJE TRASERO");
                regdatospd2.newLine();
                regdatospd2.flush();

                regdatosffd1 = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos Derecha 1.txt")));
                regdatosffd1.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DERECHA DEL EJE DELANTERO");
                regdatosffd1.newLine();
                regdatosffd1.flush();
                regdatosffd2 = new BufferedWriter(new FileWriter(new File("Backup Datos Frenos Derecha 2.txt")));
                regdatosffd2.write("  BITACORA DE LOS DATOS DE LA PRUEBA DE FUERZA DE FRENADO DERECHA DEL EJE TRASERO");
                regdatosffd2.newLine();
                regdatosffd2.flush();
            } catch (IOException ex) {
                System.out.println("no se pudo crear el archivo de datos" + ex);
            }
        }
    }

    private void medirValoresMedidasFuerza() {
        if (Boolean.parseBoolean(System.getProperty("com.soltelec.detalle.prueba", "false"))) {
            int s = Datosfil.size();

            try {
                if (ejemedido.equals("Delantero")) {
                    regdatosffd1.write("el cero de fuerza derecha es: " + valcalcero1);
                    regdatosffd1.newLine();
                    regdatosffd1.write("el span de fuerza derecha es: " + spanf);
                    regdatosffd1.newLine();
                    regdatosffd1.flush();
                    for (t = 0; t < s; t++) {
                        regdatosffd1.write(Datosfil.get(t).toString() + "  " + Datos1.get(t));
                        regdatosffd1.newLine();
                    }
                    regdatosffd1.close();
                } else {
                    regdatosffd2.write("el cero de fuerza derecha es: " + valcalcero1);
                    regdatosffd2.newLine();
                    regdatosffd2.write("el span de fuerza derecha es: " + spanf);
                    regdatosffd2.newLine();
                    regdatosffd2.flush();
                    for (t = 0; t < s; t++) {
                        regdatosffd2.write(Datosfil.get(t).toString() + "  " + Datos1.get(t));
                        regdatosffd2.newLine();
                    }
                    regdatosffd2.close();
                }
            } catch (IOException ex) {
                System.out.println("no se pudo crear el archivo de datos" + ex);
            }
        }
    }

    private void medirValoresMedidasPeso() {
        if (Boolean.parseBoolean(System.getProperty("com.soltelec.detalle.prueba", "false"))) {
            int s = Datosfil.size();
            try {
                if (ejemedido.equals("Delantero")) {
                    regdatospd1.write("el cero de peso derecho es: " + valcalcero2);
                    regdatospd1.newLine();
                    regdatospd1.write("el span de peso derecho es: " + spanp);
                    regdatospd1.newLine();
                    regdatospd1.flush();
                    for (t = 0; t < s; t++) {
                        regdatospd1.write(Datos2.get(t).toString());
                        regdatospd1.newLine();
                    }
                    regdatospd1.close();
                } else {
                    regdatospd2.write("el cero de peso derecho es: " + valcalcero2);
                    regdatospd2.newLine();
                    regdatospd2.write("el span de peso derecho es: " + spanp);
                    regdatospd2.newLine();
                    regdatospd2.flush();
                    for (t = 0; t < s; t++) {
                        regdatospd2.write(Datos2.get(t).toString());
                        regdatospd2.newLine();
                    }
                    regdatospd2.close();
                }

            } catch (IOException ex) {
                System.out.println("no se pudo crear el archivo de datos" + ex);
            }
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
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        BotonEnviarCancelacion = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        PanelBotones = new javax.swing.JPanel();
        BotonEmpezar = new javax.swing.JButton();
        BotonContinuar = new javax.swing.JButton();
        BotonFinalizar = new javax.swing.JButton();
        BotonCancelar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        LabelEje = new javax.swing.JLabel();
        LabelAviso = new javax.swing.JLabel();

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

        jLabel2.setFont(new java.awt.Font("Showcard Gothic", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("PRUEBA DE FRENOMETRO PARA MOTOCICLETAS");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 999, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(95, 95, 95))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                .addContainerGap())
        );

        jProgressBar1.setFont(new java.awt.Font("Tahoma", 0, 45)); // NOI18N
        jProgressBar1.setForeground(new java.awt.Color(255, 0, 51));
        jProgressBar1.setMaximum(10);
        jProgressBar1.setStringPainted(true);

        PanelBotones.setLayout(new java.awt.GridLayout(1, 0));

        BotonEmpezar.setFont(new java.awt.Font("Imprint MT Shadow", 1, 24)); // NOI18N
        BotonEmpezar.setText("EMPEZAR PRUEBA");
        BotonEmpezar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEmpezarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonEmpezar);

        BotonContinuar.setFont(new java.awt.Font("Imprint MT Shadow", 1, 24)); // NOI18N
        BotonContinuar.setText("CONTINUAR PRUEBA");
        BotonContinuar.setEnabled(false);
        BotonContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonContinuarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonContinuar);

        BotonFinalizar.setFont(new java.awt.Font("Imprint MT Shadow", 1, 24)); // NOI18N
        BotonFinalizar.setText("FINALIZAR PRUEBA");
        BotonFinalizar.setEnabled(false);
        BotonFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonFinalizarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonFinalizar);

        BotonCancelar.setFont(new java.awt.Font("Imprint MT Shadow", 1, 24)); // NOI18N
        BotonCancelar.setText("CANCELAR PRUEBA");
        BotonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonCancelar);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1337, Short.MAX_VALUE)
            .addComponent(PanelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, 1337, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(PanelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 0, 48)); // NOI18N
        jLabel3.setText("Iniciando Sistema...");

        LabelEje.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        LabelEje.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelEje.setText("Eje");

        LabelAviso.setFont(new java.awt.Font("Comic Sans MS", 0, 36)); // NOI18N
        LabelAviso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelAviso.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 1098, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 716, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(LabelAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 1315, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelAviso, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelEje, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    Timer timeraviso = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Color C = LabelAviso.getBackground();
            switch (LabelAviso.getText()) {
                case "PLANCHA":
                    if (C == jPanel1.getBackground()) {
                        LabelAviso.setBackground(Color.GREEN);
                    } else {
                        LabelAviso.setBackground(jPanel1.getBackground());
                    }
                    break;
                case "CALIENTE":
                    if (C == jPanel1.getBackground()) {
                        LabelAviso.setBackground(Color.YELLOW);
                    } else {
                        LabelAviso.setBackground(jPanel1.getBackground());
                    }
                    break;
                case "FRENE":
                    if (C == jPanel1.getBackground()) {
                        LabelAviso.setBackground(Color.RED);
                         LabelAviso.setIcon(imageOn);
                        LabelAviso.setForeground(Color.white);
                    } else {
                        LabelAviso.setBackground(jPanel1.getBackground());
                        LabelAviso.setIcon(imageOff);
                        LabelAviso.setForeground(Color.BLACK);
                    }
                    break;
            }
        }
    });

    Timer timerautomatico = new Timer(2000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            BotonContinuar.doClick();
        }
    });

    void RegistrarMedidas() throws SQLException, ClassNotFoundException {
        FrenosMotos frenos = new FrenosMotos();
        frenos.setFuerzaDelantera(fuerzas.get(0) * spanf);
        frenos.setFuerzaTrasera(fuerzas.get(1) * spanf);
        frenos.setPesoDelantero(pesos.get(0) * spanp);
        frenos.setPesoTrasero(pesos.get(1) * spanp);

        PruebaDefaultDAO frenosDAO = new PruebaDefaultDAO();
        try 
        {
           repetirPrueba = frenosDAO.persist(frenos, idPrueba, idUsuario,aplicTrans,this.ipEquipo,"Moto","Moto","");
           if (repetirPrueba == false) 
           {
                tramaAuditoria = "{\"eficaciaTotal\":\"".concat(String.valueOf(eficacia)).concat("\",").concat("\"eficaciaAuxiliar\":\"").concat(String.valueOf(" ")).concat("\",");
                tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(0 + 1)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(0 + 1)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(0 + 1)).concat("Derecho\":\"").concat(String.valueOf(frenos.getFuerzaDelantera())).concat("\",").concat("\"pesoEje").concat(String.valueOf(0 + 1)).concat("Derecho\":\"").concat(String.valueOf(frenos.getPesoDelantero())).concat("\",").concat("\"eje").concat(String.valueOf(0 + 1)).concat("Desequilibrio\":\"").concat(String.valueOf(" ")).concat("\",");
                tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(2)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(2)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(2)).concat("Derecho\":\"").concat(String.valueOf(frenos.getFuerzaTrasera())).concat("\",").concat("\"pesoEje").concat(String.valueOf(2)).concat("Derecho\":\"").concat(String.valueOf(frenos.getPesoTrasero())).concat("\",").concat("\"eje").concat(String.valueOf(2)).concat("Desequilibrio\":\"").concat(String.valueOf(" ")).concat("\",");
                for (int k = 3; k < 6; k++) {
                    tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(k)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(k)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(k)).concat("Derecho\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(k)).concat("Derecho\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"eje").concat(String.valueOf(k)).concat("Desequilibrio\":\"").concat(String.valueOf(" ")).concat("\",");
                }
                tramaAuditoria = tramaAuditoria.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPrueba)).concat("\"}");
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

    public void MedirVelocidad() {
        double ab = CalcularMedia(Datos3);
        velocidades.add(ab - valcalcero3);
        System.out.println("de nuevo el cero de velocidad " + (valcalcero3 * spanv) + " Km/h");
        System.out.println("velocidad sin calibrar " + ejemedido + ": " + (ab * spanv) + " Km/h");
        System.out.println("velocidad del eje " + ejemedido + ": " + (velocidades.get(0) * spanv) + " Km/h");

    }

    private void BotonEnviarCancelacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEnviarCancelacionActionPerformed
        // TODO add your handling code here:
        motivoCancelacion = jTextArea1.getText();
        try {
            registrarCancelacion(motivoCancelacion);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("La prueba fue cancelada por: " + motivoCancelacion);
        dialogCancelacion.dispose();
        this.dispose();
    }//GEN-LAST:event_BotonEnviarCancelacionActionPerformed

    private void BotonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarActionPerformed
        // TODO add your handling code here:
        if (puerto != null) {
            comandoSTOP();
        }
        Thread tmpBlinker = hilo3;
        tmpBlinker = hilo3;
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
        timerautomatico.stop();
        timeraviso.stop();
        System.out.println("Prueba cancelada");
        puerto.close();
        dialogCancelacion.setVisible(true);
        try {
            registrarCancelacion(jTextArea1.getText());
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_BotonCancelarActionPerformed

    private void BotonFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonFinalizarActionPerformed
        // TODO add your handling code here:
        double fuerzadel, fuerzatra, pesodel, pesotra;
        comandoSTOP();
        puerto.close();
        long s = fuerzas.size();
        double fuerzat = 0, pesot = 0;
        for (t = 0; t < s; t++) {
            pesot += pesos.get(t);
            fuerzat += fuerzas.get(t);
            System.out.println("peso " + (pesos.get(t) * spanp) + " fuerza " + (fuerzas.get(t) * spanf));
        }
        System.out.println("peso " + (pesot * spanp) + " fuerza " + (fuerzat * spanf));
        fuerzadel = fuerzas.get(0) * spanf;    //Valor de la fuerza de frenado maxima delantera
        fuerzatra = fuerzas.get(1) * spanf;    //Valor de la fuerza de frenado maxima trasera
        pesodel = pesos.get(0) * spanp;        //Valor del peso del eje delantero
        pesotra = pesos.get(1) * spanp;        //Valor del peso del eje trasero
        eficacia = (fuerzat * spanf / (pesot * spanp)) * 100;
        if (eficacia > 100) {
            eficacia = 100;
        }
        System.out.println("la eficacia del sistema de frenos fue de: " + eficacia + " ---%");
        try {
            RegistrarMedidas();
        } catch (SQLException | ClassNotFoundException ex) {
        }
        this.dispose();
    }//GEN-LAST:event_BotonFinalizarActionPerformed

    private void BotonContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonContinuarActionPerformed

        BotonContinuar.setEnabled(false);
        tecladoactivado = false;
        //JOptionPane.showMessageDialog(this,"Asegurese de que el eje "+ejemedido+" este sobre la plancha","Precaución",
        //    JOptionPane.WARNING_MESSAGE);
        jLabel3.setText("<html>Por favor avance la motocicleta hasta que el eje " + ejemedido
                +/*" <br>" +*/ " este sobre la plancha</html>");
        comandoFREN();
        //timer4.setRepeats(false);
        //timer4.start();
        //t1enabled(true);
        hilo1.start();
    }//GEN-LAST:event_BotonContinuarActionPerformed

    @SuppressWarnings("static-access")
    private void BotonEmpezarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEmpezarActionPerformed
        // TODO add your handling code here:
        try {
            int opcion;
            tecladoactivado = false;
            comandoSTOP();
            if (!PruebaConexion()) 
            {
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
                            Logger.getLogger(dialogfrenmotos.class.getName()).log(Level.SEVERE, null, ex);
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
                //JOptionPane.showMessageDialog(this,"Asegurese de que la plancha este libre","Precaución",
                //    JOptionPane.WARNING_MESSAGE);
                jLabel3.setText("Calibrando ceros...");
                comandoFREN();
                t1enabled(true);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace(System.err);
        }
    }//GEN-LAST:event_BotonEmpezarActionPerformed
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialogfrenmotos dialog = new dialogfrenmotos(new javax.swing.JFrame(), true, 1, 1, 1,1,"");
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
    private javax.swing.JPanel PanelBotones;
    private javax.swing.JDialog dialogCancelacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        double pesomed;
        numtimer1++;
        if (numtimer1 == 1) {
            jProgressBar1.setMaximum(8);
        }
        jProgressBar1.setValue(numtimer1);
        jProgressBar1.setString(numtimer1 * 12.5 + "%");
        CapturarDatos();
        if (numtimer1 == 8) {
            t1enabled(false);
            numtimer1 = 0;
            if (pasomedfren == 0) {
                CalibrarCeros(true, true);
                jLabel3.setText("Sistema calibrado por cero");
                BotonContinuar.setEnabled(true);
                timerautomatico.setRepeats(false);
                timerautomatico.start();
                tecladoactivado = true;
                pasomedfren++;
            } else {
                MedirPeso();
                Datos2.clear();
                jLabel3.setText("<html>Por favor devuelva la motocicleta hasta la linea</html>");
                if (ejemedido.equals("Delantero")) {
                    hilo2.start();
                }
                if (ejemedido.equals("Trasero")) {
                    hilo4.start();
                }
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
                    System.out.println("peso medido " + ((pesomed - valcalcero2) * spanp) + " N " + ejemedido);
                    Datos1.clear();
                    Datos2.clear();
                    if ((((pesomed - valcalcero2) * spanp) >= umbral_peso) && ejemedido.equals("Delantero")) {
                        break;
                    }
                    try {
                        if ((((pesomed - valcalcero2) * spanp) >= (pesos.get(0) * spanp + umbral_peso)) && ejemedido.equals("Trasero")) {
                            break;
                        }
                    } catch (IndexOutOfBoundsException ex) {

                    }
                }
                t1enabled(true);

            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }
            LabelAviso.setText("");
            timeraviso.stop();
            LabelAviso.setBackground(jPanel1.getBackground());
        }
    }

    class EsperaFuerza extends Thread {

        public void run() {
            double pesomed;
            try {
                numtimer2 = 0;
                sleep(500);
                while (true) {
                    sleep(500);
                    numtimer2++;
                    CapturarDatos();
                    pesomed = CalcularMediana(Datos2);
                    if (pesomed >= valcalcero2) {
                        System.out.println("peso medido de nuevo" + ((pesomed - valcalcero2) * spanp) + " N " + ejemedido);
                        Datos1.clear();
                        Datos2.clear();
                        if (((pesomed - valcalcero2) * spanp) <= (valcalcero2 * spanp * 0.05)) {
                            System.out.println("se rompio");
                            break;
                        }
                    }
                    if (numtimer2 == 10) {
                        break;
                    }
                }
                LabelAviso.setText("CALIENTE");
                timeraviso.start();
                Thread.currentThread().sleep(2500);
                LabelAviso.setText("");
                timeraviso.stop();
                LabelAviso.setBackground(jPanel1.getBackground());
                comandoFREN();
                jLabel3.setText("<html>Por favor avance con la motocicleta con velocidad</html>");
                Thread.currentThread().sleep(500);
                imageOn = new ImageIcon(getClass().getResource("/Imagenes/FrenoOn.png"));
                imageOff = new ImageIcon(getClass().getResource("/Imagenes/FrenoOf.png"));
                LabelAviso.setText("FRENE");
                timeraviso.start();
                while (true) {
                    Thread.sleep(100);
                    CapturarDatos();
                    pesomed = CalcularMediana(Datos2);
                    System.out.println("peso medido otra vez " + ((pesomed - valcalcero2) * spanp) + " N " + ejemedido);
                    Datos1.clear();
                    Datos2.clear();
                    if (((pesomed - valcalcero2) * spanp) >= (pesos.get(0) * 0.8)) {
                        break;
                    }
                }
                Random r = new Random();
                double velo = r.nextDouble() * 5;
                int veloint = (int) velo + 7;
                double ab = CalcularMedia(Datos3);
                velocidades.add(ab - valcalcero3);
                System.out.println("de nuevo el cero de velocidad " + (valcalcero3 * spanv) + " Km/h");
                System.out.println("velocidad sin calibrar " + ejemedido + ": " + (ab * spanv) + " Km/h");
                System.out.println("velocidad del eje " + ejemedido + ": " + (velocidades.get(0) * spanv) + " Km/h");
                System.out.println("valor ab: " + ab);
                double vel = ((1 / ab) * 100) * spanv;
                int velint = (int) (vel);
                ab = CalcularMedia(Datos3);;
                comandoFREN();
                Thread.sleep(2000);
                CapturarDatos();
                MedirFuerza();
                Datos1.clear();
                Datos2.clear();
                LabelAviso.setText("");
                LabelAviso.setIcon(null);
                timeraviso.stop();
                LabelAviso.setBackground(jPanel1.getBackground());
                if (ejemedido.equals("Trasero")) {
                    veloint += 2;

                }
                jLabel3.setText("Velocidad de prueba: " + vel + " Km/h    OK");
                System.out.println(vel);
                Thread.sleep(2000);
                switch (ejemedido) {
                    case "Trasero":
                        BotonFinalizar.setEnabled(true);
                        pesomed = pesos.get(1) - pesos.get(0);
                        pesos.set(1, pesomed);
                        jLabel3.setText("Prueba completada. Presione Finalizar");
                        break;
                    case "Delantero":
                        ejemedido = "Trasero";
                        LabelEje.setText("Eje " + ejemedido);
                        jLabel3.setText("<html>Por favor avance hasta que toda la motocicleta este sobre"
                                + " la plancha</html>");
                        hilo3.start();
                        break;
                }
                if (!(velint >= rangob && velint <= rangoa)) {
                    dialogfrenmotos.this.setVisible(false);
                    JOptionPane.showMessageDialog(null, "velocidad fuera de rango");

                    comandoSTOP();
                    puerto.close();
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }

        }
    }
}
