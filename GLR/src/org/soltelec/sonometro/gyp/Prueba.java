/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.gyp;

import gnu.io.SerialPort;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.sonometro.indutesa.PruebaHilos;
import org.soltelec.util.PortSerialUtil;

/**
 *
 * @author Dany
 */
public class Prueba {
    public static void main(String args[]){
        try {
            
            String nombrePuerto = "COM7";
            SerialPort puerto = PortSerialUtil.connect(nombrePuerto, 9600, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE );
            BlockingQueue<List<Integer>> lista = new ArrayBlockingQueue<>(100);
            ProductorH55633T productor = new ProductorH55633T(puerto,lista);
            ConsumidorH55633T consumidor = new ConsumidorH55633T(lista,productor);
            
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
