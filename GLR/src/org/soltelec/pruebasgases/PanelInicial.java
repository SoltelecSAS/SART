/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class PanelInicial extends JPanel {
    PanelVerificacion panelVerificacion;
    private JButton buttonContinuar;
    public PanelInicial(){
        //La interfaz grafica tendra tres zonas, la superior por compatibilidad
        //La del medio para el contenido principal y la de abajo para los botones
        panelVerificacion = new PanelVerificacion();
        this.add(panelVerificacion);
        buttonContinuar = new JButton("Continuar");
        buttonContinuar.addActionListener(panelVerificacion);
        this.add(buttonContinuar);
    }//end of Constructor

    public static void main(String args[]){
            JFrame app  = new JFrame();
            PanelInicial panelInicial= new PanelInicial();
            app.add(panelInicial);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
            app.setExtendedState(app.getExtendedState()|JFrame.MAXIMIZED_BOTH);
            //app.setSize(820,650);
            //panelC.iniciarProceso();
    }

}
