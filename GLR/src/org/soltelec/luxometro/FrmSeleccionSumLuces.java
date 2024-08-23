/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import java.awt.Component;
import javax.swing.JOptionPane;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import javax.persistence.EntityManager;
import javax.swing.JTextField;
/**
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class FrmSeleccionSumLuces extends javax.swing.JDialog {

    private double[] luzBajaDerecha;
    private double[] luzAltaDerecha;
    private double[] luzBajaIzquierda;
    private double[] luzAltaIzquierda;
    double sumaExploradoras;    
    double acumSumaIntensidad;
    double acumSumaIntensidad2;
    double acumReturnSum;
    private String[] simCase1 = {"N","N","N","N","N","N","N","N","N","N","N","N","N","N","N","N","N"};
    private String[] simCase2 = {"N","N","N","N","N","N","N","N","N","N","N","N","N","N","N","N","N"};
    String [] lucesSimultaneas = new String[17];
    
    //---
    ////////////////////////////////////////////PARA NO PERDER LA REFERENCIA DEL OBJETO///////////

      
    java.awt.Frame frame;
       

    FrmSeleccionSumLuces(double[] luzBajaDerecha,double[] luzAltaDerecha,double[] luzBajaIzquierda,double[] luzAltaIzquierda,double sumaExploradoras) {
        initComponents();
         Dimension d = new java.awt.Dimension(1050, 900);
        setSize(d);
        this.luzBajaDerecha=luzBajaDerecha;
        this.luzAltaDerecha=luzAltaDerecha;
        this.luzBajaIzquierda=luzBajaIzquierda;
        this.luzAltaIzquierda=luzAltaIzquierda;
        this.sumaExploradoras=sumaExploradoras;
        setTitle("Sum. Luces..¡");
        bajDerFar2Com1.setEnabled(false);
        bajDerFar3Com1.setEnabled(false);
        bajDerFar4Com1.setEnabled(false);
        
        bajDerFar2Com2.setEnabled(false);
        bajDerFar3Com2.setEnabled(false);
        bajDerFar4Com2.setEnabled(false);
        if(this.luzBajaDerecha.length==2){
             bajDerFar2Com1.setEnabled(true);
            bajDerFar2Com2.setEnabled(true);
        }
        if(this.luzBajaDerecha.length==3){
            bajDerFar2Com2.setEnabled(true);
            bajDerFar3Com2.setEnabled(true);
            bajDerFar2Com1.setEnabled(true);
            bajDerFar3Com1.setEnabled(true);
        }
        if(this.luzBajaDerecha.length==4){
           bajDerFar2Com2.setEnabled(true);
           bajDerFar3Com2.setEnabled(true);
           bajDerFar4Com2.setEnabled(true);
           bajDerFar2Com1.setEnabled(true);
           bajDerFar3Com1.setEnabled(true);
           bajDerFar4Com1.setEnabled(true);
        }
        bajIzqrFar2Com2.setEnabled(false);
        bajIzqrFar3Com2.setEnabled(false);
        bajIzqrFar4Com2.setEnabled(false);        
        bajIzqrFar2Com1.setEnabled(false);
        bajIzqrFar3Com1.setEnabled(false);
        bajIzqrFar4Com1.setEnabled(false);
        
        if(this.luzBajaIzquierda.length==2){
            bajIzqrFar2Com2.setEnabled(true);
            bajIzqrFar2Com1.setEnabled(true);
        }
        if(this.luzBajaIzquierda.length==3){
            bajIzqrFar2Com2.setEnabled(true);
            bajIzqrFar3Com2.setEnabled(true);
            bajIzqrFar2Com1.setEnabled(true);
            bajIzqrFar3Com1.setEnabled(true);
        }
        if(this.luzBajaIzquierda.length==4){
           bajIzqrFar2Com2.setEnabled(true);
           bajIzqrFar3Com2.setEnabled(true);
           bajIzqrFar4Com2.setEnabled(true);
           bajIzqrFar2Com1.setEnabled(true);
           bajIzqrFar3Com1.setEnabled(true);
           bajIzqrFar4Com1.setEnabled(true);
        }
        altDerFar2Com2.setEnabled(false);
        altDerFar3Com2.setEnabled(false);
        altDerFar4Com2.setEnabled(false);
        
        altDerFar2Com1.setEnabled(false);
        altDerFar3Com1.setEnabled(false);
        altDerFar4Com1.setEnabled(false);
        
        if(this.luzAltaDerecha.length==2){
            altDerFar2Com2.setEnabled(true);
            altDerFar2Com1.setEnabled(true);
        }
        if(this.luzAltaDerecha.length==3){
            altDerFar2Com2.setEnabled(true);
            altDerFar3Com2.setEnabled(true);
            altDerFar2Com1.setEnabled(true);
            altDerFar3Com1.setEnabled(true);
        }
        if(this.luzAltaDerecha.length==4){
           altDerFar2Com2.setEnabled(true);
           altDerFar3Com2.setEnabled(true);
           altDerFar4Com2.setEnabled(true);
           altDerFar2Com1.setEnabled(true);
           altDerFar3Com1.setEnabled(true);
           altDerFar4Com1.setEnabled(true);
        }
        altIzqFar2Com2.setEnabled(false);
        altIzqFar3Com2.setEnabled(false);
        altIzqFar4Com2.setEnabled(false);
        
         altIzqFar2Com1.setEnabled(false);
        altIzqFar3Com1.setEnabled(false);
        altIzqFar4Com1.setEnabled(false);
        
        if(this.luzAltaIzquierda.length==2){
           altIzqFar2Com2.setEnabled(true);
           altIzqFar2Com1.setEnabled(true);
        }
        if(this.luzAltaIzquierda.length==3){
           altIzqFar2Com2.setEnabled(true);
           altIzqFar3Com2.setEnabled(true);
           altIzqFar2Com1.setEnabled(true);
           altIzqFar3Com1.setEnabled(true);
        }
        if(this.luzAltaIzquierda.length==4){
           altIzqFar2Com2.setEnabled(true);
           altIzqFar3Com2.setEnabled(true);
           altIzqFar4Com2.setEnabled(true);
           
           altIzqFar2Com1.setEnabled(true);
           altIzqFar3Com1.setEnabled(true);
           altIzqFar4Com1.setEnabled(true);
        }
        
        this.requestFocusInWindow();
        System.out.println("c1");
        setResizable(false);
    }

   
    //////////////////////////////////////////////PARA NO PERDER LA REFERENCIA DEL OBJETO/////////

    public FrmSeleccionSumLuces(java.awt.Frame frame,ModalityType modalityType, boolean modal, int idVehiculo, int hojaPruebasActual, int idPrueba, int idUsuario) {
        super(frame, modalityType);     
        initComponents();
       Dimension d = new java.awt.Dimension(788, 272);
        setSize(d);
        setResizable(false);        
       
       
 }
    public FrmSeleccionSumLuces(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d);
        setResizable(false);
    }
    public double getAcumSumaIntensidad() {
        return acumReturnSum;
    }
    
    public String[] getLucesSimultaneas(){
        return lucesSimultaneas;
    }

    public void setAcumSumaIntensidad(double acumReturnSum) {
        this.acumReturnSum = acumReturnSum;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        sumExploradoraC1 = new javax.swing.JCheckBox();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel12 = new javax.swing.JPanel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        bajDerFar1Com1 = new javax.swing.JCheckBox();
        bajDerFar3Com1 = new javax.swing.JCheckBox();
        bajDerFar2Com1 = new javax.swing.JCheckBox();
        bajDerFar4Com1 = new javax.swing.JCheckBox();
        bajIzqrFar1Com1 = new javax.swing.JCheckBox();
        bajIzqrFar2Com1 = new javax.swing.JCheckBox();
        bajIzqrFar4Com1 = new javax.swing.JCheckBox();
        bajIzqrFar3Com1 = new javax.swing.JCheckBox();
        altDerFar1Com1 = new javax.swing.JCheckBox();
        altDerFar2Com1 = new javax.swing.JCheckBox();
        altDerFar3Com1 = new javax.swing.JCheckBox();
        altDerFar4Com1 = new javax.swing.JCheckBox();
        altIzqFar1Com1 = new javax.swing.JCheckBox();
        altIzqFar2Com1 = new javax.swing.JCheckBox();
        altIzqFar3Com1 = new javax.swing.JCheckBox();
        altIzqFar4Com1 = new javax.swing.JCheckBox();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        bajDerFar1Com2 = new javax.swing.JCheckBox();
        bajDerFar3Com2 = new javax.swing.JCheckBox();
        bajDerFar2Com2 = new javax.swing.JCheckBox();
        bajDerFar4Com2 = new javax.swing.JCheckBox();
        bajIzqrFar1Com2 = new javax.swing.JCheckBox();
        bajIzqrFar2Com2 = new javax.swing.JCheckBox();
        bajIzqrFar4Com2 = new javax.swing.JCheckBox();
        bajIzqrFar3Com2 = new javax.swing.JCheckBox();
        altDerFar1Com2 = new javax.swing.JCheckBox();
        altDerFar2Com2 = new javax.swing.JCheckBox();
        altDerFar3Com2 = new javax.swing.JCheckBox();
        altDerFar4Com2 = new javax.swing.JCheckBox();
        altIzqFar1Com2 = new javax.swing.JCheckBox();
        altIzqFar2Com2 = new javax.swing.JCheckBox();
        altIzqFar3Com2 = new javax.swing.JCheckBox();
        altIzqFar4Com2 = new javax.swing.JCheckBox();
        jLabel106 = new javax.swing.JLabel();
        guardar = new javax.swing.JButton();
        jLabel84 = new javax.swing.JLabel();
        sumExploradoraC2 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro Profundidad Labrado");
        setAlwaysOnTop(true);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(new java.awt.Font("Serif", 1, 36)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("<html><center>Seleccion Sumatoria</center></html>");

        

        sumExploradoraC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumExploradoraC1chkHabilitar5ActionPerformed(evt);
            }
        });

        jPanel12.setToolTipText("");
        jPanel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel85.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel85.setText("Baja Derecha:");

        jLabel86.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel86.setText("FAROLA 1:");

        jLabel87.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel87.setText("Baja Izquierda:");

        jLabel88.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel88.setText("FAROLA 1:");

        jLabel89.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel89.setText("Alta Izquierda:");

        jLabel90.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel90.setText("Alta Derecha:");

        jLabel91.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel91.setText("FAROLA 2:");

        jLabel92.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel92.setText("FAROLA 3:");

        jLabel93.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel93.setText("FAROLA 4:");

        jLabel94.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel94.setText("FAROLA 2:");

        jLabel95.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel95.setText("FAROLA 3:");

        jLabel96.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel96.setText("FAROLA 4:");

        jLabel97.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel97.setText("FAROLA 2:");

        jLabel98.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel98.setText("FAROLA 3:");

        jLabel99.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel99.setText("FAROLA 4:");

        jLabel100.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel100.setText("FAROLA 2:");

        jLabel101.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel101.setText("FAROLA 3:");

        jLabel102.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel102.setText("FAROLA 4:");

        jLabel103.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel103.setText("FAROLA 1:");

        jLabel104.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel104.setText("FAROLA 1:");

        bajDerFar1Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajDerFar1Com1chkHabilitar2ActionPerformed(evt);
            }
        });

        bajDerFar3Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajDerFar3Com1chkHabilitar3ActionPerformed(evt);
            }
        });

        bajDerFar2Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajDerFar2Com1chkHabilitar4ActionPerformed(evt);
            }
        });

        bajDerFar4Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajDerFar4Com1chkHabilitar5ActionPerformed(evt);
            }
        });

        bajIzqrFar1Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajIzqrFar1Com1chkHabilitar6ActionPerformed(evt);
            }
        });

        bajIzqrFar2Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajIzqrFar2Com1chkHabilitar7ActionPerformed(evt);
            }
        });

        bajIzqrFar4Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajIzqrFar4Com1chkHabilitar8ActionPerformed(evt);
            }
        });

        bajIzqrFar3Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bajIzqrFar3Com1chkHabilitar9ActionPerformed(evt);
            }
        });

        altDerFar1Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altDerFar1Com1chkHabilitar6ActionPerformed(evt);
            }
        });

        altDerFar2Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altDerFar2Com1chkHabilitar7ActionPerformed(evt);
            }
        });

        altDerFar3Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altDerFar3Com1chkHabilitar9ActionPerformed(evt);
            }
        });

        altDerFar4Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altDerFar4Com1chkHabilitar8ActionPerformed(evt);
            }
        });

        altIzqFar1Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altIzqFar1Com1chkHabilitar6ActionPerformed(evt);
            }
        });

        altIzqFar2Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altIzqFar2Com1chkHabilitar7ActionPerformed(evt);
            }
        });

        altIzqFar3Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altIzqFar3Com1chkHabilitar9ActionPerformed(evt);
            }
        });

        altIzqFar4Com1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                altIzqFar4Com1chkHabilitar8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator5)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel85)
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel86)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bajDerFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(34, 34, 34)))
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel92)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(bajDerFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addComponent(jLabel93)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(bajDerFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(40, 40, 40))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel91)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajDerFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel95)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel87)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel103)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel96)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel90)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel99)
                            .addComponent(jLabel98)
                            .addComponent(jLabel97)
                            .addComponent(jLabel88))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(altDerFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altDerFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altDerFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altDerFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(54, 54, 54)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel89)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel102)
                            .addComponent(jLabel101)
                            .addComponent(jLabel100)
                            .addComponent(jLabel104))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(altIzqFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altIzqFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altIzqFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altIzqFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel85)
                            .addComponent(jLabel87))
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bajDerFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel86)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel103))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(bajIzqrFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel94)
                                    .addComponent(bajIzqrFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel95)
                                    .addComponent(bajIzqrFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel96)
                                    .addComponent(bajIzqrFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel91)
                                    .addComponent(bajDerFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bajDerFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel92))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bajDerFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel93))
                                .addGap(4, 4, 4)))
                        .addGap(249, 249, 249))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel90)
                                .addGap(11, 11, 11)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                        .addComponent(jLabel88)
                                        .addGap(17, 17, 17))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                        .addComponent(altDerFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)))
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel97)
                                    .addComponent(altDerFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(altDerFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel98))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel99)
                                    .addComponent(altDerFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel89)
                                .addGap(11, 11, 11)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel104)
                                    .addComponent(altIzqFar1Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel100)
                                    .addComponent(altIzqFar2Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(altIzqFar3Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel101))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(altIzqFar4Com1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel102))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Caso de Combinacion 1", new javax.swing.ImageIcon(getClass().getResource("/imagenes/arrow_up_24.png")), jPanel12); // NOI18N

        jPanel11.setToolTipText("");
        jPanel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel64.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel64.setText("Baja Derecha:");

        jLabel65.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel65.setText("FAROLA 1:");

        jLabel66.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel66.setText("Baja Izquierda:");

        jLabel67.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel67.setText("FAROLA 1:");

        jLabel68.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel68.setText("Alta Izquierda:");

        jLabel69.setFont(new java.awt.Font("SansSerif", 1, 29)); // NOI18N
        jLabel69.setText("Alta Derecha:");

        jLabel70.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel70.setText("FAROLA 2:");

        jLabel71.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel71.setText("FAROLA 3:");

        jLabel72.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel72.setText("FAROLA 4:");

        jLabel73.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel73.setText("FAROLA 2:");

        jLabel74.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel74.setText("FAROLA 3:");

        jLabel75.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel75.setText("FAROLA 4:");

        jLabel76.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel76.setText("FAROLA 2:");

        jLabel77.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel77.setText("FAROLA 3:");

        jLabel78.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel78.setText("FAROLA 4:");

        jLabel79.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel79.setText("FAROLA 2:");

        jLabel80.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel80.setText("FAROLA 3:");

        jLabel81.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel81.setText("FAROLA 4:");

        jLabel82.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel82.setText("FAROLA 1:");

        jLabel83.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel83.setText("FAROLA 1:");

      

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator4)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel64)
                                .addGroup(jPanel11Layout.createSequentialGroup()
                                    .addComponent(jLabel65)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bajDerFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(34, 34, 34)))
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                    .addComponent(jLabel71)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(bajDerFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel11Layout.createSequentialGroup()
                                    .addComponent(jLabel72)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(bajDerFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(40, 40, 40))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajDerFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel74)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel66)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel82)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel75)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bajIzqrFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel69)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel78)
                            .addComponent(jLabel77)
                            .addComponent(jLabel76)
                            .addComponent(jLabel67))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(altDerFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altDerFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altDerFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altDerFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(54, 54, 54)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel68)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81)
                            .addComponent(jLabel80)
                            .addComponent(jLabel79)
                            .addComponent(jLabel83))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(altIzqFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altIzqFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altIzqFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(altIzqFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel64)
                            .addComponent(jLabel66))
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bajDerFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel65)))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel82))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(bajIzqrFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel73)
                                    .addComponent(bajIzqrFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel74)
                                    .addComponent(bajIzqrFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel75)
                                    .addComponent(bajIzqrFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel70)
                                    .addComponent(bajDerFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bajDerFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel71))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(bajDerFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel72))
                                .addGap(4, 4, 4)))
                        .addGap(249, 249, 249))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel69)
                                .addGap(11, 11, 11)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                        .addComponent(jLabel67)
                                        .addGap(17, 17, 17))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                        .addComponent(altDerFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)))
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel76)
                                    .addComponent(altDerFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(altDerFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel77))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel78)
                                    .addComponent(altDerFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel68)
                                .addGap(11, 11, 11)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel83)
                                    .addComponent(altIzqFar1Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel79)
                                    .addComponent(altIzqFar2Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(altIzqFar3Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel80))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(altIzqFar4Com2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel81))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Caso de Combinacion 2", new javax.swing.ImageIcon(getClass().getResource("/imagenes/arrow_up_24.png")), jPanel11); // NOI18N

        jLabel106.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel106.setText("Sum. Exploradora");

        guardar.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        guardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/save_24.png"))); // NOI18N
        guardar.setText("Calcular");
        guardar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                guardarMouseClicked(evt);
            }
        });
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });

        jLabel84.setFont(new java.awt.Font("SansSerif", 1, 22)); // NOI18N
        jLabel84.setText("Sum. Exploradora");

        sumExploradoraC2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumExploradoraC2chkHabilitar5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel84)
                .addGap(18, 18, 18)
                .addComponent(sumExploradoraC2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 959, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(228, 228, 228)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel106)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sumExploradoraC1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 959, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(jTabbedPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sumExploradoraC1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel106))
                .addGap(9, 9, 9)
                .addComponent(jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel84, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(sumExploradoraC2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sumExploradoraC1chkHabilitar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumExploradoraC1chkHabilitar5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumExploradoraC1chkHabilitar5ActionPerformed

    private void guardarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_guardarMouseClicked
    
    }//GEN-LAST:event_guardarMouseClicked

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        
        if (sumExploradoraC1.isSelected() == true) 
        {
            acumSumaIntensidad = acumSumaIntensidad + sumaExploradoras;
            simCase1[0]="Y";
        } else
        {
            simCase1[0]="N";
        }      
        
        if (this.luzBajaDerecha.length == 1) {
            if (bajDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[0];
                simCase1[1]="Y";
            }           
        }    
        
        if (this.luzBajaDerecha.length == 2) {
            if (bajDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[0];
                simCase1[1]="Y";
            }           
            if (bajDerFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[1];
                simCase1[2]="Y";
            }
        }
        
        
        if(this.luzBajaDerecha.length==3){
           if (bajDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[0];
                simCase1[1]="Y";
            }           
            if (bajDerFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[1];
                simCase1[2]="Y";
            }
            if (bajDerFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[2];
                simCase1[3]="Y";
            }
            
        }
        if(this.luzBajaDerecha.length==4){
           if (bajDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[0];
                simCase1[1]="Y";
            }           
            if (bajDerFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[1];
                simCase1[2]="Y";
            }
            if (bajDerFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[2];
                simCase1[3]="Y";
            }
            if (bajDerFar4Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaDerecha[3];
                simCase1[4]="Y";
            }
        }
        if(this.luzBajaIzquierda.length==1){          
            if (bajIzqrFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[0];
                simCase1[5]="Y";
            }           
        }        
        
        if(this.luzBajaIzquierda.length==2){          
            if (bajIzqrFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[0];
                simCase1[5]="Y";
            }           
            if (bajIzqrFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[1];
                simCase1[6]="Y";
            }
        }
        if(this.luzBajaIzquierda.length==3){           
            if (bajIzqrFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[0];
                simCase1[5]="Y";
            }           
            if (bajIzqrFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[1];
                simCase1[6]="Y";
            }
            if (bajIzqrFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[2];
                simCase1[7]="Y";
            }
        }
        if(this.luzBajaIzquierda.length==4){
            if (bajIzqrFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[0];
                simCase1[5]="Y";
            }
            if (bajIzqrFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[1];
                simCase1[6]="Y";
            }
            if (bajIzqrFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[2];
                simCase1[7]="Y";
            }
            if (bajIzqrFar4Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzBajaIzquierda[3];
                simCase1[8]="Y";
            }
        }
        if(this.luzAltaIzquierda.length==1){
           if (altIzqFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[0];
                simCase1[9]="Y";
            }            
        }
        
         if(this.luzAltaIzquierda.length==2){
           if (altIzqFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[0];
                simCase1[10]="Y";
            }
            if (altIzqFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[1];
                simCase1[11]="Y";
            }
        }
        if(this.luzAltaIzquierda.length==3){            
            if (altIzqFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[0];
                simCase1[9]="Y";
            }
            if (altIzqFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[1];
                simCase1[10]="Y";
            }
            if (altIzqFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[2];
                simCase1[11]="Y";
            }          
        }
        if(this.luzAltaIzquierda.length==4){
            if (altIzqFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[0];
                simCase1[9]="Y";
            }
            if (altIzqFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[1];
                simCase1[10]="Y";
            }
            if (altIzqFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[2];
                simCase1[11]="Y";
            }
            if (altIzqFar4Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[3];
                simCase1[12]="Y";
            }           
        }
        if(this.luzAltaDerecha.length==1){
           if (altDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[0];
                simCase1[13]="Y";
            }           
        }
        if(this.luzAltaDerecha.length==2){
           if (altDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[0];
                simCase1[13]="Y";
            }
            if (altDerFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[1];
                simCase1[14]="Y";
            }
        }
        if(this.luzAltaDerecha.length==3){
            if (altDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[0];
                simCase1[13]="Y";
            }
            if (altDerFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[1];
                simCase1[14]="Y";
            }
            if (altDerFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[2];
                simCase1[15]="Y";
            }
        }
        if(this.luzAltaDerecha.length==4){           
           if (altDerFar1Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[0];
                simCase1[13]="Y";
            }
            if (altDerFar2Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[1];
                simCase1[14]="Y";
            }
            if (altDerFar3Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[2];
                simCase1[15]="Y";
            }
            if (altDerFar4Com1.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaDerecha[3];
                simCase1[16]="Y";
            }           
        }
        
      //casousoCombinacion2  
        
        if (sumExploradoraC2.isSelected() == true) {
            acumSumaIntensidad2 = acumSumaIntensidad2 + sumaExploradoras;
            simCase2[0]="Y";
        }       
        
        if (this.luzBajaDerecha.length == 1) {
            if (bajDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[0];
                simCase2[1]="Y";
            }           
        }    
        
        if (this.luzBajaDerecha.length == 2) {
            if (bajDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[0];
                simCase2[1]="Y";
            }           
            if (bajDerFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[1];
                simCase2[2]="Y";
            }
        }
        
        
        if(this.luzBajaDerecha.length==3){
           if (bajDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[0];
                simCase2[1]="Y";
            }           
            if (bajDerFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[1];
                simCase2[2]="Y";
            }
            if (bajDerFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[2];
                simCase2[3]="Y";
            }
            
        }
        if(this.luzBajaDerecha.length==4){
           if (bajDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[0];
                simCase2[1]="Y";
            }           
            if (bajDerFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[1];
                simCase2[2]="Y";
            }
            if (bajDerFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[2];
                simCase2[3]="Y";
            }
            if (bajDerFar4Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaDerecha[3];
                simCase2[4]="Y";
            }
        }
        if(this.luzBajaIzquierda.length==1){          
            if (bajIzqrFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[0];
                simCase2[5]="Y";
            }           
        }        
        
        if(this.luzBajaIzquierda.length==2){          
            if (bajIzqrFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[0];
                simCase2[5]="Y";
            }           
            if (bajIzqrFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[1];
                simCase2[6]="Y";
            }
        }
        if(this.luzBajaIzquierda.length==3){           
            if (bajIzqrFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[0];
                simCase2[5]="Y";
            }           
            if (bajIzqrFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[1];
                simCase2[6]="Y";
            }
            if (bajIzqrFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[2];
                simCase2[7]="Y";
            }
        }
        if(this.luzBajaIzquierda.length==4){
            if (bajIzqrFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[0];
                simCase2[5]="Y";
            }
            if (bajIzqrFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[1];
                simCase2[6]="Y";
            }
            if (bajIzqrFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[2];
                simCase2[7]="Y";
            }
            if (bajIzqrFar4Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzBajaIzquierda[3];
                simCase2[8]="Y";
            }
        }
        if(this.luzAltaIzquierda.length==1){
           if (altIzqFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[0];
                simCase2[9]="Y";
            }            
        }
        
         if(this.luzAltaIzquierda.length==2){
           if (altIzqFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[0];
                simCase2[9]="Y";
            }
            if (altIzqFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[1];
                simCase2[10]="Y";
            }
        }
        if(this.luzAltaIzquierda.length==3){            
            if (altIzqFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[0];
                simCase2[9]="Y";
            }
            if (altIzqFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[1];
                simCase2[10]="Y";
            }
            if (altIzqFar3Com2.isSelected() == true) {
                acumSumaIntensidad = acumSumaIntensidad + luzAltaIzquierda[2];
                simCase2[11]="Y";
            }          
        }
        if(this.luzAltaIzquierda.length==4){
            if (altIzqFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[0];
                simCase2[9]="Y";
            }
            if (altIzqFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[1];
                simCase2[10]="Y";
            }
            if (altIzqFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[2];
                simCase2[11]="Y";
            }
            if (altIzqFar4Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaIzquierda[3];
                simCase2[12]="Y";
            }           
        }
        if(this.luzAltaDerecha.length==1){
           if (altDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[0];
                simCase2[13]="Y";
            }           
        }
        if(this.luzAltaDerecha.length==2){
           if (altDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[0];
                simCase2[13]="Y";
            }
            if (altDerFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[1];
                simCase2[14]="Y";
            }
        }
        if(this.luzAltaDerecha.length==3){
            if (altDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[0];
                simCase2[13]="Y";
            }
            if (altDerFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[1];
                simCase2[14]="Y";
            }
            if (altDerFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[2];
                simCase2[15]="Y";
            }
        }
        if(this.luzAltaDerecha.length==4){           
           if (altDerFar1Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[0];
                simCase2[13]="Y";
            }
            if (altDerFar2Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[1];
                simCase2[14]="Y";
            }
            if (altDerFar3Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[2];
                simCase2[15]="Y";
            }
            if (altDerFar4Com2.isSelected() == true) {
                acumSumaIntensidad2 = acumSumaIntensidad2 + luzAltaDerecha[3];
                simCase2[16]="Y";
            }           
        }
        acumReturnSum = ( acumSumaIntensidad2) > ( acumSumaIntensidad)  ? acumSumaIntensidad2: acumSumaIntensidad;
        
        if(acumSumaIntensidad2 > acumSumaIntensidad){
            System.out.print("Va a mostrar el caso simultaneto mayor");
            for (int c=0; c < simCase2.length;c++){
                lucesSimultaneas[c]= simCase2[c];
                System.out.print("C2" + lucesSimultaneas[c] +" - ");
            }
        }else{
            for (int c=0; c < simCase1.length ;c++){
                lucesSimultaneas[c]= simCase1[c];
                System.out.print("Caso1:" + lucesSimultaneas[c] + " ** ");
            }
        }
        
        setVisible(false);
        dispose();
    }//GEN-LAST:event_guardarActionPerformed

    private void bajDerFar1Com1chkHabilitar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajDerFar1Com1chkHabilitar2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajDerFar1Com1chkHabilitar2ActionPerformed

    private void bajDerFar3Com1chkHabilitar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajDerFar3Com1chkHabilitar3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajDerFar3Com1chkHabilitar3ActionPerformed

    private void bajDerFar2Com1chkHabilitar4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajDerFar2Com1chkHabilitar4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajDerFar2Com1chkHabilitar4ActionPerformed

    private void bajDerFar4Com1chkHabilitar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajDerFar4Com1chkHabilitar5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajDerFar4Com1chkHabilitar5ActionPerformed

    private void bajIzqrFar1Com1chkHabilitar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajIzqrFar1Com1chkHabilitar6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajIzqrFar1Com1chkHabilitar6ActionPerformed

    private void bajIzqrFar2Com1chkHabilitar7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajIzqrFar2Com1chkHabilitar7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajIzqrFar2Com1chkHabilitar7ActionPerformed

    private void bajIzqrFar4Com1chkHabilitar8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajIzqrFar4Com1chkHabilitar8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajIzqrFar4Com1chkHabilitar8ActionPerformed

    private void bajIzqrFar3Com1chkHabilitar9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bajIzqrFar3Com1chkHabilitar9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bajIzqrFar3Com1chkHabilitar9ActionPerformed

    private void altDerFar1Com1chkHabilitar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altDerFar1Com1chkHabilitar6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altDerFar1Com1chkHabilitar6ActionPerformed

    private void altDerFar2Com1chkHabilitar7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altDerFar2Com1chkHabilitar7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altDerFar2Com1chkHabilitar7ActionPerformed

    private void altDerFar3Com1chkHabilitar9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altDerFar3Com1chkHabilitar9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altDerFar3Com1chkHabilitar9ActionPerformed

    private void altDerFar4Com1chkHabilitar8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altDerFar4Com1chkHabilitar8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altDerFar4Com1chkHabilitar8ActionPerformed

    private void altIzqFar1Com1chkHabilitar6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altIzqFar1Com1chkHabilitar6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altIzqFar1Com1chkHabilitar6ActionPerformed

    private void altIzqFar2Com1chkHabilitar7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altIzqFar2Com1chkHabilitar7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altIzqFar2Com1chkHabilitar7ActionPerformed

    private void altIzqFar3Com1chkHabilitar9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altIzqFar3Com1chkHabilitar9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altIzqFar3Com1chkHabilitar9ActionPerformed

    private void altIzqFar4Com1chkHabilitar8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_altIzqFar4Com1chkHabilitar8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_altIzqFar4Com1chkHabilitar8ActionPerformed

    private void sumExploradoraC2chkHabilitar5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumExploradoraC2chkHabilitar5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sumExploradoraC2chkHabilitar5ActionPerformed

  
   
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /////////////////////////////////MÉTODOS SOBRE LA FUNCIONALIDAD //////////////////////////////
    


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
            java.util.logging.Logger.getLogger(FrmSeleccionSumLuces.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the dialog
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                FrmSeleccionSumLuces dialog = new FrmSeleccionSumLuces(new javax.swing.JFrame(), true);
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
    private javax.swing.JCheckBox altDerFar1Com1;
    private javax.swing.JCheckBox altDerFar1Com2;
    private javax.swing.JCheckBox altDerFar2Com1;
    private javax.swing.JCheckBox altDerFar2Com2;
    private javax.swing.JCheckBox altDerFar3Com1;
    private javax.swing.JCheckBox altDerFar3Com2;
    private javax.swing.JCheckBox altDerFar4Com1;
    private javax.swing.JCheckBox altDerFar4Com2;
    private javax.swing.JCheckBox altIzqFar1Com1;
    private javax.swing.JCheckBox altIzqFar1Com2;
    private javax.swing.JCheckBox altIzqFar2Com1;
    private javax.swing.JCheckBox altIzqFar2Com2;
    private javax.swing.JCheckBox altIzqFar3Com1;
    private javax.swing.JCheckBox altIzqFar3Com2;
    private javax.swing.JCheckBox altIzqFar4Com1;
    private javax.swing.JCheckBox altIzqFar4Com2;
    private javax.swing.JCheckBox bajDerFar1Com1;
    private javax.swing.JCheckBox bajDerFar1Com2;
    private javax.swing.JCheckBox bajDerFar2Com1;
    private javax.swing.JCheckBox bajDerFar2Com2;
    private javax.swing.JCheckBox bajDerFar3Com1;
    private javax.swing.JCheckBox bajDerFar3Com2;
    private javax.swing.JCheckBox bajDerFar4Com1;
    private javax.swing.JCheckBox bajDerFar4Com2;
    private javax.swing.JCheckBox bajIzqrFar1Com1;
    private javax.swing.JCheckBox bajIzqrFar1Com2;
    private javax.swing.JCheckBox bajIzqrFar2Com1;
    private javax.swing.JCheckBox bajIzqrFar2Com2;
    private javax.swing.JCheckBox bajIzqrFar3Com1;
    private javax.swing.JCheckBox bajIzqrFar3Com2;
    private javax.swing.JCheckBox bajIzqrFar4Com1;
    private javax.swing.JCheckBox bajIzqrFar4Com2;
    private javax.swing.JButton guardar;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JCheckBox sumExploradoraC1;
    private javax.swing.JCheckBox sumExploradoraC2;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = 0;
}
