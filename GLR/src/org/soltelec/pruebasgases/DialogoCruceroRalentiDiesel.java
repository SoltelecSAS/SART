/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import org.soltelec.medicionrpm.ConsumidorCapelec;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Window;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.ColorDef;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.GaugeType;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.LedColor;
import eu.hansolo.steelseries.tools.PointerType;
import eu.hansolo.steelseries.tools.Section;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import static java.awt.GridBagConstraints.*;

/**
 *Dialogo para capturar las velocidades de crucero y ralenti
 * en la prueba de gases para vehiculos diesel tambien captura la temperatura
 * impidiendo salir del dialogo hasta que la temperatura supere los 60 grados
 * @author Usuario
 */
public class DialogoCruceroRalentiDiesel extends JPanel{
    private JPanel panelUno;//Panel para los radio y los textfields
    private JPanel panelFiguras;//Panel para el tacometro y el termometro
    private JLabel lblRalenti,lblCrucero,lblTitulo/*,lblGobernador*/;
    private JCheckBox checkBoxGobernador;
    private JTextField txtFldRalenti,txtFldCrucero;
    private JTextArea txtAreaKit;//Para ver lo que esta generando el kit de respuesta
    private JButton buttonContinuar,buttonCancelar;
    private Radial radialTacometro;
    private JRadioButton radioDigitar,radioAceleracion;
    private ButtonGroup buttonGroup;
    private Linear linearTemperatura;
    private JToggleButton toggleAutoDetectar;
    private Border raisedbevel;
    private Border loweredbevel;
    private CompoundBorder compound;
    private int velRalenti, velCrucero;
    private boolean terminarPrueba = false;
    private ConsumidorCapelec consumidor;
    private HiloDialogRalentiGases hilo;
    private HiloDetectarGobernadas hiloDeteccion;
    private Thread threadDeteccion;

    public DialogoCruceroRalentiDiesel(){
        inicializarInterfaz();
    }//end of constructor

    private void inicializarInterfaz() {
        crearComponentes();
        ponerComponentes();
    }//end of inicializarInterfaz

