/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.util.LinkedList;

/**
 * Clase para encapsular la medicion de rpm y de temperatura de aceite Comparte
 * el productor y el consumidor toma datos como cadena
 *
 * @author Usuario
 */
public class BufferStrings {

    //lista de los datos que envia el productor
    private final LinkedList<String> listaStrSerial;
    boolean empty;

    public BufferStrings() {
        listaStrSerial = new LinkedList();
    }//end of class

    public void addStr(String str) {
        listaStrSerial.add(str);
    }

    public String getStr() {
        for (String string : listaStrSerial) {
            System.out.println(string);
        }
        return listaStrSerial.pop();
    }

    public boolean isEmpty() {
        return listaStrSerial.isEmpty();
    }//end of method empty

}//end of class
