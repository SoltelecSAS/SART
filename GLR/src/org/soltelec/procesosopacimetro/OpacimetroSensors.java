/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.UtilGasesModelo;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *Clase para comunicarse y enviar comandos para el opacimetro sensors
 * 
 * @author GerenciaDesarrollo
 */
public class OpacimetroSensors implements Opacimetro {

    private SerialPort port;
    private OutputStream out;
    private InputStream in;

    //comandos de prueba para la comunicacion

    /**
     * este maneja sincronia enviando y recibiendo comandos
     * intenta leer tres veces si no lo logra retorna una referencia de null
     */
    private byte[] enviarRecibirTrama(byte[] arregloEnviar){
       boolean respuestaRecibida = false;
       byte[] buffer = null;
        try {
            out.write(arregloEnviar);
        } catch (IOException ex) {
            System.out.println("Excepcion enviando datos");
            return buffer;
        }//end of try/catch

        try {
            Thread.sleep(40);
        } catch (InterruptedException ex) {
            Logger.getLogger(OpacimetroSensors.class.getName()).log(Level.SEVERE, null, ex);
        }
        int contador = 0;
        buffer = new byte[2048];
        while( respuestaRecibida == false && contador < 2){
                System.out.println("\nLeyendo datos..-..");
                int data;
                  try
                          {
                                int len = 0;
                                while ( ( data = in.read()) > -1 )//esto indica que hay datos en el flujo
                                {//es un polling
                                   buffer[len++] = (byte)data;//no importa porque lee de byte en byte
                                   //System.out.println("Bloqueado aqui");
                                }//end while
                                if(len > 0) {//si hay datos
                                    System.out.println("Longitud del mensaje:" + len);
                                    respuestaRecibida = true;
                                    //copiar a un arreglo para armar el mensaje
                                    byte arreglo[] = new byte[len];
                                    //armar el mensaje
                                    System.arraycopy(buffer, 0, arreglo, 0, len);
                                    return arreglo;//return arreglo
                                } else {
                                    System.out.println("Intentos de lectura: " + contador);
                                    //respuestaRecibida = true;//aqui sale del ciclo de maner que solamente pregunta una vez
                                    contador++;
                                    //contador++;
                                }
                            } catch(IOException ioe){
                                System.out.println("Excepcion leyendo datos del opacimetro");
                                contador++;
                            }//end catch
                            catch(ArrayIndexOutOfBoundsException ae){
                                return null;
                            }
                }//end of while
        return null;
    }//end of method

    /**
     * Este metodo se supone que alista
     * la operación de tomar medidas por 10 segundos
     * ascii '97' 
     */
public void armarLCS(){
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)'a');
    byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);

}//end of method

/**
 * Establece la intensidad del opacimetro para intensidad
 * media
 * @param valor menor a 50 para intensidad media y mayor a 50 para intensidad full
 * @return true para una configuración exitosa, falso para una configuracion falida
 */
public boolean configurarIntensidad(int valor){
    byte valorByte = (byte)valor;
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)'c');
    byte datos[] = new byte[2];//problema si son o no son datos
    datos[0] = 0;//para indicar que vamos a  escribir configurarIntensidad Media
    datos[1] = valorByte;//menora a 50 para configurar Intensidad media mayor a 50 para intensidad normal o full
    msjOpacimetro.setDatos(datos);
    byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    if(respuesta.getComando() == 'c')
        System.out.println("Comando recibido OK");
    //Hasta aquí todo bien
    //Verificar que la intensidad
    //enviar otro comando
    //leer la intensidad
    datos[0] = -64;//verificar que sea de la fomra 1xxxxxxxx
    datos[1] = 0;//no importa
    byte[] tramaRespuesta2 = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta2 =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta2);
    if (respuesta2.getDatos()[1] == 50 && valor <= 50){
        System.out.println("La intensidad esta configurada en:" + 50 + "correctamente");
        return true;//para verificacion
    }//end if
    else if(respuesta2.getDatos()[1] == 100 && (valor > 50 && valor <= 100) ){
        System.out.println("La intensidad esta configurada en:" + 100 + "correctamente");
        return true; //para verificacion
    } else {
        System.out.println("La intensidad es: " + respuesta2.getDatos() + "la configuracion ha fallado");
        return false;
    }
}//end of method

