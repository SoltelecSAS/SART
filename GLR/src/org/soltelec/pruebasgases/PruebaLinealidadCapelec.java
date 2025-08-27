/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.pruebasgases;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.soltelec.procesosbanco.RegVefCalibraciones;
import org.soltelec.util.MedicionOpacidad;
import org.soltelec.util.Mensajes;
import org.soltelec.util.capelec.OpacimetroCapelec;

/**
 *
 * @author GerenciaDesarrollo
 */
public class PruebaLinealidadCapelec implements Runnable {

    private PanelPruebaGases panel;
    private final OpacimetroCapelec opacimetro;
    private final Timer timer;
    private int contadorTemporizacion;
    private final DialogoFiltros dialogo;
    double valorFiltro1, valorFiltro2, valorFiltro3,valorFiltro4;
    private MedicionOpacidad med;
    private ArrayList<Double> listaMed;
    private boolean filtro1Coincide, filtro2Coincide, filtro3Coincide,filtro4Coincide;//falso por defecto

    public PruebaLinealidadCapelec(OpacimetroCapelec opacimetro) {
        this.opacimetro = opacimetro;
        dialogo = new DialogoFiltros();
        listaMed = new ArrayList<>();
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                contadorTemporizacion++;
            }
        });
    }

    @Override
    public void run() {

        timer.start();
        panel.getPanelMensaje().setVisible(true);
        panel.getPanelFiguras().setVisible(false);
        panel.getProgressBar().setVisible(false);
        panel.getButtonFinalizar().setVisible(false);
        panel.getButtonRpm().setVisible(false);
        dialogo.setVisible(true);
        valorFiltro1 = dialogo.getValorFiltro1();
        valorFiltro2 = dialogo.getValorFiltro2();
        valorFiltro3 = dialogo.getValorFiltro3();
        valorFiltro4 = dialogo.getValorFiltro4();

        int opcion;
        panel.getPanelMensaje().setText("Calibracion de Cero\n Asegurese que no existan residuos\n ni particulas");
        dormir(1000);
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Calibrar", "Calibracion", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        opacimetro.calibrar();
        dormir(1200);

        panel.getPanelMensaje().setVisible(true);
        panel.getPanelMensaje().setText("Introduzca el Filtro # 1");
        dormir(1000);

        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Establecer la intensidad de la fuente  a " + valorFiltro1, "Intensidad 1", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);
        //opacimetro.intensidadFuenteLuz((int) valorFiltro1, false);
        //dormir(1000);

        panel.getPanelMensaje().setText("Filtro1 : Tomando Medidas");

        double promedioParaFiltro1 = obtenerPromedioMedidas();

        if ((promedioParaFiltro1 <  2) ) {
            filtro1Coincide = true;
            panel.getPanelMensaje().setText("Verificacion del Filtro1 OK");
            System.out.println("Filtro1 OK");
            dormir(1000);
        } else {//TODO registrar
            filtro1Coincide = false;
            System.out.println("Medidas no coinciden con el Valor del Filtro1 ");
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor \n del Filtro1");
            dormir(1200);

        }

        panel.getPanelMensaje().setText("Valor de Intensidad # 2");
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Establecer el valor de Intensidad a " + valorFiltro2, "Intensidad 2", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        panel.getPanelMensaje().setText("Intensidad 2 : Tomando Medidas ... ");
        dormir(1000);
        //opacimetro.intensidadFuenteLuz((int) valorFiltro2, false);
       

        double promedioParaFiltro2 = obtenerPromedioMedidas();
        
        if (promedioParaFiltro2  <= (valorFiltro2+2.0) &&  promedioParaFiltro2 >= (valorFiltro2-2.0)) {
            filtro2Coincide = true;
            System.out.println("Filtro2 OK");
            panel.getPanelMensaje().setText("Verificacion del Filtro2 OK");
            dormir(1200);
        } else {
            filtro2Coincide = false;
            System.out.println("Medidas no coinciden con el Valor del Filtro2");
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 2 ");
            dormir(1200);
        }
        panel.getPanelMensaje().setText("Intensidad 3");
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Establecer el valor de Intensidad # 3 a " + valorFiltro3, "Intensidad 3", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);
       dormir(1000);
        //opacimetro.intensidadFuenteLuz((int) valorFiltro3, false);
       // dormir(1000);
        double promedioParaFiltro3 = obtenerPromedioMedidas();
        if (promedioParaFiltro3  <= (valorFiltro3+2.0) &&  promedioParaFiltro3 >= (valorFiltro3-2.0)) {
            filtro3Coincide = true;
            panel.getPanelMensaje().setText("Verificacion del Filtro3 OK");
            System.out.println("Filtro3 OK");
            dormir(1200);
        } else {
            filtro3Coincide = false;
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 3");
            System.out.println("Medidas no coinciden con el valor Del Filtro3");
            dormir(1200);
        }
        
         panel.getPanelMensaje().setText("Valor de Intensidad # 4");
        do {
            opcion = JOptionPane.showConfirmDialog(panel, "Establecer el valor de Intensidad a " + valorFiltro4, "Intensidad 4", JOptionPane.YES_NO_OPTION);
        } while (opcion != JOptionPane.YES_OPTION);

        panel.getPanelMensaje().setText("Intensidad 4 : Tomando Medidas ... ");
        dormir(1000);
        //opacimetro.intensidadFuenteLuz((int) valorFiltro2, false);
       

        double promedioParaFiltro4 = obtenerPromedioMedidas();
        
        if (promedioParaFiltro4  <= (valorFiltro4+2.0) &&  promedioParaFiltro4 >= (valorFiltro4-2.0)) {
            filtro4Coincide = true;
            System.out.println("Filtro4 OK");
            panel.getPanelMensaje().setText("Verificacion del Filtro4 OK");
            dormir(1200);
        } else {
            filtro4Coincide = false;
            System.out.println("Medidas no coinciden con el Valor del Filtro4");
            panel.getPanelMensaje().setText("Medidas no coinciden con el Valor\n del Filtro 4 ");
            dormir(1200);
        }        
        timer.stop();
       long serial = opacimetro.obtenerSerial();
        RegVefCalibraciones rc = new RegVefCalibraciones();
        if (filtro2Coincide && filtro3Coincide && filtro4Coincide) {
            panel.getPanelMensaje().setText("Prueba Realizada con Exito");
            try {
                rc.registrarPruebaLinelidad(String.valueOf(opacimetro.obtenerSerial()), true,valorFiltro1, valorFiltro2, valorFiltro3, valorFiltro4,promedioParaFiltro1, promedioParaFiltro2, promedioParaFiltro3, promedioParaFiltro4,1);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Mensajes.mostrarExcepcion(ex);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            if (!filtro1Coincide) {
                sb.append("El valor del Filtro1 no Coincide");
            }
            if (!filtro2Coincide) {
                sb.append("\nEl valor del Filtro2 no Coincide");
            }
            if (!filtro3Coincide) {
                sb.append("\nEl valor del Filtro3 no Coincide");
            }
            if (!filtro4Coincide) {
                sb.append("\nEl valor del Filtro4 no Coincide");
            }

            panel.getPanelMensaje().setText("Prueba Finalizada sin Exito\n");
            panel.getPanelMensaje().appendText(sb.toString());
            System.out.println("Verificacion 1: " + promedioParaFiltro1);
            System.out.println("Verificacion 2: " + promedioParaFiltro2);
            System.out.println("Verificacion 3: " + promedioParaFiltro3);
            System.out.println("Verificacion 4: " + promedioParaFiltro4);
            try {
                rc.registrarPruebaLinelidad(String.valueOf(opacimetro.obtenerSerial()), false,valorFiltro1, valorFiltro2, valorFiltro3, valorFiltro4,promedioParaFiltro1, promedioParaFiltro2, promedioParaFiltro3, promedioParaFiltro4,1);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Mensajes.mostrarExcepcion(ex);
            }
        }
        dormir(5000);
        panel.cerrar();
    }
    private void dormir(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) { }
    }

    private double obtenerPromedioMedidas() {
        listaMed = new ArrayList<>();
        contadorTemporizacion = 0;

        //med = opacimetro.obtenerDatos();
        while (contadorTemporizacion < 4) {
            dormir(100);
            Integer t= opacimetro.obtenerDatosBruto();
            double opacidad = (opacimetro.obtenerDatosBruto()) * 0.1;
            listaMed.add(opacidad);
        } //end of while
        //timer.stop();
        //end of while
        double promedio = 0;
        double suma = 0;
        for (Double d : listaMed) {
            suma += d;
        } //end for
        promedio = suma / listaMed.size();
        return promedio;
    }//end of method

    public PanelPruebaGases getPanel() {
        return panel;
    }

    public void setPanel(PanelPruebaGases panel) {
        this.panel = panel;
    }

}
