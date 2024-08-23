/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.Mensajes;
import org.soltelec.util.RegistrarMedidas;
import org.soltelec.util.UtilFugas;
import termohigrometro.TermoHigrometroArtisan;
 

/**
 * Clase para realizar la prueba de gases de una moto
 *
 * @author Gerencia Desarrollo Soluciones Tecnologicas
 */
public class JDialogMotosGases extends JDialog {

    private final PanelPruebaGases panelPruebaGases;

    private JDialog frm_Placas;
    private TermoHigrometroArtisan termoHigrometroArtisan;
    

    public JDialogMotosGases(Frame owner, boolean modal, int idPrueba, int idUsuario, int idHojaPruebas, JDialog frm_Placas, String placas, TermoHigrometroArtisan termoHigrometroArtisan) throws IOException {
        super(owner, modal);        
        panelPruebaGases = new PanelPruebaGases();
        panelPruebaGases.setDialogMotosGases(this);
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setTitle("SART 1.7.3 PRUEBA DE GASES PARA MOTO");       
        if (idPrueba != 0 && idUsuario != 0) {
            iniciarHilo(idPrueba, idUsuario, idHojaPruebas, placas);
            panelPruebaGases.cerrar();
            frm_Placas.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        this.termoHigrometroArtisan = termoHigrometroArtisan;
    }

    public JDialogMotosGases(Frame owner, boolean modal, int idPrueba, int idUsuario, int idHojaPruebas) throws IOException {
        super(owner, modal);
        panelPruebaGases = new PanelPruebaGases();
        panelPruebaGases.setDialogMotosGases(this);
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        //this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);       
        if (owner != null) {
            iniciarHilo(idPrueba, idUsuario, idHojaPruebas, "");
        }
    }

    public PanelPruebaGases getPanelPruebaGases() {
        return panelPruebaGases;
    }

    /**
     *
     * @return
     */
    public static boolean verificarFugasCalibraciones(String serialBanco) throws IOException 
    {
        System.out.println("------------------------------------------------");
        System.out.println("-----------------verificarFugasCalibraciones-----");
        System.out.println("------------------------------------------------");

        boolean estado = false;
        try {
            RegVefCalibraciones r = new RegVefCalibraciones();
            boolean necesitaFugas = r.necesitaFugas(serialBanco);
            boolean necesitaCalibracion = r.necesitaCalibracionDosPuntos(serialBanco);
            if (necesitaFugas) {
                estado = false;
            } else if(necesitaCalibracion){
                estado = false;
            }
            else {
                estado = true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            //mostrar mensaje al usuario acerca del error
            
            Mensajes.mostrarExcepcion(ex);
        }catch(Exception e){
           
           
        }
        return estado;
    }

    private void iniciarHilo(int idPrueba, int idUsuario, int idHojaPruebas, String placas) {
        try {
            System.out.println("Estoy en iniciarHilo");
            panelPruebaGases.getProgressBar().setVisible(false);
            WorkerInicioMotos wi = new WorkerInicioMotos(idUsuario, idPrueba, idHojaPruebas, panelPruebaGases, placas, termoHigrometroArtisan);
            wi.execute();
            System.out.println("Estoy en JDialogo motos gases de motos");
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, "Error iniciando la prueba de gases " + exc.getMessage());
            Logger.getRootLogger().error("Error iniciando la prueba de gases ", exc);
        }
    }

    private String obtenerSerialBanco() {
        String numeroSerial = null;
        try {
            numeroSerial = UtilFugas.obtenerSerialBanco();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "no se puede leer el numero serial del banco");
        }
        return numeroSerial;
    }

    /**
     * @param frm_Placas the frm_Placas to set
     */
    public void setFrm_Placas(JDialog frm_Placas) {
        this.frm_Placas = frm_Placas;
    }

}
