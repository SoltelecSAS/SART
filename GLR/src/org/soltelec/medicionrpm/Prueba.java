/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.medicionrpm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Usuario
 */
public class Prueba {

    public static void main(String args[]){

        //Pattern  expression = Pattern.compile("(\\d{3,4})T(\\d{3})\\.*");
        //Pattern  expression = Pattern.compile("\\d*T(\\d+)T\\d+");

        Pattern  expression = Pattern.compile("(\\d{3,4})(T?)(\\d{3})");

        Matcher matcher = expression.matcher("10101010110T0220001000100010001000T022100010001000                  ");
        while(matcher.find()){
            System.out.println(matcher.group());

            System.out.println(matcher.group(1));

            System.out.println(matcher.group(2));

            System.out.println(matcher.group(3));
        }
    }

}
