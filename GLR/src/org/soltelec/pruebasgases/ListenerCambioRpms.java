/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import com.soltelec.loginadministrador.ConsultasLogin;
import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.pruebasgases.motocicletas.JDialogMotosGases;
import org.soltelec.pruebasgases.motocicletas.WorkerCiclosMoto;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;

/**
 * Se invoca al presionar el boton cambio de rpms.
 *
 * @author gerenciaDesarrollo
 */
public class ListenerCambioRpms implements ActionListener {

    private Future tareaParaCancelar;
    private BancoGasolina banco;
    private final MedidorRevTemp medRevTemp;
    private PanelPruebaGases panel;
    JDialogMotosGases frmC;
    private PanelPruebaGases panelCancelacion;
    private long idPrueba, idUsuario;

    public ListenerCambioRpms(Future tarea, MedidorRevTemp medRevTemp, BancoGasolina banco, PanelPruebaGases panel, long idUsuario) {
        tareaParaCancelar = tarea;
        this.medRevTemp = medRevTemp;
        this.banco = banco;
        this.panel = panel;
        this.idUsuario = idUsuario;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
           System.out.println("entreActionperformedRPM");
            panel.getFuncion().setText("rechazo");
             panel.cerrar();
            if (banco != null) {
                banco.encenderBombaMuestras(false);
            }
            //si es otro dispositivo de medicion entonces cerrar el puerto
          /*  if (!(medRevTemp instanceof BancoSensors) && medRevTemp != null) {
                System.out.println("L1");
                SerialPort puertoSerial = medRevTemp.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close(); //para no cerrarlo dos veces                    
                }
                System.out.println("L2");
            }  */       
            while (tareaParaCancelar.isCancelled() == false) {
                tareaParaCancelar.cancel(true);                             
            }               
            System.out.println("Termine Logica de cancelacion y Cierre de Puertos");
        } catch (Exception exc) {
            System.out.println("Swallow exception cerrando puerto");
        }
    }

}
