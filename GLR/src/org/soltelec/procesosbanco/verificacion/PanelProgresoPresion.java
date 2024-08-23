/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco.verificacion;

import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.Section;
import javax.swing.*;
import java.awt.*;
import static java.awt.GridBagConstraints.*;
/**
 *
 * Un panel con una barra de progreso y unaetiqueta para ponerlo por ahi
 * 17/02/2011 se agrega un display de presion para proceso de calibracion
 * este es el panel que sale cuando se hace calibracion de dos puntos
 * @author Usuario
 */
public class PanelProgresoPresion extends JPanel{

    JProgressBar barraTiempo;
    JLabel labelMensaje;
    int contadorTimer = 0;
    Timer timer;
    Radial radial;
   public static Integer tall= 0;
    public PanelProgresoPresion(){//
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        //Un panel con una barra de progreso y unaetiqueta para ponerlo por ahi
        configurarRadialPresion();
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        //llenar todo el espacio horizontal
        this.setLayout(bag);
        c.insets = new Insets(1,1,1, 1);
        c.weightx = 4.0;
        c.fill = HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 4.33;
        c.gridwidth = REMAINDER;
        this.add(radial,c);
        labelMensaje = new JLabel("Ingresando gas ... ");
        labelMensaje.setFont(new Font("Serif",Font.PLAIN,40));
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.33;
        this.add(labelMensaje, c);
        c.fill = BOTH;
        c.gridx = 0;
        c.gridy = 2;
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

     private void configurarRadialPresion() {

        radial = new Radial();
        radial.init(1350,1350);
      //  radial.setFrame3dEffectVisible(true);
        radial.setFrameDesign(FrameDesign.BLACK_METAL);
        radial.setArea3DEffectVisible(true);        
        Dimension d = this.getToolkit().getScreenSize();
        int size = (int) (d.height * 0.43);
        tall=size;
        final Section[] SECTIONS = {new Section(0, 5900, java.awt.Color.WHITE), new Section(6000, 8000, java.awt.Color.RED)};
        radial.setTickmarkSections(SECTIONS);
        radial.setTickmarkSectionsVisible(true);
        radial.setThreshold(2500);
        radial.setFrameVisible(true);
        radial.setTickmarkColor(Color.ORANGE);
        System.out.println("seteo de hilo  presion "+size);
        radial.setPreferredSize(new Dimension(size,size));  
        radial.setTitle("Presion");
        radial.setUnitString("inHG");
        radial.setBackgroundColor(BackgroundColor.BRUSHED_METAL);
        radial.setMaxValue(1500);
        radial.setMinValue(0);
        //radial.setTickLabelPeriod(100);
        //radial.setScaleDividerPower(1);
    }

     public void setRadialSize() {
        radial.setPreferredSize(new Dimension(1000,1000));  
    }
    public Radial getRadial() {
        return radial;
    }


}

