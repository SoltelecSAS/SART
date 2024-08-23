/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JFrame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import static java.awt.GridBagConstraints.*;
/**
 *Panel para mostrar las medidas hace parte de la primera version descontinuada
 * no usado en produccion o versiones posteriores
 * @author GerenciaDesarrollo
 */
public class PanelMedidas extends JPanel{

    JPanel panelBanderas;//en este panel voy a poner la fila de las bandeeras
    JProgressBar barraCalentamiento;
    JPanel panelBotones;//en este panel se pondrán los botones correspondientes
    JLabelPersonalizada etiquetas;
    private JTextFieldPersonalizada txtFieldHC,txtFieldCO,txtFieldCO2,txtFieldO2;
    private JTextFieldPersonalizada txtFieldRPM,txtFieldTAceite,txtFieldRH,txtFieldText;
    private JLabelBanderas labelBajoFlujo,labelNoExacta,labelCero,labelhiHC,labelCondensacion;
    private JLabelBanderas labelCalentamiento,labelHCRange,labelCORange,labelCO2Range,labelO2Range;
    private JLabelBanderas labelNOxRange,labelInterna;
    private JLabelPersonalizada labelMensaje,noSaltar;
    private JLabelPersonalizada labelTitulo;
//    private JButton buttonSol1,buttonSol2,buttonBombaMuestras,buttonBombaDrenaje;
//    private JButton buttonCalSol1,buttonCalSol2;//Demas botones necesarios

