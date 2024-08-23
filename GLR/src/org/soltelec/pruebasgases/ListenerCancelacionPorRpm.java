/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Future;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.util.RegistrarMedidas;

/**
 *
 * @author user
 */
public class ListenerCancelacionPorRpm implements ActionListener {

    private final Future futureParaCancelar;
    private final long idPrueba, idUsuario;
   
    private final BancoGasolina banco;
    private final MedidorRevTemp medRevTemp;
    private PanelPruebaGases panel;

    private double temperatura;
    private double humedad;
    
    public ListenerCancelacionPorRpm(Future futureParaCancelar, long idPrueba, BancoGasolina banco, 
                                        MedidorRevTemp medRevTemp, PanelPruebaGases panel, long idUsuario, double temp, double humedad) {
        this.futureParaCancelar = futureParaCancelar;
        this.idPrueba = idPrueba;
        this.banco = banco;
        this.medRevTemp = medRevTemp;
        this.panel = panel;
        this.idUsuario = idUsuario;
        this.temperatura = temp;
        this.humedad = humedad;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            panel.getFuncion().setText("rpm");
            RegistrarMedidas regMedidas = new RegistrarMedidas();
            regMedidas.registrarTemperaturInicio(temperatura, this.idPrueba);
            regMedidas.registrarHumedadInicio(humedad, this.idPrueba);
            regMedidas.registraRechazoRpm(" ", "4.1.1.1.5 Revoluciones fuera de rango. ", idUsuario, idPrueba, 84018);
            System.out.println("Ya registre medidas de rechazo por RPM: " + "idPrueba = " + idPrueba);
            System.out.println("Cierre de Panel ");
            System.out.println(panel.getFuncion().getText());
            panel.cerrar();
            System.out.println("Entro en el metodo de Cancelamiento ");
            if (banco != null) {
                System.out.println("estoy en bomba muestras");
                banco.encenderBombaMuestras(false);
            }
            //si es otro dispositivo de medicion entonces cerrar el puerto
            /* if (!(medRevTemp instanceof BancoSensors) && medRevTemp != null) {
                 System.out.println("L1");
                SerialPort puertoSerial = medRevTemp.getPuertoSerial();
                if (puertoSerial != null) {
                    puertoSerial.close(); //para no cerrarlo dos veces                   
                } System.out.println("L2");
            }  */
            System.out.println("aun envio datos");
            if (futureParaCancelar != null) {
                while (futureParaCancelar.isCancelled() == false) {
                    futureParaCancelar.cancel(true);
                }
            }
        } catch (Exception exc) {
            System.out.println("Excepcion cerrando puertos seriales debido a " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
