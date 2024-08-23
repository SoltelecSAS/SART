/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.soltelec.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Clase para agrupar la medición de opacidad del LCS
 *
 * @author Usuario
 */
public class MedicionOpacidad {

    private short opacidad;
    private int temperaturaGas;
    private int temperaturaTubo;
    // private boolean[] estado;

    private boolean tempAmbFueraTolerancia;
    private boolean tempDetectFueraTolerancia;
    private boolean tempTuboFueraTolerancia;
    private boolean voltLineaFueraTolerancia;
    private boolean opacidadFueraRango;
    private boolean realTimeDataNotReady;
    private boolean calibracionProgreso;
    private boolean limpiarVentanas;
    private boolean armadoRecoleccion;
    private boolean recoleccionDisparada;
    private boolean desequilibrioVentiladores;
    private boolean umbralTemperatura;
    private boolean termistorDañado;
    private boolean EEPROM;
    private boolean resetProcesador;
    private boolean standBy;
    private boolean ventiladorOn;
    private boolean temperaturaGasBaja;
    private boolean triggerActivado;//capelec
    private boolean zeroEnProgreso;//capelec zero running
    private double densidadHumo;
    private int tiempoRecDatos;

    short shortEstado;
    DecimalFormat df2;
    StringBuilder sb;

    public MedicionOpacidad() {
        Locale locale = new Locale("US");//para que sea con punto separador de decimales
        df2 = (DecimalFormat) NumberFormat.getInstance(locale);
        if (df2 instanceof DecimalFormat) {
            ((DecimalFormat) df2).setDecimalSeparatorAlwaysShown(true);
        }
        df2.setMaximumFractionDigits(2);
    }

    public MedicionOpacidad(byte[] datos) {
        this();
        try {
            byte msbOpacidad = datos[0];
            byte lsbOpacidad = datos[1];
            byte[] arreglo = {lsbOpacidad, msbOpacidad};
            opacidad = UtilGasesModelo.fromBytesToShort(arreglo);
            temperaturaGas = datos[2];
            temperaturaTubo = datos[3];
            byte[] estadoBytes = {datos[5], datos[4]};
            shortEstado = UtilGasesModelo.fromBytesToShort(estadoBytes);
        }catch(Exception e){
            System.out.println("----Estoy en MedicionOpacidad-------" + " " + e.getMessage());
            e.printStackTrace();
        }
//        estado = UtilGasesModelo.shortToFlags(shortEstado);
//        
//        tempAmbFueraTolerancia = estado[0];
//        tempDetectFueraTolerancia = estado[1];
//        tempTuboFueraTolerancia = estado[2];
//        voltLineaFueraTolerancia = estado[3];
//        calibracionProgreso = estado[8];
//        limpiarVentanas = estado[9];
//        armadoRecoleccion = estado[10];
//        recoleccionDisparada = estado[11];

    }//end of constructor

    public int getTiempoRecogidaDatos() {
        return tiempoRecDatos;
    }

    public void setTiempoRecogidaDatos(int tiempoRecDatos) {
        this.tiempoRecDatos = tiempoRecDatos;
    }

    public boolean isArmadoRecoleccion() {
        return armadoRecoleccion;
    }

    public void setArmadoRecoleccion(boolean armadoRecoleccion) {
        this.armadoRecoleccion = armadoRecoleccion;
    }

    public boolean isCalibracionProgreso() {
        return calibracionProgreso;
    }

    public void setCalibracionProgreso(boolean calibracionProgreso) {
        this.calibracionProgreso = calibracionProgreso;
    }

    public boolean isLimpiarVentanas() {
        return limpiarVentanas;
    }

    public void setLimpiarVentanas(boolean limpiarVentanas) {
        this.limpiarVentanas = limpiarVentanas;
    }

    public int getOpacidad() {
        return opacidad;
    }

    public double getOpacidadDouble() {
        return opacidad * 0.1;
    }

    public void setOpacidad(short opacidad) {
        this.opacidad = opacidad;
    }

    public boolean isRecoleccionDisparada() {
        return recoleccionDisparada;

    }

    public void setRecoleccionDisparada(boolean recoleccionDisparada) {
        this.recoleccionDisparada = recoleccionDisparada;
    }

    public boolean isTempAmbFueraTolerancia() {
        return tempTuboFueraTolerancia;
    }

    public void setTempAmbFueraTolerancia(boolean tempAmbFueraTolerancia) {
        this.tempAmbFueraTolerancia = tempAmbFueraTolerancia;
    }

    public boolean isTempDetectFueraTolerancia() {
        return tempDetectFueraTolerancia;
    }

    public void setTempDetectFueraTolerancia(boolean tempDetectFueraTolerancia) {
        this.tempDetectFueraTolerancia = tempDetectFueraTolerancia;
    }

    public boolean isTempTuboFueraTolerancia() {
        return tempTuboFueraTolerancia;
    }

