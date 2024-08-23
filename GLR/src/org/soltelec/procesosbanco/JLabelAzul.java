/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import javax.swing.JLabel;

/**
 *
 * @author soltelec
 */
public class JLabelAzul extends JLabel{
    
    static final String cadena = "<html><font size=\"3\" color=\"000066\"><p>";
    static final String cadenaFinal = "</p></font></html>";
    
    public JLabelAzul(String text){
       super();
       super.setText(cadena+text+cadenaFinal);
    }


    public void setText(String str){
        super.setText(cadena + str + cadenaFinal);
    }
    
}
