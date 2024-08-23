/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;
//import eu.hansolo.custom.mbutton.MButton;

import com.soltelec.loginadministrador.ConsultasLogin;
import com.soltelec.loginadministrador.LoginServiceCDA;
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
import org.soltelec.conexion_seriales.Conexion;
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
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import static java.awt.GridBagConstraints.*;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXLoginPane;


import org.soltelec.pruebasgases.motocicletas.JDialogMotosGases;
import org.soltelec.util.LeerArchivo;
import org.soltelec.util.Mensajes;

import org.soltelec.util.RegistrarMedidas;

public class PanelPruebaGases extends JPanel implements ActionListener {

    JDialogMotosGases frmC;
    private BancoSensors banco;
    JProgressBar progressBar;
    private final JLabelPersonalizada mensaje;
    private JLabel funcion;
    private Long idUsuario;
    private final JButton buttonFinalizar, buttonRpm, btnContHumo, btnWorkerCicloMotos /*, buttonKit buttonSimulacion,
             buttonBanco*/;
    Radial radialTacometro;
    Linear linearTemperatura;
    JPanel panelFiguras;
    JPanel panelRadios;
    PanelMensaje panelMensaje;
    private final JButton[] botones;
    private JDialogMotosGases dialogMotosGases; //Para cerrar el dialogo de gases
    private PanelPruebaGases panelCancelacion;

    public void setIdUsuario(Long id){
        this.idUsuario = id;
    }

    private String placas;

    public void setPlacas(String placas){
        this.placas = placas;
    }

    private long idPrueba;

    public void setIdPrueba(Long id){
        this.idPrueba = id;
    }

    
    public PanelPruebaGases() {
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
        Font f = new Font("Serif", Font.PLAIN, 42);
        Font fBotones = new Font("Serif", Font.PLAIN, 24);
        progressBar.setFont(f);
        progressBar.setBorderPainted(true);
        progressBar.setStringPainted(true);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0;
        mensaje = new JLabelPersonalizada("");
        c.fill = HORIZONTAL;
        c.anchor = CENTER;
        c.gridx = 0;
        c.gridy = 2;
        this.add(mensaje, c);
        funcion = new JLabel();
        c.gridx = 1;
        c.gridy = 3;
        this.add(funcion, c);
        c.gridx = 0;
        c.gridy = 3;
        this.add(progressBar, c);
        buttonRpm = new JButton("CAMBIAR RPMS");//crear un boton ejecutarWorkerCicloMoto no visible y que se ejecute automaticamente, dejar la responsibilidad de rechazar por rpm a este b
        buttonRpm.setForeground(Color.BLUE);
        buttonRpm.setFont(fBotones);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 3;
        c.gridy = 4;
        this.add(buttonRpm, c);
        btnWorkerCicloMotos = new JButton("Rechazar por RPM");
        btnWorkerCicloMotos.setVisible(LeerArchivo.rechazarPorRpm());
        btnWorkerCicloMotos.setForeground(Color.CYAN);
        btnWorkerCicloMotos.setFont(fBotones);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 4;
        c.gridy = 4;
        this.add(btnWorkerCicloMotos, c);

        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cronometro.png"));
        btnContHumo = new JButton(image);
        btnContHumo.setText("10 Seg.");
        btnContHumo.setVisible(false);
        btnContHumo.setForeground(Color.BLACK);
        btnContHumo.setFont(fBotones);
        btnContHumo.setBounds(new Rectangle(90, 30, 35, 75));
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 4;
        this.add(btnContHumo, c);

        buttonFinalizar = new JButton("Abortar Prueba");
        buttonFinalizar.setVisible(true);
        buttonFinalizar.setForeground(Color.red);
        buttonFinalizar.setFont(fBotones);
        buttonFinalizar.addActionListener(this);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 4;
        this.add(buttonFinalizar, c);
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = LINE_END;
//        this.add(buttonSimulacion,c);
//        c.gridx = 2;c.gridy = 4;
//        this.add(buttonKit,c);
//        c.gridx = 3;c.gridy = 4;
//        this.add(buttonBanco,c);
        c.gridx = 4;
        c.gridy = 4;
        this.add(buttonFinalizar, c);

        botones = new JButton[]{/*buttonBanco,*/buttonRpm,/* buttonFinalizar,buttonKit,buttonSimulacion*/};
//        for (JButton b : botones) {
//            b.setVisible(true);
//        }
        //this.setFocusable(true);
        //   this.addKeyListener(new ListenerTeclado());
    }//end of constructor

    private void configurarPanelMedidas() {
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
        panelFiguras.add(radialTacometro, c);
        c.gridx = 1;
        c.gridy = 0;
        c.fill = VERTICAL;
        configurarTemometro();
        JPanel panelEnvoltor = new JPanel(new BorderLayout());
        panelEnvoltor.setPreferredSize(new Dimension(150, 100));
        panelEnvoltor.add(linearTemperatura);
        panelFiguras.add(panelEnvoltor, c);
    }

