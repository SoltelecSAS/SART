/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.sonometro;
//import eu.hansolo.custom.mbutton.MButton;

import org.soltelec.pruebasgases.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.IconView;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import org.soltelec.procesosbanco.JLabelPersonalizada;
import org.soltelec.procesosbanco.BancoSensors;
import eu.hansolo.steelseries.gauges.Linear;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.ColorDef;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.GaugeType;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.PointerType;
import eu.hansolo.steelseries.tools.Section;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import static java.awt.GridBagConstraints.*;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.soltelec.pruebasgases.motocicletas.JDialogMotosGases;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.propiedadesSonometro;

/**
 * Clase para mostrar la velocidad del auto y la temperatura del mismo principal
 * panel de la prueba de gases
 *
 * 15 Sept 2011 modificacion al panel adicionados botones para prueba con kit,
 * con banco y simulada
 *
 * @author Usuario
 */
public class PanelPruebaSonometro extends JPanel implements ActionListener {

    private BancoSensors banco;
    JProgressBar progressBar;
    private final JLabelPersonalizada mensaje;
    private final JButton buttonFinalizar, buttonRpm/*, buttonKit, buttonSimulacion,
             buttonBanco*/;
    Radial radialTacometro;
    Linear linearTemperatura;
    JPanel panelFiguras;
    JPanel panelRadios;
    PanelMensaje panelMensaje;
    private final JButton[] botones;
    private JDialogMotosGases dialogMotosGases; //Para cerrar el dialogo de gases
//    private propiedadesSonometro PropiedadesSonometro;

    public PanelPruebaSonometro() {
        //this.setBackground(Color.BLACK);
        //ListenerDeshabilitar listenerDeshabilitar = new ListenerDeshabilitar();
        panelMensaje = new PanelMensaje();
        GridBagLayout bag = new GridBagLayout();
        this.setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 10, 20, 10);
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        mensaje = new JLabelPersonalizada("");
        configurarPanelMedidas();
        //Poner un panel con los radioButtons para la medicion de temperatura y rpms
        //poner en el panel de Figuras
        //panelFiguras.add(panelRadios);
        c.gridx = 0;
        c.gridy = 0;
        panelFiguras.setVisible(false);
        this.add(panelFiguras, c);
        this.add(panelMensaje, c);
//        panelVerificacionDiesel = new PanelVerificacionDiesel();
//        this.add(panelVerificacionDiesel,c);
        //this.setPreferredSize(new Dimension(820,450));
        progressBar = new JProgressBar();
        Font f = new Font("Serif", Font.PLAIN, 48);
        Font fBotones = new Font("Serif", Font.PLAIN, 24);
        progressBar.setFont(f);
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0;

        c.fill = HORIZONTAL;
        c.anchor = CENTER;

        c.gridx = 0;
        c.gridy = 2;
        this.add(mensaje, c);
        c.gridx = 0;
        c.gridy = 3;
        this.add(progressBar, c);
        buttonRpm = new JButton("CAMBIAR RPMS");
        buttonRpm.setForeground(Color.BLUE);
        buttonRpm.setFont(fBotones);
        //buttonRpm.setVisible(false);
        //    buttonRpm.addActionListener(listenerDeshabilitar);
        buttonFinalizar = new JButton("Cancelar Prueba");
        buttonFinalizar.setVisible(true);
        buttonFinalizar.setForeground(Color.red);
        buttonFinalizar.setFont(fBotones);
        // buttonFinalizar.addActionListener(this);
//        buttonKit = new JButton("Kit capelec");
//        buttonKit.addActionListener(listenerDeshabilitar);
//        buttonBanco = new JButton("Banco ");
//        buttonBanco.addActionListener(listenerDeshabilitar);
//        buttonSimulacion = new JButton("Simulacion");
//        buttonSimulacion.addActionListener(listenerDeshabilitar);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 4;
        this.add(buttonRpm, c);
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = LINE_END;
//        this.add(buttonSimulacion,c);
//        c.gridx = 2;c.gridy = 4;
//        this.add(buttonKit,c);
//        c.gridx = 3;c.gridy = 4;
//        this.add(buttonBanco,c);
//        c.gridx = 4;c.gridy = 4;
        this.add(buttonFinalizar, c);

