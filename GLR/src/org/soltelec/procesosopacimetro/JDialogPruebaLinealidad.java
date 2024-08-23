/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosopacimetro;

import java.awt.Image;
import java.awt.Window;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.soltelec.pruebasgases.PanelPruebaGases;

/**
 *
 * @author GerenciaDesarrollo
 */
public class JDialogPruebaLinealidad extends JDialog{

    private final PanelPruebaGases panel;


    public JDialogPruebaLinealidad(Window owner ,boolean modal) {
        super();        
        panel = new PanelPruebaGases();
        this.setTitle("SART Version:1.7.3 copyright  2009 ");
        this.setIconImage(getIconImage());
        this.getContentPane().add(panel);
        this.setSize(this.getToolkit().getScreenSize());
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setModal(modal);
    }//end of constructor JDialogPruebaLinealidad JDialog   

    public PanelPruebaGases getPanel() {
        return panel;
    }
    public Image getIconImage() {
        ImageIcon image = new ImageIcon(this.getClass().getResource("/imagenes/car.png"));
        return image.getImage();
    }

}//class JDialogPruebaLinealidad
