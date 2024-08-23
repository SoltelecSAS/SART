/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.lujan;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogLucesVehiculoLujan extends JDialog{

    PanelLuxometroLujan panelLuxometro;    
    String placas;

    public JDialogLucesVehiculoLujan(Frame owner, boolean modal){
        super(owner, modal);
        panelLuxometro = new PanelLuxometroLujan();
        this.getContentPane().add(panelLuxometro);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(this.getToolkit().getScreenSize());
     }

    public JDialogLucesVehiculoLujan(Frame owner, boolean modal, long idPruebas, long idUsuario, long idHojaPrueba,int aplicTrans,String placas){
        this(owner,modal);
        this.placas= placas;
        iniciarHilo(idPruebas,idUsuario,idHojaPrueba,aplicTrans);
        this.setVisible(true);
    }

    public PanelLuxometroLujan getPanelLuxometro() {
        return panelLuxometro;
    }
    
    private void iniciarHilo(long idPruebas,long idUsuario,long idHojaPrueba,int aplicTrans) {
        HiloPruebaLuxometroVehiculoLujan hilo = new HiloPruebaLuxometroVehiculoLujan(panelLuxometro,aplicTrans,this.placas);
        hilo.setIdPrueba(idPruebas);
        hilo.setIdUsuario(idUsuario);
        hilo.setIdHojaPrueba(idHojaPrueba);
        (new Thread(hilo)).start();       
    }    
}
