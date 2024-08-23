/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogLucesLujan extends JDialog{

    PanelLuxometro panelLuxometro;
   
    

    public JDialogLucesLujan(Frame owner, boolean modal){
        super(owner, modal);
        panelLuxometro = new PanelLuxometro();
        this.getContentPane().add(panelLuxometro);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(this.getToolkit().getScreenSize());
       
     }

    public JDialogLucesLujan(Frame owner, boolean modal, long idPruebas, long idUsuario, long idHojaPrueba,int aplicTrans){
        this(owner,modal);
        iniciarHilo(idPruebas,idUsuario,idHojaPrueba,aplicTrans);
        this.setVisible(true);
    }

    public PanelLuxometro getPanelLuxometro() {
        return panelLuxometro;
    }
    private void iniciarHilo(long idPruebas,long idUsuario,long idHojaPrueba,int aplicTrans) {
        HiloPruebaLuxometro hilo = new HiloPruebaLuxometro(panelLuxometro,aplicTrans);
        hilo.setIdPrueba(idPruebas);
        hilo.setIdUsuario(idUsuario);
        hilo.setIdHojaPrueba(idHojaPrueba);
        (new Thread(hilo)).start();       
    }    
}
