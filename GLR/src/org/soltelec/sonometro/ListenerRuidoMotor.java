/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.pruebasgases.DialogoRPMTemp;
import org.soltelec.pruebasgases.PanelPruebaGases;

/**
 *
 * @author Usuario
 */
public class ListenerRuidoMotor implements ActionListener {

    private PanelPruebaGases panel;
    private DialogoRPMTemp dialogRPMTemp;

    public ListenerRuidoMotor(PanelPruebaGases panel, DialogoRPMTemp dialogRPMTemp) {
        this.panel = panel;
        this.dialogRPMTemp = dialogRPMTemp;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        //Mostrar un dialogo para seleccionar el tipo de las revoluciones


    }//end of method actionPerformed



}//end of class ListenerRuidoMotor
