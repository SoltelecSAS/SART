/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class PruebaWorker extends JFrame {
    private PanelMedidas panelMedidas;
    private WorkerDatos worker;
    private JButton botonIniciar,botonTerminar;
    private JPanel panelBotones;
    ManejadorBotones manejador;
    public PruebaWorker(){
        manejador = new ManejadorBotones();
        panelBotones = new JPanel();
        panelMedidas = new PanelMedidas();
        botonIniciar = new JButton("Iniciar");
        botonIniciar.addActionListener(manejador);
        botonTerminar = new JButton("Terminar");
        botonTerminar.addActionListener(manejador);
       // worker = new WorkerDatos(panelMedidas);
        //poner los componentes
        this.add(panelMedidas,BorderLayout.CENTER );
        panelBotones.add(botonIniciar);

        panelBotones.add(botonTerminar);
        this.add(panelBotones,BorderLayout.SOUTH);
    }//end of constructor

   public static void main(String args[]){
       PruebaWorker app = new PruebaWorker();
       app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       app.setSize(650,650);
       app.setVisible(true);
   }//end of main method

   class ManejadorBotones implements ActionListener{
   boolean cambiar = true;
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == botonIniciar){
                worker.execute();

            }else if(e.getSource() == botonTerminar){
                worker.setInterrumpido(cambiar);
                cambiar = !cambiar;
            }
        }

   }
}//end of classs
