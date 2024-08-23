/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author User
 */
public class UtilCancelaciones {
    
    public static void removerListeners(JButton boton) {
        
        for ( ActionListener l :boton.getActionListeners()){
            boton.removeActionListener(l);
        }
        
        
    }
    
}
