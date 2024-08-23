/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.luxometro;

import java.awt.Color;
import org.soltelec.util.PortSerialUtil;
import gnu.io.CommPortIdentifier;
import java.awt.Dimension;
import java.util.Map;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import static java.awt.GridBagConstraints.*;

/**
 *Clase para preguntar cuantas exploradoras
 * se van a medir del ángulo
 * @author Usuario
 */
public class PanelConfiguracionExploradoras extends JPanel implements ActionListener{
    //Elementos de interfaz gráfica
    private JCheckBox checkExploradoras;
    private JComboBox comboNumeroExploradoras;
    private TitledBorder bordeGeneral,bordeCombo;
    private JLabel labelMensaje;
   
    private TitledBorder titled3;
    //datos
    private boolean tieneExploradoras;
    private int numeroExploradoras;


    //puerto Serial

    private Integer[] valores;
    private Map<String, CommPortIdentifier> mapaPuertos;


    public PanelConfiguracionExploradoras(int valorFatolas)
    {
        valores= new Integer[valorFatolas];
        for (int i = 0; i < valorFatolas; i++) 
        {
            valores[i]=i+1;
        }
        crearComponentes();
        ponerComponentes();
    }

    private void crearComponentes() 
    {
        bordeGeneral = BorderFactory.createTitledBorder("Parametros Exploradoras");
        bordeCombo = BorderFactory.createTitledBorder("Num Exploradoras");
        titled3 = BorderFactory.createTitledBorder("Seleccione Puerto Serial");
        titled3.setTitleColor(Color.red);
        labelMensaje = new JLabel("El vehiculo tiene Exploradoras");
        checkExploradoras = new JCheckBox();
        checkExploradoras.addActionListener(this);
        comboNumeroExploradoras = new JComboBox(valores);
        comboNumeroExploradoras.setEnabled(false);      
          
       
    }//end of method crearComponentes

    private void ponerComponentes() {
        this.setBorder(bordeGeneral);
        comboNumeroExploradoras.setBorder(bordeCombo);
        JPanel panelEnvoltor = new JPanel();
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);

        c.weightx = 0.3;
        c.fill = HORIZONTAL;

        panelEnvoltor.add(labelMensaje);
        panelEnvoltor.add(checkExploradoras);
        c.gridx = 1;
        c.gridy = 1;
        this.add(panelEnvoltor,c);
        //
        c.weightx = 0.7;
        c.gridx = 2;
        c.gridy = 1;
        this.add(comboNumeroExploradoras,c);
        c.gridx = 1;
        c.gridy = 2;
      
        //
        this.setPreferredSize(new Dimension(400,120));
    }//end of method ponerComponentes

    public void actionPerformed(ActionEvent e) {

        if(checkExploradoras.isSelected())
            comboNumeroExploradoras.setEnabled(true);
        else
            comboNumeroExploradoras.setEnabled(false);
    }

   

    public static void main(String args[]){
        PanelConfiguracionExploradoras p = new PanelConfiguracionExploradoras(6);
        JOptionPane.showMessageDialog(null,p,"titulo", JOptionPane.PLAIN_MESSAGE);
        System.out.println("Exploradoras" + p.checkExploradoras.isSelected());
        System.out.println("Numero Exploradoras" + ((Integer)p.comboNumeroExploradoras.getSelectedItem()).intValue() );
    }

    public JCheckBox getCheckExploradoras() {
        return checkExploradoras;
    }

    public JComboBox getComboNumeroExploradoras() {
        return comboNumeroExploradoras;
    }

    public JLabel getLabelMensaje() {
        return labelMensaje;
    }

    public TitledBorder getBordeGeneral() {
        return bordeGeneral;
    }




}//end of class PanelExploradoras
