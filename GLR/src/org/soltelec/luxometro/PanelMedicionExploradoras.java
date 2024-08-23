/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JPanel;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import static java.awt.GridBagConstraints.*;

/**
 *Para mostrar las exploradoras
 * @author Usuario
 */
public class PanelMedicionExploradoras extends JPanel{

        private int numeroLeds = 1;
        private JLabel labelTitulo;
        private JLabel labelMensaje;
        private Led ledUno,ledDos,ledTres,ledCuatro,ledCinco,ledSeis;
        private JButton buttonContinuar;
        private Led[] leds ;

        public PanelMedicionExploradoras(){
            crearComponentes();
            ponerComponentes();

        }//end of constructor

        public PanelMedicionExploradoras (int numeroExploradoras){
            this();
            deshabilitar(6-numeroExploradoras);
        }



    private void crearComponentes() {
        Font f = new Font(Font.SERIF,Font.BOLD,60);
        Font f1 = new Font(Font.SERIF,Font.PLAIN,50);
        labelTitulo = new JLabel("EXPLORADORA NUMERO ");
        labelTitulo.setFont(f);
       
        labelMensaje = new JLabel("Presione Enter en luxometro");
         labelMensaje.setFont(f1);
        ledUno = new Led();
        ledDos = new Led();
        ledTres = new Led();
        ledCuatro = new Led();
        ledCinco = new Led();
        ledSeis = new Led();
        buttonContinuar = new JButton("   Continuar   ");
        buttonContinuar.setFont(f1);
        leds = new Led[]{ledUno,ledDos,ledTres,ledCuatro,ledCinco,ledSeis};
        for(Led l: leds){
            l.setPreferredSize(new Dimension(150,150));
            l.setLedColor(LedColor.GREEN_LED);
        }
    }

    private void ponerComponentes(){
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 0.8;
        c.weighty = 0.2;
        this.setLayout(bag);
        c.insets = new Insets(10,5,10,5);

        c.gridwidth = REMAINDER;
        filaColumna(1,1,c);
        this.add(labelTitulo,c);
        filaColumna(2,1,c);
        this.add(labelMensaje,c);
        //poner los Leds

        JPanel panelEnvoltor = new JPanel();
        for(Led l: leds){
            panelEnvoltor.add(l);
        }

        c.weighty = 0.4;
        filaColumna(3,1,c);
        this.add(panelEnvoltor,c);
        c.weighty = 0.2;
        c.weightx = 0.25;
        filaColumna(4,1,c);
        
        c.fill = VERTICAL;
        this.add(buttonContinuar,c);

    }//end of ponerComponentes

    private void filaColumna(int fila,int columna,GridBagConstraints c ){
        c.gridx = columna;
        c.gridy = fila;
    }

    private void deshabilitar(int numero){
        for (int i= numero; i < leds.length; i++ ){
                leds[i-1].setVisible(false);
        }
    }

    public static void main(String args[]){

        SwingUtilities.invokeLater(new Runnable(){

        public void run() {
                 JFrame app = new JFrame();
                 app.getContentPane().add(new PanelMedicionExploradoras(3));
                 app.setExtendedState(JFrame.MAXIMIZED_BOTH);
                 app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 app.setVisible(true);
            }
        });

    }
}
