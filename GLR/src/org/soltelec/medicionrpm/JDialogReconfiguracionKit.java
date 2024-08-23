/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import gnu.io.SerialPort;
import javax.swing.JDialog;

/**
 *
 * @author Usuario
 */
public class JDialogReconfiguracionKit extends JDialog{
    private ConsumidorCapelec consumidor;//para que muestre la clase
    private SerialPort serialPortKit;//para no necesidad conexión
    private PanelServicioCapelec panelServicio;

    public JDialogReconfiguracionKit(ConsumidorCapelec c,SerialPort sp){
        super();
        panelServicio = new PanelServicioCapelec(c, sp);
        this.getContentPane().add(panelServicio);
        this.setSize(this.getToolkit().getScreenSize());
        this.setModal(true);
   }
}
