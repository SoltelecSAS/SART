/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import org.apache.log4j.Logger;
import org.soltelec.procesosbanco.BancoGasolina;
import org.soltelec.procesosbanco.PuntoCalibracion;
import org.soltelec.util.EstadoMaquina;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.UtilGasesModelo;
import static org.soltelec.util.UtilGasesModelo.*;
import org.soltelec.medicionrpm.MedidorRevTemp;

/**
 *Clase que implementa los comandos para el funcionamiento
 * del Banco Capelec
 * 
 * @author User
 */
public class BancoCapelec implements BancoGasolina,MedidorRevTemp {
    
    private SerialPort puertoSerial;
    
    //private MedicionGases medicion;
    
    private SelectorMediciones selectorMediciones = SelectorMediciones.RPMTEMP;
    
    private FormatoMediciones formatoMediciones = FormatoMediciones.ENTERO;

    
    
    public BancoCapelec() {
        
    }
    
    
    
    /**
     * Recibe un comando en el formato del capelec, lo convierte
     * a bytes y lo envia por el puerto serial, espera la respuesta
     * desde el banco capelec y la convierte en un comando o mensaje de respuesta
     * @param mensaje
     * @return 
     */
    private synchronized MensajeCapelec enviarMensajeCapelec(MensajeCapelec mensaje){
        
        OutputStream out = null;
        InputStream in = null;
        //puertoSerial.enableReceiveTimeout(500);
        byte[] b = UtilGasesCapelec.armarTrama(mensaje);
        try {
         //Enviar la trama del puerto serial   
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
                return UtilGasesCapelec.armarMensajeCapelec(arreglo);
            
        }catch(IOException ioexc){
            System.out.println("Error leyendo los datos desde el puerto serial");
            ioexc.printStackTrace();
            return null;
        }        
    }//end of method
    
    
    /**
     * Verifica que la respuesta no sea NACK ANSWER
     * @param mensajeOriginal
     * @param mensajeRespuesta
     * @return 
     */
    private boolean isRespuestaNack(MensajeCapelec mensajeOriginal, MensajeCapelec mensajeRespuesta){
       
        if(mensajeOriginal.getLetraComando() != mensajeRespuesta.getLetraComando()){
            System.out.println("La letra de comando original no corresponde con la letra de comando de respuesta");
            return true;
        }else {
            if(mensajeRespuesta.getNumeroDatos() == 1 && (mensajeRespuesta.getDatos())[0] == 0x15){
                return true;
            } else {
                return false;
            }
        }
        
    }//
    
    /**
     * Cambia la velocidad del puerto serial.
     * 
     * La nueva velocidad de comunicacion es aplicaca depues de enviar el comando, recibir una respuesta 
     * positiva, y posteriormente apagar y prender el banco.
     * @param velocidad
     * @return 
     */
    
    public boolean establecerVelocidad(int velocidad){
        
        MensajeCapelec mensajeComando = null;
        switch(velocidad){
            case 9600:
                mensajeComando = new MensajeCapelec('G',0x01,new byte[]{0x00});                 
            break;                
            case 19200:
                mensajeComando = new MensajeCapelec('G',0x01,new byte[]{0x01});
                
            break;            
            default:
                System.out.println("Velocidad no soportada");
                
            return false;    
        }
        
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        
        return isRespuestaNack(mensajeComando, mensajeRespuesta);
        
        
    }//end of method
    
    
      
    /**
     * Metodo para obtener el status o byte de Entradas y salidas
     * @return byte de I/O Status
     */
    private byte obtenerIOStatus(){
        //manda un comando para obtener las medidas y el estatus en formato entero, obtiene las mediciones
        MensajeCapelec mensajeComando = new MensajeCapelec(BancoCapelec.FormatoMediciones.ENTERO.getLetraComando(), 0x01,new byte[]{BancoCapelec.SelectorMediciones.RPMTEMP.getValor()}/*0x20*/ );
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        //puedo entonces traer el byte con la informacion conocida
        byte[] tramaRecibida = mensajeRespuesta.getDatos();
        return tramaRecibida[19];
        
    }//end of method
    
    
    
    
   
    
    /**
     * Para el banco capelec existen tres formatos de salida : texto, entero y float
     * 
     * pero tambien existen 4 tipos distintos para seleccionar los datos que se quieran leer
     * 
     * por lo tanto segun la variable FormatoMediciones se retornaran las medidas 
     * 
     * en formato texto, entero o float , y segun la variable SelectorMedidas se 
     * 
     * retornaran las correspondientes medidas
     * 
     * entonces serian doce.
     * @return 
     */
    @Override
    public MedicionGases obtenerDatos() {
        
        byte[]  datosMensaje = new byte[]{this.selectorMediciones.getValor()};//seria el valor 0x15
        char letraComando = this.formatoMediciones.getLetraComando();
        MensajeCapelec mensajeComando = new MensajeCapelec(letraComando, 0x01,datosMensaje);
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        MedicionGases medicion = procesarMedicion(mensajeRespuesta,formatoMediciones,selectorMediciones);
        return medicion;
    }//end of method obtenerDatos

