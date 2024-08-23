/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;

/**
 *
 * @author Usuario
 */

public class JTextFieldPersonalizada extends JTextField{
    private Color background = Color.BLACK;
    private int size = 40;
    private Font font;
    public JTextFieldPersonalizada(){
        super();
        this.setBackground(background);
        font = new Font("Arial",Font.BOLD,size);
        this.setFont(font);
        this.setForeground(Color.GREEN);
        this.setHorizontalAlignment(JTextField.CENTER);
    }//end fo contstructor

    public JTextFieldPersonalizada(String cadena){
        this();
        this.setText(cadena);
    }

}
