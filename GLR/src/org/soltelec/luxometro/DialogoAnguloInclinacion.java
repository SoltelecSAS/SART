/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

/**
 *
 * @author Usuario
 */
public class DialogoAnguloInclinacion extends JDialog implements ActionListener{

    private PanelAnguloInclinacion panel = new PanelAnguloInclinacion();

    public DialogoAnguloInclinacion(Window owner, String title, ModalityType modalityType) {
        
        super(owner,title,modalityType);
         System.out.println("Entra construtor " );
        this.getContentPane().add(panel);
        this.getRootPane().setDefaultButton(panel.getButtonOk());
        panel.getButtonOk().addActionListener(this);
        Dimension d = getToolkit().getScreenSize();
        double altura  = d.getHeight()*0.95;
        d.setSize(d.getWidth(), altura);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setSize(d);
    }

    public DialogoAnguloInclinacion(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
        this.getContentPane().add(new PanelAnguloInclinacion());
    }

    public DialogoAnguloInclinacion(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        this.getContentPane().add(new PanelAnguloInclinacion());
    }

    public DialogoAnguloInclinacion() {
        super();
        this.getContentPane().add(new PanelAnguloInclinacion());
    }

    public void actionPerformed(ActionEvent e) {
        this.dispose();
    }

    public PanelAnguloInclinacion getPanel() {
        return panel;
    }
}
