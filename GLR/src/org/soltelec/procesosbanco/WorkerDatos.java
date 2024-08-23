/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.soltelec.util.MedicionGases;
import java.util.Random;
import org.soltelec.models.controllers.EquipoController;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import org.soltelec.models.entities.Equipo;
import org.soltelec.pruebasgases.JDialogPruebaGasolina;
import org.soltelec.pruebasgases.JDialogServicioEstabilizacionMedidas;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.capelec.BancoCapelec;

/**
 *
 * @author Usuario
 */
public class WorkerDatos extends SwingWorker<Integer, MedicionGases> {

    public volatile boolean interrumpido, terminado;
    private boolean primeraEjecucion = true;
    private PanelMedidasSteel panelMedidas;//para actualizar la interfaz cada cierto tiempo
    JPanel panelBotones;
    private Random random = new Random();
    private BancoGasolina banco;//hay que ponerselo al instanciarlo
    private WorkerCalentamiento workerCalentamiento;
    private String serial, PEF;

    public WorkerDatos(PanelMedidasSteel panelMedidas, JPanel PanelBotones) {
        this.panelMedidas = panelMedidas;
        this.panelBotones = PanelBotones;
        serial = "";
        PEF = "";
    }

    //Metodo que realiza la tarea principal
    @Override
    protected Integer doInBackground() throws Exception {
        boolean primeraVez = true;
        MedicionGases medicion = new MedicionGases();
        while (!isTerminado()) {
            if (!isInterrumpido()) {
                try {
//                medicionHC = String.valueOf(random.nextInt(15));
//                medicionCO2 = String.valueOf(random.nextInt(10));
//                medicionO2 = String.valueOf(random.nextInt(12));
//                medicionCO = String.valueOf(random.nextInt(10));
//                medicionTAceite = String.valueOf(random.nextInt(50));
//                medicionRPM = String.valueOf(random.nextInt(2000));
//                medicionTAmbiente = String.valueOf(random.nextInt(40));
//                medicionRH = String.valueOf(random.nextInt(50));
                    try {
                        //System.out.println("Proceso interrumpido");
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WorkerDatos.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    EquipoController controller = new EquipoController();
                    if (banco != null) {
                        medicion = banco.obtenerDatos();
                        if (primeraEjecucion) {
                            serial = banco.numeroSerial();
                            PEF = banco.obtenerPEF();
                            Equipo serialresolucion = controller.findEquipoBySerialResolucion(serial);
                            panelMedidas.getLabelTitulo().setText("Serial : " + serialresolucion.getSerialresolucion() + " PEF: 0," + PEF);
                            
                            primeraEjecucion = false;
                        }
                        //medicion = banco.obtenerDatos();
                        //publish(medicion);
                        if (!medicion.isCalentamiento()) {
                            panelMedidas.getLedCalentamiento().setLedBlinking(false);
                            publish(medicion);
                            for (Component a : panelBotones.getComponents()) {
                                a.setEnabled(true);
                            }
                            System.out.println("Componentes desbloqueados ");
                            System.out.println("esta en calentamiento = false");
                        } else {
                            for (Component a : panelBotones.getComponents()) {
                                a.setEnabled(false);
                            }
                            System.out.println("Componentes bloqueados durante el calentamiento");
                            System.out.println("esta en calentamiento = true");
                            // panelBotones.setEnabled(false);
                            panelMedidas.getLedCalentamiento().setLedBlinking(true);
                            panelBotones.setEnabled(false);

                            workerCalentamiento = new WorkerCalentamiento(this.banco, panelMedidas.getBarraCalentamiento(), panelMedidas.getLabelMensaje());
//                    Thread t = new Thread(workerCalentamiento);
//                    t.start();
//                    //Uniendo dos hilos este  se bloquea hasta que el otro termine
//                    t.join();
                            workerCalentamiento.execute();

                            workerCalentamiento.addPropertyChangeListener(new PropertyChangeListener() {

                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {

                                    if (evt.getPropertyName().equals("progress")) {
                                        panelMedidas.getBarraCalentamiento().setValue((Integer) evt.getNewValue());
                                    }

                                }
                            });
                            try {
                                workerCalentamiento.get();
                            } catch (ExecutionException ex) {
                                Logger.getLogger(WorkerDatos.class.getName()).log(Level.SEVERE, null, ex);
                            }
//                    while(! workerCalentamiento.isFinalizado()){
//                        
//                    }

//                    if(primeraVez){
//                    workerCalentamiento = new WorkerCalentamiento(banco,panelMedidas.getBarraCalentamiento(),panelMedidas.getLabelMensaje());
//                    //workerCalentamiento.addPropertyChangeListener(progreso);
//                    workerCalentamiento.execute();
//                    primeraVez = false;
//                    } else if(workerCalentamiento.isFinalizado()){//este es pra ver si ese hilo ya termino
//                        panelMedidas.getLabelMensaje().setText("Calentamiento");
//                        workerCalentamiento = new WorkerCalentamiento(banco,panelMedidas.getBarraCalentamiento(),panelMedidas.getLabelMensaje());
//                            //workerCalentamiento.addPropertyChangeListener(progreso);
//                        workerCalentamiento.execute();
//                    }
                            panelBotones.setEnabled(true);
                        }//end else calentamiento

                    }

                    Thread.sleep(120);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WorkerDatos.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    //System.out.println("Proceso interrumpido");
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(WorkerDatos.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return 0;
    }//end of method doInBackGround

    protected void publish(MedicionGases valores) {
        if (primeraEjecucion) {
            this.panelMedidas.getLabelTitulo().setText("BANCOS PEF: " + PEF + "\n SERIAL: " + serial);
            primeraEjecucion = false;
        }
        this.panelMedidas.getDisplayHC().setLcdValue(valores.getValorHC());
        if (banco instanceof BancoCapelec) {
            this.panelMedidas.getDisplayCO2().setLcdValue((valores.getValorCO2() * 0.1) / 10);
        } else {
            this.panelMedidas.getDisplayCO2().setLcdValue(valores.getValorCO2() * 0.1);
        }
        this.panelMedidas.getDisplayO2().setLcdValue(valores.getValorO2() * 0.01);
        this.panelMedidas.getDisplayCO().setLcdValue(valores.getValorCO() * 0.01);
        this.panelMedidas.getDisplayTAceite().setLcdValue(valores.getValorTAceite());
        this.panelMedidas.getDisplayRPM().setLcdValue(valores.getValorRPM());
        this.panelMedidas.getDisplayPresion().setLcdValue(valores.getValorPres());
        this.panelMedidas.getDisplayNox().setLcdValue(valores.getValorNox());
        //actualizar los flags
        this.panelMedidas.getLedBajoFlujo().setLedOn(valores.isBajoFlujo() || valores.isVacuumSwitch());
        this.panelMedidas.getLedCO2Range().setLedOn(valores.isCO2range());
        this.panelMedidas.getLedCORange().setLedOn(valores.isCOrange());
        this.panelMedidas.getLedCalentamiento().setLedOn(valores.isCalentamiento());
        this.panelMedidas.getLedCero().setLedOn(valores.isCero());
        this.panelMedidas.getLedCondensacion().setLedOn(valores.isCondensacion());
        this.panelMedidas.getLedHCRange().setLedOn(valores.isHCrange());
        this.panelMedidas.getLedInterna().setLedOn(valores.isInterna());
        this.panelMedidas.getLedNOxRange().setLedOn(valores.isNOxRange());
        this.panelMedidas.getLedO2Range().setLedOn(valores.isO2range());
        this.panelMedidas.getLedhiHC().setLedOn(valores.isHiHC());

        //salidas digitales
    }//end of publish method

    public void done() {
        System.out.println("El Hilo de datos es Terminado");
        setTerminado(true);
    }//end of method done

    public boolean isInterrumpido() {
        return interrumpido;
    }

    public void setInterrumpido(boolean interrumpido) {
        this.interrumpido = interrumpido;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public BancoGasolina getBanco() {
        return banco;
    }

    public void setBanco(BancoGasolina banco) {
        this.banco = banco;
    }

}//end of class WorkerDatos
