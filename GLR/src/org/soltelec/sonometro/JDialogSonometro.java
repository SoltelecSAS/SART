/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import java.awt.Frame;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
//import org.soltelec.pruebasgases.HiloPruebaSonometro;
//import org.soltelec.pruebasgases.PanelPruebaGases;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogSonometro extends JDialog {

    private PanelPruebaSonometro panelPruebaGases;
    private String nombregasolina;   
    public static String serialEquipo="";

    public JDialogSonometro(Frame owner, boolean modal) {
        super(owner, modal);
        panelPruebaGases = new PanelPruebaSonometro();
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }

    public JDialogSonometro(Frame owner, boolean modal, int idPruebas, int idUsuario, int idHojaPruebas) {
        this(owner, modal);
        iniciarHilo(idPruebas, idUsuario, idHojaPruebas);
    }

    public JDialogSonometro(Frame owner, boolean modal, int idPruebas, int idUsuario, int idHojaPruebas, String nombregasolina) {
        this(owner, modal);
        this.nombregasolina = nombregasolina;
        iniciarHilo(idPruebas, idUsuario, idHojaPruebas);
    }

    public PanelPruebaSonometro getPanelPruebaGases() {
        return panelPruebaGases;
    }

    public static void main(String args[]) {
        JFrame app = new JFrame();
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        PanelPruebaSonometro panel = new PanelPruebaSonometro();
        app.add(panel);
        HiloPruebaSonometro hilo = new HiloPruebaSonometro(panel);
        Thread t = new Thread(hilo);
        t.setName("hilo sonometro principal");
        app.setVisible(true);
        t.start();
    }

    private void iniciarHilo(int idPrueba, int idUsuario, int idHojaPrueba) {
        try {
            WorkerSonometroMotor workerSonometroMotor = new WorkerSonometroMotor(panelPruebaGases, idUsuario, idHojaPrueba, idPrueba,nombregasolina);
            workerSonometroMotor.execute();
           
        } catch (Exception exc) {
            Logger.getRootLogger().error("Error durante la prueba de sonometro", exc);
            JOptionPane.showMessageDialog(null, "Error durante la prueba de sonometro");
        }
       // this.setVisible(true);
    }//end of method iniciarHilo
}
