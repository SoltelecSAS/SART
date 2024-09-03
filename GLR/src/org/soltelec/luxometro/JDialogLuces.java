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
public class JDialogLuces extends JDialog{

    PanelLuxometro panelLuxometro;
   
    

    public JDialogLuces(Frame owner, boolean modal){
        super(owner, modal);
        panelLuxometro = new PanelLuxometro();
        this.getContentPane().add(panelLuxometro);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(this.getToolkit().getScreenSize());
       
     }

    public JDialogLuces(Frame owner, boolean modal, long idPruebas, long idUsuario, long idHojaPrueba,int aplicTrans, String tipoVehiculo){
        this(owner,modal);
        iniciarHilo(idPruebas,idUsuario,idHojaPrueba,aplicTrans, tipoVehiculo);
        this.setVisible(true);
    }

    public PanelLuxometro getPanelLuxometro() {
        return panelLuxometro;
    }
    private void iniciarHilo(long idPruebas,long idUsuario,long idHojaPrueba,int aplicTrans, String tipoVehiculo) {
        System.out.println(" entro a inicar hilo luces con: "+idPruebas+" "+idUsuario+" "+idHojaPrueba+" "+aplicTrans+" "+tipoVehiculo);
        HiloPruebaLuxometro hilo = new HiloPruebaLuxometro(panelLuxometro,aplicTrans);
        hilo.setIdPrueba(idPruebas);
        hilo.setIdUsuario(idUsuario);
        hilo.setIdHojaPrueba(idHojaPrueba);
        hilo.setTipoVehiculo(tipoVehiculo);
        (new Thread(hilo)).start();       
    }    
}