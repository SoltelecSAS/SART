/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.Insets;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import java.awt.Dimension;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import eu.hansolo.steelseries.extras.Led;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import static java.awt.GridBagConstraints.*;


/**
 *Version mejorada con displays digitales y leds para
 * la parte de servicio cambiar aqui para que se vea
 * mejor usando las nuevas versiones de SteelSeries
 * @author Usuario
 */
public class PanelMedidasSteel extends PanelTextura {
    JPanel panelBanderas;//en este panel voy a poner la fila de las bandeeras
    JProgressBar barraCalentamiento;//esa hay que personalizarla
    JPanel panelBotones;//en este panel se pondrán los botones correspondientes
    private DisplayMulti displayHC,displayCO,displayCO2,displayO2;
    private DisplayMulti displayRPM;
    private DisplayMulti displayTAceite;
    private DisplayMulti displayPresion;
    private DisplayMulti displayNox;
    private Led ledBajoFlujo,ledNoExacta,ledCero,ledhiHC,ledCondensacion;
    private Led ledCalentamiento,ledHCRange,ledCORange,ledCO2Range,ledO2Range;
    private Led ledNOxRange,ledInterna;
    //agregar 6 leds para banderas de entrada salida 
    private Led ledBombaMuestra, ledBombaDrenaje,ledSolenoideUno;
    private Led ledSolenoideDos, ledCalSol1,ledCalSol2;
    private JLabelPersonalizada labelMensaje;
    private JLabelPersonalizada labelTitulo;
    private  JLabel labelBajoFlujo;
    private  JLabel labelNoExacta;
    private  JLabel labelCero;
    private  JLabel labelhiHC;
    private  JLabel labelCondensacion;
    private  JLabel labelCalentamiento;
    private  JLabel labelHCRange;
    private  JLabel labelCORange;
    private  JLabel labelCO2Range;
    private  JLabel labelO2Range;
    private  JLabel labelNOxRange;
    private  JLabel labelInterna;
    //etiquetas para los leds de entrada salida:
    private JLabel labelBombaMuestra, labelBombaDrenaje, labelSolenoideUno;
    private JLabel labelSolenoideDos, labelCalSol1,labelCalSol2;
    
    private JLabelAzul labelInfo;
//    private JButton buttonSol1,buttonSol2,buttonBombaMuestras,buttonBombaDrenaje;
//    private JButton buttonCalSol1,buttonCalSol2;//Demas botones necesarios

