/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import org.soltelec.procesosbanco.PanelServicioBanco;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

/**
 *
 * @author GerenciaDesarrollo
 */
public class DialogServicio extends JDialog {

   private PanelServicioBanco panel;
   public DialogServicio(){
       super();
       this.panel = new PanelServicioBanco();
       this.getContentPane().add(panel);
       //this.addWindowListener(new ManejadorCerrar());
   }

   public DialogServicio(Window w){
       super(w);
       this.panel = new PanelServicioBanco();
       this.getContentPane().add(panel);
       //this.addWindowListener(new ManejadorCerrar());
   }

    public DialogServicio(Frame owner, boolean modal) {
       super(owner, modal);
       this.panel = new PanelServicioBanco();
       this.getContentPane().add(panel);
       //this.addWindowListener(new ManejadorCerrar());
       this.setSize(this.getToolkit().getScreenSize());
       this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
  }

   public void cerrar(){
     
   }
class ManejadorCerrar extends WindowAdapter{


        @Override
  public void windowClosing(WindowEvent e)
        {
            panel.desconectar();
            //cerrar();
        }

        
    }//end of manejador
}
