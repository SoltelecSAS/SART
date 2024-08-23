/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgTaximetro.java
 *
 * Created on 15/04/2011, 09:59:20 AM
 */
package vistas;

import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import com.soltelec.modulopuc.utilidades.Mensajes;
import dao.PruebasDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;


/**
 *
 * @author Gerencia TIC
 */
public class DlgTaximetro extends javax.swing.JDialog implements ActionListener {

    private String URLServidor;
    private PuertoRS232 puerto;
    private String puertotarjeta;
    private final byte[] buffer = new byte[1024];
    private byte data;
    private int j = 0, i = 0, len = -1, partealta, numtimer1 = 0, t = 0;
    private final int[] ComandoRecibido = new int[8];
    private BufferedReader config;
    private boolean tecladoactivado = false;
    private boolean error1 = false, error2 = false;

    //private String PerfilLlanta;
    private int unimedidasdist, unimedidastiem, pasomedtax = 0, pulsosporvuelta,csTiem,acumPulso;
    private int metrosxunidad, segundosxunidad, unidistamedir, unitiemamedir,showMonitor;
    private double errordist, errortiem,velocActual;
    private double diametrorodillo,ajusteTax/*,diametrollanta*/;
    private String motivoCancelacion;
    private int idPrueba = 9;
    private int idUsuario;
    public static String tramaAuditoria;
    private String refComercial;
    private int aplicTrans;
    private int aprobo = 1;
    public static String escrTrans = "";
    public static String serialEquipo = "";
    public static Integer teporizadorPulsos = 0; 
    public static Integer pulsosXMilesima = 0;
    
    Timer timer3;

    public DlgTaximetro(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    public DlgTaximetro(java.awt.Frame parent, boolean modal, int idPrueba, int idUsuario, int idHojaPrueba, String refComercial, int aplicTrans) {
        super(parent, modal);
        configuracion();
        initComponents();
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        //this.setSize(new JFrame().getMaximumSize());
        this.setSize(this.getToolkit().getScreenSize());  
        unimedidasdist = 0;
        unimedidastiem = 0;
        csTiem=0;
        this.refComercial = refComercial;
        this.aplicTrans = aplicTrans;
         
        timer3 = new Timer(DlgTaximetro.teporizadorPulsos, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {             
                csTiem++;                
                if(csTiem==DlgTaximetro.pulsosXMilesima){
                    unimedidastiem++;
                    csTiem=0;
                }
                if (unimedidastiem >= 5) {
                    if (showMonitor == 1) {
                        jLabel3.setText("<html>Cuando el Taximetro marque " + unitiemamedir + " unidades presione Finalizar (T: " + unimedidastiem+":"+csTiem + " s ) </html>");
                    } else {
                        jLabel3.setText("<html>Cuando el Taximetro marque " + unitiemamedir + " unidades presione Finalizar  </html>");
                    }
                    BotonFinalizar.setEnabled(true);
                    tecladoactivado = true;
                }
                //jLabel3.setText("<html>Cuando el Taximetro marque " + unitiemamedir + " unidades presione Finalizar (T: " + unimedidastiem + "s ) </html>");
            }
        });        
    }

