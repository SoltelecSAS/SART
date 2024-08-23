/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import org.soltelec.pruebasgases.ListenerCancelacionGases;
import gnu.io.SerialPort;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXLoginPane;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.medicionrpm.centralauto.PanelServicioTB8600;
import org.soltelec.medicionrpm.centralauto.TB85000;
import org.soltelec.medicionrpm.centralauto.TB86000;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.pruebasgases.CondicionesAnormalesException;
import org.soltelec.pruebasgases.DialogoRPMTempMoto;
import org.soltelec.pruebasgases.EvaluarRegistrarPruebaGasolina;
import org.soltelec.pruebasgases.FrmPanelCancelacion;
import org.soltelec.pruebasgases.HumoAzulException;
import org.soltelec.pruebasgases.HumoNegroException;
import org.soltelec.pruebasgases.ListenerCambioRpms;
import org.soltelec.pruebasgases.ListenerCancelacionPorRpm;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.AproximacionMedidas;
import org.soltelec.util.DefectoGeneral;
import org.soltelec.util.GenerarArchivo;
import org.soltelec.util.InfoVehiculo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.Mensajes;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.TipoDefecto;
import org.soltelec.util.UtilInfoVehiculo;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.BancoCapelec;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;

/**
 * Clase para la parte especifica de la prueba de gasolina para motos.Cuando se
 * da click en el boton cambiar rpms se cancela el CallableCicloMotos o el
 * CallableSimulacionMotos y se inicia un nuevo WorkerCiclosMoto.
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class WorkerCiclosMoto extends SwingWorker<Void, Void> {

    private final PanelPruebaGases panel;
    private PanelPruebaGases panelCancelacion;
    private PanelPruebaGases panelAborto;
    private final long idHojaPrueba, idPrueba, idUsuario;
    private int numeroTiempos = 4;//Cuatro tiempos un escape por defecto
    private int numeroEscapes = 1;
    public int modelo = 0;
    private final BancoGasolina banco;
    private final DialogoRPMTempMoto dlgRpm;
    private MedidorRevTemp medidorRevTemp;
    private String placas;
    JDialogMotosGases frmC;
    public static int aplicTrans = 0;
    public static String escrTrans = "";
    public static String ipEquipo;
    private final double tempAmbiente;
    private final double humedadAmbiente;

    WorkerCiclosMoto(PanelPruebaGases panel, BancoGasolina banco, long idHojaPrueba, long idPrueba, long idUsuario, int numeroTiempos, int numeroEscapes, String placas, double tempAmbiente, double humedadAmbiente) {
        this.panel = panel;
        this.panel.setIdUsuario(idUsuario);
        this.panel.setPlacas(placas);
        this.panel.setIdPrueba(idPrueba);
        this.idHojaPrueba = idHojaPrueba;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.numeroEscapes = 1;//numeroEscapes;
        this.numeroTiempos = numeroTiempos;
        this.banco = banco;
        this.placas = placas;
        dlgRpm = new DialogoRPMTempMoto();//dialogo para seleccionar el dispositivo de medicion de rpms
        this.tempAmbiente = tempAmbiente;
        this.humedadAmbiente = humedadAmbiente;

    }

    @Override
    @SuppressWarnings("empty-statement")
    protected Void doInBackground() throws Exception {

        mostrarDialogoRpm();//Muestra el dialogo para seleccionar el dispositivo de medir revoluciones.

        System.out.println("Numero de cilindros :" + dlgRpm.getNumeroCilindros());
        System.out.println("Equipo de Medicion :" + dlgRpm.getEquipo());
        System.out.println("Metodo de Medicion :" + dlgRpm.getMetodoMedicion());
        panel.getButtonFinalizar().setVisible(true);
        int numeroCilindros = dlgRpm.getNumeroCilindros();//variable de clase??

        String equipoMedicion = dlgRpm.getEquipo();
        String metodoMedicion = dlgRpm.getMetodoMedicion();
        boolean simulacion = dlgRpm.simulacion;
        int numTiempos = dlgRpm.getNumeroTiempos();
        //remover el Listener de la Cancelacion  como es inseguro entonces se envuelve para evitar que se bloquee 
        try {
            ActionListener[] actionListeners = panel.getButtonFinalizar().getActionListeners();//
            panel.getButtonFinalizar().removeActionListener(actionListeners[0]);//
        } catch (Exception exc) {
            System.out.println("Shallow exception of actionListenr");
        }

        try {
            ActionListener[] actionListeners = panel.getButtonRpm().getActionListeners();//
            panel.getButtonRpm().removeActionListener(actionListeners[0]);//
        } catch (Exception exc) {
            System.out.println("Shallow exception of actionListenr");
        }

        List<MedicionGases> lstDatoasMedicion = null;

        //MODIFICACION 09/01/2021
        //SE CONFIGURA DESDE EL ARCHIVO PORPERTIES UN BOOLEAN PARA EL BOTON RMP
        String activarBotonRPM = UtilPropiedades.cargarPropiedad("activarBotonRPM", "propiedades.properties");
        boolean activarBotonRPMBolleano = Boolean.parseBoolean(activarBotonRPM);
        panel.getButtonRpm().setVisible(activarBotonRPMBolleano);
        System.out.println("El Equipo de Medicion Seleccionado es: " + equipoMedicion);
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            if (simulacion) {
                Future<List<MedicionGases>> future = executor.submit(new CallableSimulacionMotos(panel, banco, (MedidorRevTemp) banco, numeroTiempos, numeroEscapes, idPrueba, idUsuario, this.placas));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(future, idPrueba, banco, null, panel, idUsuario));//para la cancelacion
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future, null, banco, panel, idUsuario));//para cambio de rpms
                panel.getBtnWorkerCicloMotos().addActionListener(new ListenerCancelacionPorRpm(future, idPrueba, banco, null, panel, idUsuario, tempAmbiente, humedadAmbiente));
                lstDatoasMedicion = future.get();
                System.out.println("Sali del Hilo de Simulaciones con " + lstDatoasMedicion.size());
            } else if (equipoMedicion.equals("banco") && simulacion == false) {
                System.out.println("Equipo de Medicion is banco ");
                Future<List<MedicionGases>> future = executor.submit(new CallablePruebaMotos(banco, (MedidorRevTemp) banco, panel, numeroEscapes, idPrueba, this.placas, activarBotonRPMBolleano, tempAmbiente, humedadAmbiente));//el cast es para que el banco funcione como medidor de rpms
                panel.getBtnWorkerCicloMotos().addActionListener(new ListenerCancelacionPorRpm(future, idPrueba, banco, null, panel, idUsuario, tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(future, idPrueba, banco, null, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future, null, banco, panel, idUsuario));

                lstDatoasMedicion = future.get();
            } else if (equipoMedicion.equals("tb8600") && simulacion == false) {
                System.out.println("selecciono tb8600 como Equipo de Medicion ");
                //configurar el metodo de
                //llamar al mismo callable polimorfico
                //dejamos el mismo nombre de propiedad para el tb8600 que para el tb8500
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                System.out.println("PuertoCentralAuto:" + nombrePuerto);
                SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                puertoSerial.enableReceiveTimeout(100);
                System.out.println("Levante del File Property el puerto");
                TB86000 tb8600 = new TB86000(puertoSerial);
                //JOptionPane.showMessageDialog(null,"Numero de Tiempos: " + numTiempos + " Numero de Cilindros: " + numeroCilindros);
                Logger.getRootLogger().info("Numero de Tiempos: " + numTiempos + " Numero de Cilindros: " + numeroCilindros);
                tb8600.setNumeroCilindros(numeroCilindros);
                System.out.println("Voy a Lenvantar el Panel de Servicio");
                PanelServicioTB8600 gui = new PanelServicioTB8600(tb8600);
                JDialog app = new JDialog();
                app.add(gui);
                app.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                app.setSize(app.getToolkit().getScreenSize());
                app.setModal(true);
                System.out.println("Voy Hacer Visible el Kit de Servicio");
                app.setVisible(true);
                medidorRevTemp = tb8600;
                System.out.println("Tome Referencia en Memoria del Kit RPM");
                Future<List<MedicionGases>> future = executor.submit(new CallablePruebaMotos(banco, tb8600, panel, numeroEscapes, idPrueba, this.placas, activarBotonRPMBolleano, tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(future, idPrueba, banco, tb8600, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future, tb8600, banco, panel, idUsuario));
                panel.getBtnWorkerCicloMotos().addActionListener(new ListenerCancelacionPorRpm(future, idPrueba, banco, null, panel, idUsuario, tempAmbiente, humedadAmbiente));
                lstDatoasMedicion = future.get();
            } else if (equipoMedicion.equals("kit") && simulacion == false) {
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoKit", "propiedades.properties");
                SerialPort serialPort = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            } else if (equipoMedicion.equals("centralAuto") && simulacion == false) {//hacer una enumeracion para el equipo de medicion de revoluciones
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                System.out.println("PuertoCentralUto:" + nombrePuerto);
                SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                TB85000 tb8500 = new TB85000(puertoSerial);
                //TB85000 tb8500 = new TB85000();
                tb8500.cambiarCilindros(numeroCilindros);

                medidorRevTemp = tb8500;
                Future<List<MedicionGases>> future = executor.submit(new CallablePruebaMotos(banco, tb8500, panel, numeroEscapes, idPrueba, this.placas, activarBotonRPMBolleano, tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(future, idPrueba, banco, tb8500, panel, idUsuario));
                panel.getButtonRpm().addActionListener(new ListenerCambioRpms(future, tb8500, banco, panel, idUsuario));
                panel.getBtnWorkerCicloMotos().addActionListener(new ListenerCancelacionPorRpm(future, idPrueba, banco, null, panel, idUsuario, tempAmbiente, humedadAmbiente));
                lstDatoasMedicion = future.get();
            }
            System.out.println("Sali del Hilo de ejecucion de pruebas");
            System.out.println("Tamaño de la lista de MEDICIONES: " + lstDatoasMedicion.size());
            if (lstDatoasMedicion.size() >= 1) {
                CallablePruebaMotos.condicionTemperatura = 7;
                System.out.println("entre bloque de captura de datos");
                InfoVehiculo infoVehiculo = UtilInfoVehiculo.traerInfoVehiculo(idPrueba);

                //    lstDatoasMedicion = GenerarArchivo.generarArchivoGases(lstDatoasMedicion, placas, "M", banco);  
                //  listaMediciones = GenerarArchivo.generarArchivoGases(listaMediciones, placas, "M", banco);               
                modelo = infoVehiculo.getModelo();
                numeroTiempos = infoVehiculo.getNumeroTiempos();
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println("++++++++++++++++++++ tamaño lstDatoasMedicion: " + lstDatoasMedicion.size() + " ++++++++++++++++++++++++++");
                double[] medidas = procesarListas(lstDatoasMedicion, GenerarArchivo.temperatura, simulacion);
                System.out.println("obtengo procesar medias ");
                List<MedidaGeneral> listaMedidas = armarMedidas(medidas);
                //humedad y temperatura
                System.out.println("YA ARMO MEDIDAS ");
                //if (strTermohigrometro != null && strTermohigrometro.equalsIgnoreCase("Habilitado")) {
                listaMedidas.add(new MedidaGeneral(8031, tempAmbiente));
                listaMedidas.add(new MedidaGeneral(8032, humedadAmbiente));
                System.out.println("add list medidas la temp y humedad ");
                //}
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                Connection cn = null;
                try {
                    cn = regMedidas.getConnection();
                    cn.setAutoCommit(false);
                    InfoVehiculo info = regMedidas.cargarInfoVehiculo(idPrueba, cn);
                    boolean pruebaAprobada = evaluarPrueba(medidas, info.getModelo());
                    System.out.println("VOY A PERSISITR LA LISTA DE MEDIDAS" + pruebaAprobada);
                    boolean escrTrans = false;
                    if (WorkerCiclosMoto.aplicTrans == 1 && pruebaAprobada == true) {
                        escrTrans = true;
                        System.out.println("Paso no necesito aplicar artificio ");
                    }
                    if (WorkerCiclosMoto.aplicTrans == 0) {
                        System.out.println("aplico artificio: ");
                        escrTrans = true;
                    }
                    System.out.println("aplicTras (WORKERCICLO) es: " + aplicTrans);
                    System.out.println("escrTrans (WORKERCICLO) es: " + escrTrans);
                    System.out.println("voy a registrar las medidas (WORKERCICLO) es: " + escrTrans);
                    System.out.println("Ident Prueba is " + idPrueba);
                    regMedidas.registrarMedidas(listaMedidas, idPrueba, cn);//Las medidas se registran
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println("TERMINE DE REGISTRAR (WORKERCICLO)");
                    if (escrTrans == true) {
                        if (!pruebaAprobada) {
                            List<DefectoGeneral> listaDefectos = new ArrayList<>();
                            listaDefectos.add(new DefectoGeneral(84018, TipoDefecto.DEFECTO_A));
                            regMedidas.registrarDefectos(listaDefectos, idPrueba, cn);
                        }
                    }
                    regMedidas.registrarPruebaFinalizada(cn, pruebaAprobada, idUsuario, idPrueba, escrTrans, ipEquipo);
                    System.out.println("Ya Registre Prueba " + idPrueba);
                    cn.setAutoCommit(true);
                } catch (SQLException | ClassNotFoundException sqlexc) {
                    throw sqlexc;
                } finally {
                    try {
                        if (cn != null) {
                            cn.close();
                        }
                    } catch (Exception e) {
                    }
                }
            }
            if (lstDatoasMedicion.size() == 0) {
                if (panel.getFuncion().getText().equalsIgnoreCase("Tiempo Limite Toma Revoluciones")) {
                    this.cancelacion("Aborto");
                }

                if (!panel.getFuncion().getText().equalsIgnoreCase("condicioninesperada") && !panel.getFuncion().getText().equalsIgnoreCase("Tiempo Limite Toma Revoluciones")) {
                    System.out.println("Registrar entrada en condiciones inesperada ");
                    this.rechazoPrueba(panel.getFuncion().getText());
                }
                String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
                if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
                    //Muestra de Validacion de Temperatura y Humedad
                    EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
                    evalReg.registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);

                }
                if (CallablePruebaMotos.condicionTemperatura == 1) {
                    System.out.println("Registrar condiciones x temperatura ");
                    RegistrarMedidas regMedidas = new RegistrarMedidas();
                    Connection cn = regMedidas.getConnection();
                    EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
                    evalReg.registrorRPMTemp(CallablePruebaMotos.rpmStored, CallablePruebaMotos.tempStored);
                }
            }
        } catch (InterruptedException exc) {
            System.out.println("Validacion de falla InterruptedException ".concat(exc.getCause().toString()));
            if (exc.getCause() instanceof InterruptedException) {
                System.out.println("InterruptedException");
                this.cancelacion("Prueba Cancelada o InterruptedException");
            } else {
                JOptionPane.showMessageDialog(panel, "Se desconecto el KIT de RPM");
                exc.printStackTrace(System.err);
            }
        } catch (CancellationException calexc) {
            System.out.println("Validacion de falla CancellationException ".concat(calexc.getCause().toString()));
            String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
            if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
                //Muestra de Validacion de Temperatura y Humedad
                EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
                evalReg.registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);
            }

            System.out.println("Registrar condiciones x temperatura ");
            RegistrarMedidas regMedidas = new RegistrarMedidas();
            Connection cn = regMedidas.getConnection();
            EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
            evalReg.registrorRPMTemp(CallablePruebaMotos.rpmStored, CallablePruebaMotos.tempStored);

            System.out.println("quien mando el Callable exception " + panel.getFuncion().getText());
            if (panel.getFuncion().getText().equalsIgnoreCase("rechazo")) {
                this.rechazoPrueba("4.1.1.1.5 Revoluciones Fuera Rango");
            }
            if (panel.getFuncion().getText().equalsIgnoreCase("abortado")) {
                this.cancelacion("Aborto");

            }
        } catch (ExecutionException exc) {
            System.out.println("Validacion de falla ExecutionException ".concat(exc.getCause().toString()));
            exc.printStackTrace();
            if (exc.getCause() instanceof ArrayIndexOutOfBoundsException || exc.getCause() instanceof RuntimeException) {
                JOptionPane.showMessageDialog(panel, "DISCULPE, proceso interrumpido, por favor verificar comunicacion con los perifericos o Banco de gases entre otros \n Posibles Causas: problemas con el cable de comunicacion");
                panel.cerrar();
                return null;
            }
            String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
            if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
                //Muestra de Validacion de Temperatura y Humedad
                EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
                evalReg.registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);

            }
            System.out.println("Registrar condiciones x temperatura ");
            RegistrarMedidas regMedidas = new RegistrarMedidas();
            Connection cn = regMedidas.getConnection();
            EvaluarRegistrarPruebaGasolina evalReg = new EvaluarRegistrarPruebaGasolina(0, 0, idPrueba);
            evalReg.registrorRPMTemp(CallablePruebaMotos.rpmStored, CallablePruebaMotos.tempStored);

            System.out.println("quien mando el Callable exception " + panel.getFuncion().getText());
            if (panel.getFuncion().getText().equalsIgnoreCase("rechazo")) {
                this.rechazoPrueba("4.1.1.1.5 Revoluciones Fuera Rango");
            }
            if (panel.getFuncion().getText().equalsIgnoreCase("abortado")) {
                this.cancelacion("Aborto");
            }
        } catch (RuntimeException exc) {
            //JOptionPane.showMessageDialog(panel, "DISCULPE, proceso interrumpido, por favor verificar comunicacion con los perifericos o Banco de gases entre otros \n Posibles Causas: problemas con el cable de comunicacion");
            panel.cerrar();
            return null;
        } catch (Exception exc) {

            System.out.println(" NATURALEZA  INTERRUPCION a \n" + exc.getMessage() + "\n Causa: " + exc.getCause() + "\n Clase: " + exc.getClass());
        }
        System.out.println("va hacia el metodo de liberar recursos ");
        liberarRecursos();
        if (panel.getFuncion().getText().equalsIgnoreCase("condicioninesperada") || panel.getFuncion().getText().equalsIgnoreCase("rechazo") || panel.getFuncion().getText().equalsIgnoreCase("abortado") || CallablePruebaMotos.condicionTemperatura == 1) {
            JOptionPane.showMessageDialog(panel, " La  Prueba de Moto ha Finalizado antes de lo esperado ..!", "SART 1.7.3  ", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(panel, "He Concluido La  Prueba de Moto de una manera Exitosa ..!", "SART 1.7.3  ", JOptionPane.INFORMATION_MESSAGE);
        }

        try {
            EquipoController Eq = new EquipoController();
            Eq.ActFechaFinal(idPrueba);
            System.out.println("--------------------------------Fecha Actualizada----------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }//end of method doInBackground

    private void rechazoPrueba(String causa) {
        System.out.println("voy a mostrar  logica de Rechazo");
        panel.cerrar();
        try {
            frmC = new JDialogMotosGases(null, false, 0, 0, 0, null, "", null);

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WorkerCiclosMoto.class.getName()).log(Level.SEVERE, null, ex);
        }
        frmC.setTitle("SART 1.7.3 RECHAZO DE PRUEBA");
        panelCancelacion = new PanelPruebaGases();
        panelCancelacion.setDialogMotosGases(frmC);
        frmC.getContentPane().add(panelCancelacion);
        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
        Font eve = new Font(Font.SANS_SERIF, Font.BOLD, 10);
        panelCancelacion.getPanelMensaje().setFont(eve);
        panelCancelacion.getPanelMensaje().setText(causa.toUpperCase());
        panelCancelacion.getProgressBar().setVisible(false);
        JLabel lblImg = (JLabel) panelCancelacion.getPanelMensaje().getComponent(0);
        lblImg.setIcon(image);
        frmC.setIconImage(new ImageIcon(getClass().getResource("/imagenes/cancelar.png")).getImage());
        panelCancelacion.getButtonFinalizar().setVisible(false);
        frmC.setVisible(true);
        panelCancelacion.setVisible(true);
        if (causa.equalsIgnoreCase("Revoluciones Fuera Rango")) {

        }
        System.out.println("Imprimo en el metodo rechazo x humo :" + CallableInicioMotos.lecturaCondicionesAnormales);
        if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente a la causal de Rechazo?")) {
            frmC.setModal(true);
            panelCancelacion.getMensaje().setText("Registro de Ampliacion de Observaciones Encontradas..! ");
            org.soltelec.pruebasgases.FrmComentario frm = new org.soltelec.pruebasgases.FrmComentario(SwingUtilities.getWindowAncestor(panelCancelacion), idPrueba, JDialog.DEFAULT_MODALITY_TYPE, causa.toUpperCase(), idUsuario, "rechazo", 84018);
            frm.setLocationRelativeTo(panelCancelacion);
            System.out.println("RECHAZO PRUEBA");
            frm.setVisible(true);
            frm.setModal(true);
        } else {
            try {
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                Connection cn = regMedidas.getConnection();
                ConsultasLogin consultasLogin = new ConsultasLogin();
                regMedidas.registraRechazo(causa, causa, this.idUsuario, idPrueba, 84018);//Registra la prueba como Rechazada
            } catch (SQLException | ClassNotFoundException exc) {
                Mensajes.mostrarExcepcion(exc);
            } finally {
            }
        }
        System.out.println("Ya sali de la vista");
        panelCancelacion.cerrar();
        frmC.setVisible(false);
        System.out.println("fin fin fin ..!");
        frmC.setVisible(false);
        panelCancelacion.setVisible(false);
        panelCancelacion.cerrar();
    }

    public void mostrarDialogoRpm() {
        //ocultar aparatos no usados
        dlgRpm.getRadioKit().setEnabled(false);
        dlgRpm.getRadioBanco().setEnabled(true);
        //dlgRpm.getRadioCentralAuto().setEnabled(false);
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        d.setTitle("Kit de RPM Disponible");
        d.getContentPane().add(dlgRpm);
        d.setSize(500, 300);
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panel);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setVisible(true);

    }//end of method mostrarDialogoRPM

    /**
     * Metodo que obtiene las medidas maximas de cada una de las listas
     *
     * @param listaMediciones
     */
    private double[] procesarListas(List<MedicionGases> lstDatoasMedicion, int temp, Boolean simulada) {
        //sacar el promedio del gas
        System.out.println("+++++++++++++++++++++++++++++++++++++++ingrese a procesarListas+++++++++++++++++++++++++++");
        double mediaHC = 0;//para la listaRalentiUno
        double mediaCO = 0;
        double mediaCO2 = 0;
        double mediaO2 = 0;
        double mediaRpm = 0;
        double mediaTemp = 0;
        //List<MedicionGases> listaPromedios = sacarMediaDeListas(listaDeLista, temp);
        //Verificar si es necesaria la corrección
        int oxigenoCorregir = 0;
        System.out.println("nro de tiempos en la moto " + numeroTiempos);
        if (numeroTiempos == 4) {
            oxigenoCorregir = 6;
        }
        if (numeroTiempos == 2 && modelo >= 2010) {
            oxigenoCorregir = 6;
        }
        if (numeroTiempos == 2 && modelo < 2010) {
            oxigenoCorregir = 11;
        }

        System.out.println("el oxigeno a corregir es" + oxigenoCorregir);
        if (simulada == true) {
            for (MedicionGases med : lstDatoasMedicion) {
                if (med.getValorO2() * 0.01 >= oxigenoCorregir) {
                    System.out.println("entro correccion oxigeno ");
                    double oxigenoPromedioLista = med.getValorO2() * 0.01;
                    double factor = (21 - oxigenoCorregir) / (21 - oxigenoPromedioLista);
                    int nuevoHC = (int) ((int) med.getValorHC() * factor);
                    med.setValorHC(nuevoHC);
                    int nuevoCO = (int) (med.getValorCO() * factor);
                    med.setValorCO(nuevoCO);
                    double nuevoO2 = oxigenoPromedioLista * 100;
                    med.setValorO2(nuevoO2);
                }
            }
        }
        double permisibleHC = 0;
        double permisibleCO = 0;
        if (numeroTiempos == 4) {
            permisibleHC = 1300;
            System.out.println("--------permisible HC: " + permisibleHC + " --------------------------");
            permisibleCO = 3.5;
            System.out.println("---------permisibles CO: " + permisibleCO + " -------------------");
        } else if (numeroTiempos == 2) {
            if (modelo < 2010) {
                permisibleHC = 8000;
                permisibleCO = 3.5;
            } else {
                permisibleHC = 1600;
                permisibleCO = 3.5;
            }
        }
        //en este punto ya esta corregido los valores
        Double medCo;
        Double medHc;
        for (MedicionGases medicionGases : lstDatoasMedicion) {
            System.out.println("************ lista 10 datos promediada HC: " + medicionGases.getValHC());
            System.out.println("************ lista 10 datos promediada CO: " + medicionGases.getValCO());

            if (medicionGases.getValorO2() * 0.01 >= oxigenoCorregir) {
                double oxigenoPromedioLista = medicionGases.getValorO2() * 0.01;
                System.out.println("el valor de oxigeno se utiliza sin aproximacion: " + oxigenoPromedioLista);
                double factor = (21 - oxigenoCorregir) / (21 - oxigenoPromedioLista);
                medHc = medicionGases.getValHC() * factor;
                medCo = medicionGases.getValCO() * factor;
            } else {
                medHc = Double.parseDouble(String.valueOf(medicionGases.getValHC()));
                medCo = Double.parseDouble(String.valueOf(medicionGases.getValCO()));
            }

            if (mediaCO2 == 0) {
                mediaCO2 = medicionGases.getValCO2();
            } else {
                if (mediaCO2 < medicionGases.getValCO2()) {
                    mediaCO2 = medicionGases.getValCO2();
                }
            }
            if (mediaCO == 0) {
                mediaCO = medCo;
            } else {
                if (permisibleCO > mediaCO && medCo > permisibleCO) {//permisible co= 4.5  
                    mediaCO = medCo;
                }
                if (mediaCO > permisibleCO && permisibleCO < medCo) {
                    mediaCO = mediaCO;
                }

                if (mediaCO > permisibleCO && medCo > permisibleCO) {
                    if (medCo > mediaCO) {
                        mediaCO = medCo;
                    }
                }
                if (permisibleCO > mediaCO && permisibleCO > medCo) {
                    if (medCo > mediaCO) {
                        mediaCO = medCo;
                    }
                }
            }

            if (mediaHC == 0) {
                mediaHC = medHc;
            } else {
                if (permisibleHC > mediaHC && medHc > permisibleHC) {
                    mediaHC = medHc;
                }
                if (mediaHC > permisibleHC && permisibleHC < medHc) {
                    mediaHC = mediaHC;
                }
                if (mediaHC > permisibleHC && medHc > permisibleHC) {
                    if (medHc > mediaHC) {
                        mediaHC = medHc;
                    }
                }
                if (permisibleHC > mediaHC && permisibleHC > medHc) {
                    if (medHc > mediaHC) {
                        mediaHC = medHc;
                    }
                }
            }

            if (mediaO2 == 0) {
                mediaO2 = medicionGases.getValorO2();
            } else {
                if (medicionGases.getValorO2() > mediaO2) {
                    mediaO2 = medicionGases.getValorO2();
                }
            }
            /* System.out.println(" **********  ");
             System.out.println(" El valor de la media HC ES  " + medicionGases.getValorHC());
             System.out.println(" El valor de la media HC ES  " + mediaHC);
             System.out.println(" **********  ");
             System.out.println(" El valor de la media CO2 ES  " + medicionGases.getValorCO2());
             System.out.println(" El valor de la media CO2 ES  " + mediaCO2);

             System.out.println(" El valor de la media O2 ES  " + mediaO2);
             System.out.println(" El valor de la media O2 ES  " + medicionGases.getValorO2());*/
            mediaRpm = medicionGases.getValRPM();

            if (mediaTemp < temp) {//getValorTAceite aplica la correccion por linealidad del bano sensors
                mediaTemp = medicionGases.getValorTAceiteSinTabla();
            }
        }
        mediaCO = mediaCO * 0.01;
        mediaCO2 = mediaCO2 * 0.1;
        mediaO2 = mediaO2 * 0.01;
        double[] medidas = {mediaHC, mediaCO, mediaCO2, mediaO2, mediaRpm, mediaTemp};
        System.out.println("+++++++++++++++++++++++++++++++++++++++sali de procesarListas+++++++++++++++++++++++++++");
        return medidas;
    }//end of method procesarListaMediciones

    /**
     *
     * @param listaRpmUno
     * @return
     */
    private double media(List<? extends Number> listaRpmUno) {
        if (!listaRpmUno.isEmpty()) {
            double media = 0;
            for (Number d : listaRpmUno) {
                media = media + d.doubleValue();
            }

            return media / listaRpmUno.size();
        } else {
            return 0;
        }
    }//end of method media

    /**
     * arma las medidas dependiento
     *
     * @param medidas arreglo de doubles en el siguiente orden
     * HC,CO,CO2,O2,RPM,T
     * @return
     */
    private List<MedidaGeneral> armarMedidas(double[] medidas) {
        System.out.println("+++++++++++++++++++++++++++++++++++++++ingrese a armarMedidas+++++++++++++++++++++++++++");
        List<MedidaGeneral> listaMedidas = new ArrayList<MedidaGeneral>();

        if (numeroTiempos == 4) {
            //HC
            //medidas[0] = AproximarMedidas(medidas[0]);
            System.out.println("++++++++++++++++medida de HC:" + medidas[0] + "++++++++++++++++++++++++++++++");
            medidas[0] = AproximacionMedidas.AproximarMedidas(medidas[0]);
            listaMedidas.add(new MedidaGeneral(8001, medidas[0]));//lo del arreglo es un poco burdo
            //CO
            // medidas[1] = AproximarMedidas(medidas[1]);
            System.out.println("++++++++++++++++medida de CO:" + medidas[1] + "++++++++++++++++++++++++++++++");
            medidas[1] = AproximacionMedidas.AproximarMedidas(medidas[1]);
            listaMedidas.add(new MedidaGeneral(8002, medidas[1]));
            //CO2
            if (banco instanceof BancoCapelec) {
                //  medidas[2] = AproximarMedidas(medidas[2] / 10);
                medidas[2] = AproximacionMedidas.AproximarMedidas(medidas[2] / 10);
                listaMedidas.add(new MedidaGeneral(8003, medidas[2]));
            } else {
                // medidas[2] = AproximarMedidas(medidas[2]);
                medidas[2] = AproximacionMedidas.AproximarMedidas(medidas[2]);
                listaMedidas.add(new MedidaGeneral(8003, medidas[2]));
            }

            //O2
            // medidas[3] = AproximarMedidas(medidas[3]);
            medidas[3] = AproximacionMedidas.AproximarMedidas(medidas[3]);
            listaMedidas.add(new MedidaGeneral(8004, medidas[3]));

            //rpm
            listaMedidas.add(new MedidaGeneral(8005, medidas[4]));
            //temp
            listaMedidas.add(new MedidaGeneral(8006, medidas[5]));
        } else if (numeroTiempos == 2) {
            //HC
            //  medidas[0] = AproximarMedidas(medidas[0]);
            medidas[0] = AproximacionMedidas.AproximarMedidas(medidas[0]);
            listaMedidas.add(new MedidaGeneral(8018, medidas[0]));
            //CO
            // medidas[1] = AproximarMedidas(medidas[1]);
            medidas[1] = AproximacionMedidas.AproximarMedidas(medidas[1]);
            listaMedidas.add(new MedidaGeneral(8020, medidas[1]));
            //CO2
            if (banco instanceof BancoCapelec) {
                //medidas[2] = AproximarMedidas(medidas[2]/10);
                medidas[2] = AproximacionMedidas.AproximarMedidas(medidas[2] / 10);
                listaMedidas.add(new MedidaGeneral(8019, medidas[2]));
            } else {
                //  medidas[2] = AproximarMedidas(medidas[2]);
                medidas[2] = AproximacionMedidas.AproximarMedidas(medidas[2]);
                listaMedidas.add(new MedidaGeneral(8019, medidas[2]));
            }

            //O2
            // medidas[3] = AproximarMedidas(medidas[3]);
            medidas[3] = AproximacionMedidas.AproximarMedidas(medidas[3]);
            listaMedidas.add(new MedidaGeneral(8021, medidas[3]));
            //rpm
            listaMedidas.add(new MedidaGeneral(8028, medidas[4]));
            //temp
            listaMedidas.add(new MedidaGeneral(8022, medidas[5]));
        }
        System.out.println("GUARDE MEDIDAS");
        System.out.println("+++++++++++++++++++++++++++++++++++++++sali de armarMedidas+++++++++++++++++++++++++++");
        return listaMedidas;
    }

    private boolean evaluarPrueba(double[] medidas, int modelo) {
        System.out.println("-_-_-_-_-_-_-_-_-_-entre a evaluar rechazo de prueba -_-_-_-_-_-_-__--____--_--");
        System.out.println("en este metodo es donde se rechaza la prueba de gases para Moto");
        double valorCO = medidas[1];
        double valorHC = medidas[0];

        double permisibleHC = 0;
        double permisibleCO = 0;

        if (numeroTiempos == 4) {
            System.out.println("4T");
            System.out.println("cambiar valores en caso de nueva resolucion");
            System.out.println("limites para HC actual " + 1300);
            permisibleHC = 1300;
            System.out.println("cambiar valores en caso de nueva resolucion");
            System.out.println("limites para CO actual " + 3.5);
            permisibleCO = 3.5;

        } else if (numeroTiempos == 2) {
            if (modelo < 2010) {
                permisibleHC = 8000;
                permisibleCO = 3.5;
            } else {
                permisibleHC = 1600;
                permisibleCO = 3.5;
            }
        }

        if (valorCO <= permisibleCO && valorHC <= permisibleHC) {
            return true;
        } else {
            return false;
        }
    }

    private void liberarRecursos() {
        try {

            if (banco != null) {
                banco.getPuertoSerial().close();//aun asi el orden es importante como la forma
                SerialPort puertoSerial = banco.getPuertoSerial();

                if (puertoSerial != null) {
                    System.out.println("CIERRO EL PUERTO SERIAL ");
                    puertoSerial.close();
                    Thread.sleep(450);
                }
            } else {
                System.out.println("no cerre  banco y puerto serial ");
            }
            //si es otro dispositivo de medicion entonces cerrar el puerto
            if (!(medidorRevTemp instanceof BancoSensors) && medidorRevTemp != null) {
                System.out.println("entre en cierre de condicional de kit rpm");
                SerialPort puertoSerial = medidorRevTemp.getPuertoSerial();
                if (puertoSerial != null) {
                    System.out.println("CIERRA EL PUERTO SERIAL DEL KIT RPM");
                    puertoSerial.close(); //para no cerrarlo dos veces
                    Thread.sleep(300);
                }
                medidorRevTemp.getPuertoSerial().close();
                System.out.println(" cerrar kit rpm ");
                Thread.sleep(100);
            }
        } catch (Exception exc) {
            System.out.println("Excepcion cerrando puertos seriales debido a " + exc.getMessage());
            exc.printStackTrace();
        }
        System.out.println("sale del metodo de liberar recursos");
    }

    private MedicionGases maximaMedicionLista(List<MedicionGases> listaMediciones) {
        List<Integer> listaHC = new ArrayList<Integer>();
        List<Integer> listaCO = new ArrayList<Integer>();
        List<Integer> listaCO2 = new ArrayList<Integer>();
        List<Double> listaO2 = new ArrayList<Double>();
        List<Integer> listaRPM = new ArrayList<Integer>();
        List<Integer> listaTEMP = new ArrayList<Integer>();
        for (MedicionGases med : listaMediciones) {
            listaHC.add(med.getValorHC());
            listaCO.add(med.getValorCO());
            listaCO2.add(med.getValorCO2());
            listaO2.add(med.getValorO2());
            listaRPM.add(med.getValorRPM());
            int temp = med.getValorTAceiteSinTabla();
            if (temp <= 0 || temp > 110) {
                temp = med.getValorTAceite();
            }
            listaTEMP.add(temp);
        }
        int maximoHC = Collections.max(listaHC);
        int maximoCO = Collections.max(listaCO);
        int maximoCO2 = Collections.max(listaCO2);
        double maximoO2 = Collections.max(listaO2);
        int maximoRPM = Collections.max(listaRPM);
        int maximoTEMP = Collections.max(listaTEMP);
        MedicionGases medicion = new MedicionGases(maximoHC, maximoCO, maximoCO2, (int) maximoO2);
        System.out.println("Medicion Oxigeno" + maximoO2);
        medicion.setValorRPM(maximoRPM);
        medicion.setValorTAceite(maximoTEMP);
        return medicion;
    }

    public static void main(String args[]) {

        MedicionGases med1 = new MedicionGases(259, 23, 117, 463);
        med1.setValorRPM(1200);
        med1.setValorTAceite(52);

        MedicionGases med2 = new MedicionGases(256, 23, 117, 461);
        med2.setValorRPM(1200);
        med2.setValorTAceite(52);

        MedicionGases med3 = new MedicionGases(256, 22, 117, 460);
        med3.setValorRPM(1200);
        med3.setValorTAceite(52);

        MedicionGases med4 = new MedicionGases(256, 22, 117, 459);
        med4.setValorRPM(1200);
        med4.setValorTAceite(52);

        MedicionGases med5 = new MedicionGases(254, 22, 117, 458);
        med5.setValorRPM(1200);
        med5.setValorTAceite(52);

        MedicionGases med6 = new MedicionGases(254, 22, 117, 458);
        med6.setValorRPM(1200);
        med6.setValorTAceite(52);

        MedicionGases med7 = new MedicionGases(254, 22, 117, 457);
        med7.setValorRPM(1200);
        med7.setValorTAceite(52);

        MedicionGases med8 = new MedicionGases(242, 22, 117, 457);
        med8.setValorRPM(1200);
        med8.setValorTAceite(52);

        MedicionGases med9 = new MedicionGases(242, 22, 117, 457);
        med9.setValorRPM(1200);
        med9.setValorTAceite(52);

        MedicionGases med10 = new MedicionGases(235, 21, 117, 457);
        med10.setValorRPM(1200);
        med10.setValorTAceite(52);

        MedicionGases med11 = new MedicionGases(235, 21, 117, 457);
        med11.setValorRPM(1200);
        med11.setValorTAceite(52);

        MedicionGases med12 = new MedicionGases(235, 21, 117, 457);
        med12.setValorRPM(1200);
        med12.setValorTAceite(52);

        MedicionGases m1 = new MedicionGases(179, 19, 118, 437);
        m1.setValorRPM(1400);
        m1.setValorTAceite(64);

        MedicionGases m2 = new MedicionGases(179, 19, 118, 435);
        m2.setValorRPM(1400);
        m2.setValorTAceite(64);

        MedicionGases m3 = new MedicionGases(183, 19, 118, 435);
        m3.setValorRPM(1400);
        m3.setValorTAceite(64);

        MedicionGases m4 = new MedicionGases(183, 20, 118, 432);
        m4.setValorRPM(1400);
        m4.setValorTAceite(64);

        MedicionGases m5 = new MedicionGases(183, 20, 118, 432);
        m5.setValorRPM(1400);
        m5.setValorTAceite(64);

        MedicionGases m6 = new MedicionGases(183, 20, 118, 432);
        m6.setValorRPM(1400);
        m6.setValorTAceite(64);

        MedicionGases m7 = new MedicionGases(197, 20, 118, 432);
        m7.setValorRPM(1400);
        m7.setValorTAceite(64);

        MedicionGases m8 = new MedicionGases(197, 20, 118, 432);
        m8.setValorRPM(1400);
        m8.setValorTAceite(64);

        MedicionGases m9 = new MedicionGases(197, 20, 118, 433);
        m9.setValorRPM(1400);
        m9.setValorTAceite(64);

        MedicionGases m10 = new MedicionGases(197, 20, 118, 433);
        m10.setValorRPM(1400);
        m10.setValorTAceite(64);

        MedicionGases m11 = new MedicionGases(196, 20, 118, 433);
        m11.setValorRPM(1400);
        m11.setValorTAceite(64);

        MedicionGases m12 = new MedicionGases(196, 20, 118, 433);
        m12.setValorRPM(1400);
        m12.setValorTAceite(64);

        WorkerCiclosMoto worker = new WorkerCiclosMoto(null, null, 1, 1, 1, 2, 2, null, Double.MIN_VALUE, Double.MIN_VALUE);
        worker.modelo = 2011;

        List<MedicionGases> listaUno = new ArrayList<MedicionGases>();
        listaUno.add(med1);
        listaUno.add(med2);
        listaUno.add(med3);
        listaUno.add(med4);
        listaUno.add(med5);
        listaUno.add(med6);
        listaUno.add(med7);
        listaUno.add(med8);
        listaUno.add(med9);
        listaUno.add(med10);
        listaUno.add(med11);

        List<MedicionGases> listaDos = new ArrayList<MedicionGases>();
        listaDos.add(m1);
        listaDos.add(m2);
        listaDos.add(m3);
        listaDos.add(m4);
        listaDos.add(m5);
        listaDos.add(m6);
        listaDos.add(m7);
        listaDos.add(m8);
        listaDos.add(m9);
        listaDos.add(m10);
        listaDos.add(m11);

        List<List<MedicionGases>> listaDeLista = new ArrayList<List<MedicionGases>>();
        listaDeLista.add(listaUno);
        listaDeLista.add(listaDos);

        //worker.procesarListas(listaDeLista, 0);
    }

    private List<MedicionGases> sacarMediaDeListas(List<List<MedicionGases>> listaDeLista, int tempA) {
        List<MedicionGases> listaPromedios = new ArrayList<MedicionGases>();
        double mediaHC;
        double mediaCO;
        double mediaCO2;
        double mediaO2;
        double mediaRPM;
        double mediaTemp;

        for (List<MedicionGases> lista : listaDeLista) {
            mediaHC = 0;
            mediaCO = 0;
            mediaCO2 = 0;
            mediaO2 = 0;
            mediaRPM = 0;
            mediaTemp = 0;
            int tamanioLista = 0;
            System.out.println("TAMAÑO DE LA LST MET sacarMediaDeListas es " + tamanioLista);
            System.out.println("HC \t CO \t CO2 ;\t  O2 ");
            for (MedicionGases med : lista) {
                System.out.println(med.getValorHC() + ";\t" + med.getValorCO() + ";\t" + med.getValorCO2() + ";\t" + med.getValorO2());
                if (med.isLect5Seg() == true) {
                    mediaHC = mediaHC + med.getValorHC();
                    mediaCO = mediaCO + med.getValorCO();
                    mediaCO2 = mediaCO2 + med.getValorCO2();
                    mediaO2 = mediaO2 + med.getValorO2();
                    mediaRPM = mediaRPM + med.getValorRPM();
                    tamanioLista = tamanioLista + 1;
                }
            }
            //  System.out.println("Oxigeno FUR" + mediaO2 * 100);
            //  System.out.println("TERMINE DE SACAR LOS ACUMULADOS (sacarMediaDeListas) ");
            mediaHC = mediaHC / tamanioLista;
            mediaCO = mediaCO / tamanioLista;
            mediaCO2 = mediaCO2 / tamanioLista;
            mediaO2 = mediaO2 / tamanioLista;
            mediaRPM = mediaRPM / tamanioLista;
            mediaTemp = tempA;
            //mediaTemp = mediaTemp / tamanioLista;
            MedicionGases medicion = new MedicionGases((int) mediaHC, (int) mediaCO, mediaCO2, (int) mediaO2);
            medicion.setValorRPM((int) mediaRPM);
            medicion.setValorTAceite((int) mediaTemp);
            //  System.out.println("Medicion Temperatura: " + mediaTemp);
            //   System.out.println("Medicion OBJ : " + medicion.getValorTAceite());
            listaPromedios.add(medicion);
        }
        // System.out.println("TAMAÑO LST PROMEDIOS (sacarMediaDeListas) " + listaPromedios);
        return listaPromedios;
    }

    private void cancelacion(String causa) {
        panel.cerrar();
    }
    /*
    private double AproximarMedidas(Double Medida) { //Metodo para realizar la aproximacion de las medidas 
        Integer Bandera = 0;
        Double Bandera2;
        System.out.println("entro a aproximar medida :" + Medida);
        if (Medida > 99) {
            Bandera = Medida.intValue();
            Bandera2 = Medida - Bandera;
            Medida = (Bandera2 > 0.5) ? Medida + 1 : Medida;
           // return Medida;

        } else if (Medida > 10 && Medida < 100) {
            Medida = Medida * 10;
            Bandera = Medida.intValue();
            Bandera2 = Medida - Bandera;
            Medida = (Bandera2 > 0.5) ? Medida + 1 : Medida;
            Medida = Medida / 10;
           // return Medida;

        } else if (Medida < 10) {
            Medida = Medida * 100;
            Bandera = Medida.intValue();
            Bandera2 = Medida - Bandera;
            Medida = (Bandera2 > 0.5) ? Medida + 1 : Medida;
            Medida = Medida / 100;
            //return Medida;
        }
        System.out.println("despues de aproximar: "+ Medida);
        return Medida;
    }
     */
}//end of class