    @Override
    public String obtenerPEF() {
        
        //para obtener el pef se debe cambiar temporalmente el selector de medidas
        //a OTROS obtener la medicion y volver a poner el selector de medidas en entero o en
        //el que estaba
        //retorna el pef en formato entero
        
        SelectorMediciones selectorActual = this.selectorMediciones;
        this.setSelectorMediciones( BancoCapelec.SelectorMediciones.OTROS);
        MedicionGases medicion = obtenerDatos();
        
        this.setSelectorMediciones( selectorActual);
        
        return String.valueOf( medicion.getPef() );
        
        
    }

    @Override
    public String numeroSerial() {
        
        MensajeCapelec mensajeComando = new MensajeCapelec( 'N',0x01,new byte[]{0x00 });
        
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        
        byte[] datos = mensajeRespuesta.getDatos();
        
        byte[] arrayCaracteresSerial = new byte[5];
        
        System.arraycopy(datos,1,arrayCaracteresSerial,0,5);
        
        String serial = new String(arrayCaracteresSerial);
        
        return serial;
        
        
    }

    @Override
    public String encenderBombaMuestras(boolean onOff) {
        //al enviar este comando directamente el problema es que se apagan las demas
        //salidas
        
        byte ioStatus = obtenerIOStatus();//se tiene que cambiar el bit 7 a 1 para que se prenda la bomba
        boolean[] arrayBits  = UtilGasesModelo.shortToFlags((short)byteToInt(ioStatus));//se pasa el estatus a un arreglo de booleanos en donde true es uno y false es cero
        
        arrayBits[7] = onOff;//se configura el bit correspondiente como true o false
        
        MensajeCapelec mensajeComando = null;
        short retorno = fromFlagsToShort(arrayBits);//se calcula el numero de nuevo a short
        
        byte b = UtilGasesModelo.toBytes(retorno)[0];       //se toma el byte mas bajo
        mensajeComando = new MensajeCapelec('O',0x01,new byte[]{ b} );       //se construye el comando
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        byte ioStatusRespuesta = mensajeRespuesta.getDatos()[0];
        return UtilGasesModelo.shortToString( ioStatusRespuesta);
        
    }

    @Override
    public String encenderSolenoideUno(boolean onOff) {
        byte ioStatus = obtenerIOStatus();
        
        short ioStatusShort = (short)UtilGasesModelo.byteToInt(ioStatus);
        
        boolean[] arrayBits = UtilGasesModelo.shortToFlags(ioStatusShort);
        
        arrayBits[5] = onOff;
        
        short nuevoIoStatus = fromFlagsToShort(arrayBits);
        byte nuevoByteIOStatus = toBytes(nuevoIoStatus)[0];
        MensajeCapelec mensajeComando = null;
        mensajeComando = new MensajeCapelec('O',0x01,new byte[]{ nuevoByteIOStatus});           
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        byte ioStatusRespuesta = mensajeRespuesta.getDatos()[0];
        return shortToString( ioStatusRespuesta );
    }

    @Override
    public String encenderSolenoide2(boolean onOff) {
        //Solenoide dos es el bit 4
        
        byte ioStatus = obtenerIOStatus();
        
        short ioStatusShort = (short)byteToInt(ioStatus);
        
        boolean[] arrayBits = shortToFlags(ioStatusShort);
        
        arrayBits[4] = onOff;
        
        short nuevoIoStatus = fromFlagsToShort(arrayBits);
        
        byte byteNuevoIOstatus = toBytes(nuevoIoStatus)[0];
        
        MensajeCapelec mensajeComando = new MensajeCapelec('O', 0x01,  new byte[]{byteNuevoIOstatus });
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        
        byte ioStatusRespuesta = mensajeRespuesta.getDatos()[0];
        
        return "iostatus byte" + ioStatusRespuesta;
                
    }

    @Override
    public String encenderCalSol1(boolean onOff) {
       return "encenderCalSol1 No soportado por el banco capelec";
    }

    @Override
    public String encenderCalSol2(boolean onOff) {
        return "encenderCalSol2 no soportado por el banco capelec ";
    }

    @Override
    public String encenderDrainPump(boolean on) {
        //encender bomba dos bit 6
        
        byte ioStatus = obtenerIOStatus();
        
        short shortIoStatus = (short)byteToInt(ioStatus);
        
        boolean[] arrayBits = shortToFlags(shortIoStatus);
        
        arrayBits[6] = on;
        
        short nuevoIOStatus = fromFlagsToShort(arrayBits);
        
        byte byteNuevoIOStatus = toBytes(nuevoIOStatus)[0];
        
        MensajeCapelec mensajeComando = new MensajeCapelec( '0', 0x01, new byte[]{byteNuevoIOStatus});
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        
        byte ioStatusNueva = mensajeRespuesta.getDatos()[0];
        
        return "IOStatus nuevo:" + ioStatusNueva;
        
        
    }//end of method 

