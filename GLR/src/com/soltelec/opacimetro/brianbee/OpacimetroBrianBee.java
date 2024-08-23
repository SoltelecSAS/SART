package com.soltelec.opacimetro.brianbee;

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import org.apache.log4j.Logger;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.UtilGasesModelo;

import com.soltelec.servidor.utils.CMensajes;

public class OpacimetroBrianBee
  implements Opacimetro
{
  private SerialPort serialPort;
  private InputStream in;
  private OutputStream out;
  private static final double LTOE = 0.43D;

  public void standBy()
  {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("SB");
    byte[] trama = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);

    byte[] respuesta = enviarRecibirTrama(trama);

    CMensajes.mensajeCorrecto("La respuesta del opacimetro es: "+respuesta);

    mensaje = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(respuesta);
  }

  public void armarLCS() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void calibrar() {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("AZ");
    byte[] trama = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);
    byte[] respuesta = enviarRecibirTrama(trama);
  }

  public byte[] datosRTTrigger(int indiceInicial, int indiceFinal) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void desarmarTrigger() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void dispararTrigger() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public SerialPort getPort() {
    return this.serialPort;
  }

  public int numeroDatosAdquiridos() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public byte[] obtenerArregloDatosOpacidad() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MedicionOpacidad obtenerDatos() {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("VA");
    byte[] tramaComando = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);
    byte[] tramaRespuesta = enviarRecibirTrama(tramaComando);
    MensajeOpacimetroBrianBee mensajeRespuesta = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(tramaRespuesta);
    MedicionOpacidad medicion = new MedicionOpacidad();
    System.out.println("Listado de Datos Obtenidos Metodos obtenerDatos: " );
    int i=1;
    for(String datos: mensajeRespuesta.getDatos()){
        System.out.println(" Datos Recojidos pos "+i+" es: "+datos );
        i++;
    }
    double transmitancia =0.0;
   try{
        transmitancia = Double.parseDouble((String)mensajeRespuesta.getDatos().get(0));
   }catch(NumberFormatException ex){
       System.out.println(" no tome transmitancia no es numeric lo que entrega es: "+ mensajeRespuesta.getDatos().get(0));  
    } 
    medicion.setDensidadHumo(transmitancia);
    double opacidad = (1.0D - Math.exp(-transmitancia * 0.43D)) * 100.0D;
    System.out.println("Opacidad new: " + opacidad);
    medicion.setOpacidad((short)(int)(opacidad * 10.0D));
    mensaje = null;
    mensaje = new MensajeOpacimetroBrianBee("ST");
    tramaComando = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);
    tramaRespuesta = enviarRecibirTrama(tramaComando);

    byte status = tramaRespuesta[11];

    boolean[] flags = UtilGasesModelo.shortToFlags((short)UtilGasesModelo.byteToInt(status));
    medicion.setCalibracionProgreso(flags[2]);
    medicion.setZeroEnProgreso(flags[2]);
    medicion.setStandBy(flags[1]);
    medicion.setTempTuboFueraTolerancia(flags[0]);

    mensajeRespuesta = null;
    mensajeRespuesta = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(tramaRespuesta);

    return medicion;
  }

  public int obtenerDatosBruto() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public MedicionOpacidad obtenerDatosEstatusBruto() {
    return obtenerDatos();
  }

  private MedicionOpacidad armarBytesEstado(MedicionOpacidad medicion) {
    short shortEstado = medicion.getShortEstado();
    boolean[] estado = UtilGasesModelo.shortToFlags(shortEstado);

    medicion.setTempAmbFueraTolerancia(estado[0]);
    medicion.setTempDetectFueraTolerancia(estado[1]);
    medicion.setTempTuboFueraTolerancia(estado[2]);
    medicion.setVoltLineaFueraTolerancia(estado[3]);
    medicion.setCalibracionProgreso(estado[8]);
    medicion.setLimpiarVentanas(estado[9]);
    medicion.setArmadoRecoleccion(estado[10]);
    medicion.setRecoleccionDisparada(estado[11]);

    return medicion;
  }

  public int obtenerFrecuenciaMuestreo() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public int[] obtenerInfoSoftware() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void setPort(SerialPort port) {
    try {
      this.serialPort = port;
      this.out = this.serialPort.getOutputStream();
      this.in = this.serialPort.getInputStream();
    } catch (IOException ex) {
      Logger.getRootLogger().error(ex);
    }
  }

  public long obtenerSerial() {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("ID");
    byte[] trama = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);
    byte[] respuesta = enviarRecibirTrama(trama);
    imprimirRespuesta(respuesta);
    mensaje = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(respuesta);
    return Long.parseLong((String)mensaje.getDatos().get(3));
  }

  public void resetearValoresPico() {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("AP");
    byte[] trama = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);
    byte[] respuesta = enviarRecibirTrama(trama);
  }

  public MedicionOpacidad obtenerOpacidad() {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("VA");
    byte[] tramaComando = UtilOpacimetroBrianBee.armarTramaOpacimetroBrianBee(mensaje);
    byte[] tramaRespuesta = enviarRecibirTrama(tramaComando);
    MensajeOpacimetroBrianBee mensajeRespuesta = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(tramaRespuesta);
    MedicionOpacidad medicion = new MedicionOpacidad();
     System.out.println("Listado de Datos Obtenidos Metodo ObtenerOpacidad: " );
     int i=1;
    for(String datos: mensajeRespuesta.getDatos()){
        System.out.println(" Datos Recojidos pos "+i+" es: "+datos );
        i++;
    }
    double transmitancia = Double.parseDouble((String)mensajeRespuesta.getDatos().get(0));
    System.out.println("transmitancia: " + transmitancia);
    medicion.setDensidadHumo(transmitancia);
    double opacidad = (1.0D - Math.exp(-transmitancia * 0.43D)) * 100.0D;
    System.out.println("Opacidad: " + opacidad);
    medicion.setOpacidad((short)(int)(opacidad * 10.0D));
    return medicion;
  }

  private synchronized byte[] enviarRecibirTrama(byte[] trama) {
    boolean respuestaRecibida = false;
    byte[] buffer = null;
    try {
      for (byte b : trama) {
        System.out.print(b + " ");
      }
      System.out.println("");
      this.out.write(trama);
    } catch (IOException ex) {
      System.out.println("Excepcion enviando datos");
      Logger.getRootLogger().error("Excepcion enviando datos", ex);
      return buffer;
    }
    try {
      Thread.sleep(50L);
    } catch (InterruptedException ex) {
    }
    int contador = 0;
    buffer = new byte[2048];
    while ((!respuestaRecibida) && (contador < 4)) {
    
      try {
        int len = 0;
        int data;
        while ((data = this.in.read()) > -1) {
          buffer[(len++)] = ((byte)data);
        }
        if (len > 0) {       
          respuestaRecibida = true;
          byte[] arreglo = new byte[len];
          System.arraycopy(buffer, 0, arreglo, 0, len);
          return arreglo;
        }
        System.out.println("Intentos de lectura: " + contador);
        contador++;
      } catch (IOException ioe) {
        System.out.println("Excepcion leyendo datos del opacimetro");
        contador++;
      } catch (ArrayIndexOutOfBoundsException ae) {
        return null;
      }
    }
    return null;
  }

  private void imprimirRespuesta(byte[] respuesta) {
    System.out.println("Respuesta: ");
    for (byte b : respuesta) {
      System.out.print(Integer.toString(b).toUpperCase() + " ");
    }
    System.out.println("");
  }
}