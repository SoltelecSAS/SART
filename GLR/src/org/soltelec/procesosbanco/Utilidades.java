/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

/**
 *
 * @author GerenciaDesarrollo
 */
public class Utilidades {

    private static char patron = 'M';

    public static String cifra(String cadena, char patron) {
        char[] secStr = cadena.toCharArray();
        String strEncript = "";
        int e = secStr.length;
        for (int n = 0; n < secStr.length; n++) {
            char c = (char) (secStr[n] ^ patron);
            strEncript = strEncript + c;
        }
        return strEncript;
    }

    public static String deCifrar(String cadena) {
        String strEncript = "";
        if (cadena.length() > 0) {
            char[] secStr = cadena.toCharArray();            
            for (int n = 0; n < secStr.length; n++) {
                char c = (char) (secStr[n] ^ patron);
                strEncript = strEncript + c;
            }
        }
        return strEncript;
    }

    public static void servicio() {

    }

}
