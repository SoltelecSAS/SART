/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases.motocicletas;

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
import java.awt.Dimension;
import java.awt.GraphicsDevice;
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
import java.awt.Toolkit;
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
public class DialogoMotos extends JPanel implements ActionListener {

    private JRadioButton radioDosTiempos, radioCuatroTiempos;
    private JLabel labelCilindros, labelEscapes, labelIniRpm, labelFinRpm;
    private ButtonGroup groupButton;
    private Border lineBorder, tittledBorder;
    private JTextField txtFieldCilindros, txtFieldEscapes, txtIniRpm, txtFinRpm;
    private JPanel panelAuxiliar;
    private JButton buttonContinuar;
    boolean simulacion = false;
    private JCheckBox Scoter;
    private JRadioButton CatalizadorSI, CatalizadorNO,CatalizadorNA;
    private String placa;
    private int lineaCarro;

    private int numeroTiempos, numeroCilindros, numeroEscapes, Scooter, rpmIni, rpmFin, catalizadorVariable;

    public DialogoMotos() {
        crearComponentes();
        ponerComponentes();
    }//end of class DialogoMotos

    private void crearComponentes() {
        radioDosTiempos = new JRadioButton("2T");
        radioCuatroTiempos = new JRadioButton("4T");
        Scoter = new JCheckBox("#¿ES SCOOTER ?");
        CatalizadorSI = new JRadioButton("#¿TIENE CATALIZADOR?");
        CatalizadorNO = new JRadioButton("#¿NO TIENE CATALIZADOR?");
        CatalizadorNA = new JRadioButton("#¿NO APLICA CATALIZADOR?");
        
        ButtonGroup grupoRadios = new ButtonGroup();
        grupoRadios.add(CatalizadorSI);
        grupoRadios.add(CatalizadorNO);
        grupoRadios.add(CatalizadorNA);
        
        Scoter.setHorizontalTextPosition(SwingConstants.LEFT);
        CatalizadorSI.setHorizontalTextPosition(SwingConstants.LEFT);
        CatalizadorNO.setHorizontalTextPosition(SwingConstants.LEFT);
        CatalizadorNA.setHorizontalTextPosition(SwingConstants.LEFT);
        radioCuatroTiempos.setSelected(true);
        radioDosTiempos.setSelected(false);

        labelIniRpm = new JLabel(" # Rpm Fabrica Inicial");
        labelFinRpm = new JLabel(" # Rpm Fabrica Final");
        txtIniRpm = new JTextField("800");
        txtFinRpm = new JTextField("1800");
        labelCilindros = new JLabel(" # Cilindros");
        labelEscapes = new JLabel(" #ESCAPES FUNCIONALES");
        lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        tittledBorder = BorderFactory.createTitledBorder(lineBorder, "Tiempos");
        txtFieldCilindros = new JTextField("1");
        txtFieldEscapes = new JTextField("1");
        groupButton = new ButtonGroup();
        groupButton.add(radioDosTiempos);
        groupButton.add(radioCuatroTiempos);
        buttonContinuar = new JButton("Continuar");
        buttonContinuar.addActionListener(this);
        buttonContinuar.addKeyListener(new ManejadorDobleClick(this));

    }//end of method crearComponentes

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);
        c.insets = new Insets(10, 5, 10, 5);
        c.weightx = 0.5;//
        c.anchor = FIRST_LINE_START;
        filaColumna(0, 0, c);
