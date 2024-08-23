/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.Logger;
import org.soltelec.medicionrpm.JDialogReconfiguracionKit;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.procesosbanco.EstabilizacionMedidas;
import org.soltelec.procesosbanco.PanelCero;
import org.soltelec.procesosbanco.WorkerCalentamiento;
import org.soltelec.util.BloqueoEquipoUtilitario;
import org.soltelec.util.Conex;
import org.soltelec.util.ConsultarDatosVehiculo;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.UtilPropiedades;

/**
 *
 * Esta clase es la que da inicio a la prueba de gases hasta el momento en el
 * que es necesario seleccionar el dispositivo de medicion de las revoluciones
 *
 * Configuracion del puerto serial por archivo es decir se debe especificar el
 * puerto no lo busca automaticamente
 *
 * @author Usuario
 */
/*Codigos medidas de gases
 * 8001 HC en ralenti
 * 8002 CO en ralenti
 * 8003 CO2 en ralenti
 * 8004 O2 en ralenti
 * 8005 rpm en ralenti
 * 8006 temp en ralenti
 * 8007 HC en crucero
 * 8008 CO en crucero
 * 8009 CO2 en crucero
 * 8010 O2 en crucero
 * 8011 rpm en crucero
 * 8012 temp en crucero
 *
 */
public class CallableInicioGases implements Callable<Void> {

    private BancoGasolina banco;///se obtiene por búsqueda
    private final PanelPruebaGases panel;//Este panel es quien invoca al hilo y es donde el hilo pondra y removera otros paneles
    private boolean reiniciarProceso = false;
    private boolean Prueba;
    String Estabilizacion = "";
    private WorkerCalentamiento workerCalentamiento;
    // private Timer timer, timerSimulacion;//timer de un segundo para los diversos retardos
    private final DialogoRPMTemp dialogRPMTEmp;
    // private int contadorTemporizacion;
    private EstabilizacionMedidas estabilizacion;
    double[] arregloMedidas = new double[4];
    double[] arregloFiltradas = new double[10];
    MedicionGases med;

    //private static final double LIMITE_HC = 20;
    //valores para el filtro logico de las rpm es decir para detectar valores absurdos
    //private int UMBRAL_RANGO_0_1500;
    //private int UMBRAL_RANGO_1500_2200;
    //private int UMBRAL_RANGO_2200_2700;
    private JDialogReconfiguracionKit dialogReconfiguracion;
    // private String urljdbc;
    private long idPrueba = 22;
    private long idUsuario = 1;
    private long idHojaPrueba = 4;
    private String placas;
        private final double tempAmbiente;
    private final double humedadAmbiente;

    public CallableInicioGases(PanelPruebaGases panel, long idPrueba, long idUsuario, BancoGasolina banco, long hojaPrueba, String placas, double tempAmbiente, double humedadAmbiente) {
        this.panel = panel;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
        this.banco = banco;
        this.idHojaPrueba = hojaPrueba;
        dialogRPMTEmp = new DialogoRPMTemp(hojaPrueba);
        this.placas = placas;
        this.Prueba = true;
        this.tempAmbiente = tempAmbiente;
        this.humedadAmbiente = humedadAmbiente;
    }

