/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.soltelec.procesosopacimetro.MensajeOpacimetro;

/**
 *
 * @author Usuario
 */
public class UtilGasesModelo {

    private static boolean correrHaciaAtras;

    public static byte[] armarBytes(Mensaje mensaje) {
        //Necesito que esta clase me permita construir la trama para enviarla
        byte[] arrayTemporal = new byte[2];
        byte[] arrayParaEnviar;
        List<Byte> listaEnviar = new ArrayList(); //lista para poner los bytes a enviar ???? a bajo nivel debo de haerlo
        //Numero de Comando corresponde al primer byte
        listaEnviar.add((byte) mensaje.getCodigoComando());
        //Modo de respuesta
        listaEnviar.add((byte) mensaje.getNumModoComando());
        //numero de datos
        arrayTemporal = toBytes(mensaje.getNumeroDeDatos());
        listaEnviar.add(arrayTemporal[0]);
        listaEnviar.add(arrayTemporal[1]);
        //poner los datos
        if (mensaje.getNumeroDeDatos() > 0 && mensaje.getDatos() != null) {
            for (short dato : mensaje.getDatos()) {
                arrayTemporal = toBytes(dato);
                listaEnviar.add(arrayTemporal[0]);
                listaEnviar.add(arrayTemporal[1]);
            }//end of for
        }//end if
        //calcular el checksum y ponerlo
        arrayTemporal = toBytes(mensaje.getChecksum());
        listaEnviar.add(arrayTemporal[0]);
        listaEnviar.add(arrayTemporal[1]);
        //La lista esta completa es momento de enviarla
        arrayParaEnviar = new byte[listaEnviar.size()];//el arreglo es del tamaño adecuado
        int contador = 0;
        for (Byte unByte : listaEnviar) {
            arrayParaEnviar[contador++] = unByte.byteValue();
        }//end for
        return arrayParaEnviar;
    }//end of method ModosDeRespuesta

    /**
     * Retorna los dos bytes que componene el short el primero del arreglo es el
     * menos significativo
     *
     * @param s
     * @return
     */
    public static byte[] toBytes(short s) {//el primero del arreglo es el menos significativo
        return new byte[]{(byte) (s & 0x00FF), (byte) ((s & 0xFF00) >> 8)};
    }//end of methods toBytes

    public static short fromBytesToShort(byte bajo, byte alto) {
        return fromBytesToShort(new byte[]{bajo, alto});
    }

    /**
     * REcibe un erreglo de dos bytes que representan un short o un entero. El
     * primer byte b[0] es el menos significativo o bajo, el segundo byte b[1]
     * es el mas significativo o alto.
     *
     * @param b
     * @return
     */
    public static short fromBytesToShort(byte b[]) {
        //pasar de bytes a cadena tampoco funciona
        String cadenaLSB = Integer.toBinaryString(b[0] & 0xFF);//remiendo bug de programa
        StringBuilder lsb = new StringBuilder();
        for (int i = cadenaLSB.length() - 1; i < 7; i++) {
            lsb.append('0');
        }//rellenar de ceros
        lsb.append(cadenaLSB);
        //System.out.println("Primer byte: " + lsb);
        String cadenaMSB = Integer.toBinaryString(b[1] & 0xFF);
        StringBuilder msb1 = new StringBuilder();
        for (int i = cadenaMSB.length() - 1; i < 7; i++) {
            msb1.append('0');
        }//rellenar de ceros
        msb1.append(cadenaMSB);
        //System.out.println("Segundo byte:" + msb1);
        //System.out.println("Conversion:" + cadenaMSB+cadenaLSB);
        short numero = (short) Integer.parseInt((msb1.append(lsb)).toString(), 2);
        return numero;
    }//end of methid fromBytesToShort