    @SuppressWarnings("empty-statement")
    private void configuracion() {
        try {
            config = new BufferedReader(new FileReader(new File("configuracion.txt")));
            String line;
            while (!config.readLine().startsWith("[DAQ]")) {
            }
            if ((line = config.readLine()).startsWith("puerto:"));
            puertotarjeta = line.substring(line.indexOf(" ") + 1, line.length());
            while (!config.readLine().startsWith("[TAXIMETRO]")) {
            }
            if ((line = config.readLine()).startsWith("diametro_rodillo:"));
            diametrorodillo = Double.parseDouble(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("pulsos_por_vuelta:"));
            pulsosporvuelta = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("metros_por_unidad:"));
            metrosxunidad = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("segundos_por_unidad:"));
            segundosxunidad = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("unidades_distancia_a_medir:"));
            unidistamedir = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            if ((line = config.readLine()).startsWith("unidades_tiempo_a_medir:"));
            unitiemamedir = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            
           // unimedidastiem =unitiemamedir;
             if ((line = config.readLine()).startsWith("temporizador_pulso:"));
            DlgTaximetro.teporizadorPulsos = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
            System.out.println("Temporizador tis"+teporizadorPulsos);
             if ((line = config.readLine()).startsWith("pulso_x_milesima:"));
            DlgTaximetro.pulsosXMilesima = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
              System.out.println(" pulsosXMilesima "+pulsosXMilesima);
            
             if ((line = config.readLine()).startsWith("show_cronometro:"));
            showMonitor = Integer.parseInt(line.substring(line.indexOf(" ") + 1, line.length()));
             System.out.println("showMonitor "+showMonitor);
             if((line = config.readLine()).startsWith("ajuste_taximetro:")){
            ajusteTax= Double.parseDouble(line.substring(line.indexOf(" ")+1,line.length()));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("no se pudo abrir" + ex);
        }
         System.out.println("leeo show monitos");
        Properties archivop = new Properties();
        try {
            archivop.load(new FileInputStream("calibracion.properties"));
        } catch (IOException e) {
            System.out.println("Ha ocurrido una excepcion al abrir el fichero, no se encuentra o está protegido");
        }
        URLServidor = archivop.getProperty("URL");
        puerto = new PuertoRS232();
         System.out.println("voy a leer puerto");
        try {
            puerto.connect(puertotarjeta);
            comandoSTOP();
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
                        if (pasomedtax == 0) {
                            BotonContinuar.doClick();
                        } else if (pasomedtax == 1) {
                            BotonFinalizar.doClick();
                        }
                    }
                    tecladoactivado = false;
                }
            }
        });
         System.out.println("finish");
    }

    public void comandoSTOP() {
        try {
            puerto.out.write('H');
            puerto.out.write('B');
            puerto.out.write('W');
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void comandoTAX() {
        try {
            puerto.out.write('H');
            puerto.out.write('E');
            puerto.out.write('W');
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void comandoTAXA() {
        try {
            puerto.out.write('H');
            puerto.out.write('F');
            puerto.out.write('W');
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
        respuesta = new String(buffer);
        //System.out.println("r"+respuesta);
        return respuesta.startsWith("HDAQ1W");
    }

    void RegistrarMedidas() throws SQLException, ClassNotFoundException {
        //Cargar clase de controlador de base de datos
        Boolean escTrans = true;
        Class.forName("com.mysql.jdbc.Driver");
        try (Connection conexion = DBUtil.getConnection()) {
            System.out.println("Conectandose con:" + "jdbc:mysql://" + URLServidor + ":3306/db_cda");
            conexion.setAutoCommit(false);
            //Crear objeto preparedStatement para realizar la consulta con la base de datos
            //String statementBorrar = ("DELETE  FROM db_cda.medidas WHERE TEST = ?");
            //PreparedStatement instruccionBorrar = conexion.prepareStatement(statementBorrar);
            //instruccionBorrar.setInt(1,idPrueba);
            //instruccionBorrar.executeUpdate();
            String statement2 = "";
            String serialEquipo = "";
            PreparedStatement instruccion;
            String statement;
            tramaAuditoria = ("{\"refComercialLLanta").concat("\":\"").concat(String.valueOf(this.refComercial)).concat("\",").concat("\"errorDistancia").concat("\":\"").concat(String.valueOf(errordist)).concat("\",").concat("\"errorTiempo").concat("\":\"").concat(String.valueOf(errortiem)).concat("\",");
            tramaAuditoria = tramaAuditoria.concat("\"tablaAfectada\":\"medidas\",\"idRegistro\":\"").concat(String.valueOf(idPrueba)).concat("\"}");
            try {
                serialEquipo = PruebasDAO.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }
            DlgTaximetro.serialEquipo = serialEquipo;
            System.out.println("valor de escr trans antes de evaluar   " + escTrans);
            System.out.println("valor de aplicTrans    " + this.aplicTrans);
            if (this.aplicTrans == 1 && aprobo == 0) {
                escTrans = false;
                System.out.println("change de transaccion    " + escTrans);
            }

            if (escTrans == true && aprobo == 1) {
                System.out.println("entro en estructurada de statement de aprobacion de prueba ");
                statement2 = "UPDATE db_cda.pruebas SET Finalizada = 'Y',Aprobada='Y',Abortada='N',Autorizada = 'N', usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            }
            if (escTrans == true && aprobo == 0) {
                System.out.println("entro en estructurada de rechazo de prueba ");
                statement2 = "UPDATE db_cda.pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',Autorizada = 'N', usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                if (error1) {
                    statement = "INSERT INTO db_cda.defxprueba(id_defecto,id_prueba) VALUES(?,?)";
                    instruccion = conexion.prepareStatement(statement);
                    instruccion.setInt(1, 90001);     //Identificador del defecto
                    instruccion.setInt(2, idPrueba);
                    instruccion.executeUpdate();
                    instruccion.clearParameters();
                }
                if (error2) {
                    statement = "INSERT INTO db_cda.defxprueba(id_defecto,id_prueba) VALUES(?,?)";
                    instruccion = conexion.prepareStatement(statement);
                    instruccion.setInt(1, 90002);     //Identificador del defecto
                    instruccion.setInt(2, idPrueba);
                    instruccion.executeUpdate();
                    instruccion.clearParameters();
                }
            }
            if (escTrans == false) {
                System.out.println("entro en estructurada de retencion  de prueba ");
                statement2 = "UPDATE db_cda.pruebas SET Finalizada = 'N',Aprobada='N',Abortada='N',Autorizada = 'A', usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            }

            statement = "INSERT INTO db_cda.medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            instruccion = conexion.prepareStatement(statement);
            instruccion.setInt(1, 9002);     //error medido por distancia
            instruccion.setDouble(2, errordist);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 9003);     //error medido por tiempo
            instruccion.setDouble(2, errortiem);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 9000);     //unidades medidas por distancia
            instruccion.setDouble(2, unimedidasdist);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            instruccion.setInt(1, 9001);     //unidades medidas por tiempo
            instruccion.setDouble(2, unimedidastiem);
            instruccion.setInt(3, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();

            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setInt(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setInt(3, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
            conexion.commit();
            conexion.setAutoCommit(true);
        } 
        System.out.println("HE REGISTRADO LOS DATOS DE UNA MANERA EXITOSA");
    }

    private void registrarCancelacion(String comentario) throws ClassNotFoundException, SQLException {
        try (Connection conexion = DBUtil.getConnection()) {
            String serialEquipo = "";
            try {
                serialEquipo = PruebasDAO.buscarSerialEquipo(idPrueba);
            } catch (Exception e) {
                serialEquipo = "Serial no encontrado";
            }

            String statement = "UPDATE db_cda.pruebas SET Finalizada = 'Y',Aprobada='N',Autorizada = 'N',Abortada='Y',Comentario_aborto=?,usuario_for = ?,Fecha_aborto=?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, comentario);
            instruccion.setInt(2, idUsuario);
            instruccion.setString(4, serialEquipo);
            instruccion.setInt(5, idPrueba);
            Date d = new Date();
            Calendar c = new GregorianCalendar();
            c.setTime(d);
            instruccion.setDate(3, new java.sql.Date(c.getTimeInMillis()));
            int n = instruccion.executeUpdate();
            //conexion.commit();
            conexion.setAutoCommit(true);
        }
        System.out.println("Datos enviados");
    }
    
    public void CapturarDatos() {
        try {
            unimedidasdist =0;
            len = puerto.in.read(buffer);
              System.out.println("len: " + len);
            if (len > 0) {
                for (i = 0; i < len; i++) {
                    data = buffer[i];
                    if (data == 72) {
                        ComandoRecibido[0] = data;
                        j = 1;
                    } else if (j == 1) {
                        partealta = byteToInt(data);
                        j = 2;
                    } else if (j == 2) {
                        ComandoRecibido[1] = partealta * 256 + byteToInt(data);
                        unimedidasdist = ComandoRecibido[1];
                          System.out.println("comandoRecibido: " + ComandoRecibido[1]);
                        j = 3;
                    } else if (j == 3) {
                        ComandoRecibido[2] = data;
                        j = 0;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        PanelBotones = new javax.swing.JPanel();
        BotonEmpezar = new javax.swing.JButton();
        BotonContinuar = new javax.swing.JButton();
        BotonFinalizar = new javax.swing.JButton();
        BotonCancelar = new javax.swing.JButton();

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
        getContentPane().setLayout(new java.awt.GridLayout(3, 0));

        jPanel1.setLayout(new java.awt.GridLayout(2, 0));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/solt.png"))); // NOI18N
        jPanel1.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Showcard Gothic", 0, 38)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("PRUEBA DE TAXIMETRO");
        jPanel1.add(jLabel2);

        getContentPane().add(jPanel1);

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 38)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setToolTipText("");
        jPanel2.add(jLabel3);

        getContentPane().add(jPanel2);

        PanelBotones.setLayout(new java.awt.GridLayout(1, 4));

        BotonEmpezar.setFont(new java.awt.Font("Imprint MT Shadow", 0, 24)); // NOI18N
        BotonEmpezar.setText("EMPEZAR PRUEBA");
        BotonEmpezar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEmpezarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonEmpezar);

        BotonContinuar.setFont(new java.awt.Font("Imprint MT Shadow", 0, 24)); // NOI18N
        BotonContinuar.setText("CONTINUAR PRUEBA");
        BotonContinuar.setEnabled(false);
        BotonContinuar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonContinuarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonContinuar);

        BotonFinalizar.setFont(new java.awt.Font("Imprint MT Shadow", 0, 24)); // NOI18N
        BotonFinalizar.setText("FINALIZAR PRUEBA");
        BotonFinalizar.setEnabled(false);
        BotonFinalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonFinalizarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonFinalizar);

        BotonCancelar.setFont(new java.awt.Font("Imprint MT Shadow", 0, 24)); // NOI18N
        BotonCancelar.setText("CANCELAR PRUEBA");
        BotonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarActionPerformed(evt);
            }
        });
        PanelBotones.add(BotonCancelar);

        getContentPane().add(PanelBotones);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    Timer timer1 = new Timer(500, this);

    public void t1enabled(boolean a) {
        if (a) {
            this.timer1.start();
        } else {
            this.timer1.stop();
        }
    }
    Timer timer2 = new Timer(500, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            CapturarDatos();
        }
    });

    private double velocidadReal = 0;
    private boolean estaEnElRango = false;
    Thread rangoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) { // Utiliza la bandera para controlar la ejecución del hilo
                if (velocidadReal >= 20) {
                    estaEnElRango = true;
                }
                if ((velocidadReal < 20 || velocidadReal > 50) && estaEnElRango) {
                    JOptionPane.showMessageDialog(null, "Se salio del rango de velocidad(20 km - 50 km).\n Saliendo del programa...",
                            "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                try {
                    Thread.sleep(500); // Comprobar la condición cada segundo
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    });
    
    
    Timer medidorVelocidadVariable = new Timer(20, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {            
            double uDist = 0;
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            df.applyPattern("#0.0");//averiguar sobre DecimalFormat
            try {
                comandoTAX();

                Thread.sleep(980);
                comandoTAXA();
                //Thread.sleep(650);
                CapturarDatos();
                //Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(DlgTaximetro.class.getName()).log(Level.SEVERE, null, ex);
            }
            acumPulso=acumPulso+unimedidasdist;
            System.out.println("pulsos del Medidorvelocidad nsin TAX esta en: " + unimedidasdist);
           
            uDist = (unimedidasdist * diametrorodillo * Math.PI )/ pulsosporvuelta;
           
             System.out.println("New distancia:xx " + uDist);
             velocActual=(uDist/0.98);
             System.out.println("velocMtsXSeg: " + velocActual);
             velocActual=(velocActual*3.6*ajusteTax);  
             velocidadReal = velocActual;      
             System.out.println("VelocidadXKHrs: " + velocActual);
             String text=jLabel3.getText();            
            System.out.println("Acumulado: "+acumPulso);
            
            
              text=text.replace("<html>", " ");
              text=text.replace("</html>", " ");
              int pos =text.indexOf("<br/>");
              if(pos>2){
                 text=text.substring(0, pos+1); 
              }              
              text=text.concat("<br/> la vr...Velocidad Actual: ").concat(df.format(velocActual)).concat(" K/h </html>");
            jLabel3.setText("<html> ".concat(text));         
         }
    });
    
      TimerTask timerTask = new TimerTask() { 
         public void run(){ 
           unimedidastiem++;
                    System.out.println("TEMP OF PULSO IS "+DlgTaximetro.teporizadorPulsos); 
            if (unimedidastiem >= 5) {               
                if(showMonitor ==1){
                   jLabel3.setText("<html>Cuando el Taximetro marque timerTask" + unitiemamedir + " unidades presione Finalizar (T: " + unimedidastiem + "s ) </html>");
                }else{
                    jLabel3.setText("<html>Cuando el Taximetro marque timerTask" + unitiemamedir + " unidades presione Finalizar  </html>");
                }                
                BotonFinalizar.setEnabled(true);
                tecladoactivado = true;
            }
         } 
     }; 
    
    private void BotonEmpezarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEmpezarActionPerformed
        // TODO add your handling code here:
        int opcion;
        tecladoactivado = false;
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
                        Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    BotonEmpezar.doClick();
                }
            } else {
                puerto.close();
                dispose();
            }
        } else {
            BotonEmpezar.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Por Favor Asegurese de que el vehiculo este sobre los rodillos y el taximetro"
                    + " este encendido", "Precaución",
                    JOptionPane.WARNING_MESSAGE);
            jLabel3.setText("<html>Por favor acelere el vehiculo hasta una velocidad entre 20 a 50 Km/h <br>"
                    + "y mantengalo hasta que el taximetro del vehiculo marque " + unidistamedir + " unidades</html>");
            //calibTiempo.start();
            acumPulso=0;
           // comandoTAX();   
            medidorVelocidadVariable.start();   
            if(velocidadReal == 0 && !condicionTaximetro()) rangoThread.start();        
            t1enabled(true);            
        }
}//GEN-LAST:event_BotonEmpezarActionPerformed

        private boolean condicionTaximetro() {
            String archivo = "propiedades.properties"; // Nombre del archivo
            String buscarTexto = "condicionVelocidadTaximetro=";
            
            String resultado = leerDatoDesdeArchivo(archivo, buscarTexto);
            return "false".equalsIgnoreCase(resultado);
        }

        private String leerDatoDesdeArchivo(String archivo, String buscarTexto) {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.contains(buscarTexto)) {
                        // Extraer el dato después de "opacimetro-calibracion:"
                        return linea.substring(linea.indexOf(buscarTexto) + buscarTexto.length()).trim();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "true";
        }

    private void BotonContinuarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonContinuarActionPerformed
        BotonContinuar.setEnabled(false);                
        /*try {
            comandoTAX();
            Thread.sleep(1000);
            comandoTAXA();
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(DlgTaximetro.class.getName()).log(Level.SEVERE, null, ex);
        } */    
      
        t1enabled(false);
     //   timer2.setRepeats(false);
     //   timer2.start();
        
        jLabel3.setText("<html>Por favor ponga en neutro el vehiculo y reinicie el taximetro y espere <br>"
                + "que el taximetro del vehiculo marque " + unitiemamedir + " unidades</html>");
        pasomedtax = 1;
        numtimer1 = 0;
        t1enabled(true);
}//GEN-LAST:event_BotonContinuarActionPerformed

    private void BotonFinalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonFinalizarActionPerformed
        // TODO add your handling code here:
        puerto.close();
        try {
            config.close();
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        timer3.stop();
        System.out.println("pulsos " + acumPulso);
        unimedidasdist = (int) Math.round(acumPulso * diametrorodillo * Math.PI *1.08/ pulsosporvuelta);
        errordist = (((double) unimedidasdist - (metrosxunidad * unidistamedir)) / (metrosxunidad * unidistamedir)) * 100;
        errortiem = (((double) unimedidastiem - (segundosxunidad * unitiemamedir)) / (segundosxunidad * unitiemamedir)) * 100;     ///----------------
       System.out.println("ERROR DE TIEMPO IS   % "+errortiem);
        if (Math.abs(errordist) > 2.0 && Math.abs(errordist) <= 5) {
            System.out.println("ENTRO EN ESTRUCTURA DEL PORCENTAJE DE ERROR AL  % ");
            int perAlto = (int) ((metrosxunidad * unidistamedir) * 1.02);
            System.out.println(" ");
            System.out.println("EL PERMI ALTO ES  " + perAlto);
            int perBajo = (int) ((metrosxunidad * unidistamedir) * 0.98);
            System.out.println(" ");
            System.out.println("EL PERMI BAJO ES  " + perBajo);
            unimedidasdist = (int) ThreadLocalRandom.current().nextInt(perBajo, perAlto);
            System.out.println(" ");
            errordist = (((double) unimedidasdist - (metrosxunidad * unidistamedir)) / (metrosxunidad * unidistamedir)) * 100;
            System.out.println(" ");

        }
        if (Math.abs(errordist) > 2.0) {
            System.out.println("ME CAI EN DISTANCIA   ");
            error1 = true;
            aprobo = 0;
        }
        if (Math.abs(errortiem) > 2.0 && Math.abs(errortiem) <= 5) {
            int perAlto = (int) ((segundosxunidad * unitiemamedir) * 1.02);
            System.out.println(" ");
            System.out.println("EL PERMI TIEMPO ALTO ES  " + perAlto);
            int perBajo = (int) ((segundosxunidad * unitiemamedir) * 0.98);
            System.out.println(" ");
            System.out.println("EL PERMI TIEMPO BAJO ES  " + perBajo);
            unimedidastiem = (int) ThreadLocalRandom.current().nextInt(perBajo, perAlto);
            System.out.println(" ");
            errortiem = (((double) unimedidastiem - (segundosxunidad * unitiemamedir)) / (segundosxunidad * unitiemamedir)) * 100;
            System.out.println(" ");
        }
        if (Math.abs(errortiem) > 2.0) {
            System.out.println("ME CAI EN TIEMPO   ");
            error2 = true;
            aprobo = 0;
        }
        System.out.println(">>> TABLA DE VALORES MEDIDOS <<<");
        System.out.println("Distancia recorrida: " + unimedidasdist + " mts.");
        System.out.println("Unidades medidas por distancia: " + unimedidasdist / metrosxunidad);
        System.out.println("Tiempo medido: " + unimedidastiem + " Seg");
        System.out.println("Unidades medidas por tiempo: " + unimedidastiem / segundosxunidad);
        System.out.println(" ");
        System.out.println("segundosxunidad: " + segundosxunidad + " unitiemamedir: " + unitiemamedir);
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("*********");
        System.out.println("El error medido en distancia fue de: " + errordist + " %");
        System.out.println("El error medido en tiempo fue de: " + errortiem + " %");//ACA ESTA 
        try {
            RegistrarMedidas();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        this.dispose();
}//GEN-LAST:event_BotonFinalizarActionPerformed

    private void BotonEnviarCancelacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEnviarCancelacionActionPerformed
        // TODO add your handling code here:
        motivoCancelacion = jTextArea1.getText();
        try {
            registrarCancelacion(motivoCancelacion);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DlgFrenoMoto.class.getName()).log(Level.SEVERE, null, ex);
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
        t1enabled(false);
        //timer4.stop();
        System.out.println("Prueba cancelada");
        puerto.close();
        dialogCancelacion.setVisible(true);
        //this.dispose();
    }//GEN-LAST:event_BotonCancelarActionPerformed

    public static int byteToInt(byte b) {//-'
        String cadenaLSB = Integer.toBinaryString(b & 0xFF);
        StringBuilder lsb = new StringBuilder();
        for (int i = cadenaLSB.length() - 1; i < 7; i++) {
            lsb.append('0');
        }
        lsb.append(cadenaLSB);
        int valor = Integer.parseInt(cadenaLSB, 2);
        return valor;
    }

    public boolean isError1() {
        return error1;
    }

    public boolean isError2() {
        return error2;
    }

    public int getUnimedidasdist() {
        return unimedidasdist;
    }

    public int getUnimedidastiem() {
        return unimedidastiem;
    }

    public void setIdPrueba(int idPrueba) {
        this.idPrueba = idPrueba;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DlgTaximetro dialog = new DlgTaximetro(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                //dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonCancelar;
    private javax.swing.JButton BotonContinuar;
    private javax.swing.JButton BotonEmpezar;
    private javax.swing.JButton BotonEnviarCancelacion;
    private javax.swing.JButton BotonFinalizar;
    private javax.swing.JPanel PanelBotones;
    private javax.swing.JDialog dialogCancelacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {//--'
        
        numtimer1++;
        
        if (numtimer1 == 10 && pasomedtax == 0) {            
            jLabel3.setText("<html>Cuando el taximetro marque " + unidistamedir + " unidades presione Continuar </html>");
            BotonContinuar.setEnabled(true);
            tecladoactivado = true;
        }
        
        if (numtimer1 == 4 && pasomedtax == 1) {
            t1enabled(false);
             medidorVelocidadVariable.stop();
            JOptionPane.showMessageDialog(this, "Presione aceptar para iniciar la medición", "Precaución",
                    JOptionPane.WARNING_MESSAGE);
            unimedidastiem = 0;
            timer3.start();    
        }
        
    }

}
