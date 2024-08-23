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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Future;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.RegistrarMedidas;

/**
 *
 * @author User
 */
public class ListenerFallaSubitaDiesel implements ActionListener {

    private final Future futureParaCancelar;
    private final long idPrueba;

    public ListenerFallaSubitaDiesel(Future futureParaCancelar, long idPrueba, PanelPruebaGases panel, Opacimetro opacimetro, MedidorRevTemp medidorRevTemp) {
        this.futureParaCancelar = futureParaCancelar;
        this.idPrueba = idPrueba;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(false));
        pane.setLocale(new Locale("ES"));
        pane.setBannerText("USUARIO");
        pane.setErrorMessage("Usuario o contrasenia incorrecta");

        JXLoginPane.Status status = JXLoginPane.showLoginDialog(null, pane);

        if (status == JXLoginPane.Status.SUCCEEDED) {
            Connection cn = null;
            try {

                String nombreUsuario = pane.getUserName();//El nombre de usuario que se acaba de autenticar
                //traer el id de ese usuario
                System.out.println("Usuario :" + nombreUsuario);

                //futureParaCancelar.cancel(true);
                //preguntar el motivo de la cancelacion
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                 System.out.println("event1 :" );
                cn = regMedidas.getConnection();
                 System.out.println("event2 :" );
                ConsultasLogin consultasLogin = new ConsultasLogin();
                 System.out.println("event3 :" );
                long idUsuario = consultasLogin.obtenerIdUsuario(nombreUsuario);
                 System.out.println("event5 :" );                
                 System.out.println("event7 :" );              
                 System.out.println("event8 :" );
                 regMedidas.setMensaDefe("Falla Subita del Motor y /o sus Accesosrios");
                regMedidas.registrarDefectosDiesel( idPrueba, idUsuario);
                 System.out.println("event9 :" );
                cn.close();
                 System.out.println("event10 :" );
                 if(futureParaCancelar!=null){
                while (futureParaCancelar.isCancelled() == false) {
                   futureParaCancelar.cancel(true);                
               }
            }
              
 System.out.println("event11 :" );
            } catch (ClassNotFoundException | SQLException exc) {

                JOptionPane.showMessageDialog(null, "Error registrando la cancelacion de la prueba en la base de datos");
                Logger.getRootLogger().error(exc);
            } finally {//al cancelar la prueba se deben liberar todos los recursos.

                try {
                    if (cn != null) {
                        cn.close();
                    }
                } catch (SQLException ignorar) {
                }

            }

        } else {

            System.out.println("El usuario no se a autenticado como administrador");

        }

    }

}
