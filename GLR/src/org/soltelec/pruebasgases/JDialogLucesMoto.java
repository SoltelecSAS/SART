/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.awt.Frame;
import javax.swing.JDialog;
import org.soltelec.luxometro.HiloLucesMoto;
import org.soltelec.luxometro.PanelLuxometroMotos;

/**
 *JDialog de la prueba de luces para Motos
 * @author Usuario
 */
public class JDialogLucesMoto extends JDialog{
    PanelLuxometroMotos panel;

    public JDialogLucesMoto(Frame owner, boolean modal){
        super(owner,modal);
        panel = new PanelLuxometroMotos();
        this.getContentPane().add(panel);
        this.setSize(this.getToolkit().getScreenSize());
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public JDialogLucesMoto(Frame owner, boolean modal, Long idPrueba, int idHojaPruebas, int idUsuario) {
        this(owner, modal);
        iniciarHilo(idPrueba,idUsuario,idHojaPruebas);
    }


    public PanelLuxometroMotos getPanel() {
        return panel;
    }//end of method

    private void iniciarHilo(Long idPrueba, int idUsuario, int idHojaPruebas) {
        HiloLucesMoto h = new HiloLucesMoto(panel,1,"SERIAL");
         h.setIdPrueba(idPrueba);
         h.setIdUsuario(idUsuario);
         h.setIdHojaPrueba(idHojaPruebas);
         Thread t = new Thread(h);
         t.start();
         this.setVisible(true);
    }




}//end of class JDialogLucesMoto
