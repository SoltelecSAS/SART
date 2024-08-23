/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.gyp;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dany
 */
public class ProductorH55633T implements Runnable {
    private SerialPort serialPort;
    private InputStream in;
    volatile boolean  terminado = false;
    private BlockingQueue<List<Integer>> listaBytes;
    int[] buffer = new int[1024];

    public ProductorH55633T(SerialPort serialPort, BlockingQueue<List<Integer>> lista) throws IOException {
        this.serialPort = serialPort;
        
        this.in = serialPort.getInputStream();
        this.listaBytes = lista;
        
    }

    
    
    
    
    @Override
    public void run() {
       
        //listaBytes = new ArrayBlockingQueue<List<Integer>>(0);
                int data = 0;
                int len = 0;
        while(!terminado){
            try {
                data = 0;
                len = 0;
                
                //Leer el puerto Serial
                while( (data = in.read() ) > -1  && len < 1024){
                    int val = data & 0xff;
                    buffer[len++] = data;
                    if(data == 0){
                        break;
                    }
                    
                }
            
                if(len > 0){
                    List<Integer> lista = new ArrayList<>();
                    for(int i = 0; i < len; i++){
                        lista.add(buffer[i]);
                    }
                
                    listaBytes.add(lista);
                } else {
                    System.out.println("No recibe nada del puerto serial");
                }
                Thread.sleep(1);
                
            } catch (IOException ex) {
                Logger.getLogger(ProductorH55633T.class.getName()).log(Level.SEVERE, null, ex);
            } catch(InterruptedException iexc){
                
            }
            
        }//end of mientras no terminado
        
        
        
        //informa que el hilo ha terminado
        System.out.println("Productor del sonometro terminado");
        serialPort.close();
        
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }
}
