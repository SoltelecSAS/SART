package org.soltelec.pruebasgases;

import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static java.awt.GridBagConstraints.*;

class PanelValorFiltros extends JPanel implements ActionListener {

    private JTextField txtFieldValor1, txtFieldValor2, txtFieldValor3, txtFieldValor4;
    private JButton buttonOk;
    private double valorFiltro1, valorFiltro2, valorFiltro3, valorFiltro4;
    private JLabel lblTitulo, lblUno, lblDos, lblTres, lblCuatro;

    public PanelValorFiltros() {
        crearComponentes();
        ponerComponentes();

    }//end of constructor

    private void crearComponentes() {
        lblTitulo = new JLabel(" DIGITE LOS VALORES DE LOS FILTROS:");
        lblUno = new JLabel("Filtro1: ");
        lblDos = new JLabel("Filtro2: ");
        lblTres = new JLabel("Filtro3: ");
        lblCuatro = new JLabel("Filtro4: ");
        txtFieldValor1 = new JTextField("0");
        txtFieldValor1.setEditable(false);
        txtFieldValor2 = new JTextField("30");
        txtFieldValor2.setEditable(false);
        txtFieldValor3 = new JTextField("60");
        txtFieldValor3.setEditable(false);
        txtFieldValor4 = new JTextField("100");
        txtFieldValor4.setEditable(false);
        buttonOk = new JButton("OK");
        buttonOk.addActionListener(this);
    }//end of method crearComponentes

