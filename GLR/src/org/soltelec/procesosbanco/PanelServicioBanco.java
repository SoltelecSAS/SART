/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import com.soltelec.loginadministrador.LoginServiceCDA;
import com.soltelec.loginadministrador.UtilPropiedades;
import conexion.PersistenceController;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.helper.Helper;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.horiba.BancoHoriba;
import org.soltelec.models.controllers.CalibracionController;
import org.soltelec.models.controllers.CalibracionDosPuntosController;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.procesosbanco.verificacion.CallableSubidaPresion;
import org.soltelec.procesosbanco.verificacion.PanelProcesoVerificacion;
import org.soltelec.procesosbanco.verificacion.PanelProgresoPresion;
import org.soltelec.procesosbanco.verificacion.UtilRegistrarVerificacion;
import org.soltelec.util.*;
import org.soltelec.util.capelec.BancoCapelec;

/**
 * Panel de servicio del banco de gasolina primera clase que se realizo
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class PanelServicioBanco extends JPanel implements ActionListener {

    boolean solenoide1 = true; //para mantener un estado de las entradas y salidas
    boolean solenoide2 = true;//cuando se da click sobre el boton entonces cambia el estado
    boolean calsol1 = true;//cuando se da click cambia a false
    boolean calsol2 = true;
    boolean drain = true;
    boolean sample = true;
    boolean habilitarAuditoria = false;
    private JComboBox puertosJCombo;
    private ButtonGroup grupoBitsParada;
    private JTextField baudRateJTextField, dataBitsJTextField;
    //private JTextField datosEnviarJTextField;
    private JRadioButton unBitParada, unoMedioBitsParada, dosBitsParada;
    private JCheckBox bitParidadCheckBox;
    // private JTextArea datosRecibidosJTextArea;
    private PanelMedidasSteel panel;//panel en el que se muestran las medidas
    //private WorkerCalentamiento workerCalentamiento;
    JPanel panelConfigPuerto;
    private JButton zeroJButton, limpiarJButton;//Calibraciòn de cero
    private JButton infoCalJButton, cal2JButton;//Informacion del punto de calibracion y boton para la calibracion de dos puntos
    private JButton fugasJButton, ruidoJButton;
    private JButton buttonSol1, buttonSol2, buttonBombaMuestras, buttonBombaDrenaje;
    private JButton buttonDesbloquear;
    private JButton buttonCalSol1, buttonCalSol2, buttonSalir;//Demas botones necesarios
    private JButton enviarJButton, conectarJButton, exactitudJButton, bloquearEquipolJButton, verificacionJButton, ultLectGases;
    JPanel panelBotones;//en este panel se pondrán los botones correspondientes

    //LectorMensajesMaquina lector = new LectorMensajesMaquina();
    int bitsParada = SerialPort.STOPBITS_1;
    OutputStream out;
    InputStream in;
    BancoGasolina banco;// = new BancoSensors();//instancia uno de propósito general
    private WorkerDatos workerDatos;//este es el que se encarga de mostrar continuamente los datos
    //variables para la manipulación del puerto
    Map<String, CommPortIdentifier> mapaPuertos;

    private String serialBanco;

    public PanelServicioBanco() {
        instanciarBanco();
        inicializarInterfaz();
    }//end of constructor
    
        
    private void inicializarInterfaz() {

        ManejadorBotones manejador = new ManejadorBotones();//maneja los botones para el serial,estado del banco,datos,calibracion 2 ptos, infoCal, fugas,ruido
        panelConfigPuerto = new JPanel();//panel para poner los elementos de la configuracion del puerto
        GridBagConstraints c = new GridBagConstraints();
        GridBagLayout bag = new GridBagLayout();
        //a la izquierda todos los controles para la configuración del puerto

        this.setLayout(bag);
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.1;//10% de la pantalla para este panel
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        GridLayout g = new GridLayout(10, 2, 5, 5);//10 filas 2 columnas
        //***poner todos los elementos de la interfaz gráfica para la configuración del puerto
        //en el panel config puerto
        panelConfigPuerto.setLayout(g);
        panelConfigPuerto.add(new JLabel("Puerto: "));
        mapaPuertos = PortSerialUtil.listPorts();
        String[] opciones;
        Object[] objetos = mapaPuertos.keySet().toArray();
        opciones = new String[objetos.length];
        for (int i = 0; i < objetos.length; i++) {
            opciones[i] = (String) objetos[i];
        }

        puertosJCombo = new JComboBox(opciones);
        panelConfigPuerto.add(puertosJCombo);/////////////--------------------------------------------------------------
        panelConfigPuerto.add(new JLabel("Baud Rate: "));////////////////------------------------------------------------------
        baudRateJTextField = new JTextField("9600");
        panelConfigPuerto.add(baudRateJTextField);
        panelConfigPuerto.add(new JLabel("Numero Bits"));
        dataBitsJTextField = new JTextField("8");
        panelConfigPuerto.add(dataBitsJTextField);
        panelConfigPuerto.add(new JLabel("Paridad?: "), c);
        bitParidadCheckBox = new JCheckBox();
        panelConfigPuerto.add(bitParidadCheckBox);
        panelConfigPuerto.add(new JLabel("Bits Parada: "));
        unBitParada = new JRadioButton("1", true);
        unBitParada.addItemListener(new manejadorRadioButtons(SerialPort.STOPBITS_1));
        unoMedioBitsParada = new JRadioButton("1.5", false);
        unoMedioBitsParada.addItemListener(new manejadorRadioButtons(SerialPort.STOPBITS_1_5));
        dosBitsParada = new JRadioButton("2", false);
        dosBitsParada.addItemListener(new manejadorRadioButtons(SerialPort.STOPBITS_2));
        panelConfigPuerto.add(unBitParada);
        panelConfigPuerto.add(new JLabel(""));
        panelConfigPuerto.add(unoMedioBitsParada);
        panelConfigPuerto.add(new JLabel(""));
        panelConfigPuerto.add(dosBitsParada);
        grupoBitsParada = new ButtonGroup();
        grupoBitsParada.add(unBitParada);
        grupoBitsParada.add(unoMedioBitsParada);
        grupoBitsParada.add(dosBitsParada);
        add(panelConfigPuerto, c);//en la fila 0 columna 0 se pone al panelConfigPuerto
        conectarJButton = new JButton("Conectar");
        conectarJButton.addActionListener(new manejadorConectar());
        panelConfigPuerto.add(conectarJButton);
        //poner el JButton para enviar los datos
        c.weightx = 0.1;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        enviarJButton = new JButton("Enviar");
        enviarJButton.addActionListener(new manejadorEnviar());
        enviarJButton.setEnabled(false);//sin conectar antes no se debe enviar los datos
        
        //Configuracion de todos los botones panel superior de la interfaz gráfica
        panelBotones = new JPanel();
        GridLayout grid = new GridLayout(2, 7, 5, 5);//2 filas siete columnas
        panelBotones.setLayout(grid);
        buttonBombaDrenaje = new JButton("Confg. Pipetas");
        buttonBombaDrenaje.addActionListener(this);
        buttonBombaDrenaje.setEnabled(false);
        buttonBombaMuestras = new JButton("Sample Pump");
        buttonBombaMuestras.addActionListener(this);
        buttonBombaMuestras.setEnabled(false);
        buttonSol1 = new JButton("Air Sol1");
        buttonSol1.addActionListener(this);
        buttonSol1.setEnabled(false);
        buttonSol2 = new JButton("Air Sol2");
        buttonSol2.addActionListener(this);
        buttonSol2.setEnabled(false);
        buttonCalSol1 = new JButton("Cal Sol1");
        buttonCalSol1.addActionListener(this);
        buttonCalSol1.setEnabled(false);
        buttonCalSol2 = new JButton("Cal Sol2");
        buttonCalSol2.addActionListener(this);
        buttonCalSol2.setEnabled(false);
        //poner el Action Listener
        panelBotones.add(buttonBombaDrenaje);
        panelBotones.add(buttonBombaMuestras);
        panelBotones.add(buttonSol1);
        panelBotones.add(buttonSol2);
        panelBotones.add(buttonCalSol1);
        panelBotones.add(buttonCalSol2);
// Para el Numero Serial
        bloquearEquipolJButton = new JButton("Bloquear");
        bloquearEquipolJButton.setEnabled(false);
        bloquearEquipolJButton.addActionListener(manejador);
        panelBotones.add(bloquearEquipolJButton);
//Poner el boton Calibracion de Cero
        zeroJButton = new JButton("Cero");
        zeroJButton.setEnabled(false);
        zeroJButton.addActionListener(this);
        panelBotones.add(zeroJButton);
//Poner el boton de Calibracion de Uno y Dos Puntos
        infoCalJButton = new JButton("InfoPtoCal");//infoCalJButton.setEnabled(false);
        infoCalJButton.addActionListener(manejador);
        cal2JButton = new JButton("Verif.2Ptos");
        cal2JButton.addActionListener(manejador);
        cal2JButton.setEnabled(false);
        panelBotones.add(infoCalJButton);
        panelBotones.add(cal2JButton);
//Poner los botones de Prueba de fugas y
        fugasJButton = new JButton("Fugas");
        fugasJButton.setEnabled(false);
        fugasJButton.addActionListener(manejador);
        ruidoJButton = new JButton("Ruido");
        ruidoJButton.setEnabled(false);
        ruidoJButton.addActionListener(manejador);
        panelBotones.add(fugasJButton);
        panelBotones.add(ruidoJButton);
//prueba de ruido
        //c.gridx = 0; c.gridy= 3;
        verificacionJButton = new JButton("Verificacion");
        verificacionJButton.setEnabled(false);
        verificacionJButton.addActionListener(manejador);
        panelBotones.add(verificacionJButton, c);
//prueba exactitud
///////////*************************actionlistener
        //c.gridx = 0; c.gridy = 4;
        exactitudJButton = new JButton("Exactitud y Repet");
        exactitudJButton.setEnabled(false);
        exactitudJButton.addActionListener(manejador);
        panelBotones.add(exactitudJButton, c);

        //panelBotones.add(buttonSalir);
        //poner el ActionListener
        limpiarJButton = new JButton("Limpiar");
        limpiarJButton.addActionListener(this);
        panelBotones.add(limpiarJButton);

        buttonDesbloquear = new JButton("Desbloquear");
        buttonDesbloquear.addActionListener(manejador);
        panelBotones.add(buttonDesbloquear);

        ultLectGases = new JButton("Ult. Lect. Gases");
        ultLectGases.addActionListener(manejador);
        panelBotones.add(ultLectGases, c);
        buttonSalir = new JButton("Salir");
        buttonSalir.setForeground(Color.red);
        buttonSalir.addActionListener(this);
        panelBotones.add(buttonSalir);

        c.weightx = 0.9;//90% de la pantalla a partir de este momento
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        c.weighty = 0.15;
        c.fill = GridBagConstraints.BOTH;
        panelBotones.setEnabled(false);//Deshabilitar todos los botones hasta conectar
        panelBotones.setVisible(false);
        add(panelBotones, c);//en la columna 1 fila 0 se pone el panel de los botones
        //c.weighty = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.7;
        //c.fill = GridBagConstraints.VERTICAL;
        //c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridwidth = GridBagConstraints.REMAINDER;
//        datosEnviadosJTextArea = new JTextArea();
//        datosEnviadosJTextArea.setEnabled(false);//al inicio deben deshabilitarse este control
//        add(datosEnviadosJTextArea,c);
//JLabelPersonalizada etiqueta = new  JLabelPersonalizada("fabian");
//add(etiqueta,c);
        c.fill = GridBagConstraints.BOTH;
        panel = new PanelMedidasSteel();
        panel.setVisible(false);
        add(panel, c);//en la columna 1 fila 1 se pone el panel con los displays
        //c.weighty = 0.5;
        c.weighty = 0.15;
        //poner una JTextArea para la TextArea de recibidos
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.REMAINDER;

//        datosRecibidosJTextArea = new JTextArea();
//        datosRecibidosJTextArea.setEnabled(false);
//        datosRecibidosJTextArea.setVisible(false);
//        datosRecibidosJTextArea.setRows(5);
//       add(new JScrollPane(datosRecibidosJTextArea),c);
    }//end of inicializarInterfaz

    public void callZero() {
        try {
            workerDatos.setInterrumpido(true);

            panel.getBarraCalentamiento().setMaximum(30);

            WorkerCero workerCero = new WorkerCero(panel.getLabelMensaje(), banco);

            workerCero.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("progress")) {
                        panel.barraCalentamiento.setValue((Integer) evt.getNewValue());
                    }
                }
            });
            workerCero.execute();
            //panelCero.iniciarProceso();
        } catch (NullPointerException ne) {
            System.out.println("Haciendo calibración de cero, error no hilo de datos");
            Logger.getRootLogger().error("Haciendo calibración de cero, error no hilo de datos", ne);
        } finally {
            workerDatos.setInterrumpido(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == zeroJButton) {
            try {
                workerDatos.setInterrumpido(true);

                panel.getBarraCalentamiento().setMaximum(30);

                WorkerCero workerCero = new WorkerCero(panel.getLabelMensaje(), banco);

                workerCero.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("progress")) {
                            panel.barraCalentamiento.setValue((Integer) evt.getNewValue());
                        }
                    }
                });
                workerCero.execute();
            } catch (NullPointerException ne) {
                System.out.println("Haciendo calibración de cero, error no hilo de datos");
                Logger.getRootLogger().error("Haciendo calibración de cero, error no hilo de datos", ne);
            } finally {
                workerDatos.setInterrumpido(false);
            }

        }//end if comprobacion boton
        else if (e.getSource() == buttonSol2) {
            workerDatos.setInterrumpido(true);
            banco.encenderSolenoide2(solenoide2);
            refrescarLedsIO();
            solenoide2 = !solenoide2;
            workerDatos.setInterrumpido(false);
        } else if (e.getSource() == buttonSol1) {
            workerDatos.setInterrumpido(true);
            banco.encenderSolenoideUno(solenoide1);
            refrescarLedsIO();
            solenoide1 = !solenoide1;
            workerDatos.setInterrumpido(false);
        } else if (e.getSource() == buttonBombaDrenaje) {
            Window w = SwingUtilities.getWindowAncestor(ruidoJButton);
            PanelPipetas pn = new PanelPipetas();
            pn.setVisible(true);
            JFrame.setDefaultLookAndFeelDecorated(false);
            JDialog dlg = new JDialog(w);
            dlg.setTitle("Sart 1.7.3 Panel Mantenimiento");
            dlg.add(pn);
            dlg.setSize(1200, 700);
            dlg.setModal(true);
            dlg.setLocationRelativeTo(null);
            dlg.setVisible(true);
            dlg.setResizable(false);
        } else if (e.getSource() == buttonBombaMuestras) {
            workerDatos.setInterrumpido(true);
            banco.encenderBombaMuestras(sample);
            refrescarLedsIO();
            sample = !sample;
            workerDatos.setInterrumpido(false);
        } else if (e.getSource() == buttonCalSol1) {
            workerDatos.setInterrumpido(true);
            banco.encenderCalSol1(calsol1);
            refrescarLedsIO();
            calsol1 = !calsol1;
            workerDatos.setInterrumpido(false);
        } else if (e.getSource() == buttonCalSol2) {
            workerDatos.setInterrumpido(true);
            banco.encenderCalSol2(calsol2);
            refrescarLedsIO();
            calsol2 = !calsol2;
            workerDatos.setInterrumpido(false);
        } else if (e.getSource() == limpiarJButton) {
            ProcesoDrenaje procesoDrenaje = new ProcesoDrenaje(banco, panel.getBarraCalentamiento(), panel.getLabelMensaje());
            (new Thread(procesoDrenaje)).start();
        } else if (e.getSource() == buttonDesbloquear) {
            refrescarLedsIO();
        } else if (e.getSource() == buttonSalir) {
            this.desconectar();
            (SwingUtilities.getWindowAncestor(this)).dispose();
        } else {
        }

    }

    private void refrescarLedsIO() {
        short estadoShort = banco.mapaFisicoES((short) 0, false);
        boolean[] booleanEstado = UtilGasesModelo.shortToFlags(estadoShort);
        //Para el caaso del BancoSensors
        if (banco instanceof BancoSensors) {//Refresca los leds verdes
            panel.getLedCalSol1().setLedOn(booleanEstado[0]);
            panel.getLedCalSol2().setLedOn(booleanEstado[1]);
            panel.getLedSolenoideUno().setLedOn(booleanEstado[2]);
            panel.getLedSolenoideDos().setLedOn(booleanEstado[3]);
            panel.getLedBombaMuestra().setLedOn(booleanEstado[4]);
            panel.getLedBombaDrenaje().setLedOn(booleanEstado[5]);

        } else if (banco instanceof BancoCapelec) {
            panel.getLedBombaMuestra().setLedOn(booleanEstado[7]);
            panel.getLedBombaDrenaje().setLedOn(booleanEstado[6]);
            panel.getLedSolenoideUno().setLedOn(booleanEstado[5]);
            panel.getLedSolenoideDos().setLedOn(booleanEstado[4]);
            panel.getLedCalSol2().setLedOn(false);
            panel.getLedCalSol1().setLedOn(false);

        } else if (banco instanceof BancoHoriba) {
            panel.getLedBombaMuestra().setLedOn(booleanEstado[6]);
            panel.getLedSolenoideUno().setLedOn(booleanEstado[0]);
            panel.getLedBombaDrenaje().setLedOn(booleanEstado[7]);
            panel.getLedSolenoideDos().setLedOn(booleanEstado[1]);
            panel.getLedCalSol1().setLedOn(booleanEstado[2]);
            panel.getLedCalSol2().setLedOn(booleanEstado[3]);
        }
    }

    /**
     * Crea una instancia del Banco de Gasolina dependiendo de la marca del
     * dispositivo que se configura en el archivo propiedades.properties como
     * MarcaBanco
     */
    private void instanciarBanco() {
        String marcaBanco = "";

        try {
            marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
        } catch (FileNotFoundException ex) {
            Logger.getRootLogger().error("Archivo de propiedades no encontrado", ex);
        } catch (IOException ex) {
            Logger.getRootLogger().error("Error abriendo el archivo de propiedades", ex);
        }

        if (marcaBanco.equalsIgnoreCase("Sensors")) {
            banco = new BancoSensors();
        } else if (marcaBanco.equalsIgnoreCase("Capelec")) {
            banco = new BancoCapelec();
        } else if (marcaBanco.equalsIgnoreCase("Horiba")) {
            banco = new BancoHoriba();
        }
    }

    private String cargarInformacion(String serial) {
        StringBuilder sb = new StringBuilder();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMMM-yyyy hh:mm aaa");

        try {
            EntityManager em = PersistenceController.getEntityManager();
            System.out.println(" abri el EM: " + em.toString());
            sb.append(" ").append(UtilInfoServicioGases.obtenerNombreCDA(em));
            System.out.println("valor del cda " + sb.toString());
            EquipoController eqpCont = new EquipoController();
            String trama = eqpCont.findIdEqpBySerial(serial);
            int posCut = trama.indexOf("&");
            Integer idEquipo = Integer.parseInt(trama.substring(0, posCut));
            System.out.println("el identificador equipo " + idEquipo);
            Date fechaFugas = new CalibracionController().findPruebaFugas(idEquipo).getFecha();
            System.out.println("cargue fecha fugas" + fechaFugas.getTime());
            if (fechaFugas != null) {
                String strFugas = sdf.format(fechaFugas);
                sb.append(" FECHA FUGAS: ").append(strFugas);
                System.out.println(" fecha fugas" + sb.toString());
            }

            Date fechaCalibracion = new CalibracionDosPuntosController().findCalibracionDosPuntos(idEquipo).getFecha();
            System.out.println("cargue fecha Calibracion" + fechaCalibracion.getTime());
            if (fechaCalibracion != null) {
                String strCalibracion = sdf.format(fechaCalibracion);
                sb.append("\nFECHA VERIFICACION: ").append(strCalibracion);
                System.out.println("entre condicional fecha Calibracion");
            }
            //System.out.println("Fecha de Fugas: " + strFugas);
            //System.out.println("Fecha de Verificacion" + strCalibracion);            
            //String marcaBanco = org.soltelec.util.UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
            EquipoController controller = new EquipoController();
            System.out.println("el numero del serial es :" + serial);
            String marcaBanco = trama.substring(posCut + 1, trama.length());
            System.out.println("la marca de banco es:" + marcaBanco);
            sb.append(" Marca Banco: ").append(marcaBanco);
            System.out.println("MARCA: " + marcaBanco);

        } catch (IOException | SQLException | ClassNotFoundException exc) {
            Mensajes.mostrarExcepcion(exc);
        }
        return sb.toString();
    }

    public void habilitarBotonnesAuditoria() {

        habilitarAuditoria = true;

    }
    //Hace la conexión con el puertoSerial

    class manejadorConectar implements ActionListener {

        boolean primeraVez = true;

        @Override
        public void actionPerformed(ActionEvent e) {
            int baudRate = Integer.parseInt(baudRateJTextField.getText());
            int paridad = bitParidadCheckBox.isSelected() ? SerialPort.PARITY_MARK : SerialPort.PARITY_NONE;
            int databits = Integer.parseInt(dataBitsJTextField.getText());
            System.out.println("PASE X XX 3");
            switch (databits) {
                case 5:
                    databits = SerialPort.DATABITS_5;
                case 6:
                    databits = SerialPort.DATABITS_6;
                case 7:
                    databits = SerialPort.DATABITS_7;
                case 8:
                    databits = SerialPort.DATABITS_8;
            }
            String localPortName = (String) puertosJCombo.getSelectedItem();
            CommPortIdentifier portIdentifier = mapaPuertos.get(localPortName);
            System.out.println("PASE X C1");
            if (portIdentifier.isCurrentlyOwned()) {
                JOptionPane.showMessageDialog(null, "Error: Port is currently in use");
            } else {
                CommPort commPort;
                try {
                    commPort = portIdentifier.open(this.getClass().getName(), 1000);
                    if (commPort instanceof SerialPort) {
                        System.out.println("PASE X C2");
                        SerialPort serialPort = (SerialPort) commPort;
                        //serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                        serialPort.setSerialPortParams(baudRate, databits, bitsParada, paridad);
                        serialPort.setDTR(true);
                        System.out.println("PASE X C4");
                        //serialPort.setRTS(true);
                        //  serialPort.disableReceiveTimeout();
                        // serialPort.enableReceiveThreshold(1);
                        serialPort.enableReceiveTimeout(100);
                        in = serialPort.getInputStream();
                        out = serialPort.getOutputStream();
                        System.out.println("PASE X C5");
                        if (banco == null) {
                            System.out.println("la intancia no exite papa");
                        }
                        banco.setPuertoSerial((SerialPort) commPort);
                        //banco.setIn(in);//Inicializar apropiadamente los flujos
                        //banco.setOut(out);
                    }//end if
                    else {
                        System.out.println("Error: Only serial ports are handled by this example.");
                    }//ned else
                    for (Component c : panelConfigPuerto.getComponents()) {
                        c.setEnabled(false);
                    }//end for
                    System.out.println("PASE X C7");
                    panelConfigPuerto.setVisible(false);//opcional
                    panel.setVisible(true);
                    //datosRecibidosJTextArea.setVisible(true);
                    panelBotones.setVisible(true);
                    conectarJButton.setEnabled(false);
                    enviarJButton.setEnabled(true);
                    //datosRecibidosJTextArea.setEnabled(true);
                    //datosRecibidosJTextArea.setEditable(false);
                    exactitudJButton.setEnabled(true);
                    verificacionJButton.setEnabled(true);
                    bloquearEquipolJButton.setEnabled(true);
                    buttonBombaDrenaje.setEnabled(true);
                    buttonBombaMuestras.setEnabled(true);
                    buttonCalSol1.setEnabled(true);
                    buttonCalSol2.setEnabled(true);
                    buttonSol1.setEnabled(true);
                    System.out.println("PASE X C9");
                    buttonSol2.setEnabled(true);
                    zeroJButton.setEnabled(true);
                    ruidoJButton.setEnabled(true);
                    fugasJButton.setEnabled(true);
                    cal2JButton.setEnabled(true);
                    exactitudJButton.setEnabled(true);
                    System.out.println("PASE X C18");
                    if (!habilitarAuditoria) {
                        buttonDesbloquear.setEnabled(false);
                        exactitudJButton.setEnabled(false);
                        ruidoJButton.setEnabled(false);
                        bloquearEquipolJButton.setEnabled(false);
                    }
                    ///Iniciar toma de datos
                    //MedicionGases gases = banco.obtenerDatos();
                    //banco.encenderBombaMuestras(false);

                    //datosJButton.setText("Parar");
                    System.out.println("PASE X C4");
                    if (primeraVez || workerDatos.isTerminado()) {
                        workerDatos = new WorkerDatos(panel, panelBotones);
                        workerDatos.setBanco(banco);
                        workerDatos.execute();
                        primeraVez = false;
                    } else {
                        workerDatos.setInterrumpido(false);

                    }

                    //datosRecibidosJTextArea.setText(gases.toString());
                } catch (PortInUseException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Port is currently in use");
                } catch (IOException ioexc) {
                    JOptionPane.showMessageDialog(null, "Error IO");
                } catch (gnu.io.UnsupportedCommOperationException ge) {
                    JOptionPane.showMessageDialog(null, "Error Unssupprted Comm operation");
                } catch (NullPointerException ne) {
                    //    datosRecibidosJTextArea.setText("MAQUINA DESCONECTADA");                
                }
            }//end else
            
            try {
                serialBanco = banco.numeroSerial();
                System.out.println("Nro de Serial que recojo del Banco " + banco.numeroSerial());
                panel.mostrarInfo(cargarInformacion(serialBanco));
            } catch (Exception ex) {
                serialBanco = "No conectado";
            }
        }//end of actionPerformed

    }//end of class

    public void desconectar() {
        if (this.workerDatos != null) {
            this.workerDatos.setInterrumpido(true);
            this.workerDatos.setTerminado(true);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException iexc) {

        }
        if (banco.getPuertoSerial() != null) {
            banco.getPuertoSerial().close();
        }
    }

    public BancoGasolina getBanco() {
        return banco;
    }

    class manejadorRadioButtons implements ItemListener {

        int localBitsParada;

        public manejadorRadioButtons(int losBitsParada) {
            this.localBitsParada = losBitsParada;
        }

        public void itemStateChanged(ItemEvent e) {
            bitsParada = localBitsParada;
        }

    }

    class manejadorEnviar implements ActionListener {

        boolean prender = true;

        public void actionPerformed(ActionEvent e) {
            Mensaje recibido = null;
            Mensaje msg1 = new Mensaje((short) 4, (short) 0, (short) 0, null);
            short[] datos = {1, 16, 0};
            Mensaje msg2 = new Mensaje((short) 8, (short) 0, (short) 3, datos);
            if (prender) {
            } else {
            }
            short dato = banco.mapaFisicoES((short) 0, false);
            System.out.println("Mapa de Bombas: " + dato);
            banco.mapaFisicoES((short) 32, true);
            if (recibido != null) {
                System.out.println("Mensaje recibido: " + recibido);
            }
        }//end of method

    }//end of class

    class ManejadorBotones implements ActionListener {

        String unaCadena = "";
        boolean iniciar = true;
        boolean primeraVez = true;
        private PanelProgreso panelProgreso;

        @Override
        @SuppressWarnings("empty-statement")
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == bloquearEquipolJButton) {//voy a poner un JDialog aqui
                //String serial = banco.numeroSerial();
                //UtilRegistrarVerificacion.bloquearEquipo(serial);
                //datosRecibidosJTextArea.setText(unaCadena);
                BloqueoEquipoUtilitario.bloquearEquipo(
                "Se bloqueo desde el boton de bloquear equipo");
            }
            if (e.getSource() == buttonDesbloquear) {//voy a poner un JDialog aqui
                //String serial = banco.numeroSerial();
                //UtilRegistrarVerificacion.desBloquearEquipo(serial);
                //datosRecibidosJTextArea.setText(unaCadena);
                BloqueoEquipoUtilitario.validarYEliminarArchivo();
            } else if (e.getSource() == exactitudJButton) {
                Window w = SwingUtilities.getWindowAncestor(ruidoJButton);
                boolean isSubida = true;
                int tiempo = Integer.parseInt(JOptionPane.showInputDialog("POR FAVOR INGRESE EL TIEMPO DE TOMA DE DATOS"));
                if (isSubida) {
                    try {
                        UtilInfoUsuario utilInfoUsuario = new UtilInfoUsuario();
                        String serialResolucion = utilInfoUsuario.obtenerSerialResolucion(banco.numeroSerial());
                        PanelProgreso progreso = new PanelProgreso();
                        JDialog dlg = new JDialog(w);
                        dlg.setModal(true);
                        dlg.setSize(dlg.getToolkit().getScreenSize());
                        dlg.add(progreso);
                        dlg.setTitle("Muestra Lectura Gases en " + tiempo + " Seg.");
                        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dlg.setResizable(false);
                        ProcesoRuido procesoRuido = new ProcesoRuido(progreso, banco, workerDatos, serialResolucion);
                        procesoRuido.setTIEMPO(tiempo);
                        (new Thread(procesoRuido)).start();
                        dlg.setVisible(true);
                    } catch (Exception ex) {
                        java.util.logging.Logger.getLogger(PanelServicioBanco.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == ultLectGases) {
                Window w = SwingUtilities.getWindowAncestor(ruidoJButton);
                boolean isSubida = true;
                if (isSubida) {
                    PanelProgreso progreso = new PanelProgreso();
                    JDialog dlg = new JDialog(w);
                    dlg.setModal(true);
                    dlg.setSize(dlg.getToolkit().getScreenSize());
                    dlg.add(progreso);
                    dlg.setTitle("Informacion de la Ultima Lectura de Gases");
                    dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dlg.setResizable(false);
                    ProcesoRuido proceso = new ProcesoRuido(progreso, workerDatos);
                    proceso.lodUltLectGases();
                    dlg.setVisible(true);
                }
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == verificacionJButton) {//Verificacion
                workerDatos.setInterrumpido(true);
                JDialog dlg = new JDialog();
                dlg.setModal(true);
                dlg.setSize(dlg.getToolkit().getScreenSize());
                PanelProcesoVerificacion panelProcesoVerificacion = new PanelProcesoVerificacion(banco);
                dlg.add(panelProcesoVerificacion);
                dlg.setVisible(true);
                workerDatos.setInterrumpido(false);
                //datosRecibidosJTextArea.setText(gases.toString());
            } else if (e.getSource() == cal2JButton) {
                try {
                    //Hacer la calibración de dos puntos
                    workerDatos.setInterrumpido(true);
                    JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(false));
                    pane.setLocale(new Locale("ES"));
                    pane.setBannerText("USUARIO");
                    pane.setErrorMessage("Usuario o Contrasenia Incorrecta");
                    //LoginDialog.setVisible(true);
                    RegVefCalibraciones r = new RegVefCalibraciones();
                    // JOptionPane.showMessageDialog(null,"serial bco: "+banco.numeroSerial());
                    String analizador = r.naturalezaBco(banco.numeroSerial());
                    JOptionPane.showMessageDialog(null, "naturaleza del  analizador: " + analizador);
                    JXLoginPane.Status status = JXLoginPane.showLoginDialog(null, pane);
                    if (status == JXLoginPane.Status.SUCCEEDED) {
                        try {
                            String nombreUsuario = pane.getUserName();
                            UtilInfoUsuario utilInfoUsuario = new UtilInfoUsuario();
                            int idUsuario = utilInfoUsuario.obtenerIdUsuarioPorNombre(nombreUsuario);
                            Window w = SwingUtilities.getWindowAncestor(cal2JButton);
                            JDialog dlg = new JDialog(w);
                            dlg.setModal(true);
                            dlg.setSize(dlg.getToolkit().getScreenSize());
                            PanelCalibracion2Pts panelC = new PanelCalibracion2Pts(banco, analizador, idUsuario);//en panel calibracion dos puntos esta la llamada al proceso de calibracion
                            dlg.add(panelC);
                            dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            dlg.setResizable(false);
                            dlg.setVisible(true);
                            panel.getBarraCalentamiento().setMaximum(30);
                            WorkerCero workerCero = new WorkerCero(panel.getLabelMensaje(), banco);
                            workerCero.addPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getPropertyName().equals("progress")) {
                                        panel.barraCalentamiento.setValue((Integer) evt.getNewValue());
                                    }
                                }
                            });
                            workerCero.execute();
                            panel.mostrarInfo(cargarInformacion(serialBanco));

                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error consultando datos de usuario" + ex.getMessage());
                            Logger.getRootLogger().error("Error consultando el usuario", ex);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error consultando datos de usuario" + ex.getMessage());
                            Logger.getRootLogger().error("Error consultando el usuario", ex);
                        }
                    }
                    workerDatos.setInterrumpido(false);
                } catch (SQLException ex) {
                    java.util.logging.Logger.getLogger(PanelServicioBanco.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(PanelServicioBanco.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(PanelServicioBanco.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (e.getSource() == infoCalJButton) {//Obtener la informacion de los puntos de calibracion
                workerDatos.setInterrumpido(true);
                StringBuilder sb = new StringBuilder();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getRootLogger().error("Error abriendo archivo de propiedades", ex);
                }
                serialBanco = banco.numeroSerial();
                EntityManager em = PersistenceController.getEntityManager();
                EquipoController eqpCont = new EquipoController();
                String cadenaCalibracion = null;
                String[] flujoCali = null;
                try {
                    cadenaCalibracion = eqpCont.lastCalib2Pto(serialBanco);
                    flujoCali = cadenaCalibracion.split(";");
                    System.out.println("---- Valores del flujo de la calibracion ----");
                    for (int i = 0; i < flujoCali.length; i++) {
                        String string = flujoCali[i];
                        System.out.println(String.format("valor %d: %s ", i, string));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Disculpe no puedo Presentar los valores de la Ultima lectura calibracion");
                }
                PuntoCalibracion ptoCalibracion1 = new PuntoCalibracion(); //banco.leerDatosCalibracionPto1(1);
                ptoCalibracion1.setBancoCo(Double.parseDouble(flujoCali[0]));
                ptoCalibracion1.setBancoCo2(Double.parseDouble(flujoCali[1]));
                ptoCalibracion1.setBancoHc(Double.parseDouble(flujoCali[2]));
                ptoCalibracion1.setValorCO((short) Double.parseDouble(flujoCali[6]));
                ptoCalibracion1.setValorCO2((short) Double.parseDouble(flujoCali[7]));
                ptoCalibracion1.setValorHC((short) Double.parseDouble(flujoCali[8]));
                sb.append(ptoCalibracion1);
                sb.append("Muestra Baja\n");
                sb.append(ptoCalibracion1);
                PuntoCalibracion ptoCalibracion2 = banco.leerDatosCalibracionPto1(2);
                sb.append("\nMuestra Alta\n");
                ptoCalibracion2.setBancoCo(Double.parseDouble(flujoCali[9]));
                ptoCalibracion2.setBancoCo2(Double.parseDouble(flujoCali[10]));
                ptoCalibracion2.setBancoHc(Double.parseDouble(flujoCali[11]));
                ptoCalibracion2.setValorCO((short) Double.parseDouble(flujoCali[3]));
                ptoCalibracion2.setValorCO2((short) Double.parseDouble(flujoCali[4]));
                ptoCalibracion2.setValorHC((short) Double.parseDouble(flujoCali[5]));
                sb.append(ptoCalibracion2);
                JOptionPane.showMessageDialog(null, sb.toString());
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == fugasJButton) {
                JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(false));
                pane.setLocale(new Locale("ES"));
                pane.setBannerText("USUARIO");
                pane.setErrorMessage("Usuario o Contrasenia Incorrecta");
                workerDatos.setInterrumpido(true);
                panelProgreso = new PanelProgreso();
                Window w = SwingUtilities.getWindowAncestor(fugasJButton);
                JDialog dlg = new JDialog(w);
                dlg.setModal(true);
                dlg.setSize(dlg.getToolkit().getScreenSize());
                dlg.add(panelProgreso);
                dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dlg.setResizable(false);
                String nombreUsuario = pane.getUserName();
                UtilInfoUsuario utilInfoUsuario = new UtilInfoUsuario();
                int idUsuario = 1;
                try {
                    idUsuario = utilInfoUsuario.obtenerIdUsuarioPorNombre(nombreUsuario);
                } catch (Exception ex) {
                }

                WorkerFugas workerFugas = new WorkerFugas(panelProgreso.getLabelMensaje(), banco, dlg, idUsuario);
                //SwingUtilities.invokeLater(procesoFugas);
                workerFugas.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("progress")) {
                            panelProgreso.getBarraTiempo().setValue((Integer) evt.getNewValue());
                        }
                    }
                });

                workerFugas.execute();
                dlg.setVisible(true);

                while (!workerFugas.isDone());
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == ruidoJButton) {
                try {
                    Window w = SwingUtilities.getWindowAncestor(ruidoJButton);
                    workerDatos.setInterrumpido(true);
                    PanelProgresoPresion panelPresion = new PanelProgresoPresion();
                    JDialog dlgPresion = new JDialog(w);
                    dlgPresion.setModal(true);
                    dlgPresion.setSize(dlgPresion.getToolkit().getScreenSize());
                    dlgPresion.add(panelPresion);
                    dlgPresion.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dlgPresion.setResizable(false);

                    CallableSubidaPresion callableSubidaPresion = new CallableSubidaPresion(panelPresion, banco);
                    ExecutorService exec = Executors.newSingleThreadExecutor();
                    final Future<Boolean> future = exec.submit(callableSubidaPresion);
                    dlgPresion.setVisible(true);
                    boolean isSubida = future.get();
                    dlgPresion.setVisible(false);
                    int tiempo = Integer.parseInt(JOptionPane.showInputDialog("TIEMPO DE TOMA DE DATOS"));

                    dlgPresion.setVisible(false);
                    if (isSubida) {
                        PanelProgreso progreso = new PanelProgreso();
                        JDialog dlg = new JDialog(w);
                        dlg.setModal(true);
                        dlg.setSize(dlg.getToolkit().getScreenSize());
                        dlg.add(progreso);
                        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dlg.setResizable(false);
                        ProcesoRuido procesoRuido = new ProcesoRuido(progreso, banco, workerDatos, "");
                        procesoRuido.setTIEMPO(tiempo);
                        (new Thread(procesoRuido)).start();
                        dlg.setVisible(true);
                    }
                    workerDatos.setInterrumpido(false);
                } catch (InterruptedException | ExecutionException ex) {
                    Mensajes.mostrarExcepcion(ex);
                }
            }
        }//end of actionPerformed

    }//end of class manejadorBotones

}//end of principal class
