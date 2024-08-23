/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.temperaturadinamica;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Color;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleInsets;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import org.jfree.chart.ChartPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.PrintWriter;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static java.awt.GridBagConstraints.*;

/**
 *Clase para ajustar dinamicamente la curva de temperatura
 * con el fin de tomat lecturas desde el banco sensors
 * @author Usuario
 */
public class GUITemperatura extends JPanel implements ActionListener{

    private JSlider sliderPt0,sliderPt1,sliderPt2,sliderPt3,sliderPt4,sliderPt5;
    private JSlider sliderPt6,sliderPt7,sliderPt8,sliderPt9,sliderPt10;
    private JSlider sliderPt11,sliderPt12,sliderPt13,sliderPt14,sliderPt15;
    private JSlider sliderPt16,sliderPt17,sliderPt18,sliderPt19,sliderPt20;
    private JTextField[] etiquetas;
    private JPanel panelBotones;

    private JSlider sliders[];

    private JButton buttonCargar, buttonGuardar, buttonInvertir, buttonPredeterminado;
    private JButton buttonMas, buttonMenos,buttonConvertir;
    private double y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20;

    private JTextField txtFieldNumero;
    private XYSeries serie;
    private XYSeriesCollection dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    
    private  int[] x = {0,300,600,900,1200,1500,1800,2100,2400,2700,3000,
                                3300,3600,3900,4200,4500,4800,5100,5400,5700,6000};
    final JFileChooser fc = new JFileChooser();

    private DecimalFormat df ;
    private PrintWriter output;
    private Scanner input;

