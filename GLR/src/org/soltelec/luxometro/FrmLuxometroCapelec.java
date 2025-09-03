/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import com.soltelec.util.Utilidades2;

import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.Utilidades;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author user
 */
public class FrmLuxometroCapelec extends javax.swing.JFrame {

    /**
     * Creates new form FrmLuxometroCapelec
     */
    
    Double[][] medidas = {
        /*Derechas                    Izquierdas*/
        {null, null, null,      null, null, null}, //deviaciones bajas
        {null, null, null,      null, null, null}, //bajas
        {null, null, null,      null, null, null}, //altas
        {null, null, null,      null, null, null}  //exploradoras
    };

    Integer[][] codigoMedidas = {
        /*Derechas                    Izquierdas*/
        {2042, 2041, 2040,      2044, 2045, 2046}, //deviaciones bajas
        {2026, 2025, 2024,      2031, 2030, 2029}, //bajas
        {2038, 2037, 2032,      2036, 2033, 2034}, //altas
        {2052, 2051, 2050,      2053, 2054, 2055}  //exploradoras
    };

    Integer[][] codigoMedidasMotos = {
        /*Derechas                    Izquierdas*/
        {2042, 2022, 2013,      2044, 2045, 2046}, //deviaciones bajas
        {2026, 2000, 2014,      2031, 2030, 2029}, //bajas
        {2038, 2061, 2057,      2036, 2033, 2034}  //altas
    };

    boolean[][] simultaneasSeleccionadas = {
        /*Derechas                    Izquierdas*/
        {false, false, false,      false, false, false}, //bajas
        {false, false, false,      false, false, false}, //altas
        {false, false, false,      false, false, false}  //exploradoras
    };

    String[][] nombreLuces = {
        /*Derechas                                                      Izquierdas*/
        {"Baja D3",  "Baja D2",  "Baja D1",      "Baja I1",  "Baja I2",  "Baja I3"}, //bajas
        {"Alta D3",  "Alta D2",  "Alta D1",      "Alta I1",  "Alta I2",  "Alta I3"}, //altas
        {"Explo D3", "Explo D2", "Explo D1",     "Explo I1", "Explo I2", "Explo I3"}  //exploradoras
    };

    //Codigos defectos          IntBaja     suma        desviacion 
    Integer[] codigosDefectos ={20000,      20001,      20002};

    //Codigos defectos             IntBaja     suma     desviacion 
    Integer[] codigosDefectosMotos ={20003,     0,      24006};
    
    String tipoLuxometro;
    private String placa;
    private String location;
    private static boolean error = false;
    private boolean mostrarMedidas = false;
    private int idPrueba;
    private int idUsuario;
    private double sumaTotal = 0;
    private double valorMaximo = 0;
    private boolean esLujan = false;
    private boolean esMoto = false;
    private boolean moonNuevo = false;

    public FrmLuxometroCapelec(String tipoLuxometro, String placa, int idHojaPrueba, int idPrueba, int idUsuario, String location, boolean ver) throws IOException {
        this.esMoto = tipoLuxometro.equalsIgnoreCase("MOONMOTOS");
        this.placa = placa;
        this.tipoLuxometro = tipoLuxometro;
        this.location= location;
        this.mostrarMedidas = ver;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        initComponents();
        if(tipoLuxometro.equalsIgnoreCase("CAPELEC")){
            FrmLuxometroCapelec.error = cargarPlacaCapelec();
            leerDatos.setEnabled(true);
        }else if(tipoLuxometro.equalsIgnoreCase("LUJAN")){
            FrmLuxometroCapelec.error = cargarPlacaCapelec();
            leerDatos.setEnabled(true);
            esLujan = true;
        }else{
            
            moonNuevo = tipoLuxometro.equalsIgnoreCase("moonNuevo");
            altaIzquierda1.setEnabled(true);
            altaDerecha1.setEnabled(true);
            exploDerecha1.setEnabled(true);
            exploIzquierda1.setEnabled(true);
            leerDatos.setVisible(false);
            
            ED3.setSelected(true);
            ED2.setSelected(true);
            ED1.setSelected(true);
            EI3.setSelected(true);
            EI2.setSelected(true);
            EI1.setSelected(true);
            
            
            if(esMoto){
                jLabel5.setVisible(false);
                altaIzquierda1.setEnabled(false);
                exploIzquierda1.setVisible(false);
                exploIzquierda2.setVisible(false);
                exploIzquierda3.setVisible(false);
                exploDerecha1.setVisible(false);
                exploDerecha2.setVisible(false);
                exploDerecha3.setVisible(false);
                altaDerecha1.setEnabled(false);
                bajaIzquierda1.setEnabled(false);
                ED3.setVisible(false);
                ED2.setVisible(false);
                ED1.setVisible(false);
                EI3.setVisible(false);
                EI2.setVisible(false);
                EI1.setVisible(false);
                intExploDerecha1.setVisible(false);
                intExploDerecha2.setVisible(false);
                intExploDerecha3.setVisible(false);
                intExploIzquierda1.setVisible(false);
                intExploIzquierda2.setVisible(false);
                intExploIzquierda3.setVisible(false);
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/moto-roja.jpg")));
                cambiarCodigosAMotos(codigoMedidas, codigoMedidasMotos);
                codigosDefectos[0] = codigosDefectosMotos[0]; //20003
                codigosDefectos[1] = codigosDefectosMotos[1]; //0
                codigosDefectos[2] = codigosDefectosMotos[2]; //24006
            }
        }

        desviIzqui1.setVisible(false);
        desvidere1.setVisible(false);
        desviIzqui2.setVisible(false);
        desvidere2.setVisible(false);
        desviIzqui3.setVisible(false);
        desvidere3.setVisible(false);
    }

    public static void cambiarCodigosAMotos(Integer[][] original, Integer[][] nuevosValores) {
        for (int i = 0; i < nuevosValores.length; i++) {
            if (original[i].length != nuevosValores[i].length) {
                throw new IllegalArgumentException("Las filas " + i + " no tienen la misma longitud");
            }

            for (int j = 0; j < nuevosValores[i].length; j++) {
                original[i][j] = nuevosValores[i][j];
            }
        }
    }
    
    private void tomarLasMedidas(int luzDerecha, boolean esDerecho) throws IOException, FileNotFoundException, InterruptedException {
        if(tipoLuxometro.equalsIgnoreCase("CAPELEC")){
            JOptionPane.showMessageDialog(null, "Luxometro listo y preparado para realizar la prueba.");
            JOptionPane.showMessageDialog(null, "Apenas finalice la prueba dele clic al boton leer datos y por ultimo al boton finalizar una vez comprobado");
        }else{
            cargarParametrosMoon(luzDerecha, esDerecho);
        }
    }

