/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm.centralauto;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.medicionrpm.MedidorRevTemp;
import org.soltelec.util.PortSerialUtil;

/**
 *Clase para implementar la comunicacion con el
 * kit de revoluciones tb85000
 * @author GerenciaDesarrollo
 */
public class TB85000 implements MedidorRevTemp {

    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;
    private int rpm;
    private int temp;
    private List<Byte> listaComando;
    private byte[] arregloInicial = {0x02,0x52,0x50,0x4D,0x17,0x31,0x17};//arreglo minimo para el comando
    private int numeroIntentos = 0;
    
    /**
     * Dos alternativas 1: arreglos de bytes estaticos para cada uno de los casos
     * de los comandos que son en resumen un conjunto no infinito
     * Alternativa dinamica para los comandos dinamicos
     * que son cambiar cilindros y cambiar numero de tiempos
     *
     */

    /**bloque de inicializacion para las cadenas**/


    public TB85000() {

    }

    public TB85000(SerialPort serialPort) throws IOException{
        this.serialPort = serialPort;
        this.in = serialPort.getInputStream();
        this.out = serialPort.getOutputStream();
    }


    public void cambiarCilindros(int numeroCilindros) throws IOException{
        byte[] trama = armarComando("CI", numeroCilindros);
        byte[] respuesta = enviarRecibirTrama(trama);
    }

    public void cambiarTiempos(int numerotiempos) throws IOException{
       byte[] trama = armarComando("TE",numerotiempos);
       byte[] respuesta = enviarRecibirTrama( trama );
    }

    public String solicitarID() throws IOException{
        byte[] trama = armarComando("ID");
        byte[] respuesta = enviarRecibirTrama(trama);
        String str = new String(respuesta);
        return str;
    }

    public int obtenerRpms() throws IOException{
        byte[] trama = armarComando("VA");
        byte[] respuesta = enviarRecibirTrama(trama);
        byte[] byteRpms = new byte[4];
        System.arraycopy(respuesta, 10, byteRpms,0, 4);
        String strInt = new String(byteRpms);
        try{
            rpm = Integer.parseInt(strInt);
        }catch(NumberFormatException ne){
            rpm = -1;
        }
        return rpm;
   }

    public void obtenerRPMsTemp() throws IOException{
        byte[] trama = armarComando("VE");
        byte[] respuesta = enviarRecibirTrama( trama);
        byte[] byteRpms = new byte[4];
        if(respuesta != null){
        System.arraycopy(respuesta, 10, byteRpms,0, 4);
        String strInt = new String(byteRpms);
        try{
            rpm = Integer.parseInt(strInt);
        }catch(NumberFormatException ne){
            rpm = -1;
        }
        byte[] byteTemp = new byte[3];
        System.arraycopy(respuesta,15, byteTemp, 0, 3);
        String strTemp = new String(byteTemp);
        try{
            temp = Integer.parseInt(strTemp);
        }catch(NumberFormatException ne){
            temp = -1;
        }
        System.out.println("R:" + rpm +" T: " + temp);
        }
    }

