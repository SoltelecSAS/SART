/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

//import com.soltelec.integrador.cliente.ClienteSicov;
//import com.soltelec.estandar.EstadoEventosSicov;
import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import eu.hansolo.steelseries.tools.BackgroundColor;
import gnu.io.SerialPort;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.medicionrpm.CapelecSerial;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.medicionrpm.ProductorCapelec;
import org.soltelec.medicionrpm.centralauto.TB85000;
import org.soltelec.medicionrpm.centralauto.TB86000;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.pruebasgases.motocicletas.JDialogMotosGases;
import org.soltelec.util.BufferStrings;
import org.soltelec.util.GenerarArchivo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilPropiedades;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;

/**
 * Esta clase muestra un dialogo para seleccionar el dispositivo de medición de
 * rpms y temperatura, dependiendo de la seleccion inicia el hilo y regista
 *
 * @author Gerencia TIC
 */
public class WorkerCruceroRalenti extends SwingWorker<Void, Void> {

    private final PanelPruebaGases panel;
    private PanelPruebaGases panelCancelacion;
    private final DialogoRPMTemp dialogRPMTemp;
    private final BancoGasolina banco;
    private final long idUsuario, idHojaPrueba, idPrueba;
    private String placas;
    private MedidorRevTemp medidorRevTemp;
    private String forMed;
    private Integer tempStored = 0;
    private Timer timer2 = null;
    private int contadorTemporizacion;
    private boolean converCatalitico;
    JDialogPruebaGasolina frmC;
    public static String escrTrans = "";
    public static int aplicTrans = 1;
    public static String ipEquipo;
    public static String serialEquipo = "";
    private double tempAmbiente;
    private double humedadAmbiente;

    public WorkerCruceroRalenti(PanelPruebaGases panel, BancoGasolina banco, long usuario, long hojaPrueba, long prueba, String placas, double tempAmbiente, double humedadAmbiente) {
        this.panel = panel;
        this.panel.setIdUsuario(usuario);
        this.panel.setPlacas(placas);
        this.panel.setIdPrueba(prueba);
        this.banco = banco;
        dialogRPMTemp = new DialogoRPMTemp(hojaPrueba);
        this.idUsuario = usuario;
        this.idHojaPrueba = hojaPrueba;
        this.idPrueba = prueba;
        this.placas = placas;
        MedidorRevTemp medidorRevTemp;
        crearNuevoTimer();
        this.tempAmbiente = tempAmbiente;
        this.humedadAmbiente = humedadAmbiente;
    }

