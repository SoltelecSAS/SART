/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.horiba;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.PuntoCalibracion;
import org.soltelec.util.EstadoMaquina;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.UtilGasesModelo;
import org.soltelec.util.capelec.BancoCapelec.TipoCalibracion;

/**
 *
 * @author User
 */
public class BancoHoriba implements BancoGasolina{
    
    private SerialPort serialPort;
    private InputStream in;
    private OutputStream out;


    @Override
    public MedicionGases obtenerDatos() {
        
        
        byte[] trama = enviarTramaRetardo(MensajeHoriba.tramaDatos);
        MedicionGases medicion = armarMedicion(trama);
        byte[] tramaEstado = enviarTrama(MensajeHoriba.tramaStatusAnalizador);
        byte[] tramaEstadoSistema = enviarTrama(MensajeHoriba.tramaStatusSistema);
        MedicionGases medicionYEstatus = adicionarStatus(medicion, tramaEstado,tramaEstadoSistema);
        return medicionYEstatus;
        
        
    }

    @Override
    public String obtenerPEF() {
        
        String pef = "";
        try {
            
            byte[] tramaRecibida = enviarTrama(MensajeHoriba.tramaPEF);
            int numeroPef = UtilGasesModelo.fromBytesToShort(tramaRecibida[4], tramaRecibida[3]);
            pef = String.valueOf(numeroPef);
            
            
        } catch (Exception e) {
        }
         return pef;       
        
    }

    @Override
    public String numeroSerial() {
        String serial = null;
        byte[] tramaRecibida = enviarTrama(MensajeHoriba.tramaSerial);
        try{
            
            byte[] arregloSerial = new byte[6];
            System.arraycopy(tramaRecibida, 3, arregloSerial, 0, 6);
            serial = new String(arregloSerial);
            
            
            
        }catch(Exception exc){
            System.out.println("Error leyendo el serial de la maquina");
            
        }
        return serial;
        
    }

    @Override
    public String encenderBombaMuestras(boolean onOff) {
        
        byte[] tramaBomba = onOff? MensajeHoriba.tramaBombaOn: MensajeHoriba.tramaBombaOff;
        enviarTrama(tramaBomba);
        return onOff? "Bomba on":"Bomba off";
        
    }

    @Override
    public String encenderSolenoideUno(boolean onOff) {
        
        byte[] tramaSolenoide = onOff ? MensajeHoriba.tramaSolenoideUnoOn : MensajeHoriba.tramaSolenoideUnoOff;
        enviarTrama(tramaSolenoide);
        return onOff ? "Solenoide Uno ON": "Solenoide Uno OFF";
        
    }

    @Override
    public String encenderSolenoide2(boolean onOff) {
       byte[] trama = onOff ? MensajeHoriba.tramaSonenoideDosOn : MensajeHoriba.tramaSolenoideDosOff ;
       enviarTrama(trama);
       return onOff ? "Solenoide Dos On" : "Solenoide Dos off";
    }

    @Override
    public String encenderCalSol1(boolean onOff) {
        //Seria el solenoide 3 en el software de horiba
        byte[] trama = onOff ? MensajeHoriba.tramaCalSol1On: MensajeHoriba.tramaCalSolOff;
        enviarTrama(trama);
        return onOff ? "CalSol1 On": "CalSol2 off";
           
    }

    @Override
    public String encenderCalSol2(boolean onOff) {
        
        byte[] trama = onOff ? MensajeHoriba.tramaCalSol2On:MensajeHoriba.tramaCalSol2Off;
        enviarTrama(trama);
        return onOff ? "CalSol2 On": "CalSol2 Off";
        
    }

    @Override
    public String encenderDrainPump(boolean onOff) {
        
        byte[] trama = onOff ? MensajeHoriba.tramaDrainPumpOn: MensajeHoriba.tramaDrainPumpOff;
        enviarTrama(trama);
        return onOff ? "Drain Pump On":"Drain Pump Off";
    }

