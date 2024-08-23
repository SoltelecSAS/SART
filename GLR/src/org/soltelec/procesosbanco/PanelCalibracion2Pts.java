/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import java.awt.Font;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.soltelec.models.controllers.CalibracionDosPuntosController;
import org.soltelec.models.entities.Calibracion;
import org.soltelec.procesosbanco.verificacion.PanelProgresoPresion;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.capelec.BancoCapelec;

/**
 * Esta clase tiene como fin dar una interfaz gráfica para realizar el proceso
 * completo para capturar la calibración de los datos 17/02/2011 adicionado un
 * display de presion Otra clase definida dentro de esta es la que guarda los
 * textfields para los valores del punto de calibración no importa si es dos o
 * cuatro tiempos
 *
 * @author GerenciaDesarrollo
 */
public class PanelCalibracion2Pts extends JPanel implements ActionListener {

    int contadorProceso = 0;//contador para ver el progreso
    String mensaje;
    JLabel etiqueta;
    //JTextFieldPersonalizada HCtxtField,COtxtField,CO2txtField,O2txtField;//para los valores del punto de calibracion
    PanelPtoCalibracion panelPtoCalibracion;
    JButton siguienteJButton;
    // JProgressBar barraProgreso;
    JPanel panelSur;
    PanelProgresoPresion panelProgreso;
    BancoGasolina banco;
    PuntoCalibracion puntoCalibracion;
    private int idUsuario;
    public static String analizador;
    public static Integer metAplic;
    public static int contadorMuestra = 1;

    private ProcesoCalibracion procesoCalibracionAlta;
    private ProcesoCalibracion procesoCalibracionBaja;
    public static Properties props;

