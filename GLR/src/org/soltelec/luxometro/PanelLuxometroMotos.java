/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.luxometro;

import javax.swing.JButton;
import org.soltelec.componenteluces.LuzBaja;
import org.soltelec.componenteluces.LuzAlta;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import eu.hansolo.steelseries.gauges.DisplayMulti;
import eu.hansolo.steelseries.tools.LcdColor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static java.awt.GridBagConstraints.*;
import org.soltelec.componenteluces.LuzExploradora;

/**
 *
 * @author Usuario
 */

/*
 * Codigos de las medidas para la prueba de motos
 * 2000 intensidad luz alta derecha en motos
 * 2001 intensidad alta izquierda en motos
 * 2002 angulo inclinacion baja izquierad para motos
 * 2013 angulo inclinacion baja derecha en motos
 * 2014 intensidad de luz baja derecha en motos
 * 2015 intensidad de luz baja izquierda en motos
 *
 *
 *
 *
 *
 */
public class PanelLuxometroMotos extends JPanel 
{

    private GridBagLayout bag;
    private GridBagConstraints contenedor;
    private Font f = new Font(Font.SERIF, Font.PLAIN, 30);
    private Font f1 = new Font(Font.SERIF, Font.PLAIN, 40);
    private JLabel labelTitulo;
    private DisplayMulti display;
    private JButton buttonCancelar;
    private LuzExploradora[] exploradoras;
    private LuzAlta altaUno,altaDos;
    private LuzBaja bajaUno,bajaDos;
    private LuzExploradora exploradoraUno,exploradoraDos,exploradoraTres,exploradoraCuatro;
    private JLabel labelLuzBajaDerecha,labelLuzAltaDerecha,labelLuzBajaIzquierda,labelLuzAltaIzquierda;
    
    public PanelLuxometroMotos()
    {
        super();
        bag= new GridBagLayout();
        contenedor = new GridBagConstraints();
        contenedor.insets = new Insets(5,5,5,5);
        contenedor.weightx = 1.0;
        contenedor.weighty = 1.0;
        this.setLayout(bag);
        contenedor.gridwidth = REMAINDER;
        filaColumna(0,0,contenedor);

        agregarTituloPanel();
          exploradoras();
//        farolaAltaUno();
//        farolaBajaUno();
//        farolaAltaDos();
//        farolaBajaDos();
//        agregarDisplay();
//        botonCancelar();
    }
    
    private void filaColumna(int fila, int columna, GridBagConstraints c) 
    {
        c.gridx = columna;
        c.gridy = fila;
    }//end of method filaColumna

    public void cerrar() 
    {
        (SwingUtilities.getWindowAncestor(this)).dispose();
        altaUno.setBlinking(false);
        bajaUno.setBlinking(false);
//        bajaDos.setBlinking(false);
//        altaDos.setBlinking(false);
       
    }

    public static void main(String args[]) {
        JFrame app = new JFrame();
        PanelLuxometroMotos p = new PanelLuxometroMotos();
        app.getContentPane().add(p);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HiloLucesMoto h = new HiloLucesMoto(p,1,"SERIAL");
        p.getButtonCancelar().addActionListener(h);
        Thread t = new Thread(h);
        //t.start();
        app.setVisible(true);
    }//end of method main

    
    
    //-----------------------------------------------
    public DisplayMulti getDisplay() 
    {
        return display;
    }

    public JButton getButtonCancelar() 
    {
        return buttonCancelar;
    }
    
    //-----------------------------------------------
    public void agregarDisplay(boolean mostrar)
    {
        display = new DisplayMulti();
        display.setDigitalFont(true);
        display.setLcdValue(0);
        display.setLcdDecimals(0);
        display.setValueCoupled(false);
        display.setLcdColor(LcdColor.STANDARD_LCD);
        display.setLcdUnitString("s");
        display.setPreferredSize(new Dimension(120,100));
      //c.fill = BOTH;
        contenedor.gridheight = 1;
        filaColumna(2,2,contenedor);
        this.add(display,contenedor);
        display.setVisible(mostrar);
    }
    
    public void botonCancelar(boolean mostrar)
    {
        buttonCancelar = new JButton("Cancelar");
        buttonCancelar.setForeground(Color.red);
        filaColumna(4,2,contenedor);
        this.add(buttonCancelar,contenedor);
        buttonCancelar.setVisible(mostrar);
    }
    
    
    //------------------Titulo-------------
    
    public JLabel getLabelTitulo() 
    {
        return labelTitulo;
    }
    
    public void agregarTituloPanel()
    {
        labelTitulo=new JLabel();//"INICIA PRUEBA LUCES PARA MOTO"
        labelTitulo.setFont(f1);
        labelTitulo.setForeground(Color.RED);
        this.add(labelTitulo,contenedor);
        contenedor.gridwidth = REMAINDER;
        filaColumna(0,3,contenedor);
        labelTitulo.setVisible(true);
    }

    
    //------------------ Exploradoras   -------------
    
    public LuzExploradora[] getExploradoras()
    {
        return exploradoras;
    }
    
