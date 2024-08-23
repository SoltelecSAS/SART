/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgServicio.java
 *
 * Created on 18/05/2011, 09:40:28 AM
 */
package vistas;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import eu.hansolo.steelseries.gauges.DisplaySingle;
import static vistas.DlgTaximetro.teporizadorPulsos;

/**
 *
 * @author Gerencia TIC
 */
public class DlgServicio extends javax.swing.JDialog {

    final double gravedad = 9.81;
    final double escalatiempo = 0.0052;
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private final byte[] buffer = new byte[1024];
    private byte data;
    private final int[] ComandoRecibido = new int[4];
    private int j = 0, i = 0, len = -1, partealta, numtimer1 = 0, t = 0;
    private BufferedReader config;
    private Timer timer = null;
    private int contadorTemporizacion;
    private final List<Integer> Datos1 = new ArrayList<>(); //Datos correspondientes al canal a calibrar
    private double spanfd, spanfi, spanpd, spanpi, spanvd, spanvi, spand;  //valores de span de los canales obtenidos de la calibración
    private double offsetfd, offsetfi, offsetpd, offsetpi, offsetvd, offsetvi, offsetd;  //valores de offset de los canales obtenidos de la calibración
    private int pulsosporvuelta;
    private double diametrorodillo;
    private double anchoPlaca;
    private String line;
    private byte salidaalta, salidabaja;
    private boolean canal0 = false, canal1 = false, canal2 = false, canal3 = false;
    private boolean correrhilo = true, cambiocanal = false;
    private final TServicio hilo1 = new TServicio();
   

    private XYDataset dataset1 = new XYSeriesCollection();
    private final XYSeries Serie1 = new XYSeries("Señal");
    private int tiempo = 0;
    Boolean cronometroActivo=false;
   public static Integer teporizadorPulsos = 0; 
   public static Integer pulsosXMilesima = 0; 
   
   public static Integer p = 0; 

    public DlgServicio(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setSize(this.getToolkit().getScreenSize());
        asociarBotones();
        configuracion();
        displayCanalPeso.setVisible(false);
    }

