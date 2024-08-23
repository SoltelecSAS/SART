/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

/**
 *Clase para permitir cerrar las ventanas
 * pide un motivo ... para cerrar la clase
 * @author Usuario
 */
public class CerrarVentanas extends WindowAdapter{
    @Override
    public void windowClosing(WindowEvent e){//TODO hacer validacion de que solo pueda cancelar un superusuario
        String razon = JOptionPane.showInputDialog("Razon Para cancelar la prueba");
        System.out.println("Razon para cancelar la prueba: " + razon);
        System.exit(-1);
    }//
}//end of class WindowAdapter