    public void exploradoras() 
    {
        exploradoraUno = new LuzExploradora();
        exploradoraDos = new LuzExploradora();
        exploradoraTres = new LuzExploradora();
        exploradoraCuatro = new LuzExploradora();
        
        exploradoras = new LuzExploradora[]{exploradoraUno, exploradoraDos, exploradoraTres, exploradoraCuatro};
        for (LuzExploradora l : exploradoras) 
        {
            l.setPreferredSize(new Dimension(50, 50));
            l.setVisible(false);
        }

        //añadidas las exploradoras
        JPanel panelExploradoras = new JPanel();
        for (LuzExploradora l : exploradoras)
        {
            panelExploradoras.add(l);
        }
        filaColumna(1,0,contenedor);
        this.add(panelExploradoras,contenedor);
        contenedor.gridwidth = 1; 
    }
    
    public void habilitarExploradoras(int numero)
    {
        for (int i= 0; i < numero; i++ )
        {
            exploradoras[i].setVisible(true);
        }
    }
    

    //------------------ Alta derecha -------------
    
    public JLabel getTituloLuzAltaUno()
    {
        return labelLuzAltaDerecha;
    }
    
    private void tituloLuzAltaUno()
    {
        labelLuzAltaDerecha = new JLabel("Alta 1");
        labelLuzAltaDerecha.setFont(f); 
        filaColumna(1,5,contenedor);
        this.add(labelLuzAltaDerecha,contenedor);
        labelLuzAltaDerecha.setVisible(false);
    }
    
    public void farolaAltaUno(boolean mostrar)
    {
        altaUno=new LuzAlta(); 
        JPanel panelEnvoltor = new JPanel();
        panelEnvoltor.add(altaUno);
        contenedor.gridheight = 2;
        filaColumna(2,4,contenedor);
        this.add(panelEnvoltor,contenedor);
        altaUno.setVisible(mostrar);
        tituloLuzAltaUno();
        getTituloLuzAltaUno().setVisible(mostrar);
    }
    
    public LuzAlta getLuzAltaUno()
    {
        return altaUno;
    }
    
    
    //------------------ Alta Izquierda -------------
    
    public JLabel getTituloAltaDos()
    {
        return labelLuzAltaIzquierda;
    }
    
    private void tituloAltaDos()
    {
        labelLuzAltaIzquierda = new JLabel("Alta 2");
        labelLuzAltaIzquierda.setFont(f);
        contenedor.gridwidth = 1;
        filaColumna(2,0,contenedor);
        this.add(labelLuzAltaIzquierda,contenedor);
        labelLuzAltaIzquierda.setVisible(false);
    }
    
    public void  farolaAltaDos(boolean mostrar)
    {
        altaDos=new LuzAlta();
        JPanel panelEnvoltor = new JPanel();
        filaColumna(2,1,contenedor);
        panelEnvoltor.add(altaDos);
        this.add(panelEnvoltor,contenedor);
        altaDos.setVisible(mostrar);
        tituloAltaDos();
        getTituloAltaDos().setVisible(mostrar);
    }
   
    public LuzAlta getLuzAltaDos() 
    {
        return altaDos;
    }
    
        
    //------------------ Baja derecha -------------
    
    public JLabel getTituloBajaUno()
    {
        return labelLuzBajaDerecha;
    }
    
    private void tituloBajaUno()
    {
        labelLuzBajaDerecha = new JLabel("Baja 1");
        labelLuzBajaDerecha.setFont(f);
        filaColumna(3,5,contenedor);
        this.add(labelLuzBajaDerecha,contenedor);
        labelLuzBajaDerecha.setVisible(false);
    }
    
    public void farolaBajaUno(boolean mostar) 
    {
        JPanel panelEnvoltor = new JPanel();
        bajaUno = new LuzBaja();
        panelEnvoltor.add(bajaUno);
        filaColumna(3,4,contenedor);
        this.add(panelEnvoltor,contenedor);
        bajaUno.setVisible(mostar);
        tituloBajaUno();
        getTituloBajaUno().setVisible(mostar);
    }
 
    public LuzBaja getLuzBajaUno() 
    {
        return bajaUno;
    }
    
    
    //------------------ Baja Izquierda -------------
    
    public JLabel getTituloBajaDos()
    {
        return labelLuzBajaIzquierda;
    }

    private void tituloBajaDos()
    {
        labelLuzBajaIzquierda = new JLabel("Baja 2");
        labelLuzBajaIzquierda.setFont(f);
        contenedor.gridheight = 1;
        filaColumna(4,0,contenedor);       
        this.add(labelLuzBajaIzquierda,contenedor);
        labelLuzBajaIzquierda.setVisible(false);
    }
    
    public void farolaBajaDos(boolean mostrar)
    {
        JPanel panelEnvoltor = new JPanel();
        bajaDos = new LuzBaja();
        panelEnvoltor.add(bajaDos);
        contenedor.gridheight = 2;
        filaColumna(3,1,contenedor);
        this.add(panelEnvoltor,contenedor);
        bajaDos.setVisible(mostrar);
        tituloBajaDos();
        getTituloBajaDos().setVisible(mostrar);
    }
    
    public LuzBaja getLuzBajaDos() 
    {

        return bajaDos;
    }
    
}
