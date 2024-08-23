/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm.centralauto;

import org.soltelec.medicionrpm.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.ColorDef;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.GaugeType;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.PointerType;
import eu.hansolo.steelseries.tools.Section;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.soltelec.util.PortSerialUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *Interfaz gráfica para interactuar con el kit central auto
 * modelo TB85000
 * modelo 8500 8520
 * @author Usuario
 */
public class PanelServicioCentral extends JPanel implements ActionListener{

    private JButton buttonProgramRun, buttonProgramStop, buttonResetRequest;
    private JButton buttonTension, buttonVibracion, buttonCambiarCilindros,buttonConectar;
    private JButton buttonSalir,buttonTiempos;
    private JComboBox comboNumeroCilindros;
    private JComboBox comboPuertosSeriales;
    private JTextArea textAreaRPM;///puede cambiarse por tablas
    private JPanel panelBotones;
    private String[] opcionesCilindros = {"1","2","3","4","5","6","8","10","12"};
    private SerialPort serialPort;
    private Map<String,CommPortIdentifier> opcionesPuertos;
    //private CapelecSerial capelecServicios;
   // private ProductorCapelec lector;
    private Radial radialRPM;
    private Linear linearTemperatura;
    private JPanel panel;
    private TB85000 tb85000;
    private WorkerServicioCentralAuto ws;
    //private BufferStrings bufferMed;
    //private ProductorCapelec productorCapelec;
    //private ConsumidorCapelec consumidorCapelec;

    //private Thread threadProductor;
    //private Thread threadConsumidor;
    public PanelServicioCentral(){
        inicializarInterfaz();
        //capelecServicios = new CapelecSerial();
    }//end of constructor