    public void cancelacion() {
        cerrar();
        String strSeleccion = null;
        try {
            frmC = new JDialogMotosGases(null, false, 0, 0, 0, null, placas, null);
        } catch (IOException ex) {
        }
        frmC.setTitle("SART 1.7.3 ABORTO DE PRUEBA");
        panelCancelacion = new PanelPruebaGases();
        panelCancelacion.setDialogMotosGases(frmC);
        frmC.getContentPane().add(panelCancelacion);
        frmC.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        ImageIcon image = new ImageIcon(getClass().getResource("/imagenes/cancelar.png"));
        panelCancelacion.getPanelMensaje().setText("PRUEBA DE GASES ABORTADA ");
        panelCancelacion.getProgressBar().setVisible(false);
        JLabel lblImg = (JLabel) panelCancelacion.getPanelMensaje().getComponent(0);
        lblImg.setIcon(image);
        frmC.setIconImage(new ImageIcon(getClass().getResource("/imagenes/cancelar.png")).getImage());
        panelCancelacion.getMensaje().setText("ESPERANDO AL INGENIERO DE PISTA ..!");
        panelCancelacion.getButtonFinalizar().setVisible(false);
        JXLoginPane pane = new JXLoginPane(new LoginServiceCDA(true));

        pane.setBannerText("ADMINISTRADOR");
        pane.setErrorMessage("Usuario No Autorizado para Abortar Prueba de Gases");

        pane.setLocale(new Locale("ES"));

        frmC.setVisible(true);
        JXLoginPane.Status status = JXLoginPane.showLoginDialog(frmC, pane);
        while (true) {
            if (status == JXLoginPane.Status.SUCCEEDED) {
                String nombreUsuario = pane.getUserName();//El nombre de usuario que se acaba de autenticar
                //traer el id de ese usuario
                System.out.println("Usuario :" + nombreUsuario);
                //preguntar el motivo de la cancelacion
                boolean pressMotivo = false;
                Object objSeleccion = null;
                objSeleccion = JOptionPane.showInputDialog(
                        null,
                        "Motivo del Aborto Prueba Gases",
                        "Abortar Prueba Por:",
                        JOptionPane.QUESTION_MESSAGE,
                        null, // null para icono defecto
                        new Object[]{
                            "Fallas del equipo de medicion",
                            "Falla subita de fluido electrico del equipo de medicion",
                            "Bloqueo forzado del equipo de medicion",
                            "Ejecucion Incorrecta de la Prueba"},
                        "Causales de Aborto");
                strSeleccion = (String) objSeleccion;
                guardarCausaAborto(strSeleccion);
                System.out.println("va preguntar comentario");
                if (Mensajes.mensajePregunta("¿Desea Agregar otra Observacion Referente al Aborto?")) {
                    panelCancelacion.getMensaje().setText("Registro de Ampliacion de Observaciones Encontradas..!");
                    System.out.println("va instanciar comentario");
                    org.soltelec.pruebasgases.FrmComentario frm = new org.soltelec.pruebasgases.FrmComentario(SwingUtilities.getWindowAncestor(frmC), idUsuario, JDialog.DEFAULT_MODALITY_TYPE, strSeleccion, idUsuario, "aborto", 0);
                    System.out.println("ya instancio");
                    frm.setLocationRelativeTo(panelCancelacion);
                    System.out.println("entra bloque comentario");
                    frm.setModal(true);
                    frm.setVisible(true);
                    JOptionPane.showMessageDialog(null, "Aborto de la  prueba por: " + strSeleccion);

                } else {
                    try {
                        RegistrarMedidas regMedidas = new RegistrarMedidas();
                        Connection cn = regMedidas.getConnection();
                        ConsultasLogin consultasLogin = new ConsultasLogin();
                        regMedidas.registrarCancelacion(strSeleccion, strSeleccion, this.idUsuario, idUsuario);//Registra la prueba como Rechazada           
                    } catch (SQLException | ClassNotFoundException exc) {
                        Mensajes.mostrarExcepcion(exc);
                    } finally {//al cancelar la prueba se deben liberar todos los recursos.

                    }
                }
                panelCancelacion.cerrar();
                break;
            }
            if (status == JXLoginPane.Status.NOT_STARTED || status == JXLoginPane.Status.CANCELLED) {
                Mensajes.mensajeAdvertencia("DISCULPE ESPERANDO  CANCELACION POR PARTE DEL DIRECTOR TECNICO");
                status = JXLoginPane.showLoginDialog(frmC, pane);
            }
        }
    }

    // Método para guardar la causa de aborto en la base de datos
    private void guardarCausaAborto(String causaAborto) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Conectar a la base de datos (modifica los parámetros según tu configuración)
            conn = DriverManager.getConnection(Conexion.getUrl(), Conexion.getUsuario(), Conexion.getContrasena());