    //Clase BancoSensors para hacer todas las operaciones
    //Clase WorkerDatos que es la que pide continuamente los datos a la maquina
    //Clase WorkerCalentamiento que es la que inicializa la barra de tareas
    public PanelMedidasSteel(){
        //inicializar el panel de las banderas
        inicializarComponentes();
        JPanel panelTAceite,panelPresion,panelCO2,panelO2,panelNox;
        Dimension dimension = new Dimension(270,80);
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        this.setLayout(bag);
        //Añadir un titulo
        //fila 1 columna 1

        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridx = 0;c.gridy = 0;
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        c.fill = HORIZONTAL;
        labelTitulo = new JLabelPersonalizada("MEDICION BANCO");
        this.add(labelTitulo,c);
        c.gridwidth = 1;
        c.fill = NONE;
        filaColumna(1,0,c);
        this.add(new JLabelPersonalizada("HC"),c);
        c.gridx = 1;c.gridy = 1;
        c.fill = BOTH;
        JPanel panelEnvoltor = new JPanel(new BorderLayout());
        panelEnvoltor.setPreferredSize(dimension);
        panelEnvoltor.add(displayHC);panelEnvoltor.setOpaque(false);
        this.add(panelEnvoltor,c);
        filaColumna(2, 0, c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("CO"),c);
        c.fill = BOTH;
        filaColumna(2,1,c);
        JPanel panelEnvoltorCO = new JPanel(new BorderLayout());
        panelEnvoltorCO.setPreferredSize(dimension);
        panelEnvoltorCO.add(displayCO);panelEnvoltorCO.setOpaque(false);
        this.add(panelEnvoltorCO,c);
        filaColumna(3,0,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("CO2"),c);
        c.fill = BOTH;
        filaColumna(3,1,c);
        JPanel panelEnvoltorCO2 = new JPanel(new BorderLayout());
        panelEnvoltorCO2.setPreferredSize(dimension);
        panelEnvoltorCO2.add(displayCO2);
        panelEnvoltorCO2.setOpaque(false);
        this.add(panelEnvoltorCO2,c);
        filaColumna(4,0,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("O2"),c);
        c.fill = BOTH;
        JPanel panelEnvoltorO2 = new JPanel(new BorderLayout());
        panelEnvoltorO2.setPreferredSize(dimension);
        panelEnvoltorO2.add(displayO2);
        panelEnvoltorO2.setOpaque(false);
        filaColumna(4,1,c);
        this.add(panelEnvoltorO2,c);
        filaColumna(1,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("TºAceite"),c);
        c.fill = BOTH;
        filaColumna(1,3,c);
        panelTAceite = new JPanel(new BorderLayout());
        panelTAceite.setPreferredSize(dimension);
        panelTAceite.add(displayTAceite);panelTAceite.setOpaque(false);
        this.add(panelTAceite,c);
        filaColumna(2,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("RPM"),c);
        //c.fill = BOTH;
        c.fill = NONE;
        filaColumna(2,3,c);
        c.gridheight = 1;
        JPanel panelRPM = new JPanel(new BorderLayout());
        panelRPM.setPreferredSize(dimension);
        panelRPM.add(displayRPM);panelRPM.setOpaque(false);
        c.fill = BOTH;
        this.add(panelRPM,c);
        filaColumna(3,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("Presion"),c);
        filaColumna(3,3,c);
        c.fill = BOTH;
        panelPresion = new JPanel(new BorderLayout());
        panelPresion.setPreferredSize(dimension);
        panelPresion.add(displayPresion);panelPresion.setOpaque(false);
        this.add(panelPresion,c);
        filaColumna(4,2,c);
        c.fill = NONE;
        c.gridheight = 1;
        this.add(new JLabelPersonalizada("NOx"),c);
        c.fill = BOTH;
        filaColumna(4,3,c);
        panelNox = new JPanel(new BorderLayout());
        panelNox.setPreferredSize(dimension);
        panelNox.setOpaque(false);
        panelNox.add(displayNox);
        this.add(panelNox,c);
        filaColumna(5,0,c);
//        //poner el panel de las banderas
        c.fill = NONE;
        c.anchor = CENTER;
        c.gridwidth = REMAINDER;
        c.gridheight = 2;
        this.add(panelBanderas,c);
        this.setBackground(Color.orange);
        filaColumna(7,0,c);
        ///c.weighty = 0.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        labelMensaje = new JLabelPersonalizada("Proceso");
        c.gridheight =1;
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        this.add(labelMensaje,c);
        filaColumna(8,0,c);
        c.gridwidth = REMAINDER;
        c.fill = BOTH;
        barraCalentamiento = new JProgressBar();
        barraCalentamiento.setStringPainted(true);
        this.add(barraCalentamiento,c);
        filaColumna(9,0,c);
        c.anchor = FIRST_LINE_START;
        c.fill = NONE;
        this.add(labelInfo,c);
    }//end of class PanelMedidas

    public DisplayMulti getDisplayCO() {
        return displayCO;
    }

    public void setDisplayCO(DisplayMulti displayCO) {
        this.displayCO = displayCO;
    }

    public DisplayMulti getDisplayCO2() {
        return displayCO2;
    }

    public void setDisplayCO2(DisplayMulti displayCO2) {
        this.displayCO2 = displayCO2;
    }

    public DisplayMulti getDisplayHC() {
        return displayHC;
    }

    public void setDisplayHC(DisplayMulti displayHC) {
        this.displayHC = displayHC;
    }

    public DisplayMulti getDisplayNox() {
        return displayNox;
    }

    public void setDisplayNox(DisplayMulti displayNox) {
        this.displayNox = displayNox;
    }

    public DisplayMulti getDisplayO2() {
        return displayO2;
    }

    public void setDisplayO2(DisplayMulti displayO2) {
        this.displayO2 = displayO2;
    }

    public Led getLedBajoFlujo() {
        return ledBajoFlujo;
    }

    public void setLedBajoFlujo(Led ledBajoFlujo) {
        this.ledBajoFlujo = ledBajoFlujo;
    }

    public Led getLedCO2Range() {
        return ledCO2Range;
    }

    public void setLedCO2Range(Led ledCO2Range) {
        this.ledCO2Range = ledCO2Range;
    }

    public Led getLedCORange() {
        return ledCORange;
    }

    public void setLedCORange(Led ledCORange) {
        this.ledCORange = ledCORange;
    }

    public Led getLedCalentamiento() {
        return ledCalentamiento;
    }

