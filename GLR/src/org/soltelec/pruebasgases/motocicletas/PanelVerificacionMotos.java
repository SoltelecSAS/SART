/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import javax.swing.JFrame;
import org.soltelec.pruebasgases.diesel.PanelVerificacionDiesel;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PanelVerificacionMotos extends PanelVerificacionDiesel{

    public PanelVerificacionMotos() {
        super();
        ocultarBotones();
    }

    private void ocultarBotones() {
       
        getLbl4().setVisible(false);
        getLbl5().setVisible(false);
        getLbl6().setVisible(false);
        getLbl7().setVisible(false);
        getLbl9().setVisible(false);
//        getLbl10().setVisible(false);
        
        getCheckBox4().setVisible(false);
        getCheckBox5().setVisible(false);
        getCheckBox6().setVisible(false);
        getCheckBox7().setVisible(false);
        getCheckBox9().setVisible(false);
//        getCheckBox10().setVisible(false);
        
    }
    
    
    public static void main(String args[]){
            JFrame app  = new JFrame();
            PanelVerificacionMotos panelC = new PanelVerificacionMotos();
            app.add(panelC);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
            app.setSize(820,650);
            //panelC.iniciarProceso();
    }
    
}