    /**
     * Metodo para armar el comando del protocolo MCTCNet
     *
     * @param comando
     * @param args
     * @return
     */
    private byte[] armarComando(String comando, int... argumentos){

        //si el arreglo de argumentos es vacio es decir su longitud es cero, entonces
        //poner en el nuevo arreglo el hexa del comando, calcular el checksum y armar
        ///el arreglo completo
        //si existen uno o mas parametros como argumentos entonces
        //ponerlos en el arreglo calcular el checksum y armar la trama completa
        short checksum = 0;

        //solamente un comando entonces se conoce la longitud del arreglo
        //como longitud del arreglo base mas la longitud del comando mas los dos bytes
        //del caracter de suma mas el caracter ETX
            char[] caracteres = comando.toCharArray();
            byte[] arrayComando = new byte[caracteres.length];
            int i = 0;
            for(char c: caracteres){
                int aux = (int)c;
                byte b = (byte)(aux&0xFF);
                arrayComando[i++] = b;
            }//end of method for
            byte[] arrayArgumentos = new byte[argumentos.length];
            i=0;
            StringBuilder db = new StringBuilder();
            for(int argumento: argumentos){
                db.append(String.valueOf(argumento));
            }//end of method for
            i = 0;
            char[] caracteresComando = db.toString().toCharArray();
            for(char c: caracteresComando){
                int aux = (int)c;
                byte b = (byte)(aux&0xFF);
                arrayArgumentos[i++] = b;
            }
            //suma base de el arreglo inicial
            
            byte[] arregloTotal = new byte[arregloInicial.length+arrayComando.length+(arrayArgumentos.length*2)+3];
            int j = 0;
            System.arraycopy(arregloInicial, 0, arregloTotal, 0, arregloInicial.length);//copia el arreglo base
            j = arregloInicial.length;//el indice debe ir ahi
            System.arraycopy(arrayComando,0,arregloTotal, arregloInicial.length, arrayComando.length);
            j = arregloInicial.length + arrayComando.length;
            for(int k = 0; k < arrayArgumentos.length;k++){
                arregloTotal[j] = 23;
                j = j+1;
                arregloTotal[j] = arrayArgumentos[k];
                j = j+1;
            }

            for(int k=1; k<arregloTotal.length;k++){
                checksum += arregloTotal[k];
            }
            
            String hexaChecksum = (Integer.toHexString(checksum&0xFF)).toUpperCase();
            //System.out.println(hexaChecksum);
            char[] caracteresCS = hexaChecksum.toCharArray();
            byte[] checksumArray = new byte[caracteresCS.length];
            i=0;
            for(char c:caracteresCS){
                byte b = (byte)c;
                checksumArray[i++] = b;
            }
            arregloTotal[arregloTotal.length-3] = checksumArray[0];
            arregloTotal[arregloTotal.length -2] = checksumArray[1];
            arregloTotal[arregloTotal.length-1] = 3;
//            for(byte b:arregloTotal){
//                System.out.print(b+" ");
//            }
            return arregloTotal;
    }

    @Override
    public int getRpm() throws ArrayIndexOutOfBoundsException {
         try {
            obtenerRPMsTemp();
         } catch (IOException ex) {
             System.out.println("Excepcion enviando trama por puerto serial");
             throw new ArrayIndexOutOfBoundsException();
         }
        return rpm;

    }

    @Override
    public int getTemp() throws ArrayIndexOutOfBoundsException {
        try {
            obtenerRPMsTemp();
        } catch (IOException ex) {
            System.out.println("Excepcion enviando trama por puerto Serial");
            throw new ArrayIndexOutOfBoundsException();
        }
        return temp;
    }

    public byte[] enviarRecibirTrama(byte[] arregloEnviar) throws IOException{
        out.write(arregloEnviar);
        System.out.println("Trama enviada: ");
//            for(byte b: arregloEnviar)
//                System.out.print(b+" ");
        //Leer la respuesta que envia el TB6000

        byte[] buffer = new byte[1024];
        int data;

        int len = 0;
        numeroIntentos = 0;
        while (numeroIntentos < 5) {
            while ((data = in.read()) > -1) {
                buffer[len++] = (byte) data;
            }
            if (len > 0) {
                byte[] arregloRetornar = new byte[len];
                System.arraycopy(buffer, 0, arregloRetornar, 0, len);
                System.out.println("");
                String str = new String(arregloRetornar);
                System.out.println(str);
//                    for(short s: arregloRetornar)
//                        System.out.print(s +" ");
                return arregloRetornar;
            } else {
                System.out.println("No hubo respuesta desde el dispositivo");
                numeroIntentos++;
                
            }
        }   
        return null;
    }

    public static void main(String args[]) throws IOException{
        SerialPort puerto = null;
        try {
            puerto = PortSerialUtil.connect("COM1", 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (Exception ex) {
            System.out.println("imposible conectarse a COM20");
            return;
        }

       TB85000 tb85000 = null;
        try {
            tb85000 = new TB85000(puerto);
        } catch (IOException ex) {
            System.out.println("Error configurando flujos de puerto");
        }
//        byte[] armarComando = tb85000.armarComando("ID");
//        System.out.println("");
//        armarComando = tb85000.armarComando("VA");
//        System.out.println("");
//        armarComando = tb85000.armarComando("VE");
//        System.out.println("");
//        armarComando = tb85000.armarComando("CI",8);
//        System.out.println("");
//        armarComando = tb85000.armarComando("TE",4);
       for(int i = 0; i<10;i++){
           tb85000.getRpm();
           tb85000.getTemp();
       }
       puerto.close();
    }

    @Override
    public SerialPort getPuertoSerial() {
        return serialPort;
    }

    @Override
    public void setPuertoSerial(SerialPort port) {
        this.serialPort = port;

    }

    @Override
    public void stop() {
        try {
            Thread.sleep(100);
            


        
        if(serialPort != null){
    serialPort.close();
}
        } catch (InterruptedException ex) {
            Logger.getLogger(TB85000.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}//end of class TB85000



