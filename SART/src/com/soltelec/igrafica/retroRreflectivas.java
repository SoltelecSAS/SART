/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.igrafica;

import com.soltelec.dao.ControladorVerificar;
import com.soltelec.dao.PruebasJpaController;
import com.soltelec.dao.exceptions.IllegalOrphanException;
import com.soltelec.dao.exceptions.NonexistentEntityException;
import com.soltelec.model.HojaPruebas;
import com.soltelec.model.Pruebas;
import com.soltelec.modulopuc.persistencia.conexion.DBUtil;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.soltelec.util.Mensajes;
import com.soltelec.dao.HojaPruebasJpaController;
import com.soltelec.dao.PruebasJpaController;
import static com.soltelec.igrafica.Frm_ASenalizacionv2.em;
import com.soltelec.model.Pruebas;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Diego Garzón
 */
public class retroRreflectivas extends javax.swing.JDialog {

    private Connection conexion;
    public int idPrueba;
    //private Pruebas prueba;
    private int hojaPruebasActual;
    private HojaPruebasJpaController hojaPruebasController;
    private HojaPruebas hojaPruebas;
    private PruebasJpaController p;
    //private Pruebas prueba;

    public retroRreflectivas(java.awt.Frame parent, boolean modal, int idPrueba, int hojaPruebasActual) {
        super(parent, modal);
        this.idPrueba = idPrueba;
        this.hojaPruebasActual = hojaPruebasActual;

        initComponents();
    }

