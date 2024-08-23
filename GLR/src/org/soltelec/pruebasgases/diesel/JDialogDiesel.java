/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import java.awt.Frame;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.Mensajes;

/**
 *
 * @author Gerencia Desarrollo Soluciones Tecnologicas
 */
public class JDialogDiesel extends JDialog {

    private final PanelPruebaGases panelPruebaGases;
    private ProcesoPruebaOpacimetro hilo;//no usado desde la primera version

    public JDialogDiesel(Frame owner, boolean modal, long idPrueba, long idUsuario, long idHojaPrueba) throws SQLException, ClassNotFoundException, IOException {
        super(owner, modal);
        panelPruebaGases = new PanelPruebaGases();
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setTitle("SART 1.7.3 PRUEBA DE DIESEL");
        if (idPrueba != 0 && idUsuario != 0) {
            iniciarHilo(idPrueba, idUsuario, idHojaPrueba);
            this.setVisible(true);
        }
    }

    public PanelPruebaGases getPanelPruebaGases() {
        return panelPruebaGases;
    }

    private void iniciarHilo(long idPrueba, long idUsuario, long idHojaPrueba) {
        try {           
            WorkerInicioDiesel wid = new WorkerInicioDiesel(panelPruebaGases, idHojaPrueba, idPrueba, idUsuario);
            wid.execute();
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "Error iniciando la pureba de gases Diesel " + exc.getMessage());
            Logger.getRootLogger().error(exc);
        }
    }
}//end of method JDialogDiesel
