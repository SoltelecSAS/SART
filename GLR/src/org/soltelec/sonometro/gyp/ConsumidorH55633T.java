/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.gyp;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.sonometro.Sonometro;

/**
 *
 * @author Dany
 */
public class ConsumidorH55633T implements Runnable, Sonometro {
    volatile boolean  terminado = false;
    private BlockingQueue<List<Integer>> listaBytes;
    private double medicion;
    private ProductorH55633T productor;

    public ConsumidorH55633T(BlockingQueue<List<Integer>> listaBytes, ProductorH55633T productor) {
        this.listaBytes = listaBytes;
        this.productor = productor;
    }
  
    @Override
    public void run() {
       
        while(!terminado){
            
            try {
                int paso = 0;
                List<Integer> lista = listaBytes.take();
                System.out.println("Elemento recibido");
                StringBuilder sb = new StringBuilder();
                for(Integer numero: lista){
                    
                    if (Integer.toHexString(numero).equals("fa") && paso == 0) {
                        paso = 1;
                    } else if (paso == 1) {
                        sb.append(Integer.toHexString(numero));
                        paso = 2;
                    } else if (paso == 2) {
                        sb.append(Integer.toHexString(numero));
                        paso = 0;
                    }
                    
                }
                
                procesarCadena(sb.toString());    
                Thread.sleep(1);
            } //end of terminado
            catch (InterruptedException ex) {
                Logger.getLogger(ConsumidorH55633T.class.getName()).log(Level.SEVERE, null, ex);
            }
            
       }//end of terminado
             
    }//end of method run

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    private void procesarCadena(String cadena) {
        if (cadena.length() > 1) {
            char last = cadena.charAt(cadena.length() - 1);
            if (cadena.length() == 2) {
                cadena = cadena.substring(0,cadena.length() - 1) + "0." + last;
            } else {
                cadena = cadena.substring(0,cadena.length() - 1) + "." + last;
            }
        } else {
            cadena = cadena + "0.0";
        }        
        medicion = Double.parseDouble(cadena);         
        System.out.println("Medicion en double: " + medicion);        
    }

    @Override
    public double getDecibeles() {
       return medicion;
    }

    @Override
    public void stop() {
        this.terminado = true;
        productor.setTerminado(true);
    }
}
