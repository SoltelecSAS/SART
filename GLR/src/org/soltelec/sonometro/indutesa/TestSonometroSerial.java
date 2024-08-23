/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.indutesa;

import gnu.io.SerialPort;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.PortSerialUtil;

/**
 *
 * @author User
 */
public class TestSonometroSerial {
    
    
    public static void main(String args[]){
        try {
            SerialPort puertoSerial = PortSerialUtil.connect("COM1", 9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE );
            
            SonometrolPCE322ASerial sonometro = new SonometrolPCE322ASerial(puertoSerial);
            
            Thread t = new Thread(sonometro);
            t.start();
            
        } catch (Exception ex) {
            Logger.getLogger(TestSonometroSerial.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
}
