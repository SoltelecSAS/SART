/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import javax.swing.JButton;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static java.awt.GridBagConstraints.*;



/**
 *Clase solamente por compatibilidad
 * @author Usuario
 */
public class PanelAnguloInclinacion extends JPanel implements ChangeListener{
    private JSlider sliderAngulo;
    private JLabel labelAngulo;
    private JLabel labelTitulo;
    private JButton buttonOk;
    private DecimalFormat df;
   
    NumberFormat nf;

    public PanelAnguloInclinacion(){
     crearComponentes();
     ponerComponentes();

    }

    private void crearComponentes() {
        Font f = new Font(Font.SERIF,Font.PLAIN,48);
        Font f1 = new Font(Font.SERIF,Font.ITALIC,60);
        labelAngulo = new JLabel("2.0");
        labelAngulo.setFont(f1);
        labelTitulo = new JLabel("Angulo de Inclinacion");
        labelTitulo.setFont(f);
        sliderAngulo = new JSlider(0, 40);
        sliderAngulo.addChangeListener(this);
        sliderAngulo.setSnapToTicks(true);
        sliderAngulo.setMinorTickSpacing(1);
        sliderAngulo.setPaintTrack(true);
        sliderAngulo.setPaintTicks(false);        
        buttonOk = new JButton("OK");
        buttonOk.setFont(f1);
        df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        df.applyPattern("##.#");
        df.setMinimumFractionDigits(1);
    }

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
         System.out.println("ln 68 " );
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,5,10,5);
        c.weightx = 0.8;
        c.weighty = 0.25;
        this.setLayout(bag);
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = CENTER;
        c.gridwidth = REMAINDER;
          System.out.println("ln 78 " );
        this.add(labelTitulo,c);
        c.fill = BOTH;
        c.gridx = 1;
        c.gridy = 2;
          System.out.println("ln 83 " );
        this.add(sliderAngulo,c);
        
        c.fill = NONE;
        c.gridx = 1;
        c.gridy = 3;
          System.out.println("ln 89 " );
        this.add(labelAngulo,c);
        //c.fill = BOTH;
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 1;
          System.out.println("ln 95 " );
      
        c.weightx = 0.25;
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 1;
        c.fill = HORIZONTAL;
        this.add(buttonOk,c);
        this.setPreferredSize(new Dimension(600,480));       
        
    }

    public void stateChanged(ChangeEvent e) {
        labelAngulo.setText(df.format(sliderAngulo.getValue()*0.1));
    }

    public JLabel getLabelTitulo() {
        return labelTitulo;
    }

    public JButton getButtonOk() {
        return buttonOk;
    }

    public double getValorSlider() {
        return sliderAngulo.getValue()*0.1;
    }    
 
}//end of class DialogoAnguloInclinacion


