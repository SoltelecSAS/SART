/*
 * 
 * 
 */
package org.soltelec.procesosbanco;

import gnu.io.SerialPort;
import java.util.List;
import org.soltelec.util.EstadoMaquina;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.SwingWorker;
import java.lang.Integer;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import org.soltelec.pruebasgases.PanelPruebaGases;
import org.soltelec.util.BloqueoEquipoUtilitario;
import org.soltelec.util.MedicionGases;
import org.soltelec.util.UtilPropiedades;
import org.soltelec.util.capelec.BancoCapelec;

/**
 * Hilo que monitorea el proceso de calentamiento del banco
 *
 * @author Usuario
 */
public class EstabilizacionMedidas extends SwingWorker<Integer, Integer> {

    private BancoGasolina banco;
    public int contador, vehiculo;
    private final PanelPruebaGases panel;
    private MedicionGases medicion;
    String Estabilizacion = "";
    double co = 0, hc = 0, co2 = 0, o2 = 0;
    double tempco, temphc, tempco2, tempo2;
    public boolean Resultado = true;

    public EstabilizacionMedidas(PanelPruebaGases panel, BancoGasolina banco, int vehiculo) {
        //this.texto = texto;
        this.panel = panel;
        this.banco = banco;
        this.medicion = medicion;
        this.contador = 300;
        this.Resultado = true;
        this.vehiculo = vehiculo;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        // Estabilizacion = UtilPropiedades.cargarPropiedad("Estabilizacion", "propiedades.properties");//LEE EL VALOR DE LA VARIABLE TemporizadorOpacimetro
        //   Estabilizacion=(Estabilizacion==null)?"false":Estabilizacion;
        //   System.out.println((Estabilizacion== "" || Estabilizacion.equalsIgnoreCase("false"))? "variable estabilizacion configurada en false":"variable estabilizacion configurada en true");
        MedicionGases medicion = new MedicionGases();
        medicion = banco.obtenerDatos();
        int prueba = 0;
        do {
            contador--;
            //  System.out.println("valor de contador: " + contador);
            //medicion = new MedicionGases();
            medicion = banco.obtenerDatos();
            Thread.sleep(1000);
            if (banco instanceof BancoCapelec) {
                System.out.println("el banco es capelec");
                this.panel.getPanelMensaje().setText("\nComprobando estabilidad \n\n[HC:" + medicion.getValorHC() + ", CO:" + (medicion.getValorCO() * 0.01) + ", CO2:" + ((medicion.getValorCO2() * 0.1) / 10) + "]\n[" + contador + "s]");
                System.out.println("[HC:" + medicion.getValorHC() + ", CO:" + medicion.getValorCO() + ", CO2:" + ((medicion.getValorCO2() * 0.1) / 10) + "]\n[" + contador + "]" + "valores co" + co + ", hc:" + hc + ", co2:" + co2);

            } else {
                System.out.println("el banco es sensor");
                this.panel.getPanelMensaje().setText("\nComprobando estabilidad \n\n[HC:" + (medicion.getValorHC() - 1) + ", CO:" + (medicion.getValorCO() * 0.01) + ", CO2:" + (medicion.getValorCO2() * 0.1) + "]\n[" + contador + "s]");
                //System.out.println("[HC:"+medicion.getValorHC()+", CO:" + medicion.getValorCO() +", CO2:"+(medicion.getValorCO2() * 0.1) +"]\n[" + contador + "]");
                System.out.println("[HC: " + medicion.getValorHC() + ", CO: " + medicion.getValorCO() + ", CO2: " + medicion.getValorCO2() + "]\n[" + contador + "]" + "valores co: " + co + ", hc: " + hc + ", co2: " + co2);
                prueba = medicion.getValorHC();
                // System.out.println("valor de prueba : " + prueba);
            }
            //5.2.3.3.1
            hc = (prueba <= 1) ? hc + prueba : hc + (prueba - 1);
            //hc= hc + (prueba-1);
            co = co + (medicion.getValorCO() * 0.01);
            co2 = co2 + (medicion.getValorCO2() * 0.01);

        } while (contador > 0);
        System.out.println("valor de ANTES DE PROMEDIAR Hc: " + hc);
        System.out.println("valor de ANTES DE PROMEDIAR  CO: " + co);
        System.out.println("valor de  ANTES DE PROMEDIAR CO2: " + co2);
        co = co / 300;
        co2 = co2 / 300;
        hc = hc / 300;
        //o2 = o2 / 300;
        System.out.println("valores obtenidos despues de promediar, hc: " + hc + ", co2: " + co2 + ", co: " + co + ", o2: " + o2);

        if (vehiculo == 1) {
            if (co > 0.03 || co2 > 0.18 || hc > 9) {
                JOptionPane.showMessageDialog(null, "Se ejecuta bloqueo del equipo de acuerdo a numeral 5.2.3.3 Ntc5365..", "ERROR DE ESTABILIZACION, PRUEBA BLOQUEADA", JOptionPane.WARNING_MESSAGE);
                BloqueoEquipoUtilitario.bloquearEquipo(
                    "Estabilizacion medidas opcion 1   \n"+
                    "Condiciones: co > 0.03 || co2 > 0.18 || hc > 9   \n"+
                    "Valores: co ="+co+" co2 ="+co2+" hc ="+hc);
                Resultado = false;
            } else {
                this.panel.getPanelMensaje().setText("Finalice exitosamente la medida");
                BloqueoEquipoUtilitario.eliminarArchivo();
                Resultado = true;
            }
        } else if (vehiculo == 4) {
            if (co > 0.05 || co2 > 0.3 || hc > 7.5) {
                JOptionPane.showMessageDialog(null, "Prueba cancelada, No se pudo realizar estabilizacion de las medidas \n como se indica en  la norma 5365 :2012 numeral 5.2.3.3.1,\n por favor realice calibracion de los equipos o contacte a soporte tecnico.", "ERROR DE ESTABILIZACION, PRUEBA BLOQUEADA", JOptionPane.WARNING_MESSAGE);
                BloqueoEquipoUtilitario.bloquearEquipo(
                    "Estabilizacion medidas opcion 2   \n"+
                    "Condiciones: co > 0.05 || co2 > 0.3 || hc > 7.5   \n"+
                    "Valores: co ="+co+" co2 ="+co2+" hc ="+hc);
                Resultado = false;
            } else {
                this.panel.getPanelMensaje().setText("Finalice exitosamente la medida");
                BloqueoEquipoUtilitario.eliminarArchivo();
                Resultado = true;
            }
        }

        Thread.sleep(3500);
        return 0;
    }//end of method

    @Override
    public void done() {

    }

    protected void publish(MedicionGases medicion, int contador) {

    }
}