    public PanelCalibracion2Pts(BancoGasolina theBanco, String analizador, int idUsuario) {
        try {
            this.banco = theBanco;
            Font f = new Font("Serif", Font.PLAIN, 30);
            Font fetiqueta = new Font("Serif", Font.PLAIN, 40);
            panelProgreso = new PanelProgresoPresion();
            GridBagLayout grid = new GridBagLayout();
            GridBagConstraints c1 = new GridBagConstraints();
            this.setLayout(grid);
            etiqueta = new JLabel("Datos para punto Calibracion Muestra Baja paso 1");
            etiqueta.setFont(fetiqueta);
            c1.gridx = 0;
            c1.gridy = 0;//columna 0 fila 0
            c1.gridwidth = REMAINDER;//ocupa todo el espacio horizontal
            c1.anchor = LINE_START;//los componentes se ponen a la izquierda
            c1.weighty = 0.33;//divididos horizontalmente en 3 partes iguales
            c1.weightx = 1.0;//se expande a todo el espacio horizontal
            c1.insets = new Insets(20, 20, 20, 20);
            add(etiqueta, c1);//aniade la etiqueta como si fuera un titulo
            panelPtoCalibracion = new PanelPtoCalibracion();//El panel con los textfields para el punto de calibracion
            //panelPtoCalibracion.getButtonGuardar().addActionListener(this);
            c1.gridx = 0;
            c1.gridy = 1;//columna 0 fila 1
            c1.anchor = CENTER;//pone el panel anclado en el centro
            c1.fill = HORIZONTAL;//hace que el panel ocupe todo el espacio horizontal de su celda
            add(panelPtoCalibracion, c1);//El panel de punto de calibracion es el que contiene los valores de texto para las calibraciones
            siguienteJButton = new JButton("Paso1");//boton que aumenta el numero del proceso
            siguienteJButton.setFont(f);
            siguienteJButton.addActionListener(this);
            //configuracion del panel sur
            panelSur = new JPanel();//panel para contener el boton de forma que no llene todo el espacio ni se vea muy pequenio
            GridBagConstraints c = new GridBagConstraints();//nuevo no confundirlo con el principal
            c.anchor = GridBagConstraints.LINE_END;//pone le boton alineado a la izquierda
            c.gridwidth = REMAINDER;//el ancho de la celda es todo el que pueda ocupar
            c.gridx = 0;
            c.gridy = 0;//fila cero y columna cero
            panelSur.setLayout(new GridBagLayout());//aniade el alyout al panel
            panelSur.add(siguienteJButton, c);//pone el boton en este panel envolvente
            //termina de configurar el panel sur
            c1.fill = NONE;//cambia para que no llene la celda sino se posicione con su tamanio preferido
            c1.gridx = 0;
            c1.gridy = 2;//columna 0 fila 2
            c1.anchor = LINE_END;//pone el componente a la izquierda
            this.add(panelSur, c1);//aniade el panel sur a este panel
            puntoCalibracion = new PuntoCalibracion();
            this.analizador = analizador;
            this.idUsuario = idUsuario;
            props = new Properties();
            props.load(new FileInputStream("equipos.properties"));

        } catch (IOException ex) {
            Logger.getLogger(PanelCalibracion2Pts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {//actionPerformed para cuando se da click en el boton paso 1, paso 2, etc
        //Validacion de los textFields        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.fill = HORIZONTAL;
        c.gridwidth = REMAINDER;
        c.weightx = 0.85;
        c.insets = new Insets(20, 20, 20, 20);
        if (e.getSource() == siguienteJButton) {
            double valorCO = 0;
            double valorCO2 = 0;
            double valorHC = 0;
            if (contadorProceso == 0) {//obtiene los valores para el primer punto de calibracion y realiza la calibracion del primer punto o de muestra baja, TODO : traerlos desde la base de datos;
                //validar que los valores de los textFields sean por lo menos numeros
                try {
                    valorCO = Double.parseDouble(panelPtoCalibracion.getCOtxtField().getText());
                    valorCO2 = Double.parseDouble(panelPtoCalibracion.getCO2txtField().getText());
                    valorHC = Double.parseDouble(panelPtoCalibracion.getHCtxtField().getText());
                    if (valorCO == 0 && valorCO2 == 0 && valorHC == 0) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena los Valores Digitados no son Validos");
                        return;
                    }
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena los Valores Digitados deben de ser Numericos");
                    return;
                }
                if (valorHC < 255 || valorHC > 345) {
                    JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para HC no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                    return;
                }
                if (valorCO < 0.85 || valorCO > 1.15) {
                    JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para CO no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                    return;
                }
                if (valorCO2 < 5.10 || valorCO2 > 6.90) {
                    JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para CO2 no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                    return;
                }

                PuntoCalibracion puntoBaja = new PuntoCalibracion();
                puntoBaja.setValorHC((short) valorHC);
                puntoBaja.setValorCO((short) (valorCO * 100));
                puntoBaja.setValorCO2((short) (valorCO2 * 10));
                this.remove(panelPtoCalibracion);
                this.revalidate();
                this.repaint();
                this.add(panelProgreso, c);//pone el panel con el display de presion y la barra de progreso.

                //dependiendo de la instancia del banco se realizara uno u otro proceso.    
                procesoCalibracionBaja = new ProcesoCalibracion(panelProgreso, siguienteJButton, banco);
                procesoCalibracionBaja.setPtoCalibracion(puntoBaja);//sino le indico un numero de muestra la muestra es baja
                (new Thread(procesoCalibracionBaja)).start();
                PanelCalibracion2Pts.contadorMuestra = 2;
                siguienteJButton.setText("Paso 2");
                siguienteJButton.setEnabled(false);
                this.revalidate();
                this.repaint();
                contadorProceso++;

            }//end if
            else if (contadorProceso == 1) {//poner otra vez unos textfields

                this.etiqueta.setText("Ingrese Datos segunda pipeta Muestra Alta");
                this.remove(panelProgreso);
                this.revalidate();
                this.repaint();
                this.add(panelPtoCalibracion, c);
                panelPtoCalibracion.reset();
                siguienteJButton.setText("Paso3");
                this.revalidate();
                this.repaint();
                contadorProceso++;
                PanelCalibracion2Pts.contadorMuestra = 2;

            }//end else if de contador
            else if (contadorProceso == 2) {//REaliza el proceso de ingreso de gas para la muestra de alta
                try {
                    valorCO = Double.parseDouble(panelPtoCalibracion.getCOtxtField().getText());
                    valorCO2 = Double.parseDouble(panelPtoCalibracion.getCO2txtField().getText());
                    valorHC = Double.parseDouble(panelPtoCalibracion.getHCtxtField().getText());
                    if (valorCO == 0 && valorCO2 == 0 && valorHC == 0) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena los Valores Digitados no son Validos");
                        return;
                    }
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena los Valores Digitados deben de ser Numericos");
                    return;
                }
                System.out.println(" EL ANALIZADOR ES DE " + PanelCalibracion2Pts.analizador);
                if (PanelCalibracion2Pts.analizador.equalsIgnoreCase("4t")) {
                    if (valorHC > 1380 || valorHC < 1020) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para HC no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                        return;
                    }
                    if (valorCO > 4.60 || valorCO < 3.4) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para CO no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                        return;
                    }
                    if (valorCO2 > 13.8 || valorCO2 < 10.2) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para CO2 no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                        return;
                    }
                } else {
                    if (valorHC > 3680 || valorHC < 2720) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para HC no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                        return;
                    }
                    if (valorCO > 9.2 || valorCO < 6.8) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para CO no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                        return;
                    }
                    if (valorCO2 > 13.8 || valorCO2 < 10.2) {
                        JOptionPane.showMessageDialog(panelPtoCalibracion, "Que Pena El Valor Introducido para CO2 no debe de Exceder del 15 % de las Fracciones del volumen requeridas");
                        return;
                    }
                }
                PuntoCalibracion puntoAlta = new PuntoCalibracion();
                puntoAlta.setValorHC((short) valorHC);
                puntoAlta.setValorCO((short) (valorCO * 100));//adecuado??
                if (banco instanceof BancoCapelec) {
                    puntoAlta.setValorCO2((short) ((valorCO2 * 10) / 10));
                    System.out.println(" EL BANCO ES CAPELEC");
                } else {
                    puntoAlta.setValorCO2((short) (valorCO2 * 10));
                    System.out.println(" EL BANCO ES SENSOR");
                }
                this.etiqueta.setText("Ingresando Gas desde la pipeta Muestra Alta");
                this.remove(panelPtoCalibracion);
                this.revalidate();
                this.repaint();
                this.add(panelProgreso, c);
                procesoCalibracionAlta = new ProcesoCalibracion(panelProgreso, siguienteJButton, banco);
                procesoCalibracionAlta.setMuestra(2);//esto indica que la muestra sea la muestra alta
                procesoCalibracionAlta.setPtoCalibracion(puntoAlta);
                (new Thread(procesoCalibracionAlta)).start();
                siguienteJButton.setText("Finalizar");
                siguienteJButton.setEnabled(false);
                this.revalidate();
                this.repaint();
                contadorProceso++;
            } else if (contadorProceso == 3) {//Registrar los valores y el resultado de la calibracion
                PuntoCalibracion p1 = procesoCalibracionAlta.getPtoCalibracion();
                PuntoCalibracion p2 = procesoCalibracionBaja.getPtoCalibracion();
                PuntoCalibracion pto = new PuntoCalibracion();
                int pef = Integer.parseInt(banco.obtenerPEF());
                String serial = banco.numeroSerial();
                RegVefCalibraciones r = new RegVefCalibraciones();
                //if(procesoCalibracionAlta.isProcesoAltaExitoso() && procesoCalibracionBaja.isProcesoBajaExitoso()){
                if (procesoCalibracionAlta.procesoExitoso && procesoCalibracionBaja.procesoExitoso) {
                    JOptionPane.showMessageDialog(panelProgreso, "Calibracion Alta Fue Exitosa " + procesoCalibracionAlta.isProcesoAltaExitoso());
                    JOptionPane.showMessageDialog(panelProgreso, "Calibracion Baja Fue Exitosa " + procesoCalibracionBaja.isProcesoBajaExitoso());
                    try {
                        System.out.println("Ingreso a registrar cal2pts");
                        r.registrarResultadoCal2Ptos(true, p2, p1, serial, pef, banco, idUsuario);
                        System.out.println("Ingreso a registrar Calibracion aprobada");
                    } catch (Exception ex) {
                        System.out.println("No registro cal2pts");
                        Mensajes.mostrarExcepcion(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(panelProgreso, "Disculpe; la Calibracion no Fue exitosa");//debe bloquear el equipo
                    try {
                        r.registrarResultadoCal2Ptos(false, p2, p1, serial, pef, banco, idUsuario);
                    } catch (SQLException ex) {
                        Logger.getLogger(PanelCalibracion2Pts.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(PanelCalibracion2Pts.class.getName())
                                .log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PanelCalibracion2Pts.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                contadorProceso = 0;
                PanelCalibracion2Pts.contadorMuestra = 1;
                Window w = SwingUtilities.getWindowAncestor(this);
                w.dispose();

                //System.exit(0);
            }
        } //poner otros manejadores
    }//end of method actionPerformed

}

class PanelPtoCalibracion extends JPanel implements ActionListener {

    JTextFieldPersonalizada HCtxtField, COtxtField, CO2txtField, O2txtField;//para los valores del punto de calibracion
    JButton buttonDefecto, buttonPipeta;

    public PanelPtoCalibracion() {
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        Font fuente = new Font("Serif", Font.PLAIN, 40);
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(5, 10, 5, 10);//en contra del reloj
        //toda de la pantalla modo horizontal
        c.weightx = 1.0;
        //un titulo, ocupar toda la fila, centrarse
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;

        //añadir el titulo, fila 0 columna 0
        c.gridx = 0;
        c.gridy = 0;
        c.fill = NONE;
        JLabel labelTitulo = new JLabel("Datos Punto Calibracion");
        labelTitulo.setFont(fuente);
        this.add(labelTitulo, c);
        c.gridwidth = 1;
        //mitad de la pantalla horizontalmente
        //etiqueta y campo de texto para el HC
        c.gridx = 0;
        c.gridy = 1;//fila1 columna 0 centro
        c.fill = NONE;
        JLabel labelHC = new JLabel("HC ppm");
        labelHC.setFont(fuente);
        this.add(labelHC);
        c.gridx = 1;
        c.gridy = 1;//poner el campo del texto de l valor de HC
        c.fill = HORIZONTAL;
        HCtxtField = new JTextFieldPersonalizada("0");
        this.add(HCtxtField, c);
        //poner la etiqueta y el campo para el CO
        c.gridx = 0;
        c.gridy = 2;
        c.fill = NONE;
        JLabel labelCO = new JLabel("CO %");
        labelCO.setFont(fuente);
        this.add(labelCO, c);
        c.gridx = 1;
        c.gridy = 2;
        c.fill = HORIZONTAL;
        COtxtField = new JTextFieldPersonalizada("0");
        this.add(COtxtField, c);
        //poner la etiqueta y el campo para el CO2
        c.gridx = 0;
        c.gridy = 3;
        c.fill = NONE;
        JLabel labelCO2 = new JLabel("CO2 %");
        labelCO2.setFont(fuente);
        this.add(labelCO2, c);
        c.gridx = 1;
        c.gridy = 3;
        c.fill = HORIZONTAL;
        CO2txtField = new JTextFieldPersonalizada("0");
        this.add(CO2txtField, c);
        //poner el boton de OK
        //en el centro, que ocupe todas las filas
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = REMAINDER;
        c.fill = NONE;
        /*buttonDefecto = new JButton("Val. Por Defecto");
         buttonDefecto.setFont(fuente);
         buttonDefecto.addActionListener(this);
         this.add(buttonDefecto, c);*/
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = REMAINDER;
        c.fill = NONE;
        buttonPipeta = new JButton("Val. Pipetas");
        buttonPipeta.setFont(fuente);
        buttonPipeta.addActionListener(this);
        this.add(buttonPipeta, c);
        this.HCtxtField.setEditable(false);
        this.HCtxtField.setEnabled(false);
        this.COtxtField.setEditable(false);
        this.COtxtField.setEnabled(false);
        this.CO2txtField.setEditable(false);
        this.CO2txtField.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (PanelCalibracion2Pts.contadorMuestra == 1) {
            if (e.getSource() == buttonDefecto) {
                this.HCtxtField.setEditable(true);
                this.HCtxtField.setEnabled(true);
                this.COtxtField.setEditable(true);
                this.COtxtField.setEnabled(true);
                this.CO2txtField.setEditable(true);
                this.CO2txtField.setEnabled(true);
                this.HCtxtField.setText("300");
                this.COtxtField.setText("1.0");
                this.CO2txtField.setText("6.0");
                PanelCalibracion2Pts.metAplic = 0;
            } else {
                this.HCtxtField.setEditable(true);
                this.HCtxtField.setEnabled(true);
                this.COtxtField.setEditable(true);
                this.COtxtField.setEnabled(true);
                this.CO2txtField.setEditable(true);
                this.CO2txtField.setEnabled(true);
                if (PanelCalibracion2Pts.analizador.equalsIgnoreCase("4t")) {
                    this.HCtxtField.setText(PanelCalibracion2Pts.props.getProperty("PBHC"));
                    this.COtxtField.setText(PanelCalibracion2Pts.props.getProperty("PBCO"));
                    this.CO2txtField.setText(PanelCalibracion2Pts.props.getProperty("PBCO2"));
                } else {
                    this.HCtxtField.setText(PanelCalibracion2Pts.props.getProperty("PBHC2T"));
                    this.COtxtField.setText(PanelCalibracion2Pts.props.getProperty("PBCO2T"));
                    this.CO2txtField.setText(PanelCalibracion2Pts.props.getProperty("PBCO22T"));
                }
                this.HCtxtField.setEditable(false);
                this.HCtxtField.setEnabled(false);
                this.COtxtField.setEditable(false);
                this.COtxtField.setEnabled(false);
                this.CO2txtField.setEditable(false);
                this.CO2txtField.setEnabled(false);
                PanelCalibracion2Pts.metAplic = 1;
            }
        } else if (PanelCalibracion2Pts.contadorMuestra == 2) {
            if (e.getSource() == buttonDefecto) {
                this.HCtxtField.setEditable(true);
                this.HCtxtField.setEnabled(true);
                this.COtxtField.setEditable(true);
                this.COtxtField.setEnabled(true);
                this.CO2txtField.setEditable(true);
                this.CO2txtField.setEnabled(true);
                if (PanelCalibracion2Pts.analizador.equalsIgnoreCase("4t")) {
                    this.HCtxtField.setText("1200");
                    this.COtxtField.setText("4.0");
                    this.CO2txtField.setText("12.0");
                } else {
                    this.HCtxtField.setText("3200");
                    this.COtxtField.setText("8.0");
                    this.CO2txtField.setText("12.0");
                }

            } else {
                if (PanelCalibracion2Pts.analizador.equalsIgnoreCase("4t")) {
                    this.HCtxtField.setText(PanelCalibracion2Pts.props.getProperty("PAHC"));
                    this.COtxtField.setText(PanelCalibracion2Pts.props.getProperty("PACO"));
                    this.CO2txtField.setText(PanelCalibracion2Pts.props.getProperty("PACO2"));
                } else {
                    this.HCtxtField.setText(PanelCalibracion2Pts.props.getProperty("PAHC2T"));
                    this.COtxtField.setText(PanelCalibracion2Pts.props.getProperty("PACO2T"));
                    this.CO2txtField.setText(PanelCalibracion2Pts.props.getProperty("PACO22T"));
                }
                this.HCtxtField.setEditable(false);
                this.HCtxtField.setEnabled(false);
                this.COtxtField.setEditable(false);
                this.COtxtField.setEnabled(false);
                this.CO2txtField.setEditable(false);
                this.CO2txtField.setEnabled(false);
            }

        }
    }//end of method actionPerformed

    public void reset() {
        this.HCtxtField.setText("0");
        this.COtxtField.setText("0");
        this.CO2txtField.setText("0");
    }

    public JTextFieldPersonalizada getCO2txtField() {
        return CO2txtField;
    }

    public JTextFieldPersonalizada getCOtxtField() {
        return COtxtField;
    }

    public JTextFieldPersonalizada getHCtxtField() {
        return HCtxtField;
    }

    public JTextFieldPersonalizada getO2txtField() {
        return O2txtField;
    }

}
