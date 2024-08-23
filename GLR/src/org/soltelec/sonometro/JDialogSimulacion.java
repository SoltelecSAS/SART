/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

//import org.soltelec.medicionrpm.*;
import java.awt.Frame;
import javax.swing.JDialog;
//import org.soltelec.pruebasgases.JDialogSonometro;

/**
 *
 * @author Usuario
 */
public class JDialogSimulacion extends JDialog{

    private PanelInicioSim panelInicioSim;

    public JDialogSimulacion(Frame owner, boolean modal) {
        super(owner,modal);
        panelInicioSim = new PanelInicioSim();
        this.getContentPane().add(panelInicioSim);
        this.setSize(800,400);
        this.setLocationRelativeTo(owner);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public PanelInicioSim getPanelInicioSim() {
        return panelInicioSim;
    }

    public void setPanelInicioSim(PanelInicioSim panelInicioSim) {
        this.panelInicioSim = panelInicioSim;
    }

    public static void main(String args[]){
       JDialogSonometro dlg = new JDialogSonometro(null, true);
       HiloPruebaSonometro hilo = new HiloPruebaSonometro(dlg.getPanelPruebaGases());
       Thread t = new Thread(hilo);
       t.start();
    }
}//end of class JDialogServCapelec
