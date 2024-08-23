/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.util.BufferStrings;

/**
 * Consumidor del Lector Htech
 *
 * @author GerenciaDesarrollo
 */
public class ConsumidorHtech implements Runnable, Sonometro {

    private double decibeles = -1;

    private boolean terminado = false;
    private final BufferStrings bufferStr;
    private String str;
    private final LectorHTech lector;

    public ConsumidorHtech(BufferStrings bufferStr, LectorHTech lector) {
        this.bufferStr = bufferStr;
        this.lector = lector;
    }

    @Override
    public void run() {
        while (!terminado) {
            synchronized (bufferStr) {
                if (bufferStr.isEmpty()) {
                    try {
                        System.out.println("...Lista Vacia (HTECH) ...");
                        bufferStr.wait();
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
                try {
                    if (bufferStr.isEmpty() == false) {
                        str = bufferStr.getStr();
                        if (str.length() < 7) {
                            System.out.println("Length STR menor a 7");
                            System.out.println("STR: " + str);
                            System.out.println("Poniendo Buffer en espera...");
                            bufferStr.wait();
                        }
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                    System.out.println(exc);
                    System.out.println("Excepcion leyendo la lista de datos");
                }
                System.out.println("lst procesa decb "+str);               
                procesarString(str);
            }//end synchronized
        }//end of while
        System.out.println("Hilo Consumidor Terminado");
    }//end of run

    /**
     * Metodo para sacar de la trama la informacion de los decibeles
     *
     * @param str la cadena a procesar
     */
    private void procesarString(String str) {
        try {
            String cadenacortada = str.substring(2, 7);
            //System.out.println("Medicion tomada: " + cadenacortada);
            this.decibeles = Double.parseDouble(cadenacortada);
        } catch (NumberFormatException ne) {
            this.decibeles = -1;
            ne.printStackTrace();
        }
    }//end of method procesarString

    @Override
    public double getDecibeles() {
        return decibeles;
    }

    @Override
    public void stop() {
        this.terminado = true;
        //Thread.currentThread().interrupt();
        lector.stop();
    }

}//end of class ConsumidorHTech