    @Override
    public Void call() throws Exception {//proceso principal para la prueba del banco
        MedicionGases medicion = null;
        panel.getButtonFinalizar().setVisible(false);
        double oxigeno = 0;

        

        //el alcance de medicion es el metodo run
        try {
            medicion = banco.obtenerDatos();
            panel.getPanelMensaje().getCronometro().setText("");
            panel.getPanelMensaje().setText(" ASEGURESE DE : \n VEHICULO EN NEUTRO O EN POSICION DE PARQUEO\n"
                    + "EL MOTOR DEBE ESTAR APAGADO");
            Thread.sleep(3500);
            panel.getPanelMensaje().setText(" ASEGURESE DE :\n LUCES DEL VEHICULO ENCENDIDAS Y OTROS DISPOSITIVOS APAGADOS");
            Thread.sleep(3500);
            //while (!reiniciarProceso) {
            //reiniciar por filtros sucios en la linea de muestra
            if (medicion.isCalentamiento()) {
                out.println("Banco esta en calentamiento");
                panel.getProgressBar().setVisible(true);
                //Usando join no lo puedo creer
                panel.getPanelMensaje().setText("Por favor Espere \n Banco En Calentamiento...");
                panel.getPanelMensaje().getCronometro().setText("");
                try {
                    workerCalentamiento = new WorkerCalentamiento(this.banco, this.panel.getProgressBar(), this.panel.getPanelMensaje().getCronometro());

                } catch (Exception e) {
                    System.out.println("---------Exception--------:" + e.getMessage());
                }
                Thread t = new Thread(workerCalentamiento);
                t.start();
                //Uniendo dos hilos este  se bloquea hasta que el otro termine
                t.join();
                panel.getProgressBar().setVisible(false);
                
                //CREO FUNCION PARA MEDIR HC,CO Y CO2 DURANTE 5 MINUTOS
                Estabilizacion = UtilPropiedades.cargarPropiedad("Estabilizacion", "propiedades.properties");//LEE EL VALOR DE LA VARIABLE TemporizadorOpacimetro
                Estabilizacion = (Estabilizacion == null) ? "false" : Estabilizacion;
                System.out.println((Estabilizacion == "" || Estabilizacion.equalsIgnoreCase("false")) ? "variable estabilizacion configurada en false" : "variable estabilizacion configurada en true");

                if (Estabilizacion.equalsIgnoreCase("true")) {

                    System.out.println("voy a entrar a hilo de mestabilizacion");
                    estabilizacion = new EstabilizacionMedidas(this.panel, this.banco, 1);
                    //CREO HILO
                    Thread t2 = new Thread(estabilizacion);
                    //INICIA HILO
                    t2.start();
                    t2.join();

                    Thread.sleep(3500);
                    Prueba = estabilizacion.Resultado;
                } else {
                    Prueba = true;
                }
                // panel.getProgressBar().setVisible(false);
            }
            // }//end of while reiniciar Proceso
            // Prueba = (estabilizacion.Resultado == null) ? estabilizacion.Resultado = true: estabilizacion.Resultado;

            System.out.println("valor de resultado :" + Prueba);
            if (Prueba) {
                WorkerCruceroRalenti workerCruceroRalenti = new WorkerCruceroRalenti(panel, banco, idUsuario, idHojaPrueba, idPrueba, placas, tempAmbiente, humedadAmbiente);
                workerCruceroRalenti.execute();
                // panel.getPanelMensaje().setText("SELECCIONE DISPOSITIVO PARA MEDICION DE RPM");

                //15 Sept 2011 cambio para posibilitar cambiar las revoluciones y mejora de la cancelacion de la prueba;
                //ademas la simulacion de las revoluciones no se oculta sino que se pide contraseña
                //de usuario administrador
                //Se registra aqui un WorkerCruceroRalenti la idea es que
                //este worker muestre un dialogo para la selección del dispositivo de medicion
                //de revoluciones y temperatura e inicie una callable apropiadamente        
                panel.getButtonRpm().addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                });
                panel.getButtonRpm().doClick();

                //HABILITAR LOS BOTONES PARA LA PRUEBA
//        for(JButton b: panel.getBotones()){
//          if(!b.getActionCommand().equals("Simulacion"))
//           b.setVisible(true);
//        }
            } else {
                //  banco.getPuertoSerial().close();
                System.out.println("prueba no permitida debido a que no se pudo estabilizar las medidas, calibre los equipos o revise la variable Estabilizacion");
                //JOptionPane.showMessageDialog(null, "No se pudo realizar estabilizacion de las medidas \n como se indica en  la norma 4983:2012 numeral 5.2.3.3.1,\n por favor realice calibracion de los equipos o contacte a soporte tecnico.", "ERROR DE ESTABILIZACION, PRUEBA BLOQUEADA", JOptionPane.WARNING_MESSAGE);
                BloqueoEquipoUtilitario.bloquearEquipo(
                    "prueba no permitida debido a que no se pudo estabilizar las medidas, calibre los equipos o revise la variable Estabilizacion"
                );
                Prueba = true;
                System.out.println("valor de prueba2: " + Prueba);
                panel.cerrar();
                banco.getPuertoSerial().close();
                throw new CancellationException("Prueba Cancelada, no se ha podido estabilizar las medidas, por favor realice calibracion del equipo o consulte con soporte tecnico");

            }
            //panel.getButtonSimulacion().addActionListener(new ListenerInicioSimulacion(banco,panel,idPrueba,idHojaPrueba,idUsuario));
            //panel.getButtonKit().addActionListener(new ListenerKitGasolinaInicio(panel,banco,idUsuario,idHojaPrueba,idPrueba));
            //panel.getButtonBanco().addActionListener(new ListenerBanco(banco, panel, idPrueba, idHojaPrueba, idUsuario));
        }//end try cancelacion
        catch (InterruptedException ie) {

            //String showInputDialog = JOptionPane.showInputDialog("Comentario de Cancelacion");
            // registrarCancelacion(showInputDialog);
            throw new CancellationException("Prueba Cancelada");
        } catch (Exception exc) {//si ocurre algun error inesperado entonces cerrar el puerto serial del banco.
            if (banco != null) {
                banco.getPuertoSerial().close();
            }
            exc.printStackTrace(System.err);
            throw new ExecutionException(exc);
        }

        return null;
    }//end of method run

