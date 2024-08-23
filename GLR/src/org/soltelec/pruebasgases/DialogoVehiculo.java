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
public class DialogoVehiculo extends JPanel implements ActionListener {

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

    public DialogoVehiculo() {
        crearComponentes();
        ponerComponentes();
    }//end of class DialogoMotos

    private void crearComponentes()  {    
        labelIniRpm = new JLabel(" # Rpm Fabrica Inicial");
        labelFinRpm = new JLabel(" # Rpm Fabrica Final");
        txtIniRpm = new JTextField("400");
        txtFinRpm = new JTextField("1100");
        lineBorder = BorderFactory.createLineBorder(Color.BLACK);      
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
        d.add(new DialogoVehiculo());
         d.setSize(890, 900);
        d.setVisible(true);

    }//end of

    public void fillData(String placas) {
        //this.idPrueba=idPrueba;
        this.placa=placas;
        InfoVehiculo infoVehiculo = new InfoVehiculo(4, 1, 1, 1, "");
        RegistrarMedidas reg = new RegistrarMedidas();
        try (Connection cn = reg.getConnection()) {
            infoVehiculo = reg.findInfoVehiculo(infoVehiculo, placas, cn);
            txtFieldEscapes.setText(String.valueOf(infoVehiculo.getNumeroExostos())); 
            numeroTiempos=infoVehiculo.getNumeroTiempos();
            if (infoVehiculo.getNumeroTiempos() == 4) {
                radioCuatroTiempos.setSelected(true);
                radioDosTiempos.setSelected(false);
            }else{
                radioCuatroTiempos.setSelected(false);
                radioDosTiempos.setSelected(true);
            }
           if(infoVehiculo.getRpmIni()==0){
               txtIniRpm.setText("800");
           }else{
              txtIniRpm.setText(String.valueOf(infoVehiculo.getRpmIni()));
           }
           if(infoVehiculo.getRpmFin()==0){
               txtFinRpm.setText("1800");
           }else{
              txtFinRpm.setText(String.valueOf(infoVehiculo.getRpmFin()));
           }
           lineaCarro=infoVehiculo.getNroLinea();
        diseno="Convencional";        
        } catch (Exception ex) {
        }
    }
    

    public void actionPerformed(ActionEvent e) {   
        try {        
            System.out.println(String.format("Nuevo formato DLGVEhiculos %s ", numeroCilindros, numeroEscapes, numeroTiempos));
            InfoVehiculo infoVehiculo = new InfoVehiculo(4, 1, 1, 1, "");          
            rpmIni= Integer.parseInt(this.txtIniRpm.getText());
            rpmFin= Integer.parseInt(this.txtFinRpm.getText());
            infoVehiculo.setRpmIni(rpmIni);
            infoVehiculo.setRpmFin(rpmFin);
            infoVehiculo.setNroLinea(lineaCarro);
            infoVehiculo.setDiseño("Convencional");
           
            //falta el numero de cilindros
            RegistrarMedidas reg = new RegistrarMedidas();
            try (Connection cn = reg.getConnection()) {
                reg.actualizarLivianoLineas(infoVehiculo,placa, cn);
           }
            CallableCiclosGasolina.rngIniRpm=Integer.parseInt(txtIniRpm.getText());
            CallableCiclosGasolina.rngFinRpm=Integer.parseInt(txtFinRpm.getText());
            this.cerrar();
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

        DialogoVehiculo dlg;

        public ManejadorDobleClick(DialogoVehiculo dlg) {
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
