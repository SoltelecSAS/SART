/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.pruebasgases.ListenerCancelacionGases;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;

/**
 *
 * @author GerenciaDesarrollo
 */
public class ListenerCancelacionDiesel implements ActionListener {

    private Future futureParaCancelar;
    private long idPrueba, idUsuario;
    private Opacimetro opacimetro;
    private MedidorRevTemp medRevTemp;
    private PanelPruebaGases panel;

    public ListenerCancelacionDiesel(Future futureParaCancelar, long idPrueba, Opacimetro opacimetro, MedidorRevTemp medRevTemp, PanelPruebaGases panel, long idUsuario) {
        this.futureParaCancelar = futureParaCancelar;
        this.idPrueba = idPrueba;
        this.opacimetro = opacimetro;
        this.medRevTemp = medRevTemp;
        this.panel = panel;
        this.idUsuario = idUsuario;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        try {
            futureParaCancelar.cancel(true);
            while (futureParaCancelar.isCancelled() == false) {
                futureParaCancelar.cancel(true);
                Thread.sleep(50);
            }
            Thread.sleep(500);
            JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(true));
            pane.setLocale(new Locale("ES"));
            pane.setBannerText("ADMINISTRADOR");
            pane.setErrorMessage("Usuario o contrasenia incorrecta,\n o el usuario no es administrador");
            JXLoginPane.Status status = JXLoginPane.showLoginDialog(null, pane);
            if (status == JXLoginPane.Status.SUCCEEDED) {
                Connection cn = null;
                String nombreUsuario = pane.getUserName();//El nombre de usuario que se acaba de autenticar
                //traer el id de ese usuario
                System.out.println("Usuario :" + nombreUsuario);
                //preguntar el motivo de la cancelacion
                Object objSeleccion = JOptionPane.showInputDialog(
                        null,
                        "Motivo de la cancelacion",
                        "Cancelacion",
                        JOptionPane.QUESTION_MESSAGE,
                        null, // null para icono defecto
                        new Object[]{
                            "Fallas del equipo de medicion",
                            "Falla subita de fluido electrico del equipo de medicion",
                            "Bloqueo forzado del equipo de medicion",
                            "Ejecucion incorrecta de la prueba",},
                        "Causales de Aborto");
                String strSeleccion = (String) objSeleccion;
                if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente al Aborto?")) {
                    org.soltelec.pruebasgases.FrmComentario frm = new org.soltelec.pruebasgases.FrmComentario(SwingUtilities.getWindowAncestor(panel), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, strSeleccion, idUsuario, "aborto",0);
                    frm.setVisible(true);
                    frm.setModal(true);
                }
                if (opacimetro != null) {
                    opacimetro.getPort().close();
                }
                if (medRevTemp != null) {
                    medRevTemp.getPuertoSerial().close();
                }
                if (panel != null) {
                    panel.cerrar();
                }
                try {
                    if (cn != null) {
                        cn.close();
                    }
                } catch (SQLException ignorar) {
                }
            } else {
                System.out.println("El usuario no se a autenticado como administrador");
            }
        } catch (InterruptedException ex) {  }
    }
}
