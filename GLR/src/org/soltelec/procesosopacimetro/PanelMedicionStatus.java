/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import eu.hansolo.steelseries.extras.Led;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.soltelec.procesosbanco.JLabelPersonalizada;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import static java.awt.GridBagConstraints.*;
import org.soltelec.procesosbanco.JLabelAzul;

/**
 * Panel para la medicion y el estatus del opacimetro
 * solo contiene elementos de interfaz no contiene botones
 * 12/02/2011 adicionados displays digitales, fondo del panel
 * siguiente modificacion poner displays de temperatura
 *
 * @author Usuario
 */
public class PanelMedicionStatus extends JPanel{
    
    private JLabelPersonalizada /*labelTitulo,*/labelOpacidad,labelTempGas,labelTempTubo,labelDensidad;
    private JLabelPersonalizada labelStatus;
    private DisplayMulti displayOpacidad, displayTempGas, displayTempTubo, displayDensidad;
    private Led ledTempAmb, ledTempDet, ledTempTubo, ledFueraRango, ledCalibracion;
    private Led ledCleanWindow, ledArmado, ledDisp, ledVentilador, ledTermistor, ledEEprom, ledVoltaje , ledResetProcesador,ledUmbralGas, ledRealTimeData,ledTermistorDañado;
    private JPanel panelBanderas;
    private JTextArea textArea;
    private JLabel labelTempAmb;
    private JLabel labelTempDetector;
    private JLabel labelLedTempTubo;
    private JLabel labelFueraRango;
    private JLabel labelCalibracion;
    private JLabel labelCleanWindow;
    private JLabel labelArmado;
    private JLabel labelDisp;
    private JLabel labelVentilador;
    private JLabel labelTermistor;
    private JLabel labelEEprom;
    private JLabel labelVoltaje;
    private JLabel labelResetProcesador;
    private JLabel labelUmbralGas;
    private JLabel labelRealTimeData;
    private JLabelAzul labelInformacion;
    

    public PanelMedicionStatus() {
        inicializarInterfaz();
    }

    private void inicializarCampos() {

        Font f = new Font(Font.SERIF,Font.PLAIN,38);
        //labelTitulo = new JLabelPersonalizada("Opacimetro: ");
        labelOpacidad = new JLabelPersonalizada("Opacidad N");
        labelDensidad = new JLabelPersonalizada("Dens. Gas : K");
        labelTempGas = new JLabelPersonalizada("Temp Gas:");
        labelTempTubo = new JLabelPersonalizada("Temp Tubo:");
        labelStatus = new JLabelPersonalizada("OPACIMETRO DESCONECTADO");

        //Iniciar las etiquetas
        labelTempAmb = new JLabel("TAmb");
        labelTempDetector = new JLabel("TDet");
        labelLedTempTubo = new JLabel("TTub");
        labelFueraRango = new JLabel("NFueRan");
        labelCalibracion = new JLabel("Cal");
        labelCleanWindow = new JLabel("C Wdw");
        labelArmado = new JLabel("TrigArm");
        labelDisp = new JLabel("TrigDisp");
        labelVentilador = new JLabel("Vent.");
        labelTermistor = new JLabel("Tmstr");
        labelEEprom = new JLabel("EE");
        labelVoltaje = new JLabel("V");
        labelResetProcesador = new JLabel("ResProc");
        labelUmbralGas = new JLabel("UmbGas");
        labelRealTimeData = new JLabel("RTData");
        labelInformacion = new JLabelAzul("Info");

        //Inicializar los leds
        ledTempAmb = new Led();
        ledTempDet = new Led();
        ledTempTubo = new Led();
        ledFueraRango = new Led();
        ledCalibracion = new Led();
        ledCleanWindow = new Led();
        ledArmado = new Led();
        ledDisp = new Led();
        ledVentilador = new Led();
        ledTermistor = new Led();
        ledEEprom = new Led();
        ledVoltaje = new Led();
        ledResetProcesador = new Led();
        ledUmbralGas = new Led();
        ledTermistorDañado = new Led();
        ledRealTimeData = new Led();

        //El color del led
        Led[] leds = {ledTempAmb,ledTempDet,ledTempTubo,ledFueraRango,
                        ledCalibracion,ledCleanWindow,ledArmado,ledDisp,
                        ledVentilador,ledTermistor,ledEEprom,ledVoltaje,
                        ledResetProcesador,ledUmbralGas,ledRealTimeData,ledTermistorDañado};

        for(Led l : leds){
            l.setLedColor(LedColor.GREEN_LED);
        }//end for
        //Inicializar los campos de texto

        displayOpacidad = new DisplayMulti();
        displayDensidad = new DisplayMulti();
        displayTempGas = new DisplayMulti();
        displayTempTubo = new DisplayMulti();

        DisplayMulti[] displays = {displayOpacidad,displayDensidad,displayTempGas,displayTempTubo};

        for(DisplayMulti dm : displays){
            dm.setLcdColor(LcdColor.REDDARKRED_LCD);
            dm.setDigitalFont(true);
            dm.setPreferredSize(new Dimension(80,80));
        }//end for
        displayOpacidad.setLcdUnitString("%");
        displayDensidad.setLcdUnitString("m-1");
        displayTempGas.setLcdUnitString("°C");
        displayTempTubo.setLcdUnitString("°C");

        textArea = new JTextArea();
        textArea.setEditable(false);
    }//end of method inicializarCampos

    public JLabelAzul getLabelInformacion() {
        return labelInformacion;
    }