    //Clase BancoSensors para hacer todas las operaciones
    //Clase WorkerDatos que es la que pide continuamente los datos a la maquina
    //Clase WorkerCalentamiento que es la que inicializa la barra de tareas
    public PanelMedidas(){
        //inicializar el panel de las banderas
        panelBanderas = new JPanel(new FlowLayout());
        panelBanderas.setBackground(Color.orange);
        labelBajoFlujo = new JLabelBanderas("LF");panelBanderas.add(labelBajoFlujo);
        labelNoExacta = new JLabelBanderas("MedNoEx");panelBanderas.add(labelNoExacta);
        labelCero = new JLabelBanderas("Cero");panelBanderas.add(labelCero);
        labelhiHC = new JLabelBanderas("hiHC");panelBanderas.add(labelhiHC);
        labelCondensacion = new JLabelBanderas("Cond");panelBanderas.add(labelCondensacion);
        labelCalentamiento = new JLabelBanderas("Calent");panelBanderas.add(labelCalentamiento);
        labelHCRange = new JLabelBanderas("HCRange");panelBanderas.add(labelHCRange);
        labelCORange = new JLabelBanderas("CORange");panelBanderas.add(labelCORange);
        labelCO2Range = new JLabelBanderas("CO2Range");panelBanderas.add(labelCO2Range);
        labelO2Range = new JLabelBanderas("O2Range");panelBanderas.add(labelO2Range);
        labelNOxRange = new JLabelBanderas("NOxRAnge");panelBanderas.add(labelNOxRange);
        labelInterna = new JLabelBanderas("Int");panelBanderas.add(labelInterna);
        //Cuatro Filas & 6 Columnas

        //Poner los botones
        //Seis Botones usar GridLayout
//        panelBotones = new JPanel();
//        GridLayout grid = new GridLayout(2,6,5,5);//2 filas seis columnas
//        panelBotones.setLayout(grid);
//        buttonBombaDrenaje = new JButton("Drain Pump");buttonBombaMuestras = new JButton("Sample Pump");
//        buttonSol1 = new JButton("Air Sol1"); buttonSol2 = new JButton("Air Sol2");
//        buttonCalSol1 = new JButton("Cal Sol1"); buttonCalSol2 = new JButton("Cal Sol2");
//        //poner el Action Listener
//        panelBotones.add(buttonBombaDrenaje);panelBotones.add(buttonBombaMuestras);
//        panelBotones.add(buttonSol1);panelBotones.add(buttonSol2);
//        panelBotones.add(buttonCalSol1);panelBotones.add(buttonCalSol2);
        //poner los primeros 6 Botones
        
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        txtFieldHC = new JTextFieldPersonalizada("0");txtFieldCO = new JTextFieldPersonalizada("0");
        txtFieldCO2 = new JTextFieldPersonalizada("0");txtFieldO2 = new JTextFieldPersonalizada("0");
        txtFieldRPM = new JTextFieldPersonalizada("0");txtFieldTAceite = new JTextFieldPersonalizada("0");
        txtFieldRH = new JTextFieldPersonalizada("0");txtFieldText = new JTextFieldPersonalizada("0");

        //Añadir un titulo
        //fila 1 columna 1
        //c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridx = 0;c.gridy = 0;
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        //c.fill = HORIZONTAL;
        labelTitulo = new JLabelPersonalizada("MEDICION BANCO");
        this.add(labelTitulo,c);

        c.gridwidth = 1;
        //Añadir la etiqueta y el campo de texto para el HC
       // c.weightx = 0.5;
        c.gridx = 0;c.gridy = 1;
        c.weightx = 0.25;
        c.weighty = 1.0;
        this.add(new JLabelPersonalizada("HC"),c);
        c.fill = HORIZONTAL;
        c.gridx = 1;c.gridy = 1;
        this.add(txtFieldHC,c);
        filaColumna(2, 0, c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("CO"),c);
        c.fill = HORIZONTAL;
        filaColumna(2,1,c);
        this.add(txtFieldCO,c);
        filaColumna(3,0,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("CO2"),c);
        c.fill = HORIZONTAL;
        filaColumna(3,1,c);
        this.add(txtFieldCO2,c);
        filaColumna(4,0,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("O2"),c);
        c.fill = HORIZONTAL;
        filaColumna(4,1,c);
        this.add(txtFieldO2,c);
        filaColumna(1,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("Tº Aceite"),c);
        c.fill = HORIZONTAL;
        filaColumna(1,3,c);
        this.add(txtFieldTAceite,c);
        filaColumna(2,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("RPM"),c);
        c.fill = HORIZONTAL;
        filaColumna(2,3,c);
        this.add(txtFieldRPM,c);
        filaColumna(3,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("Tº Ext"),c);
        c.fill = HORIZONTAL;
        filaColumna(3,3,c);
        this.add(txtFieldText,c);
        filaColumna(4,2,c);
        c.fill = NONE;
        this.add(new JLabelPersonalizada("RH"),c);
        c.fill = HORIZONTAL;
        filaColumna(4,3,c);
        this.add(txtFieldRH,c);
        filaColumna(5,0,c);
        //poner el panel de las banderas
        c.fill = NONE;
        c.anchor = CENTER;
        c.gridwidth = REMAINDER;
        this.add(panelBanderas,c);
        this.setBackground(Color.orange);
        filaColumna(6,0,c);
        c.weighty = 0.0;
        //c.fill = GridBagConstraints.HORIZONTAL;
        labelMensaje = new JLabelPersonalizada("Proceso");
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        this.add(labelMensaje,c);
        filaColumna(7,0,c);
        c.gridwidth = REMAINDER;
        c.fill=BOTH;
        barraCalentamiento = new JProgressBar();
        barraCalentamiento.setStringPainted(true);
        this.add(barraCalentamiento,c);
        //filaColumna(4,0,c);
//        c.fill = HORIZONTAL;
//        c.gridwidth = REMAINDER;
//        filaColumna(7,0,c);
//        this.add(panelBotones,c);
        //Añadir la etiqueta y el campo de texto paa el CO

        ////Añadir la etiqueta y el campo de texto paa el CO2

        //Añadir la etiqueta y el campo de texto paa el O2

        //Añadir la etiqueta y el campo de texto para Temperatura de Aceita

        //Añadir la etiqueta y el campo de texto para RPM
        //Añadir la etiqueta y el campo de texto paa  Humedad
        //Añadir la etiqueta y el campo de texto paa Temperatura Ambiente
        //Añadir la etiqueta y el campo de texto paa el CO


    }//end of class PanelMedidas

