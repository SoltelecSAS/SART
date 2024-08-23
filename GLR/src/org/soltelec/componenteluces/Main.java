/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.componenteluces;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author Usuario
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame app = new JFrame();
//        app.getContentPane().setLayout(new FlowLayout());
//        //app.getContentPane().add(new LightCar());
//        LuzAlta luz = new LuzAlta();
//        LuzBaja luzBaja = new LuzBaja();
//        luzBaja.setBlinking(true);
//        luz.setBlinking(true);
//        LuzExploradora luzExploradora = new LuzExploradora();
//
//        app.getContentPane().add(luz);
//        app.getContentPane().add(luzBaja);
//        app.getContentPane().add(luzExploradora);
//        //app.getContentPane().add(new JButton("YO"));

        PruebaLuces pruebaLuces = new PruebaLuces();
        app.getContentPane().add(pruebaLuces);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }

}
