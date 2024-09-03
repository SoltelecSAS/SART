/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.pruebasgases.ListenerCancelacionGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;

/**
 *
 * @author GerenciaDesarrollo
 */
public class ListenerCancelacionSonometro implements ActionListener {

    private final Future futureParaCancelar;
    private final long idPrueba,idUsuario;
    private final PanelPruebaSonometro panel;
    private final Sonometro sonometro;

    public ListenerCancelacionSonometro(Future futureParaCancelar, long idPrueba, PanelPruebaSonometro panel, Sonometro sonometro,long idUsuario) {
        this.futureParaCancelar = futureParaCancelar;
        this.idPrueba = idPrueba;
        this.panel = panel;
        this.sonometro = sonometro;
         this.idUsuario = idUsuario;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            panel.getProgressBar().setVisible(false);
            panel.getPanelFiguras().setVisible(false);
            panel.getButtonFinalizar().setVisible(false);
            panel.getPanelMensaje().setVisible(true);
            panel.getMensaje().setText("Esperando AUTORIZACION del Ingeniero Pista");
            Font eve = new Font(Font.SANS_SERIF, Font.BOLD, 40);
            panel.getPanelMensaje().setFont(eve);
            panel.getPanelMensaje().setText("Prueba Actual de Gases ha sido ABORTADA ");
            Thread.sleep(500);
            ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
            JLabel lblImg = (JLabel) panel.getPanelMensaje().getComponent(0);
            lblImg.setIcon(image);
            Thread.sleep(500);
            //Status status = UtilLogin.loginAdminDBCDA();
            JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(true));
            pane.setLocale(new Locale("ES"));
            pane.setBannerText("ADMINISTRADOR");
            pane.setErrorMessage("Usuario No Autorizado para Abortar Prueba de Gases");
            //loginDialog.setVisible(true);
            JXLoginPane.Status status = JXLoginPane.showLoginDialog(null, pane);
            if (status == JXLoginPane.Status.SUCCEEDED) {
                String nombreUsuario = pane.getUserName();//El nombre de usuario que se acaba de autenticar
                //traer el id de ese usuario
                System.out.println("Usuario :" + nombreUsuario);
                if (panel != null) {
                    //panel.cerrar();
                    //panel.cerrarDialogoMotosGases();
                }
                //preguntar el motivo de la cancelacion
                boolean pressMotivo = false;
                Object objSeleccion = null;
                while (pressMotivo == false) {
                    objSeleccion = JOptionPane.showInputDialog(
                            null,
                            "Motivo del Aborto Prueba Gases",
                            "Abortar Prueba desde listener:",
                            JOptionPane.QUESTION_MESSAGE,
                            null, // null para icono defecto
                            new Object[]{
                                "Fallas del equipo de medicion",
                                "Falla subita de fluido electrico del equipo de medicion",
                                "Bloqueo forzado del equipo de medicion",
                                "Ejecucion Incorrecta de la Prueba"},
                            "Causa de la cancelacion");
                    if (objSeleccion == null) {
                        Mensajes.messageWarningTime(">>>  Disculpe, NO me has especificado el Motivo de Aborto, Por favor seleccione  uno<<<", 5);
                    } else {
                        pressMotivo = true;
                    }
                }
                String strSeleccion = (String) objSeleccion;
                if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente al Aborto?")) {
                    org.soltelec.pruebasgases.FrmComentario frm = new org.soltelec.pruebasgases.FrmComentario(SwingUtilities.getWindowAncestor(panel), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, strSeleccion, idUsuario,"aborto",0);
                    frm.setVisible(true);
                    frm.setModal(true);
                    JOptionPane.showMessageDialog(null, "Aborto de la  prueba por: " + strSeleccion);
                }
                panel.cerrar();
            } else {
                System.out.println("El usuario no se a autenticado como administrador");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ListenerCancelacionGases.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of method 

    
}//end of class
