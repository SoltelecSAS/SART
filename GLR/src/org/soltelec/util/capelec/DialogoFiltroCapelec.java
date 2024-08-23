/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util.capelec;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import org.soltelec.util.UtilGasesModelo;

/**
 * Clase para proporcionar una vista con informacion
 * sobre el filtro de capelec para manipularlo
 * @author User
 */
public class DialogoFiltroCapelec extends JPanel{
    
    
    private JRadioButton radioBessel,radioKyN;
    private ButtonGroup buttonGroup;
    private JComboBox comboNumeroPolos;
    private JTextField textFieldCa,textFieldCb,textFieldOmega;
    private JLabel lblCa,lblCb,lblOmega;
    private JPanel panelEnvoltor;
            
    
    private byte[] datos;
    
    public DialogoFiltroCapelec() {
        
        crearComponentes();
        ponerComponentes();
    }

    
    
    private void crearComponentes() {
        
        ListenerRadios listenerRadios = new ListenerRadios();
    
        radioBessel = new JRadioButton("Bessel");
        radioBessel.addActionListener(listenerRadios);
        
        radioKyN = new JRadioButton("k y N");
        radioKyN.addActionListener(listenerRadios);
        radioKyN.setSelected(true);
        
        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioBessel);
        buttonGroup.add(radioKyN);
        
        TitledBorder bordeGrupo = BorderFactory.createTitledBorder("Filtro");
        panelEnvoltor = new JPanel();
        panelEnvoltor.add(radioBessel);
        panelEnvoltor.add(radioKyN);
        panelEnvoltor.setBorder(bordeGrupo);
        
        comboNumeroPolos = new JComboBox(new Integer[]{0,1,2});
        TitledBorder bordeCombo = BorderFactory.createTitledBorder("Numero de Polos");
        comboNumeroPolos.setBorder(bordeCombo);
        
        lblCa = new JLabel("Ca");
        lblCb = new JLabel("Cb");
        lblOmega = new JLabel("Omega");
                
