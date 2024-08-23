/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import gnu.io.CommPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que proporciona los servicios para comunicarse y configurar
 * un sensor de rpms marca capelec modelso CAP8500-CAP8510
 * KISS --> sin eventos
 * COMPLEX ---> Eventos
 * Debe inyectarse el puerto serial para obedecer al low coupling
 * @author Usuario
 */
public class CapelecSerial {//implements MedidorRPM
    //Usa un puerto serial
    //Tiene un conjunto de comandos
    //constantes para comandos que no necesitan parametros
    public static final int ID = 0;
    public static final int RESET = 1;
    public static final int PROGRAM_STOP = 2;
    public static final int RUN_PROGRAM = 3;
    public static final int BATERIA = 3;
    public static final int VIBRACION = 2;

    private CommPort puertoSerial;//el puerto serial

    private OutputStream out;//flujos de salida del puerto
    private InputStream in;//flujos de entrada del puerto

    private int rpm;
    private int temperaturaAceite;

    public CapelecSerial(){

    }//end of empty constructor

    /**
     * Metodo para manejar la sincronia
     * envia una trama y espera la respuesta
     * en forma de arreglo de bytes
     * no debe accederse por mas de un hilo a la vez
     * @param bytes
     * @return 
     * @throws java.io.IOException
     */
    public synchronized byte[] enviarEsperarTrama( byte[] bytes) throws IOException{
        boolean respuestaRecibida = false;
        out.write(bytes);
        System.out.println("Trama enviada");
//        while( respuestaRecibida == false){
//                System.out.println("Leyendo datos..-..");
//                byte[] buffer = new byte[1024];
//                        int data;
//                           try
//                            {
//                                int len = 0;
//                                while ( ( data = in.read()) > -1 )
//                                {
//                                   buffer[len++] = (byte)data;
//                                   //System.out.println("Bloqueado aqui");
//                                }//end while
//                                if(len > 0) {//si hay datos
//                                    System.out.println("Longitud del mensaje:" + len);
//                                    respuestaRecibida = true;
//                                    //copiar a un arreglo para armar el mensaje
//                                    byte arreglo[] = new byte[len];
//                                    //armar el mensaje
//                                    System.arraycopy(buffer, 0, arreglo, 0, len);
//                                    return arreglo;
//                                }//end if else
//
//                                else {
//                                    System.out.println("Len: " + len);
//                                    respuestaRecibida = true;
//                                }
//                            }
//                            catch ( IOException e )
//                            {
//                            e.printStackTrace();
//                            System.exit(-1);
//                            }//end catch
//
//            }//end while

            //no termino en el tiempo esperado entonces retornar null indicando error
            System.out.println("La máquina no envió respuesta en el tiempo intermensaje");
            return null;
    } //end of method

