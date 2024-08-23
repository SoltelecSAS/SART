/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.soltelec.procesosbanco;

import org.soltelec.util.MedicionGases;
import java.awt.Color;
import java.awt.GradientPaint;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Gerencia Desarrollo de Soluciones Tecnologicas
 */
public class GenerarGrafica {

    private List<MedicionGases> listaMediciones;
    private XYSeriesCollection xySerie;
    private XYSeries xySerieHC,xySerieCO2,xySerieCO,xySerieO2;

    public  GenerarGrafica( List<MedicionGases> lista){
        this.listaMediciones = lista;
        xySerieHC = new XYSeries("HC");
        xySerieCO2 = new XYSeries("CO");
        xySerieCO = new XYSeries("CO2");
        xySerieO2 = new XYSeries("O2");
        llenarXYSeries();

    }//end of Constructor

    private void llenarXYSeries(){
        int contador = 0;
        int apFill=0;
        for(MedicionGases med: listaMediciones){
           //   apFill = ThreadLocalRandom.current().nextInt(1, 12);            
              
            xySerieHC.add(contador, med.getValorHC());
            xySerieCO.add(contador, med.getValCO()*0.01);
            xySerieCO2.add(contador,med.getValCO2()*0.1);
            xySerieO2.add(contador,med.getValorO2() * 0.01);
            contador++;
        }//end for

    }//end of method llenarXYseries

    public JPanel getGraficaTodas(){
        XYSeriesCollection xySeriesHC = new XYSeriesCollection();
        xySeriesHC.addSeries(xySerieHC);
        XYSeriesCollection xySeriesCO = new XYSeriesCollection();
        xySeriesCO.addSeries(xySerieCO);
        XYSeriesCollection xySeriesCO2 = new XYSeriesCollection();
        xySeriesCO2.addSeries(xySerieCO2);
        XYSeriesCollection xySeriesO2 = new XYSeriesCollection();
        xySeriesO2.addSeries(xySerieO2);

        JFreeChart chart = ChartFactory.createXYLineChart(
        "Medicion de Gases", // chart title
        "Numero Medicion", // domain axis label
        "Datos", // range axis label
        this.xySerie, // data
        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        true, // tooltips
        false // urls
        );
        // make a common vertical axis for all the sub-plots
        NumberAxis valueAxis = new NumberAxis("HC ppm");
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        double rangoHC[] = this.rangoSerie(xySerieHC);
        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
        renderer1.setLinesVisible(false);
        valueAxis.setLowerBound(rangoHC[0]);
        valueAxis.setUpperBound(rangoHC[1]);
        CombinedDomainXYPlot parent = new CombinedDomainXYPlot();
        XYPlot subplot1 = new XYPlot(xySeriesHC, null,valueAxis, renderer1);
        parent.add(subplot1, 1);

        // add subplot 2...
        NumberAxis valueAxis2 = new NumberAxis("CO %");
        valueAxis2.setAutoRangeIncludesZero(false);  // override default
        double[] rangoCO = this.rangoSerie(xySerieCO);
        valueAxis2.setLowerBound(rangoCO[0]);
        valueAxis2.setUpperBound(rangoCO[1]);
        XYLineAndShapeRenderer renderer2 = new XYLineAndShapeRenderer();
        renderer2.setLinesVisible(false);

        XYPlot subplot2 = new XYPlot(xySeriesCO,  null,valueAxis2,renderer2);
        parent.add(subplot2, 1);

        // add subplot 3...
        NumberAxis valueAxis3 = new NumberAxis("Rango CO2 %");
        valueAxis3.setAutoRangeIncludesZero(false);  // override default
        double[] rangoCO2 = this.rangoSerie(xySerieCO2);
        valueAxis3.setLowerBound(rangoCO2[0]);
        valueAxis3.setUpperBound(rangoCO2[1]);
        XYLineAndShapeRenderer renderer3 = new XYLineAndShapeRenderer();
        renderer3.setLinesVisible(false);
        XYPlot subplot3 = new XYPlot(xySeriesCO2,null, valueAxis3, renderer3);
        parent.add(subplot3, 1);

        NumberAxis valueAxis4 = new NumberAxis("Rango O2 %");
        valueAxis4.setAutoRangeIncludesZero(false);
        double[] rangoO2 = this.rangoSerie(xySerieO2);
        valueAxis4.setLowerBound(rangoO2[0]);
        valueAxis4.setUpperBound(rangoO2[1]);
        XYLineAndShapeRenderer renderer4 = new XYLineAndShapeRenderer();
        renderer4.setLinesVisible(false);

        XYPlot subplot4 = new XYPlot(xySeriesO2, null,valueAxis4, renderer4);
        parent.add(subplot4, 1);
        chart = new JFreeChart("Graficas", JFreeChart.DEFAULT_TITLE_FONT, parent, true);


        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white, 0, 1000, Color.blue));
        return new ChartPanel(chart);
       

    }

    public double[] rangoSerie(XYSeries xySerie){
        double max = xySerie.getMaxY();
        double min = xySerie.getMinY();

        double longitud = max-min;
        double tolerancia  = longitud/20;

        double rangos[] = new double[2];
        rangos[0] = min - tolerancia;
        rangos[1] = max  +tolerancia;

        return rangos;

    }

    public JPanel getGraficaHC(){
        xySerie = new XYSeriesCollection(this.xySerieHC);
        JFreeChart chart = ChartFactory.createXYLineChart(
        "Medicion de Gases", // chart title
        "Numero Medicion", // domain axis label
    "Datos", // range axis label
        this.xySerie, // data
        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        true, // tooltips
        false // urls
        );
       return new ChartPanel(chart);

    }//end of method getGraficaHC

    public JPanel getGraficaCO2(){
        xySerie = new XYSeriesCollection(this.xySerieCO2);
        JFreeChart chart = ChartFactory.createXYLineChart(
        "Medicion de Gases", // chart title
        "Numero Medicion", // domain axis label
        "Datos", // range axis label
        this.xySerie, // data
        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        true, // tooltips
        false // urls
        );
       return new ChartPanel(chart);
    }

    public JPanel getGraficaCO(){
        xySerie = new XYSeriesCollection(this.xySerieCO);
        JFreeChart chart = ChartFactory.createXYLineChart(
        "Medicion de Gases", // chart title
        "Numero Medicion", // domain axis label
        "Datos", // range axis label
        this.xySerie, // data
        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        true, // tooltips
        false // urls
        );
       return new ChartPanel(chart);
    }

    public JPanel getGraficaO2(){
        xySerie = new XYSeriesCollection(this.xySerieCO2);
        JFreeChart chart = ChartFactory.createXYLineChart(
        "Medicion de Gases", // chart title
        "Numero Medicion", // domain axis label
        "Datos", // range axis label
        this.xySerie, // data
        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        true, // tooltips
        false // urls
    );
        ChartPanel chartPanel = new ChartPanel(chart, 250, 250, 200, 200, 250,250,true, true, false, false, false,false);
        
       return chartPanel;
    }
}//end of class GenerarGrafica