//        this.add(labelCilindros, c);
//        c.fill = HORIZONTAL;
//        filaColumna(1, 0, c);
//        this.add(txtFieldCilindros, c);
//        c.fill = NONE;
//        c.anchor = FIRST_LINE_START;
        filaColumna(2, 0, c);
        this.add(labelEscapes, c);
        c.fill = HORIZONTAL;
        filaColumna(3, 0, c);
        this.add(txtFieldEscapes, c);
        filaColumna(4, 0, c);
        this.add(Scoter, c);
        filaColumna(5, 0, c);
        this.add(CatalizadorSI, c);
        filaColumna(6, 0, c);
        this.add(CatalizadorNO, c);
        filaColumna(7, 0, c);
        this.add(CatalizadorNA, c);
        filaColumna(8, 0, c);

        panelAuxiliar = new JPanel();
        panelAuxiliar.setLayout(new GridLayout(1, 2, 5, 5));
        panelAuxiliar.setBorder(tittledBorder);
        panelAuxiliar.add(radioDosTiempos);
        panelAuxiliar.add(radioCuatroTiempos);
        c.fill = HORIZONTAL;
        filaColumna(9, 0, c);
        this.add(panelAuxiliar, c);

        filaColumna(10, 0, c);
        this.add(labelIniRpm, c);
        c.fill = HORIZONTAL;
        filaColumna(11, 0, c);
        this.add(txtIniRpm, c);
        c.fill = NONE;
        c.anchor = FIRST_LINE_START;

        filaColumna(12, 0, c);
        this.add(labelFinRpm, c);
        c.fill = HORIZONTAL;
        filaColumna(13, 0, c);
        this.add(txtFinRpm, c);
        c.fill = NONE;
        c.anchor = FIRST_LINE_START;

        filaColumna(14, 0, c);
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
        d.add(new DialogoMotos());
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        d.setSize(400, 650);
        d.setLocation((screenSize.width / 4), (screenSize.height / 6));
        d.setVisible(true);

    }//end of*/

    public void fillData(String placas) {
        //this.idPrueba=idPrueba;
        this.placa = placas;
        InfoVehiculo infoVehiculo = new InfoVehiculo(0, 1, 1, 1, "");
        RegistrarMedidas reg = new RegistrarMedidas();
        try (Connection cn = reg.getConnection()) {
            infoVehiculo = reg.findInfoVehiculo(infoVehiculo, placas, cn);
            txtFieldEscapes.setText(String.valueOf(infoVehiculo.getNumeroExostos()));
            if (infoVehiculo.getNumeroTiempos() == 4) {
                radioCuatroTiempos.setSelected(true);
                radioDosTiempos.setSelected(false);
            } else {
                radioCuatroTiempos.setSelected(false);
                radioDosTiempos.setSelected(true);
            }
            if (infoVehiculo.getDiseño().equalsIgnoreCase("Scooter")) {
                Scoter.setSelected(true);
            }
            if (infoVehiculo.getRpmIni() == 0) {
                txtIniRpm.setText("800");
            } else {
                txtIniRpm.setText(String.valueOf(infoVehiculo.getRpmIni()));
            }
            if (infoVehiculo.getRpmFin() == 0) {
                txtFinRpm.setText("1800");
            } else {
                txtFinRpm.setText(String.valueOf(infoVehiculo.getRpmFin()));
            }
            lineaCarro = infoVehiculo.getNroLinea();
        } catch (Exception ex) {
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (radioDosTiempos.isSelected()) {
            numeroTiempos = 2;
        } else {
            numeroTiempos = 4;
        }
        if (Scoter.isSelected()) {
            Scooter = 1;
        } else {
            Scooter = 2;
        }
        if (CatalizadorSI.isSelected()) {
            catalizadorVariable = 1;//tiene catalizador
        } else if(CatalizadorNO.isSelected()) {
            catalizadorVariable = 2; //no tiene catalizador 
        }else if(CatalizadorNA.isSelected()){
            catalizadorVariable = 3;
        }else{
            catalizadorVariable = 0;
        }
        
        if (catalizadorVariable == 0) {
            JOptionPane.showMessageDialog(this, "Porfavor seleccione si presenta catalizador");
                return;
        }
        try {
            numeroEscapes = Integer.parseInt(txtFieldEscapes.getText());
            if (numeroEscapes <= 0) {
                JOptionPane.showMessageDialog(this, "Por Favor Introduzca un Numero Escape Valido ");
                return;
            }
            if (numeroEscapes > 2) {
                JOptionPane.showMessageDialog(this, "Por Favor Introduzca un Numero Escape Valido ");
                return;
            }
            numeroCilindros = Integer.parseInt(txtFieldCilindros.getText());
            System.out.println(String.format("Numero Cilindros: %s Numero Escapes: %s Numero Tiempos: %s ", numeroCilindros, numeroEscapes, numeroTiempos));
            InfoVehiculo infoVehiculo = new InfoVehiculo(numeroTiempos, 1, 1, 1, "");
            infoVehiculo.setNumeroExostos(numeroEscapes);
            infoVehiculo.setNumeroTiempos(numeroTiempos);
            if (Scooter == 1) {
                infoVehiculo.setDiseño("Scooter");
            } else {
                infoVehiculo.setDiseño("Convencional");
            }
            if (catalizadorVariable == 1) {
                infoVehiculo.setCatalizador("SI");
                System.out.println("CATALIZADOR SI");
            } else if(catalizadorVariable == 2){
                infoVehiculo.setCatalizador("NO");
                System.out.println("CATALIZADOR No");
            }else if (catalizadorVariable == 3){
                infoVehiculo.setCatalizador("NO APLICA");
                System.out.println("CATALIZADOR No Aplica");
            }
            rpmIni = Integer.parseInt(this.txtIniRpm.getText());
            rpmFin = Integer.parseInt(this.txtFinRpm.getText());
            infoVehiculo.setRpmIni(rpmIni);
            infoVehiculo.setRpmFin(rpmFin);
            infoVehiculo.setNroLinea(lineaCarro);
            //falta el numero de cilindros
            RegistrarMedidas reg = new RegistrarMedidas();
            try (Connection cn = reg.getConnection()) {
                reg.actualizarInfoVehiculo(infoVehiculo, placa, cn);
            }
            CallablePruebaMotos.rngIniRpm = Integer.parseInt(txtIniRpm.getText());
            CallablePruebaMotos.rngFinRpm = Integer.parseInt(txtFinRpm.getText());
            this.cerrar();
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(this, "Por Favor Introduzca SOLO numeros ");
            return;
        } catch (Exception ne) {
            JOptionPane.showMessageDialog(this, "Ocurrio error debido en " + ne.getMessage());
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

    public int getScooter() {
        return Scooter;
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

    public JButton getButtonContinuar() {
        return buttonContinuar;
    }

    class ManejadorDobleClick extends KeyAdapter {

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
