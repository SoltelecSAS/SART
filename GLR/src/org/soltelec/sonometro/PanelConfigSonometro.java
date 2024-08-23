/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.sonometro;

import org.soltelec.util.PortSerialUtil;
import gnu.io.CommPortIdentifier;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 *Clase simplemente para seleccionar el puerto serial apropiado
 * @author Usuario
 */
public class PanelConfigSonometro extends JPanel implements ActionListener{

    private JComboBox comboPuertoSerial;
    private JButton buttonOk;
    private TitledBorder bordePanel,bordeCombo;
    private Map<String, CommPortIdentifier> mapaPuertos;

    public PanelConfigSonometro(){
        crearComponentes();
        ponerComponenetes();
    }//end of constructor

    private void crearComponentes() {
        mapaPuertos = PortSerialUtil.listPorts();
        String[] opciones;
        Object[] objetos = mapaPuertos.keySet().toArray();
        opciones = new String[objetos.length];
        for (int i = 0; i<objetos.length; i++){
            opciones[i] = (String) objetos[i];
        }
        comboPuertoSerial = new JComboBox(opciones);
        //
        bordeCombo = BorderFactory.createTitledBorder("Seleccione Puerto Serial");
        bordeCombo.setTitleColor(Color.red);        //
        comboPuertoSerial.setBorder(bordeCombo);
        buttonOk = new JButton("OK");
        buttonOk.addActionListener(this);
        //
        bordePanel = BorderFactory.createTitledBorder("PUERTO SERIAL SONOMETRO");
        this.setBorder(bordePanel);
    }//end of crearComponentes

    @Override
    public void actionPerformed(ActionEvent e) {
        (SwingUtilities.getWindowAncestor(this)).dispose();
    }

    private void ponerComponenetes() {
        this.add(comboPuertoSerial);
       // this.add(buttonOk);
        this.setSize(300,100);
    }

    public CommPortIdentifier getCommPortIdentifierSelected() {
        return mapaPuertos.get((String) comboPuertoSerial.getSelectedItem());
    }
}//end of class PanelConfigSonometro
