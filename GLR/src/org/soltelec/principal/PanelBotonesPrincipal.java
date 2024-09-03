/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.*/
package org.soltelec.principal;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.REMAINDER;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.soltelec.luxometro.HiloPruebaLuxometro;
import org.soltelec.luxometro.JDialogLuces;
import org.soltelec.luxometro.JDialogLucesMoto;
import org.soltelec.medicionrpm.JDialogServCapelec;
import org.soltelec.medicionrpm.centralauto.PanelServicioCentral;
import org.soltelec.procesosopacimetro.JDialogPruebaLinealidad;
import org.soltelec.procesosopacimetro.JDialogServicioOpacimetro;
import org.soltelec.pruebasgases.*;
import org.soltelec.sonometro.JDialogSonometro;
import org.soltelec.medicionrpm.centralauto.PanelServicioTB8600;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PanelBotonesPrincipal extends JPanel implements ActionListener {

    private JButton buttonPruebaGasolina, buttonPruebaGases, buttonPruebaLuces;
    private JButton buttonServicioGasolina, buttonServicioGases, buttonServicioCapelec;
    private JButton buttonFoto, buttonGasesMoto, buttonLucesMoto, buttonLinealidadOp;
    private JButton buttonSonometro;
    JDialogPruebaGasolina dlgPruebaGasolina;
    JDialogLuces dlgLuces;
    DialogServicio dlgServicio;
    JDialogServicioOpacimetro dlgServicioOpacimetro;
    private JDialogServCapelec dlgServCapelec;
    private JButton buttonPruebaMoto;

    private JDialogSonometro dlgSonometro;
    private JDialogPruebaLinealidad dlgPruebaLinealidad;
    private JDialogLucesMoto dlgLucesMoto;
    private JLabel lblTitulo, lblSubtitulo;
    private JButton buttonServicioCentralAuto, buttonServicioTB8600;

    public PanelBotonesPrincipal() {
        crearComponentes();
        ponerComponentes();
        crearDialogos();
    }

    private void crearDialogos() {
        Window w = SwingUtilities.getWindowAncestor(this);
        dlgServCapelec = new JDialogServCapelec(null, true);
        dlgServCapelec.setTitle("SART 1.7.3");
    }

    private void crearComponentes()  {

        Font f = new Font(Font.SERIF, Font.BOLD, 42);
        Font f1 = new Font(Font.SERIF, Font.BOLD, 38);
        buttonPruebaGasolina = new JButton("Prueba Gasolina");
        buttonPruebaGasolina.addActionListener(this);
        buttonPruebaGases = new JButton("Prueba Gases Diesel");
        buttonPruebaGases.addActionListener(this);
        buttonPruebaLuces = new JButton("Prueba Luces");
        buttonPruebaLuces.addActionListener(this);
        buttonServicioGasolina = new JButton("Banco de Gases");
        buttonServicioGasolina.addActionListener(this);
        buttonServicioGases = new JButton("Opacimetro");
        buttonServicioGases.addActionListener(this);
        buttonServicioCapelec = new JButton("Kit capelec");
        buttonServicioCapelec.addActionListener(this);
        buttonServicioCentralAuto = new JButton("Central Auto");
        buttonServicioCentralAuto.addActionListener(this);
        buttonServicioTB8600 = new JButton("TB8600");
        buttonServicioTB8600.addActionListener(this);
        buttonFoto = new JButton("Captura Fotos");
        buttonFoto.addActionListener(this);
        buttonGasesMoto = new JButton("GasesMoto");
        buttonGasesMoto.addActionListener(this);
        buttonLinealidadOp = new JButton("Linealidad Op");
        buttonLinealidadOp.addActionListener(this);
        buttonLucesMoto = new JButton("LucesMoto");
        buttonLucesMoto.addActionListener(this);
        buttonPruebaMoto = new JButton("Gases Moto");
        buttonPruebaMoto.addActionListener(this);
        buttonSonometro = new JButton("Sonometro");
        buttonSonometro.addActionListener(this);
        lblTitulo = new JLabel("PROGRAMA DE SERVICIO");
        lblTitulo.setFont(f);
        lblSubtitulo = new JLabel("SOLTELEC SAS");
        lblSubtitulo.setFont(f1);

        JButton botones[] = new JButton[]{buttonFoto, buttonGasesMoto, buttonLinealidadOp, buttonLucesMoto,
            buttonPruebaGases, buttonPruebaGasolina, buttonPruebaLuces, buttonPruebaMoto, buttonServicioCapelec,
            buttonServicioGases, buttonServicioGasolina, buttonSonometro, buttonServicioCentralAuto, buttonServicioTB8600};

        for (JButton b : botones) {
            b.setFont(f1);
        }
    }

    private void ponerComponentes() {
        GridBagLayout bag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(bag);

        //c.weightx = 0.75;
        //c.weighty = 0.75;
        c.insets = new Insets(10, 5, 10, 5);
        c.gridwidth = REMAINDER;
        filaColumna(0, 0, c);
        this.add(lblTitulo, c);
        filaColumna(1, 0, c);
        this.add(lblSubtitulo, c);
        filaColumna(1, 1, c);
        c.gridwidth = 1;
        c.fill = HORIZONTAL;
        //this.add(buttonPruebaGasolina,c);
        filaColumna(1, 2, c);
        //this.add(buttonPruebaGases,c);
        filaColumna(1, 3, c);
        //this.add(buttonPruebaLuces,c);
        filaColumna(2, 1, c);
        this.add(buttonServicioGasolina, c);
        filaColumna(2, 2, c);
        this.add(buttonServicioGases, c);
        filaColumna(2, 3, c);
        this.add(buttonServicioCapelec, c);
        filaColumna(2, 4, c);
        this.add(buttonServicioCentralAuto, c);
        filaColumna(3, 2, c);
        //this.add(buttonLinealidadOp,c);
        this.add(buttonServicioTB8600, c);
        filaColumna(3, 3, c);
        //this.add(buttonPruebaMoto,c);
        filaColumna(4, 1, c);
        //this.add(buttonSonometro,c);
        filaColumna(4, 2, c);
        //this.add(buttonLucesMoto,c);
    }//end of method ponerComponentes

    private void filaColumna(int fila, int columna, GridBagConstraints c) {
        c.gridx = columna;
        c.gridy = fila;
    }
    public Image getIconImage() {
        ImageIcon image = new ImageIcon(this.getClass().getResource("/imagenes/car.png"));
        return image.getImage();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.gc();
        Window w = SwingUtilities.getWindowAncestor(this);

        if (e.getSource() == buttonPruebaGasolina) {
            dlgPruebaGasolina = new JDialogPruebaGasolina(null, true);
            dlgPruebaGasolina.setTitle("SART Version:1.7.3 copyright  2009 ");  
               //CallableInicioGases hilo = new CallableInicioGases(dlgPruebaGasolina.getPanelPruebaGases());
            //(new Thread(hilo)).start();
            //dlgPruebaGasolina.setVisible(true);
        } else if (e.getSource() == buttonPruebaGases) {
            dlgPruebaGasolina = new JDialogPruebaGasolina(null, true);

        } else if (e.getSource() == buttonPruebaLuces) {
            dlgLuces = new JDialogLuces(null, true);
            HiloPruebaLuxometro hilo = new HiloPruebaLuxometro(dlgLuces.getPanelLuxometro(),1);
            (new Thread(hilo)).start();
            dlgLuces.setVisible(true);
        } else if (e.getSource() == buttonServicioGasolina) {
            dlgServicio = new DialogServicio(null, true);
            dlgServicio.setTitle("SART Version:1.7.3 copyright  2009 ");
            dlgServicio.setIconImage(getIconImage());
            dlgServicio.setVisible(true);
        } else if (e.getSource() == buttonServicioGases) {
            dlgServicioOpacimetro = new JDialogServicioOpacimetro(null, true);
            dlgServicioOpacimetro.setTitle("SART Version:1.7.3 copyright  2009 ");
            dlgServicioOpacimetro.setIconImage(getIconImage());
            dlgServicioOpacimetro.setVisible(true);
        } else if (e.getSource() == buttonServicioCapelec) {
            dlgServCapelec.setVisible(true);
        } else if (e.getSource() == buttonFoto) {
               //dlgCamara.iniciar();
            //            dlgCamara = new JDialogCamara((Frame)w, true);

        } else if (e.getSource() == buttonLinealidadOp) {
            dlgPruebaLinealidad = new JDialogPruebaLinealidad(null, true);
            PruebaLinealidad hiloPruebaLinealidad = new PruebaLinealidad();
            hiloPruebaLinealidad.setPanel(dlgPruebaLinealidad.getPanel());
            (new Thread(hiloPruebaLinealidad)).start();
            dlgPruebaLinealidad.setVisible(true);
        } else if (e.getSource() == buttonPruebaMoto) {

        } else if (e.getSource() == buttonSonometro) {
            dlgSonometro = new JDialogSonometro(null, true, 9/*idPrueba*/, 1/*idUsuario*/, 4/*idHojaPrueba*/);
        } else if (e.getSource() == buttonLucesMoto) {
            dlgLucesMoto = new JDialogLucesMoto(null, true, 9L/*idPrueba*/, 1/*idUsuario*/, 4/*idHojaPrueba*/,1,"SERIAL", "moto");
        } else if (e.getSource() == buttonServicioCentralAuto) {
            PanelServicioCentral gui = new PanelServicioCentral();
            JDialog app = new JDialog();
            app.add(gui);
            app.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            app.setSize(app.getToolkit().getScreenSize());
            app.setModal(true);
            app.setVisible(true);

        } else if (e.getSource() == buttonServicioTB8600) {
            PanelServicioTB8600 gui = new PanelServicioTB8600();
            JDialog app = new JDialog();
            app.add(gui);
            app.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            app.setSize(app.getToolkit().getScreenSize());
            app.setModal(true);
            app.setVisible(true);

        }
    }//end of method actionPerformed

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

//                try {
//
//                UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
//
//                } catch (Exception e) {
//
//                    System.out.println("Substance Graphite failed to initialize");
//
//                }
//                Status status = UtilLogin.loginAdminDBCDA();//muestra el dialogo para el login de la aplicacion, solamente usuario administrador
//        
//                    if( status == JXLoginPane.Status.SUCCEEDED){
//                        iniciarProgramaServicio();
//                    }else {
//                        JOptionPane.showMessageDialog(null,"Autenticacion fallida");
//                    }   
                iniciarProgramaServicio();

            }
        });

    }

    private static void iniciarProgramaServicio() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PanelBotonesPrincipal panel = new PanelBotonesPrincipal();
                JFrame app = new JFrame();
                app.getContentPane().add(panel);
                app.setExtendedState(JFrame.MAXIMIZED_BOTH);
                app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                app.setVisible(true);
            }
        });
    }

}
