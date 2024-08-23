/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.gemini;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogGemini extends JDialog{

    PanelLuxoGemini panelLuxometro; 
    public static String tramaAuditoria;
    public JDialogGemini(Frame owner, boolean modal, long idPruebas, long idUsuario, long idHojaPrueba,String placa){
        super(owner,modal);
        panelLuxometro = new PanelLuxoGemini(idHojaPrueba, placa, idPruebas, idUsuario);
        this.getContentPane().add(panelLuxometro);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(this.getToolkit().getScreenSize());
        this.setVisible(true);       
    }

    public PanelLuxoGemini getPanelLuxometro() {
        return panelLuxometro;
    }

}
