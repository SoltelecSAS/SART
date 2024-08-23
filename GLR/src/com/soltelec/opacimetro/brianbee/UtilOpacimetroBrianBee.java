package com.soltelec.opacimetro.brianbee;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilOpacimetroBrianBee             // 2, 79, 80, 65, 23, 51, 49, 23 asumimos que es para comunicvar cali
{                                              //  2, 79, 80, 65, 23, 48, 23 para comunicar buena ventura adrees 0
                                           
  private static final byte[] INICIO_COMANDO = {0x02, 0x4F, 0x50, 0x41, 0x17, 0x31, 0x17, 0x53, 0x42, 0x44, 0x31, 0x03}; // STX, OPA, ETB, 1, ETB

  public static byte[] armarTramaOpacimetroBrianBee(MensajeOpacimetroBrianBee mensaje) {
      List<Byte> listaTrama = copiarEncabezadoTrama();

      // Añadir el comando
      byte[] bytesComando = convertirABytes(mensaje.getComando());
      listaTrama = aniadirBytes(listaTrama, bytesComando);

      // Añadir datos si existen
      List<String> listaDatos = mensaje.getDatos();
      if (!listaDatos.isEmpty()) {
          Byte[] tramaDatos = armarTramaDatos(listaDatos);
          listaTrama = aniadirBytes(listaTrama, tramaDatos);
      }

      // Calcular checksum
      String checksum = calcularCheckSum(listaTrama);
      byte[] bytesChecksum = convertirABytes(checksum);
      listaTrama = aniadirBytes(listaTrama, bytesChecksum);

      // Añadir ETX (End of Text)
      Byte etx = 0x03;
      listaTrama.add(etx);

      // Convertir la lista de bytes a un array de bytes
      byte[] tramaDeBytes = new byte[listaTrama.size()];
      for (int i = 0; i < tramaDeBytes.length; i++) {
          tramaDeBytes[i] = listaTrama.get(i).byteValue();
      }
      return tramaDeBytes;
  }

  private static List<Byte> copiarEncabezadoTrama() {
      List<Byte> listaTrama = new ArrayList<>();
      for (byte b : INICIO_COMANDO) {
          listaTrama.add(b);
      }
      return listaTrama;
  }

  private static byte[] convertirABytes(String valor) {
      // Implementa la conversión de String a array de bytes (dependiendo de la codificación que necesites)
      return valor.getBytes();
  }

  private static List<Byte> aniadirBytes(List<Byte> lista, byte[] bytes) {
      for (byte b : bytes) {
          lista.add(b);
      }
      return lista;
  }

  private static Byte[] armarTramaDatos(List<String> datos) {
      // Implementa la construcción de la trama de datos
      // Convierte cada dato en bytes y añádelos a la lista
      List<Byte> tramaDatos = new ArrayList<>();
      for (String dato : datos) {
          byte[] bytesDato = convertirABytes(dato);
          for (byte b : bytesDato) {
              tramaDatos.add(b);
          }
      }
      return tramaDatos.toArray(new Byte[0]);
  }

  private static String calcularCheckSum(List<Byte> listaTrama) {
      // Implementa el cálculo del checksum aquí
      // El checksum se basa en la suma de los bytes en la trama
      int sum = 0;
      for (Byte b : listaTrama) {
          sum += b & 0xFF; // Asegúrate de que el byte sea tratado como un valor positivo
      }
      sum = sum & 0xFFFF; // Asegúrate de que el checksum sea de 16 bits
      return String.format("%04X", sum); // Retorna el checksum en formato hexadecimal
  }

  /* private static byte[] convertirABytes(String cadena) {
    byte[] trama = null;
    try {
      trama = cadena.getBytes("US-ASCII");
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace(System.err);
    }
    return trama;
  } */

  /* private static List<Byte> copiarEncabezadoTrama() {
    List lista = new ArrayList();
    byte[] arrayOfByte = INICIO_COMANDO;
    System.out.println("Tomo Cabecera de inicio de Comando:");
    int i = arrayOfByte.length;
    for (int j = 0; j < i; j++) {
      Byte b = Byte.valueOf(arrayOfByte[j]);
      lista.add(b);
    }
    return lista;
  } */

  /* private static List<Byte> aniadirBytes(List<Byte> lista, byte[] trama) {
    byte[] arrayOfByte = trama;
    int i = arrayOfByte.length;
    for (int j = 0; j < i; j++) {
      Byte b = Byte.valueOf(arrayOfByte[j]);
      lista.add(b);
    }
    return lista;
  } */

  private static List<Byte> aniadirBytes(List<Byte> lista, Byte[] trama) {
    lista.addAll(Arrays.asList(trama));
    return lista;
  }

  /* private static Byte[] armarTramaDatos(List<String> listaDatos) {
    List listaTrama = new ArrayList();
    for (String cadena : listaDatos) {
      listaTrama.add(Byte.valueOf((byte)23));
      byte[] bytesCadena = convertirABytes(cadena);
      byte[] arrayOfByte1 = bytesCadena;
      int i = arrayOfByte1.length;
      for (int j = 0; j < i; j++) {
        Byte b = Byte.valueOf(arrayOfByte1[j]);
        listaTrama.add(b);
      }
    }
    Byte[] b = new Byte[listaTrama.size()];
    return (Byte[])listaTrama.toArray(b);
  } */

  public static MensajeOpacimetroBrianBee armarMensajeOpacimetroBrianBee(byte[] trama) {
    if (trama == null) {
      return null;
    }
    int contadorOcurrencias = 0;
    List listaEncontrados = new ArrayList();

    List temporal = new ArrayList();
    for (int i = 0; i < trama.length - 3; i++) {
      if ((trama[i] == 23) && (contadorOcurrencias == 0))
        contadorOcurrencias = 1;
      else if (trama[i] == 23)
        try {
          byte[] buffer = new byte[temporal.size()];
          for (int j = 0; j < temporal.size(); j++) {
            buffer[j] = ((Byte)temporal.get(j)).byteValue();
          }
          listaEncontrados.add(new String(buffer, "US-ASCII"));
          temporal.clear();
        } catch (UnsupportedEncodingException ex) {
          Logger.getLogger(UtilOpacimetroBrianBee.class.getName()).log(Level.SEVERE, null, ex);
        }
      else if (contadorOcurrencias != 0) {
        temporal.add(Byte.valueOf(trama[i]));
      }
      if ((i == trama.length - 4) && (!temporal.isEmpty())) {
        try
        {
          byte[] buffer = new byte[temporal.size()];
          for (int j = 0; j < temporal.size(); j++) {
            buffer[j] = ((Byte)temporal.get(j)).byteValue();
          }
          listaEncontrados.add(new String(buffer, "US-ASCII"));
          temporal.clear();
        } catch (UnsupportedEncodingException ex) {
          Logger.getLogger(UtilOpacimetroBrianBee.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee((String)listaEncontrados.get(1));
    listaEncontrados.remove(0);
    listaEncontrados.remove(0);
    mensaje.setDatos(listaEncontrados);
    return mensaje;
  }

  /* private static String calcularCheckSum(List<Byte> listaTrama) {
    int suma = 0;
    for (int i = 1; i < listaTrama.size(); i++) {
      suma += ((Byte)listaTrama.get(i)).byteValue();
    }
    suma &= 255;
    String chkSum = Integer.toHexString(suma).toUpperCase();
    System.out.println("Longitud de la cadena version Test 1:" + chkSum.length());
    if (chkSum.length() == 1) {
      chkSum = chkSum + 0 + chkSum;
    }
    if (chkSum.length() > 2) {
      chkSum = chkSum.substring(1);
    }
    return chkSum;
  } */

  public static void main(String[] args) {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("VA");
    byte[] trama = armarTramaOpacimetroBrianBee(mensaje);
    for (byte b : trama) {
      System.out.print(Integer.toHexString(b).toUpperCase() + " ");
    }
    System.out.println("");
    for (byte b : trama)
      System.out.print((short)b + " ");
  }
}