/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.soltelec.models.controllers.Conexion;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.util.UtilFugas;
import org.soltelec.util.Mensajes;
import termohigrometro.TermoHigrometroArtisan;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogPruebaGasolina extends JDialog {

    private final PanelPruebaGases panelPruebaGases;
    private TermoHigrometroArtisan termoHigrometroArtisan;
    private static EntityManager em;

    public JDialogPruebaGasolina(Frame owner, boolean modal) {
        super(owner, modal);
        panelPruebaGases = new PanelPruebaGases();
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }

    public JDialogPruebaGasolina(Frame owner, boolean modal, long idPrueba, long idUsuario, long idHojaPrueba, JDialog frm_Placas, String placas, TermoHigrometroArtisan termoHigrometroArtisan) throws SQLException, ClassNotFoundException, IOException {
        super(owner, modal);
        panelPruebaGases = new PanelPruebaGases();
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setTitle("SART 1.7.3 PRUEBA DE GASES PARA LIVIANOS");
        if (idPrueba != 0 && idUsuario != 0) {
            iniciarHilo(idPrueba, idUsuario, idHojaPrueba, placas);
            panelPruebaGases.cerrar();
            this.dispose();
            frm_Placas.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            return;
        }
    }
    /*Añadido 31 ago*/
     public JDialogPruebaGasolina(Frame owner, boolean modal, int a) throws SQLException, ClassNotFoundException, IOException {
        super(owner, modal);
        panelPruebaGases = new PanelPruebaGases();
        this.getContentPane().add(panelPruebaGases);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setTitle("SART 1.7.3 PRUEBA DE GASES PARA LIVIANOS");
        iniciarHilo(1, 1, 1, "1");
        panelPruebaGases.cerrar();
        this.dispose();
        return;
        
    }

    public PanelPruebaGases getPanelPruebaGases() {
        return panelPruebaGases;
    }

    private void iniciarHilo(long idPrueba, long idUsuario, long idHojaPrueba, String placas) {
        WorkerInicioPruebaGases workerInicioPruebaGases = new WorkerInicioPruebaGases((int) idUsuario, idHojaPrueba, idPrueba, panelPruebaGases, placas,  termoHigrometroArtisan,em);
        workerInicioPruebaGases.execute();
    }

    private boolean verificarFugasCalibraciones(String numeroSerial) throws SQLException, ClassNotFoundException, IOException {
        RegVefCalibraciones r = new RegVefCalibraciones();
        boolean flag = true;  
        
        try{
            Connection cn = r.obtenerConexion();
            System.out.println("voy a consultar fugas ");
            boolean necesitaFugas = r.necesitaFugas(numeroSerial, cn);
            System.out.println("voy a consultar calibracion ");
            boolean necesitaCalibracion = r.necesitaCalibracionDosPuntos(numeroSerial, cn);

            System.out.println("resultado de fugas " + necesitaFugas + " resultado de calibracion " + necesitaCalibracion);
            if (necesitaFugas || necesitaCalibracion) {
                System.out.println("Buscando Cerrar panel");
                flag = false;
            } else {
                cn.close();
                flag =  true;
            }
        }catch(SQLException | ClassNotFoundException ex ){
            //mostrar mensaje al usuario acerca del error
           
            Mensajes.mostrarExcepcion(ex);
        }catch(Exception ex){
             JOptionPane.showMessageDialog(null, "no se encontro el equipo con serial" + numeroSerial +""
                                                + "o el equipo no tiene calibraciones", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return flag;
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

}
