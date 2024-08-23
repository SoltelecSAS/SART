/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.Callable;
import javax.swing.Timer;
import org.soltelec.medicionrpm.MedidorRevTemp;

/**
 * Hilo Simulador de las revoluciones para prueba de sonometro
 * simula tres aceleraciones a mas de 2500 rpm , cada aceleracion
 * debe sostenerse por 3 segundos
 * la prueba es igual para vehiculos que para motos
 *
 * @author GerenciaDesarrollo
 */
class SimuladorRmpSonometro implements Callable<Void>,ActionListener,MedidorRevTemp{

    private int contadorSimulacion;
    private Timer timer,timerSimulacion;
    private int velocidadRalenti;
    private int velocidadCrucero;
    private Random random2;
    private int watchDog ;//contador para prevenir que este hilo se ejecute por siempre
    private double rpmSimuladas;
    private boolean simulandoRalenti,subidaAceleracion,
            simulandoCrucero,bajadaAceleracion;//estas variables en conjunto determinan el comportamiento
                                                //de las revoluciones


    public SimuladorRmpSonometro() {
        timer = new Timer(500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                watchDog++;
            }
        });
        timerSimulacion = new Timer(250,this);//una base de tiempo adecuada para la funcion
        random2 = new Random();
        velocidadRalenti =  1950 - random2.nextInt(950);
        velocidadCrucero = 3700 - random2.nextInt(1200);
    }//end of constructor SimuladorRpmSonometro

    


    @Override
    public Void call() throws Exception {
        timer.start();
        timerSimulacion.start();
        while (watchDog< 240 && !Thread.currentThread().isInterrupted()){
            System.out.println("Revoluciones " + rpmSimuladas);
            try{
                Thread.sleep(500);
            }catch(InterruptedException ie){
               System.out.println("Cancelacion");
               break;               
            }
        }
        timer.stop();
        timerSimulacion.stop();
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

           if(simulandoRalenti && !subidaAceleracion){// retorna la velocidad Ralenti estable
                rpmSimuladas = velocidadRalenti + (15-random2.nextInt(30));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if(subidaAceleracion&& !simulandoCrucero){//retorna la subida de aceleracion desde ralenti hasta crucero
                contadorSimulacion++;
                rpmSimuladas = ( (velocidadCrucero-velocidadRalenti)*(1-Math.exp(0-(0.25-random2.nextDouble()*0.03)*contadorSimulacion))+(15-random2.nextInt(30)) ) + velocidadRalenti;
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if(simulandoCrucero && !simulandoRalenti &&!subidaAceleracion){//simula estabilidad en crucero
                rpmSimuladas = ((velocidadCrucero ) + (15-random2.nextInt(30)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }
            if(bajadaAceleracion&& !simulandoRalenti &&!subidaAceleracion &&!simulandoCrucero){//simula la desaceleracion desde crucero hasta ralenti
                contadorSimulacion++;
                rpmSimuladas = (velocidadRalenti + (velocidadCrucero-velocidadRalenti)*(Math.exp((-0.12-random2.nextDouble()*0.01)*contadorSimulacion)) +(16-random2.nextInt(28)));
                rpmSimuladas = rpmSimuladas - rpmSimuladas % 10;
            }

    }

    @Override
    public int getRpm() {
        return (int)rpmSimuladas;
    }

    @Override
    public int getTemp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isBajadaAceleracion() {
        return bajadaAceleracion;
    }

    public void setBajadaAceleracion(boolean bajadaAceleracion) {
        this.bajadaAceleracion = bajadaAceleracion;
    }

    public int getContadorSimulacion() {
        return contadorSimulacion;
    }

    public void setContadorSimulacion(int contadorSimulacion) {
        this.contadorSimulacion = contadorSimulacion;
    }

    public boolean isSimulandoCrucero() {
        return simulandoCrucero;
    }

    public void setSimulandoCrucero(boolean simulandoCrucero) {
        this.simulandoCrucero = simulandoCrucero;
    }

    public boolean isSimulandoRalenti() {
        return simulandoRalenti;
    }

    public void setSimulandoRalenti(boolean simulandoRalenti) {
        this.simulandoRalenti = simulandoRalenti;
    }

    public boolean isSubidaAceleracion() {
        return subidaAceleracion;
    }

    public void setSubidaAceleracion(boolean subidaAceleracion) {
        this.subidaAceleracion = subidaAceleracion;
    }

    public int getVelocidadCrucero() {
        return velocidadCrucero;
    }

    public void setVelocidadCrucero(int velocidadCrucero) {
        this.velocidadCrucero = velocidadCrucero;
    }

    public int getVelocidadRalenti() {
        return velocidadRalenti;
    }

    public void setVelocidadRalenti(int velocidadRalenti) {
        this.velocidadRalenti = velocidadRalenti;
    }

    @Override
    public SerialPort getPuertoSerial() {
        return null;
    }

    @Override
    public void setPuertoSerial(SerialPort port) {
        System.out.println("No implementado");
    }

    @Override
    public void stop() {
        Thread.currentThread().interrupt();
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}//end of class SimuladorRpmSonometro
