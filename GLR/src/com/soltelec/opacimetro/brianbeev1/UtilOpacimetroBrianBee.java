package com.soltelec.opacimetro.brianbeev1;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilOpacimetroBrianBee
{                                                                                              
  private static final byte[] INICIO_COMANDO = {2, 79, 80, 65, 23, 49, 23 };

  public static byte[] armarTramaOpacimetroBrianBee(MensajeOpacimetroBrianBee mensaje)
  {
    List listaTrama = copiarEncabezadoTrama();
    byte[] bytesComando = convertirABytes(mensaje.getComando());

    listaTrama = aniadirBytes(listaTrama, bytesComando);

    List listaDatos = mensaje.getDatos();
    if (!listaDatos.isEmpty()) {
      Byte[] tramaDatos = armarTramaDatos(listaDatos);
      listaTrama = aniadirBytes(listaTrama, tramaDatos);
    }
    String checksum = calcularCheckSum(listaTrama);

    byte[] bytesChecksum = convertirABytes(checksum);
    listaTrama = aniadirBytes(listaTrama, bytesChecksum);
    Byte etx = new Byte((byte)3);
    listaTrama.add(etx);
    byte[] tramaDeBytes = new byte[listaTrama.size()];
    for (int i = 0; i < tramaDeBytes.length; i++)
    {
      tramaDeBytes[i] = ((Byte)listaTrama.get(i)).byteValue();
    }

    return tramaDeBytes;
  }

  private static byte[] convertirABytes(String cadena) {
    byte[] trama = null;
    try {
      trama = cadena.getBytes("US-ASCII");
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace(System.err);
    }
    return trama;
  }

  private static List<Byte> copiarEncabezadoTrama()
  {
    List lista = new ArrayList();
    byte[] arr$ = INICIO_COMANDO; int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Byte b = Byte.valueOf(arr$[i$]);
      lista.add(b);
    }
    return lista;
  }

  private static List<Byte> aniadirBytes(List<Byte> lista, byte[] trama)
  {
    byte[] arr$ = trama; int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Byte b = Byte.valueOf(arr$[i$]);
      lista.add(b);
    }
    return lista;
  }

  private static List<Byte> aniadirBytes(List<Byte> lista, Byte[] trama) {
    lista.addAll(Arrays.asList(trama));
    return lista;
  }

  private static Byte[] armarTramaDatos(List<String> listaDatos)
  {
    List listaTrama = new ArrayList();

    for (String cadena : listaDatos) {
      listaTrama.add(Byte.valueOf((byte)23));
      byte[] bytesCadena = convertirABytes(cadena);
      byte[] arr$ = bytesCadena; int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Byte b = Byte.valueOf(arr$[i$]);
        listaTrama.add(b);
      }
    }

    Byte[] b = new Byte[listaTrama.size()];
    return (Byte[])listaTrama.toArray(b);
  }

  public static MensajeOpacimetroBrianBee armarMensajeOpacimetroBrianBee(byte[] trama)
  {
    if (trama == null) {
      return null;
    }

    int contadorOcurrencias = 0;
    List listaEncontrados = new ArrayList();

    List temporal = new ArrayList();
    for (int i = 0; i < trama.length - 3; i++)
    {
      if ((trama[i] == 23) && (contadorOcurrencias == 0)) {
        contadorOcurrencias = 1;
      }
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
      if ((i == trama.length - 4) && 
        (!temporal.isEmpty())) {
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
      }

    }

    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee((String)listaEncontrados.get(1));
    listaEncontrados.remove(0);
    listaEncontrados.remove(0);
    mensaje.setDatos(listaEncontrados);
    return mensaje;
  }

  private static String calcularCheckSum(List<Byte> listaTrama)
  {
    int suma = 0;
    for (int i = 1; i < listaTrama.size(); i++)
    {
      suma += ((Byte)listaTrama.get(i)).byteValue();
    }
    suma &= 255;
    String chkSum = Integer.toHexString(suma).toUpperCase();
    System.out.println("Longitud de la cadena:" + chkSum.length());
    if (chkSum.length() == 1) {
      chkSum = chkSum + 0 + chkSum;
    }
    if (chkSum.length() > 2) {
      chkSum = chkSum.substring(1);
    }
    return chkSum;
  }

  public static void main(String[] args)
  {
    MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("VA");
    byte[] trama = armarTramaOpacimetroBrianBee(mensaje);
    for (byte b : trama)
    {
      System.out.print(Integer.toHexString(b).toUpperCase() + " ");
    }
    System.out.println("");
    for (byte b : trama)
    {
      System.out.print((short)b + " ");
    }
  }
}