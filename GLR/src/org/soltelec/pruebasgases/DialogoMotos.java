/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

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

/**
 *panel para poner en un dialogo captura
 * los datos de la moto: si es de 2 o cuatro tiempos, el numero
 * de tubos de escape funcionales y el número de cilindros
 * @author Usuario
 */
public class DialogoMotos extends JPanel implements ActionListener{
    private JRadioButton radioDosTiempos,radioCuatroTiempos;
    private JLabel labelCilindros, labelEscapes;
    private ButtonGroup groupButton;
    private Border lineBorder,tittledBorder;
    private JTextField txtFieldCilindros,txtFieldEscapes;
    private JPanel panelAuxiliar;
    private JButton buttonContinuar;
    boolean simulacion = false;

    private int numeroTiempos,numeroCilindros,numeroEscapes;
    
    public DialogoMotos(){
        crearComponentes();
        ponerComponentes();        
    }//end of class DialogoMotos

    private void crearComponentes(){

        radioDosTiempos = new JRadioButton("2T");
        radioCuatroTiempos = new JRadioButton("4T");
        radioCuatroTiempos.setSelected(true);
        radioDosTiempos.setSelected(false);
        labelCilindros = new JLabel(" # Cilindros");
        labelEscapes = new JLabel(" #Escapes FUNCIONALES");
        lineBorder = BorderFactory.createLineBorder(Color.BLACK);        
        tittledBorder = BorderFactory.createTitledBorder(lineBorder,"Tiempos");
        txtFieldCilindros = new JTextField("4");
        txtFieldEscapes = new JTextField("1");
        groupButton = new ButtonGroup();
        groupButton.add(radioDosTiempos);
        groupButton.add(radioCuatroTiempos);
        buttonContinuar = new JButton("Continuar");
        buttonContinuar.addActionListener(this);
        buttonContinuar.addKeyListener(new ManejadorDobleClick(this));
     }//end of method crearComponentes

    private void ponerComponentes(){
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(10,5,10,5);
        c.weightx = 0.5;//
        c.anchor = FIRST_LINE_START;
        filaColumna(0,0,c);
        this.add(labelCilindros,c);
        c.fill = HORIZONTAL;
        filaColumna(1,0,c);
        this.add(txtFieldCilindros,c);
        c.fill = NONE;
        c.anchor = FIRST_LINE_START;
        filaColumna(2,0,c);
        this.add(labelEscapes,c);
        c.fill = HORIZONTAL;
        filaColumna(3,0,c);
        this.add(txtFieldEscapes,c);
        panelAuxiliar = new JPanel();
        panelAuxiliar.setLayout(new GridLayout(1,2,5,5));
        panelAuxiliar.setBorder(tittledBorder);
        panelAuxiliar.add(radioDosTiempos);
        panelAuxiliar.add(radioCuatroTiempos);
        c.fill = HORIZONTAL;
        filaColumna(4,0,c);
        this.add(panelAuxiliar,c);
        filaColumna(5,0,c);
        c.anchor = NONE;
        c.anchor = LAST_LINE_END;
        this.add(buttonContinuar,c);

    }//end of method ponerComponentes

    private void filaColumna(int fila, int columna,GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;
    }//edn of class filaColumna

    public static void main(String args[]){
        JDialog d = new JDialog();
        d.add(new DialogoMotos());
        d.setSize(240,300);
        d.setVisible(true);
        
    }//end of

    public void actionPerformed(ActionEvent e) {
        if(radioDosTiempos.isSelected())
            numeroTiempos = 2;
        else
            numeroTiempos = 4;
        try {
            numeroEscapes = Integer.parseInt(txtFieldEscapes.getText());
            numeroCilindros = Integer.parseInt(txtFieldCilindros.getText());
            System.out.println(String.format("Numero Cilindros: %s Numero Escapes: %s Numero Tiempos: %s ",numeroCilindros,numeroEscapes,numeroTiempos ));
            this.cerrar();
        }catch (NumberFormatException ne){
            JOptionPane.showMessageDialog(this, "Introduzca numeros Validos");
            return;
        }
   }

    private void cerrar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        w.dispose();
    }

    public int getNumeroCilindros() {
        return numeroCilindros;
    }

    public int getNumeroEscapes() {
        return numeroEscapes;
    }

    public int getNumeroTiempos() {
        return numeroTiempos;
    }

    public JRadioButton getRadioCuatroTiempos() {
        return radioCuatroTiempos;
    }

    public void setRadioCuatroTiempos(JRadioButton radioCuatroTiempos) {
        this.radioCuatroTiempos = radioCuatroTiempos;
    }

    public JRadioButton getRadioDosTiempos() {
        return radioDosTiempos;
    }

    public void setRadioDosTiempos(JRadioButton radioDosTiempos) {
        this.radioDosTiempos = radioDosTiempos;
    }

    public boolean isSimulacion() {
        return simulacion;
    }

    public void setSimulacion(boolean simulacion) {
        this.simulacion = simulacion;
    }

    JButton getButtonContinuar() {
        return buttonContinuar;
    }


    class ManejadorDobleClick extends KeyAdapter{

        DialogoMotos dlg;

        public ManejadorDobleClick(DialogoMotos dlg) {
            this.dlg = dlg;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            dlg.setSimulacion(true);
            buttonContinuar.doClick();
            //(SwingUtilities.getWindowAncestor(dlg)).dispose();
        }
    }

}//end of class DialogoMotos
