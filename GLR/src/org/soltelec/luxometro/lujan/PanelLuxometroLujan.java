/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro.lujan;

import javax.swing.JButton;
import org.soltelec.componenteluces.LuzExploradora;
import org.soltelec.componenteluces.LuzBaja;
import org.soltelec.componenteluces.LuzAlta;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import java.awt.Window;
import java.awt.Font;
import javax.swing.JFrame;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import static java.awt.GridBagConstraints.*;

/**
 *Clase para implementar la interfaz gráfica del luxometro
 *  *
 * @author Usuario
 */
public class PanelLuxometroLujan extends JPanel {

   //private Led ledLuzBajaDerecha, ledLuzAltaDerecha,ledLuzBajaIzquierda,ledLuzAltaIzquierda;

    private LuzAlta altaDerecha,altaIzquierda;
    private LuzBaja bajaDerecha,bajaIzquierda;
    private LuzExploradora exploradoraUno,exploradoraDos,exploradoraTres,exploradoraCuatro,exploradoraCinco,exploradoraSeis;
    //private Led ledUno,ledDos,ledTres,ledCuatro,ledCinco,ledSeis;
    private JLabel labelTitulo;
    //private Led[] leds;
    private LuzExploradora[] exploradoras;
    private JLabel labelMensaje;
    private JLabel labelLuzBajaDerecha,labelLuzAltaDerecha,labelLuzBajaIzquierda,labelLuzAltaIzquierda;
    private DisplayMulti display;
    //private JSlider sliderAnguloDerecha,sliderAnguloIzquierda;
    private JButton buttonCancelar;
    public PanelLuxometroLujan(){
        crearComponentes();
        ponerComponentes();
    }

    private void crearComponentes() 
    {
        Font f = new Font(Font.SERIF,Font.PLAIN,38);
        
        bajaDerecha = new LuzBaja();
        bajaIzquierda = new LuzBaja();
        altaDerecha = new LuzAlta();
        altaIzquierda = new LuzAlta();
        
        labelLuzAltaIzquierda = new JLabel("Izquierda Alta");
        labelLuzAltaDerecha = new JLabel("DerechaAlta");
        labelLuzBajaIzquierda = new JLabel("Izquierda Baja");
        labelLuzBajaDerecha = new JLabel("Derecha Baja");
        labelTitulo = new JLabel(" PRUEBA DE LUCES ");
        
        JLabel[] etiquetas = {labelLuzAltaIzquierda,labelLuzAltaDerecha,
                                labelLuzBajaIzquierda,labelLuzBajaDerecha,labelTitulo};
        for(JLabel etiqueta: etiquetas)
        {
            etiqueta.setFont(f);
        }
        configurarLabelMensaje();

        // Exploradoras
        exploradoraUno = new LuzExploradora();
        exploradoraDos = new LuzExploradora();
        exploradoraTres = new LuzExploradora();
        exploradoraCuatro = new LuzExploradora();
        exploradoraCinco = new LuzExploradora();
        exploradoraSeis = new LuzExploradora();

            
        exploradoras = new LuzExploradora[]{exploradoraUno,exploradoraDos,exploradoraTres,exploradoraCuatro,exploradoraCinco,exploradoraSeis};
        for(LuzExploradora l: exploradoras){
            l.setPreferredSize(new Dimension(50,50));
            l.setVisible(false);
        }
        display = new DisplayMulti();
        display.setDigitalFont(true);
        display.setLcdValue(0);
        display.setLcdDecimals(0);
        display.setValueCoupled(false);
        display.setLcdColor(LcdColor.STANDARD_LCD);
        display.setLcdUnitString("s");
        display.setPreferredSize(new Dimension(120,100));
        buttonCancelar = new JButton("Cancelar");
        buttonCancelar.setForeground(Color.red);
    }//end of crearComponentes