    public JLabelPersonalizada getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(JLabelPersonalizada etiquetas) {
        this.etiquetas = etiquetas;
    }

    public JTextFieldPersonalizada getTxtFieldCO() {
        return txtFieldCO;
    }

    public void setTxtFieldCO(JTextFieldPersonalizada txtFieldCO) {
        this.txtFieldCO = txtFieldCO;
    }

    public JTextFieldPersonalizada getTxtFieldCO2() {
        return txtFieldCO2;
    }

    public void setTxtFieldCO2(JTextFieldPersonalizada txtFieldCO2) {
        this.txtFieldCO2 = txtFieldCO2;
    }

    public JTextFieldPersonalizada getTxtFieldHC() {
        return txtFieldHC;
    }

    public void setTxtFieldHC(JTextFieldPersonalizada txtFieldHC) {
        this.txtFieldHC = txtFieldHC;
    }

    public JTextFieldPersonalizada getTxtFieldO2() {
        return txtFieldO2;
    }

    public void setTxtFieldO2(JTextFieldPersonalizada txtFieldO2) {
        this.txtFieldO2 = txtFieldO2;
    }

    public JTextFieldPersonalizada getTxtFieldRH() {
        return txtFieldRH;
    }

    public void setTxtFieldRH(JTextFieldPersonalizada txtFieldRH) {
        this.txtFieldRH = txtFieldRH;
    }

    public JTextFieldPersonalizada getTxtFieldRPM() {
        return txtFieldRPM;
    }

    public void setTxtFieldRPM(JTextFieldPersonalizada txtFieldRPM) {
        this.txtFieldRPM = txtFieldRPM;
    }

    public JTextFieldPersonalizada getTxtFieldTAceite() {
        return txtFieldTAceite;
    }

    public void setTxtFieldTAceite(JTextFieldPersonalizada txtFieldTAceite) {
        this.txtFieldTAceite = txtFieldTAceite;
    }

    public JTextFieldPersonalizada getTxtFieldText() {
        return txtFieldText;
    }

    public void setTxtFieldText(JTextFieldPersonalizada txtFieldText) {
        this.txtFieldText = txtFieldText;
    }

    public JLabelBanderas getLabelBajoFlujo() {
        return labelBajoFlujo;
    }

    public JLabelBanderas getLabelCO2Range() {
        return labelCO2Range;
    }

    public JLabelBanderas getLabelCORange() {
        return labelCORange;
    }

    public JLabelBanderas getLabelCalentamiento() {
        return labelCalentamiento;
    }

    public JLabelBanderas getLabelCero() {
        return labelCero;
    }

    public JLabelBanderas getLabelCondensacion() {
        return labelCondensacion;
    }

    public JLabelBanderas getLabelHCRange() {
        return labelHCRange;
    }

    public JLabelBanderas getLabelInterna() {
        return labelInterna;
    }

    public JLabelBanderas getLabelNOxRange() {
        return labelNOxRange;
    }

    public JLabelBanderas getLabelNoExacta() {
        return labelNoExacta;
    }

    public JLabelBanderas getLabelO2Range() {
        return labelO2Range;
    }

    public JLabelBanderas getLabelhiHC() {
        return labelhiHC;
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
    



    //public void ponerComponente(int fila,int columna,JComponent comp, )
    public final void filaColumna(int fila,int columna,GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;

    }

    public static void main(String args[]){
        JFrame frame = new JFrame();
        PanelMedidas panel = new PanelMedidas();
        frame.add(panel);
        frame.setSize(450,450);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}//end of PanelMedidas