        botones = new JButton[]{/*buttonBanco,*/buttonRpm, buttonFinalizar/*,buttonKit,buttonSimulacion*/};
        for (JButton b : botones) {
            b.setVisible(false);
        }
        //this.setFocusable(true);
        //   this.addKeyListener(new ListenerTeclado());
        
        
    }//end of constructor

    private void configurarPanelMedidas() 
    {
        propiedadesSonometro PropiedadesSonometro=new propiedadesSonometro();
        panelFiguras = new JPanel();
        panelFiguras.setBackground(Color.BLACK);
        GridBagLayout bag = new GridBagLayout();
        panelFiguras.setLayout(bag);
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        //c.fill = HORIZONTAL;
        configurarTacometro();
//        if (Propiedades.getInstance().getRpmPruebaRuidoHabilitada()) {
        if (PropiedadesSonometro.cargarConfiProperties()) 
        {
            panelFiguras.add(mensaje, c);
        } else {
            panelFiguras.add(radialTacometro, c);
        }
        c.gridx = 1;
        c.gridy = 0;
        c.fill = VERTICAL;
        configurarTemometro();
        JPanel panelEnvoltor = new JPanel(new BorderLayout());
        panelEnvoltor.setPreferredSize(new Dimension(150, 100));
        panelEnvoltor.add(linearTemperatura);
//        if (!Propiedades.getInstance().getRpmPruebaRuidoHabilitada()) {
        if (!PropiedadesSonometro.cargarConfiProperties()) 
        { 
            panelFiguras.add(panelEnvoltor, c);
        }
    }

