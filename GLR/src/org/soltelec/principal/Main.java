/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.principal;
import org.soltelec.procesosbanco.PanelServicioBanco;
import javax.swing.JFrame;
//import org.pushingpixels.substance.api.SubstanceLookAndFeel;
//import org.pushingpixels.substance.api.skin.TwilightSkin;
/**
 *
 * @author GerenciaDesarrollo
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PanelServicioBanco panel = new PanelServicioBanco();
         JFrame.setDefaultLookAndFeelDecorated(true);
        //Aplicamos un skin a nuestra ventana, en este caso SaharaSkin
        //boolean setSkin = SubstanceLookAndFeel.setSkin(new VisualSchedulerSkin());
        JFrame app = new JFrame("Servicio");
        //Le decimos al SO que java se encargara del manejo de la decoracion
        //del contorno de la ventana
       
        app.add(panel);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH );
        app.setVisible(true);
        app.setResizable(false);
    }
}
