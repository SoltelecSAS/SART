/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

/**
 *
 * @author soltelec
 */
public class AproximacionMedidas {

    public static void main(String[] args) {
        System.out.println(AproximarMedidas(23.45));
    }
    
    public static double AproximarMedidas(Double Medida) { //Metodo para realizar la aproximacion de las medidas 
        Integer Bandera = 0;
        Double Bandera2;
        System.out.println("entro a aproximar medida :" + Medida);
        if (Medida > 99) {
            Bandera = Medida.intValue();
            Bandera2 = Medida - Bandera;
            Medida = (Bandera2 >= 0.5) ? Medida + (1 - Bandera2) : Medida- Bandera2;
            // return Medida;

        } else if (Medida > 10 && Medida < 100) {
            System.out.println("+++++++++++++++++++++++++++ medida: " + Medida + " +++++++++++++++++++++++++");
            Medida = Medida * 10;
            Bandera = Medida.intValue();
            Bandera2 = Medida - Bandera;
            Medida = (Bandera2 >= 0.5) ? Medida + (1 - Bandera2) : Medida- Bandera2;
            Medida = Medida / 10;
            System.out.println("+++++++++++++++++++++++++++ medida: " + Medida + " +++++++++++++++++++++++++");
            // return Medida;
        } else if (Medida < 10) {
            Medida = Medida * 100;
            Bandera = Medida.intValue();
            Bandera2 = Medida - Bandera;
            Medida = (Bandera2 >= 0.5) ? Medida + (1 - Bandera2) : Medida- Bandera2;
            Medida = Medida / 100;
            //return Medida;
        }
        System.out.println("despues de aproximar: " + Medida);
        return Medida;
    }

}