    @SuppressWarnings("empty-statement")
    private void configuracion() {
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            while (!config.readLine().startsWith("[DAQ]")) {
            }
            if ((line = config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
            }
            while (!config.readLine().startsWith("[DESVIACION]")) {
            }
            if ((line = config.readLine()).startsWith("ancho_placa:"));
            anchoPlaca = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            while (!config.readLine().startsWith("[TAXIMETRO]")) {
            }
            if ((line = config.readLine()).startsWith("diametro_rodillo:"));
            diametrorodillo = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("pulsos_por_vuelta:"));
            pulsosporvuelta = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));            
            if ((line = config.readLine()).startsWith("metros_por_unidad:"));
            p = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("segundos_por_unidad:"));
            p = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("unidades_distancia_a_medir:"));
            p = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
             if ((line = config.readLine()).startsWith("unidades_tiempo_a_medir:"));
            p = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("temporizador_pulso:"));
            teporizadorPulsos = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("pulso_x_milesima:"));
            pulsosXMilesima = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            
            
            System.out.println("Temporizador "+teporizadorPulsos);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("no se pudo abrir" + ex);
        }
        Properties archivop = new Properties();
        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (IOException e) {
            System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o está protegido");
        }
        spanfd = Double.parseDouble(archivop.getProperty("spanfd"));
        spanfi = Double.parseDouble(archivop.getProperty("spanfi"));
        spanpd = Double.parseDouble(archivop.getProperty("spanpd"));
        spanpi = Double.parseDouble(archivop.getProperty("spanpi"));
        spanvd = Double.parseDouble(archivop.getProperty("spanvd"));
        spanvi = Double.parseDouble(archivop.getProperty("spanvi"));
        spand = Double.parseDouble(archivop.getProperty("spand"));
        offsetfd = Double.parseDouble(archivop.getProperty("offsetfd"));
        offsetfi = Double.parseDouble(archivop.getProperty("offsetfi"));
        offsetpd = Double.parseDouble(archivop.getProperty("offsetpd"));
        offsetpi = Double.parseDouble(archivop.getProperty("offsetpi"));
        offsetvd = Double.parseDouble(archivop.getProperty("offsetvd"));
        offsetvi = Double.parseDouble(archivop.getProperty("offsetvi"));
        offsetd = Double.parseDouble(archivop.getProperty("offsetd"));

        puerto = new PuertoRS232();
        try {
            puerto.connect(puertotarjeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConfigurarDatos();
    }

    void ConfigurarDatos() {
        dataset1 = new XYSeriesCollection(Serie1);
        jLineChart1.setDataset(dataset1);
        Serie1.setMaximumItemCount(1344);
    }

    void CargaDato(double ti, double x) {
        double auxtiempo = ti * escalatiempo;
        try {
            this.Serie1.add(ti * escalatiempo, x);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("No el error ocurre aqui");
        }
    }

    void CargaVentana(List<Integer> Datos) {
        int s;
        long start = System.currentTimeMillis();
        s = Datos.size();
        System.out.println("el numero de datos es " + s);
        for (t = 0; t < s; t++) {
            CargaDato(tiempo, (double) Datos.get(t));
            tiempo++;
        }
        long stop = System.currentTimeMillis();
        System.out.println("duracion " + (stop - start));
    }

    void LimpiarVentana() {
        this.Serie1.clear();
        tiempo = 0;
    }

    private void asociarBotones() {
        GrupoBotonSal0.add(BotonONSal0);
        GrupoBotonSal0.add(BotonOFFSal0);
        GrupoBotonSal1.add(BotonONSal1);
        GrupoBotonSal1.add(BotonOFFSal1);
        GrupoBotonSal2.add(BotonONSal2);
        GrupoBotonSal2.add(BotonOFFSal2);
        GrupoBotonSal3.add(BotonONSal3);
        GrupoBotonSal3.add(BotonOFFSal3);
        GrupoBotonSal4.add(BotonONSal4);
        GrupoBotonSal4.add(BotonOFFSal4);
        GrupoBotonSal5.add(BotonONSal5);
        GrupoBotonSal5.add(BotonOFFSal5);
        GrupoBotonSal6.add(BotonONSal6);
        GrupoBotonSal6.add(BotonOFFSal6);
        GrupoBotonSal7.add(BotonONSal7);
        GrupoBotonSal7.add(BotonOFFSal7);
        GrupoBotonSal8.add(BotonONSal8);
        GrupoBotonSal8.add(BotonOFFSal8);
        GrupoBotonSal9.add(BotonONSal9);
        GrupoBotonSal9.add(BotonOFFSal9);
        GrupoBotonSal10.add(BotonONSal10);
        GrupoBotonSal10.add(BotonOFFSal10);
        GrupoBotonSal11.add(BotonONSal11);
        GrupoBotonSal11.add(BotonOFFSal11);
        GrupoBotonSal0.setSelected(BotonOFFSal0.getModel(), true);
        GrupoBotonSal1.setSelected(BotonOFFSal1.getModel(), true);
        GrupoBotonSal2.setSelected(BotonOFFSal2.getModel(), true);
        GrupoBotonSal3.setSelected(BotonOFFSal3.getModel(), true);
        GrupoBotonSal4.setSelected(BotonOFFSal4.getModel(), true);
        GrupoBotonSal5.setSelected(BotonOFFSal5.getModel(), true);
        GrupoBotonSal6.setSelected(BotonOFFSal6.getModel(), true);
        GrupoBotonSal7.setSelected(BotonOFFSal7.getModel(), true);
        GrupoBotonSal8.setSelected(BotonOFFSal8.getModel(), true);
        GrupoBotonSal9.setSelected(BotonOFFSal9.getModel(), true);
        GrupoBotonSal10.setSelected(BotonOFFSal10.getModel(), true);
        GrupoBotonSal11.setSelected(BotonOFFSal11.getModel(), true);
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

    public void comandoRESET() {
        try {
            puerto.out.write('H');
            puerto.out.write('E');
            puerto.out.write('W');
        } catch (IOException ex) {
            System.out.println("Error en el comando de reiniciar el taximetro");
        }
    }

    public void comandoSERV(byte canal) {
        if (canal <= 8) {
            try {
                puerto.out.write('H');
                puerto.out.write('I');
                puerto.out.write(canal);
                puerto.out.write('W');
            } catch (IOException ex) {
                System.out.println("Error en el comando de servicio");
            }
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

    public boolean PruebaConexion() {
        String respuesta;
        comandoCONEX();
        try {
            Thread.currentThread().sleep(200);
            puerto.in.read(buffer);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error en la lectura del buffer de entrada del puerto RS232");
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        return respuesta.startsWith("HDAQ1W");
    }

    public double CalcularMedia(List<Integer> Datos) {
        long b = 0, s;
        double c = 0;
        s = Datos.size();
        if (s == 0) {
            return 0;
        }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GrupoBotonSal0 = new javax.swing.ButtonGroup();
        GrupoBotonSal1 = new javax.swing.ButtonGroup();
        GrupoBotonSal2 = new javax.swing.ButtonGroup();
        GrupoBotonSal3 = new javax.swing.ButtonGroup();
        GrupoBotonSal4 = new javax.swing.ButtonGroup();
        GrupoBotonSal5 = new javax.swing.ButtonGroup();
        GrupoBotonSal6 = new javax.swing.ButtonGroup();
        GrupoBotonSal7 = new javax.swing.ButtonGroup();
        GrupoBotonSal8 = new javax.swing.ButtonGroup();
        GrupoBotonSal9 = new javax.swing.ButtonGroup();
        GrupoBotonSal10 = new javax.swing.ButtonGroup();
        GrupoBotonSal11 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SeleccionCanal = new javax.swing.JComboBox();
        jLineChart1 = new org.jfree.beans.JLineChart();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        displayTax = new eu.hansolo.steelseries.gauges.DisplaySingle();
        BotonReiniciarTax = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        BotonONSal0 = new javax.swing.JRadioButton();
        BotonONSal1 = new javax.swing.JRadioButton();
        BotonONSal2 = new javax.swing.JRadioButton();
        BotonONSal3 = new javax.swing.JRadioButton();
        BotonONSal4 = new javax.swing.JRadioButton();
        BotonONSal5 = new javax.swing.JRadioButton();
        BotonONSal6 = new javax.swing.JRadioButton();
        BotonONSal7 = new javax.swing.JRadioButton();
        BotonONSal8 = new javax.swing.JRadioButton();
        BotonONSal9 = new javax.swing.JRadioButton();
        BotonONSal10 = new javax.swing.JRadioButton();
        BotonONSal11 = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        BotonOFFSal0 = new javax.swing.JRadioButton();
        BotonOFFSal1 = new javax.swing.JRadioButton();
        BotonOFFSal2 = new javax.swing.JRadioButton();
        BotonOFFSal3 = new javax.swing.JRadioButton();
        BotonOFFSal4 = new javax.swing.JRadioButton();
        BotonOFFSal5 = new javax.swing.JRadioButton();
        BotonOFFSal6 = new javax.swing.JRadioButton();
        BotonOFFSal7 = new javax.swing.JRadioButton();
        BotonOFFSal8 = new javax.swing.JRadioButton();
        BotonOFFSal9 = new javax.swing.JRadioButton();
        BotonOFFSal10 = new javax.swing.JRadioButton();
        BotonOFFSal11 = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        SeleccionUniTax = new javax.swing.JComboBox();
        BotonApaSalidas = new javax.swing.JButton();
        BotonCarSalidas = new javax.swing.JButton();
        BotonSalir = new javax.swing.JButton();
        BotonConectar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        displayCanal = new eu.hansolo.steelseries.gauges.DisplaySingle();
        displayCanalPeso = new eu.hansolo.steelseries.gauges.DisplaySingle();
        SeleccionPromedio = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        SeleccionOffset = new javax.swing.JCheckBox();
        led4 = new eu.hansolo.steelseries.extras.Led();
        led3 = new eu.hansolo.steelseries.extras.Led();
        led2 = new eu.hansolo.steelseries.extras.Led();
        led1 = new eu.hansolo.steelseries.extras.Led();
        BotonRecal = new javax.swing.JButton();
        displaySeg = new eu.hansolo.steelseries.gauges.DisplaySingle();
        BotonCronometro = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Showcard Gothic", 0, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("MENU DE SERVICIO");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<html><div style=\"text-align:justify\">CANAL<br>ANALOGO</div></html>");

        SeleccionCanal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "F. DERECHA (CH 0)", "F. IZQUIERDA (CH 1)", "P. DERECHO (CH 2)", "P. IZQUIERDO (CH 3)", "V. DERECHA (CH 4)", "V. IZQUIERDA (CH 5)", "DESVIACION", "CANAL 7 S/ USO", "TAXIMETRO" }));
        SeleccionCanal.setSelectedItem(null);
        SeleccionCanal.setToolTipText("");
        SeleccionCanal.setEnabled(false);
        SeleccionCanal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SeleccionCanalActionPerformed(evt);
            }
        });

        jLineChart1.setSource("SOLTELEC LTDA.");
        jLineChart1.setSubtitle("señal de voltaje vs tiempo");
        jLineChart1.setTitle("OSCILOSCOPIO");
        jLineChart1.setXAxisLabel("Tiempo (s)");
        jLineChart1.setYAxisLabel("Voltaje (mV)");

        javax.swing.GroupLayout jLineChart1Layout = new javax.swing.GroupLayout(jLineChart1);
        jLineChart1.setLayout(jLineChart1Layout);
        jLineChart1Layout.setHorizontalGroup(
            jLineChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 560, Short.MAX_VALUE)
        );
        jLineChart1Layout.setVerticalGroup(
            jLineChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("CANALES DIGITALES");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("TAXIMETRO");

        displayTax.setEnabled(false);
        displayTax.setLcdDecimals(0);
        displayTax.setLcdUnitString("pulsos");

        javax.swing.GroupLayout displayTaxLayout = new javax.swing.GroupLayout(displayTax);
        displayTax.setLayout(displayTaxLayout);
        displayTaxLayout.setHorizontalGroup(
            displayTaxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 230, Short.MAX_VALUE)
        );
        displayTaxLayout.setVerticalGroup(
            displayTaxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        BotonReiniciarTax.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        BotonReiniciarTax.setText("REINICIAR");
        BotonReiniciarTax.setEnabled(false);
        BotonReiniciarTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonReiniciarTaxActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("SALIDAS");

        jPanel3.setLayout(new java.awt.GridLayout(3, 13));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("ON");
        jPanel3.add(jLabel11);

        BotonONSal0.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal0);

        BotonONSal1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal1);

        BotonONSal2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal2);

        BotonONSal3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal3);

        BotonONSal4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal4);

        BotonONSal5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal5);

        BotonONSal6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal6);

        BotonONSal7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal7);

        BotonONSal8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal8);

        BotonONSal9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal9);

        BotonONSal10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal10);

        BotonONSal11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonONSal11);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("OFF");
        jPanel3.add(jLabel12);

        BotonOFFSal0.setSelected(true);
        BotonOFFSal0.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal0);

        BotonOFFSal1.setSelected(true);
        BotonOFFSal1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal1);

        BotonOFFSal2.setSelected(true);
        BotonOFFSal2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal2);

        BotonOFFSal3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal3);

        BotonOFFSal4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal4);

        BotonOFFSal5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal5);

        BotonOFFSal6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal6);

        BotonOFFSal7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal7);

        BotonOFFSal8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal8);

        BotonOFFSal9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal9);

        BotonOFFSal10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal10);

        BotonOFFSal11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel3.add(BotonOFFSal11);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("NO.");
        jPanel3.add(jLabel13);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("0");
        jPanel3.add(jLabel14);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("1");
        jPanel3.add(jLabel15);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("2");
        jPanel3.add(jLabel16);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("3");
        jPanel3.add(jLabel17);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("4");
        jPanel3.add(jLabel18);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("5");
        jPanel3.add(jLabel19);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("6");
        jPanel3.add(jLabel20);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("7");
        jPanel3.add(jLabel21);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("8");
        jPanel3.add(jLabel22);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("9");
        jPanel3.add(jLabel23);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("10");
        jPanel3.add(jLabel24);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("11");
        jPanel3.add(jLabel25);

        SeleccionUniTax.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        SeleccionUniTax.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "pulsos", "metros" }));
        SeleccionUniTax.setEnabled(false);
        SeleccionUniTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SeleccionUniTaxActionPerformed(evt);
            }
        });

        BotonApaSalidas.setText("Apagar Todas");
        BotonApaSalidas.setEnabled(false);
        BotonApaSalidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonApaSalidasActionPerformed(evt);
            }
        });

        BotonCarSalidas.setText("Cargar");
        BotonCarSalidas.setEnabled(false);
        BotonCarSalidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCarSalidasActionPerformed(evt);
            }
        });

        BotonSalir.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        BotonSalir.setText("SALIR");
        BotonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSalirActionPerformed(evt);
            }
        });

        BotonConectar.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        BotonConectar.setText("CONECTAR");
        BotonConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonConectarActionPerformed(evt);
            }
        });

        jPanel4.setLayout(new java.awt.GridLayout(2, 0));

        displayCanal.setEnabled(false);
        displayCanal.setLcdDecimals(0);

        javax.swing.GroupLayout displayCanalLayout = new javax.swing.GroupLayout(displayCanal);
        displayCanal.setLayout(displayCanalLayout);
        displayCanalLayout.setHorizontalGroup(
            displayCanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );
        displayCanalLayout.setVerticalGroup(
            displayCanalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        jPanel4.add(displayCanal);

        displayCanalPeso.setCustomLcdUnitFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        displayCanalPeso.setLcdUnitString("Kg");

        javax.swing.GroupLayout displayCanalPesoLayout = new javax.swing.GroupLayout(displayCanalPeso);
        displayCanalPeso.setLayout(displayCanalPesoLayout);
        displayCanalPesoLayout.setHorizontalGroup(
            displayCanalPesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 171, Short.MAX_VALUE)
        );
        displayCanalPesoLayout.setVerticalGroup(
            displayCanalPesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        jPanel4.add(displayCanalPeso);

        SeleccionPromedio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Media", "Mediana" }));
        SeleccionPromedio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SeleccionPromedioActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("PROMEDIO:");

        SeleccionOffset.setText("OFFSET");
        SeleccionOffset.setEnabled(false);

        javax.swing.GroupLayout led4Layout = new javax.swing.GroupLayout(led4);
        led4.setLayout(led4Layout);
        led4Layout.setHorizontalGroup(
            led4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        led4Layout.setVerticalGroup(
            led4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout led3Layout = new javax.swing.GroupLayout(led3);
        led3.setLayout(led3Layout);
        led3Layout.setHorizontalGroup(
            led3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        led3Layout.setVerticalGroup(
            led3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout led2Layout = new javax.swing.GroupLayout(led2);
        led2.setLayout(led2Layout);
        led2Layout.setHorizontalGroup(
            led2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        led2Layout.setVerticalGroup(
            led2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout led1Layout = new javax.swing.GroupLayout(led1);
        led1.setLayout(led1Layout);
        led1Layout.setHorizontalGroup(
            led1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );
        led1Layout.setVerticalGroup(
            led1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );

        BotonRecal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        BotonRecal.setText("Recal-Offset");
        BotonRecal.setToolTipText("");
        BotonRecal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonRecalActionPerformed(evt);
            }
        });

        displaySeg.setDigitalFont(true);
        displaySeg.setDoubleBuffered(true);
        displaySeg.setEnabled(false);
        displaySeg.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        displaySeg.setLcdColor(eu.hansolo.steelseries.tools.LcdColor.BLACK_LCD);
        displaySeg.setLcdDecimals(0);
        displaySeg.setLcdInfoFont(new java.awt.Font("Verdana", 1, 36)); // NOI18N
        displaySeg.setLcdMaxValue(250.0);
        displaySeg.setLcdNumericValues(false);
        displaySeg.setLcdUnitString("Seg.");
        displaySeg.setLcdUnitStringVisible(false);
        displaySeg.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout displaySegLayout = new javax.swing.GroupLayout(displaySeg);
        displaySeg.setLayout(displaySegLayout);
        displaySegLayout.setHorizontalGroup(
            displaySegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );
        displaySegLayout.setVerticalGroup(
            displaySegLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 74, Short.MAX_VALUE)
        );

        BotonCronometro.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        BotonCronometro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cronometro.png"))); // NOI18N
        BotonCronometro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        BotonCronometro.setSelected(true);
        BotonCronometro.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/cronometro.png"))); // NOI18N
        BotonCronometro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCronometroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 897, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(BotonCarSalidas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BotonApaSalidas, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(SeleccionCanal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(SeleccionPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(SeleccionOffset, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(BotonRecal))
                        .addGap(25, 25, 25)))
                .addComponent(jLineChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(BotonConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(displaySeg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonCronometro))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(displayTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SeleccionUniTax, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BotonReiniciarTax)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(led3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(led4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(led4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(led3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(led1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(led2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(92, 92, 92)
                            .addComponent(jLabel9)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(displayTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(SeleccionUniTax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(BotonReiniciarTax)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(displaySeg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(BotonCronometro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(9, 9, 9)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(BotonConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(39, 39, 39))
                                .addComponent(BotonSalir)))
                        .addComponent(jLineChart1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(SeleccionCanal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SeleccionPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(SeleccionOffset)
                        .addGap(18, 18, 18)
                        .addComponent(BotonRecal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(BotonApaSalidas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonCarSalidas))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonConectarActionPerformed
        // TODO add your handling code here:
        int opcion;
        comandoSTOP();
        jLineChart1.setSource("SOLTELEC S.A.S");
        if (!PruebaConexion()) {
            opcion = JOptionPane.showOptionDialog(this, "La tarjeta no se encuentra conectada o inicializada."
                    + " Desea conectarla", "Configuración", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (opcion == 0) {
                Tarjeta tar = new Tarjeta();
                tar.setPuerto(puerto);
                tar.IniciaTarjeta();
                if (tar.getNumconex() == 3) {
                    JOptionPane.showMessageDialog(null, "Por Favor salgase el Apps. e Intenete de nuevo",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    puerto.close();
                    dispose();
                } else {
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BotonConectar.doClick();
                }
            } else {
                puerto.close();
                dispose();
            }
        } else {
            BotonApaSalidas.setEnabled(true);
            BotonCarSalidas.setEnabled(true);
            BotonReiniciarTax.setEnabled(true);
            SeleccionCanal.setEnabled(true);
            SeleccionUniTax.setEnabled(true);
            displayTax.setEnabled(true);
            displaySeg.setEnabled(true);
           
            displayCanal.setEnabled(true);
            SeleccionOffset.setEnabled(true);
        }
        BotonConectar.setEnabled(false);
    }//GEN-LAST:event_BotonConectarActionPerformed

    @SuppressWarnings("static-access")
    private void BotonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSalirActionPerformed
        // TODO add your handling code here:
        this.cronometroActivo = false;
        comandoSTOP();
        try {
            puerto.close();
        } catch (NullPointerException ex) {
            System.out.println("El puerto no fue creado");
        }
        correrhilo = false;
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }//GEN-LAST:event_BotonSalirActionPerformed

    private void BotonApaSalidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonApaSalidasActionPerformed
        // TODO add your handling code here:
        comandoSTOPDigital();
        GrupoBotonSal0.setSelected(BotonOFFSal0.getModel(), true);
        GrupoBotonSal1.setSelected(BotonOFFSal1.getModel(), true);
        GrupoBotonSal2.setSelected(BotonOFFSal2.getModel(), true);
        GrupoBotonSal3.setSelected(BotonOFFSal3.getModel(), true);
        GrupoBotonSal4.setSelected(BotonOFFSal4.getModel(), true);
        GrupoBotonSal5.setSelected(BotonOFFSal5.getModel(), true);
        GrupoBotonSal6.setSelected(BotonOFFSal6.getModel(), true);
        GrupoBotonSal7.setSelected(BotonOFFSal7.getModel(), true);
        GrupoBotonSal8.setSelected(BotonOFFSal8.getModel(), true);
        GrupoBotonSal9.setSelected(BotonOFFSal9.getModel(), true);
        GrupoBotonSal10.setSelected(BotonOFFSal10.getModel(), true);
        GrupoBotonSal11.setSelected(BotonOFFSal11.getModel(), true);
        try {
            Thread.currentThread().sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_BotonApaSalidasActionPerformed

    private void BotonCarSalidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCarSalidasActionPerformed
        // TODO add your handling code here:
        salidaalta = 0;
        salidabaja = 0;
        if (GrupoBotonSal0.getSelection() == BotonONSal0.getModel()) {
            salidabaja += 1;
        }
        if (GrupoBotonSal1.getSelection() == BotonONSal1.getModel()) {
            salidabaja += 2;
        }
        if (GrupoBotonSal2.getSelection() == BotonONSal2.getModel()) {
            salidabaja += 4;
        }
        if (GrupoBotonSal3.getSelection() == BotonONSal3.getModel()) {
            salidabaja += 8;
        }
        if (GrupoBotonSal4.getSelection() == BotonONSal4.getModel()) {
            salidabaja += 16;
        }
        if (GrupoBotonSal5.getSelection() == BotonONSal5.getModel()) {
            salidabaja += 32;
        }
        if (GrupoBotonSal6.getSelection() == BotonONSal6.getModel()) {
            salidaalta += 1;
        }
        if (GrupoBotonSal7.getSelection() == BotonONSal7.getModel()) {
            salidaalta += 2;
        }
        if (GrupoBotonSal8.getSelection() == BotonONSal8.getModel()) {
            salidaalta += 4;
        }
        if (GrupoBotonSal9.getSelection() == BotonONSal9.getModel()) {
            salidaalta += 8;
        }
        if (GrupoBotonSal10.getSelection() == BotonONSal10.getModel()) {
            salidaalta += 16;
        }
        if (GrupoBotonSal11.getSelection() == BotonONSal11.getModel()) {
            salidaalta += 32;
        }
        EnviaSalidas(salidaalta, salidabaja);
    }//GEN-LAST:event_BotonCarSalidasActionPerformed

    private void SeleccionCanalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SeleccionCanalActionPerformed
        // TODO add your handling code here:
        int aux;
        cambiocanal = true;
        aux = SeleccionCanal.getSelectedIndex();
        comandoSERV((byte) aux);
        LimpiarVentana();
        hilo1.setCanal(aux);
        try {
            hilo1.start();
        } catch (IllegalThreadStateException ex) {
            System.out.println("El hilo ya estaba corriendo");
        }
        displayCanalPeso.setVisible(false);
        if (aux < 2) {
            displayCanal.setLcdUnitString("N");
            displayCanal.setLcdDecimals(0);
        } else if (aux < 4) {
            displayCanal.setLcdUnitString("N");
            displayCanalPeso.setVisible(true);
            displayCanal.setLcdDecimals(0);
        } else if (aux < 6) {
            displayCanal.setLcdUnitString("Km/h");
            displayCanal.setLcdDecimals(0);
        } else if (aux < 7) {
            displayCanal.setLcdUnitString("m/Km");
            displayCanal.setLcdDecimals(2);
        } else {
            displayCanal.setLcdUnitString("");
            displayCanal.setLcdDecimals(0);
        }
    }//GEN-LAST:event_SeleccionCanalActionPerformed

    private void BotonReiniciarTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonReiniciarTaxActionPerformed
        // TODO add your handling code here:
        comandoRESET();
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgServicio.class.getName()).log(Level.SEVERE, null, ex);
        }
        comandoSERV((byte) 8);
        displaySeg.setLcdValue((int)  contadorTemporizacion );
        contadorTemporizacion=0;
        crearNuevoTimer();
    }//GEN-LAST:event_BotonReiniciarTaxActionPerformed
private void crearNuevoTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            } //end actionPerformed

        });
    }
    private void SeleccionUniTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SeleccionUniTaxActionPerformed
        // TODO add your handling code here:
        if (SeleccionUniTax.getSelectedIndex() == 0) {
            displayTax.setLcdUnitString("pulsos");
            displayTax.setLcdDecimals(0);
        } else if (SeleccionUniTax.getSelectedIndex() == 1) {
            displayTax.setLcdDecimals(1);
            displayTax.setLcdUnitString("metros");
        }
    }//GEN-LAST:event_SeleccionUniTaxActionPerformed

    private void SeleccionPromedioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SeleccionPromedioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SeleccionPromedioActionPerformed

    private void BotonRecalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonRecalActionPerformed
        if (SeleccionOffset.isSelected() == true) {
            try {
                Properties archivop = new Properties();
                archivop.load(new FileInputStream("calibracion.properties"));
                if (SeleccionCanal.getSelectedIndex() == 6) {
                    Double valRemain = displayCanal.getLcdValue();
                     JOptionPane.showMessageDialog(null," value LCD  "+valRemain);
                     valRemain =  valRemain * anchoPlaca;
                     JOptionPane.showMessageDialog(null," value cal of plancha  "+valRemain);
                    Double ofSetAct = Double.parseDouble(archivop.getProperty("offsetd"));
                      JOptionPane.showMessageDialog(null," OFFSET of FILE IS  "+ofSetAct);
                    if (Math.abs(valRemain) > 0.10) {
                        if (ofSetAct > 0.10) {
                            ofSetAct = ofSetAct - valRemain;
                        } else {
                            ofSetAct = ofSetAct + valRemain;
                        }
                        JOptionPane.showMessageDialog(null," OFFSET actually IS  "+ofSetAct);
                        archivop.setProperty("offsetd", Double.toString(ofSetAct));
                        offsetd=ofSetAct;
                    }else{
                        JOptionPane.showMessageDialog(null,"Disculpe; No se PUEDE  Recalcullar mas el OFFSET ");
                    }
                }
                if (SeleccionCanal.getSelectedIndex() == 0) {
                    Double valRemain = displayCanal.getLcdValue();                     
                    Double ofSetAct = Double.parseDouble(archivop.getProperty("offsetfd"));
                   System.out.println("offset fuerza derec " + ofSetAct);
                    if (Math.abs(valRemain) > 0.10) {                        
                        if (ofSetAct > 0.10) {                           
                            ofSetAct = ofSetAct -  Math.abs(valRemain);
                        } else {                            
                            ofSetAct = ofSetAct +  Math.abs(valRemain);
                        }
                        archivop.setProperty("offsetfd", Double.toString(ofSetAct));
                        offsetfd=ofSetAct;
                    }else{
                        JOptionPane.showMessageDialog(null,"Disculpe; No se PUEDE  Recalcular mas el OFFSET ");
                    }
                }
                if (SeleccionCanal.getSelectedIndex() == 2) {
                    Double valRemain = displayCanal.getLcdValue();                     
                    Double ofSetAct = Double.parseDouble(archivop.getProperty("offsetpd"));
                    System.out.println("offset peso derec en archivo " + ofSetAct);
                    System.out.println("val  remains " + valRemain);
                    
                    if (Math.abs(valRemain) > 0.10) {                        
                        if (ofSetAct > 0.10) {  
                            System.out.println("aplica opr resta " );
                            ofSetAct = ofSetAct - Math.abs(valRemain);
                        } else {
                            System.out.println("aplica opr suma" );
                            ofSetAct = ofSetAct + Math.abs(valRemain);
                        }
                        archivop.setProperty("offsetpd", Double.toString(ofSetAct));
                        offsetpd=ofSetAct;
                    }else{
                        JOptionPane.showMessageDialog(null,"Disculpe; No se PUEDE  Recalcular mas el OFFSET ");
                    }
                } 
                
                if (SeleccionCanal.getSelectedIndex() == 1) {
                    Double valRemain = displayCanal.getLcdValue();                     
                    Double ofSetAct = Double.parseDouble(archivop.getProperty("offsetfi"));
                   System.out.println("offset fuerza izq " + ofSetAct);
                    if (Math.abs(valRemain) > 0.10) {                        
                        if (ofSetAct > 0.10) {                           
                            ofSetAct = ofSetAct -  Math.abs(valRemain);
                        } else {                            
                            ofSetAct = ofSetAct +  Math.abs(valRemain);
                        }
                        archivop.setProperty("offsetfi", Double.toString(ofSetAct));
                        offsetfi=ofSetAct;
                    }else{
                        JOptionPane.showMessageDialog(null,"Disculpe; No se PUEDE  Recalcular mas el OFFSET ");
                    }
                }                
                if (SeleccionCanal.getSelectedIndex() == 3) {
                    Double valRemain = displayCanal.getLcdValue();                   
                    Double ofSetAct = Double.parseDouble(archivop.getProperty("offsetpi"));
                     System.out.println("offset peso izq " + ofSetAct);
                    if (Math.abs(valRemain) > 0.10) {                        
                        if (ofSetAct > 0.10) {                           
                            ofSetAct = ofSetAct -  Math.abs(valRemain);
                        } else {                            
                            ofSetAct = ofSetAct +  Math.abs(valRemain);
                        }
                        archivop.setProperty("offsetpi", Double.toString(ofSetAct));
                        offsetpi=ofSetAct;
                    }else{
                        JOptionPane.showMessageDialog(null,"Disculpe; No se PUEDE  Recalcular mas el OFFSET ");
                    }
                }                
                archivop.store(new FileOutputStream("calibracion.properties"), "Valores de calibración de los canales");
                SeleccionCanalActionPerformed(null);
            } catch (IOException ex) {

            }
        } else {
            JOptionPane.showMessageDialog(null, "Disculpe; no se ha seleccionado el OFFSET para poder RECALCULAR");
        }

    }//GEN-LAST:event_BotonRecalActionPerformed

    private void BotonCronometroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCronometroActionPerformed
        TCronometro hiloC = new TCronometro();
        if (this.cronometroActivo == false) {
            this.cronometroActivo = true;
            displaySeg.setLcdInfoString("Inciado Cronometro");
            try {
                hiloC.start();
            } catch (IllegalThreadStateException ex) {
                System.out.println("El hilo ya estaba corriendo");
            }
        } else {
              displaySeg.setLcdInfoString("Detenido Cronometro");
            this.cronometroActivo = false;
        }       
    }//GEN-LAST:event_BotonCronometroActionPerformed

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
                            ComandoRecibido[1] = (byteToInt(data) & 0x0F);
                        } else {
                        }
                        j = 2;
                    } else if (j == 2) {
                        partealta = byteToInt(data);
                        j = 3;
                    } else if (j == 3) {
                        ComandoRecibido[2] = partealta * 256 + byteToInt(data);
                        if (ComandoRecibido[2] <= 4095) {
                            //Datos1.add(ComandoRecibido[2]);
                        } else {

                        }
                        j = 4;
                    } else if (j == 4) {
                        ComandoRecibido[3] = data;
                        j = 0;
                        if (data == 'W') {
                            setCanal0((ComandoRecibido[1] & 0x01) > 0);
                            setCanal1((ComandoRecibido[1] & 0x02) > 0);
                            setCanal2((ComandoRecibido[1] & 0x04) > 0);
                            setCanal3((ComandoRecibido[1] & 0x08) > 0);
                            if (ComandoRecibido[2] <= 4095) {
                                Datos1.add(ComandoRecibido[2]);
                                if (SeleccionCanal.getSelectedIndex() != 8) {
                                    try {
                                        CargaDato(tiempo, (double) Datos1.get(Datos1.size() - 1));
                                    } catch (IndexOutOfBoundsException ex) {
                                        System.out.println("El error esta ocurriendo aqui");
                                    }
                                    tiempo++;
                                }
                            } else {

                            }
                        }
                    }
                }
                led1.setLedOn(isCanal0());
                led2.setLedOn(isCanal1());
                led3.setLedOn(isCanal2());
                led4.setLedOn(isCanal3());
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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

    public boolean isCanal2() {
        return canal2;
    }

    public void setCanal2(boolean canal2) {
        this.canal2 = canal2;
    }

    public boolean isCanal3() {
        return canal3;
    }

    public void setCanal3(boolean canal3) {
        this.canal3 = canal3;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                DlgServicio dialog = new DlgServicio(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton BotonApaSalidas;
    private javax.swing.JButton BotonCarSalidas;
    private javax.swing.JButton BotonConectar;
    private javax.swing.JButton BotonCronometro;
    private javax.swing.JRadioButton BotonOFFSal0;
    private javax.swing.JRadioButton BotonOFFSal1;
    private javax.swing.JRadioButton BotonOFFSal10;
    private javax.swing.JRadioButton BotonOFFSal11;
    private javax.swing.JRadioButton BotonOFFSal2;
    private javax.swing.JRadioButton BotonOFFSal3;
    private javax.swing.JRadioButton BotonOFFSal4;
    private javax.swing.JRadioButton BotonOFFSal5;
    private javax.swing.JRadioButton BotonOFFSal6;
    private javax.swing.JRadioButton BotonOFFSal7;
    private javax.swing.JRadioButton BotonOFFSal8;
    private javax.swing.JRadioButton BotonOFFSal9;
    private javax.swing.JRadioButton BotonONSal0;
    private javax.swing.JRadioButton BotonONSal1;
    private javax.swing.JRadioButton BotonONSal10;
    private javax.swing.JRadioButton BotonONSal11;
    private javax.swing.JRadioButton BotonONSal2;
    private javax.swing.JRadioButton BotonONSal3;
    private javax.swing.JRadioButton BotonONSal4;
    private javax.swing.JRadioButton BotonONSal5;
    private javax.swing.JRadioButton BotonONSal6;
    private javax.swing.JRadioButton BotonONSal7;
    private javax.swing.JRadioButton BotonONSal8;
    private javax.swing.JRadioButton BotonONSal9;
    private javax.swing.JButton BotonRecal;
    private javax.swing.JButton BotonReiniciarTax;
    private javax.swing.JButton BotonSalir;
    private javax.swing.ButtonGroup GrupoBotonSal0;
    private javax.swing.ButtonGroup GrupoBotonSal1;
    private javax.swing.ButtonGroup GrupoBotonSal10;
    private javax.swing.ButtonGroup GrupoBotonSal11;
    private javax.swing.ButtonGroup GrupoBotonSal2;
    private javax.swing.ButtonGroup GrupoBotonSal3;
    private javax.swing.ButtonGroup GrupoBotonSal4;
    private javax.swing.ButtonGroup GrupoBotonSal5;
    private javax.swing.ButtonGroup GrupoBotonSal6;
    private javax.swing.ButtonGroup GrupoBotonSal7;
    private javax.swing.ButtonGroup GrupoBotonSal8;
    private javax.swing.ButtonGroup GrupoBotonSal9;
    private javax.swing.JComboBox SeleccionCanal;
    private javax.swing.JCheckBox SeleccionOffset;
    private javax.swing.JComboBox SeleccionPromedio;
    private javax.swing.JComboBox SeleccionUniTax;
    private eu.hansolo.steelseries.gauges.DisplaySingle displayCanal;
    private eu.hansolo.steelseries.gauges.DisplaySingle displayCanalPeso;
    private eu.hansolo.steelseries.gauges.DisplaySingle displaySeg;
    private eu.hansolo.steelseries.gauges.DisplaySingle displayTax;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private org.jfree.beans.JLineChart jLineChart1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private eu.hansolo.steelseries.extras.Led led1;
    private eu.hansolo.steelseries.extras.Led led2;
    private eu.hansolo.steelseries.extras.Led led3;
    private eu.hansolo.steelseries.extras.Led led4;
    // End of variables declaration//GEN-END:variables

    class TCronometro extends Thread {

        int canal;
        double promedio;
        Integer minutos=0;
       Integer segundos=0;
        Integer milesimas=0;
        String strSegundo="";
        String strMilesima="";


        @Override
        public void run() {
            minutos=0;
            segundos=0;
            milesimas=0;
                     
            while (cronometroActivo) {   
             try {
              Thread.currentThread().sleep(DlgServicio.teporizadorPulsos);
              milesimas++;
              if(milesimas==DlgServicio.pulsosXMilesima){
                 milesimas=0;
                 segundos++;                
              }
              if(segundos<=9){
                  strSegundo="0".concat(segundos.toString());
              }else{
                 strSegundo="".concat(segundos.toString());                               
              }
              if(milesimas<=9){
                  strMilesima="0".concat(milesimas.toString());
              }else{
                 strMilesima="".concat(milesimas.toString());                               
              }
              if(segundos>=10 &&  segundos<=19){
                 displaySeg.setLcdText(strSegundo.concat(":").concat(strMilesima).concat(" Seg")); 
              }else{
                 displaySeg.setLcdText(strSegundo.concat(":").concat(strMilesima).concat(" Seg."));  
              }                            
              System.out.println("teen"+DlgServicio.teporizadorPulsos);          
              
           //  displaySeg.setLcdText(String.valueOf(segundos).concat(":").concat(String.valueOf(milesimas)));
          } catch (Exception ex) {
              Logger.getLogger(DlgServicio.class.getName()).log(Level.SEVERE, null, ex);
          }
       
        }    
        }
    }
    class TServicio extends Thread {

        int canal;
        double promedio;

        public int getCanal() {
            return canal;
        }

        public void setCanal(int canal) {
            this.canal = canal;
        }

        @Override
        public void run() {
            while (correrhilo) {
                try {
                    this.sleep(480);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DlgServicio.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (cambiocanal) {
                    try {
                        puerto.in.read(buffer);
                    } catch (IOException ex) {
                        Logger.getLogger(DlgServicio.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    cambiocanal = false;
                }

                CapturarDatos();

                if (getCanal() <= 7) {
                    if (SeleccionPromedio.getSelectedIndex() == 0) {
                        promedio = CalcularMedia(Datos1);
                    } else if (SeleccionPromedio.getSelectedIndex() == 1) {
                        promedio = CalcularMediana(Datos1);
                    }
                    if (getCanal() == 0) {
                        promedio *= spanfd;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetfd;
                        }
                    } else if (getCanal() == 1) {
                        promedio *= spanfi;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetfi;
                        }
                    } else if (getCanal() == 2) {
                        promedio *= spanpd;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetpd;
                        }
                    } else if (getCanal() == 3) {
                        promedio *= spanpi;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetpi;
                        }
                    } else if (getCanal() == 4 || getCanal() == 5) {
                        promedio *= spanvd;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetvd;
                        }
                    } else if (getCanal() == 5) {
                        promedio *= spanvi;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetvi;
                        }
                    } else if (getCanal() == 6) {
                        promedio *= spand;
                        if (SeleccionOffset.isSelected()) {
                            promedio += offsetd;
                        }
                        System.out.println("Valor del Promedio: " + promedio);
                        System.out.println("Valor del Ancho Placa: " + anchoPlaca);
                        promedio = promedio / anchoPlaca;
                        displayCanal.setLcdDecimals(2);
                        System.out.println("After of Operation: " + promedio);
                    } else {
                        displayCanal.setLcdDecimals(0);
                    }
                    displayCanal.setLcdValue(promedio);
                    if (getCanal() == 2 || getCanal() == 3) {
                        displayCanalPeso.setLcdValue(displayCanal.getLcdValue() / gravedad);
                    }
                } else {
                    if (SeleccionUniTax.getSelectedIndex() == 0) {
                        displayTax.setLcdValue((double) ComandoRecibido[2]);
                         System.out.println("Valor cmd recibido cuando es x pulso " + ComandoRecibido[2]);
                    } else if (SeleccionUniTax.getSelectedIndex() == 1) {
                        displayTax.setLcdValue((double) ComandoRecibido[2] * diametrorodillo * Math.PI / pulsosporvuelta);
                         System.out.println("Valor cmd recibido: " + ComandoRecibido[2]);
                         System.out.println("diametrorodillo: " + diametrorodillo);
                         System.out.println("pulsosporvuelta: " + pulsosporvuelta);                         
                    }
                }

                Datos1.clear();
            }
        }
    }
}