    public void manejadorRespuesta(byte[] b){//rutina para identificar la respuesta a un comando

        try {
            //rutina para identificar la respuesta a un comando
            String s = new String(b, "US_ASCII");
            if(s.charAt(0) == '='){
              System.out.println("Comando Recibido OK ACK");
            } else if(s.charAt(0) == '!' ){
              System.out.println("Comando mal Recibido o NACK");
            } else {//supongo que estara enviando una medicion
                if(s.charAt(0) =='T'){//valor de temperatura
                    String strTemperatura = s.substring(1, 3);
                    int valorTemperatura = Integer.parseInt(strTemperatura);
                    //this.temperaturaAceite = valorTemperatura;
                   System.out.println("Lectura de temperatura la temperatura:" + valorTemperatura);
                }//fin del if de temperatura
                else {
                    String strRPM = s.substring(0,3);
                    int valorRPM = Integer.parseInt(strRPM);
                    //this.rpm = valorRPM;
                    System.out.println("Lectura de rpms" + valorRPM);
                }//final de if de medicion
            }//final de mirar si la medicion es de temperatura o de rpm

        }catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CapelecSerial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of method


    public void enviarComandoNoParametros(int comando) {//envia comandos que no tienen parametros

        String strComando ;
        byte[] b;
        try {
        switch (comando){
            case CapelecSerial.ID :
                strComando = armarComando("PR");
                break;
            case CapelecSerial.PROGRAM_STOP:
                strComando = armarComando("PS");
            break;
            case CapelecSerial.RESET:
                byte bReset[] = {35,82,83,13};
                strComando = armarComando("RS");
                System.out.println(strComando);
                out.write(bReset);
                return;
            case CapelecSerial.RUN_PROGRAM:
                strComando = armarComando("PR");
            break;
            default:
                System.err.println("Comando no soportado por la maquina");
            return;//no trate de escribir null
        }//end switch
            b = strComando.getBytes("ASCII");
            //out.flush();
            out.write(b);//kiss no espero respuesta
        }
        catch (UnsupportedEncodingException uee){
            uee.printStackTrace(System.err);
        } catch(IOException ioe){
            System.err.println("Error escribiendo comando");
            ioe.printStackTrace(System.err);
        }
    }//end of method
    /*
     * Metodo para cambiar el numero de cilindros en la medicion
     */
    public void cambiarCilindros(int numCilindros){
        try {
            byte[] b;
            StringBuilder strComando = new StringBuilder("CY");
            String strCilindros = String.valueOf(numCilindros);
            if (strCilindros.length() < 2) {
                //ponerle un cero
                strCilindros = "0" + strCilindros;
            } //end if
            strComando.append(strCilindros);
            String comandoArmado = armarComando(strComando.toString());
            b = comandoArmado.getBytes("ASCII");
            out.write(b);
            System.out.print(new String(b));
        } //end of method
        catch (UnsupportedEncodingException ex) {
            System.out.println("No soporta ASCII");
        }//end catch
        catch(IOException ioe){
            System.out.println("Error escribiendo el comando de cambiar cilindros");
        }//end catch
    }//end of method

    /**
     * metodo para enviar el comando PL
     * @param n toma valores 3 es para medicion con tension de bateria y 2 es para medicion
     * usando vibracion
     */
    public void enviarProgramLoad(int n){
        try {
            byte[] b;
            String strComando = "PL" + String.valueOf(n);
            String comandoArmado = armarComando(strComando);
            b = comandoArmado.getBytes("ASCII");
            out.write(b);
        } //end of enviarProgramLoad
        catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CapelecSerial.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe){
            System.err.println("Error de I/O enviando comando Program Load");
            ioe.printStackTrace(System.err);
        }

    }//end of enviarProgramLoad


    public void medirUsandoVibracion(){
        try {
            //Secuencia de comandos
            enviarComandoNoParametros(CapelecSerial.PROGRAM_STOP); //ojo sin asincronia
            //#PS
            Thread.sleep(100);
            enviarProgramLoad(CapelecSerial.VIBRACION);
            //#PL3
            Thread.sleep(100);
            enviarComandoNoParametros(CapelecSerial.RUN_PROGRAM);
            //#PR
            Thread.sleep(100);
        } //end of medirUsandoVibracion
        catch (InterruptedException ex) {
            Logger.getLogger(CapelecSerial.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of medirUsandoVibracion

    public void medirUsandoBateria(){
        try {
            enviarComandoNoParametros(CapelecSerial.RESET);
            Thread.sleep(100);
            ///#PS
            //        Paso 1 : #PS
//            enviarComandoNoParametros(CapelecSerial.PROGRAM_STOP);
//            Thread.sleep(100);
//            //Paso 2 : #PL2
            enviarProgramLoad(CapelecSerial.BATERIA);
            Thread.sleep(100);
//            //Paso 3 : #PR
            enviarComandoNoParametros(CapelecSerial.RUN_PROGRAM);
            Thread.sleep(100);
            //#PL2
            //#PR
        } //end of medirUsandoBateria
        catch (InterruptedException ex) {
            Logger.getLogger(CapelecSerial.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//end of medirUsandoBateria

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public CommPort getPuertoSerial() {
        return puertoSerial;
    }

    public void setPuertoSerial(CommPort puertoSerial) {
        try {
            this.puertoSerial = puertoSerial;
            this.out = puertoSerial.getOutputStream();
            this.in = puertoSerial.getInputStream();
        } catch (IOException ex) {
            System.err.println("Error estableciendo los flujos del puerto Serial");
        }
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public int getTemperaturaAceite() {
        return temperaturaAceite;
    }

    public void setTemperaturaAceite(int temperaturaAceite) {
        this.temperaturaAceite = temperaturaAceite;
    }



    private static String armarComando(String c){
        return "#" + c + "\r"; //primer punto de error si no se interpreta \r como 0x0d
    }//end of armaComando



    public class SerialReader implements Runnable //un hilo estatico???
    {
        InputStream in;

        public SerialReader ( InputStream in )
        {
            this.in = in;
        }

        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

   
}//end of class CapelecSerial
