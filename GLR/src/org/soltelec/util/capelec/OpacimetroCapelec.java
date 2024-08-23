/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.UtilGasesModelo;

/**
 *Clase para implementar los comandos para el opacimetro capelec.
 * @author User
 */
public class OpacimetroCapelec implements Opacimetro{
    
    private SerialPort puertoSerial;
    
      
    /**
     * Envia un mensaje de comando al opacimetro capelec y recibe un mensaje de respuesta del mismo
     * @param mensajeComando
     * @return 
     */
    private synchronized  MensajeOpacidadCapelec enviarMensaje(MensajeOpacidadCapelec mensajeComando){
        
        OutputStream out = null;
        InputStream in = null;
        //puertoSerial.enableReceiveTimeout(500);
        byte[] b = UtilGasesCapelec.armarTramaOpacidad( mensajeComando );
        try {
         //Enviar la trama del puerto serial   ojo con el tamanio del buffer que puede er insuficiente
         out = puertoSerial.getOutputStream();
         out.write(b);
         
        
        } catch (IOException ioexc){
            Logger.getRootLogger().error("Error emviando el comando", ioexc);
            ioexc.printStackTrace();
            return null;
        }
        //leer la trama del puerto serial
        
        try{
            
            in = puertoSerial.getInputStream();
            System.out.println("Leyendo datos..-..");
                byte[] buffer = new byte[1024];
                int data = 0;
                int len = 0;
                while ( ( data = in.read() ) > -1 )
                  {
                    buffer[len++] = (byte)data;
                  }//end while          
                byte arreglo[] = new byte[len];
                //armar el mensaje
                System.arraycopy(buffer, 0, arreglo, 0, len);
                return UtilGasesCapelec.armarMensajeOpacidadCapelec( arreglo );
            
        }catch(IOException ioexc){
            System.out.println("Error leyendo los datos desde el puerto serial");
            ioexc.printStackTrace();
            Logger.getRootLogger().error("Error leyendo respuesta desde el puerto serial", ioexc);
            return null;
        }        
        
        
        
        
    }
    
    
    
    

    @Override
    public void armarLCS() {
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('a');
        MensajeOpacidadCapelec enviarMensaje = enviarMensaje(mensaje);//TODO manejar la respuesta del opacimetro;
        
    }

