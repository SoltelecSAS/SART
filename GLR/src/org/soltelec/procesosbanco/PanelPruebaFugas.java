/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import javax.swing.*;
/**
 *Clase para mostrar en un dialogo o poner el el panel principal para
 * hacer la prueba de fugas, este es e l panel principal contiene una etiqueta
 * una barra de progreso y opcionalmente un boton
 * @author Usuario
 */
public class PanelPruebaFugas extends JPanel{
     int contadorProceso = 0;//contador para ver el progreso
        JLabel etiqueta;
        JButton siguienteJButton;
        JPanel panelSur;
        PanelProgreso panelProgreso;
     public PanelPruebaFugas(){
         panelProgreso = new PanelProgreso();
         this.add(panelProgreso);
     }//end of constructor


}//end of class PanelPruebaFugas
