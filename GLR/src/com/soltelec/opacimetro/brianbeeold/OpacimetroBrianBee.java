package com.soltelec.opacimetro.brianbeeold;
 
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
 
 public class OpacimetroBrianBee implements Opacimetro
 {
   private SerialPort serialPort;
   private InputStream in;
   private OutputStream out;
   private static final double LTOE = 0.43D;
   
   public void standBy()
   {
/*  23 */     byte[] trama = { 2, 79, 80, 65, 23, 48, 23, 83, 66, 68, 51, 3 };
/*  24 */     byte[] respuesta = enviarRecibirTrama(trama);
     
/*  26 */     UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(respuesta);
   }
   
   public void armarLCS() {
/*  30 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public void calibrar() {
/*  34 */     byte[] trama = { 2, 79, 80, 65, 23, 48, 23, 65, 90, 68, 57, 3 };
/*  35 */     byte[] respuesta = enviarRecibirTrama(trama);
   }
   
   public byte[] datosRTTrigger(int indiceInicial, int indiceFinal) {
/*  39 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public void desarmarTrigger() {
/*  43 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public void dispararTrigger() {
/*  47 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public SerialPort getPort() {
/*  51 */     return this.serialPort;
   }
   
   public int numeroDatosAdquiridos() {
/*  55 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public byte[] obtenerArregloDatosOpacidad() {
/*  59 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public MedicionOpacidad obtenerDatos() {
/*  63 */     byte[] tramaComando = { 2, 79, 80, 65, 23, 48, 23, 86, 65, 68, 53, 3 };
/*  64 */     byte[] tramaRespuesta = enviarRecibirTrama(tramaComando);
/*  65 */     MensajeOpacimetroBrianBee mensajeRespuesta = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(tramaRespuesta);
/*  66 */     MedicionOpacidad medicion = new MedicionOpacidad();
/*  67 */     double transmitancia = Double.parseDouble((String)mensajeRespuesta.getDatos().get(0));
/*  68 */     System.out.println("transmitancia: " + transmitancia);
/*  69 */     medicion.setDensidadHumo(transmitancia);
/*  70 */     double opacidad = (1.0D - Math.exp(-transmitancia * 0.43D)) * 100.0D;
/*  71 */     System.out.println("Opacidad: " + opacidad);
/*  72 */     medicion.setOpacidad((short)(int)(opacidad * 10.0D));
     
/*  74 */     byte[] tramaComandoST = { 2, 79, 80, 65, 23, 48, 23, 83, 84, 69, 53, 3 };
/*  75 */     tramaRespuesta = enviarRecibirTrama(tramaComandoST);
     
/*  77 */     byte status = tramaRespuesta[10];
     
/*  79 */     boolean[] flags = UtilGasesModelo.shortToFlags((short)UtilGasesModelo.byteToInt(status));
/*  80 */     medicion.setCalibracionProgreso(flags[2]);
/*  81 */     medicion.setZeroEnProgreso(flags[2]);
/*  82 */     medicion.setStandBy(flags[1]);
/*  83 */     medicion.setTempTuboFueraTolerancia(flags[0]);
     
/*  85 */     UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(tramaRespuesta);
     
/*  87 */     return medicion;
   }
   
   public int obtenerDatosBruto() {
/*  91 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public MedicionOpacidad obtenerDatosEstatusBruto() {
/*  95 */     return obtenerDatos();
   }
   
   public int obtenerFrecuenciaMuestreo() {
/*  99 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public int[] obtenerInfoSoftware() {
/* 103 */     throw new UnsupportedOperationException("Not supported yet.");
   }
   
   public void setPort(SerialPort port) {
     try {
/* 108 */       this.serialPort = port;
/* 109 */       this.out = this.serialPort.getOutputStream();
/* 110 */       this.in = this.serialPort.getInputStream();
     } catch (IOException ex) {
/* 112 */       Logger.getRootLogger().error(ex);
     }
   }
   
   public long obtenerSerial() {
/* 117 */     byte[] trama = { 2, 79, 80, 65, 23, 48, 23, 73, 68, 67, 66, 3 };
/* 118 */     byte[] respuesta = enviarRecibirTrama(trama);
/* 119 */     imprimirRespuesta(respuesta);
/* 120 */     MensajeOpacimetroBrianBee mensaje = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(respuesta);
/* 121 */     return Long.parseLong((String)mensaje.getDatos().get(3));
   }
   
   public void resetearValoresPico() {
/* 125 */     byte[] trama = { 2, 79, 80, 65, 23, 48, 23, 65, 80, 67, 70, 3 };
/* 126 */     byte[] respuesta = enviarRecibirTrama(trama);
   }
   
   public MedicionOpacidad obtenerOpacidad() {
/* 130 */     byte[] tramaComando = { 2, 79, 80, 65, 23, 48, 23, 86, 65, 68, 53, 3 };
/* 131 */     byte[] tramaRespuesta = enviarRecibirTrama(tramaComando);
/* 132 */     MensajeOpacimetroBrianBee mensajeRespuesta = UtilOpacimetroBrianBee.armarMensajeOpacimetroBrianBee(tramaRespuesta);
/* 133 */     MedicionOpacidad medicion = new MedicionOpacidad();
/* 134 */     double transmitancia = Double.parseDouble((String)mensajeRespuesta.getDatos().get(0));
/* 135 */     System.out.println("transmitancia: " + transmitancia);
/* 136 */     medicion.setDensidadHumo(transmitancia);
/* 137 */     double opacidad = (1.0D - Math.exp(-transmitancia * 0.43D)) * 100.0D;
/* 138 */     System.out.println("Opacidad: " + opacidad);
/* 139 */     medicion.setOpacidad((short)(int)(opacidad * 10.0D));
/* 140 */     return medicion;
   }
   
   private synchronized byte[] enviarRecibirTrama(byte[] trama) {
/* 144 */     boolean respuestaRecibida = false;
/* 145 */     byte[] buffer = null;
     try {
/* 147 */       for (byte b : trama) {
/* 148 */         System.out.print(b + " ");
       }
/* 150 */       System.out.println("");
/* 151 */       this.out.write(trama);
     } catch (IOException ex) {
/* 153 */       System.out.println("Excepcion enviando datos");
/* 154 */       Logger.getRootLogger().error("Excepcion enviando datos", ex);
/* 155 */       return buffer;
     }
     try {
/* 158 */       Thread.sleep(50L);
     }
     catch (InterruptedException ex) {}
/* 161 */     int contador = 0;
/* 162 */     buffer = new byte[2048];
/* 163 */     while ((!respuestaRecibida) && (contador < 4)) {
/* 164 */       System.out.println("\nLeyendo datos..-..");
       try {
/* 166 */         int len = 0;
         int data;
/* 168 */         while ((data = this.in.read()) > -1) {
/* 169 */           buffer[(len++)] = ((byte)data);
         }
/* 171 */         if (len > 0) {
/* 172 */           System.out.println("Longitud del mensaje:" + len);
/* 173 */           respuestaRecibida = true;
           
/* 175 */           byte[] arreglo = new byte[len];
           
/* 177 */           System.arraycopy(buffer, 0, arreglo, 0, len);
/* 178 */           return arreglo;
         }
/* 180 */         System.out.println("Intentos de lectura: " + contador);
         
/* 182 */         contador++;
       } catch (IOException ioe) {
/* 184 */         System.out.println("Excepcion leyendo datos del opacimetro");
/* 185 */         contador++;
       } catch (ArrayIndexOutOfBoundsException ae) {
/* 187 */         return null;
       }
     }
/* 190 */     return null;
   }
   
   private void imprimirRespuesta(byte[] respuesta) {
/* 194 */     System.out.println("Respuesta: ");
/* 195 */     for (byte b : respuesta) {
/* 196 */       System.out.print(Integer.toString(b).toUpperCase() + " ");
     }
/* 198 */     System.out.println("");
   }
 }


/* Location:              /home/luisberna/Dropbox/maven-soltelec/Sart-git-3/modulos/ModuloGLR.jar!/com/soltelec/opacimetro/brianbee/OpacimetroBrianBee.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */