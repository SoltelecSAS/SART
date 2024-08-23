/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro.chino;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.soltelec.luxometro.gemini.MedidaLuxometro;

/**
 *
 * @author User
 */
public class LuxometroChino {
    
    
    private SerialPort puertoSerial;
    private OutputStream out;
    private InputStream in;
    
    private final byte[] tramaComando = { 49, 56, 26 };
    
    public MedidaLuxometroChino procesarTrama(byte[] tramaRecibida){       
        
        System.out.println( "Longitud Recibida:" + tramaRecibida.length );
        
        byte[] tramaAngulo = new byte[4];
        int j = 0;
        for(int i = 2; i<6;i++){
        
            tramaAngulo[j]= tramaRecibida[i];
            j++;
        }
        
        j=0;
        byte[] tramaLuminosidad = new byte[3];
        for(int i = 6; i < 9;i++){
            tramaLuminosidad[j] = tramaRecibida[i];
            j++;
        }
        
        double porcentaje = Double.parseDouble(new String(tramaAngulo));
        
        porcentaje = porcentaje*0.01;
        
        double luminosidad = Double.parseDouble(new String(tramaLuminosidad));
        
        luminosidad = luminosidad * 0.1;
        
        MedidaLuxometroChino medidaLuxometro = new MedidaLuxometroChino();
        medidaLuxometro.setLuminosidad(luminosidad);
        medidaLuxometro.setPorcentajeInclinacion(porcentaje);
       
        
        return medidaLuxometro;
        
    }

    public SerialPort getPuertoSerial() {
        return puertoSerial;
    }

    public void setPuertoSerial(SerialPort puertoSerial) throws IOException {
        this.puertoSerial = puertoSerial;
        this.out = puertoSerial.getOutputStream();
        this.in = puertoSerial.getInputStream();
    }
    
    
    
    public byte[] enviarRecibirBytes(byte arregloEnviar[]) throws IOException{

            out.write(arregloEnviar);
            System.out.println("Trama enviada: ");
            for(byte b: arregloEnviar)
                System.out.print(b+" ");
            //Leer la respuesta que envia el TB6000
            byte[] buffer = new byte[1024];
            int data = -1;

                int len = 0;
                int numeroIntentos = 0;
                while (numeroIntentos < 3){
                    while ((data = in.read()) > -1) {
                        buffer[len++] = (byte) data;
                    }
                    if (len > 0) {
                        byte[] arregloRetornar = new byte[len];
                        System.arraycopy(buffer, 0, arregloRetornar, 0, len);
                        System.out.println("");
                        String str = new String(arregloRetornar);
                        System.out.println(str);
                        for(short s: arregloRetornar)
                            System.out.print(s +" ");
                        return arregloRetornar;
                    } else {
                        System.out.println("No hubo respuesta desde el dispositivo");
                        //return null;
                        numeroIntentos++;
                        System.out.println("Numero Intentos: " + numeroIntentos++);
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ex) {
                            
                        }

                    }
                }//end while intentos
                return null;

    }
    
    
    public MedidaLuxometroChino obtenerMedicion() throws IOException{
        
        byte[] tramaRecibida = enviarRecibirBytes(tramaComando);
        MedidaLuxometroChino medida = procesarTrama(tramaRecibida);
        return medida;
        
    }
    
}
