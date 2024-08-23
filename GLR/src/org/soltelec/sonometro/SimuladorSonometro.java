/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.PortSerialUtil;

/**
 *
 * @author User
 */
public class SimuladorSonometro {
    
    
   
    
    public static void main(String[] args) {
        try {
            SerialPort puertoSerial = PortSerialUtil.connect("COM3",2400,SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            OutputStream out = puertoSerial.getOutputStream();
            
            String trama = "N077.77db";
            while(true){
                
                out.write(trama.getBytes("US-ASCII"));
                Thread.sleep(250);
            }
            
            
        } catch (NoSuchPortException ex) {
            Logger.getLogger(SimuladorSonometro.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SimuladorSonometro.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
