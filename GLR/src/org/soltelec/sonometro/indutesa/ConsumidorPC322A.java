/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.indutesa;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.soltelec.sonometro.Sonometro;

/**
 *
 * @author User
 */
public class ConsumidorPC322A implements Runnable, Sonometro {
    
    volatile boolean  terminado = false;
    private BlockingQueue<List<Integer>> listaBytes;
    private double medicion;
    private ProductorSonometroPC322A productor;

    public ConsumidorPC322A(BlockingQueue<List<Integer>> listaBytes, ProductorSonometroPC322A productor) {
        this.listaBytes = listaBytes;
        this.productor = productor;
    }

    
    
    
    @Override
    public void run() {
       
        while(!terminado){
            
            try {
                
                List<Integer> lista = listaBytes.take();
                System.out.println("Elemento recibido"
                        + "");
                StringBuilder sb = new StringBuilder();
                for(Integer numero: lista){
                    
                    sb.append(Integer.toHexString(numero));
                    //System.out.print(Integer.toHexString(numero)+ " ");
                    
                }
                
                procesarCadena(sb.toString());    
                Thread.sleep(1);
            } //end of terminado
            catch (InterruptedException ex) {
                Logger.getLogger(ConsumidorPC322A.class.getName()).log(Level.SEVERE, null, ex);
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
        
        Pattern  expression = Pattern.compile("a5d(\\d{3,4})");//Busco Temperatura en la cadena
        Matcher matcher = expression.matcher(cadena);
        if(matcher.find()){
        System.out.println("medicion : " + matcher.group(1));
        String strMedicion = matcher.group(1);
        
         int auxiliar = Integer.parseInt(strMedicion);
         medicion = auxiliar*0.1;
         System.out.println("Medicion en double: " + medicion);  
            
            
        }
        
        
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
