/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.procesosbanco;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.BloqueoEquipoUtilitario;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.capelec.BancoCapelec;

/**
 *
 * @author SOLTELEC
 */
public class EstabilizacionMedidasServicio extends SwingWorker<Integer, Integer>{
    private BancoGasolina banco;
    public int contador, vehiculo;
    private JLabelPersonalizada label;
    private MedicionGases medicion;
    String Estabilizacion = "";
    double co = 0, hc = 0, co2 = 0, o2 = 0;
    double tempco, temphc, tempco2, tempo2;
    public boolean Resultado = true;
    private JProgressBar progressBar;
    private int contadorInicio;
    
    public EstabilizacionMedidasServicio(JLabelPersonalizada label, JProgressBar progressBar, BancoGasolina banco, int vehiculo, int contadorInicio) {
        //this.texto = texto;
        this. label = label;
        this.banco = banco;
        this.medicion = medicion;
        this.contador = 300;
        this.Resultado = true;
        this.vehiculo = vehiculo;
        this.progressBar = progressBar;
        this.contadorInicio  = contadorInicio;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        MedicionGases medicion = new MedicionGases();
        medicion = banco.obtenerDatos();
        progressBar.setValue(0);
        int progreso =0;
        int prueba = 0;
        do {
            contador = contador - 1;
            //  System.out.println("valor de contador: " + contador);
            //medicion = new MedicionGases();
            medicion = banco.obtenerDatos();
            Thread.sleep(1000);
            if (banco instanceof BancoCapelec) {
                System.out.println("el banco es capelec");
                this.label.setText("\nComprobando estabilidad \n\n[HC:" + medicion.getValorHC() + ", CO:" + (medicion.getValorCO() * 0.01) + ", CO2:" + ((medicion.getValorCO2() * 0.1) / 10) + "]\n[" + contador + "s]");
                System.out.println("[HC:" + medicion.getValorHC() + ", CO:" + medicion.getValorCO() + ", CO2:" + ((medicion.getValorCO2() * 0.1) / 10) + "]\n[" + contador + "]" + "valores co" + co + ", hc:" + hc + ", co2:" + co2);

            } else {
                System.out.println("el banco es sensor");
                this.label.setText("\nComprobando estabilidad \n\n[HC:" + Math.abs(medicion.getValorHC() - 1) + ", CO:" + (medicion.getValorCO() * 0.01) + ", CO2:" + (medicion.getValorCO2() * 0.1) + "]\n[" + contador + "s]");
                //System.out.println("[HC:"+medicion.getValorHC()+", CO:" + medicion.getValorCO() +", CO2:"+(medicion.getValorCO2() * 0.1) +"]\n[" + contador + "]");
                System.out.println("[HC: " + medicion.getValorHC() + ", CO: " + medicion.getValorCO() + ", CO2: " + medicion.getValorCO2() + "]\n[" + contador + "]" + "valores co: " + co + ", hc: " + hc + ", co2: " + co2);
                prueba = medicion.getValorHC();
                progreso = (100 - (100 * contador) / 300);
                System.out.println("contador de inicio: " + contadorInicio);
                System.out.println("escribiendo progrreso :" + progreso);
                // System.out.println("valor de prueba : " + prueba);
            }
            //5.2.3.3.1
            hc = (prueba <= 1) ? hc + prueba : hc + (prueba - 1);
            //hc= hc + (prueba-1);
            co = co + (medicion.getValorCO() * 0.01);
            co2 = co2 + (medicion.getValorCO2() * 0.01);
            //setProgress(progreso);
            progressBar.setValue(progreso);
            
            System.out.println("progreso:" + progreso);
        } while (contador > 0);
        System.out.println("valor de ANTES DE PROMEDIAR Hc: " + hc);
        System.out.println("valor de ANTES DE PROMEDIAR  CO: " + co);
        System.out.println("valor de  ANTES DE PROMEDIAR CO2: " + co2);
        co = co / 300;
        co2 = co2 / 300;
        hc = hc / 300;
        o2 = o2 / 300;
        System.out.println("valores obtenidos despues de promediar, hc: " + hc + ", co2: " + co2 + ", co: " + co + ", o2: " + o2);

        if (vehiculo == 1) {
            if (co > 0.03 || co2 > 0.18 || hc > 9) {
                JOptionPane.showMessageDialog(null, "Se ejecuta bloqueo del equipo de acuerdo a numeral 5.2.3.3 Ntc5365.", "ERROR DE ESTABILIZACION, PRUEBA BLOQUEADA", JOptionPane.WARNING_MESSAGE);
                BloqueoEquipoUtilitario.bloquearEquipo(
                    "Estabilizacion medidas Servicio opcion 1   \n"+
                    "Condiciones: co > 0.03 || co2 > 0.18 || hc > 9   \n"+
                    "Valores: co ="+co+" co2 ="+co2+" hc ="+hc);
                
                Resultado = false;
            } else {
                this.label.setText("Finalice exitosamente la medida");
                BloqueoEquipoUtilitario.eliminarArchivo();
                Resultado = true;
            }
        } else if (vehiculo == 4) {
            if (co > 0.05 || co2 > 0.3 || hc > 7.5) {
                JOptionPane.showMessageDialog(null, "Prueba cancelada, No se pudo realizar estabilizacion de las medidas \n como se indica en  la norma 5365 :2012 numeral 5.2.3.3.1,\n por favor realice calibracion de los equipos o contacte a soporte tecnico.", "ERROR DE ESTABILIZACION, PRUEBA BLOQUEADA", JOptionPane.WARNING_MESSAGE);
                BloqueoEquipoUtilitario.bloquearEquipo(
                    "Estabilizacion medidas Servicio opcion 2   \n"+
                    "Condiciones: co > 0.05 || co2 > 0.3 || hc > 7.5   \n"+
                    "Valores: co ="+co+" co2 ="+co2+" hc ="+hc);
                Resultado = false;
            } else {
                this.label.setText("Finalice exitosamente la medida");
                BloqueoEquipoUtilitario.eliminarArchivo();
                Resultado = true;
            }
        }

        Thread.sleep(3500);
        return 0;
    }
    
    protected void publish(Integer numero) {
        progressBar.setValue(numero);
    }
}