    /**
     * Constructor para reconfiguración de kit no nueva conexion asi que deshabilita el boton conectar
     * @param consumidor
     * @param serialPort
     */
    public PanelServicioCentral(ConsumidorCapelec consumidor, SerialPort serialPort){
      this();//construye normal
        try {
            panelConfigurar(serialPort, consumidor);
        } catch (IOException ex) {
            //Logger.getLogger(PanelServicioCapelec.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//




    private void inicializarInterfaz() {
        this.setBackground(Color.BLACK);
        GridLayout grid = new GridLayout(2, 5, 5, 5);
        panelBotones = new JPanel(grid);
        panelBotones.setOpaque(false);
        buttonConectar = new JButton("Conectar");
        buttonConectar.addActionListener(this);
        buttonProgramRun = new JButton("Program Run");
        buttonProgramRun.addActionListener(this);
        buttonProgramRun.setEnabled(false);
        buttonProgramStop = new JButton("Program Stop");
        buttonProgramStop.addActionListener(this);
        buttonProgramStop.setEnabled(false);
        buttonResetRequest = new JButton("Reset Request");
        buttonResetRequest.addActionListener(this);
        buttonResetRequest.setEnabled(false);
        buttonTension = new JButton("Tension");//
        buttonTension.setEnabled(false);
        buttonVibracion = new JButton("Vibracion");
        buttonVibracion.setEnabled(false);
        buttonCambiarCilindros = new JButton("Cambiar Cilindros");
        buttonCambiarCilindros.addActionListener(this);
        buttonCambiarCilindros.setEnabled(true);
        buttonSalir = new JButton("Salir");
        buttonSalir.setForeground(Color.red);
        buttonSalir.addActionListener(this);
        buttonTiempos = new JButton("4T");
        buttonTiempos.addActionListener(this);
        comboNumeroCilindros = new JComboBox(opcionesCilindros);
        comboNumeroCilindros.setEnabled(true);
        opcionesPuertos = PortSerialUtil.listPorts();
        String opciones[] = opcionesPuertos.keySet().toArray(new String[0]);
        
        comboPuertosSeriales = new JComboBox(opciones);

        panelBotones.add(buttonConectar);
        panelBotones.add(comboPuertosSeriales);
        //panelBotones.add(buttonProgramRun);
        //panelBotones.add(buttonProgramStop);
        //panelBotones.add(buttonResetRequest);
        //panelBotones.add(buttonTension);
        //panelBotones.add(buttonVibracion);
        panelBotones.add(comboNumeroCilindros);
        panelBotones.add(buttonCambiarCilindros);        
        panelBotones.add(buttonTiempos);
        panelBotones.add(buttonSalir);


        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.insets = new Insets(20, 10, 20, 10);
        this.setLayout(bag);
        textAreaRPM = new JTextArea();
        JScrollPane scroll = new JScrollPane(textAreaRPM);
        //
        JLabel labelTitulo = new JLabel("CONSOLA KIT RPM ");
        labelTitulo.setFont( new Font(Font.SERIF,Font.BOLD,40));
        labelTitulo.setForeground(Color.WHITE);
        filaColumna(0, 0, c);
        this.add(labelTitulo, c);
        //
        c.weighty = 0.5;
        filaColumna(1, 0, c);
        c.fill = GridBagConstraints.BOTH;
        this.add(scroll, c);

        //poner el tacometro y el termometro
        configurarTacometro();
        configurarTermometro();
        ponerPanel();
        filaColumna(2,0,c);
        this.add(panel,c);
        
        c.fill = GridBagConstraints.NONE;
        c.weighty = 0.0;
        filaColumna(3, 0, c);
        this.add(panelBotones, c);

    }

private void filaColumna(int fila,int columna,GridBagConstraints cons){
        cons.gridx = columna;
        cons.gridy = fila;
}//

    public Linear getLinearTemperatura() {
        return linearTemperatura;
    }

    public void setLinearTemperatura(Linear linearTemperatura) {
        this.linearTemperatura = linearTemperatura;
    }

    public Radial getRadialRPM() {
        return radialRPM;
    }

    public void setRadialRPM(Radial radialRPM) {
        this.radialRPM = radialRPM;
    }

    public JTextArea getTextAreaRPM() {
        return textAreaRPM;
    }




public static void main(String args[]){
        PanelServicioCentral gui = new PanelServicioCentral();
        JFrame app = new JFrame("Capelec");
        app.add(gui);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
}//end of main




private void inicializarPuertoSerial(String portName) throws Exception {
        serialPort = connect(portName,9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        if(serialPort!= null){
                textAreaRPM.append("Conexion con" + serialPort.getName());
                System.out.println("Puerto Serial conectado");
        }
        //capelecServicios.setPuertoSerial(serialPort);
        buttonConectar.setEnabled(false);
        tb85000 = new TB85000(serialPort);
        ws = new WorkerServicioCentralAuto(this, tb85000);
        ws.execute();

        //serialPort.setDTR(true);
 }//end of inicializarPuertoSerial




private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits,int parity ) throws Exception
    {
        
        serialPort = null;
        CommPortIdentifier portIdentifier = opcionesPuertos.get(portName) ;
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),1000);
            if ( commPort instanceof SerialPort )
            {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudRate,dataBits,stopBits,parity);
                serialPort.enableReceiveTimeout(100);

                           }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == buttonConectar){
            try{
                inicializarPuertoSerial( (String)(this.comboPuertosSeriales.getSelectedItem()) );
                //habilitarBotones();
            }catch(Exception ex){
                ex.printStackTrace(System.err);
            }//end of catch
      } else if(e.getSource() == buttonProgramRun){
          //capelecServicios.enviarComandoNoParametros(CapelecSerial.RUN_PROGRAM);
      } else if (e.getSource() == buttonProgramStop){
          //capelecServicios.enviarComandoNoParametros(CapelecSerial.PROGRAM_STOP);
      } else if(e.getSource() == buttonResetRequest){
          //System.out.println("Reset enviado");
          //capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);
      } else if(e.getSource() == buttonTension){
          //capelecServicios.medirUsandoBateria();
      } else if(e.getSource() == buttonVibracion){
          //capelecServicios.medirUsandoVibracion();
      } else if(e.getSource() == buttonCambiarCilindros){
          String strCilindros = (String)comboNumeroCilindros.getSelectedItem();
          int numCilindros = Integer.parseInt(strCilindros);
            try {
                ws.setInterrumpido(true);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PanelServicioCentral.class.getName()).log(Level.SEVERE, null, ex);
                }
                tb85000.cambiarCilindros(numCilindros);
                ws.setInterrumpido(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "No se puede enviar el comando");
                ex.printStackTrace(System.err);
            }
      } else if(e.getSource() == buttonSalir){
            try {
                desconectar();
            } catch (Throwable ex) {
                Logger.getLogger(PanelServicioCentral.class.getName()).log(Level.SEVERE, null, ex);
            }
      } else if(e.getSource() == buttonTiempos){
            try {
                ws.setInterrumpido(true);
                Thread.sleep(250);
                if(buttonTiempos.getActionCommand().equals("4T")){
                    tb85000.cambiarTiempos(4);
                    buttonTiempos.setActionCommand("2T");
                    buttonTiempos.setText("2T");
                }
                else if(buttonTiempos.getActionCommand().equals("2T")){
                    tb85000.cambiarTiempos(2);
                    buttonTiempos.setActionCommand("4T");
                    buttonTiempos.setText("4T");
                }
                ws.setInterrumpido(false);
            } catch (Exception exc) {
                exc.printStackTrace();

            }
      }

    }//end of actionPerformed

    private void configurarTacometro() {
        radialRPM = new Radial();
        radialRPM.setPreferredSize(new Dimension(300,300));
        radialRPM.setMaxValue(8000);
        radialRPM.setMinValue(0);
        radialRPM.setTitle("RPM");
        radialRPM.setUnitString("rev/min");
       // radialRPM.setTickLabelPeriod(1000);
        //radialRPM.setScaleDividerPower(2);
        radialRPM.setGaugeType(GaugeType.TYPE3);
        final Section[] SECTIONS = {new Section(0, 5900, java.awt.Color.WHITE), new Section(6000, 8000, java.awt.Color.RED)};
        radialRPM.setTickmarkSections(SECTIONS);
        radialRPM.setTickmarkSectionsVisible(true);
        radialRPM.setThreshold(2500);
        radialRPM.setFrameDesign(FrameDesign.BLACK_METAL);
        radialRPM.setBackgroundColor(BackgroundColor.BLACK);
        radialRPM.setDigitalFont(true);
        radialRPM.setLcdColor(LcdColor.REDDARKRED_LCD);
        radialRPM.setPointerType(PointerType.TYPE2);
        radialRPM.setPointerColor(ColorDef.GRAY);
        //radialRPM.setFrame3dEffectVisible(true);
        radialRPM.setBackgroundColor(BackgroundColor.BLACK);

    }//end of method configurarTacometro

    private void configurarTermometro(){
        linearTemperatura = new Linear();
        linearTemperatura.setPreferredSize(new Dimension(150,300));
        //linearTemperatura.init(100,300);
        linearTemperatura.setTitle("T Aceite");
        linearTemperatura.setUnitString("°C");
        linearTemperatura.setThreshold(60);
        linearTemperatura.setBackgroundColor(BackgroundColor.BLACK);
        final Section[] SECTIONS = {new Section(0, 40, java.awt.Color.RED), new Section(41,59, java.awt.Color.ORANGE),new Section(60,99,java.awt.Color.GREEN)};
        linearTemperatura.setSections(SECTIONS);
        linearTemperatura.setSectionsVisible(true);
        linearTemperatura.setFrameVisible(true);
       // linearTemperatura.setFrame3dEffectVisible(true);
        linearTemperatura.setFrameDesign(FrameDesign.BLACK_METAL);
        linearTemperatura.setValueColor(ColorDef.RED);
        linearTemperatura.setValue(100);


    }//end of configurarTermometro

    private void ponerPanel() {
        panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.add(radialRPM);
        JPanel panelEnvoltor = new JPanel(new BorderLayout());
        panelEnvoltor.setPreferredSize(new Dimension(150,300));
        panelEnvoltor.setOpaque(false);
        //panelEnvoltor.add(linearTemperatura);
        panel.add(linearTemperatura);
   }//end of ponerPanle

   private void desconectar() throws Throwable{
       //radialRPM = null;
       //linearTemperatura = null;
       if(ws!=null){
       ws.setInterrumpido(true);
       Thread.sleep(250);
       ws.setTerminado(true);
       Thread.sleep(250);
       }
       tb85000 = null;
        
       if(serialPort != null){
       serialPort.close();
       serialPort = null;
       }
       deshabilitarBotones();
       Window w =SwingUtilities.getWindowAncestor(this);
       w.dispose();
}

   private void deshabilitarBotones(){
       buttonProgramRun.setEnabled(false);
        buttonProgramStop.setEnabled(false);
        buttonCambiarCilindros.setEnabled(false);
        buttonResetRequest.setEnabled(false);
        buttonTension.setEnabled(false);
        buttonVibracion.setEnabled(false);
        comboNumeroCilindros.setEnabled(false);
        buttonConectar.setEnabled(true);
        comboPuertosSeriales.setEnabled(true);
        textAreaRPM.setText("");//memoria
   }

    private void habilitarBotones() {

        buttonProgramRun.setEnabled(true);
        buttonProgramStop.setEnabled(true);
        buttonCambiarCilindros.setEnabled(true);
        buttonResetRequest.setEnabled(true);
        buttonTension.setEnabled(true);
        buttonVibracion.setEnabled(true);
        comboNumeroCilindros.setEnabled(true);
    }

    private void panelConfigurar(SerialPort sp,ConsumidorCapelec c) throws IOException {
        ////
        buttonConectar.setEnabled(false);
        buttonConectar.setVisible(false);
        comboPuertosSeriales.setEnabled(false);
        comboPuertosSeriales.setVisible(false);
        //
        habilitarBotones();
        
        c.setRadialRPM(radialRPM);
        c.setTxtArea(textAreaRPM);
        c.setLinearTemperatura(linearTemperatura);
    }//end of panelConfigurar

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        radialRPM = null;
        linearTemperatura = null;
        System.err.println(" Finalizado panel servicio");        
    }



    
}//end of class