    public static int bytesToInt(byte bajo1, byte bajo2, byte bajo3, byte bajo4) {

        StringBuilder cadenaEntero = new StringBuilder();
        String cadenaBajo1 = byteToFullString(bajo4);
        cadenaEntero.append(cadenaBajo1);
        String cadenaBajo2 = byteToFullString(bajo3);
        cadenaEntero.append(cadenaBajo2);
        String cadenaBajo3 = byteToFullString(bajo2);
        cadenaEntero.append(cadenaBajo3);
        String cadenaBajo4 = byteToFullString(bajo1);
        cadenaEntero.append(cadenaBajo4);
        System.out.println("Valor Entero : " + cadenaEntero);
        int valor = Integer.parseInt(cadenaEntero.toString(), 2);
        return valor;
    }

    public static String byteToFullString(byte numero) {

        String cadena = Integer.toBinaryString(numero & 0xFF);
        StringBuilder lsb = new StringBuilder();
        for (int i = cadena.length() - 1; i < 7; i++) {
            lsb.append('0');
        }//rellenar de ceros
        lsb.append(cadena);
        return lsb.toString();
    }

    public static Mensaje armarMensaje(byte bytesRecibidos[]) {
        try {
            System.out.println("Numero de Bytes: " + bytesRecibidos.length);
            byte[] temporal = new byte[2];
            Mensaje mensaje = new Mensaje();///ojo con el constructor
            mensaje.setCodigoComando(bytesRecibidos[0]);
            System.out.println("Numero de comando:" + mensaje.getCodigoComando());
            byte modoComando = bytesRecibidos[1];
            if (modoComando > 15) {
                System.out.println("Correr los datos hacia adelante");
                System.arraycopy(bytesRecibidos, 0, bytesRecibidos, 1, bytesRecibidos.length - 1);
            }
            modoComando = bytesRecibidos[1];
            mensaje.setNumModoComando(bytesRecibidos[1]);
            System.out.println("Modo del comando:" + mensaje.getNumModoComando());
            //armar el numero de datos
            temporal[0] = bytesRecibidos[2];
            temporal[1] = bytesRecibidos[3];
            short numeroPalabras = fromBytesToShort(temporal);
            if (numeroPalabras > bytesRecibidos.length || correrHaciaAtras) {//el modo de comando es 0 o es 1 no existe otro
                System.out.println("Error de comunicacion : correr los datos hacia atras");
                correrHaciaAtras = true;
                //correr el mensaje hacia atras
                byte b[] = new byte[bytesRecibidos.length - 1];
                System.arraycopy(bytesRecibidos, 1, bytesRecibidos, 0, bytesRecibidos.length - 1);//ya lo corri hacia la izquierda
                mensaje.setCodigoComando(bytesRecibidos[0]);
                System.out.println("Numero de comando:" + mensaje.getCodigoComando());
                mensaje.setNumModoComando(bytesRecibidos[1]);
                System.out.println("Modo del comando:" + mensaje.getNumModoComando());
                temporal[0] = bytesRecibidos[2];
                temporal[1] = bytesRecibidos[3];
                numeroPalabras = fromBytesToShort(temporal);
                System.out.println("Numero da palabras nuevo: " + numeroPalabras);
            }
            if (numeroPalabras > bytesRecibidos.length) {//el modo de comando es 0 o es 1 no existe otro
                System.out.println("Error de comunicacion : correr los datos hacia atras");
                correrHaciaAtras = true;
                //correr el mensaje hacia atras
                byte b[] = new byte[bytesRecibidos.length - 1];
                System.arraycopy(bytesRecibidos, 1, bytesRecibidos, 0, bytesRecibidos.length - 1);//ya lo corri hacia la izquierda
                mensaje.setCodigoComando(bytesRecibidos[0]);
                System.out.println("Numero de comando:" + mensaje.getCodigoComando());
                mensaje.setNumModoComando(bytesRecibidos[1]);
                System.out.println("Modo del comando:" + mensaje.getNumModoComando());
                temporal[0] = bytesRecibidos[2];
                temporal[1] = bytesRecibidos[3];
                numeroPalabras = fromBytesToShort(temporal);
                System.out.println("Numero da palabras nuevo: " + numeroPalabras);
            }
            mensaje.setNumeroDeDatos(numeroPalabras);
            //armar los datos
            short[] datos = new short[mensaje.getNumeroDeDatos()];
            System.out.println("numero de datos recibidos:" + mensaje.getNumeroDeDatos());
            int contadora = 3;
            for (int i = 0; i < mensaje.getNumeroDeDatos(); i++) {
                for (int j = 0; j < 2; j++) {
                    ++contadora;
                    if (contadora >= bytesRecibidos.length) {
                        break;
                    }
                    temporal[j] = bytesRecibidos[contadora];
                }//end for
                datos[i] = fromBytesToShort(temporal);
            }//end outer for
            //Probar este código con un arregl ode bytes
            mensaje.setDatos(datos);
            //comprobar el checksum que son los dos bytes después de los datos
            if (contadora < bytesRecibidos.length) {
                temporal[0] = bytesRecibidos[++contadora];
                contadora++;
                if (contadora < bytesRecibidos.length) {
                    temporal[1] = bytesRecibidos[contadora];
                }
            } else {

            }
            short chksum = fromBytesToShort(temporal);
            mensaje.setChecksum(chksum);
            short chksumCalculado = Mensaje.obtenerCheckSum(
                    mensaje.getCodigoComando(), mensaje.getNumModoComando(),
                    mensaje.getNumeroDeDatos(), mensaje.getDatos());
            if (chksum == chksumCalculado) {
                System.out.println("ChecksumConcuerdan");
            }//end if
            return mensaje;
        } catch (Exception exc) {
            System.out.println("Error armando el mensaje");
            exc.printStackTrace();
            return null;
        }

    }//end of method armarMensaje