    @Override
    protected Void doInBackground() throws Exception {

        try {
            ActionListener[] actionListeners = panel.getButtonFinalizar().getActionListeners();//
            panel.getButtonFinalizar().removeActionListener(actionListeners[0]);//
        } catch (Exception exc) {
            System.out.println("Shallow exception of actionListenr");
        }
        for (ActionListener al : panel.getButtonFinalizar().getActionListeners()) {
            panel.getButtonRpm().removeActionListener(al);
        }
        panel.getPanelMensaje().setText(" ASEGURESE DE : \n QUE EL MOTOR ESTE ENCENDIDO");
        Thread.sleep(3000);
        panel.getPanelMensaje().setText(" ");
        mostrarDialogoRPM();
        System.out.println("Numero de cilindros :" + dialogRPMTemp.getNumeroCilindros());
        System.out.println("Equipo de Medicion :" + dialogRPMTemp.getEquipo());
        System.out.println("Metodo de Medicion :" + dialogRPMTemp.getMetodoMedicion());
        System.out.println("Multiplicador :" + dialogRPMTemp.isUsaMultiplicador());

        int numeroCilindros = dialogRPMTemp.getNumeroCilindros();//variable de clase??
        String equipoMedicion = dialogRPMTemp.getEquipo();

        //SEGUN LA OOP DEBERIA USAR LA INSTANCIA DEL APARATO QUE MIDE RPM
        //EN CONCORDANCIA A LO QUE SELECCIONE USANDO EL DIALOGO
        //Y LLAMAR POLIMORFICAMENTE AL METODO PARA QUE ME DE LA TEMPERATURA
        //Y LAS REVOLUCIONES
        //LA OTRA ALTERNATIVA ES USE IF ELSE Y REPETIR CODIGO ESA HA SIDO SIEMPRE LA SOLUCION
//        for(ActionListener al : panel.getButtonRpm().getActionListeners()){
//            panel.getButtonRpm().removeActionListener(al);
//        }
        //Simulacion
        boolean simulacion = dialogRPMTemp.simulacion;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<List<MedicionGases>> listaMedicionGases = null;
        List<String> lstValMisc = null;

        String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
        MedicionTermoHigrometro medicionTermoHigrometro = null;
        if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
            MedicionTermoHigrometro medicion = null;
            String strMarcaTermohigrometro = UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");

            if (strMarcaTermohigrometro.equalsIgnoreCase("Artisan")) {
                Integer i = 0;
                if (humedadAmbiente == 0 && tempAmbiente == 0) {
                    JOptionPane.showMessageDialog(null, "Disculpe, no DETECTO el Termohigrometro, por lo tanto esta prueba no se REALIZARA ..! ");
                    panel.cerrar();
                    return null;
                }
            } else {
                TermoHigrometro termoHigrometro = new TermoHigrometroPCSensors();
                medicionTermoHigrometro = termoHigrometro.obtenerValores();
                humedadAmbiente = termoHigrometro.obtenerValores().getValorHumedad();
                tempAmbiente = termoHigrometro.obtenerValores().getValorTemperatura();
            }
            System.out.println("Variantes Obtenidad termohigrometro.- hum:" + humedadAmbiente + " temp:" + tempAmbiente);

            //Validar vrs. TermoHigrometro visible
            //JOptionPane.showMessageDialog(panel, "","");
//        JOptionPane.showMessageDialog(null,
//        "TEMPERATURA: '" + temp + "'."+"HUMEDAD: '" + hum + "'.",
//        "Validando Termohigrometro",
//        JOptionPane.INFORMATION_MESSAGE);
//        if(hum >= 30 && hum <= 90){
//        JOptionPane.showMessageDialog(null, "Humedad no valida");
//        }
//        if(temp >= 5 && temp <= 55){
//        JOptionPane.showMessageDialog(null, "Temperatura no valida");
//        }  
        }
        try {
            /* if (simulacion) {
                Future<List<List<MedicionGases>>> futureSimulacion = executor.submit(new CallableSimulacionGasolina(panel, banco, idPrueba, idUsuario, idHojaPrueba));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureSimulacion, (MedidorRevTemp) banco, banco, panel, idUsuario));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureSimulacion, idPrueba, banco, null, panel, idUsuario));
                while (!futureSimulacion.isDone()) {
                }
                listaMedicionGases = futureSimulacion.get();//este metodo bloquea
            }*/

            if (equipoMedicion.equals("banco") && simulacion == false) {
                Future<List<String>> futureValidador = executor.submit(new CallableValidadorPruebas(panel, banco, (MedidorRevTemp) banco, simulacion));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureValidador, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureValidador, (MedidorRevTemp) banco, banco, panel, idUsuario));

                lstValMisc = futureValidador.get();
                try {
                    ActionListener[] actionListeners = panel.getButtonFinalizar().getActionListeners();//
                    panel.getButtonFinalizar().removeActionListener(actionListeners[0]);//
                } catch (Exception exc) {
                    System.out.println("Shallow exception of actionListenr");
                }
                for (ActionListener al : panel.getButtonFinalizar().getActionListeners()) {
                    panel.getButtonRpm().removeActionListener(al);
                }

