/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.lujan;

import javax.swing.JButton;
import org.soltelec.componenteluces.LuzBaja;
import org.soltelec.componenteluces.LuzAlta;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import eu.hansolo.steelseries.tools.LcdColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static java.awt.GridBagConstraints.*;

/**
 *
 * @author Usuario
 */

/*
 * Codigos de las medidas para la prueba de motos
 * 2000 intensidad luz alta derecha en motos
 * 2001 intensidad alta izquierda en motos
 * 2002 angulo inclinacion baja izquierad para motos
 * 2013 angulo inclinacion baja derecha en motos
 * 2014 intensidad de luz baja derecha en motos
 * 2015 intensidad de luz baja izquierda en motos
 *
 *
 *
 *
 *
 */
public class PanelLuxometroMotosLujan extends JPanel {

    //private LuzAlta luzAlta, luzAlta2;
    private LuzBaja luzBaja, luzBaja2,luzBaja3,luzBaja4;
    private JLabel labelLuzBaja, labelLuzBaja2,labelLuzBaja3,labelLuzBaja4;
    private JLabel labelTitulo;
    private DisplayMulti display;
    private JButton buttonCancelar;

    public PanelLuxometroMotosLujan() {
        super();
        crearComponentes();
        ponerComponentes();
    }

    private void crearComponentes() {
        Font f = new Font(Font.SERIF, Font.PLAIN, 30);
        Font f1 = new Font(Font.SERIF, Font.PLAIN, 40);
        /*luzAlta = new LuzAlta();
        luzAlta.setPreferredSize(new Dimension(200, 200));
        luzAlta.setVisible(false);
        luzAlta2 = new LuzAlta();
        luzAlta2.setPreferredSize(new Dimension(200, 200));
        luzAlta2.setVisible(false);*/
        luzBaja = new LuzBaja();
        luzBaja.setPreferredSize(new Dimension(200, 200));
        //luzBaja.setLedColor(LedColor.CYAN_LED);
        luzBaja2 = new LuzBaja();
        luzBaja2.setPreferredSize(new Dimension(200, 200));
        luzBaja2.setVisible(false);
        luzBaja3 = new LuzBaja();
        luzBaja3.setPreferredSize(new Dimension(200, 200));
        luzBaja3.setVisible(false);
        luzBaja4 = new LuzBaja();
        luzBaja4.setPreferredSize(new Dimension(200, 200));
        luzBaja4.setVisible(false);
        //ledLuzAlta.setEnabled(false);

        labelLuzBaja = new JLabel("LUZ BAJA #1");
        labelLuzBaja.setFont(f);
        labelTitulo = new JLabel("PRUEBA DE LUCES PARA MOTOS LUXOMETRO");
        labelTitulo.setFont(f1);
        labelLuzBaja2 = new JLabel("LUZ BAJA #2");
        labelLuzBaja2.setFont(f);
        labelLuzBaja2.setVisible(false);
        labelLuzBaja3 = new JLabel("LUZ BAJA #3");
        labelLuzBaja3.setFont(f);
        labelLuzBaja3.setVisible(false);
        labelLuzBaja4 = new JLabel("LUZ BAJA #4");
        labelLuzBaja4.setFont(f);
        labelLuzBaja4.setVisible(false);
        display = new DisplayMulti();
        display.setLcdColor(LcdColor.BLUEDARKBLUE_LCD);
        display.setDigitalFont(true);
        display.setLcdUnitString("s");
        display.setPreferredSize(new Dimension(100, 50));
        buttonCancelar = new JButton("Cancelar");
        buttonCancelar.setForeground(Color.red);
    }//end of method crearComponentes

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.insets = new Insets(25, 10, 25, 10);
        c.gridwidth = REMAINDER;
        c.gridheight = 2;
        filaColumna(0, 1, c);
        this.add(labelTitulo, c);
        c.gridwidth = 1;
        c.gridheight = 1;
        filaColumna(2, 1, c);
        filaColumna(2, 2, c);
        c.gridheight = 2;
        JPanel panelEnvoltor = new JPanel();
        panelEnvoltor.add(luzBaja4);
        this.add(panelEnvoltor, c);