    public GUITemperatura(){
        crearComponentes();
        ponerComponentes();

        Locale locale = new Locale("US");//para que sea con punto separador de decimales
       df = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df instanceof DecimalFormat) {
            ((DecimalFormat) df).setDecimalSeparatorAlwaysShown(true);
        }
        df.setMaximumFractionDigits(2);
    }//end of constructor

    private void crearComponentes() {
        fc.setSelectedFile(new File("temp.txt"));
        fc.setApproveButtonText("Guardar");
        ManejadorSlider manejadorSlider  = new ManejadorSlider();
        ManejadorTextFields manejadorTextFields = new ManejadorTextFields();
        sliderPt0 = new JSlider(JSlider.VERTICAL,0,250,137); sliderPt1 = new JSlider(JSlider.VERTICAL,0,250,98);
        sliderPt2 = new JSlider(JSlider.VERTICAL,0,250,70); sliderPt3 = new JSlider(JSlider.VERTICAL,0,250,51);
        sliderPt4 = new JSlider(JSlider.VERTICAL,0,250,38); sliderPt5 = new JSlider(JSlider.VERTICAL,0,250,28);
        sliderPt6 = new JSlider(JSlider.VERTICAL,0,250,22); sliderPt7 = new JSlider(JSlider.VERTICAL,0,250,18);
        sliderPt8 = new JSlider(JSlider.VERTICAL,0,250,14); sliderPt9 = new JSlider(JSlider.VERTICAL,0,250,12);
        sliderPt10 = new JSlider(JSlider.VERTICAL,0,250,11); sliderPt11 = new JSlider(JSlider.VERTICAL,0,250,10);
        sliderPt12 = new JSlider(JSlider.VERTICAL,0,250,9); sliderPt13 = new JSlider(JSlider.VERTICAL,0,250,9);
        sliderPt14 = new JSlider(JSlider.VERTICAL,0,250,8); sliderPt15 = new JSlider(JSlider.VERTICAL,0,250,8);
        sliderPt16 = new JSlider(JSlider.VERTICAL,0,250,8); sliderPt17 = new JSlider(JSlider.VERTICAL,0,250,8);
        sliderPt18 = new JSlider(JSlider.VERTICAL,0,250,8); sliderPt19 = new JSlider(JSlider.VERTICAL,0,250,8);
        sliderPt20 = new JSlider(JSlider.VERTICAL,0,250,8);


        sliders = new JSlider[]{sliderPt0,sliderPt1,sliderPt2,sliderPt3,sliderPt4,
                                sliderPt5,sliderPt6,sliderPt7,sliderPt8,sliderPt9,
                                sliderPt10,sliderPt11,sliderPt12,sliderPt13,sliderPt14,
                                sliderPt15,sliderPt16,sliderPt17,sliderPt18,sliderPt19,
                                sliderPt20};
        etiquetas = new JTextField[21];
        for (int i = 0; i<=20; i++){
            etiquetas[i] = new JTextField(String.valueOf(sliders[i].getValue()));
            etiquetas[i].addActionListener(manejadorTextFields);
        }

        for(JSlider s: sliders){
            s.addChangeListener(manejadorSlider);
            s.setMinorTickSpacing(1);
            s.setSnapToTicks(true);
        }

        buttonPredeterminado = new JButton("Predeterminado");
        buttonPredeterminado.addActionListener(this);
        buttonCargar = new JButton("Cargar Valores");
        buttonCargar.addActionListener(this);
        buttonConvertir = new JButton("Convertir");
        buttonConvertir.addActionListener(this);
        buttonGuardar = new JButton("Guardar Valores");
        buttonGuardar.addActionListener(this);
        buttonInvertir = new JButton("Invertir");
        buttonMas = new JButton("+");
        buttonMas.addActionListener(this);
        buttonMenos = new JButton("-");
        buttonMenos.addActionListener(this);
        txtFieldNumero = new JTextField("0");
        iniciarGrafica();
        configurarPanelBotones();
    }//end of crearComponentes

    private void ponerComponentes() {//
      GridBagLayout bag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      this.setLayout(bag);

      c.insets = new Insets(10,5,10,5);
      c.weightx = 1.0;
      c.weighty = 0.4;
      c.fill = VERTICAL;
      int i = 0;
      for(JSlider s : sliders){
          filaColumna(0,i++,c);
          this.add(s,c);
      }//end for
      i=0;
      c.weighty =0.0;
      c.fill = HORIZONTAL;
      for(JTextField t: etiquetas){
          filaColumna(1,i++,c);
          this.add(t,c);
      }
      c.weighty =1.0;
      c.fill = BOTH;
      c.gridwidth = REMAINDER;
      filaColumna(2,0,c);
      //chartPanel.setPreferredSize(new Dimension(400,400));
      this.add(chartPanel,c);
      c.weighty = 0.0;
      filaColumna(3,0,c);
      this.add(panelBotones,c);

      //poner el botton convertir y el JTextField

      JPanel panelConvertir = new JPanel(new GridLayout(1,2,5,5));
      panelConvertir.add(buttonConvertir);
      panelConvertir.add(txtFieldNumero);

      c.fill = NONE;
      c.weightx = 0.4;
      filaColumna(4,0,c);
      this.add(panelConvertir,c);


    }//

    private void iniciarGrafica() {
        y0 = sliderPt0.getValue();y1 = sliderPt1.getValue();
        y2 = sliderPt2.getValue();y3 = sliderPt3.getValue();
        y4 = sliderPt4.getValue();y5 = sliderPt5.getValue();
        y6 = sliderPt6.getValue();y7 = sliderPt7.getValue();
        y8 = sliderPt8.getValue();y9 = sliderPt9.getValue();
        y10 = sliderPt10.getValue();y11 = sliderPt11.getValue();
        y12 = sliderPt12.getValue();y13 = sliderPt13.getValue();
        y14 = sliderPt14.getValue();y15 = sliderPt15.getValue();
        y16 = sliderPt16.getValue();y17 = sliderPt17.getValue();
        y18 = sliderPt18.getValue();y19 = sliderPt19.getValue();
        y20 = sliderPt20.getValue();

        dataset = new XYSeriesCollection();
        serie = new XYSeries("T°");
        serie.add(0, y0);serie.add(300,y1);
        serie.add(600,y2);serie.add(900,y3);
        serie.add(1200,y4);serie.add(1500,y5);
        serie.add(1800,y6);serie.add(2100,y7);
        serie.add(2400,y8);serie.add(2700,y9);
        serie.add(3000,y10);serie.add(3300,y11);
        serie.add(3600, y12);serie.add(3900, y13);
        serie.add(4200, y14);serie.add(4500, y15);
        serie.add(4800, y16);serie.add(5100, y17);
        serie.add(5400, y18);serie.add(5700, y19);
        serie.add(6000,y20);
        dataset.addSeries(serie);

        chart = ChartFactory.createXYLineChart(
            "T° vs mV", // chart title
            "mV", // x axis label
            "T°", // y axis label
            dataset, // data
            PlotOrientation.VERTICAL,
            true, // include legend
            true, // tooltips
            false // urls
         );

        chartPanel = new ChartPanel(chart);
        chart.setBackgroundPaint(Color.white);
            // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);



        XYLineAndShapeRenderer renderer
         = (XYLineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesShapesFilled(0, true);
            renderer.setSeriesShapesVisible(0,true);
            renderer.setSeriesItemLabelsVisible(0,true);
        }//end of iniciarGrafica


    private void filaColumna(int fila, int columna, GridBagConstraints c){
        c.gridy = fila;
        c.gridx = columna;

    }


    public static void main(String args[]){
        JFrame app = new JFrame("Cargar Temperatura");
        GUITemperatura panel = new GUITemperatura();
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.add(panel);
        app.setVisible(true);
    }//end of main method

    private void configurarPanelBotones() {
        GridLayout grid = new GridLayout(1,4,5,5);
        panelBotones = new JPanel(grid);
        panelBotones.add(buttonCargar);
        panelBotones.add(buttonGuardar);
        panelBotones.add(buttonInvertir);
        panelBotones.add(buttonPredeterminado);
        panelBotones.add(buttonMas);
        panelBotones.add(buttonMenos);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == buttonPredeterminado){
            y0 = 137;y1 = 98 ;
            y2 = 70;y3 = 51;
            y4 = 38;y5 = 28;
            y6 = 22;y7 = 18;
            y8 = 14;y9 = 12;
            y10 = 11;y11 = 10;
            y12 = 9;y13 = 9;
            y14 = 8;y15 = 8;
            y16 = 8;y17 = 8;
            y18 = 8;y19 = 8;
            y20 = 8;


            x = new int[]{0,300,600,900,1200,1500,1800,2100,2400,2700,3000,
                            3300,3600,3900,4200,4500,4800,5100,5400,5700,6000};
            dataset.removeAllSeries();
            serie.clear();
            serie.add(x[0], y0);serie.add(x[1],y1);
            serie.add(x[2],y2);serie.add(x[3],y3);
            serie.add(x[4],y4);serie.add(x[5],y5);
            serie.add(x[6],y6);serie.add(x[7],y7);
            serie.add(x[8],y8);serie.add(x[9],y9);
            serie.add(x[10],y10);serie.add(x[11],y11);
            serie.add(x[12], y12);serie.add(x[13], y13);
            serie.add(x[14], y14);serie.add(x[15], y15);
            serie.add(x[16], y16);serie.add(x[17], y17);
            serie.add(x[18], y18);serie.add(x[19], y19);
            serie.add(x[20],y20);
            dataset.addSeries(serie);

            double arreglo[] = {y0,y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12,y13,y14,y15,y16,y17,y18,y19,y20};
            int i = 0;
            for(JSlider s: sliders){
                s.setValue((int) arreglo[i++]);
            }
        } else if(e.getSource() == buttonMas){
            for(JSlider s: sliders){
                s.setValue(s.getValue() + 1);
            }//edn for
        }else if(e.getSource() ==  buttonMenos){
            for(JSlider s:sliders)
            s.setValue(s.getValue() -1);
        }else if(e.getSource() == buttonConvertir){
            try{
            double ye = Double.parseDouble(txtFieldNumero.getText());
            txtFieldNumero.setText( df.format(obtenerTempTabla(ye)));
            }catch(NumberFormatException ne){
                JOptionPane.showMessageDialog(null,"Ingrese numero valido");
            }
        }else if(e.getSource() == buttonGuardar){
            fc.setApproveButtonText("Guardar");
            int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();

                    //This is where a real application would open the file.
                    output = new PrintWriter(file);
                    for (int i = 0; i < 21; i++) {
                        System.out.printf("%.0f\t%.0f\n", (double)x[i],(double) sliders[i].getValue());
                        output.printf("%.0f\t%.0f\n", (double)x[i],(double) sliders[i].getValue());
                    }
                    System.out.println("Opening: " + file.getName() + ".");
                    output.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("No es posible escribir al archivo");
                }
                } else {
                    System.out.println("Open command cancelled by user.");
                }
        }else if(e.getSource() == buttonCargar){
            fc.setApproveButtonText("Abrir");
            int returnVal = fc.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fc.getSelectedFile();

                    //This is where a real application would open the file.
                    input = new Scanner(file);
                    for(int i = 0; i < 21; i++){
                       x[i] = (int) input.nextDouble();
                       System.out.println("x: "+ i + x[i]);
                       sliders[i].setValue((int)input.nextDouble());

                    }
                    actualizarGrafica();
                    System.out.println("Opening: " + file.getName() + ".");
                    input.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("No es posible Abrir el archivo");
                } catch(NumberFormatException ne){
                    System.err.println("Error leyendo desde el archivo");
                }
                } else {
                    System.out.println("Open command cancelled by user.");
                }

        }//end else
    }//end of actionPerformed Method

     private void actualizarGrafica() {
            y0 = sliderPt0.getValue();
            y1 = sliderPt1.getValue();
            y2 = sliderPt2.getValue();
            y3 = sliderPt3.getValue();
            y4 = sliderPt4.getValue();
            y5 = sliderPt5.getValue();
            y6 = sliderPt6.getValue();
            y7 = sliderPt7.getValue();
            y8 = sliderPt8.getValue();
            y9 = sliderPt9.getValue();
            y10 = sliderPt10.getValue();
            y11 = sliderPt11.getValue();
            y12 = sliderPt12.getValue();
            y13 = sliderPt13.getValue();
            y14 = sliderPt14.getValue();
            y15 = sliderPt15.getValue();
            y16 = sliderPt16.getValue();
            y17 = sliderPt17.getValue();
            y18 = sliderPt18.getValue();
            y19 = sliderPt19.getValue();
            y20 = sliderPt20.getValue();
            for (int i = 0; i <= 20; i++) {
                etiquetas[i].setText(String.valueOf(sliders[i].getValue()));
            }
            serie.clear();
            serie.add(x[0], y0);
            serie.add(x[1], y1);
            serie.add(x[2], y2);
            serie.add(x[3], y3);
            serie.add(x[4], y4);
            serie.add(x[5], y5);
            serie.add(x[6], y6);
            serie.add(x[7], y7);
            serie.add(x[8], y8);
            serie.add(x[9], y9);
            serie.add(x[10], y10);
            serie.add(x[11], y11);
            serie.add(x[12], y12);
            serie.add(x[13], y13);
            serie.add(x[14], y14);
            serie.add(x[15], y15);
            serie.add(x[16], y16);
            serie.add(x[17], y17);
            serie.add(x[18], y18);
            serie.add(x[19], y19);
            serie.add(x[20], y20);
            dataset.removeAllSeries();
            dataset.addSeries(serie); //se repintaria
        }//end of method ActualizarGrafica

      private double obtenerTempTabla(double temp) {

         int[] y = new int[21];
         int j = 0;
         for(JSlider s: sliders){
             y[j++] = s.getValue();
         }//end for

         double x1 = 0; double x2 = 0; double Y1 = 0; double Y2 = 0;
         double temperatura = 0;
         for (int i = 0; i < 20; i++){
            if(estaRango(temp,x[i],x[i+1])){
                x1 = x[i]; x2 =  x[i+1];
                System.out.println("x1: " + x1);
                System.out.println("x2: " + x2);
                Y1 = y[i]; Y2 = y[i+1];
                double pendiente = (Y2 - Y1)/(x2 -x1);
                double intersecto = Y1 - pendiente*x1;
                temperatura = pendiente*temp + intersecto ;
                return temperatura;
          }//end if
       }//end for
     return 0;
   }

    private boolean estaRango(double t,double menor, double mayor){
        if (t >= menor && t < mayor )
         return true;
       else
         return false;
    }


    class ManejadorSlider implements ChangeListener{
        public void stateChanged(ChangeEvent e) {
            actualizarGrafica();
        }
    }

    class ManejadorTextFields implements ActionListener{

        public void actionPerformed(ActionEvent e) {
            try{
             for(int i = 0; i <=20;i++  ){
                sliders[i].setValue( Integer.parseInt(etiquetas[i].getText()) );
             }
            }
         catch(NumberFormatException ne){
            JOptionPane.showMessageDialog(null,"introduzca numeros validos");
        }
        actualizarGrafica();
      }
    }//edn of class  Manejador
}//edn of class GUITemperatura
