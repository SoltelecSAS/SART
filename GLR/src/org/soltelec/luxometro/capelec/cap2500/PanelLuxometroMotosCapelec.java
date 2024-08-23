/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.capelec.cap2500;

import org.soltelec.luxometro.*;
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
public class PanelLuxometroMotosCapelec extends JPanel{

    private LuzAlta luzAlta,luzAlta2;
    private LuzBaja luzBaja,luzBaja2;
    private JLabel labelLuzAlta, labelLuzBaja,labelLuzAlta2,labelLuzBaja2;
    private JLabel labelTitulo;
    private DisplayMulti display;
    private JButton buttonCancelar;

    public PanelLuxometroMotosCapelec() {
        super();
        crearComponentes();
        ponerComponentes();
    }

    private void crearComponentes() {
        Font f = new Font(Font.SERIF,Font.PLAIN,30);
        Font f1 = new Font(Font.SERIF,Font.PLAIN,40);
        luzAlta = new LuzAlta();
        luzAlta.setPreferredSize( new Dimension(200,200));
        luzAlta.setVisible(false);
        luzAlta2 = new LuzAlta();
        luzAlta2.setPreferredSize(new Dimension(200,200));
        luzAlta2.setVisible(false);
        luzBaja = new LuzBaja();
        luzBaja.setPreferredSize(new Dimension(200,200));
        //luzBaja.setLedColor(LedColor.CYAN_LED);
        luzBaja2 = new LuzBaja();
        luzBaja2.setPreferredSize( new Dimension(200,200));
        luzBaja2.setVisible(false);
        //ledLuzAlta.setEnabled(false);
        labelLuzAlta = new JLabel("LUZ ALTA #1");
        labelLuzAlta.setFont(f);
        labelLuzAlta.setVisible(false);
        labelLuzBaja = new JLabel("LUZ BAJA #1");
        labelLuzBaja.setFont(f);
        labelTitulo = new JLabel("PRUEBA DE LUCES PARA MOTOS LUXOMETRO");
        labelTitulo.setFont(f1);
        labelLuzAlta2 = new JLabel("LUZ ALTA #2");
        labelLuzAlta2.setFont(f);
        labelLuzAlta2.setVisible(false);
        labelLuzBaja2 = new JLabel("LUZ BAJA #2");
        labelLuzBaja2.setFont(f);
        labelLuzBaja2.setVisible(false);
        display = new DisplayMulti();
        display.setLcdColor(LcdColor.BLUEDARKBLUE_LCD);
        display.setDigitalFont(true);
        display.setLcdUnitString("s");
        display.setPreferredSize( new Dimension(100,50));
        buttonCancelar = new JButton("Cancelar");
        buttonCancelar.setForeground(Color.red);
    }//end of method crearComponentes

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.insets = new Insets(25,10,25,10);
        c.gridwidth = REMAINDER;
        c.gridheight = 2;
        filaColumna(0,1,c);
        this.add(labelTitulo,c);
        c.gridwidth = 1;
        c.gridheight = 1;
        filaColumna(2,1,c);
        //this.add(labelLuzAlta2,c);
        filaColumna(2,2,c);
        c.gridheight = 2;
        JPanel panelEnvoltor = new JPanel();
        panelEnvoltor.add(luzAlta2);
        this.add(panelEnvoltor,c);

        filaColumna(2,3,c);
        c.gridheight = 1;
        //this.add(labelLuzAlta,c);
        c.gridheight = 2;
        JPanel panelEnvoltor2 = new JPanel();
        //panelEnvoltor2.add(luzAlta);
        filaColumna(2,4,c);
        this.add(panelEnvoltor2,c);
        filaColumna(4,1,c);
        c.gridheight = 1;
        //this.add(labelLuzBaja2,c);
        filaColumna(4,2,c);
        c.gridheight = 2;
        JPanel panelEnvoltor1 = new JPanel();
        panelEnvoltor1.add(luzBaja2);
        this.add(panelEnvoltor1,c);
        c.gridheight = 1;
        filaColumna(4,3,c);
        //this.add(labelLuzBaja,c);
        filaColumna(4,4,c);
        c.gridheight = 2;
        JPanel panelEnvoltor3 = new JPanel();
        panelEnvoltor3.add(luzBaja);
        this.add(panelEnvoltor3,c);
        //this.add(panelEnvoltor1,c);
        filaColumna(2,5,c);
        JPanel panelEnvoltor4 = new JPanel();
        panelEnvoltor4.add(display,c);
        this.add(panelEnvoltor4,c);
        filaColumna(4,5,c);
        this.add(buttonCancelar,c);

    }//end of method ponerComponentes

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//end of method filaColumna

   public void cerrar(){
        (SwingUtilities.getWindowAncestor(this)).dispose();
        luzAlta.setBlinking(false);
        luzBaja.setBlinking(false);
        luzAlta2.setBlinking(false);
        luzBaja2.setBlinking(false);
    }//end of method cerrar

    public static void main(String args[]){
        JFrame app = new JFrame();
        PanelLuxometroMotosCapelec p = new PanelLuxometroMotosCapelec();
        app.getContentPane().add(p);
        app.setExtendedState( JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HiloPruebaLuxometroMoto h = new HiloPruebaLuxometroMoto(p);
        p.getButtonCancelar().addActionListener(h);
        Thread t  = new Thread(h);
        //t.start();
        app.setVisible(true);
    }//end of method main

    public DisplayMulti getDisplay() {
        return display;
    }

    public JLabel getLabelLuzAlta() {
        return labelLuzAlta;
    }

    public JLabel getLabelLuzBaja() {
        return labelLuzBaja;
    }

    public JLabel getLabelTitulo() {
        return labelTitulo;
    }

    public LuzAlta getLuzAlta() {
        return luzAlta;
    }

    public LuzBaja getLuzBaja() {
        return luzBaja;
    }

    public void habilitarLuces(int numeroLuces){

        if(numeroLuces == 1){
        labelLuzAlta.setVisible(true);
        luzAlta.setVisible(true);
        System.out.println("...Luces deben ser altas eliminadas por Norma");
        }
        if(numeroLuces ==2 ){
        labelLuzAlta.setVisible(true);
        luzAlta.setVisible(true);
        labelLuzBaja2.setVisible(true);
        luzBaja2.setVisible(true);
        }
        
    }

    public JLabel getLabelLuzAlta2() {
        return labelLuzAlta2;
    }

    public JLabel getLabelLuzBaja2() {
        return labelLuzBaja2;
    }

    public LuzAlta getLuzAlta2() {
        return luzAlta2;
    }

    public LuzBaja getLuzBaja2() {
        return luzBaja2;
    }

    public JButton getButtonCancelar() {
        return buttonCancelar;
    }


}//end of class PanelLuxometroMotos