    public void setTempTuboFueraTolerancia(boolean tempTuboFueraTolerancia) {
        this.tempTuboFueraTolerancia = tempTuboFueraTolerancia;
    }

    public int getTemperaturaTubo() {
        return temperaturaTubo;
    }

    public void setTemperaturaTubo(int temperaturaTubo) {
        this.temperaturaTubo = temperaturaTubo;
    }

    public boolean isVoltLineaFueraTolerancia() {
        return voltLineaFueraTolerancia;
    }

    public void setVoltLineaFueraTolerancia(boolean voltLineaFueraTolerancia) {
        this.voltLineaFueraTolerancia = voltLineaFueraTolerancia;
    }

    public boolean isDesequilibrioVentiladores() {
        return desequilibrioVentiladores;
    }

    public void setDesequilibrioVentiladores(boolean desequilibrioVentiladores) {
        this.desequilibrioVentiladores = desequilibrioVentiladores;
    }

    public boolean isOpacidadFueraRango() {
        return opacidadFueraRango;
    }

    public void setOpacidadFueraRango(boolean opacidadFueraRango) {
        this.opacidadFueraRango = opacidadFueraRango;
    }

    public boolean isRealTimeDataNotReady() {
        return realTimeDataNotReady;
    }

    public void setRealTimeDataNotReady(boolean realTimeDataNotReady) {
        this.realTimeDataNotReady = realTimeDataNotReady;
    }

    public boolean isTermistorDañado() {
        return termistorDañado;
    }

    public void setTermistorDañado(boolean termistorDañado) {
        this.termistorDañado = termistorDañado;
    }

    public boolean isUmbralTemperatura() {
        return umbralTemperatura;
    }

    public void setUmbralTemperatura(boolean umbralTemperatura) {
        this.umbralTemperatura = umbralTemperatura;
    }

    public int getTemperaturaGas() {
        return temperaturaGas;
    }

    public void setTemperaturaGas(int temperaturaGas) {
        this.temperaturaGas = temperaturaGas;
    }

    public boolean isEEPROM() {
        return EEPROM;
    }

    public void setEEPROM(boolean EEPROM) {
        this.EEPROM = EEPROM;
    }

    public boolean isResetProcesador() {
        return resetProcesador;
    }

    public void setResetProcesador(boolean resetProcesador) {
        this.resetProcesador = resetProcesador;
    }

    public boolean isStandBy() {
        return standBy;
    }

    public void setStandBy(boolean standBy) {
        this.standBy = standBy;
    }

    public boolean isTemperaturaGasBaja() {
        return temperaturaGasBaja;
    }

    public void setTemperaturaGasBaja(boolean temperaturaGasBaja) {
        this.temperaturaGasBaja = temperaturaGasBaja;
    }

    public boolean isVentiladorOn() {
        return ventiladorOn;
    }

    public void setVentiladorOn(boolean ventiladorOn) {
        this.ventiladorOn = ventiladorOn;
    }

    public boolean isTriggerActivado() {
        return triggerActivado;
    }

    public void setTriggerActivado(boolean triggerActivado) {
        this.triggerActivado = triggerActivado;
    }

    public boolean isZeroEnProgreso() {
        return zeroEnProgreso;
    }

    public void setZeroEnProgreso(boolean zeroEnProgreso) {
        this.zeroEnProgreso = zeroEnProgreso;
    }

    public short getShortEstado() {
        return shortEstado;
    }

    public void setShortEstado(short shortEstado) {
        this.shortEstado = shortEstado;
    }

    public double getDensidadHumo() {
        return densidadHumo;
    }

    public void setDensidadHumo(double densidadHumo) {
        this.densidadHumo = densidadHumo;
    }

    public String getStrOpacidad() {
        //poner el valor de CO en 0.01%
        if (this.getOpacidad() <= 0) {
            return "0.00 %";
        } else {
            String formateado = df2.format(this.getOpacidad() * 0.01);
            return formateado + "%";
        }//return cadenaCO;
    }//end of method getStrOpacidad

    public String getStrTemperaturaTubo() {
        if (temperaturaTubo <= 0) {
            return "0.00 °C";
        } else {
            String formateado = df2.format(temperaturaTubo);
            return formateado;
        }
    }//end of

    public String getStrTemperaturaGas() {
        if (this.getTemperaturaGas() <= 0) {
            return "0.00 °C";
        } else {
            String formateado = df2.format(this.getTemperaturaGas());
            return formateado + " °C";
        }
    }// end method

    @Override
    public String toString() {
        sb = new StringBuilder();
        sb.append("Opacidad: ").append(opacidad);
        sb.append(" Temp Gas:").append(temperaturaGas);
        sb.append(" Temp Tubo").append(temperaturaTubo);
        sb.append(" Estado: ").append(shortEstado);
        return sb.toString();
    }
}//end of class