    @Override
    public EstadoMaquina statusMaquina() {
        
        byte[] tramaMaquina = enviarTrama(MensajeHoriba.tramaStatusAnalizador);
        byte[] tramaSistema = enviarTrama(MensajeHoriba.tramaStatusSistema);
        EstadoMaquina estado = armarEstado(tramaMaquina,tramaSistema);
        return estado;
    }

    @Override
    public short mapaFisicoES(short palabra, boolean leerEscribir) {
        
        //escribir no esta soportado;
        byte[] tramaRecibida = enviarTrama(MensajeHoriba.tramaStatusSistema);
        return tramaRecibida[7];   
        
    }

    @Override
    public void ordenarCalibracion(int modoCalibracion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void leerEscribirDatosCal(boolean leerEscribir, PuntoCalibracion ptoCal, int dataset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PuntoCalibracion leerDatosCalibracionPto1(int dataset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SerialPort getPuertoSerial() {
        return this.serialPort;
    }

    @Override
    public void setPuertoSerial(SerialPort serialPort) throws IOException {
        this.serialPort = serialPort;
        this.out =serialPort.getOutputStream();
        this.in = serialPort.getInputStream();
    }

    @Override
    public void calibracion(TipoCalibracion tipoCalibracion, double valorCO, double valorCO2, int valorHC) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    public synchronized byte[] enviarTrama(byte[] trama){
       
        
        boolean respuestaRecibida = false;
        byte b[] = trama;
        try{
            out.write(b);//Enviar el comando por el puerto serial         
        }catch(IOException ioe){
            //System.out.println("Error enviando el comando: " + comando.toString());
            ioe.printStackTrace();
        }   
            
            while( respuestaRecibida == false){
                System.out.println("Leyendo datos..-..");
                byte[] buffer = new byte[1024];
                int data = -1;
                           try
                            {
                               int len = 0;
                                while ( (data = in.read() ) > -1 )
                                {
                                   buffer[len++] = (byte)data;
                                   //data = in.read(bleer);//modificacion para que funcione en linux
                                   //System.out.println("Bloqueado aqui");
                                }//end while
                                if(len > 0) {//si hay datos
                                    System.out.println("Longitud del mensaje:" + len);
                                    respuestaRecibida = true;
                                    //copiar a un arreglo para armar el mensaje
                                    byte arreglo[] = new byte[len];
                                    //armar el mensaje
                                    System.arraycopy(buffer, 0, arreglo, 0, len);
                                            //Thread.sleep(25);//sincronia
                    
                                   return arreglo;
                                }//
                                else {
                                    System.out.println("No se ha recibido respuesta del banco de gases");
                                    respuestaRecibida = true;
                                }
                            }
                            catch ( IOException e )
                            {
                            
                            }//end catch
            }//end while

            //no termino en el tiempo esperado entonces retornar null indicando error
            System.out.println("La máquina no envió respuesta en el tiempo intermensaje");
        
    return null;
    }//end of method

    
    public synchronized byte[] enviarTramaRetardo(byte[] trama){
       
        
        boolean respuestaRecibida = false;
        byte b[] = trama;
        try{
            out.write(b);//Enviar el comando por el puerto serial          
        }catch(IOException ioe){
            ioe.printStackTrace();
        }   
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoHoriba.class.getName()).log(Level.SEVERE, null, ex);
        }
        int contador = 0;
            while( contador < 3){
                System.out.println("Leyendo datos..-..");
                byte[] buffer = new byte[1024];
                int data = -1;
                           try
                            {
                               int len = 0;
                                while ( (data = in.read() ) > -1 )
                                {
                                   buffer[len++] = (byte)data;
                                   //data = in.read(bleer);//modificacion para que funcione en linux
                                   //System.out.println("Bloqueado aqui");
                                }//end while
                                if(len > 0) {//si hay datos
                                    System.out.println("Longitud del mensaje:" + len);
                                    respuestaRecibida = true;
                                    //copiar a un arreglo para armar el mensaje
                                    byte arreglo[] = new byte[len];
                                    //armar el mensaje
                                    System.arraycopy(buffer, 0, arreglo, 0, len);
                                            //Thread.sleep(25);//sincronia
                    
                                   return arreglo;
                                }//
                                else {
                                    System.out.println("No se ha recibido respuesta del banco de gases");
                                   contador++;
                                }
                            }
                            catch ( IOException e )
                            {
                            e.printStackTrace();
                            }//end catch
            }//end while
            System.out.println("Contador:" + contador);
            //no termino en el tiempo esperado entonces retornar null indicando error
            System.out.println("La máquina no envió respuesta en el tiempo intermensaje");
        
    return null;
    }//end of method
    
    
   
    
    private MedicionGases armarMedicion(byte[] trama) {
        
        MedicionGases medicion = new MedicionGases();
        medicion.setValorHC( UtilGasesModelo.fromBytesToShort(trama[9], trama[8]));
        medicion.setValorCO( UtilGasesModelo.fromBytesToShort(trama[7], trama[6]));
        medicion.setValorCO2(UtilGasesModelo.fromBytesToShort(trama[5], trama[4]));
        medicion.setValorNox(UtilGasesModelo.fromBytesToShort(trama[11],trama[10]));
        medicion.setValorO2(UtilGasesModelo.fromBytesToShort(trama[13], trama[12]));
        medicion.setValorTAmbiente( UtilGasesModelo.fromBytesToShort(trama[17],trama[16]));
        
        return medicion;
        
        
        
        
    }

    private MedicionGases adicionarStatus(MedicionGases medicion, byte[] tramaEstado, byte[] tramaEstadoSistema) {
        
        boolean[] flagsSS = UtilGasesModelo.shortToFlags(tramaEstado[3]);
        boolean[] flagsZS = UtilGasesModelo.shortToFlags(tramaEstado[4]);
        medicion.setCalentamiento(flagsSS[3]);
        medicion.setCero(flagsZS[5]);
        medicion.setCeroEnProgreso(flagsSS[7]);
        
        boolean[] flagsSF1 = UtilGasesModelo.shortToFlags(tramaEstadoSistema[3]);
        boolean[] flagsIO = UtilGasesModelo.shortToFlags(tramaEstadoSistema[7]);
        boolean[] flagsSF3 = UtilGasesModelo.shortToFlags(tramaEstadoSistema[5]);
       
        medicion.setBajoFlujo( flagsSF1[5]);
        medicion.setPruebaFugasAprobada( flagsSF3[1]);
        medicion.setBombaEncendida(flagsIO[6]);
        return medicion;     
        
    }

    private EstadoMaquina armarEstado(byte[] tramaMaquina, byte[] tramaSistema) {
        EstadoMaquina estado = new EstadoMaquina();
        
        boolean[] flagsSS = UtilGasesModelo.shortToFlags(tramaMaquina[3]);
        boolean[] flagsZS = UtilGasesModelo.shortToFlags(tramaMaquina[4]);
        estado.setCalentamiento(flagsSS[3] );
       
        estado.setZero(flagsZS[5]);
        estado.setZeroEnProgreso(flagsSS[7]);
        
        boolean[] flagsSF1 = UtilGasesModelo.shortToFlags(tramaSistema[3]);
        boolean[] flagsSF3 = UtilGasesModelo.shortToFlags(tramaSistema[5]);
       
        estado.setBajoFlujo( flagsSF1[5]);
        estado.setPruebaFugasAprobada( flagsSF3[1]);        
        estado.setContadorCalentamiento(0); 
       
        return estado;     
        
    }

    public void llamarRutinaCero() {
        
        enviarTrama(MensajeHoriba.tramaCero);
        
        
    }

    @Override
    public void setFactorRPM(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
