/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosopacimetro;

import com.soltelec.conexion_seriales.Consultas;
import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.RuntimeErrorException;
import javax.swing.*;
import org.soltelec.util.PanelTiempoRepuestaOpacimetro;
import org.soltelec.conexion_seriales.Conexion;
import org.soltelec.pruebasgases.PruebaLinealidad;
import org.soltelec.pruebasgases.PruebaLinealidadCapelec;
import org.soltelec.util.*;
import org.soltelec.util.capelec.DialogoFiltroCapelec;
import org.soltelec.util.capelec.OpacimetroCapelec;

/**
 * Clase para implementar una interfaz gráfica para conectarse con el opacimetro
 * y hacer pruebas de comandos
 *
 * @author Gerencia Desarrollo soluciones Tecnologicas
 */
public class PanelServicioOpacimetro extends JPanel {

    //Elementos de la interfaz para la conexión con el puerto serial
    private final PanelMedicionStatus panelMedicionStatus;
    private JPanel panelConfigPuerto;
    private JPanel panelEnvoltor;
    private JComboBox puertosJCombo;
    private ButtonGroup grupoBitsParada;
    private JTextField baudRateJTextField, dataBitsJTextField, datosEnviarJTextField;
    private JRadioButton unBitParada, unoMedioBitsParada, dosBitsParada;
    private JCheckBox bitParidadCheckBox;
    private JButton buttonConectar;
    //variables para la manipulación del puerto
    Map<String, CommPortIdentifier> mapaPuertos;
    private int bitsParada = 1;
    //Elementos para botones
    private JPanel panelBotones;
    private JButton buttonLinealizacion, buttonSerial, buttonConfigLentes, buttonFrecuencia, buttonCalibrar;
    private JButton buttonUmbralTemperatura, buttonInfoHardware, buttonVentiladores, buttonArmarTrigger;
    private JButton buttonDispararTrigger;
    private JButton buttonSalir;
    private JButton buttonIntensidad;
    //CheckBox para leer opacidad Corregida
    private JToggleButton buttonOpacidadCorregida;
    private DialogoFiltroCapelec dialogoFiltros;
    //Elementos del Panel Principal en donde se muestran los //datos y el estado desde el opacimetro

    //variables para la consola
    private Opacimetro opacimetro;//debe ser tratado polimorficamente
    WorkerDatosOpacimetro workerDatos;

    public PanelServicioOpacimetro() {
        seleccionarImplementacionOpacimetro();
        configurarPanelPrincipal();
        inicializarPanelConexion();
        add(panelConfigPuerto);
        inicializarPanelBotones();
        panelEnvoltor.add(panelBotones, BorderLayout.NORTH);
        panelMedicionStatus = new PanelMedicionStatus();
        panelEnvoltor.add(panelMedicionStatus, BorderLayout.CENTER);
        panelEnvoltor.setVisible(false);
        add(panelEnvoltor);
    }

