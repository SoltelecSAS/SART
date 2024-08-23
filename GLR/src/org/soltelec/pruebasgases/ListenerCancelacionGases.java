/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import gnu.io.SerialPort;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXLoginPane.Status;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;

/**
 * Clase para hacer la cancelacion de la prueba de motos.
 *
 * @author User
 */
public class ListenerCancelacionGases implements ActionListener {

    private final Future futureParaCancelar;
    private final long idPrueba, idUsuario;

    private final BancoGasolina banco;
    private final MedidorRevTemp medRevTemp;
    private PanelPruebaGases panel;

    public ListenerCancelacionGases(Future futureParaCancelar, long idPrueba, 
                                    BancoGasolina banco, MedidorRevTemp medRevTemp, 
                                    PanelPruebaGases panel, long idUsuario) {
        this.futureParaCancelar = futureParaCancelar;
        this.idPrueba = idPrueba;
        this.banco = banco;
        this.medRevTemp = medRevTemp;
        this.panel = panel;
        this.idUsuario = idUsuario;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {                          
             panel.getFuncion().setText("abortado");
             System.out.println(panel.getFuncion().getText());
              panel.cerrar();
            System.out.println("Entro en el metodo de Cancelamiento ");
            if (banco != null) {
                 banco.encenderBombaMuestras(false);
            }
            //si es otro dispositivo de medicion entonces cerrar el puerto
           /* if (!(medRevTemp instanceof BancoSensors) && medRevTemp != null) {
                 System.out.println("L1");
                SerialPort puertoSerial = medRevTemp.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close(); //para no cerrarlo dos veces                   
                } System.out.println("L2");
            }  */ 
            if(futureParaCancelar!=null){
                while (futureParaCancelar.isCancelled() == false) {
                   futureParaCancelar.cancel(true);                
               }
            }
        } catch (Exception exc) {
            System.out.println("Excepcion cerrando puertos seriales debido a " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
