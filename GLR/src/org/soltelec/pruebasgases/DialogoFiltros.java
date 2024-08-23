/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.awt.Image;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import org.soltelec.models.controllers.EquipoController;

/**
 *
 * @author Usuario
 */
public class DialogoFiltros extends JDialog {
    PanelValorFiltros panelValorFiltros;

    public DialogoFiltros(){
        this.setModal(true);
        panelValorFiltros = new PanelValorFiltros();
        this.setTitle("SART Version:1.7.3 copyright  2009 ");
        this.setIconImage(getIconImage());
        this.setSize( this.getToolkit().getScreenSize() );
        
        this.getContentPane().add( panelValorFiltros );
        this.getRootPane().setDefaultButton( panelValorFiltros.getButtonOk() );
    }//end of DialogoFiltros
    
    public DialogoFiltros(Long serial){
        try {
            this.setModal(true);
            panelValorFiltros = new PanelValorFiltros();
            this.setTitle("SART Version:1.7.3 copyright  2009 ");
            this.setIconImage(getIconImage());
            this.setSize( this.getToolkit().getScreenSize() );
            EquipoController eqpCont = new EquipoController();
            String response =eqpCont.findConfigEquip(serial,"opacimetro");
            String[]atrrConfig =response.split(";");
            panelValorFiltros.setValorFiltro2(Double.parseDouble(atrrConfig[0]));
            panelValorFiltros.setValorFiltro3(Double.parseDouble(atrrConfig[1]));
            
            this.getContentPane().add( panelValorFiltros );
            this.getRootPane().setDefaultButton( panelValorFiltros.getButtonOk() );
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(DialogoFiltros.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }//end of DialogoFiltros

    public double getValorFiltro1() {
        return panelValorFiltros.getValorFiltro1();
    }
    
     public Image getIconImage() {
        ImageIcon image = new ImageIcon(this.getClass().getResource("/imagenes/car.png"));
        return image.getImage();
    }

    public double getValorFiltro2() {
        return panelValorFiltros.getValorFiltro2();
    }

    public double getValorFiltro3() {
        
        return panelValorFiltros.getValorFiltro3();
    }

    public double getValorFiltro4(){
        return panelValorFiltros.getValorFiltro4();
    }



}//end of class
