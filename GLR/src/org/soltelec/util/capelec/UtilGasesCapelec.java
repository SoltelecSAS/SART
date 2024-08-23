/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import org.soltelec.util.UtilGasesModelo;

/**
 *
 * @author User
 */
public class UtilGasesCapelec {
    
    
    /**
     * Recibe un arreglo de bytes y retorna una instancia de la 
     * clase MensajeCapelec
     * @param b
     * @return 
     */
    public static MensajeCapelec armarMensajeCapelec(byte trama[]){
        int tamanioDatos = UtilGasesModelo.byteToInt(trama[1]);
        byte[] datos = new byte[tamanioDatos];
        System.arraycopy(trama, 2, datos, 0, tamanioDatos);
        MensajeCapelec mensaje = new MensajeCapelec(trama[0], trama[1], datos);
        return mensaje;       
    }
    
    
    public static int numeroDecimales(double f){
       int numeroDecimales = -1;
       double parteDecimal  = 0;
       double parteEntera = 0;
       double auxiliar;
       do {
       numeroDecimales++;    
       auxiliar =   (float) (f * Math.pow(10, numeroDecimales));    
       parteEntera = Math.floor(auxiliar);
       parteDecimal = auxiliar - parteEntera;
       }
       while(parteDecimal != 0);
       return numeroDecimales;
    }
    
    /**
     * Construye un arreglo de bytes a partir de un mensaje codificado
     * con la sintaxis del banco capelec
     * @param mensaje
     * @return 
     */
    public static byte[] armarTrama(MensajeCapelec mensaje){
        
        //letra de comando un byte, tamanio un byte y checksum un byte
        //y los datos que son un arreglo de byte del tamanio que indique 
        
        int tamanioTrama = 3 + mensaje.getDatos().length;
        
        byte[] b = new byte[tamanioTrama];
        
        b[0] = mensaje.getLetraComando();//es una letra aunque se represente como un byte
        
        b[1] = mensaje.getNumeroDatos();//es un numero natural entre 0 y 255
        
        System.arraycopy(mensaje.getDatos(), 0, b, 2, mensaje.getDatos().length);
        b[b.length - 1] = mensaje.getChecksum();
        
        return  b;
        
    }
    
    
    private static float fromByteArrayToFloat(byte b[]){
        
        String strNumero = new String(b);
        
        float numero = -1;
        
            
            numero = Float.parseFloat(strNumero);          
        
        
        return numero;
    }
    
    
    public static byte[] armarTramaOpacidad(MensajeOpacidadCapelec mensaje){
        
        int longitudTrama = mensaje.getDatos().length + 2;
        
        byte[] trama = new byte[longitudTrama];
        
        trama[0] = mensaje.comando;
        
        System.arraycopy(mensaje.getDatos(), 0, trama, 1, mensaje.getDatos().length);
        
        trama[longitudTrama -1] = mensaje.getChecksum();
        
        return trama;
        
    }

    static MensajeOpacidadCapelec armarMensajeOpacidadCapelec(byte[] arreglo) {
        
        byte comando = arreglo[0];
        
        byte[] datos = new byte[arreglo.length -2];
        
        System.arraycopy(arreglo, 1, datos, 0, arreglo.length -2 );
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec(comando, datos);
        
        return mensaje;      
        
    }
    
}
