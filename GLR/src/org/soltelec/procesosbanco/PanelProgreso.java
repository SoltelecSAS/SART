/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;
import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
/**
 *Realmente no tiene mucho sentido
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class PanelProgreso extends JPanel{

    JProgressBar barraTiempo;
    JLabel labelMensaje,labelTiempo;
    int contadorTimer = 0;
    Timer timer;
    public PanelProgreso(){//Un panel con una barra de progreso y unaetiqueta para ponerlo por ahi
        inicializarInterfaz();

    }

    private void inicializarInterfaz() {
        //Un panel con una barra de progreso y unaetiqueta para ponerlo por ahi
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        //llenar todo el espacio horizontal
        this.setLayout(bag);
        c.insets = new Insets(5,5,3,3);
        c.weightx = 1.0;
        
        c.fill = NONE;
        labelMensaje = new JLabel("Ingresando Gas ...! ");
        labelMensaje.setFont(new Font("Serif",Font.PLAIN,40));
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.75;
        this.add(labelMensaje, c);
        labelTiempo = new JLabel(" ");
        labelTiempo.setFont(new Font("Serif",Font.PLAIN,28));
        labelTiempo.setForeground(Color.gray);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.2;
        c.fill = CENTER;
        this.add(labelTiempo, c);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = BOTH;
        c.weighty = 0.25;
        barraTiempo = new JProgressBar();
        barraTiempo.setBorderPainted(true);
        barraTiempo.setStringPainted(true);
        barraTiempo.setMaximum(20);
        barraTiempo.setFont(new Font("Serif",Font.PLAIN,30));
        this.add(barraTiempo, c);
    }//end of method

    public JProgressBar getBarraTiempo() {
        return barraTiempo;
    }

    public JLabel getLabelMensaje() {
        return labelMensaje;
    }

    public JLabel getLabelTiempo() {
        return labelTiempo;
    }



}
