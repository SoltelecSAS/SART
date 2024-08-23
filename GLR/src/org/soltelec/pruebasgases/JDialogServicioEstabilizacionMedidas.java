/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import java.awt.Frame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.EstabilizacionMedidas;
import org.soltelec.procesosbanco.WorkerCalentamiento;

/**
 *
 * @author SOLTELEC
 */
public class JDialogServicioEstabilizacionMedidas extends JDialog {

    /**
     * @param args the command line arguments
     */
    private PanelPruebaGases panelPruebaGases;
    private BancoGasolina banco;
    private WorkerCalentamiento workerCalentamiento;

    public JDialogServicioEstabilizacionMedidas(Frame owner, String title, boolean modal, BancoGasolina banco) {
        super(owner, title, modal);
        this.panelPruebaGases = new PanelPruebaGases();
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setTitle("SART 1.7.3 PRUEBA DE GASES PARA LIVIANOS");
        iniciarHilo(1, 1, 1, "1");
        panelPruebaGases.cerrar();
        this.dispose();
        return;
    }

    private void iniciarHilo(long idPrueba, long idUsuario, long idHojaPrueba, String placas) {
        System.out.println("voy a entrar a hilo de mestabilizacion");
        EstabilizacionMedidas estabilizacion = new EstabilizacionMedidas(this.panelPruebaGases, this.banco, 1);
        //CREO HILO
        Thread t2 = new Thread(estabilizacion);
        //INICIA HILO
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(JDialogServicioEstabilizacionMedidas.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Thread.sleep(3500);
        } catch (InterruptedException ex) {
            Logger.getLogger(JDialogServicioEstabilizacionMedidas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PanelPruebaGases getPanelPruebaGases() {
        return panelPruebaGases;
    }

    public static void main(String[] args) {

    }

}
