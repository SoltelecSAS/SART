/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import org.soltelec.util.UtilGasesModelo;

/**
 *Clase para encapsular los mensajes de comando y respuesta que se envian hacia el
 * opacimetro capelec
 * @author User
 */
public class MensajeOpacidadCapelec {
    
    public byte comando;
    public byte checksum;
    public byte[] datos;

    public MensajeOpacidadCapelec() {
    }

    /**
     * Constructor para los comandos que solamente utilizan el char
     * @param comando 
     */
    public MensajeOpacidadCapelec(char letraComando) {
        this.comando = (byte)letraComando;
        this.datos = new byte[0];
        this.checksum = obtenerCheckSum(comando,datos);
    }

    
    
    
    public MensajeOpacidadCapelec(byte comando, byte[] datos) {
        this.comando = comando;
        this.datos = datos;
        this.checksum = obtenerCheckSum(comando, datos);
    }

    public MensajeOpacidadCapelec(char comando, byte[] datos) {
        this.comando = (byte)comando;
        this.datos = datos;
        this.checksum = obtenerCheckSum(this.comando, datos);
    }

    public MensajeOpacidadCapelec(char comando, byte dato) {
        this.comando = (byte)comando;
        this.datos = new byte[]{dato};
        this.checksum = obtenerCheckSum(this.comando, datos);
    }

    
    
    
    public byte getComando() {
        return comando;
    }

    public void setComando(byte comando) {
        this.comando = comando;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
        this.checksum = obtenerCheckSum(comando,this.datos);
    }

    public byte getChecksum() {
        return checksum;
    }
    
    
    
    
    
    
     public  static byte obtenerCheckSum( byte numeroComando,  byte[]  datos) {
       //convertir todo a entero hacer las operaciones y despues convertir a byte;
      
       //int intNumeroComando = UtilGasesModelo.byteToInt(numeroComando);
       //int intNumeroDatos = UtilGasesModelo.byteToInt(numeroDatos);
       
       int[] intDatos = new int[datos.length];
       int sumaDatos  = 0;
       for(int i = 0; i < datos.length; i++){
           
           intDatos[i] = UtilGasesModelo.byteToInt(datos[i]);
           sumaDatos += intDatos[i];
           
       }
       
       sumaDatos += UtilGasesModelo.byteToInt(numeroComando);
       
       
       //calcular el complemento
       
       int complemento = (~sumaDatos) + 1;
       
       //System.out.println("complemento: " + complemento);
       
       short residuo = (short) (complemento % 0x100);
       
       //System.out.println("residuo: " + residuo);
       
       short mascara = (short) (residuo & 0xFF);
       //pasar el numero a cadena
     
       //pasar ahora a byte
       byte byteSuma = (UtilGasesModelo.toBytes(mascara))[0];
       
       //System.out.println("checksum final" + Integer.toHexString(byteSuma));       
       //la suma debe ser entera       
       return byteSuma;
       
   }

    
    
}
