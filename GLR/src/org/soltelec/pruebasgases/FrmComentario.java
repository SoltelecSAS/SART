/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import org.soltelec.luxometro.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.soltelec.modulopuc.utilidades.Mensajes;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import java.awt.Component;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import org.soltelec.util.ConsultarDatosVehiculo;
import com.soltelec.modulopuc.configuracion.modelo.Conexion;
import java.sql.ResultSet;
import org.soltelec.pruebasgases.motocicletas.CallableInicioMotos;

/**
 *
 * @author GerenciaDesarrollo
 */
public class FrmComentario extends javax.swing.JDialog {

    java.awt.Frame frame;
    //---Conexión por JDBC
    private Connection conexion;
    private String usuario;
    private String password;
    private String direccionIP;
    private Long idPrueba;
    private long idUsuario;
    private String motivoAborto;
    private String funcion;
    private Timer timer = null;
    private Integer codDefecto;

    //DIRECCIÓN IP DEL SERVIDOR = 186.112.176.34
    //---
    FrmComentario(Window windowAncestor, Long idPrueba1, ModalityType DEFAULT_MODALITY_TYPE1, String a_) {
    }

    ////////////////////////////////////////////PARA NO PERDER LA REFERENCIA DEL OBJETO/////////
    public FrmComentario(Window owner, Long idPrueba, ModalityType modalityType, String motivoAborto, long idUsuario, String funcion, Integer codDefecto) {
        super(owner, modalityType);
        initComponents();
        Dimension d = new java.awt.Dimension(688, 172);
        setSize(d);
        System.out.println("c");
        setTitle("Descripcion General..¡");
        this.requestFocusInWindow();
        System.out.println("c1");
        setResizable(false);
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.motivoAborto = motivoAborto;
        System.out.println("El Motivo de cancelacion ".concat(this.motivoAborto));
        this.funcion = funcion;
        System.out.println("c3");
        this.txtComent.requestFocus();
        this.codDefecto = codDefecto;
        timer = new Timer(575, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Robot ro = new Robot();
                    ro.keyPress(KeyEvent.VK_TAB);
                    System.out.printf("Robot ya hizo tab");
                    timer.stop();
                } catch (AWTException ex) {
                }
            }
        });
        this.jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/save_24.png")));
        timer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtObservacion = new javax.swing.JScrollPane();
        txtComent = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Observaciones Generales");

        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtObservacion.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        txtComent.setColumns(20);
        txtComent.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtComent.setLineWrap(true);
        txtComent.setRows(5);
        txtComent.setTabSize(0);
        txtComent.setWrapStyleWord(true);
        txtComent.setPreferredSize(new java.awt.Dimension(200, 89));
        txtObservacion.setViewportView(txtComent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtObservacion)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtObservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        setBounds(0, 0, 537, 198);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = (Connection) DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContraseña());
        } catch (ClassNotFoundException | SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        }
        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
        } catch (Exception e) {
            serialEquipo = "Serial no Encontrado ";
        }
        System.out.println("entro al metodo de persistencia de datos");
        try {
            conexion.setAutoCommit(false);
            if (funcion.equalsIgnoreCase("aborto")) {
                String statement = "UPDATE pruebas SET Finalizada = 'N',Aprobada='N',Abortada='Y',Comentario_aborto=?,observaciones=? , usuario_for = ?,serialEquipo = ?, Fecha_final = ? WHERE pruebas.Id_Pruebas = ?;";
                java.sql.PreparedStatement instruccion = conexion.prepareStatement(statement);
                if (motivoAborto.equalsIgnoreCase("DILUCION DE MUESTRA")) {

                } else {
                    instruccion.setString(1, motivoAborto);
                }
                instruccion.setString(2, motivoAborto.concat(" obs:").concat(txtComent.getText()));
                instruccion.setLong(3, idUsuario);
                instruccion.setString(4, serialEquipo);
                Date d = new Date();
                Calendar c = new GregorianCalendar();
                c.setTime(d);
                instruccion.setTimestamp(5, new java.sql.Timestamp(c.getTimeInMillis()));
                instruccion.setLong(6, idPrueba);
                instruccion.executeUpdate();
                instruccion.clearParameters();
                JOptionPane.showMessageDialog(this, "Se Ha Almacenado el Comentario Aborto de una manera Exitosa ..¡");
            } else {
                System.out.println("antesala a senteencia sql");
                String statement = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',Comentario_aborto=?,observaciones=?,usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
                java.sql.PreparedStatement instruccion = conexion.prepareStatement(statement);

                if (CallableInicioMotos.lecturaCondicionesAnormales.length() > 3) {
                    if (!motivoAborto.equalsIgnoreCase("Condiciones Anormales")) {
                        instruccion.setString(2, motivoAborto.concat("; Condiciones Anormales Encotradas .-").concat(CallableInicioMotos.lecturaCondicionesAnormales).concat(" obs:").concat(txtComent.getText()));
                    } else {
                        instruccion.setString(2, motivoAborto.concat(".-").concat(CallableInicioMotos.lecturaCondicionesAnormales).concat(" obs:").concat(txtComent.getText()));
                    }
                    instruccion.setString(1, "Condiciones Anormales");
                    System.out.println("Imprimo Preparent Stantement :" + CallableInicioMotos.lecturaCondicionesAnormales);
                } else {
                    instruccion.setString(1, motivoAborto);
                    instruccion.setString(2, motivoAborto.concat(" obs:").concat(txtComent.getText()));
                }
                instruccion.setLong(3, idUsuario);
                Date d = new Date();
                Calendar c = new GregorianCalendar();
                c.setTime(d);
                instruccion.setString(4, serialEquipo);
                instruccion.setLong(5, idPrueba);
                instruccion.executeUpdate();

                // Validar si el defecto ya existe para esta prueba
                String consultaExistencia = "SELECT COUNT(*) FROM defxprueba WHERE id_defecto = ? AND id_prueba = ?";
                java.sql.PreparedStatement consultaExistenciaInstruccion = conexion.prepareStatement(consultaExistencia);
                consultaExistenciaInstruccion.setInt(1, codDefecto);
                consultaExistenciaInstruccion.setLong(2, idPrueba);

                ResultSet resultadoExistencia = consultaExistenciaInstruccion.executeQuery();
                resultadoExistencia.next();

                int cantidadFilas = resultadoExistencia.getInt(1);

                if (cantidadFilas > 0) {
                    // El defecto ya existe para esta prueba, realiza aquí la lógica necesaria
                    System.out.println("El defecto ya existe para esta prueba.");
                    // Puedes lanzar una excepción, mostrar un mensaje, etc.
                } else {
                    instruccion.clearParameters();
                    statement = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES(?,?)";
                    instruccion = conexion.prepareStatement(statement);
                    conexion.setAutoCommit(false);
                    instruccion.setInt(1, codDefecto);
                    instruccion.setLong(2, idPrueba);
                    instruccion.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Se Ha Almacenado el Comentario Rechazo de una manera Exitosa ..¡");
                }
                consultaExistenciaInstruccion.close();
                resultadoExistencia.close();
            }
            System.out.println("antesala al commit sql");
            conexion.commit();
            System.out.println("implemente commit  sql");
            conexion.setAutoCommit(true);
            conexion.close();
            System.out.println("close commit  sql");
            //System.out.println("Datos enviados");
        } catch (SQLException ex) {
            System.out.println("hubo sql excep debido a " + ex.getMessage());

        } finally {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.setAutoCommit(true);
                    conexion.close();
                    System.out.println("close commit  sql");
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        this.dispose();
        System.out.println("cierro Ventana");
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(FrmComentario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                try {
                    Robot ro = new Robot();
                    ro.keyPress(KeyEvent.VK_TAB);
                } catch (AWTException ex) {
                    Logger.getLogger(FrmComentario.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea txtComent;
    private javax.swing.JScrollPane txtObservacion;
    // End of variables declaration//GEN-END:variables
    private int returnStatus = 0;
}
