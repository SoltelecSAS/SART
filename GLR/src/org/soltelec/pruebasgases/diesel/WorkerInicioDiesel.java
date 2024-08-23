/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.diesel;

import com.soltelec.opacimetro.brianbee.OpacimetroBrianBee;
import gnu.io.SerialPort;
import java.awt.HeadlessException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.procesosopacimetro.OpacimetroSensors;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.OpacimetroCapelec;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;

/**
 * Este es el punto de inicio de la prueba En esta clase se debe configurar que
 * opacimetro usar dependiendo si es el opacimetro sensors o el opacimetro
 * capelec o el opacimetro siemens
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class WorkerInicioDiesel extends SwingWorker<Void, Void> {

    private final PanelPruebaGases panel;
    private final long idHojaPrueba, idPrueba, idUsuario;
    double tempAmbiente;
    double humedadAmbiente;

    public WorkerInicioDiesel(PanelPruebaGases panel, long idHojaPrueba, long idPrueba, long idUsuario) {
        this.panel = panel;
        this.idHojaPrueba = idHojaPrueba;
        this.idPrueba = idPrueba;
        this.idUsuario = idUsuario;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            String strTermoHigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
            panel.getProgressBar().setVisible(false);
            if (strTermoHigrometro != null && strTermoHigrometro.equalsIgnoreCase("habilitado")) {
                Mensajes.messageWarningTime("3.1.1 VALIDANDO CONDICIONES AMBIENTALES   ", 4);
                String strMarcaTermohigrometro = UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");
                if (strMarcaTermohigrometro.equalsIgnoreCase("Artisan")) {
                    Integer i = 0;
                    TermoHigrometroArtisan termoHigrometroArtisan = new TermoHigrometroArtisan(i);
                    humedadAmbiente = Double.parseDouble(termoHigrometroArtisan.obtenerHumedad());
                    tempAmbiente = Double.parseDouble(termoHigrometroArtisan.obtenerTemperatura());
                    if (humedadAmbiente == 0 && tempAmbiente == 0) {
                        JOptionPane.showMessageDialog(null, "Disculpe, no DETECTO el Termohigrometro, por lo tanto esta prueba no se REALIZARA ..! ");
                        panel.cerrar();
                        return null;
                    }
                } else {
                    TermoHigrometro termoHigrometro = new TermoHigrometroPCSensors();
                    MedicionTermoHigrometro medicionTermo = termoHigrometro.obtenerValores();
                    tempAmbiente = medicionTermo.getValorTemperatura();
                    humedadAmbiente = medicionTermo.getValorHumedad();
                }
                if (tempAmbiente >= 5 && tempAmbiente <= 55 && humedadAmbiente <= 90 /*&& humedadAmbiente>=30*/) {
                    com.soltelec.modulopuc.utilidades.Mensajes.messageDoneTime("VALORES VALIDOS\n" + "Temperatura: " + tempAmbiente + " Humedad: " + humedadAmbiente, 3);
                    iniciarPruebaDiesel();
                } else {
                    DecimalFormat df = new DecimalFormat("#.0#");
                    JOptionPane.showMessageDialog(null, "Disculpe, no Poseo las  CONDICIONES de Humedad o Temperatura ambiental apropiadas segun numeral 4.1.1");
                    if (tempAmbiente < 5 || tempAmbiente > 55) {
                        JOptionPane.showMessageDialog(null, "TEMPERATURA INADECUADA: " + df.format(tempAmbiente) + "Debe estar entre 5 y 55 ");
                    }
                    if (humedadAmbiente > 90 || humedadAmbiente < 30) {
                        JOptionPane.showMessageDialog(null, "HUMEDAD RELATIVA INADECUADA: " + df.format(humedadAmbiente) + "Debe ser menor a 90");
                    }
                    panel.cerrar();
                    return null;
                }
            } else {
                iniciarPruebaDiesel();
            }
        } catch (IOException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Excepcion: " + e.getCause());
            Logger.getRootLogger().error(e);
        } catch (Error e) {
            Logger.getRootLogger().error("Error iniciando el termohigrometro", e);
            JOptionPane.showMessageDialog(null, "Error iniciando el termohigrometro" + e.getMessage());
            panel.cerrar();

        }

        try {
            EquipoController Eq = new EquipoController();
            Eq.ActFecha(idPrueba);
            System.out.println("--------------------------------Fecha Actualizada----------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param opacimetro
     * @param panel
     */
    private void liberarRecursos(Opacimetro opacimetro, PanelPruebaGases panel) {

        try {
            panel.cerrar();
            if (opacimetro != null && opacimetro.getPort() != null) {
                opacimetro.getPort().close();
            }
        } catch (Exception exc) {
        }
    }

    /**
     * Metodo que permite iniciar el hilo para la prueba de Diesel
     */
    private void iniciarPruebaDiesel() throws HeadlessException {
        System.out.println("---------------------------- ------");
        System.out.println("----   Iniciar Prueba Diesel ------");
        System.out.println("----------------------------- -----");
        Opacimetro opacimetro = null;
        try {
            opacimetro = crearInstanciaTiPoOpacimetro();
            CallableInicioDiesel callable = new CallableInicioDiesel(panel, opacimetro, idHojaPrueba, idPrueba, idUsuario, tempAmbiente, humedadAmbiente);
            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<Void> futurePruebaDiesel = exec.submit(callable);
            panel.getButtonFinalizar().addActionListener(new ListenerCancelacionDiesel(futurePruebaDiesel, idPrueba, opacimetro, null, panel, idUsuario));
            futurePruebaDiesel.get();
        } catch (CancellationException ce) {
            System.out.println("Prueba de Gases cancelada");
            Logger.getLogger("igrafica").info("Prueba de opacidad cancelada: " + idPrueba);
        } catch (ExecutionException exc) {
            JOptionPane.showMessageDialog(null, "Error durante la ejecucion de la prueba");
            System.out.println("Idealmente nunca deberia ejecutarse porque es un crash");
            Logger.getRootLogger().error("Error durante la prueba de opacidad:" + idPrueba, exc);
            liberarRecursos(opacimetro, panel);
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "Error durante la ejecucion de la prueba");
            System.out.println("Idealmente nunca deberia ejecutarse porque es un crash");
            Logger.getRootLogger().error("Error durante la prueba de opacidad:" + idPrueba, exc);
            liberarRecursos(opacimetro, panel);
        } finally {
            //liberarRecursos(opacimetro, panel);
        }
    }

    /**
     * Creando instancia para el tipo de opacimetro
     */
    private Opacimetro crearInstanciaTiPoOpacimetro() {
        System.out.println("---------------------------- ----------------------");
        System.out.println("-   Creando  instancia para el tipo de opacimetro -");
        System.out.println("----------------------------- ---------------------");
        Opacimetro opacimetro = null;
        try {
            String marcaOpacimetro = UtilPropiedades.cargarPropiedad("MarcaOpacimetro", "propiedades.properties");
            if (marcaOpacimetro == null || marcaOpacimetro.equals("Sensors")) {
                opacimetro = new OpacimetroSensors();
            } else if (marcaOpacimetro.equals("Capelec")) {
                opacimetro = new OpacimetroCapelec();
            } else if (marcaOpacimetro.equalsIgnoreCase("BrianBee")) {
                opacimetro = new OpacimetroBrianBee();
            }
            opacimetro.setPort(cargarPuertoTipoOpacimetro(marcaOpacimetro));
        } catch (IOException e) {
            System.out.println("Errror en el metodo :crearInstanciaTiPoOpacimetro()" + e.getMessage());
            System.out.println("Errror " + e.getLocalizedMessage());
            System.out.println("Errror " + e);
        }
        return opacimetro;
    }

    /**
     * Metodo que carga el puerto Serial teniendo encuenta la marca del
     * Opacimetro
     *
     * @param marcaOpacimetro
     * @return
     */
    private SerialPort cargarPuertoTipoOpacimetro(String marcaOpacimetro) {
        System.out.println("---------------------------- -----------------------------------------");
        System.out.println("-Cargando el puerto Serial teniendo encuenta la marca del Opacimetro -");
        System.out.println("----------------------------- ----------------------------------------");
        SerialPort puerto = null;
        try {
            //dependiendo de la marca del opacimetro se toma el puerto
            String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoOpacimetro", "propiedades.properties");
            if (marcaOpacimetro.equals("Capelec")) {
                puerto = PortSerialUtil.connect(nombrePuerto, 19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            } else {
                puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            }
        } catch (IOException e) {
            System.out.println("Errror en el metodo :cargarPuertoTipoOpacimetro()" + e.getMessage());
            System.out.println("Errror " + e.getLocalizedMessage());
            System.out.println("Errror " + e);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(WorkerInicioDiesel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return puerto;
    }

}//end of class
