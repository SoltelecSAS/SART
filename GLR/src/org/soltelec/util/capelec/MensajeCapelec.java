/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import org.soltelec.util.Mensaje;
import org.soltelec.util.UtilGasesModelo;

/**
 *Clase que encapsula un comando y una respuesta de un comando
 * del analizador de gases CAP 3300
 * @author User
 */
public class MensajeCapelec {
    
    private byte letraComando;
    private byte numeroDatos;
    private byte[] datos;
    private byte checksum;

    public MensajeCapelec(byte numeroComando, byte numeroDatos, byte[] datos) {
        this.letraComando = numeroComando;
        this.numeroDatos = numeroDatos;
        this.datos = datos;
        this.checksum = obtenerCheckSum(this.letraComando, this.numeroDatos, this.datos);
    }

    MensajeCapelec(char letra, int numeroBytes, byte[] b) {
        this.letraComando = (byte)letra;
        this.numeroDatos = (byte)numeroBytes;
        this.datos = b;
        this.checksum = obtenerCheckSum(letraComando, numeroDatos, datos);
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }

    public byte getLetraComando() {
        return letraComando;
    }

    public void setLetraComando(byte letraComando) {
        this.letraComando = letraComando;
    }

    public byte getNumeroDatos() {
        return numeroDatos;
    }

    public void setNumeroDatos(byte numeroDatos) {
        this.numeroDatos = numeroDatos;
    }

    /**
     * Obtiene el checksum segun protocolo del BancoCapelec
     * @param numeroComando
     * @param numeroDatos
     * @param datos
     * @return 
     */
   public  static byte obtenerCheckSum( byte numeroComando, byte numeroDatos, byte[]  datos) {
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
       sumaDatos += UtilGasesModelo.byteToInt(numeroDatos);
       
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

    public byte getChecksum() {
        return MensajeCapelec.obtenerCheckSum(letraComando, numeroDatos, datos);
    }
   
   
    
}
