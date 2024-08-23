/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
import com.soltelec.modulopuc.utilidades.Mensajes;
import org.soltelec.pruebasgases.ListenerCancelacionGases;
import gnu.io.SerialPort;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.*;
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
import org.soltelec.horiba.BancoHoriba;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.pruebasgases.CondicionesAnormalesException;
import org.soltelec.pruebasgases.JDialogPruebaGasolina;
import org.soltelec.pruebasgases.ListenerCancelacionPorRpm;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.pruebasgases.WorkerCruceroRalenti;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.BancoCapelec;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroPCSensors;
import termohigrometro.TermoHigrometroArtisan;

/**
 * Clase que invoca a CallableInicioMotos en general inicia la prueba de gases
 * para motocicletas
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class WorkerInicioMotos extends SwingWorker<Void, Void> {

    private final long idUsuario, idPrueba, idHojaPrueba;
    private final PanelPruebaGases panel;
    private String placas;
    private PanelPruebaGases panelCancelacion;
    JDialogMotosGases frmC;
    private TermoHigrometroArtisan termoHigrometroArtisan;
    private double tempAmbiente;
    private double humedadAmbiente;

    /**
     *
     * @param idUsuario
     * @param idPrueba
     * @param idHojaPrueba
     * @param panel
     */
    public WorkerInicioMotos(long idUsuario, long idPrueba, long idHojaPrueba, PanelPruebaGases panel, String placas, TermoHigrometroArtisan termoHigrometroArtisan) {
        this.idUsuario = idUsuario;
        this.idPrueba = idPrueba;
        this.idHojaPrueba = idHojaPrueba;
        this.panel = panel;
        this.panel.setIdUsuario(idUsuario);
        this.panel.setPlacas(placas);
        this.panel.setIdPrueba(idPrueba);
        this.placas = placas;
        this.termoHigrometroArtisan = termoHigrometroArtisan;
    }

    @Override
    protected Void doInBackground() throws Exception {
        //Verificacion de humedad y temperatura visible;
        //Cargar por archivo de propiedades la marca del banco
        System.out.println("Estoy en WorkerInicioMotos");
        String strTermohigrometro = null;
        try {
            strTermohigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
            if (strTermohigrometro == null) {
                throw new NullPointerException();
            }
            System.out.println("cargo Propiedad " + strTermohigrometro);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Verifique el puerto del termohigrometro y cambielo en el achivo propiedades", "error", JOptionPane.ERROR_MESSAGE);
        }
        panel.getProgressBar().setVisible(false);

        if (strTermohigrometro != null && strTermohigrometro.equalsIgnoreCase("Habilitado")) {
            System.out.println("bloque de habilitacionTermo");
            Mensajes.messageWarningTime(">>> VALIDANDO TERMOHIGROMETRO <<<", 3);

            try {
                EquipoController Eq = new EquipoController();
                Eq.ActFecha(idPrueba);
                System.out.println("--------------------------------Fecha Actualizada----------------------------------");
            } catch (Exception e) {
                e.printStackTrace();
            }

            MedicionTermoHigrometro medicion = null;
            String strMarcaTermohigrometro = UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");
            tempAmbiente = 0.0;
            humedadAmbiente = 0.0;
            System.out.println("marca Termohigrometro " + strMarcaTermohigrometro);
            if (strMarcaTermohigrometro.equalsIgnoreCase("Artisan")) {
                Integer i = 0;
                System.out.println("bloque Artisan ");
                termoHigrometroArtisan = new TermoHigrometroArtisan(i);
                humedadAmbiente = Double.parseDouble(termoHigrometroArtisan.obtenerHumedad());
                System.out.println("valoresHumedad " + humedadAmbiente);
                tempAmbiente = Double.parseDouble(termoHigrometroArtisan.obtenerTemperatura());
                System.out.println("valoresTemp " + tempAmbiente);
                //registrar temperatura  

                //fin registrar temperatura
                if (humedadAmbiente == 0 && tempAmbiente == 0) {
                    JOptionPane.showMessageDialog(null, "Disculpe, no DETECTO el Termohigrometro, por lo tanto esta prueba no se REALIZARA ..! ");
                    panel.cerrar();
                    return null;
                }
            } else {
                System.out.println("bloque pcsensor ");
                try {
                    TermoHigrometro termoHigrometro = new TermoHigrometroPCSensors();
                    medicion = termoHigrometro.obtenerValores();
                    System.out.println("obtengoMedicionObjTermo ");
                } catch (Throwable th) {
                    JOptionPane.showMessageDialog(null, "No se pueden tomar mediciones de humedad y termohigrometro");
                    Logger.getRootLogger().error("Error con el termohigrometro", th);
                    panel.cerrar();
                    return null;
                }
                tempAmbiente = medicion.getValorTemperatura();//no se puede saber si demora un poco la cosa
                System.out.println("valoresTemp " + tempAmbiente);
                humedadAmbiente = medicion.getValorHumedad();
                System.out.println("valoresHumedad " + humedadAmbiente);

            }
            // if (tempAmbiente >= BancoGasolina.LIM_TEMP_AMB_MIN && tempAmbiente <= BancoGasolina.LIM_TEMP_AMB_MAX && humedadAmbiente >= BancoGasolina.LIM_HUMEDAD_MIN && humedadAmbiente <= BancoGasolina.LIM_HUMEDAD_MAX) {
            //JOptionPane.showMessageDialog(null,"VALORES VALIDOS\n"+"Temperatura: "+tempAmbiente+" Humedad: "+humedadAmbiente);
            if (tempAmbiente >= 5 && tempAmbiente <= 55 && humedadAmbiente <= 90 && humedadAmbiente >= 30) {
                Mensajes.messageDoneTime("VALORES VALIDOS\n" + "Temperatura: " + tempAmbiente + " Humedad: " + humedadAmbiente, 3);
                BancoGasolina banco = null;
                String marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
                if (marcaBanco == null || marcaBanco.equals("Sensors")) {
                    banco = new BancoSensors();
                } else if (marcaBanco.equals("Capelec")) {
                    banco = new BancoCapelec();
                }
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
                //no soporte para comunicacion a 19200 del banco capelec
                SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                banco.setPuertoSerial(puerto);
                ExecutorService exec = Executors.newSingleThreadExecutor();
                try {
                    Future<Void> futureInicio = exec.submit(new CallableInicioMotos(banco, panel, idHojaPrueba, idPrueba, idUsuario, placas, tempAmbiente, humedadAmbiente));
                    panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureInicio, idPrueba, banco, null, panel, idUsuario));
                    panel.getBtnWorkerCicloMotos().addActionListener(new ListenerCancelacionPorRpm(futureInicio, idPrueba, banco, null, panel, idUsuario, tempAmbiente, humedadAmbiente));


                    Void get = futureInicio.get();
                } catch (CancellationException ce) {
                    //JOptionPane.showMessageDialog(panel,"inicio de la prueba de motos interrumpido");
                    System.out.println("hubo un cancellation exception");
                    if (panel.getFuncion().getText().equalsIgnoreCase("abortado")) {
                        this.cancelacion("Aborto");
                    }
                    ce.printStackTrace(System.err);
                } catch (ExecutionException exc) {
                    System.out.println("hubo un execution exception");
                    if (panel.getFuncion().getText().equalsIgnoreCase("abortado")) {
                        this.cancelacion("Aborto");
                    }
                }
                //fin de verificacin de temperatura y de humedad
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                StringBuilder sb = new StringBuilder(" Disculpe, no Poseo las  CONDICIONES de Humedad o Temperatura Ambiental apropiadas segun numeral 4.1.1");
                sb.append("\n").append("Temperatura: ").append(df.format(tempAmbiente));
                sb.append("\n").append("Humedad Relativa: ").append(df.format(humedadAmbiente));
                JOptionPane.showMessageDialog(null, sb.toString());
                panel.cerrar();
                return null;
            }
            //fin de termohigrometro habilitado 
        } else {//si no se tiene el termohigrometro habilitado
            try {
                BancoGasolina banco = null;
                String marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
                if (marcaBanco == null || marcaBanco.equals("Sensors")) {
                    banco = new BancoSensors();
                    System.out.println("la Marca del ES Sensor");
                } else if (marcaBanco.equals("Capelec")) {
                    banco = new BancoCapelec();
                    System.out.println("la Marca del ES CAPELEC");
                } else if (marcaBanco.equals("Horiba")) {
                    banco = new BancoHoriba();
                }
                String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
                //no soporte para comunicacion a 19200 del banco capelec
                SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                banco.setPuertoSerial(puerto);
                System.out.println("nombre del puerto ".concat(nombrePuerto));
                ExecutorService exec = Executors.newSingleThreadExecutor();
                Future<Void> futureInicio = exec.submit(new CallableInicioMotos(banco, panel, idHojaPrueba, idPrueba, idUsuario, placas, tempAmbiente, humedadAmbiente));
                panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(futureInicio, idPrueba, banco, null, panel, idUsuario));
                Void get = futureInicio.get();
                System.out.println("esta en WORKERINICIO de motos");
            } catch (CancellationException ce) {
                System.out.println("Cambio de revoluciones o prueba cancelada");
                ce.printStackTrace(System.err);
                // banco.getPuertoSerial().close();
            } catch (ExecutionException exc) {

            } catch (Exception exc) {
                JOptionPane.showMessageDialog(null, "Exception !!!" + exc.getMessage());
                Logger.getRootLogger().error("Error durante el inicio de la prueba de gases para motocicletas", exc);
            }
        }// fin del  condicional de vno aplicar el termohigrometro  
        System.out.println("salgo workeriniCIO de motos");
        return null;
    }//fin del metodo

    private void cancelacion(String causa) {
        panel.cerrar();
    }

}