    /**
     * Instancia una implementacion de la interface opacimetro dependiendo de la
     * propiedad MarcaOpacimetro en el archivo propiedades.properties los
     * valores de las marcas son Sensors o Capelec
     */
    private void seleccionarImplementacionOpacimetro() {
        String marcaOpacimetro = null;
        try {
            marcaOpacimetro = UtilPropiedades.cargarPropiedad("MarcaOpacimetro", "propiedades.properties");
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {
        }

        if (marcaOpacimetro == null || marcaOpacimetro.equals("Sensors")) {
            opacimetro = new OpacimetroSensors();
        } else if (marcaOpacimetro.equals("Capelec")) {
            opacimetro = new OpacimetroCapelec();
        } else if (marcaOpacimetro.equalsIgnoreCase("BrianBee")) {
            System.out.println("Entro en intacia BrianBeee panel Sevicio");
            opacimetro = new OpacimetroBrianBee();
        }
    }

    private void inicializarPanelConexion() {

        panelConfigPuerto = new JPanel();
        GridLayout g = new GridLayout(10, 2, 5, 5);
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

        panelConfigPuerto.add(puertosJCombo);
        panelConfigPuerto.add(new JLabel("Baud Rate: "));
        baudRateJTextField = new JTextField("9600");
        panelConfigPuerto.add(baudRateJTextField);
        panelConfigPuerto.add(new JLabel("Numero Bits"));
        dataBitsJTextField = new JTextField("8");
        panelConfigPuerto.add(dataBitsJTextField);
        panelConfigPuerto.add(new JLabel("Paridad?: "));
        bitParidadCheckBox = new JCheckBox();
        panelConfigPuerto.add(bitParidadCheckBox);
        panelConfigPuerto.add(new JLabel("Bits Parada: "));
        unBitParada = new JRadioButton("1", true);
        unBitParada.addItemListener(new ManejadorRadioButtons(SerialPort.STOPBITS_1));
        unoMedioBitsParada = new JRadioButton("1.5", false);
        unoMedioBitsParada.addItemListener(new ManejadorRadioButtons(SerialPort.STOPBITS_1_5));
        dosBitsParada = new JRadioButton("2", false);
        dosBitsParada.addItemListener(new ManejadorRadioButtons(SerialPort.STOPBITS_2));
        panelConfigPuerto.add(unBitParada);
        panelConfigPuerto.add(new JLabel(""));
        panelConfigPuerto.add(unoMedioBitsParada);
        panelConfigPuerto.add(new JLabel(""));
        panelConfigPuerto.add(dosBitsParada);
        grupoBitsParada = new ButtonGroup();
        grupoBitsParada.add(unBitParada);
        grupoBitsParada.add(unoMedioBitsParada);
        grupoBitsParada.add(dosBitsParada);
        buttonConectar = new JButton("Conectar");
        panelConfigPuerto.add(buttonConectar);
        //add(panelConfigPuerto,c);
        buttonConectar.addActionListener(new ManejadorConectar());

    }//end of inicializarPanelConexion

    private void inicializarPanelBotones() {
        ManejadorBotones manejadorBotones = new ManejadorBotones();
        GridLayout grid = new GridLayout(2, 7, 5, 5);//2 filas seis columnas para una rejilla de botones
        panelBotones = new JPanel(grid);
        panelBotones.setBackground(Color.orange);
        //Inicialización de los botones en un rato libre ponerle unas imágenes
        buttonLinealizacion = new JButton("Linealizacion");
        buttonLinealizacion.addActionListener(manejadorBotones);
        buttonSerial = new JButton("Serial");
        buttonSerial.addActionListener(manejadorBotones);
        buttonConfigLentes = new JButton("Config Lentes");
        buttonConfigLentes.addActionListener(manejadorBotones);
        buttonFrecuencia = new JButton("Freq. Muestreo");
        buttonFrecuencia.addActionListener(manejadorBotones);
        buttonCalibrar = new JButton("Calibrar");
        buttonCalibrar.addActionListener(manejadorBotones);
        buttonUmbralTemperatura = new JButton("Umbral °T Gas");
        buttonUmbralTemperatura.addActionListener(manejadorBotones);
        buttonInfoHardware = new JButton("Info. Hardware");
        buttonInfoHardware.addActionListener(manejadorBotones);
        buttonVentiladores = new JButton("Verificacion Datos");
        buttonVentiladores.addActionListener(manejadorBotones);
        buttonArmarTrigger = new JButton("Armar Trigger");
        buttonArmarTrigger.addActionListener(manejadorBotones);
        buttonDispararTrigger = new JButton("Disp. Trigger");
        buttonDispararTrigger.addActionListener(manejadorBotones);
        buttonOpacidadCorregida = new JToggleButton("Op Corregida");
        buttonOpacidadCorregida.addActionListener(manejadorBotones);

        buttonSalir = new JButton("Salir");
        buttonSalir.setForeground(Color.red);
        buttonSalir.addActionListener(manejadorBotones);

        buttonIntensidad = new JButton("Intensidad");
        buttonIntensidad.addActionListener(manejadorBotones);
        // añadir los botones
        JButton[] arregloBotones = {buttonLinealizacion, buttonSerial, buttonConfigLentes,
            buttonFrecuencia, buttonCalibrar, buttonUmbralTemperatura,
            buttonInfoHardware, buttonVentiladores, buttonArmarTrigger,
            buttonDispararTrigger};
        for (JButton btn : arregloBotones) {
            panelBotones.add(btn);
        }//end for
        panelBotones.add(buttonOpacidadCorregida);
        panelBotones.add(buttonSalir);
        panelBotones.add(buttonIntensidad);
    }//end of inicializarPanelPrincipal

    /**
     * Metodo para configurar el Panel Principal en donde se mostraran las
     * medidas que es el que va a ir en el centro en un BorderLayout, en el sur
     * una etiqueta con información
     */
    private void configurarPanelPrincipal() {
        BorderLayout border = new BorderLayout();
        panelEnvoltor = new JPanel(border);
    }//end of configurarPanelPrincipal

    void cerrar() {
        (SwingUtilities.getWindowAncestor(this)).dispose();
    }

    /**
     * Clase inner no anonima, usada para manejar el evento de conexion al
     * puerto serial
     */
    class ManejadorConectar implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int baudRate = Integer.parseInt(baudRateJTextField.getText());
            int paridad = bitParidadCheckBox.isSelected() ? SerialPort.PARITY_MARK : SerialPort.PARITY_NONE;
            int databits = Integer.parseInt(dataBitsJTextField.getText());

            switch (databits) {
                case 5:
                    databits = SerialPort.DATABITS_5;
                    break;
                case 6:
                    databits = SerialPort.DATABITS_6;
                    break;
                case 7:
                    databits = SerialPort.DATABITS_7;
                    break;
                case 8:
                    databits = SerialPort.DATABITS_8;
                    break;
            }

            String localPortName = (String) puertosJCombo.getSelectedItem();
            CommPortIdentifier portIdentifier = mapaPuertos.get(localPortName);
            if (portIdentifier.isCurrentlyOwned()) {
                JOptionPane.showMessageDialog(null, "Error: Port is currently in use");
            } else {
                CommPort commPort;
                try {
                    commPort = portIdentifier.open(this.getClass().getName(), 2000);
                    if (commPort instanceof SerialPort) {
                        SerialPort serialPort = (SerialPort) commPort;
                        serialPort.setSerialPortParams(baudRate, databits, bitsParada, paridad);
                        serialPort.enableReceiveTimeout(100);
                        opacimetro.setPort(serialPort);

                        panelConfigPuerto.setVisible(false);
                        panelEnvoltor.setVisible(true);
                        workerDatos = new WorkerDatosOpacimetro(panelMedicionStatus, opacimetro);
                        (new Thread(workerDatos)).start();
                    }//end if
                    else {
                        System.out.println("Error: Only serial ports are handled by this example.");
                    }//ned else
                } catch (PortInUseException ex) {
                    JOptionPane.showMessageDialog(null, "Error: Port is currently in use");
                } catch (gnu.io.UnsupportedCommOperationException ge) {
                    JOptionPane.showMessageDialog(null, "Error Unssupprted Comm operation");
                }//end catch

            }//end else
        }//end of method actionPermormed
    }//end of class ManejadorConectar

    /**
     * Clase para manejar los eventos de los botones del panel principal
     */
    class ManejadorBotones implements ActionListener {

        public Image getIconImage() {
            ImageIcon image = new ImageIcon(this.getClass().getResource("/imagenes/car.png"));
            return image.getImage();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == buttonInfoHardware) {

                if (opacimetro instanceof OpacimetroSensors) {
                    int[] info = ((OpacimetroSensors) opacimetro).obtenerClassIdNumeroID();

                    if (info[0] == 3) {
                        panelMedicionStatus.getTextArea().insert("Unidad LCS", 0);
                    } else if (info[1] == 0) {
                        panelMedicionStatus.getTextArea().insert("Modelo 2100 o 2200", 0);
                    } else {
                        panelMedicionStatus.getTextArea().insert("Modelo 1100", 0);
                    }

                } else if (opacimetro instanceof OpacimetroCapelec) {
                    OpacimetroCapelec opacimetroCap = (OpacimetroCapelec) opacimetro;
                    byte[] datosFiltro = opacimetroCap.leerInformacionFiltro();

                    mostrarDialogoPolos(datosFiltro);

                    byte datos[] = dialogoFiltros.getDatos();

                    opacimetroCap.escribirInformacionFiltro(datos);
                }

            } else if (e.getSource() == buttonSerial) {
                workerDatos.setInterrumpido(true);
                JOptionPane.showMessageDialog(null, "Puede ver el serial en la pantalla de inicio del SART en la parte de 'Info Opacidad'" );
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == buttonConfigLentes) {
                System.out.println("Entre-----------------------------------------");
                Window w = SwingUtilities.getWindowAncestor(buttonConfigLentes);
                JDialog dlg = new JDialog(w);
                Config_Lentes cf = new Config_Lentes();
                cf.setVisible(true);
                dlg.add(cf);
                dlg.setSize(900, 400);
                dlg.setModal(true);
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
                dlg.setResizable(false);
            } else if (e.getSource() == buttonFrecuencia) {

                int frecuencia = opacimetro.obtenerFrecuenciaMuestreo();
                panelMedicionStatus.getTextArea().insert("Frecuencia de Muestreo: " + frecuencia, 0);

            } else if (e.getSource() == buttonUmbralTemperatura) {
                if (opacimetro instanceof OpacimetroSensors) {
                    int umbralTemperatura = ((OpacimetroSensors) opacimetro).verificarUmbralTemperatura();
                    panelMedicionStatus.getTextArea().insert("Umbral de Temperatura: " + umbralTemperatura, 0);
                } else if (opacimetro instanceof OpacimetroCapelec) {
                    JOptionPane.showMessageDialog(null, "Este metodo no es implementado por el opacimetro capelec");
                }
            } else if (e.getSource() == buttonLinealizacion) {

                JTextField usernameField = new JTextField(15);
                JPasswordField passwordField = new JPasswordField(15);

                JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("Username:"));
                panel.add(usernameField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);

                int option = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                String username = "";
                String password = "";

                if (option == JOptionPane.OK_OPTION) {
                    username = usernameField.getText();
                    password = new String(passwordField.getPassword());
                } else {
                    System.out.println("Login canceled");
                    return;
                }

                System.out.println("---USUARIO---"+username+"-----");

                String consulta = "SELECT * FROM usuarios WHERE LOWER(Nick_usuario) = LOWER('"+username+"') AND Contrasenia = '"+password+"';";
                Integer idUser = null;

                Conexion.setConexionFromFile();

                try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
                    PreparedStatement consultaDagma = conexion.prepareStatement(consulta)) {

                    //rc representa el resultado de la consulta
                    try (ResultSet rc = consultaDagma.executeQuery()) {
                        int contador = 0;
                        while (rc.next()) {
                            System.out.println("--DENTRO DE LOS RESULTADOS--");
                            idUser= rc.getInt("GEUSER");
                            contador +=1;
                            username=rc.getString("Nombre_usuario");
                        }
                        if(contador == 0){
                            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                            throw new RuntimeException("Usuario no encontrado");
                        }else{
                            JOptionPane.showMessageDialog(null, "Bienvenido "+username, "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (SQLException ex) { 
                    Logger.getLogger(PanelServicioOpacimetro.class.getName()).log(Level.SEVERE, null, ex);
                }

                workerDatos.setInterrumpido(true);

                if (opacimetro instanceof OpacimetroSensors || opacimetro instanceof OpacimetroBrianBee) {
                    Window w = SwingUtilities.getWindowAncestor(buttonLinealizacion.getParent());
                    JDialogPruebaLinealidad dlgPruebaLinealidad = new JDialogPruebaLinealidad((JDialog) w, true);
                    dlgPruebaLinealidad.setTitle("SART Version:1.7.3 copyright  2009 ");
                    dlgPruebaLinealidad.setIconImage(getIconImage());
                    PruebaLinealidad hiloPrueba = new PruebaLinealidad(opacimetro, idUser);
                    hiloPrueba.setPanel(dlgPruebaLinealidad.getPanel());
                    (new Thread(hiloPrueba)).start();
                    dlgPruebaLinealidad.setVisible(true);
                } else if (opacimetro instanceof OpacimetroCapelec) {
                    Window w = SwingUtilities.getWindowAncestor(buttonLinealizacion.getParent());
                    JDialogPruebaLinealidad dlgPruebaLinealidad = new JDialogPruebaLinealidad((JDialog) w, true);
                    PruebaLinealidadCapelec hiloPrueba = new PruebaLinealidadCapelec((OpacimetroCapelec) opacimetro);
                    hiloPrueba.setPanel(dlgPruebaLinealidad.getPanel());
                    (new Thread(hiloPrueba)).start();
                    dlgPruebaLinealidad.setVisible(true);
                }
                workerDatos.setInterrumpido(false);

            } else if (e.getSource() == buttonCalibrar) {
                workerDatos.setInterrumpido(true);
                opacimetro.calibrar();
                try {
                    Thread.sleep(650);
                } catch (InterruptedException ex) {

                }
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == buttonVentiladores) {

                if (opacimetro == null) {
                    JOptionPane.showMessageDialog(null, "DISCULPE, NO ME PUEDO CONECTAR AL  OPACIMETRO ..! ");
                    return;
                }
                workerDatos.setInterrumpido(true);
                String serial;
                try {
                    serial = String.valueOf(opacimetro.obtenerSerial());
                } catch (Exception ex) {
                    serial = "";
                }
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                RecogerDatosOpacimetro monitoreo = new RecogerDatosOpacimetro(opacimetro, executor, workerDatos, serial);
                ScheduledFuture<?> result = executor.scheduleAtFixedRate(monitoreo, 250, 250, TimeUnit.MILLISECONDS);
                Window w = SwingUtilities.getWindowAncestor(buttonVentiladores);
                JFrame.setDefaultLookAndFeelDecorated(false);
                JDialog dlg = new JDialog(w);
                dlg.setTitle("Sart 1.7.3 Panel Verificacion Tiempo Repuesta");
                PanelTiempoRepuestaOpacimetro pTRO = new PanelTiempoRepuestaOpacimetro(monitoreo);
                pTRO.setVisible(true);
                dlg.add(pTRO);
                dlg.setSize(900, 800);
                dlg.setModal(true);
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
                dlg.setResizable(false);
                /* if (opacimetro instanceof OpacimetroCapelec) {
                 switch (e.getActionCommand()) {
                 case "EncenderVentilador":
                 ((OpacimetroCapelec) opacimetro).encenderVentilador(true);
                 buttonVentiladores.setActionCommand("ApagarVentilador");
                 buttonVentiladores.setText("ApagarVentilador");
                 break;
                 case "ApagarVentilador":
                 ((OpacimetroCapelec) opacimetro).encenderVentilador(false);
                 buttonVentiladores.setActionCommand("EncenderVentilador");
                 buttonVentiladores.setText("EncenderVentilador");
                 break;
                 }
                 } else {
                 JOptionPane.showMessageDialog(null, " Opacimetro Sensors no soporta este comando");

                 }*/

            } else if (e.getSource() == buttonArmarTrigger) {
                workerDatos.setInterrumpido(true);
                opacimetro.armarLCS();
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == buttonDispararTrigger) {
                workerDatos.setInterrumpido(true);
                opacimetro.dispararTrigger();
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == buttonOpacidadCorregida) {
                workerDatos.setInterrumpido(true);
                if (buttonOpacidadCorregida.isSelected()) {
                    buttonOpacidadCorregida.setText("Op Sin Corregir");
                    workerDatos.setOpCorregida(false);
                    panelMedicionStatus.getLabelStatus().setText("Medicion Opacidad Sin Corregir");
                } else {
                    buttonOpacidadCorregida.setText("Op Corregida");
                    workerDatos.setOpCorregida(true);
                    panelMedicionStatus.getLabelStatus().setText("Medicion Opacidad Corregida");
                }
                workerDatos.setInterrumpido(false);
            } else if (e.getSource() == buttonSalir) {
                if (workerDatos != null) {
                    workerDatos.setInterrumpido(true);
                    workerDatos.setTerminado(true);
                }
                if (opacimetro.getPort() != null) {
                    opacimetro.getPort().close();
                }
                cerrar();
            } else if (e.getSource() == buttonIntensidad) {

                if (opacimetro instanceof OpacimetroCapelec) {

                    int intensidadLeida = ((OpacimetroCapelec) opacimetro).intensidadFuenteLuz(50, true);
                    JOptionPane.showMessageDialog(null, "La intensidad es: " + intensidadLeida);

                    String strIntensdidad = JOptionPane.showInputDialog("Introduzca un valor de intensidad entre 0 y 100");
                    int intensidad;

                    try {
                        intensidad = Integer.parseInt(strIntensdidad);
                    } catch (NumberFormatException nfexc) {

                        JOptionPane.showMessageDialog(null, "Introduzca un valor valido");
                        return;
                    }

                    OpacimetroCapelec opacimetroCapelec = (OpacimetroCapelec) opacimetro;
                    opacimetroCapelec.intensidadFuenteLuz(intensidad, false);//false es escribir

                    intensidadLeida = ((OpacimetroCapelec) opacimetro).intensidadFuenteLuz(50, true);
                    JOptionPane.showMessageDialog(null, "La intensidad es: " + intensidadLeida);

                }

            }

        }//end of actionPerformedMethod

        private void mostrarDialogoPolos(byte[] datosFiltro) {

            dialogoFiltros = new DialogoFiltroCapelec();
            dialogoFiltros.setDatos(datosFiltro);
            dialogoFiltros.setSize(new Dimension(400, 300));
            JOptionPane.showMessageDialog(null, dialogoFiltros);

        }

    }//end of inner class ActionListener

    class ManejadorRadioButtons implements ItemListener {

        int localBitsParada;

        public ManejadorRadioButtons(int losBitsParada) {
            this.localBitsParada = losBitsParada;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            bitsParada = localBitsParada;
        }

    }//end of class ManejadorRadioButtons

//Para probar el panel
    public static void main(String args[]) {
        JFrame app = new JFrame("Consola Opacimetro");
        PanelServicioOpacimetro consolaOpacimetro = new PanelServicioOpacimetro();
        app.add(consolaOpacimetro);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }
}//end of class PanelServicioOpacimetro