    private void crearComponentes(){
        ManejadorRadios manejadorRadios = new ManejadorRadios();
        lblTitulo = new JLabel(" VELOCIDAD CRUCERO Y RALENTI ");
        lblRalenti = new JLabel("Ralenti");
        lblCrucero = new JLabel("Crucero");
        txtFldRalenti = new JTextField();
        txtFldCrucero = new JTextField();
        //lblGobernador = new JLabel("Gobernador no Funciona");
        checkBoxGobernador = new JCheckBox("Dispositivo no Funciona");
        buttonContinuar = new JButton("Continuar");
        buttonContinuar.addActionListener(manejadorRadios);
        buttonCancelar = new JButton("Cancelar");
        radioDigitar = new JRadioButton("Digitar");
        radioDigitar.addActionListener(manejadorRadios);
        radioDigitar.setSelected(true);
        radioAceleracion = new JRadioButton("Probar Aceleracion");
        radioAceleracion.addActionListener(manejadorRadios);
        radioAceleracion.setSelected(false);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioDigitar);
        buttonGroup.add(radioAceleracion);
        txtAreaKit = new JTextArea();
        configurarTacometro();
        configurarTermometro();
        configurarPanel1();
        configurarPanelFiguras();
    }//end of method crearComponentes

        private void configurarTacometro() {
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
        radialTacometro.setDigitalFont(true);
        radialTacometro.setLcdColor(LcdColor.REDDARKRED_LCD);
        radialTacometro.setPointerType(PointerType.TYPE2);
        radialTacometro.setPointerColor(ColorDef.RED);
       // radialTacometro.setFrame3dEffectVisible(true);
        radialTacometro.setFrameDesign(FrameDesign.BLACK_METAL);
        radialTacometro.setPointerColor(ColorDef.GRAY);
        radialTacometro.setBackgroundColor(BackgroundColor.BLACK);
    }

    private void configurarTermometro() {
        linearTemperatura = new Linear();
        linearTemperatura.setPreferredSize(new Dimension(300,120));
        linearTemperatura.setTitle("T Aceite");
        linearTemperatura.setUnitString("°C");
        linearTemperatura.setThreshold(60);
       // linearTemperatura.setBackgroundColor(BackgroundColor.BRUSHED_METAL);
        final Section[] SECTIONS = {new Section(0, 40, java.awt.Color.RED), new Section(41,59, java.awt.Color.ORANGE),new Section(60,99,java.awt.Color.GREEN)};
        linearTemperatura.setSections(SECTIONS);
        linearTemperatura.setSectionsVisible(true);
        linearTemperatura.setFrameDesign(FrameDesign.BLACK_METAL);
        linearTemperatura.setBackgroundColor(BackgroundColor.BLACK);
        
    }

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(10,5,10,5);
        //Poner el titulo
        c.gridwidth = REMAINDER;
        filaColumna(0,0,c);
        //this.add(lblTitulo,c);
        c.gridwidth = 1;//ocupa solamente una fila
        c.gridheight = 4;
        filaColumna(1,0,c);
        c.weighty = 0.85;
        c.weightx = 0.50;
        panelFiguras.setPreferredSize(new Dimension(400,600));
        this.add(panelFiguras,c);
        filaColumna(1,1,c);
        c.weightx = 0.50;
        c.fill = BOTH;
        c.gridheight = 1;
        c.anchor = LINE_START;
        this.add(panelUno,c);
        filaColumna(2,1,c);
        c.fill = BOTH;
        c.anchor = FIRST_LINE_START;
        this.add(new JScrollPane(txtAreaKit),c);
        filaColumna(2,1,c);
        //this.add(buttonContinuar,c);
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        loweredbevel = BorderFactory.createLoweredBevelBorder();
        compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
        TitledBorder titled = BorderFactory.createTitledBorder(compound,"VELOCIDAD RALENTI Y CRUCERO");
        titled.setTitleJustification(TitledBorder.CENTER);
        this.setBorder(titled);
        
    }//end of method ponerComponentes

    private void configurarPanel1() {
        TitledBorder titled;
        titled = BorderFactory.createTitledBorder("RPM");
        GridBagLayout bag1 = new GridBagLayout();
        GridBagConstraints c1 = new GridBagConstraints();
        panelUno = new JPanel(bag1);
        panelUno.setBorder(titled);
        c1.insets = new Insets(10,5,10,5);
        c1.weightx = 0.33;
        //c1.weighty = 1.0;
        //c1.anchor = FIRST_LINE_START;
        c1.anchor = LINE_START;
        filaColumna(0,0,c1);
        panelUno.add(lblRalenti,c1);
        filaColumna(0,1,c1);
        c1.fill = HORIZONTAL;
        panelUno.add(txtFldRalenti,c1);
        filaColumna(0,2,c1);
        c1.fill = NONE;
        panelUno.add(radioDigitar,c1);
        filaColumna(1,0,c1);
        panelUno.add(lblCrucero,c1);
        filaColumna(1,1,c1);
        c1.fill = HORIZONTAL;
        panelUno.add(txtFldCrucero,c1);
        filaColumna(1,2,c1);
        panelUno.add(radioAceleracion,c1);
        filaColumna(2,1,c1);
        panelUno.add(checkBoxGobernador,c1);
        filaColumna(2,2,c1);
        panelUno.add(buttonContinuar,c1);
    }//end of configurarPanel1

    private void configurarPanelFiguras() {
        panelFiguras = new JPanel();
        GridBagLayout bag = new GridBagLayout();
        panelFiguras.setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = FIRST_LINE_START;
        c.fill = HORIZONTAL;
        panelFiguras.add(radialTacometro,c);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = HORIZONTAL;
        JPanel panelEnvoltor = new JPanel(new BorderLayout());
        panelEnvoltor.setPreferredSize(new Dimension(240,100));
        panelEnvoltor.add(linearTemperatura);
        panelFiguras.add(panelEnvoltor,c);
        Border simple = BorderFactory.createEtchedBorder();
        panelFiguras.setBorder(simple);
    }//end of method

    private void filaColumna(int fila, int columna, GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;
    }


    public static void main(String args[]){
    JFrame app = new JFrame("Dialogo Tacometro");
        //app.setSize(500,250);
        app.getContentPane().add(new DialogoCruceroRalentiDiesel());
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);

    }
 private void validarTxtField(){
           boolean ralentiInvalida = false;
           boolean cruceroInvalida = false;
           try{
               velRalenti = Integer.parseInt(txtFldRalenti.getText());
               ralentiInvalida = (velRalenti <= 0 ? true : false);
           }catch(NumberFormatException ne){
               ralentiInvalida = true;
           }
           try{
               velCrucero = Integer.parseInt(txtFldCrucero.getText());
               if(velCrucero <= velRalenti ||velCrucero <= 0)
                   cruceroInvalida = true;
           }catch(NumberFormatException ne){
               cruceroInvalida = true;
           }

           StringBuilder strBuilder = new StringBuilder();
           if(ralentiInvalida) strBuilder.append("\n Velocidad Ralenti Invalida ");
           if(cruceroInvalida) strBuilder.append("\n Velocidad Crucero Invalida ");

           if(ralentiInvalida || cruceroInvalida ){
               JOptionPane.showMessageDialog(null,strBuilder.toString());
           } else {
               hilo.setTerminado(true);
               if(hiloDeteccion != null)
                   hiloDeteccion.stop();
              Window w = SwingUtilities.getWindowAncestor(this);
              w.dispose();
           }

        }//end of method validarTxtField
       public void cerrar(){
           Window w = SwingUtilities.getWindowAncestor(this);
           w.dispose();
       }
    class ManejadorRadios implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {

            if( (e.getSource() instanceof JRadioButton) ){
                if(radioDigitar.isSelected()){
                    txtFldCrucero.setEnabled(true);
                    txtFldRalenti.setEnabled(true);
                    if(hiloDeteccion!= null)
                        hiloDeteccion.stop();
                }else if(radioAceleracion.isSelected()){//
                    //en este momento probar aceleracion e intentar calcular
                    //la velocidad gobernada y la velocidad ralenti
                    hiloDeteccion = new HiloDetectarGobernadas(consumidor, txtFldRalenti, txtFldCrucero);
                    threadDeteccion = new Thread(hiloDeteccion);
                    threadDeteccion.start();
                    //txtFldCrucero.setEnabled(false);
                    //txtFldRalenti.setEnabled(false);
                }
            }//end of JRadioButton
            else if(e.getSource() == buttonContinuar){
                if(checkBoxGobernador.isSelected()){
                    JOptionPane.showMessageDialog(null,"Dispositivo No Funciona");
                    //TODO registro gobernador no funciona
                    terminarPrueba = true;
                    hilo.setTerminado(true);
                    if(hiloDeteccion != null)
                        hiloDeteccion.stop();
                    cerrar();
                }else{
//                     if (linearTemperatura.getValue() < 60){
//                        JOptionPane.showMessageDialog(null,"La temperatura del motor no es adecuada");
//                        return;//sale del metodo sin validar
//                    }//end if linearTemperatura
                    validarTxtField();
                }//end else
           }//end else if buttonContinuar
        }//end of actionPerformed
   }//end of ManejadorRadios

    public Linear getLinearTemperatura() {
        return linearTemperatura;
    }

    public void setLinearTemperatura(Linear linearTemperatura) {
        this.linearTemperatura = linearTemperatura;
    }

    public Radial getRadialTacometro() {
        return radialTacometro;
    }

    public void setRadialTacometro(Radial radialTacometro) {
        this.radialTacometro = radialTacometro;
    }

    public boolean isTerminarPrueba() {
        return terminarPrueba;
    }

    public void setTerminarPrueba(boolean terminarPrueba) {
        this.terminarPrueba = terminarPrueba;
    }

    public void setHilo(HiloDialogRalentiGases hilo) {
        this.hilo = hilo;
    }

    public int getVelCrucero() {
        return velCrucero;
    }

    public int getVelRalenti() {
        return velRalenti;
    }

    public JTextArea getTxtAreaKit() {
        return txtAreaKit;
    }

    public void setTxtAreaKit(JTextArea txtAreaKit) {
        this.txtAreaKit = txtAreaKit;
    }

    public void setConsumidor(ConsumidorCapelec consumidor) {
        this.consumidor = consumidor;
    }
}//end of class DialogoCruceroRalentiDiesel