//    private void dormir(int tiempo) {
//        try {
//            Thread.sleep(tiempo);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(CallableInicioGases.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        /*rpmFiltrada = filtroRPM(revolucionesSimuladas);*/
//        /*rpmFiltrada = filtroRPM(revolucionesSimuladas);*/
//    }
    public boolean buscarBanco() {//TODO probar en otro computador

        banco = new BancoSensors();
        HashSet<CommPortIdentifier> puertosSeriales = PortSerialUtil.getAvailableSerialPorts();
        if (puertosSeriales.isEmpty()) {
            System.out.println("No hay puertos Seriales en el banco");
            return false;
        }//end if
        Iterator<CommPortIdentifier> itr = puertosSeriales.iterator();

        while (itr.hasNext()) {
            try {
                CommPortIdentifier portIdentifier = itr.next();
                CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                banco.setPuertoSerial(serialPort);
                System.out.println("Buscando banco en:" + portIdentifier.getName());
                String serial = banco.numeroSerial();
                System.out.println(serial);
                if (serial != null) {
                    System.out.println("Banco encontrado en" + portIdentifier.getName() + "Serial" + serial);
                    System.out.println("Flujos de Entrada/Salida del banco configurados");
                    return true;
                } else {
                    System.out.println("Banco no encontrado en " + portIdentifier.getName());
                    commPort.close();
                }
            } //end of while
            catch (PortInUseException ex) {

            } catch (IOException ioexc) {
                Logger.getLogger(this.getClass()).error(ioexc);
                ioexc.printStackTrace();
            } catch (UnsupportedCommOperationException ucoe) {
                Logger.getLogger(this.getClass()).error(ucoe);
                ucoe.printStackTrace();
            }
        }//end of while
        return false;//retorna falso si sale por aca
    }//end of method run

//simulacion de una funcion creciente amortiguada
    /**
     * Metodo para mostrar un dialogo con los parametros necesarios
     *
     */
    public void mostrarDialogoRPM() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(panel));
        d.getContentPane().add(dialogRPMTEmp);
        d.setSize(500, 300);
        d.setResizable(false);
        d.setModal(true);
        d.setLocationRelativeTo(panel);
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setVisible(true);
    }//end of method mostrarDialogoRPM

    public static double mean(double[] p) {
        double sum = 0;  // sum of all the elements
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }//end method mean

    /**
     * Metodo que se conecta con el puerto serial apropiado seleccionado del
     * dialogo.... configura los flujos de entrada salida e inicia el productor
     * y el consumidor
     *
     */
    /**
     * Metodo que se conecta con el puerto serial apropiado seleccionado del
     * dialogo....configura los flujos de entrada salida e inicia el productor y
     * el consumidor
     *
     * @return
     */
    public long getIdHojaPrueba() {
        return idHojaPrueba;
    }

    public void setIdHojaPrueba(long idHojaPrueba) {
        this.idHojaPrueba = idHojaPrueba;
    }

    public long getIdPrueba() {
        return idPrueba;
    }

    public void setIdPrueba(long idPrueba) {
        this.idPrueba = idPrueba;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Registra las medidas los defectos y evalua la prueba
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    class ManejadorFinalizar implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //cerrarPuertosHilos();
        }//end of actionPerformed

    }//end of ManejadorCancelar
}//end of class CallableInicioGases

