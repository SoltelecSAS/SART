/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.capelec.cap2500;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import org.soltelec.luxometro.HiloLucesMoto;
import org.soltelec.luxometro.PanelLuxometroMotos;

/**
 * JDialog de la prueba de luces para Motos
 *
 * @author Usuario
 */
public class JDialogLucesMotoCapelec extends JDialog {

    public static void main(String[] args) {        
        new JDialogLucesMotoCapelec(null, true).setVisible(true);
    }
    PanelLuxometroMotosCapelec panel;
    
    public JDialogLucesMotoCapelec(Frame owner, boolean modal) {
        super(owner, modal);
        panel = new PanelLuxometroMotosCapelec();
        this.getContentPane().add(panel);
        this.setSize(this.getToolkit().getScreenSize());
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public JDialogLucesMotoCapelec(Frame owner, boolean modal, int idPrueba, int idHojaPruebas, int idUsuario) {
        this(owner, modal);
        iniciarHilo(idPrueba, idUsuario, idHojaPruebas);
    }
    
    public PanelLuxometroMotosCapelec getPanel() {
        return panel;
    }//end of method

    private void iniciarHilo(int idPrueba, int idUsuario, int idHojaPruebas) {
        String myLibraryPath = System.getProperty("user.dir");//or another absolute or relative path
        System.setProperty("java.library.path", myLibraryPath);
        HiloPruebaLuxometroMoto h = new HiloPruebaLuxometroMoto(panel);
        h.setIdPrueba(idPrueba);
        h.setIdUsuario(idUsuario);
        h.setIdHojaPrueba(idHojaPruebas);
        Thread t = new Thread(h);
        t.start();
        this.setVisible(true);
        System.out.println("esperando informacion");
        setVisible(false);
        
    }
    
}//end of class JDialogLucesMoto