    public void setLedCalentamiento(Led ledCalentamiento) {
        this.ledCalentamiento = ledCalentamiento;
    }

    public Led getLedCero() {
        return ledCero;
    }

    public void setLedCero(Led ledCero) {
        this.ledCero = ledCero;
    }

    public Led getLedCondensacion() {
        return ledCondensacion;
    }

    public void setLedCondensacion(Led ledCondensacion) {
        this.ledCondensacion = ledCondensacion;
    }

    public Led getLedHCRange() {
        return ledHCRange;
    }

    public void setLedHCRange(Led ledHCRange) {
        this.ledHCRange = ledHCRange;
    }

    public Led getLedInterna() {
        return ledInterna;
    }

    public void setLedInterna(Led ledInterna) {
        this.ledInterna = ledInterna;
    }

    public Led getLedNOxRange() {
        return ledNOxRange;
    }

    public void setLedNOxRange(Led ledNOxRange) {
        this.ledNOxRange = ledNOxRange;
    }

    public Led getLedNoExacta() {
        return ledNoExacta;
    }

    public void setLedNoExacta(Led ledNoExacta) {
        this.ledNoExacta = ledNoExacta;
    }

    public Led getLedO2Range() {
        return ledO2Range;
    }

    public void setLedO2Range(Led ledO2Range) {
        this.ledO2Range = ledO2Range;
    }

    public Led getLedhiHC() {
        return ledhiHC;
    }

    public void setLedhiHC(Led ledhiHC) {
        this.ledhiHC = ledhiHC;
    }

    public DisplayMulti getDisplayTAceite() {
        return displayTAceite;
    }

    public void setDisplayTAceite(DisplayMulti linearTAceite) {
        this.displayTAceite = linearTAceite;
    }

    public DisplayMulti getDisplayPresion() {
        return displayPresion;
    }

    public void setDisplayPresion(DisplayMulti radialPresion) {
        this.displayPresion = radialPresion;
    }

    public DisplayMulti getDisplayRPM() {
        return displayRPM;
    }

    public void setDisplayRPM(DisplayMulti radialRPM) {
        this.displayRPM = radialRPM;
    }



   public JProgressBar getBarraCalentamiento() {
        return barraCalentamiento;
    }

    public JLabelPersonalizada getLabelMensaje() {
        return labelMensaje;
    }

    public void setLabelMensaje(JLabelPersonalizada labelMensaje) {
        this.labelMensaje = labelMensaje;
    }

    public void setLabelTitulo(JLabelPersonalizada labelTitulo) {
        this.labelTitulo = labelTitulo;
    }

    public JLabelPersonalizada getLabelTitulo() {
        return labelTitulo;
    }

    public Led getLedBombaDrenaje() {
        return ledBombaDrenaje;
    }

    public Led getLedBombaMuestra() {
        return ledBombaMuestra;
    }

    public Led getLedCalSol1() {
        return ledCalSol1;
    }

    public Led getLedCalSol2() {
        return ledCalSol2;
    }

    public Led getLedSolenoideDos() {
        return ledSolenoideDos;
    }

    public Led getLedSolenoideUno() {
        return ledSolenoideUno;
    }

    
    




    public final void filaColumna(int fila,int columna,GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;

    }

    private void inicializarComponentes(){
        //inicializar los leds
        this.setBackground(Color.BLACK);
        panelBanderas = new JPanel();
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,10,5,10);
        panelBanderas.setLayout(bag);

        panelBanderas.setOpaque(false);
        panelBanderas.setBackground(Color.orange);
        ledBajoFlujo = new Led();
        ledCO2Range = new Led();
        ledCORange = new Led();
        ledCalentamiento = new Led();
        ledCero = new Led();
        ledCondensacion = new Led();
        ledHCRange = new Led();
        ledInterna = new Led();
        ledNOxRange = new Led();
        ledNoExacta = new Led();
        ledO2Range = new Led();
        ledhiHC = new Led();
        
        
        
        
        Led[] leds = { ledBajoFlujo,ledCO2Range,ledNoExacta,ledCORange,ledCalentamiento,ledCero,ledCondensacion
                        ,ledHCRange,ledInterna,ledNOxRange,ledO2Range,ledhiHC};
       Dimension dimension = new Dimension(10,10);
        
        int columna = 0;
        for(Led l: leds){
            l.setPreferredSize(dimension);
            filaColumna(0,columna++,c);
            panelBanderas.add(l,c);
        }//end for

