package org.soltelec.pruebasgases.diesel;

import com.soltelec.loginadministrador.LoginServiceCDA;
import gnu.io.SerialPort;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.RowFilter.Entry;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.soltelec.conexion_seriales.Conexion;
import org.soltelec.medicionrpm.CapelecSerial;
import org.soltelec.medicionrpm.ConsumidorCapelec;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.medicionrpm.ProductorCapelec;
import org.soltelec.medicionrpm.centralauto.TB85000;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.PanelServicioOpacimetro;
import org.soltelec.pruebasgases.DialogoRPMTemp;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.pruebasgases.EvaluarRegistrarPruebaGasolina;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.AproximacionMedidas;
import org.soltelec.util.BufferStrings;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedidaGeneral;
import org.soltelec.util.Mensajes;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilConexion;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.Utilidades;
import org.soltelec.util.VariablesOpacidad;

import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;
import org.jdesktop.swingx.JXLoginPane;

/**
 *
 * Clase que llama al Callable (hilo) apropiado segun se seleccione el
 * dispositivo de medicion de las rpms ya sea simulacion o cualquiera de los
 * kits de revoluciones.
 *
 * @author Gerencia Desarrollo Soluciones Tecnologicas
 */
public class WorkerCiclosDiesel extends SwingWorker<Void, Void> {

    private PanelPruebaGases panel;
    private DialogoRPMTemp dialogRPMTemp;
    private Opacimetro opacimetro;
    private DialogoCiclosAceleracion dialogoCiclos;
    public static double diametroExosto;
    private final DecimalFormat df2;
    public static int aplicTrans = 1;
    public static String ipEquipo = "";
    public static String serialEquipo = "";
    public static String placas = "";
    public MedidorRevTemp medidorRevTemp;
    public double temp_motor;
    private static long idPrueba;
    private static long idUsuario;
    private static long idHojaPrueba;
    private static Connection conexionGlobal;
    private double tempInicial = 0;
    double tempAmbiente;
    double humedadAmbiente;

