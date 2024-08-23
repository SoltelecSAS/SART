/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.lujan;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * JDialog de la prueba de luces para Motos
 *
 * @author Gerencia TIC
 */
public class JDialogLucesMotoLujan extends JDialog {

    public static void main(String[] args) {
        new JDialogLucesMotoLujan(null, true).setVisible(true);
    }
    PanelLuxometroMotosLujan panel;
    String placas;
    
    
    public JDialogLucesMotoLujan(Frame owner, boolean modal) {
        super(owner, modal);
        panel = new PanelLuxometroMotosLujan();
        this.getContentPane().add(panel);
        this.setSize(this.getToolkit().getScreenSize());
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public JDialogLucesMotoLujan(Frame owner, boolean modal, Long idPrueba, int idHojaPruebas, int idUsuario,int aplicTrans,String tipoLuxometro,String placas) {
        this(owner, modal);        
         this.placas= placas;
        iniciarHilo(idPrueba, idUsuario, idHojaPruebas,aplicTrans,tipoLuxometro);   
    }
    
    public PanelLuxometroMotosLujan getPanel() {
        return panel;
    }//end of method

    private void iniciarHilo(Long idPrueba, int idUsuario, int idHojaPruebas,int aplicTrans,String tipoLuxometro) {
         System.out.println("el tipo luxometro is "+tipoLuxometro);
        HiloLucesMotoLujan h = new HiloLucesMotoLujan(panel,aplicTrans,tipoLuxometro, this.placas);
        h.setIdPrueba(idPrueba);
        h.setIdUsuario(idUsuario);
        h.setIdHojaPrueba(idHojaPruebas);
        Thread t = new Thread(h);
        t.start();
        this.setVisible(true);
    }
    
}//end of class JDialogLucesMoto