        labelBajoFlujo = new JLabel("LF",JLabel.CENTER);
        labelCO2Range = new JLabel("CO2Range",JLabel.CENTER);
        labelNoExacta = new JLabel("NoEx",JLabel.CENTER);
        labelCORange = new JLabel("CORange",JLabel.CENTER);
        labelCalentamiento = new JLabel("Calent",JLabel.CENTER);
        labelCero = new JLabel("Cero",JLabel.CENTER);
        labelCondensacion = new JLabel("Cond",JLabel.CENTER);
        labelHCRange = new JLabel("HCRange",JLabel.CENTER);
        labelInterna = new JLabel("Int",JLabel.CENTER);
        labelNOxRange = new JLabel("NOxRAnge",JLabel.CENTER);
        labelO2Range = new JLabel("O2Range",JLabel.CENTER);
        labelhiHC = new JLabel("hiHC",JLabel.CENTER);
        //Inicializar los displays
        JLabel labels[] = {labelBajoFlujo,labelCO2Range,labelNoExacta,
                            labelCORange,labelCalentamiento,labelCero,
                            labelCondensacion,labelHCRange,labelInterna,
                            labelNOxRange,labelO2Range,labelhiHC};
        columna = 0;
        for(JLabel lbl: labels){
           filaColumna(1,columna++,c);
           panelBanderas.add(lbl,c);
        }
        //agregar los leds de Entrada salida
        ledBombaMuestra = new Led();
        ledBombaDrenaje = new Led();
        ledSolenoideUno = new Led();
        ledSolenoideDos = new Led();
        ledCalSol1 = new Led();
        ledCalSol2 = new Led();
        
        Led[] ledsEntradaSalida = {ledBombaMuestra,ledBombaDrenaje,ledSolenoideUno,ledSolenoideDos,
                                    ledCalSol1,ledCalSol2};
        columna = 3;
        for(Led led: ledsEntradaSalida){//lo pone en la fila 3
            led.setLedColor(LedColor.GREEN_LED);
            filaColumna(2,columna++,c);
            panelBanderas.add(led,c);
        }
        
        //agregar las Etiquetas de Entrada Salida
        
        labelBombaMuestra = new JLabel("Bmb1",JLabel.CENTER);
        labelBombaDrenaje = new JLabel("Bmb2",JLabel.CENTER);
        labelSolenoideUno = new JLabel("Sol1",JLabel.CENTER);
        labelSolenoideDos = new JLabel("Sol2",JLabel.CENTER);
        labelCalSol1 = new JLabel("CSol1",JLabel.CENTER);
        labelCalSol2 = new JLabel("CSol2",JLabel.CENTER);
        
        JLabel[]  etiquetasES = {labelBombaMuestra,labelBombaDrenaje,labelSolenoideUno,
                                    labelSolenoideDos,labelCalSol1,labelCalSol2};
        columna = 3; 
        for(JLabel etiqueta: etiquetasES){
            
            filaColumna(3,columna++,c);
            panelBanderas.add(etiqueta,c);
            
        }
        displayCO = new DisplayMulti();
        displayCO.setLcdUnitString("%");
        displayCO.setLcdDecimals(2);
        displayCO2 = new DisplayMulti();
        displayCO2.setLcdUnitString("%");
        displayHC = new DisplayMulti();
        displayHC.setLcdUnitString("ppm");
        displayNox = new DisplayMulti();

        displayRPM = new DisplayMulti();
        displayRPM.setLcdUnitString("RPM");

        displayPresion = new DisplayMulti();
        displayPresion.setLcdUnitString("mBar");

        displayTAceite = new DisplayMulti();
        displayTAceite.setLcdUnitString("°C");

        displayO2 = new DisplayMulti();
        displayO2.setLcdDecimals(2);
        displayO2.setLcdUnitString("%");
        DisplayMulti[] displays = {displayCO,displayCO2,displayHC,displayNox,displayO2,displayTAceite,
                                    displayRPM,displayPresion};

        for(DisplayMulti d: displays){
            //d.init(200,50);
            d.setDigitalFont(true);
            d.setLcdColor(LcdColor.ORANGE_LCD);
        }

        labelInfo = new JLabelAzul("");

    }

    
    public void mostrarInfo(String cadena){
         labelInfo.setSize(600,300);
        labelInfo.setText(cadena);
        
    }
    
    


    public static void main(String args[]){
        JFrame frame = new JFrame();
        PanelMedidasSteel panel = new PanelMedidasSteel();
        frame.add(panel);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(450,450);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



}
