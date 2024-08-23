/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.igrafica;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.soltelec.dao.ControladorVerificar;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import com.soltelec.model.Vehiculos;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import java.awt.Component;
import java.text.NumberFormat;
import javax.persistence.EntityManager;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class FrmProfundidadLabrado extends javax.swing.JDialog {

    private int contadorA = 0;
    private int contadorB = 0;

    private int defecto;
    private boolean aprobado = false;
    private String cadena = "\n";
    private String cadenaSFrenos = "";
    private String grupo = "";
    //---Conexión por JDBC
    private Connection conexion;
    private String usuario;
    private String password;
    private String direccionIP;
    //DIRECCIÓN IP DEL SERVIDOR = 186.112.176.34
    //---
    ////////////////////////////////////////////PARA NO PERDER LA REFERENCIA DEL OBJETO///////////
    NoReiniciable nrei;
    private int idVehiculo;
    private int hojaPruebasActual;
    private int idPrueba;
    Vehiculos vehiculo = null;
    java.awt.Frame frame;
    int idUsuario;
    boolean ensenianza;
    boolean aplicTaximetro;
    EntityManager em;

    FrmProfundidadLabrado() {
    }

    public NoReiniciable getNrei() {
        return nrei;
    }

    public void setNrei(NoReiniciable nrei) {
        this.nrei = nrei;
    }
    //////////////////////////////////////////////PARA NO PERDER LA REFERENCIA DEL OBJETO/////////

    public FrmProfundidadLabrado(java.awt.Frame frame, boolean modal, int idVehiculo, int hojaPruebasActual, int idPrueba, int idUsuario, Vehiculos vehiculo, EntityManager em) {
        super(frame, modal);
        this.idVehiculo = idVehiculo;
        this.hojaPruebasActual = hojaPruebasActual;
        this.idPrueba = idPrueba;
        initComponents();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d);
        setResizable(false);
        this.frame = frame;
        this.vehiculo = vehiculo;
        this.idUsuario = idUsuario;
        this.em = em;
        ControladorVerificar controladorVerificar = new ControladorVerificar();
        lblPesosBruto.setText(String.valueOf(vehiculo.getPesoBruto()));
        String txtLabrado = controladorVerificar.exisTestLab(this.idPrueba, em);
        
//        //funcion de formateo a maximo dos digitos en un rango de 0  a 99
//        NumberFormat format = NumberFormat.getInstance();
//        NumberFormatter formatter = new NumberFormatter(format);
//        formatter.setValueClass(Integer.class);
//        formatter.setMinimum(0);
//        formatter.setMaximum(99);
//        formatter.setAllowsInvalid(false);
//        formatter.setCommitsOnValidEdit(true);
//        
//        //asignacion de formateo a cada campo
//        txtLLantaInz1 = new JFormattedTextField(formatter);
//        txtLLantaDer1 = new JFormattedTextField(formatter);
//        txtLLantaRespuesto1 = new JFormattedTextField(formatter);
//        txtLLantaIzq2 = new JFormattedTextField(formatter);
//        txtLLantaIzq3 = new JFormattedTextField(formatter);
//        txtLLantaDer3 = new JFormattedTextField(formatter);
//        txtLLantaAuxIzq3 = new JFormattedTextField(formatter);
//        txtLLantaAuxDer3 = new JFormattedTextField(formatter);
//        txtLLantaIzq4 = new JFormattedTextField(formatter);
//        txtLLantaDer4 = new JFormattedTextField(formatter);
//        txtLLantaAuxIzq4 = new JFormattedTextField(formatter);
//        txtLLantaAuxDer4 = new JFormattedTextField(formatter);
//        txtLLantaIzq5 = new JFormattedTextField(formatter);
//        txtLLantaDer5 = new JFormattedTextField(formatter);
//        txtLLantaAuxIzq5 = new JFormattedTextField(formatter);
//        txtLLantaAuxDer5 = new JFormattedTextField(formatter);
//        //nuemro de columnas permitidas
//        txtLLantaInz1.setColumns(2);
//        txtLLantaDer1.setColumns(2);
//        txtLLantaRespuesto1.setColumns(2);
//        txtLLantaIzq2.setColumns(2);
//        txtLLantaIzq3.setColumns(2);
//        txtLLantaDer3.setColumns(2);
//        txtLLantaAuxIzq3.setColumns(2);
//        txtLLantaAuxDer3.setColumns(2);
//        txtLLantaIzq4.setColumns(2);
//        txtLLantaDer4.setColumns(2);
//        txtLLantaAuxIzq4.setColumns(2);
//        txtLLantaAuxDer4.setColumns(2);
//        txtLLantaIzq5.setColumns(2);
//        txtLLantaDer5.setColumns(2);
//        txtLLantaAuxIzq5.setColumns(2);
//        txtLLantaAuxDer5.setColumns(2);
//        //13-03-2023 para limitar campo no supere los dos digitos
        
        if (!txtLabrado.equalsIgnoreCase("0")) {
            String[] lstObs = txtLabrado.split("obs");
            String[] lstTrama = lstObs[0].split("@@");
            String[] lstEjes = lstTrama[0].split("&");
            String eje = lstEjes[0].replace("$", "-");
            String[] infLabr = eje.split("-");
            txtLLantaInz1.setText(infLabr[0].substring(5, infLabr[0].length()));
            txtLLantaDer1.setText(infLabr[1].substring(5, infLabr[1].length()));
            eje = lstEjes[1].replace("$", "-");
            infLabr = eje.split("-");
            txtLLantaIzq2.setText(infLabr[0].substring(5, infLabr[0].length()));
            txtLLantaDer2.setText(infLabr[1].substring(5, infLabr[1].length()));
            chkHabilitar2.setEnabled(true);
            if (infLabr.length > 3) {
                chkHabilitar2.setSelected(true);
                txtLLantaAuxIzq2.setEnabled(true);
                txtLLantaAuxDer2.setEnabled(true);
                lblLLantaAuxDer21.setEnabled(true);
                lblLLantaAuxIzq2.setEnabled(true);
                lblLLantaAuxDer2.setEnabled(true);
                txtLLantaAuxIzq2.setText(infLabr[2].substring(5, infLabr[2].length()));
                txtLLantaAuxDer2.setText(infLabr[3].substring(5, infLabr[3].length()));
            }

            if (lstEjes.length > 2) {
                lblEje3.setEnabled(true);
                lblLLantaIzq3.setEnabled(true);
                lblLLantaDer3.setEnabled(true);
                txtLLantaIzq3.setEnabled(true);
                txtLLantaDer3.setEnabled(true);
                chkHabilitar3.setEnabled(true);
                eje = lstEjes[2].replace("$", "-");
                infLabr = eje.split("-");
                txtLLantaIzq3.setText(infLabr[0].substring(5, infLabr[0].length()));
                txtLLantaDer3.setText(infLabr[1].substring(5, infLabr[1].length()));
                if (infLabr.length > 3) {
                    chkHabilitar3.setSelected(true);
                    txtLLantaAuxIzq3.setEnabled(true);
                    txtLLantaAuxDer3.setEnabled(true);
                    lblLLantaAuxIzq3.setEnabled(true);
                    lblLLantaAuxDer3.setEnabled(true);
                    txtLLantaAuxIzq3.setText(infLabr[2].substring(5, infLabr[2].length()));
                    txtLLantaAuxDer3.setText(infLabr[3].substring(5, infLabr[3].length()));
                }
            }
            if (lstEjes.length > 3) {
                lbleje4.setEnabled(true);
                lblLLantaIzq4.setEnabled(true);
                lblLLantaDer4.setEnabled(true);
                txtLLantaIzq4.setEnabled(true);
                txtLLantaDer4.setEnabled(true);
                chkHabilitar4.setEnabled(true);
                eje = lstEjes[3].replace("$", "-");
                infLabr = eje.split("-");
                txtLLantaIzq4.setText(infLabr[0].substring(5, infLabr[0].length()));
                txtLLantaDer4.setText(infLabr[1].substring(5, infLabr[1].length()));
                if (infLabr.length > 3) {
                    chkHabilitar4.setSelected(true);
                    txtLLantaAuxIzq4.setEnabled(true);
                    txtLLantaAuxDer4.setEnabled(true);
                    lblLLantaAuxIzq4.setEnabled(true);
                    lblLLantaAuxDer4.setEnabled(true);
                    txtLLantaAuxIzq4.setText(infLabr[2].substring(5, infLabr[2].length()));
                    txtLLantaAuxDer4.setText(infLabr[3].substring(5, infLabr[3].length()));
                }
            }
            if (lstEjes.length > 4) {
                lbleje5.setEnabled(true);
                lblLLantaIzq5.setEnabled(true);
                lblLLantaDer5.setEnabled(true);
                txtLLantaIzq5.setEnabled(true);
                txtLLantaDer5.setEnabled(true);
                chkHabilitar5.setEnabled(true);

                eje = lstEjes[4].replace("$", "-");
                infLabr = eje.split("-");
                txtLLantaIzq5.setText(infLabr[0].substring(5, infLabr[0].length()));
                txtLLantaDer5.setText(infLabr[1].substring(5, infLabr[1].length()));
                if (infLabr.length > 3) {
                    chkHabilitar5.setSelected(true);
                    txtLLantaAuxIzq5.setEnabled(true);
                    txtLLantaAuxDer5.setEnabled(true);
                    lblLLantaAuxIzq5.setEnabled(true);
                    lblLLantaAuxDer5.setEnabled(true);
                    txtLLantaAuxIzq5.setText(infLabr[3]);
                    txtLLantaAuxDer5.setText(infLabr[4]);//
                }
            }
            String[] llantaRespuesto = lstTrama[1].split("%");
            txtLLantaRespuesto1.setText(llantaRespuesto[0]);
            System.out.println("////////////////////////-------------[0] : " + llantaRespuesto[0] + "---------------////////////////////////");
            if (llantaRespuesto.length > 1) {
                txtLLantaRespuesto2.setText(llantaRespuesto[1]);
                System.out.println("////////////////////////-------------[1] : " + llantaRespuesto[1] + "---------------////////////////////////");
            }
        }

        validarCamposNumericos(txtLLantaIzq2);
        validarCamposNumericos(txtLLantaDer2);
        validarCamposNumericos(txtLLantaInz1);
        validarCamposNumericos(txtLLantaDer1);

        if (vehiculo.getNumeroejes() == 3) {
            lblEje3.setEnabled(true);
            lblLLantaIzq3.setEnabled(true);
            lblLLantaDer3.setEnabled(true);
            txtLLantaIzq3.setEnabled(true);
            txtLLantaDer3.setEnabled(true);
            chkHabilitar3.setEnabled(true);
            validarCamposNumericos(txtLLantaIzq3);
            validarCamposNumericos(txtLLantaDer3);
        }
        if (vehiculo.getNumeroejes() == 4) {
            lblEje3.setEnabled(true);
            lbleje4.setEnabled(true);
            lblLLantaIzq4.setEnabled(true);
            lblLLantaDer4.setEnabled(true);
            txtLLantaIzq4.setEnabled(true);
            txtLLantaDer4.setEnabled(true);
            chkHabilitar4.setEnabled(true);
            lblLLantaIzq3.setEnabled(true);
            lblLLantaDer3.setEnabled(true);
            txtLLantaIzq3.setEnabled(true);
            txtLLantaDer3.setEnabled(true);
            chkHabilitar3.setEnabled(true);
            validarCamposNumericos(txtLLantaIzq4);
            validarCamposNumericos(txtLLantaDer4);
        }
        if (vehiculo.getNumeroejes() == 5) {
            lblEje3.setEnabled(true);
            lbleje4.setEnabled(true);
            lblLLantaIzq4.setEnabled(true);
            lblLLantaDer4.setEnabled(true);
            txtLLantaIzq4.setEnabled(true);
            txtLLantaDer4.setEnabled(true);
            chkHabilitar4.setEnabled(true);
            lblLLantaIzq3.setEnabled(true);
            lblLLantaDer3.setEnabled(true);
            txtLLantaIzq3.setEnabled(true);
            txtLLantaDer3.setEnabled(true);
            chkHabilitar3.setEnabled(true);
            lbleje5.setEnabled(true);
            lblLLantaIzq5.setEnabled(true);
            lblLLantaDer5.setEnabled(true);
            txtLLantaIzq5.setEnabled(true);
            txtLLantaDer5.setEnabled(true);
            chkHabilitar5.setEnabled(true);
            validarCamposNumericos(txtLLantaIzq5);
            validarCamposNumericos(txtLLantaDer5);
        }

    }

    private void cargarConexion() {
        Properties props = new Properties();
        //try retrieve data from file
        try {
            props.load(new FileInputStream("./conexion.properties"));//TODO warining
            direccionIP = props.getProperty("urljdbc");
            usuario = props.getProperty("usuario");
            password = props.getProperty("password");
            //System.out.println(urljdbc);
        } //catch exception in case properties file does not exist
        catch (IOException e) {
            e.printStackTrace();
        }
    }//end of method cargarUrl

    public FrmProfundidadLabrado(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d);
        setResizable(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtLLantaDer1 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtLLantaIzq2 = new javax.swing.JTextField();
        txtLLantaDer2 = new javax.swing.JTextField();
        lblLLantaAuxIzq2 = new javax.swing.JLabel();
        chkHabilitar5 = new javax.swing.JCheckBox();
        lblEje3 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        lblLLantaIzq3 = new javax.swing.JLabel();
        lblLLantaDer3 = new javax.swing.JLabel();
        lblLLantaAuxIzq3 = new javax.swing.JLabel();
        txtLLantaAuxIzq3 = new javax.swing.JTextField();
        txtLLantaDer3 = new javax.swing.JTextField();
        txtLLantaIzq3 = new javax.swing.JTextField();
        chkHabilitar2 = new javax.swing.JCheckBox();
        jSeparator7 = new javax.swing.JSeparator();
        lblLLantaIzq4 = new javax.swing.JLabel();
        lblLLantaDer4 = new javax.swing.JLabel();
        lblLLantaAuxIzq4 = new javax.swing.JLabel();
        txtLLantaIzq4 = new javax.swing.JTextField();
        txtLLantaDer4 = new javax.swing.JTextField();
        txtLLantaAuxIzq4 = new javax.swing.JTextField();
        chkHabilitar3 = new javax.swing.JCheckBox();
        lbleje4 = new javax.swing.JLabel();
        lbleje5 = new javax.swing.JLabel();
        lblLLantaDer5 = new javax.swing.JLabel();
        lblLLantaIzq5 = new javax.swing.JLabel();
        lblLLantaAuxIzq5 = new javax.swing.JLabel();
        txtLLantaIzq5 = new javax.swing.JTextField();
        txtLLantaDer5 = new javax.swing.JTextField();
        txtLLantaAuxIzq5 = new javax.swing.JTextField();
        chkHabilitar4 = new javax.swing.JCheckBox();
        lblLLantaAuxDer4 = new javax.swing.JLabel();
        txtLLantaAuxDer2 = new javax.swing.JTextField();
        txtLLantaAuxIzq2 = new javax.swing.JTextField();
        lblLLantaAuxDer2 = new javax.swing.JLabel();
        lblLLantaAuxDer5 = new javax.swing.JLabel();
        lblLLantaAuxDer3 = new javax.swing.JLabel();
        txtLLantaAuxDer5 = new javax.swing.JTextField();
        txtLLantaAuxDer3 = new javax.swing.JTextField();
        txtLLantaAuxDer4 = new javax.swing.JTextField();
        txtLLantaInz1 = new javax.swing.JTextField();
        guardar = new javax.swing.JButton();
        lblLLantaAuxDer21 = new javax.swing.JLabel();
        txtLLantaRespuesto2 = new javax.swing.JTextField();
        lblLLantaAuxDer7 = new javax.swing.JLabel();
        txtLLantaRespuesto1 = new javax.swing.JTextField();
        lblPeso = new javax.swing.JLabel();
        lblPesosBruto = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro Profundidad Labrado");
        setAlwaysOnTop(true);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/solt.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Serif", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html><center>Registro<br/> Profundidad Labrado</center></html>");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/rueda.png"))); // NOI18N

        jPanel7.setToolTipText("");
        jPanel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel4.setText("Eje1:");

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel5.setText("LLanta Izq:");

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel6.setText("LLanta Der:");

        txtLLantaDer1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaDer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLLantaDer1ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel10.setText("Eje2:");

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel11.setText("LLanta Izq:");

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel12.setText("LLanta Der:");

        txtLLantaIzq2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        txtLLantaDer2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        lblLLantaAuxIzq2.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxIzq2.setText("LLanta Int Izq:");
        lblLLantaAuxIzq2.setEnabled(false);

        chkHabilitar5.setText("Habilitar");
        chkHabilitar5.setEnabled(false);
        chkHabilitar5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHabilitar5ActionPerformed(evt);
            }
        });

        lblEje3.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        lblEje3.setText("Eje3:");
        lblEje3.setEnabled(false);

        lblLLantaIzq3.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaIzq3.setText("LLanta Izq:");
        lblLLantaIzq3.setEnabled(false);

        lblLLantaDer3.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaDer3.setText("LLanta Der:");
        lblLLantaDer3.setEnabled(false);

        lblLLantaAuxIzq3.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxIzq3.setText("LLanta Int Izq:");
        lblLLantaAuxIzq3.setEnabled(false);

        txtLLantaAuxIzq3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxIzq3.setEnabled(false);

        txtLLantaDer3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaDer3.setEnabled(false);

        txtLLantaIzq3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaIzq3.setEnabled(false);

        chkHabilitar2.setText("Habilitar");
        chkHabilitar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHabilitar2ActionPerformed(evt);
            }
        });

        jSeparator7.setDebugGraphicsOptions(javax.swing.DebugGraphics.FLASH_OPTION);
        jSeparator7.setMinimumSize(new java.awt.Dimension(60, 10));

        lblLLantaIzq4.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaIzq4.setText("LLanta Izq:");
        lblLLantaIzq4.setEnabled(false);

        lblLLantaDer4.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaDer4.setText("LLanta Der:");
        lblLLantaDer4.setEnabled(false);

        lblLLantaAuxIzq4.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxIzq4.setText("LLanta Int Izq:");
        lblLLantaAuxIzq4.setEnabled(false);

        txtLLantaIzq4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaIzq4.setEnabled(false);
        txtLLantaIzq4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLLantaIzq4ActionPerformed(evt);
            }
        });

        txtLLantaDer4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaDer4.setEnabled(false);

        txtLLantaAuxIzq4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxIzq4.setEnabled(false);

        chkHabilitar3.setText("Habilitar");
        chkHabilitar3.setEnabled(false);
        chkHabilitar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHabilitar3ActionPerformed(evt);
            }
        });

        lbleje4.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        lbleje4.setText("Eje4:");
        lbleje4.setEnabled(false);

        lbleje5.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        lbleje5.setText("Eje5:");
        lbleje5.setEnabled(false);

        lblLLantaDer5.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaDer5.setText("LLanta Der:");
        lblLLantaDer5.setEnabled(false);

        lblLLantaIzq5.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaIzq5.setText("LLanta Izq:");
        lblLLantaIzq5.setEnabled(false);

        lblLLantaAuxIzq5.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxIzq5.setText("LLanta Int Izq:");
        lblLLantaAuxIzq5.setEnabled(false);

        txtLLantaIzq5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaIzq5.setEnabled(false);

        txtLLantaDer5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaDer5.setEnabled(false);

        txtLLantaAuxIzq5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxIzq5.setEnabled(false);

        chkHabilitar4.setText("Habilitar");
        chkHabilitar4.setEnabled(false);
        chkHabilitar4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHabilitar4ActionPerformed(evt);
            }
        });

        lblLLantaAuxDer4.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxDer4.setText("LLanta Int Der:");
        lblLLantaAuxDer4.setEnabled(false);

        txtLLantaAuxDer2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxDer2.setEnabled(false);

        txtLLantaAuxIzq2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxIzq2.setEnabled(false);
        txtLLantaAuxIzq2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLLantaAuxIzq2ActionPerformed(evt);
            }
        });

        lblLLantaAuxDer2.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxDer2.setText("LLanta Rep. 2:");

        lblLLantaAuxDer5.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxDer5.setText("LLanta Int Der:");
        lblLLantaAuxDer5.setEnabled(false);

        lblLLantaAuxDer3.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxDer3.setText("LLanta Int Der:");
        lblLLantaAuxDer3.setEnabled(false);

        txtLLantaAuxDer5.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxDer5.setEnabled(false);

        txtLLantaAuxDer3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxDer3.setEnabled(false);

        txtLLantaAuxDer4.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaAuxDer4.setEnabled(false);

        txtLLantaInz1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        guardar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/save_24.png"))); // NOI18N
        guardar.setText("Siguiente");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });

        lblLLantaAuxDer21.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxDer21.setText("LLanta Int Der:");
        lblLLantaAuxDer21.setEnabled(false);

        txtLLantaRespuesto2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtLLantaRespuesto2.setText("NA");
        txtLLantaRespuesto2.setToolTipText("");

        lblLLantaAuxDer7.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblLLantaAuxDer7.setText("LLanta Rep. 1:");

        txtLLantaRespuesto1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        lblPeso.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        lblPeso.setText("PESO BRUTO");
        lblPeso.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        lblPesosBruto.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 974, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel10)
                                .addGap(24, 24, 24)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 958, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addComponent(lblEje3)
                                .addGap(18, 18, 18)
                                .addComponent(jSeparator6))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(txtLLantaIzq2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(90, 90, 90)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLLantaDer2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLLantaRespuesto2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLLantaAuxDer2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(chkHabilitar2)
                                .addGap(13, 13, 13)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblLLantaAuxIzq2)
                                    .addComponent(txtLLantaAuxIzq2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLLantaAuxDer2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLLantaAuxDer21))
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(txtLLantaIzq3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(85, 85, 85)
                                                .addComponent(txtLLantaDer3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(87, 87, 87)
                                                .addComponent(lblLLantaDer3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(chkHabilitar3)
                                                .addGap(6, 6, 6))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                                        .addGap(98, 98, 98)
                                                        .addComponent(lblLLantaDer5)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(chkHabilitar5))
                                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(chkHabilitar4)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblLLantaAuxIzq5)
                                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                                    .addComponent(lblLLantaAuxIzq3)
                                                    .addGap(11, 11, 11))
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(lblLLantaAuxIzq4)
                                                    .addComponent(txtLLantaAuxIzq3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(txtLLantaAuxIzq4, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(txtLLantaAuxIzq5, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(lblLLantaIzq3)
                                        .addGap(764, 764, 764)))
                                .addGap(16, 16, 16)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtLLantaAuxDer3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblLLantaAuxDer3))
                                .addGap(16, 16, 16))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtLLantaIzq4, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblLLantaIzq5)
                                                    .addComponent(lbleje5)
                                                    .addComponent(txtLLantaIzq5, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(56, 56, 56)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtLLantaDer4, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblLLantaDer4)
                                            .addComponent(txtLLantaDer5, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(498, 498, 498)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtLLantaAuxDer4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblLLantaAuxDer5)
                                            .addComponent(txtLLantaAuxDer5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(lbleje4)
                                        .addGap(18, 18, 18)
                                        .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 963, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(lblLLantaIzq4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblLLantaAuxDer4)
                                .addGap(17, 17, 17))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(txtLLantaInz1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(85, 85, 85)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtLLantaDer1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(lblLLantaAuxDer7, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblPeso, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(txtLLantaRespuesto1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblPesosBruto, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33)))
                                .addGap(35, 35, 35)
                                .addComponent(guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLLantaInz1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLLantaDer1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLLantaRespuesto1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblPeso)
                                    .addComponent(lblLLantaAuxDer7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPesosBruto)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel12)
                                .addComponent(lblLLantaAuxDer2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLLantaIzq2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLLantaDer2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLLantaRespuesto2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblEje3))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLLantaAuxIzq2)
                            .addComponent(chkHabilitar2)
                            .addComponent(lblLLantaAuxDer21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLLantaAuxIzq2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLLantaAuxDer2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLLantaAuxIzq3)
                    .addComponent(chkHabilitar3)
                    .addComponent(lblLLantaAuxDer3)
                    .addComponent(lblLLantaIzq3)
                    .addComponent(lblLLantaDer3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLLantaIzq3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtLLantaAuxIzq3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtLLantaAuxDer3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtLLantaDer3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbleje4)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblLLantaAuxDer4)
                        .addComponent(lblLLantaAuxIzq4)
                        .addComponent(chkHabilitar4))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblLLantaIzq4)
                        .addComponent(lblLLantaDer4)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLLantaDer4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLLantaIzq4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbleje5))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLLantaAuxIzq4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLLantaAuxDer4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLLantaDer5)
                    .addComponent(chkHabilitar5)
                    .addComponent(lblLLantaAuxIzq5)
                    .addComponent(lblLLantaAuxDer5)
                    .addComponent(lblLLantaIzq5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtLLantaDer5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtLLantaIzq5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtLLantaAuxIzq5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLLantaAuxDer5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(132, 132, 132))
        );

        jTabbedPane1.addTab("Descripcion de Profundidad", new javax.swing.ImageIcon(getClass().getResource("/com/soltelec/Icon/arrow_up_24.png")), jPanel7); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1072, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 633, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed

        establecerGrupo("Formulario Profundidad de Labrado");
        Double numero;
        try {
            numero = Double.parseDouble(txtLLantaInz1.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq. (Eje 1) no es NUMERICO");
            return;
        }
        try {
            numero = Double.parseDouble(txtLLantaDer1.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der. (Eje 1) no es NUMERICO");
            return;
        }

        try {
            numero = Double.parseDouble(txtLLantaIzq2.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq. (Eje 2) no es NUMERICO");
            return;
        }
        try {
            numero = Double.parseDouble(txtLLantaDer2.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der. (Eje 2) no es NUMERICO");
            return;
        }

        String strLabrado = "D.Izq".concat(txtLLantaInz1.getText()).concat("$").concat("D.Der").concat(txtLLantaDer1.getText()).concat("&").concat("T.Izq").concat(txtLLantaIzq2.getText()).concat("$").concat("T.Der").concat(txtLLantaDer2.getText());
        if (this.chkHabilitar2.isSelected() == true) {
            try {
                numero = Double.parseDouble(txtLLantaAuxIzq2.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq-Aux. (Eje 2) no es NUMERICO");
                return;
            }
            try {
                numero = Double.parseDouble(txtLLantaAuxDer2.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der-Aux. (Eje 2) no es NUMERICO");
                return;
            }
            strLabrado = strLabrado.concat("$").concat("AuIzq").concat(txtLLantaAuxIzq2.getText()).concat("$").concat("AuDer").concat(txtLLantaAuxDer2.getText()).concat("&");
        } else {
            strLabrado = strLabrado.concat("&");
        }

        if (txtLLantaIzq3.isEnabled() == true) {
            try {
                numero = Double.parseDouble(txtLLantaIzq3.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq. (Eje 3) no es NUMERICO");
                return;
            }
            try {
                numero = Double.parseDouble(txtLLantaDer3.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der. (Eje 3) no es NUMERICO");
                return;
            }
            strLabrado = strLabrado.concat("T.Izq").concat(txtLLantaIzq3.getText().concat("$").concat("T.Der").concat(txtLLantaDer3.getText()));
            if (this.chkHabilitar3.isSelected() == true) {
                try {
                    numero = Double.parseDouble(txtLLantaAuxIzq3.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq-Aux. (Eje 3) no es NUMERICO");
                    return;
                }
                try {
                    numero = Double.parseDouble(txtLLantaAuxDer3.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der-Aux (Eje 3) no es NUMERICO");
                    return;
                }
                strLabrado = strLabrado.concat("$").concat("AuIzq ").concat(txtLLantaAuxIzq3.getText()).concat("$").concat("AuDer").concat(txtLLantaAuxDer3.getText()).concat("&");
            } else {
                strLabrado = strLabrado.concat("&");
            }
        }
        if (txtLLantaIzq4.isEnabled() == true) {
            try {
                numero = Double.parseDouble(txtLLantaIzq4.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq. (Eje 4) no es NUMERICO");
                return;
            }
            try {
                numero = Double.parseDouble(txtLLantaDer4.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der. (Eje 4) no es NUMERICO");
                return;
            }
            strLabrado = strLabrado.concat("T.Izq").concat(txtLLantaIzq4.getText().concat("$").concat("T.Der").concat(txtLLantaDer4.getText()));
            if (this.chkHabilitar4.isSelected() == true) {
                try {
                    numero = Double.parseDouble(txtLLantaAuxIzq4.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq-Aux. (Eje 4) no es NUMERICO");
                    return;
                }
                try {
                    numero = Double.parseDouble(txtLLantaAuxDer4.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der-Aux (Eje 4) no es NUMERICO");
                    return;
                }
                strLabrado = strLabrado.concat("$").concat("AuIzq").concat(txtLLantaAuxIzq4.getText()).concat("$").concat("AuDer").concat(txtLLantaAuxDer4.getText()).concat("&");
            } else {
                strLabrado = strLabrado.concat("&");
            }
        }
        if (txtLLantaIzq5.isEnabled() == true) {
            try {
                numero = Double.parseDouble(txtLLantaIzq5.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq. (Eje 5) no es NUMERICO");
                return;
            }
            try {
                numero = Double.parseDouble(txtLLantaDer5.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der. (Eje 5) no es NUMERICO");
                return;
            }
            strLabrado = strLabrado.concat("T.Izq").concat(txtLLantaIzq5.getText().concat("$").concat("T.Der").concat(txtLLantaDer5.getText()).concat("$"));
            if (this.chkHabilitar5.isSelected() == true) {
                try {
                    numero = Double.parseDouble(txtLLantaAuxIzq5.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Izq-Aux. (Eje 5) no es NUMERICO");
                    return;
                }
                try {
                    numero = Double.parseDouble(txtLLantaAuxDer5.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta Der-Aux (Eje 5) no es NUMERICO");
                    return;
                }
                strLabrado = strLabrado.concat("$").concat("AuIzq ").concat(txtLLantaAuxIzq5.getText()).concat("$").concat("AuDer").concat(txtLLantaAuxDer5.getText()).concat("&");
            } else {
                strLabrado = strLabrado.concat("&");
            }
        }

        if (!txtLLantaRespuesto1.getText().equalsIgnoreCase("NA")) {
            try {
                numero = Double.parseDouble(txtLLantaRespuesto1.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta de Repuesto no es NUMERICO");
                return;
            }
        }
        strLabrado = strLabrado.concat("@@").concat(txtLLantaRespuesto1.getText()).concat("% ");
        if (!txtLLantaRespuesto2.getText().equalsIgnoreCase("NA")) {
            try {
                numero = Double.parseDouble(txtLLantaRespuesto2.getText());
                strLabrado = strLabrado.concat(txtLLantaRespuesto2.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Disculpe, la Informacion de Llanta de Repuesto (2) no es NUMERICO");
                return;
            }
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = (Connection) DriverManager.getConnection("jdbc:mysql://" + Conexion.getIpServidor() + ":" + Conexion.getPuerto() + "/" + Conexion.getBaseDatos(), Conexion.getUsuario(), Conexion.getContraseña());
        } catch (ClassNotFoundException | SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        try {
            ControladorVerificar controladorVerificar = new ControladorVerificar();
            String txtObs = controladorVerificar.exisObs(this.idPrueba, em);
            conexion.setAutoCommit(false);
            String statement = "UPDATE pruebas SET observaciones=? WHERE Id_Pruebas = ?  ";
            java.sql.PreparedStatement instruccion = conexion.prepareStatement(statement);
            instruccion.setString(1, strLabrado.concat("obs").concat(txtObs));
            instruccion.setInt(2, idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            //System.out.println("Datos enviados");
        } catch (SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        Double refLab = 0.0;
        Boolean writeDefProf = false;
        Boolean writeDefProfRepuesto = false;
        if (vehiculo.getPesoBruto() > 3500) {
            refLab = 2.0;
        } else {
            refLab = 1.6;
        }
        if (Double.parseDouble(txtLLantaInz1.getText()) < refLab) {
            writeDefProf = true;
        }
        if (Double.parseDouble(txtLLantaDer1.getText()) < refLab) {
            writeDefProf = true;
        }
        if (Double.parseDouble(txtLLantaIzq2.getText()) < refLab) {
            writeDefProf = true;
        }
        if (Double.parseDouble(txtLLantaDer2.getText()) < refLab) {
            writeDefProf = true;
        }
        if (!txtLLantaRespuesto1.getText().equalsIgnoreCase("NA")) {
            if (Double.parseDouble(txtLLantaRespuesto1.getText()) < refLab) {
                writeDefProfRepuesto = true;
            }
        }
        if (!txtLLantaRespuesto2.getText().equalsIgnoreCase("NA")) {
            if (Double.parseDouble(txtLLantaRespuesto2.getText()) < refLab) {
                writeDefProfRepuesto = true;
            }
        }

        if (chkHabilitar2.isSelected() == true) {
            if (Double.parseDouble(txtLLantaAuxIzq2.getText()) < refLab) {
                writeDefProf = true;
            }
            if (Double.parseDouble(txtLLantaAuxDer2.getText()) < refLab) {
                writeDefProf = true;
            }
        }

        if (vehiculo.getNumeroejes() > 2) {
            if (Double.parseDouble(txtLLantaIzq3.getText()) < refLab) {
                writeDefProf = true;
            }
            if (Double.parseDouble(txtLLantaDer3.getText()) < refLab) {
                writeDefProf = true;
            }
            if (chkHabilitar3.isSelected() == true) {
                if (Double.parseDouble(txtLLantaAuxIzq3.getText()) < refLab) {
                    writeDefProf = true;
                }
                if (Double.parseDouble(txtLLantaAuxDer3.getText()) < refLab) {
                    writeDefProf = true;
                }
            }
        }
        if (vehiculo.getNumeroejes() > 3) {
            if (Double.parseDouble(txtLLantaIzq4.getText()) < refLab) {
                writeDefProf = true;
            }
            if (Double.parseDouble(txtLLantaDer4.getText()) < refLab) {
                writeDefProf = true;
            }
            if (chkHabilitar4.isSelected() == true) {
                if (Double.parseDouble(txtLLantaAuxIzq4.getText()) < refLab) {
                    writeDefProf = true;
                }
                if (Double.parseDouble(txtLLantaAuxDer4.getText()) < refLab) {
                    writeDefProf = true;
                }
            }
        }
        if (vehiculo.getNumeroejes() > 4) {
            if (Double.parseDouble(txtLLantaIzq5.getText()) < refLab) {
                writeDefProf = true;
            }
            if (Double.parseDouble(txtLLantaDer5.getText()) < refLab) {
                writeDefProf = true;
            }
            if (chkHabilitar5.isSelected() == true) {
                if (Double.parseDouble(txtLLantaAuxIzq5.getText()) < refLab) {
                    writeDefProf = true;
                }
                if (Double.parseDouble(txtLLantaAuxDer5.getText()) < refLab) {
                    writeDefProf = true;
                }
            }
        }
        try {
            String statement = "DELETE FROM db_cda.defxprueba WHERE id_defecto=? AND id_prueba=?";
            PreparedStatement instruccion = (PreparedStatement) conexion.prepareStatement(statement);
            if (vehiculo.getPesoBruto() > 3500) {
                instruccion.setInt(1, 10095);
            } else {
                instruccion.setInt(1, 10094);
            }
            instruccion.setInt(2, this.idPrueba);
            instruccion.executeUpdate();
            instruccion.clearParameters();
            conexion.commit();
            conexion.setAutoCommit(true);
            conexion.close();

            if (writeDefProf == true) {
                conexion = (Connection) DriverManager.getConnection("jdbc:mysql://" + Conexion.getIpServidor() + ":" + Conexion.getPuerto() + "/" + Conexion.getBaseDatos(), Conexion.getUsuario(), Conexion.getContraseña());
                conexion.setAutoCommit(false);
                statement = "INSERT INTO db_cda.defxprueba(id_defecto,id_prueba) VALUES(?,?)";
                //JOptionPane.showMessageDialog(null, "IdPrueba = " +idPrueba);
                instruccion = (PreparedStatement) conexion.prepareStatement(statement);
                if (vehiculo.getPesoBruto() > 3500) {
                    instruccion.setInt(1, 10095);
                } else {
                    instruccion.setInt(1, 10094);
                }
                instruccion.setInt(2, this.idPrueba);
                //instruccion.setInt(3, hojaPruebasActual);
                //instruccion.setString(4, "A");
                instruccion.executeUpdate();
                instruccion.clearParameters();
                conexion.commit();
                conexion.setAutoCommit(true);
                conexion.close();
            }
        } catch (SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        this.dispose();
        JOptionPane.showMessageDialog(this, "Se ha REGISTRADO la Profundida de Labrado de una manera Exitosa..! ");
        doClose(0);
        // nrei.establecer_oprimido(true);
    }//GEN-LAST:event_guardarActionPerformed

    private void txtLLantaAuxIzq2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLLantaAuxIzq2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLLantaAuxIzq2ActionPerformed

    private void chkHabilitar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHabilitar4ActionPerformed
        if (chkHabilitar4.isSelected() == true) {
            txtLLantaAuxIzq4.setEnabled(true);
            lblLLantaAuxIzq4.setEnabled(true);
            lblLLantaAuxDer4.setEnabled(true);
            txtLLantaAuxDer4.setEnabled(true);
            validarCamposNumericos(txtLLantaAuxIzq4);
            validarCamposNumericos(txtLLantaAuxDer4);
        } else {
            txtLLantaAuxIzq4.setEnabled(false);
            lblLLantaAuxIzq4.setEnabled(false);
            lblLLantaAuxDer4.setEnabled(false);
            txtLLantaAuxDer4.setEnabled(false);
        }
    }//GEN-LAST:event_chkHabilitar4ActionPerformed

    private void chkHabilitar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHabilitar3ActionPerformed
        if (chkHabilitar3.isSelected() == true) {
            txtLLantaAuxIzq3.setEnabled(true);
            lblLLantaAuxIzq3.setEnabled(true);
            lblLLantaAuxDer3.setEnabled(true);
            txtLLantaAuxDer3.setEnabled(true);
            validarCamposNumericos(txtLLantaAuxIzq3);
            validarCamposNumericos(txtLLantaAuxDer3);
        } else {
            txtLLantaAuxIzq3.setEnabled(false);
            lblLLantaAuxIzq3.setEnabled(false);
            lblLLantaAuxDer3.setEnabled(false);
            txtLLantaAuxDer3.setEnabled(false);
        }
    }//GEN-LAST:event_chkHabilitar3ActionPerformed

    private void txtLLantaIzq4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLLantaIzq4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLLantaIzq4ActionPerformed

    private void chkHabilitar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHabilitar2ActionPerformed
        if (chkHabilitar2.isSelected() == true) {
            txtLLantaAuxIzq2.setEnabled(true);
            lblLLantaAuxIzq2.setEnabled(true);
            lblLLantaAuxDer21.setEnabled(true);
            txtLLantaAuxDer2.setEnabled(true);
            validarCamposNumericos(txtLLantaAuxIzq2);
            validarCamposNumericos(txtLLantaAuxDer2);
        } else {
            txtLLantaAuxIzq2.setEnabled(false);
            lblLLantaAuxIzq2.setEnabled(false);
            lblLLantaAuxDer2.setEnabled(false);
            txtLLantaAuxDer2.setEnabled(false);
        }
    }//GEN-LAST:event_chkHabilitar2ActionPerformed

    private void chkHabilitar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHabilitar5ActionPerformed

        if (chkHabilitar5.isSelected() == true) {
            txtLLantaAuxIzq5.setEnabled(true);
            lblLLantaAuxIzq5.setEnabled(true);
            lblLLantaAuxDer5.setEnabled(true);
            txtLLantaAuxDer5.setEnabled(true);
            validarCamposNumericos(txtLLantaAuxIzq5);
            validarCamposNumericos(txtLLantaAuxDer5);
        } else {
            txtLLantaAuxIzq5.setEnabled(false);
            lblLLantaAuxIzq5.setEnabled(false);
            lblLLantaAuxDer5.setEnabled(false);
            txtLLantaAuxDer5.setEnabled(false);
        }
    }//GEN-LAST:event_chkHabilitar5ActionPerformed

    private void txtLLantaDer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLLantaDer1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLLantaDer1ActionPerformed
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /////////////////////////////////MÉTODOS SOBRE LA FUNCIONALIDAD //////////////////////////////
    public void establecerCantidadProblemas(int contadorA, int contadorB) {
        this.contadorA = contadorA;
        this.contadorB = contadorB;
    }

    public int obtenerCantidadProblemasA() {
        return contadorA;
    }

    public int obtenerCantidadProblemasB() {
        return contadorB;
    }

    public void establecerAprobado(int defecto) {
        if (defecto >= 100) {
            aprobado = false;
        }
        //TODO: Mirar si es un camion, una moto, un cuatriciclo, pq cada uno tiene un número máximo de rechazos
        //diferente, para carros normales es 10.
        if (defecto >= 10) {
            aprobado = false;
        }
    }

    public boolean obtenberAprobado() {
        return aprobado;
    }

    public void establecerNombreProblema(String cadenaSFrenos) {
        this.cadenaSFrenos += cadenaSFrenos + "\n";
    }

    public String obtenerNombreProblema() {
        return cadenaSFrenos;
    }

    public void establecerGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String obtenerGrupo() {
        return grupo;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmProfundidadLabrado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                FrmProfundidadLabrado dialog = new FrmProfundidadLabrado(new javax.swing.JFrame(), true);
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

    public static void validarCamposNumericos(Component textField) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                soloNumeros(evt);
            }
        });
    }

    public static void soloNumeros(java.awt.event.KeyEvent e) {
        char caracter = e.getKeyChar();
        if (((caracter < '0') || (caracter > '9')) && (caracter != java.awt.event.KeyEvent.VK_BACK_SPACE) && (caracter != '.')) {
            e.consume();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkHabilitar2;
    private javax.swing.JCheckBox chkHabilitar3;
    private javax.swing.JCheckBox chkHabilitar4;
    private javax.swing.JCheckBox chkHabilitar5;
    private javax.swing.JButton guardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblEje3;
    private javax.swing.JLabel lblLLantaAuxDer2;
    private javax.swing.JLabel lblLLantaAuxDer21;
    private javax.swing.JLabel lblLLantaAuxDer3;
    private javax.swing.JLabel lblLLantaAuxDer4;
    private javax.swing.JLabel lblLLantaAuxDer5;
    private javax.swing.JLabel lblLLantaAuxDer7;
    private javax.swing.JLabel lblLLantaAuxIzq2;
    private javax.swing.JLabel lblLLantaAuxIzq3;
    private javax.swing.JLabel lblLLantaAuxIzq4;
    private javax.swing.JLabel lblLLantaAuxIzq5;
    private javax.swing.JLabel lblLLantaDer3;
    private javax.swing.JLabel lblLLantaDer4;
    private javax.swing.JLabel lblLLantaDer5;
    private javax.swing.JLabel lblLLantaIzq3;
    private javax.swing.JLabel lblLLantaIzq4;
    private javax.swing.JLabel lblLLantaIzq5;
    private javax.swing.JLabel lblPeso;
    private javax.swing.JLabel lblPesosBruto;
    private javax.swing.JLabel lbleje4;
    private javax.swing.JLabel lbleje5;
    private javax.swing.JTextField txtLLantaAuxDer2;
    private javax.swing.JTextField txtLLantaAuxDer3;
    private javax.swing.JTextField txtLLantaAuxDer4;
    private javax.swing.JTextField txtLLantaAuxDer5;
    private javax.swing.JTextField txtLLantaAuxIzq2;
    private javax.swing.JTextField txtLLantaAuxIzq3;
    private javax.swing.JTextField txtLLantaAuxIzq4;
    private javax.swing.JTextField txtLLantaAuxIzq5;
    private javax.swing.JTextField txtLLantaDer1;
    private javax.swing.JTextField txtLLantaDer2;
    private javax.swing.JTextField txtLLantaDer3;
    private javax.swing.JTextField txtLLantaDer4;
    private javax.swing.JTextField txtLLantaDer5;
    private javax.swing.JTextField txtLLantaInz1;
    private javax.swing.JTextField txtLLantaIzq2;
    private javax.swing.JTextField txtLLantaIzq3;
    private javax.swing.JTextField txtLLantaIzq4;
    private javax.swing.JTextField txtLLantaIzq5;
    private javax.swing.JTextField txtLLantaRespuesto1;
    private javax.swing.JTextField txtLLantaRespuesto2;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = 0;
}
