package org.soltelec.medicionrpm;

import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CancellationException;
import javax.swing.JTextArea;
import javax.swing.Timer;
import org.soltelec.util.BufferStrings;




//Productor
public class ProductorCapelec implements  Runnable {//Utilizar un wrapper porque se esta quedando pegado
       private InputStream in;//entrada del puerto serial
       private JTextArea textArea;
       private Radial radial;
       private Linear linearT;
       private final BufferStrings bufferStr;
       private boolean leer = true;
       private boolean terminado = false;
       private boolean interrumpido = false;
       private byte[] buffer = new byte[1024];
       String s;
       Timer timer;

       
       public ProductorCapelec(InputStream theIn, BufferStrings theBuffer){//????liberación recursos no me convence bien
         this.in = theIn;
         this.bufferStr = theBuffer;
       }//end of constructor

       public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public boolean isInterrumpido() {
        return interrumpido;
    }

    public void setInterrumpido(boolean interrumpido) {
        this.interrumpido = interrumpido;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public void setLinearT(Linear linearT) {
        this.linearT = linearT;
    }

    public void setRadial(Radial radial) {
        this.radial = radial;
    }

    @Override
    public void run() {
        boolean concatenar = false;
        String santerior ="";
        while(!terminado){
        int data;
            try
            {
                 
                if (leer){
                    int len = 0;
                    while( (data = in.read()) > -1){
                        try{
                        if(data =='\r') {
                                break;
                            }
                        buffer[len++] = (byte)data;
                        }catch(ArrayIndexOutOfBoundsException ae){
                            len--;
                            break;
                        }
                    }//end while
                 if(len <= 1024) {
                        s = new String(buffer,0,len);
                    }
                    else {
                        s="";
                    }//es la cadena leida
                 synchronized(bufferStr){//existe un consumidor esperando sobre bufferString que cuando se hace notify lee
                     //la cadena y la procesa
                 if(!s.isEmpty() && !(s.length() == 1) && !concatenar){//Optimismo de que no hayan rpms menores a 10
                     System.out.println(s);
                     bufferStr.addStr(s);
                     bufferStr.notify();
                 }
                 if(concatenar){///problema cuando envia un 1 despues las tres siguientes cifras... no debe enviar nunca menos de dos digitos
                    concatenar = false;
                    s = santerior + s;
                    System.out.println("Concatenada = " + s);
                    bufferStr.addStr(s);
                    bufferStr.notify();
                    s = "";
                 }else if(s.length() == 1){//cuando es 0 paila
                    santerior = s;
                    concatenar = true;
                    System.out.println("Concatenar");
                 }
                 }//end synchronized
                }
                Thread.sleep(10);
            }
            catch ( IOException e )
            {
                System.exit(-1);
            }
            catch(InterruptedException ie){
                throw new CancellationException("Hilo Interrumpido");

            }
        }//while terminado
        System.out.println("Hilo Productor Terminado");
        synchronized(bufferStr){
            bufferStr.addStr("");
            bufferStr.notify();
        }
       // timer.stop();
    }//end of serialEvent
   }//end of class LectorPuerto