                Future<List<List<MedicionGases>>> futureBanco = executor.submit(new CallableCiclosGasolina(panel, banco, (MedidorRevTemp) banco, idPrueba, idUsuario, idHojaPrueba, lstValMisc.get(0), Integer.parseInt(lstValMisc.get(1)), Boolean.parseBoolean(lstValMisc.get(2)), this.placas, simulacion,tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureBanco, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureBanco, (MedidorRevTemp) banco, banco, panel, idUsuario));
                listaMedicionGases = futureBanco.get();

            } else if (equipoMedicion.equals("kit") && simulacion == false) {
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoKit", "propiedades.properties");
                SerialPort serialPort = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                CapelecSerial capelecServicios = new CapelecSerial();
                capelecServicios.setOut(out);
                capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);
                capelecServicios.cambiarCilindros((numeroCilindros > 4) ? numeroCilindros : 4);
                System.out.println("Numero Cilindros: " + ((numeroCilindros > 4) ? numeroCilindros : 4));

                BufferStrings bufferMed = new BufferStrings();//maestro productor consumidor estructura comun a ambos
                ProductorCapelec productor = new ProductorCapelec(in, bufferMed);
                ConsumidorCapelec capelec = new ConsumidorCapelec(bufferMed, productor);
                //PARA QUE SE PUEDA CERRAR EL PUERTO SERIAL
                capelec.setPuertoSerial(serialPort);
                (new Thread(productor)).start();
                (new Thread(capelec)).start();
                //llamar al mismo callable polimorfico
                JDialogReconfiguracionKit dlgReconfiguracion = new JDialogReconfiguracionKit(capelec, serialPort);
                dlgReconfiguracion.setVisible(true);//se bloquea hasta que el kit este correctamente configurado
                Future<List<String>> futureValidador = executor.submit(new CallableValidadorPruebas(panel, banco, capelec, simulacion));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureValidador, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureValidador, capelec, banco, panel, idUsuario));
                lstValMisc = futureValidador.get();
                for (ActionListener al : panel.getButtonFinalizar().getActionListeners()) {
                    panel.getButtonFinalizar().removeActionListener(al);
                }
                for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
                    panel.getButtonRpm().removeActionListener(al);
                }
                this.medidorRevTemp = (MedidorRevTemp) capelec;
                Future<List<List<MedicionGases>>> futureKit = executor.submit(new CallableCiclosGasolina(panel, banco, capelec, idPrueba, idUsuario, idHojaPrueba, lstValMisc.get(0), Integer.parseInt(lstValMisc.get(1)), Boolean.parseBoolean(lstValMisc.get(2)), this.placas, simulacion,tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureKit, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureKit, (MedidorRevTemp) banco, banco, panel, idUsuario));
                listaMedicionGases = futureKit.get();
                capelec.stop();