    private FrmLuxometroCapelec() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bajaIzquierda3 = new javax.swing.JButton();
        bajaIzquierda2 = new javax.swing.JButton();
        bajaDerecha1 = new javax.swing.JButton();
        bajaDerecha3 = new javax.swing.JButton();
        bajaDerecha2 = new javax.swing.JButton();
        bajaIzquierda1 = new javax.swing.JButton();
        altaDerecha3 = new javax.swing.JButton();
        altaDerecha2 = new javax.swing.JButton();
        altaDerecha1 = new javax.swing.JButton();
        altaIzquierda2 = new javax.swing.JButton();
        altaIzquierda3 = new javax.swing.JButton();
        exploDerecha1 = new javax.swing.JButton();
        altaIzquierda1 = new javax.swing.JButton();
        exploDerecha3 = new javax.swing.JButton();
        exploIzquierda1 = new javax.swing.JButton();
        exploIzquierda2 = new javax.swing.JButton();
        exploDerecha2 = new javax.swing.JButton();
        exploIzquierda3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        leerDatos = new javax.swing.JButton();
        intBajaIzquierda3 = new javax.swing.JLabel();
        intBajaIzquierda1 = new javax.swing.JLabel();
        intBajaIzquierda2 = new javax.swing.JLabel();
        desvidere1 = new javax.swing.JLabel();
        desvidere3 = new javax.swing.JLabel();
        intAltaDerecha3 = new javax.swing.JLabel();
        intBajaDerecha3 = new javax.swing.JLabel();
        intAltaDerecha1 = new javax.swing.JLabel();
        intExploDerecha3 = new javax.swing.JLabel();
        intExploDerecha2 = new javax.swing.JLabel();
        intAltaDerecha2 = new javax.swing.JLabel();
        intExploDerecha1 = new javax.swing.JLabel();
        intAltaIzquierda2 = new javax.swing.JLabel();
        intAltaIzquierda1 = new javax.swing.JLabel();
        intExploIzquierda1 = new javax.swing.JLabel();
        intAltaIzquierda3 = new javax.swing.JLabel();
        intExploIzquierda3 = new javax.swing.JLabel();
        intExploIzquierda2 = new javax.swing.JLabel();
        intBajaDerecha2 = new javax.swing.JLabel();
        desvidere2 = new javax.swing.JLabel();
        intBajaDerecha1 = new javax.swing.JLabel();
        desviIzqui1 = new javax.swing.JLabel();
        desviIzqui2 = new javax.swing.JLabel();
        desviIzqui3 = new javax.swing.JLabel();
        finalizar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        BD1 = new javax.swing.JCheckBox();
        BD2 = new javax.swing.JCheckBox();
        BD3 = new javax.swing.JCheckBox();
        BI1 = new javax.swing.JCheckBox();
        BI2 = new javax.swing.JCheckBox();
        BI3 = new javax.swing.JCheckBox();
        AD1 = new javax.swing.JCheckBox();
        AD2 = new javax.swing.JCheckBox();
        AD3 = new javax.swing.JCheckBox();
        AI1 = new javax.swing.JCheckBox();
        AI2 = new javax.swing.JCheckBox();
        AI3 = new javax.swing.JCheckBox();
        ED1 = new javax.swing.JCheckBox();
        ED2 = new javax.swing.JCheckBox();
        ED3 = new javax.swing.JCheckBox();
        EI1 = new javax.swing.JCheckBox();
        EI2 = new javax.swing.JCheckBox();
        EI3 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bajaIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-not.png"))); // NOI18N
        bajaIzquierda3.setEnabled(false);
        bajaIzquierda3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajaIzquierda3ActionPerformed(evt);
            }
        });

        bajaIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-not.png"))); // NOI18N
        bajaIzquierda2.setEnabled(false);
        bajaIzquierda2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajaIzquierda2ActionPerformed(evt);
            }
        });

        bajaDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-not.png"))); // NOI18N
        bajaDerecha1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajaDerecha1ActionPerformed(evt);
            }
        });

        bajaDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-not.png"))); // NOI18N
        bajaDerecha3.setEnabled(false);
        bajaDerecha3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajaDerecha3ActionPerformed(evt);
            }
        });

        bajaDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-not.png"))); // NOI18N
        bajaDerecha2.setEnabled(false);
        bajaDerecha2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajaDerecha2ActionPerformed(evt);
            }
        });

        bajaIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-not.png"))); // NOI18N
        bajaIzquierda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajaIzquierda1ActionPerformed(evt);
            }
        });

        altaDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-not.png"))); // NOI18N
        altaDerecha3.setEnabled(false);
        altaDerecha3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altaDerecha3ActionPerformed(evt);
            }
        });

        altaDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-not.png"))); // NOI18N
        altaDerecha2.setEnabled(false);
        altaDerecha2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altaDerecha2ActionPerformed(evt);
            }
        });

        altaDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-not.png"))); // NOI18N
        altaDerecha1.setEnabled(false);
        altaDerecha1.setFocusable(false);
        altaDerecha1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altaDerecha1ActionPerformed(evt);
            }
        });

        altaIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-not.png"))); // NOI18N
        altaIzquierda2.setEnabled(false);
        altaIzquierda2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altaIzquierda2ActionPerformed(evt);
            }
        });

        altaIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-not.png"))); // NOI18N
        altaIzquierda3.setEnabled(false);
        altaIzquierda3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altaIzquierda3ActionPerformed(evt);
            }
        });

        exploDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-not.png"))); // NOI18N
        exploDerecha1.setEnabled(false);
        exploDerecha1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploDerecha1ActionPerformed(evt);
            }
        });

        altaIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-not.png"))); // NOI18N
        altaIzquierda1.setEnabled(false);
        altaIzquierda1.setFocusable(false);
        altaIzquierda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altaIzquierda1ActionPerformed(evt);
            }
        });

        exploDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-not.png"))); // NOI18N
        exploDerecha3.setEnabled(false);
        exploDerecha3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploDerecha3ActionPerformed(evt);
            }
        });

        exploIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-not.png"))); // NOI18N
        exploIzquierda1.setEnabled(false);
        exploIzquierda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploIzquierda1ActionPerformed(evt);
            }
        });

        exploIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-not.png"))); // NOI18N
        exploIzquierda2.setEnabled(false);
        exploIzquierda2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploIzquierda2ActionPerformed(evt);
            }
        });

        exploDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-not.png"))); // NOI18N
        exploDerecha2.setEnabled(false);
        exploDerecha2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploDerecha2ActionPerformed(evt);
            }
        });

        exploIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-not.png"))); // NOI18N
        exploIzquierda3.setEnabled(false);
        exploIzquierda3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exploIzquierda3ActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/coche-plata.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel2.setText("Bajas");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel3.setText("Derecha");

        leerDatos.setText("Leer datos");
        leerDatos.setEnabled(false);
        leerDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leerDatosActionPerformed(evt);
            }
        });

        intBajaIzquierda3.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intBajaIzquierda3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intBajaIzquierda3.setText("I3");

        intBajaIzquierda1.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intBajaIzquierda1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intBajaIzquierda1.setText("I1");

        intBajaIzquierda2.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intBajaIzquierda2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intBajaIzquierda2.setText("I2");

        desvidere1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desvidere1.setText("0");

        desvidere3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desvidere3.setText("0");

        intAltaDerecha3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intAltaDerecha3.setText("D3");

        intBajaDerecha3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intBajaDerecha3.setText("D3");

        intAltaDerecha1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intAltaDerecha1.setText("D1");

        intExploDerecha3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intExploDerecha3.setText("D3");

        intExploDerecha2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intExploDerecha2.setText("D2");

        intAltaDerecha2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intAltaDerecha2.setText("D2");

        intExploDerecha1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intExploDerecha1.setText("D1");

        intAltaIzquierda2.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intAltaIzquierda2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intAltaIzquierda2.setText("I2");

        intAltaIzquierda1.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intAltaIzquierda1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intAltaIzquierda1.setText("I1");

        intExploIzquierda1.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intExploIzquierda1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intExploIzquierda1.setText("I1");

        intAltaIzquierda3.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intAltaIzquierda3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intAltaIzquierda3.setText("I3");

        intExploIzquierda3.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intExploIzquierda3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intExploIzquierda3.setText("I3");

        intExploIzquierda2.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        intExploIzquierda2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intExploIzquierda2.setText("I2");

        intBajaDerecha2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intBajaDerecha2.setText("D2");

        desvidere2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desvidere2.setText("0");

        intBajaDerecha1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        intBajaDerecha1.setText("D1");

        desviIzqui1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desviIzqui1.setText("0");

        desviIzqui2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desviIzqui2.setText("0");

        desviIzqui3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        desviIzqui3.setText("0");

        finalizar.setText("Finalizar");
        finalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finalizarActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel4.setText("Altas");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel5.setText("Exploradoras");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel6.setText("Izquierda");

        BD1.setSelected(true);
        BD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BD1ActionPerformed(evt);
            }
        });

        BD2.setSelected(true);
        BD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BD2ActionPerformed(evt);
            }
        });

        BD3.setSelected(true);

        BI1.setSelected(true);
        BI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BI1ActionPerformed(evt);
            }
        });

        BI2.setSelected(true);
        BI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BI2ActionPerformed(evt);
            }
        });

        BI3.setSelected(true);
        BI3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BI3ActionPerformed(evt);
            }
        });

        AD1.setSelected(true);
        AD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AD1ActionPerformed(evt);
            }
        });

        AD2.setSelected(true);
        AD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AD2ActionPerformed(evt);
            }
        });

        AD3.setSelected(true);
        AD3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AD3ActionPerformed(evt);
            }
        });

        AI1.setSelected(true);
        AI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AI1ActionPerformed(evt);
            }
        });

        AI2.setSelected(true);
        AI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AI2ActionPerformed(evt);
            }
        });

        AI3.setSelected(true);
        AI3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AI3ActionPerformed(evt);
            }
        });

        ED1.setSelected(true);
        ED1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ED1ActionPerformed(evt);
            }
        });

        ED2.setSelected(true);
        ED2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ED2ActionPerformed(evt);
            }
        });

        ED3.setSelected(true);
        ED3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ED3ActionPerformed(evt);
            }
        });

        EI1.setSelected(true);
        EI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EI1ActionPerformed(evt);
            }
        });

        EI2.setSelected(true);
        EI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EI2ActionPerformed(evt);
            }
        });

        EI3.setSelected(true);
        EI3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EI3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(desvidere3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bajaIzquierda3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(intBajaDerecha3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BD3)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bajaIzquierda2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(desvidere2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(intBajaDerecha2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BD2)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(115, 115, 115)
                                .addComponent(BD1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(desvidere1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(altaDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exploDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(intAltaDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AD3))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(intExploDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ED3)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(intExploDerecha2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ED2))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(altaDerecha2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(intAltaDerecha2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(AD2)))
                                .addComponent(exploDerecha2)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(altaDerecha1)
                                            .addComponent(intAltaDerecha1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(intExploDerecha1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(ED1))
                                                .addComponent(exploDerecha1))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(intBajaDerecha1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(bajaDerecha1))))
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                                        .addComponent(jLabel5)
                                        .addGap(32, 32, 32))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(104, 104, 104)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel4))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addComponent(AD1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bajaIzquierda1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(altaIzquierda1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exploIzquierda1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(desviIzqui1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BI1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(intBajaIzquierda1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(AI1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(intAltaIzquierda1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(EI1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(intExploIzquierda1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bajaDerecha2)
                            .addComponent(desviIzqui2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(BI2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intBajaIzquierda2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bajaDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(desviIzqui3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(BI3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intBajaIzquierda3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(altaIzquierda2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AI2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intAltaIzquierda2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(altaIzquierda3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AI3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intAltaIzquierda3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(exploIzquierda2)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(EI2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intExploIzquierda2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(exploIzquierda3)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(EI3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(intExploIzquierda3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(25, 25, 25))
            .addGroup(layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leerDatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(finalizar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(127, 127, 127))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(desvidere3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(desvidere2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(desvidere1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(desviIzqui1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(desviIzqui2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(desviIzqui3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bajaIzquierda3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bajaIzquierda1)
                            .addComponent(bajaDerecha2)
                            .addComponent(bajaDerecha3)
                            .addComponent(bajaDerecha1)
                            .addComponent(bajaIzquierda2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(intBajaDerecha1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BD1)
                    .addComponent(BD2)
                    .addComponent(intBajaDerecha2)
                    .addComponent(intBajaDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BD3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(intBajaIzquierda2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BI2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(intBajaIzquierda1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BI1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BI3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(intBajaIzquierda3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(altaDerecha2)
                            .addComponent(altaDerecha3)
                            .addComponent(altaDerecha1)
                            .addComponent(altaIzquierda1)
                            .addComponent(altaIzquierda2)
                            .addComponent(altaIzquierda3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AD2)
                            .addComponent(intAltaDerecha3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(intAltaDerecha2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(intAltaDerecha1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AD3)
                            .addComponent(intAltaIzquierda1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(AD1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AI1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AI2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(intAltaIzquierda2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(intAltaIzquierda3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AI3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exploDerecha2)
                            .addComponent(exploDerecha3)
                            .addComponent(exploDerecha1)
                            .addComponent(exploIzquierda1)
                            .addComponent(exploIzquierda2)
                            .addComponent(exploIzquierda3)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jLabel4)
                        .addGap(109, 109, 109)
                        .addComponent(jLabel5)))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(intExploIzquierda3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(intExploIzquierda2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(intExploIzquierda1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(EI1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(intExploDerecha1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ED1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(intExploDerecha2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ED2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(intExploDerecha3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ED3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(EI2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(EI3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(leerDatos)
                        .addGap(8, 8, 8)
                        .addComponent(finalizar)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(jLabel3))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(jLabel6)))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void leerDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leerDatosActionPerformed
        try {
            cargarParametrosCapelec();
        } catch (IOException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_leerDatosActionPerformed

    private void finalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalizarActionPerformed
        

        //Esta matriz hace referencia a cuales luces se tendran en el fur, dado que no todas las luces medidas se pueden tener en cuenta si se trata del moon
        boolean[][] lucesTenidasEnCuenta = {
            /*Derechas                    Izquierdas*/
            {BD3.isSelected(), BD2.isSelected(), BD1.isSelected(),      BI1.isSelected(), BI2.isSelected(), BI3.isSelected()}, //bajas
            {AD3.isSelected(), AD2.isSelected(), AD1.isSelected(),      AI1.isSelected(), AI2.isSelected(), AI3.isSelected()}, //altas
            {ED3.isSelected(), ED2.isSelected(), ED1.isSelected(),      EI1.isSelected(), EI2.isSelected(), EI3.isSelected()}  //exploradoras
        };

        boolean esAprobada = true;

        // Definir opciones del JComboBox
        String[] opciones = {"1", "2", "3", "4", "5", "6"};
        JComboBox<String> comboBox = new JComboBox<>(opciones);

        // Mostrar el JOptionPane
        int seleccion = esMoto ? JOptionPane.NO_OPTION : JOptionPane.showConfirmDialog(null, comboBox, "# de casos luces simultaneas", JOptionPane.OK_CANCEL_OPTION);
        
        // Si el usuario hace clic en OK, proceder
        if (seleccion == JOptionPane.OK_OPTION) {
            // Obtener el número de combinaciones seleccionado
            int numCombinaciones = Integer.parseInt((String) comboBox.getSelectedItem());

            // Repetir el proceso según el número de combinaciones seleccionadas
            for (int n = 0; n < numCombinaciones; n++) {
                // Crear y mostrar el diálogo para checkboxes
                JCheckBox[] checkboxes = new JCheckBox[18];
                JPanel panel = new JPanel(new GridLayout(3, 6)); // 3 filas, 6 columnas

                int count = 0;
                for (int i = 0; i < nombreLuces.length; i++) {
                    // Bucle para recorrer las columnas del arreglo
                    for (int j = 0; j < nombreLuces[i].length; j++) {
                        checkboxes[count] = new JCheckBox(nombreLuces[i][j]);
                        if (medidas[i+1][j] == null || !lucesTenidasEnCuenta[i][j]) {
                            checkboxes[count].setEnabled(false);
                            panel.add(checkboxes[count]);
                        } else {
                            panel.add(checkboxes[count]);
                        }
                        count++;
                    }
                }

                int result = JOptionPane.showConfirmDialog(null, panel, "Seleccione cuales luces se puede encender simultaneamente caso "+(n+1), JOptionPane.OK_CANCEL_OPTION);
                
                // Si el usuario hace clic en OK, proceder
                if (result == JOptionPane.OK_OPTION) {
                    // Actualizar el arreglo sumas con los valores de los checkboxes
                    sumaTotal = 0;
                    for (int i = 0; i < simultaneasSeleccionadas.length; i++) {
                        for (int j = 0; j < simultaneasSeleccionadas[i].length; j++) {
                            simultaneasSeleccionadas[i][j] = checkboxes[i * 6 + j].isSelected();

                            if(checkboxes[i * 6 + j].isSelected()) sumaTotal += medidas[i+1][j];
                        }
                    }
                    if(sumaTotal > valorMaximo){
                        valorMaximo = sumaTotal;
                        for (int i = 0; i < simultaneasSeleccionadas.length; i++) {
                            for (int j = 0; j < simultaneasSeleccionadas[i].length; j++) {
                                simultaneasSeleccionadas[i][j] = checkboxes[i * 6 + j].isSelected();
                            }
                        }
                    } 
                    System.out.println("Suma luces caso "+(n+1)+": "+sumaTotal);
                } else{
                    return;
                } 
            }

            if (valorMaximo > 225 && codigosDefectos[1] != null) {
                try {
                    if(Utilidades.getIsEditable() == 0) cargarDefecto(codigosDefectos[1]);
                    codigosDefectos[1] = null;
                } catch (SQLException ex) {
                    Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
                }
                esAprobada = false;
            }

            try {
                cargarMedida(2011, valorMaximo, "'N'"); //valor medida sumas con su codigo de medida
            } catch (SQLException ex) {
                Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }

        

        for (int i = 0; i < codigoMedidas.length; i++) {
            // Bucle para recorrer las columnas del arreglo
            for (int j = 0; j < codigoMedidas[i].length; j++) {
                if (medidas[i][j] == null) continue;
                if (i == 0 && !lucesTenidasEnCuenta[i][j]) continue; // Pregunta que angulos de las bajas no se tienen en cuenta
                if (i > 0 && !lucesTenidasEnCuenta[i-1][j]) continue; // Pregunta que intensidades de todas no las luces se tienen en cuenta
                //si entra dentro del if en continue omite todo lo de abajo y continua con la siguiente luz
                try {
                    //System.out.println("codigoMedidas[" + i + "][" + j + "] = " + codigoMedidas[i][j]);

                    String simultanea = "'N'";
                    if (i!=0 && simultaneasSeleccionadas[i-1][j] ) {
                        simultanea = "'Y'";
                    }

                    cargarMedida(codigoMedidas[i][j], medidas[i][j], simultanea);
                    if (i==0 && codigosDefectos[2] != null && (medidas[i][j] > 3.5 || medidas[i][j] < 0.5)) {
                        if(Utilidades.getIsEditable() == 0) cargarDefecto(codigosDefectos[2]);
                        codigosDefectos[2] = null;
                        esAprobada = false;
                    }

                    if (i==1 && codigosDefectos[0] != null && medidas[i][j] < 2.5) {
                        if(Utilidades.getIsEditable() == 0) cargarDefecto(codigosDefectos[0]);
                        codigosDefectos[0] = null;
                        esAprobada = false;
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        try {
            updatePruebaMethod(esAprobada);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_finalizarActionPerformed

    private void bajaDerecha1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajaDerecha1ActionPerformed
        try {
            tomarLasMedidas(2, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bajaDerecha1ActionPerformed

    private void bajaIzquierda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajaIzquierda1ActionPerformed
        try {
            tomarLasMedidas(2, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bajaIzquierda1ActionPerformed

    private void altaDerecha1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altaDerecha1ActionPerformed
        try {
            tomarLasMedidas(2, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_altaDerecha1ActionPerformed

    private void exploDerecha1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploDerecha1ActionPerformed
        try {
            tomarLasMedidas(2, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exploDerecha1ActionPerformed

    private void exploIzquierda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploIzquierda1ActionPerformed
        try {
            tomarLasMedidas(2, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exploIzquierda1ActionPerformed

    private void altaIzquierda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altaIzquierda1ActionPerformed
        try {
            tomarLasMedidas(2, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_altaIzquierda1ActionPerformed

    private void bajaIzquierda2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajaIzquierda2ActionPerformed
        try {
            tomarLasMedidas(1, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bajaIzquierda2ActionPerformed

    private void altaDerecha2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altaDerecha2ActionPerformed
        try {
            tomarLasMedidas(1, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_altaDerecha2ActionPerformed

    private void exploDerecha2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploDerecha2ActionPerformed
        try {
            tomarLasMedidas(1, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exploDerecha2ActionPerformed

    private void bajaDerecha2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajaDerecha2ActionPerformed
        try {
            tomarLasMedidas(1, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bajaDerecha2ActionPerformed

    private void altaIzquierda2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altaIzquierda2ActionPerformed
        try {
            tomarLasMedidas(1, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_altaIzquierda2ActionPerformed

    private void exploIzquierda2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploIzquierda2ActionPerformed
        try {
            tomarLasMedidas(1, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exploIzquierda2ActionPerformed

    private void bajaIzquierda3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajaIzquierda3ActionPerformed
        try {
            tomarLasMedidas(0, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bajaIzquierda3ActionPerformed

    private void altaDerecha3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altaDerecha3ActionPerformed
        try {
            tomarLasMedidas(0, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_altaDerecha3ActionPerformed

    private void exploDerecha3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploDerecha3ActionPerformed
        try {
            tomarLasMedidas(0, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exploDerecha3ActionPerformed

    private void bajaDerecha3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajaDerecha3ActionPerformed
        try {
            tomarLasMedidas(0, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_bajaDerecha3ActionPerformed

    private void altaIzquierda3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altaIzquierda3ActionPerformed
        try {
            tomarLasMedidas(0, true);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_altaIzquierda3ActionPerformed

    private void exploIzquierda3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exploIzquierda3ActionPerformed
        try {
            tomarLasMedidas(0, false);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exploIzquierda3ActionPerformed

    private void BD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BD1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BD1ActionPerformed

    private void BD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BD2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BD2ActionPerformed

    private void BI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BI1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BI1ActionPerformed

    private void BI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BI2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BI2ActionPerformed

    private void BI3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BI3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BI3ActionPerformed

    private void AD1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AD1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AD1ActionPerformed

    private void AD2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AD2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AD2ActionPerformed

    private void AD3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AD3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AD3ActionPerformed

    private void AI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AI1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AI1ActionPerformed

    private void AI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AI2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AI2ActionPerformed

    private void AI3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AI3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AI3ActionPerformed

    private void ED1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ED1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ED1ActionPerformed

    private void ED2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ED2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ED2ActionPerformed

    private void ED3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ED3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ED3ActionPerformed

    private void EI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EI1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EI1ActionPerformed

    private void EI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EI2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EI2ActionPerformed

    private void EI3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EI3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EI3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmLuxometroCapelec.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(!error){
                    FrmLuxometroCapelec capelec = new FrmLuxometroCapelec();
                    capelec.setVisible(true);
                }
            }
        });
    }
    
    private boolean cargarPlacaCapelec() throws FileNotFoundException, IOException {        
        //try retrieve data from file
        JOptionPane.showMessageDialog(null, "localizacion CG: "+location);
        File archivo = new File(location+"/CG/"+placa+".CG");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("[CARTEGRISE]");
            bw.newLine();
            bw.write("0200=" + this.placa);
            bw.newLine();
            bw.write("0320=" + this.placa+".P");
            bw.flush();
            return false;
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Disculpe, no Existe la Mustra Correspondiente de la Placa");
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Imposible cargar archivo propiedades");
            return true;
        }
    }//end of method cargarUrl

    private void cargarParametrosMoon(int posicionDere, boolean esDerecho) throws FileNotFoundException, IOException, InterruptedException {
        int posicionIzq = -1;
        if (posicionDere == 2) posicionIzq = 3;
        if (posicionDere == 1) posicionIzq = 4;
        if (posicionDere == 0) posicionIzq = 5;
        

        
        int[] lineaMedidas;

        if (moonNuevo) 
            /*lineas medidas:        inclinacionD  int.bajaD   int.altaD   int.exploradoraD    inclinacionI    int.bajaI   int.altaI   int.exploradoraI*/
            lineaMedidas = new int[]{19,           18,         28,         38,                 24,             23,         33,         43};
        else
            /*lineas medidas:        inclinacionD  int.bajaD   int.altaD   int.exploradoraD    inclinacionI    int.bajaI   int.altaI   int.exploradoraI*/
            lineaMedidas = new int[]{17,           16,         26,         36,                 22,             21,         31,         41};
        
        crearPlacaFile();

        if (esMoto && esDerecho) {
            lineaMedidas[0] = 22;
            lineaMedidas[1] = 21;
            lineaMedidas[2] = 31;
            lineaMedidas[4] = 17;
            lineaMedidas[5] = 16;
            lineaMedidas[6] = 26;
        }

        String cmdLocation = location.equals(".") ? System.getProperty("user.dir") : location;
        System.out.println("posicion luz derecha: "+posicionDere);
        final Process process = Runtime.getRuntime().exec(String.format("cmd /c start /wait %s/moon/luxometro.bat", cmdLocation));//INICIA PROGRAMA LUXOMETRO
        final int exitVal = process.waitFor();

        try {
            Thread.sleep(1000); // 1000 milisegundos = 1 segundo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (exitVal == 0){//MIENTRAS EL PROGRAMA NO SE CIERRE
            String ruta = location + "/moon/SALIDA.DAT";
            BufferedReader config = null;
            try {
                config = new BufferedReader(new FileReader(new File(ruta)));
                String linea;
                int contadorLineas = 0;
                while (true) {
                    contadorLineas++;

                    linea = config.readLine();
                    System.out.println("info Linea "+contadorLineas+": "+linea);


                    if (contadorLineas == lineaMedidas[7]+1) break;

                    if (linea.equals("") || linea == null || linea.equals(",")) continue;

                    if (contadorLineas == lineaMedidas[0]){
                        medidas[0][posicionDere] = Math.abs(Double.parseDouble(linea.split(",")[1]));
                        if (posicionDere == 2 && mostrarMedidas){
                            desvidere1.setText(String.valueOf(medidas[0][posicionDere])+ "%");
                            desvidere1.setVisible(true);
                        }
                        if (posicionDere == 1 && mostrarMedidas){
                            desvidere2.setText(String.valueOf(medidas[0][posicionDere])+ "%");
                            desvidere2.setVisible(true);
                        }
                        if (posicionDere == 0 && mostrarMedidas){
                            desvidere3.setText(String.valueOf(medidas[0][posicionDere])+ "%");
                            desvidere3.setVisible(true);
                        }
                    }

                    if (contadorLineas == lineaMedidas[1]){
                        System.out.println("info Linea :"+contadorLineas+" posicionDere: "+posicionDere);
                        if (linea.split(",").length < 2) medidas[1][posicionDere] = Double.parseDouble(linea);
                        else medidas[1][posicionDere] = Double.parseDouble(linea.split(",")[1]) / 1000;
                        if (posicionDere == 2){
                            bajaDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                            if(mostrarMedidas) intBajaDerecha1.setText(String.valueOf(medidas[1][posicionDere]) + " Klux" );
                        }
                        if (posicionDere == 1){
                            bajaDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                            if(mostrarMedidas) intBajaDerecha2.setText(String.valueOf(medidas[1][posicionDere]) + " Klux" );
                        }
                        if (posicionDere == 0){
                            bajaDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                            if(mostrarMedidas) intBajaDerecha3.setText(String.valueOf(medidas[1][posicionDere]) + " Klux" );
                        }
                    }

                    if (contadorLineas == lineaMedidas[2]){
                        medidas[2][posicionDere] = Double.parseDouble(linea.split(",")[1]) / 1000;

                        if (posicionDere == 2){
                            altaDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                            altaDerecha1.setEnabled(true);
                            if(mostrarMedidas) intAltaDerecha1.setText(String.valueOf(medidas[2][posicionDere]) + " Klux" );
                        }
                        if (posicionDere == 1){
                            altaDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                            altaDerecha2.setEnabled(true);
                            if(mostrarMedidas) intAltaDerecha2.setText(String.valueOf(medidas[2][posicionDere]) + " Klux" );
                        }
                        if (posicionDere == 0){
                            altaDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                            altaDerecha3.setEnabled(true);
                            if(mostrarMedidas) intAltaDerecha3.setText(String.valueOf(medidas[2][posicionDere]) + " Klux" );
                        }
                    }
                    
                    if (contadorLineas == lineaMedidas[3]){
                        medidas[3][posicionDere] = Double.parseDouble(linea.split(",")[1]) / 1000;

                        if (posicionDere == 2){
                            exploDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                            exploDerecha1.setEnabled(true);
                            if(mostrarMedidas) intExploDerecha1.setText(String.valueOf(medidas[3][posicionDere]) + " Klux" );
                        }
                        if (posicionDere == 1){
                            exploDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                            exploDerecha2.setEnabled(true);
                            if(mostrarMedidas) intExploDerecha2.setText(String.valueOf(medidas[3][posicionDere]) + " Klux" );
                        }
                        if (posicionDere == 0){
                            exploDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                            exploDerecha3.setEnabled(true);
                            if(mostrarMedidas) intExploDerecha3.setText(String.valueOf(medidas[3][posicionDere]) + " Klux" );
                        }
                    }

                    if (contadorLineas == lineaMedidas[4]){
                        medidas[0][posicionIzq] = Math.abs(Double.parseDouble(linea.split(",")[1]));
                        if (posicionIzq == 3 && mostrarMedidas){
                            desviIzqui1.setText(String.valueOf(medidas[0][3])+ "%");
                            desviIzqui1.setVisible(true);
                        }
                        if (posicionIzq == 4 && mostrarMedidas){
                            desviIzqui2.setText(String.valueOf(medidas[0][3])+ "%");
                            desviIzqui2.setVisible(true);
                        }
                        if (posicionIzq == 5 && mostrarMedidas){
                            desviIzqui3.setText(String.valueOf(medidas[0][3])+ "%");
                            desviIzqui3.setVisible(true);
                        }
                    }

                    if (contadorLineas == lineaMedidas[5]){
                        medidas[1][posicionIzq] = Double.parseDouble(linea.split(",")[1]) / 1000;
                        if (posicionIzq == 3){
                            bajaIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                            if(mostrarMedidas) intBajaIzquierda1.setText(String.valueOf(medidas[1][posicionIzq]) + " Klux" );
                        }
                        if (posicionIzq == 4){
                            bajaIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                            if(mostrarMedidas) intBajaIzquierda2.setText(String.valueOf(medidas[1][posicionIzq]) + " Klux" );
                        }
                        if (posicionIzq == 5){
                            bajaIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                            if(mostrarMedidas) intBajaIzquierda3.setText(String.valueOf(medidas[1][posicionIzq]) + " Klux" );
                        }
                    }

                    if (contadorLineas == lineaMedidas[6]){
                        medidas[2][posicionIzq] = Double.parseDouble(linea.split(",")[1]) / 1000;

                        if (posicionIzq == 3){
                            altaIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                            altaIzquierda1.setEnabled(true);
                            if(mostrarMedidas) intAltaIzquierda1.setText(String.valueOf(medidas[2][posicionIzq]) + " Klux" );
                        }
                        if (posicionIzq == 4){
                            altaIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                            altaIzquierda2.setEnabled(true);
                            if(mostrarMedidas) intAltaIzquierda2.setText(String.valueOf(medidas[2][posicionIzq]) + " Klux" );
                        }
                        if (posicionIzq == 5){
                            altaIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                            altaIzquierda3.setEnabled(true);
                            if(mostrarMedidas) intAltaIzquierda3.setText(String.valueOf(medidas[2][posicionIzq]) + " Klux" );
                        }

                    }
                    
                    if (contadorLineas == lineaMedidas[7]){
                        medidas[3][posicionIzq] = Double.parseDouble(linea.split(",")[1]) / 1000;

                        if (posicionIzq == 3){
                            exploIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                            exploIzquierda1.setEnabled(true);
                            if(mostrarMedidas) intExploIzquierda1.setText(String.valueOf(medidas[3][posicionIzq]) + " Klux" );
                        }
                        if (posicionIzq == 4){
                            exploIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                            exploIzquierda2.setEnabled(true);
                            if(mostrarMedidas) intExploIzquierda2.setText(String.valueOf(medidas[3][posicionIzq]) + " Klux" );
                        }
                        if (posicionIzq == 5){
                            exploIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                            exploIzquierda3.setEnabled(true);
                            if(mostrarMedidas) intExploIzquierda3.setText(String.valueOf(medidas[3][posicionIzq]) + " Klux" );
                        }
                        break;
                    }
                }

                if (posicionDere == 2) {
                    desvidere2.setEnabled(true);
                    bajaDerecha2.setEnabled(true);
                    altaIzquierda2.setEnabled(true);
                    exploDerecha2.setEnabled(true);
                    desviIzqui2.setEnabled(true);
                    bajaIzquierda2.setEnabled(true);
                    altaDerecha2.setEnabled(true);
                    exploIzquierda2.setEnabled(true);
                    if (esMoto) {
                        bajaIzquierda1.setEnabled(true);
                        altaDerecha1.setEnabled(true);
                    }
                }


                if (posicionDere == 1) {
                    desvidere3.setEnabled(true);
                    bajaDerecha3.setEnabled(true);
                    altaIzquierda3.setEnabled(true);
                    exploDerecha3.setEnabled(true);
                    desviIzqui3.setEnabled(true);
                    bajaIzquierda3.setEnabled(true);
                    altaDerecha3.setEnabled(true);
                    exploIzquierda3.setEnabled(true);
                }

                // Crear un objeto File
                File archivo = new File(ruta);

                // Verificar si el archivo existe
                if (archivo.exists()) {
                    // Intentar vaciar el contenido del archivo
                    try (FileWriter fw = new FileWriter(archivo, false)) {
                        // Al abrir el archivo en modo de escritura sin append, se trunca el contenido
                        System.out.println("El archivo fue vaciado con éxito.");
                    } catch (IOException e) {
                        System.out.println( "No se pudo vaciar el archivo: "+ e.getMessage());
                    }
                } else {
                    System.out.println("El archivo no existe: "+ ruta);
                }

            }catch (IOException ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(null, "Error al tratar de leer el archivo de medicion. Revise la carpeta: " + ruta);
            } finally {
                if (config != null) {
                    try {
                        config.close();
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                }
            }
        }else{
            JOptionPane.showMessageDialog(null, "Revise el archivo luxometro.bat y verifique que la ubicacion del programa sea el correcto");
        }
    }

    private void crearPlacaFile() {
        try {
            File file = new File(location + "/moon/ENTRADA.DAT");

            if (!file.exists()) {
                file.createNewFile();
            }

            

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);


            String tipo = "M1";
            String tieneLuzAlta = "";

            if (esMoto) {
                tipo = "L3e";
                tieneLuzAlta = Utilidades2.dialogo2Opciones(
                    "Si",
                    "No", 
                    "Tiene altas", 
                    "¿El vehiculo tiene luces altas?") ? "Y" : "N";
            }

            System.out.println("Tiene luces altas: " + tieneLuzAlta);

            bw.write(placa);
            bw.newLine();
            bw.write(tipo);
            bw.newLine();
            bw.write("");
            bw.newLine();
            bw.write("");
            bw.newLine();
            bw.write("");
            bw.newLine();
            bw.write("");
            bw.newLine();
            bw.write(tieneLuzAlta);
            bw.close();
            System.out.println("Se logro crear el archivo-placa ENTRADA.DAT");
            File file2 = new File(location + "/moon/SALIDA.DAT");
            if (!file2.exists()) {
                file2.createNewFile();
            }
            System.out.println("Se logro crear el archivo-placa SALIDA.DAT");
        } catch (IOException e) {
            System.out.println( "Problemas al crear el archivo de entrada: "+ e.getMessage());
        }
    }
    
    private void cargarParametrosCapelec() throws FileNotFoundException, IOException {

        String ruta = location + "/RES/" + this.placa + ".P";
        BufferedReader config = null;

        try {
            config = new BufferedReader(new FileReader(new File(ruta)));
            String line;

            while ((line = config.readLine()) != null) {

                if (line.startsWith(esLujan ? "491" :"7703")){
                    medidas[0][3] = Math.abs(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())));
                    if(mostrarMedidas){
                        desviIzqui1.setText(String.valueOf(medidas[0][3])+ "%");
                        desviIzqui1.setVisible(true);
                    } 
                }if (line.startsWith("7704")){
                    medidas[1][3] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    bajaDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                    if(mostrarMedidas) intBajaIzquierda1.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }if (line.startsWith(esLujan ? "493" : "7708")){
                    medidas[0][2] = Math.abs(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())));
                    if(mostrarMedidas){
                        desvidere1.setText(medidas[0][2] + "%" );
                        desvidere1.setVisible(true);
                    } 
                }if (line.startsWith("7709")){
                    medidas[1][2] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    bajaIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                    if(mostrarMedidas) intBajaDerecha1.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                
                
                if (line.startsWith(esLujan ? "4912" : "7847")){
                    medidas[0][4] = Math.abs(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())));
                    if(mostrarMedidas){
                        desviIzqui2.setText(medidas[0][4] + "%" );
                        desviIzqui2.setVisible(true);
                    } 
                }if (line.startsWith(esLujan ? "77042" : "7848")){
                    medidas[1][4] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    bajaIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                    bajaIzquierda2.setEnabled(true);
                    if(mostrarMedidas) intBajaIzquierda2.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                    
                }if (line.startsWith(esLujan ? "4932" : "7851")){
                    medidas[0][1] = Math.abs(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())));
                    if(mostrarMedidas){
                        desvidere2.setText(medidas[0][1] + "%" );
                        desvidere2.setVisible(true);
                    } 
                }if (line.startsWith(esLujan ? "77092" : "7852")){
                    medidas[1][1] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    bajaDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                    bajaDerecha2.setEnabled(true);
                    if(mostrarMedidas) intBajaDerecha2.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                
                if (line.startsWith(esLujan ? "4913" : "7867")){
                    medidas[0][5] = Math.abs(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())));
                    if(mostrarMedidas){
                        desviIzqui3.setText(medidas[0][5] + "%" );
                        desviIzqui3.setVisible(true);
                    } 
                }if (line.startsWith(esLujan ? "77043" : "7868")){
                    medidas[1][5] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    bajaIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                    bajaIzquierda3.setEnabled(true);
                    if(mostrarMedidas) intBajaIzquierda3.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }if (line.startsWith(esLujan ? "4933" : "7871")){
                    medidas[0][0] = Math.abs(Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length())));
                    if(mostrarMedidas){
                        desvidere3.setText(medidas[0][0] + "%" );
                        desvidere3.setVisible(true);
                    } 
                }if (line.startsWith(esLujan ? "77093" : "7872")){
                    medidas[1][0] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    bajaDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/baja-izquierda-ok.png")));
                    bajaDerecha3.setEnabled(true);
                    if(mostrarMedidas) intBajaDerecha3.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }

                if (line.startsWith("7724")){
                    medidas[2][2] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    altaDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                    altaDerecha1.setEnabled(true);
                    if(mostrarMedidas) intAltaIzquierda1.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith("7729")){
                    medidas[2][3] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    altaIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                    altaIzquierda1.setEnabled(true);
                    if(mostrarMedidas) intAltaDerecha1.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith("7785")){
                    medidas[2][1] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    altaDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                    altaDerecha2.setEnabled(true);
                    if(mostrarMedidas) intAltaIzquierda2.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith("7790")){
                    medidas[2][4] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    altaIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                    altaIzquierda2.setEnabled(true);
                    if(mostrarMedidas) intAltaDerecha2.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith("7799")){
                    medidas[2][0] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    altaDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                    altaDerecha3.setEnabled(true);
                    if(mostrarMedidas) intAltaIzquierda3.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith("7804")){
                    medidas[2][5] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    altaIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/alta-izquierda-ok.png")));
                    altaIzquierda3.setEnabled(true);
                    if(mostrarMedidas) intAltaDerecha3.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }


                if (line.startsWith(esLujan ? "7771" : "7744")){
                    medidas[3][2] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    exploIzquierda1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                    exploIzquierda1.setEnabled(true);
                    if(mostrarMedidas) intExploIzquierda1.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith(esLujan ? "7733" : "7749")){
                    medidas[3][3] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    exploDerecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                    exploDerecha1.setEnabled(true);
                    if(mostrarMedidas) intExploDerecha1.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith(esLujan ? "77712" : "7813")){
                    medidas[3][1] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    exploIzquierda2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                    exploIzquierda2.setEnabled(true);
                    if(mostrarMedidas) intExploIzquierda2.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith(esLujan ? "77332" : "7818")){
                    medidas[3][4] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    exploDerecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                    exploDerecha2.setEnabled(true);
                    if(mostrarMedidas) intExploDerecha2.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith(esLujan ? "77713" : "7886")){
                    medidas[3][0] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    exploIzquierda3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                    exploIzquierda3.setEnabled(true);
                    if(mostrarMedidas) intExploIzquierda3.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }
                if (line.startsWith(esLujan ? "77333" : "7891")){
                    medidas[3][5] = Double.parseDouble(line.substring(line.indexOf("=") + 1, line.length()));
                    exploDerecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exploradoras-izquierdas-ok.png")));
                    exploDerecha3.setEnabled(true);
                    if(mostrarMedidas) intExploDerecha3.setText(line.substring(line.indexOf("=") + 1, line.length()) + " Klux" );
                }

                if(line.startsWith("[CRC]")){
                    break;
                }

            }

            System.out.println("Medidas luxometro: ");
            for (int i = 0; i < medidas.length; i++) {
                for (int j = 0; j < medidas[i].length; j++) {
                    
                    System.out.print(medidas[i][j] + "\t");
                }
                System.out.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "Error al tratar de leer el archivo de medicion. Revise la carpeta: " + ruta);
        } finally {
            if (config != null) {
                try {
                    config.close();
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private void cargarMedida(Integer codigoMedida, Double valorMedida, String simultanea) throws SQLException{
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContraseña());
                Statement stmt = conexion.createStatement()) {

            
            String addMedida = "INSERT INTO medidas (MEASURETYPE, Valor_medida, TEST, Simult) "+
            "VALUES("+codigoMedida+","+valorMedida+","+idPrueba+","+simultanea+");";

            System.out.println("Query ejecutado para insertar medida:");
            System.out.println(addMedida);
            
            stmt.executeUpdate(addMedida);
        }
    }

    private void cargarDefecto(int codigoDefecto) throws SQLException{
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContraseña());
                Statement stmt = conexion.createStatement()) {
            
            String addDefecto = "INSERT INTO defxprueba (id_defecto, id_prueba) "+
            "VALUES("+codigoDefecto+","+idPrueba+");";

            System.out.println("Query ejecutado para insertar defecto:");
            System.out.println(addDefecto);
            
            stmt.executeUpdate(addDefecto);
        }
    }

    public void updatePruebaMethod(boolean esAprobada) throws IOException{

        String comentario = Utilidades.obtenerComentario();
        System.out.println("Comentario: " + comentario);

        String serialEquipo = UtilPropiedades.cargarPropiedad("serialLux", "seriales.properties");

        if (esAprobada || (Utilidades.getIsEditable() == 0 && !esAprobada)) {
            Utilidades.actualizarPrueba(true, esAprobada, (long) idUsuario, serialEquipo, (long)idPrueba, comentario);
        } else {
            Utilidades.actualizarPrueba(false, false, (long) idUsuario, serialEquipo, (long) idPrueba, comentario);
        }

        finalizar.setEnabled(false);
        finalizar.setText("Prueba finalizada");

        JOptionPane.showMessageDialog(null, "Prueba terminada con exito");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox AD1;
    private javax.swing.JCheckBox AD2;
    private javax.swing.JCheckBox AD3;
    private javax.swing.JCheckBox AI1;
    private javax.swing.JCheckBox AI2;
    private javax.swing.JCheckBox AI3;
    private javax.swing.JCheckBox BD1;
    private javax.swing.JCheckBox BD2;
    private javax.swing.JCheckBox BD3;
    private javax.swing.JCheckBox BI1;
    private javax.swing.JCheckBox BI2;
    private javax.swing.JCheckBox BI3;
    private javax.swing.JCheckBox ED1;
    private javax.swing.JCheckBox ED2;
    private javax.swing.JCheckBox ED3;
    private javax.swing.JCheckBox EI1;
    private javax.swing.JCheckBox EI2;
    private javax.swing.JCheckBox EI3;
    private javax.swing.JButton altaDerecha1;
    private javax.swing.JButton altaDerecha2;
    private javax.swing.JButton altaDerecha3;
    private javax.swing.JButton altaIzquierda1;
    private javax.swing.JButton altaIzquierda2;
    private javax.swing.JButton altaIzquierda3;
    private javax.swing.JButton bajaDerecha1;
    private javax.swing.JButton bajaDerecha2;
    private javax.swing.JButton bajaDerecha3;
    private javax.swing.JButton bajaIzquierda1;
    private javax.swing.JButton bajaIzquierda2;
    private javax.swing.JButton bajaIzquierda3;
    private javax.swing.JLabel desviIzqui1;
    private javax.swing.JLabel desviIzqui2;
    private javax.swing.JLabel desviIzqui3;
    private javax.swing.JLabel desvidere1;
    private javax.swing.JLabel desvidere2;
    private javax.swing.JLabel desvidere3;
    private javax.swing.JButton exploDerecha1;
    private javax.swing.JButton exploDerecha2;
    private javax.swing.JButton exploDerecha3;
    private javax.swing.JButton exploIzquierda1;
    private javax.swing.JButton exploIzquierda2;
    private javax.swing.JButton exploIzquierda3;
    private javax.swing.JButton finalizar;
    private javax.swing.JLabel intAltaDerecha1;
    private javax.swing.JLabel intAltaDerecha2;
    private javax.swing.JLabel intAltaDerecha3;
    private javax.swing.JLabel intAltaIzquierda1;
    private javax.swing.JLabel intAltaIzquierda2;
    private javax.swing.JLabel intAltaIzquierda3;
    private javax.swing.JLabel intBajaDerecha1;
    private javax.swing.JLabel intBajaDerecha2;
    private javax.swing.JLabel intBajaDerecha3;
    private javax.swing.JLabel intBajaIzquierda1;
    private javax.swing.JLabel intBajaIzquierda2;
    private javax.swing.JLabel intBajaIzquierda3;
    private javax.swing.JLabel intExploDerecha1;
    private javax.swing.JLabel intExploDerecha2;
    private javax.swing.JLabel intExploDerecha3;
    private javax.swing.JLabel intExploIzquierda1;
    private javax.swing.JLabel intExploIzquierda2;
    private javax.swing.JLabel intExploIzquierda3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JButton leerDatos;
    // End of variables declaration//GEN-END:variables
}
