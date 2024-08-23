/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.rowset.CachedRowSetImpl;
import java.awt.event.MouseAdapter;
import gnu.io.CommPortIdentifier;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;
import static java.awt.GridBagConstraints.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.soltelec.models.controllers.Conexion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.soltelec.models.controllers.Conexion;

/**
 * Dialogo para proporcionar la seleccion de la información del tacometro para
 * usarlo en la prueba
 *
 * @author GerenciaDesarrollo Soluciones Tecnologicas
 */
public class DialogoRPMTempMoto extends JPanel implements ActionListener {

    private JComboBox comboNumeroCilindros, comboFormaMedicion, comboPuertoSerial;  
    private JRadioButton radioKit, radioBanco, radioCentralAuto, radioTB8600;
    private JLabel labelTitulo;
    private JButton buttonOk;
    private final String[] opcionesCilindros = {"1", "2", "3", "4", "5", "6"};
    private JRadioButton radioCuatroTiempos, radioDosTiempos;

    private final String[] opcionesFormaMedicion = {"Vibracion"};
    private ButtonGroup group, groupTiempos;
    JPanel panelRadios, panelRadiosTiempos;

    //
    private int numeroCilindros;
    private boolean usaMultiplicador;
    public boolean simulacion;
    private String equipo;
    private String metodoMedicion;
    private Map<String, CommPortIdentifier> mapaPuertos;
    private CommPortIdentifier commPortIdentifier;

    public DialogoRPMTempMoto() {
        iniciarComponentes();
        ponerComponentes();
    }//end of constructor

    private void iniciarComponentes() {
        comboNumeroCilindros = new JComboBox(opcionesCilindros);
        comboNumeroCilindros.setSelectedIndex(1);
        comboFormaMedicion = new JComboBox(opcionesFormaMedicion);
        radioKit = new JRadioButton("Kit Capelec", false);
        radioKit.addActionListener(this);
       
        radioBanco = new JRadioButton("Banco", false);
        radioBanco.addActionListener(this);
        //24 de Nov 2011 kit CentralAuto añadido
        radioCentralAuto = new JRadioButton("MGT 300 (EVO)",true);
        radioCentralAuto.addActionListener(this);
        //30 de Nov 2011 kit Central Auto tb8600
        radioTB8600 = new JRadioButton("Kit TB8600");
        radioTB8600.addActionListener(this);
        group = new ButtonGroup();
        group.add(radioKit);
        group.add(radioCentralAuto);
        group.add(radioBanco);
        group.add(radioTB8600);

        //radios para el numero de tiempos
        radioCuatroTiempos = new JRadioButton("4T");
        radioCuatroTiempos.setSelected(true);
        radioDosTiempos = new JRadioButton("2T");
        groupTiempos = new ButtonGroup();
        groupTiempos.add(radioCuatroTiempos);
        groupTiempos.add(radioDosTiempos);

        buttonOk = new JButton("OK");
        buttonOk.addActionListener(this);
        labelTitulo = new JLabel("Dispositivo Medicion \n RPM y T° Aceite");
        this.setFocusable(true);

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                String consulta = ("select cont_cda from cda where id_cda = 1");
                boolean versimulacion;
                try {
                    versimulacion = Conexion.ejecutarVerificarSimulacion(consulta);
                    if (versimulacion) {
                    super.mouseClicked(e);
                    requestFocusInWindow();
                    labelTitulo.setForeground(Color.RED);
                    simulacion = true; //simulacion activada o desactivada   
                }
                } catch (SQLException ex) {
                    Logger.getLogger(DialogoRPMTempMoto.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(DialogoRPMTempMoto.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DialogoRPMTempMoto.class.getName()).log(Level.SEVERE, null, ex);
                }

                  

            }

        });
        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_L:
                            simulacion = true;
                            labelTitulo.setForeground(Color.BLUE);
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        GridLayout grid = new GridLayout(2, 1, 5, 5);
        panelRadios = new JPanel(grid);
        panelRadios.add(radioKit);
        panelRadios.add(radioBanco);
        panelRadios.add(radioCentralAuto);
        panelRadios.add(radioTB8600);
        //un borde
        TitledBorder titled, titled1, titled2, titled3;
        titled = BorderFactory.createTitledBorder("Dispositivo");
        titled1 = BorderFactory.createTitledBorder("Numero Cilindros");
        titled2 = BorderFactory.createTitledBorder("Metodo");
        titled3 = BorderFactory.createTitledBorder("Tiempos del Motor");
        //titled3.setTitleColor(Color.red);
        panelRadios.setBorder(titled);

        panelRadiosTiempos = new JPanel();
        panelRadiosTiempos.add(radioCuatroTiempos);
        panelRadiosTiempos.add(radioDosTiempos);
        panelRadiosTiempos.setBorder(titled3);

        comboNumeroCilindros.setBorder(titled1);
        comboFormaMedicion.setBorder(titled2);       
