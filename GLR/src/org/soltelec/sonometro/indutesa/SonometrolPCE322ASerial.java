/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro.indutesa;

import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.sonometro.Sonometro;

/**
 *
 * @author User
 */
public class SonometrolPCE322ASerial implements Runnable, Sonometro{
    
    
    private byte[] comandoIdentificarse = {0x02, 0x4B,0x00,0x00,0x00,0x00,0x00,0x03};
    private byte[] comandoAuxiliar = {0x02,0x61,0x00,0x00,0x00,0x00,0x00,0x03};
    private byte[] comandoIniciarTransmision = {0x02, 0x61,0x00,0x00,0x00,0x00,0x00,0x03,0x02,0x58,0x00,0x00,0x00,0x00,0x00,0x03,0x02, 0x58, 0x00, 0x00, 0x00,0x00,0x00,0x03};
    private byte[] comandoPedirDatos = {0x02, 0x58, 0x00, 0x00, 0x00,0x00,0x00,0x03};
    
    
    private SerialPort puertoSerial;
    private InputStream in;
    private OutputStream out;
    private volatile boolean terminado = false;

    public SonometrolPCE322ASerial(SerialPort puerto) throws IOException, UnsupportedCommOperationException {
        
        this.puertoSerial = puerto;
        puertoSerial.setDTR(true);
        puertoSerial.setDTR(true);
            puertoSerial.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN| SerialPort.FLOWCONTROL_RTSCTS_OUT);
            puertoSerial.setRTS(true);
        this.in = puertoSerial.getInputStream();
        this.out = puertoSerial.getOutputStream();
        
    }

    
    
    
    
    @Override
    public void run(){
        try {
            
            
            byte[] tramaRecibida = enviarRecibirTrama(this.comandoIdentificarse);
            Thread.sleep(2283);
            tramaRecibida = enviarRecibirTrama(this.comandoAuxiliar);
            Thread.sleep(1000);
            tramaRecibida = enviarRecibirTrama(this.comandoIdentificarse);
            Thread.sleep(0);
            tramaRecibida = enviarRecibirTrama(this.comandoIniciarTransmision);
            Thread.sleep(0);
            tramaRecibida = enviarRecibirTrama(this.comandoPedirDatos);
            Thread.sleep(0);
            
            
            while(!terminado){
                
                tramaRecibida = enviarRecibirTrama(this.comandoPedirDatos);
                
                procesarTrama(tramaRecibida);
                Thread.sleep(50);
                
                
            }
            
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(SonometrolPCE322ASerial.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException iexc){
            
        }
        
        
        
        
        
        
    }

    @Override
    public double getDecibeles() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
    private byte[] enviarRecibirTrama(byte[] arregloEnviar) throws IOException{
        
        
         out.write(arregloEnviar);
         System.out.println("Trama enviada: ");

         byte[] buffer = new byte[1024];
         int data = -1;

         int len = 0;
        try {
            Thread.sleep(600);
        } catch (InterruptedException ex) {
            Logger.getLogger(SonometrolPCE322ASerial.class.getName()).log(Level.SEVERE, null, ex);
        }
         while ((data = in.read()) > -1) {
             
                    buffer[len++] = (byte) data;
                    
         }
                if (len > 0) {
                    byte[] arregloRetornar = new byte[len];
                    System.arraycopy(buffer, 0, arregloRetornar, 0, len);
                    System.out.println("Respuesta desde el sonometro recibida");
                    return arregloRetornar;
                } else {
                    System.out.println("No hubo respuesta desde el sonometro");
                    return null;
                }
    }

    private void procesarTrama(byte[] tramaRecibida) {
        
        for(byte b : tramaRecibida){
            
            
            System.out.print(b + " ");
        }
        
        
    }
    
    
}