    public void setLabelInformacion(JLabelAzul labelInformacion) {
        this.labelInformacion = labelInformacion;
    }

    private void inicializarInterfaz() {
            this.setBackground(Color.orange);
            inicializarCampos();
            ponerComponentes();

    }//end of method inicializarInterfaz

    private void ponerComponentes() {
        Dimension dimension = new Dimension(200,80);
        GridBagLayout bag = new GridBagLayout();
        setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();
        //ocupar todo el espacio horizontal
        c.weightx = 0.5;
        //reclamar el espacio vertical
        c.weighty = 1.0;
        //espacios entre componentes
        c.insets = new Insets(10,5,10,5);
       //El titulo
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        filaColumna(0,0,c);
        //add(labelTitulo,c);
        c.gridwidth = 1;
        // Opacidad
        filaColumna(1,0,c);
        add(labelOpacidad,c);
        c.fill = BOTH;
        filaColumna(1,1,c);
        JPanel panelOpacidad = new JPanel(new BorderLayout());
        panelOpacidad.setPreferredSize(dimension);
        panelOpacidad.add(displayOpacidad);
        add(displayOpacidad,c);
        //Densidad
        c.fill= NONE;
        filaColumna(2,0,c);
        add(labelDensidad,c);
        c.fill= BOTH;
        filaColumna(2,1,c);
        add(displayDensidad,c);
        //Temperatura Gas
        c.fill = NONE;
        filaColumna(3,0,c);
        add(labelTempGas,c);
        c.fill = BOTH;
        filaColumna(3,1,c);
        add(displayTempGas,c);
        //Temperatura Tubo
        c.fill = NONE;
        filaColumna(4,0,c);
        add(labelTempTubo,c);
        c.fill = BOTH;
        filaColumna(4,1,c);
        add(displayTempTubo,c);
        
        //ocupe todo
        c.gridwidth = REMAINDER;
        c.fill = BOTH;
        //c.gridy = 0;
        filaColumna(5,0,c);
        configurarPanelBanderas();
        //Poner los leds
        add(panelBanderas,c);
        c.fill = NONE;
        filaColumna(6,0,c);
        add(labelStatus,c);
        c.fill = BOTH;
        filaColumna(7,0,c);
        //add(new JScrollPane(textArea),c);
        add(labelInformacion);
    }//end of method ponerComponentes

    private void configurarPanelBanderas(){
        //GridLayout grid = new GridLayout(2,7,5,5);
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        panelBanderas = new JPanel(bag);
        panelBanderas.setBackground(Color.orange);
        Led[] arregloLeds = {ledTermistor,ledEEprom,ledUmbralGas,ledVentilador
                             ,ledDisp,ledArmado,ledCleanWindow,ledCalibracion
                             ,ledResetProcesador,ledFueraRango,ledVoltaje,
                             ledTempTubo,ledTempDet,ledTempAmb};
        int columna = 0;
        for(Led l: arregloLeds){
            filaColumna(0,columna++,c);
            panelBanderas.add(l,c);
        }//end for

        JLabel[] etiquetas = {labelTermistor,labelEEprom,labelUmbralGas,labelVentilador
                            ,labelDisp,labelArmado,labelCleanWindow,labelCalibracion
                            ,labelResetProcesador,labelFueraRango,labelVoltaje,
                            labelLedTempTubo,labelTempDetector,labelTempAmb};
        columna = 0;
        for(JLabel lbl: etiquetas){
            lbl.setHorizontalAlignment(JLabel.HORIZONTAL);
            filaColumna(1,columna++,c);
            panelBanderas.add(lbl,c);
        }//end for

    }//end of method configurarPanelBanderas

   private void filaColumna(int fila, int columna, GridBagConstraints c){
       c.gridy = fila;
       c.gridx = columna;

   }//end of method filaColumna

    public DisplayMulti getDisplayDensidad() {
        return displayDensidad;
    }

    public DisplayMulti getDisplayOpacidad() {
        return displayOpacidad;
    }

    public DisplayMulti getDisplayTempGas() {
        return displayTempGas;
    }

    public DisplayMulti getDisplayTempTubo() {
        return displayTempTubo;
    }

    public Led getLedArmado() {
        return ledArmado;
    }

    public Led getLedCalibracion() {
        return ledCalibracion;
    }

    public Led getLedCleanWindow() {
        return ledCleanWindow;
    }

    public Led getLedDisp() {
        return ledDisp;
    }

    public Led getLedEEprom() {
        return ledEEprom;
    }

    public Led getLedFueraRango() {
        return ledFueraRango;
    }

    public Led getLedTempAmb() {
        return ledTempAmb;
    }

    public Led getLedTempDet() {
        return ledTempDet;
    }

    public Led getLedTempTubo() {
        return ledTempTubo;
    }

    public Led getLedTermistor() {
        return ledTermistor;
    }

    public Led getLedVentilador() {
        return ledVentilador;
    }

    public Led getLedVoltaje() {
        return ledVoltaje;
    }

    public Led getLedResetProcesador() {
        return ledResetProcesador;
    }

    public Led getLedUmbralGas() {
        return ledUmbralGas;
    }

    public Led getLedRealTimeData() {
        return ledRealTimeData;
    }

    public Led getLedTermistorDañado() {
        return ledTermistorDañado;
    }    

    public JTextArea getTextArea() {
        return textArea;
    }

    public JLabelPersonalizada getLabelStatus() {
        return labelStatus;
    }    

}//end of class Panel Medicion Status