        filaColumna(2, 3, c);
        c.gridheight = 1;
        //this.add(labelLuzAlta,c);
        c.gridheight = 2;
        JPanel panelEnvoltor2 = new JPanel();
        panelEnvoltor2.add(luzBaja3);
        filaColumna(2, 4, c);
        this.add(panelEnvoltor2, c);
        filaColumna(4, 1, c);
        c.gridheight = 1;
        //this.add(labelLuzBaja2,c);
        filaColumna(4, 2, c);
        c.gridheight = 2;
        JPanel panelEnvoltor1 = new JPanel();
        panelEnvoltor1.add(luzBaja2);
        this.add(panelEnvoltor1, c);
        c.gridheight = 1;
        filaColumna(4, 3, c);
        //this.add(labelLuzBaja,c);
        filaColumna(4, 4, c);
        c.gridheight = 2;
        JPanel panelEnvoltor3 = new JPanel();
        panelEnvoltor3.add(luzBaja);
        this.add(panelEnvoltor3, c);
        //this.add(panelEnvoltor1,c);
        filaColumna(2, 5, c);
        JPanel panelEnvoltor4 = new JPanel();
        panelEnvoltor4.add(display, c);
        this.add(panelEnvoltor4, c);
        filaColumna(4, 5, c);
        this.add(buttonCancelar, c);

    }//end of method ponerComponentes

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//end of method filaColumna

    public void cerrar() {
        (SwingUtilities.getWindowAncestor(this)).dispose();
        //luzAlta.setBlinking(false);
        luzBaja.setBlinking(false);
        //luzAlta2.setBlinking(false);
        luzBaja2.setBlinking(false);
        luzBaja3.setBlinking(false);
        luzBaja4.setBlinking(false);
    }//end of method cerrar

    public static void main(String args[]) {
        JFrame app = new JFrame();
        PanelLuxometroMotosLujan p = new PanelLuxometroMotosLujan();
        app.getContentPane().add(p);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HiloLucesMotoLujan h = new HiloLucesMotoLujan(p,1,"SERIAL","eve700");
        p.getButtonCancelar().addActionListener(h);
        Thread t = new Thread(h);
        //t.start();
        app.setVisible(true);
    }//end of method main

    public DisplayMulti getDisplay() {
        return display;
    }

    public JLabel getLabelLuzBaja() {
        return labelLuzBaja;
    }

    public JLabel getLabelTitulo() {
        return labelTitulo;
    }

    

    public LuzBaja getLuzBaja() {
        return luzBaja;
    }

    public void habilitarLuces(int numeroLuces) {

        if (numeroLuces == 1) {
            luzBaja.setVisible(true);
            System.out.println("...Luces deben ser altas eliminadas por Norma");
        }
        if (numeroLuces == 2) {
            labelLuzBaja2.setVisible(true);
            luzBaja2.setVisible(true);
        }
        if (numeroLuces == 3) {
            labelLuzBaja2.setVisible(true);
            luzBaja2.setVisible(true);
            labelLuzBaja3.setVisible(true);
            luzBaja3.setVisible(true);
        }
        if (numeroLuces == 4) {
            labelLuzBaja2.setVisible(true);
            luzBaja2.setVisible(true);
            labelLuzBaja3.setVisible(true);
            luzBaja3.setVisible(true);
            labelLuzBaja4.setVisible(true);
            luzBaja4.setVisible(true);            
        }
    }

    public JLabel getLabelLuzBaja2() {
        return labelLuzBaja2;
    }
    public JLabel getLabelLuzBaja3() {
        return labelLuzBaja2;
    }
    public JLabel getLabelLuzBaja4() {
        return labelLuzBaja2;
    }   

    public LuzBaja getLuzBaja2() {
        return luzBaja2;
    }
    public LuzBaja getLuzBaja3() {
        return luzBaja3;
    }
    public LuzBaja getLuzBaja4() {
        return luzBaja4;
    }

    public JButton getButtonCancelar() {
        return buttonCancelar;
    }

}//end of class PanelLuxometroMotos