            // Consulta SQL para actualizar la tabla 'pruebas'
            String sql = "UPDATE pruebas SET Comentario_aborto = ?, Abortada = ?, Finalizada = ? WHERE id_pruebas = ?";

            // Preparar la declaración
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, causaAborto);
            pstmt.setString(2, "Y"); // Marcar como abortada
            pstmt.setString(3, "Y"); // Marcar como finalizada
            pstmt.setLong(4, idPrueba);

            // Ejecutar la actualización
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    public JButton getBtnWorkerCicloMotos() {
        return btnWorkerCicloMotos;
    }

    public void setPanelFiguras(JPanel panelFiguras) {
        this.panelFiguras = panelFiguras;
    }

    public PanelMensaje getPanelMensaje() {
        return panelMensaje;
    }

    public JLabel getFuncion() {
        return this.funcion;
    }

    public static void main(String args[]) {
        JFrame app = new JFrame();
        PanelPruebaGases panelC = new PanelPruebaGases();
        app.add(panelC);
        //panelC.iniciar();
        app.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        panelC.getPanelMensaje().setVisible(false);
        panelC.getPanelFiguras().setVisible(true);//puede ser false
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);

        //panelC.iniciarProceso();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonFinalizar) {
            this.cancelacion();
        } else {
            //TODO añadir codigo para registrar el defecto por 
//            System.out.println("se preciono el boton btnWorkerCicloMotos");
//            RegistrarMedidas regMedidas = new RegistrarMedidas();
//            
//            try {
//                regMedidas.registraRechazo(" ", "rechazo por revoluciones fuera de rango. ", idUsuario, idPrueba, 84018);
//            } catch (SQLException ex) {
//                Logger.getLogger(PanelPruebaGases.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(PanelPruebaGases.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            System.out.println("Ya registre medidas de rechazo por RPM: " + "idPrueba = " + 15);
//            System.out.println("Cierre de Panel ");
//
//            this.cerrar();
        }
    }//end of method actionPerformed

    // Método para cerrar la ventana y guardar la causa de aborto en la base de datos
    public void cerrar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        w.setVisible(false);
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
//        radialTacometro.setTickLabelPeriod(1000);
        //      radialTacometro.setScaleDividerPower(2);
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
        linearTemperatura.setTitle("T. Aceite");
        linearTemperatura.setUnitString("°C");
        linearTemperatura.setThreshold(40);
        // linearTemperatura.setBackgroundColor(BackgroundColor.BRUSHED_METAL);
        final Section[] SECTIONS = {new Section(0, 40, java.awt.Color.RED), new Section(41, 59, java.awt.Color.ORANGE), new Section(60, 99, java.awt.Color.RED)};
        linearTemperatura.setSections(SECTIONS);
        linearTemperatura.setSectionsVisible(true);
        linearTemperatura.setFrameVisible(true);
//        linearTemperatura.setFrame3dEffectVisible(true);       
        /*linearTemperatura.setFrameDesign(FrameDesign.BLACK_METAL);
        linearTemperatura.setBackgroundColor(BackgroundColor.BLACK);*/
        linearTemperatura.setFrameDesign(eu.hansolo.steelseries.tools.FrameDesign.GLOSSY_METAL);
        linearTemperatura.setFrameEffect(eu.hansolo.steelseries.tools.FrameEffect.EFFECT_INNER_FRAME);
        linearTemperatura.setValueColor(ColorDef.YELLOW);
        linearTemperatura.setValue(60);
        linearTemperatura.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        linearTemperatura.setAreasVisible(true);
        linearTemperatura.setAutoResetToZero(true);
        linearTemperatura.setBackgroundColor(eu.hansolo.steelseries.tools.BackgroundColor.LIGHT_GRAY);
        linearTemperatura.setCustomLcdUnitFontEnabled(true);
        linearTemperatura.setDigitalFont(true);
        linearTemperatura.setFocusTraversalPolicyProvider(true);
        linearTemperatura.setLabelNumberFormat(eu.hansolo.steelseries.tools.NumberFormat.STANDARD);
        linearTemperatura.setLcdBlinking(true);
        linearTemperatura.setLedBlinking(true);
        linearTemperatura.setLedVisible(true);
        linearTemperatura.setMajorTickmarkType(eu.hansolo.steelseries.tools.TickmarkType.CIRCLE);
        linearTemperatura.setOpaque(true);
        linearTemperatura.setPeakValue(38.0);
        linearTemperatura.setTickmarkSectionsVisible(true);
        linearTemperatura.setTrackSection(10.0);
        linearTemperatura.setTrackSectionColor(Color.RED);
        linearTemperatura.setUnitStringVisible(false);
        linearTemperatura.setUserLedOn(false);

    }

    public JButton getButtonRpm() {
        return buttonRpm;
    }

    public JButton getButtonFinalizar() {
        return buttonFinalizar;
    }

    public JButton getBtnContHumo() {
        return this.btnContHumo;
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
