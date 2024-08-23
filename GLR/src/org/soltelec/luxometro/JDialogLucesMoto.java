/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * JDialog de la prueba de luces para Motos
 *
 * @author Gerencia TIC
 */
public class JDialogLucesMoto extends JDialog 
{

    public static void main(String[] args) 
    {
        new JDialogLucesMoto(null, true).setVisible(true);
    }
    PanelLuxometroMotos panel;
    
    
    public JDialogLucesMoto(Frame owner, boolean modal) 
    {
        super(owner, modal);
        panel = new PanelLuxometroMotos();
        this.getContentPane().add(panel);
        this.setSize(this.getToolkit().getScreenSize());
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public JDialogLucesMoto(Frame owner, boolean modal, Long idPrueba, int idHojaPruebas, int idUsuario,int aplicTrans,String tipoLuxometro) {
        this(owner, modal);        
        iniciarHilo(idPrueba, idUsuario, idHojaPruebas,aplicTrans,tipoLuxometro);   
    }
    
    public PanelLuxometroMotos getPanel() {
        return panel;
    }//end of method

    private void iniciarHilo(Long idPrueba, int idUsuario, int idHojaPruebas,int aplicTrans,String tipoLuxometro) {
         System.out.println("el tipo luxometro is "+tipoLuxometro);
        HiloLucesMoto h = new HiloLucesMoto(panel,aplicTrans,tipoLuxometro);
        h.setIdPrueba(idPrueba);
        h.setIdUsuario(idUsuario);
        h.setIdHojaPrueba(idHojaPruebas);
        Thread t = new Thread(h);
        t.start();
        this.setVisible(true);       
    }
    
}//end of class JDialogLucesMoto
