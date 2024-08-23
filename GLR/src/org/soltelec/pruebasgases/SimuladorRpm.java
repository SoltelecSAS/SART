/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.procesosbanco.BancoGasolina;

/**
 * Hilo para simular las revoluciones
 *
 * @author GerenciaDesarrollo
 */
public class SimuladorRpm implements Callable<Void>, ActionListener, MedidorRevTemp {

    private double revoluciones;
    private int contadorCrucero;
    private int contadorRalenti;
    private boolean simularCrucero = true;
    private final Timer timer;
    private int REVOLUCIONES_CRUCERO, REVOLUCIONES_RALENTI;
    private final int tempAceite;
    private final Random r;
    private boolean detener;

    public SimuladorRpm() {
        timer = new Timer(250, this);
        r = new Random();
        //Revoluciones en crucero
        REVOLUCIONES_CRUCERO = 2677 - r.nextInt(250);

        REVOLUCIONES_RALENTI = 1000 - r.nextInt(350);
        revoluciones = REVOLUCIONES_RALENTI;        //
        tempAceite = 90 - r.nextInt(19);
        //detener = true;//para que no cuente los 240 segundos antes de salir
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!detener) {
            contadorCrucero++;
        }
        //la simulacion de crucero es las revoluciones ralenti
        //+ el efecto transitorio de las revoluciones crucero
        if (simularCrucero) {
            if (contadorCrucero > 5) {

                revoluciones = REVOLUCIONES_RALENTI
                        + ((REVOLUCIONES_CRUCERO - REVOLUCIONES_RALENTI) * (1 - Math.exp(0 - (contadorCrucero - 5) * 0.12)));
            }
            //quitar el residuo de dividir en 10 para que siempre sea multiplo de 10
            revoluciones = revoluciones - r.nextInt(25);
            revoluciones = revoluciones - revoluciones % 10;
        } else {
            //simular la bajada de las revoluciones hasta ralenti
            contadorRalenti++;
            revoluciones = REVOLUCIONES_RALENTI
                    + ((REVOLUCIONES_CRUCERO - REVOLUCIONES_RALENTI) * (Math.exp(0 - contadorRalenti * 0.31)));
            revoluciones = revoluciones - r.nextInt(25);
            revoluciones = revoluciones - revoluciones % 10;
        }
    }

    @Override
    public Void call() throws Exception {
        timer.start();
        try {
            while (!detener && !Thread.currentThread().isInterrupted()) {
                if (getRpm() <= (BancoGasolina.LIM_CRUCERO - 250) || getRpm() > (BancoGasolina.LIM_CRUCERO + 250)) {
                    System.out.println("Fuera de rango");
                }

                System.out.println("Revoluciones " + revoluciones);
                Thread.sleep(500);
            }
            timer.stop();
        } catch (InterruptedException iexc) {
            System.out.println("SimuladorCancelado");
            timer.stop();
        }//end of catch
        return null;
    }//end of method call

    @Override
    public int getTemp() {
        return tempAceite;
    }

    public void setSimularCrucero(boolean simularCrucero) {
        this.simularCrucero = simularCrucero;
        contadorCrucero = 0;
        contadorRalenti = 0;
    }//end of setSimularCrucero

    public void setREVOLUCIONES_CRUCERO(int REVOLUCIONES_CRUCERO) {
        this.REVOLUCIONES_CRUCERO = REVOLUCIONES_CRUCERO - (new Random()).nextInt(300);
    }

    public void setDetener(boolean detener) {
        this.detener = detener;
    }

    public double getRevoluciones() {
        return revoluciones;
    }
    

    @Override
    public int getRpm() {
        return (int) getRevoluciones();
    }

    @Override
    public SerialPort getPuertoSerial() {
        return null;
    }

    @Override
    public void setPuertoSerial(SerialPort port) {
        System.out.println("no implementado");
    }

    public static void main(String args[]) {

        SimuladorRpm simulador = new SimuladorRpm();
        ExecutorService exec = Executors.newSingleThreadExecutor();
        simulador.setSimularCrucero(false);
        Future<Void> submit = exec.submit(simulador);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimuladorRpm.class.getName()).log(Level.SEVERE, null, ex);

        }
        simulador.setREVOLUCIONES_CRUCERO(2700);
        simulador.setSimularCrucero(true);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimuladorRpm.class.getName()).log(Level.SEVERE, null, ex);

        }
        simulador.setDetener(true);
    }

    @Override
    public void stop() {
        setDetener(true);
        timer.stop();
    }

    public void setREVOLUCIONES_RALENTI(int REVOLUCIONES_RALENTI) {
        this.REVOLUCIONES_RALENTI = REVOLUCIONES_RALENTI;
    }

    @Override
    public void setRpm(Integer rpm) {     
        revoluciones = rpm;   
        this.REVOLUCIONES_CRUCERO= rpm;
    }

}