    //puede generar nullpointerexception
    public static MensajeOpacimetro armarMensajeOpacimetro(byte[] recibidos) {

        if (recibidos != null) {

            MensajeOpacimetro msjOpacimetro = new MensajeOpacimetro();
            msjOpacimetro.setComando((char) recibidos[0]);
            msjOpacimetro.setByteDelComando(recibidos[0]);
            //hacer una rutina para determinar los datos;
            //los datos comprenden las posiciones desde 1 hasta
            //la del arreglo longitud -1
            int startIndex = 1;
            int endIndex = recibidos.length - 1;
            //llenar el arreglo de enteros
            if (endIndex > startIndex) {
                byte[] datosBytes = new byte[endIndex - startIndex];
                int contador = 0;
                for (int i = startIndex; i < endIndex; i++) {
                    datosBytes[contador++] = recibidos[i];
                }//end for
                msjOpacimetro.setDatos(datosBytes);
            }//end if
            else {
                //la respuesta no incluye datos
                msjOpacimetro.setDatos(new byte[0]);//null para que no retorne nada
            }
            msjOpacimetro.setChecksum(recibidos[endIndex]);
            return msjOpacimetro;
        } else {
            return null;
        }

    }//

    public static byte[] armarTramaOpacimetro(MensajeOpacimetro msjOpacimetro) {
        if (msjOpacimetro != null) {
            byte[] datos = msjOpacimetro.getDatos();
            int longitudTrama = 0;
            byte[] trama;
            if (datos != null && datos.length != 0) {
                longitudTrama = datos.length + 2;//el byte del comando y el checksum
            } else if (datos.length == 0) {
                longitudTrama = 2;
            }//end else
            trama = new byte[longitudTrama];
            trama[0] = msjOpacimetro.getByteDelComando();
            for (int i = 0; i < datos.length; i++) {
                trama[i + 1] = datos[i];//
            }//
            trama[longitudTrama - 1] = msjOpacimetro.getChecksum();
            for (byte b : trama) {
                System.out.print("byte trama " + b);
            }
            return trama;
//        byte[] tramaTemporal = new byte[longitudTrama - 1];
//        System.arraycopy(trama,0,tramaTemporal,0, longitudTrama - 1);
//        trama[longitudTrama -1] = calcularCheckSumOpacimetro(tramaTemporal);
//        return trama;
        } else {
            System.out.println("Null armando el mensaje");
            return null;
        }
    }//end of method