    public WorkerCiclosDiesel(PanelPruebaGases panel, Opacimetro opacimetro, long idUsuario, long idHojaPrueba, long idPrueba, double diametroExosto, double tempAmbiente, double humedadAmbiente) {

        this.panel = panel;
        this.opacimetro = opacimetro;
        this.idUsuario = idUsuario;
        this.idHojaPrueba = idHojaPrueba;
        this.idPrueba = idPrueba;
        dialogRPMTemp = new DialogoRPMTemp(idHojaPrueba);
        this.diametroExosto = diametroExosto;
        Locale locale = new Locale("US");//para que sea con punto separador de decimales
        df2 = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df2 instanceof DecimalFormat) {
            ((DecimalFormat) df2).setDecimalSeparatorAlwaysShown(true);
        }
        df2.setMinimumFractionDigits(2);//muestre siempre dos digitos decimales
        df2.setMaximumFractionDigits(2);//muestre siempre dos digitos decimales
        serialEquipo = cargarSerial();
        this.tempAmbiente = tempAmbiente;
        this.humedadAmbiente = humedadAmbiente;
    }

    public double WorkerCiclosDiesel() {
        /*Locale locale = new Locale("US");//para que sea con punto separador de decimales
        df2 = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df2 instanceof DecimalFormat) {
            ((DecimalFormat) df2).setDecimalSeparatorAlwaysShown(true);
        }
        df2.setMinimumFractionDigits(2);//muestre siempre dos digitos decimales
        df2.setMaximumFractionDigits(2);//muestre siempre dos digitos decimales
         */
        serialEquipo = cargarSerial();
        try {
            mostrarDialogoRPM();//Abre un dialogo par la seleccion del dispositivo con el que se miden las revoluciones.
            
            temp_motor = medidorRevTemp.getTemp();

            System.out.println("Numero de cilindros :" + dialogRPMTemp.getNumeroCilindros());
            System.out.println("Equipo de Medicion :" + dialogRPMTemp.getEquipo());
            System.out.println("Metodo de Medicion :" + dialogRPMTemp.getMetodoMedicion());
            System.out.println("Multiplicador :" + dialogRPMTemp.isUsaMultiplicador());

            Utilidades.setMetodoMedicionRpmDiesel(dialogRPMTemp.getMetodoMedicion());

            int numeroCilindros = dialogRPMTemp.getNumeroCilindros();//variable de clase??
            String equipoMedicion = dialogRPMTemp.getEquipo();
            // String metodoMedicion = dialogRPMTemp.getMetodoMedicion();
            //boolean multiplicador = dialogRPMTemp.isUsaMultiplicador();
            boolean simulacion = dialogRPMTemp.simulacion;

            if (equipoMedicion.equals("centralAuto")) {
                //configurar el metodo de
                //llamar al mismo callable polimorfico
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                System.out.println("PuertoCentralUto:" + nombrePuerto);

                TB85000 tb8500 = null;
                if (simulacion == false) {
                    SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    tb8500 = new TB85000(puertoSerial);
                    tb8500.cambiarCilindros(numeroCilindros);
                } else {
                    //tb8500 = new TB85000();
                }
                System.out.println("Voy a leer la primera temperatura del motor");
                temp_motor = medidorRevTemp.getTemp();

            }
        } catch (InterruptedException exc) {
            System.out.println("Tarea interrumpida");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(WorkerCiclosDiesel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(WorkerCiclosDiesel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return temp_motor;
        // }

    }

    @Override
    protected Void doInBackground() {
        //Mostrar el dialogo para seleccionar el tipo de revoluciones

        try {
            mostrarDialogoRPM();//Abre un dialogo par la seleccion del dispositivo con el que se miden las revoluciones.
            System.out.println("Numero de cilindros :" + dialogRPMTemp.getNumeroCilindros());
            System.out.println("Equipo de Medicion :" + dialogRPMTemp.getEquipo());
            System.out.println("Metodo de Medicion :" + dialogRPMTemp.getMetodoMedicion());
            System.out.println("Multiplicador :" + dialogRPMTemp.isUsaMultiplicador());
            int numeroCilindros = dialogRPMTemp.getNumeroCilindros();//variable de clase??
            String equipoMedicion = dialogRPMTemp.getEquipo();
            String metodoMedicion = dialogRPMTemp.getMetodoMedicion();
            boolean multiplicador = dialogRPMTemp.isUsaMultiplicador();
            boolean simulacion = dialogRPMTemp.simulacion;
            List<MedidaGeneral> listaMedidas = null;//Una lista de listas conteniendo las medidas de los tres ciclos de opacidad.
            ExecutorService executor = Executors.newSingleThreadExecutor();
            UtilCancelaciones.removerListeners(panel.getButtonFinalizar());
            UtilCancelaciones.removerListeners(panel.getButtonRpm());
            String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
            if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("Habilitado")) {
                MedicionTermoHigrometro medicion = null;
                String strMarcaTermohigrometro = UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");
                if (strMarcaTermohigrometro.equalsIgnoreCase("Artisan")) {
                    if (humedadAmbiente == 0 && tempAmbiente == 0) {
                        JOptionPane.showMessageDialog(null, "Disculpe, no DETECTO el Termohigrometro, por lo tanto esta prueba no se REALIZARA ..! ");
                        panel.cerrar();
                        return null;
                    }
                } else {
                    TermoHigrometro termoHigrometro = new TermoHigrometroPCSensors();
                    MedicionTermoHigrometro medicionTermoHigrometro = termoHigrometro.obtenerValores();
                    humedadAmbiente = termoHigrometro.obtenerValores().getValorHumedad();
                    tempAmbiente = termoHigrometro.obtenerValores().getValorTemperatura();
                }
                //Muestra de Validacion de Temperatura y Humedad                       
            }

            if (equipoMedicion.equals("kit")) {
                System.out.println("------------------------------------------");
                System.out.println("-------PRUEBA PARA EL KIT ------------");
                System.out.println("------------------------------------------");

                //Configurar para la medicion el kit
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoKit", "propiedades.properties");
                System.out.println(" --  PuertoKit : " + nombrePuerto);
                SerialPort serialPort = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                System.out.println(" --  serialPort : " + serialPort.getName());
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                CapelecSerial capelecServicios = new CapelecSerial();
                capelecServicios.setOut(out);
                // capelecServicios.enviarComandoNoParametros(CapelecSerial.RESET);
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
                CallableCiclosOpacidadSensors callable = new CallableCiclosOpacidadSensors(opacimetro, capelec, panel, dialogoCiclos, diametroExosto, idPrueba, idUsuario, simulacion, idHojaPrueba, tempAmbiente, humedadAmbiente);
                Future<List<MedidaGeneral>> future = executor.submit(callable);
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionDiesel(future, idPrueba, opacimetro, null, panel, idUsuario));
                panel.getButtonRpm().setText("Falla Subita");

                for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
                    panel.getButtonRpm().removeActionListener(al);
                }

                panel.getButtonRpm().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        registrarFallaSubita(idPrueba);
                    }
                });

                try {
                    listaMedidas = future.get();
                } catch (CancellationException ce) {
                    System.out.println("Prueba de Gases canceladaCapelec");
                    System.out.println("VoyCloseOpacimetro");
                    opacimetro.getPort().close();
                    System.out.println("VoyClosePortSerial");
                    serialPort.close();//cierra el puerto serial de todas formas.
                    System.out.println("VoyClosePanel");
                    panel.cerrar();
                    return null;

                } catch (InterruptedException exc) {
                    System.out.println("Tarea interrumpida");
                } finally {//ocurra o no ocurra el error se deben terminar estos hilos
                    capelec.setTerminado(true);//si no ocurre ninguna excepcion
                    Thread.sleep(500);
                    productor.setTerminado(true);//si no ocurre ninguna excepcion
                    Thread.sleep(500);
                    serialPort.close();//cierra el puerto serial de todas formas.
                }
            } else if (equipoMedicion.equals("centralAuto")) {
                //configurar el metodo de
                //llamar al mismo callable polimorfico
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoCentralAuto", "propiedades.properties");
                System.out.println("PuertoCentralUto:" + nombrePuerto);

                TB85000 tb8500 = null;
                if (simulacion == false) {
                    SerialPort puertoSerial = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    tb8500 = new TB85000(puertoSerial);
                    tb8500.cambiarCilindros(numeroCilindros);
                } else {
                    tb8500 = new TB85000();
                }
                Future<List<MedidaGeneral>> future = executor.submit(new CallableCiclosOpacidadSensors(opacimetro, tb8500, panel, dialogoCiclos, diametroExosto, idPrueba, idUsuario, simulacion, idHojaPrueba, tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionDiesel(future, idPrueba, opacimetro, null, panel, idUsuario));
                panel.getButtonRpm().setText("Falla Subita");

                for (ActionListener al : panel.getButtonRpm().getActionListeners()) {
                    panel.getButtonRpm().removeActionListener(al);
                }

                panel.getButtonRpm().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        registrarFallaSubita(idPrueba);
                    }
                });
                
                try {
                    listaMedidas = future.get();
                } catch (CancellationException ce) {
                    System.out.println("Prueba de Gases canceladaKitCentralAuto");
                    System.out.println("VoyCloseKit");
                    panel.cerrar();
                    if (simulacion == false) {
                        tb8500.getPuertoSerial().close();//cierra de todas formas el puerto serial.
                    }
                    System.out.println("VoyCloseOpacimetro");
                    opacimetro.getPort().close();
                    System.out.println("VoyClosePanel");
                    
                    return null;
                } finally {
                    System.out.println("VoyCloseKit");
                    if (simulacion == false) {
                        tb8500.getPuertoSerial().close();//cierra de todas formas el puerto serial.               
                    }
                }
            } //por el momento no se incluye el kit tb8600
            if (listaMedidas != null) {
                registrarMedidas(listaMedidas);
                registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);
            }
            panel.getPanelMensaje().setText(" POR FAVOR RETIRE LA SONDA");
            Thread.sleep(1000);
            //Evaluar la prueba y registrar las medidas
        } catch (ExecutionException ejex) {
            ejex.printStackTrace();
            if (ejex.getCause() instanceof DeficienteOperacionException) {
                //REGISTRAR LA PRUEBA COMO PERDIDA Y EL DEFECTO CORRESPONDIENTE
                Set<Integer> setDefectos = new HashSet<>();
                setDefectos.add(80000);
                RegistrarMedidas regMedidas = new RegistrarMedidas();
                try (Connection cn = regMedidas.getConnection()) {
                    regMedidas.registrarDefectos(setDefectos, idPrueba, idUsuario);
                    regMedidas.registrarPruebaFinalizadaConObservacion(cn, false, idUsuario, idPrueba, true, ipEquipo, DeficienteOperacionException.tipoException);
                    if (dialogRPMTemp.simulacion == true) {
                        try {
                            registrarMedidasCondAnormales(CallableSimulacionDiesel.listaMedidasTemp);
                        } catch (Exception ex) {
                        }
                    } else {
                        try {
                            registrarMedidasCondAnormales(CallableCiclosOpacidadSensors.listaMedidasTemp);
                        } catch (Exception ex) {
                        }
                    }
                } catch (ClassNotFoundException | SQLException exc) {
                    Mensajes.mostrarExcepcion(exc);
                }
            } else {
                Logger.getRootLogger().error(ejex);
                ejex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Excepcion durante la prueba " + ejex.getMessage());
            }
        } catch (Exception exc) {
            Logger.getRootLogger().error(exc);
            exc.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Excepcion durante la prueba");
        } finally {
            if (opacimetro != null) {
                opacimetro.getPort().close();
            }
            panel.cerrar();
        }
        return null;
    }

    private void registrarFallaSubita(long idPrueba) {

        String sqlInsertDefecto = "INSERT INTO defxprueba (id_defecto, id_prueba) VALUES (80000, ?)";
        String sqlUpdatePrueba = "UPDATE pruebas SET observaciones = ?, Aprobada = ?, Finalizada = ?, serialEquipo = ? WHERE id_pruebas = ?";
    
        System.out.println("----------------------------------------------------------------------");
        System.out.println("-----------Temperatura: " + VariablesOpacidad.getTemperaturaMotor() + "--------------------");
        System.out.println("----------------------------------------------------------------------");
    
        Conexion.setConexionFromFile();
    
        registrarTemperaturaHumedad(tempAmbiente, humedadAmbiente);
        registrarTemperaturaMotorInicial(VariablesOpacidad.getTemperaturaMotor());
    
        try (Connection conexion = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());
             PreparedStatement insertarDefecto = conexion.prepareStatement(sqlInsertDefecto);
             PreparedStatement updatePruebasStmt = conexion.prepareStatement(sqlUpdatePrueba)) {
    
            // Establecer los parámetros para la inserción
            insertarDefecto.setLong(1, idPrueba);
    
            // Ejecutar la inserción
            insertarDefecto.executeUpdate();
    
            // Establecer los parámetros para la actualización
            updatePruebasStmt.setString(1, "Falla súbita del motor y/o sus accesorios");
            updatePruebasStmt.setString(2, "N");
            updatePruebasStmt.setString(3, "Y");
            updatePruebasStmt.setString(4, cargarSerial());
            updatePruebasStmt.setLong(5, idPrueba);
    
            // Ejecutar la actualización
            updatePruebasStmt.executeUpdate();
    
            // Mostrar mensaje y esperar 3 segundos antes de cerrar la aplicación
            JOptionPane.showMessageDialog(null, "Prueba rechazada y finalizada con éxito. \nPor seguridad cerraremos el programa", "Información", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            
        } catch (SQLException ex) { 
            ex.printStackTrace();
            System.out.println("Error al tratar de rechazar y finalizar la prueba por falla súbita: " + ex.getMessage());
        }
    }

    public void mostrarDialogoRPM() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        d.getContentPane().add(dialogRPMTemp);
        d.setSize(500, 300);
        d.setTitle("Seleccion KIT RPM");
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panel);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setVisible(true);
    }//end of method mostrarDialogoRPM

    private void registrarMedidasCondAnormales(List<MedidaGeneral> listaMedidas) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        //La primera es el maximo del ciclo 1        

        MedidaGeneral maximoCiclo0 = listaMedidas.get(0);//8033

        MedidaGeneral maximoCiclo1 = listaMedidas.get(1);//8013

        MedidaGeneral maximoCiclo2 = listaMedidas.get(2);//8014

        MedidaGeneral maximoCiclo3 = listaMedidas.get(3);//8015

        MedidaGeneral promedio = listaMedidas.get(4);//8017

        Double promedioTresMediciones = promedio.getValorMedida();

        MedidaGeneral opacidad = listaMedidas.get(5);//8030

        MedidaGeneral temperaturaMotor = listaMedidas.get(6);//8034

        MedidaGeneral velocidadRal = listaMedidas.get(7);//8035

        MedidaGeneral velocidadGobernada = listaMedidas.get(8);//8036
        MedidaGeneral tempMotorFinal = listaMedidas.get(9); //8037
        MedidaGeneral gob0 = listaMedidas.get(10);//8038
        MedidaGeneral gob1 = listaMedidas.get(11);//8039
        MedidaGeneral gob2 = listaMedidas.get(12);//8040
        MedidaGeneral gob3 = listaMedidas.get(13);//8041

        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 8033);
        instruccion.setDouble(2, maximoCiclo0.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8013);//Opacidad maxima primer ciclo
        instruccion.setDouble(2, maximoCiclo1.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8014);//Opacidad maxima segundo ciclo
        instruccion.setDouble(2, maximoCiclo2.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8015);//Opacidad maxima tercer ciclo
        instruccion.setDouble(2, maximoCiclo3.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8017);//Promedio de los tres ciclos
        instruccion.setDouble(2, promedio.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8034);//Temperatura del motor
        instruccion.setDouble(2, temperaturaMotor.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8035);//Velocidad Ralenti
        instruccion.setDouble(2, velocidadRal.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8036);//Velocidad Gobernada
        instruccion.setDouble(2, velocidadGobernada.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        System.out.println("Registro de la TFMD");
        instruccion.setInt(1, 8037);//Temperatura Final Motor
        instruccion.setDouble(2, tempMotorFinal.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8038);//Gob0
        instruccion.setDouble(2, gob0.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8039);//Gob1
        instruccion.setDouble(2, gob1.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8040);//Gob2
        instruccion.setDouble(2, gob2.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8041);//Gob3
        instruccion.setDouble(2, gob3.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        System.out.println("Medidas Registradas");
    }//end of method registarMedidas

    public static void registrarMedidas(List<MedidaGeneral> listaMedidas) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {

        //Crear el objeto de <span class="IL_AD" id="IL_AD12">conexion</span> a la base de datos
        Connection conexion = Conex.getConnection();
        conexion.setAutoCommit(false);
        //La primera es el maximo del ciclo 1
        MedidaGeneral maximoCiclo0 = listaMedidas.get(0);//8033

        MedidaGeneral maximoCiclo1 = listaMedidas.get(1);//8013

        MedidaGeneral maximoCiclo2 = listaMedidas.get(2);//8014

        MedidaGeneral maximoCiclo3 = listaMedidas.get(3);//8015

        MedidaGeneral promedio = listaMedidas.get(4);//8017

        Double promedioTresMediciones = promedio.getValorMedida();

        MedidaGeneral opacidad = listaMedidas.get(5);//8030

        MedidaGeneral temperaturaMotor = listaMedidas.get(6);//8034

        MedidaGeneral velocidadRal = listaMedidas.get(7);//8035

        MedidaGeneral velocidadGobernada = listaMedidas.get(8);//8036
        MedidaGeneral tempMotorFinal = listaMedidas.get(9); //8037
        MedidaGeneral gob0 = listaMedidas.get(10);//8038
        MedidaGeneral gob1 = listaMedidas.get(11);//8039
        MedidaGeneral gob2 = listaMedidas.get(12);//8040
        MedidaGeneral gob3 = listaMedidas.get(13);//8041

        String sqlInfo = "SELECT CAR,CARTYPE,Modelo,Tiempos_motor,FUELTYPE,SERVICE,Cinlindraje from vehiculos as v inner join hoja_pruebas as hp on v.CAR=hp.Vehiculo_for where hp.TESTSHEET = ?";
        PreparedStatement psInfo = conexion.prepareStatement(sqlInfo);
        psInfo.setLong(1, idHojaPrueba);
        ResultSet rs = psInfo.executeQuery();
        rs.next();//primer registro
        int modelo = rs.getInt("Modelo");
        int cilindraje = rs.getInt("Cinlindraje");
        //EVALUAR LA PRUEBA
        long car = rs.getLong("CAR");

        // String strDiametro = "UPDATE vehiculos SET Diametro = ? WHERE CAR = ?";
        // PreparedStatement psDiametro = conexion.prepareStatement(strDiametro);
        // psDiametro.setInt(1, (int) diametroExosto);
        // psDiametro.setLong(2, car);
        //  psDiametro.executeUpdate();
        boolean pruebaAprobada = false;

//        if (modelo <= 1970) {
//            if (promedioTresMediciones < 50) {
//                pruebaAprobada = true;
//            } else {
//                pruebaAprobada = false;
//            }
//        } else if (modelo > 1970 && modelo <= 1984) { // tambien esta bie 
//            if (promedioTresMediciones < 45) {
//                pruebaAprobada = true;
//            } else {
//                pruebaAprobada = false;
//            }
//        } else if (modelo > 1984 && modelo <= 1997) { // este esta bien segun la tabla expuesta
//            if (promedioTresMediciones < 40) {
//                pruebaAprobada = true;
//            } else {
//                pruebaAprobada = false;
//            }
//        } else if (modelo > 1997) {// tambien esta bien 
//            if (promedioTresMediciones < 35) {
//                pruebaAprobada = true;
//            } else {
//                pruebaAprobada = false;
//            }
//        }
        
                    if (modelo <= 2000) {
                if (cilindraje < 5000) {
                    if (promedioTresMediciones < 6.0) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                } else if (cilindraje >= 5000) {
                    if (promedioTresMediciones < 5.5) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                }
            } else if (modelo > 2000 && modelo <= 2015) {
                if (cilindraje < 5000) {
                    if (promedioTresMediciones < 5.0) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                } else if (cilindraje >= 5000) {
                    if (promedioTresMediciones < 4.5) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                }
            } else {
                if (cilindraje < 5000) {
                    if (promedioTresMediciones < 4.0) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                } else if (cilindraje >= 5000) {
                    if (promedioTresMediciones < 3.5) {
                        pruebaAprobada = true;
                    } else {
                        pruebaAprobada = false;
                    }
                }
            }

        boolean escrTrans = false;
        if (aplicTrans == 1 && pruebaAprobada == true) {
            escrTrans = true;
        }
        if (aplicTrans == 0) {
            escrTrans = true;
        }
        String statement2;
        String statement = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
        PreparedStatement instruccion = conexion.prepareStatement(statement);
        instruccion.setInt(1, 8033);
        System.out.println("maximociclo0---------------------------------------------------------------------------------------------------"+maximoCiclo0.getValorMedida());
        instruccion.setDouble(2, maximoCiclo0.getValorMedida());
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8013);//Opacidad maxima primer ciclo
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(maximoCiclo1.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8014);//Opacidad maxima segundo ciclo
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(maximoCiclo2.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8015);//Opacidad maxima tercer ciclo
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(maximoCiclo3.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8017);//Promedio de los tres ciclos
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(promedio.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8034);//Temperatura del motor
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(temperaturaMotor.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8035);//Velocidad Ralenti
        instruccion.setDouble(2,AproximacionMedidas.AproximarMedidas(velocidadRal.getValorMedida()));//modificado el dia 27-07-2023
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8036);//Velocidad Gobernada
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(velocidadGobernada.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        System.out.println("Registro de la TFMD");
        instruccion.setInt(1, 8037);//Temperatura Final Motor
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(tempMotorFinal.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8038);//Gob0
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(gob0.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8039);//Gob1
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(gob1.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8040);//Gob2
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(gob2.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        instruccion.setInt(1, 8041);//Gob3
        instruccion.setDouble(2, AproximacionMedidas.AproximarMedidas(gob3.getValorMedida()));
        instruccion.setLong(3, idPrueba);
        instruccion.executeUpdate();
        instruccion.clearParameters();

        if (escrTrans == true) {
            statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada=?,Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            if (pruebaAprobada) {
                instruccion2.setString(1, "Y");
            } else {
                instruccion2.setString(1, "N");
            }
            instruccion2.setLong(2, idUsuario);
            instruccion2.setString(3, serialEquipo);
            instruccion2.setLong(4, idPrueba);
            instruccion2.executeUpdate();
            //insertar el unico defecto que corresponde con gases
            if (!pruebaAprobada) {
                String strDefecto = "INSERT INTO defxprueba(id_defecto,id_prueba) VALUES (?,?)";
                PreparedStatement psDefecto = conexion.prepareStatement(strDefecto);
                psDefecto.setInt(1, 80000);
                psDefecto.setLong(2, idPrueba);
                psDefecto.executeUpdate();
            }
        } else {
            statement2 = "UPDATE pruebas SET Autorizada = 'A',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
            PreparedStatement instruccion2 = conexion.prepareStatement(statement2);
            instruccion2.setLong(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setLong(3, idPrueba);
            int executeUpdate = instruccion2.executeUpdate();
        }
        conexion.commit();
        conexion.setAutoCommit(true);
        conexion.close();
        System.out.println("Medidas Registradas con Exito");
    }//end of method registarMedidas

    /**
     * Metodo que contiene la logica principal para realizar el rechazo de la
     * prueba por temas de temperatura
     *
     * @autor ELKIN B
     * @param listaMedidas
     */
    public static void registrarMedidasRechazada(List<MedidaGeneral> listaMedidas) {
        System.out.println("------------------------------------------------");
        System.out.println("---- Registrar Medidas Prueba Rechazada   ------");
        System.out.println("------------------------------------------------");
        try {
            conexionGlobal = Conex.getConnection();
            conexionGlobal.setAutoCommit(false);
            // actualizarDiametroExostoVehiculo(idHojaPrueba, diametroExosto);
            insertarMedidas(cargarTipoYMedida(listaMedidas), idPrueba);
            updatePruebaDB(idUsuario, serialEquipo, idPrueba);
            conexionGlobal.commit();
            conexionGlobal.setAutoCommit(true);
            conexionGlobal.close();
            System.out.println("Medidas Registradas con Exito");
        } catch (SQLException e) {
            System.out.println("Error en el metodo : registrarMedidasRechazada()");
            System.out.println("Error : " + e);
            Mensajes.mostrarExcepcion(e);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WorkerCiclosDiesel.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en el metodo : registrarMedidasRechazada()" + ex.getMessage());
            System.out.println("Error : " + ex);
            Mensajes.mostrarExcepcion(ex);
        }
    }

    /**
     * Metodo que carga en un mapa las medidas y los valores a insertar en la db
     *
     * @autor ELKIN B
     * @param listaMedidas
     * @return UN MAP
     */
    private static Map<Integer, Double> cargarTipoYMedida(List<MedidaGeneral> listaMedidas) {
        Map<Integer, Double> mapMedidas = new HashMap<Integer, Double>();
        try {
            mapMedidas.put(8033, listaMedidas.get(0).getValorMedida());
            mapMedidas.put(8013, listaMedidas.get(1).getValorMedida());
            mapMedidas.put(8014, listaMedidas.get(2).getValorMedida());
            mapMedidas.put(8015, listaMedidas.get(3).getValorMedida());
            mapMedidas.put(8017, listaMedidas.get(4).getValorMedida());
            mapMedidas.put(8034, listaMedidas.get(6).getValorMedida());
            mapMedidas.put(8035, listaMedidas.get(7).getValorMedida());
            mapMedidas.put(8036, listaMedidas.get(8).getValorMedida());
            System.out.println("Registro Temperatura Final Motor");
            mapMedidas.put(8037, listaMedidas.get(9).getValorMedida());
            System.out.println("Registro gobernadas");
            mapMedidas.put(8038, listaMedidas.get(10).getValorMedida());//Gob0
            mapMedidas.put(8039, listaMedidas.get(11).getValorMedida());//Gob1
            mapMedidas.put(8040, listaMedidas.get(12).getValorMedida());//Gob2   
            mapMedidas.put(8041, listaMedidas.get(13).getValorMedida());//Gob3
        } catch (Exception e) {
            System.out.println("Erro en el metodo : cargarTipoYMedida()");
            System.out.println("Error : " + e);
            Mensajes.mostrarExcepcion(e);
        }
        return mapMedidas;
    }

    /**
     * Metodo que actualiza el estado de la prueba a finalizada y reprobada
     *
     * @autor ELKIN B
     * @param idUsuario
     * @param serialEquipo
     * @param idPrueba
     */
    private static void updatePruebaDB(long idUsuario, String serialEquipo, long idPrueba) {
        System.out.println("------------------------------------------------");
        System.out.println("-------        updatePruebaDB             ------");
        System.out.println("------------------------------------------------");

        String statement2 = "UPDATE pruebas SET Finalizada = 'Y',Aprobada='N',Abortada='N',usuario_for = ?,serialEquipo = ? WHERE pruebas.Id_Pruebas = ?";
        try {
            PreparedStatement instruccion2 = conexionGlobal.prepareStatement(statement2);
            instruccion2.setLong(1, idUsuario);
            instruccion2.setString(2, serialEquipo);
            instruccion2.setLong(3, idPrueba);
            instruccion2.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro en el metodo : updatePruebaDB()");
            System.out.println("Error : " + e);
            Mensajes.mostrarExcepcion(e);
        }
    }

    /**
     * Metodo que inserta medidas para la prueba finalizada y reporbada por
     * temperatura
     *
     * @autor ELKIN B
     * @param conexion
     * @param query
     * @param tipoMedida
     * @param valorMedida
     * @param idHojaPrueba
     */
    private static void insertarMedidas(Map<Integer, Double> mapMedidas, long idHojaPrueba) {
        System.out.println("------------------------------------------------");
        System.out.println("-------     insertarMedidas               ------");
        System.out.println("------------------------------------------------");
        try {
            String insert = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            for (Map.Entry<Integer, Double> o : mapMedidas.entrySet()) {
                int llave = o.getKey();
                double valor = o.getValue();
                System.out.println(" llave  " + llave);
                System.out.println(" valor  " + valor);

                PreparedStatement instruccion = conexionGlobal.prepareStatement(insert);
                instruccion.setInt(1, llave);
                instruccion.setDouble(2, valor);
                instruccion.setLong(3, idHojaPrueba);
                instruccion.executeUpdate();
                instruccion.clearParameters();
            }
        } catch (SQLException e) {
            System.out.println("Erro en el metodo : insertarMedidas()");
            System.out.println("Error : " + e);
            System.out.println(" " + e.getSQLState());
            System.out.println(" " + e.getNextException() + e.getMessage());
            Mensajes.mostrarExcepcion(e);
        }
    }

    /**
     * Metodo que permite actualizar el diametro del exosto al vehiculo
     *
     * @Autor ELKIN B
     * @param conexion
     * @param idHojaPrueba
     * @param diametroExosto
     */
    public static void actualizarDiametroExostoVehiculo(long idHojaPrueba, int diametroExosto) {
        System.out.println("------------------------------------------------");
        System.out.println("------- actualizarDiametroExostoVehiculo  ------");
        System.out.println("------------------------------------------------");

        try {
            String sqlInfo = "SELECT CAR,CARTYPE,Modelo,Tiempos_motor,FUELTYPE,SERVICE from vehiculos as v inner join hoja_pruebas as hp on v.CAR=hp.Vehiculo_for where hp.TESTSHEET = ?";
            PreparedStatement psInfo = conexionGlobal.prepareStatement(sqlInfo);
            psInfo.setLong(1, idHojaPrueba);
            ResultSet rs = psInfo.executeQuery();
            rs.next();//primer registro
            long idVehiculo = rs.getLong("CAR");

            String strDiametro = "UPDATE vehiculos SET Diametro = ? WHERE CAR = ?";
            PreparedStatement psDiametro = conexionGlobal.prepareStatement(strDiametro);
            psDiametro.setInt(1, diametroExosto);
            psDiametro.setLong(2, idVehiculo);
            psDiametro.executeUpdate();
            System.out.println("actualice dametro del exosto, valor que recibi: " + diametroExosto);
        } catch (SQLException e) {
            System.out.println("Erro en el metodo : actualizarDiametroExostoVehiculo()");
            System.out.println("Error : " + e);
            Mensajes.mostrarExcepcion(e);
        }
    }

    /**
     * Metodo que carga el serial el equipo con el cual se realizo la prueba
     *
     * @autor ELKIN B
     * @return string
     */
    private static String cargarSerial() {
        System.out.println("--------------------------------------------");
        System.out.println("---------      Cargando Serial    ----------");
        System.out.println("--------------------------------------------");

        String serialEquipo = "";
        try {
            serialEquipo = ConsultarDatosVehiculo.buscarSerialEquipo(idPrueba);
            System.out.println(" -- Serial encontrado : " + serialEquipo);
        } catch (Exception e) {
            serialEquipo = "Serial no encontrado";
            e.printStackTrace();
        }
        return serialEquipo;
    }

    /**
     *
     * @param conjuntoDefectos
     */
    private void registrarDefectos(Set<Integer> conjuntoDefectos) {
        RegistrarMedidas regMedidas = new RegistrarMedidas();
        try (Connection cn = regMedidas.getConnection()) {
            if (aplicTrans == 0) {
                regMedidas.registrarDefectos(conjuntoDefectos, idPrueba, idUsuario);
            }
            regMedidas.registrarPruebaFinalizada(cn, false, idUsuario, idPrueba, false, ipEquipo);
        } catch (ClassNotFoundException | SQLException exc) {
            Mensajes.mostrarExcepcion(exc);
        }
    }//end of method registrarDeficienteOperacion

    private void registrarTemperaturaHumedad(Double tempAmbiente, Double humedadAmbiente) {
        Connection cn = null;
        try {
            cn = UtilConexion.obtenerConexion();
            String strInsert = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = cn.prepareStatement(strInsert);
            //insertar la temperatura
            cn.setAutoCommit(false);
            ps.setInt(1, 8031);
            ps.setDouble(2, tempAmbiente);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            ps.clearParameters();
            ps.setInt(1, 8032);
            ps.setDouble(2, humedadAmbiente);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            cn.commit();
            cn.setAutoCommit(true);
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "No se pueden grabar los valores de humedad y temperatura");
            Logger.getRootLogger().error("No se pueden grabar los valores de humedad y temperatura");
            exc.printStackTrace();

        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private void registrarTemperaturaMotorInicial(double tempIniMotor) {

        if (tempIniMotor == 0) JOptionPane.showMessageDialog(null, "La temperatura inicial del motor fue 0");
        Connection cn = null;
        try {
            cn = UtilConexion.obtenerConexion();
            String strInsert = "INSERT INTO medidas(MEASURETYPE,Valor_medida,TEST) VALUES(?,?,?)";
            PreparedStatement ps = cn.prepareStatement(strInsert);
            //insertar la temperatura
            cn.setAutoCommit(false);
            ps.setInt(1, 8034);
            ps.setDouble(2, tempIniMotor);
            ps.setLong(3, idPrueba);
            ps.executeUpdate();
            cn.commit();
            cn.setAutoCommit(true);
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "No se pueden grabar los valores de temperatura inicial motor");
            Logger.getRootLogger().error("No se pueden grabar los valores de temperatura inicial motor");
            exc.printStackTrace();

        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
            }
        }
    }

    class ListenerCancelacionDeficienteOperacion implements ActionListener {

        Future futureParaCancelar;

        public ListenerCancelacionDeficienteOperacion(Future futureParaCancelar) {
            this.futureParaCancelar = futureParaCancelar;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            futureParaCancelar.cancel(true);
            Set<Integer> conjuntoDefectos = new HashSet<Integer>();
            conjuntoDefectos.add(80000);
            RegistrarMedidas regMedidas = new RegistrarMedidas();
            try (Connection cn = regMedidas.getConnection()) {
                regMedidas.registrarDefectos(conjuntoDefectos, idPrueba, idUsuario);
                regMedidas.registrarPruebaFinalizadaConObservacion(cn, false, idUsuario, idPrueba, true, ipEquipo, "La diferencia aritmetica entre el valor mayor y menor de opacidad de las tres aceleraciones.");
            } catch (ClassNotFoundException | SQLException exc) {
                Mensajes.mostrarExcepcion(exc);
            }
            try {
                panel.cerrar();
                opacimetro.getPort().close();
            } catch (Exception ignorada) {

            }

        }

    }//end of class ListenerCancelacionDeficienteOperacion

}//end of class WorkerCiclosDiesel
