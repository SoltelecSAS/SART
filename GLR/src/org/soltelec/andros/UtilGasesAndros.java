/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.andros;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class UtilGasesAndros {
    
    
    
    /**
     * Metodo para convertir un Mensaje para comunicarse con el
     * protocolo de Andros en un arreglo de bytes para enviarlos por
     * el 
     * @param mensaje
     * @return 
     */
    public static byte[] armarTrama(MensajeAndros mensaje){
        
        byte[] b = new byte[mensaje.getListaDatos().size() + 4];
        b[0] = (byte) mensaje.getDIDNACK();
        b[1] = (byte)mensaje.getNumeroBytes();
        b[2] = (byte)mensaje.getComando();
        List<Byte> lista = mensaje.getListaDatos();
        for(int i=0; i <lista.size(); i++ ){
            
            b[3+i] = lista.get(i);
            
        }
        b[lista.size() + 3] = (byte)mensaje.obtenerCheckSum();
        
        return b;      
        
    }
    
    
    /**
     * Convierte una trama de bytes retornados desde el puerto serial en un
     * objeto de la clase MensajeAndros para facilitar su manipulación 
     * @param b
     * @return 
     */
    
    public static MensajeAndros armarMensaje(byte[] b){
        
        int didnack = b[0];
        int comando = b[2];        
        int numeroBytes = b[1];
        List<Byte> listaDatos = new ArrayList<Byte>();        
        if (numeroBytes > 1){//se supone que debe tener mas de uno para que tenga una lista de datos
            
            
            for(int i=0; i < numeroBytes -1; i++){            
                
                listaDatos.add(b[3+numeroBytes]);
                        
            }
            
        } else {//se espera
            
        }
        MensajeAndros mensaje = new MensajeAndros(comando,listaDatos);
        mensaje.setDIDNACK(didnack);
        //el checksum debe obtenerse en el constructor
        
        return mensaje;
        
    }
    
    
    
    
}