    /**
     * Va de derecha a izquierda, de msb a lsb el mas derecho es el menos
     * significativo
     *
     * @param s
     * @return
     */
    public static boolean[] shortToFlags(short s) {
        //convertir el short a una cadena;
        boolean[] arregloFlags = new boolean[16];//un short son 16 bits

        for (int i = 0; i < arregloFlags.length; i++) {
            arregloFlags[i] = false;
        }
        String representacionBinaria = Integer.toString(s & 0xFFFF, 2);
        //System.out.println("el numero es: " + s +" Su representacion binaria:" + representacionBinaria);
        //pasar a arreglo de caracteres o usar charAt

        char[] caracteres = representacionBinaria.toCharArray();

        for (int i = 0; i < caracteres.length; i++) {
            arregloFlags[i] = caracteres[(caracteres.length - 1) - i] == '1';
        }//end for
        //System.out.println("El arreglo correspondiente " );

//        for(boolean b: arregloFlags){
//            System.out.print(b + " ");
//        }//end for
        //System.out.print("\n");
        return arregloFlags;
    }//end of method shortToFlags

    /**
     * Metodo para convertir un byte con signo a un entero sin signo es decir
     * sei el numero es -64 debo sera 255 - 64 =
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        //lo paso a cadena :
        String cadenaLSB = Integer.toBinaryString(b & 0xFF);//remiendo bug de programa
        StringBuilder lsb = new StringBuilder();
        for (int i = cadenaLSB.length() - 1; i < 7; i++) {
            lsb.append('0');
        }//rellenar de ceros
        lsb.append(cadenaLSB);
        //pasar a entero
        int valor = Integer.parseInt(cadenaLSB, 2);
        return valor;
    }

    /**
     *
     * @param s
     * @return
     */
    public static String shortToString(short s) {
        String shortBinario = Integer.toString(s & 0xFFFF, 2);
        return shortBinario;
    }//end of method shortToString

    public static byte calcularCheckSumOpacimetro(byte[] arreglo) {
        int suma = 0;
        for (byte b : arreglo) {
            int numero = UtilGasesModelo.byteToInt(b);
            suma = suma + numero;
        }

        //tengo la suma
        int mod256 = suma % 256;
        int checksumInt = 0 - mod256;
        //pasarlo a  byte
        byte checksum = (byte) checksumInt;
        //System.err.println(checksum);
        return checksum;
    }//end of method

    public static float byteStringArrayToFloat(byte[] b) {

        String strNumero = new String(b);

        float numero = -1;

        numero = Float.parseFloat(strNumero);

        return numero;

    }

    public static float fromByteArrayToFloat(byte[] b) {
        if (b.length != 4) {
            return 0.0F;
        }

        // Same as DataInputStream's 'readInt' method
        int i = (((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8 | (b[3] & 0xff)));

        return Float.intBitsToFloat(i);
    }

    public static short fromFlagsToShort(boolean[] arregloBits) {

        StringBuilder sb = new StringBuilder();

        for (boolean b : arregloBits) {
            sb.append(b ? "1" : "0");
        }
        sb.reverse();
        return (short) Integer.parseInt(sb.toString(), 2);

    }//end of method 

    /**
     * Cambioa de posicion los dos bytes del arreglo
     *
     * @param arreglo
     * @return
     */
    public static byte[] invertirArreglo(byte[] arreglo) {

        byte temp = arreglo[1];
        arreglo[1] = arreglo[0];
        arreglo[0] = temp;
        return arreglo;
    }

}//end of class
