/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 *La idea de esta clase es que dependa de un balor booleano para que se pinte
 * como verde o negra según sea true o false
 * No usada reemplazada por los leds de SteelSeries
 * @author Usuario
 */
public class JLabelBanderas extends JLabel{
    private Color colorPrender = Color.red;
    private Color colorApagar = Color.BLACK;
    private boolean on = false;

    public JLabelBanderas() {
        super();
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        this.setBorder(border);
        this.setOpaque(true);
        this.setBackground(colorApagar);
        this.setForeground(colorApagar);
                //this.setPreferredSize(new Dimension(25,25));//para que se muestre de manera correcta
    }

    public JLabelBanderas(String texto){
        this();
        this.setText(texto);
    }



    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if(on){
            this.setBackground(colorPrender);
        }else {
            this.setBackground(colorApagar);
        }
    }


}
