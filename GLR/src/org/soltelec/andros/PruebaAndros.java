/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.andros;

import java.util.ArrayList;
import java.util.List;
import org.soltelec.util.UtilGasesModelo;

/**
 *
 * @author User
 */
public class PruebaAndros {
 
    
    
    public static void main(String args[]){
        
        List<Byte> listaEjemplo = new ArrayList<Byte>();
        listaEjemplo.add((byte)1);
        listaEjemplo.add((byte)5);
        
        MensajeAndros mensaje = new MensajeAndros(2,listaEjemplo);
        
        System.out.println("Checksum: " +  mensaje.obtenerCheckSum());
        
        byte[] b = UtilGasesAndros.armarTrama(mensaje);
        
        for(Byte dato: b){
            System.out.print(dato +" ");
        }
        
        
        byte bajo1 = 0x34;
        byte bajo2 = 0;
        byte bajo3 = 0;
        byte bajo4 = 0;
        
        int entero = UtilGasesModelo.bytesToInt(bajo1, bajo2, bajo3, bajo4);
        System.out.println("El numero es:" + entero);
        
    }
}
