/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.pruebasgases;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.soltelec.procesosbanco.JLabelPersonalizada;
import static java.awt.GridBagConstraints.*;

public class PanelMensaje extends JPanel {
    private String mensaje = "";
    private JLabel icono;
    private JTextArea textArea;
    private JTextPane textPane;
    private Border compound;
    private JLabelPersonalizada cronometro;
    private Border raisedbevel;
    private Border loweredbevel;
    private Dimension dimension1;
    private Dimension dimension2;
    public PanelMensaje(){
        this.setBackground(Color.WHITE);
        inicializarInterfaz();
    }//end of connstructor

    private void inicializarInterfaz() {
        //ImageIcon image = new ImageIcon("./images/Warning.png");
        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/Warning.png"));
        Font f = new Font(Font.SERIF,Font.PLAIN,44);
        Font eve= new Font( Font.SANS_SERIF, Font.BOLD, 60 );
        GridBagLayout bag = new GridBagLayout();
        this.setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();
        filaColumna(0,0,c);
        icono = new JLabel(image);
        this.add(icono,c);
        c.gridwidth = GridBagConstraints.RELATIVE;
        filaColumna(1,0,c);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 1.0;
//        textArea = new JTextArea();
//        textArea.setFont(f);
//        textArea.setText(mensaje);
//        textArea.setEditable(false);
//        JPanel panelTextArea = new JPanel();
//        dimension1 = this.getToolkit().getScreenSize();
//        dimension2 = new Dimension((int)(dimension1.getWidth()*0.95),(int)(dimension1.height*0.25));
//        panelTextArea.setPreferredSize(dimension2);
//        panelTextArea.add(textArea);
//        this.add(panelTextArea,c);
        textPane = new JTextPane();
        dimension1 = this.getToolkit().getScreenSize();
        dimension2 = new Dimension((int)(dimension1.getWidth()*45.90),(int)(dimension1.height*4.25));
        textPane.setPreferredSize(dimension1);
        textPane.setFont(eve);
        textPane.setEditable(false);
        textPane.setOpaque(true);
        textPane.setEditorKit(new MyEditorKit());
        SimpleAttributeSet attrs=new SimpleAttributeSet();
        StyleConstants.setAlignment(attrs,StyleConstants.ALIGN_CENTER);
        StyledDocument doc=(StyledDocument)textPane.getDocument();
        try {
            doc.insertString(0, "", attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(PanelMensaje.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc.setParagraphAttributes(0,doc.getLength()-1,attrs,false);
        filaColumna(2,0,c);
        this.add(textPane,c);
        c.fill = NONE;
        c.anchor = CENTER;
        c.weighty = 0.0;
        cronometro = new JLabelPersonalizada("");
        cronometro.setFont(eve);
        cronometro.setPreferredSize((new Dimension(700,400)));
        this.add(cronometro,c);
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        loweredbevel = BorderFactory.createLoweredBevelBorder();
        compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
        this.setBorder(compound);
        this.setPreferredSize(new Dimension(400,400));

    }//end of inicializarInterfaz

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }

    public void setText(String string) {
        this.textPane.setText(string);
    }

    public void appendText(String string){
      SimpleAttributeSet attrs=new SimpleAttributeSet();
      StyleConstants.setAlignment(attrs,StyleConstants.ALIGN_CENTER);
        try {
            textPane.getDocument().insertString(0, string, attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(PanelMensaje.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public JLabelPersonalizada getCronometro() {
        return cronometro;
    }
}//end of class