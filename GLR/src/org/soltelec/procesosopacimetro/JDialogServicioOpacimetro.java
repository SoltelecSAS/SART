/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogServicioOpacimetro extends JDialog{
    private final PanelServicioOpacimetro panelServicioOpacimetro;

    public JDialogServicioOpacimetro(Frame owner, boolean modal) {
     super(owner, modal);
     panelServicioOpacimetro = new PanelServicioOpacimetro();
     this.getContentPane().add(panelServicioOpacimetro);
     this.setSize(this.getToolkit().getScreenSize());
     this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }        
}