//    private boolean cargarConfiProperties() 
//    {
//        System.out.println("---------------------------------------------------");
//        System.out.println("-----------   cargarConfiProperties   -------------");
//        System.out.println("---------------------------------------------------");
//        try 
//        {
//            System.out.println("Cargando la propiedad rpmPruebaRuidoHabilitada en GLR");
//            boolean rpmPruebaRuidoHabilitada=Boolean.parseBoolean(UtilPropiedades.cargarPropiedad("rpmPruebaRuidoHabilitada", "propiedades.properties"));
//            System.out.println("Valor de la  porpiedad rpmPruebaRuidoHabilitada " + rpmPruebaRuidoHabilitada);
//            return rpmPruebaRuidoHabilitada;
//        } catch (IOException e) {
//            System.out.println("----------------------------------------------");
//            System.out.println("Errro en el metodo : " + e.getMessage() + e.getLocalizedMessage());
//            System.out.println("----------------------------------------------");
//            JOptionPane.showMessageDialog(null,"Erro al cagar la propiedad rpmPruebaRuidoHabilitada"+ e.getMessage());
//        }
//        return false;
//    }
    
    public void mostrarBotonFinalizar() {
        buttonFinalizar.setVisible(true);
    }

    public void mostrarBotonDilucion() {
        buttonRpm.setVisible(true);
    }

    public void ocultarBotonDilucion() {
        buttonRpm.setVisible(false);
    }

    public BancoSensors getBanco() {
        return banco;
    }

    public JLabelPersonalizada getMensaje() {
        return mensaje;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JPanel getPanelFiguras() {
        return panelFiguras;
    }

    public void setPanelFiguras(JPanel panelFiguras) {
        this.panelFiguras = panelFiguras;
    }

    public PanelMensaje getPanelMensaje() {
        return panelMensaje;
    }

    public static void main(String args[]) {

//        Propiedades.getInstance().setRpmPruebaRuidoHabilitada(true);
        JFrame app = new JFrame();
        PanelPruebaSonometro panelC = new PanelPruebaSonometro();
        app.add(panelC);
        panelC.getMensaje().setText("Prueba");
        //panelC.iniciar();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelC.getPanelMensaje().setVisible(false);
        panelC.getPanelFiguras().setVisible(true);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);

        //panelC.iniciarProceso();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonFinalizar) {
            this.cerrar();
        } else {
            //TODO añadir codigo para registrar el defecto por dilucion
        }
    }//end of method actionPerformed

    public void cerrar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        w.setVisible(false);
        /*
         Window w1 = SwingUtilities.getWindowAncestor(w);
         if(w1 != null)
         w1.dispose();*/
        //System.gc();
    }

    public void cerrarDialogoMotosGases() {
        if (dialogMotosGases != null) {
            dialogMotosGases.setVisible(false);
        }
    }

    private void configurarTacometro() {
        radialTacometro = new Radial();
        Dimension d = this.getToolkit().getScreenSize();
        int size = (int) (d.height * 0.43);
        radialTacometro.setPreferredSize(new Dimension(size, size));
        radialTacometro.setBackgroundColor(BackgroundColor.BLACK);
        radialTacometro.setMaxValue(8000);
        radialTacometro.setMinValue(0);
        radialTacometro.setTitle("RPM");
        radialTacometro.setUnitString("rev/min");
       // radialTacometro.setTickLabelPeriod(1000);
        //radialTacometro.setScaleDividerPower(2);
        radialTacometro.setGaugeType(GaugeType.TYPE3);
        final Section[] SECTIONS = {new Section(0, 5900, java.awt.Color.WHITE), new Section(6000, 8000, java.awt.Color.RED)};
        radialTacometro.setTickmarkSections(SECTIONS);
        radialTacometro.setTickmarkSectionsVisible(true);
        radialTacometro.setThreshold(2500);
        radialTacometro.setTickmarkColor(Color.ORANGE);
        //radialTacometro.setGaugeType(GaugeType.TYPE);
        radialTacometro.setPointerType(PointerType.TYPE2);
        radialTacometro.setFrameDesign(FrameDesign.BLACK_METAL);
       // radialTacometro.setFrame3dEffectVisible(true);
        radialTacometro.setPointerColor(ColorDef.GRAY);
        radialTacometro.setLcdColor(LcdColor.REDDARKRED_LCD);
        //radialTacometro.setLcdColor(LcdColor.BLUE2_LCD);
        radialTacometro.setDigitalFont(true);
    }

    private void configurarTemometro() {
        linearTemperatura = new Linear();
        linearTemperatura.setPreferredSize(new Dimension(300, 100));
        linearTemperatura.setTitle("T Aceite");
        linearTemperatura.setUnitString("°C");
        linearTemperatura.setThreshold(60);
        // linearTemperatura.setBackgroundColor(BackgroundColor.BRUSHED_METAL);
        final Section[] SECTIONS = {new Section(0, 40, java.awt.Color.RED), new Section(41, 59, java.awt.Color.ORANGE), new Section(60, 99, java.awt.Color.GREEN)};
        linearTemperatura.setSections(SECTIONS);
        linearTemperatura.setSectionsVisible(true);
        linearTemperatura.setFrameVisible(true);
        //linearTemperatura.setFrame3dEffectVisible(true);
        
        linearTemperatura.setFrameDesign(FrameDesign.BLACK_METAL);
        linearTemperatura.setBackgroundColor(BackgroundColor.BLACK);
        linearTemperatura.setValueColor(ColorDef.RED);
        linearTemperatura.setValue(60);

    }

    public JButton getButtonRpm() {
        return buttonRpm;
    }

    public JButton getButtonFinalizar() {
        return buttonFinalizar;
    }

    public Linear getLinearTemperatura() {
        return linearTemperatura;
    }

    public void setLinearTemperatura(Linear linearTemperatura) {
        this.linearTemperatura = linearTemperatura;
    }

    public Radial getRadialTacometro() {
        return radialTacometro;
    }

    public void setRadialTacometro(Radial radialTacometro) {
        this.radialTacometro = radialTacometro;
    }

    public JButton[] getBotones() {
        return botones;
    }

//    public JButton getButtonBanco() {
//        return buttonBanco;
//    }
//
//    public JButton getButtonKit() {
//        return buttonKit;
//    }
//
//    public JButton getButtonSimulacion() {
//        return buttonSimulacion;
//    }
    @Override
    public boolean isFocusable() {
        return true;
    }

