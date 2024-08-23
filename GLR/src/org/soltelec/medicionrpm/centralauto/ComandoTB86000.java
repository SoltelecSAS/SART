/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm.centralauto;

import org.soltelec.util.UtilGasesModelo;

/**
 *Clase para encapsular los comandos de
 * el modulo TB6000
 * @author DANIELA SANA
 */
public class ComandoTB86000 {

    private short comando;//caracter asccii del comando
    private short argumentos[];//argumentos del comando
    private short checksum;

    public ComandoTB86000() {
        
    }



    public ComandoTB86000(short comando, short[] argumentos){
        this.comando = comando;
        this.argumentos = argumentos;
    }

    /**
     * Metodo para convertir todos los shorts a bytes y retornar
     * el argumento
     * @return
     */
    public byte[] comandoToBytes(){
          byte[] arregloBytes = new byte[2+argumentos.length];
          arregloBytes[0] = (byte) (comando & 0xFF);
          for(int i = 0; i < argumentos.length;i++){
              arregloBytes[i+1] = (byte) (argumentos[i] & 0xFF);
          }
          arregloBytes[arregloBytes.length -1] = obtenerCheckSum(comando,argumentos);
          return arregloBytes;
    }

    /**
     * metodo para determinar el checksum de
     * los comandos
     * se calcula usando shorts para que las operaciones no se vean
     * afectadas.
     * @param argumento
     * @param numeros
     * @return
     */
    public static byte obtenerCheckSum(short argumento ,short numeros[]){
        short suma = argumento;
        for(short s:numeros)
            suma+=s;
        System.out.println("suma: " + suma);
        suma = (short) (256 - suma);
        suma = (short) (0x0ff & suma);
        return (UtilGasesModelo.toBytes(suma))[0];        
    }





//    public static void main(String args[]){
//        ComandoTB6000 c = new ComandoTB6000();
//        System.out.println("Checksum de: ");
//        short[] arregloPrueba = new short[]{0x76,0x7A,0x01};
//        System.out.println(c.obtenerCheckSum(0,arregloPrueba));
//        short[] arregloPrueba2 = new short[]{0x075,0x01,0x1f,0x25,0x50,0x21};
//        System.out.println("Checksum: " + c.obtenerCheckSum(0,arregloPrueba2));
//
//
//    }
}
