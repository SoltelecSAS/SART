/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.login;

import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JDialog;


/**
 *JDialog de la prueba de luces para Motos
 * @author Usuario
 */
public class JDialogUsuarioContrasenia extends JDialog{
    DialogoUsuario panel;

    public JDialogUsuarioContrasenia(Frame owner, boolean modal){
        super(owner,modal);
        panel = new DialogoUsuario();
        this.getContentPane().add(panel);
        this.getRootPane().setDefaultButton(panel.getBotonOK());
        this.setSize(new Dimension(400,200));
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setResizable(false);
        this.setResizable(false);
        this.setLocationRelativeTo(owner);
        //this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }//end of constructor

    public DialogoUsuario getPanel() {
        return panel;
    }


}//end of class JDialogLucesMoto
