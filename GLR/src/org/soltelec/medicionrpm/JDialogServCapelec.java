/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogServCapelec extends JDialog{

    private PanelServicioCapelec panelServicioCapelec;

    public JDialogServCapelec(Frame owner, boolean modal) {
        super(owner,modal);
        panelServicioCapelec = new PanelServicioCapelec();
        this.getContentPane().add(panelServicioCapelec);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public PanelServicioCapelec getPanelServicioCapelec() {
        return panelServicioCapelec;
    }

    @Override
    public void dispose() {
        super.dispose();
        
    }

    @Override
    public void finalize() throws Throwable {
        panelServicioCapelec = null;
        super.finalize();
    }



}//end of class JDialogServCapelec
