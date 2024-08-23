/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.BufferStrings;
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
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.SwingUtilities;

/**
 *Interfaz gráfica para interactuar con el kit de capelec
 * modelo 8500 8520
 * @author Usuario
 */
public class PanelServicioCapelec extends JPanel implements ActionListener{

    private JButton buttonProgramRun, buttonProgramStop, buttonResetRequest;
    private JButton buttonTension, buttonVibracion, buttonCambiarCilindros,buttonConectar;
    private JButton buttonSalir;
    private JComboBox comboNumeroCilindros;
    private JComboBox comboPuertosSeriales;
    private JTextArea textAreaRPM;///puede cambiarse por tablas
    private JPanel panelBotones;
    private String[] opcionesCilindros = {"1","2","3","4","5","6","8","10","12"};
    private SerialPort serialPort;
    private Map<String,CommPortIdentifier> opcionesPuertos;
    private CapelecSerial capelecServicios;
   // private ProductorCapelec lector;
    private Radial radialRPM;
    private Linear linearTemperatura;
    private JPanel panel;
    private BufferStrings bufferMed;
    private ProductorCapelec productorCapelec;
    private ConsumidorCapelec consumidorCapelec;

    private Thread threadProductor;
    private Thread threadConsumidor;
    
    public PanelServicioCapelec(){
        inicializarInterfaz();
        capelecServicios = new CapelecSerial();
    }//end of constructor

    /**
     * Constructor para reconfiguración de kit no nueva conexion asi que deshabilita el boton conectar
     * @param consumidor
     * @param serialPort
     */
    public PanelServicioCapelec(ConsumidorCapelec consumidor, SerialPort serialPort){
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
        buttonCambiarCilindros.setEnabled(false);
        buttonSalir = new JButton("Salir");
        buttonSalir.setForeground(Color.red);
        buttonSalir.addActionListener(this);
        comboNumeroCilindros = new JComboBox(opcionesCilindros);
        comboNumeroCilindros.setEnabled(false);
        opcionesPuertos = PortSerialUtil.listPorts();
        String opciones[] = opcionesPuertos.keySet().toArray(new String[0]);
        
        comboPuertosSeriales = new JComboBox(opciones);

        panelBotones.add(buttonConectar);
        panelBotones.add(comboPuertosSeriales);
        panelBotones.add(buttonProgramRun);
        panelBotones.add(buttonProgramStop);
        panelBotones.add(buttonResetRequest);
        panelBotones.add(buttonTension);
        panelBotones.add(buttonVibracion);
        panelBotones.add(buttonCambiarCilindros);
        panelBotones.add(comboNumeroCilindros);
        panelBotones.add(buttonSalir);


        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.insets = new Insets(20, 10, 20, 10);
        this.setLayout(bag);
        textAreaRPM = new JTextArea();
        JScrollPane scroll = new JScrollPane(textAreaRPM);
        //
        JLabel labelTitulo = new JLabel("CONSOLA KIT CAPELEC");
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



public static void main(String args[]){
        PanelServicioCapelec gui = new PanelServicioCapelec();
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
        //serialPort.setDTR(true);
 }//end of inicializarPuertoSerial


private SerialPort connect(String portName, int baudRate, int dataBits, int stopBits,int parity ) throws Exception
    {
        bufferMed = new BufferStrings();
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
                serialPort.enableReceiveTimeout(250);                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
//                out.flush();
                capelecServicios.setOut(out);
                
                
                out.write(35);
                out.write(82);
                out.write(83);
                out.write(13);
               // capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);
                Thread.sleep(100);
                productorCapelec = new ProductorCapelec(in,bufferMed);
                consumidorCapelec = new ConsumidorCapelec(bufferMed,textAreaRPM,radialRPM,linearTemperatura);
                threadProductor = new Thread(productorCapelec);
                threadProductor.start();
                threadConsumidor = new Thread(consumidorCapelec);
                threadConsumidor.start();
                Thread.sleep(100);
                
                System.out.println("Reset enviado");
                
                //capelecServicios.cambiarCilindros(4);
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
        return serialPort;
    }//end of method connect

    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == buttonConectar){
            try{
                inicializarPuertoSerial( (String)(this.comboPuertosSeriales.getSelectedItem()) );
                habilitarBotones();
            }catch(Exception ex){
            }//end of catch
      } else if(e.getSource() == buttonProgramRun){
          capelecServicios.enviarComandoNoParametros(CapelecSerial.RUN_PROGRAM);
      } else if (e.getSource() == buttonProgramStop){
          capelecServicios.enviarComandoNoParametros(CapelecSerial.PROGRAM_STOP);
      } else if(e.getSource() == buttonResetRequest){
          System.out.println("Reset enviado");
          capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);
      } else if(e.getSource() == buttonTension){
          capelecServicios.medirUsandoBateria();
      } else if(e.getSource() == buttonVibracion){
          capelecServicios.medirUsandoVibracion();
      } else if(e.getSource() == buttonCambiarCilindros){
          String strCilindros = (String)comboNumeroCilindros.getSelectedItem();
          int numCilindros = Integer.parseInt(strCilindros);
          capelecServicios.cambiarCilindros(numCilindros);
      } else if(e.getSource() == buttonSalir){
            try {
                desconectar();
            } catch (Throwable ex) {
                Logger.getLogger(PanelServicioCapelec.class.getName()).log(Level.SEVERE, null, ex);
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
      //  radialRPM.setScaleDividerPower(2);
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
       if(productorCapelec != null){
           productorCapelec.setTerminado(true);
           productorCapelec = null;
       }

       if(consumidorCapelec != null)
       {
           consumidorCapelec.setTerminado(true);
           consumidorCapelec = null;
       }
        
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
        capelecServicios.setOut(sp.getOutputStream());
        c.setRadialRPM(radialRPM);
        c.setTxtArea(textAreaRPM);
        c.setLinearTemperatura(linearTemperatura);//no cierra el puerto porque no establece las variables de clase
        //es decir no hace que las variables de instancia que referencian el puerto serial y el consumidor
        //apunten a los parametros del constructor.
    }//end of panelConfigurar

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        radialRPM = null;
        linearTemperatura = null;
        System.err.println(" Finalizado panel servicio");        
    }


    
}//end of class