    public void actionPerformed(ActionEvent e) {
        //validacion de cadena
        boolean filtro1Invalido = false;
        boolean filtro2Invalido = false;
        boolean filtro3Invalido = false;
        boolean filtro4Invalido = false;
        boolean filtrosEnOrden = true;
        StringBuilder sb = new StringBuilder();
        try {
            valorFiltro1 = Double.parseDouble(txtFieldValor1.getText());
        } catch (NumberFormatException ne) {
            filtro1Invalido = true;
            sb.append(" Valor Filtro 1 Invalido\n");
        }
        try {
            valorFiltro2 = Double.parseDouble(txtFieldValor2.getText());
        } catch (NumberFormatException ne) {
            filtro2Invalido = true;
            sb.append(" Valor Filtro 2 Invalido\n");
        }
        try {
            valorFiltro3 = Double.parseDouble(txtFieldValor3.getText());
        } catch (NumberFormatException ne) {
            filtro3Invalido = true;
            sb.append(" Valor Filtro 3 Invalido\n");
        }
        try {
            valorFiltro4 = Double.parseDouble(txtFieldValor4.getText());
        } catch (NumberFormatException ne) {
            filtro4Invalido = true;
            sb.append(" Valor del Filtro 4 invalido\n");
        }
        if (filtro1Invalido || filtro2Invalido || filtro3Invalido || filtro4Invalido) {
            JOptionPane.showMessageDialog(this, "Error!!\n" + sb.toString(), "Validacion", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            if (valorFiltro1 >= valorFiltro2 || valorFiltro2 >= valorFiltro3 || valorFiltro3 >= valorFiltro4) {
                filtrosEnOrden = false;
            }
            if (valorFiltro2 >= valorFiltro3) {
                filtrosEnOrden = false;
            }
        }
        if (!filtrosEnOrden) {
            JOptionPane.showMessageDialog(this, " Verifique filtro1 <= filtro2 <= filtro3 \n"
                    + " filtro2 <= filtro3 ", "Validacion", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean filtrosDifieren13 = false;
        boolean filtrosDifieren32 = false;
        boolean filtrosDifieren34 = false;
        boolean filtrosDifieren12 = false;

        if (Math.abs(valorFiltro3 - valorFiltro2) >= 15) {
            filtrosDifieren32 = true;
        }
        
        if (Math.abs(valorFiltro1 - valorFiltro2) >= 15) {
            filtrosDifieren12 = true;
        }
        if (Math.abs(valorFiltro1 - valorFiltro3) >= 15) {
            filtrosDifieren13 = true;
        }
        if (Math.abs(valorFiltro3 - valorFiltro4) >= 15) {
            filtrosDifieren34 = true;
        }

        if (!filtrosDifieren32) {
            JOptionPane.showMessageDialog(null, "Los valores del filtro 2 y el filtro 3 no no cumplen con la separacion minima 15 % segun NTC 4231 #4.2.2");
            return;
        }
        if (!filtrosDifieren12) {
            JOptionPane.showMessageDialog(null, "Los valores del filtro 1 y el filtro 2 no no cumplen con la separacion minima 15 % segun NTC 4231 #4.2.2");
            return;
        }
        if (!filtrosDifieren34) {
            JOptionPane.showMessageDialog(null, "Los valores del filtro 4 y el filtro 3 no no cumplen con la separacion minima 15 % segun NTC 4231 #4.2.2");
            return;
        }
        if (!filtrosDifieren13) {
            JOptionPane.showMessageDialog(null, "Los valores del filtro 1 y el filtro 3 no no cumplen con la separacion minima 15 % segun NTC 4231 #4.2.2");
            return;
        }
        this.setVisible(false);
        (SwingUtilities.getWindowAncestor(this)).dispose();
    }//end of method actionPerformed

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        Font fTitulo = new Font(Font.SERIF, Font.BOLD, 20);
        Font fTexto = new Font(Font.SERIF, Font.PLAIN, 16);
        this.setLayout(bag);
        c.insets = new Insets(10, 5, 10, 5);
        //c.weightx = 0.5;

        filaColumna(0, 0, c);
        c.gridwidth = REMAINDER;
        lblTitulo.setFont(fTitulo);
        this.add(lblTitulo, c);
        filaColumna(1, 0, c);
        c.gridwidth = 1;
        //poner la lblUno
        lblUno.setFont(fTexto);
        this.add(lblUno, c);
        filaColumna(1, 1, c);
        c.fill = HORIZONTAL;
        txtFieldValor1.setFont(fTexto);
        this.add(txtFieldValor1, c);
        c.fill = NONE;
        filaColumna(2, 0, c);
        lblDos.setFont(fTexto);
        this.add(lblDos, c);
        c.fill = HORIZONTAL;
        filaColumna(2, 1, c);
        txtFieldValor2.setFont(fTexto);
        this.add(txtFieldValor2, c);
        c.fill = NONE;
        filaColumna(3, 0, c);
        lblTres.setFont(fTexto);
        this.add(lblTres, c);
        c.fill = HORIZONTAL;
        filaColumna(3, 1, c);
        txtFieldValor3.setFont(fTexto);
        this.add(txtFieldValor3, c);
        filaColumna(4, 0, c);
        lblCuatro.setFont(fTexto);
        c.fill = NONE;
        this.add(lblCuatro, c);
        filaColumna(4, 1, c);
        txtFieldValor4.setFont(fTexto);
        c.fill = HORIZONTAL;
        this.add(txtFieldValor4, c);
        filaColumna(5, 0, c);
        c.gridwidth = 2;
        c.anchor = CENTER;
        c.fill = NONE;
        this.add(buttonOk, c);

    }//end of method ponerComponentes

    public JButton getButtonOk() {
        return buttonOk;
    }

    public double getValorFiltro1() {
        return valorFiltro1;
    }

    public double getValorFiltro2() {
        return valorFiltro2;
    }

    public double getValorFiltro3() {
        return valorFiltro3;
    }
    
    
     public void setValorFiltro2(Double valorFiltro2) {
       this.valorFiltro2=valorFiltro2;
       txtFieldValor2.setText(String.valueOf(valorFiltro2));
    }
     
      public void setValorFiltro3(Double valorFiltro3) {
       this.valorFiltro3=valorFiltro3;
       txtFieldValor3.setText(String.valueOf(valorFiltro3));
    }

   
    public double getValorFiltro4() {
        return valorFiltro4;
    }

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }//end of method filaColumna

}//end of class  PanelValorFiltros