public int valorIntensidad(){
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)'c');
    byte[] dato = {-128};
    msjOpacimetro.setDatos(dato);
    byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    short intensidad = UtilGasesModelo.fromBytesToShort(respuesta.getDatos());
    return intensidad;
}

/**
 * Comando para obtener unidades de datos
 * los datos se actualizan cada 20 ms
 * es pedir datos continuamente
 *
 *
 */

public MedicionOpacidad obtenerDatosOlder(){
    
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)'d');
    byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    MedicionOpacidad medicion = new MedicionOpacidad(respuesta.getDatos());
    return medicion;
    //
}//end of method

/**
 * Metodo para definir un filtro para los datos
 * El LCS puede filtrar los datos muestreados internamente, así relevando al software anfitrión de esta tarea.
    El usuario puede seleccionar entre las siguientes opciones de filtrado:
    -no filtrado (default al encender)
    - Filtro en N (porcentaje de opacidad)
    - filtro en k (coeficiente de absorción de luz [m-1]
    - filtro usando IIR-filtro
    -filtro usando fltro FIR, de 1 a 32 promedio de muestras movible
 * @param modoUnidad : 1, seleccionar filtro en N, 2 seleccionar filtro en k
 * @param leerEscribir : true para leer el valor del polo false para escribirlo
 * @param filtro : 0 filtro en FIR, 1 filtro en IIR
 * @param polo * 100 si quiere 0.100 --> 100
 */
public void seleccionFiltro(byte modoUnidad, short polo,boolean leerEscribir,byte filtro ){
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)'e');
    byte b = 0;
    switch(modoUnidad){//bit 6 es decir 64
        case 1:
        b = (byte)(b |0x40);
        break;
        case 2:
        b= (byte)(b&-65);//pa blanquerlo
        break;
        default:
        System.out.println("Bad argument en seleccion de filtro modo incorrecto");
        break;
    }//end switch
    switch(filtro){//bit 5 es decir 32
        case 0://filtro en FIR o sin filtro
            b = (byte)(b|32);//poner en uno ese bit
        break;
        case 1:
            b = (byte)(b&-65);
        break;
        default:
         System.out.println("Bad argument en seleccion de filtro modo incorrecto");
        break;
    }//edn switch
    if(leerEscribir){
        b = (byte)(b|128);
        //la respuesta es distinta
       } else {//Escribir
        b = (byte)(b&127);//blanquerlo
       }//end else
     byte[] arregloPolo = UtilGasesModelo.toBytes(polo);
        byte[] datosMensaje = {b,arregloPolo[0],arregloPolo[1]};
        msjOpacimetro.setDatos(datosMensaje);
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    if(respuesta.getByteDelComando() == 'e'){
        System.out.println("El comando e  para escribir el polo fue enviado exitosamente ");
    }
}//end of method

/**
 *Metodo retorna -1 en caso de error
 * @return el valor del polo independientemente si es FIR o IIR
 */
public short leerValorPolo(){
    short valorPolo = -1;
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)'e');
    byte datos[] = {-128};//binario 10000000
    msjOpacimetro.setDatos(datos);
    byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    if(respuesta.getDatos().length == 3){
        byte[] arregloPolo = {respuesta.getDatos()[1],respuesta.getDatos()[2]};
        valorPolo = UtilGasesModelo.fromBytesToShort(arregloPolo);
    }else if(respuesta.getDatos().length == 2){
        byte polo = respuesta.getDatos()[1];
        String strPolo = Integer.toString(polo&0xFF, 2);
        valorPolo =  (short)Integer.parseInt(strPolo,2);
    }
    return valorPolo;
}//end of method




/**
 *Comando para obtener la frecuencia de Muestreo del
 * Opacimetro
 * @return entero frecuencia de muestreo
 */
public int obtenerFrecuenciaMuestreo(){
        int frecuenciaMuestreo;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'f');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta =UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        try{
        if(respuesta.getDatos().length != 1)//la respuesta deber ser de 1 entre 50 y 60
        return -1;
        frecuenciaMuestreo = respuesta.getDatos()[0];}
        catch(Exception exc)
        {frecuenciaMuestreo = -1;}
        System.out.println("Frecuencia de Muestreo:" + frecuenciaMuestreo);
        return frecuenciaMuestreo;
    }//end of methodlll

    /**
     * 
     * 
     * 
     * Metodo para obtener el Class ID y el Numero Id de la maquina
     * @return un arreglo con el ClassID y el NumeroId
     * 
     * 
     * 
     */

    public int[] obtenerClassIdNumeroID(){
        int[] info;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'j');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        info = new int[2];
        info[0] = respuesta.getDatos()[0];//aqui ya vota el null
        info[1] = respuesta.getDatos()[1];
        //end if
        System.out.println("La Class Id es:" + info[0]);
        System.out.println("El device id es:" + info[1]);
        return info;
    }

