/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.chino;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author User
 */
public class JDialogLuxometroChino  extends JDialog{
    
    PanelLuxometroChino panelLuxometro ;
    
    public JDialogLuxometroChino(Frame owner, boolean modal,long idPrueba,long idUsuario){
        super(owner, modal);
        panelLuxometro = new PanelLuxometroChino(idPrueba, idUsuario);
        this.getContentPane().add(panelLuxometro);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(this.getToolkit().getScreenSize());
     }
    
}
