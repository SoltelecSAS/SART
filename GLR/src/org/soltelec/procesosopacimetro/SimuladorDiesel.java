/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import gnu.io.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;
import org.soltelec.medicionrpm.MedidorRevTemp;

/**
 *
 * @author GerenciaDesarrollo
 */
public class SimuladorDiesel implements Runnable,MedidorRevTemp,ActionListener{

    private Timer timer;
    private int contadorTemporizacion;
    private double rpm;
    private final double velocidadMinima = 750;
    private final double velocidadGobernada = 1500;
    private Random r;
    private boolean simularMinima;
    private boolean simularGobernada;
    private boolean simularAceleracion;
    private boolean simularDesaceleracion;

    public SimuladorDiesel() {
        
        timer = new Timer(125,this);
        r = new Random();
    }
    
    
    
    
    @Override
    public int getRpm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTemp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(simularMinima){
            rpm = velocidadMinima + 10 - r.nextInt(20);
        }
        
        if(simularGobernada){
            rpm = velocidadGobernada + 10 -r.nextInt(20);
        }
        
        if(simularAceleracion){
            
            
        }
        
        
    }

   
    
    @Override
    public SerialPort getPuertoSerial() {
        return null;
    }

    @Override
    public void setPuertoSerial(SerialPort port) {
        System.out.println(" no implementado");
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }




}