    private retroRreflectivas(JFrame jFrame, boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tituloCintas = new java.awt.Label();
        btnGuardarCintas = new java.awt.Button();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("CintasRetro"); // NOI18N

        tituloCintas.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        tituloCintas.setForeground(new java.awt.Color(255, 0, 0));
        tituloCintas.setText("PROCESO DE REVISIÓN CINTAS REFLECTIVAS");

        btnGuardarCintas.setBackground(new java.awt.Color(0, 153, 0));
        btnGuardarCintas.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnGuardarCintas.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarCintas.setLabel("Guardar Defectos");
        btnGuardarCintas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarCintasActionPerformed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox1.setText("Inexistencia de la demarcación con Cintas Retrorreflectivas en los vehículos obligados a llevarlas");
        jCheckBox1.setToolTipText("");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox2.setText("Demarcación con Cintas Retrorreflectivas No Reglamentarias");

        jCheckBox3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox3.setText("Utilizar cintas u otros elementos retroreflectivos de color blanco en la parte trasera, ni de color rojo en la parte delantera.");

        jCheckBox4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox4.setText("Las cintas retrorreflectivas no podrán estar cubiertas por ningún elemento o en condiciones que impidan su visibilidad.");

        jCheckBox5.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox5.setText("Color No Reglamentario");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox6.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox6.setText("No cumplir con las especificaciones del embebido Embebido no presente");
        jCheckBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox6ActionPerformed(evt);
            }
        });

        jCheckBox7.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox7.setText("No cumplir con las especificaciones del embebido no visible o borroso");
        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });

        jCheckBox8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox8.setText("No cumplir con las especificaciones del embebido con información errónea");

        jCheckBox9.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox9.setText("No cumplir con las especificaciones del embebido con información faltante");

        jCheckBox10.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox10.setText("No cumplir con las especificaciones del embebido alterado");

        jCheckBox11.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox11.setText("Placa no visible o borrosa");
        jCheckBox11.setActionCommand("JCheckbox11");
        jCheckBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox11ActionPerformed(evt);
            }
        });

        jCheckBox12.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jCheckBox12.setText("Otros");
        jCheckBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox11)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox4)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jCheckBox10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jCheckBox5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jCheckBox6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jCheckBox7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jCheckBox8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                                        .addComponent(jCheckBox9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jCheckBox12))
                                .addGap(67, 67, 67)
                                .addComponent(btnGuardarCintas, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                                .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(238, 238, 238)
                        .addComponent(tituloCintas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tituloCintas, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addGap(13, 13, 13)
                .addComponent(jCheckBox2)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox5)
                        .addGap(12, 12, 12)
                        .addComponent(jCheckBox6)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox7)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox8)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox9)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox12)
                        .addContainerGap(54, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardarCintas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarCintasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCintasActionPerformed
        Pruebas prueba = new Pruebas();
        prueba.setIdPruebas(idPrueba);
        hojaPruebasController = new HojaPruebasJpaController();
        hojaPruebas = hojaPruebasController.findHojaPruebas(hojaPruebasActual);
        System.out.println("\n" + hojaPruebas + "\n");
        ControladorVerificar controladorVerificar = new ControladorVerificar();
        String txtObs = controladorVerificar.exisObsCrud(this.idPrueba, em);
        String[] lstObs = txtObs.split("Cintas Reflectivas:");
        /*if(lstObs.length>1){ 
            txtComentario.setText(lstObs[1]); 
        }
        txtProfLabrado=lstObs[0];*/
        cintas();
        this.dispose();
        JOptionPane.showMessageDialog(null, "POR FAVOR GUARDE EL DEFECTO DE CINTAS REFLECTIVAS");
    }//GEN-LAST:event_btnGuardarCintasActionPerformed

    public void cintas() {
        System.out.println("---------------------------------------------------");
        System.out.println("----------------------cintas------------------------");
        System.out.println("---------------------------------------------------");

        PruebasJpaController controller = new PruebasJpaController();
        Pruebas pruebaedit = null;
        for (Pruebas prueba : hojaPruebas.getPruebasCollection()) {
            System.out.println("\n" + idPrueba + " Este es el Ide de Prueba" + "\n");
            System.out.println("\n" + prueba.getIdPruebas() + "\n");

            if (prueba.getTipoPrueba().getTesttype() == 1) {
                String obs = prueba.getObservaciones();
                System.out.println("Ciclo " + obs);
                if (obs != null) {
                    prueba.setObservaciones(obs.concat("Cintas Reflectivas: "));
                    System.out.println("------------------PRUEBA ---------------");
                }

                if (jCheckBox1.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox1.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox1.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }

                if (jCheckBox2.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox2.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox2.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox3.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox3.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox3.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox4.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox4.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox4.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox5.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox5.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox5.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox6.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox6.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox6.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox7.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox7.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox7.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox8.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox8.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox8.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox9.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox9.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox9.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox10.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox10.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox10.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                if (jCheckBox11.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox11.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("obs".concat(jCheckBox11.getText()).concat("\n"));
                    }
                    pruebaedit = prueba;
                }
                
                if (jCheckBox12.isSelected()) {
                    if (prueba.getObservaciones() != null) {
                        prueba.setObservaciones(prueba.getObservaciones().concat(jCheckBox12.getText()).concat("\n"));
                    } else {
                        prueba.setObservaciones("".concat(jCheckBox12.getText()).concat(": "));
                    }
                    pruebaedit = prueba;
                }

            }
        }//Cierre For

        try {
            controller.edit(pruebaedit);
            com.soltelec.modulopuc.utilidades.Mensajes.mensajeCorrecto("Cintas Reflectivas se guardo con Exito ..¡");
        } catch (IllegalOrphanException ex) {
            
                com.soltelec.modulopuc.utilidades.Mensajes.mensajeError("No se logro guardar las Cintas Reflectivas ");
                com.soltelec.modulopuc.utilidades.Mensajes.mostrarExcepcion(ex);
            
        } catch (NonexistentEntityException ex) {
            
                com.soltelec.modulopuc.utilidades.Mensajes.mensajeError("No se logro guardar las Cintas Reflectivas ");
                com.soltelec.modulopuc.utilidades.Mensajes.mostrarExcepcion(ex);
            
        } catch (Exception ex) {
            
                com.soltelec.modulopuc.utilidades.Mensajes.mensajeError("No se logro guardar las Cintas Reflectivas ");
                com.soltelec.modulopuc.utilidades.Mensajes.mostrarExcepcion(ex);
            
        }
    }

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed

    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed

    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void jCheckBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox6ActionPerformed

    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed

    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox11ActionPerformed

    private void jCheckBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox12ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jCheckBox12ActionPerformed

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
            java.util.logging.Logger.getLogger(retroRreflectivas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(retroRreflectivas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(retroRreflectivas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(retroRreflectivas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                retroRreflectivas dialog = new retroRreflectivas(new javax.swing.JFrame(), true);
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
    private java.awt.Button btnGuardarCintas;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private java.awt.Label tituloCintas;
    // End of variables declaration//GEN-END:variables
}