//    class ListenerDeshabilitar implements ActionListener{
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if(e.getSource() == buttonSimulacion){
//                //deshabilita Kit y Banco
//                buttonBanco.setVisible(false);
//                buttonKit.setVisible(false);
//            }else if(e.getSource() == buttonBanco){
//                //deshabilitar Simulacion y Banco
//                buttonKit.setVisible(false);
//                buttonSimulacion.setVisible(false);
//            }else if(e.getSource() == buttonKit){
//                buttonBanco.setVisible(false);
//                buttonSimulacion.setVisible(false);
//            }else if(e.getSource() == buttonRpm){
//                buttonBanco.setVisible(true);
//                buttonSimulacion.setVisible(true);
//                buttonKit.setVisible(true);
//            }
//        }//end of actionPerformed
//    }//end of ActionListener
//    class ListenerTeclado extends KeyAdapter{
//
//        @Override
//        public void keyPressed(KeyEvent ke) {
//
//            switch(ke.getKeyCode()){
//                case KeyEvent.VK_F3:
//                    if(ke.isControlDown()){
//                        JDialogUsuarioContraseña jdialogUc = new JDialogUsuarioContraseña(null, true);
//                        final DialogoUsuario du = jdialogUc.getPanel();
//                        du.getBotonCancelar().removeActionListener(du);
//                        du.getBotonOK().removeActionListener(du);//se remueve el action listener por
//                        //defecto que tiene
//                        du.getBotonOK().addActionListener(new ActionListener() {
//                        @Override
//                        public void actionPerformed(ActionEvent e) {
//
//                            String nombreUsuario = du.getCampoUsuario().getText();
//                            char[] password = du.getPass().getPassword();
//                            String strContraseña = new String(password);
//                            String contraseña = "STLC";
//                            Calendar c = Calendar.getInstance();
//                            String diaMes = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
//                            String nuevaContraseña = "STLC"+diaMes;
//                            if(nombreUsuario.equals("ADMIN")){
//                                if(strContraseña.equals(nuevaContraseña)){
//                                   du.setLoginCorrecto(true);
//                                   (SwingUtilities.getWindowAncestor(du)).dispose();
//                                }
//                                else {
//                                JOptionPane.showMessageDialog(null,"Usuario o contraseña incorrectos");
//                                }
//                            } else {
//                                JOptionPane.showMessageDialog(null,"Usuario o contraseña incorrectos");
//                            }
//                        }
//                        });
//                        du.getBotonCancelar().addActionListener(new ActionListener() {
//                            @Override
//                            public void actionPerformed(ActionEvent e) {
//                                (SwingUtilities.getWindowAncestor(du)).dispose();
//                            }
//                        });
//                        jdialogUc.setVisible(true);//que mute!!!
//                        if (du.isLoginCorrecto()){
//                            buttonSimulacion.setVisible(true);
//                        }
//                    }//end of if ke.isDown
//
//
//                break;
//            }//end of switch
    //  }//end of method keyPressed
    //}//end of method KeyAdapter
    /**
     * @param dialogMotosGases the dialogMotosGases to set
     */
    public void setDialogMotosGases(JDialogMotosGases dialogMotosGases) {
        this.dialogMotosGases = dialogMotosGases;
    }
}//end of class PanelPruebaGases

/**
 * 22 de Marzo de 2011 modificacion cambio de JTextArea por JTextPane
 *
 * @author Usuario
 */
class MyEditorKit extends StyledEditorKit {

    @Override
    public ViewFactory getViewFactory() {
        return new StyledViewFactory();
    }

    static class StyledViewFactory implements ViewFactory {

        @Override
        public View create(javax.swing.text.Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                switch (kind) {
                    case AbstractDocument.ContentElementName:
                        return new LabelView(elem);
                    case AbstractDocument.ParagraphElementName:
                        return new ParagraphView(elem);
                    case AbstractDocument.SectionElementName:
                        return new CenteredBoxView(elem, View.Y_AXIS);
                    case StyleConstants.ComponentElementName:
                        return new ComponentView(elem);
                    case StyleConstants.IconElementName:
                        return new IconView(elem);
                }
            }

            return new LabelView(elem);
        }
    }
}

class CenteredBoxView extends BoxView {

    public CenteredBoxView(Element elem, int axis) {
        super(elem, axis);
    }
//    protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
//        super.layoutMajorAxis(targetSpan,axis,offsets,spans);
//        int textBlockHeight = 0;
//        int offset = 0;
//        for (int i = 0; i < spans.length; i++) {
//            textBlockHeight = spans[i];
//        }
//        offset = (targetSpan - textBlockHeight) / 2;
//        for (int i = 0; i < offsets.length; i++) {
//            offsets[i] += offset;
//        }
//    }
}
