/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author SOLTELEC
 */
public class propiedadesSonometro 
{
    public boolean cargarConfiProperties() 
    {
        System.out.println("---------------------------------------------------");
        System.out.println("-----------   cargarConfiProperties   -------------");
        System.out.println("---------------------------------------------------");
        try {
            System.out.println("Cargando la propiedad rpmPruebaRuidoHabilitada en GLR");
            boolean rpmPruebaRuidoHabilitada = Boolean.parseBoolean(UtilPropiedades.cargarPropiedad("rpmPruebaRuidoHabilitada", "propiedades.properties"));
            System.out.println("Valor de la  porpiedad rpmPruebaRuidoHabilitada " + rpmPruebaRuidoHabilitada);
            return rpmPruebaRuidoHabilitada;
        } catch (IOException e) {
            System.out.println("----------------------------------------------");
            System.out.println("Errro en el metodo : " + e.getMessage() + e.getLocalizedMessage());
            System.out.println("----------------------------------------------");
            JOptionPane.showMessageDialog(null, "Erro al cagar la propiedad rpmPruebaRuidoHabilitada" + e.getMessage());
        }
        return false;
    }
}
