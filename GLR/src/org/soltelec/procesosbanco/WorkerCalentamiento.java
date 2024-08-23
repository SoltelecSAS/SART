/*
 * 
 * 
 */
package org.soltelec.procesosbanco;

import java.awt.FlowLayout;
import org.soltelec.util.EstadoMaquina;
import javax.swing.SwingWorker;
import java.lang.Integer;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.BancoCapelec;

/**
 * Hilo que monitorea el proceso de calentamiento del banco
 *
 * @author Usuario
 */
public class WorkerCalentamiento extends SwingWorker<Integer, Integer> {

    private JLabelPersonalizada label;
    private JProgressBar progressBar;
    private EstadoMaquina estado;
    private BancoGasolina banco;
    private boolean finalizado = false;
    private int indice;
    JPanel PanelBotones = null;
    private final PanelPruebaGases panel;
    private PanelPruebaGases panelPruebaGases;

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }

    public WorkerCalentamiento(BancoGasolina banco, JProgressBar progressBar, JLabelPersonalizada unalabel) {
        this.progressBar = progressBar;
        this.banco = banco;
        this.label = unalabel;
        this.panel = new PanelPruebaGases();
    }

    @Override
    protected Integer doInBackground() throws Exception {
        progressBar.setValue(0);
        int contadorInicio, progreso = 0, contador;
        estado = banco.statusMaquina();//esta parte es distinta
        contadorInicio = estado.getContadorCalentamiento();//esta parte tambien es distinta en los dos bancos
        //progressBar.setMaximum(contadorInicio);//el maximo es el contador //EDT
        setProgress(0);
        label.setText("Banco en calentamiento");
        //mientras el estado sea en calentamiento
        while (estado.isCalentamiento()) {

            contador = estado.getContadorCalentamiento();
            label.setText("Banco en calentamiento:" + contador+"s");
            if (banco instanceof BancoSensors) {
                progreso = (100 - (100 * contador) / contadorInicio);
                System.out.println("escribiendo progrreso :" + progreso);
            } else if (banco instanceof BancoCapelec) {
                progreso = contador;
            }
            setProgress(progreso);
            publish(progreso);
            estado = banco.statusMaquina();
            Thread.sleep(200);
        }
        
        //<editor-fold defaultstate="collapsed" desc="ZERO">
                label.setText("Por Favor Espere. \\n Haciendo Cero en el Banco ...!");
                System.out.println("Initial ZERO by powerJava"+ System.currentTimeMillis());
                PanelCero panelCero = new PanelCero();
                panel.getPanelMensaje().setText("Por Favor Espere. \n Haciendo Cero en el Banco ...!");
                panelCero.setBanco(banco);
                panelCero.setBarraTiempo(this.panel.getProgressBar());
                panelCero.setLabelMensaje(this.panel.getPanelMensaje().getCronometro());

                Thread t1 = new Thread(panelCero);
                t1.start();
                try {
                    t1.join();
                    //Realizar calibracion de cero//sincronizar con hilo principal
                } catch (InterruptedException ex) {
                    System.out.println("Error haciendo join con hilo de cero");
                }
                System.out.println("End ZERO by powerJava: " + System.currentTimeMillis());
                //</editor-fold>
        
        String Estabilizacion = UtilPropiedades.cargarPropiedad("Estabilizacion", "propiedades.properties"); //LEE EL VALOR DE LA VARIABLE TemporizadorOpacimetro
        Estabilizacion = (Estabilizacion == null) ? "false" : Estabilizacion;
        System.out.println((Estabilizacion == "" || Estabilizacion.equalsIgnoreCase("false")) ? "variable estabilizacion configurada en false" : "variable estabilizacion configurada en true");
        if (Estabilizacion.equalsIgnoreCase("true")) {
            label.setText("Banco en Estabilizacion:");
            Thread.sleep(600);
            System.out.println("voy a entrar a hilo de mestabilizacion");
            EstabilizacionMedidasServicio estabilizacion = new EstabilizacionMedidasServicio(this.label,this.progressBar, this.banco, 1, contadorInicio);
            //CREO HILO
            Thread t2 = new Thread(estabilizacion);
            //INICIA HILO
            t2.start();
            t2.join();

            Thread.sleep(3500);
        }

        publish(progressBar.getMaximum());
        System.out.println("Proceso de calentamiento terminado");
        this.label.setText("Calentamiento Finalizado");
        this.setFinalizado(true);
        //
        return 0;
    }//end of method

    @Override
    public void done() {
        progressBar.setValue(0);;
    }

    protected void publish(Integer numero) {
        progressBar.setValue(numero);
    }
}