    private void ponerComponentes() 
    {

        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.setLayout(bag);
        c.gridwidth = REMAINDER;
        filaColumna(0,0,c);
        this.add(labelMensaje,c);
        
        //añadidas las exploradoras
        JPanel panelExploradoras = new JPanel();
        for(LuzExploradora l: exploradoras){
            panelExploradoras.add(l);
        }
        c.gridwidth = REMAINDER;
        filaColumna(1,0,c);
        this.add(panelExploradoras,c);
        c.gridwidth = 1;
        filaColumna(2,0,c);
        
        this.add(labelLuzAltaIzquierda,c);
        filaColumna(2,2,c);
        
        this.add(labelLuzAltaDerecha,c);
        c.gridheight = 2;
        filaColumna(3,0,c);
        
        JPanel panelEnvoltor = new JPanel();
        panelEnvoltor.add(altaIzquierda);
        this.add(panelEnvoltor,c);
        filaColumna(3,2,c);
        
        JPanel panelEnvoltor1 = new JPanel();
        panelEnvoltor1.add(altaDerecha);
        this.add(panelEnvoltor1,c);
        c.gridheight = 1;
        filaColumna(5,0,c);
        this.add(labelLuzBajaIzquierda,c);
        filaColumna(5,2,c);
        this.add(labelLuzBajaDerecha,c);
        c.gridheight = 2;
        filaColumna(6,0,c);
        JPanel panelEnvoltor2 = new JPanel();
        panelEnvoltor2.add(bajaIzquierda);
        this.add(panelEnvoltor2,c);
        filaColumna(6,2,c);
        JPanel panelEnvoltor3 = new JPanel();
        panelEnvoltor3.add(bajaDerecha);
        this.add(panelEnvoltor3,c);
        //c.fill = BOTH;
        c.gridheight = 1;
        filaColumna(3,1,c);
        this.add(display,c);
        filaColumna(7,1,c);
        this.add(buttonCancelar,c);//quedo ok
    }//end of method ponerComponentes

    private void filaColumna(int fila, int columna,GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;
    }//end of filaColumna

    private void configurarLabelMensaje() {
        labelMensaje = new JLabel("INICIO DE LA PRUEBA");
        labelMensaje.setFont(new Font("Serif", Font.PLAIN,50));
        labelMensaje.setForeground(Color.RED);
    }

     public void habilitar(int numero){
        for (int i= 0; i < numero; i++ ){
                exploradoras[i].setVisible(true);
        }
    }

    public static void main(String args[]){

        JFrame app = new JFrame();
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        PanelLuxometroLujan panel = new PanelLuxometroLujan();
        app.add(panel);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread t  = new Thread(new HiloPruebaLuxometroLujan(panel,1));
//        t.start();
        app.setVisible(true);
    }

    public JLabel getLabelLuzAltaDerecha() {
        return labelLuzAltaDerecha;
    }

    public JLabel getLabelLuzAltaIzquierda() {
        return labelLuzAltaIzquierda;
    }

    public JLabel getLabelLuzBajaDerecha() {
        return labelLuzBajaDerecha;
    }

    public JLabel getLabelLuzBajaIzquierda() {
        return labelLuzBajaIzquierda;
    }

    

    
    public JLabel getLabelMensaje() {
        return labelMensaje;
    }

    

    public DisplayMulti getDisplay() {
        return display;
    }

    public LuzAlta getAltaDerecha() {
        return altaDerecha;
    }

    public LuzAlta getAltaIzquierda() {
        return altaIzquierda;
    }

    public LuzBaja getBajaDerecha() {
        return bajaDerecha;
    }

    public LuzBaja getBajaIzquierda() {
        return bajaIzquierda;
    }

    public LuzExploradora[] getExploradoras() {
        return exploradoras;
    }

    public JButton getButtonCancelar() {
        return buttonCancelar;
    }

    

    
public void cerrar() {
   for(LuzExploradora l: exploradoras)
       l.setBlinking(false);
       altaDerecha.setBlinking(false);
       altaIzquierda.setBlinking(false);
       bajaDerecha.setBlinking(false);
       bajaIzquierda.setBlinking(false);
        Window w = SwingUtilities.getWindowAncestor(this);
        w.dispose();
    }    
  }//end of class luxometro
