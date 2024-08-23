/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import static java.awt.GridBagConstraints.*;

/**
 *Clase para dar un mecanismo de simulación en la primera versión
 * de la prueba del sonometro.
 * @author DANIELA SANA
 */
public class PanelInicioSim extends JPanel{

    private JLabel labelMensaje;
    private JButton buttonOk;
    private TitledBorder border;
    public boolean simulacion;

    public PanelInicioSim() {
        super();
        crearComponentes();
        ponerComponentes();
    }

    private void crearComponentes() {
        labelMensaje = new JLabel("VERIFIQUE SONOMETRO ENCENDIDO");
        buttonOk = new JButton(" Aceptar ");
        buttonOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                (SwingUtilities.getWindowAncestor(buttonOk)).dispose();
            }
        });
        this.addMouseListener(new MouseClick());
    }

    private void ponerComponentes() {
        Font f = new Font(Font.SERIF, Font.BOLD,35);
        labelMensaje.setFont(f);
        buttonOk.setFont(f);
        border = BorderFactory.createTitledBorder("Bienvenido");
        this.setBorder(border);
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(25,25,25,25);
        filaColumna(1,0,c);
        c.gridwidth = REMAINDER;
        this.add(labelMensaje,c);
        c.gridwidth = 1;
        filaColumna(2,0,c);
        this.add(buttonOk,c);

    }

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }


class MouseClick extends MouseAdapter{

    @Override
    public void mouseClicked(MouseEvent e) {
     simulacion = true;
     labelMensaje.setForeground(Color.red);
    }
    }



}//end of DialogoInicio