    @Override
    public void calibrar() {//para el caso calibrar tambien es enviar el comando de cero
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('l');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);//TODO manejar la respuesta del opacimetro
        
    }

    @Override
    public byte[] datosRTTrigger(int indiceInicial, int indiceFinal) {
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec((byte)0x8a,new byte[]{(byte)indiceInicial,(byte)indiceFinal});
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        //si la respuesta es NAK 
        if(mensajeRespuesta.getComando() == 0x15 && mensajeRespuesta.getChecksum() == 0xEB){
            
            Logger.getLogger( this.getClass()).warn("Datos de opacidad no disponibles");
            return new byte[0];//es mas logico esto.
        } else {
        // si los datos se pueden obtener
        byte[] trama = mensaje.getDatos();
        
        
            return trama;
        }//end of else       
    }

    @Override
    public void desarmarTrigger() {
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('q');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);//TODO manejar la respuesta del opacimetro
        
        
    }

    @Override
    public void dispararTrigger() {
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('t');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);//TODO manejar la respuesta del opacimetro
        
    }

    @Override
    public SerialPort getPort() {
        
        return this.puertoSerial;
        
    }

    @Override
    public int numeroDatosAdquiridos() {
        //se parece mucho al comando del capelec por eso lo voy a dejar aqui
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('w');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje( mensaje );
        byte[] datos = new byte[2];
        
        datos = mensajeRespuesta.getDatos();
        short numeroDeDatos = UtilGasesModelo.fromBytesToShort(datos[1], datos[0]);
        
        return numeroDeDatos;
        
        
    }

    @Override
    public byte[] obtenerArregloDatosOpacidad() {
         
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec( 'o');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje( mensaje );
        return mensajeRespuesta.getDatos();
        
        
    }

    @Override
    public MedicionOpacidad obtenerDatos() {//
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('u',new byte[0]);
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        MedicionOpacidad med = armarMedicionOpacidad( mensajeRespuesta.getDatos() );
        return med;
        
        
    }

    @Override
    public int obtenerDatosBruto() {
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec( (byte) 0x8b , new byte[0] );
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        if(mensaje.getDatos() != null){
            
        int opacidad = UtilGasesModelo.fromBytesToShort(mensajeRespuesta.getDatos()[1], mensajeRespuesta.getDatos()[0]);           
        return opacidad;
        }
        else return -1;
        
    }

    @Override
    public MedicionOpacidad obtenerDatosEstatusBruto() {//es un pequenio remiendo porque el banco capelec no funciona igual
        
        MedicionOpacidad medicion = obtenerDatos();
        int opacidadBruto = obtenerDatosBruto();
        medicion.setOpacidad((short)opacidadBruto);
        return medicion;
        
    }

    @Override
    public int obtenerFrecuenciaMuestreo() {
        return 20;
    }

    @Override
    public int[] obtenerInfoSoftware() {
        
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('v');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        byte[] datos = mensajeRespuesta.getDatos();
        
        int[] info = new int[3];
        
        byte[] temp = new byte[]{ datos[1],datos[0] };
        int numeroVersion = UtilGasesModelo.fromBytesToShort(temp);        
        int numeroSerial = UtilGasesModelo.fromBytesToShort(datos[3], datos[2]);
        info[1] = numeroSerial;
        info[0] = numeroVersion;        
        return info;
        
    }

    @Override
    public void setPort(SerialPort port) {
       this.puertoSerial = port;
        
    }
    
    /**
     * Metodo de prueba para revisar si se puede desactivar el filtrado por 
     * opacidad. Escribe los valores de filtro del ejemplo pero desactiva todo
     */
    
    private void desactivarFiltrado(){
        
        byte byteEscribir = 0; //pone que no se haga ningun fultrado y cero numero de polos
        
        
        
        
    }
    
    
    /**
     * Metodo para encencer el ventilador o apagarlo 
     * 
     * @param OnOff 
     */
    public void encenderVentilador(boolean onOff){
       byte byteOnOff = (byte) (onOff ? 1 : 0);
       MensajeOpacidadCapelec mensajeOpacidadCapelec = new MensajeOpacidadCapelec( 's', byteOnOff );
       MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje( mensajeOpacidadCapelec);//TODO manejar la respuesta del opacimetro
    }
    
    /**
     * Arma la medicion de opacidad segun los datos que se envien con la trama
     * @param trama 
     */
    private MedicionOpacidad armarMedicionOpacidad( byte[] trama){
        
        MedicionOpacidad medicion = new MedicionOpacidad();
        
        //la opacidad son los dos primeros bytes y esta multiplicada por 10
        short opacidad = UtilGasesModelo.fromBytesToShort(trama[1], trama[0]);
        medicion.setOpacidad( opacidad );
        //la temperatura del gas seria el byte numero 2
        medicion.setTemperaturaGas( trama[2] );
        //la temperatura del tubo seria el byte 3
        medicion.setTemperaturaTubo( trama[3] );
        //la parte de los bits de estado
        short status = UtilGasesModelo.fromBytesToShort(trama[5], trama[4]);
        boolean[] banderas = UtilGasesModelo.shortToFlags(status);
        
        medicion.setTempAmbFueraTolerancia( banderas[0]);
        medicion.setTempDetectFueraTolerancia( banderas[1] );
        medicion.setTempTuboFueraTolerancia( banderas[2] );
        medicion.setVoltLineaFueraTolerancia( banderas[3] );
        medicion.setVentiladorOn( banderas[4] );
        medicion.setRealTimeDataNotReady( banderas[6] );
        medicion.setStandBy( banderas[7] );
        
        //segunda palabra
        medicion.setZeroEnProgreso( banderas[ 8 ]);
        medicion.setLimpiarVentanas( banderas[9] );
        medicion.setRecoleccionDisparada( banderas[11] );
        medicion.setArmadoRecoleccion( banderas[10] );
        medicion.setTriggerActivado( banderas[11] );
        medicion.setTemperaturaGasBaja( banderas[13] );
        medicion.setTermistorDañado( banderas[15]);
        
        return medicion;
               
    }
    
    
    /**
     * documentacion del manual:
     * 
     * El valor por defecto al inicio es de 100%, si l = 50 entonces la intensidad de la luz
     * sera de 50%, esta rutina permite llevar a cabo un procedimiento de control de desplazamiento de la medida;
     * 
     * La intensidad de la fuente de opacidad puede configurarse hasta a 32 posiciones entre 0 y 100%.
     * 
     * 
     * Dando la opacidad que se quiere con este comando el opacimetro establece la opacidad a la posicion mas
     * cercana, por esta via es posible no solamente verificar la desviacion del 100% sino tambien la desviacion de la linealidad
     * en todo el rango.
     * 
     * @param intensidad
     * @param leerEscribir true para leer
     * @return 
     */
    
    public int intensidadFuenteLuz(int intensidad, boolean leerEscribir){
        
        byte leerEscribirbyte =  (byte) (leerEscribir ? -128 : 0) ;
        
        byte[] datos = new byte[2];
        datos[0] = leerEscribirbyte;
        datos[1] = (byte)intensidad;
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('c', datos);
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        
        int intensidadResultante = 0;
        if(leerEscribir){
            
            datos = mensajeRespuesta.getDatos();
            intensidadResultante = datos[1];
        } else {
            
            intensidadResultante = intensidad;
        }
        
        return intensidadResultante;
        
    }//end of intensidadFuenteLuz 
    
    
    /**
     * Metodo para leer los valores de las constantes de los filtros
     * 
     * 
     * 
     */
    
    public byte[] leerInformacionFiltro(){
        
        //byte s = (byte) 0x92;
        byte s = (byte)0xA2;
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('e',new byte[]{s});
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        
        byte[] datos = mensajeRespuesta.getDatos();
        
        return datos;
    }
    
    
    /**
     * 
     * @param info 
     */
    public void escribirInformacionFiltro(byte[] info){
        
       MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('e', info);
       
       
        
       MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
       
       
    }

    @Override
    public long obtenerSerial() {
        MensajeOpacidadCapelec mensaje = new MensajeOpacidadCapelec('v');
        MensajeOpacidadCapelec mensajeRespuesta = enviarMensaje(mensaje);
        byte[] datos = mensajeRespuesta.getDatos();
        
        int[] info = new int[3];
        
        byte[] temp = new byte[]{ datos[1],datos[0] };
        int numeroVersion = UtilGasesModelo.fromBytesToShort(temp);        
        int numeroSerial = UtilGasesModelo.fromBytesToShort(datos[3], datos[2]);
        info[1] = numeroSerial;
        info[0] = numeroVersion;        
        return numeroSerial;
    }
    
    
    
    
}
