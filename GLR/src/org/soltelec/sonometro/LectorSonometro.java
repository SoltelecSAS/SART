/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Usuario
 */
public class LectorSonometro implements Runnable{

    private InputStream in;//el flujo de entrada del puerto serial

    private boolean interrumpido = false;
    private boolean terminado = false;
    private String marca = "EXTECH";
    private double medicion;


    public void run() {
       while(!terminado){
           while(!interrumpido){

               if(marca.equals("EXTECH")){
                    byte[] buffer = new byte[1024];
                    int data;
                           try
                            {
                                int len = 0;
                                do{
                                   data = in.read();
                                   if(data > 0 )
                                   buffer[len++] = (byte)data;
                                   //System.out.println("Bloqueado aqui");
                                } while (data > -1 || data == 0x0A);//daria algo asi como
                                   //end while
                                if(len > 0) {//si hay datos
                                    System.out.println("Longitud del mensaje:" + len);
                                    //copiar a un arreglo para armar el mensaje
                                    byte arreglo[] = new byte[len];
                                    //armar el mensaje
                                    System.arraycopy(buffer, 0, arreglo, 0, len);
                                    //return msj = UtilGasesModelo.armarMensaje(arreglo);
                                    String medida = new String(arreglo, 2, 5);
                                    System.out.println("Medida: " + medida);
                                    try {
                                         medicion = Double.parseDouble(medida);
                                        System.out.println("medicion en double" + medicion);
                                    }catch(NumberFormatException ne){
                                        System.out.println("NO SE PUEDE CONVERTIR EL NUMERO:" + medida);
                                    }
                                }//end if else
                                else {
                                    System.out.println("Len: " + len);
                                }
                            }
                            catch ( IOException e )
                            {
                            e.printStackTrace();
                            System.exit(-1);
                            }//end catch

               }else if(marca.equals("TENMA")){
                    byte[] buffer = new byte[1024];
                    int data;
                           try
                            {
                                int len = 0;
                                do{
                                   data = in.read();
                                   if(data > 0 )
                                   buffer[len++] = (byte)data;
                                   //System.out.println("Bloqueado aqui");
                                } while (data > -1 || data == 0x0A);//deja el CR
                                   //end while
                                if(len > 0) {//si hay datos
                                    System.out.println("Longitud del mensaje:" + len);
                                    //copiar a un arreglo para armar el mensaje
                                    byte arreglo[] = new byte[len];
                                    //armar el mensaje
                                    System.arraycopy(buffer, 0, arreglo, 0, len);

                                    //
                                    if(arreglo[0] == '\''){
                                        arreglo[0] = '0';
                                    }
                                    //return msj = UtilGasesModelo.armarMensaje(arreglo);
                                    String medida = new String(arreglo, 1, 4);
                                    try {
                                         medicion = Double.parseDouble(medida);
                                        System.out.println("medicion en double" + medicion);
                                    }catch(NumberFormatException ne){
                                        System.out.println("NO SE PUEDE CONVERTIR EL NUMERO:" + medida);
                                    }
                                }//end if else
                                else {
                                    System.out.println("Len: " + len);
                                }
                            }
                            catch ( IOException e )
                            {
                            e.printStackTrace();
                            System.exit(-1);
                            }//end catch
               }//end of if TENMA
           }//end of while interrumpido          
       }//end of while terminado
    }

    public double getMedicion() {
        return medicion;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public boolean isInterrumpido() {
        return interrumpido;
    }

    public void setInterrumpido(boolean interrumpido) {
        this.interrumpido = interrumpido;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }
}//end of class LectorSonometro