    @Override
    public EstadoMaquina statusMaquina() {
        
        FormatoMediciones formatoAnterior = this.formatoMediciones;
        
        this.setFormatoMediciones( BancoCapelec.FormatoMediciones.ENTERO);
        
        SelectorMediciones selectorAnterior = this.selectorMediciones;
        
        this.selectorMediciones = BancoCapelec.SelectorMediciones.OTROS;
        
        MensajeCapelec mensajeComando = new MensajeCapelec( formatoMediciones.getLetraComando(),0x01, new byte[]{selectorMediciones.getValor()});
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        
        byte[] datos = mensajeRespuesta.getDatos();
        
        byte errorByte1 = datos[17];
        boolean[] arrayStatus = shortToFlags( errorByte1 );        
        int porcentajeCal = fromBytesToShort(datos[12], datos[11]);
        
        EstadoMaquina estadoMaquina = new EstadoMaquina();
        
        estadoMaquina.setContadorCalentamiento(porcentajeCal);
        
        //cero
        estadoMaquina.setZero( arrayStatus[6] );
        estadoMaquina.setCalentamiento( arrayStatus[5] );
        estadoMaquina.setZeroEnProgreso( arrayStatus[7] );
        estadoMaquina.setCalibracionEnProgreso( arrayStatus[4] );
        estadoMaquina.setCalibracionRequerida( arrayStatus[3] );
        
        this.setFormatoMediciones( formatoAnterior );
        this.setSelectorMediciones( selectorAnterior );
        
        return estadoMaquina;
    }

    @Override
    public short mapaFisicoES(short palabra, boolean leerEscribir) {
        
        if(leerEscribir){//escribir
            
            byte b  = toBytes(palabra)[0];//escoge el byte bajo
            
            MensajeCapelec mensajeComando = new MensajeCapelec('O',0x01,new byte[]{b});
            MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
            
            byte IOStatusRespuesta = mensajeRespuesta.getDatos()[0];
            
            return IOStatusRespuesta;
                   
            
            
        }else {//leer debe ser false
            
           return  obtenerIOStatus();
            
        }
        
        
        
    }//end of method

    @Override
    public void ordenarCalibracion(int modoCalibracion) {//solo se implementa la calibracion de cero
        
        switch(modoCalibracion){
            
            case 0:
                enviarCero();
            break;
            default:
                Logger.getRootLogger().info("Modo de calibracion no soportado en el banco capelec");
            break;    
        }
        
    }

    @Override
    public void leerEscribirDatosCal(boolean leerEscribir, PuntoCalibracion ptoCal, int dataset) {
        Logger.getRootLogger().info("Comando de leerEscribirDatosCal no soportado por Banco capelec");
    }

    @Override
    public PuntoCalibracion leerDatosCalibracionPto1(int dataset) {
        Logger.getRootLogger().info("Comando leerDatosCalibracionPto1 no soportado por Banco Capelec");
        return null;
    }

    @Override
    public SerialPort getPuertoSerial() {
       return puertoSerial;
    }

    @Override
    public void setPuertoSerial(SerialPort serialPort)  {
        this.puertoSerial = serialPort;
    }

    public FormatoMediciones getFormatoMediciones() {
        return formatoMediciones;
    }

    public void setFormatoMediciones(FormatoMediciones formatoMediciones) {
        this.formatoMediciones = formatoMediciones;
    }

    public SelectorMediciones getSelectorMediciones() {
        return selectorMediciones;
    }