//        //mapaPuertos = PortSerialUtil.listPorts();
//        String[] opciones;
//        Object[] objetos = mapaPuertos.keySet().toArray();
//        opciones = new String[objetos.length];
//        for (int i = 0; i<objetos.length; i++){
//            opciones[i] = (String) objetos[i];
//        }
//        comboPuertoSerial = new JComboBox(opciones);
//        comboPuertoSerial.setBorder(titled3);

        this.setFocusable(true);
    }//end of method iniciarComponentes

    private void ponerComponentes() {
        //poner los radioButtons

        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        //
        c.insets = new Insets(5, 10, 5, 10);
        this.setLayout(bag);
        c.gridwidth = REMAINDER;
        c.anchor = CENTER;
        filaColumna(0, 0, c);
        add(labelTitulo, c);
        c.anchor = PAGE_START;
        c.fill = HORIZONTAL;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.weightx = 1.0;
        c.weighty = 1.0;
        // c.weighty = 1.0;
        filaColumna(1, 0, c);
        add(panelRadios, c);
        c.gridwidth = 2;
        c.gridheight = 1;
        filaColumna(1, 2, c);
        add(comboNumeroCilindros, c);
        filaColumna(2, 2, c);
        add(comboFormaMedicion, c);
        filaColumna(3, 2, c);
        c.gridheight = 2;
        add(panelRadiosTiempos, c);
        c.gridheight = 1;
        filaColumna(5, 2, c);
       // add(checkSistemaDist, c);
        filaColumna(6, 2, c);
        c.anchor = ABOVE_BASELINE;
        c.gridheight = 1;
        add(buttonOk, c);
    }//end of ponerComponentes method

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//end of method filaColumna

    public static void main(String args[]) {
        JFrame app = new JFrame("Dialogo Tacometro");
        app.setSize(500, 300);
        app.getContentPane().add(new DialogoRPMTempMoto());
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);

    }//end of method

    public String getEquipo() {
        return equipo;
    }

    public String getMetodoMedicion() {
        return metodoMedicion;
    }

     public int getNumeroCilindros() {
        return numeroCilindros;
    }

    public CommPortIdentifier getCommPortIdentifier() {
        return commPortIdentifier;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonOk) {
            numeroCilindros = Integer.parseInt((String) comboNumeroCilindros.getSelectedItem());
          System.out.println("btn kit rpm 1");
            if (radioKit.isSelected()) {
                equipo = "kit";
            } else if (radioBanco.isSelected()) {
                equipo = "banco";
            } else if (radioCentralAuto.isSelected()) {
                equipo = "centralAuto";///////////ojojojoojojoojo
            } else if (radioTB8600.isSelected()) {
                equipo = "tb8600";
            }
            System.out.println("btn kit rpm 2");
            metodoMedicion = (String) comboFormaMedicion.getSelectedItem();
            System.out.println("btn kit rpm 3");
           // usaMultiplicador = checkSistemaDist.isSelected();
            System.out.println("termine logica de btn kit rpm :");
            //commPortIdentifier = mapaPuertos.get((String)comboPuertoSerial.getSelectedItem());
            (SwingUtilities.getWindowAncestor(this)).dispose();
        } else if (e.getSource() == radioBanco) {
            if (radioBanco.isSelected()) {
                comboFormaMedicion.setEnabled(false);
//            comboPuertoSerial.setEnabled(false);
            } else {
                comboFormaMedicion.setEnabled(true);
                //           comboPuertoSerial.setEnabled(true);
            }
        } else if (e.getSource() == radioKit) {
            if (radioKit.isSelected()) {
                comboFormaMedicion.setEnabled(true);
                //           comboPuertoSerial.setEnabled(true);
            }
        }

    }//end of actionPerformed

    public JRadioButton getRadioBanco() {
        return radioBanco;
    }

    public JRadioButton getRadioCentralAuto() {
        return radioCentralAuto;
    }

    public JRadioButton getRadioKit() {
        return radioKit;
    }

    public JRadioButton getRadioTB8600() {
        return radioTB8600;
    }

    public int getNumeroTiempos() {
        if (radioCuatroTiempos.isSelected()) {
            return 4;

        } else {

            return 2;
        }
    }  

}//end of class DialogRPMTemp
