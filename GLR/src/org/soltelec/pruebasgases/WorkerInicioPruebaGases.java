/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import gnu.io.SerialPort;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.soltelec.andros.BancoAndros;
import org.soltelec.horiba.BancoHoriba;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.BancoSensors;
import org.soltelec.util.Mensajes;
import org.soltelec.util.PortSerialUtil;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.BancoCapelec;
import termohigrometro.MedicionTermoHigrometro;
import termohigrometro.TermoHigrometro;
import termohigrometro.TermoHigrometroArtisan;
import termohigrometro.TermoHigrometroPCSensors;
import org.soltelec.models.entities.TipoAborto;

/**
 * Clase para iniciar la prueba de gases
 *
 * @author GerenciaDesarrollo
 */
public class WorkerInicioPruebaGases extends SwingWorker<Void, Void> {

    private final int idUsuario;
    private final long idHojaPrueba;
    private final long idPrueba;
    private final PanelPruebaGases panel;
    private String placas;
    private TermoHigrometroArtisan termoHigrometroArtisan;
    private double tempAmbiente;
    private double humedadAmbiente;
    private static EntityManager em;

    public WorkerInicioPruebaGases(int idUsuario, long idHojaPrueba, long idPrueba, PanelPruebaGases panel, String placas, TermoHigrometroArtisan termoHigrometroArtisan, EntityManager eManager) {
        this.idUsuario = idUsuario;
        this.idHojaPrueba = idHojaPrueba;
        this.idPrueba = idPrueba;
        this.panel = panel;
        this.placas = placas;
        em = eManager;
        this.termoHigrometroArtisan = termoHigrometroArtisan;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            panel.getProgressBar().setVisible(false);
            String termohigrometro = UtilPropiedades.cargarPropiedad("TermoHigrometro", "propiedades.properties");
            if (termohigrometro != null && termohigrometro.equalsIgnoreCase("Habilitado")) {
                Mensajes.messageWarningTime("VALIDANDO CONDICIONES AMBIENTALES   ", 5);

                String strMarcaTermohigrometro = utiltermohigrometro.UtilPropiedades.cargarPropiedad("MarcaTermoHigrometro", "propiedades.properties");
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
                    MedicionTermoHigrometro medicion = termoHigrometro.obtenerValores();
                    tempAmbiente = medicion.getValorTemperatura();//no se puede saber si demora un poco la cosa
                    humedadAmbiente = medicion.getValorHumedad();
                }

                try {
                    EquipoController Eq = new EquipoController();
                    Eq.ActFecha(idPrueba);
                    System.out.println("--------------------------------Fecha Actualizada----------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //  if( tempAmbiente >= BancoGasolina.LIM_TEMP_AMB_MIN && tempAmbiente <= BancoGasolina.LIM_TEMP_AMB_MAX && humedadAmbiente >= BancoGasolina.LIM_HUMEDAD_MIN && humedadAmbiente <= BancoGasolina.LIM_HUMEDAD_MAX) {
                if (tempAmbiente >= 5 && tempAmbiente <= 55 && humedadAmbiente <= 90 && humedadAmbiente >= 30) {
                    com.soltelec.modulopuc.utilidades.Mensajes.messageDoneTime("VALORES VALIDOS\n" + "Temperatura: " + tempAmbiente + " Humedad: " + humedadAmbiente, 3);
                } else {
                    DecimalFormat df = new DecimalFormat("#.##");
                    StringBuilder sb = new StringBuilder("Disculpe, no Poseo las  CONDICIONES de humedad o temperatura ambiental apropiadas segun numeral 4.1.1 ");
                    sb.append("\n").append("Temperatura: ").append(df.format(tempAmbiente));
                    sb.append("\n").append("Humedad Relativa: ").append(df.format(humedadAmbiente));
                    JOptionPane.showMessageDialog(null, sb.toString());
                    panel.cerrar();
                    return null;
                }
            }//fin de termohiggrometro habilitado                      
            BancoGasolina banco;
            String marcaBanco = UtilPropiedades.cargarPropiedad("MarcaBanco", "propiedades.properties");
            switch (marcaBanco) {
                case "Capelec":
                    banco = new BancoCapelec();
                    break;
                case "Andros":
                    banco = new BancoAndros();
                    break;
                case "Horiba":
                    banco = new BancoHoriba();
                    break;
                default:
                    banco = new BancoSensors();
                    break;
            }

            String nombrePuerto = UtilPropiedades.cargarPropiedad("PuertoBanco", "propiedades.properties");
            SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            banco.setPuertoSerial(puerto);

            CallableInicioGases hilo = new CallableInicioGases(panel, idPrueba, idUsuario, banco, idHojaPrueba, placas, tempAmbiente, humedadAmbiente);
            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<Void> submit = exec.submit(hilo);
            panel.getButtonFinalizar().addActionListener(new ListenerCancelacionGases(submit, idPrueba, banco, null, panel, idUsuario));
            Void get = submit.get();
        } catch (CancellationException ce) {
            System.out.println("Prueba de Gases cancelada");
            ce.printStackTrace(System.err);
        } catch (InterruptedException exc) {
            System.out.println("Tarea interrumpida");
            if (exc.getCause() instanceof InterruptedException) {
                System.out.println("InterruptedException");
            } else {
                JOptionPane.showMessageDialog(panel, "Se desconecto el KIT de RPM");
                exc.printStackTrace(System.err);
            }
        } catch (DilucionException dexc) {
            JOptionPane.showMessageDialog(panel, " Dilucion encontrada");

        } catch (Exception exc) {
            //JOptionPane.showMessageDialog(null,"Error durante la ejecucion de la prueba");
            Logger.getLogger(this.getClass()).error("Error durante la ejecucion de la prueba", exc);

            JOptionPane.showMessageDialog(panel, "Error durante la prueba\n" + exc.getCause());
            exc.printStackTrace(System.err);
            panel.cerrar();
        } catch (Error err) {
            JOptionPane.showMessageDialog(null, "No se puede iniciar el sensor de temperatura y humedad ambiental");
        }
        return null;
    }

    
    
    
}