/**
 *Comando para obtener el valor actua del Umbral de temperatura 
 * @return el valor del umbral de temperatura tener cuidado  por que es un short asi que un valor negativo puede ser engañoso
 */
    public int verificarUmbralTemperatura(){
        int valorTemperatura;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'h');
        byte[] arregloDatos = new byte[1];
        arregloDatos[0] = -128; // 1000000000
        msjOpacimetro.setDatos(arregloDatos);
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        String stringTemperatura = Integer.toBinaryString(respuesta.getDatos()[1]&0xFF);
        valorTemperatura = Integer.parseInt(stringTemperatura, 2);
        return valorTemperatura;
    }//end of method

    /**
     * Metodo para configurar el umbral de temperatura para el gas
     * @param valor el valor de temperatura, claro que no puede ser mayor a 255 porque no alcanza
     */
    public void configurarUmbralTemperatura(int valor){
        int valorTemperatura = (valor > 355 ? 50: valor);//solo para hacer una pequeña validacion
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando( (byte)'h' );
        byte[] arregloDatos = new byte[2];
        String strValor = Integer.toBinaryString(valorTemperatura&0xff);
        arregloDatos[1] = (byte)(Integer.parseInt(strValor,2) );
        arregloDatos[0] = 0;
        msjOpacimetro.setDatos(arregloDatos);
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    }//end of method

    /**Sets mínimum detector full span para operación lineal en el campo.
     * Por consiguiente este “clean window factor” en porcentaje se refiere al
     * máximo nivel de intensidad el cual es disponible directamente después de manufacturar la unidad.
     * En tiempo de manufactura, este valor se establece a 10% y almacenado en la EEPROM.
     * Debido al posible construcción sucia en la óptica, la intensidad del LED en el detector puede ser atenuada.
     * Si la atenuación hace la intensidad corriente menor que la umbral de ventana limpia,
     * el bit de estatus 9 se activa.
     * @param span el facor de escala o "CleanWindowFactor"
     */
    public void configurarSpan(int span){
        if(span <=10 || span >= 70)
            return;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'k');
        byte[] arregloDatos = new byte[2];
        arregloDatos[0] = 0;//10000000
        arregloDatos[1] = (byte)span;
        msjOpacimetro.setDatos(arregloDatos);
         byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        System.out.println("Respuesta desde el opacimetro" + respuesta.toString());
    }//end of mehod configurarSpan

    /**
     * Lee el valor de "Clean Window Factor" o factir de Ventana limpia
     * es un valor de span para la intensidad del led
     * @return el valor de "Clean Window Factor"
     */
    public int leerValorSpan(){
        int valorSpan= -1;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'k');
        byte[] arregloDatos = new byte[1];
        arregloDatos[0] = -128; //10000000
        msjOpacimetro.setDatos(arregloDatos);
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        valorSpan = respuesta.getDatos()[1];
        return valorSpan;

    }//end of method leerValorSpan


    /**
     * Metodo para linealizar no se debe pedir datos durante este 
     * periodo, bit se activa
     */
    public void calibrar(){
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'l');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        //System.out.println("Respuesta desde el opacimetro " + respuesta.toString() );
    }//end of method calibrar

    /**
     * Metodo para leer valores de la eeprom, no se proporciona
     * para escritura
     * @param direccion la direccion de inicio de lectura
     * @param bytes ¿cuantos datos leer?
     * @return los datos leidos
     */

    public byte[] leerValoresEEprom(int direccion, int bytes){
        if(direccion < 0 ||direccion > 127)
            return null;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'m');
        byte[] arregloDatos = new byte[2];
        arregloDatos[0] = (byte)direccion;
        arregloDatos[1] = (byte)bytes;
        msjOpacimetro.setDatos(arregloDatos);
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        return respuesta.getDatos();
    }//end of leerValoresEEprom


    /**
     * Instruye a la LCS a subir 10 segundos del buffer de datos de opacidad desde un evento ARM&TRIGGER.
     * La velocidad de muestreo de 50 muestras/segundo resulta en 500 palabras de datos
     * (1000 bytes) ascci de 'o' = 111 chksum -111
     * @return retorna el arreglo de datos
     */

    public byte[] obtenerArregloDatosOpacidad(){
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'o');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        return respuesta.getDatos();
    }//end of method obtenerArregloDatosOpacidad

    /**
     * Instruye al LCS a desarmar el disparador y dejar d escribir datos de opacidad en la memoria del buffer. El LCS aborta el estado armado y resetea el bit trigger del status.
     * Nota: Desarmer el tigger o del detonador o el disparador terminara el modo de
     * trigger antes que  diez segundos de  datos sean adquiridos y guardados en
     * el buffer de memoria. ( 1 segundo pre-trigger y 9 segundos buffer trigger).
     * ascii de 'q' es 113
     * Esto permite al operador del software acortar el tiempo de recolección de datos.
     */
    public void desarmarTrigger(){
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando( (byte)'q');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        System.out.println("Respuesta recibida desde el opacimetro" + respuesta);
    }//end of method desarmarTrigger


    /**
     * Instruye al LCS a dispararse o detonarse, resetea el bit de estatus
     * arm-trigger  y activa el bit de estado trigger. Si el LCS estuvo armado
     * por más de un segundo, el buffer de datos retiene el último segundo de
     * información. Si el LCS estuvo armado por un tiempo menor a un segundo,
     * el buffer de datos retenerá esta información y empezará a almacenar datos
     * desde el disparo en el buffer de datos. El buffer de datos puede almacenar
     * hasta 10 segundos de datos armados (pre – disparado) y datos disparados.
     * codigo ascii de 't' 116
     */

    public void dispararTrigger(){
       MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
       msjOpacimetro.setByteDelComando( (byte)'t' );
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        System.out.println("Respuesta recibida desde el opacimetro" + respuesta);
    }//end of method dispararTrigger

    /**
     * Pide datos desde el LCS, los datos son actualizados cada 20 ms
     * @return
     */

    @Override
    public MedicionOpacidad obtenerDatos(){
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)117);
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        MedicionOpacidad medicion = new MedicionOpacidad(respuesta.getDatos());
        System.out.println(medicion.toString());
        return armarBytesEstado(medicion);
    }//end of method obtenerDatos


    /**
     * A)	Unidades LCS con version de software hasta 1.42
            Peticion  de numero de revisión de software
     * B)	La siguiente estructura de comando esta disponible en versiones de software 1.43 y superiores:
        Peticion de numero de revisión de software y su checksum
     */

    public int[] obtenerInfoSoftware(){
        int[] infoSoftware = new int[3];
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'v');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        //hacer la conversion
        byte[] arregloVersion = {respuesta.getDatos()[1],respuesta.getDatos()[0]};
        infoSoftware[0] = UtilGasesModelo.fromBytesToShort(arregloVersion);
        infoSoftware[1] = respuesta.getDatos()[1];//constante de filtro alto
        infoSoftware[2] = respuesta.getDatos()[2];

        return infoSoftware;
    }//end of method obtenerVersionSoftware

    /**
     * Reporta el número de puntos adquiridos de datos durante el disparo.
     * Puede ser ejecutado solamente después de enviar el comando de quitar – gatillo (‘q’).
     * Donde puntos de datos disparados es el número de datos mestreados de puntos disparados en el buffe de datos del triger
        50<= triggered data points <= 500 *
     *
     * @return el numero de datos adquiridos
     */
    public int numeroDatosAdquiridos(){

        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando((byte)'w');
        byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        byte[] arreglo = {respuesta.getDatos()[1],respuesta.getDatos()[0]};
        short datos = UtilGasesModelo.fromBytesToShort(arreglo);
        return datos;
    }//end of method numeroDatosAdquiridos

    /**
     * Permite subida de datos en tiempo real durante el evento de disparo del
     * trigger. Depues de mandar el comando de trigger(‘t’) 0x8a puede ser secuenciado
     * para subir un conjunto de datos de puntos de opacidad del buffer de disparo. El comando asegurará rápida y completa transmisión de datos sin tener que esperar el final de un ciclo de disparo
     */
    public byte[] datosRTTrigger(int indiceInicial, int indiceFinal){
         byte[] arreglo;
        MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
        msjOpacimetro.setByteDelComando( (byte)0x8a);
       //toca partirlos
       short shortInicial = (short)indiceInicial;
       arreglo = UtilGasesModelo.toBytes(shortInicial);
       byte[] arregloInicial = {arreglo[1],arreglo[0]};
       short shortFinal = (short)indiceFinal;
       arreglo = UtilGasesModelo.toBytes(shortFinal);
       byte[] arregloFinal = {arreglo[1],arreglo[0]};
       byte[] arregloDatos = {arregloInicial[0],arregloInicial[1],arregloFinal[0],arregloFinal[1] };
       msjOpacimetro.setDatos(arregloDatos);
       byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
       MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
       
       return respuesta.getDatos();
   }//end of method datosRTTrigger


  /**Este comando solamente se puede usar para realizar pruebas de linealidad
   * con atenuadores
   *
   * @return un dato en bruto ( es decir sin filtrar, no tiene corrección
   * de temperatura de gas, no esta EOPL corregida)
   */
   public int obtenerDatosBruto(){
       MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
       msjOpacimetro.setByteDelComando((byte)0x8b);
       byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
       MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
       short shortDato = UtilGasesModelo.fromBytesToShort(respuesta.getDatos());
       return shortDato;


   }//end of method obtenerDatosBruto

   /**
    *
    * Este comando solamente se puede usar para realizar pruebas de linealidad
    * con atenuadores
    * @return una medicion que consiste en dato y estatus
    */

    @Override
   public MedicionOpacidad obtenerDatosEstatusBruto(){
       MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
       msjOpacimetro.setByteDelComando((byte)0x8C);
       byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
       MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
       MedicionOpacidad medicion = new MedicionOpacidad(respuesta.getDatos());
       
       return armarBytesEstado(medicion);
   }

   /**
    * Un bloque son 50 datos o palabras, una palabra son dos bytes
    *
    * @param numeroBloques
    * @return
    */


   public int[] obtenerBloquesDatos(int numeroBloques){
       if(numeroBloques < 0 || numeroBloques > 9)
           return null;
       int datos[];
       String strNumeroBloques = Integer.toString(numeroBloques);
       String strComandoHexa = "8"+strNumeroBloques;
       int comandoInt = Integer.parseInt(strComandoHexa, 16);
       String byteStr = Integer.toBinaryString(comandoInt&0xFF);
       byte comandoByte = (byte)Integer.parseInt(byteStr,2);
       MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
       msjOpacimetro.setByteDelComando( comandoByte );
       byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
       MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
       int i = 0;
       byte[] arregloTemp = new byte[2];
       datos = new int[respuesta.getDatos().length];
       for(byte b: respuesta.getDatos()){
           arregloTemp[0] = respuesta.getDatos()[i+1];
           arregloTemp[1] = respuesta.getDatos()[i];
           datos[i]= UtilGasesModelo.fromBytesToShort(arregloTemp);
           i++;
       }//end for
       return datos;
   }//end of method obtenerDatosEnBloque

   /**
    * Inicializa el LCS con los siguientes parámetros del sistema. Esos parámetros son actualizados (recalculados) o el valor por defecto dado en [] son almacenados en la EEPROM

-	Umbral de temperatura de gas [50 °C]  Eeprom Addres 25
-	Umbral de Ventana Limpia [10%]    Eeprom addres 26
-	Maximum_Intensity_Span                 Eeprom addres 27
-	Intensidad Media(%*10, valore bruto)  Eeprom addres 29
-	Vline_Offset				-> Eeprom Address 31
-		- Right_Fan_Offset			-> Eeprom Address 33
-		- Left_Fan_Offset 			-> Eeprom Address 35
-		- Thermistor_Specification		-> Eeprom Address 41
Este comando solamente es para propósitos de producción y servicio
    */

   public void inicializarLCS(){
    MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
    msjOpacimetro.setByteDelComando((byte)0x99);
    byte[] tramaRespuesta = enviarRecibirTrama(UtilGasesModelo.armarTramaOpacimetro(msjOpacimetro));
    MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
    System.out.println("Respuesta desde el LCS:" + respuesta.toString());
   }//end of method inicializarLCS

    public void setPort(SerialPort port) {
        try {
            this.port = port;
            this.out = port.getOutputStream();
            this.in = port.getInputStream();
        } catch (IOException ex) {
            System.out.println("Error configurando el puerto serial y los flujos de entrada salida");
        }
    }//end of setPort method

    public SerialPort getPort() {
        return port;
    }

    private MedicionOpacidad armarMedicion( byte[] datos){
       MedicionOpacidad medicion = new MedicionOpacidad();
      
        byte msbOpacidad = datos[0];
        byte lsbOpacidad = datos[1];
        byte[] arreglo = {lsbOpacidad,msbOpacidad};
        medicion.setOpacidad( UtilGasesModelo.fromBytesToShort(arreglo) );
        medicion.setTemperaturaGas( datos[2] );
        medicion.setTemperaturaTubo( datos[3] );
        byte[] estadoBytes = {datos[5],datos[4]};
        short shortEstado = UtilGasesModelo.fromBytesToShort(estadoBytes);
        boolean[] estado = UtilGasesModelo.shortToFlags(shortEstado);
        
        medicion.setTempAmbFueraTolerancia(estado[0]);
        medicion.setTempDetectFueraTolerancia( estado[1] );
        medicion.setTempTuboFueraTolerancia( estado[2] );
        medicion.setVoltLineaFueraTolerancia( estado[ 3] );
        medicion.setCalibracionProgreso( estado[8] );
        medicion.setLimpiarVentanas( estado[9] );
        medicion.setArmadoRecoleccion( estado[10]);
        medicion.setRecoleccionDisparada( estado[11] );
        
        return medicion;
    }

    @Override
    public long obtenerSerial() {      
        
        MensajeOpacimetro mensaje = new MensajeOpacimetro();
        mensaje.setByteDelComando((byte)'m');
        mensaje.setDatos(new byte[]{37,4});
        byte[] tramaParaEnviar = UtilGasesModelo.armarTramaOpacimetro(mensaje);
        byte[] tramaRespuesta = enviarRecibirTrama(tramaParaEnviar);
        MensajeOpacimetro respuesta = UtilGasesModelo.armarMensajeOpacimetro(tramaRespuesta);
        byte[] datos = respuesta.getDatos();       
        String suno = obtenerBinario(datos[0]);
        String sdos = obtenerBinario(datos[1]);
        String stres = obtenerBinario(datos[2]);
        String scuatro = obtenerBinario(datos[3]);
        
        StringBuilder sbNumeroTotal = new StringBuilder(suno);
        sbNumeroTotal.append(sdos);
        sbNumeroTotal.append(stres);
        sbNumeroTotal.append(scuatro);        
        
        long serial = Long.parseLong(sbNumeroTotal.toString(),2);
        return serial;          
    }

    private MedicionOpacidad armarBytesEstado(MedicionOpacidad medicion) {
        //Agregar el pedazo que falta del status
        short shortEstado = medicion.getShortEstado();
        boolean [] estado = UtilGasesModelo.shortToFlags(shortEstado);
        
        //Las banderas o bits de estado son diferentes en cada opacimetro, para el caso de Sensors no estaba configurado
        medicion.setTempAmbFueraTolerancia( estado[0] ) ;
        medicion.setTempDetectFueraTolerancia( estado[1] );
        medicion.setTempTuboFueraTolerancia( estado[2] );
        medicion.setVoltLineaFueraTolerancia( estado[3] );
        medicion.setCalibracionProgreso( estado[8] );
        medicion.setLimpiarVentanas( estado[9] );
        medicion.setArmadoRecoleccion( estado[10] );
        medicion.setRecoleccionDisparada(estado[11]);     
        
        return medicion;
    }

    private String obtenerBinario(byte b){
        //ej : 1111
        String cadenaLSB = Integer.toBinaryString(b&0xFF);//remiendo bug de programa
        StringBuilder lsb = new StringBuilder();
        for(int i = cadenaLSB.length()-1; i < 7; i++){
            lsb.append('0');
        }//hace una cadena con los ceros que faltan
        //0000
        lsb.append(cadenaLSB);
        //a la cadena con los ceros que faltan le pone la representacion original
        //00001111
        return lsb.toString();
    }

}//end of class OpacimetroSensors
