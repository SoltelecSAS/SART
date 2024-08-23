/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soltelec.test;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;
import org.soltelec.procesosopacimetro.Opacimetro;
import org.soltelec.util.MedicionOpacidad;

/**
 *
 * @author SOLTELEC
 */
public class PruebaOpacidad 
{

    
    public static void main(String[] args) 
    {
        double numeroRandom = ThreadLocalRandom.current().nextDouble(90.0, 99.0);
           System.out.println("valor " + numeroRandom);
           
           double randon = Double.parseDouble( new DecimalFormat("#.000").format(((Math.random() * (99.0 -99.9)) + 99.9)).replace(",", "."));
           System.out.println(" valor 2 : " + randon);
    }

}
