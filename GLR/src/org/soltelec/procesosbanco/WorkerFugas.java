/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.soltelec.models.controllers.CalibracionController;
import org.soltelec.models.controllers.EquipoController;
import org.soltelec.models.entities.Calibracion;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.Mensajes;

/**
 *
 * @author soltelec
 */
public class WorkerFugas extends SwingWorker<Void, Integer> {

    private WorkerDatos worker;
    private final JLabel labelMensaje;
    private final BancoGasolina banco;
    private final Timer timer;
    private int contadorTimer;
    private boolean aprobada;
    private String serialEquipo;
    private int idUsuario ;
    private final JDialog panelFugas;

    public WorkerFugas(JLabel labelMensaje, BancoGasolina banco, JDialog panelFugas,int idUsuario) {
        this.labelMensaje = labelMensaje;
        this.banco = banco;
        this.panelFugas = panelFugas;
        this.idUsuario= idUsuario;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTimer++;
            }
        });
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            if (worker != null) {
                worker.setInterrumpido(true);
            }
            serialEquipo = banco.numeroSerial();

            MedicionGases medicion;
            medicion = banco.obtenerDatos();
            while (medicion.isCalentamiento()){
              JOptionPane.showMessageDialog(null, "El Banco esta en calentamiento");
              Thread.sleep(500);
              medicion = banco.obtenerDatos();
            }
            System.out.print("Continuo con la prueba de Fugas");
            /*
            if(medicion.isCalentamiento()){
                System.out.print("Entre para que caliente");
                
                JOptionPane.showMessageDialog(null,"Banco en calentamiento");
                Thread.sleep(100);
                
                //
            }else{*/
            
            labelMensaje.setText("Generando Vacio.....");

            timer.start();

            banco.encenderCalSol1(false);
            banco.encenderCalSol2(false);
            //apagar el Solenoide 1 y el solenoide 2
            banco.encenderSolenoideUno(false);
            banco.encenderSolenoide2(false);
            //prende la bomba de muestra y apaga la bomba de Drenaje palabra : 00010000
            banco.encenderDrainPump(false);
            banco.encenderBombaMuestras(true);
            //banco.mapaFisicoES((short)16, true);//este metodo no sirve para el banco capelec
            //banco.encenderSolenoideUno(true);

            while (contadorTimer < 11) { //10 segundos de vacio
                setProgress(contadorTimer);
                Thread.sleep(120);
            }//end while

            //apagar la bomba de muestras y mantener cerrado todo los demas
            //banco.mapaFisicoES((short)0, true);
            banco.encenderBombaMuestras(false);

            //Vacio Generado verificar la bandera de bajoFlujo
            //Interrumpir el hilo de datos
            medicion = banco.obtenerDatos();
//            while(contadorTimer < 21 && medicion.isBajoFlujo()){ //repetir durante 20 segundos
            while (contadorTimer < 21 &&(medicion.isBajoFlujo() || medicion.isVacuumSwitch())) { //repetir durante 20 segundos
                labelMensaje.setText("El vacio esta manteniendose");
                //panelProgreso.getBarraTiempo().setValue(contadorTimer);
                setProgress(contadorTimer);
                medicion = banco.obtenerDatos();
            }
            //no cuenta mas
            if (contadorTimer < 21) {
                labelMensaje.setText("Fallo en la prueba no hay vacio en el banco.");
                aprobada = false;
                registrarResultado(serialEquipo);
            } else {
                labelMensaje.setText("Prueba OK");
                aprobada = true;
            }

            if (worker != null) {
                worker.setInterrumpido(false);
            }

            timer.stop();

            if (aprobada) {
                registrarResultado(serialEquipo);
                labelMensaje.setText("La Prueba de Fugas Fue Aprobada y Guardada *");
            }
        //}    
            Thread.sleep(4000);
        } catch (IOException | ClassNotFoundException | InterruptedException | SQLException ex) {
            Mensajes.mostrarExcepcion(ex);
        } finally {
            panelFugas.dispose();
        }

        return null;
    }

    private void registrarResultado(String serial) throws ClassNotFoundException, SQLException, IOException {
        Calibracion calibracion = new Calibracion();

        calibracion.setUsuario(idUsuario);//la madre si me borran el usuario uno
        calibracion.setTipoPrueba(8);
        calibracion.setValor1("");
        calibracion.setValor2("");
        calibracion.setValor3("");
        calibracion.setValor4("");
        calibracion.setValor5("");
        calibracion.setValor6("");
        calibracion.setAprobada(aprobada);
        calibracion.setIdEquipo(new EquipoController().findIdEquipoBySerial(serial));
        calibracion.setIdTipoCalibracion(3);

        new CalibracionController().registrarCalibracion(calibracion);
    }//End of method registrarResultado

    private void registrarResultadoRep(String serial) throws ClassNotFoundException, SQLException, IOException {
        Calibracion calibracion = new Calibracion();

        calibracion.setUsuario(idUsuario);//la madre si me borran el usuario uno
        calibracion.setTipoPrueba(8);
        calibracion.setValor1("");
        calibracion.setValor2("");
        calibracion.setValor3("");
        calibracion.setValor4("");
        calibracion.setValor5("");
        calibracion.setValor6("");
        calibracion.setAprobada(false);
        calibracion.setIdEquipo(new EquipoController().findIdEquipoBySerial(serial));
        calibracion.setIdTipoCalibracion(3);

        new CalibracionController().registrarCalibracion(calibracion);
    }//End of method registrarResultadoRep

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

}