//            } else if (equipoMedicion.equals("'")) {
            } else if (equipoMedicion.equals("centralAuto")) {
                TB85000 tb8500 = null;
                if (simulacion == false) {
                    String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                    System.out.println("PuertoCentralUto:" + nombrePuerto);
                    SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    tb8500 = new TB85000(puertoSerial);
                    tb8500.cambiarCilindros(numeroCilindros);
                } else {
                    tb8500 = new TB85000();
                }
                Future<List<String>> futureValidador = executor.submit(new CallableValidadorPruebas(panel, banco, tb8500, simulacion));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureValidador, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureValidador, (MedidorRevTemp) banco, banco, panel, idUsuario));
                lstValMisc = futureValidador.get();

                try {
                    ActionListener[] actionListeners = panel.getButtonFinalizar().getActionListeners();//
                    panel.getButtonFinalizar().removeActionListener(actionListeners[0]);//
                } catch (Exception exc) {
                    System.out.println("Shallow exception of actionListenr");
                }
                for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
                    panel.getButtonRpm().removeActionListener(al);
                }

                this.medidorRevTemp = (MedidorRevTemp) tb8500;
                System.out.println("Numero Cilindros: " + (numeroCilindros));
                Future<List<List<MedicionGases>>> futureCentral = executor.submit(new CallableCiclosGasolina(panel, banco, tb8500, idPrueba, idUsuario, idHojaPrueba, lstValMisc.get(0), Integer.parseInt(lstValMisc.get(1)), Boolean.parseBoolean(lstValMisc.get(2)), this.placas, simulacion,tempAmbiente, humedadAmbiente));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureCentral, (MedidorRevTemp) banco, banco, panel, idUsuario));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureCentral, idPrueba, banco, null, panel, idUsuario));
                listaMedicionGases = futureCentral.get();
                if (simulacion == false) {
                    tb8500.stop();
                }

            } else if (equipoMedicion.equals("tb8600") && simulacion == false) {
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                TB86000 tb86000 = new TB86000(puerto);
                tb86000.setNumeroCilindros(numeroCilindros);
                System.out.println("Numero Cilindros: " + ((numeroCilindros > 4) ? numeroCilindros : 4));
                Future<List<String>> futureValidador = executor.submit(new CallableValidadorPruebas(panel, banco, tb86000, simulacion));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureValidador, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureValidador, (MedidorRevTemp) banco, banco, panel, idUsuario));
                lstValMisc = futureValidador.get();
                try {
                    ActionListener[] actionListeners = panel.getButtonFinalizar().getActionListeners();//
                    panel.getButtonFinalizar().removeActionListener(actionListeners[0]);//
                } catch (Exception exc) {
                    System.out.println("Shallow exception of actionListenr");
                }
                for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
                    panel.getButtonRpm().removeActionListener(al);
                }
                this.medidorRevTemp = (MedidorRevTemp) tb86000;
                Future<List<List<MedicionGases>>> futureTB86000 = executor.submit(new CallableCiclosGasolina(panel, banco, tb86000, idPrueba, idUsuario, idHojaPrueba, lstValMisc.get(0), Integer.parseInt(lstValMisc.get(1)), Boolean.parseBoolean(lstValMisc.get(2)), this.placas, simulacion,tempAmbiente, humedadAmbiente));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(futureTB86000, (MedidorRevTemp) banco, banco, panel, idUsuario));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureTB86000, idPrueba, banco, null, panel, idUsuario));
                listaMedicionGases = futureTB86000.get();
                try {
                    tb86000.getPuertoSerial().close();
                } catch (Exception exc) {
                }
            }
            //Muestra de Validacion de Temperatura y Humedad
            EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
            evalReg.registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);
            System.out.println("Registro Temperatura y Humedad");
            System.out.println("--->Temp:" + tempAmbiente + "Hum:" + humedadAmbiente);

            if (listaMedicionGases.size() > 0) {
                System.out.println("Recojo medidas ");
                evalReg = new EvaluarRegistrarPruebaGasolina(listaMedicionGases, idUsuario, idHojaPrueba, idPrueba, true);
                evalReg.registrar(WorkerCruceroRalenti.aplicTrans, ipEquipo);
            }
            if (listaMedicionGases.size() == 0) {
                System.out.println("No Recojo medidas ");
                this.rechazoPrueba(panel.getFuncion().getText());
            }
        } catch (InterruptedException exc) {
            WorkerCruceroRalenti.escrTrans = "";
            if (exc.getCause() instanceof InterruptedException) {
                this.cancelacion("InterruptedException");
            } else {
                JOptionPane.showMessageDialog(panel, "Por favor verifique que los equipos esten bien conectados \n");
                exc.printStackTrace(System.err);
                panel.cerrar();
            }
        } catch (CancellationException cexc) {
            if (panel.getFuncion().getText().equalsIgnoreCase("rechazo")) {
                this.rechazoPrueba("3.1.1.1.10 Revoluciones fuera de rango.");
            }
            if (panel.getFuncion().getText().equalsIgnoreCase("abortado")) {
                WorkerCruceroRalenti.escrTrans = "";
                this.cancelacion("Aborto");
            }
            System.out.println("quien mando el Callable exception " + panel.getFuncion().getText());
        } catch (ArrayIndexOutOfBoundsException exc) {
            WorkerCruceroRalenti.escrTrans = "";
            JOptionPane.showMessageDialog(panel, "DISCULPE; no he podido establecer COMUNICACION con el Banco de Gases en estos momentos \n Posibles Causas: problemas con el cable de comunicacion \n Banco Desconectado entre otros");
            exc.printStackTrace(System.err);
            panel.cerrar();
            return null;
        } catch (Exception exc) {
            WorkerCruceroRalenti.escrTrans = "";
            JOptionPane.showMessageDialog(panel, "Se desconecto el KIT de RPM");
            exc.printStackTrace(System.err);
            panel.cerrar();
            Logger.getRootLogger().error("Error WorkerCruceroRalenti", exc);
        }
        System.out.println("va hacia el metodo de liberar recursos RELOAD  ");
        liberarRecursos();
        Mensajes.messageDoneTime("He Concluido la  prueba de Gases(Vehiculo) ..!", 2);
        return null;
    }//end of method doInBackground

    public void mostrarDialogoRPM() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        d.setTitle("Seleccion de Kit de RPM");
        d.getContentPane().add(dialogRPMTemp);
        d.setSize(500, 300);
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panel);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setVisible(true);
    }//end of method mostrarDialogoRPM

    private void cancelacion(String causa) {
        panel.cerrar();
    }

    private void rechazoPrueba(String causa) {
        System.out.println("RECHAZO PRUEBA");
        panel.cerrar();
        try {
            try {
                frmC = new JDialogPruebaGasolina(null, false, 0, 0, 0, null, placas,null);
            } catch (SQLException ex) {

            } catch (ClassNotFoundException ex) {
            }
        } catch (IOException ex) {
        }
        frmC.setTitle("SART 1.7.3 RECHAZO DE PRUEBA");
        panelCancelacion = new PanelPruebaGases();
        frmC.getContentPane().add(panelCancelacion);
        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
        panelCancelacion.getPanelMensaje().setText(causa.toUpperCase());
        panelCancelacion.getProgressBar().setVisible(false);
        JLabel lblImg = (JLabel) panelCancelacion.getPanelMensaje().getComponent(0);
        lblImg.setIcon(image);
        frmC.setIconImage(new ImageIcon(getClass().getResource("/imagenes/cancelar.png")).getImage());
        panelCancelacion.getMensaje().setText("RECHAZO DE PRUEBAS POR ".concat(causa).toUpperCase());
        frmC.setVisible(true);
        panelCancelacion.getButtonFinalizar().setVisible(false);
        panelCancelacion.setVisible(true);
        if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente a la causal de Rechazo?")) {
            org.soltelec.pruebasgases.FrmComentarioLiviano frm = new org.soltelec.pruebasgases.FrmComentarioLiviano(SwingUtilities.getWindowAncestor(panelCancelacion), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, causa, idUsuario, "rechazo", 80000);
            panelCancelacion.getMensaje().setText("Registro de Ampliacion de Observaciones Encontradas..! ");
            frm.setLocationRelativeTo(panelCancelacion);
            frm.setVisible(true);
            frm.setModal(true);
        } else {
            try {
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                Connection cn = regMedidas.getConnection();
                ConsultasLogin consultasLogin = new ConsultasLogin();
                regMedidas.registraRechazo(causa, causa, this.idUsuario, idPrueba, 80000);//Registra la prueba como Rechazada           
            } catch (SQLException | ClassNotFoundException exc) {
                Mensajes.mostrarExcepcion(exc);
            } finally {
            }
        }
        panelCancelacion.cerrar();
    }

    private void liberarRecursos() {
        try {
            panel.cerrar();
            if (banco != null) {
                banco.getPuertoSerial().close();//aun asi el orden es importante como la forma
                SerialPort puertoSerial = banco.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close();
                    Thread.sleep(300);
                }
            }
            //si es otro dispositivo de medicion entonces cerrar el puerto
            if (!(medidorRevTemp instanceof BancoSensors) && medidorRevTemp != null) {
                SerialPort puertoSerial = medidorRevTemp.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close(); //para no cerrarlo dos veces
                    Thread.sleep(300);
                }
                medidorRevTemp.stop();
                Thread.sleep(100);
            }
        } catch (Exception exc) {
            System.out.println("Excepcion cerrando puertos seriales debido a " + exc.getMessage());
            exc.printStackTrace();
        }
    }

    private void crearNuevoTimer() {
        timer2 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            } //end actionPerformed

        });
    }

}//end of method WorkerCruceroRalenti
