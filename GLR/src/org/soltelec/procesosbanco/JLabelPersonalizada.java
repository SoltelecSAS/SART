/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import javax.swing.JLabel;

/**
 *
 * @author Usuario
 */
public class JLabelPersonalizada extends JLabel{
 static final String cadena = "<html><font size=\"105\" color=\"8a4117\">";
 static final String cadenaFinal = "</font></center></html>";
    public JLabelPersonalizada(String text){
       super();
       super.setText(cadena+text+cadenaFinal);
    }


    public void setText(String str){
        super.setText(cadena + str + cadenaFinal);
    }

}
