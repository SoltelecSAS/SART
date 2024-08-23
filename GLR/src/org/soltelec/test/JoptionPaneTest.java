/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.test;

import javax.swing.JOptionPane;
/**
 *
 * @author user
 */
public class JoptionPaneTest {

    public static void main(String[] args){
     int opcion = JOptionPane.showOptionDialog(null, "¿VISUALIZO PRESENCIA DE HUMO?",
                        "SART 1.7.3", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Negro", "Azul", "NO"}, "NO");
System.out.println(opcion); 
System.out.println(JOptionPane.NO_OPTION); 
    }
//negro = 0
//Azul = 1
//NO = 2
    
}
