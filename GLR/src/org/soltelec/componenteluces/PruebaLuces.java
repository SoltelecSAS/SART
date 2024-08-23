/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.componenteluces;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Usuario
 */
public class PruebaLuces extends JPanel implements ActionListener,ChangeListener{

    private LuzAlta luzAlta;
    private LuzBaja luzBaja;
    private LuzExploradora luzExploradora;
    private JButton buttonBlinkingAlta,buttonBlinkingBaja,buttonBlinkingExploradora;
    private JToggleButton toggleAlta,toggleBaja,toggleExploradora;
    boolean blinkingAlta,blinkingBaja,blinkingExploradora;
    public PruebaLuces(){
        crearComponentes();
        ponerComponentes();

    }

    private void crearComponentes() {
        luzAlta = new LuzAlta();
        luzBaja = new LuzBaja();
        luzExploradora = new LuzExploradora();
        buttonBlinkingAlta = new JButton("Alta Blinking");
        buttonBlinkingAlta.addActionListener(this);
        buttonBlinkingBaja = new JButton("Baja Blinking");
        buttonBlinkingBaja.addActionListener(this);
        buttonBlinkingExploradora = new JButton("Baja Exploradora");
        buttonBlinkingExploradora.addActionListener(this);

        toggleAlta = new JToggleButton("Alta onOff");
        toggleAlta.addChangeListener(this);
        toggleBaja = new JToggleButton("Baja OnOff");
        toggleBaja.addChangeListener(this);
        toggleExploradora = new JToggleButton("Exploradora OnOff");
        toggleExploradora.addChangeListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == buttonBlinkingAlta ){
            blinkingAlta = !blinkingAlta;
            luzAlta.setBlinking(blinkingAlta);
        }else if(e.getSource() == buttonBlinkingBaja){
            blinkingBaja = !blinkingBaja;
            luzBaja.setBlinking(blinkingBaja);
        }else if (e.getSource() == buttonBlinkingExploradora){
            blinkingExploradora = !blinkingExploradora;
            luzExploradora.setBlinking(blinkingExploradora);
        }
    }

    private void ponerComponentes() {
        GridLayout grid = new GridLayout(3,3,10,10);
        this.setLayout(grid);
        this.add(luzAlta);
        this.add(luzBaja);
        this.add(luzExploradora);
        this.add(buttonBlinkingAlta);
        this.add(buttonBlinkingBaja);
        this.add(buttonBlinkingExploradora);
        this.add(toggleAlta);
        this.add(toggleBaja);
        this.add(toggleExploradora);
    }

    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == toggleAlta){
            luzAlta.setOnOff(toggleAlta.isSelected());
        } else if(e.getSource() == toggleBaja){
            luzBaja.setOnOff(toggleBaja.isSelected());
        } else if(e.getSource() == toggleExploradora){
            luzExploradora.setOnOff(toggleExploradora.isSelected());
        }
    }


}
