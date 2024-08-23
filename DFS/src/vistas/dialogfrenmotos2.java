package vistas;

//import com.soltelec.estandar.EstadoEventosSicov;
//import com.soltelec.integrador.cliente.ClienteSicov;
import com.soltelec.loginadministrador.UtilPropiedades;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import dao.PruebasDAO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.Timer;
import modelo.Frenos;


public class dialogfrenmotos2
        extends JDialog
        implements ActionListener {
    private String CalificacionPrueba;
    private int permisibleprueba = 30;
    private int NumPrueba;
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private byte[] buffer = new byte[1024];
    private byte data;
    private int j = 0;
    private int i = 0;
    private int len = -1;
    private int partealta;
    private int numtimer1 = 0;
    private int numtimer2 = 0;
    private int t = 0;
    private int[] ComandoRecibido = new int[9];
    private BufferedReader config;
    private boolean tecladoactivado = false;
    private List<Integer> Datos1 = new ArrayList();
    private List<Integer> Datos2 = new ArrayList();
    private double eficacia;
    private double valcalcero1 = 0.0;
    private double valcalcero2 = 0.0;
    private double ruidomotor;
    private byte pasomedfren = 0;
    private byte pasoactual = 0;
    private List<Double> pesos = new ArrayList();
    private List<Double> fuerzas = new ArrayList();
    private List<Double> Datosfil = new ArrayList();
    private double spanf;
    private double spanp;
    private String ejemedido = "Delantero";
    private int tiempo_paso = 1;
    private byte salidaalta;
    private byte salidabaja;
    private String line;
    private double umbral_peso;
    private double umbral_fuerza;
    private double[] filtro = {0.0091, 0.0131, 0.0245, 0.0416, 0.0619, 0.0821, 0.0993, 0.1108, 0.1149, 0.1108, 0.0993, 0.0821, 0.0619, 0.0416, 0.0245, 0.0131, 0.0091};
    private String motivoCancelacion;
    private EsperaPeso hilo1 = new EsperaPeso();
    private EsperaFuerza hilo2 = new EsperaFuerza();
    private EsperaPeso hilo3 = new EsperaPeso();
    private EsperaFuerza hilo4 = new EsperaFuerza();
    private PruebaArchivoFrenometro paf;
    private long idUsuario;
    private long idHojaPrueba;
    private Long idPrueba;
    private String URLServidor;
    private BufferedWriter regdatosffd1;
    private BufferedWriter regdatosffd2;
    private BufferedWriter regdatospd1;
    private BufferedWriter regdatospd2;
    public static String tramaAuditoria;
     public static String escrTrans = "";

    public dialogfrenmotos2(Frame parent, boolean modal, long idPrueba, long idUsuario, long idHojaPruebaLocal) {
        super(parent, modal);
        initComponents();

        configuracion();
        jProgressBar1.setFont(new Font("Tahoma", 0, 30));

        tecladoactivado = true;
        try {
            permisibleprueba = consultarPermisible();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            permisibleprueba = 30;
        }
        paf = new PruebaArchivoFrenometro();
        try {
            URLServidor = UtilPropiedades.cargarPropiedad("urljdbc", "propiedades.properties");
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(parent, "No se encuentra el archivo propiedades.properties");
        } catch (IOException ex) {
            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        idHojaPrueba = idHojaPruebaLocal;
        System.out.println("Entro conrrectamnete");
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setMinimumSize(Toolkit.getDefaultToolkit().getScreenSize());
    }

    private void configuracion() {
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            while (!config.readLine().startsWith("[DAQ]")) {
            }
            if ((line = config.readLine()).startsWith("puerto:")) {
                puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
            }
            while (!config.readLine().startsWith("[FRENOMETRO]")) {
            }
            if ((line = config.readLine()).startsWith("umbral_fuerza")) {
                umbral_fuerza = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            }
            if ((line = config.readLine()).startsWith("umbral_peso")) {
                umbral_peso = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            }
            while (!config.readLine().startsWith("motos:")) {
            }
            crearEncavezadosValoresMedidas();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("no se pudo abrir" + ex);
        }
        Properties archivop = new Properties();
        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (Exception e) {
            System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o esta protegido");
        }
        spanf = Double.parseDouble(archivop.getProperty("spanfd"));
        spanp = Double.parseDouble(archivop.getProperty("spanpd"));

        puerto = new PuertoRS232();
        try {
            puerto.connect(puertotarjeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addKeyListener(new KeyListener() {
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
            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Se perdio de lineas");
        }
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

//    public void comandoFREN() {
//        try {
//            puerto.out.write(72);
//            puerto.out.write(65);
//            puerto.out.write(87);
//        } catch (IOException ex) {
//            System.out.println("Error en el comando de frenometro");
//        }
//    }
//
//    public void comandoSTOP() {
//        try {
//            puerto.out.write(72);
//            puerto.out.write(66);
//            puerto.out.write(87);
//        } catch (IOException ex) {
//            System.out.println("Error en el comando de STOP");
//        }
//    }
//
//    public void comandoSTOPAnalogo() {
//        try {
//            puerto.out.write(72);
//            puerto.out.write(66);
//            puerto.out.write(65);
//            puerto.out.write(87);
//        } catch (IOException ex) {
//            System.out.println("Error en el comando de STOP Analogo");
//        }
//    }
//
//    public void comandoSTOPDigital() {
//        try {
//            puerto.out.write(72);
//            puerto.out.write(66);
//            puerto.out.write(68);
//            puerto.out.write(87);
//        } catch (IOException ex) {
//            System.out.println("Error en el comando de STOP Digital");
//        }
//    }
//
//    public void comandoCONEX() {
//        try {
//            puerto.out.write(72);
//            puerto.out.write(74);
//            puerto.out.write(87);
//        } catch (IOException ex) {
//            System.out.println("Error en el comando de conexi�n a la tarjeta");
//        }
//    }
    public boolean PruebaConexion() {
        comandoCONEX();
        try {
            Thread.currentThread();
            Thread.sleep(200);
            puerto.in.read(buffer);
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
        }
        String respuesta = new String(buffer);
        if (respuesta.startsWith("HDAQ1W")) {
            return true;
        }
        return false;
    }

    public void CalibrarCeros(boolean a, boolean b) {
        if (a) {
            valcalcero1 = (CalcularMedia(Datos1) + ruidomotor);
            System.out.println("cero fuerza: " + valcalcero1 * spanf + " N");
        }
        Datos1.clear();
        if (b) {
            valcalcero2 = CalcularMedia(Datos2);
            System.out.println("cero peso: " + valcalcero2 * spanp + " N");
        }
        Datos2.clear();
    }

    public void MedirFuerza() {
        armonizar();
        filtrar(Datos1);
        double ab = CalcularMaximo2();
        long s = Datos1.size();
        System.out.println("el numero de datos es: " + s);
        if (ab - valcalcero1 < 0.0) {
            fuerzas.add(0.0);
        } else {
            fuerzas.add(ab - valcalcero1);
        }
        int bc = fuerzas.size();
        System.out.println("de nuevo el cero de fuerza " + valcalcero1 * spanf + " N");
        System.out.println("fuerza sin calibrar " + ejemedido + ": " + ab * spanf + " N");
        System.out.println("fuerza del eje " + ejemedido + ": " + (fuerzas.get(bc - 1)) * spanf + " N");
        medirValoresMedidasFuerza();
        Datos1.clear();
        Datosfil.clear();
    }

    public void MedirPeso() {
        double ab = CalcularMediana(Datos2);

        pesos.add(ab - valcalcero2);
        int bc = pesos.size();
        System.out.println("de nuevo el cero de peso " + valcalcero2 * spanp + " N");
        System.out.println("peso sin calibrar " + ejemedido + ": " + ab * spanp + " N");
        System.out.println("peso del eje " + ejemedido + ": " + (pesos.get(bc - 1)) * spanp + " N");
        medirValoresMedidasPeso();
        Datos2.clear();
    }

    public double CalcularMedia(List<Integer> Datos) {
        long s = Datos.size();

        long b = 0;
        for (t = 0; t < s; t += 1) {
            b += (Datos.get(t));
        }
        double c = b / s;

        return c;
    }

    public double CalcularMediana(List<Integer> Datos) {
        int s = Datos.size();
        if (s == 0) {
            return 0.0;
        }
        if (s == 1) {
            return (Datos.get(0));
        }
        for (t = 0; t < s; t += 1) {
            for (int r = t + 1; r < s; r++) {
                if ((Datos.get(r)) > (Datos.get(t))) {
                    int auxMediana = (Datos.get(t));
                    Datos.set(t, Datos.get(r));
                    Datos.set(r, auxMediana);
                }
            }
        }
        double c;
        if (s % 2 == 0) {
            c = ((Datos.get((s - 1) / 2)) + (Datos.get((s - 1) / 2 + 1))) / 2.0;
        } else {
            c = (Datos.get((s - 1) / 2));
        }
        return c;
    }

    public double CalcularMaximo(List<Integer> Datos) {
        double max = 0.0;
        long s = Datos.size();
        for (t = 0; t < s; t += 1) {
            if ((Datos.get(t)) > max) {
                max = (Datos.get(t));
            }
        }
        return max;
    }

    public double CalcularMaximo2() {
        double max = 0.0;
        long s = Datosfil.size();
        for (t = 0; t < s; t += 1) {
            if ((Datosfil.get(t)) > max) {
                max = (Datosfil.get(t));
            }
        }
        return max;
    }

    public double CalcularDesvEst(List<Integer> Datos) {
        double sum = 0.0;
        double media = CalcularMedia(Datos);
        long s = Datos.size();
        for (t = 0; t < s; t += 1) {
            sum += Math.pow((Datos.get(t)) - media, 2.0);
        }
        double desvest = sum / (s - 1);
        return desvest;
    }

    public void filtrar(List<Integer> Datos) {
        int s = Datos.size();
        System.out.println("el numero de datos en el filtro es: " + s);
        int n = filtro.length;
        for (int p = 0; p < s; p++) {
            double y = 0.0;
            for (int q = 0; q < n; q++) {
                if (p - q >= 0) {
                    y += filtro[q] * (Datos.get(p - q));
                }
            }
            Datosfil.add(y);
        }
    }

    public void armonizar() {
        double pendientesig = 0.0;
        double pendienteant = 0.0;
        int s = Datos1.size();
        for (t = 0; t < s; t += 1) {
            if (t == 0) {
                try {
                    pendienteant = ((Datos1.get(t)) - valcalcero1) / valcalcero1;
                } catch (Exception e) {
                }
            } else if (t == s - 1) {
                try {
                    pendientesig = ((Datos1.get(t)) - valcalcero1) / valcalcero1;
                } catch (Exception e) {
                }
            } else {
                try {
                    pendienteant = ((Datos1.get(t)) - (Datos1.get(t - 1))) / (Datos1.get(t - 1));
                    pendientesig = ((Datos1.get(t)) - (Datos1.get(t + 1))) / (Datos1.get(t + 1));
                } catch (Exception e) {
                }
            }
            if ((Math.abs(pendienteant) > 1) && (Math.abs(pendientesig) > 1)) {
                if (t == 0) {
                    Datos1.set(t, Datos1.get(t + 1));
                } else if (t == s - 1) {
                    Datos1.set(t, Datos1.get(t - 1));
                } else {
                    try {
                        Datos1.set(t, ((Datos1.get(t - 1)) + (Datos1.get(t + 1))) / 2);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public void CapturarDatos() {
        try {
            len = puerto.in.read(buffer);
            if (len > 0) {
                for (i = 0; i < len; i += 1) {
                    data = buffer[i];
                    if (data == 72) {
                        ComandoRecibido[0] = data;
                        j = 1;
                    } else if (j == 1) {
                        if (data <= 15) {
                            ComandoRecibido[1] = (byteToInt(data) & 0x03);
                        }
                        j = 2;
                    } else if (j == 2) {
                        partealta = byteToInt(data);
                        j = 3;
                    } else if (j == 3) {
                        ComandoRecibido[2] = (partealta * 256 + byteToInt(data));
                        if (ComandoRecibido[2] <= 4095) {
                            Datos1.add(ComandoRecibido[2]);
                        }
                        j = 4;
                    } else if (j == 4) {
                        partealta = byteToInt(data);
                        j = 5;
                    } else if (j == 5) {
                        ComandoRecibido[3] = (partealta * 256 + byteToInt(data));

                        j = 6;
                    } else if (j == 6) {
                        partealta = byteToInt(data);
                        j = 7;
                    } else if (j == 7) {
                        ComandoRecibido[4] = (partealta * 256 + byteToInt(data));
                        if (ComandoRecibido[4] <= 4095) {
                            Datos2.add(ComandoRecibido[4]);
                        }
                        j = 8;
                    } else if (j == 8) {
                        partealta = byteToInt(data);
                        j = 9;
                    } else if (j == 9) {
                        ComandoRecibido[5] = (partealta * 256 + byteToInt(data));

                        j = 10;
                    } else if (j == 10) {
                        partealta = byteToInt(data);
                        j = 11;
                    } else if (j == 11) {
                        ComandoRecibido[6] = (partealta * 256 + byteToInt(data));
                        if (ComandoRecibido[6] <= 4095) {
                        }
                        j = 12;
                    } else if (j == 12) {
                        partealta = byteToInt(data);
                        j = 13;
                    } else if (j == 13) {
                        ComandoRecibido[7] = (partealta * 256 + byteToInt(data));

                        j = 14;
                    } else if (j == 14) {
                        ComandoRecibido[8] = data;
                        j = 0;
                        if (data != 87) {
                        }
                    }
                }
            }
            comandoFREN();
        } catch (IOException ex) {
            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        dialogCancelacion = new JDialog();
        jLabel6 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        BotonEnviarCancelacion = new JButton();
        jPanel1 = new JPanel();
        jLabel2 = new JLabel();
        jLabel1 = new JLabel();
        jPanel2 = new JPanel();
        LabelAviso = new JLabel();
        jPanel5 = new JPanel();
        LabelEje = new JLabel();
        jLabel3 = new JLabel();
        jPanel4 = new JPanel();
        jProgressBar1 = new JProgressBar();
        PanelBotones = new JPanel();
        BotonEmpezar = new JButton();
        BotonContinuar = new JButton();
        BotonFinalizar = new JButton();
        BotonCancelar = new JButton();

        dialogCancelacion.setDefaultCloseOperation(0);
        dialogCancelacion.setTitle("CANCELACION DE PRUEBA");
        dialogCancelacion.setMinimumSize(new Dimension(630, 320));
        dialogCancelacion.setModal(true);
        dialogCancelacion.setResizable(false);

        jLabel6.setFont(new Font("Tahoma", 0, 18));
        jLabel6.setText("Por favor describa el motivo de la cancelaci�n de la prueba:");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new Font("Monospaced", 0, 18));
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        BotonEnviarCancelacion.setFont(new Font("Tahoma", 0, 18));
        BotonEnviarCancelacion.setText("ENVIAR");
        BotonEnviarCancelacion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                BotonEnviarCancelacionActionPerformed(evt);
            }
        });
        GroupLayout dialogCancelacionLayout = new GroupLayout(dialogCancelacion.getContentPane());
        dialogCancelacion.getContentPane().setLayout(dialogCancelacionLayout);
        dialogCancelacionLayout.setHorizontalGroup(dialogCancelacionLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(dialogCancelacionLayout.createSequentialGroup().addGroup(dialogCancelacionLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(dialogCancelacionLayout.createSequentialGroup().addContainerGap().addGroup(dialogCancelacionLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, -1, 610, 32767).addComponent(jLabel6, GroupLayout.Alignment.LEADING, -1, 610, 32767))).addGroup(dialogCancelacionLayout.createSequentialGroup().addGap(263, 263, 263).addComponent(BotonEnviarCancelacion))).addContainerGap()));

        dialogCancelacionLayout.setVerticalGroup(dialogCancelacionLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(dialogCancelacionLayout.createSequentialGroup().addContainerGap().addComponent(jLabel6, -2, 40, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jScrollPane1, -2, 178, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(BotonEnviarCancelacion).addContainerGap(-1, 32767)));

        setDefaultCloseOperation(2);
        setMinimumSize(new Dimension(1024, 768));
        setPreferredSize(new Dimension(1024, 768));

        jLabel2.setFont(new Font("Showcard Gothic", 1, 36));
        jLabel2.setHorizontalAlignment(0);
        jLabel2.setText("<html><center>PRUEBA DE FRENOMETRO PARA MOTOCICLETAS</center></html>");

        jLabel1.setHorizontalAlignment(0);
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/Imagenes/SoltelecIcon.png")));

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel2, -2, 0, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel1, -2, 294, -2).addGap(6, 6, 6)));

        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel1, -1, 146, 32767).addComponent(jLabel2)).addContainerGap()));

        LabelAviso.setFont(new Font("Comic Sans MS", 0, 36));
        LabelAviso.setHorizontalAlignment(0);
        LabelAviso.setOpaque(true);

        jPanel5.setLayout(new GridLayout(2, 0));

        LabelEje.setFont(new Font("Tahoma", 0, 36));
        LabelEje.setHorizontalAlignment(0);
        LabelEje.setText("Eje");
        jPanel5.add(LabelEje);

        jLabel3.setFont(new Font("Comic Sans MS", 0, 48));
        jLabel3.setHorizontalAlignment(0);
        jLabel3.setText("Iniciando Sistema...");
        jPanel5.add(jLabel3);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(LabelAviso, -2, 305, -2).addGap(23, 23, 23)).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel5, -1, 1004, 32767)));

        jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addComponent(LabelAviso, -2, 113, -2).addContainerGap(208, 32767)).addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(jPanel5, -1, 299, 32767).addContainerGap())));

        jProgressBar1.setForeground(new Color(255, 0, 51));
        jProgressBar1.setMaximum(10);
        jProgressBar1.setStringPainted(true);

        PanelBotones.setLayout(new GridLayout(1, 0));

        BotonEmpezar.setFont(new Font("Imprint MT Shadow", 1, 24));
        BotonEmpezar.setText("INICIAR");
        BotonEmpezar.setPreferredSize(new Dimension(100, 41));
        BotonEmpezar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BotonEmpezarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonEmpezar);

        BotonContinuar.setFont(new Font("Imprint MT Shadow", 1, 24));
        BotonContinuar.setText("CONTINUAR");
        BotonContinuar.setEnabled(false);
        BotonContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BotonContinuarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonContinuar);

        BotonFinalizar.setFont(new Font("Imprint MT Shadow", 1, 24));
        BotonFinalizar.setText("FINALIZAR");
        BotonFinalizar.setEnabled(false);
        BotonFinalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BotonFinalizarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonFinalizar);

        BotonCancelar.setFont(new Font("Imprint MT Shadow", 1, 24));
        BotonCancelar.setText("CANCELAR");
        BotonCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                BotonCancelarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonCancelar);

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(PanelBotones, -1, 994, 32767).addComponent(jProgressBar1, -1, -1, 32767)).addContainerGap()));

        jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addComponent(PanelBotones, -2, 80, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jProgressBar1, -2, 39, -2).addContainerGap()));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap(-1, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jPanel1, -1, -1, 32767).addComponent(jPanel4, GroupLayout.Alignment.TRAILING, -1, -1, 32767).addComponent(jPanel2, -1, -1, 32767)).addContainerGap(-1, 32767)));

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(jPanel1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2, -2, -1, -2).addGap(36, 36, 36).addComponent(jPanel4, -1, -1, 32767).addContainerGap(-1, 32767)));

        pack();
    }

    Timer timer1 = new Timer(500, this);

    public void t1enabled(boolean a) {
        if (a) {
            timer1.start();
        } else {
            timer1.stop();
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
                    } else {
                        LabelAviso.setBackground(jPanel1.getBackground());
                    }
                    break;
                default:
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
    private JButton BotonCancelar;
    private JButton BotonContinuar;
    private JButton BotonEmpezar;
    private JButton BotonEnviarCancelacion;
    private JButton BotonFinalizar;
    private JLabel LabelAviso;
    private JLabel LabelEje;
    private JPanel PanelBotones;
    private JDialog dialogCancelacion;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel6;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JProgressBar jProgressBar1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;

    private void BotonEmpezarActionPerformed(ActionEvent evt) {
        try {
            tecladoactivado = false;
            comandoSTOP();
            if (!PruebaConexion()) {
                int opcion = JOptionPane.showOptionDialog(this, "La tarjeta no se encuentra conectada o inicializada. Desea conectarla", "Configuración", 0, 3, null, new Object[]{"Si", "No"}, "Si");
                if (opcion == 0) {
                    Tarjeta tar = new Tarjeta();
                    tar.setPuerto(puerto);
                    tar.IniciaTarjeta();
                    if (tar.getNumconex() == 3) {
                        JOptionPane.showMessageDialog(null, "Llame a servicio técnico", "Error", 0);

                        puerto.close();
                        dispose();
                    } else {
                        try {
                            Thread.currentThread();
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
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
                jLabel3.setText("Calibrando ceros...");
                comandoFREN();
                t1enabled(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", 0);
        }
    }

    private void BotonContinuarActionPerformed(ActionEvent evt) {
        BotonContinuar.setEnabled(false);
        tecladoactivado = false;

        jLabel3.setText("<html>Por favor avance la motocicleta hasta que el eje " + ejemedido + " este sobre la plancha</html>");

        comandoFREN();

        hilo1.start();
    }

    private void BotonFinalizarActionPerformed(ActionEvent evt) {
        comandoSTOP();
        puerto.close();
        long s = fuerzas.size();
        double fuerzat = 0.0;
        double pesot = 0.0;
        for (t = 0; t < s; t += 1) {
            pesot += (pesos.get(t));
            fuerzat += (fuerzas.get(t));
            System.out.println("peso " + (pesos.get(t)) * spanp + " fuerza " + (fuerzas.get(t)) * spanf);
        }
        System.out.println("peso " + pesot * spanp + " fuerza " + fuerzat * spanf);
        double fuerzadel = (fuerzas.get(0)) * spanf;
        double fuerzatra = (fuerzas.get(1)) * spanf;
        double pesodel = (pesos.get(0)) * spanp;
        double pesotra = (pesos.get(1)) * spanp;
        eficacia = (fuerzat * spanf / (pesot * spanp) * 100.0D);
        if (eficacia > 100.0) {
            eficacia = 100.0;
        }
        System.out.println("la eficacia del sistema de frenos fue de: " + eficacia + " %");
        try {
            RegistrarMedidas();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        boolean aprobada = false;
        if (eficacia >= permisibleprueba) {
            CalificacionPrueba = "Aprobado";
            aprobada = true;
        } else {
            CalificacionPrueba = "No Aprobado";
            aprobada = false;
        }
        dispose();
    }

    void RegistrarMedidas()throws SQLException, ClassNotFoundException 
    {
        Class.forName("com.mysql.jdbc.Driver");     
        System.out.println(" ME VOY A CONECTAR A LA SIGUIENTE URL:" + com.soltelec.modulopuc.configuracion.modelo.Conexion.getUrl());
        Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContraseña());
        System.out.println("Conectandose con:jdbc:mysql://" + Conexion.getUrl());
        conexion.setAutoCommit(false);
        String statement = "INSERT INTO db_cda.medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 5008);
        instruccion.setDouble(2, (fuerzas.get(0)) * spanf);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 5009);
        instruccion.setDouble(2, (fuerzas.get(1)) * spanf);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 5000);
        instruccion.setDouble(2, (pesos.get(0)) * spanp);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 5001);
        instruccion.setDouble(2, (pesos.get(1)) * spanp);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();
        instruccion.setInt(1, 5024);
        instruccion.setDouble(2, eficacia);
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        String serialEquipo = "";
        try {
            serialEquipo = PruebasDAO.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }

        tramaAuditoria = "{\"eficaciaTotal\":\"".concat(String.valueOf(eficacia)).concat("\",").concat("\"eficaciaAuxiliar\":\"").concat(String.valueOf(" ")).concat("\",");
        tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(0 + 1)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(0 + 1)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(0 + 1)).concat("Derecho\":\"").concat(String.valueOf(fuerzas.get(0) * spanf)).concat("\",").concat("\"pesoEje").concat(String.valueOf(0 + 1)).concat("Derecho\":\"").concat(String.valueOf(pesos.get(0) * spanp)).concat("\",").concat("\"eje").concat(String.valueOf(0 + 1)).concat("Desequilibrio\":\"").concat(String.valueOf(" ")).concat("\",");
        tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(2)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(2)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(2)).concat("Derecho\":\"").concat(String.valueOf(fuerzas.get(1) * spanf)).concat("\",").concat("\"pesoEje").concat(String.valueOf(2)).concat("Derecho\":\"").concat(String.valueOf(pesos.get(1) * spanp)).concat("\",").concat("\"eje").concat(String.valueOf(2)).concat("Desequilibrio\":\"").concat(String.valueOf(" ")).concat("\",");

        for (int k = 3; k < 6; k++) {
            tramaAuditoria = tramaAuditoria.concat("\"fuerzaEje").concat(String.valueOf(k)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(k)).concat("Izquierdo\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"fuerzaEje").concat(String.valueOf(k)).concat("Derecho\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"pesoEje").concat(String.valueOf(k)).concat("Derecho\":\"").concat(String.valueOf(" ")).concat("\",").concat("\"eje").concat(String.valueOf(k)).concat("Desequilibrio\":\"").concat(String.valueOf(" ")).concat("\",");
        }
        tramaAuditoria = tramaAuditoria.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPrueba)).concat("\"}");

        String statement2 = "UPDATE db_cda.pruebas SET Finalizada = 'Y',Aprobada=?,Abortada='N',Autorizada = 'N', usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
        instruccion2.setLong(2, idUsuario);
        if (eficacia >= permisibleprueba) {
            instruccion2.setString(1, "Y");
        } else {
            instruccion2.setString(1, "N");
        }
        instruccion2.setString(3, serialEquipo);
        instruccion2.setLong(4, idPrueba);
        int executeUpdate = instruccion2.executeUpdate();
        if (eficacia < permisibleprueba) {
            String strDefecto = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES (?,?)";
            PreparedStatement psDefecto = conexion.prepareStatement(strDefecto);
            psDefecto.setInt(1, 54010);
            psDefecto.setLong(2, idPrueba);

            psDefecto.executeUpdate();
        }
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        System.out.println("Datos enviados");
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.CANCELADO, "", serialEquipo);
        // FIN EVENTOS SICOV
    }

    private void registrarCancelacion(String comentario)
            throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String serialEquipo = "";
        try {
            serialEquipo = PruebasDAO.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
        }
        Connection conexion = DriverManager.getConnection("jdbc:mysql://" + URLServidor + "/db_cda", "fabian", "passfabian");

        String statement = "UPDATE db_cda.pruebas SET Finalizada = 'Y',Aprobada='N',Autorizada = 'N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setString(1, comentario);
        instruccion.setLong(2, idUsuario);
        instruccion.setString(4, serialEquipo);
        instruccion.setLong(5, idPrueba);
        java.util.Date d = new java.util.Date();
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        instruccion.setDate(3, new java.sql.Date(c.getTimeInMillis()));
        int n = instruccion.executeUpdate();

        conexion.setAutoCommit(true);
        conexion.close();
        // INICIO EVENTOS SICOV
        //ClienteSicov.eventoPruebaSicov(idPrueba.intValue(), EstadoEventosSicov.CANCELADO, comentario, serialEquipo);
        // FIN EVENTOS SICOV
        System.out.println("Datos enviados");
    }

    private int consultarPermisible()
            throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");

        Connection conexion = DriverManager.getConnection("jdbc:mysql://" + "localhost" + "/db_cda", "root", "admin");
        String statement = "SELECT permisibles.Valor_minimo FROM permisibles WHERE permisibles.id_permisible = 15";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        ResultSet rs = instruccion.executeQuery();
        rs.first();
        int valorPermisible = rs.getInt(1);
        System.out.println("Valor permisible:" + valorPermisible);
        return valorPermisible;
    }

    private void BotonCancelarActionPerformed(ActionEvent evt) {
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
            motivoCancelacion = jTextArea1.getText();
            registrarCancelacion(motivoCancelacion);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(dialogfrenmotos2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void BotonEnviarCancelacionActionPerformed(ActionEvent evt) {
        motivoCancelacion = jTextArea1.getText();

        System.out.println("La prueba fue cancelada por: " + motivoCancelacion);
        dialogCancelacion.dispose();
        dispose();
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

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialogfrenmotos2 dialog = new dialogfrenmotos2(new JFrame(), true, 1, 1, 1);
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        numtimer1 += 1;
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
                pasomedfren = ((byte) (pasomedfren + 1));
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

    class EsperaPeso
            extends Thread {

        EsperaPeso() {
        }

        @Override
        public void run() {
            try {
                LabelAviso.setText("PLANCHA");
                timeraviso.start();
                for (;;) {
                    Thread.sleep(1000);
                    CapturarDatos();
                    double pesomed = CalcularMediana(Datos2);
                    System.out.println("peso medido " + (pesomed - valcalcero2) * spanp + " N " + ejemedido);
                    Datos1.clear();
                    Datos2.clear();
                    if (((pesomed - valcalcero2) * spanp >= umbral_peso) && (ejemedido.equals("Delantero"))) {
                        break;
                    }
                    try {
                        if (((pesomed - valcalcero2) * spanp >= (pesos.get(0)) * spanp + umbral_peso) && (ejemedido.equals("Trasero"))) {
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

    class EsperaFuerza
            extends Thread {

        EsperaFuerza() {
        }

        @Override
        public void run() {
            try {
                numtimer2 = 0;
                sleep(500);
                double pesomed;
                for (;;) {
                    sleep(500);
//                    dialogfrenmotos2.access$1808(dialogfrenmotos2.this);
                    CapturarDatos();
                    pesomed = CalcularMediana(Datos2);
                    if (pesomed >= valcalcero2) {
                        System.out.println("peso medido de nuevo" + (pesomed - valcalcero2) * spanp + " N " + ejemedido);
                        Datos1.clear();
                        Datos2.clear();
                        if ((pesomed - valcalcero2) * spanp <= valcalcero2 * spanp * 0.05D) {
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
                Thread.currentThread();
                Thread.sleep(2500);
                LabelAviso.setText("");
                timeraviso.stop();
                LabelAviso.setBackground(jPanel1.getBackground());
                comandoFREN();
                jLabel3.setText("<html>Por favor avance con la motocicleta con velocidad</html>");
                Thread.currentThread();
                Thread.sleep(500);
                LabelAviso.setText("FRENE");
                timeraviso.start();
                for (;;) {
                    Thread.sleep(100);
                    CapturarDatos();
                    pesomed = CalcularMediana(Datos2);
                    System.out.println("peso medido otra vez " + (pesomed - valcalcero2) * spanp + " N " + ejemedido);
                    Datos1.clear();
                    Datos2.clear();
                    if ((pesomed - valcalcero2) * spanp >= (pesos.get(0)) * 0.8) {
                        break;
                    }
                }
                Random r = new Random();
                double velo = r.nextDouble() * 2.0;
                int veloint = (int) velo + 4;
                comandoFREN();
                Thread.sleep(2000);
                CapturarDatos();
                MedirFuerza();
                Datos1.clear();
                Datos2.clear();
                LabelAviso.setText("");
                timeraviso.stop();
                LabelAviso.setBackground(jPanel1.getBackground());
                if (ejemedido.equals("Trasero")) {
                    jLabel3.setText("Velocidad de prueba: " + veloint + " Km/h    OK");
                }
                Thread.sleep(2000);
                if (ejemedido.equals("Trasero")) {
                    BotonFinalizar.setEnabled(true);
                    pesomed = (pesos.get(1)) - (pesos.get(0));
                    pesos.set(1, pesomed);
                    jLabel3.setText("Prueba completada. Presione Finalizar");
                } else if (ejemedido.equals("Delantero")) {
                    ejemedido = "Trasero";
                    LabelEje.setText("Eje " + ejemedido);
                    jLabel3.setText("<html>Por favor avance hasta que toda la motocicleta este sobre la plancha</html>");

                    hilo3.start();
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException("Interrupted", ex);
            }
        }
    }
}