        textFieldOmega = new JTextField();
        textFieldOmega.setEnabled(false);
        textFieldCa = new JTextField();
        textFieldCb = new JTextField();
        
        
    }

    private void ponerComponentes() {
        
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        this.setLayout(bag);
        
        c.weightx = 0.5;               
        c.fill = 1;
        c.gridwidth = 1;/*GridBagConstraints.REMAINDER;*/
        
        
        filaColumna(1,1,c);
        this.add(panelEnvoltor,c);
        
        filaColumna(1,2,c);
        this.add(comboNumeroPolos,c);
        
        c.gridwidth = 1;
        filaColumna(2,1,c);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        this.add(lblCa,c);
        
        filaColumna(2,2,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(textFieldCa,c);
        
        filaColumna(3,1,c);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        this.add(lblCb,c);
        
        
        filaColumna(3,2,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(textFieldCb,c);
        
        filaColumna(4,1,c);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        this.add(lblOmega,c);
        
        
        filaColumna(4,2,c);
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(textFieldOmega,c);
        
        
             
    }
    
    
    
    
    private void filaColumna(int fila,int columna,GridBagConstraints c){
        c.gridx = columna;
        c.gridy = fila;
    }//end of method filaColumna
    
   
    public static void main(String args[]){
        
        JFrame app = new JFrame();
        app.getContentPane().add(new DialogoFiltroCapelec());
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(400,300);
        app.setVisible(true);
        
    }
    
   class ListenerRadios implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(e.getSource() == radioKyN){
                
                comboNumeroPolos.setEnabled(true);
                textFieldCa.setEnabled(true);
                textFieldCb.setEnabled(true);
                textFieldOmega.setEnabled(false);
                
                
            } else if (e.getSource() == radioBessel){
                
                textFieldCa.setEnabled(false);
                textFieldCb.setEnabled(false);
                textFieldOmega.setEnabled(true);
                comboNumeroPolos.setEnabled(false);
                
                
            }
            
        }
       
   }

    public byte[] getDatos() {
        
        datos = new byte[5];
        byte byteS = 0;
        //armar el tipo de filtros
        if(radioKyN.isSelected()){
            
            
            
            byteS = 16;            
            int numeroPolos = (Integer)comboNumeroPolos.getSelectedItem();            
            byteS = (byte) (byteS + numeroPolos);
            
            
            double ca = Double.parseDouble( textFieldCa.getText() );
            ca = ca * 1000;
            short shortCa = (short)ca;
            
            
            
            double cb = Double.parseDouble( textFieldCb.getText() );
            cb = cb * 1000;
            short shortCb = (short)cb;
            
            byte[] arregloCa = UtilGasesModelo.toBytes(shortCa);
            arregloCa = UtilGasesModelo.invertirArreglo(arregloCa);
            
            byte[] arregloCb = UtilGasesModelo.toBytes(shortCb);
            arregloCb = UtilGasesModelo.invertirArreglo(arregloCb);
            
            datos = new byte[5];
            datos[0] = byteS;
            datos[1] = arregloCa[0];
            datos[2] = arregloCa[1];
            datos[3] = arregloCa[0];
            datos[4] = arregloCb[1];
            
            
            
        } else if(radioBessel.isSelected()){
            
            DecimalFormat df = new DecimalFormat("0000.0000");
            
            byteS = 34;//filtro bessel dos polos y escribir: 00100010
            
            
            double valorOmega = Double.parseDouble( textFieldOmega.getText() );
            valorOmega = valorOmega * 100;
            //int numeroDecimales = UtilGasesCapelec.numeroDecimales(valorOmega);
            String cadenaValor = df.format(valorOmega);           
                        
            String parteEntera = cadenaValor.substring(0,4);
            String parteDecimal = cadenaValor.substring(5,9);
            
            short shortPrimeraParteOmega = Short.valueOf(parteEntera);
            short shortSegundaParteOmega = Short.valueOf(parteDecimal);
            
            byte[] arregloPrimera = UtilGasesModelo.toBytes(shortPrimeraParteOmega);
            arregloPrimera = UtilGasesModelo.invertirArreglo(arregloPrimera);
            byte[] arregloSegunda = UtilGasesModelo.toBytes(shortSegundaParteOmega);
            arregloSegunda = UtilGasesModelo.invertirArreglo(arregloSegunda);
            
            
            datos = new byte[5];
            datos[0] = byteS;
            datos[1] = arregloPrimera[0];
            datos[2] = arregloPrimera[1];
            datos[3] = arregloSegunda[0];
            datos[4] = arregloSegunda[1];
            
        }
        
        
        
        
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
        //
        boolean[] flags = UtilGasesModelo.shortToFlags(datos[0]);
        int numeroPolos = datos[0] & 0x03;
        
        if(flags[4]){//filtro K y N
            DecimalFormat df = new DecimalFormat("#.###");//significa no decimales
            comboNumeroPolos.setSelectedIndex(numeroPolos);
            if(datos.length == 5){
                
                int valorCa = UtilGasesModelo.fromBytesToShort(datos[2], datos[1]);
                double ca = valorCa *0.001;
            
                textFieldCa.setText(df.format(ca));
            
                int valorCb = UtilGasesModelo.fromBytesToShort(datos[4],datos[3]);
                double cb = valorCb*0.001;
            
                textFieldCb.setText( df.format(cb));
                       
            }
            
            
            textFieldOmega.setText("");
                    
            
        }else if (flags[5]){//filtro de Bessel
            
            radioBessel.setSelected(true);
            
            DecimalFormat df = new DecimalFormat("#.####");
            int primerasCifras = UtilGasesModelo.fromBytesToShort(datos[2], datos[1]);
            
            primerasCifras = primerasCifras * 10000;
            int ultimasCifras = UtilGasesModelo.fromBytesToShort(datos[4],datos[3]);
            
            double omega = (primerasCifras + ultimasCifras)*0.000001;
            
            textFieldOmega.setText(df.format( omega));
            textFieldCa.setText("");
            textFieldCb.setText("");
            
           
            
        }
        
        
        
        
    }
        
   
    
}
