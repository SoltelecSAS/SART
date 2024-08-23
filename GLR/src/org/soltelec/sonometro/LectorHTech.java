/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import org.soltelec.util.BufferStrings;

/**
 *Lector para sonometros marca HTech
 * @author Usuario
 */
public class LectorHTech implements Runnable{

    private InputStream in;
    private final SerialPort serialPort;
    private boolean terminado = false;
    private final byte[] buffer = new byte[1024];
    private String s;
    private final BufferStrings bufferStr;

    public LectorHTech(SerialPort puertoSerial,BufferStrings bs) {
        this.serialPort = puertoSerial;
        this.bufferStr = bs;
        
        try{
            this.in = serialPort.getInputStream();
        } catch(IOException ioexc){
        }
    }

    @Override
    public void run() {
        while(!terminado){
            int data;
            int len = 0;

            try{

            while( (data = in.read()) > -1){
                        try{
                        if(data =='\r')
                            break;
                        buffer[len++] = (byte)data;
                        }catch(ArrayIndexOutOfBoundsException ae){
                            len--;
                            break;
                        }
            }//end while in.read()
            s = new String(buffer,0,len);
            synchronized(bufferStr){
                 if(!s.isEmpty() && !(s.length() == 1) ){//Optimismo de que no hayan rpms menores a 10 de hecho no las hay usando el capelec
                    System.out.println(s);
                    bufferStr.addStr(s);
                    bufferStr.notify();
                 }//end if
            }//end of synchronized
            }//end try
            catch(IOException ioe){
                System.out.println("#%%%#####ERROR LEYENDO IOEXCEPTION");
                ioe.printStackTrace();
            }
        }//end of while terminado
        System.out.println("LEctor tERminado");
        synchronized(bufferStr){
        bufferStr.notify();
        serialPort.close();
        }
    }//end of method run

    public void stop() {
        this.terminado = true;
    }
}
