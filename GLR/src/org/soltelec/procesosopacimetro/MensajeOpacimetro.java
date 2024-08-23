/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import org.soltelec.util.UtilGasesModelo;

/**
 * Clase para encapsular los
 * mensajes de respuesta del opacimetro
 *
 * @author Usuario
 */
public class MensajeOpacimetro {

    private byte byteDelComando;
    private char comando;
    private byte checksum;
    private byte[] datos;

    public MensajeOpacimetro() {
        datos = new byte[0];
    }

    public byte getByteDelComando() {
        return byteDelComando;
    }

    public void setByteDelComando(byte byteDelComando) {
        this.byteDelComando = byteDelComando;
        this.comando = (char) byteDelComando;
        calcularChecksum();
    }

    public byte getChecksum() {
        return checksum;
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }

    public char getComando() {
        return comando;
    }

    public void setComando(char comando) {
        this.comando = comando;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
        calcularChecksum();
    }

    private void calcularChecksum() {
        int length = datos.length;

        byte[] arreglo = new byte[length +1];
        arreglo[0] = this.byteDelComando;
        for(int i = 1; i <length +1; i++){
            arreglo[i] = datos[i-1];
        }//end for
         this.checksum = UtilGasesModelo.calcularCheckSumOpacimetro(arreglo);
    }

    @Override
    public String toString() {
        StringBuilder strB = new StringBuilder("MensajeOpacimetro{" + "byteDelComando=" + byteDelComando + "comando=" + comando + "checksum=" + checksum);
        for(byte b:datos ){
            strB.append(b+"\n");
        }
        return strB.toString();
    }



}//end of class