    public void setSelectorMediciones(SelectorMediciones selectorMediciones) {
        this.selectorMediciones = selectorMediciones;
    }
    
    
    private MedicionGases obtenerMedicionTexto(SelectorMediciones selector,MensajeCapelec mensajeRespuesta){
        
        MedicionGases medicion = new MedicionGases();
        byte[] tramaRecibida = mensajeRespuesta.getDatos();        
        
        assert(tramaRecibida[0] == selector.getValor());
        assert(mensajeRespuesta.getNumeroDatos() == 0x2D);
        assert(tramaRecibida.length == 45);
        
        byte ioStatus = tramaRecibida[43];
        
        boolean[] banderasIOStatus = UtilGasesModelo.shortToFlags(ioStatus);
        //int dividendoCO = banderasIOStatus[2] ? 10 : 1 ;//para determinar si el CO es de dos o tres digitos
        boolean isCO3Digitos = banderasIOStatus[2];
        //En texto se recibe una cadena tipo float con una cantidad de digitos segun la 
        //precision de las medidas, para compatibilidad con el codigo existente para el banco sensors
        //se decide pasar esa cantidad a entero y almacenarlo como tal en la clase MedicionGases que solo maneja enteros
        byte[] auxiliarCadena = new byte[5];
        float auxValorMedida = 0;
        int valorEntero = 0;
        System.arraycopy( tramaRecibida, 1, auxiliarCadena, 0, 5);
        //Valor del CO dependiendo de la configuracion
        auxValorMedida = byteStringArrayToFloat(auxiliarCadena);
        int multiploCO = isCO3Digitos ? 1000 : 100;
        valorEntero = (int)(auxValorMedida * multiploCO);        
        medicion.setValorCO(valorEntero);
        
        //Valor de CO2 siempre con dos digitos de precision se multiplica por 100
        System.arraycopy( tramaRecibida,5, auxiliarCadena,0,5);
        valorEntero = (int) (byteStringArrayToFloat( auxiliarCadena )*100);
        medicion.setValorCO2(valorEntero);
        //Valor de HC 0 digitos de precision
        System.arraycopy( tramaRecibida, 10, auxiliarCadena, 0, 5);
        valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena ));
        medicion.setValorHC(valorEntero);
        //Valor de Lambda tres digitos de precision
        System.arraycopy( tramaRecibida,15,auxiliarCadena, 0, 5);
        valorEntero = (int)( byteStringArrayToFloat( auxiliarCadena )*1000);
        medicion.setValorLambda(valorEntero);
        //Valor de O2 dos digitos de precision
        System.arraycopy( tramaRecibida,20,auxiliarCadena,0,5);
        valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena )*100);
        medicion.setValorO2(valorEntero);
        //Valor de Nox 0 digitos de precision
        System.arraycopy( tramaRecibida,25, auxiliarCadena, 0,5);
        valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena ));
        medicion.setValorNox(valorEntero);
        
       byte errorByte1 = tramaRecibida[41];
       byte errorByte2 = tramaRecibida[42];
       byte errorByte3 = tramaRecibida[43];
       byte errorByte4 = tramaRecibida[44];
       
       medicion = configurarBanderas(medicion,errorByte1,errorByte2,errorByte3,errorByte4);//?? asigna a la misma referencia puede dar errores
        
       switch(selector){
           //demas medidas dependiendo de el parametro datatype
           case RPMTEMP:
               //medicion de rpm tiene 0 digitos de precision
               System.arraycopy( tramaRecibida,30, auxiliarCadena, 0,5);
               valorEntero = (int)(byteStringArrayToFloat(auxiliarCadena));
               medicion.setValorRPM(valorEntero);
               //medicion de temperatura de aceite 1 digito de precision
               System.arraycopy( tramaRecibida,25,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat(auxiliarCadena)*10);
               medicion.setValorTAceite(valorEntero);
               
           break;
           
           case RPMGASPRESS:
               //medicion de rpm tiene 0 digitos de precision
               System.arraycopy( tramaRecibida,30,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat(auxiliarCadena));
               medicion.setValorRPM(valorEntero);
               //medicion de presion de gas 1 digito de precision
               System.arraycopy( tramaRecibida,35, auxiliarCadena, 0,5);
               valorEntero = (int)(byteStringArrayToFloat(auxiliarCadena)*10);
               medicion.setValorPres(valorEntero);
               
               
           break;
               
           case PEFGASPRESS:
            
               //pef tiene 3 digitos de precision
               System.arraycopy( tramaRecibida,30,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena) *1000);
               medicion.setPef(valorEntero);
               //presion de gas 1 digito de precision
               System.arraycopy( tramaRecibida,35,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat(auxiliarCadena)*10);
               medicion.setValorPres(valorEntero);
               
               
           break;   
           case OTROS:
           
               //toca armar completamente la medicion porque no tiene medidas en comun con los otros casos revisar el manual
               medicion = null;
               medicion = new MedicionGases();
               //medida de temperatura de detector 
               System.arraycopy( tramaRecibida,1,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena) * 10);
               medicion.setValorDetectorTemp(valorEntero);
               //medida de presion de gas 1 digito de precision:
               System.arraycopy(tramaRecibida,5,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena) * 10);
               medicion.setValorPres(valorEntero);
               //medida de temperatura ambiene un digito de precision
               System.arraycopy( tramaRecibida, 10, auxiliarCadena, 0, 5);
               valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena) * 10);
               medicion.setValorTAmbiente(valorEntero);
               //medide de presion de vacio 1 digito de precision
               System.arraycopy( tramaRecibida, 15, auxiliarCadena, 0 ,5 );
               valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena) * 10);
               medicion.setPresionVacio(valorEntero);
               //medida del valor del pef 3 digitos de precision
               System.arraycopy( tramaRecibida,20,auxiliarCadena,0,5);
               valorEntero = (int)(byteStringArrayToFloat( auxiliarCadena) * 1000);
               medicion.setPef( valorEntero);
               //medida del porcentaje de calentamiento 0 digitos de precision
               System.arraycopy( tramaRecibida, 25, auxiliarCadena,0,5);
               valorEntero = (int)( byteStringArrayToFloat( auxiliarCadena ));
               medicion.setPorcentajeCalentamiento(valorEntero);
               //medida del voltaje de oxigeno 3 digitos de precision
               System.arraycopy( tramaRecibida, 30, auxiliarCadena,0,5);
               valorEntero = (int)( byteStringArrayToFloat( auxiliarCadena )*1000);
               medicion.setVoltajeO2( valorEntero );
               
               errorByte1 = tramaRecibida[41];
               errorByte2 = tramaRecibida[42];
               errorByte3 = tramaRecibida[43];
               errorByte4 = tramaRecibida[44];
       
               medicion = configurarBanderas(medicion,errorByte1,errorByte2,errorByte3,errorByte4);//?? asigna a la misma referencia puede dar errores             
               
           break;    
               
          
       }//end of switch
       
       return medicion;
        
    }
    
    /**
     * Obtiene la Medicion dependiendo de el selector
     * de la informacion que se requiera, este metodo 
     * utiliza el formato entero para hacer la peticion
     * es decir el comando 'I'
     * @param selector
     * @return 
     */
    private MedicionGases obtenerMedicionEntero(SelectorMediciones selector,MensajeCapelec mensajeRespuesta){
      
       MedicionGases medicion = new MedicionGases();
       byte[] tramaRecibida = mensajeRespuesta.getDatos();
                
                //el primer byte de la trama ser igual a el valor del selector de mediciones
       assert(tramaRecibida[0] == selector.getValor());
       assert(mensajeRespuesta.getNumeroDatos() == 0x15);
       assert(tramaRecibida.length == 21);
                //byte de estatus para verificar el CO3digits.
       byte ioStatus = tramaRecibida[19];
       boolean[] banderasIOStatus = UtilGasesModelo.shortToFlags(ioStatus);
       int dividendoCO = banderasIOStatus[2] ? 10 : 1;
                //el numero de bytes recibidos no es variable por l oque debe ser 21 necesariamente
                //en el formato de sensors este entero siempre se divide por 100 verdad o se multiplica por 0.01
                //para que no se afecte ese mecanismo se divide 10 cuando el CO venga con tres digitos asi se divide por 1000
                //cuando sean tres digitos
       int valorCO = UtilGasesModelo.fromBytesToShort( new byte[]{ tramaRecibida[2],tramaRecibida[1]} );
       medicion.setValorCO( valorCO / dividendoCO );//si, exactamente quita el digito de exactitud exra!
       medicion.setValorCO2( UtilGasesModelo.fromBytesToShort( new byte[]{ tramaRecibida[4], tramaRecibida[3] } ) );
       medicion.setValorHC(  UtilGasesModelo.fromBytesToShort(  new byte[]{ tramaRecibida[6], tramaRecibida[5]} ) );
       medicion.setValorLambda( UtilGasesModelo.fromBytesToShort( new byte[]{ tramaRecibida[8], tramaRecibida[7] } ) );
       medicion.setValorO2( UtilGasesModelo.fromBytesToShort( new byte[]{ tramaRecibida[10], tramaRecibida[9] }) );
       medicion.setValorNox( fromBytesToShort( tramaRecibida[12], tramaRecibida[11] ) );
     
       byte errorByte1 = tramaRecibida[17];
       byte errorByte2 = tramaRecibida[18];
       byte errorByte3 = tramaRecibida[19];
       byte errorByte4 = tramaRecibida[20];
                //banderas
       medicion = configurarBanderas(medicion,errorByte1,errorByte2,errorByte3,errorByte4);//?? asigna a la misma referencia puede dar errores
       
       switch(selector){
           case RPMTEMP:
                
                medicion.setValorRPM( fromBytesToShort( tramaRecibida[14] , tramaRecibida[13] ) );
                medicion.setValorTAceite( ( fromBytesToShort( tramaRecibida[16], tramaRecibida[15] ) )/10 );//para que sea lo mismo que sensors
                   
           break;
               
           case RPMGASPRESS:
               
                medicion.setValorRPM( fromBytesToShort( tramaRecibida[14], tramaRecibida[13]) );
                medicion.setValorPres( ( fromBytesToShort(tramaRecibida[16], tramaRecibida[15]) ) / 10 );
                
           break;
           
           case PEFGASPRESS:
                
               medicion.setPef( fromBytesToShort( tramaRecibida[14], tramaRecibida[13] ) );
               medicion.setValorPres( fromBytesToShort( tramaRecibida[16], tramaRecibida[15] ) / 10 );
               
           break;
               
           case OTROS:
               
               medicion = new MedicionGases();
               medicion.setValorDetectorTemp( ( fromBytesToShort( tramaRecibida[2],tramaRecibida[1] ) ) / 10 );
               medicion.setValorPres( ( fromBytesToShort( tramaRecibida[4], tramaRecibida[3]) ) / 10);
               medicion.setValorTAmbiente( (fromBytesToShort( tramaRecibida[6], tramaRecibida[5]) /10 ));
               medicion.setPresionVacio( ( fromBytesToShort( tramaRecibida[8],tramaRecibida[7] ) ) /10 );
               medicion.setPef( ( fromBytesToShort( tramaRecibida[10], tramaRecibida[9] ) ) );
               medicion.setPorcentajeCalentamiento( ( fromBytesToShort( tramaRecibida[12], tramaRecibida[11] ) ) );
               medicion.setVoltajeO2( (fromBytesToShort( tramaRecibida[14], tramaRecibida[15]) ));
               
               
              errorByte1 = tramaRecibida[17];
              errorByte2 = tramaRecibida[18];
              errorByte3 = tramaRecibida[19];
              errorByte4 = tramaRecibida[20];
                //banderas
              medicion = configurarBanderas(medicion,errorByte1,errorByte2,errorByte3,errorByte4);//?? asigna a la misma referencia puede dar errores
       
              break;    
       }
       return medicion;
    }
    
    /**
     * Obtiene la medicion de gases cuando el formato en que el banco capelec envia
     * la respuesta es en formato float
     * @param selector especifica cuales medidas retornar
     * @param mensajeRespuesta respuesta que envia el banco capelec
     * @return 
     */
    
    private MedicionGases obtenerMedicionFloat(SelectorMediciones selector,MensajeCapelec mensajeRespuesta){
        
        MedicionGases medicion = new MedicionGases();
        byte[] tramaRecibida = mensajeRespuesta.getDatos();
        byte[] auxiliarFloat = new byte[4];
        int valorEntero = 0;
        
        assert(mensajeRespuesta.getNumeroDatos() == 0x25);
        assert(mensajeRespuesta.getLetraComando() == 'A');
        
        byte ioStatus = tramaRecibida[19];
        boolean[] banderasIOStatus = UtilGasesModelo.shortToFlags(ioStatus);
        int multiploCO = banderasIOStatus[2] ? 1000 : 100;
        
        //valor de C0        
        //a causa de que elo banco maneja dos otres digitos de precision se tiene que multiplicar por 1000 o por 100
        System.arraycopy( tramaRecibida,1, auxiliarFloat, 0, 4);
        valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat) * multiploCO);
        medicion.setValorCO(valorEntero);
        
        //valor de CO2 dos digitos de precision
        System.arraycopy( tramaRecibida,5, auxiliarFloat,0,4);
        valorEntero = (int)(fromByteArrayToFloat( auxiliarFloat) * 100);
        medicion.setValorCO2(valorEntero);
        
        //valor de HC 0 digitos de precision
        System.arraycopy( tramaRecibida, 9,auxiliarFloat,0,4);
        valorEntero = (int)( fromByteArrayToFloat(auxiliarFloat));
        medicion.setValorHC(valorEntero);
        
        //valor de Lambda tres digitos de precision        
        System.arraycopy( tramaRecibida, 13, auxiliarFloat,0, 4);
        valorEntero = (int) ((fromByteArrayToFloat(auxiliarFloat)*1000));
        medicion.setValorLambda(valorEntero);
        
        //valor de oxigeno dos digitos de precision
        System.arraycopy( tramaRecibida,17,auxiliarFloat,0,4);
        valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat) * 100);
        medicion.setValorO2( valorEntero);
        
        //valor de nox 0 digitos de precision
        System.arraycopy( tramaRecibida, 21, auxiliarFloat , 0, 4);
        valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat));
        medicion.setValorNox(valorEntero);
        
        byte errorByte1 = tramaRecibida[33];
        byte errorByte2 = tramaRecibida[34];
        byte errorByte3 = tramaRecibida[35];
        byte errorByte4 = tramaRecibida[36];
                //banderas
       medicion = configurarBanderas(medicion,errorByte1,errorByte2,errorByte3,errorByte4);//?? asigna a la misma referencia puede dar errores
        
               
       switch(selector){
           case RPMTEMP:
                //valor de rpm 0 digitos de precision
                System.arraycopy( tramaRecibida, 25, auxiliarFloat, 0,4);
                valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat));
                medicion.setValorRPM(valorEntero);
        
                //valor de temperatura de aceite 1 digito de precision
                System.arraycopy( tramaRecibida, 29,auxiliarFloat,0,4);
                valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat) * 10);
                medicion.setValorTAceite(valorEntero);    
        
           break;    
           
           case RPMGASPRESS:
               //valor de rpm 0 digitos de precision
               System.arraycopy( tramaRecibida, 25, auxiliarFloat, 0,4 );
               valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat ));
               medicion.setValorRPM(valorEntero);
               
               //valor de presion del gas 1 digito de precision
               System.arraycopy( tramaRecibida, 29, auxiliarFloat, 0, 4);
               valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat ) * 10);
               medicion.setValorRPM( valorEntero);              
               
           break;   
           
           case PEFGASPRESS:
               //valor de pef tres digitos de precision
               System.arraycopy( tramaRecibida, 25, auxiliarFloat, 0, 4);
               valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat ) *1000);
               medicion.setPef( valorEntero);
               
               //valor de presion del gas un digito de precision
               System.arraycopy( tramaRecibida, 29, auxiliarFloat, 0, 4);
               valorEntero = (int)((fromByteArrayToFloat( auxiliarFloat)) * 10);
               medicion.setValorPres(valorEntero);
               
           
           break;
           
           case OTROS:
               
               medicion = new MedicionGases();
               
               //medida de la temperatura del detector 1 digito de precision
               System.arraycopy( tramaRecibida,1,auxiliarFloat,0,4);
               valorEntero = (int)(fromByteArrayToFloat( auxiliarFloat) * 10);
               medicion.setValorDetectorTemp(valorEntero);
               
               //valor de la presion del gas
               System.arraycopy( tramaRecibida, 5, auxiliarFloat, 0, 4);
               valorEntero = (int)(fromByteArrayToFloat( auxiliarFloat) * 10);
               medicion.setValorPres(valorEntero);
               
               //valor de la temperatura ambiente:
               System.arraycopy( tramaRecibida, 9, auxiliarFloat, 0, 4);
               valorEntero = (int)(fromByteArrayToFloat( auxiliarFloat) * 10);
               medicion.setValorTAmbiente(valorEntero);
               
               //valor de la presion de vacio un digito de precision
               System.arraycopy( tramaRecibida,13, auxiliarFloat, 0 ,4);
               valorEntero = (int)(fromByteArrayToFloat( auxiliarFloat) * 10);
               medicion.setPresionVacio(valorEntero);
               
               //valor del pef tres digitos de precision
               
               System.arraycopy( tramaRecibida, 17, auxiliarFloat, 0,4);
               valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat) * 1000);
               medicion.setPef( valorEntero);
               
               //valor del porcentaje de calentamiento
               System.arraycopy( tramaRecibida, 21, auxiliarFloat, 0,4);
               valorEntero = (int)( fromByteArrayToFloat( auxiliarFloat));
               medicion.setPorcentajeCalentamiento(valorEntero);
               
               //voltaje del oxigeno
               
               System.arraycopy( tramaRecibida, 25, auxiliarFloat, 0, 4);
               valorEntero = ( int)(fromByteArrayToFloat( auxiliarFloat) *1000);
               medicion.setVoltajeO2( valorEntero );
               
               //banderas
               
               errorByte1 = tramaRecibida[33];
               errorByte2 = tramaRecibida[34];
               errorByte3 = tramaRecibida[35];
               errorByte4 = tramaRecibida[36];
                //banderas
                medicion = configurarBanderas(medicion,errorByte1,errorByte2,errorByte3,errorByte4);//?? asigna a la misma referencia puede dar errores 
                       
               
               
               
           break;    
           
           
              
       }
                
         return medicion;      
        
    }
    
    
    /**
     * Procesa el mensaje de respuesta para convertirlo en una medicion
     * de gases dependiendo del formato de la trama y del selector de mediciones
     * 
     * @param mensajeRespuesta
     * @param formatoMediciones
     * @param selector
     * @return 
     */
    private MedicionGases procesarMedicion(MensajeCapelec mensajeRespuesta, FormatoMediciones formatoMediciones, SelectorMediciones selector) {
        
        MedicionGases medicion = null;
        switch(formatoMediciones){
            case ENTERO:
               medicion = obtenerMedicionEntero(selector, mensajeRespuesta);
            break;    
            case FLOTANTE:
               medicion = obtenerMedicionFloat(selector, mensajeRespuesta) ;
            break;
            case TEXTO:
                medicion = obtenerMedicionTexto(selector, mensajeRespuesta);
            break;    
        }
        
        return medicion;
    }//end of method MedicionGases

    private MedicionGases configurarBanderas(MedicionGases medicion, byte errorByte1, byte errorByte2, byte errorByte3, byte errorByte4) {
        
        //del 0 al 7 serian las posiciones interesantes
        boolean[] banderasErrorByte1 = shortToFlags(errorByte1);
        boolean[] banderasErrorByte2 = shortToFlags(errorByte2);
        boolean[] banderasErrorByte3 = shortToFlags(errorByte3);//mapa de entradas y salidas
        boolean[] banderasErrorByte4 = shortToFlags(errorByte4);
        
        medicion.setCeroEnProgreso( banderasErrorByte1[7]);
        medicion.setCero( banderasErrorByte1[6] );
        medicion.setCalentamiento(  banderasErrorByte1[5] );
        medicion.setCalibracionEnProgreso( banderasErrorByte1[4] );
        medicion.setCalibracion( banderasErrorByte1[3] );
        medicion.setPresionRange( banderasErrorByte1[2] );
        medicion.setTempAmbienRange( banderasErrorByte1[1] );
        medicion.setTempDetectorRange( banderasErrorByte1[0] );
        
        //segundo Errorbyte
        
        medicion.setHCrange(  banderasErrorByte2[7] );
        medicion.setCOrange( banderasErrorByte2[6] );
        medicion.setCO2range( banderasErrorByte2[5] );
        medicion.setO2range( banderasErrorByte2[4] );
        medicion.setNOxRange( banderasErrorByte2[3] );
        medicion.setTempAceiteRange( banderasErrorByte2[2] );
        medicion.setRpmRange( banderasErrorByte2[1] );
        medicion.setVacioRange( banderasErrorByte2[0] );
        
        //el terecer byte corresponde simplemente la mayoria al byte de las entradas y salidas 
        medicion.setCO3digitos( banderasErrorByte3[2] );
        medicion.setHCpropano( banderasErrorByte3[1] );
        medicion.setChanelError( banderasErrorByte3[0] );
        medicion.setVacuumSwitch(banderasErrorByte3[3] );
        
        //cuarto byte corresponde a otras banderas
        medicion.setErrorEEPROM( banderasErrorByte4[7] );
        medicion.setBadO2Sensor( banderasErrorByte4[6] );
        medicion.setBajaSenialDetector( banderasErrorByte4[5] );
        medicion.setBadNOxSensor( banderasErrorByte4[4] );
        medicion.setCeroInicialEnProgreso( banderasErrorByte4[3] );
        medicion.setNuevoDatoGas( banderasErrorByte4[2] );
        medicion.setNuevoDatoRpm( banderasErrorByte4[1] );
        medicion.setErrorLampara( banderasErrorByte4[0] );
        
        return medicion;
    }
    
    
    /**
     * Metodo que envia el comando de cero hacia el banco capelec
     * Establece todos los canales en cero excepto el oxigeno 
     * al que le establece 20.9%
     */
    public void enviarCero(){
        
        MensajeCapelec mensajeComando = new MensajeCapelec('Z', 0x00, new byte[0]);
        enviarMensajeCapelec(mensajeComando);
        
    }//end of method 

    @Override
    public void setFactorRPM(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRpm() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getTemp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRpm(Integer rpm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    /**
     * Constante para seleccionar los datos que va a retornar 
     * el comando obtener datos y estatus
     */
    
   public enum SelectorMediciones{
        
        RPMTEMP(0x20),RPMGASPRESS(0x21),PEFGASPRESS(0x22),OTROS(0x15);
        
        
        private byte valor;

        private SelectorMediciones(int valor) {
            this.valor = (byte)valor;
        }   
        
        public byte getValor(){
            return this.valor;
        }
    }
    
    /**
     * Constante para seleccionar el tipo de formato para el comando
     * de obtener datos y estatus
     */
    public enum FormatoMediciones{
        
        TEXTO('T'),ENTERO('I'),FLOTANTE('A');
        
        private char letraComando;

        private FormatoMediciones(char letraComando) {
            this.letraComando = letraComando;
        }

        public char getLetraComando() {
            return letraComando;
        }
        
        
        
    }
    
    
    /**
     * Este comando hace que el banco capelec envie continuamente la trama seleccionada de forma
     * muy parecida al comando obtenerDatos.
     * La aplicacion que inicie este comando debera entonces estar escuchando continuamente
     * el puerto serial para procesar los resultados.
     * 
     * @param frecuencia
     * @param formato
     * @param selector 
     */
      
     
    public void iniciarModoContinuo(int frecuencia,FormatoMediciones formato, SelectorMediciones selector){
        
        MensajeCapelec mensajeComando = new MensajeCapelec( 'S', 0x03, new byte[]{ (byte)formato.getLetraComando(),selector.getValor(),(byte)frecuencia });
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);        
        //manejar la respuesta
    }
    
    /**
     * Le indica al banco capelec que termine el modo continuo de envio de informacion
     * 
     */
    public void terminarModoContinuo(){
        MensajeCapelec mensajeComando = new MensajeCapelec('Q',0x00, new byte[0]);
        MensajeCapelec mensajeRespuesta = enviarMensajeCapelec(mensajeComando);
        //manejar la respuesta

    }
    
    
    /**
     * Ordena al banco hacer la calibracion del
     * 
     * @param modoCalibracion 1: calibracion de un punto,2: calibracion de HC solamente, 3: calibracion de CO2 solamente
     * 4: Calibracion de CO solamente
     * @param valorCO
     * @param valorCO2
     * @param valorHC 
     */
    
    public void calibracion(TipoCalibracion tipo,double valorCO, double valorCO2, int valorHC){
        
        //dos digitos de precision para el valor del CO y el del CO2, ninguno para el valorHC, sinembargo
        //se debe formatear para tener cuatro numeros
        
        DecimalFormat df = new DecimalFormat("00.00");
        
        String COFormateado = df.format(valorCO);
        String CO2Formateado = df.format(valorCO2);
        
        df.applyPattern("0000");
        String HCFormateado = df.format(valorHC);
        
        //concatenar todos los valores en un arrglo de bytes
        //concatenar toda la cadena y pasarla a el arreglo de bytes
        //pasar cada cadena a arreglos y unirlos
        
        StringBuilder sb = new StringBuilder(COFormateado);
        sb.append(CO2Formateado).append(HCFormateado);
        
        byte[] b = sb.toString().getBytes();
        
            byte[] byteStr = sb.toString().getBytes();
        byte[] arregloDatos = new byte[16];
        arregloDatos[0] = tipo.getValor();
        
        System.arraycopy(byteStr,0, arregloDatos,1,byteStr.length);
        
        MensajeCapelec mensajeComando = new MensajeCapelec('C',0x10,arregloDatos );    
        MensajeCapelec mensajeRespuesta =  enviarMensajeCapelec(mensajeComando);
        //TODO manejarRespuesta
        
    }
    
    
    public enum TipoCalibracion {
        
        UNPUNTO((byte)-128),HC( (byte)0x04),CO( (byte)0x01),CO2( (byte)0x02);
        
        private byte valor;

        private TipoCalibracion(byte valor) {
            this.valor = valor;
        }

        public byte getValor() {
            return valor;
        }
               
        
    } 
            
    public static void main(String[] args) {
        new BancoCapelec().calibracion(TipoCalibracion.UNPUNTO, 25, 1, 1);
    }
    
}//end of class


