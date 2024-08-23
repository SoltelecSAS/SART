/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *Panel Falso para hacer prueba, permite iniciar el panel de servicio
 * @author Usuario
 */
public class PanelPruebaServicio extends JPanel implements ActionListener{

    JPanel panelBotones;
    private final JButton buttonPruebaGases, buttonServicioBanco;
    private final boolean inicio = true;
    private JDialog dlg;
   

    public PanelPruebaServicio(){
        GridLayout grid = new GridLayout(2,1,10,10);
        panelBotones = new JPanel();
        panelBotones.setLayout(grid);
        buttonPruebaGases = new JButton("Prueba");
        buttonPruebaGases.addActionListener(this);
        buttonServicioBanco = new JButton("Servicio");
        buttonServicioBanco.addActionListener(this);
        panelBotones.add(buttonPruebaGases);
        panelBotones.add(buttonServicioBanco);
        this.add(panelBotones);
    }//end of constructor

    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getSource() == buttonPruebaGases){
            Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
            dlg = new JDialog(w);
            dlg.setModal(true);
            dlg.setSize(dlg.getToolkit().getScreenSize());
           
            dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dlg.setResizable(false);
            dlg.setVisible(true);                        
       }else if(e.getSource() == buttonServicioBanco){
           /*
            * Dos opciones: Hacer un JDialog o hacer un repintado del panel, escojo la opcion del
            * JDIalog
            */
        
           Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
           DialogServicio dlg1 = new DialogServicio(w);
           dlg1.setModal(true);
           dlg1.setSize(dlg1.getToolkit().getScreenSize());
           dlg1.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
           dlg1.setResizable(false);
           dlg1.setVisible(true);
           
      }
    }//end of method actionPerformed
}
