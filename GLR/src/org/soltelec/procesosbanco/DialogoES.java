/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.soltelec.util.UtilGasesModelo;

/**
 *El dialogo que sale al dar E/s En el programa de servicio
 * @author Usuario
 */
public class DialogoES extends JDialog implements ActionListener{
    private short estado;
    private Led ledCalSol1, ledCalSol2,ledAirSol1,ledAirSol2,ledSamplePump,ledDrainPump;
    //private Led[] leds;
    private JLabel[] etiquetas = {new JLabel("CalSol1"), new JLabel("CalSol2")
                                  ,new JLabel("AirSol1"),new JLabel("AirSol2") 
                                  ,new JLabel("SamplePump"),new JLabel("DrainPump")};
    private JPanel panelLeds;
    private JButton buttonAceptar;

    public DialogoES(Window w,short theEstado){
        super(w);//
        this.estado = theEstado;        
        inicializarInterfaz();
    }//end of constructor

    //Pone las interfaces correspondientes e inicializa los componentes adecuadamente
    private void inicializarInterfaz() {
        //GridLayout grid = new GridLayout(2,6,5,5);// 2 filas seis columnas
        panelLeds = new JPanel();
        //Box boxLeds = Box.createHorizontalBox();
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panelLeds.setLayout(bag);
        c.insets = new Insets(10,5,10,5);


        ledCalSol1 = new Led();ledCalSol2 = new Led(); ledAirSol1 = new Led();
        ledAirSol2 = new Led();ledSamplePump = new Led();ledDrainPump = new Led();
        Led[] leds = {ledCalSol1,ledCalSol2,ledAirSol1,ledAirSol2,ledSamplePump,ledDrainPump };
        int columna = 0;
        for(Led l:leds){

           filaColumna(0,columna++,c);
           l.setLedColor(LedColor.GREEN_LED);
           l.setPreferredSize(new Dimension(10,10));
           panelLeds.add(l,c);
        }//end for

       // Box boxEtiquetas = Box.createHorizontalBox();
        columna = 0;
        for(JLabel lbl: etiquetas){
            filaColumna(1,columna++,c);
            lbl.setHorizontalAlignment(JLabel.HORIZONTAL);
            //boxEtiquetas.add(lbl);
            panelLeds.add(lbl,c);
        }//end for

//        Box boxLedEtiqueta = Box.createVerticalBox();
//        boxLedEtiqueta.add(boxLeds);
//        boxLedEtiqueta.add(boxEtiquetas);
        boolean[] booleanEstado = UtilGasesModelo.shortToFlags(estado);
        ledCalSol1.setLedOn(booleanEstado[0]);ledCalSol2.setLedOn(booleanEstado[1]);
        ledAirSol1.setLedOn(booleanEstado[2]);ledAirSol2.setLedOn(booleanEstado[3]);
        ledSamplePump.setLedOn(booleanEstado[4]);ledDrainPump.setLedOn(booleanEstado[5]);
        buttonAceptar = new JButton("Aceptar");
        buttonAceptar.addActionListener(this);
        JPanel panelBoton = new JPanel();
        panelBoton.add(buttonAceptar);

        this.add(panelLeds,BorderLayout.CENTER);
        this.add(panelBoton,BorderLayout.SOUTH);

    }//end of method inicializarInterfaz

    public void actionPerformed(ActionEvent e) {
        this.dispose();
    }//end of method actionPerformed

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }


}//end of class DialogoES
