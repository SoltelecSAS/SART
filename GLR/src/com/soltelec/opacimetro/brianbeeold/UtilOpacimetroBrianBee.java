package com.soltelec.opacimetro.brianbeeold;


  import java.io.PrintStream;
 import java.io.UnsupportedEncodingException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.logging.Level;
 import java.util.logging.Logger;


 
 
 
 
 
 
 
  public class UtilOpacimetroBrianBee  {

    /*  20 */ private static final byte[] INICIO_COMANDO = {2, 79, 80, 65, 23, 51, 49, 23};
///*  20 */ private static final byte[] INICIO_COMANDO = {2, 79, 80, 65, 23, 48, 23};
                                                         
    
  public static byte[] armarTramaOpacimetroBrianBee(MensajeOpacimetroBrianBee mensaje)  {
        /*  24 */ List<Byte> listaTrama = copiarEncabezadoTrama();
        /*  25 */ byte[] bytesComando = convertirABytes(mensaje.getComando());
        
 /*  27 */ listaTrama = aniadirBytes(listaTrama, bytesComando);
        
 /*  29 */ List<String> listaDatos = mensaje.getDatos();
        /*  30 */ if (!listaDatos.isEmpty()) {
            /*  31 */ Byte[] tramaDatos = armarTramaDatos(listaDatos);
            /*  32 */ listaTrama = aniadirBytes(listaTrama, tramaDatos);
                    }
        /*  34 */ String checksum = calcularCheckSum(listaTrama);
        
 /*  36 */ byte[] bytesChecksum = convertirABytes(checksum);
        /*  37 */ listaTrama = aniadirBytes(listaTrama, bytesChecksum);
        /*  38 */ Byte etx = new Byte((byte) 3);
        /*  39 */ listaTrama.add(etx);
        /*  40 */ byte[] tramaDeBytes = new byte[listaTrama.size()];
        /*  41 */ for (int i = 0; i < tramaDeBytes.length; i++)  {
            /*  43 */ tramaDeBytes[i] = ((Byte) listaTrama.get(i)).byteValue();
                    }
        
 
 /*  47 */ return tramaDeBytes;
            }

    
  private static byte[] convertirABytes(String cadena) {
        /*  51 */ byte[] trama = null;
         try {
            /*  53 */ trama = cadena.getBytes("US-ASCII");
                    } catch (UnsupportedEncodingException ex) {
            /*  55 */ ex.printStackTrace(System.err);
                    }
        /*  57 */ return trama;
            }

    
  private static List<Byte> copiarEncabezadoTrama()  {
        /*  62 */ List<Byte> lista = new ArrayList();
        /*  63 */ byte[] arr$ = INICIO_COMANDO;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Byte b = Byte.valueOf(arr$[i$]);
            /*  64 */ lista.add(b);
                    }
        /*  66 */ return lista;
            }

    
  private static List<Byte> aniadirBytes(List<Byte> lista, byte[] trama)  {
        /*  71 */ byte[] arr$ = trama;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$++) {
            Byte b = Byte.valueOf(arr$[i$]);
            /*  72 */ lista.add(b);
                    }
        /*  74 */ return lista;
            }

    
  private static List<Byte> aniadirBytes(List<Byte> lista, Byte[] trama) {
        /*  78 */ lista.addAll(Arrays.asList(trama));
        /*  79 */ return lista;
            }

    
  private static Byte[] armarTramaDatos(List<String> listaDatos)  {
        /*  84 */ List<Byte> listaTrama = new ArrayList();
        
 /*  86 */ for (String cadena : listaDatos) {
            /*  87 */ listaTrama.add(Byte.valueOf((byte) 23));
            /*  88 */ byte[] bytesCadena = convertirABytes(cadena);
            /*  89 */ byte[] arr$ = bytesCadena;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Byte b = Byte.valueOf(arr$[i$]);
                /*  90 */ listaTrama.add(b);
                            }
                    }
        
 /*  94 */ Byte[] b = new Byte[listaTrama.size()];
        /*  95 */ return (Byte[]) listaTrama.toArray(b);
            }

    
 
  public static MensajeOpacimetroBrianBee armarMensajeOpacimetroBrianBee(byte[] trama)  {
        /* 101 */ if (trama == null) {
            /* 102 */ return null;
                    }
        
 /* 105 */ int contadorOcurrencias = 0;
        /* 106 */ List<String> listaEncontrados = new ArrayList();
        
 /* 108 */ List<Byte> temporal = new ArrayList();
        /* 109 */ for (int i = 0; i < trama.length - 3; i++)  {
            /* 111 */ if ((trama[i] == 23) && (contadorOcurrencias == 0)) {
                /* 112 */ contadorOcurrencias = 1;
                            } /* 114 */ else if (trama[i] == 23) {
                 try {
                    /* 116 */ byte[] buffer = new byte[temporal.size()];
                    /* 117 */ for (int j = 0; j < temporal.size(); j++) {
                        /* 118 */ buffer[j] = ((Byte) temporal.get(j)).byteValue();
                                            }
                    /* 120 */ listaEncontrados.add(new String(buffer, "US-ASCII"));
                    /* 121 */ temporal.clear();
                                    } catch (UnsupportedEncodingException ex) {
                    /* 123 */ Logger.getLogger(UtilOpacimetroBrianBee.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                /* 125 */            } else if (contadorOcurrencias != 0) {
                /* 126 */ temporal.add(Byte.valueOf(trama[i]));
                            }
            /* 128 */ if ((i == trama.length - 4)
                    && /* 129 */ (!temporal.isEmpty())) {
                 try {
                    /* 131 */ byte[] buffer = new byte[temporal.size()];
                    /* 132 */ for (int j = 0; j < temporal.size(); j++) {
                        /* 133 */ buffer[j] = ((Byte) temporal.get(j)).byteValue();
                                            }
                    /* 135 */ listaEncontrados.add(new String(buffer, "US-ASCII"));
                    /* 136 */ temporal.clear();
                                    } catch (UnsupportedEncodingException ex) {
                    /* 138 */ Logger.getLogger(UtilOpacimetroBrianBee.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                            }
                    }
        
 
 /* 144 */ MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee((String) listaEncontrados.get(1));
        /* 145 */ listaEncontrados.remove(0);
        /* 146 */ listaEncontrados.remove(0);
        /* 147 */ mensaje.setDatos(listaEncontrados);
        /* 148 */ return mensaje;
            }

    
 
  private static String calcularCheckSum(List<Byte> listaTrama)  {
        /* 154 */ int suma = 0;
        /* 155 */ for (int i = 1; i < listaTrama.size(); i++)  {
            /* 157 */ suma += ((Byte) listaTrama.get(i)).byteValue();
                    }
        /* 159 */ suma &= 0xFF;
        /* 160 */ String chkSum = Integer.toHexString(suma).toUpperCase();
        /* 161 */ System.out.println("Longitud de la cadena:" + chkSum.length());
        /* 162 */ if (chkSum.length() == 1) {
            /* 163 */ chkSum = chkSum + 0 + chkSum;
                    }
        /* 165 */ if (chkSum.length() > 2) {
            /* 166 */ chkSum = chkSum.substring(1);
                    }
        /* 168 */ return chkSum;
            }

    
 
  public static void main(String[] args)  {
        /* 174 */ MensajeOpacimetroBrianBee mensaje = new MensajeOpacimetroBrianBee("VA");
        /* 175 */ byte[] trama = armarTramaOpacimetroBrianBee(mensaje);
        /* 176 */ for (byte b : trama)  {
            /* 178 */ System.out.print(Integer.toHexString(b).toUpperCase() + " ");
                    }
        /* 180 */ System.out.println("");
        /* 181 */ for (byte b : trama)  {
            /* 183 */ System.out.print((short) b + " ");
                    }
            }
     }


/* Location:              /home/luisberna/Dropbox/maven-soltelec/Sart-git-3/modulos/ModuloGLR.jar!/com/soltelec/opacimetro/brianbee/UtilOpacimetroBrianBee.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
