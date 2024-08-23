/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import org.soltelec.pruebasgases.motocicletas.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.awt.Window;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.GridLayout;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import static java.awt.GridBagConstraints.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import org.soltelec.util.InfoVehiculo;
import org.soltelec.util.RegistrarMedidas;

/**
 * panel para poner en un dialogo captura los datos de la moto: si es de 2 o
 * cuatro tiempos, el numero de tubos de escape funcionales y el número de
 * cilindros
 *
 * @author GERENCIA DE DESARROLLO DE SOLUCIONES TECNOLOGICAS
 */
public class DialogoDiesel extends JPanel implements ActionListener {

    private JRadioButton radioDosTiempos, radioCuatroTiempos;
    private JLabel  labelIniRpm,labelFinRpm;    
    private Border lineBorder;
    private JTextField  txtFieldEscapes,txtIniRpm,txtFinRpm;
    private JPanel panelAuxiliar;
    private JButton buttonContinuar;
    boolean simulacion = false;    
    private String placa;
    private String diseno;
    private int lineaCarro;
    

    private int numeroTiempos, numeroCilindros, numeroEscapes, Scooter,rpmIni,rpmFin;

    public DialogoDiesel() {
        crearComponentes();
        ponerComponentes();
    }//end of class DialogoMotos

    private void crearComponentes()  {    
        labelIniRpm = new JLabel(" # Rpm Fabrica Ralenty");
        labelFinRpm = new JLabel(" # Rpm Fabrica Gobernada");
        txtIniRpm = new JTextField("400");
        txtFinRpm = new JTextField("1500");
        lineBorder = BorderFactory.createLineBorder(Color.BLACK);      
        buttonContinuar = new JButton("Continuar");
        buttonContinuar.addActionListener(this);
       

    }//end of method crearComponentes

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(10, 5, 10, 5);
        c.weightx = 0.5;//
        c.anchor = FIRST_LINE_START;
        filaColumna(0, 0, c);
        
        c.fill = HORIZONTAL;
        filaColumna(1, 0, c);
        
        c.fill = NONE;
        c.anchor = FIRST_LINE_START;
        filaColumna(2, 0, c);
        
        c.fill = HORIZONTAL;
        filaColumna(3, 0, c);
       
        filaColumna(4, 0, c);
        

        panelAuxiliar = new JPanel();
        panelAuxiliar.setLayout(new GridLayout(1, 2, 5, 5));
       
       
        c.fill = HORIZONTAL;
        filaColumna(5, 0, c);
        this.add(panelAuxiliar, c);
        
        filaColumna(6, 0, c);
        this.add(labelIniRpm, c);
        c.fill = HORIZONTAL;
        filaColumna(7, 0, c);
        this.add(txtIniRpm, c);
        c.fill = NONE;
        c.anchor = FIRST_LINE_START;
        
        filaColumna(8, 0, c);
        this.add(labelFinRpm, c);
        c.fill = HORIZONTAL;
        filaColumna(9, 0, c);
        this.add(txtFinRpm, c);
        c.fill = NONE;
        c.anchor = FIRST_LINE_START;     
              
        filaColumna(10, 0, c);
        c.anchor = NONE;
        c.anchor = LAST_LINE_END;
        this.add(buttonContinuar, c);

    }//end of method ponerComponentes

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//edn of class filaColumna

    public static void main(String args[]) {
        JDialog d = new JDialog();
        d.add(new DialogoDiesel());
         d.setSize(890, 900);
        d.setVisible(true);

    }//end of

    public void fillData(String placas) {
        //this.idPrueba=idPrueba;
        this.placa=placas;
        
        diseno="Convencional";        
        } 
    

    public void actionPerformed(ActionEvent e) {   
        try {        
            if (e.getSource() == buttonContinuar) {
                System.out.println(String.format("Numero Cilindros: %s Numero Escapes: %s Numero Tiempos: %s ", numeroCilindros, numeroEscapes, numeroTiempos));
                rpmIni = Integer.parseInt(this.txtIniRpm.getText());
                rpmFin = Integer.parseInt(this.txtFinRpm.getText());
                this.cerrar();
            }
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(this, "Por Favor Introduzca SOLO numeros ");
            return;
        } catch (Exception ne) {
            JOptionPane.showMessageDialog(this, "Ocurrio error debido en "+ne.getMessage());
            return;
        }
    }

    private void cerrar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        w.dispose();
    }

    

    public JRadioButton getRadioCuatroTiempos() {
        return radioCuatroTiempos;
    }

    
    public JRadioButton getRadioDosTiempos() {
        return radioDosTiempos;
    }

     public int getRpmR() {
        return Integer.parseInt(this.txtIniRpm.getText());
    }

    public int getRpmC() {
        return Integer.parseInt(this.txtFinRpm.getText());
    }    

    public JButton getButtonContinuar() {
        return buttonContinuar;
    }

    class ManejadorDobleClick extends KeyAdapter {

        DialogoDiesel dlg;

        public ManejadorDobleClick(DialogoDiesel dlg) {
            this.dlg = dlg;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
          
            buttonContinuar.doClick();
            //(SwingUtilities.getWindowAncestor(dlg)).dispose();
        }
    }

}//end of class DialogoMotos
