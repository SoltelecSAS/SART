/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.capelec.cap2500;

import org.soltelec.luxometro.*;
import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;

/**
 *
 * @author Usuario
 */
public class JDialogLucesCarros extends JDialog{

    PanelLuxometro panelLuxometro;
    public static void main(String[] args) {
            new JDialogLucesCarros(null, true).setVisible(true);
    }
    

    public JDialogLucesCarros(Frame owner, boolean modal){
        super(owner, modal);
        panelLuxometro = new PanelLuxometro();
        this.getContentPane().add(panelLuxometro);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(this.getToolkit().getScreenSize());
     }

    public JDialogLucesCarros(Frame owner, boolean modal, long idPruebas, long idUsuario, long idHojaPrueba){
        this(owner,modal);
        iniciarHilo(idPruebas,idUsuario,idHojaPrueba);
        //this.setVisible(true);
    }

    public PanelLuxometro getPanelLuxometro() {
        return panelLuxometro;
    }

    private void iniciarHilo(long idPruebas,long idUsuario,long idHojaPrueba) {
        HiloPruebaLuxometroCarros hilo = new HiloPruebaLuxometroCarros(panelLuxometro);
        hilo.setIdPrueba(idPruebas);
        hilo.setIdUsuario(idUsuario);
        hilo.setIdHojaPrueba(idHojaPrueba);
        Thread t = new Thread(hilo);
        t.start();
        this.setVisible(true);
        System.out.println("esperando informacion");
        setVisible(false);
    }


    
}
