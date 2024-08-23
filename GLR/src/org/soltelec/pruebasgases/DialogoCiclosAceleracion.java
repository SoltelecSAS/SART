/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.awt.Window;
import javax.swing.SwingUtilities;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.ColorDef;
import eu.hansolo.steelseries.tools.GaugeType;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import eu.hansolo.steelseries.tools.PointerType;
import eu.hansolo.steelseries.tools.Section;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static java.awt.GridBagConstraints.*;

/**
 *Clase para mostrar las revouciones y los mensajes durante el ciclo de Aceleracion libre
 * @author Usuario
 */
public class DialogoCiclosAceleracion extends JPanel {

    private Radial radialTacometro;
    private Led ledRojo,ledAmarillo,ledVerde;
    private JLabel labelMensaje;
    private JLabel labelImagen;
    private DisplayMulti displayTiempo;
    private JButton buttonCancelar;
    private JPanel panelIzquierdo, panelDerecho,panelAbajo;
    private double ralenti, crucero;
    private boolean pruebaCancelada;

    public DialogoCiclosAceleracion(double velRalenti, double velCrucero){
        this.ralenti = velRalenti;
        this.crucero = velCrucero;
      inicializarInterfaz();
//              HiloSimulacionDiesel hilo = new HiloSimulacionDiesel(this);
//              hilo.setVelCrucero(velCrucero);
//              hilo.setVelRalenti(velRalenti);
//              (new Thread(hilo) ).start();
    }
    private void inicializarInterfaz() {
        crearComponentes();
    }//end of inicializarInterfaz

    private void crearComponentes() {
        ImageIcon image = new ImageIcon("Warning.png");
        labelImagen = new JLabel(image);
        labelMensaje = new JLabel("CICLOS DE ACELERACION LIBRE");
        labelMensaje.setFont(new Font(Font.SERIF,Font.BOLD,48));
        ledRojo = new Led();
        ledRojo.setLedColor(LedColor.RED_LED);
        ledRojo.setLedBlinking(true);
        ledAmarillo = new Led();
        ledAmarillo.setLedColor(LedColor.YELLOW_LED);
        ledVerde = new Led();
        ledVerde.setLedColor(LedColor.GREEN_LED);
        displayTiempo = new DisplayMulti();
        buttonCancelar = new JButton("Cancelar");
        configurarTacometro();
        configurarPanelIzquierdo();
        configurarPanelDerecho();
        configurarPanelAbajo();
        ponerComponentes();
    }//end of method crearComponentes

    private void configurarTacometro(){
        radialTacometro = new Radial();
        Dimension d = this.getToolkit().getScreenSize();
        int size =  (int) (d.width * 0.15);
        radialTacometro.setPreferredSize(new Dimension(size,size));
        radialTacometro.setMaxValue(8000);
        radialTacometro.setMinValue(0);
        radialTacometro.setTitle("RPM");
        radialTacometro.setUnitString("rev/min");
       // radialTacometro.setTickLabelPeriod(1000);
        //radialTacometro.setScaleDividerPower(2);
        radialTacometro.setGaugeType(GaugeType.TYPE3);
        final Section[] SECTIONS = {new Section(0, 5900, java.awt.Color.WHITE), new Section(6000, 8000, java.awt.Color.RED)};
        radialTacometro.setTickmarkSections(SECTIONS);
        radialTacometro.setTickmarkSectionsVisible(true);
        radialTacometro.setThreshold(2500);
        radialTacometro.setSectionsVisible(true);
        radialTacometro.setBackgroundColor(BackgroundColor.BLUE);
        radialTacometro.setDigitalFont(true);
        radialTacometro.setPointerType(PointerType.TYPE3);
        radialTacometro.setPointerColor(ColorDef.RED);
        Color color = new Color(120,0,0);
        final Section[] SECTIONS2 = {new Section(ralenti-250,ralenti+250,color), new Section(crucero-250, crucero + 250,color)};
        radialTacometro.setAreas(SECTIONS2);
        radialTacometro.setAreasVisible(true);
    }

    private void configurarPanelIzquierdo() {

    }//edn of method

    private void configurarPanelDerecho(){
        panelDerecho = new JPanel();
        JPanel panelSemaforo = new JPanel();
        GridLayout grid = new GridLayout(3,1,10,10);
        panelSemaforo.setLayout(grid);
        panelSemaforo.add(ledRojo);
        panelSemaforo.add(ledAmarillo);
        panelSemaforo.add(ledVerde);
        panelSemaforo.setBackground(Color.BLACK);
        panelSemaforo.setPreferredSize(new Dimension(100,400));
        displayTiempo.setPreferredSize(new Dimension(300,200));
        displayTiempo.setDigitalFont(true);
        displayTiempo.setLcdColor(LcdColor.STANDARD_LCD);
        displayTiempo.setLcdUnitString("s");
        panelDerecho.add(displayTiempo);
        panelDerecho.add(panelSemaforo);
    }//end of method

