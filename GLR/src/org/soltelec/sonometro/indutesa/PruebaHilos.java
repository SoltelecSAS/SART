/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.indutesa;

import gnu.io.SerialPort;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.PortSerialUtil;

/**
 *
 * @author User
 */
public class PruebaHilos {
    
    
    public static void main(String args[]){
        try {
            
            String nombrePuerto = "COM2";
            SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
            BlockingQueue<List<Integer>> lista = new ArrayBlockingQueue<List<Integer>>(100);
            ProductorSonometroPC322A productor = new ProductorSonometroPC322A(puerto,lista);
            ConsumidorPC322A consumidor = new ConsumidorPC322A(lista,productor);
            
            Thread t = new Thread(productor);
            Thread t2 = new Thread(consumidor);
            
            t.start();
            t2.start();
            
            Thread.sleep(10000);
            consumidor.stop();
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(PruebaHilos.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