    private void configurarPanelAbajo(){
        panelAbajo = new JPanel();
        panelAbajo.add(labelImagen);
        panelAbajo.add(labelMensaje);
    }

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(10,5,10,5);
        c.weightx = 1.0;
        c.gridwidth = REMAINDER;
        filaColumna(0,0,c);
        this.add(panelAbajo,c);
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 1.0;
        filaColumna(1,0,c);
        JPanel panelEnvoltor = new JPanel();
        panelEnvoltor.setPreferredSize(new Dimension(420,420));
        radialTacometro.setPreferredSize(new Dimension(400,400));
        panelEnvoltor.add(radialTacometro);
        c.fill = BOTH;
        this.add(panelEnvoltor,c);
        c.fill = NONE;
        filaColumna(1,1,c);
        this.add(panelDerecho,c);
        filaColumna(3,0,c);
        this.add(buttonCancelar,c);
    }

    public void filaColumna(int fila, int columna, GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;
    }//end of filaColumna

    public DisplayMulti getDisplayTiempo() {
        return displayTiempo;
    }

    public JLabel getLabelMensaje() {
        return labelMensaje;
    }

    public Led getLedAmarillo() {
        return ledAmarillo;
    }

    public Led getLedRojo() {
        return ledRojo;
    }

    public Led getLedVerde() {
        return ledVerde;
    }

    public Radial getRadialTacometro() {
        return radialTacometro;
    }

    
    public static void main(String args[]){
        JFrame app = new JFrame("Dialogo Tacometro");
        app.getContentPane().add(new DialogoCiclosAceleracion(700,4500));
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }

   public void cerrar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        w.dispose();
    }

    void setPruebaCancelada(boolean b) {
        this.pruebaCancelada = true;
    }

    public boolean isPruebaCancelada() {
        return pruebaCancelada;
    }

    public JButton getButtonCancelar() {
        return buttonCancelar;
    }

    public void setRadialTacometro(Radial radialTacometro) {
        this.remove(this.radialTacometro.getParent());
        this.radialTacometro = radialTacometro;

        JPanel panelEnvoltor = new JPanel();
        panelEnvoltor.setPreferredSize(new Dimension(420,420));
        radialTacometro.setPreferredSize(new Dimension(400,400));
        panelEnvoltor.add(radialTacometro);
        radialTacometro.setPreferredSize(new Dimension(400,400));
        panelEnvoltor.add(radialTacometro);
        GridBagConstraints c  = new GridBagConstraints();
        c.insets = new Insets(10,5,10,5);
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 1.0;
        filaColumna(1,0,c);
        this.add(panelEnvoltor,c);
        this.revalidate();
    }

    public void repintarRangos(double ralenti, double crucero) {
        radialTacometro = new Radial();
        Dimension d = this.getToolkit().getScreenSize();
        int size =  (int) (d.width * 0.15);
        radialTacometro.setPreferredSize(new Dimension(size,size));
        radialTacometro.setMaxValue(8000);
        radialTacometro.setMinValue(0);
        radialTacometro.setTitle("RPM");
        radialTacometro.setUnitString("rev/min");
        //radialTacometro.setTickLabelPeriod(1000);
        //radialTacometro.setScaleDividerPower(2);
        radialTacometro.setGaugeType(GaugeType.TYPE3);
        final Section[] SECTIONS = {new Section(0, 5900, java.awt.Color.WHITE), new Section(6000, 8000, java.awt.Color.RED)};
        radialTacometro.setTickmarkSections(SECTIONS);
        radialTacometro.setTickmarkSectionsVisible(true);
        radialTacometro.setThreshold(2500);
        radialTacometro.setSectionsVisible(true);
        radialTacometro.setBackgroundColor(BackgroundColor.BLUE);
        Color color = new Color(120,0,0);
        final Section[] SECTIONS2 = {new Section(ralenti-100,ralenti+100,color), new Section(crucero-250, crucero + 250,color)};
        radialTacometro.setAreas(SECTIONS2);
        radialTacometro.setAreasVisible(true);
        this.repaint();

    }

    
}//end of class DialogoCliclosAceleracion
